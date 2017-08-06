package com.cniao5.cniao5shop.http;

import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import com.cniao5.cniao5shop.MyApplication;
import com.cniao5.cniao5shop.utils.JSONUtil;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * OkHttpClient：客户端对象
 * Request：OkHttp中访问的请求
 * Builder：辅助类
 * Response：OkHttp的响应
 * MediaType:数据类型
 * RequestBody：请求数据 new FormEncodingBuilder()表单构造器
 *
 * 单例模式
 *
 * 1、创建OkHttpClient客户端对象
 * 2、GET请求：Request对象请求网络，传入url
 *    POST请求：RequestBody请求数据，构造post参数，Request对象请求网络，传入url和body
 * 3、发出网络响应，okHttpClient异步/同步请求网络
 *    okHttpClient.newCall(request).enqueue(new Callback())返回response对象
 */


public class OkHttpHelper {

    public static final int TOKEN_MISSING = 401;// token 丢失
    public static final int TOKEN_ERROR = 402; // token 错误
    public static final int TOKEN_EXPIRE = 403; // token 过期

    private OkHttpClient okHttpClient;

    private static OkHttpHelper mInstance;

    private Handler mHandler;

    private OkHttpHelper() {

        okHttpClient = new OkHttpClient();
        okHttpClient.setReadTimeout(10, TimeUnit.SECONDS);
        okHttpClient.setWriteTimeout(10, TimeUnit.SECONDS);
        okHttpClient.setConnectTimeout(10, TimeUnit.SECONDS);

        mHandler = new Handler(Looper.getMainLooper());
    }


    static {
        mInstance = new OkHttpHelper();
    }

    public static OkHttpHelper getInstance() {

        return mInstance;
    }

    public void doGet(String url, Map<String, String> params, BaseCallBack callback) {
        Request request = buildGetRequset(url, params);
        doRequest(request, callback);
    }

    public void doGet(String url, BaseCallBack callback) {
        doGet(url, null, callback);
    }

    public void doPost(String url, Map<String, String> params, BaseCallBack callback) {
        Request request = buildPostRequset(url, params);
        doRequest(request, callback);
    }

    /**
     * 网络请求
     * @param request
     * @param callback
     */
    public void doRequest(final Request request, final BaseCallBack callback) {

        callback.onRequestBefore(request);

        /**
         * 异步方法
         */
        okHttpClient.newCall(request).enqueue(new Callback() {

            /**
             * 请求网络时出现不可恢复的错误时调用
             * @param request
             * @param e
             */
            @Override
            public void onFailure(Request request, IOException e) {
//                callback.onFailure(request, e);
                callbackFailure(callback, request, e);
            }

            /**
             * 请求网络时成功时调用
             * @param response
             * @throws IOException
             */
            @Override
            public void onResponse(Response response) throws IOException {

//                callback.onResponse(response);
                callbackResponse(callback, response);

                //Http状态码大于200且小于300
                if (response.isSuccessful()) {

                    /**
                     * 获取json数据
                     */
                    String resultStr = response.body().string();

                    //判断是否为字符串
                    if (callback.mType == String.class) {
//                        callback.onSuccess(response, resultStr);
                        callbackSuccess(callback, response, resultStr);
                    } else {
                        try {
                            /**
                             * Gson：将json字符串转成type类型对象
                             */
                            Object object = JSONUtil.fromJson(resultStr,callback.mType);
//                            callback.onSuccess(response, object);
                            callbackSuccess(callback, response, object);
                        } catch (com.google.gson.JsonParseException e) {
                            callbackError(callback, response, response.code(), e);
//                            callback.onError(response, response.code(), e);
                        }
                    }


                } else if (response.code() == TOKEN_ERROR || response.code() == TOKEN_EXPIRE || response.code() == TOKEN_MISSING) {
                    /**
                     * token错误
                     */
                    callbackTokenError(callback, response);
                } else {
                    /**
                     * 请求网络错误回调
                     */
                    callbackError(callback, response, response.code(), null);
//                    callback.onError(response, response.code(), null);
                }

            }


        });
    }

