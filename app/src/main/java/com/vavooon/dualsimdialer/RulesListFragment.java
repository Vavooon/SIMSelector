package com.vavooon.dualsimdialer;

import android.app.ListFragment;
import android.content.Intent;
import android.database.DataSetObserver;
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
    CallRulesList rulesListInstance;
    CustomListViewAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);




        rulesListInstance = CallRulesList.getInstance();
        ArrayList rowItems = new ArrayList<RowItem>();

        for (int i = 0; i < rulesListInstance.size(); i++) {
            CallRule rule = rulesListInstance.get(i);
            RowItem item = new RowItem(i, R.mipmap.ic_menu_add, rule.ruleString);
            rowItems.add(item);
        }

        RowItem item = new RowItem(-1, R.mipmap.ic_menu_add, "Add", true);
        rowItems.add(item);

        adapter = new CustomListViewAdapter(getContext(),
                R.layout.list_layout, rowItems);
        setListAdapter(adapter);

    }


    public void updateList() {
        ArrayList rowItems = new ArrayList<RowItem>();

        for (int i = 0; i < rulesListInstance.size(); i++) {
            CallRule rule = rulesListInstance.get(i);
            RowItem item = new RowItem(i, R.mipmap.ic_menu_add, rule.ruleString);
            //Log.e("UpdateList addRowItem", ""+)
            rowItems.add(item);
        }
        RowItem item = new RowItem(-1, R.mipmap.ic_menu_add, "Add", true);
        rowItems.add(item);
        adapter.clear();
        adapter.addAll(rowItems);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        EditRuleDialogFragment dFragment = new EditRuleDialogFragment();
        Bundle args = new Bundle();
        args.putInt("id", v.getId());
        dFragment.setArguments(args);
        dFragment.setTargetFragment(this, 1);
        // Show DialogFragment
        dFragment.show(getFragmentManager(), "Dialog Fragment");
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        // Stuff to do, dependent on requestCode and resultCode
        if(requestCode == 1)  // 1 is an arbitrary number, can be any int
        {
            // This is the return result of your DialogFragment
            if(resultCode == 1) // 1 is an arbitrary number, can be any int
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
        RowItem rowItem = (RowItem)getListAdapter().getItem(info.position);
        Log.e("Con", "" + rowItem.getId());
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

            case R.id.delete: // <-- your custom menu item id here
                // do something here
                RowItem rowItem = (RowItem)getListAdapter().getItem(info.position);
                int id = rowItem.getId();
                if (id != -1) {
                    Log.e("removeRule", "" + id);
                    rulesListInstance.removeRule(id);
                    updateList();
                }
                return true;

            default:
                return super.onContextItemSelected(item);
        }
    }
}
