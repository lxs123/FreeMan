package com.toocms.freeman.ui.mine.baseinformation;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.toocms.frame.image.ImageLoader;
import com.toocms.frame.ui.BaseFragment;
import com.toocms.freeman.R;
import com.toocms.freeman.config.Constants;
import com.toocms.freeman.config.JsonArryToList;
import com.toocms.freeman.https.Hire;
import com.toocms.freeman.https.Sys;
import com.toocms.freeman.https.User;
import com.toocms.freeman.ui.BaseAty;
import com.toocms.freeman.ui.FreeManToggle;
import com.toocms.freeman.ui.index.SkillAty;
import com.toocms.freeman.ui.recruitment.joborder.JobOrderUnitAty;
import com.toocms.freeman.ui.util.MoneyUtils;
import com.toocms.freeman.ui.util.WorkOrder;
import com.toocms.freeman.ui.view.MyGridView;
import com.toocms.freeman.ui.view.MyImageDialog;
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
import java.io.InputStream;
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

import static android.app.Activity.RESULT_OK;

/**
 * 个人中心 的 技能信息 修改 页面
 */
public class SkillInformationModify extends BaseFragment {

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
    @ViewInject(R.id.new_jo_price)
    private EditText editPrice;
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
     * @param territory                  工作地域id，可数组或 字符串 1,2,3
     * @param capability                 技能图片数组
     */
    private String noid;
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
    private String is_insurance;
    private String is_dine;
    private String is_lodging;
    private String is_tool;
    private String is_transportation_expenses;
    private String is_correspondence;
    private String territory;
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
    private BaseAty activity;
    private ArrayList<Map<String, String>> sel_area;
    private User user;
    private List<String> areaIDList = new ArrayList<>();
    private String[] paths;
    private String localPath;
    private String startYear;
    private List<String> capability;

    @Override
    protected int getLayoutResId() {
        return R.layout.fgt_skill_information_modify;
    }

    @Override
    protected void initialized() {
        hire = new Hire();
        sys = new Sys();
        user = new User();
    }

