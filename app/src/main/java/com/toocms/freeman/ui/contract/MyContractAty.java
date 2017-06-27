package com.toocms.freeman.ui.contract;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
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
import android.widget.ScrollView;
import android.widget.TextView;

import com.toocms.frame.config.Settings;
import com.toocms.frame.image.ImageLoader;
import com.toocms.frame.tool.DateTool;
import com.toocms.freeman.R;
import com.toocms.freeman.config.Constants;
import com.toocms.freeman.config.JsonArryToList;
import com.toocms.freeman.https.Contract;
import com.toocms.freeman.https.Sys;
import com.toocms.freeman.ui.BaseAty;
import com.toocms.freeman.ui.contract.editcontract.EditContractAty;
import com.toocms.freeman.ui.contract.editcontract.look.LookContractInfo;
import com.toocms.freeman.ui.index.SkillAty;
import com.toocms.freeman.ui.mine.MemberDetailAty;
import com.toocms.freeman.ui.pay.DisagreePayAty;
import com.toocms.freeman.ui.pay.PayAty;
import com.toocms.freeman.ui.pay.SettlementAty;
import com.toocms.freeman.ui.recruitment.joborder.CreateTimeAty;
import com.toocms.freeman.ui.recruitment.joborder.NewJobOrderAty;
import com.toocms.freeman.ui.util.DateUtils;
import com.toocms.freeman.ui.util.WorkOrder;
import com.zero.autolayout.utils.AutoUtils;

import org.xutils.common.util.LogUtil;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import cn.qqtheme.framework.picker.DatePicker;
import cn.zero.android.common.permission.PermissionFail;
import cn.zero.android.common.permission.PermissionSuccess;
import cn.zero.android.common.util.JSONUtils;
import cn.zero.android.common.util.ListUtils;
import cn.zero.android.common.util.MapUtils;
import cn.zero.android.common.view.FButton;
import cn.zero.android.common.view.shapeimageview.CircularImageView;
import cn.zero.android.common.view.swipetoloadlayout.OnLoadMoreListener;
import cn.zero.android.common.view.swipetoloadlayout.OnRefreshListener;
import cn.zero.android.common.view.swipetoloadlayout.view.SwipeToLoadRecyclerView;

/**
 * Created by admin on 2017/3/27.
 */

public class MyContractAty extends BaseAty implements OnRefreshListener, OnLoadMoreListener {
    public final static String BOSS = "boss"; //资方
    public final static String STAFF = "staff";//劳方
    public static final int CREAT_TIME = 2017;
    @ViewInject(R.id.my_cont_noid)
    EditText editContNoid;
    @ViewInject(R.id.my_cont_keywords)
    EditText editKeywords;
    @ViewInject(R.id.my_cont_work)
    TextView tvWork;
    @ViewInject(R.id.my_cont_start_date)
    TextView tvStartDate;
    @ViewInject(R.id.my_cont_end_date)
    TextView tvEndDate;
    @ViewInject(R.id.my_cont_min_price)
    EditText editMinPrice;
    @ViewInject(R.id.my_cont_max_price)
    EditText editMaxPrice;
    @ViewInject(R.id.my_cont_min_money)
    EditText editMinMoney;
    @ViewInject(R.id.my_cont_max_money)
    EditText editMaxMoney;
    @ViewInject(R.id.my_contract_list)
    private SwipeToLoadRecyclerView swipeToLoadRecyclerView;
    @ViewInject(R.id.my_contract_drawer)
    private DrawerLayout drawerLayout;
    @ViewInject(R.id.my_contract_screen_lay)
    LinearLayout linlayScreenCont;
    @ViewInject(R.id.my_contract_lay)
    private ScrollView linlayScreen;
    @ViewInject(R.id.my_jo_screen)
    private TextView tvScreen;
    @ViewInject(R.id.my_jo_time)
    private TextView tvTime;
    View view;
    private TabLayout linearListView;
    private String[] title = new String[]{"全部", "待生效", "执行中", "待解除", "待结算", "已结算", "退补款", "纠纷"};
    private ContractAdapter adapter;
    private String flag = BOSS;
    private boolean isFirst;
    /**
     * @param noid               用户编号
     * @param character          用户角色，CAP 为资方， LAB为劳方。只能为这两种
     * @param page               分页，默认为1
     * @param contract_noid      筛选：合同编号
     * @param keywords           筛选：关键字
     * @param skill              筛选：技能工种id数组
     * @param contract_starttime 筛选：合同开始日期
     * @param contract_endtime   筛选：合同截止日期
     * @param min_suntotal       筛选：最低单价
     * @param max_suntotal       筛选：最高单价
     * @param min_amount         筛选：最低总额
     * @param max_amount         筛选：最高总额
     * @param min_validtime      筛选：生效时间左值
     * @param max_validtime      筛选：生效时间右值
     * @param status             筛选：合同状态，默认为全部。
     * <p>
     * - INVALID  待生效
     * - PROGRESS 执行中
     * - STAY     待解除
     * - UNSETTLE 待结算
     * - SETTLE   已结算
     * - DRAWBACK 退补款
     * - ISSUE    纠纷
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

    private String noid;
    private String character;
    private int page = 1;
    private String contract_noid;
    private String keywords;
    private String skill;
    private String contract_starttime;
    private String contract_endtime;
    private String min_suntotal;
    private String max_suntotal;
    private String min_amount;
    private String max_amount;
    private String min_validtime;
    private String max_validtime;
    private String status;
    private TextView tvCap;
    private TextView tvLab;
    private View vLine;
    private int position;
    private Contract contract;
    private ArrayList<Map<String, String>> dataList = new ArrayList<>();
    private String startYear;
    private String startMonth;
    private String startDay;
    private String endYear;
    private String endMonth;
    private String endDay;
    private ArrayList<String> skillList;
    private List<Map<String, String>> skillItemData;
    private Sys sys;
    /**
     * 同意结算[acceptAdequancy]
     *
     * @param contract_noid
     * @param noid
     */
    private String telephone;
    @ViewInject(R.id.my_jo_empty)
    TextView tvEmpty;
    private String dataIsAmount;
    // adapter里点击获取的合同编号
    private String cont_noid;
    private String payStutas = "null";
    private String issue_id;

