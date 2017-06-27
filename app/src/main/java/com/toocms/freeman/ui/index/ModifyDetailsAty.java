package com.toocms.freeman.ui.index;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.TabLayout;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.toocms.frame.tool.AppManager;
import com.toocms.freeman.R;
import com.toocms.freeman.config.JsonArryToList;
import com.toocms.freeman.https.Contract;
import com.toocms.freeman.https.Hire;
import com.toocms.freeman.ui.BaseAty;
import com.toocms.freeman.ui.util.DateUtils;

import org.xutils.http.RequestParams;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.zero.android.common.util.JSONUtils;
import cn.zero.android.common.util.ListUtils;
import cn.zero.android.common.util.MapUtils;
import cn.zero.android.common.view.FButton;

/**
 * 查看反馈页--修改明细
 */
public class ModifyDetailsAty extends BaseAty implements TabLayout.OnTabSelectedListener {

    //我的修改要显示
    @ViewInject(R.id.prompt_tv)
    TextView prompt_tv;
    //对方修改要显示
    @ViewInject(R.id.other_modify_ll)
    LinearLayout otherModifyLl;
    //初始化tablayout
    @ViewInject(R.id.modify_tablayout)
    TabLayout modifyTablayout;
    @ViewInject(R.id.modify_det_scroll)
    ScrollView scrollView;
    @ViewInject(R.id.l_w_e_work)
    TextView tvWork;
    @ViewInject(R.id.l_w_e_work_spot)
    View vWorkSpot;
    @ViewInject(R.id.l_w_e_person)
    TextView tvPerson;
    @ViewInject(R.id.l_w_e_start_date)
    TextView tvStartDate;
    @ViewInject(R.id.l_w_e_start_date_spot)
    View vStartDateSpot;
    @ViewInject(R.id.l_w_e_end_date)
    TextView tvEndDate;
    @ViewInject(R.id.l_w_e_end_date_spot)
    View vEndDateSpot;
    @ViewInject(R.id.l_w_e_start_time)
    TextView tvStartTime;
    @ViewInject(R.id.l_w_e_start_time_spot)
    View vStartTimeSpot;
    @ViewInject(R.id.l_w_e_end_time)
    TextView tvEndTime;
    @ViewInject(R.id.l_w_e_end_time_spot)
    View vEndTimeSpot;
    @ViewInject(R.id.l_w_e_week)
    TextView tvWeek;
    @ViewInject(R.id.l_w_e_week_spot)
    View vWeekSpot;
    @ViewInject(R.id.l_w_e_price)
    TextView tvPrice;
    @ViewInject(R.id.l_w_e_price_spot)
    View vPriceSpot;
    @ViewInject(R.id.l_w_e_total)
    TextView tvTotal;
    @ViewInject(R.id.l_w_e_total_spot)
    View vTotalSpot;
    @ViewInject(R.id.l_w_e_payment)
    TextView tvPayment;
    @ViewInject(R.id.l_w_e_payment_spot)
    View vPaymentSpot;
    @ViewInject(R.id.l_w_e_address)
    TextView tvAddress;
    @ViewInject(R.id.l_w_e_address_spot)
    View vAddressSpot;
    @ViewInject(R.id.l_w_e_insurance)
    TextView tvInsurance;
    @ViewInject(R.id.l_w_e_insurance_spot)
    View vInsuranceSpot;
    @ViewInject(R.id.l_w_e_lunch)
    TextView tvLunch;
    @ViewInject(R.id.l_w_e_lunch_spot)
    View vLunchSpot;
    @ViewInject(R.id.l_w_e_live)
    TextView tvLive;
    @ViewInject(R.id.l_w_e_live_spot)
    View vLiveSpot;
    @ViewInject(R.id.l_w_e_utils)
    TextView tvUtils;
    @ViewInject(R.id.l_w_e_utils_spot)
    View vUtilsSpot;
    @ViewInject(R.id.l_w_e_transportation)
    TextView tvTransportation;
    @ViewInject(R.id.l_w_e_transportation_spot)
    View vTransportationSpot;
    @ViewInject(R.id.l_w_e_communications)
    TextView tvCommunications;
    @ViewInject(R.id.l_w_e_communications_spot)
    View vCommunicationsSpot;
    @ViewInject(R.id.l_w_e_audit)
    TextView tvAudit;
    @ViewInject(R.id.l_w_e_other)
    TextView tvOther;
    @ViewInject(R.id.look_work_edit_content)
    ScrollView svEditContent;
    @ViewInject(R.id.l_w_e_spot_title)
    LinearLayout linlaySpot;
    @ViewInject(R.id.l_w_e_blue_title)
    TextView linlayBlue;
    @ViewInject(R.id.l_w_e_address_type)
    TextView linlayAddrTye;
    @ViewInject(R.id.l_w_e_address_click)
    LinearLayout linlayAddr;
    @ViewInject(R.id.l_w_e_empty)
    LinearLayout linlayEmpty;
    @ViewInject(R.id.l_w_e_total_text)
    TextView tvTotalText;
    @ViewInject(R.id.l_w_e_deadline)
    TextView tvDeadline;
    @ViewInject(R.id.l_w_e_deadline_spot)
    View vDeadline;
    @ViewInject(R.id.l_w_e_old_contract)
    Button btnOldCont;
    @ViewInject(R.id.l_w_e_edit_contract)
    Button btnEdit;
    @ViewInject(R.id.l_w_e_agree_contract)
    FButton fBtnQianYue;

