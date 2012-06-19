package com.areahomeschoolers.baconbits.client.widgets.cellview;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import com.areahomeschoolers.baconbits.client.event.DataReturnHandler;
import com.areahomeschoolers.baconbits.client.event.ParameterHandler;
import com.areahomeschoolers.baconbits.client.images.MainImageBundle;
import com.areahomeschoolers.baconbits.client.rpc.Callback;
import com.areahomeschoolers.baconbits.client.util.Formatter;
import com.areahomeschoolers.baconbits.client.util.Url;
import com.areahomeschoolers.baconbits.client.util.UserPreferences;
import com.areahomeschoolers.baconbits.client.widgets.AlertDialog;
import com.areahomeschoolers.baconbits.client.widgets.HasDisableCriteria;
import com.areahomeschoolers.baconbits.client.widgets.HasSortValue;
import com.areahomeschoolers.baconbits.client.widgets.MaxHeightScrollPanel;
import com.areahomeschoolers.baconbits.client.widgets.TitleBar.TitleBarStyle;
import com.areahomeschoolers.baconbits.shared.Common;
import com.areahomeschoolers.baconbits.shared.dto.Arg;
import com.areahomeschoolers.baconbits.shared.dto.ArgMap;
import com.areahomeschoolers.baconbits.shared.dto.EntityDto;

import com.google.gwt.cell.client.AbstractEditableCell;
import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.ClickableTextCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.SafeHtmlCell;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.core.client.JavaScriptException;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.TableCellElement;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.ColumnSortEvent;
import com.google.gwt.user.cellview.client.ColumnSortEvent.ListHandler;
import com.google.gwt.user.cellview.client.ColumnSortList.ColumnSortInfo;
import com.google.gwt.user.cellview.client.Header;
import com.google.gwt.user.cellview.client.RowStyles;
import com.google.gwt.user.cellview.client.TextHeader;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasHorizontalAlignment.HorizontalAlignmentConstant;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.CellPreviewEvent;
import com.google.gwt.view.client.MultiSelectionModel;
import com.google.gwt.view.client.NoSelectionModel;
import com.google.gwt.view.client.ProvidesKey;
import com.google.gwt.view.client.RangeChangeEvent;
import com.google.gwt.view.client.RowCountChangeEvent;
import com.google.gwt.view.client.RowCountChangeEvent.Handler;
import com.google.gwt.view.client.SelectionModel;
import com.google.gwt.view.client.SingleSelectionModel;

public abstract class EntityCellTable<T extends EntityDto<T>, U extends Arg, C extends Enum<C> & EntityCellTableColumn<C>> extends CellTable<T> {
	/**
	 * Selection configuration options.
	 */
	public static enum SelectionPolicy {
		ONE_ROW, MULTI_ROW, NONE, MULTI_BY_ROW
	}

	/**
	 * Sort direction constants.
	 */
	public static enum SortDirection {
		SORT_ASC, SORT_DESC
	}

	/**
	 * Sort configuration options.
	 * 
	 * DISABLED means the sort is on, but the user cannot change the sort by clicking the headers.
	 */
	public static enum SortPolicy {
		ON, OFF, DISABLED
	}

	private class AggregateFooterData {
		private AggregationMethod method;
		private ValueGetter<Number, T> valueGetter;
		private String format;
		private Label label;

		private AggregateFooterData(AggregationMethod m, ValueGetter<Number, T> vg, String f, Label l) {
			method = m;
			valueGetter = vg;
			format = f;
			label = l;
		}
	}

	private static enum AggregationMethod {
		TOTAL, AVERAGE
	}

	private static enum CellSortPolicy {
		ON, OFF, ON_USER_DISABLED
	}

	private static final String TABLE_SORT_PREF = "tableSort.";
	public static final int DEFAULT_PAGE_SIZE = 50;
	private String defaultSizePrefName = "default";

	// Layout Data
	protected EnumSet<C> displayColumns;
	private Map<String, AggregateFooterData> aggregateFooterDataMap = new HashMap<String, AggregateFooterData>();
	protected Map<String, ColumnData<T>> columnDataMap = new HashMap<String, ColumnData<T>>();
	private ArgMap<U> args;
	protected ProvidesKey<T> entityKeyProvider = new ProvidesKey<T>() {
		@Override
		public Integer getKey(T item) {
			return item.getId();
		}
	};

	// Events and Asynchronous handlers.
	private List<DataReturnHandler> dataReturnHandlers = new ArrayList<DataReturnHandler>();
	private Callback<ArrayList<T>> callback;

	// Data Lists
	protected Map<Object, T> entityIdMap = new HashMap<Object, T>();
	protected Set<T> hiddenItems = new HashSet<T>();
	protected Set<T> unfilteredList = new HashSet<T>();
	protected ArrayList<T> visibleItems = new ArrayList<T>();
	private List<T> initialSelectedItems = new ArrayList<T>();

	// Sorting
	private String defaultSortHeader = "";
	private CellSortPolicy sortPolicy = CellSortPolicy.ON;
	private SortDirection defaultSortDirection = SortDirection.SORT_ASC;
	private HandlerRegistration sortRegistration;
	private int defaultSortIndex = 0;

	// Selection Systems
	private CheckboxHeader<T, U> cbHeader;
	private AbstractEditableCell<Boolean, Boolean> selectionCell;
	private SelectionPolicy selectionPolicy = SelectionPolicy.NONE;
	private Column<T, Boolean> selectionColumn = null;
	private List<ParameterHandler<T>> selectionPolicyChangeCommands = new ArrayList<ParameterHandler<T>>();
	private Collection<Integer> initialSelectedItemIds = new HashSet<Integer>();
	private T lastSelectedItem = null;
	private Command clearCommand;

	// States
	private boolean isFinishedLoading;
	private boolean linksOpenNewTab;
	private boolean defaultByIndex = true;
	private boolean hasBeenPopulated;
	protected boolean maintainHidden = false;

	// UI Components
	private MaxHeightScrollPanel scrollPanel;
	private CellTitleBar<T> titleBar = new CellTitleBar<T>("", TitleBarStyle.SECTION);
	private Set<WidgetCellCreator<?>> cellWidgetCreators = new HashSet<WidgetCellCreator<?>>();
	private RowStyles<T> rowStyles;

	private HashSet<EntityCellTable<T, U, C>> partners = new HashSet<EntityCellTable<T, U, C>>();

	private HandlerRegistration noSortRegistration;

	public EntityCellTable() {
		super(DEFAULT_PAGE_SIZE, EntityCellTableResources.INSTANCE);
		setSelectionPolicy(selectionPolicy);
		setKeyboardSelectionPolicy(KeyboardSelectionPolicy.DISABLED);
		setLoadingIndicator(new Label("Loading..."));
		setEmptyTableWidget(new Label("No results"));
		setPageStart(0);
		registerTitleBar(titleBar);
		getTitleBar().addPagingControl();
		addRowCountChangeHandler(new Handler() {
			@Override
			public void onRowCountChange(RowCountChangeEvent event) {
				if (event.getNewRowCount() == 0) {
					addStyleName("hiddenHeader");
					addStyleName("hiddenFooter");
				} else {
					removeStyleName("hiddenHeader");
					removeStyleName("hiddenFooter");
				}
				updateTitleTotal();
			}
		});
		setSortingEnabled(true);
		setDefaultSortColumn(0, SortDirection.SORT_ASC);
		setWidth("100%");
	}

	public Column<T, ?> addAveragedNumberColumn(String header, ValueGetter<Number, T> numberGetter, String format) {
		return addAggregatedNumberColumn(header, AggregationMethod.AVERAGE, null, numberGetter, format, false);
	}

