package com.areahomeschoolers.baconbits.client.widgets.cellview;

public interface EntityCellTableColumn<C extends Enum<C> & EntityCellTableColumn<C>> {

	public enum GenericCellColumn implements EntityCellTableColumn<GenericCellColumn> {
		;

		@Override
		public String getTitle() {
			return "";
		}
	}

	public String getTitle();
}
