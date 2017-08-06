package com.cniao5.cniao5shop.utils;

import android.content.Context;
import android.widget.Toast;

import com.cjj.MaterialRefreshLayout;
import com.cjj.MaterialRefreshListener;
import com.cniao5.cniao5shop.bean.Page;
import com.cniao5.cniao5shop.http.OkHttpHelper;
import com.cniao5.cniao5shop.http.SpotsCallBack;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 分页类封装
 */
public class Pager {

    private static Builder builder;

    private OkHttpHelper helper;

    private final int STATE_NORMAL = 0;
    private final int STATE_REFRESH = 1;
    private final int STATE_MORE = 2;
    private int state = STATE_NORMAL;

    //私有构造函数
    private Pager() {
        helper = OkHttpHelper.getInstance();
        initRefreshLayout();
    }

    public void request() {
        requestData();
    }

    public void putParams(String key, Object value) {

        builder.params.put(key, value);

    }

    /**
     * 静态Builder实例化方法
     * @return
     */
    public static Builder newBuilder() {
        builder = new Builder();
        return builder;
    }

    /**
     * 刷新数据监听
     */
    private void initRefreshLayout() {

        builder.mRefreshLayout.setLoadMore(builder.canLoadMore);
        builder.mRefreshLayout.setMaterialRefreshListener(new MaterialRefreshListener() {
            @Override
            public void onRefresh(MaterialRefreshLayout materialRefreshLayout) {
                builder.mRefreshLayout.setLoadMore(builder.canLoadMore);
                refreshData();
            }

            @Override
            public void onRefreshLoadMore(MaterialRefreshLayout materialRefreshLayout) {

                if (builder.curPage * builder.pageSize < builder.totalCount) {
                    loadMoreData();
                } else {
                    ToastUtils.show(builder.context, "没有更多数据...");
                    builder.mRefreshLayout.finishRefreshLoadMore();
                    builder.mRefreshLayout.setLoadMore(false);
                }
            }
        });
    }

    /**
     * 刷新数据
     */
    private void refreshData() {
        builder.curPage = 1;
        state = STATE_REFRESH;
        requestData();
    }


    /**
     * 加载更多
     */
    private void loadMoreData() {
        builder.curPage = ++builder.curPage;
        state = STATE_MORE;
        requestData();
    }

    /**
     * 请求数据
     */
    private void requestData() {

        helper.doGet(buildeUrl(), new RequestCallBack(builder.context));

    }

    /**
     * T无法实现传值，必须重传一个type进来，并且继承父类的type
     * @param <T> 数据类型泛型
     */
    class RequestCallBack<T> extends SpotsCallBack<Page<T>> {

        public RequestCallBack(Context context) {
            super(context);

            super.mType = builder.type;
        }

        /**
         * 请求网络成功
         * @param response
         * @param page
         */
        @Override
        public void onSuccess(Response response, Page<T> page) {

            builder.curPage = page.getCurrentPage();

            builder.pageSize = page.getPageSize();

            builder.totalPage = page.getTotalPage();

            builder.totalCount = page.getTotalCount();

            showData(page.getList(), page.getTotalPage(), page.getTotalCount());
        }

        /**
         * 请求网络错误
         * @param response
         * @param code
         * @param e
         */
        @Override
        public void onError(Response response, int code, Exception e) {
            Toast.makeText(builder.context, "加载数据失败", Toast.LENGTH_LONG).show();

            if (STATE_REFRESH == state) {
                builder.mRefreshLayout.finishRefresh();
            } else if (STATE_MORE == state) {
                builder.mRefreshLayout.finishRefreshLoadMore();
            }
        }

        /**
         * 请求网络失败
         * @param request
         * @param e
         */
        @Override
        public void onFailure(Request request, IOException e) {

            dismissDialog();
            ToastUtils.show(builder.context, "请求错误：" + e.getMessage());

            if (STATE_REFRESH == state) {
                builder.mRefreshLayout.finishRefresh();
            } else if (STATE_MORE == state) {
                builder.mRefreshLayout.finishRefreshLoadMore();
            }

        }
    }

