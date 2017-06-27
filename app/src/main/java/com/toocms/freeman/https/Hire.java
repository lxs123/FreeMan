package com.toocms.freeman.https;

import android.util.Log;

import com.toocms.frame.web.ApiListener;
import com.toocms.frame.web.ApiTool;
import com.toocms.freeman.config.AppConfig;

import org.xutils.http.RequestParams;

import java.io.File;
import java.util.ArrayList;

import cn.zero.android.common.util.ListUtils;

/**
 * Created by admin on 2017/4/12.
 */

public class Hire {
    String modle = getClass().getSimpleName();

    /**
     * 创建预置信息[getFormData]
     * 创建或修改时，表单自带信息（如周历列表）
     */
    public void getFormData(ApiListener apiListener) {
        RequestParams params = new RequestParams(AppConfig.BASE_URL + modle + "/getFormData");
        ApiTool apiTool = new ApiTool();
        apiTool.postApi(params, apiListener);
    }

    /**
     * 保存招工单[save]
     *
     * @param noid                       用户编号
     * @param hire_endtime               截止日期 xxxx-xx-xx
     * @param amount                     总额
     * @param audit                      工作完成标准
     * @param skill_id                   工种，可传技能工种id数组，如[1,3,4],也可传字符串 '1,3,4'
     * @param staff                      人员数量
     * @param contract_starttime         合同开始日期， xxxx-xx-xx
     * @param contract_endtime           合同结束日期 xxxx-xx-xx
     * @param work_starttime             每天工作开始时间
     * @param work_endtime               每天工作结束时间
     * @param work_week                  工作周历id , 可传id数组或字符串
     * @param subtotal                   单价
     * @param unit                       单价单位
     * @param settle_type                结算方式id
     * @param others_text                其他信息
     * @param photos                     工作现成照片，使用多图上传方式
     * @param is_insurance               是否提供保险 是为1，否为0 默认否(0)
     * @param is_dine                    是否提供工作餐 是为1，否为0 默认否(0)
     * @param is_lodging                 是否提供住宿 是为1，否为0 默认否(0)
     * @param is_tool                    是否提供劳动工具 是为1，否为0 默认否(0)
     * @param is_transportation_expenses 是否提供交通费 是为1，否为0 默认否(0)
     * @param is_correspondence          是否提供通讯费 是为1，否为0 默认否(0)
     * @param hire_id                    自身的招工单id，默认为新建，如果传此参数为修改
     * @param province_name              省name
     * @param city_name                  市name
     * @param area_id                    区id
     * @param ress                       详细地址
     */
    public void save(String noid, String hire_endtime, String amount, String audit, String skill_id,
                     String staff, String contract_starttime, String contract_endtime, String work_starttime,
                     String work_endtime, String work_week, String subtotal, String unit, String settle_type,
                     String others_text, ArrayList<String> photos, String is_insurance, String is_dine, String is_lodging,
                     String is_tool, String is_transportation_expenses, String is_correspondence, String hire_id,
                     String province_name, String city_name, String area_id, String ress, String longitude,
                     String latitude, ApiListener apiListener) {
        RequestParams params = new RequestParams(AppConfig.BASE_URL + modle + "/save");
        params.addBodyParameter("noid", noid);
        params.addBodyParameter("hire_endtime", hire_endtime);
        params.addBodyParameter("amount", amount);
        params.addBodyParameter("audit", audit);
        params.addBodyParameter("skill_id", skill_id);
        params.addBodyParameter("staff", staff);
        params.addBodyParameter("contract_starttime", contract_starttime);
        params.addBodyParameter("contract_endtime", contract_endtime);
        params.addBodyParameter("work_starttime", work_starttime);
        params.addBodyParameter("work_endtime", work_endtime);
        params.addBodyParameter("work_week", work_week);
        params.addBodyParameter("subtotal", subtotal);
        params.addBodyParameter("unit", unit);
        params.addBodyParameter("settle_type", settle_type);
        params.addBodyParameter("others_text", others_text);
//        LogUtil.e(photos.toString());
        for (int i = 0; i < ListUtils.getSize(photos) - 1; i++) {
            params.addBodyParameter("photos[" + i + "]", new File(photos.get(i + 1)));
        }
        params.addBodyParameter("is_insurance", is_insurance);
        params.addBodyParameter("is_dine", is_dine);
        params.addBodyParameter("is_lodging", is_lodging);
        params.addBodyParameter("is_tool", is_tool);
        params.addBodyParameter("is_transportation_expenses", is_transportation_expenses);
        params.addBodyParameter("is_correspondence", is_correspondence);
        params.addBodyParameter("hire_id", hire_id);
        params.addBodyParameter("province_name", province_name);
        params.addBodyParameter("city_name", city_name);
        params.addBodyParameter("area_name", area_id);
        params.addBodyParameter("ress", ress);
        params.addBodyParameter("longitude", longitude);
        params.addBodyParameter("latitude", latitude);
        ApiTool apiTool = new ApiTool();
        apiTool.postApi(params, apiListener);
    }

