package com.areahomeschoolers.baconbits.client.content.resource;

import java.util.ArrayList;

import com.areahomeschoolers.baconbits.client.Application;
import com.areahomeschoolers.baconbits.client.HistoryToken;
import com.areahomeschoolers.baconbits.client.ServiceCache;
import com.areahomeschoolers.baconbits.client.content.minimodules.AdsMiniModule;
import com.areahomeschoolers.baconbits.client.content.system.ErrorPage;
import com.areahomeschoolers.baconbits.client.content.system.ErrorPage.PageError;
import com.areahomeschoolers.baconbits.client.content.tag.TagSection;
import com.areahomeschoolers.baconbits.client.event.FormSubmitHandler;
import com.areahomeschoolers.baconbits.client.generated.Page;
import com.areahomeschoolers.baconbits.client.images.MainImageBundle;
import com.areahomeschoolers.baconbits.client.rpc.Callback;
import com.areahomeschoolers.baconbits.client.rpc.service.ResourceService;
import com.areahomeschoolers.baconbits.client.rpc.service.ResourceServiceAsync;
import com.areahomeschoolers.baconbits.client.util.ClientUtils;
import com.areahomeschoolers.baconbits.client.util.Formatter;
import com.areahomeschoolers.baconbits.client.util.PageUrl;
import com.areahomeschoolers.baconbits.client.util.Url;
import com.areahomeschoolers.baconbits.client.widgets.AddressField;
import com.areahomeschoolers.baconbits.client.widgets.ControlledRichTextArea;
import com.areahomeschoolers.baconbits.client.widgets.CookieCrumb;
import com.areahomeschoolers.baconbits.client.widgets.DefaultListBox;
import com.areahomeschoolers.baconbits.client.widgets.EditableImage;
import com.areahomeschoolers.baconbits.client.widgets.EmailTextBox;
import com.areahomeschoolers.baconbits.client.widgets.FieldTable;
import com.areahomeschoolers.baconbits.client.widgets.Form;
import com.areahomeschoolers.baconbits.client.widgets.FormField;
import com.areahomeschoolers.baconbits.client.widgets.PaddedPanel;
import com.areahomeschoolers.baconbits.client.widgets.PhoneTextBox;
import com.areahomeschoolers.baconbits.client.widgets.RequiredTextBox;
import com.areahomeschoolers.baconbits.client.widgets.ValidatorDateBox;
import com.areahomeschoolers.baconbits.shared.Common;
import com.areahomeschoolers.baconbits.shared.dto.Arg.ResourceArg;
import com.areahomeschoolers.baconbits.shared.dto.ArgMap;
import com.areahomeschoolers.baconbits.shared.dto.ArgMap.Status;
import com.areahomeschoolers.baconbits.shared.dto.Document.DocumentLinkType;
import com.areahomeschoolers.baconbits.shared.dto.Resource;
import com.areahomeschoolers.baconbits.shared.dto.ResourcePageData;
import com.areahomeschoolers.baconbits.shared.dto.Tag.TagMappingType;

