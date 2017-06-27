package com.toocms.freeman.ui.recruitment;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.toocms.frame.image.ImageLoader;
import com.toocms.frame.tool.DateTool;
import com.toocms.freeman.R;
import com.toocms.freeman.config.JsonArryToList;
import com.toocms.freeman.https.Contract;
import com.toocms.freeman.ui.BaseAty;
import com.toocms.freeman.ui.util.DateUtils;
import com.toocms.freeman.ui.view.ImagePagerActivity;
import com.zero.autolayout.utils.AutoUtils;

import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cn.zero.android.common.util.JSONUtils;
import cn.zero.android.common.util.ListUtils;
import cn.zero.android.common.view.shapeimageview.CircularImageView;
import cn.zero.android.common.view.swipetoloadlayout.OnLoadMoreListener;
import cn.zero.android.common.view.swipetoloadlayout.OnRefreshListener;
import cn.zero.android.common.view.swipetoloadlayout.view.SwipeToLoadRecyclerView;

/**
 * 对方评价（例如：资方看劳方的评价）
 * Created by admin on 2017/5/31.
 */

public class OtherEvaluationAty extends BaseAty implements OnRefreshListener, OnLoadMoreListener {
    @ViewInject(R.id.index_message_list)
    SwipeToLoadRecyclerView swipeToLoadRecyclerView;
    @ViewInject(R.id.index_message_empty)
    private TextView tvEmpty;
    private Contract contract;
    private String page;
    private String noid;
    private int p = 1;
    private List<Map<String, String>> list = new ArrayList<>();
    private List<String> photos;
    private MyAdapter myAdapter;

    @Override
    protected int getLayoutResId() {
        return R.layout.aty_index_message;
    }

    @Override
    protected void initialized() {
        contract = new Contract();
    }

    @Override
    protected void requestData() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        swipeToLoadRecyclerView.getRecyclerView().setLayoutManager(new LinearLayoutManager(this));
        swipeToLoadRecyclerView.setOnRefreshListener(this);
        swipeToLoadRecyclerView.setOnLoadMoreListener(this);
        noid = getIntent().getStringExtra("noid");
    }

    @Override
    protected void onResume() {
        super.onResume();
        contract.assessList(noid, "ACCEPT", page, null, null, null, this);
    }

    @Override
    public void onComplete(RequestParams params, String result) {
        if (params.getUri().contains("Contract/assessList")) {

            if (p == 1) {
                list = JSONUtils.parseDataToMapList(result);
            } else {
                ArrayList<Map<String, String>> dataToMapList = JSONUtils.parseDataToMapList(result);
                if (ListUtils.isEmpty(dataToMapList)) {
                    p--;
                    page = String.valueOf(p);
                }
                list.addAll(dataToMapList);
            }

            if (myAdapter == null) {
                myAdapter = new MyAdapter();
                swipeToLoadRecyclerView.setAdapter(myAdapter);
            } else {
                myAdapter.notifyDataSetChanged();
            }

            if (ListUtils.isEmpty(list)) {
                tvEmpty.setVisibility(View.VISIBLE);
            } else {
                tvEmpty.setVisibility(View.GONE);
            }
        }
        super.onComplete(params, result);
        swipeToLoadRecyclerView.stopRefreshing();
        swipeToLoadRecyclerView.stopLoadMore();
    }


    @Override
    public void onRefresh() {
        contract.assessList(noid, "ACCEPT", page, null, null, null, this);
    }

    @Override
    public void onLoadMore() {
        p++;
        page = String.valueOf(p);
        contract.assessList(noid, "ACCEPT", page, null, null, null, this);
    }

    private class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {

        private MyGridAdapter myGridAdapter;

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.listitem_evaluate_send, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            final Map<String, String> map = list.get(position);
            holder.tvContNoid.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            holder.tvContNoid.setText(map.get("contract_noid"));
            holder.tvWork.setText(ListUtils.join(JsonArryToList.strList(map.get("skill"))));
            holder.tvStartDate.setText(map.get("contract_starttime"));
            ImageLoader imageLoader = new ImageLoader();
            imageLoader.disPlay(holder.imgvHead, map.get("head"));
            holder.tvNickname.setText(map.get("nickname"));
            String level = map.get("level");
            if (TextUtils.equals(level, "GOOD")) {
                holder.tvGrade.setText("好评");
                holder.tvGrade.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_gread, 0, 0, 0);
                holder.tvGrade.setTextColor(getResources().getColor(R.color.clr_evaluate_high));
            } else if (TextUtils.equals(level, "NORMAL")) {
                holder.tvGrade.setText("中评");
                holder.tvGrade.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_medium, 0, 0, 0);
                holder.tvGrade.setTextColor(getResources().getColor(R.color.clr_evaluate_medium));
            } else if (TextUtils.equals(level, "BAD")) {
                holder.tvGrade.setText("差评");
                holder.tvGrade.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_bad, 0, 0, 0);
                holder.tvGrade.setTextColor(getResources().getColor(R.color.clr_evaluate_low));
            }
            holder.tvContent.setText(map.get("content"));
            holder.tvSendDate.setText(map.get("create_time"));
            photos = JsonArryToList.strList(map.get("photos"));
            holder.gridView.setVisibility(View.VISIBLE);
            myGridAdapter = new MyGridAdapter(photos);
            holder.gridView.setAdapter(myGridAdapter);

            //最后评价时间
            String end_time = DateUtils.timeslashData(map.get("end_time"));
            long days = DateTool.getDays(end_time, DateTool.getStringDateShort());
            holder.vSendDate.setVisibility(View.VISIBLE);


