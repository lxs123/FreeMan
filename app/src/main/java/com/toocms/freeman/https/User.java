package com.toocms.freeman.https;

import com.toocms.frame.web.ApiListener;
import com.toocms.frame.web.ApiTool;
import com.toocms.freeman.config.AppConfig;

import org.xutils.http.RequestParams;

import java.io.File;
import java.util.ArrayList;

import cn.zero.android.common.util.ListUtils;

/**
 * Created by admin on 2017/4/20.
 */

public class User {
    String modle = getClass().getSimpleName();

    /**
     * 获取用户信息[userView]
     *
     * @param noid 用户编号
     * @param code 查看的用户编号
     */
    public void userView(String noid, String code, ApiListener apiListener) {
        RequestParams params = new RequestParams(AppConfig.BASE_URL + modle + "/userView");
        params.addBodyParameter("noid", noid);
        params.addBodyParameter("code", code);
        ApiTool apiTool = new ApiTool();
        apiTool.postApi(params, apiListener);
    }

    /**
     * 保存技能信息[saveAttestation]
     *
     * @param noid                       用户编号
     * @param skill_id                   技能id数组
     * @param staff                      提供人数
     * @param work_startdata             工作开始日期
     * @param work_enddata               工作结束日期
     * @param work_starttime             每天开始时间
     * @param work_endtime               每天结束时间
     * @param work_week                  周历id数组
     * @param subtotal                   单价
     * @param unit                       单位id
     * @param settle_type                结算方式id
     * @param is_insurance               是否提供保险 是为1，否为0 默认否(0)
     * @param is_dine                    是否提供工作餐 是为1，否为0 默认否(0)
     * @param is_lodging                 是否提供住宿 是为1，否为0 默认否(0)
     * @param is_tool                    是否提供劳动工具 是为1，否为0 默认否(0)
     * @param is_transportation_expenses 是否提供交通费 是为1，否为0 默认否(0)
     * @param is_correspondence          是否提供通讯费 是为1，否为0 默认否(0)
     * @param others_text                其他信息
     * @param territory                  工作地域id，可数组或 字符串 1,2,3
     * @param capability                 技能图片数组
     */
    public void saveAttestation(String noid, String skill_id, String staff, String work_startdata,
                                String work_enddata, String work_starttime, String work_endtime,
                                String work_week, String subtotal, String unit, String settle_type,
                                String is_insurance, String is_dine, String is_lodging, String is_tool,
                                String is_transportation_expenses, String is_correspondence, String others_text,
                                ArrayList<String> capability, String territory, ApiListener apiListener) {
        RequestParams params = new RequestParams(AppConfig.BASE_URL + modle + "/saveAttestation");
        params.addBodyParameter("noid", noid);
        params.addBodyParameter("skill_id", skill_id);
        params.addBodyParameter("staff", staff);
        params.addBodyParameter("work_startdate", work_startdata);
        params.addBodyParameter("work_enddate", work_enddata);
        params.addBodyParameter("work_starttime", work_starttime);
        params.addBodyParameter("work_endtime", work_endtime);
        params.addBodyParameter("work_week", work_week);
        params.addBodyParameter("subtotal", subtotal);
        params.addBodyParameter("unit", unit);
        params.addBodyParameter("settle_type", settle_type);
        params.addBodyParameter("is_insurance", is_insurance);
        params.addBodyParameter("is_dine", is_dine);
        params.addBodyParameter("is_lodging", is_lodging);
        params.addBodyParameter("is_tool", is_tool);
        params.addBodyParameter("is_transportation_expenses", is_transportation_expenses);
        params.addBodyParameter("is_correspondence", is_correspondence);
        params.addBodyParameter("others_text", others_text);
        for (int i = 0; i < ListUtils.getSize(capability) - 1; i++) {
            File file = new File(capability.get(i + 1));
            params.addBodyParameter("capability[" + i + "]", file);
        }
        params.addBodyParameter("territory", territory);
        ApiTool apiTool = new ApiTool();
        apiTool.postApi(params, apiListener);
    }

    /**
     * 获取技能信息内容[getAttrstation]
     *
     * @param noid 用户编号
     */
    public void getAttrstation(String noid, ApiListener apiListener) {
        RequestParams params = new RequestParams(AppConfig.BASE_URL + modle + "/getAttrstation");
        params.addBodyParameter("noid", noid);
        ApiTool apiTool = new ApiTool();
        apiTool.postApi(params, apiListener);
    }

    /**
     * 获取二维码名片[noidQrcode]
     *
     * @param noid 用户编号
     */
    public void noidQrcode(String noid, ApiListener apiListener) {
        RequestParams params = new RequestParams(AppConfig.BASE_URL + modle + "/noidQrcode");
        params.addBodyParameter("noid", noid);
        ApiTool apiTool = new ApiTool();
        apiTool.getApi(params, apiListener);
    }

