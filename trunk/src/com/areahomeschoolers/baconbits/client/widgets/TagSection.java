package com.areahomeschoolers.baconbits.client.widgets;

import java.util.ArrayList;

import com.areahomeschoolers.baconbits.client.ServiceCache;
import com.areahomeschoolers.baconbits.client.content.EntitySuggestBox;
import com.areahomeschoolers.baconbits.client.event.ParameterHandler;
import com.areahomeschoolers.baconbits.client.rpc.Callback;
import com.areahomeschoolers.baconbits.client.rpc.service.TagService;
import com.areahomeschoolers.baconbits.client.rpc.service.TagServiceAsync;
import com.areahomeschoolers.baconbits.shared.dto.Tag;
import com.areahomeschoolers.baconbits.shared.dto.Tag.TagMappingType;

import com.google.gwt.dom.client.Style.BorderStyle;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
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
			hp.getElement().getStyle().setBackgroundColor("#d2d6ec");
			hp.getElement().getStyle().setBorderStyle(BorderStyle.SOLID);
			hp.getElement().getStyle().setBorderWidth(1, Unit.PX);
			hp.getElement().getStyle().setBorderColor("#6974be");
			hp.getElement().getStyle().setColor("#2b3262");
			hp.getElement().getStyle().setMarginRight(5, Unit.PX);
			hp.getElement().getStyle().setMarginBottom(2, Unit.PX);
			hp.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
			Label label = new Label(tag.getName());
			label.getElement().getStyle().setPadding(4, Unit.PX);
			label.getElement().getStyle().setPaddingLeft(8, Unit.PX);
			ClickLabel x = new ClickLabel("x", new MouseDownHandler() {
				@Override
				public void onMouseDown(MouseDownEvent event) {
					TagWidget.this.removeFromParent();
					tagService.deleteMapping(tag, new Callback<Void>(false) {
						@Override
						protected void doOnSuccess(Void result) {
						}
					});
				}
			});
			x.getElement().getStyle().setColor("#444444");
			x.getElement().getStyle().setMarginRight(4, Unit.PX);
			x.getElement().getStyle().setPadding(4, Unit.PX);

			hp.add(label);
			hp.add(x);
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

	public TagSection(final TagMappingType mappingType, final int entityId, ArrayList<Tag> t) {
		this.tags = t;

		suggestBox = new EntitySuggestBox("Tag");
		suggestBox.getTextBox().setWidth("150px");
		suggestBox.setSelectionHandler(new ParameterHandler<Integer>() {
			@Override
			public void execute(Integer tagId) {
				Tag tag = new Tag();
				tag.setId(tagId);
				tag.setMappingType(mappingType);
				tag.setEntityId(entityId);
				suggestBox.reset();
				tagService.addMapping(tag, new Callback<Tag>() {
					@Override
					protected void doOnSuccess(Tag result) {
						fp.add(new TagWidget(result));
					}
				});
			}
		});
		suggestBox.setResetHandler(new ParameterHandler<SuggestBox>() {
			@Override
			public void execute(SuggestBox item) {
				suggestBox.setText("");
			}
		});
		suggestBox.setClearOnFocus(true);

		fp.setWidth("550px");
		vp.setSpacing(8);
		HorizontalPanel hp = new HorizontalPanel();
		add.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {

			}
		});
		hp.add(suggestBox);
		hp.add(add);
		vp.add(hp);
		vp.add(fp);

		for (Tag tag : tags) {
			fp.add(new TagWidget(tag));
		}

		initWidget(vp);
	}
}
