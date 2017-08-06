package com.cniao5.cniao5shop.activity;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.cniao5.cniao5shop.MyApplication;
import com.cniao5.cniao5shop.R;
import com.cniao5.cniao5shop.bean.User;
import com.cniao5.cniao5shop.bean.Wares;
import com.cniao5.cniao5shop.msg.BaseResMsg;
import com.cniao5.cniao5shop.net.ServiceGenerator;
import com.cniao5.cniao5shop.net.SubscriberCallBack;
import com.cniao5.cniao5shop.utils.CartProvider;
import com.cniao5.cniao5shop.utils.ToastUtils;
import com.cniao5.cniao5shop.widget.Constants;
import com.lidroid.xutils.view.annotation.ViewInject;

import java.io.Serializable;

import dmax.dialog.SpotsDialog;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 商品详情
 */
public class WaresDetailsActivity extends BaseActivity {

    @ViewInject(R.id.webView)
    private WebView mWebView;

    private WebAppInterface mAppInterface;

    private CartProvider mCartProvider;

    private Wares mWares;

    private SpotsDialog mDialog;

    private WebClient mWebClient;


    /**
     * 初始化WebView
     * 1.设置允许执⾏JS脚本：
     * webSettings.setJavaScriptEnabled(true);
     * 2.添加通信接口
     * webView.addJavascriptInterface(Interface,”InterfaceName”)
     * 3.JS调⽤Android
     * InterfaceName.MethodName
     * 4.Android调⽤JS
     * webView.loadUrl("javascript:functionName()");
     */
    private void initWebView() {
        WebSettings settings = mWebView.getSettings();
        //1、设置允许执行Js脚本
        settings.setJavaScriptEnabled(true);
        //默认为true，无法加载页面图片
        settings.setBlockNetworkImage(false);
        //设置允许缓存
        settings.setAppCacheEnabled(true);

        mWebView.loadUrl(Constants.API.WARES_DETAILS);

        System.out.println(Constants.API.WARES_DETAILS);

        mAppInterface = new WebAppInterface(this);
        mWebClient = new WebClient();

        //2.添加通信接口 name和web页面名称一致
        mWebView.addJavascriptInterface(mAppInterface, "appInterface");

        mWebView.setWebViewClient(mWebClient);
    }


    @Override
    public int getLayoutId() {
        return R.layout.activity_wares_details;
    }

    @Override
    public void init() {
        Serializable serializable = getIntent().getSerializableExtra(Constants.WARES);
        if (serializable == null)
            this.finish();

        mDialog = new SpotsDialog(this, "loading...");
        mDialog.show();

        mWares = (Wares) serializable;
        mCartProvider = CartProvider.getInstance(this);

        initWebView();
    }

    @Override
    public void setToolbar() {
        getToolbar().setTitle(R.string.wares_details);
        getToolbar().setleftButtonIcon(R.drawable.icon_back_32px);
        getToolbar().setRightButtonText(getString(R.string.share));
        getToolbar().setRightButtonOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showShare();
            }
        });
    }


    /**
     * 显示分享界面
     */
    private void showShare() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, Constants.API.WARES_DETAILS);
        startActivity(Intent.createChooser(shareIntent, "分享到"));//设置分享列表的标题
    }


    /**
     * 页面加载完之后才调用方法进行显示数据
     * 需要实现一个监听判断页面是否加载完
     */
    class WebClient extends WebViewClient {

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);

            if (mDialog != null && mDialog.isShowing())
                mDialog.dismiss();
            //显示详情
            mAppInterface.showDetail();
        }

    }


    /**
     * 定义接口进行通讯
     */
    class WebAppInterface {

        private Context context;

        public WebAppInterface(Context context) {
            this.context = context;
        }

        /**
         * 方法名和js代码中必须一直
         * 显示详情页
         */
        @JavascriptInterface
        private void showDetail() {

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //调用js代码
                    mWebView.loadUrl("javascript:showDetail(" + mWares.getId() + ")");
                }
            });
        }

        /**
         * 添加到购物车
         *
         * @param id 商品id
         */
        @JavascriptInterface
        public void buy(long id) {
            mCartProvider.put(mWares);

            ToastUtils.show(context, R.string.has_add_cart);

        }

        /**
         * 添加到收藏夹
         *
         * @param id 商品id
         */
        @JavascriptInterface
        public void addToCart(long id) {
            addToFavorite();
        }
    }

    /**
     * 添加到收藏夹
     */
    private void addToFavorite() {
        User user = MyApplication.getInstance().getUser();
        if (user == null) {
            startActivity(new Intent(this, LoginActivity.class));
        }

        String userId = MyApplication.getInstance().getUser().getId() + "";

        if (!TextUtils.isEmpty(userId)) {

            ServiceGenerator.getRetrofit(this)
                    .addFavorite(Long.parseLong(userId), mWares.getId(), MyApplication.getInstance().getToken())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new SubscriberCallBack<BaseResMsg>(this, false) {
                        @Override
                        public void onSuccess(BaseResMsg result) {
                            ToastUtils.show(WaresDetailsActivity.this, getString(R.string.has_add_favorite));
                        }
                    });

        }else {
            ToastUtils.show(this,"加载错误...");
        }
    }

}
