package com.toocms.freeman.ui.index;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
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
import com.toocms.freeman.https.Contract;
import com.toocms.freeman.https.Hire;
import com.toocms.freeman.https.Sys;
import com.toocms.freeman.ui.BaseAty;
import com.toocms.freeman.ui.mine.MemberDetailAty;
import com.toocms.freeman.ui.recruitment.AreaListAty;
import com.toocms.freeman.ui.util.WorkOrder;
import com.zero.autolayout.utils.AutoUtils;

import org.xutils.http.RequestParams;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.text.DecimalFormat;
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
import cn.zero.android.common.view.swipetoloadlayout.OnRefreshListener;
import cn.zero.android.common.view.swipetoloadlayout.view.SwipeToLoadRecyclerView;

public class ViewFeedbackAty extends BaseAty implements OnRefreshListener {

    @ViewInject(R.id.tv1)
    TextView tv1;
    @ViewInject(R.id.tv2)
    TextView tv2;
    @ViewInject(R.id.tv3)
    TextView tv3;
    @ViewInject(R.id.tv4)
    TextView tv4;
    @ViewInject(R.id.v_feedback_min)
    EditText editMin;
    @ViewInject(R.id.v_feedback_max)
    EditText editMax;
    @ViewInject(R.id.view_modify_details_btn)
    Button btnModify;
    @ViewInject(R.id.feedback_qianyue)
    FButton fBtnSign;
    @ViewInject(R.id.feedback_screen)
    private TextView tvScreen;
    @ViewInject(R.id.v_feedback_btnlay)
    LinearLayout linlayBtn;

    @ViewInject(R.id.feedback_drawer)
    private DrawerLayout drawerLayout;
    @ViewInject(R.id.feedback_lay)
    private ScrollView linlayScreen;
    //    可提供人数符合
    @ViewInject(R.id.feedback_num)
    TextView tvNum;
    //    同意招工条件
    @ViewInject(R.id.feedback_agree)
    TextView tvAgree;
    boolean isNum = false;
    boolean isAgree = false;
    //初始化swipe
    @ViewInject(R.id.collect_item_swipe)
    SwipeToLoadRecyclerView collectItemSwipe;
    @ViewInject(R.id.feedback_keywords)
    private EditText editKeywords;
    @ViewInject(R.id.feedback_area)
    private TextView tvArea;
    @ViewInject(R.id.feedback_skill)
    private TextView tvWork;
    @ViewInject(R.id.v_feedback_screen)//筛选栏 ScrollView下的布局
    private LinearLayout linlayScreenLay;
    //初始化匹配器
    CollectItemAdapter mCollectItemAdapter;
    //网络获取的数据源  并初始化
    List<Map<String, String>> collectData = new ArrayList<>();
    //处理钱数显示的格式
    private DecimalFormat mFormat = new DecimalFormat("#0.00");

    //全选状态
    private boolean isAllSelect = false;
    //    //初始化全选按钮
//    @ViewInject(R.id.all_select_tv)
//    TextView allSelTv;
    //记录全选状态的集合
    List<String> selMumberList = new ArrayList<>();
    /**
     * 我的反馈列表[reply]
     *
     * @param noid        用户编号
     * @param hire_noid   招工单编号，不是id
     * @param keywords    关键字
     * @param maxprice    最高价格
     * @param minprice    最低价格
     * @param province_id 省id
     * @param city_id     市id
     * @param area_id     区id
     * @param skill_id    技能id，可以为数组或字符串链接
     * @param mate_person 可提供人数符合，1为符合 默认为0(不设置)
     * @param mate_agree  同意招工条件，1为同意 默认为0(不设置)
     * @param sort        排序：
     * - cost: 成单量最多
     * - level: 信用最高
     * - distance: 距离最近
     * - maxprice: 单价高到底
     * - minprice: 单价低到高
     */
    private String noid;
    private String hire_noid;
    private String keywords;
    private String maxprice;
    private String minprice;
    private String province_id;
    private String city_id;
    private String area_id;
    private String skill_id;
    private String mate_person;
    private String mate_agree;
    private String sort;
    //给接口声明的常量
    private final String MEMBER_NUMBER = "lab_noid";
    private final String NICKNAME = "nickname";
    private final String FREEMAN = "role_name";
    private final String SEX = "sex_name";
    private final String PRICE = "subtotal";
    private final String SKILL = "skill_name";
    private final int SEL_AERA = 0x0001;
    private Hire hire;
    private ArrayList<String> skillList;
    private List<Map<String, String>> skillItemData;
    private Sys sys;
    /**
     * 取消收藏用户[cancelLab]
     *
     * @param noid     用户编号
     * @param lab_noid 被收藏的用户noid
     */

