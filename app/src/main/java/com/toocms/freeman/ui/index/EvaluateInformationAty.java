package com.toocms.freeman.ui.index;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.toocms.freeman.R;
import com.toocms.freeman.ui.BaseAty;
import com.zero.autolayout.utils.AutoUtils;

import org.xutils.view.annotation.ViewInject;

import java.util.List;
import java.util.Map;

import cn.zero.android.common.recyclerview.RecycleViewDivider;
import cn.zero.android.common.view.shapeimageview.CircularImageView;
import cn.zero.android.common.view.swipetoloadlayout.OnRefreshListener;
import cn.zero.android.common.view.swipetoloadlayout.view.SwipeToLoadRecyclerView;

/**
 * 评价信息页
 */

public class EvaluateInformationAty extends BaseAty implements OnRefreshListener {

    @ViewInject(R.id.evaluate_infomation_swipe)
    SwipeToLoadRecyclerView evaluateInfomationSwipe;
    private AtyEvaluateInfomationAdapter mAdapter;

    //接口相关 键
    private final String CONTRACT_NUMBER = "contract_number";
    private final String JOB_CONTENT = "job_content";
    private final String CONTRACT_START_TIME = "contract_start_time";
    private final String HEADER_CIR = "header_cir";
    private final String NAME = "name";
    private final String PRAISE = "praise";
    private final String COMMENT = "comment";
    private final String COMMENT_DATE = "comment_date";
    private final String REPLY_MESSAGE = "reply_message";

    //数据源
    private List<Map<String, String>> mMapList;

    //模拟数据源
//    {
//        for (int i = 0; i < 2; i++) {
//            Map<String,String> map = new HashMap<>();
//
//        }
//    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActionBar.setTitle("评价信息");

        evaluateInfomationSwipe.setOnRefreshListener(this);
        evaluateInfomationSwipe.getRecyclerView().setLayoutManager(new LinearLayoutManager(this));
        evaluateInfomationSwipe.getRecyclerView().addItemDecoration(new RecycleViewDivider(this, LinearLayoutManager.HORIZONTAL, AutoUtils.getPercentHeightSize(20), getResources().getColor(R.color.clr_bg)));

        updateUI();
    }

    private void updateUI() {
        if (mAdapter == null){
            mAdapter = new AtyEvaluateInfomationAdapter();
            evaluateInfomationSwipe.setAdapter(mAdapter);
        }
        mAdapter.notifyDataSetChanged();
        evaluateInfomationSwipe.stopRefreshing();
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.aty_evaluate_infomation;
    }

    @Override
    protected void initialized() {

    }

    @Override
    protected void requestData() {

    }

    //swipe更新
    @Override
    public void onRefresh() {
        evaluateInfomationSwipe.postDelayed(new Runnable() {
            @Override
            public void run() {
                updateUI();
            }
        },2000);
    }

    //=====================Adapter==========================
    public class AtyEvaluateInfomationAdapter extends RecyclerView.Adapter<AtyEvaluateInfomationAdapter.ViewHolder> {
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(EvaluateInformationAty.this).inflate(R.layout.listitem_evaluate_infomation, parent, false);
            ViewHolder viewHolder = new ViewHolder(view);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {

        }

        @Override
        public int getItemCount() {
            return 2;
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            @ViewInject(R.id.contract_number_tv)
            TextView contractNumberTv;
            @ViewInject(R.id.job_content_tv)
            TextView jobContentTv;
            @ViewInject(R.id.contract_start_time_tv)
            TextView contractStartTimeTv;
            @ViewInject(R.id.header_cir)
            CircularImageView headerCir;
            @ViewInject(R.id.name_tv)
            TextView nameTv;
            @ViewInject(R.id.praise_tv)
            TextView praiseTv;
            @ViewInject(R.id.comment_tv)
            TextView commentTv;
            @ViewInject(R.id.comment_date_tv)
            TextView commentDateTv;
            @ViewInject(R.id.reply_message_tv)
            TextView replyMessageTv;
            @ViewInject(R.id.is_have_reply_ll)
            TextView isHaveReplyLL;

            public ViewHolder(View itemView) {
                super(itemView);
            }
        }
    }
}
