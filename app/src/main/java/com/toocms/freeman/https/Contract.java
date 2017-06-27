package com.toocms.freeman.https;

import com.toocms.frame.web.ApiListener;
import com.toocms.frame.web.ApiTool;
import com.toocms.freeman.config.AppConfig;

import org.xutils.http.RequestParams;

import java.io.File;
import java.util.List;

import cn.zero.android.common.util.ListUtils;

/**
 * Created by admin on 2017/4/28.
 */

public class Contract {
    private String modle = getClass().getSimpleName();

    /**
     * 资方进行签约[sign]
     *
     * @param hire_noid 招工单编号
     * @param noid      资方编号
     * @param lab_noid  劳方编号
     */
    public void sign(String hire_noid, String noid, String lab_noid, ApiListener apiListener) {
        RequestParams params = new RequestParams(AppConfig.BASE_URL + modle + "/sign");
        params.addBodyParameter("hire_noid", hire_noid);
        params.addBodyParameter("noid", noid);
        params.addBodyParameter("lab_noid", lab_noid);
        ApiTool apiTool = new ApiTool();
        apiTool.postApi(params, apiListener);
    }

    /**
     * @param noid               用户编号
     * @param character          用户角色，CAP 为资方， LAB为劳方。只能为这两种
     * @param page               分页，默认为1
     * @param contract_noid      筛选：合同编号
     * @param keywords           筛选：关键字
     * @param skill              筛选：技能工种id数组
     * @param contract_starttime 筛选：合同开始日期
     * @param contract_endtime   筛选：合同截止日期
     * @param min_suntotal       筛选：最低单价
     * @param max_suntotal       筛选：最高单价
     * @param min_amount         筛选：最低总额
     * @param max_amount         筛选：最高总额
     * @param min_validtime      筛选：生效时间左值
     * @param max_validtime      筛选：生效时间右值
     * @param status             筛选：合同状态，默认为全部。
     *                           <p>
     *                           - INVALID  待生效
     *                           - PROGRESS 执行中
     *                           - STAY     待解除
     *                           - UNSETTLE 待结算
     *                           - SETTLE   已结算
     *                           - DRAWBACK 退补款
     *                           - ISSUE    纠纷
     */
    public void listing(String noid, String character, String page, String contract_noid, String keywords, String skill
            , String contract_starttime, String contract_endtime, String min_suntotal, String max_suntotal, String min_amount
            , String max_amount, String min_validtime, String max_validtime, String status, ApiListener apiListener) {
        RequestParams params = new RequestParams(AppConfig.BASE_URL + modle + "/listing");
        params.addBodyParameter("noid", noid);
        params.addBodyParameter("character", character);
        params.addBodyParameter("page", page);
        params.addBodyParameter("contract_noid", contract_noid);
        params.addBodyParameter("keywords", keywords);
        params.addBodyParameter("skill", skill);
        params.addBodyParameter("contract_starttime", contract_starttime);
        params.addBodyParameter("contract_endtime", contract_endtime);
        params.addBodyParameter("min_suntotal", min_suntotal);
        params.addBodyParameter("max_suntotal", max_suntotal);
        params.addBodyParameter("min_amount", min_amount);
        params.addBodyParameter("max_amount", max_amount);
        params.addBodyParameter("min_validtime", min_validtime);
        params.addBodyParameter("max_validtime", max_validtime);
        params.addBodyParameter("status", status);
        ApiTool apiTool = new ApiTool();
        apiTool.postApi(params, apiListener);
    }

    /**
     * 资方 - 发起解除[capStay] 带生效
     *
     * @param contract_noid 合同单号
     * @param noid          用户id
     * @param refuse        解除原因 80字
     */
    public void capStay(String contract_noid, String noid, String refuse, ApiListener apiListener) {
        RequestParams params = new RequestParams(AppConfig.BASE_URL + modle + "/capStay");
        params.addBodyParameter("contract_noid", contract_noid);
        params.addBodyParameter("noid", noid);
        params.addBodyParameter("refuse", refuse);
        ApiTool apiTool = new ApiTool();
        apiTool.postApi(params, apiListener);
    }

