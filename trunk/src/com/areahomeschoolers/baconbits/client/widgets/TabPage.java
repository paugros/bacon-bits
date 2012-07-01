package com.areahomeschoolers.baconbits.client.widgets;

import com.areahomeschoolers.baconbits.client.util.WidgetFactory;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.DeckPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

public class TabPage extends LazyDecoratedTabPanel {
	public interface TabPageCommand {
		void execute(VerticalPanel tabBody);
	}

	private int spacing = WidgetFactory.PAGE_SPACING;

	public TabPage() {
		DeckPanel dp = getDeckPanel();
		dp.removeStyleName("gwt-TabPanelBottom");
		dp.addStyleName("borderlessTabBottom");
	}

	public VerticalPanel add(String tabText, boolean enableCaching, final TabPageCommand command) {
		final VerticalPanel vp = new VerticalPanel();
		vp.setWidth("100%");
		vp.setSpacing(spacing);

		Command cmd = new Command() {
			@Override
			public void execute() {
				command.execute(vp);
			}
		};

		super.add(vp, tabText, enableCaching, cmd);
		return vp;
	}

	public VerticalPanel add(String tabText, TabPageCommand command) {
		return this.add(tabText, true, command);
	}

	// public <T extends EntityDto<T>> NoteSection<T> addNoteSection(final NoteType type, final T entity) {
	// final NoteSection<T> noteSection = new NoteSection<T>(type, entity);
	// final NoteCellTable noteTable = noteSection.getCellTable();
	//
	// final VerticalPanel tabBody = add("Notes", new TabPageCommand() {
	// @Override
	// public void execute(final VerticalPanel tabBody) {
	// if (entity instanceof HasRequest) {
	// int requestId = ((HasRequest) entity).getRequestId();
	// if (requestId > 0) {
	// noteSection.mergeWith(NoteType.REQUEST, requestId);
	// }
	// }
	// if (!noteTable.hasBeenPopulated()) {
	// noteTable.populate();
	// }
	// tabBody.add(noteSection);
	// }
	// });
	//
	// noteTable.addDataReturnHandler(new DataReturnHandler() {
	// @Override
	// public void onDataReturn() {
	// selectTab(tabBody);
	// }
	// });
	//
	// NoteChangeAlertManager.startNoteChangePolling(noteSection, entity);
	//
	// return noteSection;
	// }

	public int getSpacing() {
		return spacing;
	}

	public void setSpacing(int spacing) {
		this.spacing = spacing;
	}
}
