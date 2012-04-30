package com.areahomeschoolers.baconbits.client.widgets.cellview;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;

import com.areahomeschoolers.baconbits.client.rpc.Callback;
import com.areahomeschoolers.baconbits.shared.dto.Data;

import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

/**
 * <Table border="1">
 * <tr>
 * <th>Data Type</th>
 * <th>Column-prefix(s)</th>
 * <th>EntityCellTable Add Method</th>
 * </tr>
 * <tr>
 * <td>Text</td>
 * <td>Optional additional COLOR_ column</td>
 * <td>addTextColumn</td>
 * </tr>
 * <tr>
 * <td>Date</td>
 * <td>DATE_</td>
 * <td>addDateColumn</td>
 * </tr>
 * <tr>
 * <td>DateTime</td>
 * <td>DATETIME_</td>
 * <td>addDateTimeColumn</td>
 * </tr>
 * <tr>
 * <td>Integer</td>
 * <td>INTEGER_</td>
 * <td>addNumberColumn</td>
 * </tr>
 * <tr>
 * <td>Long</td>
 * <td>LONG_</td>
 * <td>addNumberColumn</td>
 * </tr>
 * <tr>
 * <td>Double</td>
 * <td>DOUBLE_</td>
 * <td>addNumberColumn</td>
 * </tr>
 * <tr>
 * <td>Currency</td>
 * <td>CURRENCY_</td>
 * <td>addCurrencyColumn</td>
 * </tr>
 * <tr>
 * <td>HyperLink</td>
 * <td>URL_ and URLTEXT_</td>
 * <td>addWidgetColumn</td>
 * </tr>
 * </table>
 * To use this class implement fetchData with the proper pipeline method.<br>
 * the table will be constructed according to the Column names returned from the SQL query
 * <p>
 * To define a column You need to define a column index, optionally a column type, and a column name<br>
 * To define a column index you need to prepend '#Integer-' to it.<br>
 * To define a column type, add 'TYPE_' after the index, type is not needed if you're only retrieving/displaying a string<br>
 * To define a column name, add your columne name after the type.
 * <p>
 * 
 * To build a URL column, you'll need to select 2 columns.<br>
 * The URL_ column represents the GWT based URL, ex. 'page=Home'<br>
 * the URLTEXT_ column is the text that the link will display;<br>
 * You may apply the column index to either part.<br>
 * 
 * <pre>
 * select top 1 
 * 	o.ID as [URLTEXT_Order], 
 * 	'page=Order&orderId=' + cast(o.ID as varchar) as [#1-URL_Order], 
 * 	a.Account as [#2-URLTEXT_Account],
 * 	'page=Account&accountID=' + cast(a.ID as varchar) as [URL_Account]
 * from Orders o
 * left join Accounts a on a.ID = o.AccountID
 * </pre>
 * 
 * <b>The way we create the URL_ component here is not preferable due to the ability for the URLs to change, instead we should use PageUrl after we retrive the
 * data from the database.</b>
 * <p>
 * <b>See OrderDaoImpl.getAssetOrderReportData(GenericEntity args) for a working example.</b>
 */
public abstract class SqlCellTable extends GenericCellTable {
	private HashSet<String> nonTextTypes = new HashSet<String>();
	{
		nonTextTypes.add("DATE_");
		nonTextTypes.add("DATETIME_");
		nonTextTypes.add("URL_");
		nonTextTypes.add("URLTEXT_");
		nonTextTypes.add("COLOR_");
		nonTextTypes.add("INTEGER_");
		nonTextTypes.add("LONG_");
		nonTextTypes.add("DOUBLE_");
		nonTextTypes.add("CURRENCY_");
	}

	private NumberFormat f = NumberFormat.getFormat("000");
	private NumberFormat reverseF = NumberFormat.getFormat("0");

	@Override
	protected void fetchData() {
		query(new Callback<ArrayList<Data>>() {
			@Override
			protected void doOnSuccess(ArrayList<Data> result) {
				visibleItems.addAll(result);
				setupColumns();
				getCallback().onSuccess(result);
			}
		});
	}

	protected abstract void query(Callback<ArrayList<Data>> callback);

