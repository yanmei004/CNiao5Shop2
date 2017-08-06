package com.cniao5.cniao5shop.http;

import com.google.gson.internal.$Gson$Types;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * Callback封装
 * @param <T>
 */
public abstract class BaseCallBack<T> {

    public Type mType;

    /**
     * 将T对象转换成type类型
     * @param subclass
     * @return
     */
    static Type getSuperclassTypeParamter(Class<?> subclass){
        Type superclass = subclass.getGenericSuperclass();

        if (superclass instanceof Class){
            throw new RuntimeException("Missing type paramter.");
        }

        ParameterizedType parameterized = (ParameterizedType) superclass;
        return $Gson$Types.canonicalize(parameterized.getActualTypeArguments()[0]);
    }

    public BaseCallBack(){
        mType = getSuperclassTypeParamter(getClass());
    }

    /**
     * 请求数据之前调用此方法
     * @param request
     */
    public abstract void onRequestBefore(Request request);

    /**
     * 请求失败调用此方法
     * @param request
     * @param e
     */
    public abstract void onFailure(Request request, IOException e);

    /**
     *请求成功时调用此方法
     * @param response
     */
    public abstract void onResponse(Response response);

    /**
     * 状态码大于200，小于300 时调用此方法
     * @param response
     * @param t
     * @throws IOException
     */
    public abstract void onSuccess(Response response,T t);

    /**
     * 状态码400，404，403，500等时调用此方法
     * @param response
     * @param code
     * @param e
     */
    public abstract void onError(Response response,int code,Exception e);

    /**
     * Token 验证失败。状态码401,402,403 等时调用此方法
     * @param response
     * @param code
     */
    public abstract void onTokenError(Response response, int code);
}
