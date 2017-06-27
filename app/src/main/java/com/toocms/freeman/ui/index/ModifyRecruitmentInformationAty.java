package com.toocms.freeman.ui.index;

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
import com.toocms.freeman.config.JsonArryToList;
import com.toocms.freeman.https.Hire;
import com.toocms.freeman.https.Sys;
import com.toocms.freeman.https.User;
import com.toocms.freeman.ui.BaseAty;
import com.toocms.freeman.ui.FreeManToggle;
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

/**
 * 2017-5-16
 * - 修改招工信息
 */
public class ModifyRecruitmentInformationAty extends BaseAty {


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
    @ViewInject(R.id.new_jo_end_retime)
    TextView tvReTime;
    @ViewInject(R.id.new_jo_total)
    EditText editTotal;
    @ViewInject(R.id.m_r_i_person_content)
    private LinearLayout linlayPerson;
    @ViewInject(R.id.l_w_e_person_divid)
    private View vPersonDivid;
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

    private String noid;
    private String hire_id;
    private Hire hire;
    private Sys sys;
    private String hire_endtime;
    private String province_name;
    private String city_name;
    private String area_id;
    private String ress;
    private String latitude;
    private String longitude;
    private String contract_endtime;
    private String work_starttime;
    private String work_endtime;
    private String settle_type;
    private String unit;
    private String contract_starttime;
    private String amount;
    private String audit;
    private String skill_id;
    private String work_week;
    private String subtotal;
    private String others_text;
    private String is_insurance;
    private String is_dine;
    private String is_lodging;
    private String is_tool;
    private String is_transportation_expenses;
    private String is_correspondence;
    /**
     * 获取修改明细[fullOpinion]
     *
     * @param hire_noid 招工单编号
     * @param lab_noid  劳方编号
     */
    private String hire_noid;
    private String lab_noid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActionBar.setTitle("修改招工信息");

