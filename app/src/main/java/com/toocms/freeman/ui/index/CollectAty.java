package com.toocms.freeman.ui.index;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.toocms.frame.image.ImageLoader;
import com.toocms.frame.tool.AppManager;
import com.toocms.freeman.R;
import com.toocms.freeman.https.Collect;
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

import cn.zero.android.common.recyclerview.RecycleViewDivider;
import cn.zero.android.common.util.JSONUtils;
import cn.zero.android.common.util.ListUtils;
import cn.zero.android.common.view.shapeimageview.CircularImageView;
import cn.zero.android.common.view.swipetoloadlayout.OnLoadMoreListener;
import cn.zero.android.common.view.swipetoloadlayout.OnRefreshListener;
import cn.zero.android.common.view.swipetoloadlayout.view.SwipeToLoadRecyclerView;

public class CollectAty extends BaseAty implements OnRefreshListener, OnLoadMoreListener {

    //初始化swipe
    @ViewInject(R.id.collect_item_swipe)
    SwipeToLoadRecyclerView collectItemSwipe;
    @ViewInject(R.id.collect_bottom_content)
    LinearLayout linlayBottom;
    //初始化匹配器
    CollectItemAdapter mCollectItemAdapter;
    //网络获取的数据源  并初始化
    List<Map<String, String>> collectData = new ArrayList<>();
    //处理钱数显示的格式
    private DecimalFormat mFormat = new DecimalFormat("#0.00");

    //全选状态
    private boolean isAllSelect = false;
    //初始化全选按钮
    @ViewInject(R.id.all_select_tv)
    TextView tvAllSel;
    @ViewInject(R.id.collect_empty)
    TextView tvEmpty;
    //记录全选状态的集合
    List<String> selMumberList = new ArrayList<>();

    //给接口声明的常量
    private final String MEMBER_NUMBER = "noid";
    private final String NICKNAME = "nickname";
    private final String FREEMAN = "role_name";
    private final String SEX = "sex_name";
    private final String CREDIT_RATING = "evaluate_score";
    private final String PRICE = "subtotal";
    private final String SKILL = "skill";
    private Collect collect;
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
    /**
     * 选择收藏劳方发布[publishByCollect]
     *
     * @param noid     用户编号
     * @param hire_id  保存的招工单id
     * @param lab_list 劳方noid列表，必须为数组形式
     */
    private String noid;
    private String page = "1";
    private String hire_id;
    private ArrayList<String> lab_list = new ArrayList<>();
    private Hire hire;
    private int p = 1;


    //    //模拟数据源
//    {
//        for (int i = 0; i < 3; i++) {
//            Map<String, String> map = new HashMap<>();
//            map.put(MEMBER_NUMBER, "1090908689" + i);
//            map.put(NICKNAME, "Jingle");
//            map.put(FREEMAN, "自由人");
//            map.put(SEX, "未填");
//            map.put(PRICE, "1098");
//            map.put(SKILL, "房屋装修");
//            collectData.add(map);
//        }
//    }
    @Override
    protected int getLayoutResId() {
        return R.layout.aty_collect;
    }

    @Override
    protected void initialized() {
        collect = new Collect();
        hire = new Hire();
    }

