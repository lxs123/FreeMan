package com.toocms.freeman.ui.contract;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.view.menu.MenuBuilder;
import android.text.TextUtils;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.toocms.frame.image.ImageLoader;
import com.toocms.frame.listener.LocationListener;
import com.toocms.frame.tool.DateTool;
import com.toocms.freeman.R;
import com.toocms.freeman.config.Constants;
import com.toocms.freeman.config.JsonArryToList;
import com.toocms.freeman.https.Collect;
import com.toocms.freeman.https.Contract;
import com.toocms.freeman.ui.BaseAty;
import com.toocms.freeman.ui.contract.editcontract.EditContractAty;
import com.toocms.freeman.ui.contract.editcontract.look.LookContractInfo;
import com.toocms.freeman.ui.pay.DisagreePayAty;
import com.toocms.freeman.ui.pay.PayAty;
import com.toocms.freeman.ui.pay.SettlementAty;
import com.toocms.freeman.ui.recruitment.joborder.NewJobOrderAty;
import com.toocms.freeman.ui.util.ApkUtils;
import com.toocms.freeman.ui.util.DateUtils;
import com.toocms.freeman.ui.util.NavigationAty;
import com.toocms.freeman.ui.view.ImagePagerActivity;
import com.toocms.freeman.ui.view.MyGridView;
import com.toocms.freeman.ui.view.MyImageDialog;
import com.toocms.freeman.ui.view.UpPhotoAty;

import org.xutils.common.util.LogUtil;
import org.xutils.http.RequestParams;
import org.xutils.image.ImageOptions;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.lang.reflect.Method;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cn.zero.android.common.permission.PermissionFail;
import cn.zero.android.common.permission.PermissionSuccess;
import cn.zero.android.common.util.JSONUtils;
import cn.zero.android.common.util.ListUtils;
import cn.zero.android.common.view.FButton;
import cn.zero.android.common.view.shapeimageview.CircularImageView;

/**
 * 合同详情
 * Created by admin on 2017/4/5.
 */

public class ContDetailAty extends BaseAty {
    @ViewInject(R.id.c_detail_old_imgs)
    MyGridView imgvOlds;
    @ViewInject(R.id.c_detail_now_imgs)
    MyGridView imgvNows;
    @ViewInject(R.id.cont_det_noid)
    TextView tvNoid;
    @ViewInject(R.id.c_detail_cap_head)
    CircularImageView imgvCapHead;
    @ViewInject(R.id.cont_det_cap_nickname)
    TextView tvCapNickname;
    @ViewInject(R.id.c_detail_cap_phone)
    TextView tvCapPhone;
    @ViewInject(R.id.c_detail_lab_head)
    CircularImageView imgvLabHead;
    @ViewInject(R.id.c_detail_lab_nickname)
    TextView tvLabNickname;
    @ViewInject(R.id.list_cont_lab_phone)
    TextView tvLabPhone;
    @ViewInject(R.id.c_detail_status)
    TextView tvStatus;
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
    @ViewInject(R.id.old_c_fbtn_agree)
    FButton fBtnAgree;
    @ViewInject(R.id.c_detail_old_tag)
    TextView tvOldTag;
    @ViewInject(R.id.c_detail_new_tag)
    TextView tvNewTag;
    @ViewInject(R.id.old_c_btn_content)
    LinearLayout linlayBtn;
    @ViewInject(R.id.c_detail_remove_lay)
    LinearLayout linlayRemove;
    @ViewInject(R.id.c_detail_remove_content)
    TextView tvRemove;
    @ViewInject(R.id.c_detail_mediate)
    TextView tvMediate;
    @ViewInject(R.id.c_detail_dispute_lay)
    LinearLayout linlayDispute;
    @ViewInject(R.id.c_detail_complain)
    TextView tvComplain;
    @ViewInject(R.id.c_detail_dispute_price)
    TextView tvDisputePrice;
    @ViewInject(R.id.c_detail_dispute_progress)
    TextView tvDisputeProgress;
    @ViewInject(R.id.c_detail_dispute_person)
    TextView tvDisputePerson;
    @ViewInject(R.id.c_detail_settlement_lay)
    LinearLayout linlaySettlement;
    @ViewInject(R.id.c_detail_tuibu_lay)
    LinearLayout linlayTuibu;
    @ViewInject(R.id.c_detail_settlement_money)
    TextView tvSettlement;
    @ViewInject(R.id.c_detail_tuibu_money)
    TextView tvTuibu;


    private List<String> oldsList = new ArrayList<>();
    private List<String> nowsList = new ArrayList<>();
    /**
     * 获取合同详情[getContractData]
     *
     * @param noid          参与者的用户编号，可以是作为资方也可以是作为劳方
     * @param contract_noid 合同编号
     */
    /**
     * 收藏合同[insertContract]
     *
     * @param noid          用户noid
     * @param contract_noid 合同编号
     */
    /**
     * capCancelStay 资方 - 拒绝解除[带生效]
     *
     * @param contract_noid
     * @param noid
     */
    private String noid;
    private String contract_noid;
    private Contract contract;
    private Map<String, String> mapCAP;
    private Map<String, String> mapLAB;
    private String accountCap;
    private String accountlab;
    private String type;
    private int status;
    private List<String> photos;
    private List<String> photos_catch;
    private ImagesAdapter adapterNow;
    private final int INVALID = 1; //待生效
    private final int PROGRESS = 2; // 执行中
    private final int SETTLE = 3; // 已结算未评价
    private final int SETTLEED = 4;// 已结算已评价
    private final int DRAWBACK = 5;// 退补款
    private final int ISSUE = 6; // 纠纷
    private final int EXPIRE = 7; // 待生效已过期
    private final int STAY = 8; // 待解除(待生效) 1 - 8
    private final int UNSETTLE = 9; // 待结算
    private final int UNSETTLESTAY = 10; // 待结算解除合同
    private final int STAYED = 11;  // 已解除待结算(待生效)
    private final int DONE = 12;  // 正常已结算
    private final int PROGRESSSTAY = 13; // 待解除(执行中) 2 - 13
    private final int PROGRESSSTAYED = 14; // 已解除待结算(执行中)
    private final int UNSETTLEING = 15; // 待结算发起中
    private final int DRAWBACKED = 16; // 退补款完成
    private final int ISSUED = 17; // 纠纷完成待结算
    private final int DRAWBACKING = 18; // 退补款发起中
    private final int ISSUEDONE = 19;// 纠纷完成已结算
    private final int STAYEDDONE = 20; // 已解除已结算
    private MenuItem itemCollect;
    private MenuItem itemCommit;
    private MenuItem itemComplaint;
    private Collect collect;
    private int position = 0;
    private String isCollect;
    private String noidCap;
    private String requested_noid;
    private String requested_type;
    private String requested_reply;
    private String money;
    private String tag;
    private String evaluate_status;
    private String end_time;
    private String noidLAB;
    private String latitude;
    private String longitude;
    private String ress;
    private NavigationAty navigationAty;
    private String requested_info;
    private String issue_cap_reply;
    private String issue_lab_reply;
    private String issue_tag;
    private String issue_id;
    private String issue_scheme;
    private String issue_amount;
    //判断第一次进入此界面
    private boolean isFirst;
    private static final String[] authBaseArr = {Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.READ_PHONE_STATE};
    private static final int authBaseRequestCode = 1;
    private String issue_cause;
    private String adequacy_status;
    private String adequacy_amount;
    private String payStutas = "null";
    private String dataIsAmount;
    private String can_evaluate;

    @Override
    protected int getLayoutResId() {
        return R.layout.aty_cont_detail;
    }

    @Override
    protected void initialized() {
        contract = new Contract();
        collect = new Collect();
    }

