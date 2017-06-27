package com.toocms.freeman.ui.mine;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.toocms.frame.config.Settings;
import com.toocms.frame.image.ImageLoader;
import com.toocms.frame.tool.DateTool;
import com.toocms.freeman.R;
import com.toocms.freeman.config.Constants;
import com.toocms.freeman.config.JsonArryToList;
import com.toocms.freeman.https.Collect;
import com.toocms.freeman.https.Contract;
import com.toocms.freeman.https.Hire;
import com.toocms.freeman.https.Sys;
import com.toocms.freeman.ui.BaseAty;
import com.toocms.freeman.ui.contract.ContDetailAty;
import com.toocms.freeman.ui.contract.EvaluateAty;
import com.toocms.freeman.ui.contract.RelieveContAty;
import com.toocms.freeman.ui.contract.editcontract.EditContractAty;
import com.toocms.freeman.ui.contract.editcontract.look.LookContractInfo;
import com.toocms.freeman.ui.index.ModifyDetailsAty;
import com.toocms.freeman.ui.index.ModifyRecruitmentInformationAty;
import com.toocms.freeman.ui.index.SkillAty;
import com.toocms.freeman.ui.pay.DisagreePayAty;
import com.toocms.freeman.ui.pay.PayAty;
import com.toocms.freeman.ui.pay.SettlementAty;
import com.toocms.freeman.ui.recruitment.AreaListAty;
import com.toocms.freeman.ui.recruitment.joborder.NewJobOrderAty;
import com.toocms.freeman.ui.searchjob.MyJobOrderDetailAty;
import com.toocms.freeman.ui.util.DateUtils;
import com.toocms.freeman.ui.util.WorkOrder;
import com.zero.autolayout.utils.AutoUtils;

import org.xutils.common.util.LogUtil;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import cn.zero.android.common.util.JSONUtils;
import cn.zero.android.common.util.ListUtils;
import cn.zero.android.common.util.MapUtils;
import cn.zero.android.common.view.FButton;
import cn.zero.android.common.view.shapeimageview.CircularImageView;
import cn.zero.android.common.view.swipetoloadlayout.OnLoadMoreListener;
import cn.zero.android.common.view.swipetoloadlayout.OnRefreshListener;
import cn.zero.android.common.view.swipetoloadlayout.view.SwipeToLoadRecyclerView;

/**
 * Created by admin on 2017/4/20.
 */

public class MyCollectAty extends BaseAty implements OnRefreshListener, OnLoadMoreListener {
    private static final int ADDRESS_ID_IN_SERCH = 9788;
    private final String MEMBER_NUMBER = "noid";
    private final String NICKNAME = "nickname";
    private final String FREEMAN = "freeman";
    private final String SEX = "sex_name";
    private final String CREDIT_RATING = "evaluate_score";
    private final String PRICE = "subtotal";
    private final String SKILL = "skill";
    @ViewInject(R.id.my_jo_screen)
    TextView tvScreen;
    @ViewInject(R.id.my_collect_member)
    TextView tvMember;
    @ViewInject(R.id.my_collect_jo)
    TextView tvJo;
    @ViewInject(R.id.my_collect_contract)
    TextView tvContract;
    @ViewInject(R.id.take_title_view)
    View vTitleView;
    @ViewInject(R.id.my_collect_list)
    SwipeToLoadRecyclerView mCollectList;
    @ViewInject(R.id.my_collect_keyword)
    EditText editKeyword;
    @ViewInject(R.id.my_collect_keyword_title)
    TextView tvKeyword;
    @ViewInject(R.id.my_contract_lay)
    ScrollView scrollView;
    @ViewInject(R.id.my_collect_drawer)
    DrawerLayout drawerLayout;
    TextView[] textViews;
    @ViewInject(R.id.my_jo_work)
    TextView tvWork;
    @ViewInject(R.id.my_jo_work_click)
    LinearLayout linlayWork;
    @ViewInject(R.id.my_collect_work_divid)
    View vWork;
    @ViewInject(R.id.my_collect_address)
    TextView tvAddress;
    @ViewInject(R.id.my_collect_address_click)
    LinearLayout linlayAddress;
    @ViewInject(R.id.my_collect_address_divid)
    View vAddress;
    @ViewInject(R.id.my_collect_min_price)
    EditText editMinPrice;
    @ViewInject(R.id.my_collect_max_price)
    EditText editMaxPrice;
    @ViewInject(R.id.my_collect_total_title)
    TextView tvTotalTitle;
    @ViewInject(R.id.my_collect_min_money)
    EditText editMinMoney;
    @ViewInject(R.id.my_collect_max_money)
    EditText editMaxMoney;
    @ViewInject(R.id.my_collect_total_lay)
    LinearLayout linlayTotal;
    @ViewInject(R.id.my_collect_skill)
    TextView tvSkill;
    @ViewInject(R.id.my_collect_skill_click)
    LinearLayout linlaySkill;
    @ViewInject(R.id.my_collect_total_divid)
    View vTotal;
    @ViewInject(R.id.my_collect_skill_divid)
    View vSkill;
    @ViewInject(R.id.my_collect_screen)
    LinearLayout linlayScreen;
    private int position = 1;
    private MemberAdapter memberAdapter;
    /**
     * 收藏用户列表[labList]
     *
     * @param noid      用户编号
     * @param page      分页，默认为1
     * @param code      筛选用户编号
     * @param min_price 最小单价
     * @param max_price 最大单价
     * @param skill_id  技能id数组
     */
    private String noid;
    private String page;
    private String code;
    private String skill_id;
    private Collect collect;
    private ArrayList<Map<String, String>> memberList;
    private Hire hire;
    private String hire_noid;
    private List<Map<String, String>> jobList;
    private String cap_noid;
    private int p = 1;
    private RecruitJOAdapter recruitJOAdapter;
    private String max_price;
    private String min_price;
    private String min_suntotal;
    private String max_suntotal;
    private Contract contract;
    private String telephone;
    /**
     * 收藏合同列表[contractList]
     *
     * @param noid         用户编号
     * @param page         分页
     * @param keywords     关键字
     * @param skill_id     技能id数组
     * @param min_subtotal 最小单价
     * @param max_subtotal 最大单价
     * @param min_price    最小金额
     * @param max_price    最大金额
     */
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
    private String keywords;
    private ContractAdapter contractAdapter;
    private ArrayList<Map<String, String>> contractList;
    private ArrayList<String> skillList = new ArrayList<>();
    private List<Map<String, String>> skillItemData = new ArrayList<>();
    private Sys sys;
    private String province_name;
    private String city_name;
    private String area_name;
    @ViewInject(R.id.my_jo_empty)
    TextView tvEmpty;
    private boolean isMember;
    private boolean isJob;
    private boolean isContract;
    //是不是第一次进去此页
    private boolean isFirst;
    //检索需要补款的钱
    private String dataIsAmount;
    // adapter里点击获取的合同编号
    private String cont_noid;
    // 是否同意结算
    private String payStutas = "null";
    private String issue_id;

    @Override
    protected int getLayoutResId() {
        return R.layout.aty_my_collect;
    }

    @Override
    protected void initialized() {
        collect = new Collect();
        hire = new Hire();
        sys = new Sys();
        contract = new Contract();
    }

    @Override
    protected void requestData() {
        sys.getSkillList("0", "0", this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActionBar.hide();
        WindowManager wm = this.getWindowManager();//获取屏幕宽高
        ViewGroup.LayoutParams layoutParams = linlayScreen.getLayoutParams();
        layoutParams.height = wm.getDefaultDisplay().getHeight();
        layoutParams.width = wm.getDefaultDisplay().getWidth() / 4 * 3;
        linlayScreen.setLayoutParams(layoutParams);
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        drawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {

            }

            @Override
            public void onDrawerOpened(View drawerView) {
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });
        textViews = new TextView[]{tvMember, tvJo, tvContract};
        changeTextClr(1);
        mCollectList.getRecyclerView().setLayoutManager(new LinearLayoutManager(this));
        mCollectList.setOnRefreshListener(this);
        mCollectList.setOnLoadMoreListener(this);
//        MoneyUtils.setPrice(editMaxMoney);
//        MoneyUtils.setPrice(editMaxPrice);
//        MoneyUtils.setPrice(editMinMoney);
//        MoneyUtils.setPrice(editMinPrice);
        initMember();


    }

    @Override
    protected void onResume() {
        super.onResume();
        noid = application.getUserInfo().get("noid");
        page = String.valueOf(p);
        if (!isFirst) {
            showProgressContent();
            collect.labList(noid, page, code, min_suntotal, max_suntotal, skill_id, this);
            isFirst = true;
        } else {
            showProgressDialog();
        }

        if (position == 1) {
            collect.labList(noid, page, code, min_suntotal, max_suntotal, skill_id, this);
        } else if (position == 2) {
            collect.hireList(noid, page, keywords, skill_id, province_name, city_name, area_name, min_price, max_price
                    , min_suntotal, max_suntotal, this);
        } else {
            collect.contractList(noid, page, keywords, skill_id, min_suntotal, max_suntotal, min_price, max_price, this);
        }
        initSkill();
        skill_id = ListUtils.join(skillList);
    }