    /**
     * 获取修改明细[fullOpinion]
     *
     * @param hire_noid 招工单编号
     * @param lab_noid  劳方编号
     */

    private List<String> skill_name = new ArrayList<>();
    /**
     * 招工单反馈详情[detail]
     *
     * @param lab_noid  劳方noid
     * @param hire_noid 招工单noid
     */
    private String hire_noid;
    private String lab_noid;
    private Map<String, String> mapLab;
    private Map<String, String> mapCap;
    private String unit_name;
    private Map<String, String> mapIntersect;
    private Contract contract;
    private Hire hire;
    private Map<String, String> mapHire;
    @ViewInject(R.id.l_w_e_person_spot)
    private View vPerson;
    @ViewInject(R.id.l_w_e_person_lay)
    private LinearLayout linlayPerson;
    @ViewInject(R.id.l_w_e_person_divid)
    private View vPersonDivid;

    @Override
    protected int getLayoutResId() {
        return R.layout.aty_modify_details;
    }

    @Override
    protected void initialized() {
        hire = new Hire();
        contract = new Contract();
    }

    @Override
    protected void requestData() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActionBar.setTitle("修改明细");
        linlayPerson.setVisibility(View.GONE);
        vPersonDivid.setVisibility(View.GONE);
        prompt_tv.setVisibility(View.GONE);
        otherModifyLl.setVisibility(View.GONE);
        tvTotalText.setText("金额");
        modifyTablayout.setOnTabSelectedListener(this);
        btnOldCont.setVisibility(View.GONE);
        btnEdit.setText("修改");
        fBtnQianYue.setText("签约");

        linlayAddr.setVisibility(View.GONE);
        linlayAddrTye.setVisibility(View.GONE);
        linlaySpot.setVisibility(View.GONE);
        linlayBlue.setVisibility(View.GONE);
        lab_noid = getIntent().getStringExtra("lab_noid");
        hire_noid = getIntent().getStringExtra("hire_noid");