    @Override
    protected void requestData() {
//        showProgressDialog();
        noid = application.getUserInfo().get("noid");
        contract_noid = getIntent().getStringExtra("contract_noid");
//        contract.getContractData(noid, contract_noid, this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActionBar.setOverflowIcon(getResources().getDrawable(R.drawable.icon_unfold));
        imgvNows.setAdapter(new ImagesAdapter(nowsList));
        //导航相关
//        NavigationAty.activityList.add(this);
//        BNOuterLogUtil.setLogSwitcher(true);
//        navigationAty = NavigationAty.getInstance();
//        navigationAty.str(this);
//        if (navigationAty.initDirs()) {
//            navigationAty.initNavi();
//        }

        LogUtil.e(ApkUtils.isAvailable(this, "com.baidu.BaiduMap") + "");
        LogUtil.e(ApkUtils.isAvailable(this, "com.autonavi.minimap") + "");
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (isFirst) {
            showProgressDialog();
        } else {
            showProgressContent();
            isFirst = true;
        }

        contract.getContractData(noid, contract_noid, this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        removeProgressDialog();
    }

    @Override
    public void onComplete(RequestParams params, String result) {
        if (params.getUri().contains("Contract/getContractData")) {
            tvNoid.setText(contract_noid);
            Map<String, String> map = JSONUtils.parseDataToMap(result);
            ArrayList<Map<String, String>> partner = JSONUtils.parseKeyAndValueToMapList(map.get("partner"));
            for (int i = 0; i < ListUtils.getSize(partner); i++) {
                if (TextUtils.equals(partner.get(i).get("role"), "CAP")) {
                    mapCAP = partner.get(i);
                } else if (TextUtils.equals(partner.get(i).get("role"), "LAB")) {
                    mapLAB = partner.get(i);
                }
            }
            tvCapNickname.setText(mapCAP.get("nickname"));
            accountCap = mapCAP.get("account");
            noidCap = mapCAP.get("noid");
            noidLAB = mapLAB.get("noid");
            String phoneCap = accountCap.substring(0, 3) + "****" + accountCap.substring(7, accountCap.length());

            ImageLoader imageLoader = new ImageLoader();
            imageLoader.disPlay(imgvCapHead, mapCAP.get("head"));
            tvLabNickname.setText(mapLAB.get("nickname"));
            accountlab = mapLAB.get("account");
            String phoneLab = accountlab.substring(0, 3) + "****" + accountlab.substring(7, accountlab.length());


            imageLoader.disPlay(imgvLabHead, mapLAB.get("head"));
            Map<String, String> mapContract = JSONUtils.parseKeyAndValueToMap(map.get("contract"));
            evaluate_status = mapContract.get("evaluate_status");
            tvStatus.setText(mapContract.get("status_name"));
            status = Integer.parseInt(mapContract.get("status"));
            if (status == INVALID || status == EXPIRE) {
                tvLabPhone.setText(phoneLab);
                tvCapPhone.setText(phoneCap);
            } else {
                tvLabPhone.setText(accountlab);
                tvCapPhone.setText(accountCap);
            }
            photos = JsonArryToList.strList(mapContract.get("photos"));
            if (ListUtils.isEmpty(photos)) {
                tvOldTag.setVisibility(View.VISIBLE);
            }
            imgvOlds.setAdapter(new ImagesAdapter(photos));
            photos_catch = JsonArryToList.strList(mapContract.get("photos_catch"));
            if (ListUtils.isEmpty(photos_catch)) {
                tvNewTag.setVisibility(View.VISIBLE);
            } else {
                tvNewTag.setVisibility(View.GONE);
            }
            adapterNow = new ImagesAdapter(photos_catch);
            imgvNows.setAdapter(adapterNow);
            ArrayList<Map<String, String>> skill = JSONUtils.parseKeyAndValueToMapList(mapContract.get("skill"));
            List<String> skill_name = new ArrayList<>();
            for (int i = 0; i < ListUtils.getSize(skill); i++) {
                skill_name.add(skill.get(i).get("name"));
            }
            tvWork.setText(ListUtils.join(skill_name));
            tvPerson.setText(mapContract.get("staff"));
            tvStartDate.setText(mapContract.get("contract_starttime"));
            tvEndDate.setText(mapContract.get("contract_endtime"));
            tvStartTime.setText(mapContract.get("work_starttime"));
            tvEndTime.setText(mapContract.get("work_endtime"));
            String unit_name = mapContract.get("unit_name");
            if (unit_name.contains("每")) {
                String replace = unit_name.replace("每", "/");
                tvPrice.setText("￥" + mapContract.get("subtotal") + replace);
            } else {
                tvPrice.setText("￥" + mapContract.get("subtotal") + "/" + unit_name);
            }
            money = mapContract.get("amount");
            tvTotal.setText("￥" + mapContract.get("amount"));
            tvPayment.setText(mapContract.get("settle_type_name"));
            ArrayList<Map<String, String>> work_week_list = JSONUtils.parseKeyAndValueToMapList(mapContract.get("work_week"));
            List<String> work_name = new ArrayList<>();
            for (int i = 0; i < ListUtils.getSize(work_week_list); i++) {
                work_name.add(work_week_list.get(i).get("name"));
            }
            tvWeek.setText(ListUtils.join(work_name));
            tvAddress.setText(mapContract.get("ress"));
            tvInsurance.setText(TextUtils.equals(mapContract.get("is_insurance"), "1") ? "是" : "否");
            tvLunch.setText(TextUtils.equals(mapContract.get("is_dine"), "1") ? "是" : "否");
            tvLive.setText(TextUtils.equals(mapContract.get("is_lodging"), "1") ? "是" : "否");
            tvUtils.setText(TextUtils.equals(mapContract.get("is_tool"), "1") ? "是" : "否");
            tvTransportation.setText(TextUtils.equals(mapContract.get("is_transportation_expenses"), "1") ? "是" : "否");
            tvCommunications.setText(TextUtils.equals(mapContract.get("is_correspondence"), "1") ? "是" : "否");
            tvAudit.setText(mapContract.get("audit"));

            isCollect = map.get("is_collect");
            tvOther.setText(mapContract.get("others_text"));
            requested_noid = mapContract.get("requested_noid");
            requested_type = mapContract.get("requested_type");
            requested_reply = mapContract.get("requested_reply");
            adequacy_status = mapContract.get("adequacy_status");
            adequacy_amount = mapContract.get("adequacy_amount");
            end_time = mapContract.get("end_time");
            can_evaluate = mapContract.get("can_evaluate");
            latitude = mapContract.get("latitude");
            longitude = mapContract.get("longitude");
            ress = mapContract.get("ress");
            if (mapContract.containsKey("requested_info")) {
                requested_info = mapContract.get("requested_info");
            }
            if (mapContract.containsKey("issue_cause")) {
                issue_cause = mapContract.get("issue_cause");
            }
            if (mapContract.containsKey("issue_cap_reply")) {
                issue_cap_reply = mapContract.get("issue_cap_reply");
                issue_lab_reply = mapContract.get("issue_lab_reply");
                issue_id = mapContract.get("issue_id");
                issue_amount = mapContract.get("issue_amount");
            }
            issue_scheme = mapContract.get("issue_scheme");
            initBtn();
        } else if (params.getUri().contains("Collect/insertContract")) {
            showToast(JSONUtils.parseKeyAndValueToMap(result).get("message"));
            isCollect = "1";
        } else if (params.getUri().contains("Collect/cancelContract")) {
            showToast(JSONUtils.parseKeyAndValueToMap(result).get("message"));
            isCollect = "0";
        } else if (params.getUri().contains("Contract/capCancelStay")) {
            showToast(JSONUtils.parseKeyAndValueToMap(result).get("message"));
            contract.getContractData(noid, contract_noid, this);
        } else if (params.getUri().contains("Contract/capAcceptStay")) {
            showToast(JSONUtils.parseKeyAndValueToMap(result).get("message"));
            contract.getContractData(noid, contract_noid, this);
        } else if (params.getUri().contains("Contract/labCancelStay")) {
            showToast(JSONUtils.parseKeyAndValueToMap(result).get("message"));
            contract.getContractData(noid, contract_noid, this);
        } else if (params.getUri().contains("Contract/labAcceptStay")) {
            showToast(JSONUtils.parseKeyAndValueToMap(result).get("message"));
            contract.getContractData(noid, contract_noid, this);
        } else if (params.getUri().contains("Contract/acceptAdequancy") ||
                params.getUri().contains("Contract/capCancelProgressStay") ||
                params.getUri().contains("Contract/capAcceptProgressStay") ||
                params.getUri().contains("Contract/labCancelProgressStay") ||
                params.getUri().contains("Contract/labAcceptProgressStay") ||
                params.getUri().contains("Contract/appectIssue")) {
            showToast(JSONUtils.parseKeyAndValueToMap(result).get("message"));
            contract.getContractData(noid, contract_noid, this);
        } else if (params.getUri().contains("Contract/acceptAdequancy")) {
            dataIsAmount = JSONUtils.parseKeyAndValueToMap(result).get("data");
            if (TextUtils.equals(dataIsAmount, "0")) {
                if (TextUtils.equals(payStutas, "settle")) {
                    contract.acceptAdequancy(contract_noid, noid, ContDetailAty.this);
                } else if (TextUtils.equals(payStutas, "tuibu")) {
                    contract.acceptDrawback(contract_noid, noid, ContDetailAty.this);
                } else if (TextUtils.equals(payStutas, "dispute")) {
                    contract.appectIssue(contract_noid, application.getUserInfo().get("noid"), issue_id, ContDetailAty.this);
                }
            } else {
                showDialog("", "付款金额不足，是否追加资金？", "确认追加", "取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Bundle bundle = new Bundle();
                        bundle.putString("total", dataIsAmount);
                        bundle.putString("contract_noid", contract_noid);
                        if (TextUtils.equals(payStutas, "settle")) {
                            bundle.putString("flag", "settle_repair");
                        } else if (TextUtils.equals(payStutas, "tuibu")) {
                            bundle.putString("flag", "tuibu_repair");
                        } else if (TextUtils.equals(payStutas, "dispute")) {
                            bundle.putString("flag", "dispute_repair");
                            bundle.putString("issue_id", issue_id);
                        }
                        startActivity(PayAty.class, bundle);
                    }

                }, null);
            }
        }

