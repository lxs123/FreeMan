package com.toocms.freeman.ui.pay.wallet;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.toocms.freeman.R;
import com.toocms.freeman.https.User;
import com.toocms.freeman.ui.BaseAty;

import org.xutils.http.RequestParams;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import java.text.DecimalFormat;
import java.text.Format;
import java.util.Map;

import cn.zero.android.common.util.JSONUtils;

/**
 * Created by admin on 2017/3/29.
 */

public class MyWalletAty extends BaseAty {
    private User mUser;
    private String mBalance;
    private String mDisable;
    private String mPossible;
    @ViewInject(R.id.balance_tv)
    private TextView tvBalance;
    @ViewInject(R.id.disable_tv)
    private TextView tvDisable;
    @ViewInject(R.id.possible_tv)
    private TextView tvPossible;
    private Format format = new DecimalFormat("#0.00");
    @Override
    protected int getLayoutResId() {
        return R.layout.aty_my_wallet;
    }

    @Override
    protected void initialized() {
        mUser = new User();
    }

    @Override
    protected void requestData() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        showProgressDialog();
//        mUser.wallet(application.getUserInfo().get("noid"), this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        showProgressContent();
        mUser.wallet(application.getUserInfo().get("noid"), this);
    }

    @Override
    public void onComplete(RequestParams params, String result) {
        if (params.getUri().contains("User/wallet")) {
            Map<String, String> map = JSONUtils.parseDataToMap(result);
            mBalance = map.get("balance");
            mDisable = map.get("disable");
            mPossible = map.get("possible");
            tvBalance.setText("￥" + format.format(Double.parseDouble(mBalance)));
            tvDisable.setText("￥" + format.format(Double.parseDouble(mDisable)));
            tvPossible.setText("￥" + format.format(Double.parseDouble(mPossible)));
        }
        super.onComplete(params, result);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_pay_record, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_pay_record:      //提现记录
                Bundle bundle = new Bundle();
                bundle.putString("flag", "payment");
                startActivity(TransactionDetailAty.class, bundle);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Event({R.id.wallet_extension, R.id.wallet_take, R.id.wallet_my_take, R.id.wallet_t_record})
    private void onClick(View view) {
        switch (view.getId()) {
            case R.id.wallet_extension:     //
                startActivity(ExtensionAty.class, null);
                break;
            case R.id.wallet_take:
                Bundle bundle = new Bundle();
                bundle.putString("can_money", "￥" + mBalance);
                bundle.putString("money",mBalance);
                bundle.putString("all_money", "￥" + (Double.parseDouble(mBalance) + Double.parseDouble(mDisable)));
                startActivity(TakeMoneyAty.class, bundle);
                break;
            case R.id.wallet_my_take:
                startActivity(MyTakeAccountAty.class, null);
                break;
            case R.id.wallet_t_record:
                startActivity(TransactionDetailAty.class, null);
                break;
        }
    }
}
