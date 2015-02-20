package com.areahomeschoolers.baconbits.client.content.book;

import com.areahomeschoolers.baconbits.client.Application;
import com.areahomeschoolers.baconbits.client.content.tag.TagSection;
import com.areahomeschoolers.baconbits.client.images.MainImageBundle;
import com.areahomeschoolers.baconbits.client.util.ClientUtils;
import com.areahomeschoolers.baconbits.client.util.Formatter;
import com.areahomeschoolers.baconbits.client.util.PageUrl;
import com.areahomeschoolers.baconbits.client.util.Url;
import com.areahomeschoolers.baconbits.client.util.WidgetFactory.ContentWidth;
import com.areahomeschoolers.baconbits.client.widgets.BuyBookWidget;
import com.areahomeschoolers.baconbits.client.widgets.ClickLabel;
import com.areahomeschoolers.baconbits.client.widgets.DefaultHyperlink;
import com.areahomeschoolers.baconbits.client.widgets.EditableImage;
import com.areahomeschoolers.baconbits.shared.Common;
import com.areahomeschoolers.baconbits.shared.dto.Book;
import com.areahomeschoolers.baconbits.shared.dto.Document.DocumentLinkType;
import com.areahomeschoolers.baconbits.shared.dto.Tag.TagMappingType;

import com.google.gwt.dom.client.Style.BorderStyle;
import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.dom.client.Style.WhiteSpace;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

public class BookDetailsPanel extends Composite {

	public BookDetailsPanel(final Book book) {
		VerticalPanel dt = new VerticalPanel();
		dt.setSpacing(2);

		HorizontalPanel pp = new HorizontalPanel();
		pp.setSpacing(10);
		VerticalPanel idt = new VerticalPanel();

		final EditableImage image = new EditableImage(DocumentLinkType.BOOK, book.getId());
		if (book.getImageId() != null) {
			image.setImage(new Image(ClientUtils.createDocumentUrl(book.getImageId(), book.getImageExtension())));
		} else {
			image.setImage(new Image(MainImageBundle.INSTANCE.defaultLarge()));
		}
		image.setEnabled(Application.administratorOf(book));
		image.populate();

		pp.add(image);
		pp.setCellWidth(image, "1%");

		if (!"Book".equals(Url.getParameter("page"))) {
			DefaultHyperlink title = new DefaultHyperlink(book.getTitle(), PageUrl.book(book.getId()));
			title.addStyleName("hugeText");
			idt.add(title);
		} else {
			Label title = new Label(book.getTitle());
			title.addStyleName("hugeText");
			idt.add(title);
		}

		if (!Common.isNullOrBlank(book.getSubTitle())) {
			idt.add(new Label(book.getSubTitle()));
		}

		Label price = new Label(Formatter.formatCurrency(book.getPrice()));
		price.addStyleName("hugeText");
		idt.add(price);

		if (!Common.isNullOrBlank(book.getNotes())) {
			Label notes = new HTML(book.getNotes());
			notes.getElement().getStyle().setPaddingTop(10, Unit.PX);
			notes.getElement().getStyle().setPaddingBottom(10, Unit.PX);
			idt.add(notes);
		}

		if (!Common.isNullOrBlank(book.getAuthor())) {
			Label author = new HTML("Author: " + book.getAuthor());
			idt.add(author);
		}

		if (book.getPublishDate() != null) {
			String ptext = Formatter.formatDate(book.getPublishDate());
			if (!Common.isNullOrBlank(book.getPublisher())) {
				ptext += " by " + book.getPublisher();
			}
			Label published = new Label("Published: " + ptext);
			idt.add(published);
		}

		if (!Common.isNullOrBlank(book.getGradeLevel())) {
			idt.add(new Label("Grade level: " + book.getGradeLevel()));
		}

		if (book.getPageCount() > 0) {
			idt.add(new Label(book.getPageCount() + " pages"));
		}

		if (!Common.isNullOrBlank(book.getCondition())) {
			Label condition = new Label("Condition: " + book.getCondition());
			idt.add(condition);
		}

		if (!Common.isNullOrBlank(book.getShippingFrom())) {
			idt.add(new Label("Ships from: " + book.getShippingFrom()));
		}

		pp.add(idt);

		VerticalPanel ddt = new VerticalPanel();
		ddt.setSpacing(6);

		if (Application.administratorOf(book)) {
			DefaultHyperlink edit = new DefaultHyperlink("Edit details", PageUrl.book(book.getId()) + "&details=true");
			edit.getElement().getStyle().setWhiteSpace(WhiteSpace.NOWRAP);
			ddt.add(edit);
		}

		if (Application.isSystemAdministrator()) {
			ddt.add(new BuyBookWidget(book));
		}

		ClickLabel contact = new ClickLabel("Contact seller", new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				BookTable.showEmailDialog(book);
			}
		});
		contact.getElement().getStyle().setWhiteSpace(WhiteSpace.NOWRAP);
		ddt.add(contact);

		DefaultHyperlink link = new DefaultHyperlink("See all from this seller", PageUrl.bookList() + "&sellerId=" + book.getUserId());
		link.getElement().getStyle().setWhiteSpace(WhiteSpace.NOWRAP);
		ddt.add(link);

		pp.add(ddt);
		pp.setCellHorizontalAlignment(ddt, HasHorizontalAlignment.ALIGN_RIGHT);

		dt.add(pp);

		TagSection ts = new TagSection(TagMappingType.BOOK, book.getId());
		ts.setEditingEnabled(false);
		ts.populate();

		dt.add(ts);

		if (!Common.isNullOrBlank(book.getDescription())) {
			HTML desc = new HTML(Formatter.formatNoteText(book.getDescription()));
			desc.getElement().getStyle().setOverflowX(Overflow.HIDDEN);
			desc.getElement().getStyle().setMargin(15, Unit.PX);
			desc.getElement().getStyle().setPadding(10, Unit.PX);
			desc.getElement().getStyle().setBackgroundColor("#ffffff");
			desc.getElement().getStyle().setBorderColor("#cccccc");
			desc.getElement().getStyle().setBorderStyle(BorderStyle.SOLID);
			desc.getElement().getStyle().setBorderWidth(1, Unit.PX);

			dt.add(desc);
			dt.addStyleName(ContentWidth.MAXWIDTH800PX.toString());
		}

		initWidget(dt);
	}

}