    @Override
    protected void requestData() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActionBar.setTitle("我的收藏");
        //初始化swipe
        collectItemSwipe.setOnRefreshListener(this);
        collectItemSwipe.setOnLoadMoreListener(this);
        collectItemSwipe.getRecyclerView().setLayoutManager(new LinearLayoutManager(this));
        collectItemSwipe.getRecyclerView().addItemDecoration(new RecycleViewDivider(
                this, LinearLayoutManager.HORIZONTAL, AutoUtils.getPercentHeightSize(5), getResources().getColor(R.color.clr_bg)));

    }


    @Override
    public void onComplete(RequestParams params, String result) {
        if (params.getUri().contains("Collect/labList")) {
            if (p == 1) {
                collectData = JSONUtils.parseDataToMapList(result);
            } else {
                ArrayList<Map<String, String>> arrayList = JSONUtils.parseDataToMapList(result);
                if (ListUtils.isEmpty(arrayList)) {
                    p--;
                    page = String.valueOf(p);
                }
                collectData.addAll(arrayList);
            }

            updateUI();
        } else if (params.getUri().contains("Hire/publishByCollect")) {
            showToast(JSONUtils.parseKeyAndValueToMap(result).get("message"));
            JODetailAty.isRelease = true;
            JODetailAty.hire_noid = JSONUtils.parseKeyAndValueToMap(result).get("data");
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
//                    startActivity(MyJobOrderAty.class, null);
                    AppManager.getInstance().killActivity(ReleaseJOAty.class);
                    finish();
                }
            }, 1500);
        }
        super.onComplete(params, result);
        collectItemSwipe.stopRefreshing();
        collectItemSwipe.stopLoadMore();
    }

    //更新UI
    private void updateUI() {

        if (mCollectItemAdapter == null) {
            mCollectItemAdapter = new CollectItemAdapter();
            collectItemSwipe.setAdapter(mCollectItemAdapter);
        } else {
            mCollectItemAdapter.notifyDataSetChanged();
        }

        if (ListUtils.isEmpty(collectData)) {
            tvEmpty.setVisibility(View.VISIBLE);
            tvAllSel.setEnabled(false);
        } else {
            tvEmpty.setVisibility(View.GONE);
            tvAllSel.setEnabled(true);
        }

    }

    @Event({R.id.all_select_tv, R.id.confirmFBtn})
    private void onClick(View vIew) {
        hire_id = getIntent().getStringExtra("hire_id");
        noid = application.getUserInfo().get("noid");
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
                for (int i = 0; i < ListUtils.getSize(collectData); i++) {
                    for (int j = 0; j < ListUtils.getSize(selMumberList); j++) {
                        if (TextUtils.equals(collectData.get(i).get(MEMBER_NUMBER), selMumberList.get(j))) {
                            lab_list.add(collectData.get(i).get("noid"));
                        }

                    }
                }
                if (ListUtils.isEmpty(selMumberList)) {
                    lab_list = new ArrayList<>();
                }
                if (ListUtils.isEmpty(lab_list)) {
                    showToast("请选择收藏劳方");
                    return;
                }
                hire.publishByCollect(noid, hire_id, lab_list, this);
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        noid = application.getUserInfo().get("noid");
        collect.labList(noid, page, null, null, null, null, this);
    }


    //swipe下拉刷新监听
    @Override
    public void onRefresh() {
        collect.labList(noid, page, null, null, null, null, this);
//        collectItemSwipe.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                updateUI();
//                //每次刷新后,回到全不选状态
//                selMumberList.clear();
//                isAllSelect = false;
//                tvAllSel.setCompoundDrawablesWithIntrinsicBounds(R.drawable.btn_unselect, 0, 0, 0);
//            }
//        }, 2000);


    }

    @Override
    public void onLoadMore() {
        p++;
        page = String.valueOf(p);
        collect.labList(noid, page, null, null, null, null, this);
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
            View view = LayoutInflater.from(CollectAty.this).inflate(R.layout.listitem_collect, parent, false);
            ViewHolder holder = new ViewHolder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(final CollectItemAdapter.ViewHolder holder, int position) {
            final Map<String, String> map = collectData.get(position);
            holder.linlayDistancel.setVisibility(View.GONE);
            holder.linlayNum.setVisibility(View.GONE);
            holder.tvMember.setText(map.get(MEMBER_NUMBER));
            holder.tvNickname.setText(map.get(NICKNAME));
            holder.tvFreeman.setText(map.get(FREEMAN));
            holder.tvSex.setText(map.get(SEX));
            holder.tvPrice.setText("￥" + map.get(PRICE) + "/天");
            ArrayList<Map<String, String>> skillList = JSONUtils.parseKeyAndValueToMapList(map.get(SKILL));
            ArrayList<String> skillName = new ArrayList<>();
            for (int i = 0; i < ListUtils.getSize(skillList); i++) {
                skillName.add(skillList.get(i).get("name"));
            }
            holder.tvSkill.setText(ListUtils.join(skillName));
            mImageLoader.disPlay(holder.headerCir, map.get("head"));
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
            if (TextUtils.equals(map.get("attestation"), "1")) {
                holder.tvAuthentication.setText("已认证");
            } else {
                holder.tvAuthentication.setText("未认证");
            }
            //根据选择选中状态
            if (selMumberList.contains(map.get(MEMBER_NUMBER))) {    //选中集合里包含会员编号
                holder.imgeSel.setImageResource(R.drawable.btn_select);
            } else {     //选中集合里 不 包含会员编号
                holder.imgeSel.setImageResource(R.drawable.btn_unselect);
            }

            //选中状态处理
            holder.linlayCollect.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (selMumberList.contains(map.get(MEMBER_NUMBER))) {     //选中集合里包含会员编号
                        holder.imgeSel.setImageResource(R.drawable.btn_unselect);
                        //遍历集合 移除带有此会员编号的内容
                        for (int i = 0; i < ListUtils.getSize(selMumberList); i++) {
                            if (selMumberList.get(i).equals(map.get(MEMBER_NUMBER))) {
                                selMumberList.remove(i);
                            }
                            allSelState();
                        }
                    } else {     //选中集合里 不 包含会员编号
                        holder.imgeSel.setImageResource(R.drawable.btn_select);
                        selMumberList.add(map.get(MEMBER_NUMBER));
                        allSelState();
                    }
                }
            });
            holder.headerCir.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    bundle.putString("code", map.get(MEMBER_NUMBER));
                    bundle.putString("flag", "collect");
                    startActivity(MemberDetailAty.class, bundle);
                }
            });
        }

        //判断点击后的状态  -->  处理全选按钮的状态
        private void allSelState() {
            if (ListUtils.getSize(selMumberList) == ListUtils.getSize(collectData)) {
                Log.e("***", "ListUtils.getSize(selMumberList)：" + ListUtils.getSize(selMumberList));
                Log.e("***", "ListUtils.getSize(collectData)：" + ListUtils.getSize(collectData));

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
            ImageView imgeSel;
            @ViewInject(R.id.header_cir)
            CircularImageView headerCir;
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
            ImageView imgvCredit;
            @ViewInject(R.id.price_tv)
            TextView tvPrice;
            @ViewInject(R.id.skill_tv)
            TextView tvSkill;
            @ViewInject(R.id.list_collect_click)
            LinearLayout linlayCollect;
            @ViewInject(R.id.list_collect_distance_content)
            LinearLayout linlayDistancel;
            @ViewInject(R.id.list_collect_num_content)
            LinearLayout linlayNum;


            public ViewHolder(View itemView) {
                super(itemView);
                x.view().inject(this, itemView);
                AutoUtils.auto(itemView);
            }
        }
    }
}
