package com.toocms.freeman.ui.pay.wallet;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.toocms.frame.config.Settings;
import com.toocms.frame.tool.Commonly;
import com.toocms.freeman.R;
import com.toocms.freeman.https.Cash;
import com.toocms.freeman.https.Platform;
import com.toocms.freeman.ui.BaseAty;
import com.toocms.freeman.ui.util.MoneyUtils;

import org.xutils.common.util.LogUtil;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import java.util.ArrayList;
import java.util.Map;

import cn.zero.android.common.util.JSONUtils;
import cn.zero.android.common.util.ListUtils;
import cn.zero.android.common.util.MapUtils;

/**
 * Created by admin on 2017/3/29.
 */

public class TakeMoneyAty extends BaseAty {
    private static final int TAKE_CARD = 0X0001;
    @ViewInject(R.id.take_title_view)
    private View vLine;
    int position = 1;
    @ViewInject(R.id.take_to_card)
    TextView tvCard;
    @ViewInject(R.id.take_to_wx)
    TextView tvWx;
    @ViewInject(R.id.take_to_alipay)
    TextView tvAlipay;
    //    标识 （变量）
    @ViewInject(R.id.take_sign)
    TextView tvSign;
    TextView[] textViews;
    @ViewInject(R.id.take_bank_content)
    LinearLayout linlayBank;
    //银行卡名
    @ViewInject(R.id.take_bank_card)
    TextView tvBank;
    @ViewInject(R.id.take_bank_card_type)
    TextView tvBankType;
    @ViewInject(R.id.take_wx_content)
    TextView linlayWx;
    @ViewInject(R.id.take_money)
    EditText editMoney;
    @ViewInject(R.id.take_all_money)
    private TextView tvAllMoney;
    @ViewInject(R.id.take_can_money)
    private TextView tvCanMoney;
    @ViewInject(R.id.take_max_money)
    private TextView tvMaxMoney;
    private Platform platform;
    private String userid;
    private Cash cash;
    /**
     * 申请提现到微信账户[applyWithdrawByWechat]
     *
     * @param noid
     * @param amount 提现金额
     * @param userid 微信帐号ID
     */
    private String noid;
    private String amount;
    private String wxUserid;

    @Override
    protected int getLayoutResId() {
        return R.layout.aty_take_money;
    }

    @Override
    protected void initialized() {
        tvCanMoney.setText(getIntent().getStringExtra("can_money"));
        tvAllMoney.setText(getIntent().getStringExtra("all_money"));
        platform = new Platform();
        cash = new Cash();
    }

    @Override
    protected void requestData() {
        linlayBank.setVisibility(View.VISIBLE);
        platform.listing(application.getUserInfo().get("noid"), "BANK", this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        textViews = new TextView[]{tvCard, tvWx, tvAlipay};
        linlayWx.setVisibility(View.GONE);
        changeTextClr(1);
        MoneyUtils.setPrice(editMoney);
        tvMaxMoney.setText(getIntent().getStringExtra("money"));
    }


    @Override
    public void onComplete(RequestParams params, String result) {
        if (params.getUri().contains("Platform/listing")) {
            ArrayList<Map<String, String>> list = JSONUtils.parseDataToMapList(result);
            if (!ListUtils.isEmpty(list)) {
                Map<String, String> map = list.get(0);
                if (!MapUtils.isEmpty(map)) {
                    if (map.containsKey("userid")) {
                        linlayWx.setText(map.get("userid"));
                        userid = map.get("userid");
                    }
                    if (position == 1) {
                        if (map.containsKey("bank_name")) {
                            tvBank.setText(map.get("bank_name"));
                            if (userid.length() > 4) {
                                tvBankType.setText("尾号" + userid.substring(userid.length() - 4, userid.length()) + map.get("card_type_name"));
                            }
                        }
                    }

                }
            }

        } else if (params.getUri().contains("Cash/applyWithdrawByWechat") ||
                params.getUri().contains("Cash/applyWithdrawByAlipay") ||
                params.getUri().contains("Cash/applyWithdrawByBank")) {
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
    public void onError(Map<String, String> error) {
        LogUtil.e(error.toString());
        super.onError(error);
    }

    @Event({R.id.take_to_card, R.id.take_to_wx, R.id.take_to_alipay, R.id.take_bank_card_click
            , R.id.take})
    private void onClick(View view) {
        String noid = application.getUserInfo().get("noid");
        switch (view.getId()) {
            case R.id.take_to_card:
                position = 1;
                tvSign.setText("银行");
                linlayBank.setVisibility(View.VISIBLE);
                linlayWx.setVisibility(View.GONE);
                tvBankType.setText("");
                tvBank.setText("");
                showProgressDialog();
                platform.listing(noid, "BANK", this);
                break;
            case R.id.take_to_wx:
                position = 2;
                tvSign.setText("微信");
                linlayWx.setVisibility(View.VISIBLE);
                linlayBank.setVisibility(View.GONE);
                tvBankType.setText("");
                tvBank.setText("");
                linlayWx.setText("");
                showProgressDialog();
                platform.listing(noid, "WECHAT", this);
                break;
            case R.id.take_to_alipay:
                position = 3;
                tvSign.setText("支付宝");
                linlayBank.setVisibility(View.GONE);
                tvBankType.setText("");
                tvBank.setText("");
                linlayWx.setText("");
                linlayWx.setVisibility(View.VISIBLE);
                showProgressDialog();
                platform.listing(noid, "ALIPAY", this);
                break;
            case R.id.take_bank_card_click:
                Bundle bundle = new Bundle();
                if (position == 2) {
                    bundle.putString("flag", "wx");
                } else if (position == 1) {
                    bundle.putString("flag", "bank");
                } else {
                    bundle.putString("flag", "ali");
                }
                startActivityForResult(SelBankCardAty.class, bundle, TAKE_CARD);
                break;
            case R.id.take:
                if (Commonly.getViewText(editMoney).isEmpty()) {
                    showToast("请输入要提现的金额");
                    return;
                }
                if (position == 2) {
                    showProgressDialog();
                    cash.applyWithdrawByWechat(noid, editMoney.getText().toString(), userid, this);
                } else if (position == 3) {
                    cash.applyWithdrawByAlipay(noid, editMoney.getText().toString(), userid, this);
                } else {
                    cash.applyWithdrawByBank(noid, editMoney.getText().toString(), userid, this);
                }
                break;
        }
        changeTextClr(position);
        startTranslate(vLine, (Settings.displayWidth / 3) * (position - 1));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            return;
        }
        switch (requestCode) {
            case TAKE_CARD:
                switch (position) {
                    case 1:
                        userid = data.getStringExtra("userid");
                        tvBank.setText(data.getStringExtra("bank_name"));
                        tvBankType.setText("尾号" + data.getStringExtra("bank_type"));
                        break;
                    case 2:
                        linlayWx.setText(data.getStringExtra("userid"));
                        userid = data.getStringExtra("userid");
                        break;
                    case 3:
                        linlayWx.setText(data.getStringExtra("userid"));
                        userid = data.getStringExtra("userid");
                        break;
                }
                break;
        }
    }

    private void changeTextClr(int position) {
        for (int i = 1; i < 4; i++) {
            if (position == i) {
                textViews[i - 1].setSelected(true);
            } else {
                textViews[i - 1].setSelected(false);
            }
        }
    }

}