        super.onComplete(params, result);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_more, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        itemCollect = menu.findItem(R.id.menu_collect);
        itemCommit = menu.findItem(R.id.menu_commit);
        itemComplaint = menu.findItem(R.id.menu_complaint);
        switch (status) {
            case EXPIRE:// 待生效已过期

//                break;
            case INVALID: // 待生效
                itemCommit.setVisible(false);
                itemComplaint.setVisible(false);
                itemCollect.setVisible(true);
                break;
            case PROGRESS:// 执行中
                if (TextUtils.equals(noidCap, noid)) {
                    itemCommit.setVisible(false);
                } else {
                    itemCommit.setVisible(true);
                }
                itemCollect.setVisible(true);
                itemComplaint.setVisible(true);
                break;
//            case SETTLE:// 已结算未评价
//                break;
//            case SETTLEED:// 已结算已评价
//                break;


            case STAY:// 待解除(待生效) 1 - 8
                if (TextUtils.equals(noidCap, noid)) {
                    itemCommit.setVisible(false);
                } else {
                    itemCommit.setVisible(true);
                }
                itemCollect.setVisible(true);
                itemComplaint.setVisible(true);
                break;
            case ISSUED:// 纠纷完成待结算
//                break;
            case UNSETTLE: // 待结算
            case UNSETTLESTAY:// 待结算解除合同
            case UNSETTLEING:// 待结算发起中
            case STAYED: // 已解除待结算(待生效)
                if (TextUtils.equals(noidCap, noid)) {
                    itemCommit.setVisible(false);
                } else {
                    itemCommit.setVisible(true);
                }
                itemCollect.setVisible(true);
                itemComplaint.setVisible(true);
                break;

            case PROGRESSSTAY:// 待解除(执行中) 2 - 13
            case PROGRESSSTAYED:// 已解除待结算(执行中)
                if (TextUtils.equals(noidCap, noid)) {
                    itemCommit.setVisible(false);
                } else {
                    itemCommit.setVisible(true);
                }
                itemCollect.setVisible(true);
                itemComplaint.setVisible(true);
                break;

            case ISSUE:// 纠纷
            case DRAWBACK:// 退补款
            case DRAWBACKING:// 退补款发起中
                if (TextUtils.equals(noidCap, noid)) {
                    itemCommit.setVisible(false);

                } else {
                    itemCommit.setVisible(true);
                }
                itemCollect.setVisible(true);
                itemComplaint.setVisible(false);

                break;
            case ISSUEDONE:// 纠纷完成已结算
//                break;

            case DRAWBACKED:// 退补款完成
            case DONE:// 正常已结算
            case STAYEDDONE:// 已解除已结算
                if (TextUtils.equals(noidCap, noid)) {
                    itemCommit.setVisible(false);
                } else {
                    itemCommit.setVisible(true);
                }
                itemCollect.setVisible(true);
                itemComplaint.setVisible(true);
                break;
        }
        if (TextUtils.equals(isCollect, "1")) {
            itemCollect.setIcon(R.drawable.icon_menu_collect_select);
            itemCollect.setTitle("取消收藏");
        } else {
            itemCollect.setIcon(R.drawable.icon_menu_collect);
            itemCollect.setTitle("收藏");
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menu_commit:
                position = 1;
                initStatus();
                break;
            case R.id.menu_complaint:
                position = 2;
                initStatus();
                break;
            case R.id.menu_collect:
                position = 3;
                initStatus();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    // 用于菜单状态控制
    private void initStatus() {
        switch (status) {
            case EXPIRE:// 待生效已过期
//                break;
            case INVALID: // 待生效
                if (position == 3) {
                    if (TextUtils.equals(isCollect, "0")) {
                        showProgressDialog();
                        noid = application.getUserInfo().get("noid");
                        collect.insertContract(noid, contract_noid, this);
                    } else {
                        showProgressDialog();
                        noid = application.getUserInfo().get("noid");
                        collect.cancelContract(noid, contract_noid, this);
                    }
                }
                break;
            case PROGRESS:// 执行中
                if (position == 3) {
                    if (TextUtils.equals(isCollect, "0")) {
                        showProgressDialog();
                        noid = application.getUserInfo().get("noid");
                        collect.insertContract(noid, contract_noid, this);
                    } else {
                        showProgressDialog();
                        noid = application.getUserInfo().get("noid");
                        collect.cancelContract(noid, contract_noid, this);
                    }
                } else if (position == 2) {
                    Bundle bundle = new Bundle();
                    bundle.putString("contract_noid", contract_noid);
                    startActivity(ComplaintAty.class, bundle);
                }
                break;
            case SETTLE:// 已结算未评价
                break;
            case SETTLEED:// 已结算已评价
                break;


            case STAY:// 待解除(待生效) 1 - 8
                if (position == 3) {
                    if (TextUtils.equals(isCollect, "0")) {
                        showProgressDialog();
                        noid = application.getUserInfo().get("noid");
                        collect.insertContract(noid, contract_noid, this);
                    } else {
                        showProgressDialog();
                        noid = application.getUserInfo().get("noid");
                        collect.cancelContract(noid, contract_noid, this);
                    }
                } else if (position == 2) {
                    Bundle bundle = new Bundle();
                    bundle.putString("contract_noid", contract_noid);
                    startActivity(ComplaintAty.class, bundle);
                }
                break;
            case UNSETTLE: // 待结算
            case ISSUED:// 纠纷完成待结算
            case UNSETTLESTAY:// 待结算解除合同
            case UNSETTLEING:// 待结算发起中
            case STAYED: // 已解除待结算(待生效)
                if (position == 3) {
                    if (TextUtils.equals(isCollect, "0")) {
                        showProgressDialog();
                        noid = application.getUserInfo().get("noid");
                        collect.insertContract(noid, contract_noid, this);
                    } else {
                        showProgressDialog();
                        noid = application.getUserInfo().get("noid");
                        collect.cancelContract(noid, contract_noid, this);
                    }
                } else if (position == 2) {
                    Bundle bundle = new Bundle();
                    bundle.putString("contract_noid", contract_noid);
                    startActivity(ComplaintAty.class, bundle);
                }
                break;
            case ISSUEDONE:// 纠纷完成已结算
//                break;

            case DRAWBACKED:// 退补款完成
            case DONE:// 正常已结算
            case STAYEDDONE:// 已解除已结算
            case PROGRESSSTAY:// 待解除(执行中) 2 - 13
            case PROGRESSSTAYED:// 已解除待结算(执行中)
                if (position == 3) {
                    if (TextUtils.equals(isCollect, "0")) {
                        showProgressDialog();
                        noid = application.getUserInfo().get("noid");
                        collect.insertContract(noid, contract_noid, this);
                    } else {
                        showProgressDialog();
                        noid = application.getUserInfo().get("noid");
                        collect.cancelContract(noid, contract_noid, this);
                    }
                } else if (position == 2) {
                    Bundle bundle = new Bundle();
                    bundle.putString("contract_noid", contract_noid);
                    startActivity(ComplaintAty.class, bundle);
                }
                break;


//                break;
            case ISSUE:// 纠纷
            case DRAWBACK:// 退补款
            case DRAWBACKING:// 退补款发起中
                if (position == 3) {
                    if (TextUtils.equals(isCollect, "0")) {
                        showProgressDialog();
                        noid = application.getUserInfo().get("noid");
                        collect.insertContract(noid, contract_noid, this);
                    } else {
                        showProgressDialog();
                        noid = application.getUserInfo().get("noid");
                        collect.cancelContract(noid, contract_noid, this);
                    }
                }
                break;

        }
        if (position == 1) {
            Bundle bundle = new Bundle();
            bundle.putString("contract_noid", contract_noid);
            startActivity(UpPhotoAty.class, bundle);
        }
    }

    //    初始化btn布局
    private void initBtn() {
        linlayRemove.setVisibility(View.GONE);
        linlayDispute.setVisibility(View.GONE);
        linlaySettlement.setVisibility(View.GONE);
        linlayTuibu.setVisibility(View.GONE);
        tvCapPhone.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.btn_phone, 0);
        tvLabPhone.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.btn_phone, 0);
        switch (status) {
            case INVALID: // 待生效
                tvCapPhone.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                tvLabPhone.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                btnNewCont.setVisibility(View.GONE);
                if (TextUtils.equals(noidCap, noid)) {
                    btnConEdit.setVisibility(View.VISIBLE);
                    btnConEdit.setText("解除合同");
                    fBtnAgree.setText("付款");
                    btnConEdit.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            showDialog("", "确定要解除该合同么？", "确定解除", "取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Bundle bundle = new Bundle();
                                    bundle.putString("contract_noid", contract_noid);
                                    bundle.putString("status", "cap");
                                    startActivity(RelieveContAty.class, bundle);
                                }
                            }, null);
                        }
                    });
                    fBtnAgree.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Bundle bundle = new Bundle();
                            bundle.putString("total", money);
                            bundle.putString("contract_noid", contract_noid);
                            startActivity(PayAty.class, bundle);
                        }
                    });
                } else {
                    btnConEdit.setVisibility(View.GONE);
                    fBtnAgree.setText("解除合同");
                    fBtnAgree.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            showDialog("", "确定要解除该合同么？", "确定解除", "取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Bundle bundle = new Bundle();
                                    bundle.putString("contract_noid", contract_noid);
                                    bundle.putString("status", "lab");
                                    startActivity(RelieveContAty.class, bundle);
                                }
                            }, null);
                        }
                    });
                }
                break;
            case PROGRESS:// 执行中
                if (TextUtils.equals(noidCap, noid)) {
                    tag = "cap";
                } else {
                    tag = "lab";
                }
                if (TextUtils.equals(requested_noid, application.getUserInfo().get("noid"))) {
                    if (TextUtils.equals(requested_type, "3")) {
                        switch (Integer.parseInt(requested_reply)) {
                            case 0:
                                linlayBtn.setVisibility(View.VISIBLE);
                                btnNewCont.setVisibility(View.VISIBLE);
                                btnNewCont.setText("查看修改明细");
                                btnConEdit.setVisibility(View.VISIBLE);
                                btnConEdit.setText("解除合同");
                                fBtnAgree.setVisibility(View.VISIBLE);
                                fBtnAgree.setText("结算");
                                btnNewCont.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Bundle bundle = new Bundle();
                                        bundle.putString("contract_noid", contract_noid);
                                        bundle.putString("tag", tag);
                                        startActivity(LookContractInfo.class, bundle);
                                    }
                                });
                                btnConEdit.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        showDialog("", "确定要解除该合同么？", "确定解除", "取消", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                Bundle bundle = new Bundle();
                                                bundle.putString("contract_noid", contract_noid);
                                                bundle.putString("status", tag);
                                                bundle.putString("flag", "progress");
                                                startActivity(RelieveContAty.class, bundle);
                                            }
                                        }, null);
                                    }
                                });
                                fBtnAgree.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        showDialog("", "确定要进行结算么？", "确认结算", "取消", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                contract.toUnsettle(contract_noid, application.getUserInfo().get("noid"), ContDetailAty.this);
                                                Bundle bundle = new Bundle();
                                                bundle.putString("contract_noid", contract_noid);
                                                bundle.putString("money", money);
                                                startActivity(SettlementAty.class, bundle);
                                            }
                                        }, null);
                                    }
                                });
                                break;
                            case 1:
                            case 2:
                                linlayBtn.setVisibility(View.VISIBLE);
                                btnNewCont.setVisibility(View.VISIBLE);
                                btnNewCont.setText("解除合同");
                                btnConEdit.setVisibility(View.VISIBLE);
                                btnConEdit.setText("结算");
                                fBtnAgree.setVisibility(View.VISIBLE);
                                fBtnAgree.setText("修改合同");
                                btnNewCont.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        showDialog("", "确定要解除该合同么？", "确定解除", "取消", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                Bundle bundle = new Bundle();
                                                bundle.putString("contract_noid", contract_noid);
                                                bundle.putString("status", tag);
                                                bundle.putString("flag", "progress");
                                                startActivity(RelieveContAty.class, bundle);
                                            }
                                        }, null);
                                    }
                                });
                                btnConEdit.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        showDialog("", "确定要进行结算么？", "确认结算", "取消", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                contract.toUnsettle(contract_noid, application.getUserInfo().get("noid"), ContDetailAty.this);
                                                Bundle bundle = new Bundle();
                                                bundle.putString("contract_noid", contract_noid);
                                                bundle.putString("money", money);
                                                startActivity(SettlementAty.class, bundle);
                                            }
                                        }, null);
                                    }
                                });
                                fBtnAgree.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Bundle bundle = new Bundle();
                                        bundle.putString("contract_noid", contract_noid);
                                        bundle.putString("tag", tag);
                                        startActivity(EditContractAty.class, bundle);
                                    }
                                });
                                break;
                        }
                    } else if (TextUtils.equals(requested_type, "1")) {
                        linlayBtn.setVisibility(View.VISIBLE);
                        btnNewCont.setVisibility(View.VISIBLE);
                        btnNewCont.setText("解除合同");
                        btnConEdit.setVisibility(View.VISIBLE);
                        btnConEdit.setText("结算");
                        fBtnAgree.setVisibility(View.VISIBLE);
                        fBtnAgree.setText("修改合同");
                        btnNewCont.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                showDialog("", "确定要解除该合同么？", "确定解除", "取消", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Bundle bundle = new Bundle();
                                        bundle.putString("contract_noid", contract_noid);
                                        bundle.putString("status", tag);
                                        bundle.putString("flag", "progress");
                                        startActivity(RelieveContAty.class, bundle);
                                    }
                                }, null);
                            }
                        });
                        btnConEdit.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                showDialog("", "确定要进行结算么？", "确认结算", "取消", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        contract.toUnsettle(contract_noid, application.getUserInfo().get("noid"), ContDetailAty.this);
                                        Bundle bundle = new Bundle();
                                        bundle.putString("contract_noid", contract_noid);
                                        bundle.putString("money", money);
                                        startActivity(SettlementAty.class, bundle);
                                    }
                                }, null);
                            }
                        });
                        fBtnAgree.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Bundle bundle = new Bundle();
                                bundle.putString("contract_noid", contract_noid);
                                bundle.putString("tag", tag);
                                startActivity(EditContractAty.class, bundle);
                            }
                        });
                    }
                } else if (TextUtils.isEmpty(requested_noid)) {
                    linlayBtn.setVisibility(View.VISIBLE);
                    btnNewCont.setVisibility(View.VISIBLE);
                    btnNewCont.setText("解除合同");
                    btnConEdit.setVisibility(View.VISIBLE);
                    btnConEdit.setText("结算");
                    fBtnAgree.setVisibility(View.VISIBLE);
                    fBtnAgree.setText("修改合同");
                    btnNewCont.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            showDialog("", "确定要解除该合同么？", "确定解除", "取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Bundle bundle = new Bundle();
                                    bundle.putString("contract_noid", contract_noid);
                                    bundle.putString("status", tag);
                                    bundle.putString("flag", "progress");
                                    startActivity(RelieveContAty.class, bundle);
                                }
                            }, null);
                        }
                    });
                    btnConEdit.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            showDialog("", "确定要进行结算么？", "确认结算", "取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    contract.toUnsettle(contract_noid, application.getUserInfo().get("noid"), ContDetailAty.this);
                                    Bundle bundle = new Bundle();
                                    bundle.putString("contract_noid", contract_noid);
                                    bundle.putString("money", money);
                                    startActivity(SettlementAty.class, bundle);
                                }
                            }, null);
                        }
                    });
                    fBtnAgree.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Bundle bundle = new Bundle();
                            bundle.putString("contract_noid", contract_noid);
                            bundle.putString("tag", tag);
                            startActivity(EditContractAty.class, bundle);
                        }
                    });
                } else {
                    if (TextUtils.equals(requested_type, "3")) {
                        switch (Integer.parseInt(requested_reply)) {
                            case 0:
                                linlayBtn.setVisibility(View.VISIBLE);
                                btnNewCont.setVisibility(View.VISIBLE);
                                btnNewCont.setText("查看修改明细");
                                btnConEdit.setVisibility(View.VISIBLE);
                                btnConEdit.setText("解除合同");
                                fBtnAgree.setVisibility(View.VISIBLE);
                                fBtnAgree.setText("结算");
                                btnNewCont.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Bundle bundle = new Bundle();
                                        bundle.putString("contract_noid", contract_noid);
                                        bundle.putString("tag", tag);
                                        startActivity(LookContractInfo.class, bundle);
                                    }
                                });
                                btnConEdit.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        showDialog("", "确定要解除该合同么？", "确定解除", "取消", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                Bundle bundle = new Bundle();
                                                bundle.putString("contract_noid", contract_noid);
                                                bundle.putString("status", tag);
                                                bundle.putString("flag", "progress");
                                                startActivity(RelieveContAty.class, bundle);
                                            }
                                        }, null);
                                    }
                                });
                                fBtnAgree.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        showDialog("", "确定要进行结算么？", "确认结算", "取消", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                contract.toUnsettle(contract_noid, application.getUserInfo().get("noid"), ContDetailAty.this);
                                                Bundle bundle = new Bundle();
                                                bundle.putString("contract_noid", contract_noid);
                                                bundle.putString("money", money);
                                                startActivity(SettlementAty.class, bundle);
                                            }
                                        }, null);
                                    }
                                });
                                break;
                            case 1:
                            case 2:
                                linlayBtn.setVisibility(View.VISIBLE);
                                btnNewCont.setVisibility(View.VISIBLE);
                                btnNewCont.setText("解除合同");
                                btnConEdit.setVisibility(View.VISIBLE);
                                btnConEdit.setText("结算");
                                fBtnAgree.setVisibility(View.VISIBLE);
                                fBtnAgree.setText("修改合同");
                                btnNewCont.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        showDialog("", "确定要解除该合同么？", "确定解除", "取消", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                Bundle bundle = new Bundle();
                                                bundle.putString("contract_noid", contract_noid);
                                                bundle.putString("status", tag);
                                                bundle.putString("flag", "progress");
                                                startActivity(RelieveContAty.class, bundle);
                                            }
                                        }, null);
                                    }
                                });
                                btnConEdit.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        showDialog("", "确定要进行结算么？", "确认结算", "取消", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                contract.toUnsettle(contract_noid, application.getUserInfo().get("noid"), ContDetailAty.this);
                                                Bundle bundle = new Bundle();
                                                bundle.putString("contract_noid", contract_noid);
                                                bundle.putString("money", money);
                                                startActivity(SettlementAty.class, bundle);
                                            }
                                        }, null);
                                    }
                                });
                                fBtnAgree.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Bundle bundle = new Bundle();
                                        bundle.putString("contract_noid", contract_noid);
                                        bundle.putString("tag", tag);
                                        startActivity(EditContractAty.class, bundle);
                                    }
                                });
                                break;
                        }
                    } else if (TextUtils.equals(requested_type, "1")) {
                        linlayBtn.setVisibility(View.VISIBLE);
                        btnNewCont.setVisibility(View.VISIBLE);
                        btnNewCont.setText("解除合同");
                        btnConEdit.setVisibility(View.VISIBLE);
                        btnConEdit.setText("结算");
                        fBtnAgree.setVisibility(View.VISIBLE);
                        fBtnAgree.setText("修改合同");
                        btnNewCont.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                showDialog("", "确定要解除该合同么？", "确定解除", "取消", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Bundle bundle = new Bundle();
                                        bundle.putString("contract_noid", contract_noid);
                                        bundle.putString("status", tag);
                                        bundle.putString("flag", "progress");
                                        startActivity(RelieveContAty.class, bundle);
                                    }
                                }, null);
                            }
                        });
                        btnConEdit.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                showDialog("", "确定要进行结算么？", "确认结算", "取消", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        contract.toUnsettle(contract_noid, application.getUserInfo().get("noid"), ContDetailAty.this);
                                        Bundle bundle = new Bundle();
                                        bundle.putString("contract_noid", contract_noid);
                                        bundle.putString("money", money);
                                        startActivity(SettlementAty.class, bundle);
                                    }
                                }, null);
                            }
                        });
                        fBtnAgree.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Bundle bundle = new Bundle();
                                bundle.putString("contract_noid", contract_noid);
                                bundle.putString("tag", tag);
                                startActivity(EditContractAty.class, bundle);
                            }
                        });
                    }
                }
                break;
