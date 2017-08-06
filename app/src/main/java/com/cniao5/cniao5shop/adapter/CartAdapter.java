package com.cniao5.cniao5shop.adapter;

import android.content.Context;
import android.net.Uri;
import android.text.Html;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import com.cniao5.cniao5shop.R;
import com.cniao5.cniao5shop.bean.ShoppingCart;
import com.cniao5.cniao5shop.utils.CartProvider;
import com.cniao5.cniao5shop.widget.NumberAddSubView;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * 购物车
 */
public class CartAdapter extends SimpleAdapter<ShoppingCart> implements BaseAdapter.OnItemClickListenner {

    private CheckBox mCheckBox;
    private TextView mTextView;

    private CartProvider cartProvider;

    public CartAdapter(Context context, List<ShoppingCart> datas, CheckBox checkBox, TextView textView) {
        super(context, datas, R.layout.template_cart);
        this.mCheckBox = checkBox;
        this.mTextView = textView;

        cartProvider = CartProvider.getInstance(context);

        setCheckBox(checkBox);
        setTextView(textView);

        setOnItemClickListenner(this);

        showTotalPrice();

    }


    public void setTextView(TextView textView) {
        this.mTextView = textView;
    }

    public void setCheckBox(CheckBox checkBox) {

        this.mCheckBox = checkBox;

        mCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAll_None(mCheckBox.isChecked());
                showTotalPrice();
            }
        });
    }

    public void showTotalPrice() {
        float total = getTotalPrice();
        mTextView.setText(Html.fromHtml(
                        "合计 ￥<span style='color:#eb4f38'>" + total + "</span>"),
                TextView.BufferType.SPANNABLE);

    }

    private float getTotalPrice() {

        float sum = 0;
        if (!isNull()) {
            return sum;
        }

        for (ShoppingCart cart : mDatas) {
            if (cart.isChecked())
                sum += cart.getCount() * Float.parseFloat(cart.getPrice());
        }

        return sum;
    }


    private boolean isNull() {
        return (mDatas != null && mDatas.size() > 0);
    }

    @Override
    public void bindData(BaseViewHolder holder, final ShoppingCart item) {

        holder.getTextView(R.id.tv_title_cart).setText(item.getName());
        holder.getTextView(R.id.tv_price_cart).setText("￥ " + item.getPrice());

        SimpleDraweeView draweeView = (SimpleDraweeView) holder.getView(R.id.drawee_view_cart);
        draweeView.setImageURI(Uri.parse(item.getImgUrl()));

        CheckBox checkBox = (CheckBox) holder.getView(R.id.checkbox);
        checkBox.setChecked(item.isChecked());

        NumberAddSubView numberAddSubView = (NumberAddSubView) holder.getView(R.id.add_sub_view);
        numberAddSubView.setValue(item.getCount());

        numberAddSubView.setOnButtonClickListener(new NumberAddSubView.OnButtonClickListener() {
            @Override
            public void onButtonAddClickListener(View view, int value) {
                item.setCount(value);
                cartProvider.update(item);
                showTotalPrice();
            }

            @Override
            public void onButtonSubClickListener(View view, int value) {
                item.setCount(value);
                cartProvider.update(item);
                showTotalPrice();
            }
        });
    }

    @Override
    public void onItemClick(View view, int position) {
        ShoppingCart cart = getItem(position);
        cart.setIsChecked(!cart.isChecked());
        notifyItemChanged(position);

        //不是全选要改变checkbox的状态
        checkListen();
        showTotalPrice();
    }

    //获取已选中的数据
    public List<ShoppingCart> getCheckData() {
        List<ShoppingCart> temp = new ArrayList<>();
        for (ShoppingCart cart : mDatas) {
            System.out.println(cart.getName() + "**11**" + cart.isChecked());
            if (cart.isChecked()) {
                System.out.println(cart.getName() + "**22**" + cart.isChecked());
                temp.add(cart);
            }
        }

        System.out.println("temp----" + temp.size());

        return temp;
    }

    private void checkListen() {
        int count = 0;

        int checkNum = 0;
        if (mDatas != null) {
            count = mDatas.size();

            for (ShoppingCart cart : mDatas) {
                if (!cart.isChecked()) {
                    mCheckBox.setChecked(false);
                    break;
                } else {
                    checkNum = checkNum + 1;
                }
            }

            if (count == checkNum) {
                mCheckBox.setChecked(true);
            }
        }
    }

    //全选或者全不选
    public void checkAll_None(boolean isChecked) {
        if (!isNull()) {
            return;
        }

        int i = 0;
        for (ShoppingCart cart : mDatas) {
            cart.setIsChecked(isChecked);
            notifyItemChanged(i);
            i++;
        }
    }


    public void delCart() {
        if (!isNull())
            return;

        //foreach循环遍历的是长度固定的数组，不能使用该循环
//        for (ShoppingCart cart:mDatas){
//            if (cart.isChecked()){
//                int position = mDatas.indexOf(cart);
//                cartProvider.delete(cart);
//                mDatas.remove(cart);
//                notifyItemChanged(position);
//            }
//        }

        //list长度会改变，不能使用foreach循环，使用迭代器实现遍历
        for (Iterator iterator = mDatas.iterator(); iterator.hasNext(); ) {
            ShoppingCart cart = (ShoppingCart) iterator.next();

            if (cart.isChecked()) {
                int position = mDatas.indexOf(cart);
                cartProvider.delete(cart);
                iterator.remove();
                notifyItemRemoved(position);

            }
        }
    }
}
