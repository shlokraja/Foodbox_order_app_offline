package com.frshlypos.ui.fragments;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.View;

import com.frshlypos.R;
import com.frshlypos.utils.SessionManager;

/**
 * Created by Akshay.Panchal on 11-Jul-17.
 */

public class BaseFragment extends Fragment{

    SessionManager sessionManager;
    Snackbar snackbar;
    private ProgressDialog progressDialog;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sessionManager = new SessionManager(getContext());
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage(getString(R.string.please_wait));
    }

    public void showSnackBar(View view,String message){
        snackbar.make(view,message,3000).show();
    }

    public void showProgressDialog(String messsage){
        progressDialog.setMessage(messsage);
        progressDialog.show();
    }

    public void showProgressDialog(){
        showProgressDialog(getString(R.string.please_wait));
    }

    public void hideProgressDialog(){
        progressDialog.hide();
    }
}