	public Column<T, ?> addCheckboxColumn(String header, final ValueGetter<Boolean, T> checkedGetter, final FieldUpdater<T, Boolean> valueChanged) {
		if (columnDataMap.containsKey(header)) {
			return null;
		}

		final CheckboxCell cbCell = new CheckboxCell(false, false);
		Column<T, Boolean> column = new Column<T, Boolean>(cbCell) {
			@Override
			public Boolean getValue(T object) {
				return checkedGetter.get(object);
			}
		};
		column.setFieldUpdater(valueChanged);
		column.setSortable(true);
		addColumn(column, header);

		updateColumnMetaData(header, checkedGetter, null, HasHorizontalAlignment.ALIGN_DEFAULT, column);
		return column;
	}

	// @SafeVarargs TODO Put these back for Java 7
	public final void addColumn(C... columns) {
		for (C column : columns) {
			if (displayColumns.contains(column)) {
				continue;
			}
			displayColumns.add(column);
		}
		setColumns();
	}

	public <W extends Widget> Column<T, ?> addCompositeColumn(EntityCellTableColumn<C> column, CompositeCellWidgetCreator<T> compositeCreator,
			ValueGetter<?, T> sortGetter) {
		return addCompositeColumn(column.getTitle(), compositeCreator, sortGetter, null);
	}

	public <W extends Widget> Column<T, ?> addCompositeColumn(String header, CompositeCellWidgetCreator<T> compositeCreator, ValueGetter<?, T> sortGetter) {
		return addCompositeColumn(header, compositeCreator, sortGetter, null);
	}

	public <W extends Widget> Column<T, ?> addCompositeColumn(String header, CompositeCellWidgetCreator<T> compositeCreator, ValueGetter<?, T> sortGetter,
			TextHeaderCreator<?> footerCreator) {
		if (columnDataMap.containsKey(header)) {
			return null;
		}

		for (WidgetCellCreator<T> widgetCreator : compositeCreator.getWidgetCreators()) {
			cellWidgetCreators.add(widgetCreator);
		}

		Column<T, T> col = compositeCreator.createColumn(this);
		col.setSortable(true);

		addColumn(col, new TextHeader(header), createTextHeader(footerCreator));
		updateColumnMetaData(header, sortGetter, sortGetter, HasHorizontalAlignment.ALIGN_DEFAULT, col);
		return col;
	}

	public <W extends Widget> Column<T, ?> addCompositeWidgetColumn(EntityCellTableColumn<C> column, WidgetCellCreator<T> widgetCreator) {
		return addCompositeWidgetColumn(column.getTitle(), widgetCreator);
	}

	public Column<T, ?> addCompositeWidgetColumn(EntityCellTableColumn<C> column, final WidgetCellCreator<T> widgetCreator, ValueGetter<?, T> sortGetter) {
		return addCompositeWidgetColumn(column.getTitle(), widgetCreator, sortGetter, null);
	}

	public <W extends Widget> Column<T, ?> addCompositeWidgetColumn(String header, WidgetCellCreator<T> widgetCreator) {
		return addCompositeWidgetColumn(header, widgetCreator, null);
	}

	public Column<T, ?> addCompositeWidgetColumn(String header, WidgetCellCreator<T> widgetCreator, ValueGetter<?, T> sortGetter) {
		return addCompositeWidgetColumn(header, widgetCreator, sortGetter, null);
	}

	public Column<T, ?> addCompositeWidgetColumn(String header, final WidgetCellCreator<T> widgetCreator, ValueGetter<?, T> sortGetter,
			TextHeaderCreator<?> footerCreator) {

		if (sortGetter == null) {
			sortGetter = getDefaultValueGetter(widgetCreator);
		}

		return addCompositeColumn(header, new CompositeCellWidgetCreator<T>() {
			@Override
			protected List<WidgetCellCreator<T>> createWidgetCreators() {
				return Common.asList(widgetCreator);
			}
		}, sortGetter, footerCreator);
	}

	public Column<T, ?> addCurrencyColumn(EntityCellTableColumn<C> column, ValueGetter<Double, T> doubleGetter) {
		return addCurrencyColumn(column.getTitle(), doubleGetter);
	}

	public Column<T, ?> addCurrencyColumn(String header, final ValueGetter<Double, T> doubleGetter) {
		Column<T, ?> col = addTextColumn(header, new ValueGetter<String, T>() {
			@Override
			public String get(T entity) {
				return Formatter.formatCurrency(doubleGetter.get(entity));
			}
		}, doubleGetter);

		setColumnAlignment(getColumnCount() - 1, HasHorizontalAlignment.ALIGN_RIGHT);

		return col;
	}

	/**
	 * Adds a {@link DataReturnHandler} to be executed upon receipt of data.
	 * 
	 * @param handler
	 */
	public HandlerRegistration addDataReturnHandler(final DataReturnHandler handler) {
		dataReturnHandlers.add(handler);
		// if the data is already here, then we can execute right now
		if (isFinishedLoading) {
			handler.onDataReturn();
		}
		return new HandlerRegistration() {
			@Override
			public void removeHandler() {
				dataReturnHandlers.remove(handler);
			}
		};
	}

	public Column<T, ?> addDateColumn(EntityCellTableColumn<C> column, ValueGetter<Date, T> dateGetter) {
		return addDateColumn(column.getTitle(), dateGetter);
	}

	public Column<T, ?> addDateColumn(String header, final ValueGetter<Date, T> dateGetter) {
		return addDateColumn(header, dateGetter, dateGetter);
	}

	public Column<T, ?> addDateColumn(String header, final ValueGetter<Date, T> dateGetter, final ValueGetter<?, T> sortGetter) {
		return addTextColumn(header, new ValueGetter<String, T>() {
			@Override
			public String get(T entity) {
				return Formatter.formatDate(dateGetter.get(entity));
			}
		}, sortGetter);
	}

	public Column<T, ?> addDateTimeColumn(EntityCellTableColumn<C> column, ValueGetter<Date, T> dateGetter) {
		return addDateTimeColumn(column.getTitle(), dateGetter);
	}

	public Column<T, ?> addDateTimeColumn(String header, final ValueGetter<Date, T> dateGetter) {
		return addTextColumn(header, new ValueGetter<String, T>() {
			@Override
			public String get(T entity) {
				return Formatter.formatDateTime(dateGetter.get(entity));
			}
		}, dateGetter);
	}

	public void addItem(T item) {
		addItem(item, true);
	}

	/**
	 * Add an item to the table.
	 * 
	 * @param item
	 *            Item to be added.
	 * @param sort
	 *            Sort after adding
	 */
	public void addItem(T item, boolean sort) {
		addItem(item, sort, true);
	}

	public void addItem(T item, boolean sort, boolean fireRowUpdate) {
		entityIdMap.put(entityKeyProvider.getKey(item), item);
		hiddenItems.remove(item);
		visibleItems.remove(item);
		unfilteredList.remove(item);
		if (visibleItems.isEmpty()) {
			ArrayList<T> items = new ArrayList<T>();
			items.add(item);
			populate(items);
		} else {
			visibleItems.add(item);
			unfilteredList.add(item);
			if (fireRowUpdate) {
				onRowDataUpdate(sort);
			}
		}
	}

	public void addItems(ArrayList<T> items) {
		addItems(items, true);
	}

	public void addItems(ArrayList<T> items, boolean sort) {
		if (visibleItems.isEmpty()) {
			populate(items);
		} else {
			for (T item : items) {
				addItem(item, false, false);
			}
			onRowDataUpdate(sort);
		}
	}

	public Column<T, ?> addNumberColumn(EntityCellTableColumn<C> column, ValueGetter<Number, T> numberGetter) {
		return addNumberColumn(column.getTitle(), numberGetter);
	}

	public Column<T, ?> addNumberColumn(EntityCellTableColumn<C> column, ValueGetter<Number, T> numberGetter, final String format) {
		return addNumberColumn(column.getTitle(), numberGetter, format);
	}

