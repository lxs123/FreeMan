package com.toocms.freeman.ui.index;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.toocms.freeman.R;
import com.toocms.freeman.ui.BaseAty;
import com.zero.autolayout.utils.AutoUtils;

import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.zero.android.common.util.ListUtils;
import cn.zero.android.common.view.swipetoloadlayout.OnRefreshListener;
import cn.zero.android.common.view.swipetoloadlayout.view.SwipeToLoadRecyclerView;

/**
 * 劳方详情 04-11
 */
public class LabourDetailAty extends BaseAty implements OnRefreshListener {

    @ViewInject(R.id.labour_detail_swipe)
    SwipeToLoadRecyclerView labourDetailSwipe;

    //item数据源
    List<Map<String, String>> itemlistData = new ArrayList<>();
    //接口相关 -- item键
    private final String JOB_CONTENT = "job_content";
    private final String PRICE = "price";
    private final String DATA = "data";
    private final String MONEY = "money";

    DecimalFormat decimalFormat = new DecimalFormat("#0.00");

    private LabourDetailAdapter mLabourDetailAdapter;

    //模拟数据
    {
        for (int i = 0; i < 3; i++) {
            Map<String, String> map = new HashMap<>();
            map.put(JOB_CONTENT, "家装 厕所维修");
            map.put(PRICE, "120.00");
            map.put(DATA, "2016-12-12");
            map.put(MONEY, "5600.00");
            itemlistData.add(map);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActionBar.setTitle("劳动详情");

        labourDetailSwipe.setOnRefreshListener(this);
        labourDetailSwipe.getRecyclerView().setLayoutManager(new LinearLayoutManager(this));

        initHeader();

        updateUI();
    }

    //初始化头部
    private void initHeader() {
        View view = View.inflate(this, R.layout.aty_header_layout_detail, null);
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

    //创建确定按钮相关
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_collect, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_collect:
                // TODO: 2017/3/24 调接口
                showToast("收藏");
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.aty_labour_detail;
    }

    @Override
    protected void initialized() {

    }

    @Override
    protected void requestData() {

    }


    //swiper刷新
    @Override
    public void onRefresh() {
        labourDetailSwipe.postDelayed(new Runnable() {
            @Override
            public void run() {
                updateUI();
            }
        }, 2000);
    }

    //=====================Adapter============================
    public class LabourDetailAdapter extends RecyclerView.Adapter<LabourDetailAdapter.ViewHolder> {
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(LabourDetailAty.this).inflate(R.layout.listitem_labour_detail, parent, false);
            ViewHolder viewHolder = new ViewHolder(view);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            Map<String, String> map = itemlistData.get(position);
            holder.jobContentTv.setText("工作内容：" + map.get(JOB_CONTENT));
            holder.priceTv.setText("单价：" + decimalFormat.format(Double.parseDouble(map.get(PRICE))));
            holder.dataTv.setText(map.get(DATA));
            holder.moneyTv.setText("￥" + map.get(MONEY));

            //去掉最后一条item的线
            if (position == getItemCount() - 1) {
                holder.itemDivider.setVisibility(View.GONE);
            }
        }

        @Override
        public int getItemCount() {
            return ListUtils.getSize(itemlistData);
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
