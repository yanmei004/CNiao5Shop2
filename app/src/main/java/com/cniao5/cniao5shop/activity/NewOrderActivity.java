package com.cniao5.cniao5shop.activity;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cniao5.cniao5shop.MyApplication;
import com.cniao5.cniao5shop.R;
import com.cniao5.cniao5shop.adapter.OrderItemAdapter;
import com.cniao5.cniao5shop.adapter.WareOrderAdapter;
import com.cniao5.cniao5shop.adapter.layoutmanager.FullyLinearLayoutManager;
import com.cniao5.cniao5shop.bean.Address;
import com.cniao5.cniao5shop.bean.Charge;
import com.cniao5.cniao5shop.bean.OrderItem;
import com.cniao5.cniao5shop.bean.ShoppingCart;
import com.cniao5.cniao5shop.msg.CreateOrderRespMsg;
import com.cniao5.cniao5shop.net.ServiceGenerator;
import com.cniao5.cniao5shop.net.SubscriberCallBack;
import com.cniao5.cniao5shop.utils.CartProvider;
import com.cniao5.cniao5shop.utils.JSONUtil;
import com.cniao5.cniao5shop.utils.ToastUtils;
import com.cniao5.cniao5shop.widget.Constants;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.pingplusplus.android.PaymentActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 创建订单
 */
public class NewOrderActivity extends BaseActivity implements View.OnClickListener {

    /**
     * 银联支付渠道
     */
    private static final String CHANNEL_UPACP = "upacp";
    /**
     * 微信支付渠道
     */
    private static final String CHANNEL_WECHAT = "wx";
    /**
     * 支付支付渠道
     */
    private static final String CHANNEL_ALIPAY = "alipay";
    /**
     * 百度支付渠道
     */
    private static final String CHANNEL_BFB = "bfb";
    /**
     * 京东支付渠道
     */
    private static final String CHANNEL_JDPAY_WAP = "jdpay_wap";

    @ViewInject(R.id.tv_name)
    private TextView mTvName;

    @ViewInject(R.id.tv_addr)
    private TextView mTvAddr;

    @ViewInject(R.id.img_add)
    private ImageView mImgAdd;

    @ViewInject(R.id.recycle_view)
    private RecyclerView mRecyclerView;

    @ViewInject(R.id.tv_order)
    private TextView mTvOrderList;

    @ViewInject(R.id.tv_total)
    private TextView mTvTotal;

    @ViewInject(R.id.btn_createOrder)
    private Button mBtnOrder;

    @ViewInject(R.id.rl_alipay)
    private RelativeLayout mRLAlipay;

    @ViewInject(R.id.rl_wechat)
    private RelativeLayout mRLWechat;

    @ViewInject(R.id.rl_bd)
    private RelativeLayout mRLBaidu;

    @ViewInject(R.id.rl_addr)
    private RelativeLayout mRLAddr;

    @ViewInject(R.id.rb_alipay)
    private RadioButton mRbAlipay;

    @ViewInject(R.id.rb_wechat)
    private RadioButton mRbWechat;

    @ViewInject(R.id.rb_bd)
    private RadioButton mRbBaidu;

    private HashMap<String, RadioButton> channels = new HashMap<>(3);

    private WareOrderAdapter wareOrderAdapter;

    private OrderItemAdapter orderItemAdapter;

    private String payChannel = CHANNEL_ALIPAY;//默认途径为支付宝

    private float amount;
    private String orderNum;
    private int SIGN;


    @Override
    public int getLayoutId() {
        return R.layout.activity_new_order;
    }

