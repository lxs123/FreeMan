package com.toocms.freeman.https;

import com.toocms.frame.web.ApiListener;
import com.toocms.frame.web.ApiTool;
import com.toocms.freeman.config.AppConfig;

import org.xutils.http.RequestParams;

/**
 * Created by admin on 2017/4/24.
 */

public class Document {
    String modle = getClass().getSimpleName();

    /**
     * 招工帮助列表[hiring]
     */
    public void hiring(ApiListener apiListener) {
        RequestParams params = new RequestParams(AppConfig.BASE_URL + modle + "/hiring");
        ApiTool apiTool = new ApiTool();
        apiTool.postApi(params, apiListener);
    }

    /**
     * 招工帮助单条[hireSingle]
     *
     * @param doc_id 文档id
     */
    public void hireSingle(String doc_id, ApiListener apiListener) {
        RequestParams params = new RequestParams(AppConfig.BASE_URL + modle + "/hireSingle");
        params.addBodyParameter("doc_id", doc_id);
        ApiTool apiTool = new ApiTool();
        apiTool.postApi(params, apiListener);
    }

    /**
     * 帮助中心列表[acolyte]
     */
    public void acolyte(ApiListener apiListener) {
        RequestParams params = new RequestParams(AppConfig.BASE_URL + modle + "/acolyte");
        ApiTool apiTool = new ApiTool();
        apiTool.postApi(params, apiListener);
    }

    /**
     * 帮助中心单条[acolyteSingle]
     *
     * @param doc_id 文章id
     */
    public void acolyteSingle(String doc_id, ApiListener apiListener) {
        RequestParams params = new RequestParams(AppConfig.BASE_URL + modle + "/acolyteSingle");
        params.addBodyParameter("doc_id", doc_id);
        ApiTool apiTool = new ApiTool();
        apiTool.postApi(params, apiListener);
    }

    /**
     * 关于我们[aboutus]
     */
    public void aboutus(ApiListener apiListener) {
        RequestParams params = new RequestParams(AppConfig.BASE_URL + modle + "/aboutus");
        ApiTool apiTool = new ApiTool();
        apiTool.postApi(params, apiListener);
    }

    /**
     * 推广帮助[pursue]
     */
    public void pursue(ApiListener apiListener) {
        RequestParams params = new RequestParams(AppConfig.BASE_URL + modle + "/pursue");
        ApiTool apiTool = new ApiTool();
        apiTool.postApi(params, apiListener);
    }

    /**
     * 搜工作帮助[working]
     */
    public void working(ApiListener apiListener) {
        RequestParams params = new RequestParams(AppConfig.BASE_URL + modle + "/working");
        ApiTool apiTool = new ApiTool();
        apiTool.postApi(params, apiListener);
    }

    /**
     * 搜工作详情[workingSingle]
     *
     * @param doc_id 文章id
     */
    public void workingSingle(String doc_id, ApiListener apiListener) {
        RequestParams params = new RequestParams(AppConfig.BASE_URL + modle + "/workingSingle");
        params.addBodyParameter("doc_id", doc_id);
        ApiTool apiTool = new ApiTool();
        apiTool.postApi(params, apiListener);
    }
}
