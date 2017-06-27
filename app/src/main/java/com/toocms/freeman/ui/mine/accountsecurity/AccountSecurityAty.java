package com.toocms.freeman.ui.mine.accountsecurity;

import android.os.Bundle;
import android.view.View;

import com.toocms.freeman.R;
import com.toocms.freeman.ui.BaseAty;

import org.xutils.view.annotation.Event;

public class AccountSecurityAty extends BaseAty {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActionBar.setTitle("账号安全");
    }


    @Override
    protected int getLayoutResId() {
        return R.layout.aty_account_security;
    }

    @Override
    protected void initialized() {

    }

    @Override
    protected void requestData() {

    }

    @Event({R.id.a_s_change_phone, R.id.a_s_password, R.id.a_s_delete})
    private void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.a_s_change_phone://修改手机号
                startActivity(ChangePhoneNumberAty.class, null);
                break;
            case R.id.a_s_password://修改安全密码
                startActivity(SecurityVerificationAty.class, null);
                break;
            case R.id.a_s_delete://申请删除帐号
                startActivity(DeleteAccountAty.class, null);
                break;
        }
    }
}
