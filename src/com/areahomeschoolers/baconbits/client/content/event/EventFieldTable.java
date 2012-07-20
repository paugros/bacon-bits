package com.areahomeschoolers.baconbits.client.content.event;

import java.util.List;

import com.areahomeschoolers.baconbits.client.event.FormSubmitHandler;
import com.areahomeschoolers.baconbits.client.widgets.EventFormField;
import com.areahomeschoolers.baconbits.client.widgets.FieldTable;
import com.areahomeschoolers.baconbits.client.widgets.Form;
import com.areahomeschoolers.baconbits.client.widgets.FormField;
import com.areahomeschoolers.baconbits.shared.dto.EventField;

public class EventFieldTable extends FieldTable {
	private List<EventField> fields;
	private Form form = new Form(new FormSubmitHandler() {
		@Override
		public void onFormSubmit(FormField formField) {

		}
	});

	public EventFieldTable(List<EventField> f) {
		super();
		fields = f;

		for (EventField field : fields) {
			EventFormField ff = new EventFormField(field);
			form.addField(ff.getFormField());
			addField(ff.getFormField());
		}

		form.initialize();
	}

	public Form getForm() {
		return form;
	}

}
