package com.cniao5.cniao5shop.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;

import com.cniao5.cniao5shop.MyApplication;
import com.cniao5.cniao5shop.R;
import com.cniao5.cniao5shop.adapter.AddressAdapter;
import com.cniao5.cniao5shop.adapter.decoration.DividerItemDecortion;
import com.cniao5.cniao5shop.bean.Address;
import com.cniao5.cniao5shop.msg.BaseResMsg;
import com.cniao5.cniao5shop.net.ServiceGenerator;
import com.cniao5.cniao5shop.net.SubscriberCallBack;
import com.cniao5.cniao5shop.utils.ToastUtils;
import com.cniao5.cniao5shop.widget.Constants;
import com.cniao5.cniao5shop.widget.CustomDialog;
import com.lidroid.xutils.view.annotation.ViewInject;

import java.util.Collections;
import java.util.List;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 地址列表
 */
public class AddressListActivity extends BaseActivity {

    @ViewInject(R.id.recycler_view)
    private RecyclerView mRecyclerview;

    private AddressAdapter mAdapter;
    private CustomDialog mDialog;

    @Override
    public int getLayoutId() {
        return R.layout.activity_address_list;
    }

    @Override
    public void init() {
        initAddress();
    }

    @Override
    public void setToolbar() {
        getToolbar().setTitle("我的地址");
        getToolbar().setleftButtonIcon(R.drawable.icon_back_32px);
        getToolbar().setRightImgButtonIcon(R.drawable.icon_add_w);
        getToolbar().setRightButtonOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toAddActivity();
            }
        });
    }

    /**
     * 显示删除提示对话框
     *
     * @param address
     */
    private void showDialog(final Address address) {
        CustomDialog.Builder builder = new CustomDialog.Builder(this);
        builder.setMessage("您确定删除该地址吗？");
        builder.setTitle("友情提示");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                deleteAddress(address);
                initAddress();

                if (mDialog.isShowing())
                    mDialog.dismiss();
            }
        });

        builder.setNegativeButton("取消",
                new android.content.DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if (mDialog.isShowing())
                            mDialog.dismiss();
                    }
                });

        mDialog = builder.create();
        mDialog.show();

    }

    /**
     * 删除地址
     *
     * @param address
     */
    private void deleteAddress(Address address) {

        ServiceGenerator.getRetrofit(this)
                .deleteAddr(address.getId(), MyApplication.getInstance().getToken())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SubscriberCallBack<BaseResMsg>(this, false) {
                    @Override
                    public void onSuccess(BaseResMsg result) {
                        if (result.getStatus() == result.STATUS_SUCCESS) {
                            setResult(RESULT_OK);
                            if (mDialog.isShowing())
                                mDialog.dismiss();
                        }
                    }
                });
    }

    /**
     * 跳转到添加地址页面
     * 点击右上角添加按钮，传入TAG_SAVE,更改添加地址页面toolbar显示
     */
    private void toAddActivity() {
        Intent intent = new Intent(this, AddressAddActivity.class);
        intent.putExtra("tag", Constants.TAG_SAVE);
        startActivityForResult(intent, Constants.ADDRESS_ADD);
    }

    /**
     * 跳转AddressAddActivity页面结果处理
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        initAddress();
    }

    /**
     * 初始化地址页面
     */
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
     * 显示地址列表
     *
     * @param addresses
     */
    private void showAddress(final List<Address> addresses) {

        Collections.sort(addresses);
        if (mAdapter == null) {
            mAdapter = new AddressAdapter(this, addresses, new AddressAdapter.AddressLisneter() {
                @Override
                public void setDefault(Address address) {
                    setResult(RESULT_OK);
                    //更改地址
                    updateAddress(address);
                }

                @Override
                public void onClickEdit(Address address) {
                    editAddress(address);
                }

                @Override
                public void onClickDelete(Address address) {
                    showDialog(address);
                    mDialog.show();
                }
            });
            mRecyclerview.setAdapter(mAdapter);
            mRecyclerview.setLayoutManager(new LinearLayoutManager(AddressListActivity.this));
            mRecyclerview.addItemDecoration(new DividerItemDecortion(this, DividerItemDecortion.VERTICAL_LIST));
        } else {
            mAdapter.refreshData(addresses);
            mRecyclerview.setAdapter(mAdapter);
        }

    }

    /**
     * 编辑地址
     * 传入TAG_COMPLETE更改AddressAddActivitytoolbar显示
     *
     * @param address
     */
    private void editAddress(Address address) {
        Intent intent = new Intent(this, AddressAddActivity.class);
        intent.putExtra("tag", Constants.TAG_COMPLETE);
        intent.putExtra("addressBean", address);

        startActivityForResult(intent, Constants.ADDRESS_EDIT);
    }

    /**
     * 更新地址
     *
     * @param address
     */
    public void updateAddress(Address address) {
        ServiceGenerator.getRetrofit(this)
                .updateAddr(address.getId(),address.getConsignee(), address.getPhone(), address.getAddr(), address.getZip_code(), address.getIsDefault(), MyApplication.getInstance().getToken())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SubscriberCallBack<BaseResMsg>(this,true) {
                    @Override
                    public void onSuccess(BaseResMsg result) {
                        if (result.getStatus() == result.STATUS_SUCCESS) {
                            //从服务端更新地址
                            initAddress();
                        }
                    }
                });

    }

}