        linlayPerson.setVisibility(View.GONE);
        vPersonDivid.setVisibility(View.GONE);
        MoneyUtils.setPrice(editTotal);
        MoneyUtils.setPrice(editPrice);
        week.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                weekGridAdapter.setIsWeek(position);
            }
        });
        linlayWeek.setVisibility(View.GONE);

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
        hire = new Hire();
        sys = new Sys();
    }

    @Override
    protected void requestData() {
        WorkOrder.getInstance().clear();
        hire.getFormData(this);
        sys.getSkillList("0", "0", this);
        showProgressContent();
//        noid = application.getUserInfo().get("noid");
//        hire_id = getIntent().getStringExtra("hire_id");
//        hire.single(noid, hire_id, this);
        if (getIntent().getStringExtra("flag") != null && getIntent().getStringExtra("flag").equals("wzw1")) {
            hire.labFullOpinion(getIntent().getStringExtra("hire_noid"),
                    application.getUserInfo().get("noid"), this);

        } else {
            hire_noid = getIntent().getStringExtra("hire_noid");
            lab_noid = getIntent().getStringExtra("lab_noid");
            hire.fullOpinion(hire_noid, lab_noid, this);
        }

    }

    @Override
    public void onComplete(RequestParams params, String result) {
        if (params.getUri().contains("Hire/getFormData")) {
            Map<String, String> map = JSONUtils.parseDataToMap(result);
            weekList = JSONUtils.parseKeyAndValueToMapList(map.get("week"));
            settle = JSONUtils.parseKeyAndValueToMapList(map.get("settle"));
            weekGridAdapter = new WeekGridAdapter();
            week.setAdapter(weekGridAdapter);
        } else if (params.getUri().contains("Sys/getSkillList")) {
            skillItemData = JSONUtils.parseDataToMapList(result);
        } else if (params.getUri().contains("Hire/fullOpinion") || params.getUri().contains("Hire/labFullOpinion")) {
            Map<String, String> map = JSONUtils.parseDataToMap(result);
            ArrayList<Map<String, String>> skill = JSONUtils.parseKeyAndValueToMapList(map.get("skill_list"));
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

            tvPay.setText(map.get("settle_type_name"));
            settle_type = map.get("settle_type");
            editPrice.setText(map.get("subtotal"));

            editTotal.setText(map.get("amount"));
            Editable editPriceText = editTotal.getText();
            Selection.setSelection(editPriceText, editPriceText.length());
//            ArrayList<Map<String, String>> unitMap = JSONUtils.parseKeyAndValueToMapList(map.get("unit"));
            String unit_name = map.get("unit_name");
            tvUnit.setText(unit_name);
            unit = map.get("unit");
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
                endDay = contract_end[2];
                endM = Integer.parseInt(endMonth);
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
                endMinutes = work_end[1];
                endH = Integer.parseInt(endHour);
                endMinute = Integer.parseInt(endMinutes);
            }
            tvReTime.setText(map.get("hire_endtime"));
            hire_endtime = map.get("hire_endtime");
//            ArrayList<Map<String, String>> week_name = JSONUtils.parseKeyAndValueToMapList(map.get("week"));
//            StringBuffer stringBuffer = new StringBuffer();
//            for (int j = 0; j < ListUtils.getSize(week_name); j++) {
//                weekID.add(week_name.get(j).get("sid"));
//                weekGridAdapter.isWeek[Integer.parseInt(week_name.get(j).get("sid")) - 1] = true;
//                weekGridAdapter.notifyDataSetChanged();
//                stringBuffer.append(week_name.get(j).get("name") + ",");
//            }
//            if (stringBuffer.length() > 0) {
//                stringBuffer.deleteCharAt(stringBuffer.length() - 1);
////                    showToast("您选择了" + stringBuffer);
//                tvWeek.setText(stringBuffer);
//            }
            work_week = map.get("work_week");
//            设置周历选中
            if (!TextUtils.isEmpty(work_week)) {
                String[] works = work_week.split(",");
                for (int i = 0; i < works.length; i++) {
                    if (weekGridAdapter == null) {
                        weekGridAdapter = new WeekGridAdapter();
                    }
                    if (!ListUtils.isEmpty(weekList)) {
                        weekGridAdapter.setIsWeek(Integer.parseInt(works[i]) - 1);
                    }

                }
            }
            List<String> week_name = JsonArryToList.strList(map.get("work_week_name"));
            tvWeek.setText(ListUtils.join(week_name));
            if (TextUtils.equals(map.get("is_correspondence"), 0 + "")) {
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
        } else if (params.getUri().contains("Hire/replyOpinion") || params.getUri().contains("Hire/labReplyOpinion")) {
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

    @Event({R.id.new_job_order_unit_click, R.id.new_jo_addresss_click, R.id.new_jo_work_click
            , R.id.new_jo_week_click, R.id.new_jo_end_retime_click, R.id.start_day_click, R.id.end_day_click
            , R.id.start_time_click, R.id.end_time_click, R.id.new_jo_werk_sure, R.id.new_jo_pay_click
            , R.id.new_jo_end_retime_click, R.id.m_r_i_edit_save})
    private void onClick(View view) {
        switch (view.getId()) {
            case R.id.new_job_order_unit_click:
                startActivityForResult(JobOrderUnitAty.class, null, UNIT_ID);
                break;
//            case R.id.new_jo_addresss_click:
//                Bundle addBundle = new Bundle();
//                addBundle.putString("flag", "new_jo");
//                startActivityForResult(RecruitAddressAty.class, addBundle, ADDRESS_ID);
//                break;
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
                DatePicker dateStartPicker = onYearMonthDayPicker(1999, 2117, true, new DatePicker.OnYearMonthDayPickListener() {
                    @Override
                    public void onDatePicked(String year, String month, String day) {
                        startY = Integer.parseInt(year);
                        startM = Integer.parseInt(month);
                        startD = Integer.parseInt(day);
                        startMonth = month;
                        startDay = day;
                        startYear = year;
                        tvStartDay.setText(year + "-" + month + "-" + day);
                    }
                });
                if (startY != 0) {
                    dateStartPicker.setSelectedItem(startY, startM, startD);
                }
                dateStartPicker.show();
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
                        if (endY > startY || endM > startM) {
                            tvEndDay.setText(year + "-" + month + "-" + day);
                            return;
                        }
                        if (endM == startM && endD >= startD) {
                            tvEndDay.setText(year + "-" + month + "-" + day);
                        } else {
                            showToast("合同截止日期必须大于合同开始日期");
                        }
                    }
                });
                if (endY != 0)
                    datePicker.setSelectedItem(endY, endM, endD);
                datePicker.show();
                break;
            case R.id.start_time_click:
                TimePicker timeStartPicker = onTimePicker(0, 23, true, new TimePicker.OnTimePickListener() {
                    @Override
                    public void onTimePicked(String hour, String minute) {
                        startH = Integer.parseInt(hour);
                        startHour = hour;
                        startMinute = Integer.parseInt(minute);
                        startMinutes = minute;
                        tvStartTime.setText(hour + ":" + minute);
                    }
                });
                if (!TextUtils.isEmpty(startHour)) {
                    timeStartPicker.setSelectedItem(startH, startMinute);
                }
                timeStartPicker.show();
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
                if (!TextUtils.isEmpty(endHour)) {
                    timePicker.setSelectedItem(endH, endMinute);
                }
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
            case R.id.new_jo_end_retime_click:
                if (startY == 0 || endY == 0) {
                    showToast("请先选择合同开始和合同截止时间");
                    return;
                }
                onYearMonthDayPicker(2000, startY, new DatePicker.OnYearMonthDayPickListener() {
                    @Override
                    public void onDatePicked(String year, String month, String day) {
//                        showToast(year + "-" + month + "-" + day);
                        hire_endtime = year + "-" + month + "-" + day;
                        tvReTime.setText(hire_endtime);
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
                noid = application.getUserInfo().get("noid");
                if (TextUtils.isEmpty(noid)) {
                    LogUtil.e("请检查noid");
                    return;
                } else if (TextUtils.isEmpty(hire_endtime)) {
                    showToast("还未选择招工截止日期");
                    return;
                } else if (TextUtils.isEmpty(amount)) {
                    showToast("请输入招工金额");
                    return;
                }
//                else if (TextUtils.isEmpty(audit)) {
//                    showToast("请输入工作标准");
//                    return;
//                }
                else if (TextUtils.isEmpty(skill_id)) {
                    showToast("请选择工作");
                    return;
                }
//                else if (TextUtils.isEmpty(staff)) {
//                    showToast("请输入招工人数");
//                    return;
//                }
                else if (startD == 0) {
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
                }

                if (getIntent().getStringExtra("flag") != null && getIntent().getStringExtra("flag").equals("wzw1")) {
                    hire.labReplyOpinion(application.getUserInfo().get("noid"), getIntent().getStringExtra("hire_noid"), skill_id, contract_starttime, contract_endtime, work_starttime, work_endtime
                            , hire_endtime, amount, subtotal, unit, settle_type, is_insurance, is_dine, is_lodging, is_tool, is_transportation_expenses, is_correspondence
                            , audit, others_text, work_week, this);
                } else {
                    hire.replyOpinion(noid, lab_noid, hire_noid, skill_id, contract_starttime, contract_endtime, work_starttime, work_endtime
                            , work_week, hire_endtime, amount, subtotal, unit, settle_type, is_insurance, is_dine, is_lodging, is_tool, is_transportation_expenses, is_correspondence
                            , audit, others_text, this);
                }

                LogUtil.e(skill_id);
//                else if (ListUtils.getSize(selectImagePath) == 1) {
//                    showToast("请添加图片");
//                    return;
//                }
//
//                showProgressDialog();
//                hire.save(noid, hire_endtime, amount, audit, skill_id, staff, contract_starttime, contract_endtime
//                        , work_starttime, work_endtime, work_week, subtotal, unit, settle_type, others_text, null, is_insurance
//                        , is_dine, is_lodging, is_tool, is_transportation_expenses, is_correspondence, hire_id, province_name
//                        , city_name, area_id, ress, longitude, latitude, this);
                break;
        }
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

    private class WeekGridAdapter extends BaseAdapter {

        private WeekGridAdapter.ViewHolder viewHolder;
        public Boolean[] isWeek = new Boolean[ListUtils.getSize(weekList)];

        public WeekGridAdapter() {
            for (int i = 0; i < ListUtils.getSize(weekList); i++) {
                isWeek[i] = false;
            }
        }

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
                convertView = LayoutInflater.from(ModifyRecruitmentInformationAty.this).inflate(R.layout.listitem_njo_week, parent, false);
                x.view().inject(viewHolder, convertView);
                AutoUtils.autoSize(convertView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (WeekGridAdapter.ViewHolder) convertView.getTag();
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