    private String lab_noid;
    private Contract contract;
    private String selStatus;

    @Override
    protected int getLayoutResId() {
        return R.layout.aty_view_feedback;
    }

    @Override
    protected void initialized() {
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
        WindowManager wm = this.getWindowManager();//获取屏幕宽高
        ViewGroup.LayoutParams layoutParams = linlayScreenLay.getLayoutParams();
        layoutParams.height = wm.getDefaultDisplay().getHeight();
        layoutParams.width = wm.getDefaultDisplay().getWidth() / 4 * 3;
        linlayScreenLay.setLayoutParams(layoutParams);
        mActionBar.hide();
        WorkOrder.getInstance().clear();
        //初始化swipe
        collectItemSwipe.setOnRefreshListener(this);
        collectItemSwipe.getRecyclerView().setLayoutManager(new LinearLayoutManager(this));
        collectItemSwipe.getRecyclerView().addItemDecoration(new RecycleViewDivider(
                this, LinearLayoutManager.HORIZONTAL, AutoUtils.getPercentHeightSize(18), getResources().getColor(R.color.clr_bg)));

        //更新UI
        updateUI();
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
        noid = application.getUserInfo().get("noid");
        hire_noid = getIntent().getStringExtra("hire_noid");
        sort = "cost";
        hire.reply(noid, hire_noid, keywords, maxprice, minprice, province_id, city_id, area_id, skill_id,
                mate_person, mate_agree, sort, this);
        initSkill();
    }


