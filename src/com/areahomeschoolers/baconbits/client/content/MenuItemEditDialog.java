package com.areahomeschoolers.baconbits.client.content;

import com.areahomeschoolers.baconbits.client.Application;
import com.areahomeschoolers.baconbits.client.content.article.ArticlePicker;
import com.areahomeschoolers.baconbits.client.validation.Validator;
import com.areahomeschoolers.baconbits.client.validation.ValidatorCommand;
import com.areahomeschoolers.baconbits.client.widgets.EntityEditDialog;
import com.areahomeschoolers.baconbits.client.widgets.FieldTable;
import com.areahomeschoolers.baconbits.client.widgets.FieldTable.LabelColumnWidth;
import com.areahomeschoolers.baconbits.client.widgets.FormField;
import com.areahomeschoolers.baconbits.client.widgets.ItemVisibilityWidget;
import com.areahomeschoolers.baconbits.client.widgets.RequiredTextBox;
import com.areahomeschoolers.baconbits.client.widgets.cellview.EntityCellTable.SelectionPolicy;
import com.areahomeschoolers.baconbits.shared.Common;
import com.areahomeschoolers.baconbits.shared.dto.Arg.ArticleArg;
import com.areahomeschoolers.baconbits.shared.dto.ArgMap;
import com.areahomeschoolers.baconbits.shared.dto.ArgMap.Status;
import com.areahomeschoolers.baconbits.shared.dto.MainMenuItem;
import com.areahomeschoolers.baconbits.shared.dto.UserGroup.VisibilityLevel;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class MenuItemEditDialog extends EntityEditDialog<MainMenuItem> {

	private RequiredTextBox nameInput;

	public MenuItemEditDialog() {
		setText("Edit Menu Item");
	}

	@Override
	public void show() {
		Scheduler.get().scheduleDeferred(new ScheduledCommand() {
			@Override
			public void execute() {
				nameInput.setFocus(true);
			}
		});

		super.show();
	}

	@Override
	protected Widget createContent() {
		FieldTable ft = new FieldTable();
		ft.setWidth("520px");
		ft.setLabelColumnWidth(LabelColumnWidth.NARROW);

		nameInput = new RequiredTextBox();
		if (entity.getParentNodeId() == null) {
			nameInput.setMaxLength(15);
		} else {
			nameInput.setMaxLength(50);
		}
		FormField nameField = form.createFormField("Name:", nameInput, null);
		nameField.setInitializer(new Command() {
			@Override
			public void execute() {
				nameInput.setText(entity.getName());
			}
		});
		nameField.setDtoUpdater(new Command() {
			@Override
			public void execute() {
				entity.setName(nameInput.getText());
			}
		});
		ft.addField(nameField);

		// type
		VerticalPanel typePanel = new VerticalPanel();
		FocusPanel fp = new FocusPanel(typePanel);
		typePanel.setSpacing(4);
		final RadioButton ab = new RadioButton("type", "Articles");
		final RadioButton wb = new RadioButton("type", "Web address");
		final RadioButton sb = new RadioButton("type", "Sub-menu");
		final TextBox addr = new TextBox();
		addr.setMaxLength(256);
		addr.setVisibleLength(40);
		addr.getElement().getStyle().setMarginLeft(20, Unit.PX);
		ArgMap<ArticleArg> args = new ArgMap<ArticleArg>(Status.ACTIVE);
		args.put(ArticleArg.OWNING_ORG_ID, Application.getCurrentOrgId());
		final ArticlePicker ap = new ArticlePicker(args);
		ap.getChangeButton().setText("Select...");
		ap.getElement().getStyle().setMarginLeft(20, Unit.PX);
		ap.getSelector().getCellTable().setSelectionPolicy(SelectionPolicy.MULTI_ROW);

		VerticalPanel vap = new VerticalPanel();
		vap.add(ab);
		vap.add(ap);

		VerticalPanel wap = new VerticalPanel();
		wap.add(wb);
		wap.add(addr);

		typePanel.add(vap);
		typePanel.add(wap);
		typePanel.add(sb);

		if (entity.getParentNodeId() != null) {
			FormField typeField = form.createFormField("Type:", fp, null);
			typeField.setInitializer(new Command() {
				@Override
				public void execute() {
					if (entity.isSaved()) {
						if (!Common.isNullOrBlank(entity.getArticleIds())) {
							ab.setValue(true);
							String[] ids = entity.getArticleIds().split(",");
							for (int i = 0; i < ids.length; i++) {
								ap.getSelector().setSelectedItemById(Integer.parseInt(ids[i]));
							}
							ap.getTextBox().setText(entity.getArticleIds());
						} else if (!Common.isNullOrBlank(entity.getUrl())) {
							wb.setValue(true);
							addr.setText(entity.getUrl());
						} else {
							sb.setValue(true);
						}
					}
				}
			});

			typeField.setDtoUpdater(new Command() {
				@Override
				public void execute() {
					if (ab.getValue()) {
						entity.setArticleIds(Common.join(ap.getValueIds(), ","));
					} else if (wb.getValue()) {
						String url = addr.getText();
						if (!url.matches("^\\w+:\\/\\/.*")) {
							url = "http://" + url;
						}
						entity.setUrl(url);
					}
				}
			});

			typeField.setValidator(new Validator(fp, new ValidatorCommand() {
				@Override
				public void validate(Validator validator) {
					if (ab.getValue() && ap.getValueIds().isEmpty()) {
						validator.setError(true);
					} else if (wb.getValue() && addr.getText().isEmpty()) {
						validator.setError(true);
					} else if (!ab.getValue() && !wb.getValue() && !sb.getValue()) {
						validator.setError(true);
					}
				}
			}));
			typeField.setRequired(true);

			ft.addField(typeField);
		}

		final ItemVisibilityWidget accessInput = new ItemVisibilityWidget();
		accessInput.removeItem(VisibilityLevel.PRIVATE);
		accessInput.removeItem(VisibilityLevel.MY_GROUPS);
		FormField accessField = form.createFormField("Visible to:", accessInput, null);
		accessField.setInitializer(new Command() {
			@Override
			public void execute() {
				accessInput.setVisibilityLevelId(entity.getVisibilityLevelId());
				accessInput.setGroupId(entity.getGroupId());
			}
		});
		accessField.setDtoUpdater(new Command() {
			@Override
			public void execute() {
				entity.setVisibilityLevelId(accessInput.getVisibilityLevelId());
				entity.setGroupId(accessInput.getGroupId());
			}
		});
		ft.addField(accessField);

		return ft;
	}

}
