package com.areahomeschoolers.baconbits.client.widgets;

import java.util.ArrayList;
import java.util.List;

import com.areahomeschoolers.baconbits.client.Application;
import com.areahomeschoolers.baconbits.client.images.MainImageBundle;
import com.areahomeschoolers.baconbits.client.util.PageUrl;
import com.areahomeschoolers.baconbits.client.util.Url;
import com.areahomeschoolers.baconbits.shared.dto.Tag.TagMappingType;

import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class ImageSwitcher extends Composite {
	private FlowPanel fp = new FlowPanel();
	private VerticalPanel outerPanel = new VerticalPanel();
	private int currentIndex = 0;
	private int baseIndex = -1;
	private int switchDelay = 11 * 1000;
	private HorizontalPanel pager = new HorizontalPanel();
	private List<Widget> panels = new ArrayList<>();
	private Timer timer = new Timer() {
		@Override
		public void run() {
			int nextIndex = currentIndex == panels.size() - 1 ? 0 : currentIndex + 1;
			loadPanel(nextIndex);
		}
	};

	public ImageSwitcher() {
		fp.setWidth("800px");
		fp.getElement().getStyle().setPosition(Position.RELATIVE);
		fp.getElement().getStyle().setTop(0, Unit.PX);
		fp.getElement().getStyle().setLeft(0, Unit.PX);

		outerPanel.add(fp);

		String h1 = "<div class=largeText style=\"margin-bottom: 4px;\">";
		h1 += "<a href=\"" + Url.getBaseUrl() + PageUrl.tagGroup(TagMappingType.EVENT.toString()) + "\">";
		h1 += "Events</a> and <a href=\"" + Url.getBaseUrl() + PageUrl.tagGroup(TagMappingType.RESOURCE.toString()) + "\">Resources</a></div>";
		h1 += "Find great homeschooling events and resources in your area with our extensive directory of local, statewide, and national listings.";
		panels.add(createPanel(new Image(MainImageBundle.INSTANCE.switcherEvents()), h1));

		String h2 = "<div class=largeText style=\"margin-bottom: 4px;\">";
		h2 += "<a href=\"" + Url.getBaseUrl() + PageUrl.userList() + "\">Find Homeschoolers</a></div>";
		h2 += "Connect with homeschoolers near you who share your interests.";
		panels.add(createPanel(new Image(MainImageBundle.INSTANCE.switcherConnect()), h2));

		String h3 = "<div class=largeText style=\"margin-bottom: 4px;\">";
		h3 += "<a href=\"" + Url.getBaseUrl() + PageUrl.tagGroup(TagMappingType.BOOK.toString()) + "\">Books</a></div>";
		h3 += "Homeschoolers can buy or sell used curriculum in our online book store. We can help your homeschool group host a book sale ";
		h3 += "with our <a href=\"" + Url.getBaseUrl() + PageUrl.article(147) + "\">book management system and tools</a>.";
		panels.add(createPanel(new Image(MainImageBundle.INSTANCE.switcherBooks()), h3));

		String h4 = "<div class=largeText style=\"margin-bottom: 4px;\">Businesses</div>";
		h4 += "Reach homeschoolers directly with our advertising services. We offer <a href=\"" + Url.getBaseUrl() + PageUrl.article(101) + "\">";
		h4 += "affordable packages</a>, and can coordinate field trips and events at your location, providing online event registration and payment if you need it.";
		panels.add(createPanel(new Image(MainImageBundle.INSTANCE.switcherVendors()), h4));

		String h5 = "<div class=largeText style=\"margin-bottom: 4px;\">Mompreneur</div>Be your family's superhero by earning extra cash doing what you live! ";
		h5 += "Join our team and help connect homeschoolers to resources in your community. ";
		h5 += "<a href=\"mailto:info@citrusgroups.com\">Contact us</a> for more information.";
		panels.add(createPanel(new Image(MainImageBundle.INSTANCE.switcherMoms()), h5));

		for (int i = 0; i < panels.size(); i++) {
			Widget panel = panels.get(i);
			panel.getElement().getStyle().setTop(0, Unit.PX);
			panel.getElement().getStyle().setLeft(0, Unit.PX);

			if (i == 0) {
				panel.getElement().getStyle().setPosition(Position.RELATIVE);
				// panel.getElement().getStyle().setZIndex(1);
				panel.getElement().getStyle().setZIndex(baseIndex + 1);
			} else {
				panel.getElement().getStyle().setPosition(Position.ABSOLUTE);
				panel.getElement().getStyle().setOpacity(0);
				// panel.getElement().getStyle().setZIndex(-1);
				panel.getElement().getStyle().setZIndex(baseIndex - 1);
			}

			fp.add(panel);
		}

		initWidget(outerPanel);

		HorizontalPanel bottomPanel = new HorizontalPanel();
		bottomPanel.setWidth("100%");
		bottomPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		Image icon = new Image(MainImageBundle.INSTANCE.facebookLarge());
		String fbText = "<a href=\"" + Application.getCurrentOrg().getFacebookUrl() + "\" style=\"vertical-align: middle;\" target=_blank>" + icon + "</a>";
		HTML fbLink = new HTML(fbText);
		bottomPanel.getElement().getStyle().setMarginTop(4, Unit.PX);
		bottomPanel.add(fbLink);
		bottomPanel.setCellWidth(fbLink, "40px");

		ConstantContactLink signUp = new ConstantContactLink("Sign Up For Updates");
		bottomPanel.add(signUp);
		bottomPanel.add(pager);
		bottomPanel.setCellHorizontalAlignment(pager, HasHorizontalAlignment.ALIGN_RIGHT);
		outerPanel.add(bottomPanel);

		Application.scheduleRepeatingPageTimer(timer, switchDelay);
	}

	private VerticalPanel createPanel(Image image, String html) {
		VerticalPanel vp = new VerticalPanel();

		vp.add(image);
		vp.getElement().getStyle().setBackgroundColor("#000000");

		HTML bottom = new HTML(html);
		bottom.setWidth("760px");
		bottom.addStyleName("imageSwitcherBottom");

		vp.add(bottom);
		vp.setCellVerticalAlignment(bottom, HasVerticalAlignment.ALIGN_MIDDLE);

		FlowPanel pagerControl = new FlowPanel();
		final int index = pager.getWidgetCount();
		pagerControl.addDomHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				timer.cancel();
				loadPanel(index);
			}
		}, ClickEvent.getType());

		pagerControl.setStylePrimaryName("fader-pager-item");

		if (index == 0) {
			pagerControl.addStyleName("fader-pager-item-active");
		}
		pager.add(pagerControl);

		return vp;
	}

	private void loadPanel(int nextIndex) {
		if (nextIndex == currentIndex) {
			timer.cancel();
			timer.scheduleRepeating(switchDelay);
			return;
		}

		final int lastIndex = currentIndex;
		final Widget outPanel = panels.get(currentIndex);
		final Widget inPanel = panels.get(nextIndex);
		inPanel.getElement().getStyle().setOpacity(1);
		// inPanel.getElement().getStyle().setZIndex(0);
		inPanel.getElement().getStyle().setZIndex(baseIndex);

		Fader fader = new Fader(outPanel);
		fader.setFadeDelay(50);
		fader.setToggleVisibility(false);
		fader.fadeOut(new Command() {
			@Override
			public void execute() {
				// inPanel.getElement().getStyle().setZIndex(1);
				// outPanel.getElement().getStyle().setZIndex(-1);
				inPanel.getElement().getStyle().setZIndex(baseIndex + 1);
				outPanel.getElement().getStyle().setZIndex(baseIndex - 1);

				pager.getWidget(currentIndex).addStyleName("fader-pager-item-active");
				pager.getWidget(lastIndex).removeStyleName("fader-pager-item-active");

				if (!timer.isRunning()) {
					timer.scheduleRepeating(switchDelay);
				}
			}
		});

		currentIndex = nextIndex;
	}

}