    private void initSkill() {
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
            else {
                tvWork.setText(ListUtils.join(list));
                tvSkill.setText(ListUtils.join(list));
            }

        }
    }


    @Override
    public void onComplete(RequestParams params, String result) {
        if (params.getUri().contains("Collect/labList")) {
            if (memberAdapter == null) {
                p = 1;
            }
            if (p == 1) {
                memberList = JSONUtils.parseDataToMapList(result);
            } else {
                ArrayList<Map<String, String>> list = JSONUtils.parseDataToMapList(result);
                if (ListUtils.isEmpty(list)) {
                    p--;
                }
                memberList.addAll(list);
            }
            recruitJOAdapter = null;
            contractAdapter = null;
            if (memberAdapter == null) {
                mCollectList.getRecyclerView().setLayoutManager(new LinearLayoutManager(this));
                memberAdapter = new MemberAdapter();
                mCollectList.setAdapter(memberAdapter);

            } else {
                memberAdapter.notifyDataSetChanged();
            }
            if (ListUtils.isEmpty(memberList)) {
                tvEmpty.setVisibility(View.VISIBLE);
            } else {
                tvEmpty.setVisibility(View.GONE);
            }

        } else if (params.getUri().contains("Collect/hireList")) {
            if (recruitJOAdapter == null) {
                p = 1;
            }
            if (p == 1) {
                jobList = JSONUtils.parseDataToMapList(result);
            } else {
                ArrayList<Map<String, String>> list = JSONUtils.parseDataToMapList(result);
                if (ListUtils.isEmpty(list)) {
                    p--;
                }
                jobList.addAll(list);
            }
            memberAdapter = null;
            contractAdapter = null;
            if (recruitJOAdapter == null) {
                mCollectList.getRecyclerView().setLayoutManager(new LinearLayoutManager(this));
                recruitJOAdapter = new RecruitJOAdapter();
                mCollectList.setAdapter(recruitJOAdapter);
            } else {
                recruitJOAdapter.notifyDataSetChanged();
            }
            if (ListUtils.isEmpty(jobList)) {
                tvEmpty.setVisibility(View.VISIBLE);
            } else {
                tvEmpty.setVisibility(View.GONE);
            }
        } else if (params.getUri().contains("Hire/cancelAccept") ||
                params.getUri().contains("Hire/labReplyAccept")) {
//            if (position == 1) {
//                collect.labList(noid, page, code, min_price, max_price, skill_id, this);
//            } else if (position == 2) {
            collect.hireList(noid, page, keywords, skill_id, province_name, city_name, area_name, min_price, max_price
                    , min_suntotal, max_suntotal, this);
//            } else {
//
//            }
        } else if (params.getUri().contains("Collect/contractList")) {
            if (contractAdapter == null) {
                p = 1;
            }
            if (p == 1) {
                contractList = JSONUtils.parseDataToMapList(result);
            } else {
                ArrayList<Map<String, String>> list = JSONUtils.parseDataToMapList(result);
                if (ListUtils.isEmpty(list)) {
                    p--;
                }
                contractList.addAll(list);
            }
            recruitJOAdapter = null;
            memberAdapter = null;
            if (contractAdapter == null) {
                mCollectList.getRecyclerView().setLayoutManager(new LinearLayoutManager(this));
                contractAdapter = new ContractAdapter();
                mCollectList.setAdapter(contractAdapter);
            } else {
                contractAdapter.notifyDataSetChanged();
            }
            if (ListUtils.isEmpty(contractList)) {
                tvEmpty.setVisibility(View.VISIBLE);
            } else {
                tvEmpty.setVisibility(View.GONE);
            }
        } else if (params.getUri().contains("Contract/capCancelStay")) {
            showToast(JSONUtils.parseKeyAndValueToMap(result).get("message"));
            collect.contractList(noid, page, keywords, skill_id, min_suntotal, max_suntotal, min_price, max_price, this);
        } else if (params.getUri().contains("Contract/capAcceptStay")) {
            showToast(JSONUtils.parseKeyAndValueToMap(result).get("message"));
            collect.contractList(noid, page, keywords, skill_id, min_suntotal, max_suntotal, min_price, max_price, this);
        } else if (params.getUri().contains("Contract/labCancelStay") ||
                params.getUri().contains("Contract/capCancelProgressStay") ||
                params.getUri().contains("Contract/capAcceptProgressStay") ||
                params.getUri().contains("Contract/labCancelProgressStay") ||
                params.getUri().contains("Contract/labAcceptProgressStay") ||
                params.getUri().contains("Contract/appectIssue")) {
            showToast(JSONUtils.parseKeyAndValueToMap(result).get("message"));
            collect.contractList(noid, page, keywords, skill_id, min_suntotal, max_suntotal, min_price, max_price, this);
        } else if (params.getUri().contains("Contract/labAcceptStay")) {
            showToast(JSONUtils.parseKeyAndValueToMap(result).get("message"));
            collect.contractList(noid, page, keywords, skill_id, min_suntotal, max_suntotal, min_price, max_price, this);
        } else if (params.getUri().contains("Contract/acceptAdequancy") ||
                params.getUri().contains("Contract/acceptDrawback") ||
                params.getUri().contains("Contract/rollbackDrawback")) {
            showToast(JSONUtils.parseKeyAndValueToMap(result).get("message"));
            collect.contractList(noid, page, keywords, skill_id, min_suntotal, max_suntotal, min_price, max_price, this);
        } else if (params.getUri().contains("Sys/getSkillList")) {
            skillItemData = JSONUtils.parseDataToMapList(result);
        } else if (params.getUri().contains("Contract/isAdequacyAmount")) {
            LogUtil.e(payStutas + "/////////////530");
            dataIsAmount = JSONUtils.parseKeyAndValueToMap(result).get("data");
            if (TextUtils.equals(dataIsAmount, "0")) {
                if (TextUtils.equals(payStutas, "settle")) {
                    contract.acceptAdequancy(cont_noid, noid, MyCollectAty.this);
                } else if (TextUtils.equals(payStutas, "tuibu")) {
                    contract.acceptDrawback(cont_noid, noid, MyCollectAty.this);
                } else if (TextUtils.equals(payStutas, "dispute")) {
                    contract.appectIssue(cont_noid, application.getUserInfo().get("noid"), issue_id, MyCollectAty.this);
                }
            } else {
                showDialog("", "付款金额不足，是否追加资金？", "确认追加", "取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Bundle bundle = new Bundle();
                        bundle.putString("total", dataIsAmount);
                        bundle.putString("contract_noid", cont_noid);
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
        mCollectList.stopRefreshing();
        mCollectList.stopLoadMore();
    }

    private void changeTextClr(int position) {
        for (int i = 1; i < 4; i++) {
            if (position == i) {
                textViews[i - 1].setSelected(true);
            } else {
                textViews[i - 1].setSelected(false);
            }
        }
    }

    private void initMember() {
        tvKeyword.setText("会员编号：");
        if (!isMember) {
            tvScreen.setText("筛选");
            editKeyword.setText("");
            keywords = null;
            WorkOrder.getInstance().clear();
            skill_id = null;
            tvWork.setText("");
            tvAddress.setText("");
            min_price = "";
            editMinMoney.setText("");
            max_price = "";
            editMaxMoney.setText("");
            min_price = "";
            min_suntotal = "";
            max_suntotal = "";
            editMinPrice.setText("");
            editMaxPrice.setText("");
            max_price = "";
            isMember = true;
        }
        isJob = false;
        isContract = false;
        linlayWork.setVisibility(View.GONE);
        vWork.setVisibility(View.GONE);
        linlayAddress.setVisibility(View.GONE);
        vAddress.setVisibility(View.GONE);
        tvTotalTitle.setVisibility(View.GONE);
        linlayTotal.setVisibility(View.GONE);
        linlaySkill.setVisibility(View.VISIBLE);
        vSkill.setVisibility(View.VISIBLE);
        vTotal.setVisibility(View.GONE);
        WorkOrder.getInstance().clear();
    }

    private void initJob() {
        tvKeyword.setText("关键词：");
        if (!isJob) {
            tvScreen.setText("筛选");
            editKeyword.setText("");
            keywords = null;
            WorkOrder.getInstance().clear();
            skill_id = null;
            tvWork.setText("");
            tvAddress.setText("");
            min_price = "";
            editMinMoney.setText("");
            max_price = "";
            editMaxMoney.setText("");
            min_price = "";
            min_suntotal = "";
            max_suntotal = "";
            editMinPrice.setText("");
            editMaxPrice.setText("");
            max_price = "";
            tvSkill.setText("");
            isJob = true;
        }
        isMember = false;
        isContract = false;
        linlayWork.setVisibility(View.VISIBLE);
        vWork.setVisibility(View.VISIBLE);
        linlayAddress.setVisibility(View.VISIBLE);
        vAddress.setVisibility(View.VISIBLE);
        tvTotalTitle.setVisibility(View.VISIBLE);
        linlayTotal.setVisibility(View.VISIBLE);
        linlaySkill.setVisibility(View.GONE);
        vSkill.setVisibility(View.GONE);
        vTotal.setVisibility(View.VISIBLE);
        WorkOrder.getInstance().clear();
    }

    private void initContract() {
        tvKeyword.setText("关键词：");
        linlayWork.setVisibility(View.VISIBLE);
        vWork.setVisibility(View.VISIBLE);
        linlayAddress.setVisibility(View.GONE);
        vAddress.setVisibility(View.GONE);
        tvTotalTitle.setVisibility(View.VISIBLE);
        linlayTotal.setVisibility(View.VISIBLE);
        linlaySkill.setVisibility(View.GONE);
        vTotal.setVisibility(View.VISIBLE);
        vSkill.setVisibility(View.GONE);
        WorkOrder.getInstance().clear();
        if (!isContract) {
            tvScreen.setText("筛选");
            editKeyword.setText("");
            keywords = null;
            WorkOrder.getInstance().clear();
            skill_id = null;
            tvWork.setText("");
            tvAddress.setText("");
            min_price = "";
            editMinMoney.setText("");
            max_price = "";
            editMaxMoney.setText("");
            min_price = "";
            min_suntotal = "";
            max_suntotal = "";
            editMinPrice.setText("");
            editMaxPrice.setText("");
            max_price = "";
            tvSkill.setText("");
            isContract = true;
        }
        isMember = false;
        isJob = false;
    }

    @Event({R.id.my_jo_back, R.id.my_jo_screen, R.id.my_collect_member, R.id.my_collect_jo, R.id.my_collect_contract, R.id.my_jo_work_click,
            R.id.my_collect_address_click, R.id.my_jo_sure, R.id.my_collect_clear, R.id.my_collect_skill_click})
    private void onViewClicked(View view) {

        switch (view.getId()) {
            case R.id.my_jo_back:
                finish();
                break;
            case R.id.my_jo_screen:
                if (!drawerLayout.isDrawerOpen(scrollView)) {
                    drawerLayout.openDrawer(scrollView);
                }
                break;
            case R.id.my_collect_member:
                position = 1;
                initMember();
                mCollectList.startRefreshing();
                break;
            case R.id.my_collect_jo:
                position = 2;
                initJob();
                mCollectList.startRefreshing();
                break;
            case R.id.my_collect_contract:
                position = 3;
                initContract();
                mCollectList.startRefreshing();
                break;
            case R.id.my_jo_work_click:

            case R.id.my_collect_skill_click:
                startActivity(SkillAty.class, null);
                break;
            case R.id.my_collect_address_click:
//                Bundle bundle = new Bundle();
//                bundle.putString("flag", "sel_area");
                startActivityForResult(AreaListAty.class, null, ADDRESS_ID_IN_SERCH);
                break;
            case R.id.my_jo_sure:

                code = editKeyword.getText().toString().trim();
                keywords = editKeyword.getText().toString().trim();
                String maxMoney = editMaxMoney.getText().toString().trim();
                String minMoney = editMinMoney.getText().toString().trim();
                String maxPrice = editMaxPrice.getText().toString().trim();
                String minPrice = editMinPrice.getText().toString().trim();
                max_price = maxMoney;
                min_price = minMoney;
                if (!TextUtils.isEmpty(maxMoney) && !TextUtils.isEmpty(minMoney)) {
                    if (Double.parseDouble(maxMoney) < Double.parseDouble(minMoney)) {
                        editMaxMoney.setText(minMoney);
                        editMinMoney.setText(maxMoney);
                        max_price = minMoney;
                        min_price = maxMoney;
                    }
                }
                min_suntotal = minPrice;
                max_suntotal = maxPrice;
                if (!TextUtils.isEmpty(maxPrice) && !TextUtils.isEmpty(minPrice)) {
                    if (Double.parseDouble(maxPrice) < Double.parseDouble(minPrice)) {
                        editMaxPrice.setText(minPrice);
                        editMinPrice.setText(maxPrice);
                        min_suntotal = maxPrice;
                        max_suntotal = minPrice;
                    }
                }
                if (position == 1) {
                    if (!TextUtils.isEmpty(code) || !TextUtils.isEmpty(min_suntotal) || !TextUtils.isEmpty(max_suntotal)
                            || !TextUtils.isEmpty(skill_id)) {
                        tvScreen.setText("已筛选");
                    } else {
                        tvScreen.setText("筛选");
                    }
                } else if (position == 2) {
                    if (!TextUtils.isEmpty(keywords) || !TextUtils.isEmpty(min_price) || !TextUtils.isEmpty(max_price)
                            || !TextUtils.isEmpty(skill_id) || !TextUtils.isEmpty(min_suntotal)
                            || !TextUtils.isEmpty(max_suntotal) || !TextUtils.isEmpty(tvAddress.getText().toString())) {
                        tvScreen.setText("已筛选");
                    } else {
                        tvScreen.setText("筛选");
                    }
                } else {
                    if (!TextUtils.isEmpty(keywords) || !TextUtils.isEmpty(min_price) || !TextUtils.isEmpty(max_price)
                            || !TextUtils.isEmpty(skill_id) || !TextUtils.isEmpty(min_suntotal)
                            || !TextUtils.isEmpty(max_suntotal)) {
                        tvScreen.setText("已筛选");
                    } else {
                        tvScreen.setText("筛选");
                    }
                }
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
                mCollectList.startRefreshing();
                break;
            case R.id.my_collect_clear:
                tvScreen.setText("筛选");
                editKeyword.setText("");
                keywords = null;
                WorkOrder.getInstance().clear();
                skill_id = null;
                tvWork.setText("");
                tvAddress.setText("");
                province_name = "";
                city_name = "";
                area_name = "";
                min_price = "";
                editMinMoney.setText("");
                max_price = "";
                editMaxMoney.setText("");
                min_price = "";
                min_suntotal = "";
                max_suntotal = "";
                editMinPrice.setText("");
                editMaxPrice.setText("");
                max_price = "";
                tvSkill.setText("");
                break;
        }
        changeTextClr(position);
        startTranslate(vTitleView, (Settings.displayWidth / 3) * (position - 1));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            return;
        }
        switch (requestCode) {
            case ADDRESS_ID_IN_SERCH:
                tvAddress.setText(data.getStringExtra("str"));
                String ress = data.getStringExtra("province_name") + "," +
                        data.getStringExtra("city_name") + "," + data.getStringExtra("name");
                province_name = data.getStringExtra("province_id");
                city_name = data.getStringExtra("city_id");
                area_name = data.getStringExtra("area_id");
                break;
        }
    }


    @Override
    public void onRefresh() {
        page = String.valueOf(p);
        if (position == 1)
            collect.labList(noid, page, code, min_suntotal, max_suntotal, skill_id, this);
        else if (position == 2) {
            collect.hireList(noid, page, keywords, skill_id, province_name, city_name, area_name, min_price, max_price
                    , min_suntotal, max_suntotal, this);
        } else {
            collect.contractList(noid, page, keywords, skill_id, min_suntotal, max_suntotal, min_price, max_price, this);
        }
    }

    @Override
    public void onLoadMore() {
        p++;
        page = String.valueOf(p);
        if (position == 1)
            collect.labList(noid, page, code, min_suntotal, max_suntotal, skill_id, this);
        else if (position == 2) {
            collect.hireList(noid, page, keywords, skill_id, province_name, city_name, area_name, min_price, max_price
                    , min_suntotal, max_suntotal, this);
        } else {
            collect.contractList(noid, page, keywords, skill_id, min_suntotal, max_suntotal, min_price, max_price, this);
        }
    }

    // 用户adapter
    public class MemberAdapter extends RecyclerView.Adapter<MemberAdapter.ViewHolder> {

        //用来加载图片
        ImageLoader mImageLoader;

        public MemberAdapter() {
            mImageLoader = new ImageLoader();
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.listitem_collect, parent, false);
            ViewHolder holder = new ViewHolder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.linlayDistancel.setVisibility(View.GONE);
            holder.linlayNum.setVisibility(View.GONE);
            holder.imgvSel.setVisibility(View.GONE);

            final Map<String, String> map = memberList.get(position);
            holder.tvMember.setText(map.get(MEMBER_NUMBER));
            holder.tvNickname.setText(map.get(NICKNAME));
//            Map<String, String> role = JSONUtils.parseKeyAndValueToMap(map.get("role"));
            holder.tvFree.setText(map.get("role_name"));
            holder.tvSex.setText(map.get(SEX));
            holder.tvPrice.setText("￥" + map.get(PRICE) + "/天");
            ArrayList<Map<String, String>> maps = JSONUtils.parseKeyAndValueToMapList(map.get(SKILL));
            ArrayList<String> skillList = new ArrayList<>();
            for (int i = 0; i < ListUtils.getSize(maps); i++) {
                Map<String, String> map1 = maps.get(i);
                skillList.add(map1.get("name"));

            }
            holder.tvSkill.setText(ListUtils.join(skillList));
//            holder.tvDistance.setText(map.get("distance"));
//            holder.tvNum.setText(map.get("cost_count"));
            if (TextUtils.equals(map.get("attestation"), "1")) {
                holder.tvAuthentication.setText("已认证");
            } else {
                holder.tvAuthentication.setText("未认证");
            }

            ImageLoader imageLoader = new ImageLoader();
            imageLoader.disPlay(holder.imgvHead, map.get("head"));
            String evaluate_score = map.get(CREDIT_RATING);
            if (TextUtils.equals(evaluate_score, "0")) {
                holder.imgvCredit.setVisibility(View.VISIBLE);
                holder.imgvCredit.setImageResource(R.drawable.icon_starnone0);
            } else if (TextUtils.equals(evaluate_score, "1")) {
                holder.imgvCredit.setImageResource(R.drawable.icon_1star);
            } else if (TextUtils.equals(evaluate_score, "2")) {
                holder.imgvCredit.setImageResource(R.drawable.icon_2star);
            } else if (TextUtils.equals(evaluate_score, "3")) {
                holder.imgvCredit.setImageResource(R.drawable.icon_3star);
            } else if (TextUtils.equals(evaluate_score, "4")) {
                holder.imgvCredit.setImageResource(R.drawable.icon_4star);
            } else if (TextUtils.equals(evaluate_score, "5")) {
                holder.imgvCredit.setImageResource(R.drawable.icon_5star);
            }
            holder.linlayCollect.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    bundle.putString("code", map.get("noid"));
                    startActivity(MemberDetailAty.class, bundle);
                }
            });


        }


        @Override
        public int getItemCount() {
            return ListUtils.getSize(memberList);
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            @ViewInject(R.id.select_img)
            ImageView imgvSel;
            @ViewInject(R.id.header_cir)
            CircularImageView imgvHead;
            @ViewInject(R.id.authentication_tv)
            TextView tvAuthentication;
            @ViewInject(R.id.member_number_tv)
            TextView tvMember;
            @ViewInject(R.id.nickname_tv)
            TextView tvNickname;
            @ViewInject(R.id.freeman_tv)
            TextView tvFree;
            @ViewInject(R.id.sex_tv)
            TextView tvSex;
            @ViewInject(R.id.credit_rating_img)
            ImageView imgvCredit;
            @ViewInject(R.id.price_tv)
            TextView tvPrice;
            @ViewInject(R.id.skill_tv)
            TextView tvSkill;
            @ViewInject(R.id.list_collect_distance)
            TextView tvDistance;
            @ViewInject(R.id.list_collect_num)
            TextView tvNum;
            @ViewInject(R.id.list_collect_distance_content)
            LinearLayout linlayDistancel;
            @ViewInject(R.id.list_collect_num_content)
            LinearLayout linlayNum;
            @ViewInject(R.id.list_collect_click)
            LinearLayout linlayCollect;

            public ViewHolder(View itemView) {
                super(itemView);
                x.view().inject(this, itemView);
                AutoUtils.auto(itemView);
            }
        }
    }

    // 招工单adapter
    public class RecruitJOAdapter extends RecyclerView.Adapter<RecruitJOAdapter.ViewHolder> {
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.listitem_query_result, parent, false);
            ViewHolder viewHolder = new ViewHolder(view);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, final int position) {
            holder.linlayContent.setPadding(0, AutoUtils.getPercentWidthSize(20), 0, 0);
            final Map<String, String> map = jobList.get(position);
            ImageLoader imageLoader = new ImageLoader();
            imageLoader.disPlay(holder.imgvheader, map.get("head"));
            holder.tvMember.setText(map.get("noid"));
            holder.tvNickname.setText(map.get("nickname"));
            switch (map.get("role")) {
                case "1":
                    holder.tvFreeman.setText("自由人");
                    break;
                case "2":
                    holder.tvFreeman.setText(map.get("role_name"));
                    break;
            }
            holder.tvSex.setText(map.get("sex_name"));
            String evaluate_score = map.get("evaluate_score");
            if (TextUtils.equals(evaluate_score, "0")) {
                holder.imgvCreditRating.setVisibility(View.VISIBLE);
                holder.imgvCreditRating.setImageResource(R.drawable.icon_starnone0);
            } else if (TextUtils.equals(evaluate_score, "1")) {
                holder.imgvCreditRating.setImageResource(R.drawable.icon_1star);
            } else if (TextUtils.equals(evaluate_score, "2")) {
                holder.imgvCreditRating.setImageResource(R.drawable.icon_2star);
            } else if (TextUtils.equals(evaluate_score, "3")) {
                holder.imgvCreditRating.setImageResource(R.drawable.icon_3star);
            } else if (TextUtils.equals(evaluate_score, "4")) {
                holder.imgvCreditRating.setImageResource(R.drawable.icon_4star);
            } else if (TextUtils.equals(evaluate_score, "5")) {
                holder.imgvCreditRating.setImageResource(R.drawable.icon_5star);
            }
            holder.tvSkill.setText(ListUtils.join(JsonArryToList.strList(map.get("skill"))));
            holder.tvStartDate.setText(map.get("contract_starttime"));
            holder.tvEndDate.setText(map.get("contract_endtime"));
            holder.tvTotal.setText(map.get("amount"));
            if (map.get("unit_name").contains("每"))
                holder.tvPrice.setText("￥" + map.get("subtotal") + map.get("unit_name").replace("每", "/"));
            else
                holder.tvPrice.setText("￥" + map.get("subtotal") + "/" + map.get("unit_name"));
            holder.tvPayment.setText(map.get("settle_type_name"));
            holder.tvAddress.setText(map.get("ress"));
            holder.tvNum.setText(map.get("photos_count") + "张");
            if (TextUtils.equals(map.get("photos_count"), "0")) {
                holder.imgvCover.setImageResource(R.drawable.icon_photo_none);
            } else {
                imageLoader.disPlay(holder.imgvCover, map.get("cover"));
            }
            if (TextUtils.equals(map.get("attestation"), "1")) {
                holder.tvAuthentication.setText("已认证");
            } else {
                holder.tvAuthentication.setText("未认证");
            }

            holder.myJobInformationDetail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Bundle bundle = new Bundle();
                    bundle.putString("flag", "wzw1");
                    bundle.putString("lab_noid", application.getUserInfo().get("noid"));  //劳方noid
                    bundle.putString("hire_noid", jobList.get(position).get("hire_noid")); //招工单noid
                    bundle.putString("noid", jobList.get(position).get("noid"));  //对方编号
                    startActivity(MyJobOrderDetailAty.class, bundle);
                }
            });
            holder.linlayMember.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    bundle.putString("flag", "wzw1");
                    bundle.putString("item", "1");
                    bundle.putString("lab_noid", application.getUserInfo().get("noid"));  //劳方noid
                    bundle.putString("hire_noid", jobList.get(position).get("hire_noid")); //招工单noid
                    bundle.putString("noid", jobList.get(position).get("noid"));  //对方编号
                    bundle.putString("status", map.get("status"));   //签约状态
                    bundle.putString("coor_status", map.get("coor_status"));
                    bundle.putString("coor_diff", map.get("coor_diff")); //招工单是否被修改
                    startActivity(MyJobOrderDetailAty.class, bundle);
                }
            });
            if (map.get("status").equals("2")) {     //招工中
                holder.imgvStatus.setVisibility(View.VISIBLE);
                if (map.get("coor_diff").equals("0")) {  //左上角：未修改
                    holder.imgvStatus.setImageDrawable(getResources().getDrawable(R.drawable.icon_unmodify));
                } else {     //左上角：已修改
                    holder.imgvStatus.setImageDrawable(getResources().getDrawable(R.drawable.icon_modify));
                }

                if (map.get("coor_status").equals("1")) {    //同意招工单
                    holder.fBtnUp.setVisibility(View.VISIBLE);
                    holder.modifyBtn.setVisibility(View.GONE);
                    holder.btnLookEdit.setVisibility(View.GONE);
                    holder.fBtnUp.setText("取消接单");
                    holder.fBtnUp.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            //取消接单接口
                            showProgressDialog();
                            hire.cancelAccept(jobList.get(position).get("hire_noid"), application.getUserInfo().get("noid"), MyCollectAty.this);
                        }
                    });
                } else if (map.get("coor_status").equals("2")) {  //修改过招工单等待回复
                    holder.fBtnUp.setVisibility(View.VISIBLE);
                    holder.modifyBtn.setVisibility(View.VISIBLE);
                    holder.btnLookEdit.setVisibility(View.VISIBLE);
                    holder.btnLookEdit.setText("查看修改明细");
                    holder.modifyBtn.setText("取消接单");
                    holder.fBtnUp.setText("同意签约");
                    holder.btnLookEdit.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Bundle bundle = new Bundle();
                            bundle.putString("flag", "wzw1");    //搜工作--》查询招工信息--》确认--》在item上点修改
                            bundle.putString("hire_noid", jobList.get(position).get("hire_noid"));
                            bundle.putString("lab_noid", application.getUserInfo().get("noid"));
                            startActivity(ModifyDetailsAty.class, bundle);
                        }
                    });
                    holder.modifyBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            showProgressDialog();
                            hire.cancelAccept(jobList.get(position).get("hire_noid"), application.getUserInfo().get("noid"), MyCollectAty.this);
                        }
                    });
                    holder.fBtnUp.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            showDialog("", "确认要接该招工单吗？", 0, "确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    showProgressDialog();
                                    noid = application.getUserInfo().get("noid");
                                    hire_noid = map.get("hire_noid");
                                    cap_noid = map.get("noid");
                                    hire.labReplyAccept(noid, hire_noid, cap_noid, MyCollectAty.this);
                                }
                            }, "取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            });
                        }
                    });
                } else if (map.get("coor_status").equals("0")) {  //未操作
                    holder.btnLookEdit.setVisibility(View.GONE);
                    holder.fBtnUp.setVisibility(View.VISIBLE);
                    holder.fBtnUp.setText("接单");
                    holder.modifyBtn.setText("修改");
                    holder.modifyBtn.setVisibility(View.VISIBLE);
                    holder.fBtnUp.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            showDialog("", "确认要接该招工单吗？", 0, "确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    showProgressDialog();
                                    noid = application.getUserInfo().get("noid");
                                    hire_noid = map.get("hire_noid");
                                    cap_noid = map.get("noid");
                                    hire.labReplyAccept(noid, hire_noid, cap_noid, MyCollectAty.this);
                                }
                            }, "取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            });

                        }
                    });
                    holder.modifyBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Bundle bundle = new Bundle();
                            bundle.putString("flag", "wzw1");    //搜工作--》查询招工信息--》确认--》在item上点修改
                            bundle.putString("hire_noid", jobList.get(position).get("hire_noid"));
                            bundle.putString("lab_noid", application.getUserInfo().get("noid"));
                            startActivity(ModifyRecruitmentInformationAty.class, bundle);
                        }
                    });
                }
            } else if (map.get("status").equals("3")) {   //已签约
                holder.imgvQianyue.setVisibility(View.VISIBLE);
                holder.fBtnUp.setVisibility(View.GONE);
                holder.modifyBtn.setVisibility(View.GONE);
                holder.btnLookEdit.setVisibility(View.GONE);
                holder.imgvStatus.setVisibility(View.GONE);
                if (TextUtils.equals(map.get("coor_status"), "5")) {
                    holder.imgvQianyue.setImageResource(R.drawable.icon_signed_me);
                } else {
                    holder.imgvQianyue.setImageResource(R.drawable.icon_signed_other);
                }
            }
        }

        @Override
        public int getItemCount() {
            return ListUtils.getSize(jobList);
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            @ViewInject(R.id.list_q_r_look_edit)
            Button btnLookEdit;
            @ViewInject(R.id.list_q_r_qianyue_img)
            ImageView imgvQianyue;
            @ViewInject(R.id.coor_status_img)
            ImageView imgvStatus;
            @ViewInject(R.id.list_q_r_content)
            LinearLayout linlayContent;
            @ViewInject(R.id.list_q_r_member_lay)
            RelativeLayout linlayMember;
            @ViewInject(R.id.job_information_detail_ll)
            LinearLayout myJobInformationDetail;
            @ViewInject(R.id.release_fbtn)
            FButton fBtnUp;
            @ViewInject(R.id.header_cir)
            CircularImageView imgvheader;
            @ViewInject(R.id.authentication_tv)
            TextView tvAuthentication;
            @ViewInject(R.id.member_number_tv)
            TextView tvMember;
            @ViewInject(R.id.nickname_tv)
            TextView tvNickname;
            @ViewInject(R.id.freeman_tv)
            TextView tvFreeman;
            @ViewInject(R.id.sex_tv)
            TextView tvSex;
            @ViewInject(R.id.credit_rating_img)
            ImageView imgvCreditRating;
            @ViewInject(R.id.list_q_r_skill)
            TextView tvSkill;
            @ViewInject(R.id.list_q_r_start_date)
            TextView tvStartDate;
            @ViewInject(R.id.list_q_r_end_date)
            TextView tvEndDate;
            @ViewInject(R.id.list_q_r_total)
            TextView tvTotal;
            @ViewInject(R.id.list_q_r_price)
            TextView tvPrice;
            @ViewInject(R.id.list_q_r_payment)
            TextView tvPayment;
            @ViewInject(R.id.list_q_r_address)
            TextView tvAddress;
            @ViewInject(R.id.list_q_r_photo_num)
            TextView tvNum;
            @ViewInject(R.id.list_q_r_photo)
            ImageView imgvCover;
            @ViewInject(R.id.modify_btn)
            Button modifyBtn;

            public ViewHolder(View itemView) {
                super(itemView);
                x.view().inject(this, itemView);
                AutoUtils.auto(itemView);
            }
        }
    }

    private class ContractAdapter extends RecyclerView.Adapter<ContractAdapter.ViewHodler> {


        @Override
        public ViewHodler onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.listitem_my_contract, parent, false);
            return new ViewHodler(view);
        }

        @Override
        public void onBindViewHolder(ViewHodler holder, int position) {
            final Map<String, String> map = contractList.get(position);
            holder.tvContractId.setText(map.get("contract_noid"));
            holder.tvState.setText(map.get("status_name"));
            holder.tvTotal.setText("￥" + map.get("amount"));
            holder.tvStartDate.setText(map.get("contract_starttime"));
            holder.tvEndDate.setText(map.get("contract_endtime"));
            holder.tvPayment.setText(map.get("settle_type_name"));
//            if (position == ListUtils.getSize(contractList) - 1) {
//                holder.vLastView.setVisibility(View.VISIBLE);
//            } else {
//                holder.vLastView.setVisibility(View.GONE);
//            }

            holder.tvWork.setText(ListUtils.join(JsonArryToList.strList(map.get("skill"))));
            String unit_name = map.get("unit_name");
            if (unit_name.contains("每")) {
                String replace = unit_name.replace("每", "/");
                holder.tvPrice.setText("￥" + map.get("subtotal") + replace);
            } else {
                holder.tvPrice.setText("￥" + map.get("subtotal") + "/" + unit_name);
            }
            final int status = Integer.parseInt(map.get("status"));
            holder.fbtnPay.setEnabled(true);
            holder.btnLook.setEnabled(true);
            holder.fbtnPay.setButtonColor(getResources().getColor(R.color.clr_main));
            holder.btnLook.setBackgroundResource(R.drawable.btn_save);
            if (TextUtils.equals(noid, map.get("cap_noid"))) {
                String evaluate_score = map.get("lab_evaluate_score");
//           LogUtil.e(DateUtils.timeslashData(1493740830+""));
//            LogUtil.e(DateUtils.dataShort("2017-05-03"));
//            LogUtil.e(DateTool.getTwoDay("2017-05-03","2017-05-01"));
                if (TextUtils.equals(evaluate_score, "0")) {
                    holder.imgvCreditRating.setImageResource(R.drawable.icon_starnone0);
                } else if (TextUtils.equals(evaluate_score, "1")) {
                    holder.imgvCreditRating.setImageResource(R.drawable.icon_1star);
                } else if (TextUtils.equals(evaluate_score, "2")) {
                    holder.imgvCreditRating.setImageResource(R.drawable.icon_2star);
                } else if (TextUtils.equals(evaluate_score, "3")) {
                    holder.imgvCreditRating.setImageResource(R.drawable.icon_3star);
                } else if (TextUtils.equals(evaluate_score, "4")) {
                    holder.imgvCreditRating.setImageResource(R.drawable.icon_4star);
                } else if (TextUtils.equals(evaluate_score, "5")) {
                    holder.imgvCreditRating.setImageResource(R.drawable.icon_5star);
                }
                if (TextUtils.equals(map.get("cap_attestation"), "1")) {
                    holder.tvAuthentication.setText("已认证");
                } else {
                    holder.tvAuthentication.setText("未认证");
                }
                ImageLoader imageLoader = new ImageLoader();
                imageLoader.disPlay(holder.imgvHead, map.get("lab_head"));
                holder.tvMemberId.setText(map.get("lab_noid"));
                holder.tvNickname.setText(map.get("lab_nickname"));
                holder.linlayNoid.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Bundle bundle = new Bundle();
                        bundle.putString("code", map.get("lab_noid"));
                        if (status == INVALID || status == EXPIRE) {

                        } else {
                            bundle.putString("flag", "contract");
                        }
//                    bundle.putString("flag", "collect");
                        startActivity(MemberDetailAty.class, bundle);
                    }
                });
                /*********************************** 资方********************************************/
                holder.imgvAgree.setVisibility(View.GONE);
                holder.imgvStatus.setVisibility(View.GONE);
                holder.linlaySettle.setVisibility(View.GONE);
                holder.linlayTuibu.setVisibility(View.GONE);
                switch (status) {
                    case INVALID: // 待生效
                        holder.linlayBtn.setVisibility(View.VISIBLE);
                        holder.btnLook.setVisibility(View.GONE);
                        holder.btnRrlieve.setVisibility(View.VISIBLE);
                        holder.fbtnPay.setVisibility(View.VISIBLE);
                        holder.btnRrlieve.setText("解除合同");
                        holder.fbtnPay.setText("付款");
                        holder.imgvStatus.setVisibility(View.GONE);
                        holder.btnRrlieve.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                showDialog("", "确定要解除该合同么？", "确定解除", "取消", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Bundle bundle = new Bundle();
                                        bundle.putString("contract_noid", map.get("contract_noid"));
                                        bundle.putString("status", "cap");
                                        startActivity(RelieveContAty.class, bundle);
                                    }
                                }, null);
                            }
                        });
                        holder.fbtnPay.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Bundle bundle = new Bundle();
                                bundle.putString("total", map.get("amount"));
                                bundle.putString("contract_noid", map.get("contract_noid"));
                                startActivity(PayAty.class, bundle);
                            }
                        });

                        break;
                    case PROGRESS:// 执行中
                        holder.imgvStatus.setVisibility(View.GONE);
                        if (TextUtils.equals(map.get("requested_noid"), application.getUserInfo().get("noid"))) {
                            if (TextUtils.equals(map.get("requested_type"), "3")) {
                                switch (Integer.parseInt(map.get("requested_reply"))) {
                                    case 0:
                                        holder.imgvAgree.setVisibility(View.VISIBLE);
                                        holder.imgvAgree.setImageResource(R.drawable.icon_contract_agree);
                                        holder.linlayBtn.setVisibility(View.VISIBLE);
                                        holder.btnLook.setVisibility(View.VISIBLE);
                                        holder.btnLook.setText("查看修改明细");
                                        holder.btnRrlieve.setVisibility(View.VISIBLE);
                                        holder.btnRrlieve.setText("解除合同");
                                        holder.fbtnPay.setVisibility(View.VISIBLE);
                                        holder.fbtnPay.setText("结算");
                                        holder.btnLook.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                Bundle bundle = new Bundle();
                                                bundle.putString("contract_noid", map.get("contract_noid"));
                                                bundle.putString("tag", "cap");
                                                startActivity(LookContractInfo.class, bundle);
                                            }
                                        });
                                        holder.btnRrlieve.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                showDialog("", "确定要解除该合同么？", "确定解除", "取消", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        Bundle bundle = new Bundle();
                                                        bundle.putString("contract_noid", map.get("contract_noid"));
                                                        bundle.putString("status", "cap");
                                                        bundle.putString("flag", "progress");
                                                        startActivity(RelieveContAty.class, bundle);
                                                    }
                                                }, null);
                                            }
                                        });
                                        holder.fbtnPay.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                showDialog("", "确定要进行结算么？", "确认结算", "取消", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        contract.toUnsettle(map.get("contract_noid"), application.getUserInfo().get("noid"), MyCollectAty.this);
                                                        Bundle bundle = new Bundle();
                                                        bundle.putString("contract_noid", map.get("contract_noid"));
                                                        bundle.putString("money", map.get("amount"));
                                                        startActivity(SettlementAty.class, bundle);
                                                    }
                                                }, null);
                                            }
                                        });
                                        break;
                                    case 1:
                                    case 2:
                                        holder.imgvAgree.setVisibility(View.GONE);
                                        holder.linlayBtn.setVisibility(View.VISIBLE);
                                        holder.btnLook.setVisibility(View.VISIBLE);
                                        holder.btnLook.setText("解除合同");
                                        holder.btnRrlieve.setVisibility(View.VISIBLE);
                                        holder.btnRrlieve.setText("结算");
                                        holder.fbtnPay.setVisibility(View.VISIBLE);
                                        holder.fbtnPay.setText("修改合同");
                                        holder.btnLook.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                showDialog("", "确定要解除该合同么？", "确定解除", "取消", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        Bundle bundle = new Bundle();
                                                        bundle.putString("contract_noid", map.get("contract_noid"));
                                                        bundle.putString("status", "cap");
                                                        bundle.putString("flag", "progress");
                                                        startActivity(RelieveContAty.class, bundle);
                                                    }
                                                }, null);
                                            }
                                        });
                                        holder.btnRrlieve.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                showDialog("", "确定要进行结算么？", "确认结算", "取消", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        contract.toUnsettle(map.get("contract_noid"), application.getUserInfo().get("noid"), MyCollectAty.this);
                                                        Bundle bundle = new Bundle();
                                                        bundle.putString("contract_noid", map.get("contract_noid"));
                                                        bundle.putString("money", map.get("amount"));
                                                        startActivity(SettlementAty.class, bundle);
                                                    }
                                                }, null);
                                            }
                                        });
                                        holder.fbtnPay.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                Bundle bundle = new Bundle();
                                                bundle.putString("contract_noid", map.get("contract_noid"));
                                                bundle.putString("tag", "cap");
                                                startActivity(EditContractAty.class, bundle);
                                            }
                                        });
                                        break;
                                }
                            } else if (TextUtils.equals(map.get("requested_type"), "1")) {
                                holder.imgvAgree.setVisibility(View.GONE);
                                holder.linlayBtn.setVisibility(View.VISIBLE);
                                holder.btnLook.setVisibility(View.VISIBLE);
                                holder.btnLook.setText("解除合同");
                                holder.btnRrlieve.setVisibility(View.VISIBLE);
                                holder.btnRrlieve.setText("结算");
                                holder.fbtnPay.setVisibility(View.VISIBLE);
                                holder.fbtnPay.setText("修改合同");
                                holder.btnLook.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        showDialog("", "确定要解除该合同么？", "确定解除", "取消", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                Bundle bundle = new Bundle();
                                                bundle.putString("contract_noid", map.get("contract_noid"));
                                                bundle.putString("status", "cap");
                                                bundle.putString("flag", "progress");
                                                startActivity(RelieveContAty.class, bundle);
                                            }
                                        }, null);
                                    }
                                });
                                holder.btnRrlieve.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        showDialog("", "确定要进行结算么？", "确认结算", "取消", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                contract.toUnsettle(map.get("contract_noid"), application.getUserInfo().get("noid"), MyCollectAty.this);
                                                Bundle bundle = new Bundle();
                                                bundle.putString("contract_noid", map.get("contract_noid"));
                                                bundle.putString("money", map.get("amount"));
                                                startActivity(SettlementAty.class, bundle);
                                            }
                                        }, null);
                                    }
                                });
                                holder.fbtnPay.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Bundle bundle = new Bundle();
                                        bundle.putString("contract_noid", map.get("contract_noid"));
                                        bundle.putString("tag", "cap");
                                        startActivity(EditContractAty.class, bundle);
                                    }
                                });
                            }
                        } else if (TextUtils.isEmpty(map.get("requested_noid"))) {
                            holder.imgvAgree.setVisibility(View.GONE);
                            holder.linlayBtn.setVisibility(View.VISIBLE);
                            holder.btnLook.setVisibility(View.VISIBLE);
                            holder.btnLook.setText("解除合同");
                            holder.btnRrlieve.setVisibility(View.VISIBLE);
                            holder.btnRrlieve.setText("结算");
                            holder.fbtnPay.setVisibility(View.VISIBLE);
                            holder.fbtnPay.setText("修改合同");
                            holder.btnLook.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    showDialog("", "确定要解除该合同么？", "确定解除", "取消", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            Bundle bundle = new Bundle();
                                            bundle.putString("contract_noid", map.get("contract_noid"));
                                            bundle.putString("status", "cap");
                                            bundle.putString("flag", "progress");
                                            startActivity(RelieveContAty.class, bundle);
                                        }
                                    }, null);
                                }
                            });
                            holder.btnRrlieve.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    showDialog("", "确定要进行结算么？", "确认结算", "取消", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            contract.toUnsettle(map.get("contract_noid"), application.getUserInfo().get("noid"), MyCollectAty.this);
                                            Bundle bundle = new Bundle();
                                            bundle.putString("contract_noid", map.get("contract_noid"));
                                            bundle.putString("money", map.get("amount"));
                                            startActivity(SettlementAty.class, bundle);
                                        }
                                    }, null);
                                }
                            });
                            holder.fbtnPay.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Bundle bundle = new Bundle();
                                    bundle.putString("contract_noid", map.get("contract_noid"));
                                    bundle.putString("tag", "cap");
                                    startActivity(EditContractAty.class, bundle);
                                }
                            });
                        } else {
                            if (TextUtils.equals(map.get("requested_type"), "3")) {
                                switch (Integer.parseInt(map.get("requested_reply"))) {
                                    case 0:
                                        holder.imgvAgree.setVisibility(View.VISIBLE);
                                        holder.imgvAgree.setImageResource(R.drawable.icon_contract_disagree);
                                        holder.linlayBtn.setVisibility(View.VISIBLE);
                                        holder.btnLook.setVisibility(View.VISIBLE);
                                        holder.btnLook.setText("查看修改明细");
                                        holder.btnRrlieve.setVisibility(View.VISIBLE);
                                        holder.btnRrlieve.setText("解除合同");
                                        holder.fbtnPay.setVisibility(View.VISIBLE);
                                        holder.fbtnPay.setText("结算");
                                        holder.btnLook.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                Bundle bundle = new Bundle();
                                                bundle.putString("contract_noid", map.get("contract_noid"));
                                                bundle.putString("tag", "cap");
                                                startActivity(LookContractInfo.class, bundle);
                                            }
                                        });
                                        holder.btnRrlieve.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                showDialog("", "确定要解除该合同么？", "确定解除", "取消", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        Bundle bundle = new Bundle();
                                                        bundle.putString("contract_noid", map.get("contract_noid"));
                                                        bundle.putString("status", "cap");
                                                        bundle.putString("flag", "progress");
                                                        startActivity(RelieveContAty.class, bundle);
                                                    }
                                                }, null);
                                            }
                                        });
                                        holder.fbtnPay.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                showDialog("", "确定要进行结算么？", "确认结算", "取消", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        contract.toUnsettle(map.get("contract_noid"), application.getUserInfo().get("noid"), MyCollectAty.this);
                                                        Bundle bundle = new Bundle();
                                                        bundle.putString("contract_noid", map.get("contract_noid"));
                                                        bundle.putString("money", map.get("amount"));
                                                        startActivity(SettlementAty.class, bundle);
                                                    }
                                                }, null);
                                            }
                                        });
                                        break;
                                    case 1:
                                    case 2:
                                        holder.imgvAgree.setVisibility(View.GONE);
                                        holder.linlayBtn.setVisibility(View.VISIBLE);
                                        holder.btnLook.setVisibility(View.VISIBLE);
                                        holder.btnLook.setText("解除合同");
                                        holder.btnRrlieve.setVisibility(View.VISIBLE);
                                        holder.btnRrlieve.setText("结算");
                                        holder.fbtnPay.setVisibility(View.VISIBLE);
                                        holder.fbtnPay.setText("修改合同");
                                        holder.btnLook.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                showDialog("", "确定要解除该合同么？", "确定解除", "取消", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        Bundle bundle = new Bundle();
                                                        bundle.putString("contract_noid", map.get("contract_noid"));
                                                        bundle.putString("status", "cap");
                                                        bundle.putString("flag", "progress");
                                                        startActivity(RelieveContAty.class, bundle);
                                                    }
                                                }, null);
                                            }
                                        });
                                        holder.btnRrlieve.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                showDialog("", "确定要进行结算么？", "确认结算", "取消", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        contract.toUnsettle(map.get("contract_noid"), application.getUserInfo().get("noid"), MyCollectAty.this);
                                                        Bundle bundle = new Bundle();
                                                        bundle.putString("contract_noid", map.get("contract_noid"));
                                                        bundle.putString("money", map.get("amount"));
                                                        startActivity(SettlementAty.class, bundle);
                                                    }
                                                }, null);
                                            }
                                        });
                                        holder.fbtnPay.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                Bundle bundle = new Bundle();
                                                bundle.putString("contract_noid", map.get("contract_noid"));
                                                bundle.putString("tag", "cap");
                                                startActivity(EditContractAty.class, bundle);
                                            }
                                        });
                                        break;
                                }
                            } else if (TextUtils.equals(map.get("requested_type"), "1")) {
                                holder.imgvAgree.setVisibility(View.GONE);
                                holder.linlayBtn.setVisibility(View.VISIBLE);
                                holder.btnLook.setVisibility(View.VISIBLE);
                                holder.btnLook.setText("解除合同");
                                holder.btnRrlieve.setVisibility(View.VISIBLE);
                                holder.btnRrlieve.setText("结算");
                                holder.fbtnPay.setVisibility(View.VISIBLE);
                                holder.fbtnPay.setText("修改合同");
                                holder.btnLook.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        showDialog("", "确定要解除该合同么？", "确定解除", "取消", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                Bundle bundle = new Bundle();
                                                bundle.putString("contract_noid", map.get("contract_noid"));
                                                bundle.putString("status", "cap");
                                                bundle.putString("flag", "progress");
                                                startActivity(RelieveContAty.class, bundle);
                                            }
                                        }, null);
                                    }
                                });
                                holder.btnRrlieve.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        showDialog("", "确定要进行结算么？", "确认结算", "取消", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                contract.toUnsettle(map.get("contract_noid"), application.getUserInfo().get("noid"), MyCollectAty.this);
                                                Bundle bundle = new Bundle();
                                                bundle.putString("contract_noid", map.get("contract_noid"));
                                                bundle.putString("money", map.get("amount"));
                                                startActivity(SettlementAty.class, bundle);
                                            }
                                        }, null);
                                    }
                                });
                                holder.fbtnPay.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Bundle bundle = new Bundle();
                                        bundle.putString("contract_noid", map.get("contract_noid"));
                                        bundle.putString("tag", "cap");
                                        startActivity(EditContractAty.class, bundle);
                                    }
                                });
                            }
                        }
                        break;