//            if (TextUtils.equals(partner, "PUBLISH")) {
//                holder.linlayReceived.setVisibility(View.GONE);
                holder.linlayReceived2.setVisibility(View.GONE);
//                if (TextUtils.equals(map.get("explain_status"), "1")) {
//                    holder.linlayReply.setVisibility(View.GONE);
//                } else {
//                    if (!TextUtils.isEmpty(map.get("explain_text"))) {
//                        holder.linlayReply.setVisibility(View.VISIBLE);
//                        holder.tvReplyContent.setText(map.get("explain_text"));
//                    }
//                }
//
//                if (days >= 0) {
//                    holder.linlayEdit.setVisibility(View.VISIBLE);
//                } else {
//                    holder.linlayEdit.setVisibility(View.GONE);
//                    if (TextUtils.equals(map.get("explain_status"), "1")) {
//                        holder.vSendDate.setVisibility(View.GONE);
//                    }
//                }
//            } else {
            holder.linlayEdit.setVisibility(View.GONE);
            holder.linlayReply.setVisibility(View.GONE);
            holder.tvSenderName.setVisibility(View.GONE);
            holder.tvSenderName.setText(map.get("nickname"));
//                if (days >= 0) {
//                    if (TextUtils.equals(map.get("explain_status"), "1")) {
//                        holder.linlayReceived.setVisibility(View.VISIBLE);
//                    }
//                }
//                else {
            holder.linlayReceived.setVisibility(View.GONE);
            holder.vSendDate.setVisibility(View.GONE);
            if (TextUtils.equals(map.get("explain_status"), "1")) {
                holder.linlayReceived.setVisibility(View.GONE);
                holder.vSendDate.setVisibility(View.GONE);
            }
