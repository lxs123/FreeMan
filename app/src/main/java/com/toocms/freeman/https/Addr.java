package com.toocms.freeman.https;

import com.toocms.frame.web.ApiListener;
import com.toocms.frame.web.ApiTool;
import com.toocms.freeman.config.AppConfig;

import org.xutils.http.RequestParams;

/**
 * Created by admin on 2017/4/12.
 */

public class Addr {
    String modle = getClass().getSimpleName();
    private ApiTool apiTool;

    /**
     * 地址 - 已添加列表[listing]
     *
     * @param noid 用户编号
     */
    public void listing(String noid, ApiListener apiListener) {
        RequestParams params = new RequestParams(AppConfig.BASE_URL + modle + "/listing");
        params.addBodyParameter("noid", noid);
        ApiTool apiTool = new ApiTool();
        apiTool.postApi(params, apiListener);
    }

    /**
     * 地址 - 添加[insert]
     *
     * @param noid          用户noid
     * @param longitude     经度
     * @param latitude      纬度
     * @param name          姓名
     * @param mobile        手机号
     * @param province_name 省名称
     * @param city_name     市名称
     * @param area_name     区名称
     * @param ress          详细地址
     * @param def           是否默认，默认时为1 其他值为不默认
     */
    public void insert(String noid, String longitude, String latitude, String name, String mobile,
                       String province_name, String city_name, String area_name, String ress,
                       String def, ApiListener apiListener) {
        RequestParams params = new RequestParams(AppConfig.BASE_URL + modle + "/insert");
        params.addBodyParameter("noid", noid);
        params.addBodyParameter("longitude", longitude);
        params.addBodyParameter("latitude", latitude);
        params.addBodyParameter("name", name);
        params.addBodyParameter("mobile", mobile);
        params.addBodyParameter("province_name", province_name);
        params.addBodyParameter("city_name", city_name);
        params.addBodyParameter("area_name", area_name);
        params.addBodyParameter("ress", ress);
        params.addBodyParameter("def", def);
        ApiTool apiTool = new ApiTool();
        apiTool.postApi(params, apiListener);
    }

    /**
     * 地址 - 修改[edit]
     *
     * @param noid          用户编号
     * @param addr_id       地址id
     * @param name          姓名
     * @param mobile        手机号
     * @param longitude     经度
     * @param latitude      纬度
     * @param province_name 省
     * @param city_name     市
     * @param area_name     区
     * @param ress          详细地址
     * @param def           是否默认
     */
    public void edit(String noid, String addr_id, String name, String mobile, String longitude, String latitude
            , String province_name, String city_name, String area_name, String ress, String def, ApiListener apiListener) {
        RequestParams params = new RequestParams(AppConfig.BASE_URL + modle + "/edit");
        params.addBodyParameter("noid", noid);
        params.addBodyParameter("addr_id", addr_id);
        params.addBodyParameter("name", name);
        params.addBodyParameter("mobile", mobile);
        params.addBodyParameter("longitude", longitude);
        params.addBodyParameter("latitude", latitude);
        params.addBodyParameter("province_name", province_name);
        params.addBodyParameter("city_name", city_name);
        params.addBodyParameter("area_name", area_name);
        params.addBodyParameter("ress", ress);
        params.addBodyParameter("def", def);
        ApiTool apiTool = new ApiTool();
        apiTool.postApi(params, apiListener);
    }

    /**
     * 地址 - 详细信息[single]
     *
     * @param noid    用户编号
     * @param addr_id 地址id
     */
    public void single(String noid, String addr_id, ApiListener apiListener) {
        RequestParams params = new RequestParams(AppConfig.BASE_URL + modle + "/single");
        params.addBodyParameter("noid", noid);
        params.addBodyParameter("addr_id", addr_id);
        apiTool = new ApiTool();
        apiTool.postApi(params, apiListener);
    }

    /**
     * 地址 - 删除[remove]
     *
     * @param noid    用户id
     * @param addr_id 地址id
     */
    public void remove(String noid, String addr_id, ApiListener apiListener) {
        RequestParams params = new RequestParams(AppConfig.BASE_URL + modle + "/remove");
        params.addBodyParameter("noid", noid);
        params.addBodyParameter("addr_id", addr_id);
        ApiTool apiTool = new ApiTool();
        apiTool.postApi(params, apiListener);
    }

