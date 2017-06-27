package com.toocms.freeman.ui.mine.accountsecurity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.toocms.freeman.R;
import com.toocms.freeman.https.Account;
import com.toocms.freeman.ui.BaseAty;

import org.xutils.common.util.LogUtil;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import cn.zero.android.common.util.JSONUtils;

public class DeleteAccountAty extends BaseAty {
    public static final int ID_CARD = 0x01;
    @ViewInject(R.id.delete_a_psw)
    EditText editPsw;
    /**
     * 申请删除帐号[delete]
     *
     * @param noid               用户编号
     * @param valid              校验参数，用户信息中valid参数
     * @param protected_password 安全密码
     * @param byperson           手持身份证，单图
     * @param byfront            身份证正面，单图
     * @param byback             身份证背面，单图
     */
    private String noid;
    private String valid;
    private String protected_password;
    private String byperson;
    private String byfront;
    private String byback;
    private Account account;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActionBar.setTitle("申请删除账号");
        noid = application.getUserInfo().get("noid");
        valid = application.getUserInfo().get("valid");
    }

    @Event({R.id.delete_a_to_photo, R.id.delete_a_submit})
    private void onClick(View view) {
        protected_password = editPsw.getText().toString().trim();
        switch (view.getId()) {
            case R.id.delete_a_to_photo:
                Bundle bundle = new Bundle();
                if (!TextUtils.isEmpty(byperson)){
                    bundle.putString("one",byperson);
                    bundle.putString("two",byfront);
                    bundle.putString("three",byback);
                }
                startActivityForResult(SubmitIDCardPhotoAty.class, bundle, ID_CARD);
                break;
            case R.id.delete_a_submit:
                if (TextUtils.isEmpty(noid)) {
                    LogUtil.e("请检查noid参数获取是否存在问题");
                    return;
                } else if (TextUtils.isEmpty(protected_password)) {
                    showToast("请输入安全密码");
                    return;
                } else if (TextUtils.isEmpty(byperson)) {
                    showToast("请提交手持身份证照片");
                    return;
                }
                showProgressDialog();
                account.delete(noid, valid, protected_password, byperson, byfront, byback, this);
                break;
        }
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.aty_delete_account;
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
        if (params.getUri().contains("Account/delete")) {
            showToast(JSONUtils.parseKeyAndValueToMap(result).get("message"));
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    finish();
                }
            }, 1500);
        }
        super.onComplete(params, result);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            return;
        }
        switch (requestCode) {
            case ID_CARD:
                byperson = data.getStringExtra("one");
                byfront = data.getStringExtra("two");
                byback = data.getStringExtra("three");
                break;

        }
    }
}