//            case SETTLE:// 已结算未评价
//                break;
//            case SETTLEED:// 已结算已评价
//                break;

            case ISSUE:// 纠纷
                linlayDispute.setVisibility(View.VISIBLE);
                tvMediate.setText(issue_scheme);
                tvComplain.setText(issue_cause);
                if (TextUtils.equals(noidLAB, noid)) {
                    tvDisputePerson.setText("资方");
                    if (TextUtils.equals(issue_cap_reply, "0")) {
                        tvDisputeProgress.setText("等待中");
                    } else if (TextUtils.equals(issue_cap_reply, "1")) {
                        tvDisputeProgress.setText("已同意");
                    } else {
                        tvDisputeProgress.setText("已拒绝");
                    }
                } else {
                    tvDisputePerson.setText("劳方");
                    if (TextUtils.equals(issue_lab_reply, "0")) {
                        tvDisputeProgress.setText("等待中");
                    } else if (TextUtils.equals(issue_lab_reply, "1")) {
                        tvDisputeProgress.setText("已同意");
                    } else {
                        tvDisputeProgress.setText("已拒绝");
                    }
                }
                if (issue_amount.contains("-")) {
                    tvDisputePrice.setTextColor(Color.parseColor("#fb4a4a"));
                    tvDisputePrice.setText(issue_amount.replace("-", "￥-"));
                } else {
                    tvDisputePrice.setTextColor(getResources().getColor(R.color.clr_main));
                    tvDisputePrice.setText("￥" + issue_amount);
                }
                if (TextUtils.equals(adequacy_status, "1")) {
                    linlaySettlement.setVisibility(View.VISIBLE);
                    if (adequacy_amount.contains("-")) {
                        tvSettlement.setText(adequacy_amount.replace("-", "￥-"));
                        tvSettlement.setTextColor(Color.parseColor("#fb4a4a"));
                    } else {
                        tvSettlement.setText("￥" + adequacy_amount);
                        tvSettlement.setTextColor(getResources().getColor(R.color.clr_main));
                    }
                } else {
                    linlaySettlement.setVisibility(View.GONE);
                }

                btnNewCont.setEnabled(true);
                fBtnAgree.setEnabled(true);
                linlayBtn.setVisibility(View.VISIBLE);
                btnNewCont.setVisibility(View.VISIBLE);
                btnNewCont.setText("拒绝调解方案");
                btnConEdit.setVisibility(View.GONE);
                fBtnAgree.setVisibility(View.VISIBLE);
                fBtnAgree.setText("同意调解方案");
                btnNewCont.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (TextUtils.isEmpty(issue_scheme)) {
                            showToast("提示：暂无调解解方案，请等待……");
                            return;
                        }
                        showDialog("", "确定要拒绝该调解方案么？", "拒绝", "取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Bundle bundle = new Bundle();
                                bundle.putString("flag", "mediate");
                                bundle.putString("contract_noid", contract_noid);
                                bundle.putString("issue_id", issue_id);
                                startActivity(DisagreePayAty.class, bundle);
                            }
                        }, null);
                    }
                });
                fBtnAgree.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (TextUtils.isEmpty(issue_scheme)) {
                            showToast("提示：暂无调解解方案，请等待……");
                            return;
                        }
                        showDialog("", "确定要同意该调解方案么？", "同意", "取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                showProgressDialog();
                                payStutas = "dispute";
                                contract.isAdequacyAmount(contract_noid, noid,
                                        requested_info, ContDetailAty.this);
//                                showProgressDialog();
//                                contract.appectIssue(contract_noid, application.getUserInfo().get("noid"), issue_id, ContDetailAty.this);
                            }
                        }, null);
                    }
                });
                if (TextUtils.equals(noidCap, noid)) {
                    issue_tag = issue_cap_reply;
                } else {
                    issue_tag = issue_lab_reply;
                }
                if (TextUtils.equals(issue_tag, "1")) {
                    btnNewCont.setEnabled(false);
                    fBtnAgree.setEnabled(false);
                    fBtnAgree.setText("已同意调解方案");
                    btnNewCont.setBackgroundDrawable(getResources().getDrawable(R.drawable.btn_save_enable));
                    btnNewCont.setTextColor(Color.WHITE);
                    fBtnAgree.setButtonColor(Color.parseColor("#cccccc"));
                } else if (TextUtils.equals(issue_tag, "2")) {
                    btnNewCont.setEnabled(false);
                    fBtnAgree.setEnabled(false);
                    btnNewCont.setBackgroundDrawable(getResources().getDrawable(R.drawable.btn_save_enable));
                    btnNewCont.setTextColor(Color.WHITE);
                    btnNewCont.setText("已拒绝调解方案");
                    fBtnAgree.setText("同意调解方案");
                    fBtnAgree.setButtonColor(Color.parseColor("#cccccc"));
                }


                break;
            case EXPIRE:// 待生效已过期
                tvCapPhone.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                tvLabPhone.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                if (TextUtils.equals(noid, noidCap)) {
                    linlayBtn.setVisibility(View.VISIBLE);
                    btnNewCont.setVisibility(View.GONE);
                    btnConEdit.setVisibility(View.GONE);
                    fBtnAgree.setVisibility(View.VISIBLE);
                    fBtnAgree.setText("重新签约");
                    fBtnAgree.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Bundle bundle = new Bundle();
                            bundle.putString("flag", "keep");
                            bundle.putString("lab_noid", noidLAB);
                            startActivity(NewJobOrderAty.class, bundle);
                        }
                    });
                } else {
                    linlayBtn.setVisibility(View.GONE);
                }


                break;
            case STAY:// 待解除(待生效) 1 - 8
                linlayRemove.setVisibility(View.VISIBLE);
                tvRemove.setText(requested_info);
                if (TextUtils.equals(requested_noid, noid)) {
                    linlayBtn.setVisibility(View.GONE);
                } else {
                    btnNewCont.setVisibility(View.GONE);
                    btnConEdit.setText("同意解除");
                    fBtnAgree.setText("不同意解除");
                }
                if (TextUtils.equals(noidCap, noid)) {
                    btnConEdit.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            showDialog("", "确定要同意解除合同么？", "同意解除", "取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    showProgressDialog();
                                    contract.capAcceptStay(contract_noid, noid, ContDetailAty.this);
                                }
                            }, null);
                        }
                    });
                    fBtnAgree.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            showDialog("", "确定不同意解除合同么？", "不同意解除", "取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    showProgressDialog();
                                    contract.capCancelStay(contract_noid, noid, ContDetailAty.this);
                                }
                            }, null);
                        }
                    });
                } else {
                    btnConEdit.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            showDialog("", "确定要同意解除合同么？", "同意解除", "取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    showProgressDialog();
                                    contract.labAcceptStay(contract_noid, noid, ContDetailAty.this);
                                }
                            }, null);
                        }
                    });
                    fBtnAgree.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            showDialog("", "确定不同意解除合同么？", "不同意解除", "取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    showProgressDialog();
                                    contract.labCancelStay(contract_noid, noid, ContDetailAty.this);
                                }
                            }, null);
                        }
                    });
                }

                break;
            case ISSUED:// 纠纷完成待结算
