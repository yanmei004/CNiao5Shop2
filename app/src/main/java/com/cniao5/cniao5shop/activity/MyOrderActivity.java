package com.cniao5.cniao5shop.activity;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;

import com.cniao5.cniao5shop.MyApplication;
import com.cniao5.cniao5shop.R;
import com.cniao5.cniao5shop.adapter.BaseAdapter;
import com.cniao5.cniao5shop.adapter.MyOrderAdapter;
import com.cniao5.cniao5shop.adapter.decoration.CardViewtemDecortion;
import com.cniao5.cniao5shop.bean.Order;
import com.cniao5.cniao5shop.net.ServiceGenerator;
import com.cniao5.cniao5shop.net.SubscriberCallBack;
import com.cniao5.cniao5shop.utils.ToastUtils;
import com.cniao5.cniao5shop.widget.Constants;
import com.lidroid.xutils.view.annotation.ViewInject;

import java.io.Serializable;
import java.util.List;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 我的订单
 */
public class MyOrderActivity extends BaseActivity implements TabLayout.OnTabSelectedListener{

    public static final int STATUS_ALL = 1000;
    public static final int STATUS_SUCCESS = 1; //支付成功的订单
    public static final int STATUS_PAY_FAIL = -2; //支付失败的订单
    public static final int STATUS_PAY_WAIT = 0; //：待支付的订单
    private int status = STATUS_ALL;

    @ViewInject(R.id.tab_layout)
    private TabLayout mTabLayout;

    @ViewInject(R.id.recycle_view)
    private RecyclerView mRecycleVie;

    private MyOrderAdapter mAdapter;

    @Override
    public int getLayoutId() {
        return R.layout.activity_my_order;
    }


    @Override
    public void init() {
        /**
         * 初始化Tab
         */
        initTab();
        /**
         * 获取订单数据
         */
        getOrders();

    }

    @Override
    public void setToolbar() {
        getToolbar().setTitle("我的订单");
        getToolbar().setleftButtonIcon(R.drawable.icon_back_32px);
    }

    //初始化tab
    private void initTab() {

        TabLayout.Tab tab = mTabLayout.newTab();
        tab.setText("全部");
        tab.setTag(STATUS_ALL);
        mTabLayout.addTab(tab);

        tab = mTabLayout.newTab();
        tab.setText("支付成功");
        tab.setTag(STATUS_SUCCESS);
        mTabLayout.addTab(tab);

        tab = mTabLayout.newTab();
        tab.setText("待支付");
        tab.setTag(STATUS_PAY_WAIT);
        mTabLayout.addTab(tab);

        tab = mTabLayout.newTab();
        tab.setText("支付失败");
        tab.setTag(STATUS_PAY_FAIL);
        mTabLayout.addTab(tab);

        mTabLayout.setOnTabSelectedListener(this);

    }

    /**
     * 获取订单数据
     */
    private void getOrders() {
        String userId = MyApplication.getInstance().getUser().getId()+"";

        if (!TextUtils.isEmpty(userId)) {
            ServiceGenerator.getRetrofit(this)
                    .orderList(Long.parseLong(userId),status,MyApplication.getInstance().getToken())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new SubscriberCallBack<List<Order>>(this,true) {
                        @Override
                        public void onSuccess(List<Order> result) {
                            showOrders(result);
                        }
                    });
        }
    }

    /**
     * 显示订单数据
     * @param orders
     */
    private void showOrders(List<Order> orders) {
        if (mAdapter == null) {
            mAdapter = new MyOrderAdapter(MyOrderActivity.this, orders, new MyOrderAdapter.OnItemWaresClickListener() {
                @Override
                public void onItemWaresClickListener(View v,Order order) {
                    /**
                     * 再次购买点击事件，跳转到支付页面
                     * 将商品和地址以及总金额传入
                     */
                    Intent intent = new Intent(MyOrderActivity.this,NewOrderActivity.class);
                    intent.putExtra("order",(Serializable)order.getItems());
                    intent.putExtra("sign",Constants.ORDER);
                    intent.putExtra("price",order.getAmount());
                    startActivity(intent,true);
                }
            });
            mRecycleVie.setAdapter(mAdapter);
            mRecycleVie.setLayoutManager(new LinearLayoutManager(this));
            mRecycleVie.addItemDecoration(new CardViewtemDecortion());

            mAdapter.setOnItemClickListenner(new BaseAdapter.OnItemClickListenner() {
                @Override
                public void onItemClick(View view, int position) {
                    ToastUtils.show(MyOrderActivity.this, "功能正在完善...");
//                    toDetailActivity(position);
                }
            });
        } else {
            mAdapter.refreshData(orders);
            mRecycleVie.setAdapter(mAdapter);
        }
    }

    private void toDetailActivity(int position) {

//        Order order = mAdapter.getItem(position);
//
//        System.out.println(order.getAmount()+"::"+order.getOrderNum()+"::"+order.getAddress().getConsignee());
    }

    /**
     * tablayout三个点击事件
     * @param tab
     */
    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        status = (int) tab.getTag();
        getOrders();
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }
}
