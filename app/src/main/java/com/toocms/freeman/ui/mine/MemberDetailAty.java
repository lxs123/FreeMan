package com.toocms.freeman.ui.mine;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.toocms.frame.image.ImageLoader;
import com.toocms.freeman.R;
import com.toocms.freeman.config.JsonArryToList;
import com.toocms.freeman.https.Collect;
import com.toocms.freeman.https.User;
import com.toocms.freeman.ui.BaseAty;
import com.toocms.freeman.ui.infomationaty.NewBaseInfoAty;
import com.toocms.freeman.ui.mine.baseinformation.SkillInformationInBaseAty;
import com.toocms.freeman.ui.recruitment.OtherEvaluationAty;
import com.toocms.freeman.ui.searchjob.TransactionRecordAty;
import com.zero.autolayout.utils.AutoUtils;

import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;
import java.util.Map;

import cn.zero.android.common.util.JSONUtils;
import cn.zero.android.common.util.ListUtils;
import cn.zero.android.common.view.shapeimageview.CircularImageView;
import cn.zero.android.common.view.swipetoloadlayout.OnRefreshListener;
import cn.zero.android.common.view.swipetoloadlayout.view.SwipeToLoadRecyclerView;

/**
 * Created by admin on 2017/4/20.
 */

public class MemberDetailAty extends BaseAty implements OnRefreshListener {
    @ViewInject(R.id.manage_fbtn_content)
    LinearLayout linlayFbtn;
    @ViewInject(R.id.labour_detail_swipe)
    SwipeToLoadRecyclerView swipeToLoadRecyclerView;
    /**
     * 获取用户信息[userView]
     *
     * @param noid 用户编号
     * @param code 查看的用户编号
     */
    /**
     * 取消收藏用户[cancelLab]
     *
     * @param noid     用户编号
     * @param lab_noid 被收藏的用户noid
     */
    /**
     * 收藏用户[cancelLab]
     *
     * @param noid     用户编号
     * @param lab_noid 收藏的用户noid
     */
    private String lab_noid;
    private String noid;
    private String code;
    private User user;
    private ArrayList<Map<String, String>> daybook;
    private LinearLayout mTransactionRecordLl;
    private LabourDetailAdapter mLabourDetailAdapter;
    private TextView tvBreach;
    private TextView tvComplains;
    private TextView tvNickname;
    private TextView tvDispute;
    private TextView tvCostCount;
    private Map<String, String> map;
    private CircularImageView imgvHead;
    private boolean isCollect = false;
    private Collect collect;
    private TextView tvSkillInfo;
    private TextView tvEvaluate;
    private TextView tvUser;

    @Override
    protected int getLayoutResId() {
        return R.layout.fgt_management;
    }

    @Override
    protected void initialized() {
        user = new User();
        collect = new Collect();
    }

