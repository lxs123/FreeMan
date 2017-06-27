package com.toocms.freeman.ui.index;

import android.content.Intent;
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
import com.toocms.freeman.https.Sys;
import com.toocms.freeman.ui.BaseAty;
import com.toocms.freeman.ui.util.WorkOrder;
import com.zero.autolayout.utils.AutoUtils;

import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import cn.zero.android.common.util.JSONUtils;
import cn.zero.android.common.util.ListUtils;
import cn.zero.android.common.util.MapUtils;
import cn.zero.android.common.view.swipetoloadlayout.view.SwipeToLoadRecyclerView;

/**
 * 技能2
 */
public class SkillSecondAty extends BaseAty {
    public static final int SKILL_SECOND = 0X0001;
    @ViewInject(R.id.skill_swipe)
    private SwipeToLoadRecyclerView skillSwipe;
    private SkillItemAdapter mSkillItemAdapter;

    //数据源
    private List<Map<String, String>> skillItemData = new ArrayList<>();
    // 记录 右边textview 文字
    private ArrayList<String> recordList = new ArrayList<>();
    // 记录 右边文字 对应的交表 ，建议用map
    private ArrayList<String> posList = new ArrayList<>();
    private ArrayList<ArrayList<String>> list = new ArrayList<>();
    private ArrayList<Serializable> parcelables;

    //接口相关
    public final String SKILL_NAME = "name";
    private Sys sys;
    private String skill_id1;
    private ArrayList<String> nameList;
    private ArrayList<String> sel;
    private String position;
    private ArrayList<Map<String, ArrayList<String>>> maps = new ArrayList<>();
    private ArrayList<Map<String, String>> nameMaps = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //得到上一页传来的信息
        Intent intent = getIntent();
        position = intent.getStringExtra("position");
        mActionBar.setTitle(intent.getStringExtra("name"));
        //初始化swipe
        skillSwipe.getRecyclerView().setLayoutManager(new LinearLayoutManager(this));

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mSkillItemAdapter != null) {
            mSkillItemAdapter.notifyDataSetChanged();
        }
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
        sys.getSkillList(skill_id1, "1", this);
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
//                Intent intent = new Intent();
//                if (!ListUtils.isEmpty(recordList)) {
//                    intent.putStringArrayListExtra("recordList", recordList);
//                    intent.putStringArrayListExtra("posList", posList);
//                }
//                if (!ListUtils.isEmpty(sel)) {
//                    intent.putStringArrayListExtra("sel", sel);
//                }
//                intent.putExtra("position", position);
////                parcelables.add((Serializable) list);
//                intent.putExtra("list", (Serializable) maps);
//                intent.putExtra("name", (Serializable) nameMaps);
//                setResult(RESULT_OK, intent);
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            return;
        }
        switch (requestCode) {
            case SKILL_SECOND:
                nameList = data.getStringArrayListExtra("name");
                sel = data.getStringArrayListExtra("path");
                String position = data.getStringExtra("position");
                StringBuilder builder = new StringBuilder(nameList.toString());
                builder.deleteCharAt(0);
                builder.deleteCharAt(builder.length() - 1);
                recordList.add(builder.toString());
                int anInt = 0;
                for (int i = 0; i < ListUtils.getSize(maps); i++) {
                    Map map = maps.get(i);
                    String key = null;
                    Iterator iter = map.entrySet().iterator();
                    while (iter.hasNext()) {
                        Map.Entry entry = (Map.Entry) iter.next();
                        key = (String) entry.getKey();
                    }
                    if (TextUtils.equals(key, position + "")) {
                        map.put(key, sel);
                        nameMaps.get(i).put(key, builder.toString());
                        anInt = 1;
                        break;
                    }
                }
                if (anInt != 1) {
                    Map<String, ArrayList<String>> stringArrayListMap = new HashMap<>();
                    stringArrayListMap.put(data.getStringExtra("position"), sel);
                    maps.add(stringArrayListMap);
                    Map<String, String> nameMap = new HashMap<>();
                    nameMap.put(position, builder.toString());
                    nameMaps.add(nameMap);
                }
                mSkillItemAdapter.SetResult(Integer.parseInt(data.getStringExtra("position")));

                posList.add(data.getStringExtra("position"));
//                list.add(nameList);

                break;
        }
    }

    private void updateUI() {

//        if (!ListUtils.isEmpty(SkillAty.maps)) {
//            maps = new ArrayList<>();
//            if (SkillAty.list2.size() > Integer.parseInt(getIntent().getStringExtra("position"))) {
//                maps = SkillAty.list2.get(Integer.parseInt(getIntent().getStringExtra("position"))).get(getIntent().getStringExtra("position"));
//                nameMaps = new ArrayList<>();
//                nameMaps = SkillAty.nameList.get(Integer.parseInt(getIntent().getStringExtra("position"))).get(getIntent().getStringExtra("position"));
//            }
//        }
        if (mSkillItemAdapter == null) {
            mSkillItemAdapter = new SkillItemAdapter();
            skillSwipe.setAdapter(mSkillItemAdapter);
        } else
            mSkillItemAdapter.notifyDataSetChanged();
//        if (!ListUtils.isEmpty(maps)) {

//        }
    }


    //========================Adapter=========================
    public class SkillItemAdapter extends RecyclerView.Adapter<SkillItemAdapter.ViewHolder> {
        int selected = 10000;


        @Override
        public SkillItemAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(SkillSecondAty.this).inflate(R.layout.listitem_skill, parent, false);
            ViewHolder viewHolder = new ViewHolder(view);
            return viewHolder;
        }

        public void SetResult(int position) {
            selected = position;
            notifyDataSetChanged();
        }

        @Override
        public void onBindViewHolder(SkillItemAdapter.ViewHolder holder, final int position) {
            final Map<String, String> map = skillItemData.get(position);
            holder.skillLeftTv.setText(map.get(SKILL_NAME));
            holder.linlaySkill.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    bundle.putString("skill_id1", skill_id1);
                    bundle.putString("skill_id2", map.get("skill_id"));
                    bundle.putString("name", map.get(SKILL_NAME));
                    startActivityForResult(SkillThirdDetailAty.class, bundle, SKILL_SECOND);
                }
            });
            List<String> list = new ArrayList<>();
            Map<String, List<Map<String, String>>> workOrder2 = WorkOrder.getInstance().getOrder2(skill_id1);
            if (MapUtils.isEmpty(workOrder2)){
                holder.tvRight.setText("");
            }else{
                if (workOrder2.containsKey(map.get("skill_id"))) {
                    List<Map<String, String>> workOrder3 = workOrder2.get(map.get("skill_id"));
                    for (int i = 0; i < ListUtils.getSize(workOrder3); i++) {
                        if (ListUtils.getSize(list)>=3) break;
                        Iterator iterator = workOrder3.get(i).entrySet().iterator();
                        while (iterator.hasNext()) {
                            Map.Entry entry = (Map.Entry) iterator.next();
                            list.add(entry.getValue().toString());
                        }
                    }
                    holder.tvRight.setText(ListUtils.join(list));
                }else {
                    holder.tvRight.setText("");
                }
            }
        }

        @Override
        public int getItemCount() {
            return ListUtils.getSize(skillItemData);
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            @ViewInject(R.id.skill_left_tv)
            TextView skillLeftTv;
            @ViewInject(R.id.skill_right_tv)
            TextView tvRight;
            @ViewInject(R.id.list_skill_click)
            LinearLayout linlaySkill;


            public ViewHolder(View itemView) {
                super(itemView);
                x.view().inject(this, itemView);
                AutoUtils.auto(itemView);
            }
        }
    }
}
