package com.toocms.freeman.https;

import com.toocms.frame.web.ApiListener;
import com.toocms.frame.web.ApiTool;
import com.toocms.freeman.config.AppConfig;

import org.xutils.http.RequestParams;

/**
 * Created by admin on 2017/5/16.
 */

public class Platform {
    String modle = getClass().getSimpleName();

    /**
     * 绑定微信[bindWechat]
     *
     * @param noid        用户编号
     * @param platform_id 微信id
     */
    public void bindWechat(String noid, String platform_id, ApiListener apiListener) {
        RequestParams params = new RequestParams(AppConfig.BASE_URL + modle + "/bindWechat");
        params.addBodyParameter("noid", noid);
        params.addBodyParameter("platform_id", platform_id);
        ApiTool apiTool = new ApiTool();
        apiTool.postApi(params, apiListener);
    }

    /**
     * 绑定支付宝[bindAlipay]
     *
     * @param noid
     * @param platform_id 支付宝id
     */
    public void bindAlipay(String noid, String platform_id, ApiListener apiListener) {
        RequestParams params = new RequestParams(AppConfig.BASE_URL + modle + "/bindAlipay");
        params.addBodyParameter("noid", noid);
        params.addBodyParameter("platform_id", platform_id);
        ApiTool apiTool = new ApiTool();
        apiTool.postApi(params, apiListener);
    }

    /**
     * 绑定银行卡[bindBank]
     *
     * @param noid       用户编号
     * @param bank_ident 银行卡id
     * @param mobile     手机号
     */
    public void bindBank(String noid, String bank_ident, String mobile, String name, ApiListener apiListener) {
        RequestParams params = new RequestParams(AppConfig.BASE_URL + modle + "/bindBank");
        params.addBodyParameter("noid", noid);
        params.addBodyParameter("bank_ident", bank_ident);
        params.addBodyParameter("mobile", mobile);
        params.addBodyParameter("name", name);
        ApiTool apiTool = new ApiTool();
        apiTool.postApi(params, apiListener);
    }

    /**
     * 绑定列表[listing]
     *
     * @param noid    noid
     * @param partner partner 第三方类型： WECHAT ALIPAY BANK
     */
    public void listing(String noid, String partner, ApiListener apiListener) {
        RequestParams params = new RequestParams(AppConfig.BASE_URL + modle + "/listing");
        params.addBodyParameter("noid", noid);
        params.addBodyParameter("partner", partner);
        ApiTool apiTool = new ApiTool();
        apiTool.postApi(params, apiListener);
    }

    /**
     * 解绑微信[unbindWechat]
     *
     * @param noid
     * @param platform_id 微信id
     */
    public void unbindWechat(String noid, String platform_id, ApiListener apiListener) {
        RequestParams params = new RequestParams(AppConfig.BASE_URL + modle + "/unbindWechat");
        params.addBodyParameter("noid", noid);
        params.addBodyParameter("userid", platform_id);
        ApiTool apiTool = new ApiTool();
        apiTool.postApi(params, apiListener);
    }

    /**
     * 解绑支付宝[unbindAlipay]
     *
     * @param noid
     * @param platform_id 支付宝id
     */
    public void unbindAlipay(String noid, String platform_id, ApiListener apiListener) {
        RequestParams params = new RequestParams(AppConfig.BASE_URL + modle + "/unbindAlipay");
        params.addBodyParameter("noid", noid);
        params.addBodyParameter("userid", platform_id);
        ApiTool apiTool = new ApiTool();
        apiTool.postApi(params, apiListener);
    }

    /**
     * 解绑银行卡[unbindBankIdent]
     *
     * @param noid
     * @param bank_ident 银行卡id
     */
    public void unbindBankIdent(String noid, String bank_ident, ApiListener apiListener) {
        RequestParams params = new RequestParams(AppConfig.BASE_URL + modle + "/unbindBankIdent");
        params.addBodyParameter("noid", noid);
        params.addBodyParameter("bank_ident", bank_ident);
        ApiTool apiTool = new ApiTool();
        apiTool.postApi(params, apiListener);
    }

    /**
     * 获取银行卡类型[bankType]
     *
     * @param bank_ident
     */
    public void bankType(String bank_ident, ApiListener apiListener) {
        RequestParams params = new RequestParams(AppConfig.BASE_URL + modle + "/bankType");
        params.addBodyParameter("bank_ident", bank_ident);
        ApiTool apiTool = new ApiTool();
        apiTool.postApi(params, apiListener);
    }
}