    /**
     * 获取合同详情[getContractData]
     *
     * @param noid          参与者的用户编号，可以是作为资方也可以是作为劳方
     * @param contract_noid 合同编号
     */
    public void getContractData(String noid, String contract_noid, ApiListener apiListener) {
        RequestParams params = new RequestParams(AppConfig.BASE_URL + modle + "/getContractData");
        params.addBodyParameter("noid", noid);
        params.addBodyParameter("contract_noid", contract_noid);
        ApiTool apiTool = new ApiTool();
        apiTool.postApi(params, apiListener);
    }

    /**
     * capCancelStay 资方 - 拒绝解除[带生效]
     *
     * @param contract_noid
     * @param noid
     */
    public void capCancelStay(String contract_noid, String noid, ApiListener apiListener) {
        RequestParams params = new RequestParams(AppConfig.BASE_URL + modle + "/capCancelStay");
        params.addBodyParameter("contract_noid", contract_noid);
        params.addBodyParameter("noid", noid);
        ApiTool apiTool = new ApiTool();
        apiTool.postApi(params, apiListener);
    }

    /**
     * capAcceptStay 资方 - 同意解除[带生效]
     *
     * @param contract_noid
     * @param noid
     */
    public void capAcceptStay(String contract_noid, String noid, ApiListener apiListener) {
        RequestParams params = new RequestParams(AppConfig.BASE_URL + modle + "/capAcceptStay");
        params.addBodyParameter("contract_noid", contract_noid);
        params.addBodyParameter("noid", noid);
        ApiTool apiTool = new ApiTool();
        apiTool.postApi(params, apiListener);
    }

    /**
     * 劳方 - 发起解除[labStay] 带生效
     *
     * @param contract_noid
     * @param noid
     * @param refuse        发起缘由
     */
    public void labStay(String contract_noid, String noid, String refuse, ApiListener apiListener) {
        RequestParams params = new RequestParams(AppConfig.BASE_URL + modle + "/labStay");
        params.addBodyParameter("contract_noid", contract_noid);
        params.addBodyParameter("lab_noid", noid);
        params.addBodyParameter("refuse", refuse);
        ApiTool apiTool = new ApiTool();
        apiTool.postApi(params, apiListener);
    }

    /**
     * 劳方 - 同意解除[labAcceptStay]带生效
     *
     * @param contract_noid
     * @param noid
     */
    public void labAcceptStay(String contract_noid, String noid, ApiListener apiListener) {
        RequestParams params = new RequestParams(AppConfig.BASE_URL + modle + "/labAcceptStay");
        params.addBodyParameter("contract_noid", contract_noid);
        params.addBodyParameter("lab_noid", noid);
        ApiTool apiTool = new ApiTool();
        apiTool.postApi(params, apiListener);
    }

    /**
     * 劳方 - 不同意解除[labCancelStay]带生效
     *
     * @param contract_noid
     * @param noid
     */
    public void labCancelStay(String contract_noid, String noid, ApiListener apiListener) {
        RequestParams params = new RequestParams(AppConfig.BASE_URL + modle + "/labCancelStay");
        params.addBodyParameter("contract_noid", contract_noid);
        params.addBodyParameter("lab_noid", noid);
        ApiTool apiTool = new ApiTool();
        apiTool.postApi(params, apiListener);
    }

    /**
     * 发送投诉请求[complain]
     *
     * @param contract_noid 合同
     * @param noid          用户
     * @param cause         投诉原因 150字
     * @param photos        图片数组
     */
    public void complain(String contract_noid, String noid, String cause, List<String> photos,
                         ApiListener apiListener) {
        RequestParams params = new RequestParams(AppConfig.BASE_URL + modle + "/complain");
        params.addBodyParameter("contract_noid", contract_noid);
        params.addBodyParameter("noid", noid);
        params.addBodyParameter("cause", cause);
        for (int i = 0; i < ListUtils.getSize(photos) - 1; i++) {
            params.addBodyParameter("photos[" + i + "]", new File(photos.get(i + 1)));
        }
        ApiTool apiTool = new ApiTool();
        apiTool.postApi(params, apiListener);
    }

    /**
     * 是否补款[isAdequacyAmount]
     *
     * @param contract_noid 合同
     * @param noid          用户
     * @param amount        交易金额
     */
    public void isAdequacyAmount(String contract_noid, String noid, String amount, ApiListener apiListener) {
        RequestParams params = new RequestParams(AppConfig.BASE_URL + modle + "/isAdequacyAmount");
        params.addBodyParameter("contract_noid", contract_noid);
        params.addBodyParameter("noid", noid);
        params.addBodyParameter("amount", amount);
        ApiTool apiTool = new ApiTool();
        apiTool.postApi(params, apiListener);
    }

