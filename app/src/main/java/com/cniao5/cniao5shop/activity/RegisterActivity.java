package com.cniao5.cniao5shop.activity;

import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.cniao5.cniao5shop.R;
import com.cniao5.cniao5shop.utils.ManifestUtil;
import com.cniao5.cniao5shop.utils.ToastUtils;
import com.cniao5.cniao5shop.widget.ClearEditText;
import com.lidroid.xutils.view.annotation.ViewInject;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;
import cn.smssdk.utils.SMSLog;

/**
 * 用户注册1
 */
public class RegisterActivity extends BaseActivity {

    private static final String DEFAULT_COUNTRY_ID = "42";

    @ViewInject(R.id.tv_Country)
    private TextView mTvCountry;

    @ViewInject(R.id.tv_country_code)
    private TextView mTvCountryCode;

    @ViewInject(R.id.et_phone)
    private ClearEditText mEtPhone;

    @ViewInject(R.id.et_pwd)
    private ClearEditText mEtPsw;

    private SMSEventHandler eventHandler;
    private String TAG = "RegisterActivity";


    @Override
    public int getLayoutId() {
        return R.layout.activity_register;
    }

    @Override
    public void init() {
        SMSSDK.initSDK(this, ManifestUtil.getMetaDataValue(this, "mob_sms_appKey"),
                ManifestUtil.getMetaDataValue(this, "mob_sms_appSectret"));

        eventHandler = new SMSEventHandler();
        SMSSDK.registerEventHandler(eventHandler);


        /**
         * 获取国家代码
         */
        String[] country = SMSSDK.getCountry(DEFAULT_COUNTRY_ID);
        if (country != null) {
            mTvCountry.setText(country[0]);
            mTvCountryCode.setText("+" + country[1]);
        }
    }

    @Override
    public void setToolbar() {
        getToolbar().setTitle("用户注册(1/2)");
        getToolbar().setRightButtonText("下一步");
        getToolbar().setleftButtonIcon(R.drawable.icon_back_32px);
        getToolbar().setRightButtonOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getCode();
            }
        });
    }

    /**
     * 验证手机号码合法性
     *
     * @param phone
     * @param code
     */
    private void checkPhoneNum(String phone, String code) {
        if (code.startsWith("+")) {
            code = code.substring(1);
        }

        if (TextUtils.isEmpty(phone)) {
            ToastUtils.show(this, "请输入手机号码");
            return;
        }

        if (code == "86") {
            if (phone.length() != 11) {
                ToastUtils.show(this, "手机号码长度不正确");
                return;
            }
        }

        String rule = "^1(3|5|7|8|4)\\d{9}";
        Pattern p = Pattern.compile(rule);
        Matcher m = p.matcher(phone);

        if (!m.matches()) {
            ToastUtils.show(this, "您输入的手机号码格式不正确");
            return;
        }
    }

    /**
     * 提交手机号码和国家代码
     */
    private void getCode() {
        String phone = mEtPhone.getText().toString().trim().replaceAll("\\s*", "");
        String countryCode = mTvCountryCode.getText().toString().trim();

        checkPhoneNum(phone, countryCode);

        //请求验证码，如果请求成功，则在EventHandler中回调并跳转到下一个注册页面
        SMSSDK.getVerificationCode(countryCode, phone);
    }


//    //通过SIM卡获取国家
//    private String[] getCurrentCountry() {
//        String mcc = this.getMCC();
//        String[] country = null;
//        if (!TextUtils.isEmpty(mcc)) {
//            country = SMSSDK.getCountryByMCC(mcc);
//        }
//
//        if (country == null) {
//            Log.w("SMSSDK", "no country found by MCC: " + mcc);
//            country = SMSSDK.getCountry("42");
//        }
//
//        return country;
//    }
//
//    private String getMCC() {
//        TelephonyManager tm = (TelephonyManager) this.getSystemService("phone");
//        String networkOperator = tm.getNetworkOperator();
//        return !TextUtils.isEmpty(networkOperator) ? networkOperator : tm.getSimOperator();
//    }


    class SMSEventHandler extends EventHandler {

        @Override
        public void afterEvent(final int event, final int result, final Object data) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (result == SMSSDK.RESULT_COMPLETE) {
                        if (event == SMSSDK.EVENT_GET_SUPPORTED_COUNTRIES) {

                            onCountryListGot((ArrayList<HashMap<String, Object>>) data);

                        } else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE) {

                            //请求验证码后，跳转到验证码填写页面
                            afterVerificationCodeRequested((Boolean) data);
                        } else if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {

                        }
                    } else {

                        //根据服务器返回的网络错误，给toast提示

                        try {
                            ((Throwable) data).printStackTrace();
                            Throwable throwable = (Throwable) data;

                            JSONObject object = null;
                            object = new JSONObject(
                                    throwable.getMessage());
                            String des = object.optString("detail");
                            if (!TextUtils.isEmpty(des)) {
                                ToastUtils.show(RegisterActivity.this, des);
                                return;
                            }
                        } catch (Exception e) {
                            SMSLog.getInstance().w(e);
                        }
                    }
                }
            });
        }
    }

    /**
     * 获取国家代码
     *
     * @param countries
     */
    private void onCountryListGot(ArrayList<HashMap<String, Object>> countries) {
        for (HashMap<String, Object> country : countries) {
            String code = (String) country.get("zone");
            String rule = (String) country.get("rule");

            if (TextUtils.isEmpty(code) || TextUtils.isEmpty(rule)) {
                continue;
            }

            Log.d(TAG, "code=" + code + ",rule=" + rule);
        }
    }

    /**
     * 传入国家代码，手机号码，密码并请求短信验证码，跳转到验证码填写页面
     */
    private void afterVerificationCodeRequested(boolean smart) {

        String phone = mEtPhone.getText().toString().trim().replaceAll("\\s*", "");
        String countryCode = mTvCountryCode.getText().toString().trim();
        String pwd = mEtPsw.getText().toString().trim();

        if (mEtPsw.getText().toString().length() < 6 || mEtPsw.getText().toString().length() > 20) {
            ToastUtils.show(this, "密码长度必须大于6位小于20位");
            return;
        }

        if (countryCode.startsWith("+")) {
            countryCode = countryCode.substring(1);
        }

        Intent intent = new Intent(RegisterActivity.this, Register2Activity.class);
        intent.putExtra("phone", phone);
        intent.putExtra("pwd", pwd);
        intent.putExtra("countryCode", countryCode);

        startActivityForResult(intent,1);
        setResult(2);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        SMSSDK.unregisterEventHandler(eventHandler);
    }
}