	public Column<T, ?> addNumberColumn(String header, ValueGetter<Number, T> numberGetter) {
		return addNumberColumn(header, numberGetter, "#,###");
	}

	public Column<T, ?> addNumberColumn(String header, final ValueGetter<Number, T> numberGetter, final String format) {
		Column<T, ?> col = addTextColumn(header, new ValueGetter<String, T>() {
			@Override
			public String get(T entity) {
				return Formatter.formatNumber(numberGetter.get(entity), format);
			}
		}, numberGetter);

		setColumnAlignment(getColumnCount() - 1, HasHorizontalAlignment.ALIGN_RIGHT);
		return col;
	}

	public Column<T, SafeHtml> addSafeHtmlColumn(EntityCellTableColumn<C> column, final ValueGetter<SafeHtml, T> sortnDisplay) {
		return addSafeHtmlColumn(column.getTitle().toString(), sortnDisplay);
	}

	public Column<T, SafeHtml> addSafeHtmlColumn(String header, final ValueGetter<SafeHtml, T> sortnDisplay) {
		final ValueGetter<SafeHtml, T> newSortnDisplay = new ValueGetter<SafeHtml, T>() {
			@Override
			public SafeHtml get(final T item) {
				return new SafeHtml() {
					private static final long serialVersionUID = 1L;

					@Override
					public String asString() {
						return sortnDisplay.get(item).asString();
					}

					@Override
					public String toString() {
						return sortnDisplay.get(item).asString();
					}
				};
			}
		};

		if (columnDataMap.containsKey(header)) {
			return null;
		}
		Column<T, SafeHtml> col = new Column<T, SafeHtml>(new SafeHtmlCell()) {
			@Override
			public SafeHtml getValue(T object) {
				return newSortnDisplay.get(object);
			}
		};
		col.setSortable(true);
		addColumn(col, new TextHeader(header));
		updateColumnMetaData(header, newSortnDisplay, newSortnDisplay, HasHorizontalAlignment.ALIGN_DEFAULT, col);
		return col;
	}

	public void addSelectionPolicyChangeCommand(ParameterHandler<T> handler) {
		selectionPolicyChangeCommands.add(handler);
	}

	public Column<T, ?> addTextColumn(EntityCellTableColumn<C> column, ValueGetter<String, T> display) {
		return addTextColumn(column.getTitle(), display);
	}

	public Column<T, ?> addTextColumn(EntityCellTableColumn<C> column, ValueGetter<String, T> display, ValueGetter<?, T> sortGetter) {
		return addTextColumn(column.getTitle(), display, sortGetter);
	}

	public Column<T, ?> addTextColumn(String header, final ValueGetter<String, T> display) {
		if (columnDataMap.containsKey(header)) {
			return null;
		}

		TextCell cell = new TextCell();

		Column<T, String> col = new Column<T, String>(cell) {
			@Override
			public String getValue(T item) {
				return display.get(item);
			}
		};
		col.setSortable(true);
		addColumn(col, header);
		updateColumnMetaData(header, display, display, HasHorizontalAlignment.ALIGN_DEFAULT, col);
		return col;
	}

	public Column<T, ?> addTextColumn(String header, final ValueGetter<String, T> display, final ValueGetter<?, T> sortGetter) {
		if (columnDataMap.containsKey(header)) {
			return null;
		}
		TextCell cell = new TextCell();
		Column<T, String> col = new Column<T, String>(cell) {
			@Override
			public String getValue(T item) {
				return display.get(item);
			}
		};
		col.setSortable(true);
		addColumn(col, header);
		updateColumnMetaData(header, sortGetter, display, HasHorizontalAlignment.ALIGN_DEFAULT, col);
		return col;
	}

	public Column<T, ?> addTotaledCurrencyColumn(String header, ValueGetter<Number, T> numberGetter) {
		return addTotaledCurrencyColumn(header, numberGetter, false);
	}

	public Column<T, ?> addTotaledCurrencyColumn(String header, ValueGetter<Number, T> numberGetter, boolean blankIfZero) {
		return addAggregatedNumberColumn(header, AggregationMethod.TOTAL, null, numberGetter, "CURRENCY", blankIfZero);
	}

	public Column<T, ?> addTotaledNumberColumn(String header, ValueGetter<Number, T> numberGetter) {
		return addAggregatedNumberColumn(header, AggregationMethod.TOTAL, null, numberGetter, "0", false);
	}

	public Column<T, ?> addTotaledNumberColumn(String header, ValueGetter<Number, T> numberGetter, String format) {
		return addAggregatedNumberColumn(header, AggregationMethod.TOTAL, null, numberGetter, format, false);
	}

	public Column<T, ?> addTotaledNumberColumn(String header, WidgetCellCreator<T> cellCreator, ValueGetter<Number, T> numberGetter, String format) {
		return addAggregatedNumberColumn(header, AggregationMethod.TOTAL, cellCreator, numberGetter, format, false);
	}

	public <W extends Widget> Column<T, ?> addWidgetColumn(EntityCellTableColumn<C> column, WidgetCellCreator<T> widgetCreator) {
		return addWidgetColumn(column.getTitle(), widgetCreator, null);
	}

	public <W extends Widget> Column<T, ?> addWidgetColumn(EntityCellTableColumn<C> column, WidgetCellCreator<T> widgetCreator, ValueGetter<?, T> sortGetter) {
		return addWidgetColumn(column.getTitle(), widgetCreator, sortGetter);
	}

	public <W extends Widget> Column<T, ?> addWidgetColumn(String header, WidgetCellCreator<T> widgetCreator) {
		return addWidgetColumn(header, widgetCreator, null);
	}

	/**
	 * Add a Widget widget column.
	 * 
	 * @param header
	 *            Column header
	 * @param valueGetter
	 *            ValueGetter implementation to initialize and provide the desired widget.
	 */
	public <W extends Widget> Column<T, ?> addWidgetColumn(String header, WidgetCellCreator<T> widgetCreator, ValueGetter<?, T> sortGetter) {
		return addWidgetColumn(header, widgetCreator, sortGetter, null);
	}

	public <W extends Widget> Column<T, ?> addWidgetColumn(String header, final WidgetCellCreator<T> widgetCreator, ValueGetter<?, T> sortGetter,
			TextHeaderCreator<?> footerCreator) {
		if (columnDataMap.containsKey(header)) {
			return null;
		}
		if (sortGetter == null) {
			sortGetter = getDefaultValueGetter(widgetCreator);
		}

		cellWidgetCreators.add(widgetCreator);

		Column<T, T> col = widgetCreator.getColumn(this);

		addColumn(col, new TextHeader(header), createTextHeader(footerCreator));
		updateColumnMetaData(header, sortGetter, widgetCreator, HasHorizontalAlignment.ALIGN_DEFAULT, col);

		return col;
	}

	public void clear() {
		visibleItems.clear();
		hiddenItems.clear();
		unfilteredList.clear();
		onRowDataUpdate(false);
	}

	public void clearInitialized() {
		clear();
		isFinishedLoading = false;
		hasBeenPopulated = false;
		clearSelection();
	}

	public void clearSelection() {
		if (clearCommand != null) {
			clearCommand.execute();
		}
		lastSelectedItem = null;
		if (cbHeader != null) {
			cbHeader.setValue(false);
		}
	}

	public void disableUserSorting() {
		sortPolicy = CellSortPolicy.ON_USER_DISABLED;
	}

	public void flushWidgets() {
		for (WidgetCellCreator<?> cwc : cellWidgetCreators) {
			cwc.flushCachce();
		}
	}

	/**
	 * @return The {@link ArgMap} that was/will be used to fetch data for this table.
	 */
	public ArgMap<U> getArgMap() {
		return args;
	}

