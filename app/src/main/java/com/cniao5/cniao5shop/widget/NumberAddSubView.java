package com.cniao5.cniao5shop.widget;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.TintTypedArray;
import android.text.InputType;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cniao5.cniao5shop.R;
import com.cniao5.cniao5shop.utils.ToastUtils;

/**
 * 自定义加减控件
 */
public class NumberAddSubView extends LinearLayout implements View.OnClickListener {

    private Button mBtnAdd;
    private Button mBtnSub;
    private TextView mTvNum;

    private LayoutInflater mInflater;

    private int minValue = 1;
    private int maxValue;
    private int value;

    private OnButtonClickListener mOnButtonClickListener;

    public NumberAddSubView(Context context) {
        this(context, null);
    }

    public NumberAddSubView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NumberAddSubView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mInflater = LayoutInflater.from(context);

        initView();

        if (attrs != null) {
            TintTypedArray a = TintTypedArray.obtainStyledAttributes(context,
                    attrs,
                    R.styleable.NumberAddSubView,
                    defStyleAttr,
                    0);

            //value
            int value = a.getInt(R.styleable.NumberAddSubView_value, 0);
            setValue(value);

            //minValue
            int minValue = a.getInt(R.styleable.NumberAddSubView_minvalue, 0);
            setMinValue(minValue);

            //maxValue
            int maxValue = a.getInt(R.styleable.NumberAddSubView_maxvalue, 0);
            setMaxValue(maxValue);

            Drawable btnAdd = a.getDrawable(R.styleable.NumberAddSubView_btnAddBackground);
            Drawable btnSub = a.getDrawable(R.styleable.NumberAddSubView_btnSubBackground);
            Drawable textView = a.getDrawable(R.styleable.NumberAddSubView_textViewBackground);

            //设置控件背景
            setBtnAddBackground(btnAdd);
            setBtnSubBackground(btnSub);
            setTextViewBackground(textView);

            //回收
            a.recycle();
        }
    }

    private void setBtnSubBackground(Drawable drawable) {
        mBtnSub.setBackgroundDrawable(drawable);
    }

    private void setTextViewBackground(int drawableId) {
        setTextViewBackground(getResources().getDrawable(drawableId));
    }

    private void setTextViewBackground(Drawable drawable) {
        mTvNum.setBackgroundDrawable(drawable);
    }

    private void setBtnAddBackground(Drawable drawable) {
        mBtnAdd.setBackgroundDrawable(drawable);
    }

    private void initView() {
        //int resource, ViewGroup root, boolean attachToRoot：
        // root引用this原因是Layout继承ViewGroup，attachToRoot是否添加进ViewGroup
        View view = mInflater.inflate(R.layout.widget_number_add_sub, this, true);
        mBtnAdd = (Button) view.findViewById(R.id.btn_add);
        mBtnSub = (Button) view.findViewById(R.id.btn_sub);
        mTvNum = (TextView) view.findViewById(R.id.tv_num);
        mTvNum.setInputType(InputType.TYPE_NULL);
        mTvNum.setKeyListener(null);

        mBtnAdd.setOnClickListener(this);
        mBtnSub.setOnClickListener(this);
    }

    public void setOnButtonClickListener(OnButtonClickListener mOnButtonClickListener) {
        this.mOnButtonClickListener = mOnButtonClickListener;
    }

    //按钮接听接口
    public interface OnButtonClickListener {

        void onButtonAddClickListener(View view, int value);

        void onButtonSubClickListener(View view, int value);
    }

    public int getMinValue() {
        return minValue;
    }

    public void setMinValue(int minValue) {
        this.minValue = minValue;
    }

    public int getMaxValue() {
        return maxValue;
    }

    public void setMaxValue(int maxValue) {
        this.maxValue = maxValue;
    }

    //获取value
    public int getValue() {
        String val = mTvNum.getText().toString();
        //判断是否为数字
        String regex = "^(-?[1-9]\\d*\\.?\\d*)|(-?0\\.\\d*[1-9])|(-?[0])|(-?[0]\\.\\d*)$";
        if (val != null && !"".equals(val) && val.matches(regex))
            this.value = Integer.parseInt(val);
        return value;
    }

    public void setValue(int value) {
        mTvNum.setText(value + "");
        this.value = value;
    }


    //加
    private void numAdd() {
        if (value < maxValue) {
            value += 1;
        }
        //value是数字，必须强制转换成字符串，否则被认为是资源id
        mTvNum.setText(value + "");

    }

    //减
    private void numSub() {

        if (value > minValue) {
            value -= 1;
        }
        mTvNum.setText(value + "");

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_add) {
            numAdd();
            if (mOnButtonClickListener != null) {
                mOnButtonClickListener.onButtonAddClickListener(v, value);
            }
        } else if (v.getId() == R.id.btn_sub) {
            numSub();
            if (mOnButtonClickListener != null) {
                mOnButtonClickListener.onButtonSubClickListener(v, value);
            }
        }
    }
}
