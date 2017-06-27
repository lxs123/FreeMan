package com.toocms.freeman.https;

import com.toocms.frame.web.ApiListener;
import com.toocms.frame.web.ApiTool;
import com.toocms.freeman.config.AppConfig;

import org.xutils.http.RequestParams;

/**
 * Created by admin on 2017/5/24.
 */

public class Cash {
    String model = getClass().getSimpleName();

    /**
     * 申请提现到微信账户[applyWithdrawByWechat]
     *
     * @param noid
     * @param amount 提现金额
     * @param userid 微信帐号ID
     */
    public void applyWithdrawByWechat(String noid, String amount, String userid, ApiListener apiListener) {
        RequestParams params = new RequestParams(AppConfig.BASE_URL + model + "/applyWithdrawByWechat");
        params.addBodyParameter("noid", noid);
        params.addBodyParameter("amount", amount);
        params.addBodyParameter("userid", userid);
        ApiTool apiTool = new ApiTool();
        apiTool.postApi(params, apiListener);
    }

    /**
     * 申请提现到支付宝[applyWithdrawByAlipay]
     *
     * @param noid
     * @param amount 提现金额
     * @param userid 支付宝帐号ID
     */
    public void applyWithdrawByAlipay(String noid, String amount, String userid, ApiListener apiListener) {
        RequestParams params = new RequestParams(AppConfig.BASE_URL + model + "/applyWithdrawByAlipay");
        params.addBodyParameter("noid", noid);
        params.addBodyParameter("amount", amount);
        params.addBodyParameter("userid", userid);
        ApiTool apiTool = new ApiTool();
        apiTool.postApi(params, apiListener);
    }

    /**
     * 申请提现到银行卡[applyWithdrawByBank]
     *
     * @param noid
     * @param amount 提现金额
     * @param userid 银行卡号，必须是自己已绑定的卡号
     */
    public void applyWithdrawByBank(String noid, String amount, String userid, ApiListener apiListener) {
        RequestParams params = new RequestParams(AppConfig.BASE_URL + model + "/applyWithdrawByBank");
        params.addBodyParameter("noid", noid);
        params.addBodyParameter("amount", amount);
        params.addBodyParameter("userid", userid);
        ApiTool apiTool = new ApiTool();
        apiTool.postApi(params, apiListener);
    }
}