	/**
	 * @return A {@link Callback} for use with fetching data.
	 */
	public Callback<ArrayList<T>> getCallback() {
		if (callback == null) {
			createCallback();
		}

		return callback;
	}

	public Integer getColumnIndex(C column) {
		return columnDataMap.get(column.getTitle()).getIndex();
	}

	public ColumnData<T> getColumnMetaData(int index) {
		for (ColumnData<T> cd : columnDataMap.values()) {
			if (cd.getIndex() == index) {
				return cd;
			}
		}
		return null;
	}

	public String getDefaultSizePrefName() {
		return defaultSizePrefName;
	}

	public SortDirection getDefaultSortDirection() {
		return defaultSortDirection;
	}

	public int getDefaultSortIndex() {
		if (defaultByIndex) {
			return defaultSortIndex;
		}

		return columnDataMap.get(defaultSortHeader).getIndex();
	}

	public EnumSet<C> getDisplayColumns() {
		return displayColumns;
	}

	public String getExcelData() {
		return getExcelData(columnDataMap);
	}

	public List<T> getFullList() {
		return new ArrayList<T>(unfilteredList);
	}

	public List<T> getHiddenList() {
		return new ArrayList<T>(hiddenItems);
	}

	public T getItemById(Object id) {
		return entityIdMap.get(id);
	}

	public List<T> getList() {
		return new ArrayList<T>(visibleItems);
	}

	public RowStyles<T> getRowStyles() {
		return rowStyles;
	}

	public List<Integer> getSelectedItemIds() {
		ArrayList<Integer> items = new ArrayList<Integer>();
		if (isFinishedLoading) {
			for (T item : getSelectedItems()) {
				items.add((Integer) entityKeyProvider.getKey(item));
			}
		} else {
			items.addAll(initialSelectedItemIds);
		}
		return items;
	}

	public List<T> getSelectedItems() {
		HashSet<T> items = new HashSet<T>();
		if (isFinishedLoading) {
			for (T item : visibleItems) {
				if (getSelectionModel().isSelected(item)) {
					items.add(item);
				}
			}
		} else {
			items.addAll(initialSelectedItems);
		}
		return new ArrayList<T>(items);
	}

	public SelectionPolicy getSelectionPolicy() {
		return selectionPolicy;
	}

	/**
	 * @return the title
	 */
	@Override
	public String getTitle() {
		return titleBar.getTitleText();
	}

	/**
	 * Gets the existing Titlebar if available, or creates and registers one if not.
	 * 
	 * @return the titleBar registered to the Table.
	 */
	public CellTitleBar<T> getTitleBar() {
		return titleBar;
	}

	/**
	 * @return the titleStyle
	 */
	public TitleBarStyle getTitleStyle() {
		return titleBar.getType();
	}

	public boolean hasBeenPopulated() {
		return hasBeenPopulated;
	}

	public boolean hasTitleBar() {
		return titleBar != null;
	}

	public boolean hideItem(T item, boolean updateTotal, boolean fireRowUpdate) {
		if (visibleItems.contains(item)) {
			hiddenItems.add(item);
			boolean removed = visibleItems.remove(item);
			if (updateTotal && hasTitleBar()) {
				updateTitleTotal();
			}
			if (fireRowUpdate) {
				onRowDataUpdate(false);
			}
			if (removed && hiddenItems.contains(item)) {
				return true;
			}
		}
		return false;
	}

	public int indexOf(T item) {
		return getVisibleItems().indexOf(item);
	}

	public void insertItem(T item, int index) {
		if (hiddenItems.contains(item)) {
			hiddenItems.remove(item);
		}
		visibleItems.remove(item);
		visibleItems.add(index, item);
		unfilteredList.remove(item);
		unfilteredList.add(item);
		entityIdMap.put(entityKeyProvider.getKey(item), item);
		onRowDataUpdate(true);
	}

	public boolean isFinishedLoading() {
		return isFinishedLoading;
	}

	public boolean isHidden(T item) {
		return hiddenItems.contains(item);
	}

	public boolean isItemSelected(T item) {
		return getSelectionModel().isSelected(item);
	}

	public boolean isItemVisible(T item) {
		return visibleItems.contains(item);
	}

	public boolean isMultiSelect() {
		return selectionPolicy.equals(SelectionPolicy.MULTI_ROW);
	}

	public boolean itemContainsString(String value, T item) {
		HashMap<String, ColumnData<T>> data = new HashMap<String, ColumnData<T>>(columnDataMap);
		for (ColumnData<T> columnData : data.values()) {
			ValueGetter<?, T> vg = columnData.getValueGetter();
			boolean result = false;
			if (vg != null) {
				Object o = vg.get(item);
				if (o instanceof HasText) {
					result = ((HasText) o).getText().toLowerCase().contains(value.toLowerCase());
				} else if (o instanceof Widget) {
					result = new HTML(o.toString()).getText().toLowerCase().contains(value.toLowerCase());
				} else if (o != null) {
					result = o.toString().toLowerCase().contains(value.toLowerCase());
				}
			}
			if (result) {
				return true;
			}
		}
		return false;
	}

	public void linkData(EntityCellTable<T, U, C> child) {
		child.partners.add(this);
		partners.add(child);
	}

	public boolean linksOpenNewTab() {
		return linksOpenNewTab;
	}

	/**
	 * A wrapper method for {@link #fetchData()} (which is defined in subclasses), for the purpose of creating ID and column maps.
	 */
	public void populate() {
		populate(true);
	}

	/**
	 * A wrapper method for {@link #fetchData()} (which is defined in subclasses), for the purpose of creating ID and column maps.
	 */
	public void populate(boolean fetchData) {
		hasBeenPopulated = true;
		setupColumns();
		if (fetchData) {
			fetchData();
		}
	}

	/**
	 * Populate table from a pre-existing list.
	 */
	public void populate(List<T> items) {
		populate(false);
		getCallback().onSuccess(new ArrayList<T>(items));
	}

	public void refresh() {
		refresh(true);
	}

	public void refresh(boolean flushWidgets) {
		if (flushWidgets) {
			flushWidgets();
		}
		setRowData(0, visibleItems);
		setRowCount(visibleItems.size());
	}

	/**
	 * Registers a {@link MaxHeightScrollPanel} to which the table must be placed inside by the caller. MaxHeightScrollPanel.adjustHeight() will automatically
	 * be called once it's registered.
	 * 
	 * @param scrollPanel
	 */
	public void registerScrollPanel(final MaxHeightScrollPanel sp) {
		this.scrollPanel = sp;
		addRangeChangeHandler(new RangeChangeEvent.Handler() {
			@Override
			public void onRangeChange(RangeChangeEvent event) {
				scrollPanel.adjustSize();
			}
		});
	}

	public void registerTitleBar(CellTitleBar<T> titleBar) {
		this.titleBar = titleBar;
		titleBar.setCellTable(this);
		updateTitleTotal();
	}

	public void removeColumn(C column) {
		if (displayColumns.contains(column)) {
			displayColumns.remove(column);
		}
		if (columnDataMap.containsKey(column.getTitle())) {
			ColumnData<T> data = columnDataMap.remove(column.getTitle());
			int index = data.getIndex();
			for (ColumnData<T> cd : columnDataMap.values()) {
				if (cd.getIndex() > index) {
					cd.setIndex(cd.getIndex() - 1);
				}
			}
			super.removeColumn(data.getIndex());
		}
	}

	@Override
	public void removeColumn(Column<T, ?> column) {
		removeColumn(getColumnIndex(column));
	}

	@Override
	public void removeColumn(int index) {
		String removed = null;
		for (ColumnData<T> data : columnDataMap.values()) {
			if (data.getIndex() == index) {
				removed = data.getHeader();
			} else if (data.getIndex() > index) {
				data.setIndex(data.getIndex() - 1);
			}
		}
		super.removeColumn(index);
		columnDataMap.remove(removed);
	}

