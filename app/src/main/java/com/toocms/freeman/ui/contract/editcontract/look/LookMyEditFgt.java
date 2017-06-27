package com.toocms.freeman.ui.contract.editcontract.look;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.toocms.frame.listener.LocationListener;
import com.toocms.frame.ui.BaseFragment;
import com.toocms.freeman.R;
import com.toocms.freeman.config.Constants;
import com.toocms.freeman.https.Contract;
import com.toocms.freeman.ui.contract.editcontract.EditContractAty;
import com.toocms.freeman.ui.util.ApkUtils;

import org.xutils.http.RequestParams;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cn.zero.android.common.permission.PermissionSuccess;
import cn.zero.android.common.util.JSONUtils;
import cn.zero.android.common.util.ListUtils;
import cn.zero.android.common.util.MapUtils;
import cn.zero.android.common.view.FButton;

/**
 * Created by admin on 2017/3/28.
 */

public class LookMyEditFgt extends BaseFragment {
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
    @ViewInject(R.id.l_w_e_empty)
    LinearLayout linlayEmpty;
    @ViewInject(R.id.l_w_e_deadline_content_divid)
    View vDeadLine;
    @ViewInject(R.id.l_w_e_deadline_content)
    LinearLayout linlayDeadline;
    @ViewInject(R.id.l_w_e_agree_contract)
    FButton fBtnAgree;
    private String contract_noid;
    private String noid;
    private String tag;
    private Contract contract;
    private Map<String, String> dateMap;
    private Map<String, String> contractMap;
    private ArrayList<String> skillName;
    private String unit_name;
    private LookContractInfo fragmentActivity;
    @ViewInject(R.id.l_w_e_person_spot)
    private View vPerson;
    private String latitude;
    private String longitude;
    private static final String[] authBaseArr = {Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.READ_PHONE_STATE};
    private static final int authBaseRequestCode = 1;
    private String ress;

    @Override
    protected int getLayoutResId() {
        return R.layout.fgt_look_worker_edit;
    }

    @Override
    protected void initialized() {
        contract = new Contract();
    }

    @Override
    protected void requestData() {
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        vDeadLine.setVisibility(View.GONE);
        linlayDeadline.setVisibility(View.GONE);
        contract_noid = getActivity().getIntent().getStringExtra("contract_noid");
        noid = application.getUserInfo().get("noid");
        tag = getActivity().getIntent().getStringExtra("tag");
        fragmentActivity = (LookContractInfo) getActivity();
    }

    @Override
    public void onResume() {
        super.onResume();
        showProgressContent();
        contract.reply(contract_noid, noid, this);
    }


