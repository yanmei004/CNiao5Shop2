package com.cniao5.cniao5shop.activity;

import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.cniao5.cniao5shop.MainActivity;
import com.cniao5.cniao5shop.R;
import com.cniao5.cniao5shop.widget.Constants;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

/**
 * 支付结果返回页面
 */
public class PayResultActivity extends BaseActivity {

    @ViewInject(R.id.img_pay)
    private ImageView mImgIcon;

    @ViewInject(R.id.tv_pay_result)
    private TextView mTvResult;

    @Override
    public int getLayoutId() {
        return R.layout.activity_pay_result;
    }

    @Override
    public void init() {
        initStatus();
    }

    @Override
    public void setToolbar() {
        getToolbar().setTitle("支付结果");
        getToolbar().setleftButtonIcon(R.drawable.icon_back_32px);
    }


    private void initStatus() {
        int status = getIntent().getIntExtra("status",-1);

        if (status == Constants.SUCCESS){
            mImgIcon.setImageResource(R.drawable.icon_success_128);
            mTvResult.setText("支付成功");

        }else {
            mImgIcon.setImageResource(R.drawable.icon_cancel_128);
            mTvResult.setText("支付失败");
        }
    }

    @OnClick(R.id.btn_back_home)
    public void goBack(View v){
        startActivity(new Intent(PayResultActivity.this,MainActivity.class));
        finish();
    }

}