    @Override
    public void init() {
        showData();

        mImgAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NewOrderActivity.this, AddressListActivity.class);
//                intent.putExtra("tag", Constants.TAG_ORDER_SAVE);
                startActivityForResult(intent, Constants.REQUEST_CODE);
            }
        });

        initAddress();

        initPayChannels();
    }

    @Override
    public void setToolbar() {
        getToolbar().setTitle("订单确认");
        getToolbar().setleftButtonIcon(R.drawable.icon_back_32px);
    }

    private void initPayChannels() {
        //保存RadioButton
        channels.put(CHANNEL_ALIPAY, mRbAlipay);
        channels.put(CHANNEL_WECHAT, mRbWechat);
        channels.put(CHANNEL_BFB, mRbBaidu);

        mRLAlipay.setOnClickListener(this);
        mRLWechat.setOnClickListener(this);
        mRLBaidu.setOnClickListener(this);


        if (SIGN == Constants.CART) {
            amount = wareOrderAdapter.getTotalPrice();
        } else if (SIGN == Constants.ORDER) {
//            amount = getIntent().getFloatExtra("price",-0.1f);

            amount = orderItemAdapter.getTotalPrice();

            System.out.println("price:::" + amount);
        }
        mTvTotal.setText("应付款：￥" + amount);
    }


    //请求服务端获取地址
    private void initAddress() {
        String userId = MyApplication.getInstance().getUser().getId() + "";

        if (!TextUtils.isEmpty(userId)) {
            ServiceGenerator.getRetrofit(this)
                    .getAddrList(Long.parseLong(userId), MyApplication.getInstance().getToken())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new SubscriberCallBack<List<Address>>(this, true) {
                        @Override
                        public void onSuccess(List<Address> result) {
                            showAddress(result);
                        }
                    });
        } else {
            ToastUtils.show(this, "加载错误...");
        }
    }


    /**
     * 显示默认地址
     *
     * @param addresses
     */
    private void showAddress(List<Address> addresses) {

        /**
         * 购物车页面传递的数据显示地址
         */
        if (SIGN == Constants.CART) {
            for (Address address : addresses) {
                if (address.getIsDefault()) {
                    mTvName.setText(address.getConsignee() + "(" + address.getPhone() + ")");
                    mTvAddr.setText(address.getAddr());
                }
            }
            /**
             * 我的订单页面显示地址
             */
        } else if (SIGN == Constants.ORDER) {
            Address addressOrder = (Address) getIntent().getSerializableExtra("address");
            if (addressOrder != null) {
                System.out.println(addressOrder.getConsignee() + "::" + addressOrder.getPhone() + "::" + addressOrder.getAddr());
                mTvName.setText(addressOrder.getConsignee() + "(" + addressOrder.getPhone() + ")");
                mTvAddr.setText(addressOrder.getAddr());
            } else {//显示默认地址
                for (Address address : addresses) {
                    if (address.getIsDefault()) {
                        mTvName.setText(address.getConsignee() + "(" + address.getPhone() + ")");
                        mTvAddr.setText(address.getAddr());
                    }
                }
            }
        }

    }

    /**
     * 选择支付渠道以及RadioButton互斥功能
     *
     * @param payChannel
     */
    private void selectPayChannel(String payChannel) {

        for (Map.Entry<String, RadioButton> entry : channels.entrySet()) {

            this.payChannel = payChannel;

            System.out.println("payChannel=" + payChannel);

            //获取的RadioButton
            RadioButton rb = entry.getValue();

            //如果当前RadioButton被点击
            if (entry.getKey().equals(payChannel)) {

                //判断是否被选中
                boolean isChecked = rb.isChecked();

                //设置为互斥操作
                rb.setChecked(!isChecked);

            } else {
                //其他的都改为未选中
                rb.setChecked(false);
            }


        }
    }


    /**
     * 显示订单数据
     */
    public void showData() {

        SIGN = getIntent().getIntExtra("sign", -1);
        /**
         * 购物车商品数据
         */
        if (SIGN == Constants.CART) {
            List<ShoppingCart> carts = (List<ShoppingCart>) getIntent().getSerializableExtra("carts");
            System.out.println("showData---" + carts.size());
            wareOrderAdapter = new WareOrderAdapter(this, carts);
            FullyLinearLayoutManager layoutManager = new FullyLinearLayoutManager(this);
            layoutManager.setOrientation(GridLayoutManager.HORIZONTAL);
            mRecyclerView.setLayoutManager(layoutManager);
            mRecyclerView.setAdapter(wareOrderAdapter);
            /**
             * 我的订单再次购买点击商品显示
             */
        } else if (SIGN == Constants.ORDER) {
            List<OrderItem> orderItems = (List<OrderItem>) getIntent().getSerializableExtra("order");
            System.out.println("orderItems---" + orderItems.size());
            orderItemAdapter = new OrderItemAdapter(this, orderItems);
            FullyLinearLayoutManager layoutManager = new FullyLinearLayoutManager(this);
            layoutManager.setOrientation(GridLayoutManager.HORIZONTAL);
            mRecyclerView.setLayoutManager(layoutManager);
            mRecyclerView.setAdapter(orderItemAdapter);
        }

    }

    /**
     * 支付渠道点击事件
     *
     * @param v
     */
    @Override
    public void onClick(View v) {

        selectPayChannel(v.getTag().toString());
        System.out.println("tag=" + v.getTag().toString());

    }

    /**
     * 提交订单
     *
     * @param view
     */
    public void postNewOrder(View view) {

        List<WareItem> items = new ArrayList<>();

        //判断购物车还是再次购买订单返回
        if (SIGN == Constants.CART) {
            postOrderByCart(items);
        } else if (SIGN == Constants.ORDER) {
            postOrderByMyOrder(items);
        }

    }

    /**
     * 提交购物车订单
     *
     * @param items 商品集合
     */
    private void postOrderByCart(List<WareItem> items) {
        final List<ShoppingCart> carts = wareOrderAdapter.getDatas();
        //获取购物车数据
        for (ShoppingCart cart : carts) {
            WareItem item = new WareItem(cart.getId(), (int) Float.parseFloat(cart.getPrice()));
            items.add(item);
        }
        String item_json = JSONUtil.toJson(items);

        String userId = MyApplication.getInstance().getUser().getId() + "";

        ServiceGenerator.getRetrofit(this)
                .orderCreate(Long.parseLong(userId),item_json,(int)amount,1,payChannel,MyApplication.getInstance().getToken())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SubscriberCallBack<CreateOrderRespMsg>(this,true) {
                    @Override
                    public void onSuccess(CreateOrderRespMsg result) {
                        mBtnOrder.setEnabled(true);

                        orderNum = result.getData().getOrderNum();

                        Charge charge = result.getData().getCharge();

                        //打开支付页面
                        openPaymentActivity(JSONUtil.toJson(charge));

                        /**
                         * 清空已购买商品
                         */
                        if (SIGN == Constants.CART) {
                            CartProvider mCartProvider = CartProvider.getInstance(NewOrderActivity.this);
                            mCartProvider.delete(carts);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        mBtnOrder.setEnabled(true);
                    }
                });
    }

    /**
     * 提交再次购买商品订单
     *
     * @param items
     */
    private void postOrderByMyOrder(List<WareItem> items) {
        List<OrderItem> orderItems = orderItemAdapter.getDatas();
        for (OrderItem orderItem : orderItems) {
            WareItem item = new WareItem(orderItem.getWares().getId(),
                    (int) Float.parseFloat(orderItem.getWares().getPrice()));
            items.add(item);
        }
        String item_json = JSONUtil.toJson(items);

        String userId = MyApplication.getInstance().getUser().getId() + "";

        ServiceGenerator.getRetrofit(this)
                .orderCreate(Long.parseLong(userId), item_json, (int) amount, 1, payChannel, MyApplication.getInstance().getToken())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SubscriberCallBack<CreateOrderRespMsg>(this, true) {
                    @Override
                    public void onSuccess(CreateOrderRespMsg result) {
                        mBtnOrder.setEnabled(true);

                        orderNum = result.getData().getOrderNum();

                        Charge charge = result.getData().getCharge();

                        //打开支付页面
                        openPaymentActivity(JSONUtil.toJson(charge));
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        mBtnOrder.setEnabled(true);
                    }
                });

    }

    /**
     * 显示模拟支付页面
     *
     * @param charge
     */
    private void openPaymentActivity(String charge) {
        Intent intent = new Intent();
        String packageName = getPackageName();
        ComponentName componentName = new ComponentName(packageName, packageName + ".wxapi.WXPayEntryActivity");
        intent.setComponent(componentName);
        intent.putExtra(PaymentActivity.EXTRA_CHARGE, charge);
        startActivityForResult(intent, Constants.REQUEST_CODE_PAYMENT);
    }


    /**
     * 支付结果返回以及地址信息结果返回
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        /**
         * 显示默认地址
         */
        showAddress();

        /**
         * 支付结果返回
         */
        paySesult(requestCode, resultCode, data);

    }

    /**
     * 显示默认地址
     */
    private void showAddress() {
        //请求服务端获取地址
        String userId = MyApplication.getInstance().getUser().getId() + "";

        if (!TextUtils.isEmpty(userId)) {
            ServiceGenerator.getRetrofit(this)
                    .getAddrList(Long.parseLong(userId), MyApplication.getInstance().getToken())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new SubscriberCallBack<List<Address>>(this, true) {
                        @Override
                        public void onSuccess(List<Address> result) {
                            for (Address address : result) {
                                if (address.getIsDefault()) {
                                    mTvName.setText(address.getConsignee() + "(" + address.getPhone() + ")");
                                    mTvAddr.setText(address.getAddr());
                                }
                            }
                        }
                    });
        } else {
            ToastUtils.show(this, "加载错误...");
        }
    }

    /**
     * 支付页面返回处理
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    private void paySesult(int requestCode, int resultCode, Intent data) {

        if (requestCode == Constants.REQUEST_CODE_PAYMENT) {
            if (resultCode == Activity.RESULT_OK) {
                String result = data.getExtras().getString("pay_result");

                if (result.equals("success")) {
                    changeOrderStatus(Constants.SUCCESS);
                } else if (result.equals("fail")) {
                    changeOrderStatus(Constants.FAIL);
                } else if (result.equals("cancel")) {
                    changeOrderStatus(Constants.CANCEL);
                } else {
                    changeOrderStatus(Constants.INVALID);
                }
                /* 处理返回值
                 * "success" - 支付成功
                 * "fail"    - 支付失败
                 * "cancel"  - 取消支付
                 * "invalid" - 支付插件未安装（一般是微信客户端未安装的情况）
                 */
