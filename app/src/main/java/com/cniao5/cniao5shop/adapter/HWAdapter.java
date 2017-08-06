package com.cniao5.cniao5shop.adapter;


import android.content.Context;
import android.net.Uri;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.cniao5.cniao5shop.R;
import com.cniao5.cniao5shop.bean.Wares;
import com.cniao5.cniao5shop.utils.CartProvider;
import com.cniao5.cniao5shop.utils.ToastUtils;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.List;

/**
 * 热卖商品适配器
 */
public class HWAdapter extends SimpleAdapter<Wares>{

    private CartProvider cartProvider;

    public HWAdapter(Context context, List<Wares> datas) {
        super(context, datas, R.layout.template_hot_wares);

        cartProvider = CartProvider.getInstance(context);
    }

    @Override
    public void bindData(BaseViewHolder holder, final Wares wares) {

        TextView tvTitle = holder.getTextView(R.id.tv_title);
        TextView tvPrice = holder.getTextView(R.id.tv_price);
        Button button = holder.getButton(R.id.btn_add);
        SimpleDraweeView draweeView = (SimpleDraweeView) holder.getView(R.id.drawee_view);

        tvTitle.setText(wares.getName());
        tvPrice.setText("￥ " + wares.getPrice());
        draweeView.setImageURI(Uri.parse(wares.getImgUrl()));

        if (button != null){

            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //添加数据到购物车
                    cartProvider.put(wares);

                    ToastUtils.show(mContext, mContext.getString(R.string.has_add_cart));
                }
            });
        }

    }

    /**
     * 设置布局
     * @param layoutId
     */
    public void reSetLayout(int layoutId){
        this.mLayoutResId = layoutId;

        notifyItemRangeChanged(0, getDatas().size());
    }


}
