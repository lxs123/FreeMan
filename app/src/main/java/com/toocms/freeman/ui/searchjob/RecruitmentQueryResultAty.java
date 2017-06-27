package com.toocms.freeman.ui.searchjob;

import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.toocms.frame.config.Settings;
import com.toocms.frame.image.ImageLoader;
import com.toocms.freeman.R;
import com.toocms.freeman.config.JsonArryToList;
import com.toocms.freeman.https.Hire;
import com.toocms.freeman.ui.BaseAty;
import com.toocms.freeman.ui.index.ModifyDetailsAty;
import com.toocms.freeman.ui.index.ModifyRecruitmentInformationAty;
import com.zero.autolayout.utils.AutoUtils;

import org.xutils.http.RequestParams;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;
import java.util.Map;

import cn.zero.android.common.recyclerview.RecycleViewDivider;
import cn.zero.android.common.util.JSONUtils;
import cn.zero.android.common.util.ListUtils;
import cn.zero.android.common.view.FButton;
import cn.zero.android.common.view.shapeimageview.CircularImageView;
import cn.zero.android.common.view.swipetoloadlayout.OnLoadMoreListener;
import cn.zero.android.common.view.swipetoloadlayout.OnRefreshListener;
import cn.zero.android.common.view.swipetoloadlayout.view.SwipeToLoadRecyclerView;

/**
 * 招工信息查询结果
 */
public class RecruitmentQueryResultAty extends BaseAty implements OnRefreshListener, OnLoadMoreListener {

    public int btn_count;   //告诉下一页按钮的个数

    @ViewInject(R.id.tv1)
    private TextView tv1;
    @ViewInject(R.id.tv2)
    private TextView tv2;
    @ViewInject(R.id.tv3)
    private TextView tv3;
    @ViewInject(R.id.tv4)
    private TextView tv4;
    @ViewInject(R.id.null_tv)
    TextView nullTv;

    @ViewInject(R.id.query_result_swipe)
    SwipeToLoadRecyclerView mSwipeToLoadRecyclerView;
    AtyRecruitmentQueryResultAdapter mResultAdapter;

    //用来处理下划线的属性动画相关
    @ViewInject(R.id.take_title_view)
    private View vLine;
    int position = 1;
    //分页显示
    int p = 1;
    //接口相关
    Hire mHire;
    //排序
    private final String LEVEL = "level";
    private final String DISTANCE = "distance";
    private final String MAXPRICE = "minprice";
    private final String MINPRICE = "maxprice";
    private final String WORKFAST = "workfast";
    private final String WORKLONG = "worklong";
    private String NOWSORT = LEVEL;
    private ArrayList<Map<String, String>> dateList;
    private String noid;
    private String hire_noid;
    private String cap_noid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActionBar.setTitle("招工信息查询结果");

