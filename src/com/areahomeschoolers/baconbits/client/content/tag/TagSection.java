package com.areahomeschoolers.baconbits.client.content.tag;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.areahomeschoolers.baconbits.client.Application;
import com.areahomeschoolers.baconbits.client.ServiceCache;
import com.areahomeschoolers.baconbits.client.content.EntitySuggestBox;
import com.areahomeschoolers.baconbits.client.event.ParameterHandler;
import com.areahomeschoolers.baconbits.client.rpc.Callback;
import com.areahomeschoolers.baconbits.client.rpc.service.TagService;
import com.areahomeschoolers.baconbits.client.rpc.service.TagServiceAsync;
import com.areahomeschoolers.baconbits.client.util.ClientUtils;
import com.areahomeschoolers.baconbits.client.util.WidgetFactory.ContentWidth;
import com.areahomeschoolers.baconbits.client.validation.HasValidator;
import com.areahomeschoolers.baconbits.client.validation.Validator;
import com.areahomeschoolers.baconbits.client.validation.ValidatorCommand;
import com.areahomeschoolers.baconbits.client.widgets.AlertDialog;
import com.areahomeschoolers.baconbits.client.widgets.ButtonPanel;
import com.areahomeschoolers.baconbits.client.widgets.ClickLabel;
import com.areahomeschoolers.baconbits.client.widgets.DefaultDialog;
import com.areahomeschoolers.baconbits.client.widgets.Fader;
import com.areahomeschoolers.baconbits.client.widgets.HtmlSuggestion;
import com.areahomeschoolers.baconbits.client.widgets.MaxHeightScrollPanel;
import com.areahomeschoolers.baconbits.shared.dto.Arg.TagArg;
import com.areahomeschoolers.baconbits.shared.dto.ArgMap;
import com.areahomeschoolers.baconbits.shared.dto.Tag;
import com.areahomeschoolers.baconbits.shared.dto.Tag.TagType;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.VerticalPanel;

public class TagSection extends Composite implements HasValidator {
	private class TagSelectDialog extends DefaultDialog {
		private VerticalPanel vp = new VerticalPanel();
		private TagServiceAsync tagService = (TagServiceAsync) ServiceCache.getService(TagService.class);
		private List<Tag> allTags;

		public TagSelectDialog() {
			setText("View All Tags");
		}

		@Override
		public void show() {
			if (allTags == null) {
				ArgMap<TagArg> args = new ArgMap<TagArg>(TagArg.TYPE, mappingType.toString());
				args.put(TagArg.ENTITY_ID, entityId);
				tagService.list(new ArgMap<TagArg>(), new Callback<ArrayList<Tag>>() {
					@Override
					protected void doOnSuccess(ArrayList<Tag> result) {
						allTags = result;
						initialize();
						center();
					}
				});

				return;
			}

			super.show();
		}

		private void initialize() {
			removeStyleName("gwt-DialogBox .dialogMiddleCenter");
			MaxHeightScrollPanel sp = new MaxHeightScrollPanel();
			FlexTable ft = new FlexTable();
			ft.setWidth("600px");
			sp.setWidget(ft);
			VerticalPanel header = new VerticalPanel();
			Label title = new Label("All Tags / Interests");
			title.addStyleName("hugeText");
			String item = mappingType.toString().toLowerCase();
			String txt = "Select all that apply ";
			if (mappingType != TagType.USER) {
				txt += "to the " + item + " you were viewing";
			}
			Label sub = new Label(txt);
			sub.getElement().getStyle().setMarginLeft(10, Unit.PX);
			header.add(title);
			header.add(sub);
			header.getElement().getStyle().setPaddingBottom(15, Unit.PX);
			header.getElement().getStyle().setPaddingLeft(15, Unit.PX);
			header.getElement().getStyle().setBackgroundColor(mappingType.getColor());
			header.setWidth("100%");
			vp.add(header);

			allTags.removeAll(tags);
			allTags.addAll(tags);

			ft.setCellPadding(3);

			int cols = 3;

			for (int i = 0; i < allTags.size(); i++) {
				final Tag tag = allTags.get(i);
				final CheckBox cb = new CheckBox(tag.getName());
				if (tag.getMappingId() > 0) {
					cb.setValue(true);
				}

				cb.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
					@Override
					public void onValueChange(ValueChangeEvent<Boolean> event) {
						if (event.getValue()) {
							tag.setMappingType(mappingType);
							tag.setEntityId(entityId);
							if (!addTag(tag)) {
								cb.setValue(false, false);
							}
						} else {
							removeTag(tag);
						}
					}
				});

