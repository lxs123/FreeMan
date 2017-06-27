package com.toocms.freeman.ui.index;

import android.content.Intent;
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
import com.toocms.freeman.https.Sys;
import com.toocms.freeman.ui.BaseAty;
import com.toocms.freeman.ui.util.WorkOrder;
import com.zero.autolayout.utils.AutoUtils;

import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.zero.android.common.util.JSONUtils;
import cn.zero.android.common.util.ListUtils;
import cn.zero.android.common.view.swipetoloadlayout.view.SwipeToLoadRecyclerView;
import cn.zero.android.common.view.swipetoloadlayout.view.listener.OnItemClickListener;

/**
 * 技能3
 */
public class SkillThirdDetailAty extends BaseAty implements OnItemClickListener {

    @ViewInject(R.id.skill_swipe)
    private SwipeToLoadRecyclerView skillSwipe;
    private SkillItemAdapter mSkillItemAdapter;

    //数据源
    private List<Map<String, String>> skillItemData = new ArrayList<>();
    private ArrayList<String> selectedList = new ArrayList<>();
    private ArrayList<String> nameList = new ArrayList<>();
    //接口相关
    public final String SKILL_NAME = "name";
    private Sys sys;
    private String skill_id1;
    private String skill_id2;
    Boolean[] isSel;
    private String position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //得到上一页传来的信息
        Intent intent = getIntent();
        String skillname = intent.getStringExtra("name");
        position = intent.getStringExtra("position");
        mActionBar.setTitle(skillname);
        skillSwipe.setOnItemClickListener(this);
        //初始化swipe
        skillSwipe.getRecyclerView().setLayoutManager(new LinearLayoutManager(this));

    }


    @Override
    protected int getLayoutResId() {
        return R.layout.aty_skill;
    }

    @Override
    protected void initialized() {
        sys = new Sys();
    }

    @Override
    protected void requestData() {
        showProgressContent();
        skill_id1 = getIntent().getStringExtra("skill_id1");
        skill_id2 = getIntent().getStringExtra("skill_id2");
        sys.getSkillList(skill_id2, "2", this);
    }

    @Override
    public void onComplete(RequestParams params, String result) {
        if (params.getUri().contains("Sys/getSkillList")) {
            skillItemData = JSONUtils.parseDataToMapList(result);
            updateUI();
        }
        super.onComplete(params, result);
    }

    //创建确定按钮相关
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_sure, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_sure:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void updateUI() {

        if (mSkillItemAdapter == null) {
            mSkillItemAdapter = new SkillItemAdapter();
            skillSwipe.setAdapter(mSkillItemAdapter);
        } else {
            mSkillItemAdapter.notifyDataSetChanged();

        }

    }

    @Override
    public void onItemClick(View view, int i) {
        mSkillItemAdapter.setSelect(i);
    }

    //========================Adapter=========================
    public class SkillItemAdapter extends RecyclerView.Adapter<SkillItemAdapter.ViewHolder> {

        public void setSelect(int position) {
            List<Map<String, String>> workOrder3 = WorkOrder.getInstance().getOrder3(skill_id1, skill_id2);
            Map<String, String> map = new HashMap<>();
            map.put(skillItemData.get(position).get("skill_id"), skillItemData.get(position).get("name"));
            if (ListUtils.isEmpty(workOrder3)) {
                map.put(skillItemData.get(position).get("skill_id"), skillItemData.get(position).get("name"));
                workOrder3 = new ArrayList<>();
                workOrder3.add(map);
                WorkOrder.getInstance().addOrder3(skill_id1, skill_id2, workOrder3);
            } else {
                if (workOrder3.contains(map)) {
                    workOrder3.remove(map);
                } else {
                    workOrder3.add(map);
                }
            }
            notifyItemChanged(position);
        }

        @Override
        public SkillItemAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(SkillThirdDetailAty.this).inflate(R.layout.listitem_detail_skill, parent, false);
            ViewHolder viewHolder = new ViewHolder(view);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(SkillItemAdapter.ViewHolder holder, int position) {
            holder.skillDetailTv.setText(skillItemData.get(position).get(SKILL_NAME));
            List<Map<String, String>> workOrder3 = WorkOrder.getInstance().getOrder3(skill_id1, skill_id2);
            Map<String, String> map = new HashMap<>();
            map.put(skillItemData.get(position).get("skill_id"), skillItemData.get(position).get("name"));
            if (workOrder3 == null) return;
            if (workOrder3.contains(map)) {
                holder.skillDetailTv.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.btn_choose, 0);
            } else {
                holder.skillDetailTv.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            }
        }

        @Override
        public int getItemCount() {
            return ListUtils.getSize(skillItemData);
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            @ViewInject(R.id.skill_detail_tv)
            TextView skillDetailTv;

            public ViewHolder(View itemView) {
                super(itemView);
                x.view().inject(this, itemView);
                AutoUtils.auto(itemView);
            }
        }
    }
}
