package com.cniao5.cniao5shop.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.cniao5.cniao5shop.R;
import com.cniao5.cniao5shop.bean.Order;
import com.cniao5.cniao5shop.bean.OrderItem;
import com.cniao5.cniao5shop.utils.ScreenUtil;
import com.cniao5.cniao5shop.utils.ToastUtils;
import com.squareup.picasso.Picasso;
import com.w4lle.library.NineGridAdapter;
import com.w4lle.library.NineGridlayout;

import java.util.List;

/**
 * 我的订单适配器
 */
public class MyOrderAdapter extends SimpleAdapter<Order>{

    public OnItemWaresClickListener onItemWaresClickListener;

    public MyOrderAdapter(Context context, List<Order> datas,OnItemWaresClickListener onItemWaresClickListener) {
        super(context, datas, R.layout.template_my_orders);

        this.onItemWaresClickListener = onItemWaresClickListener;
    }

    @Override
    public void bindData(BaseViewHolder holder, final Order order) {
        holder.getTextView(R.id.tv_order_num).setText("订单号："+order.getOrderNum());
        holder.getTextView(R.id.tv_order_money).setText("实付金额：" + order.getAmount());
        TextView mTvStatus = holder.getTextView(R.id.tv_status);

        Button mBtnBuyMore = holder.getButton(R.id.btn_buy_more);
        Button mBtnComment = holder.getButton(R.id.btn_comment);

        mBtnBuyMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemWaresClickListener.onItemWaresClickListener(v,order);
            }
        });

        mBtnComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToastUtils.show(mContext,"功能正在完善...");
            }
        });

        //根据订单状态分别显示订单
        switch (order.getStatus()){

            case Order.STATUS_SUCCESS:
                mTvStatus.setText("成功");
                mTvStatus.setTextColor(Color.parseColor("#ff4CAF50"));
                break;

            case Order.STATUS_PAY_FAIL:
                mTvStatus.setText("支付失败");
                mTvStatus.setTextColor(Color.parseColor("#ffF44336"));
                break;

            case Order.STATUS_PAY_WAIT:
                mTvStatus.setText("等待支付");
                mTvStatus.setTextColor(Color.parseColor("#ffFFEB3B"));
                break;
        }

        NineGridlayout nineGridlayout = (NineGridlayout) holder.getView(R.id.iv_ngrid_layout);
        nineGridlayout.setGap(10);
        nineGridlayout.setDefaultWidth(ScreenUtil.getScreenWidth(mContext) / 4);
        nineGridlayout.setDefaultHeight(ScreenUtil.getScreenWidth(mContext) / 4);
        nineGridlayout.setAdapter(new OrderItemAdapter(mContext,order.getItems()));
    }

    class OrderItemAdapter extends NineGridAdapter{
        private List<OrderItem> items;

        public OrderItemAdapter(Context context, List<OrderItem> items) {
            super(context, items);
            this.items = items;
        }

        @Override
        public int getCount() {
            return (items == null) ? 0 : items.size();
        }

        @Override
        public String getUrl(int position) {
            OrderItem item = items.get(position);
            return item == null ? null : item.getWares().getImgUrl();
        }

        @Override
        public OrderItem getItem(int position) {
            return (items == null) ? null : items.get(position);
        }

        @Override
        public long getItemId(int position) {
            OrderItem item = items.get(position);
        return item == null ? null : item.getOrderId();
        }

        @Override
        public View getView(int i, View view) {
            ImageView iv = new ImageView(context);
            iv.setScaleType(ImageView.ScaleType.CENTER_CROP);
            iv.setBackgroundColor(Color.parseColor("#f5f5f5"));
            Picasso.with(context).load(getUrl(i)).placeholder(new ColorDrawable(Color.parseColor("#f5f5f5"))).into(iv);
            return iv;
        }

    }

    public interface OnItemWaresClickListener{
        void onItemWaresClickListener(View v,Order order);
    }
}
