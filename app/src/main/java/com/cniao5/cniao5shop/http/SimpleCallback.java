package com.cniao5.cniao5shop.http;

import android.content.Context;
import android.content.Intent;

import com.cniao5.cniao5shop.activity.LoginActivity;
import com.cniao5.cniao5shop.MyApplication;
import com.cniao5.cniao5shop.R;
import com.cniao5.cniao5shop.utils.ToastUtils;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;

/**
 * 简单的Callback封装
 * @param <T>
 */
public abstract class SimpleCallback<T> extends BaseCallBack<T> {

    protected Context mContext;

    public SimpleCallback(Context context) {
        this.mContext = context;
    }

    @Override
    public void onRequestBefore(Request request) {

    }

    @Override
    public void onFailure(Request request, IOException e) {

    }

    @Override
    public void onResponse(Response response) {

    }


    @Override
    public void onTokenError(Response response, int code) {
        ToastUtils.show(mContext, mContext.getString(R.string.token_error));
        Intent intent = new Intent(mContext, LoginActivity.class);
        mContext.startActivity(intent);

        //清空本地用户数据
        MyApplication.getInstance().clearUser();

        System.out.println("token-------"+code);

    }
}