//                    case SETTLE:// 已结算未评价
//                        holder.btnLook.setText("退补款");
//                        holder.btnRrlieve.setText("评价");
//                        holder.fbtnPay.setText("继续签约");
//                        break;
//                    case SETTLEED:// 已结算已评价
//                        holder.btnLook.setText("退补款");
//                        holder.btnRrlieve.setText("修改评价");
//                        holder.fbtnPay.setText("继续签约");
//                        break;

                    case ISSUE:// 纠纷
                        holder.linlaySettle.setVisibility(View.VISIBLE);
                        holder.tvSettleName.setText("纠纷调解金额：");
                        holder.tvSettle.setText("￥" + map.get("issue_amount"));
                        if (map.get("issue_amount").contains("-")) {
                            holder.tvSettle.setTextColor(Color.parseColor("#fb4a4a"));
                        } else {
                            holder.tvSettle.setTextColor(getResources().getColor(R.color.clr_main));
                        }
                        if (TextUtils.equals(map.get("adequacy_status"), "0")) {
                            holder.linlayTuibu.setVisibility(View.GONE);
                        } else {
                            holder.linlayTuibu.setVisibility(View.VISIBLE);
                            holder.tvTuibuName.setText("结算金额：");
                            if (map.get("adequacy_amount").contains("-")) {
                                holder.tvTuibu.setText(map.get("adequacy_amount").replace("-", "-￥"));
                                holder.tvTuibu.setTextColor(Color.parseColor("#fb4a4a"));
                            } else {
                                holder.tvTuibu.setText("￥" + map.get("adequacy_amount"));
                                holder.tvTuibu.setTextColor(getResources().getColor(R.color.clr_main));
                            }
                        }
                        holder.btnLook.setEnabled(true);
                        holder.fbtnPay.setEnabled(true);
                        holder.linlayBtn.setVisibility(View.VISIBLE);
                        holder.btnLook.setVisibility(View.VISIBLE);
                        holder.btnLook.setText("拒绝调解方案");
                        holder.btnRrlieve.setVisibility(View.GONE);
                        holder.fbtnPay.setVisibility(View.VISIBLE);
                        holder.fbtnPay.setText("同意调解方案");
                        holder.btnLook.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                showDialog("", "确定要拒绝该调解方案么？", "拒绝", "取消", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Bundle bundle = new Bundle();
                                        bundle.putString("flag", "mediate");
                                        bundle.putString("contract_noid", map.get("contract_noid"));
                                        bundle.putString("issue_id", map.get("issue_id"));
                                        startActivity(DisagreePayAty.class, bundle);
                                    }
                                }, null);
                            }
                        });
                        holder.fbtnPay.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                showDialog("", "确定要同意该调解方案么？", "同意", "取消", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        showProgressDialog();
                                        cont_noid = map.get("contract_noid");
                                        payStutas = "dispute";
                                        issue_id = map.get("issue_id");
                                        contract.isAdequacyAmount(map.get("contract_noid"), noid,
                                                map.get("requested_info"), MyCollectAty.this);
                                    }
                                }, null);
                            }
                        });
                        if (TextUtils.equals(map.get("issue_cap_reply"), "1")) {
                            holder.btnLook.setEnabled(false);
                            holder.fbtnPay.setEnabled(false);
                            holder.fbtnPay.setText("已同意调解方案");
                            holder.btnLook.setText("拒绝调解方案");
                            holder.btnLook.setBackgroundDrawable(getResources().getDrawable(R.drawable.btn_save_enable));
                            holder.btnLook.setTextColor(Color.WHITE);
                            holder.fbtnPay.setButtonColor(Color.parseColor("#999999"));
                        } else if (TextUtils.equals(map.get("issue_cap_reply"), "2")) {
                            holder.btnLook.setEnabled(false);
                            holder.fbtnPay.setEnabled(false);
                            holder.btnLook.setBackgroundDrawable(getResources().getDrawable(R.drawable.btn_save_enable));
                            holder.btnLook.setTextColor(Color.WHITE);
                            holder.btnLook.setText("已拒绝调解方案");
                            holder.fbtnPay.setText("同意调解方案");
                            holder.fbtnPay.setButtonColor(Color.parseColor("#999999"));
                        }
                        break;
                    case EXPIRE:// 待生效已过期
                        holder.linlayBtn.setVisibility(View.VISIBLE);
                        holder.btnLook.setVisibility(View.GONE);
                        holder.btnRrlieve.setVisibility(View.GONE);
                        holder.fbtnPay.setVisibility(View.VISIBLE);
                        holder.fbtnPay.setText("重新签约");
                        holder.imgvStatus.setVisibility(View.GONE);
                        holder.fbtnPay.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Bundle bundle = new Bundle();
                                bundle.putString("flag", "keep");
                                bundle.putString("lab_noid", map.get("noid"));
                                startActivity(NewJobOrderAty.class, bundle);
                            }
                        });

                        break;
                    case STAY:// 待解除(待生效) 1 - 8
                        holder.imgvStatus.setVisibility(View.VISIBLE);
                        holder.btnLook.setVisibility(View.GONE);
                        holder.linlayBtn.setVisibility(View.VISIBLE);
                        if (TextUtils.equals(map.get("requested_noid"), application.getUserInfo().get("noid"))) {
                            holder.imgvStatus.setImageResource(R.drawable.icon_release_wait);
                            holder.imgvStatus.setVisibility(View.VISIBLE);
                            holder.btnRrlieve.setVisibility(View.GONE);
                            holder.fbtnPay.setVisibility(View.GONE);
                            holder.linlayBtn.setVisibility(View.GONE);
                        } else {
                            holder.linlayBtn.setVisibility(View.VISIBLE);
                            holder.imgvStatus.setVisibility(View.VISIBLE);
                            holder.imgvStatus.setImageResource(R.drawable.icon_release);
                            holder.btnRrlieve.setVisibility(View.VISIBLE);
                            holder.btnRrlieve.setText("同意解除");
                            holder.fbtnPay.setText("不同意解除");
                        }
                        holder.btnRrlieve.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                showDialog("", "确定要同意解除合同么？", "同意解除", "取消", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        showProgressDialog();
                                        contract.capAcceptStay(map.get("contract_noid"), noid, MyCollectAty.this);
                                    }
                                }, null);
                            }
                        });
                        holder.fbtnPay.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                showDialog("", "确定不同意解除合同么？", "不同意解除", "取消", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        showProgressDialog();
                                        contract.capCancelStay(map.get("contract_noid"), noid, MyCollectAty.this);
                                    }
                                }, null);
                            }
                        });

                        break;

                    case ISSUED:// 纠纷完成待结算

                    case UNSETTLE: // 待结算
                    case UNSETTLESTAY:// 待结算解除合同
                    case PROGRESSSTAYED:// 已解除待结算(执行中)
                    case UNSETTLEING:// 待结算发起中
                    case STAYED: // 已解除待结算(待生效)
                        if (TextUtils.equals(map.get("requested_type"), "2")) {
                            holder.linlaySettle.setVisibility(View.VISIBLE);
                            holder.tvSettleName.setText("结算金额：");
                            if (map.get("requested_info").contains("-")) {
                                holder.tvSettle.setText(map.get("requested_info").replace("-", "-￥"));
                                holder.tvSettle.setTextColor(Color.parseColor("#fb4a4a"));
                            } else {
                                holder.tvSettle.setText("￥" + map.get("requested_info"));
                                holder.tvSettle.setTextColor(getResources().getColor(R.color.clr_main));
                            }
                            if (TextUtils.equals(map.get("requested_noid"), application.getUserInfo().get("noid"))) {
                                if (TextUtils.equals(map.get("requested_reply"), "0")) {
                                    holder.imgvStatus.setVisibility(View.VISIBLE);
                                    holder.imgvStatus.setImageResource(R.drawable.icon_settlement_wait);
                                    holder.linlayBtn.setVisibility(View.GONE);
                                } else if (TextUtils.equals(map.get("requested_reply"), "2")) {
                                    holder.imgvStatus.setVisibility(View.VISIBLE);
                                    holder.imgvStatus.setImageResource(R.drawable.icon_settlement_unagree);
                                    holder.btnRrlieve.setVisibility(View.GONE);
                                    holder.linlayBtn.setVisibility(View.VISIBLE);
                                    holder.btnLook.setVisibility(View.GONE);
                                    holder.fbtnPay.setVisibility(View.VISIBLE);
                                    holder.fbtnPay.setText("结算");
                                    holder.fbtnPay.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            showDialog("", "确定要进行结算么？", "确认结算", "取消", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    Bundle bundle = new Bundle();
                                                    bundle.putString("contract_noid", map.get("contract_noid"));
                                                    bundle.putString("money", map.get("amount"));
                                                    startActivity(SettlementAty.class, bundle);
                                                }
                                            }, null);
                                        }
                                    });

                                }
                            } else if (TextUtils.isEmpty(map.get("requested_noid"))) {
                                holder.imgvStatus.setVisibility(View.GONE);
                                holder.btnRrlieve.setVisibility(View.GONE);
                                holder.btnLook.setVisibility(View.GONE);
                                holder.fbtnPay.setVisibility(View.VISIBLE);
                                holder.linlayBtn.setVisibility(View.VISIBLE);
                                holder.fbtnPay.setText("结算");
                                holder.fbtnPay.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        showDialog("", "确定要进行结算么？", "确认结算", "取消", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                Bundle bundle = new Bundle();
                                                bundle.putString("contract_noid", map.get("contract_noid"));
                                                bundle.putString("money", map.get("amount"));
                                                startActivity(SettlementAty.class, bundle);
                                            }
                                        }, null);
                                    }
                                });
                            } else {
                                if (TextUtils.equals(map.get("requested_reply"), "2")) {
                                    holder.imgvStatus.setVisibility(View.GONE);
                                    holder.btnRrlieve.setVisibility(View.GONE);
                                    holder.linlayBtn.setVisibility(View.VISIBLE);
                                    holder.btnLook.setVisibility(View.GONE);
                                    holder.fbtnPay.setVisibility(View.VISIBLE);
                                    holder.fbtnPay.setText("结算");
                                    holder.fbtnPay.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            showDialog("", "确定要进行结算么？", "确认结算", "取消", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    Bundle bundle = new Bundle();
                                                    bundle.putString("contract_noid", map.get("contract_noid"));
                                                    bundle.putString("money", map.get("amount"));
                                                    startActivity(SettlementAty.class, bundle);
                                                }
                                            }, null);
                                        }
                                    });
                                } else if (TextUtils.equals(map.get("requested_reply"), "0")) {
                                    holder.linlayBtn.setVisibility(View.VISIBLE);
                                    holder.imgvStatus.setVisibility(View.VISIBLE);
                                    holder.imgvStatus.setImageResource(R.drawable.icon_settlement);
                                    holder.btnRrlieve.setVisibility(View.VISIBLE);
                                    holder.fbtnPay.setVisibility(View.VISIBLE);
                                    holder.btnLook.setVisibility(View.GONE);
                                    holder.btnRrlieve.setText("不同意结算");
                                    holder.fbtnPay.setText("同意结算");
                                    holder.fbtnPay.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            showDialog("", "确定要同意结算么？", "确定结算", "取消", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    showProgressDialog();
                                                    cont_noid = map.get("contract_noid");
                                                    payStutas = "settle";
                                                    contract.isAdequacyAmount(map.get("contract_noid"), noid,
                                                            map.get("requested_info"), MyCollectAty.this);
                                                }
                                            }, null);

                                        }
                                    });
                                    holder.btnRrlieve.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            showDialog("", "确定不同意结算么？", "不同意结算", "取消", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    Bundle bundle = new Bundle();
                                                    bundle.putString("flag", "settlement");
                                                    bundle.putString("contract_noid", map.get("contract_noid"));
                                                    startActivity(DisagreePayAty.class, bundle);
                                                }
                                            }, null);
                                        }
                                    });
                                }
                            }

                        } else {
                            holder.linlayBtn.setVisibility(View.VISIBLE);
                            holder.imgvStatus.setVisibility(View.GONE);
                            holder.btnRrlieve.setVisibility(View.GONE);
                            holder.btnLook.setVisibility(View.GONE);
                            holder.fbtnPay.setVisibility(View.VISIBLE);
                            holder.fbtnPay.setText("结算");
                            holder.fbtnPay.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    showDialog("", "确定要进行结算么？", "确认结算", "取消", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            Bundle bundle = new Bundle();
                                            bundle.putString("contract_noid", map.get("contract_noid"));
                                            bundle.putString("money", map.get("amount"));
                                            startActivity(SettlementAty.class, bundle);
                                        }
                                    }, null);
                                }
                            });
                        }
                        break;

                    case PROGRESSSTAY:// 待解除(执行中) 2 - 13
                        holder.imgvStatus.setVisibility(View.VISIBLE);
                        holder.btnLook.setVisibility(View.GONE);
                        holder.linlayBtn.setVisibility(View.VISIBLE);
                        if (TextUtils.equals(map.get("requested_noid"), application.getUserInfo().get("noid"))) {
                            holder.imgvStatus.setImageResource(R.drawable.icon_release_wait);
                            holder.imgvStatus.setVisibility(View.VISIBLE);
                            holder.btnRrlieve.setVisibility(View.GONE);
                            holder.fbtnPay.setVisibility(View.GONE);
                            holder.linlayBtn.setVisibility(View.GONE);
                        } else {
                            holder.linlayBtn.setVisibility(View.VISIBLE);
                            holder.imgvStatus.setVisibility(View.VISIBLE);
                            holder.imgvStatus.setImageResource(R.drawable.icon_release);
                            holder.btnRrlieve.setVisibility(View.VISIBLE);
                            holder.btnRrlieve.setText("同意解除");
                            holder.fbtnPay.setText("不同意解除");
                        }
                        holder.btnRrlieve.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                showDialog("", "确定要同意解除合同么？", "同意解除", "取消", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        showProgressDialog();
                                        contract.capAcceptProgressStay(map.get("contract_noid"), noid, MyCollectAty.this);
                                    }
                                }, null);
                            }
                        });
                        holder.fbtnPay.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                showDialog("", "确定不同意解除合同么？", "不同意解除", "取消", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        showProgressDialog();
                                        contract.capCancelProgressStay(map.get("contract_noid"), noid, MyCollectAty.this);
                                    }
                                }, null);
                            }
                        });
                        break;

                    case DRAWBACK:// 退补款
                    case DRAWBACKING:// 退补款发起中
                        holder.imgvStatus.setVisibility(View.VISIBLE);
                        holder.linlayBtn.setVisibility(View.VISIBLE);
                        int requested_reply = Integer.parseInt(map.get("requested_reply"));
                        holder.btnLook.setVisibility(View.GONE);
                        if (TextUtils.equals(map.get("requested_type"), "4")) {
                            holder.linlayTuibu.setVisibility(View.VISIBLE);
                            holder.linlaySettle.setVisibility(View.VISIBLE);
                            holder.tvSettleName.setText("结算金额：");
                            if (TextUtils.equals(map.get("adequacy_status"), "1")) {
                                if (map.get("adequacy_amount").contains("-")) {
                                    holder.tvSettle.setText(map.get("adequacy_amount").replace("-", "-￥"));
                                    holder.tvSettle.setTextColor(Color.parseColor("#fb4a4a"));
                                } else {
                                    holder.tvSettle.setText("￥" + map.get("adequacy_amount"));
                                    holder.tvSettle.setTextColor(getResources().getColor(R.color.clr_main));
                                }
                            } else {
                                holder.tvSettle.setText("");
                            }
                            holder.tvTuibuName.setText("退补款金额：");
                            if (map.get("requested_info").contains("-")) {
                                holder.tvTuibu.setText(map.get("requested_info").replace("-", "-￥"));
                                holder.tvTuibu.setTextColor(Color.parseColor("#fb4a4a"));
                            } else {
                                holder.tvTuibu.setText("￥" + map.get("requested_info"));
                                holder.tvTuibu.setTextColor(getResources().getColor(R.color.clr_main));
                            }
                            if (TextUtils.equals(map.get("requested_noid"), application.getUserInfo().get("noid"))) {
                                holder.btnRrlieve.setVisibility(View.GONE);
                                holder.fbtnPay.setVisibility(View.VISIBLE);
                                switch (requested_reply) {
                                    case 0:
                                        holder.imgvStatus.setImageResource(R.drawable.icon_refunds_wait);
                                        holder.fbtnPay.setText("取消退补申请");
                                        holder.fbtnPay.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                showDialog("", "确定要取消退补申请吗？", "确定", "取消", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        showProgressDialog();
                                                        contract.rollbackDrawback(map.get("contract_noid"), noid, MyCollectAty.this);
                                                    }
                                                }, null);
                                            }
                                        });
                                        break;
                                    case 1:
                                        break;
                                    case 2:
                                        holder.imgvStatus.setImageResource(R.drawable.icon_refunds_refuse);
                                        holder.fbtnPay.setText("退补款");
                                        holder.fbtnPay.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                showDialog("", "确定要进行退补款吗？", "确定", "取消", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        contract.toDrawback(map.get("contract_noid"), noid, MyCollectAty.this);
                                                        Bundle bundle = new Bundle();
                                                        bundle.putString("flag", "tuibu");
                                                        bundle.putString("contract_noid", map.get("contract_noid"));
                                                        startActivity(SettlementAty.class, bundle);
                                                    }
                                                }, null);
                                            }
                                        });
                                        break;

                                }
                            } else {
                                holder.btnLook.setVisibility(View.VISIBLE);
                                holder.btnRrlieve.setVisibility(View.VISIBLE);
                                holder.fbtnPay.setVisibility(View.GONE);
                                switch (requested_reply) {
                                    case 0:
                                        holder.imgvStatus.setImageResource(R.drawable.icon_refunds_apply);
                                        holder.btnRrlieve.setText("同意退补");
                                        holder.btnLook.setText("拒绝退补");
                                        break;
                                    case 1:
                                        break;
                                    case 2:
                                        holder.btnLook.setVisibility(View.GONE);
                                        holder.btnRrlieve.setVisibility(View.GONE);
                                        holder.imgvStatus.setVisibility(View.GONE);
                                        holder.fbtnPay.setText("退补款");

                                        break;
                                }
                                holder.btnRrlieve.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        showDialog("", "确定要同意退补款吗？", "同意", "取消", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                showProgressDialog();
                                                cont_noid = map.get("contract_noid");
                                                payStutas = "tuibu";
                                                contract.isAdequacyAmount(map.get("contract_noid"), noid,
                                                        map.get("requested_info"), MyCollectAty.this);

                                            }
                                        }, null);
                                    }
                                });
                                holder.btnLook.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        showDialog("", "确定要拒绝退补款吗？", "拒绝", "取消", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                contract.toDrawback(map.get("contract_noid"), noid, MyCollectAty.this);
                                                Bundle bundle = new Bundle();
                                                bundle.putString("flag", "refuse");
                                                bundle.putString("contract_noid", map.get("contract_noid"));
                                                startActivity(DisagreePayAty.class, bundle);
                                            }
                                        }, null);
                                    }
                                });
                                holder.fbtnPay.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        showDialog("", "确定要进行退补款吗？", "确定", "取消", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                contract.toDrawback(map.get("contract_noid"), noid, MyCollectAty.this);
                                                Bundle bundle = new Bundle();
                                                bundle.putString("flag", "tuibu");
                                                bundle.putString("contract_noid", map.get("contract_noid"));
                                                startActivity(SettlementAty.class, bundle);
                                            }
                                        }, null);
                                    }
                                });
                            }
                        } else {
                            holder.imgvStatus.setVisibility(View.GONE);
                            holder.linlayBtn.setVisibility(View.VISIBLE);
                            holder.btnLook.setVisibility(View.GONE);
                            holder.btnRrlieve.setVisibility(View.GONE);
                            holder.fbtnPay.setVisibility(View.VISIBLE);
                            holder.fbtnPay.setText("退补款");
                            holder.fbtnPay.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    showDialog("", "确定要进行退补款吗？", "确定", "取消", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            contract.toDrawback(map.get("contract_noid"), noid, MyCollectAty.this);
                                            Bundle bundle = new Bundle();
                                            bundle.putString("flag", "tuibu");
                                            bundle.putString("contract_noid", map.get("contract_noid"));
                                            startActivity(SettlementAty.class, bundle);
                                        }
                                    }, null);
                                }
                            });
                        }
                        break;
                    case ISSUEDONE:// 纠纷完成已结算
