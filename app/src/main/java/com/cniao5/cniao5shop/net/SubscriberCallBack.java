package com.cniao5.cniao5shop.net;

import android.app.Activity;
import android.content.Intent;

import com.cniao5.cniao5shop.MyApplication;
import com.cniao5.cniao5shop.R;
import com.cniao5.cniao5shop.activity.LoginActivity;
import com.cniao5.cniao5shop.bean.User;
import com.cniao5.cniao5shop.utils.ToastUtils;
import com.cniao5.cniao5shop.widget.Constants;
import com.lidroid.xutils.util.LogUtils;

import dmax.dialog.SpotsDialog;
import rx.Subscriber;

/**
 * Created by dali on 2017/8/4.
 */

public abstract class SubscriberCallBack<T> extends Subscriber<T> {

    public static final String TOKEN_MISSING = "401";// token 丢失
    public static final String TOKEN_ERROR = "402"; // token 错误
    public static final String TOKEN_EXPIRE = "403"; // token 过期
    public static final String TOKEN_NOT_ALLOWED = "405"; // token 不允许

    private SpotsDialog mDialog;
    private Activity mContext;

    private boolean isShowDialog = false;

    public SubscriberCallBack(Activity context) {
        mContext = context;
    }

    public SubscriberCallBack(Activity context, boolean isShowDialog) {
        this.mContext = context;
        this.isShowDialog = isShowDialog;

        if (isShowDialog) {
            mDialog = new SpotsDialog(mContext, "拼命加载中");
        }
    }

    public abstract void onSuccess(T result);

    @Override
    public void onStart() {
        super.onStart();
        if (mDialog != null)
            mDialog.show();
    }

    @Override
    public void onCompleted() {
        if (mDialog != null) {
            mDialog.dismiss();
        }
    }

    @Override
    public void onError(Throwable e) {
        if (mDialog != null)
            mDialog.dismiss();

        if (e != null) {
            if (e.getMessage().contains(TOKEN_MISSING) || e.getMessage().contains(TOKEN_EXPIRE)
                    || e.getMessage().contains(TOKEN_ERROR) || e.getMessage().contains(TOKEN_NOT_ALLOWED)) {
                if (mContext != null) {
                    ToastUtils.show(mContext, mContext.getString(R.string.token_error));
                    mContext.startActivityForResult(new Intent(mContext, LoginActivity.class), Constants.REQUEST_CODE);
                    //清空本地用户数据
                    MyApplication.getInstance().clearUser();
                }
            }
            LogUtils.i("net error" + e.getMessage());
        } else {
            LogUtils.i("错误");
        }
    }

    @Override
    public void onNext(T result) {
        try {
            if (result instanceof User && result.toString().contains("账户或者密码错误")) {
                ToastUtils.show(mContext, "账户或者密码错误");
            } else {
                onSuccess(result);
            }
        } catch (Exception e) {
            onError(e);
        }

    }
}
