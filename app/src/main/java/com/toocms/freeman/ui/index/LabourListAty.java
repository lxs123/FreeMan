package com.toocms.freeman.ui.index;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.toocms.frame.image.ImageLoader;
import com.toocms.frame.tool.AppManager;
import com.toocms.freeman.R;
import com.toocms.freeman.config.JsonArryToList;
import com.toocms.freeman.https.Hire;
import com.toocms.freeman.ui.BaseAty;
import com.toocms.freeman.ui.mine.MemberDetailAty;
import com.toocms.freeman.ui.recruitment.joborder.ReleaseJOAty;
import com.toocms.freeman.ui.recruitment.myjoborder.JODetailAty;
import com.zero.autolayout.utils.AutoUtils;

import org.xutils.http.RequestParams;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cn.zero.android.common.util.JSONUtils;
import cn.zero.android.common.util.ListUtils;
import cn.zero.android.common.view.shapeimageview.CircularImageView;
import cn.zero.android.common.view.swipetoloadlayout.OnRefreshListener;
import cn.zero.android.common.view.swipetoloadlayout.view.SwipeToLoadRecyclerView;

public class LabourListAty extends BaseAty implements OnRefreshListener {


    //初始化上面四个按钮
    @ViewInject(R.id.tv1)
    TextView tv1;
    @ViewInject(R.id.tv2)
    TextView tv2;
    @ViewInject(R.id.tv3)
    TextView tv3;
    @ViewInject(R.id.tv4)
    TextView tv4;
    @ViewInject(R.id.labour_empty)
    private TextView tvEmpty;

    //初始化swipe
    @ViewInject(R.id.collect_item_swipe)
    SwipeToLoadRecyclerView collectItemSwipe;
    //初始化匹配器
    CollectItemAdapter mCollectItemAdapter;
    //网络获取的数据源
    List<Map<String, String>> collectData = new ArrayList<>();
    //处理钱数显示的格式
    private DecimalFormat mFormat = new DecimalFormat("#0.00");

    //全选状态
    private boolean isAllSelect = false;
    //初始化全选按钮
    @ViewInject(R.id.all_select_tv)
    TextView tvAllSel;
    //记录全选状态的集合
    ArrayList<String> selMumberList = new ArrayList<>();

    //给接口声明的常量
    private final String MEMBER_NUMBER = "noid";
    private final String NICKNAME = "nickname";
    private final String FREEMAN = "name";
    private final String SEX = "sex_name";
    private final String CREDIT_RATING = "evaluate_score";
    private final String PRICE = "subtotal";
    private final String SKILL = "skill";
    /**
     * 搜索劳方列表[seekLabs]
     *
     * @param noid             用户编号
     * @param hire_id          招工单id
     * @param page             分页，默认为1
     * @param keywords         关键字
     * @param min_price        最小价格
     * @param max_price        最大价格
     * @param code             搜索劳方编号
     * @param distance         距离
     * @param contract_start   合同开始日期
     * @param contract_endtime 合同结束日期
     * @param work_starttime   每天工作开始时间
     * @param work_endtime     每天工作结束时间
     * @param sort             排序：
     * - cost: 成单量最多
     * - level: 信用最高
     * - distance: 距离最近
     * - maxprice: 单价高到底
     * - minprice: 单价低到高
     * @param province_id      省id
     * @param city_id          市id
     * @param area_id          区id
     */
    private String province_id;
    private String city_id;
    //    市id
    private String area_id;
    //区id
    private String noid;
    private String hire_id;
    private String page;
    private String keywords;
    private String min_price;
    private String max_price;
    private String code;
    private String distance;
    private String contract_start;
    private String contract_endtime;
    private String work_starttime;
    private String work_endtime;
    private String sort;
    private Hire hire;
    private int anInt = 0;
    private String work_week;
    private String skill_id;
    /**
     * 手动选择劳方发布[publishByCustom]
     *
     * @param noid     用户编号
     * @param hire_id  招工单id
     * @param lab_list 劳方noid列表，必须为数组
     */
    private ArrayList<String> lab_list = new ArrayList<>();

    @Override
    protected int getLayoutResId() {
        return R.layout.aty_labour_list;
    }

    @Override
    protected void initialized() {
        hire = new Hire();
    }

