package com.cniao5.cniao5shop.activity;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.cniao5.cniao5shop.MyApplication;
import com.cniao5.cniao5shop.R;
import com.cniao5.cniao5shop.bean.User;
import com.cniao5.cniao5shop.msg.LoginRespMsg;
import com.cniao5.cniao5shop.net.ServiceGenerator;
import com.cniao5.cniao5shop.net.SubscriberCallBack;
import com.cniao5.cniao5shop.utils.DESUtil;
import com.cniao5.cniao5shop.utils.ToastUtils;
import com.cniao5.cniao5shop.widget.ClearEditText;
import com.cniao5.cniao5shop.widget.Constants;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class LoginActivity extends BaseActivity {

    @ViewInject(R.id.et_phone)
    private ClearEditText mClearEtPhone;

    @ViewInject(R.id.et_pwd)
    private ClearEditText mClearEtPwd;

    @ViewInject(R.id.btn_login)
    private Button mBtnLogin;

    @ViewInject(R.id.tv_register)
    private TextView mTvReg;

    @ViewInject(R.id.tv_forget_pwd)
    private TextView mTvForget;


    @Override
    public int getLayoutId() {
        return R.layout.activity_login;
    }

    @Override
    public void init() {

    }

    @Override
    public void setToolbar() {
        getToolbar().setTitle("用户登录");
    }


    @OnClick(R.id.btn_login)
    public void login(View view) {
        String phone = mClearEtPhone.getText().toString().trim();
        String pwd = mClearEtPwd.getText().toString().trim();

        if (TextUtils.isEmpty(phone)) {
            ToastUtils.show(this, "请输入手机号码");
            return;
        }

        if (TextUtils.isEmpty(pwd)) {
            ToastUtils.show(this, "请输入登录密码");
            return;
        }

        ServiceGenerator.getRetrofit(this)
                .login(phone, DESUtil.encode(Constants.DES_KEY, pwd))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SubscriberCallBack<LoginRespMsg<User>>(this,false) {
                    @Override
                    public void onSuccess(LoginRespMsg<User> result) {
                        MyApplication application = MyApplication.getInstance();
                        application.putUser(result.getData(), result.getToken());
                        /**
                         * 根据登录意图判断是否已经登录
                         */
                        if (application.getIntent() == null && result.getData() != null && result.getToken() != null) {

                            setResult(Constants.REQUEST_CODE);

                            ToastUtils.show(LoginActivity.this, "登录成功");

                            finish();
                        } else {
                            ToastUtils.show(LoginActivity.this, "登录失败");
                        }
                    }
                });

    }

    /**
     * 跳转到注册页面
     *
     * @param v
     */
    @OnClick(R.id.tv_register)
    public void register(View v) {
        startActivityForResult(new Intent(this, RegisterActivity.class), 1);
//        startActivity(new Intent(this, RegisterActivity.class));
    }
}
