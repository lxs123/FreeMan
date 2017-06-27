package com.toocms.freeman.ui.pay;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.Selection;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.NumberKeyListener;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.toocms.freeman.R;
import com.toocms.freeman.https.Contract;
import com.toocms.freeman.ui.BaseAty;

import org.xutils.common.util.LogUtil;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import cn.zero.android.common.util.JSONUtils;
import cn.zero.android.common.view.FButton;

/**
 * 结算
 * Created by admin on 2017/3/28.
 */

public class SettlementAty extends BaseAty {
    @ViewInject(R.id.settlement_money)
    EditText editMoney;
    @ViewInject(R.id.settlement_title)
    TextView tvTitle;
    @ViewInject(R.id.settlement_name)
    TextView tvName;
    @ViewInject(R.id.settlement_note)
    TextView tvNote;
    @ViewInject(R.id.make_sure_pay_fbtn)
    FButton PayFbtn;
    private Contract contract;
    /**
     * 是否补款[isAdequacyAmount]
     *
     * @param contract_noid 合同
     * @param noid          用户
     * @param amount        交易金额
     */

    /**
     * 开始发起结算[sponsorAdequacy]
     *
     * @param contract_noid
     * @param noid
     * @param amount        总额
     */

    /**
     * 发起退补款[sponsorDrawback]
     *
     * @param contract_noid 合同
     * @param noid
     * @param amount        总额
     */
    private String contract_noid;
    private String noid;
    private String money;
    public static boolean isSend;
    public static boolean isBackSend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (TextUtils.equals(getIntent().getStringExtra("flag"), "tuibu")) {
            mActionBar.setTitle("退补款");
            tvTitle.setText("退补款金额确认");
            tvName.setText("退补款金额：");
            tvNote.setText("注：");
            PayFbtn.setText("确认退补款");
        }
        editMoney.setText(getIntent().getStringExtra("money"));
        final Editable editMoneyText = editMoney.getText();
        Selection.setSelection(editMoneyText, editMoneyText.length());

