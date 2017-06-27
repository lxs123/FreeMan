package com.toocms.freeman.ui.searchjob;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
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
import com.toocms.frame.tool.Commonly;
import com.toocms.freeman.R;
import com.toocms.freeman.config.JsonArryToList;
import com.toocms.freeman.https.Hire;
import com.toocms.freeman.https.Sys;
import com.toocms.freeman.ui.BaseAty;
import com.toocms.freeman.ui.FreeManToggle;
import com.toocms.freeman.ui.index.ModifyDetailsAty;
import com.toocms.freeman.ui.index.ModifyRecruitmentInformationAty;
import com.toocms.freeman.ui.index.SkillAty;
import com.toocms.freeman.ui.recruitment.AreaListAty;
import com.toocms.freeman.ui.util.WorkOrder;
import com.zero.autolayout.utils.AutoUtils;

import org.xutils.http.RequestParams;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import cn.zero.android.common.recyclerview.RecycleViewDivider;
import cn.zero.android.common.util.JSONUtils;
import cn.zero.android.common.util.ListUtils;
import cn.zero.android.common.util.MapUtils;
import cn.zero.android.common.view.FButton;
import cn.zero.android.common.view.shapeimageview.CircularImageView;
import cn.zero.android.common.view.swipetoloadlayout.OnLoadMoreListener;
import cn.zero.android.common.view.swipetoloadlayout.OnRefreshListener;
import cn.zero.android.common.view.swipetoloadlayout.view.SwipeToLoadRecyclerView;

import static com.toocms.freeman.R.id.address_tv;

/**
 * 05 - 6   搜工作--》我的招工单
 */

public class MyJobOrderInSearchAty extends BaseAty implements OnRefreshListener, OnLoadMoreListener {


    @ViewInject(R.id.tv1)
    private TextView tv1;
    @ViewInject(R.id.tv2)
    private TextView tv2;
    @ViewInject(R.id.tv3)
    private TextView tv3;
    @ViewInject(R.id.tv4)
    private TextView tv4;
    @ViewInject(R.id.my_jo_work)
    private TextView weekTv;    //工作周历返回文字
    @ViewInject(R.id.skill_tv)
    private TextView skillTv;   //工作技能
    @ViewInject(R.id.keywords_edt)
    private EditText keywordsEdt;   //关键词
    @ViewInject(R.id.min_price_edt)
    private EditText minPriceEdt;
    @ViewInject(R.id.max_price_edt)
    private EditText maxPeiceEdt;
    @ViewInject(R.id.is_insurance)
    private FreeManToggle isInSuranceTb;
    @ViewInject(address_tv)
    private TextView tvAddress;
    @ViewInject(R.id.is_dine)
    private FreeManToggle isdineTb;
    @ViewInject(R.id.is_lodging)
    private FreeManToggle isLodgingTb;
    @ViewInject(R.id.is_tool)
    private FreeManToggle isToolTb;
    @ViewInject(R.id.is_transportation_expenses)
    private FreeManToggle isTransportationExpensesTb;
    @ViewInject(R.id.is_correspondence)
    private FreeManToggle isCorrespondenceTb;
    @ViewInject(R.id.time_tv)
    private TextView timeTv;
    @ViewInject(R.id.null_Rl)
    RelativeLayout nullRl;


    @ViewInject(R.id.my_job_order_swipe)
    SwipeToLoadRecyclerView mSwipeToLoadRecyclerView;
    AtyRecruitmentQueryResultAdapter mResultAdapter;
    //侧滑主页
    @ViewInject(R.id.my_job_order_drawer)
    DrawerLayout alreadyRobJobOrderDl;
    //侧滑页
    @ViewInject(R.id.my_job_order_scroll)
    private ScrollView linlayScreen;
    @ViewInject(R.id.my_jo_screen)
    private TextView tvScreen;
    @ViewInject(R.id.linlay)
    LinearLayout mLinearLayout;
    //存接口返回来的周历数据
    private String[] weekArray;
    private String[] weekIdArray;

    //用来处理下划线的属性动画相关
    @ViewInject(R.id.take_title_view)
    private View vLine;
    int position = 1;
    //分页显示
    int p = 1;