//                        break;
                    case DRAWBACKED:// 退补款完成
                    case DONE:// 正常已结算
                    case STAYEDDONE:// 已解除已结算
                        holder.linlaySettle.setVisibility(View.VISIBLE);
                        holder.tvSettleName.setText("结算金额：");
                        if (TextUtils.equals(map.get("adequacy_status"), "1")) {
                            if (map.get("adequacy_amount").contains("-")) {
                                holder.tvSettle.setText(map.get("adequacy_amount").replace("-", "-￥"));
                                holder.tvSettle.setTextColor(Color.parseColor("#fb4a4a"));
                            } else {
                                holder.tvSettle.setText("￥" + map.get("adequacy_amount"));
                                holder.tvSettle.setTextColor(getResources().getColor(R.color.clr_main));
                            }
                        } else {
                            holder.tvSettle.setText("");
                        }
                        holder.linlayBtn.setVisibility(View.VISIBLE);
                        holder.btnLook.setVisibility(View.VISIBLE);
                        holder.btnLook.setText("退补款");
                        holder.btnRrlieve.setVisibility(View.VISIBLE);
                        holder.fbtnPay.setText("继续签约");
                        holder.fbtnPay.setVisibility(View.VISIBLE);
                        String evaluate_status = map.get("cap_evaluate_status");
                        String endTime = map.get("end_time");
                        String timeslashData = DateUtils.timeslashData(endTime);
                        long twoDay = DateTool.getDays(DateTool.getStringDateShort(), timeslashData);
                        LogUtil.e(twoDay + "");
                        if (TextUtils.equals(evaluate_status, "0")) {
                            holder.btnRrlieve.setText("评价");
                            holder.btnRrlieve.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Bundle bundle = new Bundle();
                                    bundle.putString("contract_noid", map.get("contract_noid"));
                                    startActivity(EvaluateAty.class, bundle);
                                }
                            });
                            holder.btnLook.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    showDialog("", "确定要进行退补款吗？", "确定", "取消", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            contract.toDrawback(map.get("contract_noid"), noid, MyCollectAty.this);
                                            Bundle bundle = new Bundle();
                                            bundle.putString("flag", "tuibu");
                                            bundle.putString("contract_noid", map.get("contract_noid"));
                                            startActivity(SettlementAty.class, bundle);
                                        }
                                    }, null);
                                }
                            });
                            if (TextUtils.equals(map.get("can_evaluate"), "0")) {
                                holder.btnRrlieve.setVisibility(View.GONE);
                            }

                        } else {


                            if (TextUtils.equals(map.get("can_evaluate"), "0")) {
                                holder.btnLook.setVisibility(View.GONE);
                                holder.btnRrlieve.setVisibility(View.VISIBLE);
                                holder.btnRrlieve.setText("退补款");
                            } else {
                                holder.btnRrlieve.setText("修改评价");
                                holder.btnLook.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        showDialog("", "确定要进行退补款吗？", "确定", "取消", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                contract.toDrawback(map.get("contract_noid"), noid, MyCollectAty.this);
                                                Bundle bundle = new Bundle();
                                                bundle.putString("flag", "tuibu");
                                                bundle.putString("contract_noid", map.get("contract_noid"));
                                                startActivity(SettlementAty.class, bundle);
                                            }
                                        }, null);
                                    }
                                });
                                holder.btnRrlieve.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Bundle bundle = new Bundle();
                                        bundle.putString("contract_noid", map.get("contract_noid"));
                                        bundle.putString("flag", "edit");
                                        startActivity(EvaluateAty.class, bundle);
                                    }
                                });

                            }
                        }
                        holder.fbtnPay.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Bundle bundle = new Bundle();
                                bundle.putString("flag", "keep");
                                bundle.putString("lab_noid", map.get("noid"));
                                startActivity(NewJobOrderAty.class, bundle);
                            }
                        });

                        break;
                }
                holder.tvMemberId.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.icon_labor, 0);
                holder.linlayContent.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Bundle bundle = new Bundle();
