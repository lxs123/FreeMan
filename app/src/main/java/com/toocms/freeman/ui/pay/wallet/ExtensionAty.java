package com.toocms.freeman.ui.pay.wallet;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.toocms.freeman.R;
import com.toocms.freeman.https.Seminate;
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
 * 推广金额页
 * Created by admin on 2017/3/29.
 */

public class ExtensionAty extends BaseAty implements OnLoadMoreListener, OnRefreshListener {
    @ViewInject(R.id.extension_list)
    private SwipeToLoadRecyclerView swipeToLoadRecyclerView;
    View view;
    @ViewInject(R.id.extension_empty)
    private TextView tvEmpty;
    /**
     * 推广奖励列表[invite]
     *
     * @param noid 用户id
     * @param page
     */
    private String noid;
    private String page;
    private Seminate seminate;
    private TextView tvTotal;
    private ArrayList<Map<String, String>> list;
    private RecyclerAdapter recyclerAdapter;
    private int p = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        swipeToLoadRecyclerView.getRecyclerView().setLayoutManager(new LinearLayoutManager(this));
        swipeToLoadRecyclerView.setOnLoadMoreListener(this);
        swipeToLoadRecyclerView.setOnRefreshListener(this);
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.aty_extension;
    }

    @Override
    protected void initialized() {
        seminate = new Seminate();
    }

    @Override
    protected void requestData() {
        if (view == null) {
            view = LayoutInflater.from(this).inflate(R.layout.header_extension, null);
            tvTotal = (TextView) view.findViewById(R.id.header_total_price);
        }
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        view.setLayoutParams(params);
        swipeToLoadRecyclerView.addHeaderView(view);
        showProgressDialog();
        noid = application.getUserInfo().get("noid");
        seminate.invite(noid, page, this);
    }

    @Override
    public void onComplete(RequestParams params, String result) {
        if (params.getUri().contains("Trade/invite")) {
            Map<String, String> map = JSONUtils.parseDataToMap(result);
            tvTotal.setText("￥" + map.get("amount"));
            if (p == 1) {
                list = JSONUtils.parseKeyAndValueToMapList(map.get("trade_list"));
            } else {
                ArrayList<Map<String, String>> trade_list = JSONUtils.parseKeyAndValueToMapList(map.get("trade_list"));
                if (ListUtils.isEmpty(trade_list)) {
                    p--;
                }
                list.addAll(trade_list);
            }
            if (recyclerAdapter == null) {
                recyclerAdapter = new RecyclerAdapter();
                swipeToLoadRecyclerView.setAdapter(recyclerAdapter);
            } else {
                recyclerAdapter.notifyDataSetChanged();
            }
            if (ListUtils.isEmpty(list)) {
                tvEmpty.setVisibility(View.VISIBLE);
            } else {
                tvEmpty.setVisibility(View.GONE);
            }

        }
        super.onComplete(params, result);
        swipeToLoadRecyclerView.stopLoadMore();
        swipeToLoadRecyclerView.stopRefreshing();
    }

    @Override
    public void onLoadMore() {
        p++;
        page = String.valueOf(p);
        seminate.invite(noid, page, this);
    }

    @Override
    public void onRefresh() {
        page = String.valueOf(p);
        seminate.invite(noid, page, this);
    }

    private class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {


        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.listitem_extension, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            Map<String, String> map = list.get(position);
            holder.tvNickname.setText(map.get("nickname"));
            holder.tvSettlement.setText("￥" + map.get("amount"));
            holder.tvMember.setText(map.get("noid"));
            holder.tvPrice.setText("￥" + map.get("reward"));
            holder.tvDate.setText(map.get("create_time"));
        }

        @Override
        public int getItemCount() {
            return ListUtils.getSize(list);
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            @ViewInject(R.id.list_exten_nickname)
            TextView tvNickname;
            @ViewInject(R.id.list_exten_member)
            TextView tvMember;
            @ViewInject(R.id.list_exten_settlement)
            TextView tvSettlement;
            @ViewInject(R.id.list_exten_price)
            TextView tvPrice;
            @ViewInject(R.id.list_exten_date)
            TextView tvDate;

            public ViewHolder(View itemView) {
                super(itemView);
                x.view().inject(this, itemView);
                AutoUtils.autoSize(itemView);
            }
        }
    }
}
