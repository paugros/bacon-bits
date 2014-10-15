package com.areahomeschoolers.baconbits.client.content.resource;

import com.areahomeschoolers.baconbits.client.Application;
import com.areahomeschoolers.baconbits.client.HistoryToken;
import com.areahomeschoolers.baconbits.client.ServiceCache;
import com.areahomeschoolers.baconbits.client.content.Sidebar;
import com.areahomeschoolers.baconbits.client.content.Sidebar.MiniModule;
import com.areahomeschoolers.baconbits.client.content.system.ErrorPage;
import com.areahomeschoolers.baconbits.client.content.system.ErrorPage.PageError;
import com.areahomeschoolers.baconbits.client.event.FormSubmitHandler;
import com.areahomeschoolers.baconbits.client.generated.Page;
import com.areahomeschoolers.baconbits.client.rpc.Callback;
import com.areahomeschoolers.baconbits.client.rpc.service.ResourceService;
import com.areahomeschoolers.baconbits.client.rpc.service.ResourceServiceAsync;
import com.areahomeschoolers.baconbits.client.util.Formatter;
import com.areahomeschoolers.baconbits.client.util.PageUrl;
import com.areahomeschoolers.baconbits.client.util.Url;
import com.areahomeschoolers.baconbits.client.util.WidgetFactory;
import com.areahomeschoolers.baconbits.client.util.WidgetFactory.ContentWidth;
import com.areahomeschoolers.baconbits.client.widgets.FieldTable;
import com.areahomeschoolers.baconbits.client.widgets.Form;
import com.areahomeschoolers.baconbits.client.widgets.FormField;
import com.areahomeschoolers.baconbits.client.widgets.MaxLengthTextArea;
import com.areahomeschoolers.baconbits.client.widgets.RequiredTextBox;
import com.areahomeschoolers.baconbits.client.widgets.ValidatorDateBox;
import com.areahomeschoolers.baconbits.shared.dto.Resource;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

public class ResourcePage implements Page {
	private Form form = new Form(new FormSubmitHandler() {
		@Override
		public void onFormSubmit(FormField formField) {
			save(formField);
		}
	});
	private VerticalPanel page;
	private FieldTable ft = new FieldTable();
	private Resource resource = new Resource();
	private ResourceServiceAsync resourceService = (ResourceServiceAsync) ServiceCache.getService(ResourceService.class);
	private Sidebar sidebar = new Sidebar();

	public ResourcePage(VerticalPanel page) {
		int resourceId = Url.getIntegerParameter("resourceId");

		if (!Application.isAuthenticated() && resourceId < 0) {
			new ErrorPage(PageError.NOT_AUTHORIZED);
			return;
		}

		this.page = page;

		if (resourceId > 0) {
			resourceService.getById(resourceId, new Callback<Resource>() {
				@Override
				protected void doOnSuccess(Resource result) {
					if (result == null) {
						new ErrorPage(PageError.PAGE_NOT_FOUND);
						return;
					}
					resource = result;
					initializePage();
				}
			});
		} else {
			initializePage();
		}
	}

	private boolean allowEdit() {
		return true;
	}

	private void createFieldTable() {
		ft.setWidth("100%");

		final RequiredTextBox titleInput = new RequiredTextBox();
		titleInput.setMaxLength(50);
		FormField titleField = form.createFormField("Name:", titleInput, null);
		titleField.setInitializer(new Command() {
			@Override
			public void execute() {
				titleInput.setText(resource.getName());
			}
		});
		titleField.setDtoUpdater(new Command() {
			@Override
			public void execute() {
				resource.setName(titleInput.getText().trim());
			}
		});
		ft.addField(titleField);

		final Label descriptionDisplay = new Label();
		final MaxLengthTextArea descriptionInput = new MaxLengthTextArea(500);
		FormField descriptionField = form.createFormField("Description:", descriptionInput, descriptionDisplay);
		descriptionField.setInitializer(new Command() {
			@Override
			public void execute() {
				descriptionDisplay.setText(resource.getDescription());
				descriptionInput.setText(resource.getDescription());
			}
		});
		descriptionField.setDtoUpdater(new Command() {
			@Override
			public void execute() {
				resource.setDescription(descriptionInput.getText());
			}
		});
		ft.addField(descriptionField);

		final Label urlDisplay = new Label();
		final TextBox urlInput = new TextBox();
		urlInput.setMaxLength(500);
		urlInput.setVisibleLength(50);
		FormField urlField = form.createFormField("URL:", urlInput, urlDisplay);
		urlField.setRequired(true);
		urlField.setInitializer(new Command() {
			@Override
			public void execute() {
				urlDisplay.setText(resource.getUrl());
				urlInput.setText(resource.getUrl());
			}
		});
		urlField.setDtoUpdater(new Command() {
			@Override
			public void execute() {
				resource.setUrl(urlInput.getText());
			}
		});
		ft.addField(urlField);

		final Label startDateDisplay = new Label();
		final ValidatorDateBox startDateInput = new ValidatorDateBox();
		FormField startDateField = form.createFormField("Start date:", startDateInput, startDateDisplay);
		startDateField.setInitializer(new Command() {
			@Override
			public void execute() {
				startDateDisplay.setText(Formatter.formatDate(resource.getStartDate()));
				startDateInput.setValue(resource.getStartDate());
			}
		});
		startDateField.setDtoUpdater(new Command() {
			@Override
			public void execute() {
				resource.setStartDate(startDateInput.getValue());
			}
		});
		ft.addField(startDateField);

		final Label endDateDisplay = new Label();
		final ValidatorDateBox endDateInput = new ValidatorDateBox();
		FormField endDateField = form.createFormField("End date:", endDateInput, endDateDisplay);
		endDateField.setInitializer(new Command() {
			@Override
			public void execute() {
				endDateDisplay.setText(Formatter.formatDate(resource.getEndDate()));
				endDateInput.setValue(resource.getEndDate());
			}
		});
		endDateField.setDtoUpdater(new Command() {
			@Override
			public void execute() {
				resource.setEndDate(endDateInput.getValue());
			}
		});
		ft.addField(endDateField);

		Button cancelButton = new Button("Cancel", new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				History.back();
			}
		});

		form.getButtonPanel().insertCenterButton(cancelButton, 0);
	}

	private void initializePage() {
		sidebar.add(MiniModule.CITRUS, MiniModule.LINKS, MiniModule.MY_EVENTS, MiniModule.NEW_EVENTS, MiniModule.UPCOMING_EVENTS);

		String title = resource.isSaved() ? resource.getName() : "New Resource";

		createFieldTable();
		form.initialize();

		if (!resource.isSaved()) {
			form.configureForAdd(ft);
		} else {
			form.emancipate();
		}

		page.add(WidgetFactory.newSection(title, ft, ContentWidth.MAXWIDTH1000PX));

		form.setEnabled(allowEdit());

		Application.getLayout().setPage(title, page);
	}

	private void save(final FormField field) {
		resourceService.save(resource, new Callback<Resource>() {
			@Override
			protected void doOnSuccess(Resource a) {
				if (!Url.isParamValidId("resourceId")) {
					HistoryToken.set(PageUrl.resource(a.getId()));
				} else {
					resource = a;
					form.setDto(a);
					field.setInputVisibility(false);
				}
			}
		});
	}
}
