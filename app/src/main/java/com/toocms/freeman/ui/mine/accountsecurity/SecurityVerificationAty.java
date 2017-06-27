package com.toocms.freeman.ui.mine.accountsecurity;

import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.toocms.freeman.R;
import com.toocms.freeman.config.AppCountdown;
import com.toocms.freeman.https.Account;
import com.toocms.freeman.ui.BaseAty;

import org.xutils.common.util.LogUtil;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import cn.zero.android.common.util.JSONUtils;

public class SecurityVerificationAty extends BaseAty {

    @ViewInject(R.id.s_v_phone)
    TextView tvPhone;
    @ViewInject(R.id.s_v_code)
    EditText editCode;
    @ViewInject(R.id.s_v_get_code)
    TextView tvGetCode;
    /**
     * 设置安全密码:发送验证码[sendSetSafePasswordSMS]
     *
     * @param noid   用户编号
     * @param mobile 用户手机号
     */

    /**
     * 设置安全密码:检测验证码[checkSetSafePasswordSMS]
     *
     * @param mobile 手机号
     * @param verify 验证码
     */
    private String noid;
    private String mobile;
    private Account account;
    private String verify;
    private AppCountdown appCountdown;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActionBar.setTitle("安全验证");
        noid = application.getUserInfo().get("noid");
        mobile = application.getUserInfo().get("account");
        tvPhone.setText(mobile.substring(0, 3) + "****" + mobile.substring(7, mobile.length()));
        appCountdown = new AppCountdown();
        appCountdown.play(tvGetCode);
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.aty_security_verification;
    }

    @Override
    protected void initialized() {
        account = new Account();
    }

    @Override
    protected void requestData() {

    }

    @Event({R.id.s_v_get_code, R.id.s_v_fbtn})
    private void onViewClicked(View view) {
        verify = editCode.getText().toString().trim();
        switch (view.getId()) {
            case R.id.s_v_get_code:
                if (TextUtils.isEmpty(noid)) {
                    LogUtil.e("请检查noid参数获取是否存在问题");
                    return;
                } else if (TextUtils.isEmpty(mobile)) {
                    LogUtil.e("请检查mobile参数获取是否存在问题");
                    return;
                }
                showProgressDialog();
                account.sendSetSafePasswordSMS(noid, mobile, this);
                break;
            case R.id.s_v_fbtn:
                if (TextUtils.isEmpty(verify)) {
                    showToast("请输入验证码");
                    return;
                }
                showProgressDialog();
                account.checkSetSafePasswordSMS(mobile, verify, this);

                break;
        }
    }

    @Override
    public void onComplete(RequestParams params, String result) {
        if (params.getUri().contains("Account/sendSetSafePasswordSMS")) {
            showToast(JSONUtils.parseKeyAndValueToMap(result).get("message"));
            appCountdown.start();
            tvGetCode.setEnabled(false);
            tvGetCode.setTextColor(Color.parseColor("#656565"));
        } else if (params.getUri().contains("Account/checkSetSafePasswordSMS")) {
            Bundle bundle = new Bundle();
            bundle.putString("code", verify);
            startActivity(SetSecurityPasswordAty.class, bundle);
        }
        super.onComplete(params, result);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        appCountdown.stop();
    }
}
