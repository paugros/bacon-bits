package com.areahomeschoolers.baconbits.client.widgets;

import java.util.HashMap;
import java.util.Map;

import com.areahomeschoolers.baconbits.client.images.MainImageBundle;
import com.areahomeschoolers.baconbits.client.rpc.service.SuggestService;
import com.areahomeschoolers.baconbits.shared.dto.ServerSuggestion;

import com.google.gwt.dom.client.Style.VerticalAlign;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.SuggestOracle.Suggestion;

/**
 * An extension of Suggestion. This is needed because the {@link SuggestService} must return a serializable object.
 */
public class HtmlSuggestion implements Suggestion {
	private final String displayString;
	private String token;
	private final String entityType;
	private int entityId;
	private String stringId;
	private static HTML convertMe = new HTML();
	private static Map<String, Image> imageMap = new HashMap<String, Image>();
	private int fontSize;

	static {
		imageMap.put("User", new Image(MainImageBundle.INSTANCE.user()));
		imageMap.put("Event", new Image(MainImageBundle.INSTANCE.event()));
		imageMap.put("Article", new Image(MainImageBundle.INSTANCE.article()));
		imageMap.put("Book", new Image(MainImageBundle.INSTANCE.book()));
		imageMap.put("Resource", new Image(MainImageBundle.INSTANCE.resource()));
	}

	public HtmlSuggestion(ServerSuggestion suggestion, String token, int fontSize) {
		this.token = token;
		this.fontSize = fontSize;
		displayString = suggestion.getDisplayString();
		entityType = suggestion.getEntityType();
		stringId = suggestion.getStringId();
		setEntityId(suggestion.getId());
	}

	/**
	 * Gets the display string associated with this suggestion. The interpretation of the display string depends upon the value of its oracle's
	 * {@link com.google.gwt.user.client.ui.SuggestOracle#isDisplayStringHTML()}.
	 * 
	 * @return the display string for this suggestion
	 */
	@Override
	public String getDisplayString() {
		int index = 0;
		int cursor = 0;

		StringBuffer accum = new StringBuffer();
		if (imageMap.containsKey(entityType)) {
			Image image = imageMap.get(entityType);
			image.getElement().getStyle().setVerticalAlign(VerticalAlign.MIDDLE);
			accum.append(image + "&nbsp;");
		}

		String text = displayString;
		if (!text.contains("\n") && text.length() > 60) {
			text = text.substring(0, 60);
		}

		token = token.toLowerCase();
		index = text.toLowerCase().indexOf(token, index);

		if (index != -1) {
			int endIndex = index + token.length();
			String part1 = escapeText(text.substring(cursor, index));
			String part2 = escapeText(text.substring(index, endIndex));
			cursor = endIndex;
			accum.append(part1).append("<strong>").append(part2).append("</strong>");
		}

		String end = text.substring(cursor);
		accum.append(escapeText(end));

		String html = accum.toString();
		if (html.contains("\n")) {
			html = html.replaceAll("\\\n", "<br/>");
		}

		if (fontSize > 0) {
			html = "<div style=\"font-size: " + fontSize + "px;\">" + html + "</div>";
		}

		return html;
	}

	/**
	 * @return The id of the underlying suggested entity.
	 */
	public int getEntityId() {
		return entityId;
	}

	public String getEntityType() {
		return entityType;
	}

	/**
	 * Gets the replacement string associated with this suggestion. When this suggestion is selected, the replacement string will be entered into the
	 * SuggestBox's text box.
	 * 
	 * @return the string to be entered into the SuggestBox's text box when this suggestion is selected
	 */
	@Override
	public String getReplacementString() {
		return displayString;
	}

	public String getStringId() {
		return stringId;
	}

	/**
	 * Sets the id of the underlying suggested entity.
	 * 
	 * @param entityId
	 */
	public void setEntityId(int entityId) {
		this.entityId = entityId;
	}

	public void setToken(String token) {
		this.token = token;
	}

	private String escapeText(String escapeMe) {
		convertMe.setText(escapeMe);
		return convertMe.getHTML();
	}
}