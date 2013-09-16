package com.areahomeschoolers.baconbits.client.widgets;

import com.areahomeschoolers.baconbits.client.Application;
import com.areahomeschoolers.baconbits.client.HistoryToken;
import com.areahomeschoolers.baconbits.client.ServiceCache;
import com.areahomeschoolers.baconbits.client.rpc.Callback;
import com.areahomeschoolers.baconbits.client.rpc.service.ArticleService;
import com.areahomeschoolers.baconbits.client.rpc.service.ArticleServiceAsync;
import com.areahomeschoolers.baconbits.client.rpc.service.UserService;
import com.areahomeschoolers.baconbits.client.rpc.service.UserServiceAsync;
import com.areahomeschoolers.baconbits.client.util.PageUrl;
import com.areahomeschoolers.baconbits.shared.dto.Article;
import com.areahomeschoolers.baconbits.shared.dto.ServerResponseData;
import com.areahomeschoolers.baconbits.shared.dto.User;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.VerticalPanel;

public class UserAgreementDialog extends DefaultDialog {
	private ArticleServiceAsync articleService = (ArticleServiceAsync) ServiceCache.getService(ArticleService.class);
	private UserServiceAsync userService = (UserServiceAsync) ServiceCache.getService(UserService.class);
	private VerticalPanel vp = new VerticalPanel();

	public UserAgreementDialog() {
		vp.setWidth("600px");
		vp.setSpacing(10);
		setModal(true);
		setGlassEnabled(true);

		setText("End User License Agreement");

		setWidget(vp);
	}

	@Override
	public void show() {
		if (vp.getWidgetCount() > 0) {
			super.show();
			return;
		}

		articleService.getById(72, new Callback<Article>() {
			@Override
			protected void doOnSuccess(Article result) {
				MaxHeightScrollPanel sp = new MaxHeightScrollPanel(new HTML(result.getArticle()));
				vp.add(sp);
				final CheckBox cb = new CheckBox("<b>I agree to the terms and conditions above</b>", true);
				vp.add(cb);
				ButtonPanel bp = new ButtonPanel();
				final Button settings = new Button("Continue to Privacy Settings");
				settings.setEnabled(false);
				settings.addClickHandler(new ClickHandler() {
					@Override
					public void onClick(ClickEvent event) {
						if (!cb.getValue()) {
							cb.addStyleName("gwt-TextBoxError");
							return;
						}

						settings.setEnabled(false);
						User u = Application.getCurrentUser();
						u.setShowUserAgreement(false);
						u.setDirectoryOptOut(false);
						userService.save(u, new Callback<ServerResponseData<User>>() {
							@Override
							protected void doOnSuccess(ServerResponseData<User> result) {
								HistoryToken.set(PageUrl.user(Application.getCurrentUserId()) + "&tab=7&gb=true");
							}
						});

					}
				});
				bp.addCenterButton(settings);

				cb.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
					@Override
					public void onValueChange(ValueChangeEvent<Boolean> event) {
						settings.setEnabled(event.getValue());
					}
				});

				vp.add(bp);

				UserAgreementDialog.super.show();
				Scheduler.get().scheduleDeferred(new ScheduledCommand() {
					@Override
					public void execute() {
						UserAgreementDialog.super.center();
					}
				});
			}
		});
	}
}