//                break;
            case UNSETTLE: // 待结算
//                break;
            case UNSETTLESTAY:// 待结算解除合同
//                break;
            case PROGRESSSTAYED:// 已解除待结算(执行中)
            case UNSETTLEING:// 待结算发起中
            case STAYED: // 已解除待结算(待生效)
                if (TextUtils.equals(requested_type, "2")) {
                    linlaySettlement.setVisibility(View.VISIBLE);
                    if (requested_info.contains("-")) {
                        tvSettlement.setText(requested_info.replace("-", "￥-"));
                        tvSettlement.setTextColor(Color.parseColor("#fb4a4a"));
                    } else {
                        tvSettlement.setText("￥" + requested_info);
                        tvSettlement.setTextColor(getResources().getColor(R.color.clr_main));
                    }
                    if (TextUtils.equals(requested_noid, application.getUserInfo().get("noid"))) {
                        if (TextUtils.equals(requested_reply, "0")) {
                            linlayBtn.setVisibility(View.GONE);
                        } else if (TextUtils.equals(requested_reply, "2")) {
                            btnConEdit.setVisibility(View.GONE);
                            linlayBtn.setVisibility(View.VISIBLE);
                            btnNewCont.setVisibility(View.GONE);
                            fBtnAgree.setVisibility(View.VISIBLE);
                            fBtnAgree.setText("结算");
                            fBtnAgree.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    showDialog("", "确定要进行结算么？", "确认结算", "取消", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            Bundle bundle = new Bundle();
                                            bundle.putString("contract_noid", contract_noid);
                                            bundle.putString("money", money);
                                            startActivity(SettlementAty.class, bundle);
                                        }
                                    }, null);
                                }
                            });

                        }
                    } else if (TextUtils.isEmpty(requested_noid)) {
                        btnConEdit.setVisibility(View.GONE);
                        btnNewCont.setVisibility(View.GONE);
                        fBtnAgree.setVisibility(View.VISIBLE);
                        linlayBtn.setVisibility(View.VISIBLE);
                        fBtnAgree.setText("结算");
                        fBtnAgree.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                showDialog("", "确定要进行结算么？", "确认结算", "取消", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Bundle bundle = new Bundle();
                                        bundle.putString("contract_noid", contract_noid);
                                        bundle.putString("money", money);
                                        startActivity(SettlementAty.class, bundle);
                                    }
                                }, null);
                            }
                        });
                    } else {
                        if (TextUtils.equals(requested_reply, "2")) {
                            btnConEdit.setVisibility(View.GONE);
                            linlayBtn.setVisibility(View.VISIBLE);
                            btnNewCont.setVisibility(View.GONE);
                            fBtnAgree.setVisibility(View.VISIBLE);
                            fBtnAgree.setText("结算");
                            fBtnAgree.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    showDialog("", "确定要进行结算么？", "确认结算", "取消", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            Bundle bundle = new Bundle();
                                            bundle.putString("contract_noid", contract_noid);
                                            bundle.putString("money", money);
                                            startActivity(SettlementAty.class, bundle);
                                        }
                                    }, null);
                                }
                            });
                        } else if (TextUtils.equals(requested_reply, "0")) {
                            linlayBtn.setVisibility(View.VISIBLE);
                            btnConEdit.setVisibility(View.VISIBLE);
                            fBtnAgree.setVisibility(View.VISIBLE);
                            btnNewCont.setVisibility(View.GONE);
                            btnConEdit.setText("不同意结算");
                            fBtnAgree.setText("同意结算");
                            fBtnAgree.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    showDialog("", "确定要同意结算么？", "确定结算", "取消", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            showProgressDialog();
                                            payStutas = "settle";
                                            contract.isAdequacyAmount(contract_noid, noid,
                                                    requested_info, ContDetailAty.this);
                                            contract.acceptAdequancy(contract_noid, noid, ContDetailAty.this);
                                        }
                                    }, null);

                                }
                            });
                            btnConEdit.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    showDialog("", "确定不同意结算么？", "不同意结算", "取消", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            Bundle bundle = new Bundle();
                                            bundle.putString("flag", "settlement");
                                            bundle.putString("contract_noid", contract_noid);
                                            startActivity(DisagreePayAty.class, bundle);
                                        }
                                    }, null);
                                }
                            });
                        }

                    }

                } else {
                    linlayBtn.setVisibility(View.VISIBLE);
                    btnConEdit.setVisibility(View.GONE);
                    btnNewCont.setVisibility(View.GONE);
                    fBtnAgree.setVisibility(View.VISIBLE);
                    fBtnAgree.setText("结算");
                    fBtnAgree.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            showDialog("", "确定要进行结算么？", "确认结算", "取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Bundle bundle = new Bundle();
                                    bundle.putString("contract_noid", contract_noid);
                                    bundle.putString("money", money);
                                    startActivity(SettlementAty.class, bundle);
                                }
                            }, null);
                        }
                    });
                }
                break;

            case PROGRESSSTAY:// 待解除(执行中) 2 - 13
                linlayRemove.setVisibility(View.VISIBLE);
                tvRemove.setText(requested_info);
                if (TextUtils.equals(requested_noid, noid)) {
                    linlayBtn.setVisibility(View.GONE);
                } else {
                    btnNewCont.setVisibility(View.GONE);
                    btnConEdit.setText("同意解除");
                    fBtnAgree.setText("不同意解除");
                }
                if (TextUtils.equals(noidCap, noid)) {
                    btnConEdit.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            showDialog("", "确定要同意解除合同么？", "同意解除", "取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    showProgressDialog();
                                    contract.capAcceptProgressStay(contract_noid, noid, ContDetailAty.this);
                                }
                            }, null);
                        }
                    });
                    fBtnAgree.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            showDialog("", "确定不同意解除合同么？", "不同意解除", "取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    showProgressDialog();
                                    contract.capCancelProgressStay(contract_noid, noid, ContDetailAty.this);
                                }
                            }, null);
                        }
                    });
                } else {
                    btnConEdit.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            showDialog("", "确定要同意解除合同么？", "同意解除", "取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    showProgressDialog();
                                    contract.labAcceptProgressStay(contract_noid, noid, ContDetailAty.this);
                                }
                            }, null);
                        }
                    });
                    fBtnAgree.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            showDialog("", "确定不同意解除合同么？", "不解除合同", "取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    showProgressDialog();
                                    contract.labCancelProgressStay(contract_noid, noid, ContDetailAty.this);
                                }
                            }, null);
                        }
                    });
                }

                break;

            case DRAWBACK:// 退补款
            case DRAWBACKING:// 退补款发起中
                linlayBtn.setVisibility(View.VISIBLE);
                btnNewCont.setVisibility(View.GONE);
                linlaySettlement.setVisibility(View.VISIBLE);
                if (TextUtils.equals(adequacy_status, "1")) {
                    if (adequacy_amount.contains("-")) {
                        tvSettlement.setText(adequacy_amount.replace("-", "￥-"));
                        tvSettlement.setTextColor(Color.parseColor("#fb4a4a"));
                    } else {
                        tvSettlement.setText("￥" + adequacy_amount);
                        tvSettlement.setTextColor(getResources().getColor(R.color.clr_main));
                    }
                } else {
                    tvSettlement.setText("");
                }
                if (TextUtils.equals(requested_type, "4")) {
                    linlayTuibu.setVisibility(View.VISIBLE);

                    if (requested_info.contains("-")) {
                        tvTuibu.setText(requested_info.replace("-", "￥-"));
                        tvTuibu.setTextColor(Color.parseColor("#fb4a4a"));
                    } else {
                        tvTuibu.setText("￥" + requested_info);
                        tvTuibu.setTextColor(getResources().getColor(R.color.clr_main));
                    }
                    if (TextUtils.equals(requested_noid, application.getUserInfo().get("noid"))) {
                        btnConEdit.setVisibility(View.GONE);
                        fBtnAgree.setVisibility(View.VISIBLE);
                        switch (Integer.parseInt(requested_reply)) {
                            case 0:
                                fBtnAgree.setText("取消退补申请");
                                fBtnAgree.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        showDialog("", "确定要取消退补申请吗？", "确定", "取消", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                showProgressDialog();
                                                contract.rollbackDrawback(contract_noid, noid, ContDetailAty.this);
                                            }
                                        }, null);
                                    }
                                });
                                break;
                            case 1:
                                break;
                            case 2:
                                fBtnAgree.setText("退补款");
                                fBtnAgree.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        showDialog("", "确定要进行退补款吗？", "确定", "取消", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                Bundle bundle = new Bundle();
                                                bundle.putString("flag", "tuibu");
                                                bundle.putString("contract_noid", contract_noid);
                                                startActivity(SettlementAty.class, bundle);
                                            }
                                        }, null);
                                    }
                                });
                                break;

                        }
                    } else {
                        btnNewCont.setVisibility(View.VISIBLE);
                        btnConEdit.setVisibility(View.VISIBLE);
                        fBtnAgree.setVisibility(View.GONE);
                        switch (Integer.parseInt(requested_reply)) {
                            case 0:
                                btnConEdit.setText("同意退补");
                                btnNewCont.setText("拒绝退补");
                                break;
                            case 1:
                                break;
                            case 2:
                                btnNewCont.setVisibility(View.GONE);
                                btnConEdit.setVisibility(View.GONE);
                                fBtnAgree.setVisibility(View.VISIBLE);
                                fBtnAgree.setText("退补款");
                                break;
                        }
                        btnConEdit.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                showDialog("", "确定要同意退补款吗？", "同意", "取消", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                        showProgressDialog();
                                        payStutas = "tuibu";
                                        contract.isAdequacyAmount(contract_noid, noid,
                                                requested_info, ContDetailAty.this);
//                                        contract.acceptDrawback(contract_noid, application.getUserInfo().get("noid"), ContDetailAty.this);
                                    }
                                }, null);
                            }
                        });
                        btnNewCont.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                showDialog("", "确定要拒绝退补款吗？", "拒绝", "取消", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Bundle bundle = new Bundle();
                                        bundle.putString("flag", "refuse");
                                        bundle.putString("contract_noid", contract_noid);
                                        startActivity(DisagreePayAty.class, bundle);
                                    }
                                }, null);
                            }
                        });
                        fBtnAgree.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                showDialog("", "确定要进行退补款吗？", "确定", "取消", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Bundle bundle = new Bundle();
                                        bundle.putString("flag", "tuibu");
                                        bundle.putString("contract_noid", contract_noid);
                                        startActivity(SettlementAty.class, bundle);
                                    }
                                }, null);
                            }
                        });
                    }
                } else {
                    linlayBtn.setVisibility(View.VISIBLE);
                    btnNewCont.setVisibility(View.GONE);
                    btnConEdit.setVisibility(View.GONE);
                    fBtnAgree.setVisibility(View.VISIBLE);
                    fBtnAgree.setText("退补款");
                    fBtnAgree.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            showDialog("", "确定要进行退补款吗？", "确定", "取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Bundle bundle = new Bundle();
                                    bundle.putString("flag", "tuibu");
                                    bundle.putString("contract_noid", contract_noid);
                                    startActivity(SettlementAty.class, bundle);
                                }
                            }, null);
                        }
                    });
                }

                break;


            case ISSUEDONE:// 纠纷完成已结算
