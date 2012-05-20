package com.areahomeschoolers.baconbits.client.widgets.cellview;

import java.util.ArrayList;
import java.util.List;

import com.areahomeschoolers.baconbits.client.Application;
import com.areahomeschoolers.baconbits.client.event.ParameterHandler;
import com.areahomeschoolers.baconbits.client.images.MainImageBundle;
import com.areahomeschoolers.baconbits.client.rpc.Callback;
import com.areahomeschoolers.baconbits.client.util.ClientUtils;
import com.areahomeschoolers.baconbits.client.util.Formatter;
import com.areahomeschoolers.baconbits.client.widgets.ClickLabel;
import com.areahomeschoolers.baconbits.client.widgets.DefaultListBox;
import com.areahomeschoolers.baconbits.client.widgets.TitleBar;
import com.areahomeschoolers.baconbits.shared.dto.Data;
import com.areahomeschoolers.baconbits.shared.dto.EntityDto;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Style.VerticalAlign;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class CellTitleBar<T extends EntityDto<T>> extends TitleBar {

	protected EntityCellTable<T, ?, ?> cellTable;
	private VariableSizePager pagingControl = new VariableSizePager(this);
	private HorizontalPanel searchControl;
	private Image tableSearchControl;
	private Image refreshControl;
	private Image excelControl;
	private List<Command> refreshCommands = new ArrayList<Command>();
	private TextBox searchControlInput = new TextBox();
	private DefaultListBox filterListControl;

	private Timer searchTimer = new Timer() {
		@Override
		public void run() {
			searchTable();
		}
	};

	private ClickLabel clearFilter;

	public CellTitleBar(String titleText, TitleBarStyle type) {
		super(titleText, type);
	}

	public CellTitleBar(String titleText, TitleBarStyle type, VariableSizePager pagingControl) {
		super(titleText, type);
	}

	public void addExcelControl() {
		if (excelControl != null || cellTable == null) {
			return;
		}

		excelControl = new Image(MainImageBundle.INSTANCE.fileIconExcel());
		excelControl.setTitle("Export to Excel");
		excelControl.addMouseDownHandler(new MouseDownHandler() {
			@Override
			public void onMouseDown(MouseDownEvent event) {
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
		if (filterListControl == null) {
			filterListControl = new DefaultListBox();
			addControl("Show:", filterListControl);
		}
		return filterListControl;
	}

	public void addPagingControl() {
		if (cellTable != null && pagingControl != null) {
			pagingControl.setDisplay(cellTable);
			pagingControl.setPageSize(cellTable.getPageSize());
			pagingControl.setPageStart(cellTable.getPageStart());
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

		refreshControl.addMouseDownHandler(new MouseDownHandler() {
			@Override
			public void onMouseDown(MouseDownEvent event) {
				// Callback.incrementCallCount();
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
		if (tableSearchControl != null || cellTable == null) {
			return;
		}

		searchControlInput.setHeight("16px");
		if (searchControl == null) {
			if (clearFilter != null) {
				clearFilter.setEnabled(false);
			}
			searchControl = new HorizontalPanel();
			searchControlInput.addKeyDownHandler(new KeyDownHandler() {
				@Override
				public void onKeyDown(KeyDownEvent event) {
					int keyCode = event.getNativeKeyCode();
					switch (keyCode) {
					case KeyCodes.KEY_ESCAPE:
						if (searchControl.getWidgetIndex(searchControlInput) != -1) {
							tableSearchControl.fireEvent(new MouseDownEvent() {
							});
						}
						break;
					case KeyCodes.KEY_ENTER:
						clearFilter.setEnabled(true);
						break;
					case KeyCodes.KEY_BACKSPACE:
						if (clearFilter != null) {
							clearFilter.setEnabled(!((searchControlInput.getText().length() - 1) <= 0));
						}
						break;
					default:
						if (clearFilter != null) {
							clearFilter.setEnabled(true);
						}
						break;
					}
					searchTimer.cancel();
					searchTimer.schedule(150);
				}
			});
		}
		tableSearchControl = new Image(MainImageBundle.INSTANCE.search());
		tableSearchControl.setTitle("Search in table");
		tableSearchControl.addMouseDownHandler(new MouseDownHandler() {
			@Override
			public void onMouseDown(MouseDownEvent event) {
				if (searchControl.getWidgetIndex(searchControlInput) == -1) {
					searchControl.insert(searchControlInput, 0);
					Scheduler.get().scheduleDeferred(new ScheduledCommand() {
						@Override
						public void execute() {
							searchControlInput.setFocus(true);
						}
					});
					tableSearchControl.setResource(MainImageBundle.INSTANCE.cancel());
				} else if (searchControl.getWidgetIndex(searchControlInput) != -1) {
					clearFilter();
					searchControl.remove(searchControlInput);
					tableSearchControl.setResource(MainImageBundle.INSTANCE.search());
				}
			}
		});
		searchControl.add(tableSearchControl);
		searchControl.add(new Label(" "));
		tableSearchControl.getElement().getStyle().setVerticalAlign(VerticalAlign.MIDDLE);
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
								afterVisibility.execute(contents.isVisible());
								userPreferenceService.set(Application.getCurrentUser().getId(), userPreference, Boolean.toString(!contents.isVisible()),
										new Callback<Void>() {
											@Override
											protected void doOnSuccess(Void result) {
											}
										});
							}
						});
						afterVisibility.execute(true);
					}
				} else {
					try {
						hidden = !Boolean.parseBoolean(result.get(userPreference));
						afterVisibility.execute(hidden);
					} catch (Exception e) {
					}
					if (contents != null) {
						addVisibilityControl(contents, hidden, new Command() {
							@Override
							public void execute() {
								afterVisibility.execute(contents.isVisible());
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

	public void clearFilter() {
		if (searchControlInput != null) {
			searchControlInput.setText("");
		}
		if (clearFilter != null) {
			clearFilter.setEnabled(false);
		}
		searchTable();
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
		return !searchControlInput.getText().isEmpty();
	}

	public void removePagingControl() {
		pagingControl.setPageSize(Integer.MAX_VALUE);
		pagingControl.setDisplay(null);
		int pagerIndex = controlPanel.getWidgetIndex(pagingControl);
		controlPanel.remove(pagerIndex);
		controlPanel.remove(pagerIndex - 1);
	}

	/**
	 * @param cellTable
	 *            the cellTable to set
	 */
	public void setCellTable(EntityCellTable<T, ?, ?> cellTable) {
		this.cellTable = cellTable;
	}

	public void setPageResizingEnabled(boolean pageResizingEnabled) {
		pagingControl.setPageResizingEnabled(pageResizingEnabled);
	}

	@Override
	public void setTitleWidget(Widget titleWidget) {
		super.setTitleWidget(titleWidget);
		titlePanel.add(titleWidget);
	}

	private void searchTable() {
		String searchText = searchControlInput.getText().toLowerCase();
		List<T> searchList = new ArrayList<T>();
		if (filterListControl != null) {
			filterListControl.fireEvent(new ChangeEvent() {
			});
			if (cellTable != null && cellTable.getList() != null) {
				searchList = new ArrayList<T>(cellTable.getList());
			}
		} else {
			searchList = cellTable.getFullList();
		}
		if (searchText.isEmpty()) {
			cellTable.setItemsVisible(searchList, true, true);
			return;
		}
		for (T entity : searchList) {
			cellTable.setItemVisible(entity, cellTable.itemContainsString(searchText, entity), false);
		}
		cellTable.updateTitleTotal();
	}

	@Override
	protected void setTotalContents(int total) {
		if (getPager().getDisplay() != null) {
			totalPanel.add(new HTML(pagingControl.getPagingDetails()));
			return;
		}
		if (((cellTable != null && !hasFilter() && filterListControl == null) || total == cellTable.getFullList().size())) {
			totalPanel.add(new HTML(Formatter.formatNumber(total, "#,###")));
			return;
		}

		totalPanel.add(new HTML(cellTable.getList().size() + "&nbsp;of&nbsp;"));
		if (clearFilter == null) {
			clearFilter = new ClickLabel(Integer.toString(cellTable.getFullList().size()), new MouseDownHandler() {
				@Override
				public void onMouseDown(MouseDownEvent event) {
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
