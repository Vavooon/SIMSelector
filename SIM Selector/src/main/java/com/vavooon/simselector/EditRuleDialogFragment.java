package com.vavooon.simselector;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.telecom.PhoneAccountHandle;
import android.telecom.TelecomManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Vavooon on 13.10.2015.
 */
public class EditRuleDialogFragment extends DialogFragment implements DialogInterface.OnClickListener {
	List<PhoneAccountHandle> availablePhoneAccountHandles;

	private static final String TAG = "EditRuleDialog";

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		final Bundle args = getArguments();
		// Get the layout inflater
		LayoutInflater inflater = getActivity().getLayoutInflater();

		final View v = inflater.inflate(R.layout.rule_edit_dialog, null);
		// Inflate and set the layout for the dialog
		// Pass null as the parent view because its going in the dialog layout
		builder.setView(v)
			// Add action buttons
			.setPositiveButton("Save", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int id) {
					EditText e = (EditText) v.findViewById(R.id.editText);
					Spinner simSelect = (Spinner) v.findViewById(R.id.spinner);
					int ruleId = args.getInt("id");
					if (ruleId == -1) {

						ContentValues cv = new ContentValues();
						cv.put(RulesContentProvider.RULE_SIMID, simSelect.getSelectedItemPosition());
						cv.put(RulesContentProvider.RULE_TEXT, e.getText().toString());
						Uri insertUri = getContext().getContentResolver().insert(RulesContentProvider.CONTENT_URI, cv);
						Log.d(TAG, insertUri.toString());
					} else {
						ContentValues cv = new ContentValues();
						cv.put(RulesContentProvider.RULE_SIMID, simSelect.getSelectedItemPosition());
						cv.put(RulesContentProvider.RULE_TEXT, e.getText().toString());
						Uri uri = ContentUris.withAppendedId(RulesContentProvider.CONTENT_URI, ruleId);
						getContext().getContentResolver().update(uri, cv, null, null);
					}
					getTargetFragment().onActivityResult(getTargetRequestCode(), 1, getActivity().getIntent());
				}
			})
			.setNegativeButton("Cancel", this);
		EditText e = (EditText) v.findViewById(R.id.editText);
		TextView title = (TextView) v.findViewById(R.id.textView1);
		Spinner simSelect = (Spinner) v.findViewById(R.id.spinner);


		List<String> simCards = new ArrayList<>();
		TelecomManager telecomManager =
			(TelecomManager) getActivity().getSystemService(Context.TELECOM_SERVICE);
		availablePhoneAccountHandles = telecomManager.getCallCapablePhoneAccounts();
		for (int i = 0; i < availablePhoneAccountHandles.size(); i++) {
			PhoneAccountHandle phoneAccountHandle = availablePhoneAccountHandles.get(i);
			simCards.add((String) telecomManager.getPhoneAccount(phoneAccountHandle).getLabel());
		}


		ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, simCards);
		simSelect.setAdapter(adapter);
		int id = args.getInt("id");
		Log.e("ID", "" + id);
		if (id != -1) {

			simSelect.setSelection(args.getInt("simId"));
			e.setText(args.getString("ruleString"));
			title.setText("Edit rule");
		} else {
			title.setText("Add rule");
		}
		return builder.create();
	}

	public void onClick(DialogInterface dialog, int id) {
		EditRuleDialogFragment.this.getDialog().cancel();
	}
}