    @Override
    protected int getLayoutResId() {
        return R.layout.aty_my_contract;
    }

    @Override
    protected void initialized() {
        contract = new Contract();
        sys = new Sys();
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
        ViewGroup.LayoutParams layoutParams = linlayScreenCont.getLayoutParams();
        layoutParams.height = wm.getDefaultDisplay().getHeight();
        layoutParams.width = wm.getDefaultDisplay().getWidth() / 4 * 3;
        linlayScreenCont.setLayoutParams(layoutParams);
        swipeToLoadRecyclerView.setOnRefreshListener(this);
        swipeToLoadRecyclerView.setOnLoadMoreListener(this);
        swipeToLoadRecyclerView.getRecyclerView().setLayoutManager(new LinearLayoutManager(this));
        WorkOrder.getInstance().clear();
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
        noid = application.getUserInfo().get("noid");
        character = "CAP";
        page = 1;

    }

    private void initHeader() {
        if (view == null) {
            view = LayoutInflater.from(this).inflate(R.layout.header_my_contract, null);
            linearListView = (TabLayout) view.findViewById(R.id.header_my_contract_title);
            tvCap = (TextView) view.findViewById(R.id.my_contract_cap);
            tvLab = (TextView) view.findViewById(R.id.my_contract_lab);
            vLine = view.findViewById(R.id.take_title_view);
            tvCap.setSelected(false);
        }
        tvCap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                position = 1;
                tvCap.setSelected(true);
                tvLab.setSelected(false);
                flag = BOSS;
                dataList.clear();
                adapter.notifyDataSetChanged();
//                tvScreen.setVisibility(View.VISIBLE);
                startTranslate(vLine, (Settings.displayWidth / 2) * (position - 1));
                character = "CAP";
                status = "quanbu";
                linearListView.getTabAt(0).select();

//                adapter = null;
                if (linearListView.getSelectedTabPosition() == 0) {
                    page = 1;
                    swipeToLoadRecyclerView.startRefreshing();
                }
//
//                adapter.notifyDataSetChanged();
            }
        });
        tvLab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                position = 2;
                tvCap.setSelected(false);
                tvLab.setSelected(true);
                flag = STAFF;
                dataList.clear();
                adapter.notifyDataSetChanged();
                character = "LAB";
                status = "quanbu";
//                tvScreen.setVisibility(View.GONE);
                startTranslate(vLine, (Settings.displayWidth / 2) * (position - 1));
                linearListView.getTabAt(0).select();
                if (linearListView.getSelectedTabPosition() == 0) {
                    page = 1;
                    swipeToLoadRecyclerView.startRefreshing();
                }
//                adapter = null;
//                swipeToLoadRecyclerView.startRefreshing();
//                adapter.notifyDataSetChanged();
            }
        });
        for (int i = 0; i < title.length; i++) {
            linearListView.addTab(linearListView.newTab().setText(title[i]));
        }
        linearListView.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                switch (tab.getPosition()) {
                    case 0:
                        status = "quanbu";
                        break;
                    case 1:
                        status = "INVALID";
                        break;
                    case 2:
                        status = "PROGRESS";
                        break;
                    case 3:
                        status = "STAY";
                        break;
                    case 4:
                        status = "UNSETTLE";
                        break;
                    case 5:
                        status = "SETTLE";
                        break;
                    case 6:
                        status = "DRAWBACK";
                        break;
                    case 7:
                        status = "ISSUE";
                        break;
                }
                page = 1;
                swipeToLoadRecyclerView.startRefreshing();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        swipeToLoadRecyclerView.addHeaderView(view);


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

        if (TextUtils.equals(flag, BOSS)) {
            contract.listing(noid, character, page + "", contract_noid, keywords, skill, contract_starttime, contract_endtime
                    , min_suntotal, max_suntotal, min_amount, max_amount, min_validtime, max_validtime, status, this);
        } else {
            contract.listing(noid, character, page + "", null, null, null, null, null
                    , null, null, null, null, null, null, status, this);
        }
//        if (TextUtils.equals(tvScreen.getText().toString(), "筛选")) {
//            WorkOrder.getInstance().clear();
//        }
        initSkill();
        if (TextUtils.isEmpty(tvStartDate.getText()) && TextUtils.isEmpty(tvEndDate.getText()) && TextUtils.isEmpty(max_suntotal) &&
                TextUtils.isEmpty(max_amount) && TextUtils.isEmpty(min_amount) && TextUtils.isEmpty(min_suntotal)
                && TextUtils.isEmpty(tvWork.getText()) && TextUtils.isEmpty(keywords) && TextUtils.isEmpty(contract_noid)
                && TextUtils.isEmpty(tvTime.getText())) {
            tvScreen.setText("筛选");
            skill = "";
            skillList.clear();
        } else
            tvScreen.setText("已筛选");

        skill = ListUtils.join(skillList);
        if (SettlementAty.isSend) {
            showDialog("", "结算信息已发出，等待对方同意……", "确定", "", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    SettlementAty.isSend = false;
                }
            }, null);
            SettlementAty.isSend = false;
        }
        if (SettlementAty.isBackSend) {
            showDialog("", "退补款信息已发出，等待对方同意……", "确定", "", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    SettlementAty.isBackSend = false;
                }
            }, null);
            SettlementAty.isBackSend = false;
        }
