package com.vavooon.simselector;

import android.app.ListFragment;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.Icon;
import android.net.Uri;
import android.os.Bundle;
import android.telecom.PhoneAccountHandle;
import android.telecom.TelecomManager;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Vavooon on 13.10.2015.
 */
public class RulesListFragment extends ListFragment {
	CustomListViewAdapter adapter;


	private ArrayList<RowItem> createRowItems() {
		ArrayList rowItems = new ArrayList<>();
		ArrayList simNames = new ArrayList<>();
		ArrayList simIcons = new ArrayList<>();

		List<PhoneAccountHandle> availablePhoneAccountHandles;
		TelecomManager telecomManager =
			(TelecomManager) getActivity().getSystemService(Context.TELECOM_SERVICE);
		availablePhoneAccountHandles = telecomManager.getCallCapablePhoneAccounts();
		for (int i = 0; i < availablePhoneAccountHandles.size(); i++) {
			PhoneAccountHandle phoneAccountHandle = availablePhoneAccountHandles.get(i);
			simNames.add(telecomManager.getPhoneAccount(phoneAccountHandle).getLabel());
			simIcons.add(telecomManager.getPhoneAccount(phoneAccountHandle).getIcon());
		}

		Cursor cursor = getContext().getContentResolver().query(RulesContentProvider.CONTENT_URI, null, null, null, null);

		while (cursor.moveToNext()) {
			int id = cursor.getInt(cursor.getColumnIndex(RulesContentProvider.RULE_ID));
			int simId = cursor.getInt(cursor.getColumnIndex(RulesContentProvider.RULE_SIMID));
			String ruleString = cursor.getString(cursor.getColumnIndex(RulesContentProvider.RULE_TEXT));
			RowItem item = new RowItem(id, (Icon) simIcons.get(simId), simId, ruleString, simNames.get(simId).toString());
			rowItems.add(item);
		}

		Icon addIcon = Icon.createWithResource(getContext(), R.drawable.ic_add_24dp);
		RowItem item = new RowItem(-1, addIcon, -1, "Add", "SimName");
		rowItems.add(item);

		return rowItems;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (MainActivity.checkPermission(getContext())) {
			ArrayList rowItems = createRowItems();

			adapter = new CustomListViewAdapter(getContext(), R.layout.list_layout, rowItems);
			setListAdapter(adapter);
		} else {
			Toast.makeText(getContext(), "Phone permission allows us to access available SIM cards. Please allow in App Settings.", Toast.LENGTH_LONG).show();
		}
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
		if (requestCode == 1) {
			if (resultCode == 1) {
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
