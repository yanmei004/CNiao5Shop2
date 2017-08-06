package com.cniao5.cniao5shop.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.cniao5.cniao5shop.MyApplication;
import com.cniao5.cniao5shop.R;
import com.cniao5.cniao5shop.bean.User;
import com.cniao5.cniao5shop.widget.CnToolbar;
import com.lidroid.xutils.ViewUtils;

/**
 * BaseActivity封装
 */
public abstract class BaseActivity extends AppCompatActivity {

    private void initToolbar() {
        if (getToolbar() != null){
            setToolbar();
            getToolbar().setLeftButtonOnClickLinster(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        }
    }

    public CnToolbar getToolbar() {
        return (CnToolbar)findViewById(R.id.toolbar);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());

        ViewUtils.inject(this);

        initToolbar();

        init();

    }

    public abstract int getLayoutId();

    public abstract void init();

    public abstract void setToolbar();

    public void startActivity(Intent intent, boolean isNeedLogin) {

        if (isNeedLogin) {
            User user = MyApplication.getInstance().getUser();

            if (user != null) {
                super.startActivity(intent);
            } else {
                MyApplication.getInstance().putIntent(intent);
                Intent loginIntent = new Intent(this, LoginActivity.class);
                super.startActivity(loginIntent);
            }
        } else {
            super.startActivity(intent);
        }
    }
}