//        PayAty.PaySettleStatus(new PayStatusCallback() {
//            @Override
//            public void callback() {
//                LogUtil.e("/*******************同意结算回调****************************/");
//
//            }
//        });

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
            else tvWork.setText(ListUtils.join(list));

        }
    }


    @Override
    public void onComplete(RequestParams params, String result) {
        if (params.getUri().contains("Contract/listing")) {
            if (page == 1) {
                dataList = JSONUtils.parseDataToMapList(result);

            } else {
                ArrayList<Map<String, String>> list = JSONUtils.parseDataToMapList(result);
                if (ListUtils.isEmpty(list)) {
                    page--;
                }
                dataList.addAll(list);
            }

            if (adapter == null) {
                adapter = new ContractAdapter();
                if (view == null) {
                    initHeader();
                }

                swipeToLoadRecyclerView.setAdapter(adapter);

            } else adapter.notifyDataSetChanged();

            if (ListUtils.isEmpty(dataList)) {
                tvEmpty.setVisibility(View.VISIBLE);
            } else {
                tvEmpty.setVisibility(View.GONE);
            }
        } else if (params.getUri().contains("Sys/getSkillList")) {
            skillItemData = JSONUtils.parseDataToMapList(result);
        } else if (params.getUri().contains("Contract/capCancelStay") ||
                params.getUri().contains("Contract/capAcceptStay")) {
            showToast(JSONUtils.parseKeyAndValueToMap(result).get("message"));
            if (TextUtils.equals(flag, BOSS)) {
                contract.listing(noid, character, page + "", contract_noid, keywords, skill, contract_starttime, contract_endtime
                        , min_suntotal, max_suntotal, min_amount, max_amount, min_validtime, max_validtime, status, this);
            } else {
                contract.listing(noid, character, page + "", null, null, null, null, null
                        , null, null, null, null, null, null, status, this);
            }
        } else if (params.getUri().contains("Contract/labCancelStay") ||
                params.getUri().contains("Contract/capCancelProgressStay") ||
                params.getUri().contains("Contract/capAcceptProgressStay") ||
                params.getUri().contains("Contract/labCancelProgressStay") ||
                params.getUri().contains("Contract/labAcceptProgressStay") ||
                params.getUri().contains("Contract/appectIssue") ||
                params.getUri().contains("Contract/labAcceptStay")) {
            showToast(JSONUtils.parseKeyAndValueToMap(result).get("message"));
            if (TextUtils.equals(flag, BOSS)) {
                contract.listing(noid, character, page + "", contract_noid, keywords, skill, contract_starttime, contract_endtime
                        , min_suntotal, max_suntotal, min_amount, max_amount, min_validtime, max_validtime, status, this);
            } else {
                contract.listing(noid, character, page + "", null, null, null, null, null
                        , null, null, null, null, null, null, status, this);
            }
        } else if (params.getUri().contains("Contract/acceptAdequancy") ||
                params.getUri().contains("Contract/acceptDrawback") ||
                params.getUri().contains("Contract/rollbackDrawback")) {
            showToast(JSONUtils.parseKeyAndValueToMap(result).get("message"));
            if (TextUtils.equals(flag, BOSS)) {
                contract.listing(noid, character, page + "", contract_noid, keywords, skill, contract_starttime, contract_endtime
                        , min_suntotal, max_suntotal, min_amount, max_amount, min_validtime, max_validtime, status, this);
            } else {
                contract.listing(noid, character, page + "", null, null, null, null, null
                        , null, null, null, null, null, null, status, this);
            }
        } else if (params.getUri().contains("Contract/isAdequacyAmount")) {
            LogUtil.e(payStutas + "/////////////530");
            dataIsAmount = JSONUtils.parseKeyAndValueToMap(result).get("data");
            if (TextUtils.equals(dataIsAmount, "0")) {
                if (TextUtils.equals(payStutas, "settle")) {
                    contract.acceptAdequancy(cont_noid, noid, MyContractAty.this);
                } else if (TextUtils.equals(payStutas, "tuibu")) {
                    contract.acceptDrawback(cont_noid, noid, MyContractAty.this);
                } else if (TextUtils.equals(payStutas, "dispute")) {
                    LogUtil.e(issue_id + "////////539");
                    contract.appectIssue(cont_noid, application.getUserInfo().get("noid"), issue_id, MyContractAty.this);
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
        swipeToLoadRecyclerView.stopRefreshing();
        swipeToLoadRecyclerView.stopLoadMore();
    }

    @Event({R.id.my_jo_back, R.id.my_jo_screen, R.id.my_jo_work, R.id.my_cont_work_click, R.id.my_cont_start_date_click,
            R.id.my_cont_end_date_click, R.id.my_jo_sure, R.id.my_jo_time_click, R.id.my_cont_clear})
    private void onClick(View view) {
        switch (view.getId()) {
            case R.id.my_jo_back:
                finish();
                break;
            case R.id.my_jo_screen:
                if (!drawerLayout.isDrawerOpen(linlayScreen)) {
                    drawerLayout.openDrawer(linlayScreen);
                }
                break;
            case R.id.my_jo_work:
                startActivity(SkillAty.class, null);
                break;
            case R.id.my_cont_work_click:
                startActivity(SkillAty.class, null);
                break;
            case R.id.my_cont_start_date_click:
                onYearMonthDayPicker(2000, 2117, new DatePicker.OnYearMonthDayPickListener() {
                    @Override
                    public void onDatePicked(String year, String month, String day) {
                        tvStartDate.setText(year + "-" + month + "-" + day);
                        startYear = year;
                        startMonth = month;
                        startDay = day;
                    }
                });
                break;
            case R.id.my_cont_end_date_click:
                DatePicker datePicker = onYearMonthDayPicker(2000, 2117, true, new DatePicker.OnYearMonthDayPickListener() {
                    @Override
                    public void onDatePicked(String year, String month, String day) {
                        endYear = year;
                        endMonth = month;
                        endDay = day;
                        if (!TextUtils.isEmpty(startYear)) {
                            if (Integer.parseInt(year) > Integer.parseInt(startYear)) {
                                tvEndDate.setText(year + "-" + month + "-" + day);
                            } else if (Integer.parseInt(year) == Integer.parseInt(startYear)) {
                                if (Integer.parseInt(month) > Integer.parseInt(startMonth))
                                    tvEndDate.setText(year + "-" + month + "-" + day);
                                else if (Integer.parseInt(month) == Integer.parseInt(startMonth)) {
                                    if (Integer.parseInt(day) >= Integer.parseInt(startDay)) {
                                        tvEndDate.setText(year + "-" + month + "-" + day);
                                    } else {
                                        showToast("合同截止时间必须大于开始时间");
                                    }
                                } else {
                                    showToast("合同截止时间必须大于开始时间");
                                }
                            } else {
                                showToast("合同截止时间必须大于开始时间");
                            }

                        } else {
                            tvEndDate.setText(year + "-" + month + "-" + day);
                        }

                    }
                });
                if (!TextUtils.isEmpty(startYear))
                    datePicker.setSelectedItem(Integer.parseInt(startYear), Integer.parseInt(startMonth), Integer.parseInt(startDay));
                datePicker.show();
                break;
            case R.id.my_jo_sure:
                keywords = editKeywords.getText().toString();
                contract_noid = editContNoid.getText().toString();
                if (!TextUtils.isEmpty(startDay))
                    contract_starttime = startYear + "-" + startMonth + "-" + startDay;
                if (!TextUtils.isEmpty(endDay))
                    contract_endtime = endYear + "-" + endMonth + "-" + endDay;
                String maxMoney = editMaxMoney.getText().toString().trim();
                String minMoney = editMinMoney.getText().toString().trim();
                String maxPrice = editMaxPrice.getText().toString().trim();
                String minPrice = editMinPrice.getText().toString().trim();
                max_amount = maxMoney;
                min_amount = minMoney;
                if (!TextUtils.isEmpty(maxMoney) && !TextUtils.isEmpty(minMoney)) {
                    if (Integer.parseInt(maxMoney) < Integer.parseInt(minMoney)) {
                        editMaxMoney.setText(minMoney);
                        editMinMoney.setText(maxMoney);
                        max_amount = minMoney;
                        min_amount = maxMoney;
                    }
                }
                min_suntotal = minPrice;
                max_suntotal = maxPrice;
                if (!TextUtils.isEmpty(maxPrice) && !TextUtils.isEmpty(minPrice)) {
                    if (Integer.parseInt(maxPrice) < Integer.parseInt(minPrice)) {
                        editMaxPrice.setText(minPrice);
                        editMinPrice.setText(maxPrice);
                        min_suntotal = maxPrice;
                        max_suntotal = minPrice;
                    }
                }
                if (TextUtils.isEmpty(tvStartDate.getText()) && TextUtils.isEmpty(tvEndDate.getText()) && TextUtils.isEmpty(maxMoney) &&
                        TextUtils.isEmpty(maxPrice) && TextUtils.isEmpty(minMoney) && TextUtils.isEmpty(minPrice)
                        && TextUtils.isEmpty(tvWork.getText()) && TextUtils.isEmpty(keywords) && TextUtils.isEmpty(contract_noid)
                        && TextUtils.isEmpty(tvTime.getText())) {
                    tvScreen.setText("筛选");
                } else
                    tvScreen.setText("已筛选");
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
                swipeToLoadRecyclerView.startRefreshing();
                break;
            case R.id.my_jo_time_click:
                Bundle bundle = new Bundle();
                bundle.putString("flag", "contract");
                startActivityForResult(CreateTimeAty.class, bundle, CREAT_TIME);
                break;
            case R.id.my_cont_clear:
                keywords = "";
                editKeywords.setText("");
                skill = "";
                tvWork.setText("");
                WorkOrder.getInstance().clear();
                tvStartDate.setText("");
                contract_starttime = "";
                startYear = "";
                startMonth = "";
                startDay = "";
                contract_endtime = "";
                tvEndDate.setText("");
                endYear = "";
                endDay = "";
                endMonth = "";
                min_suntotal = "";
                editMinMoney.setText("");
                max_suntotal = "";
                editMaxMoney.setText("");
                min_amount = "";
                editMinPrice.setText("");
                editMaxPrice.setText("");
                max_amount = "";
                min_validtime = "";
                max_validtime = "";
                tvTime.setText("");
                tvScreen.setText("筛选");
                contract_noid = "";
                editContNoid.setText("");
                contract.listing(noid, character, page + "", contract_noid, keywords, skill, contract_starttime, contract_endtime
                        , min_suntotal, max_suntotal, min_amount, max_amount, min_validtime, max_validtime, status, this);
                break;
        }
    }

    @Override
    public void onRefresh() {
//        if (TextUtils.equals(flag, BOSS)) {
        contract.listing(noid, character, page + "", contract_noid, keywords, skill, contract_starttime, contract_endtime
                , min_suntotal, max_suntotal, min_amount, max_amount, min_validtime, max_validtime, status, this);
//        } else {
//            contract.listing(noid, "LAB", page + "", null, null, null, null, null
//                    , null, null, null, null, null, null, status, this);
//        }
    }


    @PermissionSuccess(requestCode = Constants.PERMISSIONS_CALL_PHONE)
    public void requestSuccess() {
        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + telephone));
        startActivity(intent);
    }

    @PermissionFail(requestCode = Constants.PERMISSIONS_CALL_PHONE)
    public void requestFail() {
        showToast("请求权限失败，暂时无法拨打电话");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            return;
        }
        switch (requestCode) {
            case CREAT_TIME:
                int startY = data.getIntExtra("startY", 0);
                int startM = data.getIntExtra("startM", 0);
                int startD = data.getIntExtra("startD", 0);
                int endY = data.getIntExtra("endY", 0);
                int endM = data.getIntExtra("endM", 0);
                int endD = data.getIntExtra("endD", 0);
                String strStartM = startM + "";
                String strStartD = startD + "";
                String strEndM = endM + "";
                String strEndD = endD + "";
                if (startY != 0) {
                    if (startM < 10) {
                        strStartM = "0" + startM;
                    }
                    if (startD < 10) {
                        strStartD = "0" + startD;
                    }
                    min_validtime = startY + "-" + strStartM + "-" + strStartD;
                }

                if (endY != 0) {
                    if (endD < 10) {
                        strEndD = "0" + endD;
                    }
                    if (endM < 10) {
                        strEndM = "0" + endM;
                    }
                    max_validtime = endY + "-" + strEndM + "-" + strEndD;
                }

                if (startY != 0 && endY != 0)
                    tvTime.setText(min_validtime + "   " + max_validtime);
                else if (startY != 0 && endY == 0) {

                    final Calendar c = Calendar.getInstance();
                    int mYear = c.get(Calendar.YEAR); //获取当前年份

                    int mMonth = c.get(Calendar.MONTH);//获取当前月份

                    int mDay = c.get(Calendar.DAY_OF_MONTH);//获取当前月份的日期号码

                    tvTime.setText(min_validtime);
                } else if (startY == 0 && endY != 0)
                    tvTime.setText(max_validtime);
                break;
        }
    }

    @Override
    public void onLoadMore() {
        if (adapter.getItemCount() % Integer.parseInt(getResources().getString(R.string.load_num))
                < Integer.parseInt(getResources().getString(R.string.load_min_num)) &&
                adapter.getItemCount() % Integer.parseInt(getResources().getString(R.string.load_num))
                        > 0) {
            swipeToLoadRecyclerView.stopLoadMore();
            return;
        }
        page++;
//        if (TextUtils.equals(flag, BOSS)) {
        contract.listing(noid, character, page + "", contract_noid, keywords, skill, contract_starttime, contract_endtime
                , min_suntotal, max_suntotal, min_amount, max_amount, min_validtime, max_validtime, status, this);
//        } else {
//            contract.listing(noid, character, page + "", null, null, null, null, null
//                    , null, null, null, null, null, null, status, this);
//        }
    }

    private class ContractAdapter extends RecyclerView.Adapter<ContractAdapter.ViewHodler> {


        @Override
        public ViewHodler onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.listitem_my_contract, parent, false);
            return new ViewHodler(view);
        }

        @Override
        public void onBindViewHolder(ViewHodler holder, int position) {
            final Map<String, String> map = dataList.get(position);
            holder.tvContractId.setText(map.get("contract_noid"));
            holder.tvState.setText(map.get("status_name"));
            holder.tvTotal.setText("￥" + map.get("amount"));
            holder.tvStartDate.setText(map.get("contract_starttime"));
            holder.tvEndDate.setText(map.get("contract_endtime"));
            holder.tvPayment.setText(map.get("settle_type_name"));
            String evaluate_score = map.get("evaluate_score");
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
            if (TextUtils.equals(map.get("attestation"), "1")) {
                holder.tvAuthentication.setText("已认证");
            } else {
                holder.tvAuthentication.setText("未认证");
            }
            ImageLoader imageLoader = new ImageLoader();
            imageLoader.disPlay(holder.imgvHead, map.get("head"));
            holder.tvMemberId.setText(map.get("noid"));
            holder.tvNickname.setText(map.get("nickname"));
            holder.tvWork.setText(ListUtils.join(JsonArryToList.strList(map.get("skill_name"))));
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
            holder.btnLook.setTextColor(getResources().getColor(R.color.clr_main));
            if (TextUtils.equals(map.get("cap_noid"), noid)) {
                flag = BOSS;
            } else {
                flag = STAFF;
            }

            if (TextUtils.equals(flag, BOSS)) {
                /*********************************** 资方********************************************/
                holder.imgvAgree.setVisibility(View.GONE);
                holder.imgvStatus.setVisibility(View.GONE);
                holder.imgvPhone.setVisibility(View.VISIBLE);
                holder.linlaySettle.setVisibility(View.GONE);
                holder.linlayTuibu.setVisibility(View.GONE);
                switch (status) {
                    case INVALID: // 待生效
                        holder.imgvPhone.setVisibility(View.GONE);
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
                                                        contract.toUnsettle(map.get("contract_noid"), application.getUserInfo().get("noid"), MyContractAty.this);
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
                                                        contract.toUnsettle(map.get("contract_noid"), application.getUserInfo().get("noid"), MyContractAty.this);
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
                                                contract.toUnsettle(map.get("contract_noid"), application.getUserInfo().get("noid"), MyContractAty.this);
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
                                            contract.toUnsettle(map.get("contract_noid"), application.getUserInfo().get("noid"), MyContractAty.this);
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
                                                        contract.toUnsettle(map.get("contract_noid"), application.getUserInfo().get("noid"), MyContractAty.this);
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
                                                        contract.toUnsettle(map.get("contract_noid"), application.getUserInfo().get("noid"), MyContractAty.this);
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
                                                contract.toUnsettle(map.get("contract_noid"), application.getUserInfo().get("noid"), MyContractAty.this);
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
                                holder.tvTuibu.setText(map.get("adequacy_amount").replace("-", "￥-"));
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
                                if (TextUtils.isEmpty(map.get("issue_scheme"))) {
                                    showToast("提示：暂无调解解方案，请等待……");
                                    return;
                                }
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
                                if (TextUtils.isEmpty(map.get("issue_scheme"))) {
                                    showToast("提示：暂无调解解方案，请等待……");
                                    return;
                                }
                                showDialog("", "确定要同意该调解方案么？", "同意", "取消", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        showProgressDialog();
                                        cont_noid = map.get("contract_noid");
                                        payStutas = "dispute";
                                        issue_id = map.get("issue_id");
                                        contract.isAdequacyAmount(map.get("contract_noid"), noid,
                                                map.get("requested_info"), MyContractAty.this);

//                                        Bundle bundle = new Bundle();
//                                        bundle.putString("contract_noid", map.get("contract_noid"));
//                                        bundle.putString("money", map.get("issue_amount"));
//                                        startActivity(SettlementAty.class, bundle);
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
                        holder.imgvPhone.setVisibility(View.GONE);
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
                                        contract.capAcceptStay(map.get("contract_noid"), noid, MyContractAty.this);
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
                                        contract.capCancelStay(map.get("contract_noid"), noid, MyContractAty.this);
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
                                holder.tvSettle.setText(map.get("requested_info").replace("-", "￥-"));
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
                                                    LogUtil.e(map.get("requested_info"));
                                                    showProgressDialog();
                                                    cont_noid = map.get("contract_noid");
                                                    payStutas = "settle";
                                                    contract.isAdequacyAmount(map.get("contract_noid"), noid,
                                                            map.get("requested_info"), MyContractAty.this);


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
                            holder.linlaySettle.setVisibility(View.GONE);
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
                                        contract.capAcceptProgressStay(map.get("contract_noid"), noid, MyContractAty.this);
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
                                        contract.capCancelProgressStay(map.get("contract_noid"), noid, MyContractAty.this);
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
                        holder.linlaySettle.setVisibility(View.VISIBLE);
                        holder.tvSettleName.setText("结算金额：");
                        if (TextUtils.equals(map.get("adequacy_status"), "1")) {
                            if (map.get("adequacy_amount").contains("-")) {
                                holder.tvSettle.setText(map.get("adequacy_amount").replace("-", "￥-"));
                                holder.tvSettle.setTextColor(Color.parseColor("#fb4a4a"));
                            } else {
                                holder.tvSettle.setText("￥" + map.get("adequacy_amount"));
                                holder.tvSettle.setTextColor(getResources().getColor(R.color.clr_main));
                            }
                        } else {
                            holder.tvSettle.setText("");
                        }
                        holder.btnLook.setVisibility(View.GONE);
                        if (TextUtils.equals(map.get("requested_type"), "4")) {
                            holder.linlayTuibu.setVisibility(View.VISIBLE);

                            holder.tvTuibuName.setText("退补款金额：");
                            if (map.get("requested_info").contains("-")) {
                                holder.tvTuibu.setText(map.get("requested_info").replace("-", "￥-"));
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
                                                        contract.rollbackDrawback(map.get("contract_noid"), noid, MyContractAty.this);
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
//                                        holder.imgvStatus.setImageResource(R.drawable.icon_refunds_refuse);
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
                                                showProgressDialog();
                                                cont_noid = map.get("contract_noid");
                                                payStutas = "tuibu";
                                                contract.isAdequacyAmount(map.get("contract_noid"), noid,
                                                        map.get("requested_info"), MyContractAty.this);

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
                                                Bundle bundle = new Bundle();
                                                bundle.putString("flag", "tuibu");
                                                bundle.putString("contract_noid", map.get("contract_noid"));
                                                startActivity(SettlementAty.class, bundle);
                                            }
                                        }, null);
                                    }
                                });
                            }
                        } else if (TextUtils.isEmpty(map.get("requested_type"))) {
                            holder.linlayTuibu.setVisibility(View.GONE);
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
                                            Bundle bundle = new Bundle();
                                            bundle.putString("flag", "tuibu");
                                            bundle.putString("contract_noid", map.get("contract_noid"));
                                            startActivity(SettlementAty.class, bundle);
                                        }
                                    }, null);
                                }
                            });
                        } else {
                            holder.linlayTuibu.setVisibility(View.GONE);
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
                                holder.tvSettle.setText(map.get("adequacy_amount").replace("-", "￥-"));
                                holder.tvSettle.setTextColor(Color.parseColor("#fb4a4a"));
                            } else {
                                holder.tvSettle.setText("￥" + map.get("adequacy_amount"));
                                holder.tvSettle.setTextColor(getResources().getColor(R.color.clr_main));
                            }
                        } else {
                            holder.tvSettle.setText(map.get("requested_info"));
                        }

                        holder.linlayBtn.setVisibility(View.VISIBLE);
                        holder.btnLook.setVisibility(View.VISIBLE);
                        holder.btnLook.setText("退补款");
                        holder.btnRrlieve.setVisibility(View.VISIBLE);
                        holder.fbtnPay.setText("继续签约");
                        holder.fbtnPay.setVisibility(View.VISIBLE);
                        String evaluate_status = map.get("evaluate_status");
                        String endTime = map.get("end_time");
                        String timeslashData = DateUtils.timeslashData(endTime);
                        long twoDay = DateTool.getDays(timeslashData, DateTool.getStringDateShort());
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
                                            contract.toDrawback(map.get("contract_noid"), noid, MyContractAty.this);
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
                                holder.btnRrlieve.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        showDialog("", "确定要进行退补款吗？", "确定", "取消", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                contract.toDrawback(map.get("contract_noid"), noid, MyContractAty.this);
                                                Bundle bundle = new Bundle();
                                                bundle.putString("flag", "tuibu");
                                                bundle.putString("contract_noid", map.get("contract_noid"));
                                                startActivity(SettlementAty.class, bundle);
                                            }
                                        }, null);
                                    }
                                });
                            } else {
                                holder.btnRrlieve.setText("修改评价");
                                holder.btnLook.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        showDialog("", "确定要进行退补款吗？", "确定", "取消", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                contract.toDrawback(map.get("contract_noid"), noid, MyContractAty.this);
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
                                bundle.putString("contract_noid", map.get("contract_noid"));
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
                        telephone = map.get("telephone");
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
                holder.imgvAgree.setVisibility(View.GONE);
                holder.imgvStatus.setVisibility(View.GONE);
                holder.imgvPhone.setVisibility(View.VISIBLE);
                holder.linlaySettle.setVisibility(View.GONE);
                holder.linlayTuibu.setVisibility(View.GONE);
                switch (status) {
                    case INVALID: // 待生效
                        holder.imgvPhone.setVisibility(View.GONE);
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
                                                        contract.toUnsettle(map.get("contract_noid"), application.getUserInfo().get("noid"), MyContractAty.this);
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
                                                        contract.toUnsettle(map.get("contract_noid"), application.getUserInfo().get("noid"), MyContractAty.this);
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
                                                contract.toUnsettle(map.get("contract_noid"), application.getUserInfo().get("noid"), MyContractAty.this);
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
                                            contract.toUnsettle(map.get("contract_noid"), application.getUserInfo().get("noid"), MyContractAty.this);
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
                                                        contract.toUnsettle(map.get("contract_noid"), application.getUserInfo().get("noid"), MyContractAty.this);
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
                                                        contract.toUnsettle(map.get("contract_noid"), application.getUserInfo().get("noid"), MyContractAty.this);
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
                                                contract.toUnsettle(map.get("contract_noid"), application.getUserInfo().get("noid"), MyContractAty.this);
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
                                holder.tvTuibu.setText(map.get("adequacy_amount").replace("-", "￥-"));
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
                                if (TextUtils.isEmpty(map.get("issue_scheme"))) {
                                    showToast("提示：暂无调解解方案，请等待……");
                                    return;
                                }
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
                                if (TextUtils.isEmpty(map.get("issue_scheme"))) {
                                    showToast("提示：暂无调解解方案，请等待……");
                                    return;
                                }
                                showDialog("", "确定要同意该调解方案么？", "同意", "取消", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        showProgressDialog();
                                        cont_noid = map.get("contract_noid");
                                        payStutas = "dispute";
                                        issue_id = map.get("issue_id");
                                        contract.isAdequacyAmount(map.get("contract_noid"), noid,
                                                map.get("requested_info"), MyContractAty.this);
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
                        holder.imgvPhone.setVisibility(View.GONE);
                        holder.linlayBtn.setVisibility(View.GONE);
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
                                        contract.labAcceptStay(map.get("contract_noid"), noid, MyContractAty.this);
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
                                        contract.labCancelStay(map.get("contract_noid"), noid, MyContractAty.this);
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
                                holder.tvSettle.setText(map.get("requested_info").replace("-", "￥-"));
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
                                                    payStutas = "settle";
                                                    cont_noid = map.get("contract_noid");
                                                    contract.isAdequacyAmount(map.get("contract_noid"), noid,
                                                            map.get("requested_info"), MyContractAty.this);
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
                                        contract.labAcceptProgressStay(map.get("contract_noid"), noid, MyContractAty.this);
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
                                        contract.labCancelProgressStay(map.get("contract_noid"), noid, MyContractAty.this);
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
                        holder.linlaySettle.setVisibility(View.VISIBLE);
                        holder.tvSettleName.setText("结算金额：");
                        if (TextUtils.equals(map.get("adequacy_status"), "1")) {
                            if (map.get("adequacy_amount").contains("-")) {
                                holder.tvSettle.setText(map.get("adequacy_amount").replace("-", "￥-"));
                                holder.tvSettle.setTextColor(Color.parseColor("#fb4a4a"));
                            } else {
                                holder.tvSettle.setText("￥" + map.get("adequacy_amount"));
                                holder.tvSettle.setTextColor(getResources().getColor(R.color.clr_main));
                            }
                        } else {
                            holder.tvSettle.setText(map.get("requested_info"));
                        }
                        if (TextUtils.equals(map.get("requested_type"), "4")) {
                            holder.linlayTuibu.setVisibility(View.VISIBLE);

                            holder.tvTuibuName.setText("退补款金额：");
                            if (map.get("requested_info").contains("-")) {
                                holder.tvTuibu.setText(map.get("requested_info").replace("-", "￥-"));
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
                                                        contract.rollbackDrawback(map.get("contract_noid"), noid, MyContractAty.this);
                                                    }
                                                }, null);
                                            }
                                        });
                                        break;
                                    case 1:
                                        break;
                                    case 2:
