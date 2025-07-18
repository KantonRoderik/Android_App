package com.example.szakdolgozat.helpers;

import android.app.Activity;
import android.app.AlertDialog;
import android.view.LayoutInflater;

import com.example.szakdolgozat.R;

public class LoadingDialog {

    Activity activity;
    public AlertDialog dialog;

    public LoadingDialog(Activity myActivity){
        activity = myActivity;
    }


    public void startLoadingDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        LayoutInflater inflater = activity.getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.custom_dialog, null));
        builder.setCancelable(false);

        dialog = builder.create();
        dialog.show();

    }



}