    @Override
    protected void requestData() {
        showProgressContent();
        noid = application.getUserInfo().get("noid");
        code = getIntent().getStringExtra("code");
        user.userView(noid, code, this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        linlayFbtn.setVisibility(View.GONE);
        swipeToLoadRecyclerView.getRecyclerView().setLayoutManager(new LinearLayoutManager(this));
        swipeToLoadRecyclerView.setOnRefreshListener(this);
    }


    @Override
    public void onComplete(RequestParams params, String result) {
        if (params.getUri().contains("User/userView")) {
            map = JSONUtils.parseDataToMap(result);
            daybook = JSONUtils.parseKeyAndValueToMapList(map.get("daybook"));
            isCollect = TextUtils.equals(map.get("is_collect"), "1") ? false : true;
//
            initHeader();
            updateUI();
        } else if (params.getUri().contains("Collect/cancelLab")) {
            showToast(JSONUtils.parseKeyAndValueToMap(result).get("message"));
        } else if (params.getUri().contains("Collect/insertLab")) {
            showToast(JSONUtils.parseKeyAndValueToMap(result).get("message"));
        }
        super.onComplete(params, result);
        swipeToLoadRecyclerView.stopRefreshing();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

//
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        lab_noid = code;
        switch (item.getItemId()) {
            case R.id.menu_collect:
                isCollect = !isCollect;
                if (isCollect) {
                    showProgressDialog();
                    collect.cancelLab(noid, lab_noid, this);
                    item.setTitle("收藏");
                } else {
                    showProgressDialog();
                    collect.insertLab(noid, lab_noid, this);
                    item.setTitle("取消收藏");
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void updateUI() {
        if (mLabourDetailAdapter == null) {
            mLabourDetailAdapter = new LabourDetailAdapter();
            swipeToLoadRecyclerView.setAdapter(mLabourDetailAdapter);
        }
        mLabourDetailAdapter.notifyDataSetChanged();
        swipeToLoadRecyclerView.stopRefreshing();
    }

    //初始化头部
    private void initHeader() {
        //清除之前的MenuItem
        mActionBar.getMenu().clear();
//添加新的MenuItem
        if (isCollect)
            getMenuInflater().inflate(R.menu.menu_collect, mActionBar.getMenu());
        else
            getMenuInflater().inflate(R.menu.menu_collected, mActionBar.getMenu());
        View view = View.inflate(MemberDetailAty.this, R.layout.aty_header_layout_detail, null);

        mTransactionRecordLl = (LinearLayout) view.findViewById(R.id.transaction_record_ll);
        imgvHead = (CircularImageView) view.findViewById(R.id.header_detail_head);
        tvBreach = (TextView) view.findViewById(R.id.header_breach);
        tvComplains = (TextView) view.findViewById(R.id.header_complains);
        tvNickname = (TextView) view.findViewById(R.id.header_nickname);
        tvDispute = (TextView) view.findViewById(R.id.header_dispute);
        tvCostCount = (TextView) view.findViewById(R.id.header_cost_count);
        tvSkillInfo = (TextView) view.findViewById(R.id.header_skill_info);
        tvEvaluate = (TextView) view.findViewById(R.id.evaluate_info_tv);
        tvUser = (TextView) view.findViewById(R.id.header_information_tv);
        tvUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("noid", code);
                if (!TextUtils.equals(getIntent().getStringExtra("flag"), "contract"))
                    bundle.putString("flag", "wzw1");
                startActivity(NewBaseInfoAty.class, bundle);
            }
        });

        //

        mTransactionRecordLl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("lab_noid", code);
                startActivity(TransactionRecordAty.class, bundle);
            }
        });
        ImageLoader imageLoader = new ImageLoader();
        imageLoader.disPlay(imgvHead, map.get("head"));
        tvNickname.setText(map.get("nickname"));
        tvBreach.setText(map.get("breach"));
        tvComplains.setText(map.get("complains"));
        tvDispute.setText(map.get("dispute"));
        tvCostCount.setText(map.get("cost_count"));
        swipeToLoadRecyclerView.addHeaderView(view);
        tvSkillInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("flag", "member_detail");
                bundle.putString("lab_noid", code);
                startActivity(SkillInformationInBaseAty.class, bundle);
            }
        });
        tvEvaluate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lab_noid = code;
                Bundle bundle = new Bundle();
                bundle.putString("noid", lab_noid);
                startActivity(OtherEvaluationAty.class, bundle);
            }
        });

    }

    @Override
    public void onRefresh() {
//        noid = application.getUserInfo().get("noid");
//        code = getIntent().getStringExtra("code");
        user.userView(noid, code, this);
    }

    public class LabourDetailAdapter extends RecyclerView.Adapter<LabourDetailAdapter.ViewHolder> {
        @Override
        public LabourDetailAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.listitem_labour_detail, parent, false);
            ViewHolder viewHolder = new ViewHolder(view);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(LabourDetailAdapter.ViewHolder holder, int position) {
            Map<String, String> bookMap = daybook.get(position);
            holder.tvJobContent.setText("工作内容：" + ListUtils.join(JsonArryToList.strList(bookMap.get("skill_name"))));
            holder.tvPrice.setText("单价：￥" + bookMap.get("subtotal"));
            holder.tvData.setText(bookMap.get("end_time"));

            if (TextUtils.equals(bookMap.get("mutual"), "+1")) {
                holder.tvMoney.setText("￥" + bookMap.get("amount"));
                holder.tvMoney.setTextColor(getResources().getColor(R.color.clr_main));
            } else {
                holder.tvMoney.setText("-￥" + bookMap.get("amount"));
                holder.tvMoney.setTextColor(Color.parseColor("#fe4a4a"));
            }
//            holder.jobContentTv.setText( map.get());
//            holder.priceTv.setText( map.get());
//            holder.dataTv.setText(map.get());
//            holder.moneyTv.setText( map.get());

            //去掉最后一条item的线
            if (position == getItemCount() - 1) {
                holder.itemDivider.setVisibility(View.GONE);
            } else {
                holder.itemDivider.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public int getItemCount() {
            return ListUtils.getSize(daybook);
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            @ViewInject(R.id.job_content_tv)
            TextView tvJobContent;
            @ViewInject(R.id.price_tv)
            TextView tvPrice;
            @ViewInject(R.id.data_tv)
            TextView tvData;
            @ViewInject(R.id.money_tv)
            TextView tvMoney;
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
