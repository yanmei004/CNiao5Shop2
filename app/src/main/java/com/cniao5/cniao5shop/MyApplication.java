package com.cniao5.cniao5shop;

import android.app.Application;
import android.content.Context;
import android.content.Intent;

import com.cniao5.cniao5shop.bean.User;
import com.cniao5.cniao5shop.utils.UserLocalData;
import com.facebook.drawee.backends.pipeline.Fresco;

/**
 * 单例模式
 * 软件运行时创建
 * 存储公共信息
 */
public class MyApplication extends Application {

    private static MyApplication mInstance;

    private User user;

    public static MyApplication getInstance() {
        if (mInstance == null)
            mInstance = new MyApplication();
        return mInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();

//        MultiDex.install(this);

        mInstance = this;
        //初始化用户信息
        initUser();
        //Fresco初始化
        Fresco.initialize(this);

    }

    private void initUser() {
        this.user = UserLocalData.getUser(this);
    }

    public User getUser() {
        return user;
    }

    /**
     * 获取token信息
     * @return
     */
    public String getToken() {
        return UserLocalData.getToken(this);
    }

    /**
     * 保存用户信息和token信息到本地
     * @param user
     * @param token
     */
    public void putUser(User user, String token) {
        this.user = user;
        UserLocalData.putUser(this, user);
        UserLocalData.putToken(this, token);
    }

    /**
     * 清空用户信息和token信息
     */
    public void clearUser() {
        this.user = null;
        UserLocalData.clearUser(this);
        UserLocalData.clearToken(this);
    }



    private Intent intent;

    /**
     * 保存登录意图
     */
    public void putIntent(Intent intent) {
        this.intent = intent;
    }

    /**
     * 获取登录意图
     * @return
     */
    public Intent getIntent() {
        return this.intent;
    }

    public void jumpToTargetActivity(Context context) {
        context.startActivity(intent);
        this.putIntent(null);
    }
}