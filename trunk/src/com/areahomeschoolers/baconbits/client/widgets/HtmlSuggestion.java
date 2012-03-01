package com.areahomeschoolers.baconbits.client.widgets;

import com.areahomeschoolers.baconbits.shared.dto.ServerSuggestion;

import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.SuggestOracle.Suggestion;

/**
 * An extension of Suggestion. This is needed because the {@link SuggestService} must return a serializable object.
 */
public class HtmlSuggestion implements Suggestion {
	private final String displayString;
	private String token;
	private final String entityType;
	private int entityId;
	private static HTML convertMe = new HTML();

	public HtmlSuggestion(ServerSuggestion suggestion, String token) {
		this.token = token;
		this.displayString = suggestion.getDisplayString();
		entityType = suggestion.getEntityType();
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
		token = token.toLowerCase();
		index = displayString.toLowerCase().indexOf(token, index);

		if (index != -1) {
			int endIndex = index + token.length();
			String part1 = escapeText(displayString.substring(cursor, index));
			String part2 = escapeText(displayString.substring(index, endIndex));
			cursor = endIndex;
			accum.append(part1).append("<strong>").append(part2).append("</strong>");
		}

		String end = displayString.substring(cursor);
		accum.append(escapeText(end));

		return accum.toString();
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