package com.areahomeschoolers.baconbits.client.widgets;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.areahomeschoolers.baconbits.shared.dto.GenericEntity;

import com.google.gwt.user.client.ui.SuggestOracle;

/**
 * A SuggestOracle that uses a server side source of suggestions. Instances of this class can not be shared between SuggestBox instances.
 */
public class ServerSuggestOracle extends SuggestOracle {

	/**
	 * Class to hold a response from the server.
	 */
	private static class ServerResponse {
		/**
		 * Request made by the SuggestBox.
		 */
		private final Request request;

		/**
		 * The number of suggestions the server was asked for
		 */
		private final int serverSuggestionsLimit;

		/**
		 * Suggestions returned by the server in response to the request.
		 */
		private final List<HtmlSuggestion> suggestions;

		/**
		 * Create a new instance.
		 * 
		 * @param request
		 *            Request from the SuggestBox.
		 * @param serverSuggestionsLimit
		 *            The number of suggestions we asked the server for.
		 * @param suggestions
		 *            The suggestions returned by the server.
		 */
		private ServerResponse(Request request, int serverSuggestionsLimit, List<HtmlSuggestion> suggestions) {
			this.request = request;
			this.serverSuggestionsLimit = serverSuggestionsLimit;
			this.suggestions = suggestions;
		}

		/**
		 * Filter the suggestions we got back from the server.
		 * 
		 * @param query
		 *            The query string.
		 * @param limit
		 *            The number of suggestions to return.
		 * @return The suggestions.
		 */
		public List<Suggestion> filter(String query, int limit) {
			List<Suggestion> newSuggestions = new ArrayList<Suggestion>(limit);
			String strQuery = query.replaceAll("[^0-9A-Za-z]+", "").toLowerCase();

			for (HtmlSuggestion suggestion : suggestions) {
				String strSuggest = suggestion.getReplacementString().replaceAll("[^0-9A-Za-z]+", "").toLowerCase();

				if (strSuggest.contains(strQuery)) {
					suggestion.setToken(query);
					newSuggestions.add(suggestion);
					if (newSuggestions.size() == limit) {
						break;
					}
				}
			}

			return newSuggestions;
		}

		/**
		 * Get the query string that was sent to the server.
		 * 
		 * @return The query.
		 */
		private String getQuery() {
			return request.getQuery();
		}

		/**
		 * Does the response include all possible suggestions for the query.
		 * 
		 * @return True or false.
		 */
		private boolean isComplete() {
			return suggestions.size() <= serverSuggestionsLimit;
		}
	}

	private boolean enabled = true;

	/**
	 * Number of suggestions to request from the server.
	 */
	// private static final int numberOfServerSuggestions = 200;

	/**
	 * The remote service that is the source of names.
	 */
	// private final SuggestServiceAsync namesService = (SuggestServiceAsync) ServiceCache.getService(SuggestService.class);

	/**
	 * Is there a request in progress
	 */
	private boolean requestInProgress = false;

	/**
	 * The most recent request made by the client.
	 */
	private Request mostRecentClientRequest = null;

	/**
	 * The most recent response from the server.
	 */
	private final ServerResponse mostRecentServerResponse = null;

	/**
	 * What kind of thing the suggestion is for.
	 */
	private String suggestType;

	// private GenericEntity options = new GenericEntity();

	/**
	 * Create a new instance.
	 * 
	 * @param suggestType
	 *            The kind of thing being suggested
	 */
	public ServerSuggestOracle(String suggestType) {
		this.suggestType = suggestType;
	}

	/**
	 * What kind of thing is being suggested?
	 */
	public String getSuggestType() {
		return suggestType;
	}

	@Override
	public boolean isDisplayStringHTML() {
		// we always want HTML
		return true;
	}

	public boolean isEnabled() {
		return enabled;
	}

	@Override
	public void requestSuggestions(final Request request, final Callback callback) {
		// Record this request as the most recent one.
		mostRecentClientRequest = request;
		// If there is not currently a request in progress return some suggestions. If there is a request in progress
		// suggestions will be returned when it completes.
		if (!requestInProgress) {
			returnSuggestions(callback);
		}
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public void setOptions(GenericEntity options) {
		if (options == null) {
			options = new GenericEntity();
		}
		// this.options = options;
	}

	/**
	 * Sets the kind of thing being suggested.
	 */
	public void setSuggestType(String suggestType) {
		this.suggestType = suggestType;
	}

	/**
	 * Send a request to the server.
	 * 
	 * @param request
	 *            The request.
	 * @param callback
	 *            The callback to call when the request returns.
	 */
	private void makeRequest(final Request request, final Callback callback) {
		if (!enabled) {
			return;
		}

		if (suggestType.equals("Contact")) {
			String queryToken = request.getQuery();
			for (char c : queryToken.toCharArray()) {
				if (!Character.isLetter(c)) {
					queryToken = queryToken.replaceAll("[^\\d]", "");
					request.setQuery(queryToken);
					break;
				}
			}
		}

		requestInProgress = true;
		// namesService.getSuggestions(request.getQuery(), suggestType, numberOfServerSuggestions, options, new AsyncCallback<ArrayList<ServerSuggestion>>() {
		// @Override
		// public void onFailure(Throwable caught) {
		// requestInProgress = false;
		// }
		//
		// @Override
		// public void onSuccess(ArrayList<ServerSuggestion> suggestions) {
		// String query = request.getQuery();
		// // convert ServerSuggestions (dto) to HtmlSuggestions (has highlighting capability)
		// List<HtmlSuggestion> htmlSuggestions = new ArrayList<HtmlSuggestion>();
		// for (ServerSuggestion suggestion : suggestions) {
		// htmlSuggestions.add(new HtmlSuggestion(suggestion, query));
		// }
		//
		// requestInProgress = false;
		// mostRecentServerResponse = new ServerResponse(request, numberOfServerSuggestions, htmlSuggestions);
		// ServerSuggestOracle.this.returnSuggestions(callback);
		// }
		// });
	}

	/**
	 * Return some suggestions to the SuggestBox. At this point we know that there is no call to the server currently in progress and we try to satisfy the
	 * request from the most recent results from the server before we call the server.
	 * 
	 * @param callback
	 *            The callback.
	 */
	private void returnSuggestions(Callback callback) {
		// For single character queries return an empty list.
		final String mostRecentQuery = mostRecentClientRequest.getQuery();
		if (mostRecentQuery.length() == 1) {
			callback.onSuggestionsReady(mostRecentClientRequest, new Response(Collections.<Suggestion> emptyList()));
			return;
		}
		// If we have a response from the server, and it includes all the possible suggestions for its request, and
		// that request is a superset of the request we're trying to satisfy now then use the server results, otherwise
		// ask the server for some suggestions.
		if (mostRecentServerResponse != null) {
			if (mostRecentQuery.equals(mostRecentServerResponse.getQuery())) {
				Response resp = new Response(mostRecentServerResponse.filter(mostRecentClientRequest.getQuery(), mostRecentClientRequest.getLimit()));
				callback.onSuggestionsReady(mostRecentClientRequest, resp);
			} else if (mostRecentServerResponse.isComplete() && mostRecentQuery.startsWith(mostRecentServerResponse.getQuery())) {
				Response resp = new Response(mostRecentServerResponse.filter(mostRecentClientRequest.getQuery(), mostRecentClientRequest.getLimit()));
				callback.onSuggestionsReady(mostRecentClientRequest, resp);
			} else {
				makeRequest(mostRecentClientRequest, callback);
			}
		} else {
			makeRequest(mostRecentClientRequest, callback);
		}
	}
}
