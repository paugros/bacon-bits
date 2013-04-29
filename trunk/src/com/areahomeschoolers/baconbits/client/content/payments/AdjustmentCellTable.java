package com.areahomeschoolers.baconbits.client.content.payments;

import java.util.Date;

import com.areahomeschoolers.baconbits.client.ServiceCache;
import com.areahomeschoolers.baconbits.client.content.payments.AdjustmentCellTable.AdjustmentColumn;
import com.areahomeschoolers.baconbits.client.rpc.service.PaymentService;
import com.areahomeschoolers.baconbits.client.rpc.service.PaymentServiceAsync;
import com.areahomeschoolers.baconbits.client.widgets.cellview.EntityCellTable;
import com.areahomeschoolers.baconbits.client.widgets.cellview.EntityCellTableColumn;
import com.areahomeschoolers.baconbits.client.widgets.cellview.ValueGetter;
import com.areahomeschoolers.baconbits.shared.dto.Adjustment;
import com.areahomeschoolers.baconbits.shared.dto.Arg.PaymentArg;
import com.areahomeschoolers.baconbits.shared.dto.ArgMap;

public final class AdjustmentCellTable extends EntityCellTable<Adjustment, PaymentArg, AdjustmentColumn> {
	public enum AdjustmentColumn implements EntityCellTableColumn<AdjustmentColumn> {
		TYPE("Adjustment type"), USER("User"), AMOUNT("Amount"), TOTALED_AMOUNT("Amount"), STATUS("Status"), ADDED_DATE("Added date");

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

	public AdjustmentCellTable(ArgMap<PaymentArg> args) {
		this();
		setArgMap(args);
	}

	private AdjustmentCellTable() {
		setDefaultSortColumn(AdjustmentColumn.TYPE, SortDirection.SORT_ASC);
		setDisplayColumns(AdjustmentColumn.ADDED_DATE, AdjustmentColumn.AMOUNT, AdjustmentColumn.STATUS, AdjustmentColumn.TYPE, AdjustmentColumn.USER);

		disablePaging();
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
				addTextColumn(col, new ValueGetter<String, Adjustment>() {
					@Override
					public String get(Adjustment item) {
						return item.getAdjustmentType();
					}
				});
				break;
			case USER:
				break;
			default:
				new AssertionError();
				break;
			}
		}
	}

}
