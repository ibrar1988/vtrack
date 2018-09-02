package com.perigrine.Extras;

/**
 * Created by srikantht on 7/12/17.
 */


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import com.perigrine.Interfaces.AlertCallBack;

/**
 * Created by Srikanth Talasila
 * <p/>
 * This is the class Used to show alert dialog with OK and Cancel button. If you want you can pass Caback methods also ex: AlertCallBack
 */
public class AlertDialogClass {
    private Context context;
    private String message = "Alert";
    private String positive_name = "OK";
    private String negative_name = "Cancel";
    private boolean cancelable = true;
    private AlertCallBack alertCallback;
    private boolean isNegativeButtonRequired = true;

    public AlertDialogClass(Context context, String message,
                            String positive_button_name, String negative_button_name) {
        this.context = context;

        if (message != null && message.length() > 0) {
            this.message = message;
        }
        if (positive_button_name != null && positive_button_name.length() > 0) {
            this.positive_name = positive_button_name;
        }
        if (negative_button_name != null && negative_button_name.length() > 0) {
            this.negative_name = negative_button_name;
        }
    }

    public AlertDialogClass(Context context) {
        this.context = context;
    }

    public AlertDialogClass(Context context, String message,
                            AlertCallBack alertCallback) {
        this.context = context;
        this.alertCallback = alertCallback;
        if (message != null && message.length() > 0) {
            this.message = message;
        }
    }

    public AlertDialogClass(Context context, String message,
                            boolean isNegativeButtonRequired) {
        this.context = context;
        this.isNegativeButtonRequired = isNegativeButtonRequired;
        if (message != null && message.length() > 0) {
            this.message = message;
        }
    }

    public AlertDialogClass(Context context, String message,
                            boolean isNegativeButtonRequired, AlertCallBack alertCallback) {
        this.context = context;
        this.isNegativeButtonRequired = isNegativeButtonRequired;
        this.alertCallback = alertCallback;
        if (message != null && message.length() > 0) {
            this.message = message;
        }
    }

    public AlertDialogClass(Context context, String message,
                            String positive_button_name, boolean cancelable,
                            AlertCallBack alertCallback) {
        this.context = context;
        this.alertCallback = alertCallback;
        if (message != null && message.length() > 0) {
            this.message = message;
        }
        if (positive_button_name != null && positive_button_name.length() > 0) {
            this.positive_name = positive_button_name;
        }
        this.cancelable = cancelable;
    }

    public void showAlert() {
        positive_name = "OK";
        negative_name = "CANCEL";
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                context);
        alertDialogBuilder.setMessage(message);
        alertDialogBuilder.setCancelable(cancelable);
        alertDialogBuilder.setPositiveButton(positive_name,
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        if (alertCallback != null) {
                            alertCallback.positivte(true);
                        } else {
                        }
                        arg0.dismiss();
                    }
                });
        if (isNegativeButtonRequired) {
            alertDialogBuilder.setNegativeButton(negative_name,
                    new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
        }

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();

    }
}