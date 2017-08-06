package com.cniao5.cniao5shop.widget;

/**
 * 常量
 */
public class Constants {

    public static final String CAMPAIGN_ID = "campaign_id";
    public static final String WARES = "wares";
    public static final String DES_KEY = "Cniao5_123456";
    public static final String USER_JSON = "user_json";
    public static final String TOKEN = "token";
    public static final int REQUEST_CODE = 0;
    public static final int REQUEST_CODE_PAYMENT = 1;
    public static final int SUCCESS = 1;
    public static final int FAIL = -1;
    public static final int CANCEL = -2;
    public static final int INVALID = 0;

    public static final int ADDRESS_ADD = 100;
    public static final int ADDRESS_EDIT = 200;


    public static final int TAG_SAVE = 1;
    public static final int TAG_COMPLETE = 2;

    public static final int CART = 1;
    public static final int ORDER = 2;

    public static class API {
        public static final String BASE_URL = "http://112.124.22.238:8081/course_api/";

        public static final String CAMPAIN_HOME = BASE_URL + "campaign/recommend";

        public static final String BANNER = BASE_URL + "banner/query";

        public static final String WARES_HOT = BASE_URL + "wares/hot";

        public static final String CATEGORY_LIST = BASE_URL + "category/list";
        public static final String WARES_LIST = BASE_URL + "wares/list";
        public static final String WARES_DETAILS = BASE_URL + "wares/detail.html";
        public static final String WARES_CAMPAIGN_LIST = BASE_URL + "wares/campaign/list";
        public static final String AUTH_LOGIN = BASE_URL + "auth/login";
        public static final String USER_DETAIL = BASE_URL + "user/get?id=1";
        public static final String AUTH_REG = BASE_URL + "auth/reg";
        public static final String ORDER_CREATE = BASE_URL + "order/create";
        public static final String ORDER_COMPLEPE=BASE_URL +"order/complete";
        public static final String ORDER_LIST = BASE_URL + "order/list";
        public static final String ADDR_CREATE = BASE_URL + "addr/create";
        public static final String ADDR_LIST = BASE_URL + "addr/list";
        public static final String ADDR_UPDATE = BASE_URL + "addr/update";
        public static final String ADDR_DEL = BASE_URL + "addr/del";
        public static final String FAVORITE_CREATE = BASE_URL + "favorite/create";
        public static final String FAVORITE_LIST = BASE_URL + "favorite/list";
        public static final String FAVORITE_DEL = BASE_URL + "favorite/del";

    }
}