    /**
     * 自动发布[publishByAuto]
     *
     * @param noid        用户编号
     * @param hire_id     招工单id
     * @param province_id 省id
     * @param city_id     市id
     * @param area_id     区id
     * @param distance    距离数字，单位为米
     */
    public void publishByAuto(String noid, String hire_id, String province_id, String city_id, String area_id
            , String distance, ApiListener apiListener) {
        RequestParams params = new RequestParams(AppConfig.BASE_URL + modle + "/publishByAuto");
        params.addBodyParameter("noid", noid);
        params.addBodyParameter("hire_id", hire_id);
        params.addBodyParameter("province_id", province_id);
        params.addBodyParameter("city_id", city_id);
        params.addBodyParameter("area_id", area_id);
        params.addBodyParameter("distance", distance);
        ApiTool apiTool = new ApiTool();
        apiTool.postApi(params, apiListener);

    }

    /**
     * 手动选择劳方发布[publishByCustom]
     *
     * @param noid     用户编号
     * @param hire_id  招工单id
     * @param lab_list 劳方noid列表，必须为数组
     */
    public void publishByCustom(String noid, String hire_id, ArrayList<String> lab_list, ApiListener apiListener) {
        RequestParams params = new RequestParams(AppConfig.BASE_URL + modle + "/publishByCustom");
        params.addBodyParameter("noid", noid);
        params.addBodyParameter("hire_id", hire_id);
        for (int i = 0; i < ListUtils.getSize(lab_list); i++) {
            params.addBodyParameter("lab_list[" + i + "]", lab_list.get(i));
        }
        ApiTool apiTool = new ApiTool();
        apiTool.postApi(params, apiListener);
    }

    /**
     * 搜索劳方列表[seekLabs]
     *
     * @param noid             用户编号
     * @param hire_id          招工单id
     * @param page             分页，默认为1
     * @param keywords         关键字
     * @param min_price        最小价格
     * @param max_price        最大价格
     * @param code             搜索劳方编号
     * @param distance         距离
     * @param contract_start   合同开始日期
     * @param contract_endtime 合同结束日期
     * @param work_starttime   每天工作开始时间
     * @param work_endtime     每天工作结束时间
     * @param sort             排序：
     *                         - cost: 成单量最多
     *                         - level: 信用最高
     *                         - distance: 距离最近
     *                         - maxprice: 单价高到底
     *                         - minprice: 单价低到高
     * @param province_id      省id
     * @param city_id          市id
     * @param area_id          区id
     *                         work_week
     *                         skill_id
     */
    public void seekLabs(String noid, String hire_id, String page, String keywords, String min_price, String max_price
            , String code, String distance, String contract_start, String contract_endtime, String work_starttime, String work_endtime
            , String sort, String province_id, String city_id, String area_id, String work_week, String skill_id, ApiListener apiListener) {
        RequestParams params = new RequestParams(AppConfig.BASE_URL + modle + "/seekLabs");
        params.addBodyParameter("noid", noid);
        params.addBodyParameter("hire_id", hire_id);
        params.addBodyParameter("page", page);
        params.addBodyParameter("keywords", keywords);
        params.addBodyParameter("min_price", min_price);
        params.addBodyParameter("max_price", max_price);
        params.addBodyParameter("code", code);
        params.addBodyParameter("distance", distance);
        params.addBodyParameter("contract_start", contract_start);
        params.addBodyParameter("contract_endtime", contract_endtime);
        params.addBodyParameter("work_starttime", work_starttime);
        params.addBodyParameter("work_endtime", work_endtime);
        params.addBodyParameter("sort", sort);
        params.addBodyParameter("province_id", province_id);
        params.addBodyParameter("city_id", city_id);
        params.addBodyParameter("area_id", area_id);
        params.addBodyParameter("work_week", work_week);
        params.addBodyParameter("skill_id", skill_id);
        ApiTool apiTool = new ApiTool();
        apiTool.postApi(params, apiListener);
    }

