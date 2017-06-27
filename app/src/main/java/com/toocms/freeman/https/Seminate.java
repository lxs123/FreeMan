package com.toocms.freeman.https;

import com.toocms.frame.web.ApiListener;
import com.toocms.frame.web.ApiTool;
import com.toocms.freeman.config.AppConfig;

import org.xutils.http.RequestParams;

/**
 * Created by admin on 2017/4/20.
 */

public class Seminate {
    String modle = getClass().getSimpleName();

    /**
     * 轮播图[getSlider]
     * 跳转方式
     * - 1 招工信息详情
     * - 2 注册页
     * - 3 html网页
     * - 4 个人中心
     * - 5 客服中心
     * - 6 我的劳务合同
     * - 7 文章列表
     */
    public void getSlider(ApiListener apiListener) {
        RequestParams params = new RequestParams(AppConfig.BASE_URL + modle + "/getSlider");
        ApiTool apiTool = new ApiTool();
        apiTool.postApi(params, apiListener);
    }

    /**
     * 推广奖励列表[invite]
     *
     * @param noid 用户id
     * @param page
     */
    public void invite(String noid, String page, ApiListener apiListener) {
        RequestParams params = new RequestParams(AppConfig.BASE_URL + "Trade/invite");
        params.addBodyParameter("noid", noid);
        params.addBodyParameter("page", page);
        ApiTool apiTool = new ApiTool();
        apiTool.postApi(params, apiListener);
    }

    /**
     * 意见反馈[send]
     *
     * @param contact 联系方式，只能支持手机号或邮箱
     * @param content 内容
     */
    public void send(String contact, String content, ApiListener apiListener) {
        RequestParams params = new RequestParams(AppConfig.BASE_URL + "Feedback/send");
        params.addBodyParameter("contact", contact);
        params.addBodyParameter("content", content);
        ApiTool apiTool = new ApiTool();
        apiTool.postApi(params, apiListener);
    }
}
