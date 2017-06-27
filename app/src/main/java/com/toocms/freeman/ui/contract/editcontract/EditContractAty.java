package com.toocms.freeman.ui.contract.editcontract;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.Selection;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.toocms.freeman.R;
import com.toocms.freeman.https.Contract;
import com.toocms.freeman.https.Hire;
import com.toocms.freeman.https.Sys;
import com.toocms.freeman.https.User;
import com.toocms.freeman.ui.BaseAty;
import com.toocms.freeman.ui.FreeManToggle;
import com.toocms.freeman.ui.index.SkillAty;
import com.toocms.freeman.ui.recruitment.RecruitAddressAty;
import com.toocms.freeman.ui.recruitment.joborder.JobOrderUnitAty;
import com.toocms.freeman.ui.util.MoneyUtils;
import com.toocms.freeman.ui.util.WorkOrder;
import com.toocms.freeman.ui.view.MyGridView;
import com.zero.autolayout.utils.AutoUtils;

import org.xutils.common.util.LogUtil;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import cn.qqtheme.framework.picker.DatePicker;
import cn.qqtheme.framework.picker.TimePicker;
import cn.zero.android.common.util.JSONUtils;
import cn.zero.android.common.util.ListUtils;
import cn.zero.android.common.util.MapUtils;
import cn.zero.android.common.view.FButton;

/**
 * 修改合同
 * Created by admin on 2017/3/28.
 */

