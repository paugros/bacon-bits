package com.areahomeschoolers.baconbits.client.widgets.cellview;

import java.util.ArrayList;

import com.areahomeschoolers.baconbits.client.rpc.Callback;
import com.areahomeschoolers.baconbits.client.widgets.cellview.EntityCellTableColumn.GenericCellColumn;
import com.areahomeschoolers.baconbits.shared.dto.Arg;
import com.areahomeschoolers.baconbits.shared.dto.Data;
import com.areahomeschoolers.baconbits.shared.dto.GenList;

public abstract class GenericCellTable extends EntityCellTable<Data, Arg, GenericCellColumn> {

	private Callback<GenList> genListCallback;

	/**
	 * @return A {@link Callback} for use with fetching data.
	 */
	public Callback<GenList> getGenListCallback() {
		final Callback<ArrayList<Data>> callback = getCallback();

		if (genListCallback == null) {
			genListCallback = new Callback<GenList>() {
				@Override
				protected void doOnSuccess(GenList result) {
					callback.onSuccess(result.getListData());
				}
			};
		}

		return genListCallback;
	}
}