    /**
     * 地址 - 设置为默认[setDefault]
     *
     * @param noid    用户id
     * @param addr_id 地址id
     */
    public void setDefault(String noid, String addr_id, ApiListener apiListener) {
        RequestParams params = new RequestParams(AppConfig.BASE_URL + modle + "/setDefault");
        params.addBodyParameter("noid", noid);
        params.addBodyParameter("addr_id", addr_id);
        ApiTool apiTool = new ApiTool();
        apiTool.postApi(params, apiListener);
    }

    /**
     * 地址 - 取消默认[cancelDefault]
     *
     * @param noid    用户id
     * @param addr_id 地址id
     */
    public void cancelDefault(String noid, String addr_id, ApiListener apiListener) {
        RequestParams params = new RequestParams(AppConfig.BASE_URL + modle + "/cancelDefault");
        params.addBodyParameter("noid", noid);
        params.addBodyParameter("addr_id", addr_id);
        ApiTool apiTool = new ApiTool();
        apiTool.postApi(params, apiListener);
    }

    /**
     * 已添加列表[territoryListing]
     *
     * @param noid 用户编号
     */
    public void territoryListing(String noid, ApiListener apiListener) {
        RequestParams params = new RequestParams(AppConfig.BASE_URL + modle + "/territoryListing");
        params.addBodyParameter("noid", noid);
        ApiTool apiTool = new ApiTool();
        apiTool.postApi(params, apiListener);
    }

    /**
     * 工作地域 - 添加[territoryInsert]
     *
     * @param noid        用户编号
     * @param province_id 省id
     * @param city_id     市id
     * @param area_id     区id
     */
    public void territoryInsert(String noid, String province_id, String city_id, String area_id, ApiListener apiListener) {
        RequestParams params = new RequestParams(AppConfig.BASE_URL + modle + "/territoryInsert");
        params.addBodyParameter("noid", noid);
        params.addBodyParameter("province_id", province_id);
        params.addBodyParameter("city_id", city_id);
        params.addBodyParameter("area_id", area_id);
        ApiTool apiTool = new ApiTool();
        apiTool.postApi(params, apiListener);
    }

    /**
     * 获取地区库省列表[provinceList]
     *
     * @param opend 如果为1，返回后台开通省。默认返回所有
     */
    public void provinceList(String opend, ApiListener apiListener) {
        RequestParams params = new RequestParams(AppConfig.BASE_URL + "Region/provinceList");
        params.addBodyParameter("opend", opend);
        ApiTool apiTool = new ApiTool();
        apiTool.postApi(params, apiListener);
    }

    /**
     * 获取地址库市列表[cityList]
     *
     * @param opend       如果为1，获取后台开通市。默认返回所有
     * @param province_id 省id
     */
    public void cityList(String opend, String province_id, ApiListener apiListener) {
        RequestParams params = new RequestParams(AppConfig.BASE_URL + "Region/cityList");
        params.addBodyParameter("opend", opend);
        params.addBodyParameter("province_id", province_id);
        ApiTool apiTool = new ApiTool();
        apiTool.postApi(params, apiListener);
    }

    /**
     * 获取地址库区列表[areaList]
     *
     * @param opend   如果为1，获取后台开通的区。默认返回所有
     * @param city_id 市id
     */
    public void areaList(String opend, String city_id, ApiListener apiListener) {
        RequestParams params = new RequestParams(AppConfig.BASE_URL + "Region/areaList");
        params.addBodyParameter("opend", opend);
        params.addBodyParameter("city_id", city_id);
        ApiTool apiTool = new ApiTool();
        apiTool.postApi(params, apiListener);
    }

    /**
     * 工作地域 - 删除[territoryRemove]
     *
     * @param noid         用户编号
     * @param territory_id 地域id
     */
    public void territoryRemove(String noid, String territory_id, ApiListener apiListener) {
        RequestParams params = new RequestParams(AppConfig.BASE_URL + modle + "/territoryRemove");
        params.addBodyParameter("noid", noid);
        params.addBodyParameter("territory_id", territory_id);
        ApiTool apiTool = new ApiTool();
        apiTool.postApi(params, apiListener);
    }
}
