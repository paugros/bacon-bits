package com.areahomeschoolers.baconbits.client.widgets;

import com.areahomeschoolers.baconbits.client.event.FormSubmitHandler;

import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public abstract class EntityEditDialog<T> extends DefaultDialog {
	private VerticalPanel vp = new VerticalPanel();
	private SimplePanel contentPanel = new SimplePanel();
	private VerticalPanel belowPanel = new VerticalPanel();
	private VerticalPanel abovePanel = new VerticalPanel();
	private VerticalPanel aboveButtonPanel = new VerticalPanel();
	private ButtonPanel buttonPanel;
	private boolean isInitialized;
	private boolean autoHide = true;
	private boolean isFinishedLoading = false;

	protected Form form = getForm();

	protected T entity;

	public EntityEditDialog() {
		super(false, true);
		vp.add(abovePanel);
		vp.add(contentPanel);

		buttonPanel = new ButtonPanel(this);
		buttonPanel.addLeftButton(form.getSubmitButton());
		vp.add(form.getErrorPanel());
		aboveButtonPanel.setWidth("100%");
		vp.add(aboveButtonPanel);

		vp.add(buttonPanel);

		belowPanel.setWidth("100%");
		vp.add(belowPanel);

		vp.setWidth("100%");
		super.setWidget(vp);
	}

	public void addFormSubmitHandler(FormSubmitHandler handler) {
		form.addFormSubmitHandler(handler);
	}

	public void center(T entity) {
		setEntity(entity);
		center();
	}

	public VerticalPanel getAboveButtonPanel() {
		return aboveButtonPanel;
	}

	public VerticalPanel getAbovePanel() {
		return abovePanel;
	}

	public VerticalPanel getBelowPanel() {
		return belowPanel;
	}

	public ButtonPanel getButtonPanel() {
		return buttonPanel;
	}

	public SimplePanel getContentPanel() {
		return contentPanel;
	}

	public T getEntity() {
		return entity;
	}

	public boolean isAutoHide() {
		return autoHide;
	}

	public boolean isInitialized() {
		return isInitialized;
	}

	public void setAutoHide(boolean autoHide) {
		this.autoHide = autoHide;
	}

	@Override
	public void setWidget(Widget w) {
		contentPanel.setWidget(w);
	}

	@Override
	public void show() {
		if (!(this instanceof LazyLoadDialog) || isFinishedLoading) {
			showContent();
			super.show();
		} else {
			((LazyLoadDialog) this).loadData();
		}
	}

	public void show(T entity) {
		setEntity(entity);
		show();
	}

	protected abstract Widget createContent();

	protected Form getForm() {
		return new Form(new FormSubmitHandler() {
			@Override
			public void onFormSubmit(FormField formWidget) {
				if (autoHide) {
					hide();
				}
			}
		});
	}

	protected void setEntity(T entity) {
		this.entity = entity;
	}

	protected void showContent() {
		isFinishedLoading = true;
		if (!isInitialized) {
			isInitialized = true;
			contentPanel.setWidget(createContent());
			center();
		}
		form.initialize();
		form.configureForAdd();
	}
}
