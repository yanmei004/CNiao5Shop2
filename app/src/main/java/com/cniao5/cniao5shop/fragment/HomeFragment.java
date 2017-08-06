package com.cniao5.cniao5shop.fragment;

import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.cniao5.cniao5shop.R;
import com.cniao5.cniao5shop.activity.WaresListActivity;
import com.cniao5.cniao5shop.adapter.HomeCampaignAdapter;
import com.cniao5.cniao5shop.bean.Banner;
import com.cniao5.cniao5shop.bean.Campaign;
import com.cniao5.cniao5shop.bean.HomeCampaign;
import com.cniao5.cniao5shop.net.ServiceGenerator;
import com.cniao5.cniao5shop.net.SubscriberCallBack;
import com.cniao5.cniao5shop.widget.Constants;
import com.daimajia.slider.library.SliderLayout;
import com.lidroid.xutils.view.annotation.ViewInject;

import java.util.List;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 主页
 * AndroidImageSlider 轮播广告的实现：SliderLayout
 * RecyclerView 商品分类展示：
 */
public class HomeFragment extends BaseFragment {

    @ViewInject(R.id.slider)
    private SliderLayout mSliderLayout;

    @ViewInject(R.id.recyclerview)
    private RecyclerView mRecyclerView;

    private HomeCampaignAdapter mAdatper;

    @Override
    public void setToolbar() {
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_home;
    }

    @Override
    public void init() {

        initAdapter();

        ServiceGenerator.getRetrofit(getActivity())
                .getBanner(1)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SubscriberCallBack<List<Banner>>(getActivity(),false) {
                    @Override
                    public void onSuccess(List<Banner> result) {
                        mAdatper.setBanners(result);
                    }
                });

        ServiceGenerator.getRetrofit(getActivity())
                .getHome("")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SubscriberCallBack<List<HomeCampaign>>(getActivity(),true) {
                    @Override
                    public void onSuccess(List<HomeCampaign> result) {
                        mAdatper.setDatas(result);
                    }
                });

    }

    private void initAdapter() {
        mAdatper = new HomeCampaignAdapter(getContext());

        mAdatper.setOnCampaignClickListener(new HomeCampaignAdapter.OnCampaignClickListener() {
            @Override
            public void onClick(View view, Campaign campaign) {
                Intent intent = new Intent(getActivity(), WaresListActivity.class);
                intent.putExtra(Constants.CAMPAIGN_ID,campaign.getId());
                startActivity(intent);
            }
        });

        mRecyclerView.setAdapter(mAdatper);
//        mRecyclerView.addItemDecoration(new DividerItemDecortion(getContext(),DividerItemDecortion.VERTICAL_LIST));
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this.getActivity()));
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        mSliderLayout.stopAutoCycle();
    }
}
