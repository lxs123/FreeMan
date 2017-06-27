package com.toocms.freeman.ui.lar;

import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.toocms.freeman.R;
import com.toocms.freeman.config.AppCountdown;
import com.toocms.freeman.https.Account;
import com.toocms.freeman.ui.BaseAty;

import org.xutils.http.RequestParams;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import cn.zero.android.common.util.JSONUtils;
import cn.zero.android.common.view.KeyboardLayout;

/**
 * Created by admin on 2017/4/10.
 */

public class RegisterAty extends BaseAty {

    @ViewInject(R.id.register_logo)
    ImageView imgvLogo;
    @ViewInject(R.id.register_account)
    EditText editAccount;
    @ViewInject(R.id.register_code)
    EditText editCode;
    @ViewInject(R.id.register_get_code)
    TextView tvGetCode;
    @ViewInject(R.id.register_keyboard)
    KeyboardLayout registerKeyboard;
    private AppCountdown appCountdown;
    /**
     * 发送注册验证码[sendRegisterSMS]
     *
     * @param mobile 手机号
     */
    private String mobile;
    /**
     * 检测注册验证码[checkRegisterSMS]
     *
     * @param mobile 手机号
     * @param verify 验证码
     */
    String verify;
    private Account account;

    @Override

    protected int getLayoutResId() {
        return R.layout.aty_register;
    }

    @Override
    protected void initialized() {
        account = new Account();
    }

    @Override
    protected void requestData() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        appCountdown = new AppCountdown();
        appCountdown.play(tvGetCode);
    }

    @Event({R.id.register_get_code, R.id.register_to_psw})
    private void onViewClicked(View view) {
        mobile = editAccount.getText().toString().trim();
        verify = editCode.getText().toString().trim();
        switch (view.getId()) {
            case R.id.register_get_code:
                if (TextUtils.isEmpty(mobile)) {
                    showToast("请输入要注册的手机号");
                    return;
                } else if (mobile.length() < 11) {
                    showToast("请输入正确的手机号");
                    return;
                }
                showProgressDialog();

                account.sendRegisterSMS(mobile, this);

                break;
            case R.id.register_to_psw:
                if (TextUtils.isEmpty(mobile)) {
                    showToast("请输入要注册的手机号");
                    return;
                } else if (mobile.length() < 11) {
                    showToast("请输入正确的手机号");
                    return;
                } else if (TextUtils.isEmpty(verify)) {
                    showToast("请输入获取的验证码");
                    return;
                }
                showProgressDialog();
                account.checkRegisterSMS(mobile, verify, this);
                break;
        }
    }

    @Override
    public void onComplete(RequestParams params, String result) {
        if (params.getUri().contains("Account/sendRegisterSMS")) {
            showToast(JSONUtils.parseKeyAndValueToMap(result).get("message"));
            tvGetCode.setEnabled(false);
            tvGetCode.setTextColor(Color.parseColor("#666666"));
            appCountdown.start();
        } else if (params.getUri().contains("Account/checkRegisterSMS")) {
//            showToast(JSONUtils.parseKeyAndValueToMap(result).get("message"));
            Bundle bundle = new Bundle();
            bundle.putString("mobile", mobile);
            startActivity(RegisterPswAty.class, bundle);
        }
        super.onComplete(params, result);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        appCountdown.stop();
    }
}
