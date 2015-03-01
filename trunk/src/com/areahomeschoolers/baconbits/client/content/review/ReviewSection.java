package com.areahomeschoolers.baconbits.client.content.review;

import java.util.ArrayList;
import java.util.List;

import com.areahomeschoolers.baconbits.client.Application;
import com.areahomeschoolers.baconbits.client.ServiceCache;
import com.areahomeschoolers.baconbits.client.images.MainImageBundle;
import com.areahomeschoolers.baconbits.client.rpc.Callback;
import com.areahomeschoolers.baconbits.client.rpc.service.ReviewService;
import com.areahomeschoolers.baconbits.client.rpc.service.ReviewServiceAsync;
import com.areahomeschoolers.baconbits.client.util.Formatter;
import com.areahomeschoolers.baconbits.client.util.WidgetFactory.ContentWidth;
import com.areahomeschoolers.baconbits.client.widgets.ClickLabel;
import com.areahomeschoolers.baconbits.client.widgets.DefaultListBox;
import com.areahomeschoolers.baconbits.client.widgets.PaddedPanel;
import com.areahomeschoolers.baconbits.shared.dto.Arg.ReviewArg;
import com.areahomeschoolers.baconbits.shared.dto.ArgMap;
import com.areahomeschoolers.baconbits.shared.dto.ArgMap.Status;
import com.areahomeschoolers.baconbits.shared.dto.Review;
import com.areahomeschoolers.baconbits.shared.dto.Review.ReviewType;