    /**
     * 开始发起结算[sponsorAdequacy]
     *
     * @param contract_noid
     * @param noid
     * @param amount        总额
     */
    public void sponsorAdequacy(String contract_noid, String noid, String amount, ApiListener apiListener) {
        RequestParams params = new RequestParams(AppConfig.BASE_URL + modle + "/sponsorAdequacy");
        params.addBodyParameter("contract_noid", contract_noid);
        params.addBodyParameter("noid", noid);
        params.addBodyParameter("amount", amount);
        ApiTool apiTool = new ApiTool();
        apiTool.postApi(params, apiListener);
    }

    /**
     * 同意结算[acceptAdequancy]
     *
     * @param contract_noid
     * @param noid
     */
    public void acceptAdequancy(String contract_noid, String noid, ApiListener apiListener) {
        RequestParams params = new RequestParams(AppConfig.BASE_URL + modle + "/acceptAdequancy");
        params.addBodyParameter("contract_noid", contract_noid);
        params.addBodyParameter("noid", noid);
        ApiTool apiTool = new ApiTool();
        apiTool.postApi(params, apiListener);
    }

    /**
     * 不同意结算请求[cancelAdequacy]
     *
     * @param contract_noid
     * @param noid
     * @param refuse        取消理由 80字
     * @param photos        上传图片数组
     */
    public void cancelAdequacy(String contract_noid, String noid, String refuse, List<String> photos, ApiListener apiListener) {
        RequestParams params = new RequestParams(AppConfig.BASE_URL + modle + "/cancelAdequacy");
        params.addBodyParameter("contract_noid", contract_noid);
        params.addBodyParameter("noid", noid);
        params.addBodyParameter("refuse", refuse);
        for (int i = 0; i < ListUtils.getSize(photos) - 1; i++) {
            params.addBodyParameter("photos[" + i + "]", new File(photos.get(i + 1)));
        }
        ApiTool apiTool = new ApiTool();
        apiTool.postApi(params, apiListener);
    }

    /**
     * 模拟 - 支付预付款[testSignPay]
     *
     * @param contract_noid 合同单号
     * @param noid
     */
    public void testSignPay(String contract_noid, String noid, ApiListener apiListener) {
        RequestParams params = new RequestParams(AppConfig.BASE_URL + modle + "/testSignPay");
        params.addBodyParameter("contract_noid", contract_noid);
        params.addBodyParameter("noid", noid);
        ApiTool apiTool = new ApiTool();
        apiTool.postApi(params, apiListener);
    }

    /**
     * 置为待结算状态[toUnsettle]
     *
     * @param contract_noid
     * @param noid
     */
    public void toUnsettle(String contract_noid, String noid, ApiListener apiListener) {
        RequestParams params = new RequestParams(AppConfig.BASE_URL + modle + "/toUnsettle");
        params.addBodyParameter("contract_noid", contract_noid);
        params.addBodyParameter("noid", noid);
        ApiTool apiTool = new ApiTool();
        apiTool.postApi(params, apiListener);
    }

    /**
     * 获取修改合同数据[fullOpinion]
     *
     * @param contract_noid 合同单号
     * @param noid
     */
    public void fullOpinion(String contract_noid, String noid, ApiListener apiListener) {
        RequestParams params = new RequestParams(AppConfig.BASE_URL + modle + "/fullOpinion");
        params.addBodyParameter("contract_noid", contract_noid);
        params.addBodyParameter("noid", noid);
        ApiTool apiTool = new ApiTool();
        apiTool.postApi(params, apiListener);
    }

