package com.cniao5.cniao5shop.fragment;

import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import com.cjj.MaterialRefreshLayout;
import com.cjj.MaterialRefreshListener;
import com.cniao5.cniao5shop.R;
import com.cniao5.cniao5shop.adapter.BaseAdapter;
import com.cniao5.cniao5shop.adapter.CategoryAdapter;
import com.cniao5.cniao5shop.adapter.CategoryWaresAdapter;
import com.cniao5.cniao5shop.adapter.decoration.DividerGridItemDecoration;
import com.cniao5.cniao5shop.adapter.decoration.DividerItemDecortion;
import com.cniao5.cniao5shop.bean.Banner;
import com.cniao5.cniao5shop.bean.Category;
import com.cniao5.cniao5shop.bean.Page;
import com.cniao5.cniao5shop.bean.Wares;
import com.cniao5.cniao5shop.net.ServiceGenerator;
import com.cniao5.cniao5shop.net.SubscriberCallBack;
import com.daimajia.slider.library.Animations.DescriptionAnimation;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.DefaultSliderView;
import com.lidroid.xutils.view.annotation.ViewInject;

import java.util.List;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 分类列表
 */
public class CategoryFragment extends BaseFragment {

    @ViewInject(R.id.recyclerview_category)
    private RecyclerView mRecyclerViewText;

    @ViewInject(R.id.refreshlayout_category)
    private MaterialRefreshLayout mRefreshLayout;

    @ViewInject(R.id.recyclerview_category_wares)
    private RecyclerView mRecyclerViewWares;

    @ViewInject(R.id.sliderlayout_category)
    private SliderLayout mSliderLayout;

    //左边导航适配器
    private CategoryAdapter mCategoryAdapter;
    //wares数据显示适配器
    private CategoryWaresAdapter mWaresAdapter;

//    private OkHttpHelper mOkHttpHelper = OkHttpHelper.getInstance();

    private List<Banner> mBanners;
    private List<Wares> mDatas;

    private long category_id = 0;//左部导航id
    private int curPage = 1;
    private int totalPage = 1;
    private int totalCount = 28;
    private int pageSize = 10;

    private final int STATE_NORMAL = 0;
    private final int STATE_REFRESH = 1;
    private final int STATE_MORE = 2;
    private int state = STATE_NORMAL;


    public void setToolbar() {

    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_category;
    }

    @Override
    public void init() {
        requestCategoryData();
        requestBannerData();
        initRefreshLayout();
    }


    /**
     * wares数据刷新
     */
    private void initRefreshLayout() {
        mRefreshLayout.setLoadMore(true);

        mRefreshLayout.setMaterialRefreshListener(new MaterialRefreshListener() {
            @Override
            public void onRefresh(MaterialRefreshLayout materialRefreshLayout) {
                refreshData();
            }
            @Override
            public void onRefreshLoadMore(MaterialRefreshLayout materialRefreshLayout) {
                if (curPage * pageSize < totalCount)
                    loadMoreData();
                else {
                    Toast.makeText(getContext(), "没有数据了...", Toast.LENGTH_SHORT).show();
                    mRefreshLayout.finishRefreshLoadMore();
                }
            }
        });
    }

    private void loadMoreData() {
        curPage = ++curPage;
        state = STATE_MORE;
        requestWares(category_id);
    }

    private void refreshData() {
        curPage = 1;
        state = STATE_REFRESH;
        requestWares(category_id);
    }

