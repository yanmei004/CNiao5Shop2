package com.cniao5.cniao5shop.adapter;


import android.content.Context;
import java.util.List;

public abstract class SimpleAdapter<T> extends BaseAdapter<T,BaseViewHolder>{

    public SimpleAdapter(Context context, List<T> datas, int layoutResId) {
        super(context, datas, layoutResId);
    }

    public SimpleAdapter(Context mContext, int mLayoutResId) {
        super(mContext, mLayoutResId);
    }
}