    /**
     * @param contract_noid              合同单号
     * @param noid                       用户编号
     * @param skill_id                   技能数组
     * @param staff                      提供人数
     * @param contract_starttime         合同开始时间
     * @param contract_endtime           合同结束时间
     * @param work_starttime             工作开始时间
     * @param work_endtime               工作结束时间
     * @param work_week                  周历id数组
     * @param subtotal                   单价
     * @param unit                       单位id
     * @param amount                     总价
     * @param settle_type                结算方式id
     * @param province_name              省
     * @param city_name                  市
     * @param area_name                  区
     * @param ress                       详细地址
     * @param longitude                  经度
     * @param latitude                   纬度
     * @param is_insurance               保险
     * @param is_dine                    工作餐
     * @param is_lodging                 住宿
     * @param is_tool                    劳动工具
     * @param is_transportation_expenses 交通费
     * @param is_correspondence          通讯费
     */
    public void capEditContract(String contract_noid, String noid, String skill_id, String staff, String contract_starttime
            , String contract_endtime, String work_starttime, String work_endtime, String work_week, String subtotal, String unit
            , String amount, String settle_type, String province_name, String city_name, String area_name, String ress, String longitude
            , String latitude, String is_insurance, String is_dine, String is_lodging, String is_tool, String is_transportation_expenses
            , String is_correspondence, String audit, String others_text, ApiListener apiListener) {
        RequestParams params = new RequestParams(AppConfig.BASE_URL + modle + "/capContractOpinion");
        params.addBodyParameter("contract_noid", contract_noid);
        params.addBodyParameter("noid", noid);
        params.addBodyParameter("skill_id", skill_id);
        params.addBodyParameter("staff", staff);
        params.addBodyParameter("contract_starttime", contract_starttime);
        params.addBodyParameter("contract_endtime", contract_endtime);
        params.addBodyParameter("work_starttime", work_starttime);
        params.addBodyParameter("work_endtime", work_endtime);
        params.addBodyParameter("work_week", work_week);
        params.addBodyParameter("subtotal", subtotal);
        params.addBodyParameter("unit", unit);
        params.addBodyParameter("amount", amount);
        params.addBodyParameter("settle_type", settle_type);
        params.addBodyParameter("province_name", province_name);
        params.addBodyParameter("city_name", city_name);
        params.addBodyParameter("area_name", area_name);
        params.addBodyParameter("ress", ress);
        params.addBodyParameter("longitude", longitude);
        params.addBodyParameter("latitude", latitude);
        params.addBodyParameter("is_insurance", is_insurance);
        params.addBodyParameter("is_dine", is_dine);
        params.addBodyParameter("is_lodging", is_lodging);
        params.addBodyParameter("is_tool", is_tool);
        params.addBodyParameter("is_transportation_expenses", is_transportation_expenses);
        params.addBodyParameter("is_correspondence", is_correspondence);
        params.addBodyParameter("audit", audit);
        params.addBodyParameter("others_text", others_text);
        ApiTool apiTool = new ApiTool();
        apiTool.postApi(params, apiListener);
    }

    //    labContractOpinion
    public void labContractOpinion(String contract_noid, String noid, String skill_id, String staff, String contract_starttime
            , String contract_endtime, String work_starttime, String work_endtime, String work_week, String subtotal, String unit
            , String amount, String settle_type, String province_name, String city_name, String area_name, String ress, String longitude
            , String latitude, String is_insurance, String is_dine, String is_lodging, String is_tool, String is_transportation_expenses
            , String is_correspondence, String audit, String others_text, ApiListener apiListener) {
        RequestParams params = new RequestParams(AppConfig.BASE_URL + modle + "/labContractOpinion");
        params.addBodyParameter("contract_noid", contract_noid);
        params.addBodyParameter("lab_noid", noid);
        params.addBodyParameter("skill_id", skill_id);
        params.addBodyParameter("staff", staff);
        params.addBodyParameter("contract_starttime", contract_starttime);
        params.addBodyParameter("contract_endtime", contract_endtime);
        params.addBodyParameter("work_starttime", work_starttime);
        params.addBodyParameter("work_endtime", work_endtime);
        params.addBodyParameter("work_week", work_week);
        params.addBodyParameter("subtotal", subtotal);
        params.addBodyParameter("unit", unit);
        params.addBodyParameter("amount", amount);
        params.addBodyParameter("settle_type", settle_type);
        params.addBodyParameter("province_name", province_name);
        params.addBodyParameter("city_name", city_name);
        params.addBodyParameter("area_name", area_name);
        params.addBodyParameter("ress", ress);
        params.addBodyParameter("longitude", longitude);
        params.addBodyParameter("latitude", latitude);
        params.addBodyParameter("is_insurance", is_insurance);
        params.addBodyParameter("is_dine", is_dine);
        params.addBodyParameter("is_lodging", is_lodging);
        params.addBodyParameter("is_tool", is_tool);
        params.addBodyParameter("is_transportation_expenses", is_transportation_expenses);
        params.addBodyParameter("is_correspondence", is_correspondence);
        params.addBodyParameter("audit", audit);
        params.addBodyParameter("others_text", others_text);
        ApiTool apiTool = new ApiTool();
        apiTool.postApi(params, apiListener);
    }

