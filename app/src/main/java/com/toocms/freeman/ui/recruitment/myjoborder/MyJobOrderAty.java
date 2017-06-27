package com.toocms.freeman.ui.recruitment.myjoborder;

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
import android.widget.ScrollView;
import android.widget.TextView;

import com.toocms.frame.image.ImageLoader;
import com.toocms.freeman.R;
import com.toocms.freeman.config.JsonArryToList;
import com.toocms.freeman.https.Hire;
import com.toocms.freeman.https.Sys;
import com.toocms.freeman.ui.BaseAty;
import com.toocms.freeman.ui.index.SkillAty;
import com.toocms.freeman.ui.index.ViewFeedbackAty;
import com.toocms.freeman.ui.recruitment.RecruitAddressAty;
import com.toocms.freeman.ui.recruitment.joborder.CreateTimeAty;
import com.toocms.freeman.ui.recruitment.joborder.NewJobOrderAty;
import com.toocms.freeman.ui.recruitment.joborder.ReleaseJOAty;
import com.toocms.freeman.ui.util.WorkOrder;
import com.zero.autolayout.utils.AutoUtils;

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
import cn.zero.android.common.util.JSONUtils;
import cn.zero.android.common.util.ListUtils;
import cn.zero.android.common.util.MapUtils;
import cn.zero.android.common.view.FButton;
import cn.zero.android.common.view.swipetoloadlayout.OnLoadMoreListener;
import cn.zero.android.common.view.swipetoloadlayout.OnRefreshListener;
import cn.zero.android.common.view.swipetoloadlayout.view.SwipeToLoadRecyclerView;
import cn.zero.android.common.view.swipetoloadlayout.view.listener.OnItemClickListener;

/**
 * 我的招工单
 * Created by admin on 2017/3/24.
 */

public class MyJobOrderAty extends BaseAty implements OnRefreshListener, OnItemClickListener, OnLoadMoreListener {
    public static final int CREAT_TIME = 2017;
    private static final int ADDRESS_ID = 0x0001;
    //初始化上面4个按钮
    @ViewInject(R.id.tv1)
    TextView tv1;
    @ViewInject(R.id.tv2)
    TextView tv2;
    @ViewInject(R.id.tv3)
    TextView tv3;
    @ViewInject(R.id.tv4)
    TextView tv4;
    @ViewInject(R.id.my_jo_min_price)
    EditText editMinPrice;
    @ViewInject(R.id.my_jo_max_price)
    EditText editMaxPrice;
    @ViewInject(R.id.my_jo_min_money)
    EditText editMinMoney;
    @ViewInject(R.id.my_jo_max_money)
    EditText editMaxMoney;

    @ViewInject(R.id.my_jo_list)
    private SwipeToLoadRecyclerView swipeToLoadRecyclerView;
    @ViewInject(R.id.my_jo_drawer)
    private DrawerLayout drawerLayout;
    @ViewInject(R.id.my_jo_screen_lay)
    private ScrollView linlayScreen;
    @ViewInject(R.id.my_jo_screen_contrnt)
    private LinearLayout linlayScreenCont;
    @ViewInject(R.id.my_jo_screen)
    private TextView tvScreen;
    @ViewInject(R.id.my_jo_end_day_content)
    private TextView tvEndDay;
    @ViewInject(R.id.my_jo_start_day_content)
    private TextView tvStartDay;
    @ViewInject(R.id.my_jo_time)
    private TextView tvTime;
    @ViewInject(R.id.my_jo_keywords)
    EditText editKeywords;
    @ViewInject(R.id.my_jo_work)
    private TextView tvWork;
    @ViewInject(R.id.my_jo_address)
    TextView tvAddress;
    @ViewInject(R.id.my_jo_empty)
    TextView tvEmpty;
    private String startMonth;
    private String startDay;
    private String startYear;

