package com.toocms.freeman.ui.mine;

import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.toocms.frame.config.Settings;
import com.toocms.frame.image.ImageLoader;
import com.toocms.frame.tool.DateTool;
import com.toocms.freeman.R;
import com.toocms.freeman.config.JsonArryToList;
import com.toocms.freeman.https.Contract;
import com.toocms.freeman.https.Sys;
import com.toocms.freeman.ui.BaseAty;
import com.toocms.freeman.ui.contract.ContDetailAty;
import com.toocms.freeman.ui.contract.EvaluateAty;
import com.toocms.freeman.ui.index.SkillAty;
import com.toocms.freeman.ui.util.DateUtils;
import com.toocms.freeman.ui.util.WorkOrder;
import com.toocms.freeman.ui.view.ImagePagerActivity;
import com.zero.autolayout.utils.AutoUtils;

import org.xutils.http.RequestParams;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import cn.zero.android.common.util.JSONUtils;
import cn.zero.android.common.util.ListUtils;
import cn.zero.android.common.util.MapUtils;
import cn.zero.android.common.view.shapeimageview.CircularImageView;
import cn.zero.android.common.view.swipetoloadlayout.OnLoadMoreListener;
import cn.zero.android.common.view.swipetoloadlayout.OnRefreshListener;
import cn.zero.android.common.view.swipetoloadlayout.view.SwipeToLoadRecyclerView;

/**
 * Created by admin on 2017/5/10.
 */

public class MyEvaluateAty extends BaseAty implements OnRefreshListener, OnLoadMoreListener {
    @ViewInject(R.id.my_contract_cap)
    TextView tvSend;
    @ViewInject(R.id.my_contract_lab)
    TextView tvReceive;
    @ViewInject(R.id.take_title_view)
    View vTitleView;
    @ViewInject(R.id.my_evaluate)
    SwipeToLoadRecyclerView swipeToLoadRecyclerView;
    @ViewInject(R.id.my_contract_drawer)
    private DrawerLayout drawerLayout;
    @ViewInject(R.id.my_jo_screen)
    TextView tvScreen;
    @ViewInject(R.id.my_cont_noid)
    EditText editContNoid;
    @ViewInject(R.id.my_cont_keywords)
    EditText editKeywords;
    @ViewInject(R.id.my_cont_work)
    TextView tvWork;
    @ViewInject(R.id.my_contract_screen_lay)
    LinearLayout linlayScreenLay;
    @ViewInject(R.id.my_contract_lay)
    ScrollView scrollview;
    @ViewInject(R.id.my_evaluate_empty)
    TextView tvEmpty;
    private int position;
    /**
     * 评价列表[assessList]
     *
     * @param noid     用户编号
     * @param partner  角色类型
     * PUBLISH 我发出的
     * ACCEPT 已收到的
     * @param page     分页，默认为1
     * @param skill_id 技能id数组
     */

    private String partner = "PUBLISH";
    private String page;
    private String skill_id;
    private Contract contract;
    private ArrayList<Map<String, String>> list;
    private MyAdapter myAdapter;
    private int p = 1;
    private List<String> photos;
    /**
     * 回复评价[replyAssess]
     *
     * @param contract_noid 合同编号
     * @param assess_id     评价id
     * @param noid          用户编号
     * @param content       回复内容，80字
     */
    private String noid;
    private String contract_noid;
    private String keywords;
    private ArrayList<String> skillList;
    private List<Map<String, String>> skillItemData = new ArrayList<>();
    private Sys sys;

    @Override
    protected int getLayoutResId() {
        return R.layout.aty_my_evaluate;
    }

    @Override
    protected void initialized() {
        contract = new Contract();
        sys = new Sys();
    }

    @Override
    protected void requestData() {
        sys.getSkillList("0", "0", this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActionBar.hide();
        WindowManager wm = this.getWindowManager();//获取屏幕宽高
        ViewGroup.LayoutParams layoutParams = linlayScreenLay.getLayoutParams();
        layoutParams.height = wm.getDefaultDisplay().getHeight();
        layoutParams.width = wm.getDefaultDisplay().getWidth() / 4 * 3;
        linlayScreenLay.setLayoutParams(layoutParams);
        WorkOrder.getInstance().clear();
        tvSend.setSelected(true);
        swipeToLoadRecyclerView.setOnRefreshListener(this);
        swipeToLoadRecyclerView.setOnLoadMoreListener(this);
        swipeToLoadRecyclerView.getRecyclerView().setLayoutManager(new LinearLayoutManager(this));
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        drawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {

            }

            @Override
            public void onDrawerOpened(View drawerView) {
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });
        noid = application.getUserInfo().get("noid");
        page = "1";
        showProgressContent();
    }