//                                        holder.imgvStatus.setVisibility(View.GONE);
                                        holder.imgvStatus.setVisibility(View.VISIBLE);
                                        holder.imgvStatus.setImageResource(R.drawable.icon_refunds_refuse);
                                        holder.fbtnPay.setText("退补款");
                                        holder.fbtnPay.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                showDialog("", "确定要进行退补款吗？", "确定", "取消", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
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
//                                        holder.imgvStatus.setImageResource(R.drawable.icon_refunds_refuse);
                                        holder.btnLook.setVisibility(View.GONE);
                                        holder.btnRrlieve.setVisibility(View.GONE);
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
                                                showProgressDialog();
                                                cont_noid = map.get("contract_noid");
                                                payStutas = "tuibu";
                                                contract.isAdequacyAmount(map.get("contract_noid"), noid,
                                                        map.get("requested_info"), MyContractAty.this);

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
                                                Bundle bundle = new Bundle();
                                                bundle.putString("flag", "tuibu");
                                                bundle.putString("contract_noid", map.get("contract_noid"));
                                                startActivity(SettlementAty.class, bundle);
                                            }
                                        }, null);
                                    }
                                });
                            }
                        } else if (TextUtils.isEmpty(map.get("requested_type"))) {
                            holder.linlayTuibu.setVisibility(View.GONE);
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
                                            Bundle bundle = new Bundle();
                                            bundle.putString("flag", "tuibu");
                                            bundle.putString("contract_noid", map.get("contract_noid"));
                                            startActivity(SettlementAty.class, bundle);
                                        }
                                    }, null);
                                }
                            });
                        } else {
                            holder.linlayTuibu.setVisibility(View.GONE);
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
                                holder.tvSettle.setText(map.get("adequacy_amount").replace("-", "￥-"));
                                holder.tvSettle.setTextColor(Color.parseColor("#fb4a4a"));
                            } else {
                                holder.tvSettle.setText("￥" + map.get("adequacy_amount"));
                                holder.tvSettle.setTextColor(getResources().getColor(R.color.clr_main));
                            }
                        } else {
                            holder.tvSettle.setText(map.get("requested_info"));
                        }

                        holder.linlayBtn.setVisibility(View.VISIBLE);
                        holder.btnLook.setVisibility(View.GONE);
                        holder.btnRrlieve.setVisibility(View.VISIBLE);
                        holder.btnRrlieve.setText("退补款");
                        holder.fbtnPay.setVisibility(View.VISIBLE);
                        String evaluate_status = map.get("evaluate_status");
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
                                        showDialog("", "确定要进行退补款吗？", "确定", "取消", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                contract.toDrawback(map.get("contract_noid"), noid, MyContractAty.this);
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

                            if (TextUtils.equals(map.get("can_evaluate"), "0")) {
                                holder.btnRrlieve.setVisibility(View.GONE);
                                holder.fbtnPay.setText("退补款");
                                holder.fbtnPay.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        showDialog("", "确定要进行退补款吗？", "确定", "取消", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                contract.toDrawback(map.get("contract_noid"), noid, MyContractAty.this);
                                                Bundle bundle = new Bundle();
                                                bundle.putString("flag", "tuibu");
                                                bundle.putString("contract_noid", map.get("contract_noid"));
                                                startActivity(SettlementAty.class, bundle);
                                            }
                                        }, null);
                                    }
                                });
                            } else {
                                holder.btnRrlieve.setVisibility(View.VISIBLE);
                                holder.btnRrlieve.setText("退补款");
                                holder.fbtnPay.setText("修改评价");
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

                        }
                        holder.btnRrlieve.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                showDialog("", "确定要进行退补款吗？", "确定", "取消", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        contract.toDrawback(map.get("contract_noid"), noid, MyContractAty.this);
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
                        telephone = map.get("telephone");
                        showDialog("提示", "是否拨打电话", "确定", "取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                requestPermissions(Constants.PERMISSIONS_CALL_PHONE, Manifest.permission.CALL_PHONE);
                            }
                        }, null);
                    }
                });
            }
            holder.linlayNoid.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    bundle.putString("code", map.get("noid"));
                    if (status == INVALID || status == EXPIRE) {

                    } else {
                        bundle.putString("flag", "contract");
                    }

                    startActivity(MemberDetailAty.class, bundle);
                }
            });
        }

        @Override
        public int getItemCount() {
            return ListUtils.getSize(dataList);
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