    @Override
    protected void requestData() {
        showProgressContent();
        noid = application.getUserInfo().get("noid");
        Intent intent = getIntent();
        hire_id = intent.getStringExtra("hire_id");
        min_price = intent.getStringExtra("min_price");
        max_price = intent.getStringExtra("max_price");
        code = intent.getStringExtra("code");
        page = "1";
        contract_start = intent.getStringExtra("contract_start");
        contract_endtime = intent.getStringExtra("contract_endtime");
        work_starttime = intent.getStringExtra("work_starttime");
        work_endtime = intent.getStringExtra("work_endtime");
        province_id = intent.getStringExtra("province_id");
        city_id = intent.getStringExtra("city_id");
        area_id = intent.getStringExtra("city_id");
        work_week = intent.getStringExtra("work_week");
        distance = intent.getStringExtra("distance");
        sort = "cost";
        skill_id = intent.getStringExtra("skill_id");
        hire.seekLabs(noid, hire_id, page, keywords, min_price, max_price, code, distance, contract_start, contract_endtime,
                work_starttime, work_endtime, sort, province_id, city_id, area_id, work_week, skill_id, this);


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActionBar.setTitle("劳方列表");
        //初始化swipe
        collectItemSwipe.setOnRefreshListener(this);
        collectItemSwipe.getRecyclerView().setLayoutManager(new LinearLayoutManager(this));
//        collectItemSwipe.getRecyclerView().addItemDecoration(new RecycleViewDivider(
//                this, LinearLayoutManager.HORIZONTAL, AutoUtils.getPercentHeightSize(18), getResources().getColor(R.color.clr_bg)));

        tv1.setTextColor(getResources().getColor(R.color.clr_main));

//        //更新UI
//        updateUI();
    }


