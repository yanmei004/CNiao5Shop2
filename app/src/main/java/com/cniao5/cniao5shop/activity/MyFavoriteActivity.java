package com.cniao5.cniao5shop.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;

import com.cniao5.cniao5shop.MyApplication;
import com.cniao5.cniao5shop.R;
import com.cniao5.cniao5shop.adapter.BaseAdapter;
import com.cniao5.cniao5shop.adapter.FavoriteAdapter;
import com.cniao5.cniao5shop.adapter.decoration.CardViewtemDecortion;
import com.cniao5.cniao5shop.bean.Favorite;
import com.cniao5.cniao5shop.msg.BaseResMsg;
import com.cniao5.cniao5shop.net.ServiceGenerator;
import com.cniao5.cniao5shop.net.SubscriberCallBack;
import com.cniao5.cniao5shop.widget.CustomDialog;
import com.lidroid.xutils.view.annotation.ViewInject;

import java.util.List;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


/**
 * 我的收藏
 */
public class MyFavoriteActivity extends BaseActivity {

    @ViewInject(R.id.recycle_view)
    private RecyclerView mRecyclerview;

    private FavoriteAdapter mAdapter;
    private CustomDialog mDialog;

    private void initFavorite() {

        String userId = MyApplication.getInstance().getUser().getId() + "";

        if (!TextUtils.isEmpty(userId)) {

            ServiceGenerator.getRetrofit(this)
                    .favoriteList(Long.parseLong(userId), MyApplication.getInstance().getToken())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new SubscriberCallBack<List<Favorite>>(this, true) {
                        @Override
                        public void onSuccess(List<Favorite> result) {
                            showFavorite(result);
                        }
                    });
        }
    }

    /**
     * 显示删除提示对话框
     *
     * @param favorite
     */
    private void showDialog(final Favorite favorite) {
        CustomDialog.Builder builder = new CustomDialog.Builder(this);
        builder.setMessage("您确定删除该商品吗？");
        builder.setTitle("友情提示");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                deleteFavorite(favorite);
                initFavorite();

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

    private void deleteFavorite(Favorite favorite) {

        ServiceGenerator.getRetrofit(this)
                .favoriteDel(favorite.getId(), MyApplication.getInstance().getToken())
                .subscribeOn(Schedulers.io())
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

    private void showFavorite(final List<Favorite> favorites) {

        if (mAdapter == null) {
            mAdapter = new FavoriteAdapter(this, favorites, new FavoriteAdapter.FavoriteLisneter() {
                @Override
                public void onClickDelete(Favorite favorite) {
                    showDialog(favorite);
                }
            });
            mRecyclerview.setAdapter(mAdapter);
            mRecyclerview.setLayoutManager(new LinearLayoutManager(this));
            mRecyclerview.addItemDecoration(new CardViewtemDecortion());

            mAdapter.setOnItemClickListenner(new BaseAdapter.OnItemClickListenner() {
                @Override
                public void onItemClick(View view, int position) {
                    mAdapter.showDetail(favorites.get(position).getWares());
                }
            });
        } else {
            mAdapter.refreshData(favorites);
            mRecyclerview.setAdapter(mAdapter);
        }
    }


    @Override
    public int getLayoutId() {
        return R.layout.activity_my_favorite;
    }

    @Override
    public void init() {
        initFavorite();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        initFavorite();
    }

    @Override
    public void setToolbar() {
        getToolbar().setTitle("我的收藏");
        getToolbar().setleftButtonIcon(R.drawable.icon_back_32px);
    }

}