	public void removeItem(T item) {
		removeItem(item, true, true);
	}

	public void removeItem(T item, boolean update, boolean sort) {
		if (!isFinishedLoading) {
			return;
		}
		visibleItems.remove(item);
		unfilteredList.remove(item);
		hiddenItems.remove(item);
		entityIdMap.remove(entityKeyProvider.getKey(item));
		if (update) {
			onRowDataUpdate(sort);
		}
	}

	public void removeItems(List<T> items) {
		if (!isFinishedLoading) {
			return;
		}
		for (T item : items) {
			removeItem(item, false, false);
		}
		onRowDataUpdate(true);
	}

	public void revertSelectedItems() {
		clearSelection();
		if (getList() == null) {
			return;
		}
		for (T item : visibleItems) {
			if (initialSelectedItemIds.contains(item.getId())) {
				setItemSelected(item, true);
			} else if (initialSelectedItems.contains(item)) {
				setItemSelected(item, true);
			}
		}
	}

	public void saveSelectedItems() {
		initialSelectedItemIds.clear();
		for (T item : getSelectedItems()) {
			initialSelectedItemIds.add(item.getId());
		}
	}

	/**
	 * Sets the {@link ArgMap} to be used to fetch the data for this table.
	 * 
	 * @param args
	 */
	public void setArgMap(ArgMap<U> args) {
		this.args = args;
	}

	public void setColumnAlignment(int column, HorizontalAlignmentConstant alignment) {
		super.getColumn(column).setHorizontalAlignment(alignment);
	}

	public void setDefaultSizePrefName(String defaultSizePrefName) {
		this.defaultSizePrefName = defaultSizePrefName;
	}

	public void setDefaultSortColumn(EntityCellTableColumn<C> header, SortDirection sortDirection) {
		setDefaultSortColumn(header.getTitle(), sortDirection);
	}

	/**
	 * Set the sort direction of the column at the specified index.
	 * 
	 * @param index
	 *            index of the column to be sorted.
	 * @param sortDirection
	 *            direction to sort the column.
	 */
	public void setDefaultSortColumn(int index, SortDirection sortDirection) {
		if (setSortFromPref()) {
			return;
		}

		this.defaultSortIndex = index;
		this.defaultSortDirection = sortDirection;
		this.defaultByIndex = true;
	}

	/**
	 * @param header
	 *            header of the column to be sorted.
	 * @param sortDirection
	 *            direction to sort the column.
	 */
	public void setDefaultSortColumn(String header, SortDirection sortDirection) {
		if (setSortFromPref()) {
			return;
		}

		this.defaultSortHeader = header;
		this.defaultSortDirection = sortDirection;
		this.defaultByIndex = false;
	}

	// @SafeVarargs
	public final void setDisplayColumns(C... displayColumns) {
		this.displayColumns = EnumSet.copyOf(Arrays.asList(displayColumns));
	}

	public void setItemEnabled(T item, boolean enabled) {
		if (getSelectionCell() instanceof CheckboxCell) {
			CheckboxCell cell = (CheckboxCell) getSelectionCell();
			if (enabled) {
				cell.enable(item);
			} else {
				cell.disable(item);
			}
		} else if (getSelectionCell() instanceof RadioButtonCell) {
			RadioButtonCell cell = (RadioButtonCell) getSelectionCell();
			if (enabled) {
				cell.enable(item);
			} else {
				cell.disable(item);
			}
		}
	}

	public void setItemSelected(T item, boolean selected) {
		getSelectionModel().setSelected(item, selected);
	}

	public void setItemsVisible(Collection<T> items, boolean visible) {
		setItemsVisible(items, visible, false);
	}

	public void setItemsVisible(Collection<T> items, boolean visible, boolean updateTotal) {
		for (T item : items) {
			setItemVisible(item, visible, updateTotal);
		}
		onRowDataUpdate(false);
	}

	public void setItemVisible(T item, boolean visible) {
		setItemVisible(item, visible, false);
	}

	public void setItemVisible(T item, boolean visible, boolean updateTotal) {
		if (visible) {
			showItem(item, false, updateTotal);
		} else {
			hideItem(item, updateTotal, true);
		}
	}

	public void setLinksOpenNewTab(boolean linksOpenNewTab) {
		this.linksOpenNewTab = linksOpenNewTab;
	}

	@Override
	public void setRowStyles(RowStyles<T> rowStyles) {
		super.setRowStyles(rowStyles);
		this.rowStyles = rowStyles;
	}

	public void setSelectedItem(T item) {
		clearSelection();
		if (!isFinishedLoading) {
			initialSelectedItemIds.add(item.getId());
			initialSelectedItems.add(item);
		} else {
			setItemSelected(item, true);
		}
	}

	public void setSelectedItemById(int id) {
		clearSelection();
		if (!isFinishedLoading) {
			initialSelectedItemIds.add(id);
		} else {
			for (T item : visibleItems) {
				if (item.getId() == id) {
					setItemSelected(item, true);
					break;
				}
			}
		}
	}

	public void setSelectedItems(Collection<T> items) {
		clearSelection();
		if (!isFinishedLoading) {
			for (T item : items) {
				initialSelectedItemIds.add(item.getId());
			}
			initialSelectedItems.addAll(items);
		} else {
			for (T item : items) {
				setItemSelected(item, true);
			}
		}
	}

	public void setSelectedItemsById(Collection<Integer> ids) {
		clearSelection();
		if (isFinishedLoading) {
			initialSelectedItemIds.addAll(ids);
		} else {
			for (T item : visibleItems) {
				if (ids.contains(item.getId())) {
					setItemSelected(item, true);
					break;
				}
			}
		}
	}

	@Override
	public void setSelectionModel(SelectionModel<? super T> selectionModel) {
		super.setSelectionModel(selectionModel);
	}

	public void setSelectionPolicy(SelectionPolicy selectionPolicy) {
		this.selectionPolicy = selectionPolicy;

		if (selectionColumn != null && getColumnCount() > 0) {
			removeColumn(selectionColumn);
		}

		SelectionModel<? super T> existingSm = getSelectionModel();

		switch (selectionPolicy) {
		case MULTI_ROW:
			final MultiSelectionModel<T> msm = new MultiSelectionModel<T>(entityKeyProvider);
			setSelectionModel(msm);

			final CheckboxCell cbCell = (CheckboxCell) (selectionCell = new CheckboxCell(true, true));
			selectionColumn = new Column<T, Boolean>(cbCell) {
				@Override
				public Boolean getValue(T object) {
					return getSelectionModel().isSelected(object);
				}
			};
			selectionColumn.setFieldUpdater(new FieldUpdater<T, Boolean>() {
				@Override
				public void update(int index, T object, Boolean value) {
					setItemSelected(object, value);
				}
			});
			selectionColumn.setSortable(false);

			clearCommand = new Command() {
				@Override
				public void execute() {
					msm.clear();
				}
			};

			cbHeader = new CheckboxHeader<T, U>(cbCell, this);
			insertColumn(0, selectionColumn, cbHeader);

			addCellPreviewHandler(new CellPreviewEvent.Handler<T>() {
				@Override
				public void onCellPreview(CellPreviewEvent<T> event) {
					if (event.getColumn() == 0 && "click".equals(event.getNativeEvent().getType())) {
						int totalItems = visibleItems.size();
						boolean select = !isItemSelected(event.getValue());
						if (lastSelectedItem != null && event.getNativeEvent().getShiftKey()) {
							int start = indexOf(lastSelectedItem);
							int end = indexOf(event.getValue());
							if (start > end) {
								int swap = end;
								end = start;
								start = swap;
							}
							for (int i = start; i < end; i++) {
								T item = visibleItems.get(i);
								setItemSelected(item, cbCell.isEnabled(item) && select);
							}
						}
						lastSelectedItem = event.getValue();
						setItemSelected(lastSelectedItem, select);
						int currentlySelected = getSelectedItems().size();
						if (currentlySelected == totalItems) {
							cbHeader.setValue(true);
						} else if (currentlySelected < totalItems) {
							cbHeader.setValue(false);
						}
					}
				}
			});

			break;
		case MULTI_BY_ROW:
			final MultiSelectionModel<T> msmbr = new MultiSelectionModel<T>(entityKeyProvider);
			setSelectionModel(msmbr);
			clearCommand = new Command() {
				@Override
				public void execute() {
					msmbr.clear();
				}
			};
			break;
		case NONE:
			setSelectionModel(new NoSelectionModel<T>(entityKeyProvider));
			break;
		case ONE_ROW:
			final SingleSelectionModel<T> ssm = new SingleSelectionModel<T>(entityKeyProvider);
			setSelectionModel(ssm);

			clearCommand = new Command() {
				@Override
				public void execute() {
					ssm.setSelected(ssm.getSelectedObject(), false);
				}
			};

			selectionCell = new RadioButtonCell(true, true);
			selectionColumn = new Column<T, Boolean>(selectionCell) {
				@Override
				public Boolean getValue(T object) {
					return getSelectionModel().isSelected(object);
				}
			};
			selectionColumn.setFieldUpdater(new FieldUpdater<T, Boolean>() {
				@Override
				public void update(int index, T object, Boolean value) {
					setItemSelected(object, value);
				}
			});
			selectionColumn.setSortable(false);
			insertColumn(0, selectionColumn, " ");

			break;
		}
		if (selectionColumn != null) {
			setColumnWidth(selectionColumn, "28px");
			updateColumnMetaData(" ", null, null, HasHorizontalAlignment.ALIGN_DEFAULT, selectionColumn);
		}
		if (existingSm != null && isFinishedLoading) {
			for (T item : getFullList()) {
				getSelectionModel().setSelected(item, existingSm.isSelected(item));
				for (ParameterHandler<T> cmd : selectionPolicyChangeCommands) {
					cmd.execute(item);
				}
			}
		}
	}

