package com.areahomeschoolers.baconbits.client.widgets;

import java.util.HashMap;
import java.util.Map;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.dom.client.TableRowElement;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

/**
 * Composite class containing a FlexTable to which name/value pairs are added for the consistent display of data. Things handled by this class: consistent
 * placement of field label panels, including non-staggered placement of required asterisk; consistent column width and cell spacing; easy-add of rows to the
 * underlying FlexTable (no need to specify row and cell offsets).
 */
public class FieldTable extends Composite {
	public enum LabelColumnWidth {
		DEFAULT, NARROW, WIDE, NONE
	}

	static HorizontalPanel createFieldLabelPanel(Widget fieldLabel, Label requiredLabel) {
		HorizontalPanel hp = new HorizontalPanel();
		hp.add(requiredLabel);
		hp.add(fieldLabel);
		hp.setCellWidth(requiredLabel, "10px");
		hp.setCellVerticalAlignment(requiredLabel, HasVerticalAlignment.ALIGN_TOP);
		hp.setCellVerticalAlignment(fieldLabel, HasVerticalAlignment.ALIGN_MIDDLE);
		hp.addStyleName("FormField");
		return hp;
	}

	private FlexTable flexTable = new FlexTable();
	private final Map<FormField, TableRowElement> parentRows = new HashMap<FormField, TableRowElement>();

	public FieldTable() {
		flexTable.setStyleName("sectionContent");
		setLabelColumnWidth(LabelColumnWidth.DEFAULT);
		flexTable.setCellPadding(0);
		flexTable.setCellSpacing(0);
		initWidget(flexTable);
		setWidth("100%");
	}

	public void addAll(Form form) {
		for (FormField field : form.getAllFormFields()) {
			addField(field);
		}
	}

	public void addField(FormField field) {
		addField(field.getLabelPanel(), field);

		int newRowNumber = flexTable.getRowCount() - 1;
		Element element = flexTable.getRowFormatter().getElement(newRowNumber);
		parentRows.put(field, (TableRowElement) element);
		field.setParentFieldTable(this);
	}

	public void addField(FormField field, int insertBefore) {
		flexTable.insertRow(insertBefore);
		addField(field.getLabelPanel(), field, insertBefore);

		Element element = flexTable.getRowFormatter().getElement(insertBefore);
		parentRows.put(field, (TableRowElement) element);
		field.setParentFieldTable(this);
	}

	public void addField(Label label, String value) {
		addField(label, new Label(value));
	}

	public void addField(Label label, String value, boolean createLabelPanel) {
		if (!createLabelPanel) {
			addField(label, new Label(value));
		} else {
			addField(createFieldLabelPanel(label, new Label()), new Label(value));
		}
	}

	public void addField(String name, String value) {
		addField(name, new Label(value));
	}

	public void addField(String name, Widget value) {
		addField(createFieldLabelPanel(new Label(name), new Label()), value);
	}

	public void addLinkPanel(LinkPanel linkPanel) {
		flexTable.insertRow(0);
		flexTable.setWidget(0, 0, linkPanel);
		flexTable.getFlexCellFormatter().setColSpan(0, 0, 2);
	}

	public void addRowStyle(FormField field, String style) {
		TableRowElement element = parentRows.get(field);
		if (element != null && element.getRowIndex() >= 0) {
			flexTable.getRowFormatter().addStyleName(element.getRowIndex(), style);
		}
	}

	public void addSpanningWidget(Widget widget) {
		int row = flexTable.getRowCount();
		flexTable.setWidget(row, 0, widget);
		flexTable.getFlexCellFormatter().setColSpan(row, 0, 2);
	}

	public void clear() {
		flexTable.removeAllRows();
	}

	/**
	 * Converts an HTMLTable to a string (with newlines and without HTML)
	 * 
	 * @param table
	 * @return
	 */
	public String extractText() {
		StringBuffer out = new StringBuffer();
		for (int i = 0; i < flexTable.getRowCount(); i++) {
			for (int j = 0; j < flexTable.getCellCount(i); j++) {
				out.append(flexTable.getText(i, j) + " ");
			}
			out.append("\n");
		}
		return out.toString();
	}

	public FlexTable getFlexTable() {
		return flexTable;
	}

	public void hideField(FormField field) {
		field.setEnabled(false);
		if (parentRows.get(field) != null) {
			flexTable.getRowFormatter().setVisible(parentRows.get(field).getRowIndex(), false);
		}
	}

	public void removeAllRows() {
		if (flexTable.getRowCount() > 0) {
			flexTable.removeAllRows();
		}
	}

	public void removeRowStyle(FormField field, String style) {
		TableRowElement element = parentRows.get(field);
		if (element != null && element.getRowIndex() >= 0) {
			flexTable.getRowFormatter().removeStyleName(element.getRowIndex(), style);
		}
	}

	public void setFieldVisibility(FormField field, boolean visible) {
		if (visible) {
			showField(field);
		} else {
			hideField(field);
		}
	}

	public void setLabelColumnWidth(LabelColumnWidth width) {
		String style = "";
		switch (width) {
		case DEFAULT:
			style = "defaultLabelColumn";
			break;
		case NARROW:
			style = "narrowLabelColumn";
			break;
		case WIDE:
			style = "wideLabelColumn";
			break;
		case NONE:
			style = "noWidthLabelColumn";
			break;
		}

		flexTable.getColumnFormatter().setStyleName(0, style);
	}

	public void setText(String text) {
		flexTable.removeAllRows();
		flexTable.setText(0, 0, text);
	}

	public void showField(FormField field) {
		field.setEnabled(true);
		if (parentRows.get(field) != null) {
			flexTable.getRowFormatter().setVisible(parentRows.get(field).getRowIndex(), true);
		}
	}

	private void addField(Widget label, Widget content) {
		addField(label, content, flexTable.getRowCount());
	}

	private void addField(Widget label, Widget content, int rowNumber) {
		label.getElement().getStyle().setMarginRight(5, Unit.PX);
		flexTable.getCellFormatter().setVerticalAlignment(rowNumber, 0, HasVerticalAlignment.ALIGN_TOP);
		flexTable.setWidget(rowNumber, 0, label);
		flexTable.setWidget(rowNumber, 1, content);
	}
}