//                        bundle.putString("noid", map.get("noid"));
                        bundle.putString("contract_noid", map.get("contract_noid"));
//                        bundle.putString("requested_noid", map.get("requested_noid"));
//                        bundle.putString("requested_type", map.get("requested_type"));
//                        bundle.putString("requested_reply", map.get("requested_reply"));
                        startActivity(ContDetailAty.class, bundle);
                    }
                });
                holder.imgvPhone.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        telephone = map.get("lab_telephone");
                        showDialog("提示", "是否拨打电话", "确定", "取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                requestPermissions(Constants.PERMISSIONS_CALL_PHONE, Manifest.permission.CALL_PHONE);
                            }
                        }, null);
                    }
                });
            } else {
                /*********************************** 劳方********************************************/
                String evaluate_score = map.get("cap_evaluate_score");
//           LogUtil.e(DateUtils.timeslashData(1493740830+""));
//            LogUtil.e(DateUtils.dataShort("2017-05-03"));
//            LogUtil.e(DateTool.getTwoDay("2017-05-03","2017-05-01"));
                if (TextUtils.equals(evaluate_score, "0")) {
                    holder.imgvCreditRating.setImageResource(R.drawable.icon_starnone0);
                } else if (TextUtils.equals(evaluate_score, "1")) {
                    holder.imgvCreditRating.setImageResource(R.drawable.icon_1star);
                } else if (TextUtils.equals(evaluate_score, "2")) {
                    holder.imgvCreditRating.setImageResource(R.drawable.icon_2star);
                } else if (TextUtils.equals(evaluate_score, "3")) {
                    holder.imgvCreditRating.setImageResource(R.drawable.icon_3star);
                } else if (TextUtils.equals(evaluate_score, "4")) {
                    holder.imgvCreditRating.setImageResource(R.drawable.icon_4star);
                } else if (TextUtils.equals(evaluate_score, "5")) {
                    holder.imgvCreditRating.setImageResource(R.drawable.icon_5star);
                }
                if (TextUtils.equals(map.get("lab_attestation"), "1")) {
                    holder.tvAuthentication.setText("已认证");
                } else {
                    holder.tvAuthentication.setText("未认证");
                }
                ImageLoader imageLoader = new ImageLoader();
                imageLoader.disPlay(holder.imgvHead, map.get("cap_head"));
                holder.tvMemberId.setText(map.get("cap_noid"));
                holder.tvNickname.setText(map.get("cap_nickname"));
                holder.imgvAgree.setVisibility(View.GONE);
                holder.imgvStatus.setVisibility(View.GONE);
                holder.linlaySettle.setVisibility(View.GONE);
                holder.linlayTuibu.setVisibility(View.GONE);
                holder.linlayNoid.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Bundle bundle = new Bundle();
                        bundle.putString("code", map.get("cap_noid"));
                        if (status == INVALID || status == EXPIRE) {

                        } else {
                            bundle.putString("flag", "contract");
                        }

//                    bundle.putString("flag", "collect");
                        startActivity(MemberDetailAty.class, bundle);
                    }
                });
                switch (status) {
                    case INVALID: // 待生效
                        holder.linlayBtn.setVisibility(View.VISIBLE);
                        holder.fbtnPay.setVisibility(View.VISIBLE);
                        holder.btnLook.setVisibility(View.GONE);
//                        holder.btnRrlieve.setText("解除合同");
                        holder.fbtnPay.setText("解除合同");
                        holder.btnRrlieve.setVisibility(View.GONE);
                        holder.imgvStatus.setVisibility(View.GONE);
                        holder.fbtnPay.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                showDialog("", "确定要解除该合同么？", "确定解除", "取消", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Bundle bundle = new Bundle();
                                        bundle.putString("contract_noid", map.get("contract_noid"));
                                        bundle.putString("status", "lab");
                                        startActivity(RelieveContAty.class, bundle);
                                    }
                                }, null);
                            }
                        });

                        break;
                    case PROGRESS:// 执行中
                        holder.imgvStatus.setVisibility(View.GONE);
                        holder.linlayBtn.setVisibility(View.VISIBLE);
                        if (TextUtils.equals(map.get("requested_noid"), application.getUserInfo().get("noid"))) {
                            if (TextUtils.equals(map.get("requested_type"), "3")) {
                                switch (Integer.parseInt(map.get("requested_reply"))) {
                                    case 0:
                                        holder.imgvAgree.setVisibility(View.VISIBLE);
                                        holder.imgvAgree.setImageResource(R.drawable.icon_contract_agree);
                                        holder.linlayBtn.setVisibility(View.VISIBLE);
                                        holder.btnLook.setVisibility(View.VISIBLE);
                                        holder.btnLook.setText("查看修改明细");
                                        holder.btnRrlieve.setVisibility(View.VISIBLE);
                                        holder.btnRrlieve.setText("解除合同");
                                        holder.fbtnPay.setVisibility(View.VISIBLE);
                                        holder.fbtnPay.setText("结算");
                                        holder.btnLook.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                Bundle bundle = new Bundle();
                                                bundle.putString("contract_noid", map.get("contract_noid"));
                                                bundle.putString("tag", "lab");
                                                startActivity(LookContractInfo.class, bundle);
                                            }
                                        });
                                        holder.btnRrlieve.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                showDialog("", "确定要解除该合同么？", "确定解除", "取消", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        Bundle bundle = new Bundle();
                                                        bundle.putString("contract_noid", map.get("contract_noid"));
                                                        bundle.putString("status", "lab");
                                                        bundle.putString("flag", "progress");
                                                        startActivity(RelieveContAty.class, bundle);
                                                    }
                                                }, null);
                                            }
                                        });
                                        holder.fbtnPay.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                showDialog("", "确定要进行结算么？", "确认结算", "取消", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        contract.toUnsettle(map.get("contract_noid"), application.getUserInfo().get("noid"), MyCollectAty.this);
                                                        Bundle bundle = new Bundle();
                                                        bundle.putString("contract_noid", map.get("contract_noid"));
                                                        bundle.putString("money", map.get("amount"));
                                                        startActivity(SettlementAty.class, bundle);
                                                    }
                                                }, null);
                                            }
                                        });
                                        break;
                                    case 1:
                                    case 2:
                                        holder.imgvAgree.setVisibility(View.GONE);
                                        holder.linlayBtn.setVisibility(View.VISIBLE);
                                        holder.btnLook.setVisibility(View.VISIBLE);
                                        holder.btnLook.setText("解除合同");
                                        holder.btnRrlieve.setVisibility(View.VISIBLE);
                                        holder.btnRrlieve.setText("结算");
                                        holder.fbtnPay.setVisibility(View.VISIBLE);
                                        holder.fbtnPay.setText("修改合同");
                                        holder.btnLook.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                showDialog("", "确定要解除该合同么？", "确定解除", "取消", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        Bundle bundle = new Bundle();
                                                        bundle.putString("contract_noid", map.get("contract_noid"));
                                                        bundle.putString("status", "lab");
                                                        bundle.putString("flag", "progress");
                                                        startActivity(RelieveContAty.class, bundle);
                                                    }
                                                }, null);
                                            }
                                        });
                                        holder.btnRrlieve.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                showDialog("", "确定要进行结算么？", "确认结算", "取消", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        contract.toUnsettle(map.get("contract_noid"), application.getUserInfo().get("noid"), MyCollectAty.this);
                                                        Bundle bundle = new Bundle();
                                                        bundle.putString("contract_noid", map.get("contract_noid"));
                                                        bundle.putString("money", map.get("amount"));
                                                        startActivity(SettlementAty.class, bundle);
                                                    }
                                                }, null);
                                            }
                                        });
                                        holder.fbtnPay.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                Bundle bundle = new Bundle();
                                                bundle.putString("contract_noid", map.get("contract_noid"));
                                                bundle.putString("tag", "lab");
                                                startActivity(EditContractAty.class, bundle);
                                            }
                                        });
                                        break;
                                }
                            } else if (TextUtils.equals(map.get("requested_type"), "1")) {
                                holder.imgvAgree.setVisibility(View.GONE);
                                holder.linlayBtn.setVisibility(View.VISIBLE);
                                holder.btnLook.setVisibility(View.VISIBLE);
                                holder.btnLook.setText("解除合同");
                                holder.btnRrlieve.setVisibility(View.VISIBLE);
                                holder.btnRrlieve.setText("结算");
                                holder.fbtnPay.setVisibility(View.VISIBLE);
                                holder.fbtnPay.setText("修改合同");
                                holder.btnLook.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        showDialog("", "确定要解除该合同么？", "确定解除", "取消", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                Bundle bundle = new Bundle();
                                                bundle.putString("contract_noid", map.get("contract_noid"));
                                                bundle.putString("status", "lab");
                                                bundle.putString("flag", "progress");
                                                startActivity(RelieveContAty.class, bundle);
                                            }
                                        }, null);
                                    }
                                });
                                holder.btnRrlieve.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        showDialog("", "确定要进行结算么？", "确认结算", "取消", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                contract.toUnsettle(map.get("contract_noid"), application.getUserInfo().get("noid"), MyCollectAty.this);
                                                Bundle bundle = new Bundle();
                                                bundle.putString("contract_noid", map.get("contract_noid"));
                                                bundle.putString("money", map.get("amount"));
                                                startActivity(SettlementAty.class, bundle);
                                            }
                                        }, null);
                                    }
                                });
                                holder.fbtnPay.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Bundle bundle = new Bundle();
                                        bundle.putString("contract_noid", map.get("contract_noid"));
                                        bundle.putString("tag", "lab");
                                        startActivity(EditContractAty.class, bundle);
                                    }
                                });
                            }
                        } else if (TextUtils.isEmpty(map.get("requested_noid"))) {
                            holder.imgvAgree.setVisibility(View.GONE);
                            holder.linlayBtn.setVisibility(View.VISIBLE);
                            holder.btnLook.setVisibility(View.VISIBLE);
                            holder.btnLook.setText("解除合同");
                            holder.btnRrlieve.setVisibility(View.VISIBLE);
                            holder.btnRrlieve.setText("结算");
                            holder.fbtnPay.setVisibility(View.VISIBLE);
                            holder.fbtnPay.setText("修改合同");
                            holder.btnLook.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    showDialog("", "确定要解除该合同么？", "确定解除", "取消", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            Bundle bundle = new Bundle();
                                            bundle.putString("contract_noid", map.get("contract_noid"));
                                            bundle.putString("status", "lab");
                                            bundle.putString("flag", "progress");
                                            startActivity(RelieveContAty.class, bundle);
                                        }
                                    }, null);
                                }
                            });
                            holder.btnRrlieve.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    showDialog("", "确定要进行结算么？", "确认结算", "取消", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            contract.toUnsettle(map.get("contract_noid"), application.getUserInfo().get("noid"), MyCollectAty.this);
                                            Bundle bundle = new Bundle();
                                            bundle.putString("contract_noid", map.get("contract_noid"));
                                            bundle.putString("money", map.get("amount"));
                                            startActivity(SettlementAty.class, bundle);
                                        }
                                    }, null);
                                }
                            });
                            holder.fbtnPay.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Bundle bundle = new Bundle();
                                    bundle.putString("contract_noid", map.get("contract_noid"));
                                    bundle.putString("tag", "lab");
                                    startActivity(EditContractAty.class, bundle);
                                }
                            });
                        } else {
                            if (TextUtils.equals(map.get("requested_type"), "3")) {
                                switch (Integer.parseInt(map.get("requested_reply"))) {
                                    case 0:
                                        holder.imgvAgree.setVisibility(View.VISIBLE);
                                        holder.imgvAgree.setImageResource(R.drawable.icon_contract_disagree);
                                        holder.linlayBtn.setVisibility(View.VISIBLE);
                                        holder.btnLook.setVisibility(View.VISIBLE);
                                        holder.btnRrlieve.setVisibility(View.VISIBLE);
                                        holder.fbtnPay.setVisibility(View.VISIBLE);
                                        holder.btnLook.setText("查看修改明细");
                                        holder.btnRrlieve.setText("解除合同");
                                        holder.fbtnPay.setText("结算");
                                        holder.btnLook.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                Bundle bundle = new Bundle();
                                                bundle.putString("contract_noid", map.get("contract_noid"));
                                                bundle.putString("tag", "lab");
                                                startActivity(LookContractInfo.class, bundle);
                                            }
                                        });
                                        holder.btnRrlieve.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                showDialog("", "确定要解除该合同么？", "确定解除", "取消", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        Bundle bundle = new Bundle();
                                                        bundle.putString("contract_noid", map.get("contract_noid"));
                                                        bundle.putString("status", "lab");
                                                        bundle.putString("flag", "progress");
                                                        startActivity(RelieveContAty.class, bundle);
                                                    }
                                                }, null);
                                            }
                                        });
                                        holder.fbtnPay.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                showDialog("", "确定要进行结算么？", "确认结算", "取消", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        contract.toUnsettle(map.get("contract_noid"), application.getUserInfo().get("noid"), MyCollectAty.this);
                                                        Bundle bundle = new Bundle();
                                                        bundle.putString("contract_noid", map.get("contract_noid"));
                                                        bundle.putString("money", map.get("amount"));
                                                        startActivity(SettlementAty.class, bundle);
                                                    }
                                                }, null);
                                            }
                                        });
                                        break;
                                    case 1:
                                    case 2:
                                        holder.imgvAgree.setVisibility(View.GONE);
                                        holder.linlayBtn.setVisibility(View.VISIBLE);
                                        holder.btnLook.setVisibility(View.VISIBLE);
                                        holder.btnLook.setText("解除合同");
                                        holder.btnRrlieve.setVisibility(View.VISIBLE);
                                        holder.btnRrlieve.setText("结算");
                                        holder.fbtnPay.setVisibility(View.VISIBLE);
                                        holder.fbtnPay.setText("修改合同");
                                        holder.btnLook.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                showDialog("", "确定要解除该合同么？", "确定解除", "取消", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        Bundle bundle = new Bundle();
                                                        bundle.putString("contract_noid", map.get("contract_noid"));
                                                        bundle.putString("status", "lab");
                                                        bundle.putString("flag", "progress");
                                                        startActivity(RelieveContAty.class, bundle);
                                                    }
                                                }, null);
                                            }
                                        });
                                        holder.btnRrlieve.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                showDialog("", "确定要进行结算么？", "确认结算", "取消", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        contract.toUnsettle(map.get("contract_noid"), application.getUserInfo().get("noid"), MyCollectAty.this);
                                                        Bundle bundle = new Bundle();
                                                        bundle.putString("contract_noid", map.get("contract_noid"));
                                                        bundle.putString("money", map.get("amount"));
                                                        startActivity(SettlementAty.class, bundle);
                                                    }
                                                }, null);
                                            }
                                        });
                                        holder.fbtnPay.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                Bundle bundle = new Bundle();
                                                bundle.putString("contract_noid", map.get("contract_noid"));
                                                bundle.putString("tag", "lab");
                                                startActivity(EditContractAty.class, bundle);
                                            }
                                        });
                                        break;
                                }
                            } else if (TextUtils.equals(map.get("requested_type"), "1")) {
                                holder.imgvAgree.setVisibility(View.GONE);
                                holder.linlayBtn.setVisibility(View.VISIBLE);
                                holder.btnLook.setVisibility(View.VISIBLE);
                                holder.btnLook.setText("解除合同");
                                holder.btnRrlieve.setVisibility(View.VISIBLE);
                                holder.btnRrlieve.setText("结算");
                                holder.fbtnPay.setVisibility(View.VISIBLE);
                                holder.fbtnPay.setText("修改合同");
                                holder.btnLook.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        showDialog("", "确定要解除该合同么？", "确定解除", "取消", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                Bundle bundle = new Bundle();
                                                bundle.putString("contract_noid", map.get("contract_noid"));
                                                bundle.putString("status", "lab");
                                                bundle.putString("flag", "progress");
                                                startActivity(RelieveContAty.class, bundle);
                                            }
                                        }, null);
                                    }
                                });
                                holder.btnRrlieve.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        showDialog("", "确定要进行结算么？", "确认结算", "取消", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                contract.toUnsettle(map.get("contract_noid"), application.getUserInfo().get("noid"), MyCollectAty.this);
                                                Bundle bundle = new Bundle();
                                                bundle.putString("contract_noid", map.get("contract_noid"));
                                                bundle.putString("money", map.get("amount"));
                                                startActivity(SettlementAty.class, bundle);
                                            }
                                        }, null);
                                    }
                                });
                                holder.fbtnPay.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Bundle bundle = new Bundle();
                                        bundle.putString("contract_noid", map.get("contract_noid"));
                                        bundle.putString("tag", "lab");
                                        startActivity(EditContractAty.class, bundle);
                                    }
                                });
                            }
                        }
                        break;
                    case SETTLE:// 已结算未评价
                        holder.linlayBtn.setVisibility(View.VISIBLE);
                        holder.btnLook.setVisibility(View.VISIBLE);
                        holder.btnRrlieve.setVisibility(View.VISIBLE);
                        holder.fbtnPay.setVisibility(View.VISIBLE);
                        holder.btnLook.setText("退补款");
                        holder.btnRrlieve.setText("评价");
                        holder.fbtnPay.setText("继续签约");
                        break;
                    case SETTLEED:// 已结算已评价
                        holder.btnLook.setText("退补款");
                        holder.btnRrlieve.setText("修改评价");
                        holder.fbtnPay.setText("继续签约");
                        break;

                    case ISSUE:// 纠纷
                        holder.linlaySettle.setVisibility(View.VISIBLE);
                        holder.tvSettleName.setText("纠纷调解金额：");
                        holder.tvSettle.setText("￥" + map.get("issue_amount"));
                        if (map.get("issue_amount").contains("-")) {
                            holder.tvSettle.setTextColor(Color.parseColor("#fb4a4a"));
                        } else {
                            holder.tvSettle.setTextColor(getResources().getColor(R.color.clr_main));
                        }
                        if (TextUtils.equals(map.get("adequacy_status"), "0")) {
                            holder.linlayTuibu.setVisibility(View.GONE);
                        } else {
                            holder.linlayTuibu.setVisibility(View.VISIBLE);
                            holder.tvTuibuName.setText("结算金额：");
                            if (map.get("adequacy_amount").contains("-")) {
                                holder.tvTuibu.setText(map.get("adequacy_amount").replace("-", "-￥"));
                                holder.tvTuibu.setTextColor(Color.parseColor("#fb4a4a"));
                            } else {
                                holder.tvTuibu.setText("￥" + map.get("adequacy_amount"));
                                holder.tvTuibu.setTextColor(getResources().getColor(R.color.clr_main));
                            }
                        }
                        holder.btnLook.setEnabled(true);
                        holder.fbtnPay.setEnabled(true);
                        holder.linlayBtn.setVisibility(View.VISIBLE);
                        holder.btnLook.setVisibility(View.VISIBLE);
                        holder.btnLook.setText("拒绝调解方案");
                        holder.btnRrlieve.setVisibility(View.GONE);
                        holder.fbtnPay.setVisibility(View.VISIBLE);
                        holder.fbtnPay.setText("同意调解方案");
                        holder.btnLook.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                showDialog("", "确定要拒绝该调解方案么？", "拒绝", "取消", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Bundle bundle = new Bundle();
                                        bundle.putString("flag", "mediate");
                                        bundle.putString("contract_noid", map.get("contract_noid"));
                                        bundle.putString("issue_id", map.get("issue_id"));
                                        startActivity(DisagreePayAty.class, bundle);
                                    }
                                }, null);
                            }
                        });
                        holder.fbtnPay.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                showDialog("", "确定要同意该调解方案么？", "同意", "取消", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        showProgressDialog();
                                        cont_noid = map.get("contract_noid");
                                        payStutas = "dispute";
                                        issue_id = map.get("issue_id");
                                        contract.isAdequacyAmount(map.get("contract_noid"), noid,
                                                map.get("requested_info"), MyCollectAty.this);
                                    }
                                }, null);
                            }
                        });
                        if (TextUtils.equals(map.get("issue_lab_reply"), "1")) {
                            holder.btnLook.setEnabled(false);
                            holder.fbtnPay.setEnabled(false);
                            holder.fbtnPay.setText("已同意调解方案");
                            holder.btnLook.setText("拒绝调解方案");
                            holder.btnLook.setBackgroundDrawable(getResources().getDrawable(R.drawable.btn_save_enable));
                            holder.btnLook.setTextColor(Color.WHITE);
                            holder.fbtnPay.setButtonColor(Color.parseColor("#cccccc"));
                        } else if (TextUtils.equals(map.get("issue_lab_reply"), "2")) {
                            holder.btnLook.setEnabled(false);
                            holder.fbtnPay.setEnabled(false);
                            holder.btnLook.setBackgroundDrawable(getResources().getDrawable(R.drawable.btn_save_enable));
                            holder.btnLook.setTextColor(Color.WHITE);
                            holder.btnLook.setText("已拒绝调解方案");
                            holder.fbtnPay.setText("同意调解方案");
                            holder.fbtnPay.setButtonColor(Color.parseColor("#cccccc"));
                        }

                        break;
                    case EXPIRE:// 待生效已过期
                        holder.linlayBtn.setVisibility(View.GONE);
                        holder.btnLook.setVisibility(View.GONE);
                        holder.btnRrlieve.setVisibility(View.GONE);
                        holder.fbtnPay.setText("重新签约");
                        holder.imgvStatus.setVisibility(View.GONE);

                        break;
                    case STAY:// 待解除(待生效) 1 - 8
                        holder.imgvStatus.setVisibility(View.VISIBLE);
                        holder.btnLook.setVisibility(View.GONE);
                        if (TextUtils.equals(map.get("requested_noid"), application.getUserInfo().get("noid"))) {
                            holder.imgvStatus.setVisibility(View.VISIBLE);
                            holder.imgvStatus.setImageResource(R.drawable.icon_release_wait);
                            holder.btnRrlieve.setVisibility(View.GONE);
                            holder.fbtnPay.setVisibility(View.GONE);
                            holder.linlayBtn.setVisibility(View.GONE);
                        } else {
                            holder.linlayBtn.setVisibility(View.VISIBLE);
                            holder.btnRrlieve.setVisibility(View.VISIBLE);
                            holder.imgvStatus.setImageResource(R.drawable.icon_release);
                            holder.btnRrlieve.setText("同意解除");
                            holder.fbtnPay.setText("不同意解除");
                        }
                        holder.btnRrlieve.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                showDialog("", "确定要同意解除合同么？", "同意解除", "取消", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        showProgressDialog();
                                        contract.labAcceptStay(map.get("contract_noid"), noid, MyCollectAty.this);
                                    }
                                }, null);
                            }
                        });
                        holder.fbtnPay.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                showDialog("", "确定要同意解除合同么？", "不同意解除", "取消", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        showProgressDialog();
                                        contract.labCancelStay(map.get("contract_noid"), noid, MyCollectAty.this);
                                    }
                                }, null);
                            }
                        });

                        break;

                    case UNSETTLE: // 待结算
                    case ISSUED:// 纠纷完成待结算
                    case UNSETTLESTAY:// 待结算解除合同
                    case PROGRESSSTAYED:// 已解除待结算(执行中)
                    case UNSETTLEING:// 待结算发起中  15
                    case STAYED: // 已解除待结算(待生效) 11
                        if (TextUtils.equals(map.get("requested_type"), "2")) {
                            holder.linlaySettle.setVisibility(View.VISIBLE);
                            holder.tvSettleName.setText("结算金额：");
                            if (map.get("requested_info").contains("-")) {
                                holder.tvSettle.setText(map.get("requested_info").replace("-", "-￥"));
                                holder.tvSettle.setTextColor(Color.parseColor("#fb4a4a"));
                            } else {
                                holder.tvSettle.setText("￥" + map.get("requested_info"));
                                holder.tvSettle.setTextColor(getResources().getColor(R.color.clr_main));
                            }
                            if (TextUtils.equals(map.get("requested_noid"), application.getUserInfo().get("noid"))) {
                                if (TextUtils.equals(map.get("requested_reply"), "0")) {
                                    holder.imgvStatus.setVisibility(View.VISIBLE);
                                    holder.imgvStatus.setImageResource(R.drawable.icon_settlement_wait);
                                    holder.linlayBtn.setVisibility(View.GONE);
                                } else if (TextUtils.equals(map.get("requested_reply"), "2")) {
                                    holder.linlayBtn.setVisibility(View.VISIBLE);
                                    holder.imgvStatus.setVisibility(View.VISIBLE);
                                    holder.imgvStatus.setImageResource(R.drawable.icon_settlement_unagree);
                                    holder.btnRrlieve.setVisibility(View.GONE);
                                    holder.btnLook.setVisibility(View.GONE);
                                    holder.fbtnPay.setText("结算");
                                    holder.fbtnPay.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            showDialog("", "确定要进行结算么？", "确认结算", "取消", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    Bundle bundle = new Bundle();
                                                    bundle.putString("contract_noid", map.get("contract_noid"));
                                                    bundle.putString("money", map.get("amount"));
                                                    startActivity(SettlementAty.class, bundle);
                                                }
                                            }, null);
                                        }
                                    });

                                }
                            } else if (TextUtils.isEmpty(map.get("requested_noid"))) {
                                holder.linlayBtn.setVisibility(View.VISIBLE);
                                holder.imgvStatus.setVisibility(View.GONE);
                                holder.btnRrlieve.setVisibility(View.GONE);
                                holder.btnLook.setVisibility(View.GONE);
                                holder.fbtnPay.setVisibility(View.VISIBLE);
                                holder.fbtnPay.setText("结算");
                                holder.fbtnPay.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        showDialog("", "确定要进行结算么？", "确认结算", "取消", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                Bundle bundle = new Bundle();
                                                bundle.putString("contract_noid", map.get("contract_noid"));
                                                bundle.putString("money", map.get("amount"));
                                                startActivity(SettlementAty.class, bundle);
                                            }
                                        }, null);
                                    }
                                });
                            } else {
                                if (TextUtils.equals(map.get("requested_reply"), "2")) {
                                    holder.linlayBtn.setVisibility(View.VISIBLE);
                                    holder.imgvStatus.setVisibility(View.GONE);
                                    holder.btnRrlieve.setVisibility(View.GONE);
                                    holder.btnLook.setVisibility(View.GONE);
                                    holder.fbtnPay.setVisibility(View.VISIBLE);
                                    holder.fbtnPay.setText("结算");
                                    holder.fbtnPay.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            showDialog("", "确定要进行结算么？", "确认结算", "取消", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    Bundle bundle = new Bundle();
                                                    bundle.putString("contract_noid", map.get("contract_noid"));
                                                    bundle.putString("money", map.get("amount"));
                                                    startActivity(SettlementAty.class, bundle);
                                                }
                                            }, null);
                                        }
                                    });
                                } else if (TextUtils.equals(map.get("requested_reply"), "0")) {
                                    holder.linlayBtn.setVisibility(View.VISIBLE);
                                    holder.imgvStatus.setVisibility(View.VISIBLE);
                                    holder.imgvStatus.setImageResource(R.drawable.icon_settlement);
                                    holder.btnRrlieve.setVisibility(View.VISIBLE);
                                    holder.btnLook.setVisibility(View.GONE);
                                    holder.btnRrlieve.setText("不同意结算");
                                    holder.fbtnPay.setText("同意结算");
                                    holder.fbtnPay.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            showDialog("", "确定要同意结算么？", "确定结算", "取消", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    showProgressDialog();
                                                    cont_noid = map.get("contract_noid");
                                                    payStutas = "settle";
                                                    contract.isAdequacyAmount(map.get("contract_noid"), noid,
                                                            map.get("requested_info"), MyCollectAty.this);
                                                }
                                            }, null);
                                        }
                                    });
                                    holder.btnRrlieve.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            showDialog("", "确定不同意结算么？", "不同意结算", "取消", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    Bundle bundle = new Bundle();
                                                    bundle.putString("flag", "settlement");
                                                    bundle.putString("contract_noid", map.get("contract_noid"));
                                                    startActivity(DisagreePayAty.class, bundle);
                                                }
                                            }, null);
                                        }
                                    });
                                }

                            }

                        } else {
                            holder.linlayBtn.setVisibility(View.VISIBLE);
                            holder.imgvStatus.setVisibility(View.GONE);
                            holder.btnRrlieve.setVisibility(View.GONE);
                            holder.btnLook.setVisibility(View.GONE);
                            holder.fbtnPay.setVisibility(View.VISIBLE);
                            holder.fbtnPay.setText("结算");
                            holder.fbtnPay.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    showDialog("", "确定要进行结算么？", "确认结算", "取消", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            Bundle bundle = new Bundle();
                                            bundle.putString("contract_noid", map.get("contract_noid"));
                                            bundle.putString("money", map.get("amount"));
                                            startActivity(SettlementAty.class, bundle);
                                        }
                                    }, null);
                                }
                            });
                        }
                        break;

                    case PROGRESSSTAY:// 待解除(执行中) 2 - 13
                        holder.imgvStatus.setVisibility(View.VISIBLE);
                        holder.btnLook.setVisibility(View.GONE);
                        if (TextUtils.equals(map.get("requested_noid"), application.getUserInfo().get("noid"))) {
                            holder.imgvStatus.setVisibility(View.VISIBLE);
                            holder.imgvStatus.setImageResource(R.drawable.icon_release_wait);
                            holder.btnRrlieve.setVisibility(View.GONE);
                            holder.fbtnPay.setVisibility(View.GONE);
                            holder.linlayBtn.setVisibility(View.GONE);
                        } else {
                            holder.linlayBtn.setVisibility(View.VISIBLE);
                            holder.btnRrlieve.setVisibility(View.VISIBLE);
                            holder.imgvStatus.setImageResource(R.drawable.icon_release);
                            holder.btnRrlieve.setText("同意解除");
                            holder.fbtnPay.setText("不同意解除");
                        }
                        holder.btnRrlieve.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                showDialog("", "确定要同意解除合同么？", "同意解除", "取消", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        showProgressDialog();
                                        contract.labAcceptProgressStay(map.get("contract_noid"), noid, MyCollectAty.this);
                                    }
                                }, null);
                            }
                        });
                        holder.fbtnPay.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                showDialog("", "确定要同意解除合同么？", "不同意解除", "取消", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        showProgressDialog();
                                        contract.labCancelProgressStay(map.get("contract_noid"), noid, MyCollectAty.this);
                                    }
                                }, null);
                            }
                        });


                        break;


                    case DRAWBACK:// 退补款
                    case DRAWBACKING:// 退补款发起中
                        holder.imgvStatus.setVisibility(View.VISIBLE);
                        holder.linlayBtn.setVisibility(View.VISIBLE);
                        int requested_reply = Integer.parseInt(map.get("requested_reply"));
                        holder.btnLook.setVisibility(View.GONE);
                        if (TextUtils.equals(map.get("requested_type"), "4")) {
                            holder.linlayTuibu.setVisibility(View.VISIBLE);
                            holder.linlaySettle.setVisibility(View.VISIBLE);
                            holder.tvSettleName.setText("结算金额：");
                            if (TextUtils.equals(map.get("adequacy_status"), "1")) {
                                if (map.get("adequacy_amount").contains("-")) {
                                    holder.tvSettle.setText(map.get("adequacy_amount").replace("-", "-￥"));
                                    holder.tvSettle.setTextColor(Color.parseColor("#fb4a4a"));
                                } else {
                                    holder.tvSettle.setText("￥" + map.get("adequacy_amount"));
                                    holder.tvSettle.setTextColor(getResources().getColor(R.color.clr_main));
                                }
                            } else {
                                holder.tvSettle.setText("");
                            }
                            holder.tvTuibuName.setText("退补款金额：");
                            if (map.get("requested_info").contains("-")) {
                                holder.tvTuibu.setText(map.get("requested_info").replace("-", "-￥"));
                                holder.tvTuibu.setTextColor(Color.parseColor("#fb4a4a"));
                            } else {
                                holder.tvTuibu.setText("￥" + map.get("requested_info"));
                                holder.tvTuibu.setTextColor(getResources().getColor(R.color.clr_main));
                            }
                            if (TextUtils.equals(map.get("requested_noid"), application.getUserInfo().get("noid"))) {
                                holder.btnRrlieve.setVisibility(View.GONE);
                                holder.fbtnPay.setVisibility(View.VISIBLE);
                                switch (requested_reply) {
                                    case 0:
                                        holder.imgvStatus.setImageResource(R.drawable.icon_refunds_wait);
                                        holder.fbtnPay.setText("取消退补申请");
                                        holder.fbtnPay.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                showDialog("", "确定要取消退补申请吗？", "确定", "取消", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        showProgressDialog();
                                                        contract.rollbackDrawback(map.get("contract_noid"), noid, MyCollectAty.this);
                                                    }
                                                }, null);
                                            }
                                        });
                                        break;
                                    case 1:
                                        break;
                                    case 2:
                                        holder.imgvStatus.setImageResource(R.drawable.icon_refunds_refuse);
                                        holder.fbtnPay.setText("退补款");
                                        holder.fbtnPay.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                showDialog("", "确定要进行退补款吗？", "确定", "取消", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        contract.toDrawback(map.get("contract_noid"), noid, MyCollectAty.this);
                                                        Bundle bundle = new Bundle();
                                                        bundle.putString("flag", "tuibu");
                                                        bundle.putString("contract_noid", map.get("contract_noid"));
                                                        startActivity(SettlementAty.class, bundle);
                                                    }
                                                }, null);
                                            }
                                        });
                                        break;

                                }
                            } else {
                                holder.btnLook.setVisibility(View.VISIBLE);
                                holder.btnRrlieve.setVisibility(View.VISIBLE);
                                holder.fbtnPay.setVisibility(View.GONE);
                                switch (requested_reply) {
                                    case 0:
                                        holder.imgvStatus.setImageResource(R.drawable.icon_refunds_apply);
                                        holder.btnRrlieve.setText("同意退补");
                                        holder.btnLook.setText("拒绝退补");
                                        break;
                                    case 1:
                                        break;
                                    case 2:
                                        holder.imgvStatus.setVisibility(View.GONE);
                                        holder.btnRrlieve.setVisibility(View.GONE);
                                        holder.imgvStatus.setVisibility(View.GONE);
                                        holder.fbtnPay.setVisibility(View.VISIBLE);
                                        holder.fbtnPay.setText("退补款");
                                        break;
                                }
                                holder.btnRrlieve.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        showDialog("", "确定要同意退补款吗？", "同意", "取消", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                cont_noid = map.get("contract_noid");
                                                payStutas = "tuibu";
                                                contract.isAdequacyAmount(map.get("contract_noid"), noid,
                                                        map.get("requested_info"), MyCollectAty.this);
                                            }
                                        }, null);
                                    }
                                });
                                holder.btnLook.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        showDialog("", "确定要拒绝退补款吗？", "拒绝", "取消", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                contract.toDrawback(map.get("contract_noid"), noid, MyCollectAty.this);
                                                Bundle bundle = new Bundle();
                                                bundle.putString("flag", "refuse");
                                                bundle.putString("contract_noid", map.get("contract_noid"));
                                                startActivity(DisagreePayAty.class, bundle);
                                            }
                                        }, null);
                                    }
                                });
                                holder.fbtnPay.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        showDialog("", "确定要进行退补款吗？", "确定", "取消", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                contract.toDrawback(map.get("contract_noid"), noid, MyCollectAty.this);
                                                Bundle bundle = new Bundle();
                                                bundle.putString("flag", "tuibu");
                                                bundle.putString("contract_noid", map.get("contract_noid"));
                                                startActivity(SettlementAty.class, bundle);
                                            }
                                        }, null);
                                    }
                                });
                            }
                        } else {
                            holder.imgvStatus.setVisibility(View.GONE);
                            holder.linlayBtn.setVisibility(View.VISIBLE);
                            holder.btnLook.setVisibility(View.GONE);
                            holder.btnRrlieve.setVisibility(View.GONE);
                            holder.fbtnPay.setVisibility(View.VISIBLE);
                            holder.fbtnPay.setText("退补款");
                            holder.fbtnPay.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    showDialog("", "确定要进行退补款吗？", "确定", "取消", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            contract.toDrawback(map.get("contract_noid"), noid, MyCollectAty.this);
                                            Bundle bundle = new Bundle();
                                            bundle.putString("flag", "tuibu");
                                            bundle.putString("contract_noid", map.get("contract_noid"));
                                            startActivity(SettlementAty.class, bundle);
                                        }
                                    }, null);
                                }
                            });
                        }
                        break;
                    case ISSUEDONE:// 纠纷完成已结算