    /**
     * 请求wares数据，并传入列表id
     * @param categoryId 传入的点击的列表id显示该id对应商品
     */
    private void requestWares(long categoryId) {

        ServiceGenerator.getRetrofit(getActivity())
                .getWaresList(categoryId,curPage,pageSize)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SubscriberCallBack<Page<Wares>>(getActivity(),true) {
                    @Override
                    public void onSuccess(Page<Wares> result) {
                        mDatas = result.getList();

                        curPage = result.getCurrentPage();

                        totalPage = result.getTotalPage();

                        totalCount = result.getTotalCount();

                        showCategoryWaresData();
                    }
                });
    }

    /**
     * 显示wares数据
     */
    private void showCategoryWaresData() {
        switch (state) {
            case STATE_NORMAL:
                if (mWaresAdapter == null) {
                    mWaresAdapter = new CategoryWaresAdapter(getContext(), mDatas);
                    mWaresAdapter.setOnItemClickListenner(new BaseAdapter.OnItemClickListenner() {
                        @Override
                        public void onItemClick(View view, int position) {
                            mWaresAdapter.showDetail(mWaresAdapter.getItem(position));
                        }
                    });
                    mRecyclerViewWares.setAdapter(mWaresAdapter);
                    mRecyclerViewWares.setLayoutManager(new GridLayoutManager(getContext(), 2));
                    mRecyclerViewWares.setItemAnimator(new DefaultItemAnimator());
                } else {
                    mWaresAdapter.clearData();
                    mWaresAdapter.addData(mDatas);
                }
                break;
            case STATE_MORE:
                mWaresAdapter.addData(mWaresAdapter.getDatas().size(), mDatas);
                mRecyclerViewWares.scrollToPosition(mWaresAdapter.getDatas().size());
                mRefreshLayout.finishRefreshLoadMore();
                break;
            case STATE_REFRESH:
                mWaresAdapter.clearData();
                mWaresAdapter.addData(mDatas);
                mRecyclerViewWares.setAdapter(mWaresAdapter);
                mRecyclerViewWares.scrollToPosition(0);
                mRefreshLayout.finishRefresh();
                break;
        }
    }

    /**
     * 请求左部导航菜单数据
     */
    private void requestCategoryData() {

        ServiceGenerator.getRetrofit(getActivity())
                .getCategory("")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SubscriberCallBack<List<Category>>(getActivity(),true) {
                    @Override
                    public void onSuccess(List<Category> result) {
                        showCategoryData(result);
                        if (result != null && result.size() > 0)
                            category_id = result.get(0).getId();

                        requestWares(category_id);
                    }
                });
    }

    /**
     * 左部导航
     * @param categories 导航列表
     */
    private void showCategoryData(List<Category> categories) {

        mCategoryAdapter = new CategoryAdapter(getContext(), categories);
        mRecyclerViewText.setAdapter(mCategoryAdapter);
        mCategoryAdapter.setOnItemClickListenner(new BaseAdapter.OnItemClickListenner() {
            @Override
            public void onItemClick(View view, int position) {

                //获取列表数据
                Category category = mCategoryAdapter.getItem(position);

                //获取列表数据id
                category_id = category.getId();

                curPage = 1;
                state = STATE_NORMAL;
                requestWares(category_id);

            }
        });
        mRecyclerViewText.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerViewText.setItemAnimator(new DefaultItemAnimator());
        mRecyclerViewText.addItemDecoration(new DividerItemDecortion(getContext(), DividerItemDecortion.VERTICAL_LIST));
    }

    /**
     * 请求轮播导航数据
     */
    private void requestBannerData() {

        ServiceGenerator.getRetrofit(getActivity())
                .getBanner(1)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SubscriberCallBack<List<Banner>>(getActivity(),true) {
                    @Override
                    public void onSuccess(List<Banner> result) {
                        mBanners = result;

                        initSlider();
                    }
                });
    }

    /**
     * 显示轮播数据
     */
    private void initSlider() {

        if (mBanners != null) {
            for (Banner banner : mBanners) {
                DefaultSliderView textSliderView = new DefaultSliderView(this.getActivity());
                textSliderView.image(banner.getImgUrl());
                textSliderView.description(banner.getName());
                textSliderView.setScaleType(BaseSliderView.ScaleType.Fit);
                mSliderLayout.addSlider(textSliderView);
            }
        }

        mSliderLayout.setPresetIndicator(SliderLayout.PresetIndicators.Right_Bottom);
        mSliderLayout.setCustomAnimation(new DescriptionAnimation());
        mSliderLayout.setPresetTransformer(SliderLayout.Transformer.Default);
        mSliderLayout.setDuration(3000);

    }

}