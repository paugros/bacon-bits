package com.areahomeschoolers.baconbits.client.content.book;

import java.util.ArrayList;

import com.areahomeschoolers.baconbits.client.Application;
import com.areahomeschoolers.baconbits.client.ServiceCache;
import com.areahomeschoolers.baconbits.client.content.tag.SearchSection;
import com.areahomeschoolers.baconbits.client.generated.Page;
import com.areahomeschoolers.baconbits.client.rpc.Callback;
import com.areahomeschoolers.baconbits.client.rpc.service.BookService;
import com.areahomeschoolers.baconbits.client.rpc.service.BookServiceAsync;
import com.areahomeschoolers.baconbits.client.util.PageUrl;
import com.areahomeschoolers.baconbits.client.util.Url;
import com.areahomeschoolers.baconbits.client.widgets.CookieCrumb;
import com.areahomeschoolers.baconbits.client.widgets.DefaultHyperlink;
import com.areahomeschoolers.baconbits.client.widgets.DefaultListBox;
import com.areahomeschoolers.baconbits.client.widgets.LocationFilterInput;
import com.areahomeschoolers.baconbits.client.widgets.PaddedPanel;
import com.areahomeschoolers.baconbits.client.widgets.TilePanel;
import com.areahomeschoolers.baconbits.shared.Common;
import com.areahomeschoolers.baconbits.shared.dto.Arg.BookArg;
import com.areahomeschoolers.baconbits.shared.dto.ArgMap;
import com.areahomeschoolers.baconbits.shared.dto.Book;
import com.areahomeschoolers.baconbits.shared.dto.BookPageData;
import com.areahomeschoolers.baconbits.shared.dto.Data;
import com.areahomeschoolers.baconbits.shared.dto.Tag.TagMappingType;
import com.areahomeschoolers.baconbits.shared.dto.UserGroup.AccessLevel;

import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

public final class BookListPage implements Page {
	private BookServiceAsync bookService = (BookServiceAsync) ServiceCache.getService(BookService.class);
	private ArgMap<BookArg> args = new ArgMap<BookArg>(BookArg.STATUS_ID, 1);
	private VerticalPanel optionsPanel = new VerticalPanel();
	private PaddedPanel top = new PaddedPanel(15);
	private int sellerId = Url.getIntegerParameter("sellerId");
	private TilePanel fp = new TilePanel();
	private VerticalPanel page;
	private TextBox searchControl;
	private ArrayList<Book> books;

	public BookListPage(final VerticalPanel p) {
		fp.setWidth("100%");
		page = p;
		if (Application.hasLocation()) {
			args.put(BookArg.LOCATION_FILTER, true);
		}
		if (!Application.hasRole(AccessLevel.GROUP_ADMINISTRATORS)) {
			args.put(BookArg.ONLINE_ONLY);
		}

		if (!Common.isNullOrBlank(Url.getParameter("tagId"))) {
			args.put(BookArg.HAS_TAGS, Url.getIntListParameter("tagId"));
		}

		if (sellerId > 0) {
			args.put(BookArg.USER_ID, sellerId);
		}

		String title = "Books";

		CookieCrumb cc = new CookieCrumb();
		cc.add(new DefaultHyperlink("Books By Type", PageUrl.tagGroup("BOOK")));
		if (!Common.isNullOrBlank(Url.getParameter("tagId"))) {
			String tag = URL.decode(Url.getParameter("tn"));
			cc.add(tag);
			title = "Books about " + tag;
		} else {
			cc.add("Books");
		}
		page.add(cc);

		page.add(new SearchSection(TagMappingType.BOOK, optionsPanel));
		populateOptionsPanel();

		page.add(fp);

		populate();

		Application.getLayout().setPage(title, page);
	}

	private void applyTextFilter(String text) {
		if (text == null || text.isEmpty()) {
			fp.showAll();
			return;
		}

		text = text.toLowerCase();

		for (Book b : books) {
			String[] tokens = { b.getTitle(), b.getDescription(), b.getAuthor(), b.getPublisher() };
			boolean show = false;
			for (int i = 0; i < tokens.length; i++) {
				if (tokens[i] == null) {
					continue;
				}
				if (tokens[i].toLowerCase().contains(text)) {
					show = true;
					break;
				}
			}
			fp.setVisible(b, show);
		}
	}

