package com.areahomeschoolers.baconbits.client.util;

import com.areahomeschoolers.baconbits.client.images.MainImageBundle;
import com.areahomeschoolers.baconbits.client.widgets.DefaultHyperlink;
import com.areahomeschoolers.baconbits.client.widgets.TitleBar;
import com.areahomeschoolers.baconbits.client.widgets.TitleBar.TitleBarStyle;
import com.areahomeschoolers.baconbits.client.widgets.cellview.EntityCellTable;
import com.areahomeschoolers.baconbits.shared.Common;
import com.areahomeschoolers.baconbits.shared.Constants;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.ButtonBase;
import com.google.gwt.user.client.ui.CustomButton;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public abstract class WidgetFactory {
	public enum ContentWidth {
		MAXWIDTH600PX, MAXWIDTH700PX, MAXWIDTH750PX, MAXWIDTH760PX, MAXWIDTH800PX, MAXWIDTH900PX, MAXWIDTH1000PX, MAXWIDTH1100PX, MAXWIDTH1200PX, MAXWIDTH1300PX, MAXWIDTH1400PX, MAXWIDTH1500PX;
	}

	private static final String DEFAULT_PERCENTAGE_BAR_STYLE = "percentageBar-green";
	public static final int PAGE_SPACING = 10;
	public static final int SIDEBAR_SPACING = 5;

	public static ButtonBase createClickBox(Image image, ClickHandler ch) {
		CustomButton cb = new CustomButton(image) {
		};

		cb.setStylePrimaryName("clickBox");
		cb.addClickHandler(ch);
		return cb;
	}

	public static ButtonBase createClickBox(String text, ClickHandler ch) {
		CustomButton cb = new CustomButton(text) {
		};

		cb.setStylePrimaryName("clickBox");
		cb.addClickHandler(ch);
		return cb;
	}

	public static Anchor createEmailLink(String email) {
		Anchor a = new Anchor(email, "mailto:" + email);
		return a;
	}

	public static VerticalPanel createPagePanel() {
		VerticalPanel pagePanel = new VerticalPanel();
		pagePanel.setStyleName("page");
		pagePanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_TOP);
		pagePanel.setSpacing(PAGE_SPACING);
		return pagePanel;
	}

	public static Widget createPercentageBar(double decimalPercent) {
		return createPercentageBar(decimalPercent, DEFAULT_PERCENTAGE_BAR_STYLE);
	}

	public static Widget createPercentageBar(double decimalPercent, String colorStyle) {
		return createPercentageBar(Integer.parseInt(Formatter.formatNumber(decimalPercent * 100, "##")), colorStyle);
	}

	public static Widget createPercentageBar(int percent) {
		return createPercentageBar(percent, DEFAULT_PERCENTAGE_BAR_STYLE);
	}

	public static Widget createPercentageBar(int percent, String colorStyle) {
		Grid grid = new Grid(1, 2);
		grid.setWidth("100%");

		Image posImage = new Image(MainImageBundle.INSTANCE.pixel());
		grid.setWidget(0, 0, posImage);
		grid.getCellFormatter().setStyleName(0, 0, colorStyle);
		grid.getCellFormatter().getElement(0, 0).getStyle().setWidth(percent, Unit.PCT);

		grid.setCellSpacing(0);

		Image negImage = new Image(MainImageBundle.INSTANCE.pixel());
		grid.setWidget(0, 1, negImage);
		grid.getCellFormatter().setStyleName(0, 1, "percentageBar-empty");
		grid.getCellFormatter().getElement(0, 1).getStyle().setWidth(100 - percent, Unit.PCT);

		return grid;
	}

	public static Widget getToolTipLabel(String text, String toolTipText) {
		InlineLabel label = new InlineLabel(text);
		label.setStyleName("dottedUnderline");

		if (!Common.isNullOrBlank(toolTipText)) {
			label.setTitle(toolTipText);
		}

		return label;
	}

	public static Widget newSection(EntityCellTable<?, ?, ?> cellTable) {
		return newSection(cellTable.getTitleBar(), cellTable);
	}

	public static Widget newSection(EntityCellTable<?, ?, ?> cellTable, ContentWidth width) {
		SimplePanel sp = new SimplePanel(newSection(cellTable));
		sp.addStyleName(width.toString());
		return sp;
	}

	public static Widget newSection(String title, Widget w) {
		Widget vp = newSection(new TitleBar(title, TitleBarStyle.SECTION), w);
		vp.setWidth("100%");
		return vp;
	}

	public static Widget newSection(String title, Widget w, ContentWidth width) {
		SimplePanel sp = new SimplePanel(newSection(title, w));
		sp.addStyleName(width.toString());
		return sp;
	}

	public static Widget newSection(TitleBar tb, Widget w) {
		VerticalPanel vp = new VerticalPanel();
		vp.add(tb);
		vp.add(w);
		vp.setWidth("100%");
		return vp;
	}

	public static Widget newSection(TitleBar tb, Widget w, ContentWidth width) {
		SimplePanel sp = new SimplePanel(newSection(tb, w));
		sp.addStyleName(width.toString());
		return sp;
	}

	/**
	 * Determines whether a link will open a new tab.
	 * 
	 * @param link
	 */
	public static Widget openInNewTab(DefaultHyperlink link, boolean newTab) {
		if (newTab) {
			return new HTML("<a href=\"" + Constants.URL_SEPARATOR + link.getTargetHistoryToken() + "\" target=\"_blank\">" + link.getText() + "</a>");
		}
		return link;
	}

	public static SimplePanel wrapForWidth(Widget w, ContentWidth width) {
		SimplePanel sp = new SimplePanel(w);
		sp.addStyleName(width.toString());
		return sp;
	}

	private WidgetFactory() {

	}

}
