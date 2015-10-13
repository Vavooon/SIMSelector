package com.vavooon.dualsimdialer;

import android.app.ListFragment;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.preference.Preference;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.util.ArrayList;

/**
 * Created by Vavooon on 13.10.2015.
 */
public class RulesListFragment extends ListFragment{
    CallRulesList rulesListInstance;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);




        rulesListInstance = CallRulesList.getInstance();
        ArrayList rowItems = new ArrayList<RowItem>();

        for (int i = 0; i < rulesListInstance.size(); i++) {
            CallRule rule = rulesListInstance.get(i);
            RowItem item = new RowItem(R.mipmap.ic_menu_add, rule.ruleString);
            rowItems.add(item);
        }


        CustomListViewAdapter adapter = new CustomListViewAdapter(getContext(),
                R.layout.list_layout, rowItems);
        setListAdapter(adapter);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        // ваш ход господа
    }
}
