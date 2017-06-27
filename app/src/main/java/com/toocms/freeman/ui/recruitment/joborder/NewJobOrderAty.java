package com.toocms.freeman.ui.recruitment.joborder;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.toocms.frame.image.ImageLoader;
import com.toocms.freeman.R;
import com.toocms.freeman.config.Constants;
import com.toocms.freeman.config.JsonArryToList;
import com.toocms.freeman.https.Contract;
import com.toocms.freeman.https.Hire;
import com.toocms.freeman.https.Sys;
import com.toocms.freeman.ui.BaseAty;
import com.toocms.freeman.ui.FreeManToggle;
import com.toocms.freeman.ui.index.SkillAty;
import com.toocms.freeman.ui.recruitment.RecruitAddressAty;
import com.toocms.freeman.ui.util.MoneyUtils;
import com.toocms.freeman.ui.util.WorkOrder;
import com.toocms.freeman.ui.view.MyGridView;
import com.toocms.freeman.ui.view.PhotoZoomAty;
import com.zero.autolayout.utils.AutoUtils;

import org.xutils.common.Callback;
import org.xutils.common.util.LogUtil;
import org.xutils.http.RequestParams;
import org.xutils.image.ImageOptions;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import cn.qqtheme.framework.picker.DatePicker;
import cn.qqtheme.framework.picker.TimePicker;
import cn.zero.android.common.permission.PermissionFail;
import cn.zero.android.common.permission.PermissionSuccess;
import cn.zero.android.common.util.JSONUtils;
import cn.zero.android.common.util.ListUtils;
import cn.zero.android.common.util.MapUtils;
import cn.zero.android.common.view.FButton;


/**
 * Created by admin on 2017/3/23.
 */

public class NewJobOrderAty extends BaseAty {
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
    @ViewInject(R.id.new_job_order_imgs)
    private MyGridView imgs;
    @ViewInject(R.id.new_jo_week)
    private MyGridView week;
    @ViewInject(R.id.new_jo_week_content)
    private LinearLayout linlayWeek;
    @ViewInject(R.id.new_jo_week_tv)
    private TextView tvWeek;
    @ViewInject(R.id.new_jo_pay)
    private TextView tvPay;
    @ViewInject(R.id.new_jo_end_retime)
    private TextView tvReTime;
    @ViewInject(R.id.new_jo_total)
    private EditText editTotal;
    @ViewInject(R.id.new_jo_price)
    private EditText editPrice;
    @ViewInject(R.id.new_jo_work_standard)
    private EditText editStandard;
    @ViewInject(R.id.new_jo_other)
    private EditText editOther;
    @ViewInject(R.id.new_jo_work)
    private TextView tvWork;
    @ViewInject(R.id.new_jo_person)
    private EditText editPerson;
    @ViewInject(R.id.new_job_order_unit)
    private TextView tvUnit;
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
    @ViewInject(R.id.new_jo_edit_content)
    private LinearLayout linlayEdit;
    @ViewInject(R.id.new_jo_btn_content)
    private LinearLayout linlayBtn;
    @ViewInject(R.id.new_jo_sure_contract)
    LinearLayout linlaySure;
    @ViewInject(R.id.new_jo_f_btn_keep)
    FButton fBtnKeep;
    boolean isWeek = false;
    private ArrayList<String> stringArrayList = new ArrayList<>();
    private ArrayList<String> selectImagePath = new ArrayList<>();
    private ImgGridAdapter imgGridAdapter;
    private WeekGridAdapter weekGridAdapter;
    private Hire hire;
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