//                break;
            case DRAWBACKED:// 退补款完成
            case DONE:// 正常已结算
            case STAYEDDONE:// 已解除已结算
                linlaySettlement.setVisibility(View.VISIBLE);
                if (TextUtils.equals(adequacy_status, "1")) {
                    if (adequacy_amount.contains("-")) {
                        tvSettlement.setText(adequacy_amount.replace("-", "￥-"));
                        tvSettlement.setTextColor(Color.parseColor("#fb4a4a"));
                    } else {
                        tvSettlement.setText("￥" + adequacy_amount);
                        tvSettlement.setTextColor(getResources().getColor(R.color.clr_main));
                    }
                } else {
                    tvSettlement.setText("");
                }
                if (requested_info.contains("-")) {
                    tvSettlement.setText(requested_info.replace("-", ""));
                } else {
                    tvSettlement.setText("￥" + requested_info);
                }

                if (TextUtils.equals(noidCap, noid)) {

                    linlayBtn.setVisibility(View.VISIBLE);
                    btnNewCont.setVisibility(View.VISIBLE);
                    btnNewCont.setText("退补款");
                    btnConEdit.setVisibility(View.VISIBLE);
                    fBtnAgree.setText("继续签约");
                    fBtnAgree.setVisibility(View.VISIBLE);
//                    String endTime = map.get("end_time");
                    String timeslashData = DateUtils.timeslashData(end_time);
                    long twoDay = DateTool.getDays(timeslashData, DateTool.getStringDateShort());
                    LogUtil.e(twoDay + "");
                    if (TextUtils.equals(evaluate_status, "0")) {
                        btnConEdit.setText("评价");
                        btnConEdit.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Bundle bundle = new Bundle();
                                bundle.putString("contract_noid", contract_noid);
                                startActivity(EvaluateAty.class, bundle);
                            }
                        });
                        btnNewCont.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                showDialog("", "确定要进行退补款吗？", "确定", "取消", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        contract.toDrawback(contract_noid, noid, ContDetailAty.this);
                                        Bundle bundle = new Bundle();
                                        bundle.putString("flag", "tuibu");
                                        bundle.putString("contract_noid", contract_noid);
                                        startActivity(SettlementAty.class, bundle);
                                    }
                                }, null);
                            }
                        });
                        if (TextUtils.equals(can_evaluate, "0")) {
                            btnConEdit.setVisibility(View.GONE);
                        }

                    } else {


                        if (TextUtils.equals(can_evaluate, "0")) {
                            btnNewCont.setVisibility(View.GONE);
                            btnConEdit.setVisibility(View.VISIBLE);
                            btnConEdit.setText("退补款");
                        } else {
                            btnConEdit.setText("修改评价");
                            btnNewCont.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    showDialog("", "确定要进行退补款吗？", "确定", "取消", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            contract.toDrawback(contract_noid, noid, ContDetailAty.this);
                                            Bundle bundle = new Bundle();
                                            bundle.putString("flag", "tuibu");
                                            bundle.putString("contract_noid", contract_noid);
                                            startActivity(SettlementAty.class, bundle);
                                        }
                                    }, null);
                                }
                            });
                            btnConEdit.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Bundle bundle = new Bundle();
                                    bundle.putString("contract_noid", contract_noid);
                                    bundle.putString("flag", "edit");
                                    startActivity(EvaluateAty.class, bundle);
                                }
                            });

                        }
                    }
                    fBtnAgree.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Bundle bundle = new Bundle();
                            bundle.putString("flag", "keep");
                            bundle.putString("lab_noid", noidLAB);
                            startActivity(NewJobOrderAty.class, bundle);
                        }
                    });

                } else {
                    linlayBtn.setVisibility(View.VISIBLE);
                    btnNewCont.setVisibility(View.GONE);
                    btnConEdit.setVisibility(View.VISIBLE);
                    btnConEdit.setText("退补款");
                    fBtnAgree.setVisibility(View.VISIBLE);
//                    String evaluate_status = map.get("evaluate_status");
//                    String endTime = map.get("end_time");
                    String timeslashData = DateUtils.timeslashData(end_time);
                    long twoDay = DateTool.getDays(timeslashData, DateTool.getStringDateShort());
                    if (TextUtils.equals(evaluate_status, "0")) {
                        fBtnAgree.setText("评价");
                        fBtnAgree.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Bundle bundle = new Bundle();
                                bundle.putString("contract_noid", contract_noid);
                                startActivity(EvaluateAty.class, bundle);
                            }
                        });
                        if (TextUtils.equals(can_evaluate, "0")) {
                            btnConEdit.setVisibility(View.GONE);
                            fBtnAgree.setText("退补款");
                            fBtnAgree.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    showDialog("", "确定要进行退补款吗？", "确定", "取消", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            contract.toDrawback(contract_noid, noid, ContDetailAty.this);
                                            Bundle bundle = new Bundle();
                                            bundle.putString("flag", "tuibu");
                                            bundle.putString("contract_noid", contract_noid);
                                            startActivity(SettlementAty.class, bundle);
                                        }
                                    }, null);
                                }
                            });
                        }
                    } else {

                        if (TextUtils.equals(can_evaluate, "0")) {
                            btnConEdit.setVisibility(View.GONE);
                            fBtnAgree.setText("退补款");
                            fBtnAgree.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    showDialog("", "确定要进行退补款吗？", "确定", "取消", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            contract.toDrawback(contract_noid, noid, ContDetailAty.this);
                                            Bundle bundle = new Bundle();
                                            bundle.putString("flag", "tuibu");
                                            bundle.putString("contract_noid", contract_noid);
                                            startActivity(SettlementAty.class, bundle);
                                        }
                                    }, null);
                                }
                            });


                        } else {
                            fBtnAgree.setText("修改评价");
                            fBtnAgree.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Bundle bundle = new Bundle();
                                    bundle.putString("contract_noid", contract_noid);
                                    bundle.putString("flag", "edit");
                                    startActivity(EvaluateAty.class, bundle);
                                }
                            });
                        }
                    }
                    btnConEdit.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            showDialog("", "确定要进行退补款吗？", "确定", "取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    contract.toDrawback(contract_noid, noid, ContDetailAty.this);
                                    Bundle bundle = new Bundle();
                                    bundle.putString("flag", "tuibu");
                                    bundle.putString("contract_noid", contract_noid);
                                    startActivity(SettlementAty.class, bundle);
                                }
                            }, null);


                        }
                    });

                }
                break;
        }
    }

    @Event({R.id.old_c_address_click, R.id.old_c_skill_click, R.id.old_c_week_click})
    private void onClick(View view) {
        switch (view.getId()) {
            case R.id.old_c_address_click:
                showProgressDialog();
                List<String> settle = new ArrayList<>();
                if (ApkUtils.isAvailable(this, "com.baidu.BaiduMap")) {
                    settle.add("百度地图");
                } else if (ApkUtils.isAvailable(this, "com.autonavi.minimap")) {
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
//                if (android.os.Build.VERSION.SDK_INT >= 23) {
//                    if (!navigationAty.hasBasePhoneAuth()) {
//                        requestPermissions(authBaseArr, authBaseRequestCode);
//                        return;
//                    }
//                }
//                if (navigationAty.hasInitSuccess) {
//                    Toast.makeText(this, "百度导航引擎初始化成功", Toast.LENGTH_SHORT).show();
//                } else {
//                    Toast.makeText(this, "百度导航引擎初始化失败", Toast.LENGTH_SHORT).show();
//                    return;
//                }
//
//                navigationAty.routeplanToNavi(BNRoutePlanNode.CoordinateType.BD09LL, latitude, longitude, ress);
//                /** 此延迟为模拟状态*/
//                new Handler().postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        removeProgressDialog();
//                    }
//                }, 10000);
                break;
            case R.id.old_c_skill_click:
                setDialogText(tvWork.getText().toString());
                break;
            case R.id.old_c_week_click:
                setDialogText(tvWeek.getText().toString());
                break;
        }

    }

    private void openWebMap(String slat, String slon, String sname, String dlat, String dlon, String dname, String city) {
        Uri mapUri = Uri.parse(ApkUtils.getWebBaiduMapUri(slat, slon, sname, dlat, dlon,
                dname, city, "自由人"));
        Intent loction = new Intent(Intent.ACTION_VIEW, mapUri);
        startActivity(loction);
    }

    private void setDialogText(String str) {
        MyImageDialog myImageDialog = new MyImageDialog(this, R.style.Dialog_Fullscreen, 0, 0, false, "text", str);
        myImageDialog.show();
        WindowManager windowManager = this.getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        WindowManager.LayoutParams lp = myImageDialog.getWindow().getAttributes();
        lp.width = (int) (display.getWidth()); //设置宽度
        lp.height = display.getHeight();
        myImageDialog.getWindow().setAttributes(lp);
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
//                    Toast.makeText(this, "缺少导航基本的权限!", Toast.LENGTH_SHORT).show();
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


    //toolbar溢出菜单显示图片
    @Override
    protected boolean onPrepareOptionsPanel(View view, Menu menu) {
        if (menu != null) {
            if (menu.getClass() == MenuBuilder.class) {
                try {
                    Method m = menu.getClass().getDeclaredMethod("setOptionalIconsVisible", Boolean.TYPE);
                    m.setAccessible(true);
                    m.invoke(menu, true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return super.onPrepareOptionsPanel(view, menu);
    }

//    @PermissionSuccess(requestCode = authBaseRequestCode)
//    private void onRequsetSuccess() {
//        navigationAty.routeplanToNavi(BNRoutePlanNode.CoordinateType.BD09LL, latitude, longitude, ress);
//        removeProgressDialog();
//    }

    @PermissionSuccess(requestCode = Constants.PERMISSIONS_CALL_PHONE)
    public void requestSuccess() {
        Intent intent = null;
        if (TextUtils.equals(type, "cap")) {
            intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + accountCap));
        } else {
            intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + accountlab));
        }
        startActivity(intent);
    }

    @PermissionFail(requestCode = Constants.PERMISSIONS_CALL_PHONE)
    public void requestFail() {
        showToast("请求权限失败，暂时无法拨打电话");
    }

    @Event({R.id.c_detail_cap_phone, R.id.list_cont_lab_phone})
    private void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.c_detail_cap_phone:
                type = "cap";
                showDialog("提示", "是否拨打电话", "确定", "取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        requestPermissions(Constants.PERMISSIONS_CALL_PHONE, Manifest.permission.CALL_PHONE);
                    }
                }, null);
                break;
            case R.id.list_cont_lab_phone:
                type = "lab";
                showDialog("提示", "是否拨打电话", "确定", "取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        requestPermissions(Constants.PERMISSIONS_CALL_PHONE, Manifest.permission.CALL_PHONE);
                    }
                }, null);
                break;
        }
    }

    private class ImagesAdapter extends BaseAdapter {
        private List<String> list = new ArrayList<>();
        private ViewHolder viewHolder;

        public ImagesAdapter(List<String> list) {
            this.list = list;
        }

        @Override
        public int getCount() {
            return ListUtils.getSize(list);
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
                convertView = LayoutInflater.from(ContDetailAty.this).inflate(R.layout.listitem_new_job_order, null, false);
                viewHolder = new ViewHolder();
                x.view().inject(viewHolder, convertView);
//                AutoUtils.autoSize(convertView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.imgvDelete.setVisibility(View.GONE);
            ImageLoader imageLoader = new ImageLoader();
            ImageOptions options = new ImageOptions.Builder()
                    .setUseMemCache(true).setLoadingDrawableId(R.drawable.icon_loading).build();
            imageLoader.setImageOptions(options);
            imageLoader.disPlay(viewHolder.imgvImgs, list.get(position));
            viewHolder.imgvImgs.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 图片url,为了演示这里使用常量，一般从数据库中或网络中获取
                    Bundle bundle = new Bundle();
                    bundle.putStringArrayList(ImagePagerActivity.EXTRA_IMAGE_URLS, (ArrayList<String>) list);
                    bundle.putInt(ImagePagerActivity.EXTRA_IMAGE_INDEX, position);
                    startActivity(ImagePagerActivity.class, bundle);
                    overridePendingTransition(0, 0);
                }
            });
            return convertView;
        }


        public class ViewHolder {
            @ViewInject(R.id.list_new_job_imgs)
            ImageView imgvImgs;
            @ViewInject(R.id.list_new_job_delete)
            ImageView imgvDelete;

        }

    }
}