    /**
     * 我的招工单
     *
     * @param noid               用户编号
     * @param page               分页，默认为1
     * @param sort               排序，支持：
     *                           - level: 信用最高
     *                           - distance: 距离最近
     *                           - maxprice: 价格从高到低
     *                           - minprice: 价格从低到高
     *                           - workfast: 工作日期最近
     *                           - worklong: 工作日期最久
     * @param work_week          周历
     * @param min_price          单价最低
     * @param max_price          单价最高
     * @param cap_noid           会员编号，资方编号
     * @param skill_id           工作，技能，数组id
     * @param distance           选定范围，单位为米
     * @param province_id        选定省id
     * @param city_id            选定市id
     * @param area_id            选定区id
     * @param contract_starttime 合同开始日期
     * @param contract_endtime   合同结束日期
     * @param work_starttime     工作开始时间
     * @param work_endtime       工作结束时间
     * @param listener
     */
    public void library(String noid, String page, String sort, String work_week,
                        String min_price, String max_price, String cap_noid, String skill_id,
                        String distance, String province_id, String city_id, String area_id,
                        String contract_starttime, String contract_endtime, String work_starttime,
                        String work_endtime, String hire_noid, String keywords, ApiListener listener) {
        Log.e("log", "library:" + noid + "/" + page + "/" + sort + "/" + work_week + "/" + min_price + "/" + max_price + "/" + cap_noid + "/" + skill_id + "/" + distance + "/" + province_id + "/" + city_id + "/" + area_id
                + "/" + contract_starttime + "/" + contract_endtime + "/" + work_starttime + "/" + work_endtime);
        RequestParams params = new RequestParams(AppConfig.BASE_URL + modle + "/library");
        params.addBodyParameter("noid", noid);
        params.addBodyParameter("page", page);
        params.addBodyParameter("sort", sort);
        params.addBodyParameter("work_week", work_week);
        params.addBodyParameter("min_price", min_price);
        params.addBodyParameter("max_price", max_price);
        params.addBodyParameter("cap_noid", cap_noid);
        params.addBodyParameter("skill_id", skill_id);
        params.addBodyParameter("distance", distance);
        params.addBodyParameter("province_id", province_id);
        params.addBodyParameter("city_id", city_id);
        params.addBodyParameter("area_id", area_id);
        params.addBodyParameter("contract_starttime", contract_starttime);
        params.addBodyParameter("contract_endtime", contract_endtime);
        params.addBodyParameter("work_starttime", work_starttime);
        params.addBodyParameter("work_endtime", work_endtime);
        params.addBodyParameter("hire_noid", hire_noid);
        params.addBodyParameter("keywords", keywords);
        ApiTool apiTool = new ApiTool();
        apiTool.postApi(params, listener);
    }

