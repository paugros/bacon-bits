package com.areahomeschoolers.baconbits.client.widgets;

import java.util.ArrayList;

import com.areahomeschoolers.baconbits.client.Application;
import com.areahomeschoolers.baconbits.client.ServiceCache;
import com.areahomeschoolers.baconbits.client.event.ParameterHandler;
import com.areahomeschoolers.baconbits.client.images.MainImageBundle;
import com.areahomeschoolers.baconbits.client.rpc.Callback;
import com.areahomeschoolers.baconbits.client.rpc.service.UserPreferenceService;
import com.areahomeschoolers.baconbits.client.rpc.service.UserPreferenceServiceAsync;
import com.areahomeschoolers.baconbits.client.util.Formatter;
import com.areahomeschoolers.baconbits.shared.dto.Data;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

/**
 * Composite widget for use in delineating page heading and subsections. Includes a title panel and a link panel.
 */
public class TitleBar extends Composite {
	/**
	 * Different types of title bars have different styling and behavior.
	 */
	public enum TitleBarStyle {
		MAIN, SECTION, SUBSECTION, INACTIVE, OVERDUE, SIDEBAR, CELLTABLE
	}

	public enum TitleControl {
		VISIBILITY, SEARCH, REFRESH, EXCEL, PAGING
	}

	private FlexTable titleGrid = new FlexTable();
	protected HorizontalPanel titlePanel = new HorizontalPanel();
	private Label titleLabel = new Label();
	protected HorizontalPanel totalPanel = new HorizontalPanel();
	private HorizontalPanel widgetPanel = new HorizontalPanel();
	private TitleBarLinkPanel linkPanel = new TitleBarLinkPanel();
	protected PaddedPanel controlPanel = new PaddedPanel();
	protected String titleText;
	private Widget titleWidget, contents;
	private TitleBarStyle type;
	private int total;
	private Image visibilityControl;

	// private DataTable dataTable;

	protected UserPreferenceServiceAsync userPreferenceService = (UserPreferenceServiceAsync) ServiceCache.getService(UserPreferenceService.class);

	private ArrayList<Widget> totalWidgets = new ArrayList<Widget>();

	/**
	 * @param titleText
	 *            The text of the title
	 * @param type
	 *            The kind of title bar
	 */
	public TitleBar(String titleText, TitleBarStyle type) {
		this(type);
		titlePanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		setTitleText(titleText);
		titleLabel.getElement().getStyle().setFontSize(16, Unit.PX);
		titlePanel.add(titleLabel);
		titleLabel.getElement().getStyle().setPadding(2, Unit.PX);
	}

	/**
	 * @param titleWidget
	 *            A widget to place in the title section of the title bar
	 * 
	 * @param type
	 *            The kind of title bar
	 */
	public TitleBar(Widget titleWidget, TitleBarStyle type) {
		this(type);
		setTitleWidget(titleWidget);
		titlePanel.add(titleWidget);
		titleWidget.getElement().getStyle().setPadding(2, Unit.PX);
	}

	private TitleBar(TitleBarStyle type) {
		totalPanel.getElement().getStyle().setPadding(2, Unit.PX);
		linkPanel.getElement().getStyle().setPadding(2, Unit.PX);
		titleGrid.setWidget(0, 0, titlePanel);
		titleGrid.setCellPadding(0);
		titleGrid.setCellSpacing(0);
		titleGrid.setWidth("100%");
		initWidget(titleGrid);
		setType(type);
		titleGrid.getCellFormatter().setHorizontalAlignment(0, 1, HasHorizontalAlignment.ALIGN_RIGHT);
		titleGrid.setWidget(0, 1, widgetPanel);
		widgetPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		widgetPanel.add(linkPanel);
		widgetPanel.add(controlPanel);
	}

	public void addControl(String labelText, Widget control) {
		HorizontalPanel panel = new HorizontalPanel();
		panel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		panel.add(new HTML(labelText + "&nbsp;"));
		panel.add(control);
		addControl(panel);
	}

	/**
	 * Adds a control widget to the right {@link LinkPanel}.
	 * 
	 * @param control
	 */
	public void addControl(Widget control) {
		control.addStyleName("control");

		if (controlPanel.getWidgetCount() == 0) {
			controlPanel.add(new HTML("&nbsp;&nbsp;&nbsp;"));
		}

		controlPanel.add(control);
		controlPanel.setCellVerticalAlignment(control, HasVerticalAlignment.ALIGN_MIDDLE);
	}

	public void addLink(Widget link) {
		linkPanel.add(link);
	}

	public void addTotalWidget(Widget w) {
		totalWidgets.add(w);
	}

	public void addVisibilityControl(final Widget contents) {
		addVisibilityControl(contents, true, null);
	}