    /**
     * 创建预置信息[getFormData]
     * 创建或修改时，表单自带信息（如周历列表）
     */
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
    private String noid;
    private String hire_endtime;
    private String amount;
    private String audit;
    private String skill_id;
    private String staff;
    private String contract_starttime;
    private String contract_endtime;
    private String work_starttime;
    private String work_endtime;
    private String work_week;
    private String subtotal;
    private String unit;
    private String settle_type;
    private String others_text;
    private String photos;
    private String is_insurance;
    private String is_dine;
    private String is_lodging;
    private String is_tool;
    private String is_transportation_expenses;
    private String is_correspondence;
    private String hire_id = null;
    private String province_name;
    private String city_name;
    private String area_id;
    private String ress;
    private Sys sys;
    private ArrayList<Map<String, String>> skillItemData;
    private List<String> skillList;
    private String endHour;
    private String startMinutes;
    private String endMinutes;
    private String endMonth;
    private String endDay;
    private String startMonth;
    private String startDay;
    private String latitude;
    private String longitude;
    private String flag;
    private String startYear;
    private String[] paths;
    private ArrayList<Map<String, String>> unitList;
    private Contract contract;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (TextUtils.equals(getIntent().getStringExtra("flag"), "edit")) {
            mActionBar.setTitle("修改招工单");
            linlayEdit.setVisibility(View.VISIBLE);
            linlayBtn.setVisibility(View.GONE);
        } else {
            noid = null;
            mActionBar.setTitle("新建招工单");
        }
        if (TextUtils.equals(getIntent().getStringExtra("flag"), "keep")) {
            linlayBtn.setVisibility(View.GONE);
            linlaySure.setVisibility(View.VISIBLE);
            fBtnKeep.setText("保存并发送给当前劳方");
        }
//
        MoneyUtils.setPrice(editPrice);
        MoneyUtils.setPrice(editTotal);
        //每次新建招工单都新建
        WorkOrder instance = WorkOrder.getInstance();
        instance.clear();
        noid = application.getUserInfo().get("noid");
        imgGridAdapter = new ImgGridAdapter();
        imgs.setAdapter(imgGridAdapter);
        weekGridAdapter = new WeekGridAdapter();
        week.setAdapter(weekGridAdapter);
        linlayWeek.setVisibility(View.GONE);
        if (ListUtils.isEmpty(selectImagePath)) {
            selectImagePath.add("plus");
        }
        imgs.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == ListUtils.getSize(selectImagePath) - 1 && position < 6) {
                    requestPermissions(Constants.PERMISSIONS_WRITE_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE);
                } else {
                    Bundle bundle = new Bundle();
                    bundle.putString("photo", selectImagePath.get(position + 1));
                    startActivity(PhotoZoomAty.class, bundle);
                    overridePendingTransition(0, 0);
                }
            }
        });
        week.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                weekGridAdapter.setIsWeek(position);
            }
        });


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
        return R.layout.aty_new_job_order;
    }

    @Override
    protected void initialized() {
        hire = new Hire();
        sys = new Sys();
        contract = new Contract();
    }

    @Override
    protected void requestData() {
        hire.getFormData(this);
        sys.getSkillList("0", "0", this);
        noid = application.getUserInfo().get("noid");
        if (TextUtils.equals(getIntent().getStringExtra("flag"), "edit")) {
            showProgressContent();
            hire_id = getIntent().getStringExtra("hire_id");
            hire.single(noid, hire_id, this);
        } else if (TextUtils.equals(getIntent().getStringExtra("flag"), "copy")) {
            showProgressContent();
            hire_id = getIntent().getStringExtra("hire_id");
            hire.single(noid, hire_id, this);
        } else if (TextUtils.equals(getIntent().getStringExtra("flag"), "keep")) {
            showProgressDialog();
            contract.getContractData(noid, getIntent().getStringExtra("contract_noid"), this);
        }
    }

    @Override
    public void onComplete(RequestParams params, String result) {
        if (params.getUri().contains("Hire/getFormData")) {
            Map<String, String> map = JSONUtils.parseDataToMap(result);
            weekList = JSONUtils.parseKeyAndValueToMapList(map.get("week"));
            settle = JSONUtils.parseKeyAndValueToMapList(map.get("settle"));
            settle_type = settle.get(0).get("sid");
            tvPay.setText(settle.get(0).get("name"));
            unit = map.get("unit_default_id");
            tvUnit.setText(map.get("unit_default_name"));
        } else if (params.getUri().contains("Sys/getSkillList")) {
            skillItemData = JSONUtils.parseDataToMapList(result);
        } else if (params.getUri().contains("Sys/getUnitList")) {
            unitList = JSONUtils.parseDataToMapList(result);

        } else if (params.getUri().contains("Hire/save")) {
            if (TextUtils.equals(flag, "release")) {
                String data = JSONUtils.parseKeyAndValueToMap(result).get("data");
                Bundle bundle = new Bundle();
                bundle.putString("hire_id", data);
                startActivity(ReleaseJOAty.class, bundle);
                finish();
            } else if (TextUtils.equals(flag, "keep")) {
                ArrayList<String> stringArrayList = new ArrayList<>();
                stringArrayList.add(getIntent().getStringExtra("lab_noid"));
                hire.publishByCustom(noid, JSONUtils.parseKeyAndValueToMap(result).get("data"), stringArrayList, this);
            } else {
                showToast(JSONUtils.parseKeyAndValueToMap(result).get("message"));
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        finish();
                    }
                }, 1500);
            }
        } else if (params.getUri().contains("Hire/publishByCustom")) {
            showToast(JSONUtils.parseKeyAndValueToMap(result).get("message"));
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    finish();
                }
            }, 1500);
        } else if (params.getUri().contains("Hire/single")) {
            if (TextUtils.equals(getIntent().getStringExtra("flag"), "copy")) {
                hire_id = "";
                flag = "copy";
            }
            final Map<String, String> map = JSONUtils.parseDataToMap(result);
            selectImagePath = new ArrayList<>();
            if (ListUtils.isEmpty(selectImagePath)) {
                selectImagePath.add("plus");
            }
            selectImagePath.addAll(JsonArryToList.strList(map.get("photos")));
            imgGridAdapter.notifyDataSetChanged();
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
            staff = map.get("staff");
            tvPay.setText(map.get("settle_type_name"));
            settle_type = map.get("settle_type");
            editPrice.setText(map.get("subtotal"));
            editTotal.setText(map.get("amount"));
            ArrayList<Map<String, String>> unitMap = JSONUtils.parseKeyAndValueToMapList(map.get("unit"));
            tvUnit.setText(unitMap.get(0).get("name"));
            unit = unitMap.get(0).get("sid");
            if (!TextUtils.equals(getIntent().getStringExtra("flag"), "copy")) {
                tvStartDay.setText(map.get("contract_starttime"));
                contract_starttime = map.get("contract_starttime");
                String[] contract_start = contract_starttime.split("-");
                startYear = contract_start[0];
                startY = Integer.parseInt(startYear);
                startMonth = contract_start[1];
                startM = Integer.parseInt(startMonth);
                startDay = contract_start[2];
                startD = Integer.parseInt(startDay);
                tvEndDay.setText(map.get("contract_endtime"));
                contract_endtime = map.get("contract_endtime");
                String[] contract_end = contract_endtime.split("-");
                String con_end = contract_end[0];
                endY = Integer.parseInt(con_end);
                endMonth = contract_end[1];
                endDay = contract_end[2];
                endD = Integer.parseInt(endDay);
                tvEndTime.setText(map.get("work_endtime"));
                work_endtime = map.get("work_endtime");
                String[] work_end = work_endtime.split(":");
                endHour = work_end[0];
                endMinutes = work_end[1];
                tvReTime.setText(map.get("hire_endtime"));
                hire_endtime = map.get("hire_endtime");
                tvStartTime.setText(map.get("work_starttime"));
                work_starttime = map.get("work_starttime");
                String[] work_start = work_starttime.split(":");
                startHour = work_start[0];
                startH = Integer.parseInt(startHour);
                startMinutes = work_start[1];
            }

            ArrayList<Map<String, String>> week_name = JSONUtils.parseKeyAndValueToMapList(map.get("week"));
            StringBuffer stringBuffer = new StringBuffer();
            for (int j = 0; j < ListUtils.getSize(week_name); j++) {
                weekID.add(week_name.get(j).get("sid"));
                weekGridAdapter.isWeek[Integer.parseInt(week_name.get(j).get("sid")) - 1] = true;
                weekGridAdapter.notifyDataSetChanged();
                stringBuffer.append(week_name.get(j).get("name") + ",");

            }
            if (stringBuffer.length() > 0) {
                stringBuffer.deleteCharAt(stringBuffer.length() - 1);
//                    showToast("您选择了" + stringBuffer);
                tvWeek.setText(stringBuffer);
            }
            tvAddress.setText(map.get("ress"));
            province_name = map.get("province_name");
            city_name = map.get("city_name");
            area_id = map.get("area_name");
            ress = map.get("ress");
            longitude = map.get("longitude");
            latitude = map.get("latitude");
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
        } else if (params.getUri().contains("Contract/getContractData")) {
            Map<String, String> map = JSONUtils.parseKeyAndValueToMap(JSONUtils.parseDataToMap(result).get("contract"));
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
            staff = map.get("staff");
            tvPay.setText(map.get("settle_type_name"));
            settle_type = map.get("settle_type");
            editPrice.setText(map.get("subtotal"));
            editTotal.setText(map.get("amount"));
            if (!TextUtils.isEmpty(map.get("work_endtime"))) {
                tvEndTime.setText(map.get("work_endtime"));
                work_endtime = map.get("work_endtime");
                String[] work_end = work_endtime.split(":");
                endHour = work_end[0];
                endMinutes = work_end[1];
            }
            if (!TextUtils.isEmpty(map.get("work_starttime"))) {
                tvStartTime.setText(map.get("work_starttime"));
                work_starttime = map.get("work_starttime");
                String[] work_start = work_starttime.split(":");
                startHour = work_start[0];
                startH = Integer.parseInt(startHour);
                startMinutes = work_start[1];
            }


//            ArrayList<Map<String, String>> unitMap = JSONUtils.parseKeyAndValueToMapList(map.get("unit"));
            tvUnit.setText(map.get("unit_name"));
            unit = map.get("unit");
            ArrayList<Map<String, String>> week_name = JSONUtils.parseKeyAndValueToMapList(map.get("work_week"));
            StringBuffer stringBuffer = new StringBuffer();
            for (int j = 0; j < ListUtils.getSize(week_name); j++) {
                weekID.add(week_name.get(j).get("sid"));
                weekGridAdapter.isWeek[Integer.parseInt(week_name.get(j).get("sid")) - 1] = true;
                weekGridAdapter.notifyDataSetChanged();
                stringBuffer.append(week_name.get(j).get("name") + ",");

            }
            if (stringBuffer.length() > 0) {
                stringBuffer.deleteCharAt(stringBuffer.length() - 1);
//                    showToast("您选择了" + stringBuffer);
                tvWeek.setText(stringBuffer);
            }
            tvAddress.setText(map.get("ress"));
            province_name = map.get("province_name");
            city_name = map.get("city_name");
            area_id = map.get("area_name");
            ress = map.get("ress");
            longitude = map.get("longitude");
            latitude = map.get("latitude");
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
        }
        super.onComplete(params, result);
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

    @Event({R.id.new_job_order_unit_click, R.id.new_jo_release, R.id.new_jo_addresss_click, R.id.new_jo_work_click
            , R.id.new_jo_week_click, R.id.new_jo_end_retime_click, R.id.start_day_click, R.id.end_day_click
            , R.id.start_time_click, R.id.end_time_click, R.id.new_jo_werk_sure, R.id.new_jo_pay_click
            , R.id.new_jo_save, R.id.new_jo_edit_save, R.id.new_jo_f_btn_keep})
    private void onClick(View view) {
        amount = editTotal.getText().toString().trim();
        audit = editStandard.getText().toString().trim();
        if (ListUtils.isEmpty(skillList)) skill_id = "";
        else skill_id = ListUtils.join(skillList);
        LogUtil.d(skill_id);
        staff = editPerson.getText().toString();
        contract_starttime = startY + "-" + startMonth + "-" + startDay;
        contract_endtime = endY + "-" + endMonth + "-" + endDay;
        work_starttime = startHour + ":" + startMinutes;
        work_endtime = endHour + ":" + endMinutes;
        if (!ListUtils.isEmpty(weekID))
            work_week = ListUtils.join(weekID);
        subtotal = editPrice.getText().toString();
        others_text = editOther.getText().toString();
        if (ListUtils.getSize(selectImagePath) >= 2)
            photos = selectImagePath.get(1);
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
        switch (view.getId()) {
            case R.id.new_jo_save:
                if (TextUtils.isEmpty(noid)) {
                    LogUtil.e("请检查noid");
                    return;
                } else if (TextUtils.isEmpty(hire_endtime)) {
                    showToast("还未选择招工截止日期");
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
                    showToast("请选择工作开始日期");
                    return;
                } else if (endD == 0) {
                    showToast("请选择工作截止日期");
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
//                else if (ListUtils.getSize(selectImagePath) == 1) {
//                    showToast("请添加图片");
//                    return;
//                }
                showProgressDialog();
                flag = "save";
                hire.save(noid, hire_endtime, amount, audit, skill_id, staff, contract_starttime, contract_endtime
                        , work_starttime, work_endtime, work_week, subtotal, unit, settle_type, others_text, selectImagePath, is_insurance
                        , is_dine, is_lodging, is_tool, is_transportation_expenses, is_correspondence, hire_id, province_name
                        , city_name, area_id, ress, longitude, latitude, this);
                break;
            case R.id.new_job_order_unit_click:
                startActivityForResult(JobOrderUnitAty.class, null, UNIT_ID);
                break;
            //发布招工单
            case R.id.new_jo_release:
                if (TextUtils.isEmpty(noid)) {
                    LogUtil.e("请检查noid");
                    return;
                } else if (TextUtils.isEmpty(hire_endtime)) {
                    showToast("还未选择招工截止日期");
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
//                else if (ListUtils.getSize(selectImagePath) == 1) {
//                    showToast("请添加图片");
//                    return;
//                }
                showProgressDialog();
                flag = "release";
                hire.save(noid, hire_endtime, amount, audit, skill_id, staff, contract_starttime, contract_endtime
                        , work_starttime, work_endtime, work_week, subtotal, unit, settle_type, others_text, selectImagePath, is_insurance
                        , is_dine, is_lodging, is_tool, is_transportation_expenses, is_correspondence, hire_id, province_name
                        , city_name, area_id, ress, longitude, latitude, this);

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
                onYearMonthDayPicker(1999, 2117, new DatePicker.OnYearMonthDayPickListener() {
                    @Override
                    public void onDatePicked(String year, String month, String day) {
                        startY = Integer.parseInt(year);
                        startM = Integer.parseInt(month);
                        startD = Integer.parseInt(day);
                        startMonth = month;
                        startDay = day;
                        tvStartDay.setText(year + "-" + month + "-" + day);
                    }
                });
                break;
            case R.id.end_day_click:
                if (startY == 0) {
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
                datePicker.setSelectedItem(startY, startM, startD);
                datePicker.show();
                break;
            case R.id.start_time_click:
                TimePicker timePicker1 = onTimePicker(0, 23, true, new TimePicker.OnTimePickListener() {


                    @Override
                    public void onTimePicked(String hour, String minute) {
                        startH = Integer.parseInt(hour);
                        startHour = hour;
                        startMinute = Integer.parseInt(minute);
                        startMinutes = minute;
                        tvStartTime.setText(hour + ":" + minute);
                    }
                });
                timePicker1.setSelectedItem(9, 0);
                timePicker1.show();
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
                timePicker.setSelectedItem(18, 0);
                timePicker.show();
                break;
            case R.id.new_jo_end_retime_click:
                if (startY == 0) {
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
            case R.id.new_jo_werk_sure:
                StringBuffer stringBuffer = new StringBuffer();
                weekID.clear();
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
                        switch (which) {
                            case 0:
                                tvPay.setText(charSequences[0]);
                                settle_type = settle.get(0).get("sid");
                                break;
                        }
                    }
                });
                break;
            case R.id.new_jo_edit_save:
                setSave("edit");
                break;
            case R.id.new_jo_f_btn_keep:
                setSave("keep");
                break;
        }
    }

    private void setSave(String string) {
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
//        else if (TextUtils.isEmpty(audit)) {
//            showToast("请输入工作标准");
//            return;
//        }
        else if (TextUtils.isEmpty(skill_id)) {
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
//        else if (ListUtils.getSize(selectImagePath) == 1) {
//            showToast("请添加图片");
//            return;
//        }
        showProgressDialog();
        flag = string;
        hire.save(noid, hire_endtime, amount, audit, skill_id, staff, contract_starttime, contract_endtime
                , work_starttime, work_endtime, work_week, subtotal, unit, settle_type, others_text, selectImagePath, is_insurance
                , is_dine, is_lodging, is_tool, is_transportation_expenses, is_correspondence, hire_id, province_name
                , city_name, area_id, ress, longitude, latitude, this);

    }

    int anInt = 7;

    @PermissionSuccess(requestCode = Constants.PERMISSIONS_WRITE_EXTERNAL_STORAGE)
    public void requestSuccess() {
        if (ListUtils.getSize(selectImagePath) < 7) {
            startSelectMultiImageAty(stringArrayList, anInt - ListUtils.getSize(selectImagePath));
        }
    }

    @PermissionFail(requestCode = Constants.PERMISSIONS_WRITE_EXTERNAL_STORAGE)
    public void requestFail() {
        showToast("获取权限失败，请在设置中开启");
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            return;
        }
        switch (requestCode) {
            case com.toocms.frame.config.Constants.SELECT_IMAGE:
                for (int i = ListUtils.getSize(getSelectImagePath(data)); i > 0; i--) {
                    selectImagePath.add(getSelectImagePath(data).get(i - 1));
                }
                imgGridAdapter.notifyDataSetChanged();
                break;
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

    private class ImgGridAdapter extends BaseAdapter {

        private ViewHodler viewHodler;
        private ImageLoader imageLoader;

        public ImgGridAdapter() {
            imageLoader = new ImageLoader();
            ImageOptions options = new ImageOptions.Builder().setLoadingDrawableId(R.drawable.img_index).setUseMemCache(true).build();
            imageLoader.setImageOptions(options);
        }

        @Override
        public int getCount() {
            return ListUtils.getSize(selectImagePath);
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                viewHodler = new ViewHodler();
                convertView = LayoutInflater.from(NewJobOrderAty.this).inflate(R.layout.listitem_new_job_order, parent, false);
                x.view().inject(viewHodler, convertView);
                AutoUtils.autoSize(convertView);
                convertView.setTag(viewHodler);
            } else {
                viewHodler = (ViewHodler) convertView.getTag();
            }

            if (position == ListUtils.getSize(selectImagePath) - 1 && position < 7) {
                viewHodler.imgv.setScaleType(ImageView.ScaleType.FIT_XY);
                viewHodler.imgv.setImageResource(R.drawable.btn_add);
                viewHodler.imgvDel.setVisibility(View.GONE);
                if (position == 6) {
                    viewHodler.imgv.setVisibility(View.GONE);
                }
            } else {
                if (selectImagePath.get(position + 1).contains("http")) {
                    imageLoader.loadFile(selectImagePath.get(position + 1), new Callback.CacheCallback<File>() {
                        @Override
                        public boolean onCache(RequestParams requestParams, File file) {
                            if (position < ListUtils.getSize(selectImagePath) - 1) {
                                try {
                                    String path = file.getPath();
                                    String name = file.getName();
                                    String replace = file.getPath().replace(name, "");
                                    File tempFile = File.createTempFile(file.getName(), ".png", new File(replace));
//                                    new File()

                                    Bitmap bmp = BitmapFactory.decodeFile(file.getPath());
                                    FileOutputStream fileOutStream = null;
                                    fileOutStream = new FileOutputStream(tempFile);
                                    bmp.compress(Bitmap.CompressFormat.JPEG, 100, fileOutStream);  //把位图输出到指定的文件中
                                    fileOutStream.flush();
                                    fileOutStream.close();
                                    selectImagePath.set(position + 1, tempFile.getAbsolutePath());
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }

                            return false;
                        }

                        @Override
                        public void onSuccess(RequestParams requestParams, File file) {

                        }

                        @Override
                        public void onError(Throwable throwable, boolean b) {

                        }

                        @Override
                        public void onCancelled(CancelledException e) {

                        }

                        @Override
                        public void onFinished() {

                        }
                    });
                }
                imageLoader.disPlay(viewHodler.imgv, selectImagePath.get(position + 1));
                viewHodler.imgvDel.setVisibility(View.VISIBLE);
            }
            viewHodler.imgvDel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectImagePath.remove((position + 1));
                    notifyDataSetChanged();
                }
            });
            return convertView;
        }


        public class ViewHodler {
            @ViewInject(R.id.list_new_job_imgs)
            ImageView imgv;
            @ViewInject(R.id.list_new_job_delete)
            ImageView imgvDel;

        }
    }

    private class WeekGridAdapter extends BaseAdapter {

        private ViewHolder viewHolder;
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
                viewHolder = new ViewHolder();
                convertView = LayoutInflater.from(NewJobOrderAty.this).inflate(R.layout.listitem_njo_week, parent, false);
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