        /**
         *  source    新输入的字符串
         *  start    新输入的字符串起始下标，一般为0
         *  end    新输入的字符串终点下标，一般为source长度-1
         *  dest    输入之前文本框内容
         *  dstart    原内容起始坐标，一般为0
         *  dend    原内容终点坐标，一般为dest长度-1
         */
        /**
         * 设置edittext里可输入的字符         */
        editMoney.setKeyListener(new NumberKeyListener() {
            @Override
            public int getInputType() {
                return InputType.TYPE_CLASS_PHONE;
            }

            @Override
            protected char[] getAcceptedChars() {
                String trim = editMoney.getText().toString().trim();
                LogUtil.e(editMoney.getSelectionStart() + "///////////");
                if (!TextUtils.isEmpty(trim)) {
                    if (editMoney.getSelectionStart() == 0) {
                        if (trim.contains(".")) {
                            return new char[]{'-', '1', '2', '3', '4', '5', '6', '7', '8', '9',
                                    '0', '+'};
                        }
                        return new char[]{'-', '1', '2', '3', '4', '5', '6', '7', '8', '9',
                                '0', '.', '+'};
                    } else if (trim.contains(".")) {
                        return new char[]{'1', '2', '3', '4', '5', '6', '7', '8', '9',
                                '0'};
                    } else if (TextUtils.equals(trim.substring(1), "-") || trim.length() > 0) {
                        return new char[]{'1', '2', '3', '4', '5', '6', '7', '8', '9',
                                '0', '.'};
                    } else {
                        return new char[]{'-', '1', '2', '3', '4', '5', '6', '7', '8', '9',
                                '0', '.', '+'};
                    }
                } else {
                    return new char[]{'-', '1', '2', '3', '4', '5', '6', '7', '8', '9',
                            '0', '.', '+'};
                }

            }
        });
        editMoney.addTextChangedListener(new TextWatcher() {
            int a = 0;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (s.toString().length() < 2) {
                    a = 0;
                }

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().contains(".")) {
                    if (s.length() - 1 - s.toString().indexOf(".") > 2) {
                        s = s.toString().subSequence(0, s.toString().indexOf(".") + 3);
                        editMoney.setText(s);
                        editMoney.setSelection(s.length());
                    }
                }
                if (s.toString().trim().substring(0).equals(".")) {
                    s = "0" + s;
                    editMoney.setText(s);
                    editMoney.setSelection(2);
                }

                if (s.toString().length() == 2 && a == 0) {
                    if (s.toString().substring(0, 2).equals("-0")) {
                        s = s + ".";
                        editMoney.setText(s);
                        editMoney.setSelection(3);
                        a = 1;
                    } else if (s.toString().substring(0, 2).equals("-.")) {
                        s = "-0.";
                        editMoney.setText(s);
                        editMoney.setSelection(3);
                        a = 1;
                    }
                }
                if (s.toString().startsWith("0") && s.toString().trim().length() > 1) {
                    if (!s.toString().substring(1, 2).equals(".")) {
                        editMoney.setText(s.subSequence(0, 1));
                        editMoney.setSelection(1);
                        return;
                    }
                }
                if (s.toString().contains(".") && s.toString().indexOf(".") == 0) {
                    if (s.toString().contains("-")) {
                        s = s.toString().replace(".-", "-0.");
                        editMoney.setText(s);
                    } else {
                        s = "0" + s;
                        editMoney.setText(s);
                    }

                    Editable text = editMoney.getText();
                    Selection.setSelection(text, text.length());
                }
                if (s.toString().contains("-.") && s.toString().indexOf(".") == 1) {
                    s = s.toString().replace("-.", "-0.");
                    editMoney.setText(s);
                    Editable text = editMoney.getText();
                    Selection.setSelection(text, text.length());
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }


    @Override
    protected int getLayoutResId() {
        return R.layout.aty_settlement;
    }

    @Override
    protected void initialized() {
        contract = new Contract();
    }

    @Override
    protected void requestData() {

    }

    @Override
    public void onComplete(RequestParams params, String result) {
        if (params.getUri().contains("Contract/isAdequacyAmount")) {
            final String data = JSONUtils.parseKeyAndValueToMap(result).get("data");
            if (TextUtils.equals(data, "0")) {
                if (TextUtils.equals(getIntent().getStringExtra("flag"), "tuibu")) {
                    contract.sponsorDrawback(contract_noid, noid, money, this);
                } else {
                    contract.sponsorAdequacy(contract_noid, noid, money, this);
                }
            } else {
                showDialog("", "付款金额不足，是否追加资金？", "确认追加", "取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Bundle bundle = new Bundle();
                        bundle.putString("total", data);
                        bundle.putString("flag", "settlement");
                        startActivity(PayAty.class, bundle);
                    }
                }, null);
            }
        } else if (params.getUri().contains("Contract/sponsorAdequacy")) {
//            showToast(JSONUtils.parseKeyAndValueToMap(result).get("message"));
            isSend = true;
            finish();
//            new Handler().postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    finish();
//                }
//            }, 1500);
        } else if (params.getUri().contains("Contract/sponsorDrawback")) {
            isBackSend = true;
            finish();
        }
        super.onComplete(params, result);
    }

    @Event({R.id.make_sure_pay_fbtn})
    private void onClick(View view) {
        switch (view.getId()) {
            case R.id.make_sure_pay_fbtn:
                money = editMoney.getText().toString();
                contract_noid = getIntent().getStringExtra("contract_noid");
                noid = application.getUserInfo().get("noid");
                if ((TextUtils.equals(money, "-") && money.length() == 1) || (TextUtils.equals(money, "-0.") && money.length() == 3)
                        || (TextUtils.equals(money, "0.") && money.length() == 2)) {
                    money = "0";
                }
                showProgressDialog();
                contract.isAdequacyAmount(contract_noid, noid, money, this);

//
                break;
        }
    }
}
