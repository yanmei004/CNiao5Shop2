package com.cniao5.cniao5shop.http;

import android.content.Context;

import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import java.io.IOException;

import dmax.dialog.SpotsDialog;

/**
 * 带加载框的Callback封装
 * @param <T>
 */
public abstract class SpotsCallBack<T> extends SimpleCallback<T> {

    private SpotsDialog dialog;

    public SpotsCallBack(Context context) {
        super(context);

        dialog = new SpotsDialog(context,"拼命加载中...");
    }

    private void showDialog() {
        dialog.show();
    }

    public void dismissDialog() {
        if (dialog != null)
            dialog.dismiss();
    }

    private void setMessage(String message) {
        dialog.setMessage(message);
    }

    @Override
    public void onRequestBefore(Request request) {
        showDialog();
    }

    @Override
    public void onFailure(Request request, IOException e) {
        dismissDialog();
    }

    @Override
    public void onResponse(Response response) {
        dismissDialog();
    }

}
