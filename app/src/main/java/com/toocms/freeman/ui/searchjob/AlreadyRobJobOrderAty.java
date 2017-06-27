package com.toocms.freeman.ui.searchjob;

import android.content.DialogInterface;
import android.content.Intent;
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
import android.widget.ScrollView;
import android.widget.TextView;

import com.toocms.frame.image.ImageLoader;
import com.toocms.freeman.R;
import com.toocms.freeman.config.JsonArryToList;
import com.toocms.freeman.https.Hire;
import com.toocms.freeman.https.Sys;
import com.toocms.freeman.ui.BaseAty;
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

import cn.qqtheme.framework.picker.DatePicker;
import cn.zero.android.common.recyclerview.RecycleViewDivider;
import cn.zero.android.common.util.JSONUtils;
import cn.zero.android.common.util.ListUtils;
import cn.zero.android.common.util.MapUtils;
import cn.zero.android.common.view.FButton;
import cn.zero.android.common.view.shapeimageview.CircularImageView;
import cn.zero.android.common.view.swipetoloadlayout.OnLoadMoreListener;
import cn.zero.android.common.view.swipetoloadlayout.OnRefreshListener;
import cn.zero.android.common.view.swipetoloadlayout.view.SwipeToLoadRecyclerView;

/**
 * 搜工作--》已抢招工单
 */

public class AlreadyRobJobOrderAty extends BaseAty implements OnRefreshListener, OnLoadMoreListener {

    @ViewInject(R.id.job_order_swipe)
    SwipeToLoadRecyclerView mSwipeToLoadRecyclerView;
    AtyRecruitmentQueryResultAdapter mResultAdapter;
    @ViewInject(R.id.ch_layout)
    LinearLayout chLayout;      //侧滑菜单
    @ViewInject(R.id.keywords_edt)
    EditText keywordsEdt;   //关键字
    @ViewInject(R.id.my_jo_work_ll)
    LinearLayout myjoWorkLl;    //工作
    @ViewInject(R.id.my_jo_work)
    TextView myjoWorkTv;    //工作返回值
    @ViewInject(R.id.contract_starttime_ll)
    LinearLayout contractStartTimeLl; // 合同开始时间
    @ViewInject(R.id.contract_starttime_tv)
    TextView contractStartTimeTv;   //合同开始时间返回值
    @ViewInject(R.id.contract_endtime_ll)
    LinearLayout contractEndTimeLl;   //合同结束时间
    @ViewInject(R.id.contract_endtime_tv)
    TextView contractEndTimeTv; //合同结束时间返回值
    @ViewInject(R.id.gzdd_tv_ll)
    LinearLayout addressLl; //工作地点
    @ViewInject(R.id.gzdd_tv)
    TextView gaddTv;    //工作地点返回值
    @ViewInject(R.id.min_price_edt)
    EditText minPriceEdt;   //最小价格
    @ViewInject(R.id.max_price_edt)
    EditText maxPriceEdt;   //最大价格
    @ViewInject(R.id.my_jo_sure)
    FButton sureFbtn;   //确定按钮
    @ViewInject(R.id.clear_fbtn)
    FButton clearFbtn;  //清空搜索
    @ViewInject(R.id.null_tv)
    TextView nullTv;

    //侧滑主页
    @ViewInject(R.id.already_rob_job_order_dl)
    DrawerLayout alreadyRobJobOrderDl;
    //侧滑页
    @ViewInject(R.id.my_jo_screen_lay)
    private ScrollView linlayScreen;

    private Hire mHire;
    private Sys mSys;
    int p = 1;//分页

    private String noid;
    private String hire_noid;
    private String cap_noid;
    private ArrayList<Map<String, String>> mDataListMap;    //接口返回的数据

    //存工作相关
    private List<String> skillList;     //技能key
    private ArrayList<Map<String, String>> skillItemData;   //接口中获取的技能信息