    //接口相关
    private Hire mHire;
    private Sys mSys;
    /**
     * 同意招工单[labReplyAccept]
     *
     * @param noid  用户编号
     * @param hire_noid 招工单编号
     * @param cap_noid 资方noid
     */
    public String noid;     //劳方编号
    public String hire_noid;    //招工单编号
    public String cap_noid;     //对方编号
    //存工作相关
    private List<String> skillList;
    private ArrayList<Map<String, String>> skillItemData;

    //sort字段 排序，支持：
    private static final String LEVEL = "level";
    private static final String DISTANCE = "distance";
    private static final String MAXPRICE = "maxprice";
    private static final String MINPRICE = "minprice";
    private static final String WORKFAST = "workfast";
    private static final String WORKLONG = "worklong";
    private String NOWSORT = LEVEL;

    //筛选接口要用到的参数
    private String workWeek;
    private String contractStartTime;
    private String contractEndTime;
    private String workStartTime;
    private String workEndTime;
    //    private String intSkillId;
    private String sort = NOWSORT;    //现在的tab状态
    // TODO: 2017/4/26  地址是空的
    private String distance;    //地址信息
    private int ADDRESS_ID_IN_SERCH = 9788;
    private ArrayList<Map<String, String>> dataList;
    private String mProvinceIdStr;
    private String mCityIdStr;
    private String mAreaIdStr;
    private String mRessStr;
    private int AREA = 1101;
//    private String mDistanceStr;

    @Override
    protected int getLayoutResId() {
        return R.layout.aty_my_job_order_in_search;
    }

    @Override
    protected void initialized() {
        mHire = new Hire();
        mSys = new Sys();
    }

    @Override
    protected void requestData() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WorkOrder.getInstance().clear();
        mActionBar.hide();

        //处理筛选宽度
        WindowManager windowManager = this.getWindowManager();
        ViewGroup.LayoutParams layoutParams = mLinearLayout.getLayoutParams();
        layoutParams.height = windowManager.getDefaultDisplay().getHeight();
        layoutParams.width = windowManager.getDefaultDisplay().getWidth() / 5 * 4;
        mLinearLayout.setLayoutParams(layoutParams);

        mSwipeToLoadRecyclerView.setOnRefreshListener(this);
        mSwipeToLoadRecyclerView.setOnLoadMoreListener(this);
        mSwipeToLoadRecyclerView.getRecyclerView().setLayoutManager(new LinearLayoutManager(this));
        mSwipeToLoadRecyclerView.getRecyclerView().addItemDecoration(new RecycleViewDivider(
                this, LinearLayoutManager.HORIZONTAL, AutoUtils.getPercentHeightSize(20), getResources().getColor(R.color.clr_bg)));

        //侧滑相关
        alreadyRobJobOrderDl.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        alreadyRobJobOrderDl.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {

            }

            @Override
            public void onDrawerOpened(View drawerView) {
                alreadyRobJobOrderDl.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                alreadyRobJobOrderDl.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });

        //初始化表单数据 从hire接口得到工作周历
        mHire.getFormData(this);
        //初始化工作接口
        mSys.getSkillList("0", "0", this);
        doLibrary();

