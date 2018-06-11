package com.frshlypos.ui.activities;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.frshlypos.R;
import com.frshlypos.utils.SessionManager;

/**
 * Created by Akshay.Panchal on 11-Jul-17.
 */

public class BaseActivity extends AppCompatActivity {

    public SessionManager sessionManager;
    Snackbar snackbar;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sessionManager = new SessionManager(this);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getString(R.string.please_wait));
    }

    public void showSnackBar(View view, String message) {
        snackbar.make(view, message, 3000).show();
    }

    public void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    public void showProgressDialog(String messsage) {
        progressDialog.setMessage(messsage);
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    public void showProgressDialog() {
        showProgressDialog(getString(R.string.please_wait));
    }

    public void hideProgressDialog() {
        progressDialog.dismiss();
    }

    public void showInformationDialog(String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final AlertDialog dialog = builder.create();
        dialog.setMessage(msg);
        dialog.setButton(AlertDialog.BUTTON_POSITIVE, "Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        dialog.setCancelable(false);
        dialog.show();
    }

    @Override
    protected void onPause() {
        super.onPause();
        hideProgressDialog();
    }
}