	/**
	 * Adds a visibility toggle control.
	 * 
	 * @param contents
	 */
	public void addVisibilityControl(final Widget contents, boolean showByDefault, final Command onToggleCmd) {
		this.contents = contents;
		if (visibilityControl != null) {
			return;
		}

		visibilityControl = new Image();
		setVisibilityControlResource();
		visibilityControl.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				contents.setVisible(!contents.isVisible());
				setVisibilityControlResource();
				if (onToggleCmd != null) {
					onToggleCmd.execute();
				}
			}
		});

		if (!showByDefault) {
			contents.setVisible(!contents.isVisible());
			setVisibilityControlResource();
		}
		visibilityControl.addStyleName("control");
		titleGrid.setWidget(0, 2, visibilityControl);
		titleGrid.getCellFormatter().setHorizontalAlignment(0, 2, HasHorizontalAlignment.ALIGN_RIGHT);
		titleGrid.getCellFormatter().setWidth(0, 2, "15px");
	}

	public void addVisibilityControl(final Widget contents, final String userPreference, final ParameterHandler<Boolean> afterVisibility) {
		userPreferenceService.getPreferencesByGroupName(Application.getCurrentUser().getId(), userPreference, new Callback<Data>() {
			@Override
			protected void doOnSuccess(Data result) {
				boolean hidden = false;
				if (result.isEmpty()) {
					if (contents != null) {
						addVisibilityControl(contents, true, new Command() {
							@Override
							public void execute() {
								if (afterVisibility != null) {
									afterVisibility.execute(contents.isVisible());
								}
								userPreferenceService.set(Application.getCurrentUser().getId(), userPreference, Boolean.toString(!contents.isVisible()),
										new Callback<Void>() {
											@Override
											protected void doOnSuccess(Void result) {
											}
										});
							}
						});
						if (afterVisibility != null) {
							afterVisibility.execute(true);
						}
					}
				} else {
					try {
						hidden = !Boolean.parseBoolean(result.get(userPreference));
						if (afterVisibility != null) {
							afterVisibility.execute(hidden);
						}
					} catch (Exception e) {
					}
					if (contents != null) {
						addVisibilityControl(contents, hidden, new Command() {
							@Override
							public void execute() {
								if (afterVisibility != null) {
									afterVisibility.execute(contents.isVisible());
								}
								userPreferenceService.set(Application.getCurrentUser().getId(), userPreference, Boolean.toString(!contents.isVisible()),
										new Callback<Void>() {
											@Override
											protected void doOnSuccess(Void result) {
											}
										});
							}
						});
					}
				}

			}
		});
	}

	public void collapseContents() {
		contents.setVisible(false);
		setVisibilityControlResource();
	}

	public void expandContents() {
		contents.setVisible(true);
		setVisibilityControlResource();
	}

	/**
	 * @return The title text
	 */
	public String getTitleText() {
		return titleText;
	}

	public Widget getTitleWidget() {
		return titleWidget;
	}

	/**
	 * @return The current total number of records contained under this TitleBar.
	 */
	public int getTotal() {
		return total;
	}

	/**
	 * @return The type of this title bar
	 */
	public TitleBarStyle getType() {
		return type;
	}

	public void setLinksEnabled(boolean enabled) {
		linkPanel.setVisible(enabled);
	}

	/**
	 * Sets the title text.
	 * 
	 * @param titleText
	 */
	public void setTitleText(String titleText) {
		titleLabel.setText(titleText);
		this.titleText = titleText;
	}

	public void setTitleWidget(Widget titleWidget) {
		this.titleWidget = titleWidget;
	}

	/**
	 * Sets the total number of items in the section below this TitleBar. This will appear in parentheses after the title text.
	 * 
	 * @param total
	 */
	public void setTotal(int total) {
		if (!totalPanel.isAttached()) {
			if (total == 0) {
				return;
			}
			titlePanel.add(totalPanel);
		}

		if (total > 0) {
			totalPanel.clear();
			totalPanel.add(new HTML("&nbsp;("));
			for (Widget w : totalWidgets) {
				totalPanel.add(w);
				totalPanel.add(new HTML("&nbsp;|&nbsp;"));
			}
			setTotalContents(total);
			totalPanel.add(new HTML(")"));
		} else {
			totalPanel.removeFromParent();
		}

		this.total = total;
	}

	/**
	 * Sets this title bar's type.
	 * 
	 * @param type
	 */
	public void setType(TitleBarStyle type) {
		this.type = type;

		titleGrid.setStyleName("TitleBar");

		switch (type) {
		case SIDEBAR:
			titleGrid.addStyleDependentName("sidebar");
			break;
		case SECTION:
			titleGrid.addStyleDependentName("section");
			break;
		case SUBSECTION:
			titleGrid.addStyleDependentName("subSection");
			break;
		case CELLTABLE:
			titleGrid.addStyleDependentName("cellTable");
			break;
		}
	}

	private void setVisibilityControlResource() {
		if (contents.isVisible()) {
			visibilityControl.setResource(MainImageBundle.INSTANCE.collapse());
			visibilityControl.setTitle("Hide contents");
		} else {
			visibilityControl.setResource(MainImageBundle.INSTANCE.expand());
			visibilityControl.setTitle("Show contents");
		}
	}

	protected void setTotalContents(int total) {
		totalPanel.add(new HTML(Formatter.formatNumber(total, "#,###")));
	}

}
