package com.areahomeschoolers.baconbits.client.widgets;

import com.areahomeschoolers.baconbits.client.Application;
import com.areahomeschoolers.baconbits.client.content.document.FileUploadDialog;
import com.areahomeschoolers.baconbits.client.event.UploadCompleteHandler;
import com.areahomeschoolers.baconbits.client.images.richtext.RichTextImages;
import com.areahomeschoolers.baconbits.client.validation.Validator;
import com.areahomeschoolers.baconbits.client.validation.ValidatorCommand;
import com.areahomeschoolers.baconbits.shared.Common;
import com.areahomeschoolers.baconbits.shared.dto.Document;
import com.areahomeschoolers.baconbits.shared.dto.Document.DocumentLinkType;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Style.TextDecoration;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.RichTextArea;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.ToggleButton;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class ControlledRichTextArea extends Composite {

	protected class RichTextToolbar extends Composite {
		/**
		 * We use an inner EventHandler class to avoid exposing event methods on the RichTextToolbar itself.
		 */
		private class EventHandler implements ClickHandler, ChangeHandler, KeyUpHandler {

			@Override
			public void onChange(ChangeEvent event) {
				Widget sender = (Widget) event.getSource();

				if (sender == backColors) {
					formatter.setBackColor(backColors.getValue());
					backColors.setSelectedIndex(0);
				} else if (sender == foreColors) {
					formatter.setForeColor(foreColors.getValue());
					foreColors.setSelectedIndex(0);
				} else if (sender == fonts) {
					formatter.setFontName(fonts.getValue());
					fonts.setSelectedIndex(0);
				} else if (sender == fontSizes) {
					formatter.setFontSize(fontSizesConstants[fontSizes.getSelectedIndex() - 1]);
					fontSizes.setSelectedIndex(0);
				}
			}

			@Override
			public void onClick(ClickEvent event) {
				Widget sender = (Widget) event.getSource();

				if (sender == bold) {
					formatter.toggleBold();
				} else if (sender == italic) {
					formatter.toggleItalic();
				} else if (sender == underline) {
					formatter.toggleUnderline();
				} else if (sender == subscript) {
					formatter.toggleSubscript();
				} else if (sender == superscript) {
					formatter.toggleSuperscript();
				} else if (sender == strikethrough) {
					formatter.toggleStrikethrough();
				} else if (sender == indent) {
					formatter.rightIndent();
				} else if (sender == outdent) {
					formatter.leftIndent();
				} else if (sender == justifyLeft) {
					formatter.setJustification(RichTextArea.Justification.LEFT);
				} else if (sender == justifyCenter) {
					formatter.setJustification(RichTextArea.Justification.CENTER);
				} else if (sender == justifyRight) {
					formatter.setJustification(RichTextArea.Justification.RIGHT);
				} else if (sender == insertImage) {
					String url = Window.prompt("Enter an image URL:", "http://");
					if (url != null) {
						formatter.insertImage(url);
					}
				} else if (sender == uploadImage) {
					final FileUploadDialog uploadDialog = new FileUploadDialog(DocumentLinkType.HTML_IMAGE_INSERT, 0, false, new UploadCompleteHandler() {
						@Override
						public void onUploadComplete(int documentId) {
							formatter.insertImage("/baconbits/service/file?id=" + documentId);
						}
					});

					uploadDialog.getForm().addFormValidatorCommand(new ValidatorCommand() {
						@Override
						public void validate(Validator validator) {
							String fileName = uploadDialog.getFileName();
							if (Common.isNullOrBlank(fileName)) {
								validator.setError(true);
							}

							if (!Document.hasImageExtension(fileName)) {
								validator.setError(true);
								validator.setErrorMessage("Invalid image file.");
							}
						}
					});

					uploadDialog.center();
				} else if (sender == createLink) {
					String url = Window.prompt("Enter a link URL:", "http://");
					if (url != null) {
						formatter.createLink(url);
					}
				} else if (sender == removeLink) {
					formatter.removeLink();
				} else if (sender == hr) {
					formatter.insertHorizontalRule();
				} else if (sender == ol) {
					formatter.insertOrderedList();
				} else if (sender == ul) {
					formatter.insertUnorderedList();
				} else if (sender == removeFormat) {
					formatter.removeFormat();
				} else if (sender == insertLink) {
					HtmlInsertDialog dialog = new HtmlInsertDialog();
					dialog.center();
				} else if (sender == htmlLink) {
					HtmlEditDialog dialog = new HtmlEditDialog();
					dialog.center();
				} else if (sender == richText) {
					// We use the RichTextArea's onKeyUp event to update the toolbar status.
					// This will catch any cases where the user moves the cursor using the
					// keyboard, or uses one of the browser's built-in keyboard shortcuts.
					updateStatus();
				}
			}

			@Override
			public void onKeyUp(KeyUpEvent event) {
				Widget sender = (Widget) event.getSource();
				if (sender == richText) {
					// We use the RichTextArea's onKeyUp event to update the toolbar status.
					// This will catch any cases where the user moves the cursor using the
					// keyboard, or uses one of the browser's built-in keyboard shortcuts.
					updateStatus();
				}
			}
		}

		protected class HtmlEditDialog extends DefaultDialog {
			private TextArea textArea = new TextArea();
			private ButtonPanel bp = new ButtonPanel(this);
			private VerticalPanel vp = new VerticalPanel();

			public HtmlEditDialog() {
				setModal(true);
				setText("Edit HTML");

				vp.add(textArea);
				vp.add(bp);

				Button submit = new Button("Update", new ClickHandler() {
					@Override
					public void onClick(ClickEvent event) {
						richText.setHTML(textArea.getText());
					}
				});
				bp.addRightButton(submit);
				Button submitAndClose = new Button("Update and Close", new ClickHandler() {
					@Override
					public void onClick(ClickEvent event) {
						richText.setHTML(textArea.getText());
						hide();
					}
				});
				bp.addRightButton(submitAndClose);
				bp.getCloseButton().setText("Close");
				setWidget(vp);
			}

			@Override
			public void show() {
				textArea.setSize(richText.getOffsetWidth() + "px", richText.getOffsetHeight() + "px");
				textArea.setText(richText.getHTML());

				super.show();
			}
		}

		protected class HtmlInsertDialog extends DefaultDialog {
			private TextArea textArea = new TextArea();
			private ButtonPanel bp = new ButtonPanel(this);
			private VerticalPanel vp = new VerticalPanel();

			public HtmlInsertDialog() {
				setModal(true);
				setText("Insert HTML");
				textArea.setCharacterWidth(50);
				textArea.setVisibleLines(5);

				vp.add(textArea);
				vp.add(bp);

				Button submit = new Button("Insert", new ClickHandler() {
					@Override
					public void onClick(ClickEvent event) {
						richText.getFormatter().insertHTML(textArea.getText());
						hide();
					}
				});
				bp.addRightButton(submit);
				bp.getCloseButton().setText("Close");
				setWidget(vp);
			}

			@Override
			public void show() {
				super.show();
				Scheduler.get().scheduleDeferred(new ScheduledCommand() {
					@Override
					public void execute() {
						textArea.setFocus(true);
					}
				});
			}
		}

		private final RichTextArea.FontSize[] fontSizesConstants = new RichTextArea.FontSize[] { RichTextArea.FontSize.XX_SMALL, RichTextArea.FontSize.X_SMALL,
				RichTextArea.FontSize.SMALL, RichTextArea.FontSize.MEDIUM, RichTextArea.FontSize.LARGE, RichTextArea.FontSize.X_LARGE,
				RichTextArea.FontSize.XX_LARGE };

		private final RichTextImages images = (RichTextImages) GWT.create(RichTextImages.class);
		private final EventHandler handler = new EventHandler();

		private final RichTextArea richText;
		private final RichTextArea.Formatter formatter;

		private final VerticalPanel outer = new VerticalPanel();
		private final HorizontalPanel topPanel = new HorizontalPanel();
		private final HorizontalPanel bottomPanel = new HorizontalPanel();
		private ToggleButton bold;
		private ToggleButton italic;
		private ToggleButton underline;
		private ToggleButton subscript;
		private ToggleButton superscript;
		private ToggleButton strikethrough;
		private PushButton indent;
		private PushButton outdent;
		private PushButton justifyLeft;
		private PushButton justifyCenter;
		private PushButton justifyRight;
		private PushButton hr;
		private PushButton ol;
		private PushButton ul;
		private PushButton insertImage;
		private PushButton uploadImage;
		private PushButton createLink;
		private PushButton removeLink;
		private PushButton removeFormat;
		private ClickLabel htmlLink, insertLink;

		private DefaultListBox backColors;
		private DefaultListBox foreColors;
		private DefaultListBox fonts;
		private DefaultListBox fontSizes;

		/**
		 * Creates a new toolbar that drives the given rich text area.
		 * 
		 * @param richText
		 *            the rich text area to be controlled
		 */
		public RichTextToolbar(RichTextArea richText) {
			this.richText = richText;
			this.formatter = richText.getFormatter();

			outer.add(topPanel);
			outer.add(bottomPanel);
			topPanel.setWidth("100%");
			bottomPanel.setWidth("100%");

			initWidget(outer);
			setStyleName("gwt-RichTextToolbar");
			richText.addStyleName("hasRichTextToolbar");

			if (formatter != null) {
				topPanel.add(bold = createToggleButton(images.bold(), "Toggle Bold"));
				topPanel.add(italic = createToggleButton(images.italic(), "Toggle Italic"));
				topPanel.add(underline = createToggleButton(images.underline(), "Toggle Underline"));
				topPanel.add(subscript = createToggleButton(images.subscript(), "Toggle Subscript"));
				topPanel.add(superscript = createToggleButton(images.superscript(), "Toggle Superscript"));
				topPanel.add(justifyLeft = createPushButton(images.justifyLeft(), "Left Justify"));
				topPanel.add(justifyCenter = createPushButton(images.justifyCenter(), "Center"));
				topPanel.add(justifyRight = createPushButton(images.justifyRight(), "Right Justify"));
				topPanel.add(strikethrough = createToggleButton(images.strikeThrough(), "Toggle Strikethrough"));
				topPanel.add(indent = createPushButton(images.indent(), "Indent Right"));
				topPanel.add(outdent = createPushButton(images.outdent(), "Indent Left"));
				topPanel.add(hr = createPushButton(images.hr(), "Insert Horizontal Rule"));
				topPanel.add(ol = createPushButton(images.ol(), "Insert Ordered List"));
				topPanel.add(ul = createPushButton(images.ul(), "Insert Unordered List"));
				topPanel.add(insertImage = createPushButton(images.insertImage(), "Insert Image"));
				if (Application.isSystemAdministrator()) {
					topPanel.add(uploadImage = createPushButton(images.uploadImage(), "Upload Image"));
				}
				topPanel.add(createLink = createPushButton(images.createLink(), "Create Link"));
				topPanel.add(removeLink = createPushButton(images.removeLink(), "Remove Link"));
				topPanel.add(removeFormat = createPushButton(images.removeFormat(), "Remove Formatting"));
				if (Application.isSystemAdministrator()) {
					htmlLink = new ClickLabel("Edit HTML");
					htmlLink.setWordWrap(false);
					htmlLink.addClickHandler(handler);
					htmlLink.getElement().getStyle().setTextDecoration(TextDecoration.UNDERLINE);
					topPanel.add(htmlLink);
					insertLink = new ClickLabel("Insert HTML");
					insertLink.setWordWrap(false);
					insertLink.getElement().getStyle().setTextDecoration(TextDecoration.UNDERLINE);
					insertLink.addClickHandler(handler);
				}
				bottomPanel.add(backColors = createColorList("Background"));
				bottomPanel.add(foreColors = createColorList("Foreground"));
				bottomPanel.add(fonts = createFontList());
				bottomPanel.add(fontSizes = createFontSizes());
				if (Application.isSystemAdministrator()) {
					bottomPanel.add(insertLink);
				}

				// We only use these handlers for updating status, so don't hook them up
				// unless at least basic editing is supported.
				richText.addKeyUpHandler(handler);
				richText.addClickHandler(handler);
			}
		}

		private DefaultListBox createColorList(String caption) {
			DefaultListBox lb = new DefaultListBox();
			lb.addChangeHandler(handler);
			lb.setVisibleItemCount(1);

			lb.addItem(caption);
			lb.addItem("White", "white");
			lb.addItem("Black", "black");
			lb.addItem("Red", "red");
			lb.addItem("Green", "green");
			lb.addItem("Yellow", "yellow");
			lb.addItem("Blue", "blue");
			lb.addItem("None", "transparent");
			return lb;
		}

		private DefaultListBox createFontList() {
			DefaultListBox lb = new DefaultListBox();
			lb.addChangeHandler(handler);
			lb.setVisibleItemCount(1);

			lb.addItem("Font", "");
			lb.addItem("Normal", "");
			lb.addItem("Times New Roman", "Times New Roman");
			lb.addItem("Arial", "Arial");
			lb.addItem("Courier New", "Courier New");
			lb.addItem("Georgia", "Georgia");
			lb.addItem("Trebuchet", "Trebuchet");
			lb.addItem("Verdana", "Verdana");
			return lb;
		}

		private DefaultListBox createFontSizes() {
			DefaultListBox lb = new DefaultListBox();
			lb.addChangeHandler(handler);
			lb.setVisibleItemCount(1);

			lb.addItem("Size");
			lb.addItem("XX-Small");
			lb.addItem("X-Small");
			lb.addItem("Small");
			lb.addItem("Medium");
			lb.addItem("Large");
			lb.addItem("X-Large");
			lb.addItem("XX-Large");
			return lb;
		}

		private PushButton createPushButton(ImageResource img, String tip) {
			PushButton pb = new PushButton(new Image(img));
			pb.addClickHandler(handler);
			pb.setTitle(tip);
			return pb;
		}

		private ToggleButton createToggleButton(ImageResource img, String tip) {
			ToggleButton tb = new ToggleButton(new Image(img));
			tb.addClickHandler(handler);
			tb.setTitle(tip);
			return tb;
		}

		/**
		 * Updates the status of all the stateful buttons.
		 */
		private void updateStatus() {
			if (formatter != null) {
				bold.setDown(formatter.isBold());
				italic.setDown(formatter.isItalic());
				underline.setDown(formatter.isUnderlined());
				subscript.setDown(formatter.isSubscript());
				superscript.setDown(formatter.isSuperscript());
			}

			if (formatter != null) {
				strikethrough.setDown(formatter.isStrikethrough());
			}
		}
	}

	private Grid grid = new Grid(2, 1);
	private RichTextArea textArea = new RichTextArea();
	private RichTextToolbar toolbar = new RichTextToolbar(textArea);

	public ControlledRichTextArea() {
		initWidget(grid);
		grid.setWidget(0, 0, toolbar);
		grid.setWidget(1, 0, textArea);
		textArea.setWidth("800px");
		textArea.setHeight("600px");
		textArea.setStyleName("body");

		textArea.addFocusHandler(new FocusHandler() {
			@Override
			public void onFocus(FocusEvent event) {
				textArea.getFormatter().setFontName("Arial");
			}
		});

		addStyleName("ControlledRichTextArea");
	}

	public RichTextArea getTextArea() {
		return textArea;
	}

	public RichTextToolbar getToolbar() {
		return toolbar;
	}

	public void insertHtml(String html) {
		textArea.getFormatter().insertHTML(html);
	}

	public void setTextArea(RichTextArea textArea) {
		this.textArea = textArea;
	}

	public void setToolbar(RichTextToolbar toolbar) {
		this.toolbar = toolbar;
	}
}
