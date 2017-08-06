package com.cniao5.cniao5shop.utils;

import android.content.Context;
import android.text.TextUtils;

import com.cniao5.cniao5shop.bean.User;
import com.cniao5.cniao5shop.widget.Constants;

/**
 * 用户管理类
 */
public class UserLocalData {

    //存储用户数据
    public static void putUser(Context context,User user){
        String user_json = JSONUtil.toJson(user);
        PreferencesUtils.putString(context, Constants.USER_JSON,user_json);
    }

    //存储Token
    public static void putToken(Context context,String token){
        PreferencesUtils.putString(context, Constants.TOKEN,token);
    }

    //获取用户数据
    public static User getUser(Context context){
        String user_json = PreferencesUtils.getString(context,Constants.USER_JSON);
        if (!TextUtils.isEmpty(user_json)){
            return JSONUtil.fromJson(user_json,User.class);
        }
        return null;
    }

    //获取token
    public static String getToken(Context context){
        String token_json = PreferencesUtils.getString(context,Constants.TOKEN);
        return token_json;
    }

    //清除用户数据
    public static void clearUser(Context context){
        PreferencesUtils.putString(context,Constants.USER_JSON,"");
    }

    //清除token
    public static void clearToken(Context context){
        PreferencesUtils.putString(context,Constants.TOKEN,"");
    }
}