    /**
     * 我的招工单
     *
     * @param noid                       用户编号
     * @param page                       分页，默认为1
     * @param sort                       排序，支持：
     *                                   - level: 信用最高
     *                                   - distance: 距离最近
     *                                   - maxprice: 价格从高到低
     *                                   - minprice: 价格从低到高
     *                                   - workfast: 工作日期最近
     *                                   - worklong: 工作日期最久
     * @param keywords                   关键词
     * @param min_prices                 最小价格
     * @param max_price                  最大价格
     * @param code                       资方编号
     *                                   param distance      选择距离，单位为米 String distance 此接口不掉用
     * @param contract_starttime         合同开始日期
     * @param contract_endtime           合同结束日期
     * @param work_starttime             工作开始时间
     * @param work_endtime               工作结束时间
     * @param is_insurance               是否提供保险，默认关闭，开启时传 1
     * @param is_dine                    是否提供工作餐，默认关闭，开启时传 1
     * @param is_lodging                 是否提供住宿，默认关闭，开启时传 1
     * @param is_tool                    是否提供劳动工具，默认关闭，开启时传 1
     * @param is_transportation_expenses 是否提供交通费，默认关闭，开启时传 1
     * @param is_correspondence          是否提供通讯费，默认关闭，开启时传 1
     * @param work_week                  周历id，字符串或数组
     * @param listener
     */
    public void accept(String noid, String page, String sort, String keywords, String min_prices,
                       String max_price, String code, String contract_starttime,
                       String contract_endtime, String work_starttime, String work_endtime, String is_insurance,
                       String is_dine, String is_lodging, String is_tool, String is_transportation_expenses,
                       String is_correspondence, String work_week, String province_id, String city_id, String area_id, ApiListener listener) {

//        Log.e("log", "accept" + noid + "/" + page + "/" + sort + "/" + keywords + "/" + min_prices + "/" + max_price + "/" + code + "/" + distance + "/" + contract_starttime + "/" +
//                contract_endtime + "/" + work_starttime + "/" + work_endtime + "/" + is_insurance + "/" + is_dine + "/" + is_lodging + "/" + is_tool + "/" + is_transportation_expenses +
//                "/" + is_correspondence + "/" + work_week + "/" + province_id + "/" + city_id + "/" + area_id);
        RequestParams params = new RequestParams(AppConfig.BASE_URL + modle + "/accept");
        params.addBodyParameter("noid", noid);
        params.addBodyParameter("page", page);
        params.addBodyParameter("sort", sort);
        params.addBodyParameter("keywords", keywords);
        params.addBodyParameter("min_price", min_prices);
        params.addBodyParameter("max_price", max_price);
        params.addBodyParameter("code", code);
//        params.addBodyParameter("distance", distance);
        params.addBodyParameter("contract_starttime", contract_starttime);
        params.addBodyParameter("contract_endtime", contract_endtime);
        params.addBodyParameter("work_starttime", work_starttime);
        params.addBodyParameter("work_endtime", work_endtime);
        params.addBodyParameter("is_insurance", is_insurance);
        params.addBodyParameter("is_dine", is_dine);
        params.addBodyParameter("is_lodging", is_lodging);
        params.addBodyParameter("is_tool", is_tool);
        params.addBodyParameter("is_transportation_expenses", is_transportation_expenses);
        params.addBodyParameter("is_correspondence", is_correspondence);
        params.addBodyParameter("work_week", work_week);
        params.addBodyParameter("province_id", province_id);
        params.addBodyParameter("city_id", city_id);
        params.addBodyParameter("area_id", area_id);
        ApiTool apiTool = new ApiTool();
        apiTool.postApi(params, listener);
    }