//                        break;
                    case DRAWBACKED:// 退补款完成
                    case DONE:// 正常已结算
                    case STAYEDDONE:// 已解除已结算
                        holder.linlaySettle.setVisibility(View.VISIBLE);
                        holder.tvSettleName.setText("结算金额：");
                        if (TextUtils.equals(map.get("adequacy_status"), "1")) {
                            if (map.get("adequacy_amount").contains("-")) {
                                holder.tvSettle.setText(map.get("adequacy_amount").replace("-", "-￥"));
                                holder.tvSettle.setTextColor(Color.parseColor("#fb4a4a"));
                            } else {
                                holder.tvSettle.setText("￥" + map.get("adequacy_amount"));
                                holder.tvSettle.setTextColor(getResources().getColor(R.color.clr_main));
                            }
                        } else {
                            holder.tvSettle.setText("");
                        }
                        holder.linlayBtn.setVisibility(View.VISIBLE);
                        holder.btnLook.setVisibility(View.GONE);
                        holder.btnRrlieve.setVisibility(View.VISIBLE);
                        holder.btnRrlieve.setText("退补款");
                        holder.fbtnPay.setVisibility(View.VISIBLE);
                        String evaluate_status = map.get("lab_evaluate_status");
                        String endTime = map.get("end_time");
                        String timeslashData = DateUtils.timeslashData(endTime);
                        long twoDay = DateTool.getDays(timeslashData, DateTool.getStringDateShort());
                        if (TextUtils.equals(evaluate_status, "0")) {
                            holder.fbtnPay.setText("评价");
                            holder.fbtnPay.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Bundle bundle = new Bundle();
                                    bundle.putString("contract_noid", map.get("contract_noid"));
                                    startActivity(EvaluateAty.class, bundle);
                                }
                            });
                            if (TextUtils.equals(map.get("can_evaluate"), "0")) {
                                holder.btnRrlieve.setVisibility(View.GONE);
                                holder.fbtnPay.setText("退补款");
                                holder.fbtnPay.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Bundle bundle = new Bundle();
                                        bundle.putString("flag", "tuibu");
                                        bundle.putString("contract_noid", map.get("contract_noid"));
                                        startActivity(SettlementAty.class, bundle);
                                    }
                                });
                            }
                        } else {

                            if (TextUtils.equals(map.get("can_evaluate"), "0")) {
                                holder.btnRrlieve.setVisibility(View.GONE);
                                holder.fbtnPay.setText("退补款");
                            } else {
                                holder.fbtnPay.setText("修改评价");
                            }
                            holder.fbtnPay.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Bundle bundle = new Bundle();
                                    bundle.putString("contract_noid", map.get("contract_noid"));
                                    bundle.putString("flag", "edit");
                                    startActivity(EvaluateAty.class, bundle);
                                }
                            });
                        }
                        holder.btnRrlieve.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Bundle bundle = new Bundle();
                                bundle.putString("flag", "tuibu");
                                bundle.putString("contract_noid", map.get("contract_noid"));
                                startActivity(SettlementAty.class, bundle);

                            }
                        });

                        break;
                }
                holder.tvMemberId.setCompoundDrawablesWithIntrinsicBounds(0, 0,
                        R.drawable.icon_capital, 0);
                holder.linlayContent.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Bundle bundle = new Bundle();
                        bundle.putString("noid", map.get("noid"));
                        bundle.putString("contract_noid", map.get("contract_noid"));
                        bundle.putString("requested_noid", map.get("requested_noid"));
                        bundle.putString("requested_type", map.get("requested_type"));
                        bundle.putString("requested_reply", map.get("requested_reply"));
                        startActivity(ContDetailAty.class, bundle);
                    }
                });
                holder.imgvPhone.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        telephone = map.get("cap_telephone");
                        showDialog("提示", "是否拨打电话", "确定", "取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                requestPermissions(Constants.PERMISSIONS_CALL_PHONE, Manifest.permission.CALL_PHONE);
                            }
                        }, null);
                    }
                });
            }
        }

        @Override
        public int getItemCount() {
            return ListUtils.getSize(contractList);
        }

        public class ViewHodler extends RecyclerView.ViewHolder {
            @ViewInject(R.id.list_contract_pay)
            private FButton fbtnPay;
            @ViewInject(R.id.list_look_info)
            private Button btnLook;
            @ViewInject(R.id.list_btn_content)
            LinearLayout linlayBtn;
            @ViewInject(R.id.list_contract_relieve)
            private Button btnRrlieve;
            @ViewInject(R.id.list_cont_member_id)
            private TextView tvMemberId;
            @ViewInject(R.id.list_cont_status_img)
            ImageView imgvStatus;
            @ViewInject(R.id.list_contract_id)
            TextView tvContractId;
            @ViewInject(R.id.list_cont_state)
            TextView tvState;
            @ViewInject(R.id.list_cont_total)
            TextView tvTotal;
            @ViewInject(R.id.list_cont_start_date)
            TextView tvStartDate;
            @ViewInject(R.id.list_cont_end_date)
            TextView tvEndDate;
            @ViewInject(R.id.list_cont_payment)
            TextView tvPayment;
            @ViewInject(R.id.listcont_work)
            TextView tvWork;
            @ViewInject(R.id.list_cont_price)
            TextView tvPrice;
            @ViewInject(R.id.list_cont_head)
            CircularImageView imgvHead;
            @ViewInject(R.id.list_cont_authentication)
            TextView tvAuthentication;
            @ViewInject(R.id.list_cont_nickname)
            TextView tvNickname;
            @ViewInject(R.id.list_cont_credit_rating)
            ImageView imgvCreditRating;
            @ViewInject(R.id.list_cont_agree_img)
            ImageView imgvAgree;
            @ViewInject(R.id.list_cont_phone)
            ImageView imgvPhone;
            @ViewInject(R.id.list_cont_click)
            LinearLayout linlayContent;
            @ViewInject(R.id.list_cont_settle_total_lay)
            LinearLayout linlaySettle;
            @ViewInject(R.id.list_cont_settle_total_name)
            TextView tvSettleName;
            @ViewInject(R.id.list_cont_settle_total)
            TextView tvSettle;
            @ViewInject(R.id.list_cont_tuibu_total_lay)
            LinearLayout linlayTuibu;
            @ViewInject(R.id.list_cont_tuibu_total)
            TextView tvTuibu;
            @ViewInject(R.id.list_cont_tuibu_total_name)
            TextView tvTuibuName;
            @ViewInject(R.id.list_my_cont_noid_click)
            LinearLayout linlayNoid;


            public ViewHodler(View itemView) {
                super(itemView);
                x.view().inject(this, itemView);
                AutoUtils.autoSize(itemView);
            }
        }
    }
}