    /**
     * 修改明细[reply]
     *
     * @param contract_noid
     * @param noid
     */
    public void reply(String contract_noid, String noid, ApiListener apiListener) {
        RequestParams params = new RequestParams(AppConfig.BASE_URL + modle + "/reply");
        params.addBodyParameter("contract_noid", contract_noid);
        params.addBodyParameter("noid", noid);
        ApiTool apiTool = new ApiTool();
        apiTool.postApi(params, apiListener);
    }

    /**
     * 资方 - 拒绝修改[capCancelContractOpinion]
     *
     * @param contract_noid
     * @param noid
     */
    public void capCancelContractOpinion(String contract_noid, String noid, ApiListener apiListener) {
        RequestParams params = new RequestParams(AppConfig.BASE_URL + modle + "/capCancelContractOpinion");
        params.addBodyParameter("contract_noid", contract_noid);
        params.addBodyParameter("noid", noid);
        ApiTool apiTool = new ApiTool();
        apiTool.postApi(params, apiListener);
    }

    /**
     * 劳方 - 拒绝修改[labCancelContractOpinion]
     *
     * @param contract_noid
     * @param lab_noid
     */
    public void labCancelContractOpinion(String contract_noid, String lab_noid, ApiListener apiListener) {
        RequestParams params = new RequestParams(AppConfig.BASE_URL + modle + "/labCancelContractOpinion");
        params.addBodyParameter("contract_noid", contract_noid);
        params.addBodyParameter("lab_noid", lab_noid);
        ApiTool apiTool = new ApiTool();
        apiTool.postApi(params, apiListener);
    }

    /**
     * 资方 - 同意修改[capAcceptContractOpinion]
     *
     * @param contract_noid
     * @param lab_noid
     */
    public void capAcceptContractOpinion(String contract_noid, String lab_noid, ApiListener apiListener) {
        RequestParams params = new RequestParams(AppConfig.BASE_URL + modle + "/capAcceptContractOpinion");
        params.addBodyParameter("contract_noid", contract_noid);
        params.addBodyParameter("noid", lab_noid);
        ApiTool apiTool = new ApiTool();
        apiTool.postApi(params, apiListener);
    }

    /**
     * 劳方 - 同意修改[labAcceptContractOpinion]
     *
     * @param contract_noid
     * @param lab_noid
     */
    public void labAcceptContractOpinion(String contract_noid, String lab_noid, ApiListener apiListener) {
        RequestParams params = new RequestParams(AppConfig.BASE_URL + modle + "/labAcceptContractOpinion");
        params.addBodyParameter("contract_noid", contract_noid);
        params.addBodyParameter("lab_noid", lab_noid);
        ApiTool apiTool = new ApiTool();
        apiTool.postApi(params, apiListener);
    }

    /**
     * 资方 - 发起解除[capProgressStay]
     *
     * @param contract_noid 合同单号
     * @param noid          用户id
     * @param refuse        解除原因 80字
     */
    public void capProgressStay(String contract_noid, String noid, String refuse, ApiListener apiListener) {
        RequestParams params = new RequestParams(AppConfig.BASE_URL + modle + "/capProgressStay");
        params.addBodyParameter("contract_noid", contract_noid);
        params.addBodyParameter("noid", noid);
        params.addBodyParameter("refuse", refuse);
        ApiTool apiTool = new ApiTool();
        apiTool.postApi(params, apiListener);
    }

    /**
     * capCancelStay 资方 - 拒绝解除[带生效]
     *
     * @param contract_noid
     * @param noid
     */
    public void capCancelProgressStay(String contract_noid, String noid, ApiListener apiListener) {
        RequestParams params = new RequestParams(AppConfig.BASE_URL + modle + "/capCancelProgressStay");
        params.addBodyParameter("contract_noid", contract_noid);
        params.addBodyParameter("noid", noid);
        ApiTool apiTool = new ApiTool();
        apiTool.postApi(params, apiListener);
    }