	@Override
	protected void setColumns() {
		if (visibleItems.isEmpty()) {
			return;
		}

		final ArrayList<String> columns = new ArrayList<String>();
		columns.addAll(getList().get(0).getData().keySet());
		columns.addAll(getList().get(0).getDateData().keySet());
		for (String s : columns) {
			if (s.startsWith("#")) {
				int i = columns.indexOf(s);
				int desiredColumnIndex = Integer.parseInt(s.substring(1, s.indexOf('-')));
				columns.set(i, "#" + f.format(desiredColumnIndex) + "-" + s.substring(s.indexOf('-') + 1));
			}
		}
		Collections.sort(columns);
		for (String s : columns) {
			if (s.startsWith("#")) {
				int i = columns.indexOf(s);
				int desiredColumnIndex = Integer.parseInt(s.substring(1, s.indexOf('-')));
				columns.set(i, "#" + reverseF.format(desiredColumnIndex) + "-" + s.substring(s.indexOf('-') + 1));
			}
		}
		for (final String s : columns) {
			String unOrdered = s;
			if (s.startsWith("#")) {
				int i = columns.indexOf(s);
				columns.set(i, s.substring(s.indexOf('-') + 1));
				unOrdered = columns.get(i);
			}
			final String name = unOrdered.substring(unOrdered.indexOf('_') + 1);
			int ux = unOrdered.indexOf('_') + 1;
			String type = "";
			if (ux > -1) {
				type = unOrdered.substring(0, ux);
			}
			boolean asUrl = (unOrdered.startsWith("URL_") || unOrdered.startsWith("URLTEXT_")) && columns.contains("URLTEXT_" + name);
			// Add any new column types to nonTextTypes
			if (!nonTextTypes.contains(type)) {
				if (columns.contains("COLOR_" + name)) {
					addWidgetColumn(name, new WidgetCellCreator<Data>() {
						@Override
						protected Widget createWidget(Data item) {
							Label text = new Label(item.get(s));
							text.getElement().getStyle().setColor(item.get("COLOR_" + name));
							return text;
						}
					}, new ValueGetter<String, Data>() {
						@Override
						public String get(Data item) {
							return item.get(s);
						}
					});
				} else {
					addTextColumn(name, new ValueGetter<String, Data>() {
						@Override
						public String get(Data item) {
							return item.get(s);
						}
					});
				}
			} else if (unOrdered.startsWith("DATETIME_")) {
				addDateTimeColumn(name, new ValueGetter<Date, Data>() {
					@Override
					public Date get(Data item) {
						return item.getDate(s);
					}
				});
			} else if (unOrdered.startsWith("DATE_")) {
				addDateColumn(name, new ValueGetter<Date, Data>() {
					@Override
					public Date get(Data item) {
						return item.getDate(s);
					}
				});
			} else if (unOrdered.startsWith("CURRENCY_")) {
				addCurrencyColumn(name, new ValueGetter<Double, Data>() {
					@Override
					public Double get(Data item) {
						return item.getDouble(s);
					}
				});
			} else if (unOrdered.startsWith("INTEGER_")) {
				addNumberColumn(name, new ValueGetter<Number, Data>() {
					@Override
					public Number get(Data item) {
						return item.getInt(s);
					}
				});
			} else if (unOrdered.startsWith("DOUBLE_")) {
				addNumberColumn(name, new ValueGetter<Number, Data>() {
					@Override
					public Number get(Data item) {
						return item.getDouble(s);
					}
				});
			} else if (unOrdered.startsWith("LONG_")) {
				addNumberColumn(name, new ValueGetter<Number, Data>() {
					@Override
					public Number get(Data item) {
						return item.getLong(s);
					}
				});
			} else if (asUrl) {
				addWidgetColumn(name, new WidgetCellCreator<Data>() {
					@Override
					protected Widget createWidget(Data item) {
						String linkText = "";
						String url = "";
						for (String key : item.getData().keySet()) {
							if (key.contains("URLTEXT_") && key.contains(name)) {
								linkText = item.get(key);
							}
							if (key.contains("URL_") && key.contains(name)) {
								url = item.get(key);
							}
							if (!url.isEmpty() && !linkText.isEmpty()) {
								break;
							}
						}
						return new Hyperlink(linkText, url);
					}
				}, new ValueGetter<String, Data>() {
					@Override
					public String get(Data item) {
						String linkText = "";
						for (String key : item.getData().keySet()) {
							if (key.contains("URLTEXT_") && key.contains(name)) {
								linkText = item.get(key);
								break;
							}
						}
						return linkText;
					}

				});
			}
		}
	}
}
