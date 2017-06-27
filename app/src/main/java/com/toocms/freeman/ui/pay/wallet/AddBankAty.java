package com.toocms.freeman.ui.pay.wallet;

import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.toocms.frame.tool.Commonly;
import com.toocms.freeman.R;
import com.toocms.freeman.https.Platform;
import com.toocms.freeman.ui.BaseAty;

import org.xutils.http.RequestParams;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import java.util.Map;

import cn.zero.android.common.util.JSONUtils;
import cn.zero.android.common.view.FButton;

/**
 * Created by admin on 2017/3/30.
 */

public class AddBankAty extends BaseAty {
    @ViewInject(R.id.add_wx_ali_content)
    LinearLayout linlayWxAli;
    @ViewInject(R.id.add_bank_name)
    EditText editName;
    @ViewInject(R.id.add_bank_card)
    EditText editCard;
    @ViewInject(R.id.add_bank_phone)
    EditText editPhone;
    @ViewInject(R.id.add_bank_card_type)
    TextView tvCardType;
    @ViewInject(R.id.add_wx_ali)
    private EditText editWxAli;
    @ViewInject(R.id.add_bank_content)
    private LinearLayout linlayBank;
    @ViewInject(R.id.add_b_sure)
    private FButton fbtnSure;
    /**
     * 绑定微信[bindWechat]
     *
     * @param noid        用户编号
     * @param platform_id 微信id
     */

    /**
     * 绑定银行卡[bindBank]
     *
     * @param noid       用户编号
     * @param bank_ident 银行卡id
     * @param mobile     手机号
     */
    private String name;
    private String bank_ident;
    private String mobile;
    private String noid;
    private String platform_id;
    private String flag;
    private Platform platform;
    private CharSequence mMs;

    @Override
    protected int getLayoutResId() {
        return R.layout.aty_add_bank;
    }

    @Override
    protected void initialized() {
        platform = new Platform();
    }

    @Override
    protected void requestData() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        flag = getIntent().getStringExtra("flag");
        if (TextUtils.equals(flag, "wx")) {
            mActionBar.setTitle("添加微信账户");
            linlayBank.setVisibility(View.GONE);
        } else if (TextUtils.equals(flag, "ali")) {
            mActionBar.setTitle("添加支付宝账号");
            editWxAli.setHint("请输入您的支付宝账号");
            linlayBank.setVisibility(View.GONE);
        } else {
            mActionBar.setTitle("添加银行卡");
            linlayWxAli.setVisibility(View.GONE);
            linlayBank.setVisibility(View.VISIBLE);
        }
        noid = application.getUserInfo().get("noid");
        editCard.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 16 || s.length() == 19) {
                    tvCardType.setVisibility(View.VISIBLE);
                    mMs = s;
                    platform.bankType(s.toString(), AddBankAty.this);
                } else {
                    tvCardType.setText("");
                    tvCardType.setVisibility(View.GONE);
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    public void onComplete(RequestParams params, String result) {
        if (params.getUri().contains("Platform/bindWechat") ||
                params.getUri().contains("Platform/bindAlipay") ||
                params.getUri().contains("Platform/bindBank")) {
            showToast(JSONUtils.parseKeyAndValueToMap(result).get("message"));
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    finish();
                }
            }, 1500);

        } else if (params.getUri().contains("Platform/bankType")) {
            Map<String, String> map = JSONUtils.parseDataToMap(result);
            if (map.containsKey("card_type_name"))
                tvCardType.setText(map.get("bank_name") + map.get("card_type_name"));
        }
        super.onComplete(params, result);
    }

    @Override
    public void onError(Map<String, String> error) {
        if (!TextUtils.isEmpty(mMs))
            if (mMs.length() < 19) {
                return;
            } else {
                super.onError(error);
            }
        super.onError(error);
    }

    @Event(R.id.add_b_sure)
    private void onClick(View view) {
        platform_id = editWxAli.getText().toString().trim();
        switch (view.getId()) {
            case R.id.add_b_sure:
                if (TextUtils.equals(flag, "wx")) {
                    if (TextUtils.isEmpty(platform_id)) {
                        showToast("请输入微信账号");
                        return;
                    }
                    showProgressDialog();
                    platform.bindWechat(noid, platform_id, this);
                } else if (TextUtils.equals(flag, "ali")) {
                    if (TextUtils.isEmpty(platform_id)) {
                        showToast("请输入支付宝账号");
                        return;
                    }
                    showProgressDialog();
                    platform.bindAlipay(noid, platform_id, this);
                } else {
                    bank_ident = editCard.getText().toString().trim();
                    name = editName.getText().toString().trim();
                    mobile = editPhone.getText().toString().trim();
                    if (TextUtils.isEmpty(name)) {
                        showToast("请输入银行卡户名");
                        return;
                    } else if (TextUtils.isEmpty(bank_ident)) {
                        showToast("请输入银行卡号");
                        return;
                    } else if (Commonly.getViewText(editCard).length() < 16) {
                        showToast("请输入正确格式的银行卡号");
                        return;
                    } else if (TextUtils.isEmpty(mobile)) {
                        showToast("请输入银行预留手机号");
                        return;
                    } else if (mobile.length() < 11) {
                        showToast("请输入正确格式的手机号");
                        return;
                    } else if (TextUtils.isEmpty(tvCardType.getText())) {
                        showToast("请输入正确格式的银行卡号");
                        return;
                    }
                    showProgressDialog();
                    platform.bindBank(noid, bank_ident, mobile, name, this);
                }
                break;
        }
    }
}