import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class ReviewSection extends Composite {
	private class ReviewAddSection extends Composite {
		private TextArea textArea = new TextArea();
		private Review review = new Review();
		private List<Image> stars = new ArrayList<>();
		private String[] descriptions = { "I hated it", "I didn't like it", "It was okay", "I liked it", "I loved it" };
		private Label text = new Label();
		private Button submit = new Button("All Done!");
		private DefaultListBox anonymous = new DefaultListBox();

		private ReviewAddSection() {
			anonymous.addItem(Application.getCurrentUser().getFullName(), 0);
			anonymous.addItem("Anonymous chump", 1);
			vp.setSpacing(5);
			vp.getElement().getStyle().setMarginTop(15, Unit.PX);
			vp.addStyleName(ContentWidth.MAXWIDTH700PX.toString());
			final VerticalPanel addSection = new VerticalPanel();
			addSection.setVisible(false);
			textArea.setCharacterWidth(100);
			textArea.getElement().setAttribute("placeholder", "Write your review here (optional)");
			submit.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					submit.setEnabled(false);
					save();
				}
			});

			Button cancel = new Button("Cancel", new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					hide();
					if (!review.isSaved()) {
						textArea.setText("");
						clearStars();
						anonymous.setSelectedIndex(0);
					} else {
						setReview(review);
					}
				}
			});

			PaddedPanel buttonPanel = new PaddedPanel(10);
			buttonPanel.getElement().getStyle().setMargin(10, Unit.PX);
			buttonPanel.add(cancel);
			buttonPanel.add(submit);

			PaddedPanel ap = new PaddedPanel();
			ap.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
			ap.getElement().getStyle().setMargin(10, Unit.PX);
			ap.add(new Label("Post as"));
			ap.add(anonymous);

			addSection.add(createStarWidget());
			addSection.add(ap);
			addSection.add(textArea);
			addSection.add(buttonPanel);
			addSection.setCellHorizontalAlignment(buttonPanel, HasHorizontalAlignment.ALIGN_CENTER);

			initWidget(addSection);
			setVisible(false);
		}

		private void clearStars() {
			text.setText("");
			for (Image i : stars) {
				i.setResource(MainImageBundle.INSTANCE.starLargeEmpty());
			}
		}

		private Widget createStarWidget() {
			PaddedPanel pp = new PaddedPanel(2);
			pp.getElement().getStyle().setCursor(Cursor.POINTER);
			pp.getElement().getStyle().setMargin(5, Unit.PX);
			text.addStyleName("bold");
			text.getElement().getStyle().setMarginLeft(10, Unit.PX);

			for (int i = 0; i < 5; i++) {
				Image star = new Image(MainImageBundle.INSTANCE.starLargeEmpty());
				pp.add(star);
				stars.add(star);

				final int index = i;

				star.addDomHandler(new MouseOverHandler() {
					@Override
					public void onMouseOver(MouseOverEvent event) {
						setStarsUpTo(index, MainImageBundle.INSTANCE.starLargeBlue());
					}
				}, MouseOverEvent.getType());

				star.addDomHandler(new ClickHandler() {
					@Override
					public void onClick(ClickEvent event) {
						review.setRating(index + 1);
						setStarsUpTo(index, MainImageBundle.INSTANCE.starLargeYellow());
					}
				}, ClickEvent.getType());
			}

			pp.add(text);
			pp.setCellVerticalAlignment(text, HasVerticalAlignment.ALIGN_MIDDLE);

			pp.addDomHandler(new MouseOutHandler() {
				@Override
				public void onMouseOut(MouseOutEvent event) {
					if (review.getRating() == 0) {
						clearStars();
					} else {
						setStarsUpTo(review.getRating() - 1, MainImageBundle.INSTANCE.starLargeYellow());
					}
				}
			}, MouseOutEvent.getType());

			return pp;
		}

		private void hide() {
			setVisible(false);
		}

		private void save() {
			final boolean isSaved = review.isSaved();
			if (!isSaved) {
				review.setEntityId(entityId);
				review.setType(type);
			}
			review.setReview(textArea.getText());
			review.setAnonymous(anonymous.getIntValue() == 1);
			reviewService.save(review, new Callback<Review>() {
				@Override
				protected void doOnSuccess(Review result) {
					review = result;
					if (!isSaved) {
						reviews.add(0, result);
					}
					hide();
					refresh();
					submit.setEnabled(true);
				}
			});
		}

		private void setReview(Review review) {
			this.review = review;
			textArea.setText(review.getReview());
			if (review.getRating() > 0) {
				setStarsUpTo(review.getRating() - 1, MainImageBundle.INSTANCE.starLargeYellow());
			}
			if (review.getAnonymous()) {
				anonymous.setSelectedIndex(1);
			}
		}

		private void setStarsUpTo(int index, ImageResource resource) {
			for (int j = 0; j <= index; j++) {
				stars.get(j).setResource(resource);
			}
			for (int j = index + 1; j < stars.size(); j++) {
				stars.get(j).setResource(MainImageBundle.INSTANCE.starLargeEmpty());
			}

			text.setText(descriptions[index]);
		}

		private void show() {
			setVisible(true);
		}

	}

	private class ReviewWidget extends Composite {
		private VerticalPanel vp = new VerticalPanel();

		private ReviewWidget(Review r) {
			vp.setSpacing(3);
			vp.getElement().getStyle().setMarginBottom(22, Unit.PX);

			if (r.getRating() > 0) {
				HorizontalPanel hp = new HorizontalPanel();
				for (int i = 0; i < 5; i++) {
					ImageResource res = r.getRating() > i ? MainImageBundle.INSTANCE.starSmallYellow() : MainImageBundle.INSTANCE.starSmallEmpty();
					Image star = new Image(res);
					hp.add(star);
				}
				vp.add(hp);
			}

			String name = r.getAddedByFullName();
			if (r.getAnonymous()) {
				name = "Anonymous";
				if (Application.isSystemAdministrator()) {
					name += " (" + r.getAddedByFullName() + ")";
				}
			}
			Label heading = new Label("By " + name + " on " + Formatter.formatDate(r.getAddedDate(), "MMMM d, yyyy"));

			vp.add(heading);

			HTML text = new HTML(r.getReview());
			text.getElement().getStyle().setMarginTop(8, Unit.PX);
			vp.add(text);

			initWidget(vp);
		}
	}

	private VerticalPanel vp = new VerticalPanel();
	private VerticalPanel reviewPanel = new VerticalPanel();
	private ReviewServiceAsync reviewService = (ReviewServiceAsync) ServiceCache.getService(ReviewService.class);
	private ReviewType type;
	private int entityId;
	private ArrayList<Review> reviews;
	private ClickLabel addLink;
	private ReviewAddSection addSection;

	public ReviewSection(ReviewType reviewType, int itemId) {
		type = reviewType;
		entityId = itemId;

		HorizontalPanel hp = new HorizontalPanel();
		hp.setWidth("100%");
		Label heading = new Label("Reviews");
		heading.getElement().getStyle().setMarginTop(10, Unit.PX);
		heading.addStyleName("largeText");
		hp.add(heading);

		vp.add(hp);
		if (Application.isAuthenticated()) {
			addSection = new ReviewAddSection();
			addLink = new ClickLabel("Add a review", new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					addSection.show();
				}
			});
			hp.add(addLink);
			hp.setCellHorizontalAlignment(addLink, HasHorizontalAlignment.ALIGN_RIGHT);
			vp.add(addSection);
		}

		vp.add(reviewPanel);

		initWidget(vp);
	}

	public void populate() {
		ArgMap<ReviewArg> args = new ArgMap<ReviewArg>(Status.ACTIVE);
		args.put(ReviewArg.TYPE, type.toString());
		args.put(ReviewArg.ENTITY_ID, entityId);

		reviewService.list(args, new Callback<ArrayList<Review>>() {
			@Override
			protected void doOnSuccess(ArrayList<Review> result) {
				reviews = result;
				refresh();
			}
		});
	}

	private void refresh() {
		reviewPanel.clear();

		for (Review r : reviews) {
			reviewPanel.add(new ReviewWidget(r));
			if (r.getUserId() == Application.getCurrentUserId()) {
				if (addSection != null) {
					addSection.setReview(r);
					addLink.setText("Edit your review");
				}
			}
		}

		if (reviews.isEmpty()) {
			reviewPanel.add(new Label("No reviews yet. Be the first to add one!"));
		}
	}

}
