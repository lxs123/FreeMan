package com.toocms.freeman.ui.searchjob;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.toocms.frame.image.ImageLoader;
import com.toocms.frame.ui.BaseFragment;
import com.toocms.freeman.R;
import com.toocms.freeman.config.JsonArryToList;
import com.toocms.freeman.https.Collect;
import com.toocms.freeman.https.Hire;
import com.toocms.freeman.https.User;
import com.toocms.freeman.ui.infomationaty.NewBaseInfoAty;
import com.toocms.freeman.ui.mine.baseinformation.SkillInformationInBaseAty;
import com.toocms.freeman.ui.recruitment.OtherEvaluationAty;
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
import cn.zero.android.common.view.FButton;
import cn.zero.android.common.view.shapeimageview.CircularImageView;
import cn.zero.android.common.view.swipetoloadlayout.OnRefreshListener;
import cn.zero.android.common.view.swipetoloadlayout.view.SwipeToLoadRecyclerView;

public class ManagementFgt extends BaseFragment implements OnRefreshListener {

    @ViewInject(R.id.labour_detail_swipe)
    private SwipeToLoadRecyclerView labourDetailSwipe;
    @ViewInject(R.id.new_jo_release)
    private FButton newJoReleaseFbtn;
    @ViewInject(R.id.manage_fbtn_content)
    private LinearLayout managerFbtnContentLl;


    //item数据源
    List<Map<String, String>> itemlistData = new ArrayList<>();
    //接口相关 -- item键
    private final String JOB_CONTENT = "job_content";
    private final String PRICE = "price";
    private final String DATA = "data";
    private final String MONEY = "money";

    DecimalFormat decimalFormat = new DecimalFormat("#0.00");

    private LabourDetailAdapter mLabourDetailAdapter;
    private LinearLayout mTransactionRecordLl;
    private ImageLoader mImageLoader;

