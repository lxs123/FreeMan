package com.toocms.freeman.https;

import com.toocms.frame.web.ApiListener;
import com.toocms.frame.web.ApiTool;
import com.toocms.freeman.config.AppConfig;

import org.xutils.http.RequestParams;

/**
 * Created by admin on 2017/4/20.
 */

public class Collect {
    String modle = getClass().getSimpleName();

    /**
     * 收藏用户[insertLab]
     *
     * @param noid
     * @param lab_noid
     */
    public void insertLab(String noid, String lab_noid, ApiListener apiListener) {
        RequestParams params = new RequestParams(AppConfig.BASE_URL + modle + "/insertLab");
        params.addBodyParameter("noid", noid);
        params.addBodyParameter("lab_noid", lab_noid);
        ApiTool apiTool = new ApiTool();
        apiTool.postApi(params, apiListener);
    }

    /**
     * 收藏用户列表[labList]
     *
     * @param noid      用户编号
     * @param page      分页，默认为1
     * @param code      筛选用户编号
     * @param min_price 最小单价
     * @param max_price 最大单价
     * @param skill_id  技能id数组
     */
    public void labList(String noid, String page, String code, String min_price, String max_price,
                        String skill_id, ApiListener apiListener) {
        RequestParams params = new RequestParams(AppConfig.BASE_URL + modle + "/labList");
        params.addBodyParameter("noid", noid);
        params.addBodyParameter("page", page);
        params.addBodyParameter("code", code);
        params.addBodyParameter("min_price", min_price);
        params.addBodyParameter("max_price", max_price);
        params.addBodyParameter("skill_id", skill_id);
        ApiTool apiTool = new ApiTool();
        apiTool.postApi(params, apiListener);
    }

    /**
     * 取消收藏用户[cancelLab]
     *
     * @param noid     用户编号
     * @param lab_noid 被收藏的用户noid
     */
    public void cancelLab(String noid, String lab_noid, ApiListener apiListener) {
        RequestParams params = new RequestParams(AppConfig.BASE_URL + modle + "/cancelLab");
        params.addBodyParameter("noid", noid);
        params.addBodyParameter("lab_noid", lab_noid);
        ApiTool apiTool = new ApiTool();
        apiTool.postApi(params, apiListener);
    }

    /**
     * 收藏招工单
     *
     * @param noid        用户编号
     * @param hire_noid   招工单编号
     * @param apiListener
     */
    public void insertHire(String noid, String hire_noid, ApiListener apiListener) {
        RequestParams params = new RequestParams(AppConfig.BASE_URL + modle + "/insertHire");
        params.addBodyParameter("noid", noid);
        params.addBodyParameter("hire_noid", hire_noid);
        ApiTool apiTool = new ApiTool();
        apiTool.postApi(params, apiListener);
    }

    /**
     * 取消收藏招工单
     *
     * @param noid        用户编号
     * @param hire_noid   招工单编号
     * @param apiListener
     */
    public void cancelHire(String noid, String hire_noid, ApiListener apiListener) {
        RequestParams params = new RequestParams(AppConfig.BASE_URL + modle + "/cancelHire");
        params.addBodyParameter("noid", noid);
        params.addBodyParameter("hire_noid", hire_noid);
        ApiTool apiTool = new ApiTool();
        apiTool.postApi(params, apiListener);
    }

    /**
     * 收藏合同[insertContract]
     *
     * @param noid          用户noid
     * @param contract_noid 合同编号
     */
    public void insertContract(String noid, String contract_noid, ApiListener apiListener) {
        RequestParams params = new RequestParams(AppConfig.BASE_URL + modle + "/insertContract");
        params.addBodyParameter("noid", noid);
        params.addBodyParameter("contract_noid", contract_noid);
        ApiTool apiTool = new ApiTool();
        apiTool.postApi(params, apiListener);
    }

    /**
     * 取消收藏合同[cancelContract]
     *
     * @param noid          用户noid
     * @param contract_noid 合同编号
     */
    public void cancelContract(String noid, String contract_noid, ApiListener apiListener) {
        RequestParams params = new RequestParams(AppConfig.BASE_URL + modle + "/cancelContract");
        params.addBodyParameter("noid", noid);
        params.addBodyParameter("contract_noid", contract_noid);
        ApiTool apiTool = new ApiTool();
        apiTool.postApi(params, apiListener);
    }

    /**
     * 收藏招工单列表[hireList]
     *
     * @param noid
     * @param page
     * @param keywords      关键词
     * @param skill_id      技能数组
     * @param province_name 省名称
     * @param city_name     市名称
     * @param area_name     区名称
     * @param min_price     最小金额
     * @param max_price     最大金额
     * @param min_subtotal  最小单价
     * @param max_subtotal  最大单价
     */
    public void hireList(String noid, String page, String keywords, String skill_id, String province_name, String city_name,
                         String area_name, String min_price, String max_price, String min_subtotal, String max_subtotal, ApiListener apiListener) {
        RequestParams params = new RequestParams(AppConfig.BASE_URL + modle + "/hireList");
        params.addBodyParameter("noid", noid);
        params.addBodyParameter("page", page);
        params.addBodyParameter("keywords", keywords);
        params.addBodyParameter("skill_id", skill_id);
        params.addBodyParameter("province_id", province_name);
        params.addBodyParameter("city_id", city_name);
        params.addBodyParameter("area_id", area_name);
        params.addBodyParameter("min_price", min_price);
        params.addBodyParameter("max_price", max_price);
        params.addBodyParameter("min_subtotal", min_subtotal);
        params.addBodyParameter("max_subtotal", max_subtotal);
        ApiTool apiTool = new ApiTool();
        apiTool.postApi(params, apiListener);
    }

    /**
     * 收藏合同列表[contractList]
     *
     * @param noid         用户编号
     * @param page         分页
     * @param keywords     关键字
     * @param skill_id     技能id数组
     * @param min_subtotal 最小单价
     * @param max_subtotal 最大单价
     * @param min_price    最小金额
     * @param max_price    最大金额
     */
    public void contractList(String noid, String page, String keywords, String skill_id, String min_subtotal, String max_subtotal
            , String min_price, String max_price, ApiListener apiListener) {
        RequestParams params = new RequestParams(AppConfig.BASE_URL + modle + "/contractList");
        params.addBodyParameter("noid", noid);
        params.addBodyParameter("page", page);
        params.addBodyParameter("keywords", keywords);
        params.addBodyParameter("skill_id", skill_id);
        params.addBodyParameter("min_subtotal", min_subtotal);
        params.addBodyParameter("max_subtotal", max_subtotal);
        params.addBodyParameter("min_price", min_price);
        params.addBodyParameter("max_price", max_price);
        ApiTool apiTool = new ApiTool();
        apiTool.postApi(params, apiListener);
    }
}
