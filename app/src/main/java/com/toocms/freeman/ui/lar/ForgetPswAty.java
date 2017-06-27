package com.toocms.freeman.ui.lar;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.toocms.frame.tool.Commonly;
import com.toocms.freeman.R;
import com.toocms.freeman.config.AppCountdown;
import com.toocms.freeman.https.Account;
import com.toocms.freeman.ui.BaseAty;

import org.xutils.http.RequestParams;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import cn.zero.android.common.util.JSONUtils;

/**
 * Created by admin on 2017/4/11.
 */

public class ForgetPswAty extends BaseAty {
    @ViewInject(R.id.forget_phone)
    EditText editPhone;
    @ViewInject(R.id.forget_code)
    EditText editCode;
    @ViewInject(R.id.forget_get_code)
    TextView tvGetCode;
    private AppCountdown appCountdown;
    /**
     * 发送找回密码验证码[sendForgotSMS]
     *
     * @param mobile 手机号
     */

    /**
     * 检测找回密码验证码[checkForgotSMS]
     *
     * @param mobile 手机号
     * @param verify 验证码
     */
    private String mobile;
    private String verify;
    private Account account;

    @Override
    protected int getLayoutResId() {
        return R.layout.aty_forget_psd;
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
        mobile = getIntent().getStringExtra("phone");
        editPhone.setText(mobile);
    }

    @Event({R.id.forget_get_code, R.id.forget_fbtn})
    private void onViewClicked(View view) {
        mobile = editPhone.getText().toString();
        verify = editCode.getText().toString();
        switch (view.getId()) {
            case R.id.forget_get_code:
                if (Commonly.getViewText(editPhone).isEmpty()) {
                    showToast("请输入手机号");
                    return;
                } else if (Commonly.getViewText(editPhone).length() < 11) {
                    showToast("提示：手机号为11位");
                    return;
                }
                account.sendForgotSMS(mobile, this);
                break;
            case R.id.forget_fbtn:
                if (Commonly.getViewText(editPhone).isEmpty()) {
                    showToast("请输入手机号");
                    return;
                } else if (Commonly.getViewText(editPhone).length() < 11) {
                    showToast("提示：手机号为11位");
                    return;
                } else if (Commonly.getViewText(editCode).isEmpty()) {
                    showToast("请输入验证码");
                    return;
                } else if (Commonly.getViewText(editCode).length() < 6) {
                    showToast("提示：请输入完整的验证码");
                    return;
                }
                account.checkForgotSMS(mobile, verify, this);
                break;
        }
    }

    @Override
    public void onComplete(RequestParams params, String result) {
        if (params.getUri().contains("Account/sendForgotSMS")) {
            appCountdown.start();
            tvGetCode.setEnabled(false);
            tvGetCode.setTextColor(Color.parseColor("#666666"));
            showToast(JSONUtils.parseKeyAndValueToMap(result).get("message"));
        } else if (params.getUri().contains("Account/checkForgotSMS")) {
            Bundle bundle = new Bundle();
            bundle.putString("phone", mobile);
            startActivity(SetPswAty.class, bundle);
        }
        super.onComplete(params, result);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        appCountdown.stop();
    }
}
