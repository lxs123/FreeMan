package com.toocms.freeman.ui.mine.accountsecurity;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.toocms.frame.tool.AppManager;
import com.toocms.freeman.R;
import com.toocms.freeman.config.AppCountdown;
import com.toocms.freeman.https.Account;
import com.toocms.freeman.ui.BaseAty;

import org.xutils.http.RequestParams;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import cn.zero.android.common.util.JSONUtils;

public class ChangePhoneNumberSureAty extends BaseAty {

    @ViewInject(R.id.c_p_n_new_phone)
    EditText editPhone;
    @ViewInject(R.id.c_p_n_new_code)
    EditText editCode;
    @ViewInject(R.id.c_p_n_new_get_code)
    TextView tvGetCode;
    /**
     * 修改手机号2:发送验证码[sendEditAccountStepTwoSMS]
     *
     * @param new_mobile 手机号
     * @param noid       用户编号
     * @param verify     旧手机号验证码
     */
    private String new_mobile;
    private String noid;
    private String verify;
    private Account account;
    /**
     * 修改手机号[editAccount]
     *
     * @param noid       用户编号
     * @param mobile     旧手机号
     * @param verify     旧手机号验证码
     * @param new_mobile 新手机号
     * @param new_verify 新手机号验证码
     */
    private String mobile;
    private String new_verify;
    private AppCountdown appCountdown;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActionBar.setTitle("更改手机号");
        noid = application.getUserInfo().get("noid");
        mobile = application.getUserInfo().get("account");
        verify = getIntent().getStringExtra("code");
        appCountdown = new AppCountdown();
        appCountdown.play(tvGetCode);
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.aty_change_phone_number_sure;
    }

    @Override
    protected void initialized() {
        account = new Account();
    }

    @Override
    protected void requestData() {

    }

    @Event({R.id.c_p_n_new_get_code, R.id.c_p_n_sure})
    private void onViewClicked(View view) {
        new_mobile = editPhone.getText().toString().trim();
        new_verify = editCode.getText().toString().trim();
        switch (view.getId()) {
            case R.id.c_p_n_new_get_code:
                if (TextUtils.isEmpty(new_mobile)) {
                    showToast("请输入新手机号");
                    return;
                } else if (new_mobile.length() < 11) {
                    showToast("手机号格式不正确");
                    return;
                }
                showProgressDialog();
                account.sendEditAccountStepTwoSMS(new_mobile, noid, verify, this);
                break;
            case R.id.c_p_n_sure:
                if (TextUtils.isEmpty(new_mobile)) {
                    showToast("请输入新手机号");
                    return;
                } else if (new_mobile.length() < 11) {
                    showToast("手机号格式不正确");
                    return;
                } else if (TextUtils.isEmpty(new_verify)) {
                    showToast("请输入验证码");
                    return;
                } else if (new_verify.length() < 6) {
                    showToast("验证码格式不正确");
                    return;
                }
                showProgressDialog();
                account.editAccount(noid, mobile, verify, new_mobile, new_verify, this);
                break;
        }
    }

    @Override
    public void onComplete(RequestParams params, String result) {
        if (params.getUri().contains("Account/sendEditAccountStepTwoSMS")) {
            showToast(JSONUtils.parseKeyAndValueToMap(result).get("message"));
            appCountdown.start();
            tvGetCode.setEnabled(false);
            tvGetCode.setTextColor(Color.parseColor("#656565"));
        } else if (params.getUri().contains("Account/editAccount")) {
            showToast(JSONUtils.parseKeyAndValueToMap(result).get("message"));
            application.setUserInfoItem("account", new_mobile);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    AppManager.getInstance().killActivity(ChangePhoneNumberAty.class);
                    finish();
                }
            }, 1500);
        }
        super.onComplete(params, result);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        appCountdown.stop();
    }
}
