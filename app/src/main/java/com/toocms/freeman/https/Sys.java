package com.toocms.freeman.https;

import com.toocms.frame.web.ApiListener;
import com.toocms.frame.web.ApiTool;
import com.toocms.freeman.config.AppConfig;

import org.xutils.http.RequestParams;

/**
 * Created by admin on 2017/4/15.
 */

public class Sys {
    String modle = getClass().getSimpleName();

    /**
     * 技能三级列表[getSkillList]
     *
     * @param skill_id 上级ID，顶级为0
     * @param layer    层级数，顶级为0
     */
    public void getSkillList(String skill_id, String layer, ApiListener apiListener) {
        RequestParams params = new RequestParams(AppConfig.BASE_URL + modle + "/getSkillList");
        params.addBodyParameter("skill_id", skill_id);
        params.addBodyParameter("layer", layer);
        ApiTool apiTool = new ApiTool();
        apiTool.postApi(params, apiListener);
    }

    /**
     * 单位三级列表[getUnitList]
     *
     * @param skill_id 上级ID，顶级为0
     * @param layer    层级数，顶级为0
     */
    public void getUnitList(String skill_id, String layer, ApiListener apiListener) {
        RequestParams params = new RequestParams(AppConfig.BASE_URL + modle + "/getUnitList");
        params.addBodyParameter("unit_id", skill_id);
        params.addBodyParameter("layer", layer);
        ApiTool apiTool = new ApiTool();
        apiTool.postApi(params, apiListener);
    }

    /**
     * 技能列表整体列表[getSkillAllList]
     */
    public void getSkillAllList(ApiListener apiListener) {
        RequestParams params = new RequestParams(AppConfig.BASE_URL + modle + "/getSkillAllList");
        ApiTool apiTool = new ApiTool();
        apiTool.postApi(params, apiListener);
    }
}
