package com.toocms.freeman.https;

import com.toocms.frame.web.ApiListener;
import com.toocms.frame.web.ApiTool;
import com.toocms.freeman.config.AppConfig;

import org.xutils.http.RequestParams;

/**
 * Created by admin on 2017/4/21.
 */

public class Message {
    String modle = getClass().getSimpleName();

    /**
     * 消息列表[listing]
     *
     * @param noid 用户编号
     * @param page 分页，默认为1
     */
    public void listing(String noid, String page, ApiListener apiListener) {
        RequestParams params = new RequestParams(AppConfig.BASE_URL + modle + "/listing");
        params.addBodyParameter("noid", noid);
        params.addBodyParameter("page", page);
        ApiTool apiTool = new ApiTool();
        apiTool.postApi(params, apiListener);
    }

    /**
     * 获取未读消息数量[hasUnread]
     *
     * @param noid 用户编号
     */
    public void hasUnread(String noid, ApiListener apiListener) {
        RequestParams params = new RequestParams(AppConfig.BASE_URL + modle + "/hasUnread");
        params.addBodyParameter("noid", noid);
        ApiTool apiTool = new ApiTool();
        apiTool.postApi(params, apiListener);
    }

    /**
     * 消息详情[detail]
     *
     * @param noid       用户编号
     * @param message_id 消息id
     */
    public void detail(String noid, String message_id, ApiListener apiListener) {
        RequestParams params = new RequestParams(AppConfig.BASE_URL + modle + "/detail");
        params.addBodyParameter("noid", noid);
        params.addBodyParameter("message_id", message_id);
        ApiTool apiTool = new ApiTool();
        apiTool.postApi(params, apiListener);
    }

}