//        updateUI();
    }


    //根据状态调接口的方法
    public void doLibrary() {
        //UI相关内容
        tv3.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.btn_level, 0);
        tv4.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.btn_level, 0);
        if (NOWSORT.equals(MAXPRICE)) {
            tv3.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.btn_level_up, 0);
        } else if (NOWSORT.equals(MINPRICE)) {
            tv3.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.btn_level_down, 0);
        } else if (NOWSORT.equals(WORKFAST)) {
            tv4.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.btn_level_up, 0);
        } else if (NOWSORT.equals(WORKLONG)) {
            tv4.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.btn_level_down, 0);
        }

        showProgressDialog();
        doAcceptHttps();
    }

    //初始化一些监听
    @Event({R.id.my_jo_screen, R.id.my_jo_back, R.id.my_jo_work_ll, R.id.ll_work, R.id.work_time_ll,
            R.id.tv1, R.id.tv2, R.id.ll3, R.id.ll4, R.id.my_jo_sure, R.id.my_jo_work_area, R.id.my_jo_i_s_clear})
    private void onClick(View view) {
        switch (view.getId()) {
            case R.id.my_jo_screen:
                if (!alreadyRobJobOrderDl.isDrawerOpen(linlayScreen)) {
                    alreadyRobJobOrderDl.openDrawer(linlayScreen);
                }
                break;
            case R.id.my_jo_back:
                finish();
                break;
            case R.id.my_jo_work_ll:
                Bundle bundle = new Bundle();
                bundle.putStringArray("weekarray", weekArray);
                bundle.putStringArray("weekidarray", weekIdArray);
                startActivityForResult(WorkweekAty.class, bundle, 1024);
                break;
            case R.id.ll_work:
                startActivity(SkillAty.class, null);
                break;
            case R.id.work_time_ll:
                startActivityForResult(WorkTimeAty.class, null, 9999);
                break;
            case R.id.tv1:
                showProgressDialog();
                p = 1;
                position = 1;
                initTvColor();
                tv1.setTextColor(getResources().getColor(R.color.clr_main));
                startTranslate(vLine, (Settings.displayWidth / 4) * (position - 1));
                if (!NOWSORT.equals(LEVEL)) {
                    p = 1;
                    NOWSORT = LEVEL;
                    doLibrary();
                } else {
                    return;
                }
                break;
            case R.id.tv2:
                showProgressDialog();
                p = 1;
                position = 2;
                initTvColor();
                tv2.setTextColor(getResources().getColor(R.color.clr_main));
                startTranslate(vLine, (Settings.displayWidth / 4) * (position - 1));
                if (!NOWSORT.equals(DISTANCE)) {
                    p = 1;
                    NOWSORT = DISTANCE;
                    doLibrary();
                } else {
                    return;
                }
                tv2.setTextColor(getResources().getColor(R.color.clr_main));
                startTranslate(vLine, (Settings.displayWidth / 4) * (position - 1));
                break;
            case R.id.ll3:
                showProgressDialog();
                p = 1;
                position = 3;
                initTvColor();
                tv3.setTextColor(getResources().getColor(R.color.clr_main));
                startTranslate(vLine, (Settings.displayWidth / 4) * (position - 1));
                if (!(NOWSORT.equals(MAXPRICE) || NOWSORT.equals(MINPRICE))) {
                    p = 1;
                    NOWSORT = MAXPRICE;
                    doLibrary();
                    return;
                }
                if (NOWSORT.equals(MAXPRICE)) {
                    p = 1;
                    NOWSORT = MINPRICE;
                    doLibrary();
                } else {
                    p = 1;
                    NOWSORT = MAXPRICE;
                    doLibrary();
                }
                break;
            case R.id.ll4:
                showProgressDialog();
                p = 1;
                position = 4;
                initTvColor();
                tv4.setTextColor(getResources().getColor(R.color.clr_main));
                startTranslate(vLine, (Settings.displayWidth / 4) * (position - 1));
                if (!(NOWSORT.equals(WORKFAST) || NOWSORT.equals(WORKLONG))) {
                    p = 1;
                    NOWSORT = WORKFAST;
                    doLibrary();
                    return;
                }
                if (NOWSORT.equals(WORKFAST)) {
                    p = 1;
                    NOWSORT = WORKLONG;
                    doLibrary();
                } else {
                    p = 1;
                    NOWSORT = WORKFAST;
                    doLibrary();
                }
                break;
            case R.id.my_jo_work_area:
                startActivityForResult(AreaListAty.class, null, AREA);
                break;
            case R.id.my_jo_sure:
                p = 1;
                //筛选里的确定按钮
                // TODO: 2017/4/26 写接口
                if (alreadyRobJobOrderDl.isDrawerOpen(linlayScreen)) {
                    alreadyRobJobOrderDl.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
                }
                if (!Commonly.getViewText(keywordsEdt).isEmpty() || !Commonly.getViewText(minPriceEdt).isEmpty() ||
                        !Commonly.getViewText(maxPeiceEdt).isEmpty() || !TextUtils.isEmpty(weekTv.getText().toString()) ||
                        !TextUtils.isEmpty(skillTv.getText().toString()) || !TextUtils.isEmpty(timeTv.getText().toString()) ||
                        !TextUtils.isEmpty(tvAddress.getText().toString()) ||
                        isInSuranceTb.getToggle() || isdineTb.getToggle() || isLodgingTb.getToggle() ||
                        isToolTb.getToggle() || isInSuranceTb.getToggle() || isCorrespondenceTb.getToggle()) {
//                    LogUtil.e(Commonly.getViewText(keywordsEdt)+"*"+Commonly.getViewText(minPriceEdt)+"*"+Commonly.getViewText(maxPeiceEdt));
//                    LogUtil.e(weekTv.getText().toString()+"*"+skillTv.getText().toString()+"*"+tvAddress.getText().toString());
//                    LogUtil.e(isInSuranceTb.getToggle()+"*"+isdineTb.getToggle());
                    tvScreen.setText("已筛选");
                } else {
                    tvScreen.setText("筛选");
                }
                showProgressDialog();
                doAcceptHttps();
                break;
            case R.id.my_jo_i_s_clear:
                tvScreen.setText("筛选");
                keywordsEdt.setText("");
                minPriceEdt.setText("");
                maxPeiceEdt.setText("");
                weekTv.setText("");
                skillTv.setText("");
                timeTv.setText("");
                workWeek = "";
                tvAddress.setText("");
                mProvinceIdStr = "";
                mCityIdStr = "";
                mAreaIdStr = "";
                mRessStr = "";
//                mDistanceStr = "";
                WorkOrder.getInstance().clear();
                contractStartTime = "";
                contractEndTime = "";
                workStartTime = "";
                workEndTime = "";
                isInSuranceTb.setToggleOff();
                isdineTb.setToggleOff();
                isTransportationExpensesTb.setToggleOff();
                isLodgingTb.setToggleOff();
                isToolTb.setToggleOff();
                isCorrespondenceTb.setToggleOff();
                break;
        }
    }


    private void doAcceptHttps() {
        mHire.accept(
                application.getUserInfo().get("noid"),
                String.valueOf(p),
                NOWSORT,
                keywordsEdt.getText().toString(),
                minPriceEdt.getText().toString(),
                maxPeiceEdt.getText().toString(),
                "",
//                distance.substring(0,distance.indexOf("K")),
//                (mDistanceStr == null || mDistanceStr == "") ? "" : mDistanceStr,
                contractStartTime,
                contractEndTime,
                workStartTime,
                workEndTime,
                isInSuranceTb.getToggle() ? "1" : "",
                isdineTb.getToggle() ? "1" : "",
                isLodgingTb.getToggle() ? "1" : "",
                isToolTb.getToggle() ? "1" : "",
                isTransportationExpensesTb.getToggle() ? "1" : "",
                isCorrespondenceTb.getToggle() ? "1" : "",
                workWeek,
                mProvinceIdStr,
                mCityIdStr,
                mAreaIdStr,
                this
        );
    }

    @Override
    protected void onResume() {
        super.onResume();

        Log.e("***", "进到了onResume方法中");
//        if (TextUtils.equals(tvScreen.getText().toString(), "筛选")) {
//            WorkOrder.getInstance().clear();
//        }
        List<String> list = new ArrayList<>();
        skillList = new ArrayList<>();
        Map<String, Map<String, List<Map<String, String>>>> workOrder1 = WorkOrder.getInstance().getOrder1();
        if (MapUtils.isEmpty(workOrder1)) {
            skillTv.setText("");
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
            Log.e("***", ListUtils.isEmpty(list) + "技能集合中有多少元素");
            if (ListUtils.isEmpty(list)) skillTv.setText("");
            else skillTv.setText(ListUtils.join(list));
        }
        if (!Commonly.getViewText(keywordsEdt).isEmpty() || !Commonly.getViewText(minPriceEdt).isEmpty() ||
                !Commonly.getViewText(maxPeiceEdt).isEmpty() || !TextUtils.isEmpty(weekTv.getText().toString()) ||
                !TextUtils.isEmpty(skillTv.getText().toString()) || !TextUtils.isEmpty(timeTv.getText().toString()) ||
                !TextUtils.isEmpty(tvAddress.getText().toString()) ||
                isInSuranceTb.getToggle() || isdineTb.getToggle() || isLodgingTb.getToggle() ||
                isToolTb.getToggle() || isInSuranceTb.getToggle() || isCorrespondenceTb.getToggle()) {
//                    LogUtil.e(Commonly.getViewText(keywordsEdt)+"*"+Commonly.getViewText(minPriceEdt)+"*"+Commonly.getViewText(maxPeiceEdt));
//                    LogUtil.e(weekTv.getText().toString()+"*"+skillTv.getText().toString()+"*"+tvAddress.getText().toString());
//                    LogUtil.e(isInSuranceTb.getToggle()+"*"+isdineTb.getToggle());
            tvScreen.setText("已筛选");
        } else {
            tvScreen.setText("筛选");
            skillList.clear();

        }
//
//        //把skill集合中的内容交给数组
//        if (ListUtils.getSize(skillList) != 0) {
//            intSkillId = new String();
//            intSkillId = ListUtils.join(skillList);
//        } else {
//            intSkillId = new String();
//        }
        doLibrary();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        if (data == null) return;
        if (resultCode == 2048) {
            workWeek = data.getStringExtra("intWeekId");
            weekTv.setText(data.getStringExtra("weekname"));
        }
        if (resultCode == 9998) {
            contractStartTime = data.getStringExtra("tv1Str") == null ? "" : data.getStringExtra("tv1Str");
            contractEndTime = data.getStringExtra("tv2Str") == null ? "" : data.getStringExtra("tv2Str");
            workStartTime = data.getStringExtra("tv3Str") == null ? "" : data.getStringExtra("tv3Str");
            workEndTime = data.getStringExtra("tv4Str") == null ? "" : data.getStringExtra("tv4Str");

            timeTv.setText(contractStartTime + "  " + contractEndTime + "  " + workStartTime + "  " + workEndTime);
        }
        if (requestCode == 1212) {
            doLibrary();
        }
        if (resultCode == RESULT_OK && requestCode == AREA) {
            mProvinceIdStr = data.getStringExtra("province_id");
            mCityIdStr = data.getStringExtra("city_id");
            mAreaIdStr = data.getStringExtra("area_id");
            mRessStr = data.getStringExtra("ress");
            tvAddress.setText(data.getStringExtra("str"));
        }
    }


    @Override
    public void onComplete(RequestParams params, String result) {
        if (params.getUri().contains("Hire/getFormData")) {
            String weekStr = JSONUtils.parseDataToMap(result).get("week");
            List<Map<String, String>> mWeekList = JSONUtils.parseKeyAndValueToMapList(weekStr);
            weekArray = new String[ListUtils.getSize(mWeekList)];
            weekIdArray = new String[ListUtils.getSize(mWeekList)];
            for (int i = 0; i < ListUtils.getSize(mWeekList); i++) {
                weekArray[i] = mWeekList.get(i).get("name");
                weekIdArray[i] = mWeekList.get(i).get("sid");
            }

        }

        if (params.getUri().contains("Sys/getSkillList")) {
            skillItemData = JSONUtils.parseDataToMapList(result);
        }

        if (params.getUri().contains("Hire/accept")) {

            if (JSONUtils.parseDataToMapList(result).isEmpty()) {
//                showToast("没有更多的数据了~~");
                mSwipeToLoadRecyclerView.stopLoadMore();
                mSwipeToLoadRecyclerView.stopRefreshing();
                removeProgressDialog();
//                return;
            }
//            showToast(JSONUtils.parseKeyAndValueToMap(result).get("message"));
            if (p == 1)
                dataList = JSONUtils.parseDataToMapList(result);
            else {
                ArrayList<Map<String, String>> arrayList = JSONUtils.parseDataToMapList(result);
                if (ListUtils.isEmpty(arrayList)) {
                    p--;
                }
                dataList.addAll(arrayList);
            }
//            findViewById(R.id.my_jo_i_s_clear).performClick();  //清空一下
            updateUI();
        } else if (params.getUri().contains("Hire/labReplyAccept")) {
            showToast(JSONUtils.parseKeyAndValueToMap(result).get("message"));
            doLibrary();
        } else if (params.getUri().contains("Hire/cancelAccept")) {
            showToast(JSONUtils.parseKeyAndValueToMap(result).get("message"));
            doLibrary();
        }
        super.onComplete(params, result);
    }

    @Override
    public void onError(Map<String, String> error) {
        Log.e("***", error.toString());
        super.onError(error);
    }

    private void initTvColor() {
        tv1.setTextColor(Color.parseColor("#323232"));
        tv2.setTextColor(Color.parseColor("#323232"));
        tv3.setTextColor(Color.parseColor("#323232"));
        tv4.setTextColor(Color.parseColor("#323232"));
    }

    private void updateUI() {

        if (ListUtils.isEmpty(dataList)) {
            nullRl.setVisibility(View.VISIBLE);
        } else {
            nullRl.setVisibility(View.GONE);
        }

        nullRl.setVisibility(View.GONE);
        if (ListUtils.getSize(dataList) < 10 && p == 1) {

            if (mResultAdapter == null) {
                mResultAdapter = new AtyRecruitmentQueryResultAdapter();
                mSwipeToLoadRecyclerView.setAdapter(mResultAdapter);
            } else {
                mResultAdapter.notifyDataSetChanged();
            }
            mSwipeToLoadRecyclerView.stopRefreshing();
            mSwipeToLoadRecyclerView.stopLoadMore();
            return;
        }

        if (p == 1) {
            mResultAdapter = new AtyRecruitmentQueryResultAdapter();
            mSwipeToLoadRecyclerView.setAdapter(mResultAdapter);
        } else {
            if (mResultAdapter == null) {
                mResultAdapter = new AtyRecruitmentQueryResultAdapter();
                mSwipeToLoadRecyclerView.setAdapter(mResultAdapter);
            } else {
                mResultAdapter.notifyDataSetChanged();
            }
        }
        mSwipeToLoadRecyclerView.stopRefreshing();
        mSwipeToLoadRecyclerView.stopLoadMore();
    }


    //下拉刷新监听
    @Override
    public void onRefresh() {
        p = 1;
        doAcceptHttps();
    }

    @Override
    public void onLoadMore() {
        p++;
        doAcceptHttps();
    }

    //===================Adapter相关=================
    public class AtyRecruitmentQueryResultAdapter extends RecyclerView.Adapter<AtyRecruitmentQueryResultAdapter.ViewHolder> {
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(MyJobOrderInSearchAty.this).inflate(R.layout.listitem_query_result, parent, false);
            AtyRecruitmentQueryResultAdapter.ViewHolder viewHolder = new ViewHolder(view);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, final int position) {
            final Map<String, String> map = dataList.get(position);
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
            holder.tvTotal.setText("￥" + map.get("amount"));
            if (map.get("unit_name").contains("每"))
                holder.tvPrice.setText("￥" + map.get("subtotal") + map.get("unit_name").replace("每", "/"));
            else
                holder.tvPrice.setText("￥" + map.get("subtotal") + "/" + map.get("unit_name"));
            holder.tvPayment.setText(map.get("settle_type_name"));
            holder.tvAddress.setText(map.get("ress"));
//            ImageOptions options = new ImageOptions.Builder()
//                    .setFailureDrawableId(R.drawable.icon_photo_none).build();
//            imageLoader.setImageOptions(options);
            if (TextUtils.equals(map.get("photos_count"), "0")) {
                holder.photo.setImageResource(R.drawable.icon_photo_none);
//                holder.photo.setImageResource(R.drawable.icon_photo_none);
            } else {
                imageLoader.disPlay(holder.photo, map.get("cover"));
            }

            holder.tvNum.setText(map.get("photos_count") + "张");
            if (TextUtils.equals(map.get("attestation"), "1")) {
                holder.tvAuthentication.setText("已认证");
            } else {
                holder.tvAuthentication.setText("未认证");
            }

            Log.e("***", getClass().getSimpleName() + ":" + map.get("coor_status"));

            if (map.get("status").equals("2")) {     //招工中
                holder.coorStatusImg.setVisibility(View.VISIBLE);
                if (map.get("coor_diff").equals("0")) {  //左上角：未修改
                    holder.coorStatusImg.setImageDrawable(getResources().getDrawable(R.drawable.icon_unmodify));
                } else {     //左上角：已修改
                    holder.coorStatusImg.setImageDrawable(getResources().getDrawable(R.drawable.icon_modify));
                }

                if (map.get("coor_status").equals("1")) {    //同意招工单去修改
                    holder.fBtnUp.setVisibility(View.VISIBLE);
                    holder.modifyBtn.setVisibility(View.GONE);
                    holder.checkbtn.setVisibility(View.GONE);
                    holder.fBtnUp.setText("取消接单");
                    holder.fBtnUp.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            //取消接单接口
                            showDialog("", "确定要取消该招工单么？", 0, "确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    showProgressDialog();
                                    mHire.cancelAccept(dataList.get(position).get("hire_noid"), application.getUserInfo().get("noid"), MyJobOrderInSearchAty.this);
                                }
                            }, "取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                }
                            });
                        }
                    });
                } else if (map.get("coor_status").equals("2")) {  //修改过招工单等待回复
                    holder.fBtnUp.setVisibility(View.VISIBLE);
                    holder.modifyBtn.setVisibility(View.VISIBLE);
                    holder.checkbtn.setVisibility(View.VISIBLE);
                    holder.checkbtn.setText("查看修改明细");
                    holder.modifyBtn.setText("取消接单");
                    holder.fBtnUp.setText("同意签约");
                    holder.checkbtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Bundle bundle = new Bundle();
                            bundle.putString("flag", "wzw1");
                            bundle.putString("lab_noid", application.getUserInfo().get("noid"));  //劳方noid
                            bundle.putString("hire_noid", dataList.get(position).get("hire_noid")); //招工单noid
                            bundle.putString("noid", dataList.get(position).get("noid"));  //对方编号
                            Log.e("***", hire_noid + "/" + noid);
                            startActivityForResult(ModifyDetailsAty.class, bundle, 1212);
                        }
                    });
                    holder.modifyBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            mHire.cancelAccept(dataList.get(position).get("hire_noid"), application.getUserInfo().get("noid"), MyJobOrderInSearchAty.this);
                        }
                    });
                    holder.fBtnUp.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            showDialog("", "确定要接该招工单么？", "确定", "取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    showProgressDialog();
                                    noid = application.getUserInfo().get("noid");
                                    hire_noid = map.get("hire_noid");
                                    cap_noid = map.get("noid");
                                    mHire.labReplyAccept(noid, hire_noid, cap_noid, MyJobOrderInSearchAty.this);
                                }
                            }, null);
                        }
                    });
                } else if (map.get("coor_status").equals("0")) {  //未操作
                    holder.coorStatusImg.setVisibility(View.GONE);
                    holder.checkbtn.setVisibility(View.GONE);
                    holder.fBtnUp.setVisibility(View.VISIBLE);
                    holder.fBtnUp.setText("接单");
                    holder.modifyBtn.setText("修改");
                    holder.modifyBtn.setVisibility(View.VISIBLE);
                    holder.fBtnUp.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            showDialog("", "确定要接该招工单么？", "确定", "取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    showProgressDialog();
                                    noid = application.getUserInfo().get("noid");
                                    hire_noid = map.get("hire_noid");
                                    cap_noid = map.get("noid");
                                    mHire.labReplyAccept(noid, hire_noid, cap_noid, MyJobOrderInSearchAty.this);
                                }
                            }, null);

                        }
                    });
                    holder.modifyBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Bundle bundle = new Bundle();
                            bundle.putString("flag", "wzw1");
                            bundle.putString("lab_noid", application.getUserInfo().get("noid"));  //劳方noid
                            bundle.putString("hire_noid", dataList.get(position).get("hire_noid")); //招工单noid
                            bundle.putString("noid", dataList.get(position).get("noid"));  //对方编号
                            Log.e("***", hire_noid + "/" + noid);
                            startActivityForResult(ModifyRecruitmentInformationAty.class, bundle, 1212);
                        }
                    });
                }
            } else if (map.get("status").equals("3")) {   //已签约
                holder.bbrqyImg.setVisibility(View.VISIBLE);
                holder.fBtnUp.setVisibility(View.GONE);
                holder.modifyBtn.setVisibility(View.GONE);
                holder.checkbtn.setVisibility(View.GONE);
                holder.coorStatusImg.setVisibility(View.GONE);
            }

            holder.myJobInformationDetail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Bundle bundle = new Bundle();
                    bundle.putString("flag", "wzw1");
                    bundle.putString("lab_noid", application.getUserInfo().get("noid"));  //劳方noid
                    bundle.putString("hire_noid", dataList.get(position).get("hire_noid")); //招工单noid
                    bundle.putString("noid", dataList.get(position).get("noid"));  //对方编号
                    bundle.putString("status", map.get("status"));   //签约状态
                    bundle.putString("coor_status", map.get("coor_status"));
                    bundle.putString("coor_diff", map.get("coor_diff")); //招工单是否被修改
                    startActivityForResult(MyJobOrderDetailAty.class, bundle, 1212);
                }
            });

            holder.informationLl.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Bundle bundle = new Bundle();
                    bundle.putString("flag", "wzw1");
                    bundle.putString("item", "1");
                    bundle.putString("lab_noid", application.getUserInfo().get("noid"));  //劳方noid
                    bundle.putString("hire_noid", dataList.get(position).get("hire_noid")); //招工单noid
                    bundle.putString("noid", dataList.get(position).get("noid"));  //对方编号
                    bundle.putString("status", map.get("status"));   //签约状态
                    bundle.putString("coor_status", map.get("coor_status"));
                    bundle.putString("coor_diff", map.get("coor_diff")); //招工单是否被修改
                    startActivityForResult(MyJobOrderDetailAty.class, bundle, 1212);
                }
            });

