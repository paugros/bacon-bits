package com.areahomeschoolers.baconbits.client.widgets.cellview;

import java.util.ArrayList;
import java.util.List;

import com.areahomeschoolers.baconbits.client.event.ParameterHandler;
import com.areahomeschoolers.baconbits.client.images.MainImageBundle;
import com.areahomeschoolers.baconbits.client.util.ClientUtils;
import com.areahomeschoolers.baconbits.client.util.Formatter;
import com.areahomeschoolers.baconbits.client.widgets.ClickLabel;
import com.areahomeschoolers.baconbits.client.widgets.DefaultListBox;
import com.areahomeschoolers.baconbits.client.widgets.TitleBar;
import com.areahomeschoolers.baconbits.shared.Common;
import com.areahomeschoolers.baconbits.shared.dto.ArgMap;
import com.areahomeschoolers.baconbits.shared.dto.ArgMap.Status;
import com.areahomeschoolers.baconbits.shared.dto.EntityDto;

import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.dom.client.Style.VerticalAlign;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class CellTitleBar<T extends EntityDto<T>> extends TitleBar {

	protected EntityCellTable<T, ?, ?> cellTable;
	private VariableSizePager pagingControl;
	private Image refreshControl;
	private Image excelControl;
	private List<Command> refreshCommands = new ArrayList<Command>();
	private TextBox searchControl;
	private DefaultListBox filterListControl;
	private Timer searchTimer;
	private boolean clientOnlyFilterListControl;

	private ClickLabel clearFilter;

	public CellTitleBar(String titleText, TitleBarStyle type) {
		super(titleText, type);
	}

	public CellTitleBar(String titleText, TitleBarStyle type, VariableSizePager pagingControl) {
		super(titleText, type);
	}

	public void addActiveFilterControl() {
		final DefaultListBox listBox = addFilterListControl();
		listBox.addItem("Active");
		listBox.addItem("Inactive");
		listBox.addItem("All");

		listBox.setValue(Common.ucWords(cellTable.getArgMap().getStatus().toString()));

		listBox.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				String val = listBox.getValue();
				ArgMap<?> args = cellTable.getArgMap();
				Status oldStatus = args.getStatus();
				Status newStatus;

				if ("Active".equals(val)) {
					newStatus = Status.ACTIVE;
				} else if ("Inactive".equals(val)) {
					newStatus = Status.INACTIVE;
				} else {
					newStatus = Status.ALL;
				}

				args.setStatus(newStatus);

				if (oldStatus != newStatus) {
					cellTable.populate();
				}
			}
		});
	}

	public void addExcelControl() {
		if (excelControl != null || cellTable == null) {
			return;
		}

		excelControl = new Image(MainImageBundle.INSTANCE.fileIconExcel());
		excelControl.setTitle("Export to Excel");
		excelControl.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				String data = "";
				if (cellTable != null) {
					data = cellTable.getExcelData();
				} else {
					return;
				}

				ClientUtils.exportCsvFile(titleText, data);
			}
		});

		addControl(excelControl);
	}

	public DefaultListBox addFilterListControl() {
		return addFilterListControl(true);
	}

	public DefaultListBox addFilterListControl(boolean clientOnlyFilterListControl) {
		this.clientOnlyFilterListControl = clientOnlyFilterListControl;
		if (filterListControl == null) {
			filterListControl = new DefaultListBox();
			addControl("Show:", filterListControl);
		}
		return filterListControl;
	}

	public void addPagingControl() {
		if (cellTable != null) {
			pagingControl = new VariableSizePager(this);
			pagingControl.setDisplay(cellTable);
			addControl(pagingControl);
		}
	}

	public void addRefreshCommand(Command command) {
		refreshCommands.add(command);
	}

	public void addRefreshControl() {
		if (refreshControl != null || cellTable == null) {
			return;
		}

		refreshControl = new Image(MainImageBundle.INSTANCE.refresh());
		refreshControl.setTitle("Refresh");

		refreshControl.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				if (hasFilter()) {
					clearFilter();
				}
				cellTable.populate();

				for (Command command : refreshCommands) {
					command.execute();
				}
			}
		});

		addControl(refreshControl);
	}

	public void addSearchControl() {
		if (searchControl != null || cellTable == null) {
			return;
		}

		searchTimer = new Timer() {
			@Override
			public void run() {
				searchTable();
			}
		};

		if (searchControl == null) {
			searchControl = new TextBox();
			searchControl.getElement().getStyle().setCursor(Cursor.TEXT);
			searchControl.setHeight("16px");
			final String defaultColor = "rgb(153, 153, 153)";
			final String defaultText = "Search...";
			if (clearFilter != null) {
				clearFilter.setEnabled(false);
			}
			searchControl.addFocusHandler(new FocusHandler() {
				@Override
				public void onFocus(FocusEvent event) {
					if (searchControl.getElement().getStyle().getColor().equals(defaultColor)) {
						searchControl.setText("");
						searchControl.getElement().getStyle().setColor("#000000");
					}
				}
			});

			searchControl.addBlurHandler(new BlurHandler() {
				@Override
				public void onBlur(BlurEvent event) {
					if (Common.isNullOrBlank(searchControl.getText())) {
						searchControl.getElement().getStyle().setColor(defaultColor);
						searchControl.setText(defaultText);
					}
				}
			});

			searchControl.getElement().getStyle().setColor(defaultColor);
			searchControl.setText(defaultText);

			searchControl.addKeyDownHandler(new KeyDownHandler() {
				@Override
				public void onKeyDown(KeyDownEvent event) {
					int keyCode = event.getNativeKeyCode();
					boolean reschedule = true;
					switch (keyCode) {
					case KeyCodes.KEY_ESCAPE:
						clearFilter();
						searchTimer.cancel();
						reschedule = false;
						break;
					case KeyCodes.KEY_ENTER:
						clearFilter.setEnabled(true);
						break;
					case KeyCodes.KEY_BACKSPACE:
						if (clearFilter != null) {
							clearFilter.setEnabled(!((searchControl.getText().length() - 1) <= 0));
						}
						break;
					default:
						if (clearFilter != null) {
							clearFilter.setEnabled(true);
						}
						break;
					}

					searchTimer.cancel();

					if (reschedule) {
						searchTimer.schedule(150);
					}
				}
			});
		}

		searchControl.getElement().getStyle().setVerticalAlign(VerticalAlign.MIDDLE);

		addControl(searchControl);
	}

	public void addVisibilityControl() {
		if (cellTable != null) {
			addVisibilityControl(cellTable);
		}
	}

	public void addVisibilityControl(final String userPreference, final ParameterHandler<Boolean> afterVisibility) {
		addVisibilityControl(cellTable, userPreference, afterVisibility);
	}

	public void clearFilter() {
		if (searchControl != null) {
			searchControl.setText("");
		}
		if (clearFilter != null) {
			clearFilter.setEnabled(false);
		}
		if (searchControl != null) {
			searchTable();
			searchControl.setFocus(false);
			searchControl.fireEvent(new BlurEvent() {
			});
		}
	}

	public TextBox extractSearchControl() {
		int index = controlPanel.getWidgetIndex(searchControl);
		if (index > 0) {
			controlPanel.remove(index);
			controlPanel.remove(index - 1);
		}
		return searchControl;
	}

	/**
	 * @return the cellTable
	 */
	public EntityCellTable<? extends EntityDto<?>, ?, ?> getCellTable() {
		return cellTable;
	}

	public VariableSizePager getPager() {
		return pagingControl;
	}

	public boolean hasFilter() {
		if (searchControl == null) {
			return false;
		}
		return !searchControl.getText().isEmpty();
	}

	public boolean isPaging() {
		return pagingControl != null;
	}

	public void removePagingControl() {
		int pagerIndex = controlPanel.getWidgetIndex(pagingControl);
		controlPanel.remove(pagerIndex);
		controlPanel.remove(pagerIndex - 1);
		pagingControl.setPageSize(Integer.MAX_VALUE);
		pagingControl.setDisplay(null);
		pagingControl = null;
	}

	/**
	 * @param cellTable
	 *            the cellTable to set
	 */
	public void setCellTable(EntityCellTable<T, ?, ?> cellTable) {
		this.cellTable = cellTable;
	}

	public void setPageResizingEnabled(boolean pageResizingEnabled) {
		if (isPaging()) {
			pagingControl.setPageResizingEnabled(pageResizingEnabled);
		}
	}

	@Override
	public void setTitleWidget(Widget titleWidget) {
		super.setTitleWidget(titleWidget);
		titlePanel.add(titleWidget);
	}

	private void searchTable() {
		String searchText = searchControl.getText().toLowerCase();
		if (searchText.isEmpty()) {
			cellTable.showAllItems();
		}

		List<T> searchList = new ArrayList<T>();
		if (filterListControl != null && clientOnlyFilterListControl) {
			filterListControl.fireEvent(new ChangeEvent() {
			});
			if (cellTable != null && cellTable.getList() != null) {
				searchList = new ArrayList<T>(cellTable.getList());
			}
		} else {
			searchList = cellTable.getFullList();
		}

		if (searchText.isEmpty()) {
			return;
		}

		for (T entity : searchList) {
			cellTable.setItemVisible(entity, cellTable.itemContainsString(searchText, entity), false, false, false);
		}

		cellTable.refreshForCurrentState();
	}

	@Override
	protected void setTotalContents(int total) {
		if (isPaging()) {
			HTML label = new HTML(pagingControl.getPagingDetails());
			label.setWordWrap(false);
			totalPanel.add(label);
			return;
		}

		if (((cellTable != null && !hasFilter() && filterListControl == null) || total == cellTable.getFullList().size())) {
			totalPanel.add(new HTML(Formatter.formatNumber(total, "#,###")));
			return;
		}

		totalPanel.add(new HTML(cellTable.getList().size() + "&nbsp;of&nbsp;"));
		if (clearFilter == null) {
			clearFilter = new ClickLabel(Integer.toString(cellTable.getFullList().size()), new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					if (cellTable != null) {
						clearFilter();
					}
				}
			});
			clearFilter.setEnabled(false);
		} else {
			clearFilter.setText(Integer.toString(cellTable.getFullList().size()));
		}
		totalPanel.add(clearFilter);
		totalPanel.add(new HTML("&nbsp;showing"));
	}
}