    /**
     * 构建url
     * @return
     */
    private String buildeUrl() {
        return builder.url + "?" + buildUrlParams();
    }

    /**
     * 构建url参数
     * @return
     */
    private String buildUrlParams() {
        HashMap<String, Object> map = builder.params;
        map.put("curPage", builder.curPage);
        map.put("pageSize", builder.pageSize);

        StringBuffer sb = new StringBuffer();
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            sb.append(entry.getKey() + "=" + entry.getValue());
            sb.append("&");
        }
        String s = sb.toString();

        //去掉最后一个&
        if (s.endsWith("&")) {
            s = s.substring(0, s.length() - 1);
        }
        return s;
    }

    /**
     * 显示数据
     * @param datas 数据
     * @param totalPage 总页数
     * @param totalCount 总数
     * @param <T> 数据类型集合
     */
    private <T> void showData(List<T> datas, int totalPage, int totalCount) {
        switch (state) {
            case STATE_NORMAL:

                if (builder.pageListener != null) {
                    builder.pageListener.load(datas, totalPage, totalCount);
                }
                break;
            case STATE_MORE://加载更多

                if (builder.pageListener != null) {
                    builder.pageListener.loadMore(datas, totalPage, totalCount);
                }
                builder.mRefreshLayout.finishRefreshLoadMore();
                break;
            case STATE_REFRESH://刷新

                if (builder.pageListener != null) {
                    builder.pageListener.refresh(datas, totalPage, totalCount);
                }
                builder.mRefreshLayout.finishRefresh();
                break;
            default:
                if (datas == null || datas.size() <=0){
                    ToastUtils.show(builder.context,"无法加载到数据");
                }
                break;
        }

    }

    /**
     * Builder辅助类，初始化变量
     */
    public static class Builder {

        private Context context;

        private Type type;

        private onPageListener pageListener;

        private MaterialRefreshLayout mRefreshLayout;

        private boolean canLoadMore;

        private int curPage = 1;
        private int totalPage = 1;
        private int pageSize = 10;
        private int totalCount = 28;

        private String url;

        //保存参数
        private HashMap<String, Object> params = new HashMap<>(5);

        //封装url
        public Builder setUrl(String url) {
            this.url = url;
            return builder;
        }

        //封装参数列表
        public Builder putParams(String key, Object value) {

            params.put(key, value);
            return builder;
        }

        //封装MaterialRefreshLayout
        public Builder setRefreshLayout(MaterialRefreshLayout refreshLayout) {
            this.mRefreshLayout = refreshLayout;
            return builder;
        }

        //封装pageSize
        public Builder setPageSize(int pageSize) {
            this.pageSize = pageSize;
            return builder;
        }

        //封装监听事件
        public Builder setPageListener(onPageListener pageListener) {
            this.pageListener = pageListener;
            return builder;
        }

        //是否可以加载更多
        public Builder setLoadMore(boolean loadMore) {
            this.canLoadMore = loadMore;
            return builder;
        }

        public Pager builder(Context context, Type type) {
            this.context = context;
            this.type = type;
            valida();

            return new Pager();
        }

        //判断content、refreshLayout、URL是否为空
        private void valida() {
            if (this.context == null)
                throw new RuntimeException("content cant't be null");
            if (this.url == null || "".equals(this.url))
                throw new RuntimeException("url cant't be null");
            if (this.mRefreshLayout == null)
                throw new RuntimeException("mRefreshLayout cant't be null");
        }
    }


    public interface onPageListener<T> {
        void load(List<T> datas, int totalPage, int totalCount);

        void refresh(List<T> datas, int totalPage, int totalCount);

        void loadMore(List<T> datas, int totalPage, int totalCount);
    }
}