    private String mStartDatetv;
    private String mEndDatetv;
    private String mProvinceName;
    private String mCityName;
    private String mName;
    private String mProvinceId;
    private String mCityId;
    private String mAreaId;
    private String mAreaStr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActionBar.hide();
        WorkOrder.getInstance().clear();
        //初始化表单数据 从hire接口得到工作周历
        mHire.getFormData(this);
        //初始化工作接口
        mSys.getSkillList("0", "0", this);
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
        //处理侧滑宽度
        WindowManager wm = this.getWindowManager();//获取屏幕宽高
        ViewGroup.LayoutParams layoutParams = chLayout.getLayoutParams();
        layoutParams.height = wm.getDefaultDisplay().getHeight();
        layoutParams.width = wm.getDefaultDisplay().getWidth() / 4 * 3;
        chLayout.setLayoutParams(layoutParams);

        mSwipeToLoadRecyclerView.setOnRefreshListener(this);
        mSwipeToLoadRecyclerView.setOnLoadMoreListener(this);
        mSwipeToLoadRecyclerView.getRecyclerView().setLayoutManager(new LinearLayoutManager(this));
        mSwipeToLoadRecyclerView.getRecyclerView().addItemDecoration(new RecycleViewDivider(
                this, LinearLayoutManager.HORIZONTAL, AutoUtils.getPercentHeightSize(20), getResources().getColor(R.color.clr_bg)));

//        showProgressDialog();
//        mHire.keepHireList(application.getUserInfo().get("noid"), String.valueOf(p), null, null, null, null, null, null, null, null, null, this);

//        updateUI();
    }


    @Override
    protected void onResume() {
        super.onResume();

        onRefresh();

        List<String> list = new ArrayList<>();
        skillList = new ArrayList<>();
        Map<String, Map<String, List<Map<String, String>>>> workOrder1 = WorkOrder.getInstance().getOrder1();
        if (MapUtils.isEmpty(workOrder1)) {
            myjoWorkTv.setText("");
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
            if (ListUtils.isEmpty(list))
                myjoWorkTv.setText("");
            else
                myjoWorkTv.setText(ListUtils.join(list));
        }

        if (mProvinceName != null || mCityName != null || mName != null)
            gaddTv.setText(mAreaStr);
    }

    //初始化一些监听
    @Event({R.id.my_jo_screen, R.id.my_jo_back, R.id.my_jo_work_ll, R.id.contract_starttime_ll, R.id.contract_endtime_ll, R.id.gzdd_tv_ll, R.id.my_jo_sure, R.id.clear_fbtn})
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
            case R.id.my_jo_work_ll:   //工作
                startActivity(SkillAty.class, null);
                break;
            case R.id.contract_starttime_ll:    //开始时间
                onYearMonthDayPicker(2016, 2049, new DatePicker.OnYearMonthDayPickListener() {
                    @Override
                    public void onDatePicked(String year, String month, String day) {
                        mStartDatetv = year + "-" + month + "-" + day;
                        contractStartTimeTv.setText(mStartDatetv);
                    }
                });
                break;
            case R.id.contract_endtime_ll:      //结束时间
                onYearMonthDayPicker(2016, 2049, new DatePicker.OnYearMonthDayPickListener() {
                    @Override
                    public void onDatePicked(String year, String month, String day) {
                        mEndDatetv = year + "-" + month + "-" + day;
                        contractEndTimeTv.setText(mEndDatetv);
                    }
                });
                break;
            case R.id.gzdd_tv_ll:      //工作地点
                startActivityForResult(AreaListAty.class, null, 123);
                break;
            case R.id.my_jo_sure:       //确定按钮
                p = 1;
                if (alreadyRobJobOrderDl.isDrawerOpen(linlayScreen)) {
                    alreadyRobJobOrderDl.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
                }
                showProgressDialog();
                mHire.keepHireList(application.getUserInfo().get("noid"), String.valueOf(p),
                        keywordsEdt.getText().toString(),
                        ListUtils.join(skillList), mStartDatetv, mEndDatetv, mProvinceId, mCityId, mAreaId,
                        minPriceEdt.getText().toString(), maxPriceEdt.getText().toString(), this);
                break;
            case R.id.clear_fbtn:       //清除
                keywordsEdt.setText("");
                WorkOrder.getInstance().clear();
                skillList.clear();
                myjoWorkTv.setText("");
                gaddTv.setText("");
                mStartDatetv = "";
                mEndDatetv = "";
                contractStartTimeTv.setText("");
                contractEndTimeTv.setText("");
                mProvinceName = "";
                mCityName = "";
                mName = "";
                mProvinceId = "";
                mCityId = "";
                mAreaId = "";
                gaddTv.setText("");
                minPriceEdt.setText("");
                maxPriceEdt.setText("");
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            mProvinceName = data.getStringExtra("province_name");
            mCityName = data.getStringExtra("city_name");
            mName = data.getStringExtra("name");
            mProvinceId = data.getStringExtra("province_id");
            mCityId = data.getStringExtra("city_id");
            mAreaId = data.getStringExtra("area_id");
            mAreaStr = data.getStringExtra("str");
        }
        if (requestCode == 1212) {
            showProgressDialog();
            mHire.keepHireList(application.getUserInfo().get("noid"), String.valueOf(p),
                    keywordsEdt.getText().toString(),
                    ListUtils.join(skillList), mStartDatetv, mEndDatetv, mProvinceId, mCityId, mAreaId,
                    minPriceEdt.getText().toString(), maxPriceEdt.getText().toString(), this);

        }
    }

    private void updateUI() {

        if (ListUtils.getSize(mDataListMap) == 0) {
            nullTv.setVisibility(View.VISIBLE);
//            return;
        } else
            nullTv.setVisibility(View.GONE);

        if (mResultAdapter == null) {
            mResultAdapter = new AtyRecruitmentQueryResultAdapter();
            mSwipeToLoadRecyclerView.setAdapter(mResultAdapter);
        }
        mResultAdapter.notifyDataSetChanged();
        mSwipeToLoadRecyclerView.stopRefreshing();
        mSwipeToLoadRecyclerView.stopLoadMore();

    }

    @Override
    public void onComplete(RequestParams params, String result) {

        if (params.getUri().contains("Sys/getSkillList")) {
            skillItemData = JSONUtils.parseDataToMapList(result);
        }
        if (params.getUri().contains("Hire/keepHireList")) {

            if (ListUtils.isEmpty(JSONUtils.parseDataToMapList(result))) {
//                showToast("没有更多的数据了~~");
                mSwipeToLoadRecyclerView.stopLoadMore();
                mSwipeToLoadRecyclerView.stopRefreshing();
                removeProgressDialog();
//                return;
            }

            if (p == 1) {
                mDataListMap = JSONUtils.parseDataToMapList(result);
            } else {
                ArrayList<Map<String, String>> arrayList = JSONUtils.parseDataToMapList(result);
                mDataListMap.addAll(arrayList);
                if (ListUtils.isEmpty(arrayList)) {
                    p--;
                }
            }
            updateUI();
        }
        if (params.getUri().contains("Hire/cancelAccept")) {
            showToast(JSONUtils.parseKeyAndValueToMap(result).get("message"));
            showProgressDialog();
//            mHire.keepHireList(application.getUserInfo().get("noid"), String.valueOf(p),
//                    keywordsEdt.getText().toString(),
//                    ListUtils.join(skillList), mStartDatetv, mEndDatetv, mProvinceId, mCityId, mAreaId,
//                    minPriceEdt.getText().toString(), maxPriceEdt.getText().toString(), this);
            onRefresh();
        }
        if (params.getUri().contains("Hire/labReplyAccept")) {
            showToast(JSONUtils.parseKeyAndValueToMap(result).get("message"));
            showProgressDialog();
//            mHire.keepHireList(application.getUserInfo().get("noid"), String.valueOf(p),
//                    keywordsEdt.getText().toString(),
//                    ListUtils.join(skillList), mStartDatetv, mEndDatetv, mProvinceId, mCityId, mAreaId,
//                    minPriceEdt.getText().toString(), maxPriceEdt.getText().toString(), this);
            onRefresh();
        }
        super.onComplete(params, result);
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.aty_already_rob_job_order;
    }

    @Override
    protected void initialized() {
        mHire = new Hire();
        mSys = new Sys();
    }

    @Override
    protected void requestData() {

    }

    //下拉刷新监听
    @Override
    public void onRefresh() {
        p = 1;
//        showProgressDialog();
        mHire.keepHireList(application.getUserInfo().get("noid"), String.valueOf(p),
                keywordsEdt.getText().toString(),
                ListUtils.join(skillList), mStartDatetv, mEndDatetv, mProvinceId, mCityId, mAreaId,
                minPriceEdt.getText().toString(), maxPriceEdt.getText().toString(), this);
    }

    //上拉加载
    @Override
    public void onLoadMore() {
        p++;
//        showProgressDialog();
        mHire.keepHireList(application.getUserInfo().get("noid"), String.valueOf(p),
                keywordsEdt.getText().toString(),
                ListUtils.join(skillList), mStartDatetv, mEndDatetv, mProvinceId, mCityId, mAreaId,
                minPriceEdt.getText().toString(), maxPriceEdt.getText().toString(), this);
    }


    //===================Adapter相关=================
    public class AtyRecruitmentQueryResultAdapter extends RecyclerView.Adapter<AtyRecruitmentQueryResultAdapter.ViewHolder> {

        ImageLoader mImageLoader;

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(AlreadyRobJobOrderAty.this).inflate(R.layout.listitem_query_result, parent, false);
            AtyRecruitmentQueryResultAdapter.ViewHolder viewHolder = new ViewHolder(view);
            mImageLoader = new ImageLoader();
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, final int position) {

            final Map<String, String> map = mDataListMap.get(position);
            mImageLoader.disPlay(holder.headCir, map.get("head"));
            holder.memberNumberTv.setText(map.get("noid"));
            holder.nicknameTv.setText(map.get("nickname"));
            holder.freemanTv.setText(map.get("role_name"));
            holder.sexTv.setText(map.get("sex_name"));
            if (TextUtils.equals(map.get("photos_count"), "0")) {
                holder.listQRPhotoimg.setImageResource(R.drawable.icon_photo_none);
            } else {
                mImageLoader.disPlay(holder.listQRPhotoimg, map.get("cover"));
            }

//            mImageLoader.disPlay(holder.listQRPhotoimg, map.get("cover"));
            holder.listQRPhotoNumTv.setText(map.get("photos_count") + "张");
            holder.listQRSkillTv.setText(ListUtils.join(JsonArryToList.strList(map.get("skill"))));
            holder.listQRStartDateTv.setText(map.get("contract_starttime"));
            holder.listQREndDateTv.setText(map.get("contract_endtime"));
            holder.listQRTotalTv.setText("￥" + map.get("amount"));
            if (map.get("unit_name").contains("每"))
                holder.listQRPriceTv.setText("￥" + map.get("subtotal") + map.get("unit_name").replace("每", "/"));
            else
                holder.listQRPriceTv.setText("￥" + map.get("subtotal") + "/" + map.get("unit_name"));
            holder.listQRPaymentTv.setText(map.get("settle_type_name"));
            holder.listQRAddressTv.setText(map.get("ress"));
            String evaluate_score = map.get("evaluate_score");
            if (TextUtils.equals(evaluate_score, "0")) {
//                holder.imgvCreditRating.setVisibility(View.VISIBLE);
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

            holder.btn1.setVisibility(View.GONE);
            holder.btn2.setVisibility(View.GONE);
            holder.fbtn3.setVisibility(View.GONE);
            holder.bbyqyImg.setVisibility(View.GONE);
            holder.bbrqyImg.setVisibility(View.GONE);

            if (map.get("status").equals("2")) {     //招工中
                holder.coorStatusImg.setVisibility(View.VISIBLE);
                if (map.get("coor_diff").equals("0")) {  //左上角：未修改
                    holder.coorStatusImg.setImageDrawable(getResources().getDrawable(R.drawable.icon_unmodify));
                } else {     //左上角：已修改
                    holder.coorStatusImg.setImageDrawable(getResources().getDrawable(R.drawable.icon_modify));
                }

                if (map.get("coor_status").equals("1")) {    //同意招工单去修改
                    holder.fbtn3.setVisibility(View.VISIBLE);
                    holder.btn2.setVisibility(View.GONE);
                    holder.btn1.setVisibility(View.GONE);
                    holder.fbtn3.setText("取消接单");
                    holder.fbtn3.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            //取消接单接口
                            showDialog("", "确定取消该招工单吗？", 0, "确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    showProgressDialog();
                                    mHire.cancelAccept(mDataListMap.get(position).get("hire_noid"), application.getUserInfo().get("noid"), AlreadyRobJobOrderAty.this);
                                }
                            }, "取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                }
                            });
                        }
                    });
                } else if (map.get("coor_status").equals("2") || map.get("coor_status").equals("3")) {  //修改过招工单等待回复
                    holder.fbtn3.setVisibility(View.GONE);
                    holder.btn2.setVisibility(View.VISIBLE);
                    holder.btn1.setVisibility(View.VISIBLE);
                    holder.btn1.setText("查看修改明细");
                    holder.btn2.setText("取消接单");
                    holder.fbtn3.setText("同意签约");
                    holder.btn1.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Bundle bundle = new Bundle();
                            bundle.putString("flag", "wzw1");
                            bundle.putString("lab_noid", application.getUserInfo().get("noid"));  //劳方noid
                            bundle.putString("hire_noid", mDataListMap.get(position).get("hire_noid")); //招工单noid
                            bundle.putString("noid", mDataListMap.get(position).get("noid"));  //对方编号
                            Log.e("***", hire_noid + "/" + noid);
                            startActivityForResult(ModifyDetailsAty.class, bundle, 1212);
                        }
                    });
                    holder.btn2.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            showDialog("", "确定要取消该招工单吗？", "确定", "取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    showProgressDialog();
                                    mHire.cancelAccept(mDataListMap.get(position).get("hire_noid"), application.getUserInfo().get("noid"), AlreadyRobJobOrderAty.this);
                                }
                            }, null);

                        }
                    });
                    holder.fbtn3.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            showDialog("", "确定要接该招工单么？", "确定", "取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    showProgressDialog();
                                    noid = application.getUserInfo().get("noid");
                                    hire_noid = map.get("hire_noid");
                                    cap_noid = map.get("noid");
                                    mHire.labReplyAccept(noid, hire_noid, cap_noid, AlreadyRobJobOrderAty.this);
                                }
                            }, null);
                        }
                    });
                } else if (map.get("coor_status").equals("0")) {  //未操作
                    holder.btn1.setVisibility(View.GONE);
                    holder.fbtn3.setVisibility(View.VISIBLE);
                    holder.fbtn3.setText("接单");
                    holder.btn2.setText("修改");
                    holder.btn2.setVisibility(View.VISIBLE);
                    holder.fbtn3.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            showDialog("", "确定要接该招工单么？", "确定", "取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    showProgressDialog();
                                    noid = application.getUserInfo().get("noid");
                                    hire_noid = map.get("hire_noid");
                                    cap_noid = map.get("noid");
                                    mHire.labReplyAccept(noid, hire_noid, cap_noid, AlreadyRobJobOrderAty.this);
                                }
                            }, null);

                        }
                    });
                    holder.btn2.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Bundle bundle = new Bundle();
                            bundle.putString("flag", "wzw1");
                            bundle.putString("lab_noid", application.getUserInfo().get("noid"));  //劳方noid
                            bundle.putString("hire_noid", mDataListMap.get(position).get("hire_noid")); //招工单noid
                            bundle.putString("noid", mDataListMap.get(position).get("noid"));  //对方编号
                            Log.e("***", hire_noid + "/" + noid);
                            startActivityForResult(ModifyRecruitmentInformationAty.class, bundle, 1212);
                        }
                    });
                }
            } else if (map.get("status").equals("3")) {   //已签约
                holder.bbrqyImg.setVisibility(View.VISIBLE);
                holder.btn1.setVisibility(View.GONE);
                holder.btn2.setVisibility(View.GONE);
                holder.fbtn3.setVisibility(View.GONE);
                holder.coorStatusImg.setVisibility(View.GONE);
                if (TextUtils.equals(map.get("coor_status"), "5")) {
                    holder.bbrqyImg.setImageResource(R.drawable.icon_signed_me);
                } else {
                    holder.bbrqyImg.setImageResource(R.drawable.icon_signed_other);
                }
                // TODO: 2017/5/16 判断
            }

            holder.informationLl.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Bundle bundle = new Bundle();
                    bundle.putString("item", "1");   //跳转时跳到基本信息
                    bundle.putString("flag", "wzw1");
                    bundle.putString("lab_noid", application.getUserInfo().get("noid"));  //劳方noid
                    bundle.putString("hire_noid", map.get("hire_noid")); //招工单noid
                    bundle.putString("noid", map.get("noid"));  //对方编号
                    bundle.putString("status", map.get("status"));   //签约状态
                    bundle.putString("coor_diff", map.get("coor_diff")); //招工单是否被修改
                    bundle.putString("coor_status", map.get("coor_status"));
                    Log.e("***", "测试：" + map.get("coor_status"));
                    startActivityForResult(AlreadyRobJobOrderDetailAty.class, bundle, 1212);
                }
            });

            holder.jobInformationDetailLl.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // TODO: 2017/3/29 需要传接口信息
                    Bundle bundle = new Bundle();
                    bundle.putString("flag", "wzw1");
                    bundle.putString("lab_noid", application.getUserInfo().get("noid"));  //劳方noid
                    bundle.putString("hire_noid", map.get("hire_noid")); //招工单noid
                    bundle.putString("noid", map.get("noid"));  //对方编号
                    bundle.putString("status", map.get("status"));   //签约状态
                    bundle.putString("coor_diff", map.get("coor_diff")); //招工单是否被修改
                    bundle.putString("coor_status", map.get("coor_status"));
                    Log.e("***", "测试：" + map.get("coor_status"));
                    startActivityForResult(AlreadyRobJobOrderDetailAty.class, bundle, 1212);
                }
            });
        }

        @Override
        public int getItemCount() {
            return ListUtils.getSize(mDataListMap);
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            @ViewInject(R.id.job_information_detail_ll)
            LinearLayout jobInformationDetailLl;
            @ViewInject(R.id.coor_status_img)
            ImageView coorStatusImg;    //左上角的认证状态
            @ViewInject(R.id.header_cir)
            CircularImageView headCir;  //头像
            @ViewInject(R.id.authentication_tv)
            TextView authenticationTv;  //头像下面的认证状态
            @ViewInject(R.id.member_number_tv)
            TextView memberNumberTv;    //会员编号
            @ViewInject(R.id.nickname_tv)
            TextView nicknameTv;    //昵称
            @ViewInject(R.id.freeman_tv)
            TextView freemanTv; //单位/自由人
            @ViewInject(R.id.sex_tv)
            TextView sexTv; //性别
            @ViewInject(R.id.credit_rating_img)
            ImageView imgvCreditRating;  //五颗星
            @ViewInject(R.id.list_q_r_photo)
            ImageView listQRPhotoimg;   //照片
            @ViewInject(R.id.list_q_r_photo_num)
            TextView listQRPhotoNumTv;  //照片数量
            @ViewInject(R.id.list_q_r_skill)
            TextView listQRSkillTv;     //工作
            @ViewInject(R.id.list_q_r_start_date)
            TextView listQRStartDateTv; //合同开始时间
            @ViewInject(R.id.list_q_r_end_date)
            TextView listQREndDateTv; //合同截至时间
            @ViewInject(R.id.list_q_r_total)
            TextView listQRTotalTv; //金额
            @ViewInject(R.id.list_q_r_price)
            TextView listQRPriceTv; //单价
            @ViewInject(R.id.list_q_r_payment)
            TextView listQRPaymentTv;   //完工即结
            @ViewInject(R.id.list_q_r_address)
            TextView listQRAddressTv;   //地址
            @ViewInject(R.id.check_btn)
            Button btn1;
            @ViewInject(R.id.modify_btn)
            Button btn2;
            @ViewInject(R.id.release_fbtn)
            FButton fbtn3;
            @ViewInject(R.id.bbyqy_img)
            ImageView bbyqyImg; //本人已签约
            @ViewInject(R.id.bbrqy_img)
            ImageView bbrqyImg; //被别人签约
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
