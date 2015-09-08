package com.pchsu.movieApp.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;

import com.pchsu.movieApp.R;


public class AlertDialogFragment extends DialogFragment{
    public static final String ERR_MSG_TAG = "ALERT DIALOG MSG";
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Context context = getActivity();
        String err_msg = getArguments().getString(ERR_MSG_TAG);
        AlertDialog.Builder builder = new AlertDialog.Builder(context)
                .setTitle(context.getString(R.string.error_title))
                .setMessage(err_msg)
                .setPositiveButton(context.getString(R.string.error_ok_button), null);
        AlertDialog dialog = builder.create();
        return dialog;
    }
}
