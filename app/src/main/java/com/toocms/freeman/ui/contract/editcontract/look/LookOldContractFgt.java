package com.toocms.freeman.ui.contract.editcontract.look;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.toocms.frame.listener.LocationListener;
import com.toocms.frame.ui.BaseFragment;
import com.toocms.freeman.R;
import com.toocms.freeman.config.Constants;
import com.toocms.freeman.https.Contract;
import com.toocms.freeman.ui.contract.editcontract.EditContractAty;
import com.toocms.freeman.ui.util.ApkUtils;
import com.toocms.freeman.ui.util.NavigationAty;

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
import cn.zero.android.common.view.FButton;

/**
 * Created by admin on 2017/3/28.
 */

public class LookOldContractFgt extends BaseFragment {
    @ViewInject(R.id.old_work)
    TextView tvWork;
    @ViewInject(R.id.old_c_person)
    TextView tvPerson;
    @ViewInject(R.id.old_c_start_date)
    TextView tvStartDate;
    @ViewInject(R.id.old_c_end_date)
    TextView tvEndDate;
    @ViewInject(R.id.old_c_start_time)
    TextView tvStartTime;
    @ViewInject(R.id.old_c_end_time)
    TextView tvEndTime;
    @ViewInject(R.id.old_c_week)
    TextView tvWeek;
    @ViewInject(R.id.old_c_price)
    TextView tvPrice;
    @ViewInject(R.id.old_c_total)
    TextView tvTotal;
    @ViewInject(R.id.old_c_payment)
    TextView tvPayment;
    @ViewInject(R.id.old_c_address)
    TextView tvAddress;
    @ViewInject(R.id.old_c_insurance)
    TextView tvInsurance;
    @ViewInject(R.id.old_c_lunch)
    TextView tvLunch;
    @ViewInject(R.id.old_c_live)
    TextView tvLive;
    @ViewInject(R.id.old_c_utils)
    TextView tvUtils;
    @ViewInject(R.id.old_c_transportation)
    TextView tvTransportation;
    @ViewInject(R.id.old_c_communications)
    TextView tvCommunications;
    @ViewInject(R.id.old_c_audit)
    TextView tvAudit;
    @ViewInject(R.id.old_c_other)
    TextView tvOther;
    @ViewInject(R.id.old_c_btn_new_cont)
    Button btnNewCont;
    @ViewInject(R.id.look_old_con_edit)
    Button btnConEdit;
    @ViewInject(R.id.l_w_e_agree_contract)
    FButton fBtnAgree;
    @ViewInject(R.id.old_c_btn_content)
    LinearLayout linlayBtn;
    @ViewInject(R.id.old_c_btn_cont)
    LinearLayout linlayBtn2;
    /**
     * 修改明细[reply]
     *
     * @param contract_noid
     * @param noid
     */
    /**
     * 资方 - 拒绝修改[capCancelContractOpinion]
     *
     * @param contract_noid
     * @param noid
     */
    private String contract_noid;
    private String noid;
    private Contract contract;
    private String tag;
    private LookContractInfo fragmentActivity;
    private NavigationAty navigationAty;
    private String latitude;
    private String longitude;
    private static final String[] authBaseArr = {Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.READ_PHONE_STATE};
    private static final int authBaseRequestCode = 1;
    private String ress;

    @Override
    protected int getLayoutResId() {
        return R.layout.fgt_old_contract;
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
        contract_noid = getActivity().getIntent().getStringExtra("contract_noid");
        noid = application.getUserInfo().get("noid");
        linlayBtn.setVisibility(View.GONE);
        linlayBtn2.setVisibility(View.VISIBLE);
        tag = getActivity().getIntent().getStringExtra("tag");
//        x.view().inject(getContent());
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
            Map<String, String> dateMap = JSONUtils.parseDataToMap(result);
            Map<String, String> contractMap = JSONUtils.parseKeyAndValueToMap(dateMap.get("contract"));
            ArrayList<Map<String, String>> skill = JSONUtils.parseKeyAndValueToMapList(contractMap.get("skill"));
            ArrayList<String> skillName = new ArrayList<>();
            for (int i = 0; i < ListUtils.getSize(skill); i++) {
                skillName.add(skill.get(i).get("name"));
            }
            ArrayList<Map<String, String>> work_week_list = JSONUtils.parseKeyAndValueToMapList(contractMap.get("work_week"));
            List<String> week_name = new ArrayList<>();
            for (int i = 0; i < ListUtils.getSize(work_week_list); i++) {
                week_name.add(work_week_list.get(i).get("name"));
            }
            tvWork.setText(ListUtils.join(skillName));
            tvPerson.setText(contractMap.get("staff"));
            tvStartDate.setText(contractMap.get("contract_starttime"));
            tvEndDate.setText(contractMap.get("contract_endtime"));
            tvStartTime.setText(contractMap.get("work_starttime"));
            tvEndTime.setText(contractMap.get("work_endtime"));
            tvWeek.setText(ListUtils.join(week_name));
            String unit_name = contractMap.get("unit_name");
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
            if (TextUtils.equals(tag, "cap")) {
                if (TextUtils.equals(dateMap.get("cap_chg"), "0")) {
                    fBtnAgree.setVisibility(View.VISIBLE);
                } else {
                    fBtnAgree.setVisibility(View.GONE);
                }
            } else {
                if (TextUtils.equals(dateMap.get("lab_chg"), "0")) {
                    fBtnAgree.setVisibility(View.VISIBLE);
                } else {
                    fBtnAgree.setVisibility(View.GONE);
                }
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

    @Override
    public void onError(Map<String, String> error) {
        super.onError(error);
    }

    @Event({R.id.l_w_e_old_contract, R.id.l_w_e_edit_contract, R.id.l_w_e_agree_contract, R.id.old_c_address_click})
    private void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.l_w_e_old_contract:
                showDialog("", "确定要执行原合同吗？", "执行原合同", "取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (TextUtils.equals(tag, "cap")) {
                            contract.capCancelContractOpinion(contract_noid, noid, LookOldContractFgt.this);
                        } else {
                            contract.labCancelContractOpinion(contract_noid, noid, LookOldContractFgt.this);
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
                            contract.capAcceptContractOpinion(contract_noid, noid, LookOldContractFgt.this);
                        } else {
                            contract.labAcceptContractOpinion(contract_noid, noid, LookOldContractFgt.this);
                        }
                    }
                }, null);
                break;
            case R.id.old_c_address_click:
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

//    @Override
//    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
//        // TODO Auto-generated method stub
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        if (requestCode == 1) {
//            navigationAty.initNavi();
//            for (int ret : grantResults) {
//                if (ret == 0) {
//                    continue;
//                } else {
//                    Toast.makeText(getActivity(), "缺少导航基本的权限!", Toast.LENGTH_SHORT).show();
//                    removeProgressDialog();
//                    return;
//                }
//            }
//        }
//        if (requestCode == 2) {
//            for (int ret : grantResults) {
//                if (ret == 0) {
//                    continue;
//                }
//            }
//            navigationAty.routeplanToNavi(navigationAty.mCoordinateType, latitude, longitude, ress);
//        }
//
//    }
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


    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}