public class EditContractAty extends BaseAty {
    public final int UNIT_ID = 0x0001;
    public final int ADDRESS_ID = 0x0010;
    @ViewInject(R.id.start_day)
    TextView tvStartDay;
    @ViewInject(R.id.end_day)
    TextView tvEndDay;
    @ViewInject(R.id.start_time)
    TextView tvStartTime;
    @ViewInject(R.id.end_time)
    TextView tvEndTime;
    @ViewInject(R.id.new_jo_week)
    private MyGridView week;
    @ViewInject(R.id.new_jo_week_content)
    private LinearLayout linlayWeek;
    @ViewInject(R.id.new_jo_week_tv)
    private TextView tvWeek;
    @ViewInject(R.id.new_jo_pay)
    private TextView tvPay;
    @ViewInject(R.id.new_jo_price)
    private EditText editPrice;
    @ViewInject(R.id.new_jo_other)
    private EditText editOther;
    @ViewInject(R.id.new_jo_work)
    private TextView tvWork;
    @ViewInject(R.id.new_job_order_unit)
    private TextView tvUnit;
    @ViewInject(R.id.new_jo_work_standard)
    private EditText editStandard;
    //保险
    @ViewInject(R.id.new_jo_insurance)
    private FreeManToggle tgBtnInsurance;
    //午餐
    @ViewInject(R.id.new_jo_lunch)
    private FreeManToggle tgBtnLunch;
    //住宿
    @ViewInject(R.id.new_jo_live)
    private FreeManToggle tgBtnLive;
    @ViewInject(R.id.new_jo_utils)
    private FreeManToggle tgBtnUtils;
    //交通费
    @ViewInject(R.id.new_jo_transport)
    private FreeManToggle tgBtnTransport;
    @ViewInject(R.id.new_jo_communications)
    private FreeManToggle tgBtnCommunications;
    @ViewInject(R.id.new_jo_addresss)
    TextView tvAddress;
    //    new_jo_end_retime_divid
    @ViewInject(R.id.new_jo_end_retime_click)
    LinearLayout tvReTime;
    @ViewInject(R.id.new_jo_end_retime_divid)
    View tvReTimeDivid;
    @ViewInject(R.id.new_jo_total)
    EditText editTotal;
    @ViewInject(R.id.m_r_i_edit_save)
    private FButton fBtnEdit;
    @ViewInject(R.id.m_r_i_person_content)
    private LinearLayout linlayPerson;
    @ViewInject(R.id.l_w_e_person)
    EditText editPerson;
    @ViewInject(R.id.new_jo_total_title)
    TextView tvTotalTitle;
    @ViewInject(R.id.new_jo_addresss_divid)
    View tvAddressDivid;
    @ViewInject(R.id.new_jo_addresss_click)
    LinearLayout linlayAddress;
    boolean isWeek = false;
    private ArrayList<String> stringArrayList = new ArrayList<>();
    private WeekGridAdapter weekGridAdapter;
    private ArrayList<Map<String, String>> weekList;
    private int startY;
    private int startM;
    private int startD;
    private int endY;
    private int endM;
    private int endD;
    private int startH;
    private int startMinute;
    private int endH;
    private int endMinute;
    private String startHour;
    private ArrayList<Map<String, String>> settle;//结算方式
    private ArrayList<String> weekID = new ArrayList<>();
    private ArrayList<Map<String, String>> skillItemData;
    private List<String> skillList;
    private String endHour;
    private String startMinutes;
    private String endMinutes;
    private String endMonth;
    private String endDay;
    private String startMonth;
    private String startDay;
    private ArrayList<Map<String, String>> sel_area;
    private User user;
    private List<String> areaIDList = new ArrayList<>();
    private String[] paths;
    private String localPath;
    private String startYear;
    /**
     * 获取修改合同数据[fullOpinion]
     *
     * @param contract_noid 合同单号
     * @param noid
     */
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
    private String contract_noid;
    private String noid;
    private Contract contract;
    private Sys sys;
    private Hire hire;
    private String settle_type;
    private String contract_starttime;
    private String contract_endtime;
    private String work_starttime;
    private String work_endtime;
    private String hire_endtime;
    private String amount;
    private String audit;
    private String skill_id;
    private String work_week;
    private String subtotal;
    private String unit;
    private String province_name;
    private String others_text;
    private String is_insurance;
    private String is_dine;
    private String is_lodging;
    private String is_tool;
    private String is_transportation_expenses;
    private String is_correspondence;
    private String city_name;
    private String area_id;
    private String longitude;
    private String latitude;
    private String ress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        weekGridAdapter = new WeekGridAdapter();
        week.setAdapter(weekGridAdapter);
        week.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                weekGridAdapter.setIsWeek(position);
            }
        });
        linlayWeek.setVisibility(View.GONE);
        fBtnEdit.setText("确认修改合同并提交");
        tvReTime.setVisibility(View.GONE);
        tvReTimeDivid.setVisibility(View.GONE);
        linlayPerson.setVisibility(View.VISIBLE);
        tvTotalTitle.setText("合同金额：");
        MoneyUtils.setPrice(editPrice);
        MoneyUtils.setPrice(editTotal);
        tvAddressDivid.setVisibility(View.VISIBLE);
        linlayAddress.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        List<String> list = new ArrayList<>();
        skillList = new ArrayList<>();
        Map<String, Map<String, List<Map<String, String>>>> workOrder1 = WorkOrder.getInstance().getOrder1();
        if (MapUtils.isEmpty(workOrder1)) {
            tvWork.setText("");
        } else {
            if (ListUtils.isEmpty(skillItemData)) return;
            for (int j = 0; j < ListUtils.getSize(skillItemData); j++) {
                Map<String, String> map = skillItemData.get(j);
                if (workOrder1.containsKey(map.get("skill_id"))) {
                    Map<String, List<Map<String, String>>> workOrder2 = workOrder1.get(map.get("skill_id"));
                    Iterator iterator = workOrder2.entrySet().iterator();
                    while (iterator.hasNext()) {
                        Map.Entry entry = (Map.Entry) iterator.next();
                        for (int i = 0; i < ListUtils.getSize(workOrder2.get(entry.getKey())); i++) {
//                            if (ListUtils.getSize(list) >= 3) break;
                            Iterator iterator2 = workOrder2.get(entry.getKey()).get(i).entrySet().iterator();
                            while (iterator2.hasNext()) {
                                Map.Entry entry2 = (Map.Entry) iterator2.next();
                                list.add(entry2.getValue().toString());
                                skillList.add(entry2.getKey().toString());
                            }
                        }
                    }
                }
            }
            if (ListUtils.isEmpty(list)) tvWork.setText("");
            else tvWork.setText(ListUtils.join(list));

        }
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.aty_modify_recruitment_information;
    }

    @Override
    protected void initialized() {
        contract = new Contract();
        sys = new Sys();
        hire = new Hire();
    }

    @Override
    protected void requestData() {
        showProgressContent();
        contract_noid = getIntent().getStringExtra("contract_noid");
        noid = application.getUserInfo().get("noid");
        contract.fullOpinion(contract_noid, noid, this);
        sys.getSkillList("0", "0", this);
        hire.getFormData(this);
    }

    @Override
    public void onComplete(RequestParams params, String result) {
        if (params.getUri().contains("Hire/getFormData")) {
            Map<String, String> map = JSONUtils.parseDataToMap(result);
            weekList = JSONUtils.parseKeyAndValueToMapList(map.get("week"));
            settle = JSONUtils.parseKeyAndValueToMapList(map.get("settle"));
        } else if (params.getUri().contains("Sys/getSkillList")) {
            skillItemData = JSONUtils.parseDataToMapList(result);
        } else if (params.getUri().contains("Contract/fullOpinion")) {
            Map<String, String> map = JSONUtils.parseDataToMap(result);
            ArrayList<Map<String, String>> skill = JSONUtils.parseKeyAndValueToMapList(map.get("skill"));
            List<Map<String, String>> workOrder3;
            ArrayList<String> skillName = new ArrayList<>();
            for (int i = 0; i < ListUtils.getSize(skill); i++) {
                Map<String, String> mapSkill = new HashMap<>();
                paths = skill.get(i).get("path").split("-");
                workOrder3 = WorkOrder.getInstance().getOrder3(paths[0], paths[1]);
                mapSkill.put(skill.get(i).get("sid"), skill.get(i).get("name"));
                if (ListUtils.isEmpty(workOrder3)) {
                    workOrder3 = new ArrayList<>();
                    mapSkill.put(skill.get(i).get("sid"), skill.get(i).get("name"));
                    skillName.add(skill.get(i).get("name"));
                    skillList.add(skill.get(i).get("sid"));
                    workOrder3.add(mapSkill);
                    WorkOrder.getInstance().addOrder3(paths[0], paths[1], workOrder3);
                } else {
                    workOrder3.add(mapSkill);
                    skillName.add(skill.get(i).get("name"));
                    skillList.add(skill.get(i).get("sid"));
                }
            }
            tvWork.setText(ListUtils.join(skillName));
            editOther.setText(map.get("others_text"));
            editStandard.setText(map.get("audit"));
            editPerson.setText(map.get("staff"));
            Editable editPersonText = editPerson.getText();
            Selection.setSelection(editPersonText, editPersonText.length());
            tvPay.setText(map.get("settle_type_name"));
            settle_type = map.get("settle_type");
            editPrice.setText(map.get("subtotal"));
            editTotal.setText(map.get("amount"));
            String unit_name = map.get("unit_name");
            unit = map.get("unit");
            tvUnit.setText(unit_name);
            tvStartDay.setText(map.get("contract_starttime"));
            contract_starttime = map.get("contract_starttime");
            if (!TextUtils.isEmpty(contract_starttime)) {
                String[] contract_start = contract_starttime.split("-");
                startYear = contract_start[0];
                startY = Integer.parseInt(startYear);
                startMonth = contract_start[1];
                startM = Integer.parseInt(startMonth);
                startDay = contract_start[2];
                startD = Integer.parseInt(startDay);
            }
            tvEndDay.setText(map.get("contract_endtime"));
            contract_endtime = map.get("contract_endtime");
            if (!TextUtils.isEmpty(contract_endtime)) {
                String[] contract_end = contract_endtime.split("-");
                String con_end = contract_end[0];
                endY = Integer.parseInt(con_end);
                endMonth = contract_end[1];
                endM = Integer.parseInt(endMonth);
                endDay = contract_end[2];
                endD = Integer.parseInt(endDay);
            }
            tvStartTime.setText(map.get("work_starttime"));
            work_starttime = map.get("work_starttime");
            if (!TextUtils.isEmpty(work_starttime)) {
                String[] work_start = work_starttime.split(":");
                startHour = work_start[0];
                startH = Integer.parseInt(startHour);
                startMinutes = work_start[1];
                startMinute = Integer.parseInt(startMinutes);
            }
            tvEndTime.setText(map.get("work_endtime"));
            work_endtime = map.get("work_endtime");
            if (!TextUtils.isEmpty(work_endtime)) {
                String[] work_end = work_endtime.split(":");
                endHour = work_end[0];
                endH = Integer.parseInt(endHour);
                endMinutes = work_end[1];
                endMinute = Integer.parseInt(endMinutes);
            }
            ArrayList<Map<String, String>> work_week_list = JSONUtils.parseKeyAndValueToMapList(map.get("work_week"));
            List<String> week_name = new ArrayList<>();
            weekID = new ArrayList<>();
            if (!ListUtils.isEmpty(work_week_list)) {
                for (int i = 0; i < ListUtils.getSize(work_week_list); i++) {
                    weekGridAdapter.setIsWeek(Integer.parseInt(work_week_list.get(i).get("sid")) - 1);
                    week_name.add(work_week_list.get(i).get("name"));
                    weekID.add(work_week_list.get(i).get("sid"));
                }
            }
            tvAddress.setText(map.get("ress"));
            ress = map.get("ress");
            province_name = map.get("province_name");
            city_name = map.get("city_name");
            area_id = map.get("area_name");
            longitude = map.get("longitude");
            latitude = map.get("latitude");
//            work_week = ListUtils.join(weekID);
            tvWeek.setText(ListUtils.join(week_name));
            if (TextUtils.equals(map.get("is_correspondence"), "0")) {
                tgBtnCommunications.setToggleOff();
            } else tgBtnCommunications.setToggleOn();
            if (TextUtils.equals(map.get("is_dine"), "0")) tgBtnLunch.setToggleOff();
            else tgBtnLunch.setToggleOn();
            if (TextUtils.equals(map.get("is_lodging"), "0")) tgBtnLive.setToggleOff();
            else tgBtnLive.setToggleOn();
            if (TextUtils.equals(map.get("is_tool"), "0")) tgBtnUtils.setToggleOff();
            else tgBtnUtils.setToggleOn();
            if (TextUtils.equals(map.get("is_transportation_expenses"), "0"))
                tgBtnTransport.setToggleOff();
            else tgBtnTransport.setToggleOn();
            if (TextUtils.equals(map.get("is_insurance"), "0")) tgBtnInsurance.setToggleOff();
            else tgBtnInsurance.setToggleOn();
        } else if (params.getUri().contains("Contract/capContractOpinion")) {
            showToast(JSONUtils.parseKeyAndValueToMap(result).get("message"));
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    finish();
                }
            }, 1500);
        } else if (params.getUri().contains("Contract/labContractOpinion")) {
            showToast(JSONUtils.parseKeyAndValueToMap(result).get("message"));
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    finish();
                }
            }, 1500);
        }
        super.onComplete(params, result);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            return;
        }
        switch (requestCode) {

            case UNIT_ID:
                tvUnit.setText(data.getStringExtra("name"));
                unit = data.getStringExtra("unit_id");
                break;
            case ADDRESS_ID:
                province_name = data.getStringExtra("province_name");
                city_name = data.getStringExtra("city_name");
                area_id = data.getStringExtra("area_name");
                ress = data.getStringExtra("ress");
                latitude = data.getStringExtra("latitude");
                longitude = data.getStringExtra("longitude");
                tvAddress.setText(ress);
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        WorkOrder.getInstance().clear();
    }

    @Override
    public void finish() {
        WorkOrder.getInstance().clear();
        super.finish();
    }

    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }

    @Event({R.id.new_job_order_unit_click, R.id.new_jo_addresss_click, R.id.new_jo_work_click
            , R.id.new_jo_week_click, R.id.new_jo_end_retime_click, R.id.start_day_click, R.id.end_day_click
            , R.id.start_time_click, R.id.end_time_click, R.id.new_jo_werk_sure, R.id.new_jo_pay_click
            , R.id.new_jo_end_retime_click, R.id.m_r_i_edit_save})
    private void onClick(View view) {
        switch (view.getId()) {
            case R.id.new_job_order_unit_click:
                startActivityForResult(JobOrderUnitAty.class, null, UNIT_ID);
                break;
            case R.id.new_jo_addresss_click:
                Bundle addBundle = new Bundle();
                addBundle.putString("flag", "new_jo");
                startActivityForResult(RecruitAddressAty.class, addBundle, ADDRESS_ID);
                break;
            case R.id.new_jo_work_click:
                startActivity(SkillAty.class, null);
                break;
            case R.id.new_jo_week_click:
                isWeek = !isWeek;
                if (isWeek) {
                    linlayWeek.setVisibility(View.VISIBLE);
                } else {
                    linlayWeek.setVisibility(View.GONE);
                }
                break;
            case R.id.start_day_click:

                DatePicker startDP = onYearMonthDayPicker(1969, 2117, true, new DatePicker.OnYearMonthDayPickListener() {
                    @Override
                    public void onDatePicked(String year, String month, String day) {
                        startY = Integer.parseInt(year);
                        startM = Integer.parseInt(month);
                        startD = Integer.parseInt(day);
                        if (!TextUtils.isEmpty(contract_endtime)) {
                            String endtime = contract_endtime.replace("-", "");
                            String startTime = year + month + day;
                            if (Integer.parseInt(startTime) > Integer.parseInt(endtime)) {
                                showToast("合同截止日期必须大于合同开始日期");
                                return;
                            }
                        }
                        startMonth = month;
                        startDay = day;
                        startYear = year;
                        tvStartDay.setText(year + "-" + month + "-" + day);
                    }
                });
                startDP.setSelectedItem(startY, startM, startD);
                startDP.show();
                break;
            case R.id.end_day_click:
                if (TextUtils.isEmpty(startYear)) {
                    showToast("请先选择合同开始日期");
                    return;
                }
                DatePicker datePicker = onYearMonthDayPicker(startY, 2117, true, new DatePicker.OnYearMonthDayPickListener() {
                    @Override
                    public void onDatePicked(String year, String month, String day) {
                        endY = Integer.parseInt(year);
                        endM = Integer.parseInt(month);
                        endD = Integer.parseInt(day);
                        endMonth = month;
                        endDay = day;
                        if (!TextUtils.isEmpty(contract_starttime)) {
                            String startTime = contract_starttime.replace("-", "");
                            String endtime = year + month + day;
                            if (Integer.parseInt(startTime) >Integer.parseInt(endtime)) {
                                showToast("合同截止日期必须大于合同开始日期");
                                return;
                            }
                        }
                        tvEndDay.setText(year + "-" + month + "-" + day);
//                        if (endY > startY || endM > startM) {
//
//                            return;
//                        }
//                        if (endM == startM && endD >= startD) {
//                            tvEndDay.setText(year + "-" + month + "-" + day);
//                        } else {
//                            showToast("合同截止日期必须大于合同开始日期");
//                        }
                    }
                });
                datePicker.setSelectedItem(endY, endM, endD);
                datePicker.show();
                break;
            case R.id.start_time_click:
                TimePicker startTP = onTimePicker(0, 23, true, new TimePicker.OnTimePickListener() {


                    @Override
                    public void onTimePicked(String hour, String minute) {
                        startH = Integer.parseInt(hour);
                        startHour = hour;
                        startMinute = Integer.parseInt(minute);
                        startMinutes = minute;
                        tvStartTime.setText(hour + ":" + minute);
                    }
                });
                startTP.setSelectedItem(startH, startMinute);
                startTP.show();
                break;
            case R.id.end_time_click:
                if (TextUtils.isEmpty(startHour)) {
                    showToast("请先选择工作开始时间");
                    return;
                }
                TimePicker timePicker = onTimePicker(startH, 23, true, new TimePicker.OnTimePickListener() {
                    @Override
                    public void onTimePicked(String hour, String minute) {
                        endH = Integer.parseInt(hour);
                        endMinute = Integer.parseInt(minute);
                        endHour = hour;
                        endMinutes = minute;
                        if (endH > startH || endMinute > startMinute) {
                            tvEndTime.setText(hour + ":" + minute);
                        } else {
                            showToast("工作截止时间必须大于工作开始时间");
                        }
                    }
                });
                timePicker.setSelectedItem(endH, endMinute);
                timePicker.show();
                break;
            case R.id.new_jo_werk_sure:
                weekID.clear();
                StringBuffer stringBuffer = new StringBuffer();
                for (int i = 0; i < weekGridAdapter.isWeek.length; i++) {
                    if (weekGridAdapter.isWeek[i]) {
                        stringBuffer.append(weekList.get(i).get("name") + ",");
                        weekID.add(weekList.get(i).get("sid"));
                    }
                }
                if (stringBuffer.length() > 0) {
                    stringBuffer.deleteCharAt(stringBuffer.length() - 1);
//                    showToast("您选择了" + stringBuffer);
                    tvWeek.setText(stringBuffer);
                    linlayWeek.setVisibility(View.GONE);
                    isWeek = false;
                } else {
                    showToast("请选择工作周历");
                    return;
                }
                break;
            case R.id.new_jo_pay_click:
                final CharSequence[] charSequences = new CharSequence[ListUtils.getSize(settle)];
                for (int i = 0; i < ListUtils.getSize(settle); i++) {
                    charSequences[i] = settle.get(i).get("name");
                }

                showItemsDialog("", charSequences, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
//                        switch (which) {
//                            case 0:
                        tvPay.setText(charSequences[which]);
//                        settle_type = settle.get(which).get("sid");
//                                break;
//                        }
                    }
                });
                break;
            case R.id.m_r_i_edit_save:
                amount = editTotal.getText().toString().trim();
                audit = editStandard.getText().toString().trim();
                if (ListUtils.isEmpty(skillList)) skill_id = "";
                else skill_id = ListUtils.join(skillList);
