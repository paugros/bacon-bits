package com.areahomeschoolers.baconbits.client.widgets.cellview;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import com.areahomeschoolers.baconbits.client.event.ParameterHandler;
import com.areahomeschoolers.baconbits.client.util.UserPreferences;
import com.areahomeschoolers.baconbits.client.widgets.DefaultListBox;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.cellview.client.AbstractPager;
import com.google.gwt.user.cellview.client.SimplePager;
import com.google.gwt.user.cellview.client.SimplePager.Style;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.view.client.HasRows;
import com.google.gwt.view.client.Range;

public class VariableSizePager extends AbstractPager {
	public enum PageSize {
		P_010("10", 10), P_025("25", 25), P_050("50", 50), P_075("75", 75), P_100("100", 100), ALL("All", Integer.MAX_VALUE);

		private String text;
		private int size;

		private PageSize(String text, int size) {
			this.text = text;
			this.size = size;
		}

		public int getSize() {
			return size;
		}

		public String getText() {
			return text;
		}
	}

	private static class ImageButton extends Image {
		private boolean disabled;
		private final ImageResource resDisabled;
		private final ImageResource resEnabled;
		private final String styleDisabled;

		public ImageButton(ImageResource resEnabled, ImageResource resDiabled, String disabledStyle) {
			super(resEnabled);
			this.resEnabled = resEnabled;
			this.resDisabled = resDiabled;
			this.styleDisabled = disabledStyle;
		}

		@Override
		public void onBrowserEvent(Event event) {
			// Ignore events if disabled.
			if (disabled) {
				return;
			}

			super.onBrowserEvent(event);
		}

		public void setDisabled(boolean isDisabled) {
			if (this.disabled == isDisabled) {
				return;
			}

			this.disabled = isDisabled;
			if (disabled) {
				setResource(resDisabled);
				getElement().getParentElement().addClassName(styleDisabled);
			} else {
				setResource(resEnabled);
				getElement().getParentElement().removeClassName(styleDisabled);
			}
		}
	}

	private final ImageButton firstPage;
	private final ImageButton lastPage;
	private final ImageButton nextPage;
	private final ImageButton prevPage;
	private final CellTitleBar<?> titlebar;
	private final Style style;
	private final HorizontalPanel layout;

	private int desiredPageSize = 0;
	private int index = 0;
	private DefaultListBox pageSizeListBox;
	private boolean pageResizingEnabled = true;
	private int pagingThreshold;
	private static final String PAGE_SIZE_PREF = "tablePageSize.";
	private List<ParameterHandler<Integer>> pageSizeChangeHandlers = new ArrayList<ParameterHandler<Integer>>();

	public VariableSizePager(CellTitleBar<?> titlebar) {
		this(SimplePagerResources.INSTANCE, titlebar);
	}

