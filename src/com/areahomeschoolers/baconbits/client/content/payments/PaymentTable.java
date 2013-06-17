package com.areahomeschoolers.baconbits.client.content.payments;

import java.util.Date;

import com.areahomeschoolers.baconbits.client.ServiceCache;
import com.areahomeschoolers.baconbits.client.content.payments.PaymentTable.PaymentColumn;
import com.areahomeschoolers.baconbits.client.rpc.service.PaymentService;
import com.areahomeschoolers.baconbits.client.rpc.service.PaymentServiceAsync;
import com.areahomeschoolers.baconbits.client.widgets.cellview.EntityCellTable;
import com.areahomeschoolers.baconbits.client.widgets.cellview.EntityCellTableColumn;
import com.areahomeschoolers.baconbits.client.widgets.cellview.ValueGetter;
import com.areahomeschoolers.baconbits.shared.dto.Arg.PaymentArg;
import com.areahomeschoolers.baconbits.shared.dto.ArgMap;
import com.areahomeschoolers.baconbits.shared.dto.Payment;

public final class PaymentTable extends EntityCellTable<Payment, PaymentArg, PaymentColumn> {
	public enum PaymentColumn implements EntityCellTableColumn<PaymentColumn> {
		TYPE("Payment type"), USER("User"), STATUS("Status"), PAYMENT_DATE("Added date"), AMOUNT("Amount"), TOTALED_AMOUNT("Amount");

		private String title;

		PaymentColumn(String title) {
			this.title = title;
		}

		@Override
		public String getTitle() {
			return title;
		}
	}

	private PaymentServiceAsync paymentService = (PaymentServiceAsync) ServiceCache.getService(PaymentService.class);

	public PaymentTable(ArgMap<PaymentArg> args) {
		this();
		setArgMap(args);
	}

	private PaymentTable() {
		setDefaultSortColumn(PaymentColumn.PAYMENT_DATE, SortDirection.SORT_DESC);
		setDisplayColumns(PaymentColumn.PAYMENT_DATE, PaymentColumn.TOTALED_AMOUNT, PaymentColumn.STATUS, PaymentColumn.TYPE, PaymentColumn.USER);

		disablePaging();
	}

	@Override
	protected void fetchData() {
		paymentService.list(getArgMap(), getCallback());
	}

	@Override
	protected void setColumns() {
		for (PaymentColumn col : getDisplayColumns()) {
			switch (col) {
			case PAYMENT_DATE:
				addDateTimeColumn(col, new ValueGetter<Date, Payment>() {
					@Override
					public Date get(Payment item) {
						return item.getPaymentDate();
					}
				});
				break;
			case AMOUNT:
				addCurrencyColumn(col, new ValueGetter<Double, Payment>() {
					@Override
					public Double get(Payment item) {
						return item.getAmount();
					}
				});
				break;
			case TOTALED_AMOUNT:
				addTotaledCurrencyColumn("Amount", new ValueGetter<Number, Payment>() {
					@Override
					public Number get(Payment item) {
						return item.getAmount();
					}
				});
				break;
			case STATUS:
				addTextColumn(col, new ValueGetter<String, Payment>() {
					@Override
					public String get(Payment item) {
						return item.getStatus();
					}
				});
				break;
			case TYPE:
				addTextColumn(col, new ValueGetter<String, Payment>() {
					@Override
					public String get(Payment item) {
						return item.getPaymentType();
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