				if (i % cols == 0) {
					ft.insertRow(ft.getRowCount());
				}

				int row = ft.getRowCount() - 1;
				int cell = ft.getCellCount(row);

				ft.setWidget(row, cell, cb);
			}

			vp.add(sp);

			vp.add(new ButtonPanel(this));

			setWidget(vp);
		}
	}

	private class TagWidget extends Composite {
		public TagWidget(final Tag tag) {
			HorizontalPanel hp = new HorizontalPanel();
			hp.addStyleName("TagWidget");
			if (Application.getUserInterests().contains(tag)) {
				if (tag.getMappingType() != TagType.USER || tag.getEntityId() != Application.getCurrentUserId()) {
					hp.addStyleDependentName("common");
				}
			}

			Label label = new Label(tag.getName());
			ClickLabel x = new ClickLabel("x", new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					removeTag(tag);
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
	private ArrayList<Tag> tags = new ArrayList<>();
	private EntitySuggestBox suggestBox;
	private Button add = new Button("Add");
	private FlowPanel fp = new FlowPanel();
	private TagServiceAsync tagService = (TagServiceAsync) ServiceCache.getService(TagService.class);
	private TagType mappingType;
	private int entityId;
	private final List<Character> allowedChars = new ArrayList<Character>();
	private boolean editingEnabled;
	private HorizontalPanel hp = new HorizontalPanel();
	private Validator validator;
	private Map<Tag, TagWidget> tagMap = new HashMap<>();

	public TagSection(TagType type, int itemId) {
		this.mappingType = type;
		this.entityId = itemId;

		suggestBox = new EntitySuggestBox("Tag");
		suggestBox.getTextBox().setWidth("150px");
		suggestBox.getTextBox().getElement().setAttribute("maxlength", "25");
		// disable until we have our data
		suggestBox.getTextBox().setEnabled(true);
		allowedChars.addAll(ClientUtils.ALLOWED_KEY_CODES);
		allowedChars.add(new Character(' '));
		allowedChars.add(new Character('-'));
		allowedChars.add(new Character('&'));
		allowedChars.add(new Character('$'));
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
				createNewTag();
			}
		});
		suggestBox.setSelectionHandler(new ParameterHandler<HtmlSuggestion>() {
			@Override
			public void execute(HtmlSuggestion sug) {
				Tag tag = new Tag();
				tag.setId(sug.getEntityId());
				tag.setName(sug.getReplacementString());
				tag.setMappingType(mappingType);
				tag.setEntityId(entityId);
				suggestBox.reset();

				if (tags.contains(tag)) {
					return;
				}

				addTag(tag);
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

		fp.addStyleName(ContentWidth.MAXWIDTH600PX.toString());
		vp.setSpacing(8);

		add.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				createNewTag();
			}
		});
		hp.add(suggestBox);
		hp.add(add);
		hp.setCellVerticalAlignment(add, HasVerticalAlignment.ALIGN_MIDDLE);

		String item = "tags";
		if (mappingType == TagType.USER) {
			item = "interests";
		}
		ClickLabel link = new ClickLabel("Browse " + item, new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				TagSelectDialog dialog = new TagSelectDialog();
				dialog.center();
			}
		});
		link.getElement().getStyle().setMarginLeft(15, Unit.PX);
		hp.add(link);

		vp.add(hp);

		validator = new Validator(suggestBox.getTextBox(), new ValidatorCommand() {
			@Override
			public void validate(Validator validator) {
				if (validator.isRequired()) {
					validator.setError(tags.size() == 0);
				}
			}
		});

		if (editingEnabled) {
			String helpText = "We'll put an image on your item based on the first tag you add so make sure the first tag best describes what your item is. ";
			helpText += "You can add up to 3 additional informative tags.";
			Label help = new Label(helpText);
			help.getElement().getStyle().setColor("#555555");
			help.getElement().getStyle().setWidth(500, Unit.PX);
			vp.add(help);
		}

		vp.add(fp);
		initWidget(vp);
	}

	@Override
	public Validator getValidator() {
		return validator;
	}

	@Override
	public boolean isRequired() {
		if (validator == null) {
			return false;
		}
		return validator.isRequired();
	}

	public void populate() {
		if (entityId > 0) {
			ArgMap<TagArg> args = new ArgMap<TagArg>(TagArg.ENTITY_ID, entityId);
			args.put(TagArg.TYPE, mappingType.toString());
			tagService.list(args, new Callback<ArrayList<Tag>>() {
				@Override
				protected void doOnSuccess(ArrayList<Tag> result) {
					populate(result);
				}
			});
		} else {
			populate(tags);
		}
	}

	public void populate(ArrayList<Tag> tags) {
		if (tags == null) {
			tags = new ArrayList<>();
		}

		this.tags = tags;
		fp.clear();
		for (Tag tag : tags) {
			TagWidget tw = new TagWidget(tag);
			tagMap.put(tag, tw);
			fp.add(tw);
		}

		suggestBox.getTextBox().setEnabled(true);
	}

	public void saveAll(int entityId, Callback<Void> callback) {
		tagService.addMappings(entityId, tags, callback);
	}

	public void setEditingEnabled(boolean editingEnabled) {
		this.editingEnabled = editingEnabled;
		if (!editingEnabled) {
			hp.removeFromParent();
		} else if (!hp.isAttached()) {
			vp.insert(hp, 0);
		}
	}

	@Override
	public void setRequired(boolean required) {
		if (validator == null) {
			return;
		}
		validator.setRequired(required);
	}

	private boolean addTag(Tag tag) {
		if (!Application.isSystemAdministrator()) {
			int max = 4;
			if (mappingType.equals(TagType.USER)) {
				max = 50;
			}

			if (tags.size() >= max) {
				String thing = mappingType.equals(TagType.USER) ? "interests" : "tags";
				String message = "Sorry, no more than " + max + " " + thing + ".";
				AlertDialog.alert(message);
				return false;
			}
		}

		if (tag.getEntityId() == 0 && tag.isSaved()) {
			addTagToPanel(tag);
			return true;
		}

		tagService.addMapping(tag, new Callback<Tag>() {
			@Override
			protected void doOnSuccess(Tag result) {
				addTagToPanel(result);
			}
		});

		return true;
	}

	private void addTagToPanel(Tag tag) {
		TagWidget tw = new TagWidget(tag);
		fp.add(tw);
		Fader fader = new Fader(tw);
		fader.fadeIn();
		tags.add(tag);
		tagMap.put(tag, tw);
	}

	private void createNewTag() {
		if (suggestBox.getValue().trim().isEmpty()) {
			return;
		}

		Tag tag = new Tag();
		tag.setName(suggestBox.getValue());
		tag.setMappingType(mappingType);
		tag.setEntityId(entityId);

		addTag(tag);
	}

	private void removeTag(final Tag tag) {
		if (EnumSet.of(TagType.EVENT, TagType.BOOK, TagType.RESOURCE).contains(mappingType) && tags.size() == 1) {
			AlertDialog.alert("At least one tag is required.");
			return;
		}

		final TagWidget tw = tagMap.get(tag);
		Fader f = new Fader(tw);
		f.setCommandDelay(250);
		f.fadeOut(new Command() {
			@Override
			public void execute() {
				tw.removeFromParent();
				tagMap.remove(tag);
				tags.remove(tag);
			}
		});

		tagService.deleteMapping(tag, new Callback<Void>(false) {
			@Override
			protected void doOnSuccess(Void result) {
			}
		});
	}

}
