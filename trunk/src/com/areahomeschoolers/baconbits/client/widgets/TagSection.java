package com.areahomeschoolers.baconbits.client.widgets;

import java.util.ArrayList;
import java.util.List;

import com.areahomeschoolers.baconbits.client.Application;
import com.areahomeschoolers.baconbits.client.ServiceCache;
import com.areahomeschoolers.baconbits.client.content.EntitySuggestBox;
import com.areahomeschoolers.baconbits.client.event.ParameterHandler;
import com.areahomeschoolers.baconbits.client.rpc.Callback;
import com.areahomeschoolers.baconbits.client.rpc.service.TagService;
import com.areahomeschoolers.baconbits.client.rpc.service.TagServiceAsync;
import com.areahomeschoolers.baconbits.client.util.ClientUtils;
import com.areahomeschoolers.baconbits.shared.Constants;
import com.areahomeschoolers.baconbits.shared.dto.Tag;
import com.areahomeschoolers.baconbits.shared.dto.Tag.TagMappingType;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.VerticalPanel;

public class TagSection extends Composite {
	private class TagWidget extends Composite {
		public TagWidget(final Tag tag) {
			HorizontalPanel hp = new HorizontalPanel();
			hp.addStyleName("TagWidget");
			if (Application.getUserInterests().contains(tag)) {
				if (tag.getMappingType() != TagMappingType.USER || tag.getEntityId() != Application.getCurrentUserId()) {
					hp.addStyleDependentName("common");
				}
			}

			Label label = new Label(tag.getName());
			ClickLabel x = new ClickLabel("x", new MouseDownHandler() {
				@Override
				public void onMouseDown(MouseDownEvent event) {
					TagWidget.this.removeFromParent();
					tagService.deleteMapping(tag, new Callback<Void>(false) {
						@Override
						protected void doOnSuccess(Void result) {
							tags.remove(tag);
						}
					});
				}
			});
			x.addStyleName("tagX");

			hp.add(label);
			if (editingEnabled) {
				hp.add(x);
			}
			hp.setCellVerticalAlignment(x, HasVerticalAlignment.ALIGN_MIDDLE);
			initWidget(hp);
		}
	}

	private VerticalPanel vp = new VerticalPanel();
	private ArrayList<Tag> tags;
	private EntitySuggestBox suggestBox;
	private Button add = new Button("Add");
	private FlowPanel fp = new FlowPanel();
	private TagServiceAsync tagService = (TagServiceAsync) ServiceCache.getService(TagService.class);
	private TagMappingType mappingType;
	private int entityId;
	private final List<Character> allowedChars = new ArrayList<Character>();
	private boolean editingEnabled;
	private final static String MAX_MESSAGE = "You already have the maximum number of interests (" + Constants.maxInterests + ").";

	public TagSection(TagMappingType mappingType, int entityId, ArrayList<Tag> t) {
		this.tags = t;
		this.mappingType = mappingType;
		this.entityId = entityId;

		// populate();

		initWidget(vp);
	}

	public boolean isEditingEnabled() {
		return editingEnabled;
	}

	public void populate() {
		suggestBox = new EntitySuggestBox("Tag");
		suggestBox.getTextBox().setWidth("150px");
		suggestBox.getTextBox().getElement().setAttribute("maxlength", "25");
		allowedChars.addAll(ClientUtils.ALLOWED_KEY_CODES);
		allowedChars.add(new Character(' '));
		suggestBox.getTextBox().addKeyPressHandler(new KeyPressHandler() {
			@Override
			public void onKeyPress(KeyPressEvent event) {
				Character characterCode = (char) event.getNativeEvent().getCharCode();
				Character keyCode = (char) event.getNativeEvent().getKeyCode();

				if (!Character.isDigit(characterCode) && !Character.isLetter(characterCode) && !allowedChars.contains(characterCode)
						&& !allowedChars.contains(keyCode)) {
					event.preventDefault();
				}
			}
		});
		suggestBox.setSubmitWithoutSelectionCommand(new Command() {
			@Override
			public void execute() {
				addNewTag();
			}
		});
		suggestBox.setSelectionHandler(new ParameterHandler<Integer>() {
			@Override
			public void execute(Integer tagId) {
				Tag tag = new Tag();
				tag.setId(tagId);
				tag.setMappingType(mappingType);
				tag.setEntityId(entityId);
				suggestBox.reset();

				if (tags.contains(tag)) {
					return;
				}

				saveTagMapping(tag);
			}
		});
		suggestBox.setResetHandler(new ParameterHandler<SuggestBox>() {
			@Override
			public void execute(SuggestBox item) {
				suggestBox.setText("");
			}
		});
		suggestBox.setClearOnFocus(true);
		suggestBox.setAutoSelectEnabled(false);

		fp.setWidth("600px");
		vp.setSpacing(8);
		HorizontalPanel hp = new HorizontalPanel();
		add.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				addNewTag();
			}
		});
		hp.add(suggestBox);
		hp.add(add);
		hp.setCellVerticalAlignment(add, HasVerticalAlignment.ALIGN_MIDDLE);

		if (editingEnabled) {
			vp.add(hp);
		}

		vp.add(fp);

		for (Tag tag : tags) {
			fp.add(new TagWidget(tag));
		}
	}

	public void setEditingEnabled(boolean editingEnabled) {
		this.editingEnabled = editingEnabled;
	}

	private void addNewTag() {
		if (tags.size() >= Constants.maxInterests) {
			AlertDialog.alert(MAX_MESSAGE);
			return;
		}

		Tag tag = new Tag();
		tag.setName(suggestBox.getValue());
		tag.setMappingType(mappingType);
		tag.setEntityId(entityId);

		saveTagMapping(tag);
	}

	private void saveTagMapping(Tag tag) {
		if (tags.size() >= Constants.maxInterests) {
			AlertDialog.alert(MAX_MESSAGE);
			return;
		}

		tagService.addMapping(tag, new Callback<Tag>() {
			@Override
			protected void doOnSuccess(Tag result) {
				fp.add(new TagWidget(result));
				tags.add(result);
			}
		});
	}

}
