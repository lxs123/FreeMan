package com.toocms.freeman.ui.mine.accountsecurity;

import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.toocms.frame.tool.AppManager;
import com.toocms.freeman.R;
import com.toocms.freeman.https.Account;
import com.toocms.freeman.ui.BaseAty;

import org.xutils.common.util.LogUtil;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import cn.zero.android.common.util.JSONUtils;

public class SetSecurityPasswordAty extends BaseAty {
    @ViewInject(R.id.set_s_psw)
    EditText editPsw;
    @ViewInject(R.id.set_s_repsw)
    EditText editRepsw;
    /**
     * 设置安全密码[setSafePassword]
     *
     * @param noid           用户编号
     * @param verify         验证码
     * @param password       设置密码
     * @param password_again 重复密码
     */
    private String noid;
    private String verify;
    private String password;
    private String password_again;
    private Account account;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActionBar.setTitle("设置安全密码");
        verify = getIntent().getStringExtra("code");
        noid = application.getUserInfo().get("noid");
    }


    @Override
    protected int getLayoutResId() {
        return R.layout.aty_set_security_password;
    }

    @Override
    protected void initialized() {
        account = new Account();
    }

    @Override
    protected void requestData() {

    }

    @Event({R.id.set_s_repsw, R.id.set_s_fbtn})
    private void onViewClicked(View view) {
        password = editPsw.getText().toString().trim();
        password_again = editRepsw.getText().toString().trim();
        switch (view.getId()) {
            case R.id.set_s_repsw:
                if (TextUtils.isEmpty(password)) {
                    showToast("请设置安全密码");
                    return;
                }
                break;
            case R.id.set_s_fbtn:
                if (TextUtils.isEmpty(password)) {
                    showToast("请设置安全密码");
                    return;
                } else if (TextUtils.isEmpty(password_again)) {
                    showToast("请确认安全密码");
                    return;
                } else if (TextUtils.isEmpty(noid)) {
                    LogUtil.e("请检查noid参数获取是否存在问题");
                    return;
                }
                showProgressDialog();
                account.setSafePassword(noid, verify, password, password_again, this);
                break;
        }
    }

    @Override
    public void onComplete(RequestParams params, String result) {
        if (params.getUri().contains("Account/setSafePassword")) {
            showToast(JSONUtils.parseKeyAndValueToMap(result).get("message"));
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    AppManager.getInstance().killActivity(SecurityVerificationAty.class);
                    finish();
                }
            }, 1500);
        }
        super.onComplete(params, result);
    }
}