    /**
     * 我的招工单列表[listing]
     *
     * @param noid               用户编号
     * @param status             招工单状态，默认为全部 1为未发布 2为已发布 3为已签约
     * @param keywords           关键词
     * @param skill              技能数组
     * @param contract_starttime 合同开始日期
     * @param contract_endtime   合同结束日期
     * @param start_time         创建开始日期
     * @param end_time           创建结束日期
     * @param min_price          最小单价
     * @param max_price          最大单价
     * @param min_amount         最小金额
     * @param max_amount         最大金额
     * @param page               分页
     */
    public void listing(String noid, String status, String keywords, String skill, String contract_starttime
            , String contract_endtime, String start_time, String end_time, String min_price
            , String max_price, String min_amount, String max_amount, String page, String longitude, String latitude, ApiListener apiListener) {
        RequestParams params = new RequestParams(AppConfig.BASE_URL + modle + "/listing");
        params.addBodyParameter("noid", noid);
        params.addBodyParameter("noid", noid);
        params.addBodyParameter("status", status);
        params.addBodyParameter("keywords", keywords);
        params.addBodyParameter("skill", skill);
        params.addBodyParameter("contract_starttime", contract_starttime);
        params.addBodyParameter("contract_endtime", contract_endtime);
        params.addBodyParameter("start_time", start_time);
        params.addBodyParameter("end_time", end_time);
        params.addBodyParameter("min_price", min_price);
        params.addBodyParameter("max_price", max_price);
        params.addBodyParameter("min_amount", min_amount);
        params.addBodyParameter("max_amount", max_amount);
        params.addBodyParameter("page", page);
        params.addBodyParameter("latitude", latitude);
        params.addBodyParameter("longitude", longitude);
        ApiTool apiTool = new ApiTool();
        apiTool.postApi(params, apiListener);
    }

    /**
     * 编辑招工单数据[single]
     *
     * @param noid    用户编号
     * @param hire_id 招工单id
     */
    public void single(String noid, String hire_id, ApiListener apiListener) {
        RequestParams params = new RequestParams(AppConfig.BASE_URL + modle + "/single");
        params.addBodyParameter("noid", noid);
        params.addBodyParameter("hire_id", hire_id);
        ApiTool apiTool = new ApiTool();
        apiTool.postApi(params, apiListener);
    }

    /**
     * 删除招工单[remove]
     *
     * @param noid    用户编号
     * @param hire_id 招工单id
     */
    public void remove(String noid, String hire_id, ApiListener apiListener) {
        RequestParams params = new RequestParams(AppConfig.BASE_URL + modle + "/remove");
        params.addBodyParameter("noid", noid);
        params.addBodyParameter("hire_id", hire_id);
        ApiTool apiTool = new ApiTool();
        apiTool.postApi(params, apiListener);
    }

    /**
     * 选择收藏劳方发布[publishByCollect]
     *
     * @param noid     用户编号
     * @param hire_id  保存的招工单id
     * @param lab_list 劳方noid列表，必须为数组形式
     */
    public void publishByCollect(String noid, String hire_id, ArrayList<String> lab_list, ApiListener apiListener) {
        RequestParams params = new RequestParams(AppConfig.BASE_URL + modle + "/publishByCollect");
        params.addBodyParameter("noid", noid);
        params.addBodyParameter("hire_id", hire_id);
        for (int i = 0; i < ListUtils.getSize(lab_list); i++) {
            params.addBodyParameter("lab_list[" + i + "]", lab_list.get(i));
        }
        ApiTool apiTool = new ApiTool();
        apiTool.postApi(params, apiListener);

    }

