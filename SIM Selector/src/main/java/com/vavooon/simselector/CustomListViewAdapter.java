package com.vavooon.simselector;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Vavooon on 13.10.2015.
 */

public class CustomListViewAdapter extends ArrayAdapter<RowItem> {

	Context context;

	public CustomListViewAdapter(Context context, int resourceId,
															 List<RowItem> items) {
		super(context, resourceId, items);
		this.context = context;
	}

	/*private view holder class*/
	private class ViewHolder {
		ImageView imageView;
		TextView txtTitle;
		TextView txtSimName;
		TextView txtAdd;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		RowItem rowItem = getItem(position);

		LayoutInflater mInflater = (LayoutInflater) context
			.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.list_layout, null);
			holder = new ViewHolder();
			holder.txtTitle = (TextView) convertView.findViewById(R.id.rule);
			holder.txtSimName = (TextView) convertView.findViewById(R.id.simName);
			holder.txtAdd = (TextView) convertView.findViewById(R.id.add);
			holder.imageView = (ImageView) convertView.findViewById(R.id.icon);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		convertView.setTag(R.string.app_name, rowItem);
		holder.imageView.setImageIcon(rowItem.getIcon());

		if (rowItem.getId() != -1) {
			holder.txtSimName.setText(rowItem.getSimName());
			holder.txtTitle.setText(rowItem.getTitle());
			holder.txtAdd.setVisibility(View.INVISIBLE);
		} else {
			holder.txtSimName.setVisibility(View.INVISIBLE);
			holder.txtSimName.setHeight(0);
			holder.txtTitle.setVisibility(View.INVISIBLE);
			holder.txtTitle.setHeight(0);
		}

		return convertView;
	}
}