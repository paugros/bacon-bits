package com.areahomeschoolers.baconbits.client.content.article;

import java.util.ArrayList;

import com.areahomeschoolers.baconbits.client.Application;
import com.areahomeschoolers.baconbits.client.ServiceCache;
import com.areahomeschoolers.baconbits.client.rpc.Callback;
import com.areahomeschoolers.baconbits.client.rpc.service.ArticleService;
import com.areahomeschoolers.baconbits.client.rpc.service.ArticleServiceAsync;
import com.areahomeschoolers.baconbits.client.util.Url;
import com.areahomeschoolers.baconbits.client.widgets.MaxLengthTextArea;
import com.areahomeschoolers.baconbits.shared.dto.Arg.ArticleArg;
import com.areahomeschoolers.baconbits.shared.dto.ArgMap;
import com.areahomeschoolers.baconbits.shared.dto.NewsBulletinComment;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.VerticalPanel;

public class NewsCommentSection extends Composite {
	private boolean commenting = Url.getIntegerParameter("comment") == 1;
	private final ArticleServiceAsync newsService = (ArticleServiceAsync) ServiceCache.getService(ArticleService.class);

	public NewsCommentSection(final int newsId, ArgMap<ArticleArg> args) {
		final VerticalPanel vp = new VerticalPanel();
		final VerticalPanel commentPanel = new VerticalPanel();

		VerticalPanel commentInputs = new VerticalPanel();

		final MaxLengthTextArea commentBox = new MaxLengthTextArea(2000);
		commentBox.getTextArea().setVisibleLines(4);
		commentBox.getTextArea().setWidth("590px");

		if (commenting) {
			Scheduler.get().scheduleDeferred(new ScheduledCommand() {
				@Override
				public void execute() {
					commentBox.setFocus(true);
					Application.getLayout().getBodyPanel().scrollToBottom();
				}
			});
		}

		if (Application.isAuthenticated()) {
			final Button submit = new Button("Post");

			submit.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					if (commentBox.getText().equals("")) {
						return;
					}

					NewsBulletinComment comment = new NewsBulletinComment();
					comment.setComment(commentBox.getText());
					comment.setArticleId(newsId);
					submit.setEnabled(false);
					Application.setRpcFailureCommand(new Command() {
						@Override
						public void execute() {
							submit.setEnabled(true);
						}
					});

					newsService.saveComment(comment, new Callback<NewsBulletinComment>() {
						@Override
						protected void doOnSuccess(NewsBulletinComment result) {
							commentPanel.insert(new CommentWidget(result), 0);
							commentBox.setText("");
							submit.setEnabled(true);
						}
					});
				}
			});

			commentInputs.add(commentBox);
			commentInputs.add(submit);

			vp.add(commentInputs);
		}

		commentPanel.getElement().getStyle().setMarginTop(20, Unit.PX);
		commentPanel.setSpacing(10);
		vp.add(commentPanel);

		newsService.getComments(args, new Callback<ArrayList<NewsBulletinComment>>() {
			@Override
			protected void doOnSuccess(ArrayList<NewsBulletinComment> result) {
				for (NewsBulletinComment c : result) {
					commentPanel.add(new CommentWidget(c));
				}
			}
		});

		initWidget(vp);
	}
}