    @Override
    protected void requestData() {
        showProgressContent();
        hire.getFormData(this);
        sys.getSkillList("0", "0", this);
        noid = application.getUserInfo().get("noid");

        user.getAttrstation(noid, this);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        activity = (BaseAty) getActivity();
        noid = application.getUserInfo().get("noid");

        MoneyUtils.setPrice(editPrice);
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
//                    Bundle bundle = new Bundle();
//                    bundle.putString("photo", selectImagePath.get(position + 1));
//                    startActivity(PhotoZoomAty.class, bundle);
//                    setEnterTransition(TRANSIT_FRAGMENT_OPEN);
                    MyImageDialog myImageDialog = new MyImageDialog(getActivity(), R.style.Dialog_Fullscreen, 0, 0, true, selectImagePath.get(position + 1));
                    myImageDialog.show();
                    WindowManager windowManager = getActivity().getWindowManager();
                    Display display = windowManager.getDefaultDisplay();
                    WindowManager.LayoutParams lp = myImageDialog.getWindow().getAttributes();
                    lp.width = (int) (display.getWidth()); //设置宽度
                    lp.height = display.getHeight();
                    myImageDialog.getWindow().setAttributes(lp);
//                    overridePendingTransition(0, 0);
                }
            }
        });
        week.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                weekGridAdapter.setIsWeek(position);
            }
        });
        noid = application.getUserInfo().get("noid");
    }


    @Override
    public void onResume() {
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
    public void onComplete(RequestParams params, final String result) {
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
        } else if (params.getUri().contains("User/saveAttestation")) {
            showToast(JSONUtils.parseKeyAndValueToMap(result).get("message"));
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(500);

//                        SkillInformationInBaseAty skillInfo = (SkillInformationInBaseAty) getActivity();
//                        skillInfo.replace("0");
                        FragmentManager manager = getFragmentManager();
                        FragmentTransaction ft = manager.beginTransaction();
                        ft.replace(R.id.base_infomation_ll, new SkillInfotmationComplete(), "0");
//                        ft.addToBackStack(null);
                        ft.commit();

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        } else if (params.getUri().contains("User/getAttrstation")) {
            final Map<String, String> map = JSONUtils.parseDataToMap(result);
            if (ListUtils.isEmpty(selectImagePath)) selectImagePath.add("plus");
//            selectImagePath = (ArrayList<String>) JsonArryToList.strList(map.get("capability"));
//            String path = Environment.getExternalStorageDirectory().getPath() + "/FreeManDownload/";
            capability = JsonArryToList.strList(map.get("capability"));
//            for (int i = 0; i < ListUtils.getSize(JsonArryToList.strList(map.get("capability"))); i++) {
//                selectImagePath.add(capability.get(i));
//            }

//            selPhoto = new ArrayList<String>();
//            if (ListUtils.isEmpty(selPhoto)) selPhoto.add("plus");
//            selPhoto.set(0, "123");
//            LogUtil.e(selPhoto.toString());
            selectImagePath.addAll(JsonArryToList.strList(map.get("capability")));
            if (imgGridAdapter == null) {
                imgGridAdapter = new ImgGridAdapter();
                imgs.setAdapter(imgGridAdapter);
            } else {
                imgGridAdapter.notifyDataSetChanged();
            }

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
            editOther.setText(map.get("others_text"));
            if (!TextUtils.isEmpty(map.get("settle_type"))) {
                tvPay.setText(map.get("settle_type_name"));
                settle_type = map.get("settle_type");
            }
            editPerson.setText(map.get("staff"));
            editPrice.setText(map.get("subtotal"));
            if (TextUtils.equals(editPrice.getText().toString(), "0") || TextUtils.equals(editPrice.getText().toString(), "0.00")) {
                editPrice.setText("");
            }
            if (!TextUtils.isEmpty(map.get("unit"))) {
                tvUnit.setText(map.get("unit_name"));
                unit = map.get("unit");
            }


            contract_starttime = map.get("work_startdate");
            if (!TextUtils.isEmpty(contract_starttime)) {
                tvStartDay.setText(map.get("work_startdate"));
                String[] contract_start = contract_starttime.split("-");
                startYear = contract_start[0];
                startY = Integer.parseInt(startYear);
                startMonth = contract_start[1];
                startM = Integer.parseInt(startMonth);
                startDay = contract_start[2];
                startD = Integer.parseInt(startDay);

            }
            contract_endtime = map.get("work_enddate");
            if (!TextUtils.isEmpty(contract_endtime)) {
                tvEndDay.setText(map.get("work_enddate"));
                String[] contract_end = contract_endtime.split("-");
                String con_end = contract_end[0];
                endY = Integer.parseInt(con_end);
                endMonth = contract_end[1];
                endDay = contract_end[2];
            }
            work_starttime = map.get("work_starttime");
            if (!TextUtils.isEmpty(work_starttime)) {
                tvStartTime.setText(map.get("work_starttime"));
                String[] work_start = work_starttime.split(":");
                startHour = work_start[0];
                startMinutes = work_start[1];
            }
            work_endtime = map.get("work_endtime");
            if (!TextUtils.isEmpty(work_endtime)) {
                tvEndTime.setText(map.get("work_endtime"));
                String[] work_end = work_endtime.split(":");
                endHour = work_end[0];
                endMinutes = work_end[1];
            }
            WorkOrder.getInstance().clear();
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
            ArrayList<Map<String, String>> week_name = JSONUtils.parseKeyAndValueToMapList(map.get("week_name"));
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
            sel_area = JSONUtils.parseKeyAndValueToMapList(map.get("territory"));
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < ListUtils.getSize(sel_area); i++) {
                areaIDList.add(sel_area.get(i).get("territory_id"));
                if (!TextUtils.isEmpty(sel_area.get(i).get("city_name")) &&
                        !TextUtils.isEmpty(sel_area.get(i).get("area_name")))
                    builder.append(sel_area.get(i).get("province_name") + "," + sel_area.get(i).get("city_name")
                            + "," + sel_area.get(i).get("area_name"));
                else if (TextUtils.isEmpty(sel_area.get(i).get("city_name")))
                    builder.append(sel_area.get(i).get("province_name") + "全境");
                else if (TextUtils.isEmpty(sel_area.get(i).get("area_name")))
                    builder.append(sel_area.get(i).get("province_name") + "," + sel_area.get(i).get("city_name")
                            + "全境");
                if (i != ListUtils.getSize(sel_area) - 1) {
                    builder.append("\n");
                }
            }
            tvAddress.setText(builder.toString());
            territory = ListUtils.join(areaIDList);
        }
        super.onComplete(params, result);
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu_complete, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_complete:
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm.isActive() && getActivity().getCurrentFocus() != null) {
                    if (getActivity().getCurrentFocus().getWindowToken() != null) {
                        imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                    }
                }
                skill_id = ListUtils.join(skillList);
                staff = editPerson.getText().toString();
                if (!TextUtils.isEmpty(startMonth))
                    contract_starttime = startY + "-" + startMonth + "-" + startDay;
                if (!TextUtils.isEmpty(endMonth))
                    contract_endtime = endY + "-" + endMonth + "-" + endDay;
                if (!TextUtils.isEmpty(startHour))
                    work_starttime = startHour + ":" + startMinutes;
                if (!TextUtils.isEmpty(endHour))
                    work_endtime = endHour + ":" + endMinutes;
                if (!ListUtils.isEmpty(weekID))
                    work_week = ListUtils.join(weekID);
                subtotal = editPrice.getText().toString();
                others_text = editOther.getText().toString();
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
//                if (!ListUtils.isEmpty(selPhoto)) {
//                    selectImagePath = new ArrayList<>();
//                    selectImagePath = selPhoto;
//                    selPhoto = new ArrayList<>();
//                }

