package com.toocms.freeman.ui.pay.wallet;

import android.content.Intent;
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

import com.toocms.freeman.R;
import com.toocms.freeman.https.User;
import com.toocms.freeman.ui.BaseAty;
import com.zero.autolayout.utils.AutoUtils;

import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;
import java.util.Map;

import cn.zero.android.common.util.JSONUtils;
import cn.zero.android.common.util.ListUtils;
import cn.zero.android.common.view.swipetoloadlayout.OnLoadMoreListener;
import cn.zero.android.common.view.swipetoloadlayout.OnRefreshListener;
import cn.zero.android.common.view.swipetoloadlayout.view.SwipeToLoadRecyclerView;

/**
 * 交易明细
 * Created by admin on 2017/3/29.
 */

public class TransactionDetailAty extends BaseAty implements OnRefreshListener, OnLoadMoreListener {
    @ViewInject(R.id.extension_list)
    private SwipeToLoadRecyclerView swipeToLoadRecyclerView;
    @ViewInject(R.id.extension_content)
    private LinearLayout linlayContent;
    @ViewInject(R.id.total_tv)
    private TextView totalTv;
    @ViewInject(R.id.extension_empty)
    private TextView tvEmpty;
    private String flag;
    private User mUser;
    private int p = 1;     //分页
    private String startTimeStr;
    private String endTimeStr;
    private ArrayList<Map<String, String>> mList;
    private RecyclerAdapter mAdapter;

    @Override
    protected int getLayoutResId() {
        return R.layout.aty_extension;
    }

    @Override
    protected void initialized() {
        mUser = new User();
    }

    @Override
    protected void requestData() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        flag = getIntent().getStringExtra("flag");
        if (TextUtils.equals(flag, "payment")) {
            mActionBar.setTitle("提现记录");
            doWithdraws();
        } else {
            linlayContent.setVisibility(View.VISIBLE);
            doTrades();
        }
        swipeToLoadRecyclerView.getRecyclerView().setLayoutManager(new LinearLayoutManager(this));
        swipeToLoadRecyclerView.setOnRefreshListener(this);
        swipeToLoadRecyclerView.setOnLoadMoreListener(this);

    }

    private void doWithdraws() {
        mUser.withdraws(application.getUserInfo().get("noid"),
                String.valueOf(p),
                this);
    }

    private void doTrades() {
        mUser.trades(application.getUserInfo().get("noid"),
                String.valueOf(p),
                startTimeStr,
                endTimeStr,
                this);
    }

    @Override
    public void onComplete(RequestParams params, String result) {
        if (params.getUri().contains("User/trades")) {
            Map<String, String> map = JSONUtils.parseDataToMap(result);

            totalTv.setText("￥" + map.get("total"));
            if (p == 1) {
                mList = JSONUtils.parseKeyAndValueToMapList(map.get("list"));
            } else {
                ArrayList<Map<String, String>> list = JSONUtils.parseKeyAndValueToMapList(map.get("list"));
                if (ListUtils.isEmpty(list)) {
                    p--;
                }
                mList.addAll(list);
            }
            if (ListUtils.isEmpty(mList)) {
                tvEmpty.setVisibility(View.VISIBLE);
            } else {
                tvEmpty.setVisibility(View.GONE);
            }
        }
        if (params.getUri().contains("User/withdraws")) {
            if (p == 1) {
                mList = JSONUtils.parseDataToMapList(result);
            } else {
                ArrayList<Map<String, String>> list = JSONUtils.parseDataToMapList(result);
                if (ListUtils.isEmpty(list)) {
                    p--;
                }
                mList.addAll(list);
            }
            if (ListUtils.isEmpty(mList)) {
                tvEmpty.setVisibility(View.VISIBLE);
            } else {
                tvEmpty.setVisibility(View.GONE);
            }
        }
        upDateUI();
        super.onComplete(params, result);
        swipeToLoadRecyclerView.stopRefreshing();
        swipeToLoadRecyclerView.stopLoadMore();
    }

    private void upDateUI() {
        if (mAdapter == null) {
            mAdapter = new RecyclerAdapter();
            swipeToLoadRecyclerView.setAdapter(mAdapter);
        } else {
            mAdapter.notifyDataSetChanged();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!TextUtils.equals(flag, "payment")) {
            getMenuInflater().inflate(R.menu.menu_screen, menu);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_screen:
                startActivityForResult(ScreenDayAty.class, null, 100);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            p = 1;
            startTimeStr = data.getStringExtra("start_time");
            endTimeStr = data.getStringExtra("end_time");
            doTrades();
        }
    }

    @Override
    public void onRefresh() {
        p = 1;
        if (TextUtils.equals(flag, "payment")) {
            doWithdraws();
        } else {
            doTrades();
        }
    }

    @Override
    public void onLoadMore() {
        p += 1;
        if (TextUtils.equals(flag, "payment")) {
            doWithdraws();
        } else {
            doTrades();
        }
    }

    private class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.listitem_pay_record, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            if (TextUtils.equals(flag, "payment")) {
                holder.tvNoidName.setVisibility(View.GONE);
                holder.tvContractNoid.setVisibility(View.GONE);
                holder.vPay.setVisibility(View.GONE);

            }
            holder.tvName.setText(mList.get(position).get("name"));
            holder.tvNoidName.setVisibility(View.GONE);
            holder.tvContractNoid.setVisibility(View.GONE);
            holder.vPay.setVisibility(View.GONE);
            holder.tvContractNoid.setText(mList.get(position).get("contract_noid"));
            holder.tvCreateTime.setText(mList.get(position).get("create_time"));
            if (TextUtils.equals(flag, "payment")) {
                holder.tvAmount.setText("-￥" + mList.get(position).get("amount"));
                holder.tvAmount.setTextColor(getResources().getColor(R.color.clr_default_text));
            } else {
                if (mList.get(position).get("amount").contains("-")) {
                    holder.tvAmount.setText("￥" + mList.get(position).get("amount"));
                    holder.tvAmount.setTextColor(Color.parseColor("#fb4a4a"));
                } else {
                    holder.tvAmount.setText("￥" + mList.get(position).get("amount"));
                    holder.tvAmount.setTextColor(getResources().getColor(R.color.clr_main));
                }
            }

        }

        @Override
        public int getItemCount() {
            return ListUtils.getSize(mList);
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            @ViewInject(R.id.textView3)
            private TextView tvName;
            @ViewInject(R.id.contract_noid_tv)
            private TextView tvContractNoid;
            @ViewInject(R.id.create_time_tv)
            private TextView tvCreateTime;
            @ViewInject(R.id.amount_tv)
            private TextView tvAmount;
            @ViewInject(R.id.noid_tv)
            private TextView tvNoidName;
            // view 控件 “--”
            @ViewInject(R.id.list_pay_view)
            private View vPay;

            public ViewHolder(View itemView) {
                super(itemView);
                x.view().inject(this, itemView);
                AutoUtils.auto(itemView);
            }
        }
    }
}
