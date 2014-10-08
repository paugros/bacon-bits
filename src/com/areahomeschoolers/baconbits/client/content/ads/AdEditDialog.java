package com.areahomeschoolers.baconbits.client.content.ads;

import com.areahomeschoolers.baconbits.client.Application;
import com.areahomeschoolers.baconbits.client.ServiceCache;
import com.areahomeschoolers.baconbits.client.event.FormSubmitHandler;
import com.areahomeschoolers.baconbits.client.rpc.Callback;
import com.areahomeschoolers.baconbits.client.rpc.service.ArticleService;
import com.areahomeschoolers.baconbits.client.rpc.service.ArticleServiceAsync;
import com.areahomeschoolers.baconbits.client.util.Formatter;
import com.areahomeschoolers.baconbits.client.widgets.EntityEditDialog;
import com.areahomeschoolers.baconbits.client.widgets.FieldTable;
import com.areahomeschoolers.baconbits.client.widgets.FieldTable.LabelColumnWidth;
import com.areahomeschoolers.baconbits.client.widgets.FormField;
import com.areahomeschoolers.baconbits.client.widgets.MaxLengthTextArea;
import com.areahomeschoolers.baconbits.client.widgets.RequiredTextBox;
import com.areahomeschoolers.baconbits.client.widgets.ValidatorDateBox;
import com.areahomeschoolers.baconbits.shared.dto.Ad;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class AdEditDialog extends EntityEditDialog<Ad> {
	private ArticleServiceAsync articleService = (ArticleServiceAsync) ServiceCache.getService(ArticleService.class);

	public AdEditDialog(Ad ad, final Command refreshCommand) {
		if (ad.isSaved()) {
			setText("Edit Ad");
		} else {
			setText("Add Ad");
		}
		setEntity(ad);

		addFormSubmitHandler(new FormSubmitHandler() {
			@Override
			public void onFormSubmit(FormField formField) {
				if (!entity.isSaved()) {
					entity.setAddedById(Application.getCurrentUserId());
					entity.setOwningOrgId(Application.getCurrentOrgId());
				}

				articleService.saveAd(entity, new Callback<Ad>() {
					@Override
					protected void doOnSuccess(Ad result) {
						refreshCommand.execute();
					}
				});
			}
		});
	}

	@Override
	protected Widget createContent() {
		FieldTable ft = new FieldTable();
		ft.setWidth("500px");
		ft.setLabelColumnWidth(LabelColumnWidth.NARROW);

		final RequiredTextBox titleInput = new RequiredTextBox();
		titleInput.setMaxLength(50);
		FormField titleField = form.createFormField("Title:", titleInput, null);
		titleField.setInitializer(new Command() {
			@Override
			public void execute() {
				titleInput.setText(entity.getTitle());
			}
		});
		titleField.setDtoUpdater(new Command() {
			@Override
			public void execute() {
				entity.setTitle(titleInput.getText().trim());
			}
		});
		ft.addField(titleField);

		final Label descriptionDisplay = new Label();
		final MaxLengthTextArea descriptionInput = new MaxLengthTextArea(500);
		FormField descriptionField = form.createFormField("Description:", descriptionInput, descriptionDisplay);
		descriptionField.setInitializer(new Command() {
			@Override
			public void execute() {
				descriptionDisplay.setText(entity.getDescription());
				descriptionInput.setText(entity.getDescription());
			}
		});
		descriptionField.setDtoUpdater(new Command() {
			@Override
			public void execute() {
				entity.setDescription(descriptionInput.getText());
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
				urlDisplay.setText(entity.getUrl());
				urlInput.setText(entity.getUrl());
			}
		});
		urlField.setDtoUpdater(new Command() {
			@Override
			public void execute() {
				entity.setUrl(urlInput.getText());
			}
		});
		ft.addField(urlField);

		final Label startDateDisplay = new Label();
		final ValidatorDateBox startDateInput = new ValidatorDateBox();
		FormField startDateField = form.createFormField("Start date:", startDateInput, startDateDisplay);
		startDateField.setInitializer(new Command() {
			@Override
			public void execute() {
				startDateDisplay.setText(Formatter.formatDate(entity.getStartDate()));
				startDateInput.setValue(entity.getStartDate());
			}
		});
		startDateField.setDtoUpdater(new Command() {
			@Override
			public void execute() {
				entity.setStartDate(startDateInput.getValue());
			}
		});
		ft.addField(startDateField);

		final Label endDateDisplay = new Label();
		final ValidatorDateBox endDateInput = new ValidatorDateBox();
		FormField endDateField = form.createFormField("End date:", endDateInput, endDateDisplay);
		endDateField.setInitializer(new Command() {
			@Override
			public void execute() {
				endDateDisplay.setText(Formatter.formatDate(entity.getEndDate()));
				endDateInput.setValue(entity.getEndDate());
			}
		});
		endDateField.setDtoUpdater(new Command() {
			@Override
			public void execute() {
				entity.setEndDate(endDateInput.getValue());
			}
		});
		ft.addField(endDateField);

		return ft;
	}

}
