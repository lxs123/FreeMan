package com.toocms.freeman.ui.index;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.toocms.freeman.R;
import com.toocms.freeman.https.Message;
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
 * 首页消息页
 * Created by admin on 2017/3/22.
 */

public class IndexMessageAty extends BaseAty implements OnRefreshListener, OnLoadMoreListener {
    @ViewInject(R.id.index_message_list)
    private SwipeToLoadRecyclerView swipeToLoadRecyclerView;
    /**
     * 消息列表[listing]
     *
     * @param noid 用户编号
     * @param page 分页，默认为1
     */
    private String noid;
    private int page = 1;
    private Message message;
    private ArrayList<Map<String, String>> list;
    private MessageAdapter messageAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActionBar.setTitle("消息");
        swipeToLoadRecyclerView.getRecyclerView().setLayoutManager(new LinearLayoutManager(this));
        swipeToLoadRecyclerView.setOnRefreshListener(this);
        swipeToLoadRecyclerView.setOnLoadMoreListener(this);

    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected int getLayoutResId() {
        return R.layout.aty_index_message;
    }

    @Override
    protected void initialized() {
        message = new Message();
    }

    @Override
    protected void requestData() {
        showProgressDialog();
        noid = application.getUserInfo().get("noid");
        message.listing(noid, page + "", this);
    }

    @Override
    public void onComplete(RequestParams params, String result) {
        if (params.getUri().contains("Message/listing")) {

            if (page == 1) {
                list = JSONUtils.parseDataToMapList(result);
            } else {
                ArrayList<Map<String, String>> newList = JSONUtils.parseDataToMapList(result);
                if (ListUtils.isEmpty(newList)) {
                    page--;
                }
                list.addAll(newList);
            }


        }
        if (messageAdapter == null) {
            messageAdapter = new MessageAdapter();
            swipeToLoadRecyclerView.setAdapter(messageAdapter);
        } else {
            messageAdapter.notifyDataSetChanged();
        }

        super.onComplete(params, result);
        swipeToLoadRecyclerView.stopLoadMore();
        swipeToLoadRecyclerView.stopRefreshing();
    }

    @Override
    public void onRefresh() {
        page = 1;
        message.listing(noid, page + "", this);
    }


    @Override
    public void onLoadMore() {
        page++;
        message.listing(noid, page + "", this);
    }

    private class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {


        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.listitem_index_message, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            final Map<String, String> map = list.get(position);
            holder.tvTitle.setText(map.get("title"));
            holder.tvContent.setText(map.get("content"));
            holder.tvCreateTime.setText(map.get("create_time"));
            if (TextUtils.equals(map.get("viewed"), "0")) {
                holder.vSpot.setVisibility(View.VISIBLE);
            } else {
                holder.vSpot.setVisibility(View.GONE);
            }
            holder.linlayMessage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    bundle.putString("message_id", map.get("message_id"));
                    holder.vSpot.setVisibility(View.GONE);
                    startActivity(MessageDetailAty.class, bundle);
                }
            });
        }

        @Override
        public int getItemCount() {
            return ListUtils.getSize(list);
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            @ViewInject(R.id.message_title)
            TextView tvTitle;
            @ViewInject(R.id.message_spot)
            View vSpot;
            @ViewInject(R.id.message_create_time)
            TextView tvCreateTime;
            @ViewInject(R.id.message_content)
            TextView tvContent;
            @ViewInject(R.id.message_lay)
            LinearLayout linlayMessage;

            public ViewHolder(View itemView) {
                super(itemView);
                x.view().inject(this, itemView);
                AutoUtils.autoSize(itemView);
            }
        }
    }
}