    /**
     * capAcceptStay 资方 - 同意解除[带生效]
     *
     * @param contract_noid
     * @param noid
     */
    public void capAcceptProgressStay(String contract_noid, String noid, ApiListener apiListener) {
        RequestParams params = new RequestParams(AppConfig.BASE_URL + modle + "/capAcceptProgressStay");
        params.addBodyParameter("contract_noid", contract_noid);
        params.addBodyParameter("noid", noid);
        ApiTool apiTool = new ApiTool();
        apiTool.postApi(params, apiListener);
    }

    /**
     * 劳方 - 发起解除[labStay] 带生效
     *
     * @param contract_noid
     * @param noid
     * @param refuse        发起缘由
     */
    public void labProgressStay(String contract_noid, String noid, String refuse, ApiListener apiListener) {
        RequestParams params = new RequestParams(AppConfig.BASE_URL + modle + "/labProgressStay");
        params.addBodyParameter("contract_noid", contract_noid);
        params.addBodyParameter("lab_noid", noid);
        params.addBodyParameter("refuse", refuse);
        ApiTool apiTool = new ApiTool();
        apiTool.postApi(params, apiListener);
    }

    /**
     * 劳方 - 同意解除[labAcceptStay]带生效
     *
     * @param contract_noid
     * @param noid
     */
    public void labAcceptProgressStay(String contract_noid, String noid, ApiListener apiListener) {
        RequestParams params = new RequestParams(AppConfig.BASE_URL + modle + "/labAcceptProgressStay");
        params.addBodyParameter("contract_noid", contract_noid);
        params.addBodyParameter("lab_noid", noid);
        ApiTool apiTool = new ApiTool();
        apiTool.postApi(params, apiListener);
    }

    /**
     * 劳方 - 不同意解除[labCancelStay]带生效
     *
     * @param contract_noid
     * @param noid
     */
    public void labCancelProgressStay(String contract_noid, String noid, ApiListener apiListener) {
        RequestParams params = new RequestParams(AppConfig.BASE_URL + modle + "/labCancelProgressStay");
        params.addBodyParameter("contract_noid", contract_noid);
        params.addBodyParameter("lab_noid", noid);
        ApiTool apiTool = new ApiTool();
        apiTool.postApi(params, apiListener);
    }

    /**
     * 发起退补款[sponsorDrawback]
     *
     * @param contract_noid 合同
     * @param noid
     * @param amount        总额
     */
    public void sponsorDrawback(String contract_noid, String noid, String amount, ApiListener apiListener) {
        RequestParams params = new RequestParams(AppConfig.BASE_URL + modle + "/sponsorDrawback");
        params.addBodyParameter("contract_noid", contract_noid);
        params.addBodyParameter("noid", noid);
        params.addBodyParameter("amount", amount);
        ApiTool apiTool = new ApiTool();
        apiTool.postApi(params, apiListener);
    }

    /**
     * 置为退补款状态[toDrawback]
     *
     * @param contract_noid
     * @param noid
     */
    public void toDrawback(String contract_noid, String noid, ApiListener apiListener) {
        RequestParams params = new RequestParams(AppConfig.BASE_URL + modle + "/toDrawback");
        params.addBodyParameter("contract_noid", contract_noid);
        params.addBodyParameter("noid", noid);
        ApiTool apiTool = new ApiTool();
        apiTool.postApi(params, apiListener);
    }

    /**
     * 获取评价带入信息[getSimpleContract]
     *
     * @param contract_noid
     * @param noid
     */
    public void getSimpleContract(String contract_noid, String noid, ApiListener apiListener) {
        RequestParams params = new RequestParams(AppConfig.BASE_URL + modle + "/getSimpleContract");
        params.addBodyParameter("contract_noid", contract_noid);
        params.addBodyParameter("noid", noid);
        ApiTool apiTool = new ApiTool();
        apiTool.postApi(params, apiListener);
    }

    /**
     * 发布评价[assess]
     *
     * @param contract_noid 合同
     * @param noid
     * @param level         等级， GOOD NORMAL BAD
     * @param content       评价内容 150字
     * @param photos        照片数组
     */
    public void assess(String contract_noid, String noid, String level, String content, List<String> photos, ApiListener apiListener) {
        RequestParams params = new RequestParams(AppConfig.BASE_URL + modle + "/assess");
        params.addBodyParameter("contract_noid", contract_noid);
        params.addBodyParameter("noid", noid);
        params.addBodyParameter("level", level);
        params.addBodyParameter("content", content);
        for (int i = 0; i < ListUtils.getSize(photos) - 1; i++) {
            params.addBodyParameter("photos" + "[" + i + "]", new File(photos.get(i + 1)));
        }
        ApiTool apiTool = new ApiTool();
        apiTool.postApi(params, apiListener);

    }

