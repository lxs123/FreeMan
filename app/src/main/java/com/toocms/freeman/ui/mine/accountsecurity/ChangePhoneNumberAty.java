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

public class ChangePhoneNumberAty extends BaseAty {

    @ViewInject(R.id.c_p_n_phone)
    TextView tvPhone;
    @ViewInject(R.id.c_p_n_code)
    EditText editCode;
    @ViewInject(R.id.c_p_n_get_code)
    TextView tvGetCode;
    /**
     * 修改手机号1:发送验证码[sendEditAccountStepOneSMS]
     *
     * @param mobile 手机号
     * @param noid   用户编号
     */

    /**
     * 修改手机号1:检测验证码[checkEditAccountStepOneSMS]
     *
     * @param mobile 手机号
     * @param verify 验证码
     */
    private String verify;
    private String mobile;
    private String noid;
    private Account account;
    private AppCountdown appCountdown;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActionBar.setTitle("更换手机号");
        mobile = application.getUserInfo().get("account");
        noid = application.getUserInfo().get("noid");
        tvPhone.setText(mobile.substring(0, 3) + "****" + mobile.substring(7, mobile.length()));
        appCountdown = new AppCountdown();
        appCountdown.play(tvGetCode);

    }

    @Event({R.id.next_fbtn, R.id.c_p_n_get_code})
    private void onClick(View view) {
        verify = editCode.getText().toString().trim();
        switch (view.getId()) {
            case R.id.next_fbtn:
                if (TextUtils.isEmpty(verify)) {
                    showToast("请输入验证码");
                    return;
                }
                showProgressDialog();
                account.checkEditAccountStepOneSMS(mobile, verify, this);

                break;
            case R.id.c_p_n_get_code:
                if (TextUtils.isEmpty(mobile)) {
                    LogUtil.d("请检查手机号码");
                }
                showProgressDialog();
                account.sendEditAccountStepOneSMS(mobile, noid, this);
                break;
        }
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.aty_change_phone_number;
    }

    @Override
    protected void initialized() {
        account = new Account();
    }

    @Override
    protected void requestData() {

    }

    @Override
    public void onComplete(RequestParams params, String result) {
        if (params.getUri().contains("Account/sendEditAccountStepOneSMS")) {
            showToast(JSONUtils.parseKeyAndValueToMap(result).get("message"));
            appCountdown.start();
            tvGetCode.setEnabled(false);
            tvGetCode.setTextColor(Color.parseColor("#656565"));
        } else if (params.getUri().contains("Account/checkEditAccountStepOneSMS")) {
            Bundle bundle = new Bundle();
            bundle.putString("code", verify);
            startActivity(ChangePhoneNumberSureAty.class, bundle);
        }
        super.onComplete(params, result);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        appCountdown.stop();
    }
}