        //针对搜工作的修改
        if (getIntent().getStringExtra("flag") != null && getIntent().getStringExtra("flag").equals("wzw1")) {
            btnEdit.setText("取消接单");
            fBtnQianYue.setVisibility(View.GONE);
            Log.e("***", "wzw1修改明细：" + getIntent().getStringExtra("lab_noid") + "/" + getIntent().getStringExtra("hire_noid") + "/" + getIntent().getStringExtra("noid"));
        }
        showProgressContent();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (getIntent().getStringExtra("flag") != null && getIntent().getStringExtra("flag").equals("wzw1")) {
            hire.detail(getIntent().getStringExtra("lab_noid"), getIntent().getStringExtra("hire_noid"), this);
        } else
            hire.detail(lab_noid, hire_noid, this);
    }

    @Override
    public void onComplete(RequestParams params, String result) {
        if (params.getUri().contains("Hire/detail")) {
            Log.e("***", result);
            Map<String, String> map = JSONUtils.parseDataToMap(result);
            mapHire = JSONUtils.parseKeyAndValueToMap(map.get("hire"));
            map.get("noid");
            List<Map<String, String>> skill_list = JSONUtils.parseKeyAndValueToMapList(mapHire.get("skill_list"));
            skill_name = new ArrayList<>();
            for (int i = 0; i < ListUtils.getSize(skill_list); i++) {
                skill_name.add(skill_list.get(i).get("name"));
            }
            tvPerson.setText(mapHire.get("staff"));
//            skill_name = JsonArryToList.strList(mapHire.get("skill_name"));
            String contract_starttime = mapHire.get("contract_starttime");
            String contract_endtime = mapHire.get("contract_endtime");
            String work_starttime = mapHire.get("work_starttime");
            String work_endtime = mapHire.get("work_endtime");
            String hire_endtime = mapHire.get("hire_endtime");
            List<String> week_name = JsonArryToList.strList(mapHire.get("work_week_name"));
            String subtotal = mapHire.get("subtotal");
            String amount = mapHire.get("amount");
            String type_name = mapHire.get("settle_type_name");
            String is_insurance = mapHire.get("is_insurance");
            String is_dine = mapHire.get("is_dine");
            String is_lodging = mapHire.get("is_lodging");
            String is_tool = mapHire.get("is_tool");
            String is_transportation_expenses = mapHire.get("is_transportation_expenses");
            String is_correspondence = mapHire.get("is_correspondence");
            unit_name = mapHire.get("unit_name");
            String others_text = mapHire.get("others_text");
            String audit = mapHire.get("audit");
            tvWork.setText(ListUtils.join(skill_name));
            tvStartDate.setText(contract_starttime);
            tvEndDate.setText(contract_endtime);
            tvStartTime.setText(work_starttime);
            tvEndTime.setText(work_endtime);
            tvWeek.setText(ListUtils.join(week_name));
            tvDeadline.setText(hire_endtime);
            if (unit_name.contains("每")) {
                tvPrice.setText("￥" + subtotal + unit_name.replace("每", "/"));
            } else {
                tvPrice.setText("￥" + subtotal + "/" + unit_name);
            }
            tvTotal.setText("￥" + amount);
            tvPayment.setText(type_name);
            if (TextUtils.equals(is_insurance, "1")) {
                tvInsurance.setText("是");
            } else tvInsurance.setText("否");
            if (TextUtils.equals(is_dine, "1")) tvLunch.setText("是");
            else tvLunch.setText("否");
            if (TextUtils.equals(is_lodging, "1")) tvLive.setText("是");
            else tvLive.setText("否");
            if (TextUtils.equals(is_tool, "1")) tvUtils.setText("是");
            else tvUtils.setText("否");
            if (TextUtils.equals(is_transportation_expenses, "1")) tvTransportation.setText("是");
            else tvTransportation.setText("否");
            if (TextUtils.equals(is_correspondence, "1")) tvCommunications.setText("是");
            else tvCommunications.setText("否");
            tvOther.setText(others_text);
            tvAudit.setText(audit);
            mapLab = JSONUtils.parseKeyAndValueToMap(map.get("labdiff"));
            mapCap = JSONUtils.parseKeyAndValueToMap(map.get("capdiff"));
            mapIntersect = JSONUtils.parseKeyAndValueToMap(map.get("intersect"));

        } else if (params.getUri().contains("Contract/sign")) {
            showToast(JSONUtils.parseKeyAndValueToMap(result).get("message"));
            AppManager.getInstance().killActivity(ViewFeedbackAty.class);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    finish();
                }
            }, 1500);
        } else if (params.getUri().contains("Hire/cancelAccept")) {
            showToast(JSONUtils.parseKeyAndValueToMap(result).get("message"));
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    finish();
                }
            }, 1500);
        } else if (params.getUri().contains("Hire/labReplyAccept")) {
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

    private void initStatus(Map<String, String> mapLab) {
        vWorkSpot.setVisibility(View.GONE);
        vPerson.setVisibility(View.GONE);
        vStartDateSpot.setVisibility(View.GONE);
        vEndDateSpot.setVisibility(View.GONE);
        vStartTimeSpot.setVisibility(View.GONE);
        vEndTimeSpot.setVisibility(View.GONE);
        vWeekSpot.setVisibility(View.GONE);
        vPriceSpot.setVisibility(View.GONE);
        vPaymentSpot.setVisibility(View.GONE);
        vAddressSpot.setVisibility(View.GONE);
        vInsuranceSpot.setVisibility(View.GONE);
        vLunchSpot.setVisibility(View.GONE);
        vLiveSpot.setVisibility(View.GONE);
        vUtilsSpot.setVisibility(View.GONE);
        vTransportationSpot.setVisibility(View.GONE);
        vTotalSpot.setVisibility(View.GONE);
        vCommunicationsSpot.setVisibility(View.GONE);
        vDeadline.setVisibility(View.GONE);
        if (mapLab.containsKey("skill_list")) {
            skill_name = new ArrayList<>();
            ArrayList<Map<String, String>> skill_list = JSONUtils.parseKeyAndValueToMapList(mapLab.get("skill_list"));
            for (int i = 0; i < ListUtils.getSize(skill_list); i++) {
                skill_name.add(skill_list.get(i).get("name"));
            }
            tvWork.setText(ListUtils.join(skill_name));
            vWorkSpot.setVisibility(View.VISIBLE);
        }
        if (mapLab.containsKey("contract_starttime")) {
            tvStartDate.setText(mapLab.get("contract_starttime"));
            vStartDateSpot.setVisibility(View.VISIBLE);
        }
        if (mapLab.containsKey("contract_endtime")) {
            tvEndDate.setText(mapLab.get("contract_endtime"));
            vEndDateSpot.setVisibility(View.VISIBLE);
        }
        if (mapLab.containsKey("work_starttime")) {
            tvStartTime.setText(mapLab.get("work_starttime"));
            vStartTimeSpot.setVisibility(View.VISIBLE);
        }
        if (mapLab.containsKey("work_endtime")) {
            tvEndTime.setText(mapLab.get("work_endtime"));
            vEndTimeSpot.setVisibility(View.VISIBLE);
        }
        if (mapLab.containsKey("work_week_name")) {
            tvWeek.setText(ListUtils.join(JsonArryToList.strList(mapLab.get("work_week_name"))));
            vWeekSpot.setVisibility(View.VISIBLE);
        }
        if (mapLab.containsKey("unit_name") || mapLab.containsKey("subtotal")) {
            if (mapLab.containsKey("unit_name")) {
                unit_name = mapLab.get("unit_name");
            }
            if (unit_name.contains("每")) {
                if (!mapLab.containsKey("subtotal")) {
                    tvPrice.setText("￥" + mapHire.get("subtotal") + unit_name.replace("每", "/"));
                } else {
                    tvPrice.setText("￥" + mapLab.get("subtotal") + unit_name.replace("每", "/"));
                }

            } else {
                if (!mapLab.containsKey("subtotal")) {
                    tvPrice.setText("￥" + mapHire.get("subtotal") + "/" + unit_name);
                } else {
                    tvPrice.setText("￥" + mapLab.get("subtotal") + "/" + unit_name);
                }
            }
            vPriceSpot.setVisibility(View.VISIBLE);
        }
        if (mapLab.containsKey("amount")) {
            tvTotal.setText("￥" + mapLab.get("amount"));
            vTotalSpot.setVisibility(View.VISIBLE);
        }
        if (mapLab.containsKey("hire_endtime")) {
            tvDeadline.setText(mapLab.get("hire_endtime"));
            vDeadline.setVisibility(View.VISIBLE);
        }
        if (mapLab.containsKey("settle_name")) {
            tvPayment.setText(mapLab.get("settle_name"));
            vPaymentSpot.setVisibility(View.VISIBLE);
        }
        if (mapLab.containsKey("ress")) {
            tvAddress.setText(mapLab.get("ress"));
            vAddressSpot.setVisibility(View.VISIBLE);
        }
        if (mapLab.containsKey("is_insurance")) {
            tvInsurance.setText(TextUtils.equals(mapLab.get("is_insurance"), "1") ? "是" : "否");
            vInsuranceSpot.setVisibility(View.VISIBLE);
        }
        if (mapLab.containsKey("is_dine")) {
            tvLunch.setText(TextUtils.equals(mapLab.get("is_dine"), "1") ? "是" : "否");
            vLunchSpot.setVisibility(View.VISIBLE);
        }
        if (mapLab.containsKey("is_lodging")) {
            tvLive.setText(TextUtils.equals(mapLab.get("is_lodging"), "1") ? "是" : "否");
            vLiveSpot.setVisibility(View.VISIBLE);
        }
        if (mapLab.containsKey("is_tool")) {
            tvUtils.setText(TextUtils.equals(mapLab.get("is_tool"), "1") ? "是" : "否");
            vUtilsSpot.setVisibility(View.VISIBLE);
        }
        if (mapLab.containsKey("is_transportation_expenses")) {
            tvTransportation.setText(TextUtils.equals(mapLab.get("is_transportation_expenses"), "1") ? "是" : "否");
            vTransportationSpot.setVisibility(View.VISIBLE);
        }
        if (mapLab.containsKey("is_correspondence")) {
            tvCommunications.setText(TextUtils.equals(mapLab.get("is_correspondence"), "1") ? "是" : "否");
            vCommunicationsSpot.setVisibility(View.VISIBLE);
        }
        if (mapLab.containsKey("audit")) {
            tvAudit.setText(mapLab.get("audit"));
        }
        if (mapLab.containsKey("others_text")) {
            tvOther.setText(mapLab.get("others_text"));
        }
        if (mapLab.containsKey("staff")) {
            tvPerson.setText(mapLab.get("staff"));
            vPerson.setVisibility(View.VISIBLE);
        }
    }

    private void initIntersect(Map<String, String> mapLab) {
        initTextColor(tvWork);
        initTextColor(tvPerson);
        initTextColor(tvStartDate);
        initTextColor(tvEndDate);
        initTextColor(tvStartTime);
        initTextColor(tvEndTime);
        initTextColor(tvWeek);
        initTextColor(tvDeadline);
        initTextColor(tvPrice);
        initTextColor(tvTotal);
        initTextColor(tvPayment);
        initTextColor(tvInsurance);
        initTextColor(tvLunch);
        initTextColor(tvLive);
        initTextColor(tvUtils);
        initTextColor(tvTransportation);
        initTextColor(tvCommunications);
        if (mapLab.containsKey("skill_list")) {
            skill_name = new ArrayList<>();
            ArrayList<Map<String, String>> skill_list = JSONUtils.parseKeyAndValueToMapList(mapLab.get("skill_list"));
            for (int i = 0; i < ListUtils.getSize(skill_list); i++) {
                skill_name.add(skill_list.get(i).get("name"));
            }
//            for (int i = 0; i < ListUtils.getSize(skillLab); i++) {
//                skill_name.add(skillLab.get(i).get("name"));
//            }
            tvWork.setText(ListUtils.join(skill_name));
            vWorkSpot.setVisibility(View.GONE);
            setTextColor(tvWork);
        }
        if (mapLab.containsKey("staff")) {
            tvPerson.setText(mapLab.get("staff"));
            vPerson.setVisibility(View.GONE);
            setTextColor(tvPerson);
        }
        if (mapLab.containsKey("contract_starttime")) {
            tvStartDate.setText(mapLab.get("contract_starttime"));
            vStartDateSpot.setVisibility(View.GONE);
            setTextColor(tvStartDate);
        }
        if (mapLab.containsKey("contract_endtime")) {
            tvEndDate.setText(mapLab.get("contract_endtime"));
            vEndDateSpot.setVisibility(View.GONE);
            setTextColor(tvEndDate);
        }
        if (mapLab.containsKey("work_starttime")) {
            tvStartTime.setText(DateUtils.timeMinute(mapLab.get("work_starttime")));
            vStartTimeSpot.setVisibility(View.GONE);
            setTextColor(tvStartTime);
        }
        if (mapLab.containsKey("work_endtime")) {
            tvEndTime.setText(DateUtils.timeMinute(mapLab.get("work_endtime")));
            vEndTimeSpot.setVisibility(View.GONE);
            setTextColor(tvEndTime);
        }
        if (mapLab.containsKey("hire_endtime")) {
            tvDeadline.setText(mapLab.get("hire_endtime"));
            vDeadline.setVisibility(View.GONE);
        }
        if (mapLab.containsKey("work_week_name")) {
            tvWeek.setText(ListUtils.join(JsonArryToList.strList(mapLab.get("work_week_name"))));
            vWeekSpot.setVisibility(View.GONE);
            setTextColor(tvWeek);
        }
        if (mapLab.containsKey("subtotal")) {
            if (mapLab.containsKey("unit_name")) {
                unit_name = mapLab.get("unit_name");
            }
            if (unit_name.contains("每")) {
                tvPrice.setText("￥" + mapLab.get("subtotal") + unit_name.replace("每", "/"));
            } else {
                tvPrice.setText("￥" + mapLab.get("subtotal") + "/" + unit_name);
            }
            vPriceSpot.setVisibility(View.GONE);
            setTextColor(tvPrice);
        }
        if (mapLab.containsKey("amount")) {
            tvTotal.setText("￥" + mapLab.get("amount"));
            vTotalSpot.setVisibility(View.GONE);
            setTextColor(tvTotal);
        }
        if (mapLab.containsKey("settle_name")) {
            tvPayment.setText(mapLab.get("settle_name"));
            vPaymentSpot.setVisibility(View.GONE);
            setTextColor(tvPayment);
        }
        if (mapLab.containsKey("ress")) {
            tvAddress.setText(mapLab.get("ress"));
            vAddressSpot.setVisibility(View.GONE);
            setTextColor(tvAddress);
        }
        if (mapLab.containsKey("is_insurance")) {
            tvInsurance.setText(TextUtils.equals(mapLab.get("is_insurance"), "1") ? "是" : "否");
            vInsuranceSpot.setVisibility(View.GONE);
            setTextColor(tvInsurance);
        }
        if (mapLab.containsKey("is_dine")) {
            tvLunch.setText(TextUtils.equals(mapLab.get("is_dine"), "1") ? "是" : "否");
            vLunchSpot.setVisibility(View.GONE);
            setTextColor(tvLunch);
        }
        if (mapLab.containsKey("is_lodging")) {
            tvLive.setText(TextUtils.equals(mapLab.get("is_lodging"), "1") ? "是" : "否");
            vLiveSpot.setVisibility(View.GONE);
            setTextColor(tvLive);
        }
        if (mapLab.containsKey("is_tool")) {
            tvUtils.setText(TextUtils.equals(mapLab.get("is_tool"), "1") ? "是" : "否");
            vUtilsSpot.setVisibility(View.GONE);
            setTextColor(tvUtils);
        }
        if (mapLab.containsKey("is_transportation_expenses")) {
            tvTransportation.setText(TextUtils.equals(mapLab.get("is_transportation_expenses"), "1") ? "是" : "否");
            vTransportationSpot.setVisibility(View.GONE);
            setTextColor(tvTransportation);
        }
        if (mapLab.containsKey("is_correspondence")) {
            tvCommunications.setText(TextUtils.equals(mapLab.get("is_correspondence"), "1") ? "是" : "否");
            vCommunicationsSpot.setVisibility(View.GONE);
            setTextColor(tvCommunications);
        }
        if (mapLab.containsKey("audit")) {
            tvAudit.setText(mapLab.get("audit"));
            setTextColor(tvAudit);
        }
        if (mapLab.containsKey("others_text")) {
            tvOther.setText(mapLab.get("others_text"));
            setTextColor(tvOther);
        }
        if (mapLab.containsKey("staff")) {
            tvPerson.setText(mapLab.get("staff"));
            setTextColor(tvPerson);
        }
    }

    // 设置textview 颜色
    private void setTextColor(TextView textView) {
        textView.setTextColor(getResources().getColor(R.color.clr_main));
    }

    //初始化textView 颜色
    private void initTextColor(TextView textView) {
        textView.setTextColor(getResources().getColor(R.color.clr_default_text));
    }

    @Event({R.id.l_w_e_edit_contract, R.id.l_w_e_agree_contract})
    private void onClick(View view) {
        switch (view.getId()) {
            case R.id.l_w_e_edit_contract:      //修改

                if (getIntent().getStringExtra("flag") != null && getIntent().getStringExtra("flag").equals("wzw1")) {      //搜工作中的取消

                    showDialog("", "确定要取消该招工单啊？", "确定", "取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            showProgressDialog();
                            hire.cancelAccept(getIntent().getStringExtra("hire_noid"), getIntent().getStringExtra("lab_noid"), ModifyDetailsAty.this);
                        }
                    }, null);
                } else {
                    Bundle bundle = new Bundle();
                    bundle.putString("hire_noid", hire_noid);
                    bundle.putString("lab_noid", lab_noid);
                    startActivity(ModifyRecruitmentInformationAty.class, bundle);
                }

                break;
            case R.id.l_w_e_agree_contract:     //签约

                if (getIntent().getStringExtra("flag") != null && getIntent().getStringExtra("flag").equals("wzw1")) {      //搜工作中的取消
                    showDialog("", "确定签约该招工单吗？", 0, "确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                            showProgressDialog();
                            hire.labReplyAccept(getIntent().getStringExtra("lab_noid"), getIntent().getStringExtra("hire_noid"), getIntent().getStringExtra("noid"), ModifyDetailsAty.this);
                        }
                    }, "取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    });
                } else {
                    if (TextUtils.isEmpty(lab_noid)) {
                        showToast("请选择要签约的劳方");
                        return;
                    }
                    showDialog("", "是否确定要与该劳方签约？", "确定签约", "再看看", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            showProgressDialog();
                            contract.sign(hire_noid, application.getUserInfo().get("noid"), lab_noid, ModifyDetailsAty.this);
                        }
                    }, null);
                }
                break;
        }
    }


    //====================TabLayout监听(开始)========================
    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        prompt_tv.setVisibility(View.GONE);
        otherModifyLl.setVisibility(View.GONE);
        switch (tab.getPosition()) {
            case 0:
                svEditContent.post(new Runnable() {
                    @Override
                    public void run() {
                        svEditContent.fullScroll(ScrollView.FOCUS_UP);
                    }
                });
                hire.detail(lab_noid, hire_noid, this);
                svEditContent.setVisibility(View.VISIBLE);
                linlayEmpty.setVisibility(View.GONE);
                linlaySpot.setVisibility(View.GONE);
                linlayBlue.setVisibility(View.GONE);
                initStatus(new HashMap<String, String>());
                initIntersect(new HashMap<String, String>());
                break;
            case 1:
                svEditContent.post(new Runnable() {
                    @Override
                    public void run() {
                        svEditContent.fullScroll(ScrollView.FOCUS_UP);
                    }
                });
                Log.e("***", "修改明细里的flag:" + getIntent().getStringExtra("wzw1"));
                if (getIntent().getStringExtra("flag") != null && getIntent().getStringExtra("flag").equals("wzw1")) {
                    if (MapUtils.isEmpty(mapCap) && MapUtils.isEmpty(mapIntersect)) {
                        svEditContent.setVisibility(View.GONE);
                        linlayEmpty.setVisibility(View.VISIBLE);
                        linlaySpot.setVisibility(View.GONE);
                    } else {
                        initStatus(mapHire);
                        svEditContent.setVisibility(View.VISIBLE);
                        linlayEmpty.setVisibility(View.GONE);
                        linlaySpot.setVisibility(View.VISIBLE);
                        initStatus(mapCap);
                    }
                } else {
                    if (MapUtils.isEmpty(mapLab) && MapUtils.isEmpty(mapIntersect)) {
                        svEditContent.setVisibility(View.GONE);
                        linlayEmpty.setVisibility(View.VISIBLE);
                        linlaySpot.setVisibility(View.GONE);
                    } else {
//                        hire.detail(lab_noid, hire_noid, this);
                        initStatus(mapHire);
                        svEditContent.setVisibility(View.VISIBLE);
                        linlayEmpty.setVisibility(View.GONE);
                        linlaySpot.setVisibility(View.VISIBLE);
                        initStatus(mapLab);
                    }
                }


                if (!MapUtils.isEmpty(mapIntersect)) {
                    linlayBlue.setVisibility(View.VISIBLE);
                    initIntersect(mapIntersect);
                }
                break;
            case 2:
                svEditContent.post(new Runnable() {
                    @Override
                    public void run() {
                        svEditContent.fullScroll(ScrollView.FOCUS_UP);
                    }
                });

                if (getIntent().getStringExtra("flag") != null && getIntent().getStringExtra("flag").equals("wzw1")) {
                    if (MapUtils.isEmpty(mapLab) && MapUtils.isEmpty(mapIntersect)) {
                        svEditContent.setVisibility(View.GONE);
                        linlayEmpty.setVisibility(View.VISIBLE);
                        linlaySpot.setVisibility(View.GONE);
                    } else {
                        initStatus(mapHire);
                        svEditContent.setVisibility(View.VISIBLE);
                        linlayEmpty.setVisibility(View.GONE);
                        linlaySpot.setVisibility(View.VISIBLE);
                        initStatus(mapLab);
                    }
                } else {

                    if (MapUtils.isEmpty(mapCap) && MapUtils.isEmpty(mapIntersect)) {
                        svEditContent.setVisibility(View.GONE);
                        linlayEmpty.setVisibility(View.VISIBLE);
                        linlaySpot.setVisibility(View.GONE);
                        initStatus(mapCap);
                    } else {
//                        hire.detail(lab_noid, hire_noid, this);
                        initStatus(mapHire);
                        svEditContent.setVisibility(View.VISIBLE);
                        linlayEmpty.setVisibility(View.GONE);
                        linlaySpot.setVisibility(View.VISIBLE);
                        initStatus(mapCap);
                    }
                }


                if (!MapUtils.isEmpty(mapIntersect)) {
                    linlayBlue.setVisibility(View.VISIBLE);
                    initIntersect(mapIntersect);
                }
                break;
        }
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }
    //====================TabLayout监听(结束)========================

}