//                }
            if (TextUtils.equals(map.get("explain_status"), "2")) {
                if (!TextUtils.isEmpty(map.get("explain_text"))) {
                    holder.linlayReceived2.setVisibility(View.VISIBLE);
                    holder.tvReceivedContent2.setText(map.get("explain_text"));
                }
            }


            if (position == ListUtils.getSize(list) - 1) {
                holder.vLastView.setVisibility(View.VISIBLE);
            } else {
                holder.vLastView.setVisibility(View.GONE);
            }

        }

        @Override
        public int getItemCount() {
            return ListUtils.getSize(list);
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            @ViewInject(R.id.list_e_send_noid)
            TextView tvContNoid;
            @ViewInject(R.id.list_e_send_work)
            TextView tvWork;
            @ViewInject(R.id.list_e_send_start_date)
            TextView tvStartDate;
            @ViewInject(R.id.list_e_send_head)
            CircularImageView imgvHead;
            @ViewInject(R.id.list_e_send_nickname)
            TextView tvNickname;
            @ViewInject(R.id.list_e_send_grade)
            TextView tvGrade;
            @ViewInject(R.id.list_e_send_content)
            TextView tvContent;
            @ViewInject(R.id.list_e_send_date)
            TextView tvSendDate;
            @ViewInject(R.id.list_e_gridview)
            GridView gridView;
            //            发出的评价 修改按钮
            @ViewInject(R.id.list_e_send_edit)
            TextView tvEdit;
            //            发出的评价 回复人的姓名
            @ViewInject(R.id.list_e_send_reply_name)
            TextView tvReplyName;
            //            发出的评价 回复的内容
            @ViewInject(R.id.list_e_send_reply_content)
            TextView tvReplyContent;
            //            发出的评价 修改布局
            @ViewInject(R.id.list_e_send_edit_layout)
            LinearLayout linlayEdit;
            //            发出的评价 回复布局
            @ViewInject(R.id.list_e_send_reply_layout)
            LinearLayout linlayReply;

            //            收到的评价   发送按钮
            @ViewInject(R.id.list_e_received_send)
            TextView tvReceivedSend;
            //             收到的评价   回复前
            @ViewInject(R.id.list_e_received_layout)
            LinearLayout linlayReceived;
            //            收到的评价   回复发送者名字
            @ViewInject(R.id.list_e_sender_name)
            TextView tvSenderName;
            //收到的评价   回复后的内容
            @ViewInject(R.id.list_e_received_contented)
            TextView tvReceivedContent2;
            //            收到的评价   回复前 编辑框
            @ViewInject(R.id.list_e_received_content)
            EditText editReceivedContent;
            //收到的评价   回复后
            @ViewInject(R.id.list_e_received_layouted)
            LinearLayout linlayReceived2;
            @ViewInject(R.id.list_e_lastview)
            View vLastView;
            @ViewInject(R.id.list_e_send_date_line)
            View vSendDate;
            @ViewInject(R.id.list_e_contract)
            LinearLayout linlayContract;

            public ViewHolder(View itemView) {
                super(itemView);
                x.view().inject(this, itemView);
                AutoUtils.autoSize(itemView);
            }
        }
    }

    private class MyGridAdapter extends BaseAdapter {

        private ViewHolder viewHolder;
        private List<String> photos;

        public MyGridAdapter(List<String> photos) {
            this.photos = photos;
        }

        @Override
        public int getCount() {
            return ListUtils.getSize(photos);
        }

        @Override
        public Object getItem(int position) {
            return photos.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(OtherEvaluationAty.this).inflate(R.layout.listitem_new_job_order, parent, false);
                viewHolder = new ViewHolder();
                x.view().inject(viewHolder, convertView);
                convertView.setTag(viewHolder);
                AutoUtils.autoSize(convertView);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            ImageLoader imageLoader = new ImageLoader();
            imageLoader.disPlay(viewHolder.imgvImgs, photos.get(position));
            viewHolder.imgvDelete.setVisibility(View.GONE);
            viewHolder.imgvImgs.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    bundle.putStringArrayList(ImagePagerActivity.EXTRA_IMAGE_URLS, (ArrayList<String>) photos);
                    bundle.putInt(ImagePagerActivity.EXTRA_IMAGE_INDEX, position);
                    startActivity(ImagePagerActivity.class, bundle);
                    overridePendingTransition(0, 0);
                }
            });
            return convertView;
        }


        public class ViewHolder {
            @ViewInject(R.id.list_new_job_imgs)
            ImageView imgvImgs;
            @ViewInject(R.id.list_new_job_delete)
            ImageView imgvDelete;
        }


    }

}