//                LogUtil.d(skill_id);
//                staff = editPerson.getText().toString();
                contract_starttime = startYear + "-" + startMonth + "-" + startDay;
                contract_endtime = endY + "-" + endMonth + "-" + endDay;
                work_starttime = startHour + ":" + startMinutes;
                work_endtime = endHour + ":" + endMinutes;
                if (!ListUtils.isEmpty(weekID))
                    work_week = ListUtils.join(weekID);
                subtotal = editPrice.getText().toString();
                others_text = editOther.getText().toString();
//                if (ListUtils.getSize(selectImagePath) >= 2)
//                    photos = selectImagePath.get(1);
                if (tgBtnInsurance.getToggle()) {
                    is_insurance = "1";
                } else {
                    is_insurance = "0";
                }
                if (tgBtnLunch.getToggle()) is_dine = "1";
                else is_dine = "0";
                if (tgBtnLive.getToggle()) is_lodging = "1";
                else is_lodging = "0";
                if (tgBtnUtils.getToggle()) is_tool = "1";
                else is_tool = "0";
                if (tgBtnTransport.getToggle()) is_transportation_expenses = "1";
                else is_transportation_expenses = "0";
                if (tgBtnCommunications.getToggle()) is_correspondence = "1";
                else is_correspondence = "0";
                String staff = editPerson.getText().toString();
                if (TextUtils.isEmpty(noid)) {
                    LogUtil.e("请检查noid");
                    return;
                } else if (TextUtils.isEmpty(amount)) {
                    showToast("请输入招工金额");
                    return;
                } else if (TextUtils.isEmpty(skill_id)) {
                    showToast("请选择工作");
                    return;
                } else if (TextUtils.isEmpty(staff)) {
                    showToast("请输入招工人数");
                    return;
                } else if (startD == 0) {
                    showToast("请选择合同开始日期");
                    return;
                } else if (endD == 0) {
                    showToast("请选择合同截止日期");
                    return;
                } else if (TextUtils.isEmpty(startHour)) {
                    showToast("请选择每天工作开始时间");
                    return;
                } else if (TextUtils.isEmpty(endHour)) {
                    showToast("请选择每天工作截止时间");
                    return;
                } else if (TextUtils.isEmpty(work_week)) {
                    showToast("请选择工作周历");
                    return;
                } else if (TextUtils.isEmpty(subtotal)) {
                    showToast("请输入单价");
                    return;
                } else if (TextUtils.isEmpty(unit)) {
                    showToast("请选择单价单位");
                    return;
                } else if (TextUtils.isEmpty(settle_type)) {
                    showToast("请选择结算方式");
                    return;
                } else if (TextUtils.isEmpty(province_name)) {
                    showToast("请选择工作地点");
                    return;
                }
                if (TextUtils.equals(getIntent().getStringExtra("tag"), "cap")) {
                    contract.capEditContract(contract_noid, noid, skill_id, staff, contract_starttime, contract_endtime, work_starttime
                            , work_endtime, work_week, subtotal, unit, amount, settle_type, province_name, city_name, area_id, ress, longitude,
                            latitude, is_insurance, is_dine, is_lodging, is_tool, is_transportation_expenses, is_correspondence, audit, others_text, this);
                } else {
                    contract.labContractOpinion(contract_noid, noid, skill_id, staff, contract_starttime, contract_endtime, work_starttime
                            , work_endtime, work_week, subtotal, unit, amount, settle_type, province_name, city_name, area_id, ress, longitude,
                            latitude, is_insurance, is_dine, is_lodging, is_tool, is_transportation_expenses, is_correspondence, audit, others_text, this);
                }
                break;
        }
    }


    private class WeekGridAdapter extends BaseAdapter {

        private WeekGridAdapter.ViewHolder viewHolder;
        public Boolean[] isWeek = new Boolean[]{false, false, false, false, false, false, false};

        @Override
        public int getCount() {
            return ListUtils.getSize(weekList);
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        public void setIsWeek(int position) {
            isWeek[position] = !isWeek[position];
            notifyDataSetChanged();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                viewHolder = new WeekGridAdapter.ViewHolder();
                convertView = LayoutInflater.from(EditContractAty.this).inflate(R.layout.listitem_njo_week, parent, false);
                x.view().inject(viewHolder, convertView);
                AutoUtils.autoSize(convertView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            if (isWeek[position]) {
                viewHolder.tvWeek.setCompoundDrawablesWithIntrinsicBounds(R.drawable.btn_opt, 0, 0, 0);
            } else {
                viewHolder.tvWeek.setCompoundDrawablesWithIntrinsicBounds(R.drawable.btn_opt_unselected, 0, 0, 0);
            }
            viewHolder.tvWeek.setText(weekList.get(position).get("name"));
            return convertView;
        }

        public class ViewHolder {
            @ViewInject(R.id.list_njo_week)
            TextView tvWeek;
        }
    }
}
