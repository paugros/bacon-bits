package com.areahomeschoolers.baconbits.client.content.resource;

import com.areahomeschoolers.baconbits.client.Application;
import com.areahomeschoolers.baconbits.client.HistoryToken;
import com.areahomeschoolers.baconbits.client.ServiceCache;
import com.areahomeschoolers.baconbits.client.content.system.ErrorPage;
import com.areahomeschoolers.baconbits.client.content.system.ErrorPage.PageError;
import com.areahomeschoolers.baconbits.client.content.tag.TagSection;
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
import com.areahomeschoolers.baconbits.client.widgets.AddressField;
import com.areahomeschoolers.baconbits.client.widgets.DefaultListBox;
import com.areahomeschoolers.baconbits.client.widgets.DefaultTextArea;
import com.areahomeschoolers.baconbits.client.widgets.EditableImage;
import com.areahomeschoolers.baconbits.client.widgets.FieldTable;
import com.areahomeschoolers.baconbits.client.widgets.Form;
import com.areahomeschoolers.baconbits.client.widgets.FormField;
import com.areahomeschoolers.baconbits.client.widgets.PhoneTextBox;
import com.areahomeschoolers.baconbits.client.widgets.RequiredTextBox;
import com.areahomeschoolers.baconbits.client.widgets.ValidatorDateBox;
import com.areahomeschoolers.baconbits.shared.Common;
import com.areahomeschoolers.baconbits.shared.dto.Document.DocumentLinkType;
import com.areahomeschoolers.baconbits.shared.dto.Resource;
import com.areahomeschoolers.baconbits.shared.dto.ResourcePageData;
import com.areahomeschoolers.baconbits.shared.dto.Tag.TagMappingType;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

public class ResourcePage implements Page {
	private Form form = new Form(new FormSubmitHandler() {
		@Override
		public void onFormSubmit(final FormField formField) {
			AddressField.validateAddress(resource, new Command() {
				@Override
				public void execute() {
					save(formField);
				}
			});
		}
	});
	private VerticalPanel page;
	private FieldTable ft = new FieldTable();
	private Resource resource = new Resource();
	private ResourceServiceAsync resourceService = (ResourceServiceAsync) ServiceCache.getService(ResourceService.class);

	public ResourcePage(VerticalPanel page) {
		int resourceId = Url.getIntegerParameter("resourceId");

		if (!Application.isAuthenticated() && resourceId < 0) {
			new ErrorPage(PageError.NOT_AUTHORIZED);
			return;
		}

		this.page = page;

		if (resourceId > 0) {
			resourceService.getPageData(resourceId, new Callback<ResourcePageData>() {
				@Override
				protected void doOnSuccess(ResourcePageData result) {
					if (result == null) {
						new ErrorPage(PageError.PAGE_NOT_FOUND);
						return;
					}
					resource = result.getResource();
					initializePage();
				}
			});
		} else {
			initializePage();
		}
	}

	private boolean allowEdit() {
		return Application.isAuthenticated();
	}

	private void createFieldTable() {
		ft.setWidth("100%");

		final RequiredTextBox titleInput = new RequiredTextBox();
		final Label titleDisplay = new Label();
		titleInput.setVisibleLength(35);
		titleInput.setMaxLength(50);
		FormField titleField = form.createFormField("Name:", titleInput, titleDisplay);
		titleField.setInitializer(new Command() {
			@Override
			public void execute() {
				titleDisplay.setText(resource.getName());
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
		final DefaultTextArea descriptionInput = new DefaultTextArea();
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

		final Anchor urlDisplay = new Anchor();
		final RequiredTextBox urlInput = new RequiredTextBox();
		urlInput.setMaxLength(500);
		urlInput.setVisibleLength(50);
		FormField urlField = form.createFormField("URL:", urlInput, urlDisplay);
		urlField.setInitializer(new Command() {
			@Override
			public void execute() {
				urlDisplay.setText(resource.getUrl());
				urlDisplay.setHref(resource.getUrl());
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

		final Label adDisplay = new Label();
		final DefaultListBox adInput = new DefaultListBox();
		adInput.addItem("Yes");
		adInput.addItem("No");
		FormField adField = form.createFormField("Show in ads:", adInput, adDisplay);
		adField.setInitializer(new Command() {
			@Override
			public void execute() {
				adDisplay.setText(resource.getShowInAds() ? "Yes" : "No");
				adInput.setValue(resource.getShowInAds() ? "Yes" : "No");
			}
		});
		adField.setDtoUpdater(new Command() {
			@Override
			public void execute() {
				resource.setShowInAds(adInput.getValue().equals("Yes"));
			}
		});
		ft.addField(adField);

		if (resource.isSaved() && resource.getShowInAds()) {
			String clicks = "None";
			if (resource.getClickCount() > 0) {
				clicks = resource.getClickCount() + " (last " + Formatter.formatDateTime(resource.getLastClickDate()) + ")";
			}
			ft.addField("Clicks:", clicks);
		}

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
				endDateDisplay.setText(Common.getDefaultIfNull(Formatter.formatDate(resource.getEndDate())));
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

		final Label phoneDisplay = new Label();
		final PhoneTextBox phoneInput = new PhoneTextBox();
		FormField phoneField = form.createFormField("Phone:", phoneInput, phoneDisplay);
		phoneField.setInitializer(new Command() {
			@Override
			public void execute() {
				phoneDisplay.setText(Common.getDefaultIfNull(resource.getPhone()));
				phoneInput.setText(resource.getPhone());
			}
		});
		phoneField.setDtoUpdater(new Command() {
			@Override
			public void execute() {
				resource.setPhone(phoneInput.getText());
			}
		});
		ft.addField(phoneField);

		FormField addressField = new AddressField(resource).getFormField();
		form.addField(addressField);
		ft.addField(addressField);

		if (resource.isSaved() && (resource.hasTags() || allowEdit())) {
			TagSection ts = new TagSection(TagMappingType.ARTICLE, resource.getId());
			ts.setEditingEnabled(allowEdit());
			ft.addField("Tags:", ts);
			ts.populate();
		}

		Button cancelButton = new Button("Cancel", new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				History.back();
			}
		});

		form.getButtonPanel().insertCenterButton(cancelButton, 0);
	}

	private void initializePage() {
		String title = resource.isSaved() ? resource.getName() : "New Resource";

		createFieldTable();
		form.initialize();

		if (!resource.isSaved()) {
			form.configureForAdd(ft);
		} else {
			form.emancipate();
		}

		HorizontalPanel pp = new HorizontalPanel();
		pp.setWidth("100%");
		if (resource.isSaved()) {
			EditableImage image = new EditableImage(DocumentLinkType.RESOURCE, resource.getId(), resource.getDocumentId(), true);
			image.getElement().getStyle().setMarginRight(10, Unit.PX);
			pp.add(image);

			image.addStyleName("profilePic");
			ft.removeStyleName("sectionContent");
			pp.addStyleName("sectionContent");
			pp.add(image);
		}

		pp.add(ft);

		page.add(WidgetFactory.newSection(title, pp, ContentWidth.MAXWIDTH1000PX));

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