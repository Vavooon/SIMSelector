package com.vavooon.dualsimdialer;

import android.app.ListFragment;
import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.util.ArrayList;

/**
 * Created by Vavooon on 13.10.2015.
 */
public class RulesListFragment extends ListFragment {
	CustomListViewAdapter adapter;


	private ArrayList<RowItem> createRowItems () {
		ArrayList rowItems = new ArrayList<>();


		Cursor cursor = getContext().getContentResolver().query(RulesContentProvider.CONTENT_URI, null, null, null, null);

		while (cursor.moveToNext()) {
			int id = cursor.getInt(cursor.getColumnIndex(RulesContentProvider.RULE_ID));
			int simId = cursor.getInt(cursor.getColumnIndex(RulesContentProvider.RULE_SIMID));
			String ruleString = cursor.getString(cursor.getColumnIndex(RulesContentProvider.RULE_TEXT));
			RowItem item = new RowItem(id, R.mipmap.ic_menu_add, simId, ruleString);
			rowItems.add(item);
		}


		RowItem item = new RowItem(-1, R.mipmap.ic_menu_add, -1, "Add");
		rowItems.add(item);

		return rowItems;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		ArrayList rowItems = createRowItems();

		adapter = new CustomListViewAdapter(getContext(), R.layout.list_layout, rowItems);
		setListAdapter(adapter);
	}


	public void updateList() {
		ArrayList rowItems = createRowItems();
		adapter.clear();
		adapter.addAll(rowItems);
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		EditRuleDialogFragment dFragment = new EditRuleDialogFragment();
		Bundle args = new Bundle();
		RowItem row = (RowItem) v.getTag(R.string.app_name);
		args.putInt("id", row.getId());
		args.putInt("simId", row.getSimId());
		args.putString("ruleString", row.getRuleString());
		dFragment.setArguments(args);
		dFragment.setTargetFragment(this, 1);
		dFragment.show(getFragmentManager(), "Dialog Fragment");
	}


	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// Stuff to do, dependent on requestCode and resultCode
		if (requestCode == 1)  // 1 is an arbitrary number, can be any int
		{
			// This is the return result of your DialogFragment
			if (resultCode == 1) // 1 is an arbitrary number, can be any int
			{
				// Now do what you need to do after the dialog dismisses.
				Log.e("CB", "Callback was received");
				updateList();
			}
		}
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		registerForContextMenu(getListView());
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
		RowItem rowItem = (RowItem) getListAdapter().getItem(info.position);
		if (rowItem.getId() != -1) {
			MenuInflater inflater = this.getActivity().getMenuInflater();
			inflater.inflate(R.menu.rules_context_menu, menu);
			super.onCreateContextMenu(menu, v, menuInfo);
		}
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {

		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
		switch (item.getItemId()) {

			case R.id.delete:
				RowItem rowItem = (RowItem) getListAdapter().getItem(info.position);
				int id = rowItem.getId();
				if (id != -1) {
					Log.e("removeRule", "" + id);

					Uri uri = ContentUris.withAppendedId(RulesContentProvider.CONTENT_URI, id);
					getContext().getContentResolver().delete(uri, null, null);
					updateList();
				}
				return true;

			default:
				return super.onContextItemSelected(item);
		}
	}
}
