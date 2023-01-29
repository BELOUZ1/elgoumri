package com.app.elgoumri.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;


import com.app.elgoumri.R;

public class AlertDialogUtils {

    private final AlertDialog.Builder builder;
    private final Context context;

    public AlertDialogUtils(Context context) {
        this.context = context;
        builder = new AlertDialog.Builder(this.context);
        builder.setCancelable(false);
        builder.setNegativeButton("OK", (dialog, which) -> {
            dialog.cancel();
        });
    }

    public void showAlert(String title, String message){
        builder.setMessage(message);
        builder.setTitle(title);
        builder.setIcon(R.drawable.ic_pigeon_icon2);
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }


    public AlertDialog.Builder getBuilder() {
        return builder;
    }

}
