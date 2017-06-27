package com.toocms.freeman.ui.lar;

import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.toocms.freeman.R;
import com.toocms.freeman.https.Account;
import com.toocms.freeman.ui.BaseAty;

import org.xutils.http.RequestParams;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import cn.zero.android.common.util.JSONUtils;

/**
 * Created by admin on 2017/4/11.
 */

public class SetPswAty extends BaseAty {
    @ViewInject(R.id.forget_psw)
    EditText editPsw;
    @ViewInject(R.id.forget_repsw)
    EditText editRepsw;

    /**
     * 找回密码[forgotPassword]
     *
     * @param mobile         手机号
     * @param password       新密码
     * @param password_again 确认密码
     */
    private String mobile;
    private String password;
    private String password_again;
    private Account account;

    @Override
    protected int getLayoutResId() {
        return R.layout.aty_forget_set_psw;
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
        mobile = getIntent().getStringExtra("phone");

    }

    @Event({R.id.forget_repsw, R.id.forget_fbtn})
    private void onViewClicked(View view) {
        password = editPsw.getText().toString().trim();
        password_again = editRepsw.getText().toString().trim();
        switch (view.getId()) {
            case R.id.forget_repsw:
                if (TextUtils.isEmpty(password)) {
                    showToast("请先输入新密码");
                    return;
                }
                break;
            case R.id.forget_fbtn:
                if (TextUtils.isEmpty(password)) {
                    showToast("请输入新密码");
                    return;
                } else if (TextUtils.isEmpty(password_again)) {
                    showToast("请确认输入新密码");
                    return;
                }
                account.forgotPassword(mobile, password, password_again, this);

                break;
        }
    }

    @Override
    public void onComplete(RequestParams params, String result) {
        if (params.getUri().contains("Account/forgotPassword")) {
            showToast(JSONUtils.parseKeyAndValueToMap(result).get("message") + ",请登录！");
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Bundle bundle = new Bundle();
                    bundle.putString("phone", mobile);
                    bundle.putString("flag", "forget");
                    startActivity(LoginAty.class, bundle);
                }
            }, 1500);
        }
        super.onComplete(params, result);
    }
}
