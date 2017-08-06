package com.cniao5.cniao5shop.net;

import android.app.Activity;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by dali on 2017/8/4.
 */

public class ServiceGenerator {

    private static API api;
    private static Activity mContext;

    public static API getRetrofit(Activity context) {
        mContext = context;
        if (api == null) {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(API.BASE_URL)
                    .client(getOkHttpClient())
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                    .build();

            api = retrofit.create(API.class);
        }
        return api;
    }

    private static OkHttpClient getOkHttpClient() {
        OkHttpClient mOkHttpClient = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .addInterceptor(
                        new BaseInterceptor.Builder()
                                .addHeaderParamsMap(getHeaderMap())
                                .build())
                .build();

        return mOkHttpClient;
    }

    private static Map<String, String> getHeaderMap() {
        Map<String, String> map = new HashMap<>();
        map.put("Content-Type", "application/json; charset=UTF-8");
        map.put("Connection", "application/json; charset=UTF-8");
        map.put("Accept", "*/*");
        return map;
    }
}
