package com.areahomeschoolers.baconbits.client.widgets.cellview;

import java.util.ArrayList;
import java.util.List;

import com.areahomeschoolers.baconbits.shared.dto.EntityDto;

import com.google.gwt.cell.client.CompositeCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.HasCell;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.TableRowElement;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Widget;

public abstract class CompositeCellWidgetCreator<T extends EntityDto<T>> {

	private List<WidgetCellCreator<T>> cellCreators;

	public CompositeCellWidgetCreator() {
		this(true);
	}

	public CompositeCellWidgetCreator(boolean addSpaces) {
		cellCreators = new ArrayList<WidgetCellCreator<T>>();
		List<WidgetCellCreator<T>> addedCreators = createWidgetCreators();

		// Add spaces between creators
		int count = 0;
		for (WidgetCellCreator<T> creator : addedCreators) {
			cellCreators.add(creator);
			count++;

			if (addSpaces && count < addedCreators.size()) {
				cellCreators.add(new WidgetCellCreator<T>() {
					@Override
					protected Widget createWidget(T item) {
						return new InlineLabel(" ");
					}
				});
			}
		}
	}

	public Column<T, T> createColumn(final EntityCellTable<T, ?, ?> table) {
		List<HasCell<T, ?>> cells = new ArrayList<HasCell<T, ?>>();

		final int primaryColumnIndex = table.getColumnCount();
		for (final WidgetCellCreator<T> widgetCreator : cellCreators) {
			final Column<T, T> col = new Column<T, T>(widgetCreator.createWidgetCell()) {
				@Override
				public T getValue(T item) {
					return item;
				}
			};

			col.setSortable(true);

			final int colIndex = cells.size();
			col.setFieldUpdater(new FieldUpdater<T, T>() {
				@Override
				public void update(int index, T object, T value) {
					index = table.indexOf(object);
					TableRowElement tre = table.getRowElement(index - table.getVisibleRange().getStart());
					Element e = tre.getCells().getItem(primaryColumnIndex).getChild(0).getChild(colIndex).cast();
					e.setInnerHTML(widgetCreator.get(object).toString());
				}
			});
			cells.add(col);
		}
		final Column<T, T> col = new Column<T, T>(new CompositeCell<T>(cells)) {
			@Override
			public T getValue(T item) {
				return item;
			}
		};

		return col;
	}

	/**
	 * This isn't being called at the moment because our Composite widets don't have field updaters.
	 * 
	 * @param item
	 * @return
	 */
	public String getHtml(T item) {
		String html = "";

		for (WidgetCellCreator<T> creator : cellCreators) {
			html += "<span>";
			html += creator.get(item);
			html += "</span>";
		}

		return html;
	}

	public List<WidgetCellCreator<T>> getWidgetCreators() {
		return cellCreators;
	}

	protected abstract List<WidgetCellCreator<T>> createWidgetCreators();
}