    @Override
    protected void onResume() {
        super.onResume();
        contract.assessList(noid, partner, page, skill_id, contract_noid, keywords, this);
        initSkill();
    }

    private void initSkill() {
        List<String> list = new ArrayList<>();
        skillList = new ArrayList<>();
        Map<String, Map<String, List<Map<String, String>>>> workOrder1 = WorkOrder.getInstance().getOrder1();
        if (MapUtils.isEmpty(workOrder1)) {
            tvWork.setText("");
        } else {
            if (ListUtils.isEmpty(skillItemData)) return;
            for (int j = 0; j < ListUtils.getSize(skillItemData); j++) {
                Map<String, String> map = skillItemData.get(j);
                if (workOrder1.containsKey(map.get("skill_id"))) {
                    Map<String, List<Map<String, String>>> workOrder2 = workOrder1.get(map.get("skill_id"));
                    Iterator iterator = workOrder2.entrySet().iterator();
                    while (iterator.hasNext()) {
                        Map.Entry entry = (Map.Entry) iterator.next();
                        for (int i = 0; i < ListUtils.getSize(workOrder2.get(entry.getKey())); i++) {
//                            if (ListUtils.getSize(list) >= 3) break;
                            Iterator iterator2 = workOrder2.get(entry.getKey()).get(i).entrySet().iterator();
                            while (iterator2.hasNext()) {
                                Map.Entry entry2 = (Map.Entry) iterator2.next();
                                list.add(entry2.getValue().toString());
                                skillList.add(entry2.getKey().toString());
                            }
                        }
                    }
                }
            }
            if (ListUtils.isEmpty(list)) tvWork.setText("");
            else tvWork.setText(ListUtils.join(list));

        }
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
        } else if (params.getUri().contains("Contract/replyAssess")) {
            showToast(JSONUtils.parseKeyAndValueToMap(result).get("message"));
            contract.assessList(noid, partner, page, skill_id, contract_noid, keywords, this);
        } else if (params.getUri().contains("Sys/getSkillList")) {
            skillItemData = JSONUtils.parseDataToMapList(result);
        }
        super.onComplete(params, result);
        swipeToLoadRecyclerView.stopRefreshing();
        swipeToLoadRecyclerView.stopLoadMore();
    }

    @Event({R.id.my_contract_cap, R.id.my_contract_lab, R.id.my_jo_sure, R.id.my_evaluate_clear
            , R.id.my_jo_back, R.id.my_jo_screen, R.id.my_cont_work_click})
    private void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.my_contract_cap:
                position = 1;
                tvSend.setSelected(true);
                tvReceive.setSelected(false);
                partner = "PUBLISH";
                swipeToLoadRecyclerView.startRefreshing();
                break;
            case R.id.my_contract_lab:
                position = 2;
                tvSend.setSelected(false);
                tvReceive.setSelected(true);
                partner = "ACCEPT";
                swipeToLoadRecyclerView.startRefreshing();
                break;
            case R.id.my_jo_back:
                finish();
                break;
            case R.id.my_jo_screen:
                if (!drawerLayout.isDrawerOpen(scrollview)) {
                    drawerLayout.openDrawer(scrollview);
                }
                break;
            case R.id.my_cont_work_click:
                startActivity(SkillAty.class, null);
                break;
            case R.id.my_jo_sure:
                contract_noid = editContNoid.getText().toString().trim();
                keywords = editKeywords.getText().toString().trim();
                skill_id = ListUtils.join(skillList);
                swipeToLoadRecyclerView.startRefreshing();
                drawerLayout.closeDrawer(scrollview);
                break;
            case R.id.my_evaluate_clear:
                editContNoid.setText("");
                editKeywords.setText("");
                WorkOrder.getInstance().clear();
                skillList.clear();
                contract_noid = "";
                keywords = "";
                skill_id = "";
                tvWork.setText("");
                swipeToLoadRecyclerView.startRefreshing();
                drawerLayout.closeDrawer(scrollview);
                break;
        }
        startTranslate(vTitleView, (Settings.displayWidth / 2) * (position - 1));
    }

    @Override
    public void onRefresh() {
        contract.assessList(noid, partner, page, skill_id, contract_noid, keywords, this);
    }

    @Override
    public void onLoadMore() {
        if (myAdapter.getItemCount() % Integer.parseInt(getResources().getString(R.string.load_num))
                < Integer.parseInt(getResources().getString(R.string.load_min_num)) &&
                myAdapter.getItemCount() % Integer.parseInt(getResources().getString(R.string.load_num))
                        > 0) {
            swipeToLoadRecyclerView.stopLoadMore();
            return;
        }
        p++;
        page = String.valueOf(p);
        contract.assessList(noid, partner, page, skill_id, contract_noid, keywords, this);
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


            if (TextUtils.equals(partner, "PUBLISH")) {
                holder.linlayReceived.setVisibility(View.GONE);
                holder.linlayReceived2.setVisibility(View.GONE);
                holder.linlayReply.setVisibility(View.GONE);
                if (TextUtils.equals(map.get("explain_status"), "1")) {
                    holder.linlayReply.setVisibility(View.GONE);
                } else {
                    if (!TextUtils.isEmpty(map.get("explain_text"))) {
                        holder.linlayReply.setVisibility(View.VISIBLE);
                        holder.tvReplyContent.setText(map.get("explain_text"));
                        holder.tvReplyName.setText(map.get("explain_nickname"));
                    }
                }

                if (days >= 0) {
                    holder.linlayEdit.setVisibility(View.VISIBLE);
                } else {
                    holder.linlayEdit.setVisibility(View.GONE);
                    if (TextUtils.equals(map.get("explain_status"), "1")) {
                        holder.vSendDate.setVisibility(View.GONE);
                    }
                }
            } else {
                holder.linlayEdit.setVisibility(View.GONE);
                holder.linlayReply.setVisibility(View.GONE);
                holder.linlayReceived.setVisibility(View.GONE);
                holder.linlayReceived2.setVisibility(View.GONE);
                holder.tvSenderName.setText(map.get("nickname"));
                if (days >= 0) {
                    if (TextUtils.equals(map.get("explain_status"), "1")) {
                        holder.linlayReceived.setVisibility(View.VISIBLE);
                    }
                } else {
                    if (TextUtils.equals(map.get("explain_status"), "1")) {
                        holder.linlayReceived.setVisibility(View.GONE);
                        holder.vSendDate.setVisibility(View.GONE);
                    }
                }
                if (TextUtils.equals(map.get("explain_status"), "2")) {
                    if (!TextUtils.isEmpty(map.get("explain_text"))) {
                        holder.linlayReceived2.setVisibility(View.VISIBLE);
                        holder.tvReceivedContent2.setText(map.get("explain_text"));
                    }
                }
            }
            holder.tvEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    bundle.putString("contract_noid", map.get("contract_noid"));
                    bundle.putString("flag", "edit");
                    startActivity(EvaluateAty.class, bundle);
                }
            });
            holder.tvReceivedSend.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String content = holder.editReceivedContent.getText().toString();
                    if (TextUtils.isEmpty(content)) {
                        showToast("请输入回复内容");
                        return;
                    }
                    contract.replyAssess(map.get("contract_noid"), map.get("assess_id"), noid, content, MyEvaluateAty.this);
                    holder.linlayReceived.setVisibility(View.GONE);
                }
            });
            if (position == ListUtils.getSize(list) - 1) {
                holder.vLastView.setVisibility(View.VISIBLE);
            } else {
                holder.vLastView.setVisibility(View.GONE);
            }
            holder.linlayContract.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    bundle.putString("contract_noid", map.get("contract_noid"));
                    startActivity(ContDetailAty.class, bundle);
                }
            });
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
                convertView = LayoutInflater.from(MyEvaluateAty.this).inflate(R.layout.listitem_new_job_order, parent, false);
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
