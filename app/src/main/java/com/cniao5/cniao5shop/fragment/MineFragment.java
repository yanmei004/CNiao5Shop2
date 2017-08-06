package com.cniao5.cniao5shop.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.cniao5.cniao5shop.activity.AddressListActivity;
import com.cniao5.cniao5shop.activity.LoginActivity;
import com.cniao5.cniao5shop.MyApplication;
import com.cniao5.cniao5shop.activity.MyFavoriteActivity;
import com.cniao5.cniao5shop.activity.MyOrderActivity;
import com.cniao5.cniao5shop.R;
import com.cniao5.cniao5shop.bean.User;
import com.cniao5.cniao5shop.utils.ToastUtils;
import com.cniao5.cniao5shop.widget.Constants;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * 我的
 */
public class MineFragment extends Fragment {

    @ViewInject(R.id.profile_image)
    private CircleImageView mImageView;

    @ViewInject(R.id.tv_username)
    private TextView mTvUsername;

    @ViewInject(R.id.tv_addr)
    private TextView mTvAddress;

    @ViewInject(R.id.btn_loginOut)
    private Button mBtnLoginOut;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_mine, container, false);

        ViewUtils.inject(this, view);

        initUser();

        return view;
    }


    /**
     * 刚进入我的页面就要初始化用户数据
     */
    private void initUser() {
        User user = MyApplication.getInstance().getUser();
        showUser(user);
    }

    /**
     * 登录点击事件
     * @param view
     */
    @OnClick(value = {R.id.profile_image, R.id.tv_username})
    public void toLogin(View view) {

        /**
         * 判断是否已经登录，若已登录，则提示，未登录，则跳转
         */
        User user = MyApplication.getInstance().getUser();
        if (user != null) {
            ToastUtils.show(getContext(), "您已登录");
            mImageView.setClickable(false);
            mTvUsername.setClickable(false);
        } else {
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            startActivityForResult(intent, Constants.REQUEST_CODE);
        }
    }

    /**
     * 退出登录
     * @param view
     */
    @OnClick(R.id.btn_loginOut)
    public void loginOut(View view) {
        MyApplication.getInstance().clearUser();
        showUser(null);
    }

    /**
     * 登录跳转返回结果
     * @param requestCode 请求码
     * @param resultCode 结果码
     * @param data 数据
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        initUser();
    }

    /**
     * 显示用户数据
     * @param user
     */
    private void showUser(User user) {

        if (user != null) {
            mTvUsername.setText(user.getUsername());

            if (!TextUtils.isEmpty(user.getLogo_url()))
                Picasso.with(getActivity()).load(user.getLogo_url()).into(mImageView);

            System.out.println("Username------------"+user.getUsername());
            mBtnLoginOut.setVisibility(View.VISIBLE);
        } else {
            mTvUsername.setText(R.string.to_login);
            mBtnLoginOut.setVisibility(View.GONE);
            mImageView.setClickable(true);
            mTvUsername.setClickable(true);
        }

    }

    /**
     * 地址按钮点击事件
     * @param view
     */
    @OnClick(R.id.tv_addr)
    public void showAddress(View view) {
        Intent intent = new Intent(getActivity(), AddressListActivity.class);
        startActivity(intent, true);
    }

    /**
     * 我的订单显示。需先判断是否已经登录
     * @param view
     */
    @OnClick(R.id.tv_my_order)
    public void showMyOrder(View view){
        Intent intent = new Intent(getActivity(), MyOrderActivity.class);
        startActivity(intent, true);
    }

    /**
     * 收藏夹点击事件
     * @param view
     */
    @OnClick(R.id.tv_favorite)
    public void showMyFavorite(View view){
        Intent intent = new Intent(getActivity(), MyFavoriteActivity.class);
        startActivity(intent, true);
    }

    /**
     * 启动目标activity
     * @param intent 跳转意图
     * @param isNeedLogin 是否需要登录
     */
    public void startActivity(Intent intent, boolean isNeedLogin) {

        if (isNeedLogin) {
            User user = MyApplication.getInstance().getUser();

            if (user != null) {
                super.startActivity(intent);
            } else {
                MyApplication.getInstance().putIntent(intent);
                Intent loginIntent = new Intent(getActivity(), LoginActivity.class);
                super.startActivity(loginIntent);
            }
        }else {
            super.startActivity(intent);
        }
    }
}
