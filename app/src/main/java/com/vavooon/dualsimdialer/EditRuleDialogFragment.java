package com.vavooon.dualsimdialer;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.telecom.PhoneAccountHandle;
import android.telecom.TelecomManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Vavooon on 13.10.2015.
 */
public class EditRuleDialogFragment extends DialogFragment implements DialogInterface.OnClickListener {
    List<PhoneAccountHandle> availablePhoneAccountHandles;

//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        rulesListInstance = CallRulesList.getInstance();
//        Bundle args = getArguments();
//        getDialog().setTitle("Title!");
//        View v = inflater.inflate(R.layout.rule_edit_dialog, null);
//        EditText e = (EditText)v.findViewById(R.id.editText);
//        CallRule rule = rulesListInstance.get(args.getInt("id"));
//        e.setText(rule.ruleString);
//
//        Button cancelButton = (Button)v.findViewById(R.id.btnCancel);
//        cancelButton.setOnClickListener(this);
//        return v;
//    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        final Bundle args = getArguments();
        final CallRulesList rulesListInstance = CallRulesList.getInstance();
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
                        EditText e = (EditText)v.findViewById(R.id.editText);
                        Spinner simSelect = (Spinner)v.findViewById(R.id.spinner);
                        CallRule rule = new CallRule(simSelect.getSelectedItemPosition(), e.getText().toString());
                        int ruleId = args.getInt("id");
                        if (ruleId == -1) {
                            rulesListInstance.addRule(rule);
                        }
                        else {
                            rulesListInstance.updateRule(ruleId, rule);
                        }
                        getTargetFragment().onActivityResult(getTargetRequestCode(), 1, getActivity().getIntent());
                    }
                })
                .setNegativeButton("Cancel", this);
        EditText e = (EditText)v.findViewById(R.id.editText);
        TextView title = (TextView)v.findViewById(R.id.textView1);
        Spinner simSelect = (Spinner)v.findViewById(R.id.spinner);


        List<String> simCards = new ArrayList<String>();
        TelecomManager telecomManager =
                (TelecomManager) getActivity().getSystemService(Context.TELECOM_SERVICE);
        availablePhoneAccountHandles = telecomManager.getCallCapablePhoneAccounts();
        for (int i = 0; i < availablePhoneAccountHandles.size(); i++) {
            PhoneAccountHandle phoneAccountHandle = availablePhoneAccountHandles.get(i);
            simCards.add((String) telecomManager.getPhoneAccount(phoneAccountHandle).getLabel());
        }


        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_dropdown_item, simCards);
        simSelect.setAdapter(adapter);
        int id = args.getInt("id");
        Log.e("ID", ""+id);
        if (id != -1) {
            CallRule rule = rulesListInstance.get(id);
            simSelect.setSelection(rule.cardId);
            e.setText(rule.ruleString);
            title.setText("Edit rule");
        }
        else {
            title.setText("Add rule");
        }
        return builder.create();
    }
    public void onClick(DialogInterface dialog, int id) {
        EditRuleDialogFragment.this.getDialog().cancel();
    }
}