    /**
     * 我的反馈列表[reply]
     *
     * @param noid        用户编号
     * @param hire_noid   招工单编号，不是id
     * @param keywords    关键字
     * @param maxprice    最高价格
     * @param minprice    最低价格
     * @param province_id 省id
     * @param city_id     市id
     * @param area_id     区id
     * @param skill_id    技能id，可以为数组或字符串链接
     * @param mate_person 可提供人数符合，1为符合 默认为0(不设置)
     * @param mate_agree  同意招工条件，1为同意 默认为0(不设置)
     * @param sort        排序：
     *                    - cost: 成单量最多
     *                    - level: 信用最高
     *                    - distance: 距离最近
     *                    - maxprice: 单价高到底
     *                    - minprice: 单价低到高
     */
    public void reply(String noid, String hire_noid, String keywords, String maxprice, String minprice, String province_id
            , String city_id, String area_id, String skill_id, String mate_person, String mate_agree, String sort
            , ApiListener apiListener) {
        RequestParams params = new RequestParams(AppConfig.BASE_URL + modle + "/reply");
        params.addBodyParameter("noid", noid);
        params.addBodyParameter("hire_noid", hire_noid);
        params.addBodyParameter("keywords", keywords);
        params.addBodyParameter("max_price", maxprice);
        params.addBodyParameter("min_price ", minprice);
        params.addBodyParameter("province_id", province_id);
        params.addBodyParameter("city_id", city_id);
        params.addBodyParameter("area_id", area_id);
        params.addBodyParameter("skill_id", skill_id);
        params.addBodyParameter("mate_person", mate_person);
        params.addBodyParameter("mate_agree", mate_agree);
        params.addBodyParameter("sort", sort);
        ApiTool apiTool = new ApiTool();
        apiTool.postApi(params, apiListener);
    }

    /**
     * 招工单反馈详情[detail]
     *
     * @param lab_noid  劳方noid
     * @param hire_noid 招工单noid
     */
    public void detail(String lab_noid, String hire_noid, ApiListener apiListener) {
        Log.e("log", "detail:" + lab_noid + "/" + hire_noid);
        RequestParams params = new RequestParams(AppConfig.BASE_URL + modle + "/detail");
        params.addBodyParameter("lab_noid", lab_noid);
        params.addBodyParameter("hire_noid", hire_noid);
        ApiTool apiTool = new ApiTool();
        apiTool.postApi(params, apiListener);

    }

    /**
     * 获取修改明细[fullOpinion]
     *
     * @param hire_noid 招工单编号
     * @param lab_noid  劳方编号
     */
    public void fullOpinion(String hire_noid, String lab_noid, ApiListener apiListener) {
        Log.e("log", "fullOpinion:" + hire_noid + "/" + lab_noid);
        RequestParams params = new RequestParams(AppConfig.BASE_URL + modle + "/fullOpinion");
        params.addBodyParameter("hire_noid", hire_noid);
        params.addBodyParameter("lab_noid", lab_noid);
        ApiTool apiTool = new ApiTool();
        apiTool.postApi(params, apiListener);
    }

    /**
     * 同意招工单[labReplyAccept]
     *
     * @param noid      用户编号
     * @param hire_noid 招工单编号
     * @param cap_noid  资方noid
     */
    public void labReplyAccept(String noid, String hire_noid, String cap_noid, ApiListener apiListener) {
        RequestParams params = new RequestParams(AppConfig.BASE_URL + modle + "/labReplyAccept");
        params.addBodyParameter("noid", noid);
        params.addBodyParameter("hire_noid", hire_noid);
        params.addBodyParameter("cap_noid", cap_noid);
        ApiTool apiTool = new ApiTool();
        apiTool.postApi(params, apiListener);
    }

