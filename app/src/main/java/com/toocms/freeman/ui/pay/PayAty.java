package com.toocms.freeman.ui.pay;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.toocms.freeman.R;
import com.toocms.freeman.https.Contract;
import com.toocms.freeman.ui.BaseAty;
import com.toocms.pay.Pay;
import com.toocms.pay.listener.PayStatusCallback;

import org.xutils.common.util.LogUtil;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import java.text.DecimalFormat;
import java.text.Format;
import java.util.Map;

import cn.zero.android.common.util.JSONUtils;


/**
 * 付款
 * Created by admin on 2017/3/4.
 */

public class PayAty extends BaseAty {
    @ViewInject(R.id.pay_money)
    TextView tvMoney;
    @ViewInject(R.id.pay_wx)
    TextView tvWx;
    @ViewInject(R.id.pay_ali)
    TextView tvAli;


    private int position = 1;
    private static boolean isPay = false;
    private static boolean isTuibuPay = false;
    private static boolean isDisputePay = false;
    /**
     * 模拟 - 支付预付款[testSignPay]
     *
     * @param contract_noid 合同单号
     * @param noid
     */
    private Contract contract;
    private static String contract_noid;
    private String sign;
    private String total;
    private String sn;
    private Format format = new DecimalFormat("#0.00");
    private String noid;

    @Override
    protected int getLayoutResId() {
        return R.layout.aty_pay;
    }

    @Override
    protected void initialized() {
        contract = new Contract();
    }

    @Override
    protected void requestData() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        noid = application.getUserInfo().get("noid");
    }


    @Override
    protected void onResume() {
        super.onResume();
//        if (isPay) {
//        }
        Pay.payStatusCallback(new PayStatusCallback() {
            @Override
            public void callback() {
                if (TextUtils.isEmpty(sn)) {
                    return;
                }
                contract.payCallback(sn, PayAty.this);
            }
        });
        total = getIntent().getStringExtra("total");
        tvMoney.setText("￥" + format.format(Double.parseDouble(total)));
    }

    @Event({R.id.pay_wx, R.id.pay_ali, R.id.pay_btn})
    private void onClick(View view) {
        contract_noid = getIntent().getStringExtra("contract_noid");
        switch (view.getId()) {
            case R.id.pay_wx:
                position = 1;
                tvWx.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_wet, 0, R.drawable.btn_select, 0);
                tvAli.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_pay, 0, R.drawable.btn_unselect, 0);
                break;
            case R.id.pay_ali:
                position = 2;
                tvWx.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_wet, 0, R.drawable.btn_unselect, 0);
                tvAli.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_pay, 0, R.drawable.btn_select, 0);
                break;
            case R.id.pay_btn:
                showProgressDialog();
                if (position == 1) {
                    //结算补款，或者同意结算的补款
                    if (TextUtils.equals(getIntent().getStringExtra("flag"), "settlement") ||
                            TextUtils.equals(getIntent().getStringExtra("flag"), "settle_repair") ||
                            TextUtils.equals(getIntent().getStringExtra("flag"), "tuibu_repair") ||
                            TextUtils.equals(getIntent().getStringExtra("flag"), "dispute_repair")) {
                        if (TextUtils.isEmpty(total)) {
                            LogUtil.e("error_total");
                            return;
                        }
                        contract.wechatAppendAmount(total, application.getUserInfo().get("noid"), this);
                    } else
                        contract.wechatpay(contract_noid, application.getUserInfo().get("noid"), this);
                } else {
                    if (TextUtils.equals(getIntent().getStringExtra("flag"), "settlement")) {
                        if (TextUtils.isEmpty(total)) {
                            LogUtil.e("error_total");
                            return;
                        }
                        contract.alipay2AppendAmount(total, application.getUserInfo().get("noid"), this);
                    } else
                        contract.alipay(contract_noid, application.getUserInfo().get("noid"), this);
                }
//                contract.testSignPay(contract_noid, application.getUserInfo().get("noid"), this);
                break;
        }
    }

    /**
     * 同意结算 -补款
     */
    public static final void PaySettleStatus(PayStatusCallback callback) {
        if (isPay) {
            callback.callback();
            isPay = false;
        }
    }

    /**
     * 同意退补款 - 补款
     */
    public static final void PayTuibuStatus(PayStatusCallback callback) {
        if (isTuibuPay) {
            callback.callback();
            isTuibuPay = false;
        }
    }

    /**
     * 同意纠纷 - 补款
     */
    public static final void PayDisputeStatus(PayStatusCallback callback) {
        if (isDisputePay) {
            callback.callback();
            isDisputePay = false;
        }
    }

    @Override
    public void onComplete(RequestParams params, String result) {
        if (params.getUri().contains("Contract/testSignPay")) {
            showToast(JSONUtils.parseKeyAndValueToMap(result).get("message"));
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    finish();
                }
            }, 1500);
        } else if (params.getUri().contains("Contract/wechatpay")) {

            Map<String, String> map = JSONUtils.parseDataToMap(result);
            sign = map.get("sign");
            sn = map.get("sn");
            Pay.pay(PayAty.this, result);
        } else if (params.getUri().contains("Contract/payCallback")) {
            if (TextUtils.equals(getIntent().getStringExtra("flag"), "settle_repair")) {
//                isPay = true;
                showProgressDialog();
                contract.acceptAdequancy(contract_noid, noid, PayAty.this);
            } else if (TextUtils.equals(getIntent().getStringExtra("flag"), "tuibu_repair")) {
                showProgressDialog();
                contract.acceptDrawback(contract_noid, noid, PayAty.this);
            } else if (TextUtils.equals(getIntent().getStringExtra("flag"), "dispute_repair")) {
                showProgressDialog();
                contract.appectIssue(contract_noid, application.getUserInfo().get("noid"), getIntent().getStringExtra("issue_id"), PayAty.this);
            } else {
                showToast(JSONUtils.parseKeyAndValueToMap(result).get("message"));
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        finish();
                    }
                }, 1500);
            }

        } else if (params.getUri().contains("Contract/wechatAppendAmount") ||
                params.getUri().contains("Contract/alipay")||
                params.getUri().contains("Contract/alipay2AppendAmount")) {
            LogUtil.e(result);
            Pay.pay(PayAty.this, result);
            Map<String, String> map = JSONUtils.parseDataToMap(result);
            sn = map.get("sn");
        } else if (params.getUri().contains("Contract/acceptAdequancy") ||
                params.getUri().contains("Contract/acceptDrawback") ||
                params.getUri().contains("Contract/appectIssue")) {
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

    //坚听返回键
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            showDialog("提示", "您的订单未支付，确定离开?", "确定", "取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            }, null);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    //    监听toolBar的返回图标
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 16908332:
                showDialog("提示", "您的订单未支付，确定离开?", "确定", "取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                }, null);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
