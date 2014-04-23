package com.areahomeschoolers.baconbits.client.content.user;

import java.util.Date;

import com.areahomeschoolers.baconbits.client.Application;
import com.areahomeschoolers.baconbits.client.HistoryToken;
import com.areahomeschoolers.baconbits.client.ServiceCache;
import com.areahomeschoolers.baconbits.client.event.FormSubmitHandler;
import com.areahomeschoolers.baconbits.client.rpc.Callback;
import com.areahomeschoolers.baconbits.client.rpc.service.ArticleService;
import com.areahomeschoolers.baconbits.client.rpc.service.ArticleServiceAsync;
import com.areahomeschoolers.baconbits.client.rpc.service.LoginService;
import com.areahomeschoolers.baconbits.client.rpc.service.LoginServiceAsync;
import com.areahomeschoolers.baconbits.client.rpc.service.UserService;
import com.areahomeschoolers.baconbits.client.rpc.service.UserServiceAsync;
import com.areahomeschoolers.baconbits.client.util.ClientDateUtils;
import com.areahomeschoolers.baconbits.client.util.PageUrl;
import com.areahomeschoolers.baconbits.client.util.Url;
import com.areahomeschoolers.baconbits.client.validation.Validator;
import com.areahomeschoolers.baconbits.client.validation.ValidatorCommand;
import com.areahomeschoolers.baconbits.client.widgets.AddressField;
import com.areahomeschoolers.baconbits.client.widgets.AlertDialog;
import com.areahomeschoolers.baconbits.client.widgets.ClickLabel;
import com.areahomeschoolers.baconbits.client.widgets.EntityEditDialog;
import com.areahomeschoolers.baconbits.client.widgets.FieldTable;
import com.areahomeschoolers.baconbits.client.widgets.FieldTable.LabelColumnWidth;
import com.areahomeschoolers.baconbits.client.widgets.FormField;
import com.areahomeschoolers.baconbits.client.widgets.ServerResponseDialog;
import com.areahomeschoolers.baconbits.shared.Constants;
import com.areahomeschoolers.baconbits.shared.dto.Article;
import com.areahomeschoolers.baconbits.shared.dto.ServerResponseData;
import com.areahomeschoolers.baconbits.shared.dto.User;
import com.areahomeschoolers.baconbits.shared.dto.UserGroup;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.DecoratedPopupPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class CreateUserDialog extends EntityEditDialog<User> {
	private UserServiceAsync userService = (UserServiceAsync) ServiceCache.getService(UserService.class);
	private LoginServiceAsync loginService = (LoginServiceAsync) ServiceCache.getService(LoginService.class);
	private ArticleServiceAsync articleService = (ArticleServiceAsync) ServiceCache.getService(ArticleService.class);
	private Article instructions;
	private CheckBox cb;

	public CreateUserDialog() {
		setText("Create Account");
		setAutoHide(false);

		form.getSubmitButton().setText("Create");
		form.addFormSubmitHandler(new FormSubmitHandler() {
			@Override
			public void onFormSubmit(FormField formField) {
				// verify age
				if ((ClientDateUtils.daysBetween(new Date(), entity.getBirthDate()) / 365) < 13) {
					AlertDialog.alert("Children under the age of 13 must have their account created by a parent or guardian.");
					form.getSubmitButton().setEnabled(true);
					return;
				}

				if (Url.getIntegerParameter("aagrp") > 0) {
					entity.setAutoAddToGroupId(Url.getIntegerParameter("aagrp"));
				}

				AddressField.validateAddress(entity, new Command() {
					@Override
					public void execute() {
						userService.save(entity, new Callback<ServerResponseData<User>>() {
							@Override
							protected void doOnSuccess(ServerResponseData<User> result) {
								final User savedUser = result.getData();
								if (result.hasErrors()) {
									new ServerResponseDialog(result).center();
									form.getSubmitButton().setEnabled(true);
									return;
								}

								loginService.login(entity.getUserName(), entity.getPassword(), new Callback<Boolean>() {
									@Override
									protected void doOnSuccess(Boolean result) {
										HistoryToken.set(PageUrl.user(savedUser.getId()) + "&tab=7&gb=true", false);
										Window.Location.reload();
									}
								});
							}
						});
					}
				});
			}
		});
	}

	private void showInstructions(Widget relative) {
		DecoratedPopupPanel pp = new DecoratedPopupPanel(true);
		HTML h = new HTML(instructions.getArticle());
		h.setWidth("240px");
		pp.setWidget(h);
		pp.showRelativeTo(relative);
	}

	@Override
	protected Widget createContent() {
		VerticalPanel vp = new VerticalPanel();
		final ClickLabel help = new ClickLabel("Help");
		help.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				if (instructions == null) {
					articleService.getById(Constants.ACCOUNT_CREATION_INSTRUCTIONS_ID, new Callback<Article>() {
						@Override
						protected void doOnSuccess(Article result) {
							instructions = result;
							showInstructions(help);
						}
					});
				} else {
					showInstructions(help);
				}
			}
		});
		help.addStyleName("bold");
		vp.add(help);
		vp.setCellHorizontalAlignment(help, HasHorizontalAlignment.ALIGN_RIGHT);

		vp.setWidth("530px");
		vp.setSpacing(6);

		FieldTable ft = new UserFieldTable(form, entity);
		ft.setLabelColumnWidth(LabelColumnWidth.NARROW);

		vp.add(ft);

		UserGroup g = Application.getCurrentOrg();
		if (g.getGeneralPolicyId() != null) {
			String txt = "I agree to the " + g.getGroupName() + " ";
			txt += "<a href=\"#" + PageUrl.article(g.getGeneralPolicyId()) + "&noTitle=true\" target=\"_blank\">terms and conditions</a>";
			cb = new CheckBox(txt, true);
			vp.add(cb);
		}

		String txt = "I agree to the site ";
		txt += "<a href=\"" + Constants.TOS_URL + "&noTitle=true\" target=\"_blank\">terms of service</a> ";
		txt += "and <a href=\"" + Constants.PRIVACY_POLICY_URL + "&noTitle=true\" target=\"_blank\">privacy policy</a> ";
		final CheckBox scb = new CheckBox(txt, true);
		vp.add(scb);

		form.addFormValidatorCommand(new ValidatorCommand() {
			@Override
			public void validate(Validator validator) {
				if (cb != null && !cb.getValue()) {
					validator.setError(true);
					validator.setErrorMessage("You must agree to the terms and conditions in order to join");
				}

				if (!scb.getValue()) {
					validator.setError(true);
					validator.setErrorMessage("You must agree to the site terms of service and privacy policy in order to use this site");
				}

				if (validator.hasError()) {
					return;
				}

				entity.setDirectoryOptOut(false);
				entity.setShowUserAgreement(false);

				validator.setError(false);
			}
		});

		return vp;
	}

}