    @Override
    public void onComplete(RequestParams params, String result) {
        if (params.getUri().contains("Hire/reply")) {
            collectData = JSONUtils.parseDataToMapList(result);
            updateUI();
        } else if (params.getUri().contains("Sys/getSkillList")) {
            skillItemData = JSONUtils.parseDataToMapList(result);
        } else if (params.getUri().contains("Contract/sign")) {
            showToast(JSONUtils.parseKeyAndValueToMap(result).get("message"));
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    finish();
                }
            }, 1500);
        }

        super.onComplete(params, result);
        collectItemSwipe.stopRefreshing();
    }

    //swipe下拉刷新监听
    @Override
    public void onRefresh() {
        collectItemSwipe.postDelayed(new Runnable() {
            @Override
            public void run() {
                initInternet();
                //每次刷新后,回到全不选状态
                selMumberList.clear();
                isAllSelect = false;
//                allSelTv.setCompoundDrawablesWithIntrinsicBounds(R.drawable.btn_unselect, 0, 0, 0);
            }
        }, 2000);


    }

    private void initInternet() {
        hire.reply(noid, hire_noid, keywords, maxprice, minprice, province_id, city_id, area_id, skill_id,
                mate_person, mate_agree, sort, this);
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

    //更新UI
    private void updateUI() {

        if (mCollectItemAdapter == null) {
            mCollectItemAdapter = new CollectItemAdapter();
            collectItemSwipe.setAdapter(mCollectItemAdapter);
        } else
            mCollectItemAdapter.notifyDataSetChanged();

    }

    int anInt = 0;

    @Event({R.id.v_feedback_sure, R.id.feedback_screen, R.id.feedback_num, R.id.feedback_agree,
            R.id.my_jo_work, R.id.view_modify_details_btn, R.id.back, R.id.tv1, R.id.tv2, R.id.tv3,
            R.id.feedback_price_click, R.id.v_feedback_clear, R.id.feedback_area_click, R.id.feedback_qianyue})
    private void onClick(View vIew) {
        if (!ListUtils.isEmpty(selMumberList))
            for (int i = 0; i < ListUtils.getSize(collectData); i++) {
                if (TextUtils.equals(collectData.get(i).get(MEMBER_NUMBER), selMumberList.get(0))) {
                    lab_noid = collectData.get(i).get("lab_noid");
                    selStatus = collectData.get(i).get("status");
                }
            }
        switch (vIew.getId()) {
//            //全选按钮点击事件
//            case R.id.all_select_tv:
//                if (isAllSelect) {   //当前是全选状态
//                    isAllSelect = false;
//                    allSelTv.setCompoundDrawablesWithIntrinsicBounds(R.drawable.btn_unselect, 0, 0, 0);
//                    selMumberList.clear();
//                    updateUI();
//                } else {     //当前不是全选状态
//                    isAllSelect = true;
//                    allSelTv.setCompoundDrawablesWithIntrinsicBounds(R.drawable.btn_select, 0, 0, 0);
//                    selMumberList.clear();
//                    //遍历集合 把数据源的数据交给选中列表
//                    for (int i = 0; i < ListUtils.getSize(collectData); i++) {
//                        selMumberList.add(collectData.get(i).get(MEMBER_NUMBER));
//                    }
//                    updateUI();
//                }
//                break;
//
            case R.id.feedback_qianyue:
                if (TextUtils.isEmpty(lab_noid)) {
                    showToast("请选择要签约的劳方");
                    return;
                }
                showDialog("", "是否确定要与该劳方签约？", "确定签约", "再看看", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        showProgressDialog();
                        contract.sign(hire_noid, noid, lab_noid, ViewFeedbackAty.this);
                    }
                }, null);
                break;
            case R.id.feedback_area_click:
                startActivityForResult(AreaListAty.class, null, SEL_AERA);
                break;
            case R.id.back:
                finish();
                break;
            //确认
            case R.id.v_feedback_sure:
                String min = editMin.getText().toString().trim();
                minprice = min;
                String max = editMax.getText().toString().trim();
                maxprice = max;
                if (!TextUtils.isEmpty(min) && !TextUtils.isEmpty(max)) {
                    if (Integer.parseInt(min) > Integer.parseInt(max)) {
                        editMin.setText(max);
                        editMax.setText(min);
                        minprice = max;
                        maxprice = min;
                    }
                }
                skill_id = ListUtils.join(skillList);
                if (!TextUtils.isEmpty(min) || !TextUtils.isEmpty(max) || isNum || isAgree || !TextUtils.isEmpty(skill_id) ||
                        !TextUtils.isEmpty(tvArea.getText().toString())) {
                    tvScreen.setText("已筛选");
                } else {
                    tvScreen.setText("筛选");
                }
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
                keywords = editKeywords.getText().toString();
                showProgressDialog();
                initInternet();
                break;
            case R.id.v_feedback_clear:
                tvAgree.setSelected(false);
                isAgree = false;
                mate_person = "";
                isNum = false;
                mate_agree = "";
                tvNum.setSelected(false);
                editMax.setText("");
                editMin.setText("");
                tvArea.setText("");
                tvWork.setText("");
                WorkOrder.getInstance().clear();
                skillList.clear();
                province_id = "";
                city_id = "";
                area_id = "";
                editKeywords.setText("");
                initInternet();
                break;
            case R.id.feedback_screen:
                if (!drawerLayout.isDrawerOpen(linlayScreen)) {
                    drawerLayout.openDrawer(linlayScreen);
                }
                break;
            case R.id.feedback_num:
                isNum = !isNum;
                tvNum.setSelected(isNum);
                if (isNum) mate_person = "1";
                else mate_person = "0";
                break;
            case R.id.feedback_agree:
                isAgree = !isAgree;
                tvAgree.setSelected(isAgree);
                if (isAgree) {
                    mate_agree = "1";
                } else {
                    mate_agree = "0";
                }
                break;
            case R.id.my_jo_work:
                //侧滑选择技能
                startActivity(SkillAty.class, null);
                break;
            case R.id.view_modify_details_btn:
                Bundle bundle = new Bundle();
                bundle.putString("hire_noid", hire_noid);
//                if (!ListUtils.isEmpty(selMumberList)) {
//                    if (TextUtils.equals(selStatus, "1")) {
//                        showToast("该招工单暂无修改明细");
//                        return;
//                    }
//                }

                if (ListUtils.isEmpty(selMumberList)) {
                    showToast("请选择要查看修改明细的反馈");
                    return;
                }

                bundle.putString("lab_noid", lab_noid);
                startActivity(ModifyDetailsAty.class, bundle);
                break;

            case R.id.tv1:
                initTvColor();
                tv1.setTextColor(getResources().getColor(R.color.clr_main));
                sort = "cost";
                collectItemSwipe.startRefreshing();
                anInt = 0;
                tv4.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.btn_level, 0);
                break;
            case R.id.tv2:
                initTvColor();
                tv2.setTextColor(getResources().getColor(R.color.clr_main));
                sort = "level";
                collectItemSwipe.startRefreshing();
                anInt = 0;
                tv4.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.btn_level, 0);
                break;
            case R.id.tv3:
                initTvColor();
                tv3.setTextColor(getResources().getColor(R.color.clr_main));
                sort = "distance";
                collectItemSwipe.startRefreshing();
                anInt = 0;
                tv4.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.btn_level, 0);
                break;
            case R.id.feedback_price_click:
                initTvColor();
                tv4.setTextColor(getResources().getColor(R.color.clr_main));

                if (anInt == 0) {
                    tv4.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.btn_level_up, 0);
                    sort = "minprice";
                    collectItemSwipe.startRefreshing();
                    anInt = 1;
                } else if (anInt == 1) {
                    tv4.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.btn_level_down, 0);
                    sort = "maxprice";
                    collectItemSwipe.startRefreshing();
                    anInt = 0;
                }
                collectItemSwipe.startRefreshing();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            return;
        }
        switch (requestCode) {
            case SEL_AERA:
                province_id = data.getStringExtra("province_id");
                city_id = data.getStringExtra("city_id");
                area_id = data.getStringExtra("area_id");
                tvArea.setText(data.getStringExtra("str"));
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
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(ViewFeedbackAty.this).inflate(R.layout.listitem_view_feedback, parent, false);
            ViewHolder holder = new ViewHolder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {
            final Map<String, String> map = collectData.get(position);
            holder.tvMember.setText(map.get(MEMBER_NUMBER));
            holder.tvNickname.setText(map.get(NICKNAME));
            holder.tvFreeman.setText(map.get(FREEMAN));
            holder.tvSex.setText(map.get(SEX));
            holder.tvPrice.setText("￥" + map.get(PRICE) + "/天");
//            ArrayList<Map<String, String>> skillList = JSONUtils.parseKeyAndValueToMapList(map.get(SKILL));
            List<String> skillName = JsonArryToList.strList(map.get(SKILL));
            holder.tvSkill.setText(ListUtils.join(skillName));
//            holder.tvSkill.setText(map.get(SKILL));
            mImageLoader.disPlay(holder.headerCir, map.get("head"));
            String evaluate_score = map.get("evaluate_score");
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
            holder.selectImg.setVisibility(View.VISIBLE);
            holder.imgvState.setVisibility(View.VISIBLE);
            holder.imgvSign.setVisibility(View.GONE);
            linlayBtn.setVisibility(View.VISIBLE);
            if (TextUtils.equals(getIntent().getStringExtra("flag"), "complete")) {
                linlayBtn.setVisibility(View.GONE);
                holder.selectImg.setVisibility(View.GONE);
            }
            if (TextUtils.equals(map.get("status"), "2")) {
                holder.imgvState.setImageResource(R.drawable.icon_unagree);
            } else if (TextUtils.equals(map.get("status"), "1")) {
                holder.imgvState.setImageResource(R.drawable.icon_agree);
            } else if (TextUtils.equals(map.get("status"), "5")) {
                holder.imgvState.setVisibility(View.GONE);
                holder.imgvSign.setVisibility(View.VISIBLE);
                holder.imgvSign.setImageResource(R.drawable.icon_signed);
                holder.selectImg.setVisibility(View.GONE);
                linlayBtn.setVisibility(View.GONE);
            }

            //根据选择选中状态
            if (selMumberList.contains(map.get(MEMBER_NUMBER))) {    //选中集合里包含会员编号
                holder.selectImg.setImageResource(R.drawable.btn_select);
            } else {     //选中集合里 不 包含会员编号
                holder.selectImg.setImageResource(R.drawable.btn_unselect);
            }

            //选中状态处理
//            holder.selectImg.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
////                    if (selMumberList.contains(map.get(MEMBER_NUMBER))) {     //选中集合里包含会员编号
////                        holder.selectImg.setImageResource(R.drawable.btn_unselect);
////                        //遍历集合 移除带有此会员编号的内容
////                        for (int i = 0; i < ListUtils.getSize(selMumberList); i++) {
////                            if (selMumberList.get(i).equals(map.get(MEMBER_NUMBER))) {
////                                selMumberList.remove(i);
////                            }
////                            allSelState();
////                        }
////                    } else {     //选中集合里 不 包含会员编号
//                    selMumberList.clear();
//                    holder.selectImg.setImageResource(R.drawable.btn_select);
//                    selMumberList.add(map.get(MEMBER_NUMBER));
//                    notifyDataSetChanged();
////                        allSelState();
////                  }
//
//                }
//            });
//            int visibility = holder.selectImg.getVisibility();
//            LogUtil.e(visibility + "/////////////////////////////");
            holder.linlayFeedback.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (TextUtils.equals(getIntent().getStringExtra("flag"), "complete")) {
                        Bundle bundle = new Bundle();
                        bundle.putString("code", map.get("lab_noid"));
                        bundle.putString("flag", "collect");
                        startActivity(MemberDetailAty.class, bundle);
                    }else {
                        selMumberList.clear();
                        holder.selectImg.setImageResource(R.drawable.btn_select);
                        selMumberList.add(map.get(MEMBER_NUMBER));
                        notifyDataSetChanged();
                    }

                }
            });
            holder.headerCir.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    bundle.putString("code", map.get("lab_noid"));
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
//                allSelTv.setCompoundDrawablesWithIntrinsicBounds(R.drawable.btn_select, 0, 0, 0);
            } else {
                isAllSelect = false;
//                allSelTv.setCompoundDrawablesWithIntrinsicBounds(R.drawable.btn_unselect, 0, 0, 0);
            }
        }

        @Override
        public int getItemCount() {
            return ListUtils.getSize(collectData);
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            @ViewInject(R.id.select_img)
            ImageView selectImg;
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
            @ViewInject(R.id.list_vfeedback_state)
            ImageView imgvState;
            @ViewInject(R.id.list_vfeedback_click)
            LinearLayout linlayFeedback;
            @ViewInject(R.id.list_vfeedback_state_sign)
            ImageView imgvSign;

            public ViewHolder(View itemView) {
                super(itemView);
                x.view().inject(this, itemView);
                AutoUtils.auto(itemView);
            }
        }
    }
}