    @Override
    public void onComplete(RequestParams params, String result) {
        if (params.getUri().contains("Contract/reply")) {
            dateMap = JSONUtils.parseDataToMap(result);
            contractMap = JSONUtils.parseKeyAndValueToMap(dateMap.get("contract"));
            ArrayList<Map<String, String>> skill = JSONUtils.parseKeyAndValueToMapList(contractMap.get("skill"));
            skillName = new ArrayList<>();
            for (int i = 0; i < ListUtils.getSize(skill); i++) {
                skillName.add(skill.get(i).get("name"));
            }
            tvWork.setText(ListUtils.join(skillName));
            tvPerson.setText(contractMap.get("staff"));
            tvStartDate.setText(contractMap.get("contract_starttime"));
            tvEndDate.setText(contractMap.get("contract_endtime"));
            tvStartTime.setText(contractMap.get("work_starttime"));
            tvEndTime.setText(contractMap.get("work_endtime"));
            ArrayList<Map<String, String>> work_week_list = JSONUtils.parseKeyAndValueToMapList(contractMap.get("work_week"));
            List<String> week_name = new ArrayList<>();
            for (int i = 0; i < ListUtils.getSize(work_week_list); i++) {
                week_name.add(work_week_list.get(i).get("name"));
            }
            tvWeek.setText(ListUtils.join(week_name));
            unit_name = contractMap.get("unit_name");
            if (unit_name.contains("每")) {
                tvPrice.setText("￥" + contractMap.get("subtotal") + unit_name.replace("每", "/"));
            } else {
                tvPrice.setText("￥" + contractMap.get("subtotal") + "/" + unit_name);
            }
            tvTotal.setText("￥" + contractMap.get("amount"));
            tvPayment.setText(contractMap.get("settle_type_name"));
            latitude = contractMap.get("latitude");
            longitude = contractMap.get("longitude");
            tvAddress.setText(contractMap.get("ress"));
            ress = contractMap.get("ress");
            tvInsurance.setText(TextUtils.equals(contractMap.get("is_insurance"), "1") ? "是" : "否");
            tvLunch.setText(TextUtils.equals(contractMap.get("is_dine"), "1") ? "是" : "否");
            tvLive.setText(TextUtils.equals(contractMap.get("is_lodging"), "1") ? "是" : "否");
            tvUtils.setText(TextUtils.equals(contractMap.get("is_tool"), "1") ? "是" : "否");
            tvTransportation.setText(TextUtils.equals(contractMap.get("is_transportation_expenses"), "1") ? "是" : "否");
            tvCommunications.setText(TextUtils.equals(contractMap.get("is_correspondence"), "1") ? "是" : "否");
            tvAudit.setText(contractMap.get("audit"));
            tvOther.setText(contractMap.get("others_text"));
            Map<String, String> mapLab = JSONUtils.parseKeyAndValueToMap(dateMap.get("labdiff"));
            Map<String, String> mapCap = JSONUtils.parseKeyAndValueToMap(dateMap.get("capdiff"));
            if (TextUtils.equals(tag, "cap")) {
                if (TextUtils.equals(dateMap.get("cap_chg"), "0")) {
                    svEditContent.setVisibility(View.GONE);
                    linlayEmpty.setVisibility(View.VISIBLE);
                    fBtnAgree.setVisibility(View.VISIBLE);
                } else {
                    svEditContent.setVisibility(View.VISIBLE);
                    linlayEmpty.setVisibility(View.GONE);
                    fBtnAgree.setVisibility(View.GONE);
                }
                if (MapUtils.isEmpty(mapCap)) {
//                    svEditContent.setVisibility(View.GONE);
                    linlaySpot.setVisibility(View.GONE);
                } else {
                    linlaySpot.setVisibility(View.VISIBLE);
                    initStatus(mapCap);
                }
            } else {
                if (TextUtils.equals(dateMap.get("lab_chg"), "0")) {
                    svEditContent.setVisibility(View.GONE);
                    linlayEmpty.setVisibility(View.VISIBLE);
                    fBtnAgree.setVisibility(View.VISIBLE);
                } else {
                    svEditContent.setVisibility(View.VISIBLE);
                    linlayEmpty.setVisibility(View.GONE);
                    fBtnAgree.setVisibility(View.GONE);
                }
                if (MapUtils.isEmpty(mapLab)) {
//                    svEditContent.setVisibility(View.GONE);
                    linlaySpot.setVisibility(View.GONE);
                } else {
                    linlaySpot.setVisibility(View.VISIBLE);
                    initStatus(mapLab);
                }
            }

//            } else {
//                if (TextUtils.equals(dateMap.get("lab_chg"), "0")) {
//                    svEditContent.setVisibility(View.GONE);
//                    linlayEmpty.setVisibility(View.VISIBLE);
//                } else {
//                    svEditContent.setVisibility(View.VISIBLE);
//                    linlayEmpty.setVisibility(View.GONE);
//                }
//                if (MapUtils.isEmpty(mapLab)) {
//                    linlaySpot.setVisibility(View.GONE);
//                } else {
//                    linlaySpot.setVisibility(View.VISIBLE);
//                    initStatus(mapLab);
//                }
//            }
            Map<String, String> mapIntersect = JSONUtils.parseKeyAndValueToMap(dateMap.get("intersect"));
            if (!MapUtils.isEmpty(mapIntersect)) {
                initIntersect(mapIntersect);
            }
        } else if (params.getUri().contains("Contract/labCancelContractOpinion")) {
            showToast(JSONUtils.parseKeyAndValueToMap(result).get("message"));
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    fragmentActivity.finish();
                }
            }, 1500);
        } else if (params.getUri().contains("Contract/capCancelContractOpinion")) {
            showToast(JSONUtils.parseKeyAndValueToMap(result).get("message"));
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    fragmentActivity.finish();
                }
            }, 1500);
        } else if (params.getUri().contains("Contract/labAcceptContractOpinion")) {
            showToast(JSONUtils.parseKeyAndValueToMap(result).get("message"));
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    fragmentActivity.finish();
                }
            }, 1500);
        } else if (params.getUri().contains("Contract/capAcceptContractOpinion")) {
            showToast(JSONUtils.parseKeyAndValueToMap(result).get("message"));
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    fragmentActivity.finish();
                }
            }, 1500);
        }
        super.onComplete(params, result);
    }

    private void initStatus(Map<String, String> mapLab) {
        vWorkSpot.setVisibility(View.GONE);
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
        if (mapLab.containsKey("skill")) {
            ArrayList<Map<String, String>> skillLab = JSONUtils.parseKeyAndValueToMapList(mapLab.get("skill"));
            skillName = new ArrayList<>();
            for (int i = 0; i < ListUtils.getSize(skillLab); i++) {
                skillName.add(skillLab.get(i).get("name"));
            }
            tvWork.setText(ListUtils.join(skillName));
            vWorkSpot.setVisibility(View.VISIBLE);
        }
        if (mapLab.containsKey("staff")) {
            tvPerson.setText(mapLab.get("staff"));
            vPerson.setVisibility(View.VISIBLE);
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
        if (mapLab.containsKey("work_week")) {
            ArrayList<Map<String, String>> work_week_list = JSONUtils.parseKeyAndValueToMapList(mapLab.get("work_week"));
            List<String> week_name = new ArrayList<>();
            for (int i = 0; i < ListUtils.getSize(work_week_list); i++) {
                week_name.add(work_week_list.get(i).get("name"));
            }
            tvWeek.setText(ListUtils.join(week_name));
            vWeekSpot.setVisibility(View.VISIBLE);
        }
        if (mapLab.containsKey("unit_name") || mapLab.containsKey("subtotal")) {
            if (mapLab.containsKey("unit_name")) {
                unit_name = mapLab.get("unit_name");
            }
            if (mapLab.containsKey("unit_name") && unit_name.contains("每")) {
                if (!mapLab.containsKey("subtotal")) {
                    tvPrice.setText("￥" + contractMap.get("subtotal") + unit_name.replace("每", "/"));
                } else {
                    tvPrice.setText("￥" + mapLab.get("subtotal") + unit_name.replace("每", "/"));
                }

            } else {
                if (!mapLab.containsKey("subtotal")) {
                    tvPrice.setText("￥" + contractMap.get("subtotal") + "/" + unit_name);
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

        if (mapLab.containsKey("settle_type_name")) {
            tvPayment.setText(mapLab.get("settle_type_name"));
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
    }

    private void initIntersect(Map<String, String> mapLab) {
        if (mapLab.containsKey("skill")) {
            ArrayList<Map<String, String>> skillLab = JSONUtils.parseKeyAndValueToMapList(mapLab.get("skill"));
            skillName = new ArrayList<>();
            for (int i = 0; i < ListUtils.getSize(skillLab); i++) {
                skillName.add(skillLab.get(i).get("name"));
            }
            tvWork.setText(ListUtils.join(skillName));
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
            tvStartTime.setText(mapLab.get("work_starttime"));
            vStartDateSpot.setVisibility(View.GONE);
            setTextColor(tvStartTime);
        }
        if (mapLab.containsKey("work_endtime")) {
            tvEndTime.setText(mapLab.get("work_endtime"));
            vEndTimeSpot.setVisibility(View.GONE);
            setTextColor(tvEndTime);
        }
        if (mapLab.containsKey("work_week")) {
            ArrayList<Map<String, String>> work_week_list = JSONUtils.parseKeyAndValueToMapList(mapLab.get("work_week"));
            List<String> week_name = new ArrayList<>();
            for (int i = 0; i < ListUtils.getSize(work_week_list); i++) {
                week_name.add(work_week_list.get(i).get("name"));
            }
            tvWeek.setText(ListUtils.join(week_name));
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
        if (mapLab.containsKey("settle_type_name")) {
            tvPayment.setText(mapLab.get("settle_type_name"));
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
    }

    private void setTextColor(TextView textView) {
        textView.setTextColor(getResources().getColor(R.color.clr_main));
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Event({R.id.l_w_e_old_contract, R.id.l_w_e_edit_contract, R.id.l_w_e_agree_contract, R.id.l_w_e_address_click})
    private void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.l_w_e_old_contract:
                showDialog("", "确定要执行原合同吗？", "执行原合同", "取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (TextUtils.equals(tag, "cap")) {
                            contract.capCancelContractOpinion(contract_noid, noid, LookMyEditFgt.this);
                        } else {
                            contract.labCancelContractOpinion(contract_noid, noid, LookMyEditFgt.this);
                        }
                    }
                }, null);
                break;
            case R.id.l_w_e_edit_contract:
                Bundle bundle = new Bundle();
                bundle.putString("contract_noid", contract_noid);
                bundle.putString("tag", tag);
                startActivity(EditContractAty.class, bundle);
                break;
            case R.id.l_w_e_agree_contract:
                showDialog("", "确定同意对方对合同的修改吗？", "同意", "取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (TextUtils.equals(tag, "cap")) {
                            contract.capAcceptContractOpinion(contract_noid, noid, LookMyEditFgt.this);
                        } else {
                            contract.labAcceptContractOpinion(contract_noid, noid, LookMyEditFgt.this);
                        }
                    }
                }, null);
                break;
            case R.id.l_w_e_address_click:
                showProgressDialog();
                List<String> settle = new ArrayList<>();
                if (ApkUtils.isAvailable(getActivity(), "com.baidu.BaiduMap")) {
                    settle.add("百度地图");
                } else if (ApkUtils.isAvailable(getActivity(), "com.autonavi.minimap")) {
                    settle.add("高德地图");
                }
                settle.add("百度地图(网页版)");
                final CharSequence[] charSequences = new CharSequence[ListUtils.getSize(settle)];
                for (int i = 0; i < ListUtils.getSize(settle); i++) {
                    charSequences[i] = settle.get(i);
                }
                showItemsDialog("", charSequences, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent();
                        if (TextUtils.equals(charSequences[which], "百度地图")) {
                            try {
                                intent = Intent.parseUri("intent://map/direction?" +
                                        "destination=latlng:" + latitude + "," + longitude +
                                        "|name:" + "" +
                                        "&mode=driving" +
                                        "&src=Name|AppName" +
                                        "#Intent;scheme=bdapp;package=com.baidu.BaiduMap;end", 0);
                            } catch (URISyntaxException e) {
                                e.printStackTrace();
                            }
                        } else if (TextUtils.equals(charSequences[which], "高德地图")) {
                            intent.setData(Uri
                                    .parse("androidamap://route?" +
                                            "sourceApplication=softname" +
                                            "&dlat=" + latitude +
                                            "&dlon=" + longitude +
                                            "&dname=" + "" +
                                            "&dev=0" +
                                            "&m=0" +
                                            "&t=2"));
                        } else {
                            requestPermissions(Constants.PERMISSIONS_ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION);
                            return;
                        }
                        startActivity(intent); //启动调用
                    }
                });
                removeProgressDialog();
                break;
        }
    }

    @PermissionSuccess(requestCode = Constants.PERMISSIONS_ACCESS_FINE_LOCATION)
    public void requestWebSuccess() {
        application.startBDLocation(new LocationListener() {
            @Override
            public void onReceiveLocation(BDLocation bdLocation) {
                openWebMap(String.valueOf(bdLocation.getLatitude()), String.valueOf(bdLocation.getLongitude()), "我的位置", latitude, longitude, "目的地", bdLocation.getCity().toString());
            }
        });
    }

    private void openWebMap(String slat, String slon, String sname, String dlat, String dlon, String dname, String city) {
        Uri mapUri = Uri.parse(ApkUtils.getWebBaiduMapUri(slat, slon, sname, dlat, dlon,
                dname, city, "自由人"));
        Intent loction = new Intent(Intent.ACTION_VIEW, mapUri);
        startActivity(loction);
    }

}