    /**
     * 技能信息认证状态[isAttestation]
     *
     * @param noid 用户编号
     */
    public void isAttestation(String noid, ApiListener apiListener) {
        RequestParams params = new RequestParams(AppConfig.BASE_URL + modle + "/isAttestation");
        params.addBodyParameter("noid", noid);
        ApiTool apiTool = new ApiTool();
        apiTool.postApi(params, apiListener);
    }

    /**
     * 基本信息认证状态[isPerfect]
     *
     * @param noid 用户编号
     */
    public void isPerfect(String noid, ApiListener apiListener) {
        RequestParams params = new RequestParams(AppConfig.BASE_URL + modle + "/isPerfect");
        params.addBodyParameter("noid", noid);
        ApiTool apiTool = new ApiTool();
        apiTool.postApi(params, apiListener);
    }

    /**
     * 获取基本信息内容
     *
     * @param noid     用户编号
     * @param listener
     */
    public void getPerfect(String noid, ApiListener listener) {
        RequestParams params = new RequestParams(AppConfig.BASE_URL + modle + "/getPerfect");
        params.addBodyParameter("noid", noid);
        ApiTool apiTool = new ApiTool();
        apiTool.postApi(params, listener);
    }

    /**
     * 保存基本信息
     *
     * @param noid          用户编号
     * @param nickname      昵称
     * @param role_id       角色类型，1为自由人，2为单位。见角色类型数组
     * @param name          姓名
     * @param mobile        联系电话
     * @param code          邀请人编号，如果可传时
     * @param name_show     是否显示姓名，1为显示，2为隐藏。默认为显示
     * @param longitude     经度
     * @param latitude      纬度
     * @param province_name 省
     * @param city_name     市
     * @param area_name     区
     * @param ress          详细地址
     * @param sex           性别id，1为男，2为女，3为不限，4为未填写
     * @param height        身高，单位cm
     * @param weight        体重，单位kg
     * @param organization  公司名称 20字
     * @param introduce     自我介绍 150字
     * @param head          头像
     * @param listener
     */
    public void savePerfect(String noid, String nickname, String role_id, String name, String mobile, String code,
                            String name_show, String longitude, String latitude, String province_name, String city_name,
                            String area_name, String ress, String sex, String height, String weight, String organization, String introduce, String head, ApiListener listener) {
        RequestParams params = new RequestParams(AppConfig.BASE_URL + modle + "/savePerfect");
        params.addBodyParameter("noid", noid);
        params.addBodyParameter("nickname", nickname);
        params.addBodyParameter("role_id", role_id);
        params.addBodyParameter("name", name);
        params.addBodyParameter("mobile", mobile);
        params.addBodyParameter("code", code);
        params.addBodyParameter("name_show", name_show);
        params.addBodyParameter("longitude", longitude);
        params.addBodyParameter("latitude", latitude);
        params.addBodyParameter("province_name", province_name);
        params.addBodyParameter("city_name", city_name);
        params.addBodyParameter("area_name", area_name);
        params.addBodyParameter("ress", ress);
        params.addBodyParameter("sex", sex);
        params.addBodyParameter("height", height);
        params.addBodyParameter("weight", weight);
        params.addBodyParameter("organization", organization);
        params.addBodyParameter("introduce", introduce);
        params.addBodyParameter("head", new File(head));
        ApiTool apiTool = new ApiTool();
        apiTool.postApi(params, listener);
    }
    /**
     * 我的钱包
     *
     * @param noid     用户编号
     * @param listener
     */
    public void wallet(String noid, ApiListener listener) {
        RequestParams params = new RequestParams(AppConfig.BASE_URL + modle + "/wallet");
        params.addBodyParameter("noid", noid);
        ApiTool apiTool = new ApiTool();
        apiTool.postApi(params, listener);
    }

    /**
     * 交易明细
     *
     * @param noid       用户编号
     * @param page       分页
     * @param start_time 开始时间 2017-04-06
     * @param end_time   结束时间 2017-05-08
     * @param listener
     */
    public void trades(String noid, String page, String start_time, String end_time, ApiListener listener) {
        RequestParams params = new RequestParams(AppConfig.BASE_URL + modle + "/trades");
        params.addBodyParameter("noid", noid);
        params.addBodyParameter("page", page);
        params.addBodyParameter("start_time", start_time);
        params.addBodyParameter("end_time", end_time);
        ApiTool apiTool = new ApiTool();
        apiTool.postApi(params, listener);
    }

    /**
     * 提现记录
     *
     * @param noid     用户编号
     * @param page     分页 默认为1
     * @param listener
     */
    public void withdraws(String noid, String page, ApiListener listener) {
        RequestParams params = new RequestParams(AppConfig.BASE_URL + modle + "/withdraws");
        params.addBodyParameter("noid", noid);
        params.addBodyParameter("page", page);
        ApiTool apiTool = new ApiTool();
        apiTool.postApi(params, listener);
    }
}