	public void setSortingEnabled(final boolean enabled) {
		sortPolicy = enabled ? CellSortPolicy.ON : CellSortPolicy.OFF;
		if (isFinishedLoading) {
			setAllColumnsSortable(enabled, true);
		}
	}

	/**
	 * Set the text to appear in this table's title bar.
	 * 
	 * @param title
	 *            the title to set
	 */
	@Override
	public void setTitle(String title) {
		titleBar.setTitleText(title);

		setSortFromPref();
	}

	/**
	 * @param titleStyle
	 *            the titleStyle to set
	 */
	public void setTitleStyle(TitleBarStyle titleStyle) {
		titleBar.setType(titleStyle);
	}

	/**
	 * Show all items and sort.
	 */
	public void showAllItems() {
		showAllItems(true);
	}

	/**
	 * Show all items and optionally sort.
	 * 
	 * @param sort
	 *            True if items should be sorted.
	 */
	public void showAllItems(boolean sort) {
		visibleItems.removeAll(hiddenItems);
		visibleItems.addAll(hiddenItems);
		hiddenItems.clear();
		if (hasTitleBar()) {
			updateTitleTotal();
		}
		onRowDataUpdate(sort);
	}

	/**
	 * Add an item and sort the resulting list.
	 * 
	 * @param item
	 *            item to add to the table.
	 * @return true if an item was shown.
	 */
	public boolean showItem(T item) {
		return showItem(item, true, false);
	}

	/**
	 * Show a hidden item in the cell table. This will not add items that the table does not already contain.
	 * 
	 * @param item
	 *            item to reveal.
	 * @param sort
	 *            True if you would like to sort the results.
	 * @return true if an item was shown.
	 */
	public boolean showItem(T item, boolean sort, boolean updateTitle) {
		return showItem(item, sort, updateTitle, true);
	}

	public boolean showItem(T item, boolean sort, boolean updateTitle, boolean fireUpdate) {
		if (hiddenItems.contains(item)) {
			visibleItems.add(item);
			if (updateTitle && hasTitleBar()) {
				updateTitleTotal();
			}
			if (fireUpdate) {
				onRowDataUpdate(sort);
			}
			return hiddenItems.remove(item) && visibleItems.contains(item);
		}
		return false;
	}

	/**
	 * Sort table using existing column and SortDirection information.
	 */
	public void sort() {
		ColumnSortEvent.fire(this, getColumnSortList());
	}

	/**
	 * Sort table using specified column index and SortDirection.
	 * 
	 * @param index
	 *            Column to sort from.
	 * @param direction
	 *            Direction to sort.
	 */
	public void sort(int index, SortDirection direction) {
		getColumnSortList().clear();
		getColumnSortList().push(new ColumnSortInfo(getColumn(index), SortDirection.SORT_ASC.equals(direction)));
	}

	public void updateColumnMetaData(ColumnData<T> data, Column<T, ?> column) {
		updateColumnMetaData(data.getHeader(), data.getSortGetter(), data.getValueGetter(), data.getHorizontalAlignment(), column);
	}

	/**
	 * Stores extra information about columns. This should be called before a column is added to the underlying CellTable.
	 * 
	 * @param columnHeader
	 *            the string header for the column.
	 * @param sortGetter
	 *            the value getter the column will use to sort its data.
	 * @param valueGetter
	 *            the value getter the column will use to display its data (may be omitted)
	 * @param alignment
	 *            the default alignment for the column.
	 */
	public void updateColumnMetaData(String columnHeader, ValueGetter<?, T> sortGetter, ValueGetter<?, T> valueGetter, HorizontalAlignmentConstant alignment,
			Column<T, ?> column) {
		if (columnDataMap.containsKey(columnHeader)) {
			ColumnData<T> cd = columnDataMap.get(columnHeader);
			cd.setHorizontalAlignment(alignment);
			cd.setValueGetter(valueGetter);
			cd.setSortGetter(sortGetter);
		} else {
			ColumnData<T> cd = new ColumnData<T>(columnHeader, getColumnIndex(column));
			cd.setHorizontalAlignment(alignment);
			cd.setValueGetter(valueGetter);
			cd.setSortGetter(sortGetter);
			columnDataMap.put(columnHeader, cd);
		}
	}

	public void updateTitleTotal() {
		if (getList() != null) {
			titleBar.setTotal(visibleItems.size());
		}
	}

	private Column<T, ?> addAggregatedNumberColumn(final String header, AggregationMethod method, WidgetCellCreator<T> cellCreator,
			final ValueGetter<Number, T> numberGetter, final String format, final boolean blankIfZero) {
		if (cellCreator == null) {
			cellCreator = new WidgetCellCreator<T>() {
				@Override
				protected Widget createWidget(T item) {
					Number val = numberGetter.get(item);
					if (val.doubleValue() == 0 && blankIfZero) {
						return new Label();
					}

					Label label;
					if ("CURRENCY".equals(format)) {
						label = Formatter.getCurrencyLabel(val.doubleValue());
					} else {
						label = new Label(NumberFormat.getFormat(format).format(val));
					}
					label.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
					return label;
				}
			};
		}

		Column<T, ?> col = addWidgetColumn(header, cellCreator, numberGetter, new TextHeaderCreator<HasText>() {
			@Override
			protected HasText getWidget() {
				return aggregateFooterDataMap.get(header).label;
			}
		});

		AggregateFooterData data = new AggregateFooterData(method, numberGetter, format, new Label());
		data.label.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
		aggregateFooterDataMap.put(header, data);

		return col;
	}

	/**
	 * @return A {@link Callback} for use with fetching data.
	 */
	private void createCallback() {
		callback = new Callback<ArrayList<T>>(true) {
			@Override
			protected void doOnSuccess(ArrayList<T> results) {
				handleResults(results);
			}
		};
	}