    private Hire mHire;
    private User mUser;
    public String status;   //签约状态(仅在搜索--》已抢招工单中使用)
    public String coor_diff;    //招工单修改过没有(仅在搜索--》已抢招工单中使用)
    public String noid;
    public String hireNoidStr;
    public String labNoidStr;
    private Map<String, String> mDatamap;    //接口数据
    private CircularImageView mHeaderDetailImg;
    private TextView mHeaderNicknameTv;
    private TextView mHeaderComplainsTv;
    private TextView mHeaderBreachTv;
    private TextView mHeaderDisputeTv;
    private TextView mHeaderCostCount;
    private ArrayList<Map<String, String>> mDaybook;
    private TextView mHeaderInformationTv;
    private TextView mHeaderSkillInfoTv;
    private TextView mHeaderEvaluateTv;
    private boolean isCollectd;
    private MenuItem item;
    private Collect collect;
    private String coor_status;

//    //模拟数据
//    {
//        for (int i = 0; i < 3; i++) {
//            Map<String, String> map = new HashMap<>();
//            map.put(JOB_CONTENT, "家装 厕所维修");
//            map.put(PRICE, "120.00");
//            map.put(DATA, "2016-12-12");
//            map.put(MONEY, "5600.00");
//            itemlistData.add(map);
//        }
//    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (getActivity() instanceof RecruitmentOrderAt) {
            hireNoidStr = ((RecruitmentOrderAt) getActivity()).hireNoidStr;
            noid = ((RecruitmentOrderAt) getActivity()).noid;
            labNoidStr = ((RecruitmentOrderAt) getActivity()).labNoidStr;
//            coor_status = ((RecruitmentOrderAt) getActivity()).coor_status;
        } else if (getActivity() instanceof MyJobOrderDetailAty) {
            hireNoidStr = ((MyJobOrderDetailAty) getActivity()).hireNoidStr;
            noid = ((MyJobOrderDetailAty) getActivity()).noid;
            labNoidStr = ((MyJobOrderDetailAty) getActivity()).labNoidStr;
            status = ((MyJobOrderDetailAty) getActivity()).status;
            coor_status = ((MyJobOrderDetailAty) getActivity()).coor_status;
        } else if (getActivity() instanceof AlreadyRobJobOrderDetailAty) {
            hireNoidStr = ((AlreadyRobJobOrderDetailAty) getActivity()).hireNoidStr;
            noid = ((AlreadyRobJobOrderDetailAty) getActivity()).noid;
            labNoidStr = ((AlreadyRobJobOrderDetailAty) getActivity()).labNoidStr;
            status = ((AlreadyRobJobOrderDetailAty) getActivity()).status;
            coor_status = ((AlreadyRobJobOrderDetailAty) getActivity()).coor_status;
        }
        showProgressContent();
        mUser.userView(labNoidStr, noid, this);


        if (status != null && status.equals("3")) {
            managerFbtnContentLl.setVisibility(View.GONE);
        } else if (TextUtils.equals(status, "2")) {
            if (TextUtils.equals(coor_status, "2")) {
                managerFbtnContentLl.setVisibility(View.GONE);
            }
        }

        labourDetailSwipe.setOnRefreshListener(this);
        labourDetailSwipe.getRecyclerView().setLayoutManager(new LinearLayoutManager(getActivity()));

    }

    @Override
    public void onComplete(RequestParams params, String result) {
        if (params.getUri().contains("User/userView")) {
            mDatamap = JSONUtils.parseDataToMap(result);
            initHeader();
            mImageLoader.disPlay(mHeaderDetailImg, mDatamap.get("head"));
            mHeaderNicknameTv.setText(mDatamap.get("nickname"));
            mHeaderComplainsTv.setText(mDatamap.get("complains"));
            mHeaderBreachTv.setText(mDatamap.get("breach"));
            mHeaderDisputeTv.setText(mDatamap.get("dispute"));
            mHeaderCostCount.setText(mDatamap.get("cost_count"));
            mDaybook = JSONUtils.parseKeyAndValueToMapList(mDatamap.get("daybook"));
            Log.e("***", mDaybook.toString());
            isCollectd = (mDatamap.get("is_collect").equals("0")) ? false : true;
//            if (!isCollectd) {
//                item.setTitle("收藏");
//            } else {
//                item.setTitle("取消收藏");
//            }
//            if (item != null)
            if (!isCollectd) {
                item.setTitle("收藏");
            } else {
                item.setTitle("取消收藏");
            }
            updateUI();
        }

        if (params.getUri().contains("Hire/labReplyAccept")) {
            showToast(JSONUtils.parseKeyAndValueToMap(result).get("message"));
            new TextView(getActivity()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (getActivity() != null)
                        getActivity().finish();
                }
            }, 1500);
        } else if (params.getUri().contains("Collect/cancelLab")) {
            showToast(JSONUtils.parseKeyAndValueToMap(result).get("message"));
            item.setTitle("收藏");
        } else if (params.getUri().contains("Collect/insertLab")) {
            showToast(JSONUtils.parseKeyAndValueToMap(result).get("message"));
            item.setTitle("取消收藏");
        }
        super.onComplete(params, result);
    }

    @Event({R.id.new_jo_release})
    private void onClick(View view) {
        switch (view.getId()) {
            case R.id.new_jo_release:
                if (getActivity() instanceof RecruitmentOrderAt) {
                    showProgressDialog();
                    mHire.labReplyAccept(application.getUserInfo().get("noid"),
                            ((RecruitmentOrderAt) getActivity()).hireNoidStr,
                            ((RecruitmentOrderAt) getActivity()).noid,
                            this);
                } else if (getActivity() instanceof MyJobOrderDetailAty) {
                    showProgressDialog();
                    mHire.labReplyAccept(application.getUserInfo().get("noid"),
                            ((MyJobOrderDetailAty) getActivity()).hireNoidStr,
                            ((MyJobOrderDetailAty) getActivity()).noid,
                            this);
                } else if (getActivity() instanceof AlreadyRobJobOrderDetailAty) {
                    showProgressDialog();
                    mHire.labReplyAccept(application.getUserInfo().get("noid"),
                            ((AlreadyRobJobOrderDetailAty) getActivity()).hireNoidStr,
                            ((AlreadyRobJobOrderDetailAty) getActivity()).noid,
                            this);
                }
                break;
        }
    }

    //初始化头部
    private void initHeader() {
        View view = View.inflate(getActivity(), R.layout.aty_header_layout_detail, null);
        //头像
        mHeaderDetailImg = (CircularImageView) view.findViewById(R.id.header_detail_head);
        //昵称
        mHeaderNicknameTv = (TextView) view.findViewById(R.id.header_nickname);
        //投诉数量
        mHeaderComplainsTv = (TextView) view.findViewById(R.id.header_complains);
        //违约数量
        mHeaderBreachTv = (TextView) view.findViewById(R.id.header_breach);
        //评价信息
        mHeaderDisputeTv = (TextView) view.findViewById(R.id.header_dispute);
        //成交数量
        mHeaderCostCount = (TextView) view.findViewById(R.id.header_cost_count);


        //基本信息
        mHeaderInformationTv = (TextView) view.findViewById(R.id.header_information_tv);
        //技能信息
        mHeaderSkillInfoTv = (TextView) view.findViewById(R.id.header_skill_info);
        //评价信息
        mHeaderEvaluateTv = (TextView) view.findViewById(R.id.evaluate_info_tv);

        mTransactionRecordLl = (LinearLayout) view.findViewById(R.id.transaction_record_ll);
        mTransactionRecordLl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("lab_noid", noid);
                startActivity(TransactionRecordAty.class, bundle);
            }
        });
        mHeaderInformationTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putString("noid", noid);
                bundle.putString("flag", "wzw1");
                startActivity(NewBaseInfoAty.class, bundle);
            }
        });
        mHeaderSkillInfoTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putString("noid", noid);
                bundle.putString("flag", "wzw1");
                startActivity(SkillInformationInBaseAty.class, bundle);
            }
        });
        mHeaderEvaluateTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putString("noid", noid);
                startActivity(OtherEvaluationAty.class, bundle);
            }
        });

        labourDetailSwipe.addHeaderView(view);
    }

    private void updateUI() {
        if (mLabourDetailAdapter == null) {
            mLabourDetailAdapter = new LabourDetailAdapter();
            labourDetailSwipe.setAdapter(mLabourDetailAdapter);
        }
        mLabourDetailAdapter.notifyDataSetChanged();
        labourDetailSwipe.stopRefreshing();
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.fgt_management;
    }

    @Override
    protected void initialized() {
        mHire = new Hire();
        mUser = new User();
        collect = new Collect();
        mImageLoader = new ImageLoader();
    }

    @Override
    protected void requestData() {
        setHasOptionsMenu(true);

    }

    //swiper刷新
    @Override
    public void onRefresh() {
//        labourDetailSwipe.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                updateUI();
//            }
//        }, 2000);
        mUser.userView(labNoidStr, noid, this);
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu_collect2, menu);
        item = menu.findItem(R.id.menu_collect2);
        if (item != null)
            if (!isCollectd) {
                item.setTitle("取消收藏");
            } else {
                item.setTitle("收藏");
            }
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {


        super.onPrepareOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_collect2:
                showProgressDialog();
                if (!isCollectd) {
                    collect.insertLab(application.getUserInfo().get("noid"), noid, this);
                } else {
                    collect.cancelLab(application.getUserInfo().get("noid"), noid, this);
                }
                isCollectd = !isCollectd;
                break;
        }

