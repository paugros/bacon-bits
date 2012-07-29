package com.areahomeschoolers.baconbits.client.images;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

public interface MainImageBundle extends ClientBundle {
	MainImageBundle INSTANCE = GWT.create(MainImageBundle.class);

	ImageResource arrowLeft();

	ImageResource arrowRight();

	ImageResource cancel();

	ImageResource checkMark();

	ImageResource collapse();

	ImageResource edit();

	ImageResource expand();

	@Source("file/excel.png")
	ImageResource fileIconExcel();

	@Source("file/html.png")
	ImageResource fileIconHtml();

	@Source("file/image.png")
	ImageResource fileIconImage();

	@Source("file/pdf.png")
	ImageResource fileIconPdf();

	@Source("file/powerpoint.png")
	ImageResource fileIconPowerpoint();

	@Source("file/text.png")
	ImageResource fileIconText();

	@Source("file/unknown.png")
	ImageResource fileIconUnknown();

	@Source("file/visio.png")
	ImageResource fileIconVisio();

	@Source("file/word.png")
	ImageResource fileIconWord();

	@Source("file/zip.png")
	ImageResource fileIconZip();

	ImageResource logo();

	ImageResource mapShadow();

	ImageResource pixel();

	ImageResource refresh();

	ImageResource search();

	ImageResource sortAscending();

	ImageResource sortBlank();

	ImageResource sortDescending();

	ImageResource verticalDragHandle();

	ImageResource waggingFinger();

	ImageResource yellowWarning();

}