	private Header<String> createTextHeader(final TextHeaderCreator<?> creator) {
		if (creator == null) {
			return null;
		}

		Cell<String> footerCell = new ClickableTextCell() {
			@Override
			public void onBrowserEvent(Context context, Element parent, String value, NativeEvent event, ValueUpdater<String> valueUpdater) {
				super.onBrowserEvent(context, parent, value, event, valueUpdater);

				if (creator.getClickCommand() != null) {
					creator.getClickCommand().execute();
				}
			}

			@Override
			public void render(Context context, String data, SafeHtmlBuilder sb) {
				sb.appendHtmlConstant(creator.getWidget().toString());
			}
		};

		Header<String> header = new Header<String>(footerCell) {
			@Override
			public String getValue() {
				return creator.getWidget().getText();
			}
		};

		return header;
	}

	private <W extends Widget> ValueGetter<?, T> getDefaultValueGetter(final WidgetCellCreator<T> widgetCreator) {
		return new ValueGetter<String, T>() {
			private Map<T, String> sortCache = new HashMap<T, String>();

			@Override
			public String get(T item) {
				String val = sortCache.get(item);

				if (val == null) {
					Widget widget = widgetCreator.get(item);

					if (widget instanceof HasText) {
						val = ((HasText) widget).getText();
					} else if (widget instanceof HasSortValue) {
						val = ((HasSortValue) widget).getSortValue();
					} else {
						val = new HTML(widget.toString()).getText();
					}
					sortCache.put(item, val);
				}
				return val;
			}
		};
	}

	private AbstractEditableCell<Boolean, Boolean> getSelectionCell() {
		return selectionCell;
	}

	/**
	 * For re-use between the two callbacks.
	 * 
	 * @param results
	 */
	private void handleResults(List<T> results) {
		results = preprocessResults(results);

		if (CellSortPolicy.ON_USER_DISABLED.equals(sortPolicy)) {
			setAllColumnsSortable(true, false);
		}
		setList(results);
		if (!visibleItems.isEmpty()) {
			setupSorting();
			Scheduler.get().scheduleDeferred(new ScheduledCommand() {
				@Override
				public void execute() {
					sort();
				}
			});
		}
		if (CellSortPolicy.ON_USER_DISABLED.equals(sortPolicy)) {
			setAllColumnsSortable(false, true);
		}
		if (hasTitleBar()) {
			updateTitleTotal();
		}

		setAggregateFooterLabels(results);

		for (final DataReturnHandler handler : dataReturnHandlers) {
			Scheduler.get().scheduleDeferred(new ScheduledCommand() {
				@Override
				public void execute() {
					handler.onDataReturn();
				}
			});
		}
	}

	private void setAggregateFooterLabels(List<T> results) {
		for (AggregateFooterData data : aggregateFooterDataMap.values()) {
			double aggregateValue = 0;

			for (T item : results) {
				aggregateValue += data.valueGetter.get(item).doubleValue();
			}

			if (data.method == AggregationMethod.AVERAGE) {
				aggregateValue /= results.size();
			}

			if ("CURRENCY".equals(data.format)) {
				data.label.setText(Formatter.formatCurrency(aggregateValue));

				if (aggregateValue >= 0) {
					data.label.removeStyleName("inactiveText");
				} else {
					data.label.setStyleName("inactiveText");
				}
			} else {
				data.label.setText(NumberFormat.getFormat(data.format).format(aggregateValue));
			}
		}
	}

	private void setAllColumnsSortable(final boolean sortable, boolean scheduleDeferred) {
		if (scheduleDeferred) {
			Scheduler.get().scheduleDeferred(new ScheduledCommand() {
				@Override
				public void execute() {
					for (int i = 0; i < getColumnCount(); i++) {
						getColumn(i).setSortable(sortable);
					}
					// refresh();
				}
			});
		} else {
			for (int i = 0; i < getColumnCount(); i++) {
				getColumn(i).setSortable(sortable);
			}
			// refresh();
		}
	}

	private void setList(List<T> list) {
		if (list.size() == (Common.MAX_DATA_ROWS + 1)) {
			list.remove(list.size() - 1);
			String message = "More than " + Common.MAX_DATA_ROWS + " results found. Only " + Common.MAX_DATA_ROWS + " shown.";
			AlertDialog.alert("Results Exceed Limit", new Label(message));
		}
		unfilteredList = new HashSet<T>(list);
		if (maintainHidden) {
			for (T entity : list) {
				if (hiddenItems.contains(entity)) {
					hiddenItems.remove(entity);
					hiddenItems.add(entity);
				} else {
					visibleItems.remove(entity);
					visibleItems.add(entity);
				}
			}
		} else {
			visibleItems.clear();
			visibleItems.addAll(list);
		}
		unfilteredList = new HashSet<T>(list);
		if (!SelectionPolicy.NONE.equals(getSelectionPolicy())) {
			for (T item : list) {
				if (initialSelectedItemIds.contains(item.getId()) || initialSelectedItems.contains(item)) {
					setItemSelected(item, true);
				}
			}
			initialSelectedItemIds.clear();
			initialSelectedItems.clear();
		}

		for (T item : list) {
			entityIdMap.put(entityKeyProvider.getKey(item), item);
		}
		isFinishedLoading = true;
		onRowDataUpdate(false);
	}

	/**
	 * Returns whether the sort value was set
	 * 
	 * @return
	 */
	private boolean setSortFromPref() {
		return true;
		// if (sortPolicy != CellSortPolicy.ON) {
		// return false;
		// }
		//
		// String prefName = getSortPrefString();
		// if (prefName == null) {
		// return false;
		// }
		//
		// prefName = TABLE_SORT_PREF + prefName;
		//
		// String sortVal = Application.getUserPreferences().get(prefName);
		// if (sortVal == null) {
		// return false;
		// }
		//
		// String[] vals = sortVal.split(";");
		// this.defaultSortDirection = "A".equals(vals[0]) ? SortDirection.SORT_ASC : SortDirection.SORT_DESC;
		// this.defaultSortIndex = Integer.parseInt(vals[1]);
		// this.defaultByIndex = true;
		//
		// return true;
	}

	private void setupSorting() {
		if (noSortRegistration != null) {
			noSortRegistration.removeHandler();
			noSortRegistration = null;
		}
		if (sortPolicy == CellSortPolicy.OFF) {
			getColumnSortList().clear();
			noSortRegistration = addColumnSortHandler(new ColumnSortEvent.Handler() {
				@Override
				public void onColumnSort(ColumnSortEvent event) {
					setRowData(0, visibleItems);
				}
			});
			return;
		}

		if (sortRegistration != null) {
			sortRegistration.removeHandler();
			sortRegistration = null;
		}
		ListHandler<T> columnSortHandler = new ListHandler<T>(visibleItems) {
			@Override
			public void onColumnSort(ColumnSortEvent event) {
				if (CellSortPolicy.ON.equals(sortPolicy)) {
					Column<T, ?> c = (Column<T, ?>) event.getColumn();
					int index = getColumnIndex(c);
					SortDirection curSort = event.isSortAscending() ? SortDirection.SORT_ASC : SortDirection.SORT_DESC;

					String sortPrefString = getSortPrefString();
					if ((defaultSortIndex != index || defaultSortDirection != curSort) && sortPrefString != null && index >= 0) {
						sortPrefString = TABLE_SORT_PREF + sortPrefString;
						String sortDir = event.isSortAscending() ? "A" : "D";
						UserPreferences.save(sortPrefString, sortDir + ";" + index);
						setSortFromPref();
					}
					super.onColumnSort(event);
				} else if (CellSortPolicy.ON_USER_DISABLED.equals(sortPolicy)) {
					super.onColumnSort(event);
				}
				setRowData(0, visibleItems);
			}
		};
		sortRegistration = addColumnSortHandler(columnSortHandler);
		getColumnSortList().clear();
		for (final ColumnData<T> columnData : columnDataMap.values()) {
			final Column<T, ?> c = getColumn(columnData.getIndex());
			columnSortHandler.setComparator(c, new Comparator<T>() {
				@SuppressWarnings({ "unchecked", "rawtypes" })
				@Override
				public int compare(T o1, T o2) {
					ValueGetter<?, T> valueRetriever = columnData.getSortGetter();
					if (valueRetriever == null) {
						return 0;
					}
					Object o1Value = valueRetriever.get(o1);
					Object o2Value = valueRetriever.get(o2);

					if (o1Value == null && o2Value == null) {
						return 0;
					}
					if (o1Value == null && o2Value != null) {
						return -1;
					}
					if (o1Value != null && o2Value == null) {
						return 1;
					}

					if (o1Value == o2Value) {
						return 0;
					}

					if (o1Value instanceof Comparable && o2Value instanceof Comparable) {
						Comparable so1Value = (Comparable) o1Value;
						Comparable so2Value = (Comparable) o2Value;
						return so1Value.compareTo(so2Value);
					}
					return 0;
				}
			});
		}
		if (!defaultByIndex) {
			if (columnDataMap.containsKey(defaultSortHeader)) {
				setDefaultSortColumn(columnDataMap.get(defaultSortHeader).getIndex(), defaultSortDirection);
			}
		}
		if (getColumnCount() > defaultSortIndex) {
			getColumnSortList().push(new ColumnSortInfo(getColumn(defaultSortIndex), SortDirection.SORT_ASC.equals(defaultSortDirection)));
		}
	}