    /**
     * @param noid                       资方编号
     * @param lab_noid                   劳方noid
     * @param hire_noid                  招工单编号
     * @param skill_id                   技能id数组
     * @param contract_starttime         合同开始日期
     * @param contract_endtime           合同结束日期
     * @param work_starttime             工作开始时间
     * @param work_endtime               工作结束时间
     * @param work_week                  工作周历id数组
     * @param hire_endtime               招工截止日期
     * @param amount                     总价
     * @param subtotal                   单价
     * @param unit                       单位id
     * @param settle_type                结算方式id
     * @param is_insurance               保险
     * @param is_dine                    工作餐
     * @param is_lodging                 住宿
     * @param is_tool                    劳动工具
     * @param is_transportation_expenses 交通费
     * @param is_correspondence          通讯费
     * @param audit                      完成标准
     * @param others_text                其他信息
     */
    public void replyOpinion(String noid, String lab_noid, String hire_noid, String skill_id, String contract_starttime, String contract_endtime,
                             String work_starttime, String work_endtime, String work_week, String hire_endtime, String amount, String subtotal,
                             String unit, String settle_type, String is_insurance, String is_dine, String is_lodging, String is_tool, String is_transportation_expenses,
                             String is_correspondence, String audit, String others_text, ApiListener apiListener) {
        RequestParams params = new RequestParams(AppConfig.BASE_URL + modle + "/replyOpinion");
        params.addBodyParameter("noid", noid);
        params.addBodyParameter("lab_noid", lab_noid);
        params.addBodyParameter("hire_endtime", hire_endtime);
        params.addBodyParameter("amount", amount);
        params.addBodyParameter("audit", audit);

        params.addBodyParameter("skill_id", skill_id);
        params.addBodyParameter("contract_starttime", contract_starttime);
        params.addBodyParameter("contract_endtime", contract_endtime);
        params.addBodyParameter("work_starttime", work_starttime);
        params.addBodyParameter("work_endtime", work_endtime);
        params.addBodyParameter("work_week", work_week);
        params.addBodyParameter("subtotal", subtotal);
        params.addBodyParameter("unit", unit);
        params.addBodyParameter("settle_type", settle_type);
        params.addBodyParameter("others_text", others_text);
        params.addBodyParameter("is_insurance", is_insurance);
        params.addBodyParameter("is_dine", is_dine);
        params.addBodyParameter("is_lodging", is_lodging);
        params.addBodyParameter("is_tool", is_tool);
        params.addBodyParameter("is_transportation_expenses", is_transportation_expenses);
        params.addBodyParameter("is_correspondence", is_correspondence);
        params.addBodyParameter("hire_noid", hire_noid);
        ApiTool apiTool = new ApiTool();
        apiTool.postApi(params, apiListener);
    }

    /**
     * 获取修改明细
     *
     * @param hire_noid 招工单
     * @param lab_noid  劳方noid
     * @param listener
     */
    public void labFullOpinion(String hire_noid, String lab_noid, ApiListener listener) {
        Log.e("log", "labFullOpinion:" + hire_noid + "/" + lab_noid);
        RequestParams params = new RequestParams(AppConfig.BASE_URL + modle + "/labFullOpinion");
        params.addBodyParameter("hire_noid", hire_noid);
        params.addBodyParameter("lab_noid", lab_noid);
        ApiTool apiTool = new ApiTool();
        apiTool.postApi(params, listener);
    }