    /**
     * 我的招工单列表[listing]
     *
     * @param noid               用户编号
     * @param status             招工单状态，默认为全部 1为未发布 2为已发布 3为已签约
     * @param keywords           关键词
     * @param skill              技能数组
     * @param contract_starttime 合同开始日期
     * @param contract_endtime   合同结束日期
     * @param start_time         创建开始日期
     * @param end_time           创建结束日期
     * @param min_price          最小单价
     * @param max_price          最大单价
     * @param min_amount         最小金额
     * @param max_amount         最大金额
     * @param page               分页
     */
    private String noid;
    private Hire hire;
    private ArrayList<Map<String, String>> list;
    private int anInt;
    private String status;
    private String keywords;
    private String skill;
    private String contract_starttime;
    private String contract_endtime;
    private String start_time;
    private String end_time;
    private String min_price;
    private String max_price;
    private String min_amount;
    private String max_amount;
    private String page = "1";
    private ArrayList<String> skillList;
    private List<Map<String, String>> skillItemData;
    private Sys sys;
    private String endYear;
    private String endMonth;
    private String endDay;
    private MyJOAdapter myJOAdapter;
    private ArrayList<Map<String, String>> list1;
    private String latitude;
    private String longitude;
    int p = 1;

    @Override
    protected int getLayoutResId() {
        return R.layout.aty_my_job_order;
    }

    @Override
    protected void initialized() {
        hire = new Hire();
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
        WorkOrder.getInstance().clear();
//        MoneyUtils.setPrice(editMaxMoney);
//        MoneyUtils.setPrice();
        tv1.setTextColor(getResources().getColor(R.color.clr_main));
        WindowManager wm = this.getWindowManager();//获取屏幕宽高
        ViewGroup.LayoutParams layoutParams = linlayScreenCont.getLayoutParams();
        layoutParams.height = wm.getDefaultDisplay().getHeight();
        layoutParams.width = wm.getDefaultDisplay().getWidth() / 4 * 3;
        linlayScreenCont.setLayoutParams(layoutParams);
        swipeToLoadRecyclerView.setOnRefreshListener(this);
        swipeToLoadRecyclerView.setOnItemClickListener(this);
        swipeToLoadRecyclerView.setOnLoadMoreListener(this);
        swipeToLoadRecyclerView.getRecyclerView().setLayoutManager(new LinearLayoutManager(this));
//        drawerLayout.setLayoutParams(new DrawerLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
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
    }

