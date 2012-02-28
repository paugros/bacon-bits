package com.areahomeschoolers.baconbits.client.util;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.TableElement;
import com.google.gwt.dom.client.TableSectionElement;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HTMLTable;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.UIObject;
import com.google.gwt.user.client.ui.Widget;

/**
 * A repository of useful static methods, constants, etc. for use only on the client side
 */
public abstract class ClientUtils {
	private static final Character[] PRIVATE_ALLOWED_KEY_CODES = { KeyCodes.KEY_BACKSPACE, KeyCodes.KEY_DOWN, KeyCodes.KEY_UP, KeyCodes.KEY_LEFT,
			KeyCodes.KEY_RIGHT, KeyCodes.KEY_END, KeyCodes.KEY_HOME, KeyCodes.KEY_ENTER, KeyCodes.KEY_ESCAPE, KeyCodes.KEY_DELETE, KeyCodes.KEY_SHIFT,
			KeyCodes.KEY_ALT, KeyCodes.KEY_CTRL, KeyCodes.KEY_TAB };
	public static final List<Character> ALLOWED_KEY_CODES = Collections.unmodifiableList(Arrays.asList(PRIVATE_ALLOWED_KEY_CODES));
	private static Map<String, String> linkers;

	public static String applyCodeTag(String buff) {
		if (buff == null) {
			return buff;
		}
		String ret = new String(buff);
		if (GWT.isScript()) {
			ret = injectCodeTag(ret);
		} else {
			ret = ret.replaceAll("(?=\\[pre\\])(.*?)\\[\\/pre\\]", "<pre>$1</pre>");
		}
		return ret;
	}

	/**
	 * @param buf
	 *            String to apply linkers to
	 * @return A new string, in which all linkers are applied
	 */
	public static String applyLinkers(String buf) {
		if (buf == null) {
			return buf;
		}
		String ret = new String(buf);

		for (String key : linkers.keySet()) {
			String url = linkers.get(key);
			if (GWT.isScript()) {
				ret = injectLinker(key, url, ret);
			} else {
				ret = ret.replaceAll("(?i)(" + key + ")-(\\d+)", "<a href=\"" + url + "\">$1-$2</a>");
			}
		}
		return ret;
	}

	// public static void downloadExcelFile(String title, String data) {
	// DocumentServiceAsync documentService = (DocumentServiceAsync) ServiceCache.getService(DocumentService.class);
	//
	// Document document = new Document();
	// document.setSystemDocument(true);
	// document.setStringData(data);
	// document.setFileExtension("csv");
	// document.setDescription("__TEMPORARY_CSV_FILE__");
	// document.setFileName(title + ".csv");
	// document.setFileType("application/vnd.ms-excel");
	// document.setAddedById(Application.getCurrentUser().getId());
	//
	// documentService.save(document, new Callback<Document>(false) {
	// @Override
	// protected void doOnSuccess(Document doc) {
	// String url = "/ribeye/service/file?id=" + doc.getId() + "&deleteAfterServing=1";
	// Window.Location.replace(url);
	// }
	// });
	// }

	// public static void exportCsvFile(String name, String fileData) {
	// DocumentServiceAsync documentService = (DocumentServiceAsync) ServiceCache.getService(DocumentService.class);
	//
	// Document document = new Document();
	// document.setSystemDocument(true);
	// document.setStringData(fileData);
	// document.setFileExtension("csv");
	// document.setDescription("__TEMPORARY_CSV_FILE__");
	// document.setFileName(name + ".csv");
	// document.setFileType("application/vnd.ms-excel");
	// document.setAddedById(Application.getCurrentUser().getId());
	//
	// documentService.save(document, new Callback<Document>(false) {
	// @Override
	// protected void doOnSuccess(Document doc) {
	// String url = "/ribeye/service/file?id=" + doc.getId() + "&deleteAfterServing=1";
	// Window.Location.replace(url);
	// }
	// });
	// }

	/**
	 * Returns a parent DOM element of the specified type.
	 * 
	 * @param el
	 *            A DOM element reference
	 * @param type
	 *            The HTML tag name of the parent to be retrieved
	 * @return A DOM element reference
	 */
	public static Element getParentElementByTagName(Element el, String type) {
		return getParentElementByTagName(el, type, 0);
	}

	/**
	 * Gets the parent Widget of type T to the specified Widget, or null if none found.
	 * 
	 * @param <T>
	 * @param w
	 * @param string
	 * @return
	 */
	public static Widget getParentWidgetByType(Widget w, String className) {
		while (w != RootPanel.get()) {
			w = w.getParent();
			if (w.getClass().toString().equals(className)) {
				return w;
			}
		}
		return null;
	}

	/**
	 * Returns true if the client browser is running on an iPhone or Android. Right now this will return false for iPads because they're big enough to be
	 * treated as browsers for screen layouts.
	 * 
	 * @return
	 */
	public static boolean isMobileBrowser() {
		String appVersion = Window.Navigator.getAppVersion();

		if (appVersion == null) {
			return false;
		}

		appVersion = appVersion.toLowerCase();

		if (appVersion.contains("ipad")) {
			return false;
		}

		return (appVersion.contains("iphone") || appVersion.contains("android"));
	}

	/**
	 * Removes the row from a {@link HTMLTable} in which a {@link UIObject} is located.
	 * 
	 * @param table
	 * @param child
	 */
	public static void removeParentRow(HTMLTable table, UIObject child) {
		Element currentElement = child.getElement();
		TableElement tableElement = (TableElement) Element.as(table.getElement());
		TableSectionElement tbody = tableElement.getTBodies().getItem(0);

		while (currentElement != null) {
			Element parent = currentElement.getParentElement();
			if (parent == tableElement || parent == tbody) {
				break;
			}
			currentElement = getParentElementByTagName(currentElement, "TR");
		}

		if (currentElement != null) {
			currentElement.removeFromParent();
		}
	}

	/**
	 * Called by Application on client initialization.
	 * 
	 * @param map
	 */
	public static void setLinkers(Map<String, String> map) {
		ClientUtils.linkers = map;
	}

	private static Element getParentElementByTagName(Element el, String type, int depth) {
		if (el == null) {
			return null;
		}
		type = type.toUpperCase();

		if (el.getTagName().toUpperCase().equals(type) && depth > 0) {
			return el;
		}
		return getParentElementByTagName(el.getParentElement(), type, ++depth);
	}

	private static native String injectCodeTag(String text) /*-{
		var pattern = new RegExp("\\[pre\\](.*?)(?:\\[\\/pre\\])", "gi");
		return text.replace(pattern, "<pre>$1</pre>");
	}-*/;

	private static native String injectLinker(String key, String url, String text) /*-{
		var pattern = new RegExp("(" + key + ")-(\\d+)", "gi");
		return text.replace(pattern, "<a href=\"" + url + "\">$1-$2</a>");
	}-*/;

	private ClientUtils() {
	}
}