    /**
     * 评价列表[assessList]
     *
     * @param noid          用户编号
     * @param partner       角色类型
     *                      PUBLISH 我发出的
     *                      ACCEPT 已收到的
     * @param page          分页，默认为1
     * @param skill_id      技能id数组
     * @param contract_noid
     * @param keywords
     */
    public void assessList(String noid, String partner, String page, String skill_id, String contract_noid,
                           String keywords, ApiListener apiListener) {
        RequestParams params = new RequestParams(AppConfig.BASE_URL + modle + "/assessList");
        params.addBodyParameter("noid", noid);
        params.addBodyParameter("partner", partner);
        params.addBodyParameter("page", page);
        params.addBodyParameter("skill_id", skill_id);
        params.addBodyParameter("contract_noid", contract_noid);
        params.addBodyParameter("keywords", keywords);
        ApiTool apiTool = new ApiTool();
        apiTool.postApi(params, apiListener);
    }

    /**
     * 回复评价[replyAssess]
     *
     * @param contract_noid 合同编号
     * @param assess_id     评价id
     * @param noid          用户编号
     * @param content       回复内容，80字
     */
    public void replyAssess(String contract_noid, String assess_id, String noid, String content, ApiListener apiListener) {
        RequestParams params = new RequestParams(AppConfig.BASE_URL + modle + "/replyAssess");
        params.addBodyParameter("contract_noid", contract_noid);
        params.addBodyParameter("assess_id", assess_id);
        params.addBodyParameter("noid", noid);
        params.addBodyParameter("content", content);
        ApiTool apiTool = new ApiTool();
        apiTool.postApi(params, apiListener);
    }

    /**
     * 同意退补款[acceptDrawback]
     *
     * @param contract_noid 合同
     * @param noid          用户
     */
    public void acceptDrawback(String contract_noid, String noid, ApiListener apiListener) {
        RequestParams params = new RequestParams(AppConfig.BASE_URL + modle + "/acceptDrawback");
        params.addBodyParameter("contract_noid", contract_noid);
        params.addBodyParameter("noid", noid);
        ApiTool apiTool = new ApiTool();
        apiTool.postApi(params, apiListener);
    }

    /**
     * 拒绝退补款[cancelDrawback]
     *
     * @param contract_noid
     * @param noid
     * @param refuse        拒绝原因，80字
     * @param photos        照片数组
     * @param apiListener
     */
    public void cancelDrawback(String contract_noid, String noid, String refuse, List<String> photos, ApiListener apiListener) {
        RequestParams params = new RequestParams(AppConfig.BASE_URL + modle + "/cancelDrawback");
        params.addBodyParameter("contract_noid", contract_noid);
        params.addBodyParameter("noid", noid);
        params.addBodyParameter("refuse", refuse);
        for (int i = 0; i < ListUtils.getSize(photos) - 1; i++) {
            params.addBodyParameter("photos[" + i + "]", new File(photos.get(i + 1)));
        }
        ApiTool apiTool = new ApiTool();
        apiTool.postApi(params, apiListener);
    }

    /**
     * 取消退补款申请[rollbackDrawback]
     *
     * @param contract_noid 合同编号
     * @param noid
     */
    public void rollbackDrawback(String contract_noid, String noid, ApiListener apiListener) {
        RequestParams params = new RequestParams(AppConfig.BASE_URL + modle + "/rollbackDrawback");
        params.addBodyParameter("contract_noid", contract_noid);
        params.addBodyParameter("noid", noid);
        ApiTool apiTool = new ApiTool();
        apiTool.postApi(params, apiListener);
    }

    /**
     * 同意纠纷方案[appectIssue]
     *
     * @param contract_noid 合同编号
     * @param noid
     * @param issue_id      方案id
     */
    public void appectIssue(String contract_noid, String noid, String issue_id, ApiListener apiListener) {
        RequestParams params = new RequestParams(AppConfig.BASE_URL + modle + "/appectIssue");
        params.addBodyParameter("contract_noid", contract_noid);
        params.addBodyParameter("noid", noid);
        params.addBodyParameter("issue_id", issue_id);
        ApiTool apiTool = new ApiTool();
        apiTool.postApi(params, apiListener);
    }