	protected Widget createSubRowCollapseControl(final T entity, final List<T> children) {
		final Image toggle = new Image(MainImageBundle.INSTANCE.expand());
		toggle.setTitle("Show contents");
		toggle.addMouseDownHandler(new MouseDownHandler() {
			@Override
			public void onMouseDown(MouseDownEvent event) {
				T firstSavedChild = null;
				for (T child : children) {
					if (child.isSaved()) {
						firstSavedChild = child;
						break;
					}
				}
				if (firstSavedChild == null) {
					return;
				}

				final boolean visible = isItemVisible(firstSavedChild);
				if (visible) {
					for (T child : children) {
						visibleItems.remove(child);
					}
					toggle.setResource(MainImageBundle.INSTANCE.expand());
					toggle.setTitle("Show contents");
				} else {
					toggle.setResource(MainImageBundle.INSTANCE.collapse());
					toggle.setTitle("Hide contents");

					int parentIndex = visibleItems.indexOf(entity);
					int childIndex = 1;
					for (T child : children) {
						visibleItems.add(parentIndex + childIndex++, child);
					}
				}
				refresh(false);
			}
		});
		toggle.addStyleName("pointer");

		for (T child : children) {
			hideItem(child, false, false);
		}
		refresh(false);
		return toggle;
	}

	/**
	 * Subclasses must implement this method using the desired pipeline method to retrieve the data, and must use the {@link getCallBack} method for the
	 * callback.
	 * 
	 * @param cells
	 *            A list of entities to place in the table
	 */
	protected abstract void fetchData();

	protected TableCellElement findNearestParentCell(Element elem) {
		while ((elem != null) && (elem != getElement())) {
			String tagName = elem.getTagName();
			if ("td".equalsIgnoreCase(tagName) || "th".equalsIgnoreCase(tagName)) {
				return elem.cast();
			}
			elem = elem.getParentElement();
		}
		return null;
	}

	protected Map<String, ColumnData<T>> getColumnDataCopy() {
		return new HashMap<String, ColumnData<T>>(columnDataMap);
	}

	protected String getExcelData(Map<String, ColumnData<T>> columnDataMap) {
		StringBuffer data = new StringBuffer();
		List<String> headerValues = new ArrayList<String>();

		Set<ColumnData<T>> sortedColumnData = new TreeSet<ColumnData<T>>(new Comparator<ColumnData<T>>() {
			@Override
			public int compare(ColumnData<T> o1, ColumnData<T> o2) {
				return new Integer(o1.getIndex()).compareTo(o2.getIndex());
			}
		});
		sortedColumnData.addAll(columnDataMap.values());

		for (ColumnData<T> columnData : sortedColumnData) {
			headerValues.add(Common.getExcelValue(columnData.getHeader()));
		}
		data.append(Common.join(headerValues, ",") + "\n");

		for (T item : visibleItems) {
			List<String> rowValues = new ArrayList<String>();
			for (ColumnData<T> columnData : sortedColumnData) {
				ValueGetter<?, T> getter = columnData.getSortGetter();
				if (getter != null) {
					Object value = getter.get(item);
					if (value == null) {
						value = "";
					}
					rowValues.add(Common.getExcelValue(value.toString()));
				}
			}
			data.append(Common.join(rowValues, ",") + "\n");
		}
		return data.toString();
	}

	protected String getPageSizePrefString() {
		String title = getTitle();
		if (Common.isNullOrBlank(title)) {
			return getDefaultSizePrefName();
		}
		return Url.getParam("page") + ";" + title;
	}

	protected String getSortPrefString() {
		String title = getTitle();
		if (Common.isNullOrBlank(title)) {
			return null;
		}
		return Url.getParam("page") + ";" + title;
	}

	protected void onRowDataUpdate(boolean sort) {
		onRowDataUpdate(sort, null);
	}

	protected void onRowDataUpdate(boolean sort, final boolean flushWidgets, EntityCellTable<T, U, C> callingPartner) {
		if (sort) {
			sort();
		}
		Scheduler.get().scheduleDeferred(new ScheduledCommand() {
			@Override
			public void execute() {
				refresh(flushWidgets);
				if (titleBar == null || (titleBar != null && titleBar.getPager() == null)
						|| (titleBar != null && titleBar.getPager() != null && titleBar.getPager().getDisplay() == null)) {
					setVisibleRange(0, visibleItems.size());
				}
				if (EntityCellTable.this instanceof HasDisableCriteria<?>) {
					((HasDisableCriteria<?>) EntityCellTable.this).applyDisableCriteria();
				}
				RowCountChangeEvent.fire(EntityCellTable.this, visibleItems.size(), true);
			}
		});
		for (EntityCellTable<T, U, C> partner : partners) {
			if (partner.hasBeenPopulated) {
				partner.hiddenItems = hiddenItems;
				partner.visibleItems = visibleItems;
				partner.unfilteredList = unfilteredList;
				if (!partner.equals(callingPartner)) {
					partner.onRowDataUpdate(sort, this);
				}
			} else {
				partner.populate(getFullList());
			}
		}
	}

	protected void onRowDataUpdate(boolean sort, EntityCellTable<T, U, C> callingPartner) {
		onRowDataUpdate(sort, true, callingPartner);
	}

	/**
	 * Override if extra processing of server data is needed before the table is created.
	 * 
	 * @param results
	 */
	protected List<T> preprocessResults(List<T> results) {
		return results;
	}

	@Override
	protected void renderRowValues(SafeHtmlBuilder sb, List<T> values, int start, SelectionModel<? super T> selectionModel) {
		try {
			super.renderRowValues(sb, values, start, selectionModel);
		} catch (JavaScriptException e) {
			// The JavaScriptException "(TypeError): this.insertBefore is not a function" gets caught
			// here when this CellTable is populated with no results. Ideally we want to stop the
			// exception from coming up in the first place but this will stop the exception from coming up.
		}
	}

	/**
	 * Define the columns for this table.
	 */
	protected abstract void setColumns();

	protected void setupColumns() {
		columnDataMap.clear();
		while (getColumnCount() > 0) {
			super.removeColumn(0);
		}
		setSelectionPolicy(getSelectionPolicy());
		setColumns();
	}
}