//            holder.fBtnUp.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    showDialog("", "确定要接该招工单么？", "确定", "取消", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            showProgressDialog();
//                            noid = application.getUserInfo().get("noid");
//                            hire_noid = map.get("hire_noid");
//                            cap_noid = map.get("noid");
//                            mHire.labReplyAccept(noid, hire_noid, cap_noid, MyJobOrderInSearchAty.this);
//                        }
//                    }, null);
//
//                }
//            });
//            holder.modifyBtn.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    Bundle bundle = new Bundle();
//                    bundle.putString("flag", "wzw1");
//                    bundle.putString("lab_noid", application.getUserInfo().get("noid"));  //劳方noid
//                    bundle.putString("hire_noid", dataList.get(position).get("hire_noid")); //招工单noid
//                    bundle.putString("noid", dataList.get(position).get("noid"));  //对方编号
//                    Log.e("***", hire_noid + "/" + noid);
//                    startActivity(ModifyRecruitmentInformationAty.class, bundle);
//                }
//            });
        }

        @Override
        public int getItemCount() {
            return ListUtils.getSize(dataList);
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            @ViewInject(R.id.job_information_detail_ll)
            LinearLayout myJobInformationDetail;
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
            @ViewInject(R.id.list_q_r_photo)
            ImageView photo;
            @ViewInject(R.id.list_q_r_photo_num)
            TextView tvNum;

            @ViewInject(R.id.check_btn)
            Button checkbtn;    //最左边的btn
            @ViewInject(R.id.modify_btn)
            Button modifyBtn;       //中间btn
            @ViewInject(R.id.release_fbtn)
            FButton fBtnUp;     //最右边btn


            @ViewInject(R.id.bbyqy_img)
            ImageView bryqyImg; //本人已签约
            @ViewInject(R.id.bbyqy_img)
            ImageView bbrqyImg; //被别人签约
            @ViewInject(R.id.coor_status_img)
            ImageView coorStatusImg;    //左上角图片
            @ViewInject(R.id.information_ll)
            LinearLayout informationLl;

            public ViewHolder(View itemView) {
                super(itemView);
                x.view().inject(this, itemView);
                AutoUtils.auto(itemView);
            }
        }
    }

}
