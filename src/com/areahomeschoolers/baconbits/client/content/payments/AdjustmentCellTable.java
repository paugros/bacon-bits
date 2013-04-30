package com.areahomeschoolers.baconbits.client.content.payments;

import java.util.Date;

import com.areahomeschoolers.baconbits.client.ServiceCache;
import com.areahomeschoolers.baconbits.client.content.payments.AdjustmentCellTable.AdjustmentColumn;
import com.areahomeschoolers.baconbits.client.event.ConfirmHandler;
import com.areahomeschoolers.baconbits.client.rpc.Callback;
import com.areahomeschoolers.baconbits.client.rpc.service.PaymentService;
import com.areahomeschoolers.baconbits.client.rpc.service.PaymentServiceAsync;
import com.areahomeschoolers.baconbits.client.widgets.ClickLabel;
import com.areahomeschoolers.baconbits.client.widgets.ConfirmDialog;
import com.areahomeschoolers.baconbits.client.widgets.cellview.EntityCellTable;
import com.areahomeschoolers.baconbits.client.widgets.cellview.EntityCellTableColumn;
import com.areahomeschoolers.baconbits.client.widgets.cellview.ValueGetter;
import com.areahomeschoolers.baconbits.client.widgets.cellview.WidgetCellCreator;
import com.areahomeschoolers.baconbits.shared.dto.Adjustment;
import com.areahomeschoolers.baconbits.shared.dto.Arg.PaymentArg;
import com.areahomeschoolers.baconbits.shared.dto.ArgMap;

import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public final class AdjustmentCellTable extends EntityCellTable<Adjustment, PaymentArg, AdjustmentColumn> {
	public enum AdjustmentColumn implements EntityCellTableColumn<AdjustmentColumn> {
		TYPE("Adjustment type"), USER("User"), AMOUNT("Amount"), TOTALED_AMOUNT("Amount"), STATUS("Status"), ADDED_DATE("Added date"), DESCRIPTION(
				"Description");

		private String title;

		AdjustmentColumn(String title) {
			this.title = title;
		}

		@Override
		public String getTitle() {
			return title;
		}
	}

	private PaymentServiceAsync paymentService = (PaymentServiceAsync) ServiceCache.getService(PaymentService.class);
	private AdjustmentDialog dialog;

	public AdjustmentCellTable(ArgMap<PaymentArg> args) {
		this();
		setArgMap(args);
	}

	private AdjustmentCellTable() {
		setDefaultSortColumn(AdjustmentColumn.ADDED_DATE, SortDirection.SORT_DESC);
		setDisplayColumns(AdjustmentColumn.ADDED_DATE, AdjustmentColumn.TOTALED_AMOUNT, AdjustmentColumn.STATUS, AdjustmentColumn.TYPE, AdjustmentColumn.USER,
				AdjustmentColumn.DESCRIPTION);

		disablePaging();
	}

	public AdjustmentDialog getDialog() {
		return dialog;
	}

	public void setDialog(AdjustmentDialog dialog) {
		this.dialog = dialog;
	}

	@Override
	protected void fetchData() {
		paymentService.getAdjustments(getArgMap(), getCallback());
	}

	@Override
	protected void setColumns() {
		for (AdjustmentColumn col : getDisplayColumns()) {
			switch (col) {
			case ADDED_DATE:
				addDateTimeColumn(col, new ValueGetter<Date, Adjustment>() {
					@Override
					public Date get(Adjustment item) {
						return item.getAddedDate();
					}
				});
				break;
			case AMOUNT:
				addCurrencyColumn(col, new ValueGetter<Double, Adjustment>() {
					@Override
					public Double get(Adjustment item) {
						return item.getAmount();
					}
				});
				break;
			case DESCRIPTION:
				addTextColumn(col, new ValueGetter<String, Adjustment>() {
					@Override
					public String get(Adjustment item) {
						return item.getDescription();
					}
				});
				break;
			case TOTALED_AMOUNT:
				addTotaledCurrencyColumn("Amount", new ValueGetter<Number, Adjustment>() {
					@Override
					public Number get(Adjustment item) {
						return item.getAmount();
					}
				});
				break;
			case STATUS:
				addTextColumn(col, new ValueGetter<String, Adjustment>() {
					@Override
					public String get(Adjustment item) {
						return item.getStatus();
					}
				});
				break;
			case TYPE:
				if (dialog != null) {
					addCompositeWidgetColumn(col, new WidgetCellCreator<Adjustment>() {
						@Override
						protected Widget createWidget(final Adjustment item) {
							if (item.getAdjustmentTypeId() != 1 || item.getStatusId() != 1) {
								return new Label(item.getAdjustmentType());
							}
							return new ClickLabel(item.getAdjustmentType(), new MouseDownHandler() {
								@Override
								public void onMouseDown(MouseDownEvent event) {
									dialog.center(item);
								}
							});
						}
					});
				} else {
					addTextColumn(col, new ValueGetter<String, Adjustment>() {
						@Override
						public String get(Adjustment item) {
							return item.getAdjustmentType();
						}
					});
				}
				break;
			case USER:
				break;
			default:
				new AssertionError();
				break;
			}
		}

		if (dialog != null) {
			addCompositeWidgetColumn("", new WidgetCellCreator<Adjustment>() {
				@Override
				protected Widget createWidget(final Adjustment item) {
					if (item.getAdjustmentTypeId() != 1 || item.getStatusId() != 1) {
						return new Label("");
					}

					return new ClickLabel("X", new MouseDownHandler() {
						@Override
						public void onMouseDown(MouseDownEvent event) {
							ConfirmDialog.confirm("Really delete this adjustment?", new ConfirmHandler() {
								@Override
								public void onConfirm() {
									paymentService.deleteAdjustment(item.getId(), new Callback<Void>() {
										@Override
										protected void doOnSuccess(Void result) {
											removeItem(item);
										}
									});
								}
							});
						}
					});
				}
			});
		}

	}

}