	private void populate() {
		bookService.list(args, new Callback<ArrayList<Book>>() {
			@Override
			protected void doOnSuccess(ArrayList<Book> result) {
				fp.clear();

				books = result;
				for (Book b : result) {
					fp.add(new BookTile(b), b.getId());
				}
			}
		});
	}

	private void populateOptionsPanel() {
		optionsPanel.addStyleName("boxedBlurb");
		optionsPanel.setSpacing(8);
		Label label = new Label("Show");
		top.add(label);

		DefaultHyperlink conditionLink = new DefaultHyperlink("Click here", PageUrl.article(64));
		String text = conditionLink + " for a description of book conditions.";
		page.add(new HTML(text));

		if (sellerId > 0) {
			DefaultHyperlink all = new DefaultHyperlink(">>> Books below are for a single seller. Click here to see all books.", PageUrl.bookList());
			all.addStyleName("largeText bold mediumPadding");
			page.add(all);
		}

		bookService.getPageData(0, new Callback<BookPageData>() {
			@Override
			protected void doOnSuccess(BookPageData result) {
				final DefaultListBox category = new DefaultListBox();
				category.addChangeHandler(new ChangeHandler() {
					@Override
					public void onChange(ChangeEvent event) {
						args.put(BookArg.CATEGORY_ID, category.getIntValue());
						populate();
					}
				});
				category.addItem("all categories", 0);
				for (Data item : result.getCategories()) {
					category.addItem(item.get("category"), item.getId());
				}

				final DefaultListBox grade = new DefaultListBox();
				grade.addChangeHandler(new ChangeHandler() {
					@Override
					public void onChange(ChangeEvent event) {
						args.put(BookArg.GRADE_LEVEL_ID, grade.getIntValue());
						populate();
					}
				});
				grade.addItem("all grades", 0);
				for (Data item : result.getGradeLevels()) {
					grade.addItem(item.get("gradeLevel"), item.getId());
				}

				final DefaultListBox price = new DefaultListBox();
				price.addChangeHandler(new ChangeHandler() {
					@Override
					public void onChange(ChangeEvent event) {
						if (price.getSelectedIndex() == 0) {
							args.remove(BookArg.PRICE_BETWEEN);
						} else {
							args.put(BookArg.PRICE_BETWEEN, price.getValue());
						}
						populate();
					}
				});
				price.addItem("all prices");
				price.addItem("< $1", "0-0.99");
				price.addItem("$1 - $5", "1-5");
				price.addItem("$5 - $10", "5-10");
				price.addItem("$10 - $20", "10-20");
				price.addItem("> $20", "20.01-1000");

				top.add(category);
				top.add(grade);
				top.add(price);

				for (int i = 0; i < top.getWidgetCount(); i++) {
					top.setCellVerticalAlignment(top.getWidget(i), HasVerticalAlignment.ALIGN_MIDDLE);
				}

				final LocationFilterInput locationInput = new LocationFilterInput();
				if (Application.hasLocation()) {
					locationInput.setText(Application.getCurrentLocation());
				}

				locationInput.setClearCommand(new Command() {
					@Override
					public void execute() {
						args.remove(BookArg.LOCATION_FILTER);
						populate();
					}
				});

				locationInput.setChangeCommand(new Command() {
					@Override
					public void execute() {
						args.put(BookArg.LOCATION_FILTER, true);
						populate();
					}
				});

				PaddedPanel bottom = new PaddedPanel(15);

				bottom.add(locationInput);

				for (int i = 0; i < bottom.getWidgetCount(); i++) {
					bottom.setCellVerticalAlignment(bottom.getWidget(i), HasVerticalAlignment.ALIGN_MIDDLE);
				}

				PaddedPanel extraBottom = new PaddedPanel(15);
				extraBottom.add(new Label("with text"));
				searchControl = new TextBox();
				searchControl.setVisibleLength(35);
				extraBottom.add(searchControl);
				searchControl.addBlurHandler(new BlurHandler() {
					@Override
					public void onBlur(BlurEvent event) {
						applyTextFilter(searchControl.getText());
					}
				});
				searchControl.addKeyDownHandler(new KeyDownHandler() {
					@Override
					public void onKeyDown(KeyDownEvent event) {
						if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
							applyTextFilter(searchControl.getText());
						}
					}
				});

				optionsPanel.add(top);
				optionsPanel.add(bottom);
				optionsPanel.add(extraBottom);

			}
		});
	}
}