	private VariableSizePager(SimplePager.Resources resources, final CellTitleBar<?> titlebar) {
		this.style = resources.simplePagerStyle();
		this.style.ensureInjected();
		this.titlebar = titlebar;
		// Create the buttons.
		String disabledStyle = style.disabledButton();
		firstPage = new ImageButton(resources.simplePagerFirstPage(), resources.simplePagerFirstPageDisabled(), disabledStyle);
		firstPage.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				firstPage();
				setPageSize(desiredPageSize);
				titlebar.cellTable.refresh();
			}
		});
		nextPage = new ImageButton(resources.simplePagerNextPage(), resources.simplePagerNextPageDisabled(), disabledStyle);
		nextPage.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				nextPage();
				titlebar.cellTable.refresh();
			}
		});
		prevPage = new ImageButton(resources.simplePagerPreviousPage(), resources.simplePagerPreviousPageDisabled(), disabledStyle);
		prevPage.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				previousPage();
				titlebar.cellTable.refresh();
			}
		});
		lastPage = new ImageButton(resources.simplePagerLastPage(), resources.simplePagerLastPageDisabled(), disabledStyle);
		lastPage.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				lastPage();
				titlebar.cellTable.refresh();
			}
		});

		// Construct the widget.
		layout = new HorizontalPanel();
		layout.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		initWidget(layout);

		layout.add(firstPage);
		layout.add(prevPage);

		pageSizeListBox = new DefaultListBox(false);
		for (PageSize p : EnumSet.allOf(PageSize.class)) {
			pageSizeListBox.addItem(p.getText(), p.getSize());
		}
		// pageSizeListBox.addItem("10", 10);
		// pageSizeListBox.addItem("25", 25);
		// pageSizeListBox.addItem("50", 50);
		// pageSizeListBox.addItem("75", 75);
		// pageSizeListBox.addItem("100", 100);
		// All should always be the last value to ensure that pre-selection works.
		pageSizeListBox.addItem("All", Integer.MAX_VALUE);
		pageSizeListBox.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				setPageSize(pageSizeListBox.getIntValue());
			}
		});
		layout.add(pageSizeListBox);

		layout.add(nextPage);
		layout.add(lastPage);

		// Add style names to the cells.
		firstPage.getElement().getParentElement().addClassName(style.button());
		lastPage.getElement().getParentElement().addClassName(style.button());
		nextPage.getElement().getParentElement().addClassName(style.button());
		prevPage.getElement().getParentElement().addClassName(style.button());
	}

	public HandlerRegistration addPageSizeChangeHandler(ParameterHandler<Integer> handler) {
		final int index = pageSizeChangeHandlers.size();
		pageSizeChangeHandlers.add(handler);
		return new HandlerRegistration() {
			@Override
			public void removeHandler() {
				pageSizeChangeHandlers.remove(index);
			}
		};
	}

	@Override
	public int getPageSize() {
		if (getDisplay() != null) {
			if (index + desiredPageSize > getDisplay().getRowCount()) {
				return getDisplay().getRowCount() - index;
			}
			return desiredPageSize;
		}
		return -1;
	}

	public String getPagingDetails() {
		// Default text is 1 based.
		if (getDisplay() != null) {
			NumberFormat formatter = NumberFormat.getFormat("#,###");
			HasRows display = getDisplay();
			Range range = display.getVisibleRange();
			int pageStart = range.getStart() + 1;
			int pageSize = range.getLength();
			int dataSize = display.getRowCount();
			int endIndex = Math.min(dataSize, pageStart + pageSize - 1);
			endIndex = Math.max(pageStart, endIndex);
			boolean exact = display.isRowCountExact();
			return formatter.format(pageStart) + "-" + formatter.format(endIndex) + (exact ? " of " : " of over ") + formatter.format(dataSize);
		}
		return "";
	}

	public int getPagingThreshold() {
		return pagingThreshold;
	}

	@Override
	public void previousPage() {
		if (getDisplay() != null) {
			int index = Math.max(0, getDisplay().getVisibleRange().getStart() - desiredPageSize);
			setPageStart(index);
		}
	}

	@Override
	public void setDisplay(HasRows display) {
		super.setDisplay(display);

		if (desiredPageSize == 0) {
			setPageSize(EntityCellTable.DEFAULT_PAGE_SIZE);
		}
	}

	/**
	 * Enable or disable the next page buttons.
	 * 
	 * @param disabled
	 *            true to disable, false to enable
	 */
	public void setNextPageButtonsDisabled(boolean disabled) {
		nextPage.setDisabled(disabled);
		if (lastPage != null) {
			lastPage.setDisabled(disabled);
		}
	}

	public void setPageResizingEnabled(boolean allowPageResizing) {
		this.pageResizingEnabled = allowPageResizing;
		if (!pageResizingEnabled && layout.getWidgetIndex(pageSizeListBox) >= 0) {
			layout.remove(pageSizeListBox);
		} else if (pageResizingEnabled && layout.getWidgetIndex(pageSizeListBox) == -1) {
			layout.insert(pageSizeListBox, layout.getWidgetIndex(nextPage));
		}
	}

	@Override
	public void setPageSize(int pageSize) {
		if (pageSize != pageSizeListBox.getIntValue()) {
			pageSizeListBox.setValue(pageSize);
		}
		desiredPageSize = pageSize;
		if (desiredPageSize == Integer.MAX_VALUE) {
			if (getDisplay() != null) {
				HasRows d = getDisplay();
				d.setVisibleRange(0, d.getRowCount());
			}
			setNextPageButtonsDisabled(true);
			setPrevPageButtonsDisabled(true);
		} else {
			if (getDisplay() != null) {
				HasRows d = getDisplay();
				Range r = d.getVisibleRange();
				d.setVisibleRange(r.getStart(), Math.min(d.getRowCount() - r.getStart(), desiredPageSize));
			}
		}
		titlebar.cellTable.refresh();
		UserPreferences.save(getPageSizePrefString(), pageSizeListBox.getSelectedText());

		for (ParameterHandler<Integer> ph : pageSizeChangeHandlers) {
			ph.execute(desiredPageSize);
		}
		super.setPageSize(pageSize);
	}

	public void setPageSize(PageSize p) {
		setPageSize(p.getSize());
	}

	@Override
	public void setPageStart(int index) {
		if (getDisplay() != null) {
			int pageSize = desiredPageSize;
			if (index + desiredPageSize > getDisplay().getRowCount()) {
				pageSize = getDisplay().getRowCount() - index;
			} else {
				pageSize = desiredPageSize;
			}
			index = Math.max(0, index);
			this.index = index;
			getDisplay().setVisibleRange(index, pageSize);
		}
	}

	/**
	 * Enable or disable the previous page buttons.
	 * 
	 * @param disabled
	 *            true to disable, false to enable
	 */
	public void setPrevPageButtonsDisabled(boolean disabled) {
		firstPage.setDisabled(disabled);
		prevPage.setDisabled(disabled);
	}

	protected String getPageSizePrefString() {
		return PAGE_SIZE_PREF + titlebar.getCellTable().getPageSizePrefString();
	}

	@Override
	protected void onRangeOrRowCountChanged() {
		HasRows display = getDisplay();
		Range r = display.getVisibleRange();
		int rows = titlebar.getCellTable().visibleItems.size();
		titlebar.setTotal(rows);
		int utilizedPageSize = desiredPageSize;
		if (rows < desiredPageSize * 2) {
			utilizedPageSize *= 2;
		}

		if (desiredPageSize == Integer.MAX_VALUE) {
			display.setVisibleRange(0, rows);
			setNextPageButtonsDisabled(true);
			setPrevPageButtonsDisabled(true);
		} else {
			display.setVisibleRange(r.getStart(), Math.min(rows - r.getStart(), utilizedPageSize));
			setPrevPageButtonsDisabled(!hasPreviousPage());
			setNextPageButtonsDisabled(!hasNextPage());
		}
		display.setRowCount(rows);
	}
}
