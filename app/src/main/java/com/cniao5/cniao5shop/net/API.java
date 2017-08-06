package com.cniao5.cniao5shop.net;

import com.cniao5.cniao5shop.bean.Address;
import com.cniao5.cniao5shop.bean.Banner;
import com.cniao5.cniao5shop.bean.Category;
import com.cniao5.cniao5shop.bean.Favorite;
import com.cniao5.cniao5shop.bean.HomeCampaign;
import com.cniao5.cniao5shop.bean.Order;
import com.cniao5.cniao5shop.bean.Page;
import com.cniao5.cniao5shop.bean.User;
import com.cniao5.cniao5shop.bean.Wares;
import com.cniao5.cniao5shop.msg.BaseResMsg;
import com.cniao5.cniao5shop.msg.CreateOrderRespMsg;
import com.cniao5.cniao5shop.msg.LoginRespMsg;

import java.util.List;

import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by dali on 2017/8/4.
 */

public interface API {

    String BASE_URL = "http://112.124.22.238:8081/";

    String path = "course_api/";

    @FormUrlEncoded //获取轮播图
    @POST(path + "banner/query")
    Observable<List<Banner>> getBanner(@Field("type") int type);

    @FormUrlEncoded //获取首页
    @POST(path + "campaign/recommend")
    Observable<List<HomeCampaign>> getHome(@Field("") String empty);

    @FormUrlEncoded //获取热卖
    @POST(path + "wares/hot")
    Observable<Page<Wares>> getHotWares(@Field("curPage") int curPage, @Field("pageSize") int pageSize);

    @FormUrlEncoded //获取分类标签
    @POST(path + "category/list")
    Observable<List<Category>> getCategory(@Field("") String empty);

    @FormUrlEncoded //获取分类下的列表
    @POST(path + "wares/list")
    Observable<Page<Wares>> getWaresList(@Field("categoryId") long categoryId,
                                              @Field("curPage") int curPage, @Field("pageSize") int pageSize);

    @FormUrlEncoded//首页活动下的商品列表
    @POST(path + "wares/campaign/list")
    Observable<Page<Wares>> campaignList(@Field("campaignId") long campaignId, @Field("orderBy") int orderBy,
                                       @Field("curPage") int curPage, @Field("pageSize") int pageSize);


    @FormUrlEncoded //用户登录
    @POST(path + "auth/login")
    Observable<LoginRespMsg<User>> login(@Field("phone") String phone, @Field("password") String password);

    @FormUrlEncoded //用户注册
    @POST(path + "auth/reg")
    Observable<LoginRespMsg<User>> reg(@Field("phone") String phone, @Field("password") String password);

    @FormUrlEncoded//提交订单
    @POST(path + "/order/create")
    Observable<CreateOrderRespMsg> orderCreate(@Field("user_id") long user_id, @Field("item_json") String item_json,
                                               @Field("amount") int amount, @Field("addr_id") int addr_id,
                                               @Field("pay_channel") String pay_channel, @Field("token") String token);

    @FormUrlEncoded //我的订单
    @POST(path + "order/list")
    Observable<List<Order>> orderList(@Field("user_id") long user_id, @Field("status") int status, @Field("token") String token);


    @FormUrlEncoded//更改订单状态
    @POST(path + "/order/complete")
    Observable<CreateOrderRespMsg> orderComplete(@Field("order_num") String order_num, @Field("status") int status,
                                                 @Field("token") String token);

    @FormUrlEncoded//更新地址
    @POST(path + "addr/create")
    Observable<BaseResMsg> createAddr(@Field("user_id") Long user_id, @Field("consignee") String consignee,
                                      @Field("phone") String phone, @Field("addr") String addr,
                                      @Field("zip_code") String zip_code, @Field("token") String token);

    @GET(path + "addr/list")//收货地址列表
    Observable<List<Address>> getAddrList(@Query("user_id") long user_id, @Query("token") String token);

    @FormUrlEncoded//更新地址
    @POST(path + "addr/update")
    Observable<BaseResMsg> updateAddr(@Field("id") Long id, @Field("consignee") String consignee,
                                      @Field("phone") String phone, @Field("addr") String addr,
                                      @Field("zip_code") String zip_code, @Field("is_default") boolean is_default,
                                      @Field("token") String token);

    @FormUrlEncoded//删除
    @POST(path + "addr/del")
    Observable<BaseResMsg> deleteAddr(@Field("id") Long id, @Field("token") String token);

    @FormUrlEncoded//添加收藏
    @POST(path + "favorite/create")
    Observable<BaseResMsg> addFavorite(@Field("user_id") long user_id, @Field("ware_id") long ware_id,
                                   @Field("token") String token);

    //收藏列表
    @GET(path + "favorite/list")
    Observable<List<Favorite>> favoriteList(@Query("user_id") long user_id, @Query("token") String token);

    @FormUrlEncoded//删除收藏
    @POST(path + "favorite/del")
    Observable<BaseResMsg> favoriteDel(@Field("id") long id, @Field("token") String token);

}