    private Request buildPostRequset(String url, Map<String, String> params) {
        return buildRequset(url, params, HttpMethodType.POST);
    }

    private Request buildGetRequset(String url, Map<String, String> params) {
        return buildRequset(url, params, HttpMethodType.GET);
    }

    /**
     * 构建request对象
     * @param url 请求网络url
     * @param params 参数
     * @param type 请求方式 GET POST
     * @return
     */
    private Request buildRequset(String url, Map<String, String> params, HttpMethodType type) {

        Request.Builder builder = new Request.Builder().url(url);

        if (type == HttpMethodType.GET) {
            url = buildUrlParams(url, params);
            builder.url(url).get();
        } else if (type == HttpMethodType.POST) {

            RequestBody body = buildFormData(params);

            builder.post(body);
        }

        return builder.build();
    }

    /**
     * url参数构建
     * @param url
     * @param params
     * @return
     */
    private String buildUrlParams(String url, Map<String, String> params) {

        if (params == null)
            params = new HashMap<>(1);

        String token = MyApplication.getInstance().getToken();
        if (!TextUtils.isEmpty(token))
            params.put("token", token);

        StringBuffer sb = new StringBuffer();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            sb.append(entry.getKey() + "=" + entry.getValue());
            sb.append("&");
        }
        String s = sb.toString();

        //去掉最后一个&
        if (s.endsWith("&")) {
            s = s.substring(0, s.length() - 1);
        }

        //判断是否有参数
        if (url.indexOf("?") > 0) {
            url = url + "&" + s;
        } else {
            url = url + "?" + s;
        }
        return url;
    }

    /**
     * 构建RequestBody提交form表单
     * @param params
     * @return
     */
    private RequestBody buildFormData(Map<String, String> params) {

        FormEncodingBuilder builder = new FormEncodingBuilder();

        if (params != null) {

            for (Map.Entry<String, String> entry : params.entrySet()) {
                builder.add(entry.getKey(), entry.getValue() == null ? "" : entry.getValue().toString());
            }

            String token = MyApplication.getInstance().getToken();
            if (!TextUtils.isEmpty(token))
                builder.add("token", token);
        }

        return builder.build();
    }

    /**
     * callback回调：请求成功
     * @param callback
     * @param response
     * @param object
     */
    private void callbackSuccess(final BaseCallBack callback, final Response response, final Object object) {

        mHandler.post(new Runnable() {
            @Override
            public void run() {
                callback.onSuccess(response, object);
            }
        });
    }

    /**
     * callback回调：请求错误
     * @param callback
     * @param response
     * @param code 错误代码
     * @param e 异常
     */
    private void callbackError(final BaseCallBack callback, final Response response, final int code, final Exception e) {

        mHandler.post(new Runnable() {
            @Override
            public void run() {
                callback.onError(response, code, e);
            }
        });
    }

    /**
     * callback回调：请求失败
     * @param callback
     * @param request
     * @param e
     */
    private void callbackFailure(final BaseCallBack callback, final Request request, final IOException e) {

        mHandler.post(new Runnable() {
            @Override
            public void run() {
                callback.onFailure(request, e);
            }
        });
    }

    /**
     * callback回调：网络响应
     * @param callback
     * @param response
     */
    private void callbackResponse(final BaseCallBack callback, final Response response) {

        mHandler.post(new Runnable() {
            @Override
            public void run() {
                callback.onResponse(response);
            }
        });
    }

    /**
     * callback回调:Token错误
     * @param callback
     * @param response
     */
    private void callbackTokenError(final BaseCallBack callback, final Response response) {

        mHandler.post(new Runnable() {
            @Override
            public void run() {
                callback.onTokenError(response, response.code());
            }
        });
    }

    /**
     * 枚举
     */
    enum HttpMethodType {
        GET,
        POST
    }
}