//                if (ListUtils.getSize(selectImagePath) - 1 < ListUtils.getSize(capability)) {
//                    new Handler().postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//                        }
//                    }, 3000);
////                }
//                LogUtil.e(selectImagePath.size() + "///////////////////////////");
//
//                if (ListUtils.getSize(selectImagePath) < 2) {
//                    showToast("请选择技能展示照片");
//                    break;
//                } else
                if (TextUtils.isEmpty(skill_id)) {
                    showToast("请选择技能");
                    break;
                } else if (TextUtils.isEmpty(staff)) {
                    showToast("请填写可提供人员数量");
                    break;
                } else if (TextUtils.isEmpty(contract_starttime)) {
                    showToast("请选择工作开始时间");
                    break;
                } else if (TextUtils.isEmpty(contract_endtime)) {
                    showToast("请选择工作截止时间");
                    break;
                } else if (TextUtils.isEmpty(work_starttime)) {
                    showToast("请选择每天工作开始时间");
                    break;
                } else if (TextUtils.isEmpty(work_endtime)) {
                    showToast("请选择每天工作截止时间");
                    break;
                } else if (TextUtils.isEmpty(work_week)) {
                    showToast("请选择工作周历");
                    break;
                } else if (TextUtils.isEmpty(subtotal)) {
                    showToast("请选择单价");
                    break;
                } else if (TextUtils.isEmpty(unit)) {
                    showToast("请选择单位");
                    break;
                } else if (TextUtils.isEmpty(settle_type)) {
                    showToast("请选择结算方式");
                    break;
                } else if (TextUtils.isEmpty(territory)) {
                    showToast("请选择工作地域");
                    break;
                }
                showProgressDialog();

                user.saveAttestation(noid, skill_id, staff, contract_starttime, contract_endtime, work_starttime, work_endtime, work_week, subtotal, unit, settle_type, is_insurance, is_dine,
                        is_lodging, is_tool, is_transportation_expenses, is_correspondence, others_text, selectImagePath, territory, this);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Event({R.id.new_job_order_unit_click, R.id.new_jo_addresss_click, R.id.new_jo_work_click
            , R.id.new_jo_week_click, R.id.new_jo_end_retime_click, R.id.start_day_click, R.id.end_day_click
            , R.id.start_time_click, R.id.end_time_click, R.id.new_jo_werk_sure, R.id.new_jo_pay_click
    })
    private void onClick(View view) {
        switch (view.getId()) {
            case R.id.new_job_order_unit_click:
                Intent intent = new Intent(getActivity(), JobOrderUnitAty.class);
                startActivityForResult(intent, UNIT_ID);
                break;
            case R.id.new_jo_addresss_click:
                Intent intent1 = new Intent(getActivity(), WorkAreaAty.class);
                Bundle bundle = new Bundle();
                bundle.putString("flag", "sel_area");
                if (!ListUtils.isEmpty(sel_area))
                    bundle.putSerializable("sel_area", sel_area);
                intent1.putExtras(bundle);
                startActivityForResult(intent1, ADDRESS_ID);
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

                activity.onYearMonthDayPicker(1999, 2117, new DatePicker.OnYearMonthDayPickListener() {
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
                break;
            case R.id.end_day_click:
                if (TextUtils.isEmpty(startYear)) {
                    showToast("请先选择合同开始日期");
                    return;
                }
                DatePicker datePicker = activity.onYearMonthDayPicker(startY, 2117, true, new DatePicker.OnYearMonthDayPickListener() {
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
                TimePicker timePicker1 = activity.onTimePicker(0, 23, true, new TimePicker.OnTimePickListener() {


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
                TimePicker timePicker = activity.onTimePicker(startH, 23, true, new TimePicker.OnTimePickListener() {
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
                        settle_type = settle.get(which).get("sid");
//                                break;
//                        }
                    }
                });
                break;
        }
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            return;
        }
        switch (requestCode) {
            case com.toocms.frame.config.Constants.SELECT_IMAGE:
//                if (!ListUtils.isEmpty(selPhoto)) {
//                    selectImagePath = new ArrayList<>();
//                    selectImagePath = selPhoto;
//                    selPhoto = new ArrayList<>();
//                }
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
                sel_area = (ArrayList<Map<String, String>>) data.getSerializableExtra("sel_area");

                StringBuilder builder = new StringBuilder();
                areaIDList = new ArrayList<>();
                for (int i = 0; i < ListUtils.getSize(sel_area); i++) {
                    areaIDList.add(sel_area.get(i).get("territory_id"));
                    if (!TextUtils.isEmpty(sel_area.get(i).get("city_name")) &&
                            !TextUtils.isEmpty(sel_area.get(i).get("area_name")))
                        builder.append(sel_area.get(i).get("province_name") + "," + sel_area.get(i).get("city_name")
                                + "," + sel_area.get(i).get("area_name"));
                    else if (TextUtils.isEmpty(sel_area.get(i).get("city_name")))
                        builder.append(sel_area.get(i).get("province_name") + "全境");
                    else if (TextUtils.isEmpty(sel_area.get(i).get("area_name")))
                        builder.append(sel_area.get(i).get("province_name") + "," + sel_area.get(i).get("city_name")
                                + "全境");
                    if (i != ListUtils.getSize(sel_area) - 1) {
                        builder.append("\n");
                    }
                }
                tvAddress.setText(builder.toString());
                territory = ListUtils.join(areaIDList);
                LogUtil.e(territory);
                break;
        }
    }

    public static void readAsFile(InputStream inSream, File file) throws Exception {
        FileOutputStream outStream = new FileOutputStream(file);
        byte[] buffer = new byte[1024];
        int len = -1;
        while ((len = inSream.read(buffer)) != -1) {
            outStream.write(buffer, 0, len);
        }
//        Bitmap bitmap = BitmapFactory.decodeFile(file.getPath());
//        bitmap.compress(Bitmap.CompressFormat., 100, outStream);
        outStream.close();
        inSream.close();
    }

    private class ImgGridAdapter extends BaseAdapter {

        private ViewHodler viewHodler;

        public ImgGridAdapter() {
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
                convertView = LayoutInflater.from(getActivity()).inflate(R.layout.listitem_new_job_order, parent, false);
                x.view().inject(viewHodler, convertView);
                AutoUtils.autoSize(convertView);
                convertView.setTag(viewHodler);
            } else {
                viewHodler = (ViewHodler) convertView.getTag();
            }
            final ImageLoader imageLoader = new ImageLoader();
            ImageOptions imageOptions = new ImageOptions.Builder().setUseMemCache(true).build();
            imageLoader.setImageOptions(imageOptions);
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
                                    selectImagePath.set(position + 1, tempFile.getAbsolutePath());
                                    LogUtil.e(tempFile.getAbsolutePath());
                                    Bitmap bmp = BitmapFactory.decodeFile(file.getPath());
                                    FileOutputStream fileOutStream = null;
                                    fileOutStream = new FileOutputStream(tempFile);
                                    bmp.compress(Bitmap.CompressFormat.JPEG, 100, fileOutStream);  //把位图输出到指定的文件中
                                    fileOutStream.flush();
                                    fileOutStream.close();
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
                convertView = LayoutInflater.from(getActivity()).inflate(R.layout.listitem_njo_week, parent, false);
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
