package com.areahomeschoolers.baconbits.client.widgets;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.areahomeschoolers.baconbits.client.HistoryToken;
import com.areahomeschoolers.baconbits.client.util.Url;
import com.areahomeschoolers.baconbits.shared.Common;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.logical.shared.BeforeSelectionEvent;
import com.google.gwt.event.logical.shared.BeforeSelectionHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.DecoratedTabPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * A {@link DecoratedTabPanel} that fires a {@link Command} when a tab is first selected (whether programmatically or by clicking) and only really selects the
 * tab when {@link #selectTabNow(Widget)} is invoked, usually from within the tab's {@link Command}.
 */
public class LazyDecoratedTabPanel extends DecoratedTabPanel {
	private final Set<Widget> loadedTabs = new HashSet<Widget>();
	private final Set<Widget> executedTabs = new HashSet<Widget>();
	private final Map<Widget, Command> tabCommands = new HashMap<Widget, Command>();
	private final Set<Widget> nonCachedTabs = new HashSet<Widget>();
	private boolean isInitialized = false;
	private Set<Integer> skipIndexes = new HashSet<Integer>();
	private int selectedTabIndex = 0;
	boolean progTabSelect = false;

	public LazyDecoratedTabPanel() {
		// the following ensures that our special selection code is run whether the user clicks a tab or it is selected programmatically
		addBeforeSelectionHandler(new BeforeSelectionHandler<Integer>() {
			@Override
			public void onBeforeSelection(BeforeSelectionEvent<Integer> event) {
				selectedTabIndex = event.getItem();

				if (!canSelectNow(getWidget(event.getItem()))) {
					if (!progTabSelect) {
						event.cancel();
					}
				}
			}
		});

		setWidth("100%");
		addStyleName("tabPanel");

		addSelectionHandler(new SelectionHandler<Integer>() {
			@Override
			public void onSelection(SelectionEvent<Integer> event) {
				if (isInitialized) {
					int index = event.getSelectedItem();
					for (int i : skipIndexes) {
						if (i <= index) {
							index++;
						}
					}
					setUrlIndex(index);
				}
			}
		});
	}

	public void add(Widget tabWidget, String tabText, boolean enableCaching, Command command) {
		if (!enableCaching) {
			nonCachedTabs.add(tabWidget);
		}
		add(tabWidget, tabText, command);
	}

	/**
	 * Adds a widget to the tab panel. If the Widget is already attached to the TabPanel, it will be moved to the right-most index.
	 * 
	 * @param tabWidget
	 *            The widget that goes in the tab's body
	 * @param tabText
	 *            The text on the tab itself
	 * @param command
	 *            The command to execute to populate the tab's body with content
	 */
	public void add(Widget tabWidget, String tabText, Command command) {
		// because we keep track of the tab by its widget, it doesn't matter if we rearrange the tabs after adding them
		tabCommands.put(tabWidget, command);
		add(tabWidget, tabText);

		int requestedTab = getUrlIndex();
		if (requestedTab == -1) {
			requestedTab = 0;
		}
		for (int i : skipIndexes) {
			if (i <= requestedTab) {
				requestedTab--;
			}
		}

		if (getWidgetCount() - 1 == requestedTab) {
			selectTab(requestedTab);
		}
	}

	public void addSkipIndex() {
		int widgetIndex = getWidgetCount();
		int skipCount = 0;
		for (Integer i : skipIndexes) {
			if (i <= widgetIndex) {
				skipCount++;
			}
		}
		addSkipIndex(widgetIndex + skipCount);
	}

	public void addSkipIndex(int index) {
		skipIndexes.add(index);
	}

	public void clearCache() {
		loadedTabs.clear();
		executedTabs.clear();
	}

	public void disableCaching(Widget tabWidget) {
		nonCachedTabs.add(tabWidget);
	}

	public void reloadCurrentTab() {
		reloadTab(selectedTabIndex);
	}

	public void reloadTab(int tabIndex) {
		tabCommands.get(getWidget(tabIndex)).execute();
	}

	// shortcut method so we can select tabs by their widgets
	/**
	 * Programmatically selects the specified tab. This does not actually select the tab, but rather fires the tab's {@link Command}.
	 * 
	 * @param tabWidget
	 *            The widget in the tab to be selected
	 */
	public void selectTab(Widget tabWidget) {
		selectTabNow(tabWidget, false);
	}

	public void selectTabNow(Widget tabWidget) {
		selectTabNow(tabWidget, true);
	}

	private boolean canSelectNow(Widget tabWidget) {
		// if the tab does not host a widget, let it be selected
		if (tabWidget == null) {
			return true;
		}

		Command cmd = tabCommands.get(tabWidget);

		// if it's been loaded, let it be selected
		if (loadedTabs.contains(tabWidget)) {
			return true;
		}

		// if it's not a command-loaded tab, let it be selected
		if (cmd == null) {
			return true;
		}

		// if not executed, then record it, execute it and do not allow the tab to be selected
		if (!executedTabs.contains(tabWidget)) {
			executedTabs.add(tabWidget);
			cmd.execute();
			return false;
		}
		// if executed but not loaded, then it's still loading, and the user will have to just wait
		return false;
	}

	// called when a tab has finished initializing
	/**
	 * Programmatically selects the specified tab. This method is intended to be invoked from within the tab's selection {@link Command}, which fires upon the
	 * first attempt to select.
	 * 
	 * @param tabWidget
	 *            The widget in the tab to be selected
	 */
	private void selectTabNow(final Widget tabWidget, boolean tabFinishedLoading) {
		if (tabFinishedLoading) {
			loadedTabs.add(tabWidget);
		} else {
			progTabSelect = true;
		}

		selectTab(getWidgetIndex(tabWidget));
		Scheduler.get().scheduleDeferred(new Command() {
			@Override
			public void execute() {
				isInitialized = true;
			}
		});

		if (tabFinishedLoading) {
			// TD only works when this isn't the first tab loaded
			if (nonCachedTabs.contains(tabWidget)) {
				loadedTabs.remove(tabWidget);
				executedTabs.remove(tabWidget);
			}
		} else {
			progTabSelect = false;
			loadedTabs.add(tabWidget);
		}
	}

	protected int getUrlIndex() {
		String tab = Url.getParam("tab");
		if (tab == null) {
			return 0;
		}

		if (!tab.contains("x")) {
			return Integer.parseInt(tab);
		}

		String mTab = tab.substring(0, tab.indexOf("x"));
		if (Common.isInteger(mTab)) {
			return Integer.parseInt(mTab);
		}

		return 0;
	}

	protected void setUrlIndex(int index) {
		HistoryToken.setElement("tab", Integer.toString(index), false);
	}
}