//                String errorMsg = data.getExtras().getString("error_msg"); // 错误信息
//                String extraMsg = data.getExtras().getString("extra_msg"); // 错误信息
//                showMsg(result, errorMsg, extraMsg);
            }
        }
    }

    /**
     * 修改订单状态
     * 请求跳转到支付结果页面
     * 失败直接返回请求失败状态
     *
     * @param status
     */
    private void changeOrderStatus(final int status) {
        Map<String, String> params = new HashMap<>(5);
        params.put("order_num", orderNum);
        params.put("status", status + "");

        ServiceGenerator.getRetrofit(this)
                .orderComplete(orderNum, status, MyApplication.getInstance().getToken())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SubscriberCallBack<CreateOrderRespMsg>(this, false) {
                    @Override
                    public void onSuccess(CreateOrderRespMsg result) {
                        /**
                         * 跳转到支付结果页面
                         */
                        toPayResultActivity(status);
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        /**
                         * 跳转到支付失败页面
                         */
                        toPayResultActivity(Constants.FAIL);
                    }
                });
    }

    /**
     * 跳转到支付结果页面
     *
     * @param status
     */
    private void toPayResultActivity(int status) {
        Intent intent = new Intent(this, PayResultActivity.class);
        intent.putExtra("status", status);
        startActivity(intent);
    }


    /**
     * 商品id和价格显示适配器
     */
    class WareItem {
        private Long ware_id;
        private int amount;

        public WareItem(Long ware_id, int amount) {
            this.ware_id = ware_id;
            this.amount = amount;
        }

        public Long getWare_id() {
            return ware_id;
        }

        public void setWare_id(Long ware_id) {
            this.ware_id = ware_id;
        }

        public int getAmount() {
            return amount;
        }

        public void setAmount(int amount) {
            this.amount = amount;
        }
    }
}