    @Override
    protected void onResume() {
        super.onResume();
//        if (TextUtils.equals(tvScreen.getText().toString(), "筛选")) {
//            WorkOrder.getInstance().clear();
//        }

        initSkill();
        if (TextUtils.isEmpty(tvStartDay.getText()) && TextUtils.isEmpty(tvEndDay.getText()) && TextUtils.isEmpty(max_amount) &&
                TextUtils.isEmpty(max_price) && TextUtils.isEmpty(min_amount) && TextUtils.isEmpty(min_price) && TextUtils.isEmpty(tvTime.getText()) &&
                TextUtils.isEmpty(keywords) && TextUtils.isEmpty(tvWork.getText().toString()) && TextUtils.isEmpty(latitude)) {
            tvScreen.setText("筛选");
            skillList.clear();
            skill = "";
        } else
            tvScreen.setText("已筛选");
        showProgressContent();
        noid = application.getUserInfo().get("noid");
        hire.listing(noid, status, keywords, skill, contract_starttime, contract_endtime, start_time, end_time
                , min_price, max_price, min_amount, max_amount, page, longitude, latitude, this);
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
//            skill = ListUtils.join(skillList);
            if (ListUtils.isEmpty(list)) tvWork.setText("");
            else tvWork.setText(ListUtils.join(list));

        }
    }


    @Override
    public void onComplete(RequestParams params, String result) {
        if (params.getUri().contains("Hire/listing")) {
            if (p == 1) {
                list = JSONUtils.parseDataToMapList(result);
                list1 = new ArrayList<>();

            } else {
                ArrayList<Map<String, String>> list1 = JSONUtils.parseDataToMapList(result);
                if (ListUtils.isEmpty(list1)) {
                    p--;
                    page = p + "";
                }
                list.addAll(list1);
            }
            if (myJOAdapter == null) {
                myJOAdapter = new MyJOAdapter();
                swipeToLoadRecyclerView.setAdapter(myJOAdapter);
            } else {
                myJOAdapter.notifyDataSetChanged();
            }
            if (ListUtils.isEmpty(list)) {
                tvEmpty.setVisibility(View.VISIBLE);
            } else {
                tvEmpty.setVisibility(View.GONE);
            }
        } else if (params.getUri().contains("Sys/getSkillList")) {
            skillItemData = JSONUtils.parseDataToMapList(result);
        }
        super.onComplete(params, result);
        swipeToLoadRecyclerView.stopRefreshing();
        swipeToLoadRecyclerView.stopLoadMore();
    }

    @Event({R.id.my_jo_back, R.id.my_jo_screen, R.id.my_jo_time_click, R.id.my_jo_work_click
            , R.id.my_jo_sure, R.id.tv1, R.id.tv2, R.id.tv3, R.id.tv4, R.id.my_jo_end_day,
            R.id.my_jo_start_day, R.id.my_jo_clear, R.id.my_jo_address_click})
    private void onClick(View view) {
        keywords = editKeywords.getText().toString();
        if (!TextUtils.isEmpty(startYear))
            contract_starttime = startYear + "-" + startMonth + "-" + startDay;
        if (!TextUtils.isEmpty(endYear))
            contract_endtime = endYear + "-" + endMonth + "-" + endDay;
        skill = ListUtils.join(skillList);
        switch (view.getId()) {
            case R.id.my_jo_back:
                finish();
                break;
            case R.id.my_jo_screen:
                if (!drawerLayout.isDrawerOpen(linlayScreen)) {
                    drawerLayout.openDrawer(linlayScreen);
                }
                break;
            case R.id.my_jo_time_click:
                startActivityForResult(CreateTimeAty.class, null, CREAT_TIME);
                break;
            case R.id.my_jo_work_click:
                startActivity(SkillAty.class, null);
                break;
            case R.id.my_jo_sure:
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
                min_price = minPrice;
                max_price = maxPrice;
                if (!TextUtils.isEmpty(maxPrice) && !TextUtils.isEmpty(minPrice)) {
                    if (Integer.parseInt(maxPrice) < Integer.parseInt(minPrice)) {
                        editMaxPrice.setText(minPrice);
                        editMinPrice.setText(maxPrice);
                        min_price = maxPrice;
                        max_price = minPrice;
                    }
                }
                if (TextUtils.isEmpty(tvStartDay.getText()) && TextUtils.isEmpty(tvEndDay.getText()) && TextUtils.isEmpty(maxMoney) &&
                        TextUtils.isEmpty(maxPrice) && TextUtils.isEmpty(minMoney) && TextUtils.isEmpty(minPrice) && TextUtils.isEmpty(tvTime.getText()) &&
                        TextUtils.isEmpty(keywords) && TextUtils.isEmpty(tvWork.getText().toString()) && TextUtils.isEmpty(latitude)) {
                    tvScreen.setText("筛选");
                } else
                    tvScreen.setText("已筛选");
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
                showProgressDialog();
                initData();
                break;
            case R.id.tv1:
                initTvColor();
                tv1.setTextColor(getResources().getColor(R.color.clr_main));
                status = "0";
                swipeToLoadRecyclerView.startRefreshing();

//                initData();
                break;
            case R.id.tv2:
                initTvColor();
                tv2.setTextColor(getResources().getColor(R.color.clr_main));
                status = "1";
                swipeToLoadRecyclerView.startRefreshing();
//                initData();
                break;
            case R.id.tv3:
                initTvColor();
                tv3.setTextColor(getResources().getColor(R.color.clr_main));
                status = "2";
                swipeToLoadRecyclerView.startRefreshing();
//                initData();
                break;
            case R.id.tv4:
                initTvColor();
                tv4.setTextColor(getResources().getColor(R.color.clr_main));
                status = "3";
                swipeToLoadRecyclerView.startRefreshing();

                break;
            case R.id.my_jo_end_day:

                DatePicker datePicker = onYearMonthDayPicker(2000, 2217, true, new DatePicker.OnYearMonthDayPickListener() {
                    @Override
                    public void onDatePicked(String year, String month, String day) {
                        endYear = year;
                        endMonth = month;
                        endDay = day;
                        if (!TextUtils.isEmpty(startYear)) {
                            if (Integer.parseInt(year) > Integer.parseInt(startYear)) {
                                tvEndDay.setText(year + "-" + month + "-" + day);
                            } else if (Integer.parseInt(year) == Integer.parseInt(startYear)) {
                                if (Integer.parseInt(month) > Integer.parseInt(startMonth))
                                    tvEndDay.setText(year + "-" + month + "-" + day);
                                else if (Integer.parseInt(month) == Integer.parseInt(startMonth)) {
                                    if (Integer.parseInt(day) >= Integer.parseInt(startDay)) {
                                        tvEndDay.setText(year + "-" + month + "-" + day);
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
                            tvEndDay.setText(year + "-" + month + "-" + day);
                        }

                    }
                });
                if (!TextUtils.isEmpty(startYear))
                    datePicker.setSelectedItem(Integer.parseInt(startYear), Integer.parseInt(startMonth), Integer.parseInt(startDay));
                datePicker.show();
//                } else {
//                    showToast("请先选择合同开始时间");
//                    return;
//                }
                break;
            case R.id.my_jo_address_click:
                Bundle addBundle = new Bundle();
                addBundle.putString("flag", "new_jo");
                startActivityForResult(RecruitAddressAty.class, addBundle, ADDRESS_ID);
                break;
            case R.id.my_jo_start_day:
                onYearMonthDayPicker(2017, 2217, new DatePicker.OnYearMonthDayPickListener() {
                    @Override
                    public void onDatePicked(String year, String month, String day) {
                        startYear = year;
                        startMonth = month;
                        startDay = day;
                        final Calendar c = Calendar.getInstance();

                        int mYear = c.get(Calendar.YEAR); //获取当前年份

                        int mMonth = c.get(Calendar.MONTH);//获取当前月份

                        int mDay = c.get(Calendar.DAY_OF_MONTH);//获取当前月份的日期号码
//                        判断合同开始日期是否大于当前日期
//                        if (Integer.parseInt(year) > mYear) {
//                            if (Integer.parseInt(year) == mYear && Integer.parseInt(month) > mMonth) {
//                                if (Integer.parseInt(month) == mMonth && Integer.parseInt(day) > mDay) {
//
//                                } else {
//                                    showToast("合同开始时间必须大于当前时间");
//                                }
//                            }else showToast("合同开始时间必须大于当前时间");
//                        }else {
//                            showToast("合同开始时间必须大于当前时间");
//                        }
                        tvStartDay.setText(year + "-" + month + "-" + day);
                        tvEndDay.setText("");
                    }
                });
                break;
            case R.id.my_jo_clear:
                tvEndDay.setText("");
                endYear = "";
                endMonth = "";
                endDay = "";
                contract_endtime = "";
                tvStartDay.setText("");
                contract_starttime = "";
                startYear = "";
                startMonth = "";
                startDay = "";
                tvScreen.setText("筛选");
                editMinMoney.setText("");
                editMinPrice.setText("");
                editMaxMoney.setText("");
                editMaxPrice.setText("");
                tvTime.setText("");
                tvWork.setText("");
                WorkOrder.getInstance().clear();
                skillList.clear();
                editKeywords.setText("");
//                status = "1";
                keywords = "";
                skill = "";
                start_time = "";
                end_time = "";
                min_price = " ";
                max_price = "";
                min_amount = "";
                max_amount = "";
                tvAddress.setText("");
                longitude = "";
                latitude = "";
                hire.listing(noid, status, keywords, skill, contract_starttime, contract_endtime, start_time, end_time
                        , min_price, max_price, min_amount, max_amount, page, longitude, latitude, this);
                break;
        }

    }

    private void initData() {

        hire.listing(noid, status, keywords, skill, contract_starttime, contract_endtime, start_time, end_time
                , min_price, max_price, min_amount, max_amount, page, longitude, latitude, this);
    }


    //初始化上面四个按钮的颜色
    private void initTvColor() {
        tv1.setTextColor(Color.parseColor("#656565"));
        tv2.setTextColor(Color.parseColor("#656565"));
        tv3.setTextColor(Color.parseColor("#656565"));
        tv4.setTextColor(Color.parseColor("#656565"));
    }

    @Override
    public void onRefresh() {
//        swipeToLoadRecyclerView.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                swipeToLoadRecyclerView.stopRefreshing();
//            }
//        }, 1500);
        page = 1 + "";
        p = 1;
        initData();

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
                    start_time = startY + "-" + strStartM + "-" + strStartD;
                }

                if (endY != 0) {
                    if (endD < 10) {
                        strEndD = "0" + endD;
                    }
                    if (endM < 10) {
                        strEndM = "0" + endM;
                    }
                    end_time = endY + "-" + strEndM + "-" + strEndD;
                }

                if (startY != 0 && endY != 0)
                    tvTime.setText(start_time + "   " + end_time);
                else if (startY != 0 && endY == 0) {
                    tvTime.setText(start_time);
                    final Calendar c = Calendar.getInstance();

                    int mYear = c.get(Calendar.YEAR); //获取当前年份

                    int mMonth = c.get(Calendar.MONTH);//获取当前月份

                    int mDay = c.get(Calendar.DAY_OF_MONTH);//获取当前月份的日期号码
//                    end_time = mYear + "-" + mMonth + "-" + mDay;
                } else if (startY == 0 && endY != 0)
                    tvTime.setText(end_time);
                break;
            case ADDRESS_ID:
//                province_name = data.getStringExtra("province_name");
//                city_name = data.getStringExtra("city_name");
//                area_id = data.getStringExtra("area_name");
                String ress = data.getStringExtra("ress");
                latitude = data.getStringExtra("latitude");
                longitude = data.getStringExtra("longitude");
                tvAddress.setText(ress);
                break;
        }
    }

    @Override
    public void onItemClick(View view, int i) {
        String hireId = list.get(i).get("hire_id");
        Bundle bundle = new Bundle();
        bundle.putString("hire_id", hireId);
        bundle.putString("hire_noid", list.get(i).get("hire_noid"));
        anInt = Integer.parseInt(list.get(i).get("status"));
        bundle.putInt("state", anInt);
        bundle.putString("coor_to_view", list.get(i).get("coor_to_view"));
        startActivity(JODetailAty.class, bundle);
    }



    @Override
    public void onLoadMore() {
        p++;
        page = p + "";
        initData();
    }


    private class MyJOAdapter extends RecyclerView.Adapter<MyJOAdapter.ViewHolder> {
        private final int NOT_RELEASE = 1;
        private final int NOT_FEEDBACK = 2;
        private final int HAVE_FEEDBACK = 4;
        private final int COMPLETE = 3;

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.listitem_my_job_order, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            final Map<String, String> map = list.get(position);
            ImageLoader imageLoader = new ImageLoader();
//            ImageOptions options = new ImageOptions.Builder()
//                    .setFailureDrawableId(R.drawable.icon_photo_none).build();
//            imageLoader.setImageOptions(options);
            if (!TextUtils.equals(map.get("photos_count"), "0")) {
                imageLoader.disPlay(holder.imgvImg, map.get("cover"));
            } else {
                holder.imgvImg.setImageResource(R.drawable.icon_photo_none);
            }

            holder.tvPage.setText(map.get("photos_count") + "张");
            List<String> skill = JsonArryToList.strList(map.get("skill"));
            holder.tvSkill.setText(ListUtils.join(skill));
            holder.tvStartDate.setText(map.get("contract_starttime"));
            holder.tvEndDate.setText(map.get("contract_endtime"));
            holder.tvTotal.setText("￥" + map.get("amount"));
            if (map.get("subtotal_unit").contains("每")) {
                holder.tvPrice.setText("￥" + map.get("subtotal") + "/" + map.get("subtotal_unit").replace("每", ""));
            } else {
                holder.tvPrice.setText("￥" + map.get("subtotal") + "/" + map.get("subtotal_unit"));
            }

            holder.tvPayment.setText(map.get("settle_type"));
            holder.tvAddress.setText(map.get("address"));
            String status = map.get("status");
            anInt = 1;
            if (TextUtils.equals(status, "1")) {//未发布
                anInt = NOT_RELEASE;
            } else if (TextUtils.equals(status, "2")) {//招工中无反馈
                anInt = NOT_FEEDBACK;
            } else if (TextUtils.equals(status, "3")) {//已签约，完成
                anInt = COMPLETE;
            } else if (TextUtils.equals(status, "4")) {//招工中有反馈
                anInt = HAVE_FEEDBACK;
            }
            switch (anInt) {
                case NOT_RELEASE:
                    holder.imgvSpot.setVisibility(View.GONE);
                    holder.imgvState.setImageResource(R.drawable.icon_not_release);
                    holder.btnFeedback.setText("复制");
                    holder.btnModify.setText("修改");
                    holder.fBtnRelease.setText("发布");
                    holder.btnModify.setVisibility(View.VISIBLE);
                    holder.btnFeedback.setVisibility(View.VISIBLE);
                    holder.btnModify.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Bundle bundle = new Bundle();
                            bundle.putString("hire_id", map.get("hire_id"));
                            bundle.putString("flag", "edit");
                            startActivity(NewJobOrderAty.class, bundle);
                        }
                    });
                    holder.btnFeedback.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Bundle bundle = new Bundle();
                            bundle.putString("hire_id", map.get("hire_id"));
                            bundle.putString("flag", "copy");
                            startActivity(NewJobOrderAty.class, bundle);
                        }
                    });
                    holder.fBtnRelease.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Bundle bundle = new Bundle();
                            bundle.putString("hire_id", map.get("hire_id"));
                            startActivity(ReleaseJOAty.class, bundle);
                        }
                    });
                    break;
                case NOT_FEEDBACK:
                    if (TextUtils.equals(map.get("coor_to_view"), "0")) {
                        holder.imgvSpot.setVisibility(View.GONE);
                    } else {
                        holder.imgvSpot.setVisibility(View.VISIBLE);
                    }
                    holder.btnModify.setVisibility(View.VISIBLE);
                    holder.btnFeedback.setVisibility(View.VISIBLE);
                    holder.imgvState.setImageResource(R.drawable.icon_zhaog);
                    holder.btnFeedback.setText("查看反馈");
                    holder.btnModify.setText("复制");
                    holder.fBtnRelease.setText("修改");
                    holder.btnModify.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Bundle bundle = new Bundle();
                            bundle.putString("hire_id", map.get("hire_id"));
                            bundle.putString("flag", "copy");
                            startActivity(NewJobOrderAty.class, bundle);
                        }
                    });
                    holder.btnFeedback.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Bundle bundle = new Bundle();
                            bundle.putString("hire_noid", map.get("hire_noid"));
                            startActivity(ViewFeedbackAty.class, bundle);
                        }
                    });
                    holder.fBtnRelease.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Bundle bundle = new Bundle();
                            bundle.putString("hire_id", map.get("hire_id"));
                            bundle.putString("flag", "edit");
                            startActivity(NewJobOrderAty.class, bundle);
                        }
                    });

                    break;
                case COMPLETE:
                    holder.imgvSpot.setVisibility(View.GONE);
                    holder.imgvState.setImageResource(R.drawable.icon_qianyue);
                    holder.btnModify.setText("查看反馈");
                    holder.btnFeedback.setVisibility(View.GONE);
                    holder.fBtnRelease.setText("复制");
                    holder.btnModify.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Bundle bundle = new Bundle();
                            bundle.putString("hire_noid", map.get("hire_noid"));
                            bundle.putString("flag", "complete");
                            startActivity(ViewFeedbackAty.class, bundle);
                        }
                    });
                    holder.fBtnRelease.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Bundle bundle = new Bundle();
                            bundle.putString("hire_id", map.get("hire_id"));
                            bundle.putString("flag", "copy");
                            startActivity(NewJobOrderAty.class, bundle);
                        }
                    });
                    break;
                case HAVE_FEEDBACK:
                    holder.imgvState.setImageResource(R.drawable.icon_zhaog);
                    holder.btnFeedback.setText("查看反馈");
                    holder.btnModify.setText("复制");
                    holder.fBtnRelease.setText("修改");
                    if (TextUtils.equals(map.get("coor_to_view"), "0")) {
                        holder.imgvSpot.setVisibility(View.GONE);
                    } else {
                        holder.imgvSpot.setVisibility(View.VISIBLE);
                    }
                    holder.btnModify.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Bundle bundle = new Bundle();
                            bundle.putString("hire_id", map.get("hire_id"));
                            bundle.putString("flag", "copy");
                            startActivity(NewJobOrderAty.class, bundle);
                        }
                    });
                    holder.btnFeedback.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            startActivity(ViewFeedbackAty.class, null);
                        }
                    });
                    holder.fBtnRelease.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Bundle bundle = new Bundle();
                            bundle.putString("hire_id", map.get("hire_id"));
                            bundle.putString("flag", "edit");
                            startActivity(NewJobOrderAty.class, bundle);
                        }
                    });
                    break;
            }

        }

        @Override
        public int getItemCount() {
            return ListUtils.getSize(list);
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            @ViewInject(R.id.list_feedback_btn)
            Button btnFeedback;
            @ViewInject(R.id.list_my_jo_img)
            ImageView imgvImg;
            @ViewInject(R.id.list_my_jo_state)
            ImageView imgvState;
            @ViewInject(R.id.list_my_jo_page)
            TextView tvPage;
            @ViewInject(R.id.list_my_jo_skill)
            TextView tvSkill;
            @ViewInject(R.id.list_my_jo_start_date)
            TextView tvStartDate;
            @ViewInject(R.id.list_my_jo_end_date)
            TextView tvEndDate;
            @ViewInject(R.id.list_my_jo_total)
            TextView tvTotal;
            @ViewInject(R.id.list_my_jo_price)
            TextView tvPrice;
            @ViewInject(R.id.list_my_jo_payment)
            TextView tvPayment;
            @ViewInject(R.id.list_my_jo_address)
            TextView tvAddress;
            @ViewInject(R.id.modify_btn)
            Button btnModify;
            @ViewInject(R.id.release_fbtn)
            FButton fBtnRelease;
            @ViewInject(R.id.list_feedback_spot)
            ImageView imgvSpot;


            public ViewHolder(View itemView) {
                super(itemView);
                x.view().inject(this, itemView);
                AutoUtils.autoSize(itemView);
            }
        }
    }
}
