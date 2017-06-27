package com.toocms.freeman.ui.searchjob;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.toocms.freeman.R;
import com.toocms.freeman.config.JsonArryToList;
import com.toocms.freeman.https.User;
import com.toocms.freeman.ui.BaseAty;
import com.zero.autolayout.utils.AutoUtils;

import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cn.zero.android.common.util.JSONUtils;
import cn.zero.android.common.util.ListUtils;
import cn.zero.android.common.view.swipetoloadlayout.OnRefreshListener;
import cn.zero.android.common.view.swipetoloadlayout.view.SwipeToLoadRecyclerView;

public class TransactionRecordAty extends BaseAty implements OnRefreshListener {

    @ViewInject(R.id.transaction_record_swipe)
    SwipeToLoadRecyclerView transactionRecordSwipe;

    //item数据源
    List<Map<String, String>> itemlistData = new ArrayList<>();
    //接口相关 -- item键
    private final String JOB_CONTENT = "job_content";
    private final String PRICE = "price";
    private final String DATA = "data";
    private final String MONEY = "money";

    DecimalFormat decimalFormat = new DecimalFormat("#0.00");

    private LabourDetailAdapter mLabourDetailAdapter;
    private User user;
    private String noid;
    private String code;
    private List<Map<String, String>> daybook;
    @ViewInject(R.id.transaction_num)
    TextView tvNum;

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
    protected int getLayoutResId() {
        return R.layout.aty_transaction_record;
    }

    @Override
    protected void initialized() {
        user = new User();
    }

    @Override
    protected void requestData() {
        noid = application.getUserInfo().get("noid");
        code = getIntent().getStringExtra("lab_noid");

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActionBar.setTitle("交易记录");
        transactionRecordSwipe.setOnRefreshListener(this);
        transactionRecordSwipe.getRecyclerView().setLayoutManager(new LinearLayoutManager(this));
        showProgressDialog();
        user.userView(noid, code, this);
    }

    private void updateUI() {
        if (mLabourDetailAdapter == null) {
            mLabourDetailAdapter = new LabourDetailAdapter();
            transactionRecordSwipe.setAdapter(mLabourDetailAdapter);
        } else {
            mLabourDetailAdapter.notifyDataSetChanged();
        }


    }


    @Override
    public void onComplete(RequestParams params, String result) {
        if (params.getUri().contains("User/userView")) {
            Map<String, String> map = JSONUtils.parseDataToMap(result);
            daybook = JSONUtils.parseKeyAndValueToMapList(map.get("daybook"));
            tvNum.setText(map.get("cost_count"));
            updateUI();
        }
        super.onComplete(params, result);
        transactionRecordSwipe.stopRefreshing();
    }

    //swiper刷新
    @Override
    public void onRefresh() {
        user.userView(noid, code, this);
    }


    //=====================Adapter============================
    public class LabourDetailAdapter extends RecyclerView.Adapter<LabourDetailAdapter.ViewHolder> {

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(TransactionRecordAty.this).inflate(R.layout.listitem_labour_detail, parent, false);
            ViewHolder viewHolder = new ViewHolder(view);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
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
