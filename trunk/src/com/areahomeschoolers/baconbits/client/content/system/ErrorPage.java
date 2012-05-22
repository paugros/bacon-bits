package com.areahomeschoolers.baconbits.client.content.system;

import java.util.EnumMap;
import java.util.Map;

import com.areahomeschoolers.baconbits.client.Application;
import com.areahomeschoolers.baconbits.client.images.MainImageBundle;
import com.areahomeschoolers.baconbits.client.util.Url;
import com.areahomeschoolers.baconbits.client.util.WidgetFactory;
import com.areahomeschoolers.baconbits.client.widgets.PaddedPanel;
import com.areahomeschoolers.baconbits.shared.Common;

import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Page to load when provided back history token or unauthorized access
 */
public final class ErrorPage {
	public enum PageError {
		PAGE_NOT_FOUND, NOT_AUTHORIZED, INVALID_ACTION, SYSTEM_ERROR
	}

	private VerticalPanel errorPanel = WidgetFactory.createPagePanel();
	private Map<PageError, String> pageTitles = new EnumMap<PageError, String>(PageError.class);

	public ErrorPage(PageError errorType) {
		this(errorType, null);
	}

	public ErrorPage(PageError errorType, String customMessage) {
		VerticalPanel page = Application.getLayout().getNewPagePanel();

		// initialize page titles
		pageTitles.put(PageError.PAGE_NOT_FOUND, "Page Not Found");
		pageTitles.put(PageError.NOT_AUTHORIZED, "Not Authorized");
		pageTitles.put(PageError.INVALID_ACTION, "Invalid Action");
		pageTitles.put(PageError.SYSTEM_ERROR, "System Error");

		errorPanel.setWidth("100%");
		errorPanel.setStyleName("hugeText");
		errorPanel.addStyleName("heavyPadding");

		if (customMessage != null) {
			errorPanel.add(new Label(customMessage));
		}

		switch (errorType) {
		case INVALID_ACTION:
			invalidAction();
			break;
		case NOT_AUTHORIZED:
			notAuthorized();
			break;
		case PAGE_NOT_FOUND:
			pageNotFound();
			break;
		case SYSTEM_ERROR:
			systemError();
			break;
		}

		page.add(errorPanel);

		Application.getLayout().setPage(pageTitles.get(errorType), page);
	}

	private void invalidAction() {
		errorPanel.add(new Label("The action being requested is not valid."));
	}

	private void notAuthorized() {
		PaddedPanel pp = new PaddedPanel(10);
		pp.add(new Image(MainImageBundle.INSTANCE.waggingFinger()));
		pp.add(new Label("You are not authorized to view this page or perform the requested action."));
		errorPanel.add(pp);
	}

	private void pageNotFound() {
		HorizontalPanel hp = new HorizontalPanel();
		String url = Url.getBaseUrl().replaceAll("^https?://", "") + "#" + History.getToken();

		hp.setSpacing(5);
		Image warning = new Image(MainImageBundle.INSTANCE.yellowWarning());
		hp.add(warning);
		HTML errorMessage = new HTML("The item requested - <b>" + SafeHtmlUtils.fromString(Common.getDefaultIfNull(url, "")).asString()
				+ "</b> - does not exist.");

		hp.add(errorMessage);
		errorPanel.add(hp);

		String html = "<br>Suggestions:<br><ul>";
		html += "<li>Check the spelling of the address you typed";
		html += "<li>If you are still having problems, please contact the Help Desk";
		errorPanel.add(new HTML(html));
	}

	private void systemError() {
		errorPanel.add(new Label("There was an error processing this request."));
	}
}