//        break;
//        switch (item.getItemId()) {
//            case R.id.menu_submit:
//
//        }
        return super.onOptionsItemSelected(item);
    }

    //=====================Adapter============================
    public class LabourDetailAdapter extends RecyclerView.Adapter<LabourDetailAdapter.ViewHolder> {
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(getActivity()).inflate(R.layout.listitem_labour_detail, parent, false);
            ViewHolder viewHolder = new ViewHolder(view);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
//            Map<String, String> map = itemlistData.get(position);
            holder.jobContentTv.setText("工作内容：" + ListUtils.join(JsonArryToList.strList(mDaybook.get(position).get("skill_name"))));
            holder.priceTv.setText("单价：" + decimalFormat.format(Double.parseDouble(mDaybook.get(position).get("subtotal"))));
            holder.dataTv.setText(mDaybook.get(position).get("create_time"));
            holder.moneyTv.setText("￥" + decimalFormat.format(Double.parseDouble(mDaybook.get(position).get("amount"))));
            if (mDaybook.get(position).get("mutual").equals("+1")) {
                holder.moneyTv.setText("￥" + decimalFormat.format(Double.parseDouble(mDaybook.get(position).get("amount"))));
                holder.moneyTv.setTextColor(Color.parseColor("#1B59BD"));
            } else if (mDaybook.get(position).get("mutual").equals("-1")) {
                holder.moneyTv.setText("￥" + decimalFormat.format(Double.parseDouble(mDaybook.get(position).get("amount"))));
                holder.moneyTv.setTextColor(Color.parseColor("#FE4A4A"));
            } else {
                holder.moneyTv.setText("￥" + decimalFormat.format(Double.parseDouble(mDaybook.get(position).get("amount"))));
                holder.moneyTv.setTextColor(Color.parseColor("#1B59BD"));
            }
            //去掉最后一条item的线
            if (position == getItemCount() - 1) {
                holder.itemDivider.setVisibility(View.GONE);
            }
        }

        @Override
        public int getItemCount() {
            return ListUtils.getSize(mDaybook);
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            @ViewInject(R.id.job_content_tv)
            TextView jobContentTv;
            @ViewInject(R.id.price_tv)
            TextView priceTv;
            @ViewInject(R.id.data_tv)
            TextView dataTv;
            @ViewInject(R.id.money_tv)
            TextView moneyTv;
            @ViewInject(R.id.item_divider)
            View itemDivider;

            public ViewHolder(View itemView) {
                super(itemView);
                x.view().inject(this, itemView);
                AutoUtils.auto(itemView);
            }
        }
    }
}
