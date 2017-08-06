package com.cniao5.cniao5shop.utils;

import android.content.Context;
import android.util.SparseArray;

import com.cniao5.cniao5shop.bean.ShoppingCart;
import com.cniao5.cniao5shop.bean.Wares;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

/**
 * 购物车管理类
 */
public class CartProvider {

    /**
     * SparseArray<ShoppingCart>存放购物车数据key-value值
     * SharedPreferences将购物车数据存入本地
     */
    private SparseArray<ShoppingCart> datas = null;
    private static Context mContext;
    public static final String CART_JSON = "cart_json";

    private static CartProvider mInstance;

    public static CartProvider getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new CartProvider(context);
        }
        return mInstance;
    }

    private CartProvider(Context context) {
        this.mContext = context;

        datas = new SparseArray<>(10);

        listToSparse();
    }

    //存储SparseArray<ShoppingCart>数据，同时更新SharedPreferences的数据到本地
    public void put(ShoppingCart cart) {

        //intValue():long类型id强制转换成int
        ShoppingCart temp = datas.get(cart.getId().intValue());

        if (temp != null) {
            temp.setCount(temp.getCount() + 1);
        } else {
            temp = cart;
            temp.setCount(1);
        }

        //将数据保存在SparseArray中
        datas.put(cart.getId().intValue(), temp);

        //将SparseArray<ShoppingCart>数据转换成List<ShoppingCart>数据保存在SharedPreferences中
        commit();
    }

    public void put(Wares wares) {
        ShoppingCart cart = convertData(wares);

        put(cart);
    }

    //ShoppingCart子类不能强制转换成Wares父类，将Wres中数据添加到ShoppingCart
    public ShoppingCart convertData(Wares wares) {
        ShoppingCart cart = new ShoppingCart();
        cart.setId(wares.getId());
        cart.setDescription(wares.getDescription());
        cart.setName(wares.getName());
        cart.setImgUrl(wares.getImgUrl());
        cart.setPrice(wares.getPrice());

        return cart;
    }

    //保存SparseArray<ShoppingCart>里的数据到本地
    public void commit() {
        List<ShoppingCart> carts = sparseToList();

        PreferencesUtils.putString(mContext, CART_JSON, JSONUtil.toJson(carts));
    }

    //将保存的数据转换成List<ShoppingCart>
    private List<ShoppingCart> sparseToList() {

        int size = datas.size();
        List<ShoppingCart> list = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            list.add(datas.valueAt(i));
        }
        return list;
    }

    //更新数据
    public void update(ShoppingCart cart) {
        datas.put(cart.getId().intValue(), cart);
        commit();

    }

    //删除数据
    public void delete(ShoppingCart cart) {

        datas.delete(cart.getId().intValue());

        commit();
    }

    //删除数据
    public void delete(List<ShoppingCart> carts) {
        if (carts != null && carts.size() > 0) {
            for (ShoppingCart cart : carts) {
                delete(cart);
            }
        }
    }

    //从本地获取数据
    public List<ShoppingCart> getAll() {

        return getDataFromLocal();
    }

    //将本地数据存放在SparseArray<ShoppingCart>中
    private void listToSparse() {
        List<ShoppingCart> carts = getDataFromLocal();

        if (carts != null && carts.size() > 0) {
            for (ShoppingCart cart : carts) {
                datas.put(cart.getId().intValue(), cart);
            }
        } else {
            datas.clear();
        }
    }

    ///获取本地数据
    private List<ShoppingCart> getDataFromLocal() {

        String json = PreferencesUtils.getString(mContext, CART_JSON);

        List<ShoppingCart> carts = null;

        if (json != null) {
            carts = JSONUtil.fromJson(json, new TypeToken<List<ShoppingCart>>() {
            }.getType());
        }
        return carts;
    }
}