import com.google.gwt.dom.client.Style.BorderStyle;
import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.dom.client.Style.WhiteSpace;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
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
	// private ResourcePageData pd;
	private ResourceServiceAsync resourceService = (ResourceServiceAsync) ServiceCache.getService(ResourceService.class);
	private TagSection tagSection;
	private FormField tagField;
	private String title;

	public ResourcePage(VerticalPanel p) {
		int resourceId = Url.getIntegerParameter("resourceId");

		if (!Application.isAuthenticated() && resourceId < 0) {
			new ErrorPage(PageError.NOT_AUTHORIZED);
			return;
		}

		this.page = p;

		resourceService.getPageData(resourceId, new Callback<ResourcePageData>() {
			@Override
			protected void doOnSuccess(ResourcePageData result) {
				if (result == null) {
					new ErrorPage(PageError.PAGE_NOT_FOUND);
					return;
				}

				resource = result.getResource();

				if (Url.getBooleanParameter("details") && !Application.administratorOf(resource)) {
					new ErrorPage(PageError.NOT_AUTHORIZED);
					return;
				}

				title = resource.isSaved() ? resource.getName() : "New Resource";

				CookieCrumb cc = new CookieCrumb();
				cc.add(new Hyperlink("Resources By Type", PageUrl.tagGroup("RESOURCE")));
				cc.add(new Hyperlink("Resources", PageUrl.resourceList()));
				if (Url.getBooleanParameter("details")) {
					cc.add(new Hyperlink(title, PageUrl.resource(resource.getId())));
					cc.add("Edit details");
				} else {
					cc.add(title);
				}
				page.add(cc);

				if (resource.isSaved() && !Url.getBooleanParameter("details")) {
					createViewPage();
				} else {
					createDetailsPage();
				}

				Application.getLayout().setPage(title, page);
			}
		});
	}

	private boolean allowEdit() {
		return Application.isAuthenticated();
	}

	private void createDetailsPage() {
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
			EditableImage image = new EditableImage(DocumentLinkType.RESOURCE, resource.getId());
			image.setEnabled(Application.isSystemAdministrator());

			if (resource.getImageId() != null) {
				image.setImage(new Image(ClientUtils.createDocumentUrl(resource.getImageId(), resource.getImageExtension())));
			} else {
				image.setImage(new Image(MainImageBundle.INSTANCE.defaultLarge()));
			}
			image.populate();
			image.getElement().getStyle().setMarginRight(10, Unit.PX);
			pp.add(image);

			ft.removeStyleName("sectionContent");
			pp.addStyleName("sectionContent");
			pp.add(image);
		}

		pp.add(ft);

		pp.setWidth("1000px");
		page.add(pp);

		form.setEnabled(allowEdit());
	}

	private void createFieldTable() {
		ft.setWidth("100%");

		final VerticalPanel titleVp = new VerticalPanel();
		final RequiredTextBox titleInput = new RequiredTextBox();
		titleVp.add(titleInput);
		final VerticalPanel dupePanel = new VerticalPanel();
		dupePanel.setSpacing(6);
		dupePanel.getElement().getStyle().setBackgroundColor("#e2a7c1");

		if (!resource.isSaved()) {
			titleInput.addBlurHandler(new BlurHandler() {
				@Override
				public void onBlur(BlurEvent event) {
					dupePanel.clear();

					if (titleInput.getText().trim().isEmpty()) {
						return;
					}

					ArgMap<ResourceArg> args = new ArgMap<>(Status.ACTIVE);
					args.put(ResourceArg.NAME_LIKE, titleInput.getText().trim());
					args.put(ResourceArg.LIMIT, 5);
					resourceService.list(args, new Callback<ArrayList<Resource>>(false) {
						@Override
						protected void doOnSuccess(ArrayList<Resource> result) {
							if (result.isEmpty()) {
								dupePanel.removeFromParent();
								return;
							}

							titleVp.add(dupePanel);

							String text = "The following resource";
							if (result.size() == 1) {
								text += " has a ";
							} else {
								text += "s have a ";
							}
							text += "similar name";
							Label message = new Label(text);
							message.addStyleName("bold");

							dupePanel.add(message);

							for (Resource r : result) {
								Anchor link = new Anchor(r.getName(), Url.getBaseUrl() + "#" + PageUrl.resource(0));
								link.setTarget("_blank");
								dupePanel.add(link);
							}
						}
					});
				}
			});
		}
		final Label titleDisplay = new Label();
		titleInput.setVisibleLength(35);
		titleInput.setMaxLength(50);
		FormField titleField = form.createFormField("Name:", titleVp, titleDisplay);
		titleField.setValidator(titleInput.getValidator());
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

		final Anchor urlDisplay = new Anchor();
		urlDisplay.setTarget("_blank");
		final TextBox urlInput = new TextBox();
		urlInput.setMaxLength(500);
		urlInput.setVisibleLength(50);
		FormField urlField = form.createFormField("Web site:", urlInput, urlDisplay);
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

		createTagSection();
		if (!resource.isSaved()) {
			ft.addField(tagField);
		}

		FormField addressField = new AddressField(resource).getFormField();
		form.addField(addressField);
		ft.addField(addressField);

		final Label facilityDisplay = new Label();
		final TextBox facilityInput = new TextBox();
		facilityInput.setMaxLength(100);
		FormField facilityField = form.createFormField("Facility name:", facilityInput, facilityDisplay);
		facilityField.setInitializer(new Command() {
			@Override
			public void execute() {
				facilityDisplay.setText(Common.getDefaultIfNull(resource.getFacilityName()));
				facilityInput.setText(resource.getFacilityName());
			}
		});
		facilityField.setDtoUpdater(new Command() {
			@Override
			public void execute() {
				resource.setFacilityName(facilityInput.getText());
			}
		});
		ft.addField(facilityField);

		final Label contactDisplay = new Label();
		final TextBox contactInput = new TextBox();
		contactInput.setMaxLength(100);
		FormField contactField = form.createFormField("Contact name:", contactInput, contactDisplay);
		contactField.setInitializer(new Command() {
			@Override
			public void execute() {
				contactDisplay.setText(Common.getDefaultIfNull(resource.getContactName()));
				contactInput.setText(resource.getContactName());
			}
		});
		contactField.setDtoUpdater(new Command() {
			@Override
			public void execute() {
				resource.setContactName(contactInput.getText());
			}
		});
		ft.addField(contactField);

		final Label emailDisplay = new Label();
		final EmailTextBox emailInput = new EmailTextBox();
		emailInput.setMaxLength(100);
		FormField emailField = form.createFormField("Email:", emailInput, emailDisplay);
		emailField.setInitializer(new Command() {
			@Override
			public void execute() {
				emailDisplay.setText(Common.getDefaultIfNull(resource.getContactEmail()));
				emailInput.setText(resource.getContactEmail());
			}
		});
		emailField.setDtoUpdater(new Command() {
			@Override
			public void execute() {
				resource.setContactEmail(emailInput.getText());
			}
		});
		ft.addField(emailField);

		final Label phoneDisplay = new Label();
		final PhoneTextBox phoneInput = new PhoneTextBox(true);
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

		final Label facebookDisplay = new Label();
		final TextBox facebookInput = new TextBox();
		facebookInput.setVisibleLength(50);
		facebookInput.setMaxLength(200);
		FormField facebookField = form.createFormField("Facebook URL:", facebookInput, facebookDisplay);
		facebookField.setInitializer(new Command() {
			@Override
			public void execute() {
				facebookDisplay.setText(Common.getDefaultIfNull(resource.getFacebookUrl()));
				facebookInput.setText(resource.getFacebookUrl());
			}
		});
		facebookField.setDtoUpdater(new Command() {
			@Override
			public void execute() {
				resource.setFacebookUrl(facebookInput.getText());
			}
		});
		ft.addField(facebookField);

		if (Application.isSystemAdministrator()) {
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

			final Label priorityDisplay = new Label();
			final DefaultListBox priorityInput = new DefaultListBox();
			priorityInput.addItem("Yes");
			priorityInput.addItem("No");
			FormField priorityField = form.createFormField("Directory listing priority:", priorityInput, priorityDisplay);
			priorityField.setInitializer(new Command() {
				@Override
				public void execute() {
					priorityDisplay.setText(resource.getDirectoryPriority() ? "Yes" : "No");
					priorityInput.setValue(resource.getDirectoryPriority() ? "Yes" : "No");
				}
			});
			priorityField.setDtoUpdater(new Command() {
				@Override
				public void execute() {
					resource.setDirectoryPriority(priorityInput.getValue().equals("Yes"));
				}
			});
			ft.addField(priorityField);

			adInput.addChangeHandler(new ChangeHandler() {
				@Override
				public void onChange(ChangeEvent event) {
					priorityInput.setValue(adInput.getValue());
				}
			});

			final TextBox adDescriptionInput = new TextBox();
			final Label adDescriptionDisplay = new Label();
			adDescriptionInput.setMaxLength(50);
			adDescriptionInput.setWidth("215px");
			adDescriptionInput.getElement().getStyle().setFontSize(13, Unit.PX);
			FormField adDescriptionField = form.createFormField("Ad description:", adDescriptionInput, adDescriptionDisplay);
			adDescriptionField.setInitializer(new Command() {
				@Override
				public void execute() {
					adDescriptionDisplay.setText(Common.getDefaultIfNull(resource.getAdDescription()));
					adDescriptionInput.setText(resource.getAdDescription());
				}
			});
			adDescriptionField.setDtoUpdater(new Command() {
				@Override
				public void execute() {
					resource.setAdDescription(adDescriptionInput.getText().trim());
				}
			});
			ft.addField(adDescriptionField);
		}

		if (resource.isSaved() && resource.getShowInAds()) {
			String clicks = "None";
			if (resource.getClickCount() > 0) {
				clicks = resource.getClickCount() + " (last " + Formatter.formatDateTime(resource.getLastClickDate()) + ")";
			}
			ft.addField("Clicks:", clicks);
		}

		if (Application.isSystemAdministrator()) {
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
		}

		// if (Application.isSystemAdministrator()) {
		// final Label scopeDisplay = new Label();
		// final DefaultListBox scopeInput = new DefaultListBox();
		// scopeInput.addItem("N/A", 0);
		// for (Data d : pd.getAddressScopes()) {
		// scopeInput.addItem(d.get("scope"), d.getId());
		// }
		// FormField scopeField = form.createFormField("Address scope:", scopeInput, scopeDisplay);
		// scopeField.setInitializer(new Command() {
		// @Override
		// public void execute() {
		// scopeDisplay.setText(Common.getDefaultIfNull(resource.getAddressScope(), "N/A"));
		// scopeInput.setValue(resource.getAddressScopeId());
		// }
		// });
		// scopeField.setDtoUpdater(new Command() {
		// @Override
		// public void execute() {
		// resource.setAddressScopeId(scopeInput.getIntValue());
		// }
		// });
		// ft.addField(scopeField);
		// }

		final HTML descriptionDisplay = new HTML();
		final ControlledRichTextArea descriptionInput = new ControlledRichTextArea();
		FormField descriptionField = form.createFormField("Description:", descriptionInput, descriptionDisplay);
		descriptionDisplay.getElement().getStyle().setPadding(10, Unit.PX);
		descriptionDisplay.setWidth("800px");
		descriptionDisplay.getElement().getStyle().setOverflowX(Overflow.HIDDEN);
		descriptionField.setRequired(true);
		descriptionField.setInitializer(new Command() {
			@Override
			public void execute() {
				descriptionDisplay.setHTML(resource.getDescription());
				descriptionInput.getTextArea().setHTML(resource.getDescription());
			}
		});
		descriptionField.setDtoUpdater(new Command() {
			@Override
			public void execute() {
				resource.setDescription(descriptionInput.getTextArea().getHTML());
			}
		});
		ft.addSpanningWidget(descriptionField);

		if (resource.isSaved()) {
			ft.addField(tagField);
		}

		Button cancelButton = new Button("Cancel", new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				History.back();
			}
		});

		form.getButtonPanel().insertCenterButton(cancelButton, 0);
	}

	private void createTagSection() {
		tagSection = new TagSection(TagMappingType.RESOURCE, resource.getId());
		tagSection.setEditingEnabled(allowEdit());
		tagSection.setRequired(true);
		tagSection.populate();

		tagField = form.createFormField("Tags:", tagSection);
		tagField.removeEditLabel();
	}

	private void createViewPage() {
		PaddedPanel pp = new PaddedPanel(10);
		pp.setWidth("100%");
		pp.getElement().getStyle().setMarginTop(10, Unit.PX);
		pp.getElement().getStyle().setMarginLeft(10, Unit.PX);

		EditableImage image = new EditableImage(DocumentLinkType.RESOURCE, resource.getId());
		if (resource.getImageId() != null) {
			image.setImage(new Image(ClientUtils.createDocumentUrl(resource.getImageId(), resource.getImageExtension())));
		} else {
			image.setImage(new Image(MainImageBundle.INSTANCE.defaultLarge()));
		}
		image.setEnabled(Application.isSystemAdministrator());
		image.populate();
		pp.add(image);
		pp.setCellWidth(image, "1%");

		VerticalPanel vp = new VerticalPanel();

		Label titleLabel = new Label(resource.getName());
		titleLabel.addStyleName("hugeText");

		vp.add(titleLabel);

		if (!Common.isNullOrBlank(resource.getFacilityName())) {
			vp.add(new Label(resource.getFacilityName()));
		}

		if (!Common.isNullOrBlank(resource.getAddress())) {
			Anchor address = new Anchor(resource.getAddress(), "http://maps.google.com/maps?q=" + resource.getAddress());
			address.setTarget("_blank");

			vp.add(address);
		}

		String text = "";
		if (!Common.isNullOrBlank(resource.getContactName())) {
			text += Formatter.formatNoteText(resource.getContactName()) + "<br>";
		}

		if (!Common.isNullOrBlank(resource.getContactEmail())) {
			text += "<a href=\"mailto:" + resource.getContactEmail() + "\">" + Formatter.formatNoteText(resource.getContactEmail()) + "</a><br>";
		}

		if (!Common.isNullOrBlank(resource.getPhone())) {
			text += Formatter.formatNoteText(resource.getPhone()) + "<br>";
		}

		PaddedPanel lp = new PaddedPanel();
		if (!Common.isNullOrBlank(resource.getFacebookUrl())) {
			Image fb = new Image(MainImageBundle.INSTANCE.faceBook());
			fb.getElement().getStyle().setMarginTop(5, Unit.PX);
			String fbLink = "<a href=\"" + resource.getFacebookUrl() + "\" target=_blank>" + fb + "</a>";
			lp.add(new HTML(fbLink));
		}

		if (!Common.isNullOrBlank(resource.getUrl())) {
			HTML web = new HTML("<a href=\"" + resource.getUrl() + "\" target=_blank>Web site</a>");
			lp.add(web);
			lp.setCellVerticalAlignment(web, HasVerticalAlignment.ALIGN_MIDDLE);
		}

		if (lp.getWidgetCount() > 0) {
			text += lp;
		}

		if (!text.isEmpty()) {
			HTML contactInfo = new HTML();
			contactInfo.getElement().getStyle().setMarginTop(10, Unit.PX);
			contactInfo.setHTML(text);

			vp.add(contactInfo);
		}

		pp.add(vp);

		if (allowEdit()) {
			Hyperlink edit = new Hyperlink("Edit details", PageUrl.resource(resource.getId()) + "&details=true");
			edit.getElement().getStyle().setWhiteSpace(WhiteSpace.NOWRAP);
			edit.getElement().getStyle().setMarginRight(5, Unit.PX);
			pp.add(edit);
			pp.setCellHorizontalAlignment(edit, HasHorizontalAlignment.ALIGN_RIGHT);
		}

		VerticalPanel ovp = new VerticalPanel();
		ovp.addStyleName("sectionContent");

		ovp.add(pp);

		TagSection ts = new TagSection(TagMappingType.RESOURCE, resource.getId());
		ts.setEditingEnabled(false);
		ts.populate();

		ovp.add(ts);

		if (!Common.isNullOrBlank(resource.getDescription())) {
			HTML desc = new HTML(resource.getDescription());
			desc.setWidth("700px");
			desc.getElement().getStyle().setOverflowX(Overflow.HIDDEN);
			desc.getElement().getStyle().setMarginLeft(15, Unit.PX);
			desc.getElement().getStyle().setMarginRight(15, Unit.PX);
			desc.getElement().getStyle().setMarginBottom(15, Unit.PX);
			desc.getElement().getStyle().setPadding(10, Unit.PX);
			desc.getElement().getStyle().setBackgroundColor("#ffffff");
			desc.getElement().getStyle().setBorderColor("#cccccc");
			desc.getElement().getStyle().setBorderStyle(BorderStyle.SOLID);
			desc.getElement().getStyle().setBorderWidth(1, Unit.PX);

			ovp.add(desc);
		}

		PaddedPanel outerPanel = new PaddedPanel(10);
		outerPanel.add(new AdsMiniModule());
		outerPanel.add(ovp);

		page.add(outerPanel);
	}

	private void save(final FormField field) {
		resourceService.save(resource, new Callback<Resource>() {
			@Override
			protected void doOnSuccess(final Resource r) {
				if (!Url.isParamValidId("resourceId")) {
					tagSection.saveAll(r.getId(), new Callback<Void>() {
						@Override
						protected void doOnSuccess(Void result) {
							HistoryToken.set(PageUrl.resource(r.getId()));
						}
					});
				} else {
					resource = r;
					form.setDto(r);
					field.setInputVisibility(false);
				}
			}
		});
	}
}
