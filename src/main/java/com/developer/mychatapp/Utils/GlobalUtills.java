package com.developer.mychatapp.Utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.util.Log;

public class GlobalUtills {

    private static volatile GlobalUtills instance = new GlobalUtills();

    public static GlobalUtills getInstance() {
        if (instance == null){ //if there is no instance available... create new one
            instance = new GlobalUtills();
        }
        return instance;
    }

    public boolean isNetworkConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }

    public void interntDialog(final Context context) {
        AlertDialog alertDialog = new AlertDialog.Builder(context).create();

        alertDialog.setTitle("NETWORK");
        alertDialog.setMessage("Internet not available, Cross check your internet connectivity and try again");
        alertDialog.setIcon(android.R.drawable.ic_dialog_alert);
        alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        alertDialog.show();
    }

}