    /**
     * 不同意招工单去修改
     *
     * @param lab_noid                   劳方编号
     * @param hire_noid                  招工单编号
     * @param skill_id                   技能数组
     * @param contract_starttime         合同开始日期
     * @param contract_endtime           合同结束日期
     * @param work_starttime             工作开始时间
     * @param work_endtime               工作结束时间
     * @param hire_endtime               招工截止日期
     * @param amount                     总价
     * @param subtotal                   单价
     * @param unit                       单位id
     * @param settle_type                结算方式id
     * @param is_insurance               是否提供保险 是为1，否为0 默认否(0)
     * @param is_dine                    是否提供工作餐 是为1，否为0 默认否(0)
     * @param is_lodging                 是否提供住宿 是为1，否为0 默认否(0)
     * @param is_tool                    是否提供劳动工具 是为1，否为0 默认否(0)
     * @param is_transportation_expenses 是否提供交通费 是为1，否为0 默认否(0)
     * @param is_correspondence          是否提供通讯费 是为1，否为0 默认否(0)
     * @param audit                      工作完成标准
     * @param others_text                其他信息
     * @param work_week                  周历id数组
     * @param listener
     */
    public void labReplyOpinion(String lab_noid, String hire_noid, String skill_id, String contract_starttime,
                                String contract_endtime, String work_starttime, String work_endtime, String hire_endtime,
                                String amount, String subtotal, String unit, String settle_type, String is_insurance,
                                String is_dine, String is_lodging, String is_tool, String is_transportation_expenses,
                                String is_correspondence, String audit, String others_text, String work_week, ApiListener listener) {
        Log.e("log", "labReplyOpinion:" + lab_noid + "/" + hire_noid + "/" + skill_id + "/" + contract_starttime + "/" + contract_endtime + "/" + work_starttime + "/" + work_endtime + "/" +
                hire_endtime + "/" + amount + "/" + subtotal + "/" + unit + "/" + settle_type + "/" + is_insurance + "/" + is_dine + "/" + is_lodging + "/" + is_tool + "/" + is_transportation_expenses + "/" +
                is_correspondence + "/" + audit + "/" + others_text + "/" + work_week);
        RequestParams params = new RequestParams(AppConfig.BASE_URL + modle + "/labReplyOpinion");
        params.addBodyParameter("lab_noid", lab_noid);
        params.addBodyParameter("hire_noid", hire_noid);
        params.addBodyParameter("skill_id", skill_id);
        params.addBodyParameter("contract_starttime", contract_starttime);
        params.addBodyParameter("contract_endtime", contract_endtime);
        params.addBodyParameter("work_starttime", work_starttime);
        params.addBodyParameter("work_endtime", work_endtime);
        params.addBodyParameter("hire_endtime", hire_endtime);
        params.addBodyParameter("amount", amount);
        params.addBodyParameter("subtotal", subtotal);
        params.addBodyParameter("unit", unit);
        params.addBodyParameter("settle_type", settle_type);
        params.addBodyParameter("is_insurance", is_insurance);
        params.addBodyParameter("is_dine", is_dine);
        params.addBodyParameter("is_lodging", is_lodging);
        params.addBodyParameter("is_tool", is_tool);
        params.addBodyParameter("is_transportation_expenses", is_transportation_expenses);
        params.addBodyParameter("is_correspondence", is_correspondence);
        params.addBodyParameter("audit", audit);
        params.addBodyParameter("others_text", others_text);
        params.addBodyParameter("work_week", work_week);
        ApiTool apiTool = new ApiTool();
        apiTool.postApi(params, listener);
    }

    public void keepHireList(String noid, String page, String keywords, String skill_id, String contract_starttime,
                             String contract_endtime, String province_id, String city_id, String area_id,
                             String min_price, String max_price, ApiListener listener) {

        Log.e("log", "keepHireList:" + noid + "/" + page + "/" + keywords + "/" + skill_id + "/" + contract_starttime + "/" + contract_endtime + "/" + province_id + "/" +
                city_id + "/" + area_id + "/" + min_price + "/" + max_price);
        RequestParams params = new RequestParams(AppConfig.BASE_URL + modle + "/keepHireList");
        params.addBodyParameter("noid", noid);
        params.addBodyParameter("page", page);
        params.addBodyParameter("keywords", keywords);
        params.addBodyParameter("skill_id", skill_id);
        params.addBodyParameter("contract_starttime", contract_starttime);
        params.addBodyParameter("contract_endtime", contract_endtime);
        params.addBodyParameter("province_id", province_id);
        params.addBodyParameter("city_id", city_id);
        params.addBodyParameter("area_id", area_id);
        params.addBodyParameter("min_price", min_price);
        params.addBodyParameter("max_price", max_price);
        ApiTool apiTool = new ApiTool();
        apiTool.postApi(params, listener);
    }

    public void cancelAccept(String hire_noid, String lab_noid, ApiListener apiListener) {
        Log.e("log", "cancelAccept:" + hire_noid + "/" + lab_noid);
        RequestParams params = new RequestParams(AppConfig.BASE_URL + modle + "/cancelAccept");
        params.addBodyParameter("hire_noid", hire_noid);
        params.addBodyParameter("lab_noid", lab_noid);
        ApiTool apiTool = new ApiTool();
        apiTool.postApi(params, apiListener);
    }
}