    @Override
    public void onComplete(RequestParams params, String result) {
        if (params.getUri().contains("Hire/seekLabs")) {
            collectData = JSONUtils.parseDataToMapList(result);
        } else if (params.getUri().contains("Hire/publishByCustom")) {
            showToast(JSONUtils.parseKeyAndValueToMap(result).get("message"));
            JODetailAty.isRelease = true;
            JODetailAty.hire_noid = JSONUtils.parseKeyAndValueToMap(result).get("data");
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
//                    startActivity(MyJobOrderAty.class, null);
                    AppManager.getInstance().killActivity(ManulSelectLaborAty.class);
                    AppManager.getInstance().killActivity(ReleaseJOAty.class);
                    finish();
                }
            }, 1500);
        }
        updateUI();
        selMumberList.clear();
        isAllSelect = false;
        tvAllSel.setCompoundDrawablesWithIntrinsicBounds(R.drawable.btn_unselect, 0, 0, 0);
        super.onComplete(params, result);
        collectItemSwipe.stopRefreshing();
    }

    //swipe下拉刷新监听
    @Override
    public void onRefresh() {
        initDate();
    }

    private void initDate() {
        hire.seekLabs(noid, hire_id, page, keywords, min_price, max_price, code, distance, contract_start, contract_endtime,
                work_starttime, work_endtime, sort, province_id, city_id, area_id, work_week, skill_id, this);
    }

    //更新UI
    private void updateUI() {

        if (mCollectItemAdapter == null) {
            mCollectItemAdapter = new CollectItemAdapter();
            collectItemSwipe.setAdapter(mCollectItemAdapter);
        } else mCollectItemAdapter.notifyDataSetChanged();
        if (ListUtils.isEmpty(collectData)) {
            tvEmpty.setVisibility(View.VISIBLE);
            tvAllSel.setEnabled(false);
        } else {
            tvAllSel.setEnabled(true);
            tvEmpty.setVisibility(View.GONE);
        }

    }

    @Event({R.id.all_select_tv, R.id.confirmFBtn, R.id.tv1, R.id.tv2, R.id.tv3, R.id.labour_price_click})
    private void onClick(View vIew) {
        switch (vIew.getId()) {
            //全选按钮点击事件
            case R.id.all_select_tv:
                if (isAllSelect) {   //当前是全选状态
                    isAllSelect = false;
                    tvAllSel.setCompoundDrawablesWithIntrinsicBounds(R.drawable.btn_unselect, 0, 0, 0);
                    selMumberList.clear();
                    updateUI();
                } else {     //当前不是全选状态
                    isAllSelect = true;
                    tvAllSel.setCompoundDrawablesWithIntrinsicBounds(R.drawable.btn_select, 0, 0, 0);
                    selMumberList.clear();
                    //遍历集合 把数据源的数据交给选中列表
                    for (int i = 0; i < ListUtils.getSize(collectData); i++) {
                        selMumberList.add(collectData.get(i).get(MEMBER_NUMBER));
                    }
                    updateUI();
                }
                break;
            //确认发布按钮
            case R.id.confirmFBtn:
//                for (int i = 0; i < ListUtils.getSize(collectData); i++) {
//                    for (int j = 0; j < ListUtils.getSize(selMumberList); j++) {
//                        if (TextUtils.equals(collectData.get(i).get(MEMBER_NUMBER), selMumberList.get(j))) {
//                            lab_list.add(collectData.get(i).get(MEMBER_NUMBER));
//                        }
//
//                    }
//                }
//                if (ListUtils.isEmpty(selMumberList)) {
//                    lab_list = new ArrayList<>();
//                }
                if (ListUtils.isEmpty(selMumberList)) {
                    showToast("请选择收藏劳方");
                    return;
                }
                hire.publishByCustom(noid, hire_id, selMumberList, this);
                break;

            //上面的按钮
            case R.id.tv1:
                initTvColor();
                tv1.setTextColor(getResources().getColor(R.color.clr_main));
                sort = "cost";
                collectItemSwipe.startRefreshing();
                initDate();
                anInt = 0;
                tv4.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.btn_level, 0);
                break;
            case R.id.tv2:
                initTvColor();
                tv2.setTextColor(getResources().getColor(R.color.clr_main));
                sort = "level";
                collectItemSwipe.startRefreshing();
                initDate();
                anInt = 0;
                tv4.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.btn_level, 0);
                break;
            case R.id.tv3:
                initTvColor();
                tv3.setTextColor(getResources().getColor(R.color.clr_main));
                sort = "distance";
                collectItemSwipe.startRefreshing();
                initDate();
                anInt = 0;
                tv4.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.btn_level, 0);
                break;
            case R.id.labour_price_click:
                initTvColor();
                tv4.setTextColor(getResources().getColor(R.color.clr_main));
                if (anInt == 0) {
                    tv4.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.btn_level_up, 0);
                    sort = "minprice";
                    collectItemSwipe.startRefreshing();
                    initDate();
                    anInt = 1;
                } else if (anInt == 1) {
                    tv4.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.btn_level_down, 0);
                    sort = "maxprice";
                    collectItemSwipe.startRefreshing();
                    initDate();
                    anInt = 0;
                }

                break;
        }
    }

    private void initTvColor() {
        tv1.setTextColor(Color.parseColor("#656565"));
        tv2.setTextColor(Color.parseColor("#656565"));
        tv3.setTextColor(Color.parseColor("#656565"));
        tv4.setTextColor(Color.parseColor("#656565"));
    }


    //=========================自定义Adapter=====================
    public class CollectItemAdapter extends RecyclerView.Adapter<CollectItemAdapter.ViewHolder> {

        //用来加载图片
        ImageLoader mImageLoader;

        public CollectItemAdapter() {
            mImageLoader = new ImageLoader();
        }

        @Override
        public CollectItemAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(LabourListAty.this).inflate(R.layout.listitem_collect, parent, false);
            ViewHolder holder = new ViewHolder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(final CollectItemAdapter.ViewHolder holder, int position) {
            final Map<String, String> map = collectData.get(position);
            holder.tvMember.setText(map.get(MEMBER_NUMBER));
            holder.tvNickname.setText(map.get(NICKNAME));
            holder.tvFree.setText(map.get("role_name"));
            holder.tvSex.setText(map.get(SEX));
            holder.tvPrice.setText("￥" + map.get(PRICE) + "/天");
            holder.tvSkill.setText(ListUtils.join(JsonArryToList.strList(map.get(SKILL))));
            holder.tvDistance.setText(map.get("distance"));
            holder.tvNum.setText(map.get("cost_count"));
            if (TextUtils.equals(map.get("attestation"), "1")) {
                holder.tvAuthentication.setText("已认证");
            } else {
                holder.tvAuthentication.setText("未认证");
            }
            ImageLoader imageLoader = new ImageLoader();
            imageLoader.disPlay(holder.imgvHead, map.get("head"));
            String evaluate_score = map.get(CREDIT_RATING);
            if (TextUtils.equals(evaluate_score, "0")) {
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

            holder.imgvHead.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    bundle.putString("code", map.get("noid"));
                    bundle.putString("flag", "collect");
                    startActivity(MemberDetailAty.class, bundle);
                }
            });
            //根据选择选中状态
            if (selMumberList.contains(map.get(MEMBER_NUMBER))) {    //选中集合里包含会员编号
                holder.imgvSel.setImageResource(R.drawable.btn_select);
            } else {     //选中集合里 不 包含会员编号
                holder.imgvSel.setImageResource(R.drawable.btn_unselect);
            }

            //选中状态处理
            holder.linlayLabour.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (selMumberList.contains(map.get(MEMBER_NUMBER))) {     //选中集合里包含会员编号
                        holder.imgvSel.setImageResource(R.drawable.btn_unselect);
                        //遍历集合 移除带有此会员编号的内容
                        for (int i = 0; i < ListUtils.getSize(selMumberList); i++) {
                            if (selMumberList.get(i).equals(map.get(MEMBER_NUMBER))) {
                                selMumberList.remove(i);
                            }
                            allSelState();
                        }
                    } else {     //选中集合里 不 包含会员编号
                        holder.imgvSel.setImageResource(R.drawable.btn_select);
                        selMumberList.add(map.get(MEMBER_NUMBER));
                        allSelState();
                    }
                }
            });

        }

        //判断点击后的状态  -->  处理全选按钮的状态
        private void allSelState() {
            if (ListUtils.getSize(selMumberList) == ListUtils.getSize(collectData)) {
                isAllSelect = true;
                tvAllSel.setCompoundDrawablesWithIntrinsicBounds(R.drawable.btn_select, 0, 0, 0);
            } else {
                isAllSelect = false;
                tvAllSel.setCompoundDrawablesWithIntrinsicBounds(R.drawable.btn_unselect, 0, 0, 0);
            }
        }

        @Override
        public int getItemCount() {
            return ListUtils.getSize(collectData);
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
            @ViewInject(R.id.list_collect_click)
            LinearLayout linlayLabour;

            public ViewHolder(View itemView) {
                super(itemView);
                x.view().inject(this, itemView);
                AutoUtils.auto(itemView);
            }
        }
    }
}