    /**
     * 拒绝纠纷方案[cancelIssue]
     *
     * @param contract_noid 合同编号
     * @param noid          用户编号
     * @param issue_id      方案id
     * @param photos        图片数组
     * @param refuse        拒绝理由 150字
     */
    public void cancelIssue(String contract_noid, String noid, String issue_id, List<String> photos
            , String refuse, ApiListener apiListener) {
        RequestParams params = new RequestParams(AppConfig.BASE_URL + modle + "/cancelIssue");
        params.addBodyParameter("contract_noid", contract_noid);
        params.addBodyParameter("noid", noid);
        params.addBodyParameter("issue_id", issue_id);
        params.addBodyParameter("refuse", refuse);
        for (int i = 0; i < ListUtils.getSize(photos) - 1; i++) {
            params.addBodyParameter("photos[" + i + "]", new File(photos.get(i + 1)));
        }
        ApiTool apiTool = new ApiTool();
        apiTool.postApi(params, apiListener);
    }

    /**
     * 上传照片[labUploadPhotos]
     *
     * @param noid          用户编号，只有劳方能上传图片
     * @param contract_noid
     * @param photos        多图
     */
    public void labUploadPhotos(String noid, String contract_noid, List<String> photos, ApiListener apiListener) {
        RequestParams params = new RequestParams(AppConfig.BASE_URL + modle + "/labUploadPhotos");
        params.addBodyParameter("noid", noid);
        params.addBodyParameter("contract_noid", contract_noid);
        for (int i = 0; i < ListUtils.getSize(photos); i++) {
            params.addBodyParameter("photos[" + i + "]", new File(photos.get(i)));
        }
        ApiTool apiTool = new ApiTool();
        apiTool.postApi(params, apiListener);
    }

    /**
     * 微信预付款[wechatpay]
     *
     * @param contract_noid 合同编号
     * @param noid          用户编号
     */
    public void wechatpay(String contract_noid, String noid, ApiListener apiListener) {
        RequestParams params = new RequestParams(AppConfig.BASE_URL + modle + "/wechatpay");
        params.addBodyParameter("contract_noid", contract_noid);
        params.addBodyParameter("noid", noid);
        ApiTool apiTool = new ApiTool();
        apiTool.postApi(params, apiListener);
    }

    /**
     * 预付款统一回调[payCallback]
     *
     * @param sn 订单号
     */
    public void payCallback(String sn, ApiListener apiListener) {
        RequestParams params = new RequestParams(AppConfig.BASE_URL + modle + "/payCallback");
        params.addBodyParameter("sn", sn);
        ApiTool apiTool = new ApiTool();
        apiTool.postApi(params, apiListener);
    }

    /**
     * 微信补款[wechatAppendAmount]
     *
     * @param amount 补款金额
     * @param noid
     */
    public void wechatAppendAmount(String amount, String noid, ApiListener apiListener) {
        RequestParams params = new RequestParams(AppConfig.BASE_URL + modle + "/wechatAppendAmount");
        params.addBodyParameter("amount", amount);
        params.addBodyParameter("noid", noid);
        ApiTool apiTool = new ApiTool();
        apiTool.postApi(params, apiListener);
    }

    /**
     * 支付宝预付款[alipay]
     *
     * @param contract_noid 合同编号
     * @param noid          用户编号
     */
    public void alipay(String contract_noid, String noid, ApiListener apiListener) {
        RequestParams params = new RequestParams(AppConfig.BASE_URL + modle + "/alipay2");
        params.addBodyParameter("contract_noid", contract_noid);
        params.addBodyParameter("noid", noid);
        ApiTool apiTool = new ApiTool();
        apiTool.postApi(params, apiListener);
    }

    /**
     * 支付宝2.0补款[alipay2AppendAmount]
     *
     * @param amount 补款金额
     * @param noid   用户编号
     */
    public void alipay2AppendAmount(String amount, String noid, ApiListener apiListener) {
        RequestParams params = new RequestParams(AppConfig.BASE_URL + modle + "/alipay2AppendAmount");
        params.addBodyParameter("amount", amount);
        params.addBodyParameter("noid", noid);
        ApiTool apiTool = new ApiTool();
        apiTool.postApi(params, apiListener);
    }
}