        mSwipeToLoadRecyclerView.setOnRefreshListener(this);
        mSwipeToLoadRecyclerView.setOnLoadMoreListener(this);
        mSwipeToLoadRecyclerView.getRecyclerView().setLayoutManager(new LinearLayoutManager(this));
        mSwipeToLoadRecyclerView.getRecyclerView().addItemDecoration(new RecycleViewDivider(
                this, LinearLayoutManager.HORIZONTAL, AutoUtils.getPercentHeightSize(20), getResources().getColor(R.color.clr_bg)));
        showProgressDialog();
        doLibrary();

    }

    @Override
    protected void onResume() {
        super.onResume();
//        p = 1;
        showProgressDialog();
        doLibrary();
    }

    @Override
    public void onComplete(RequestParams params, String result) {
        if (params.getUri().contains("Hire/library")) {
            // TODO: 2017/4/20 拿到数据
//            if (ListUtils.isEmpty(JSONUtils.parseDataToMapList(result))) {
////                showToast("没有更多的数据了~~");
//                mSwipeToLoadRecyclerView.stopLoadMore();
//                mSwipeToLoadRecyclerView.stopRefreshing();
//                removeProgressDialog();
////                return;
//            }
            if (p == 1) {
                dateList = JSONUtils.parseDataToMapList(result);
            } else {
                ArrayList<Map<String, String>> arrayList = JSONUtils.parseDataToMapList(result);
                if (ListUtils.isEmpty(arrayList)) {
                    p--;
                }
                dateList.addAll(arrayList);
//                if (ListUtils.isEmpty(arrayList)) {
//
//                }
            }
            updateUI();
        } else if (params.getUri().contains("Hire/labReplyAccept")) {
            showToast(JSONUtils.parseKeyAndValueToMap(result).get("message"));
            showProgressDialog();
            doLibrary();
        } else if (params.getUri().contains("Hire/cancelAccept")) {
            showToast(JSONUtils.parseKeyAndValueToMap(result).get("message"));
            showProgressDialog();
            doLibrary();
        }
        super.onComplete(params, result);
        mSwipeToLoadRecyclerView.stopLoadMore();
        mSwipeToLoadRecyclerView.stopRefreshing();
    }

    @Override
    public void onError(Map<String, String> error) {
        super.onError(error);
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

//        showProgressDialog();
        mHire.library(application.getUserInfo().get("noid"),
                String.valueOf(p),
                NOWSORT,
                getIntent().getStringExtra("work_week"),
                getIntent().getStringExtra("min_price"),
                getIntent().getStringExtra("max_price"),
                getIntent().getStringExtra("cap_noid"),
                getIntent().getStringExtra("skill_id"),
                getIntent().getStringExtra("distance"),
                getIntent().getStringExtra("province_id"),
                getIntent().getStringExtra("city_id"),
                getIntent().getStringExtra("area_id"),
                getIntent().getStringExtra("contract_starttime"),
                getIntent().getStringExtra("contract_endtime"),
                getIntent().getStringExtra("work_starttime"),
                getIntent().getStringExtra("work_endtime"),
                getIntent().getStringExtra("hire_noid"),
                getIntent().getStringExtra("keyword"),
                this);
    }

    @Event({R.id.tv1, R.id.tv2, R.id.ll3, R.id.ll4})
    private void onClick(View view) {
        showProgressDialog();
        switch (view.getId()) {
            case R.id.tv1:
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
                break;
            case R.id.ll3:
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
        }
    }

    private void initTvColor() {
        tv1.setTextColor(Color.parseColor("#323232"));
        tv2.setTextColor(Color.parseColor("#323232"));
        tv3.setTextColor(Color.parseColor("#323232"));
        tv4.setTextColor(Color.parseColor("#323232"));
    }

    private void updateUI() {

        if (ListUtils.isEmpty(dateList)) {
            nullTv.setVisibility(View.VISIBLE);
//            return;
        } else {
            nullTv.setVisibility(View.GONE);
        }


//        if (mResultAdapter) {
//            mResultAdapter = new AtyRecruitmentQueryResultAdapter();
//            mSwipeToLoadRecyclerView.setAdapter(mResultAdapter);
//        } else {
        if (mResultAdapter == null) {
            mResultAdapter = new AtyRecruitmentQueryResultAdapter();
            mSwipeToLoadRecyclerView.setAdapter(mResultAdapter);
        } else
            mResultAdapter.notifyDataSetChanged();
//        }

//        mSwipeToLoadRecyclerView.stopRefreshing();
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.aty_recruitment_query_result;
    }

    @Override
    protected void initialized() {
        mHire = new Hire();
    }

    @Override
    protected void requestData() {

    }

    //下拉刷新监听
    @Override
    public void onRefresh() {
        p = 1;
        doLibrary();
    }

    //上拉加载
    @Override
    public void onLoadMore() {
        p++;
        doLibrary();
    }

    //===================Adapter相关=================
    public class AtyRecruitmentQueryResultAdapter extends RecyclerView.Adapter<AtyRecruitmentQueryResultAdapter.ViewHolder> {

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(RecruitmentQueryResultAty.this).inflate(R.layout.listitem_query_result, parent, false);
            ViewHolder viewHolder = new ViewHolder(view);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {
            final Map<String, String> map = dateList.get(position);
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
            holder.tvNum.setText(map.get("photos_count") + "张");
            if (TextUtils.equals(map.get("attestation"), "1")) {
                holder.tvAuthentication.setText("已认证");
            } else {
                holder.tvAuthentication.setText("未认证");
            }
            if (TextUtils.equals(map.get("photos_count"), "0")) {
//                holder.imgvPhoto.setImageResource(R.drawable.icon_photo_none);
                holder.imgvPhoto.setImageResource(R.drawable.icon_photo_none);
            } else {
                imageLoader.disPlay(holder.imgvPhoto, map.get("cover"));
            }
//            imageLoader.disPlay(holder.imgvPhoto, map.get("cover"));

            if (map.get("status").equals("2")) {     //招工中
                holder.coorstatusImg.setVisibility(View.VISIBLE);
                if (map.get("coor_diff").equals("0")) {  //左上角：未修改
                    holder.coorstatusImg.setImageDrawable(getResources().getDrawable(R.drawable.icon_unmodify));
                } else {     //左上角：已修改
                    holder.coorstatusImg.setImageDrawable(getResources().getDrawable(R.drawable.icon_modify));
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
                            showDialog("", "确认要取消该招工单？", 0, "确认", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    showProgressDialog();
                                    mHire.cancelAccept(dateList.get(position).get("hire_noid"), application.getUserInfo().get("noid"), RecruitmentQueryResultAty.this);
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
                            bundle.putString("flag", "wzw1");    //搜工作--》查询招工信息--》确认--》查看修改明细
                            bundle.putString("hire_noid", dateList.get(position).get("hire_noid"));
                            bundle.putString("lab_noid", application.getUserInfo().get("noid"));
                            bundle.putString("noid", dateList.get(position).get("noid"));
                            startActivity(ModifyDetailsAty.class, bundle);
                        }
                    });
                    holder.modifyBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            showDialog("", "确认要取消该招工单吗？", 0, "确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    showProgressDialog();
                                    mHire.cancelAccept(dateList.get(position).get("hire_noid"), application.getUserInfo().get("noid"), RecruitmentQueryResultAty.this);
                                }
                            }, "取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                }
                            });
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
                                    mHire.labReplyAccept(noid, hire_noid, cap_noid, RecruitmentQueryResultAty.this);
                                }
                            }, "取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            });
                        }
                    });
                } else if (map.get("coor_status").equals("0")) {  //未操作
                    holder.checkbtn.setVisibility(View.GONE);
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
                                    mHire.labReplyAccept(noid, hire_noid, cap_noid, RecruitmentQueryResultAty.this);
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
                            bundle.putString("hire_noid", dateList.get(position).get("hire_noid"));
                            bundle.putString("lab_noid", application.getUserInfo().get("noid"));
                            startActivity(ModifyRecruitmentInformationAty.class, bundle);
                        }
                    });
                }
            } else if (map.get("status").equals("3")) {   //已签约
                holder.bbrqyImg.setVisibility(View.VISIBLE);
                holder.fBtnUp.setVisibility(View.GONE);
                holder.modifyBtn.setVisibility(View.GONE);
                holder.checkbtn.setVisibility(View.GONE);
                holder.coorstatusImg.setVisibility(View.GONE);
            }

            holder.informationLi.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Bundle bundle = new Bundle();
                    bundle.putString("flag", "wzw1");
                    bundle.putString("item", "1");
                    bundle.putString("lab_noid", application.getUserInfo().get("noid"));  //劳方noid
                    bundle.putString("hire_noid", dateList.get(position).get("hire_noid")); //招工单noid
                    bundle.putString("noid", dateList.get(position).get("noid"));  //对方编号
                    bundle.putString("status", map.get("status"));   //签约状态
                    bundle.putString("coor_status", map.get("coor_status"));     //0为未操作，1同意招工单  2修改过招工单等待回复  3资方针对修改做出回复
                    bundle.putString("coor_diff", map.get("coor_diff")); //招工单是否被修改
                    startActivity(MyJobOrderDetailAty.class, bundle);
                }
            });


            holder.jobInformationDetailLl.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Bundle bundle = new Bundle();
                    bundle.putString("flag", "wzw1");
                    bundle.putString("lab_noid", application.getUserInfo().get("noid"));  //劳方noid
                    bundle.putString("hire_noid", dateList.get(position).get("hire_noid")); //招工单noid
                    bundle.putString("noid", dateList.get(position).get("noid"));  //对方编号
                    bundle.putString("status", map.get("status"));   //签约状态
                    bundle.putString("coor_status", map.get("coor_status"));     //0为未操作，1同意招工单  2修改过招工单等待回复  3资方针对修改做出回复
                    bundle.putString("coor_diff", map.get("coor_diff")); //招工单是否被修改
                    startActivity(MyJobOrderDetailAty.class, bundle);
                }
            });

        }

        @Override
        public int getItemCount() {
            return ListUtils.getSize(dateList);
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            //            底部详情布局
            @ViewInject(R.id.job_information_detail_ll)
            LinearLayout jobInformationDetailLl;
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
            ImageView imgvPhoto;
            @ViewInject(R.id.coor_status_img)
            ImageView coorstatusImg;    //左上角图片
            @ViewInject(R.id.check_btn)
            Button checkbtn;    //左边的按钮
            @ViewInject(R.id.release_fbtn)
            FButton fBtnUp;    //右边的按钮
            @ViewInject(R.id.modify_btn)
            Button modifyBtn;  //中间的按钮
            @ViewInject(R.id.bbrqy_img)
            ImageView bbrqyImg; //被别人签约
            @ViewInject(R.id.information_ll)
            LinearLayout informationLi;

            public ViewHolder(View itemView) {
                super(itemView);
                x.view().inject(this, itemView);
                AutoUtils.auto(itemView);
            }
        }
    }
}
