package com.cniao5.cniao5shop.utils;

import android.os.CountDownTimer;
import android.widget.TextView;

import com.cniao5.cniao5shop.R;

/**
 * 计时器
 */
public class CountTimerView extends CountDownTimer {

    public static final int TIME_COUNT = 61000;//时间防止从59s开始显示（以倒计时60s为例子）
    private TextView btn;
    private int endStrRid;

    /**
     * @param millisInFuture    倒计时总时间
     * @param countDownInterval 渐变时间
     * @param btn               点击的按钮，button是textview的子类，为了通用参数定义为textview
     * @param endStrRid         倒计时结束后，按钮对应显示的文字
     */
    public CountTimerView(long millisInFuture, long countDownInterval,TextView btn,int endStrRid) {
        super(millisInFuture, countDownInterval);
        this.btn = btn;
        this.endStrRid = endStrRid;
    }

    public CountTimerView(TextView btn,int endStrRid){
        super(TIME_COUNT,1000);
        this.btn = btn;
        this.endStrRid = endStrRid;
    }

    public CountTimerView(TextView btn){
        super(TIME_COUNT,1000);
        this.btn = btn;
        this.endStrRid = R.string.smssdk_resend_identify_code;

    }

    //计时过程
    @Override
    public void onTick(long millisUntilFinished) {
        btn.setEnabled(false);
        btn.setText(millisUntilFinished/1000+" 秒后可以重新发送");
    }

    //倒计时完毕
    @Override
    public void onFinish() {
        btn.setEnabled(true);
        btn.setText(endStrRid);
    }
}
