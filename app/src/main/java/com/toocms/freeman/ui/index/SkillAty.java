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
import android.widget.BaseAdapter;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import cn.zero.android.common.util.JSONUtils;
import cn.zero.android.common.util.ListUtils;
import cn.zero.android.common.util.MapUtils;
import cn.zero.android.common.view.linearlistview.LinearListView;
import cn.zero.android.common.view.swipetoloadlayout.view.SwipeToLoadRecyclerView;
import cn.zero.android.common.view.swipetoloadlayout.view.listener.OnItemClickListener;

/**
 * 技能
 */
public class SkillAty extends BaseAty implements OnItemClickListener {
    public final String SKILL_NAME = "name";
    public static final int SKILL = 0x0001;
    @ViewInject(R.id.skill_swipe)
    private SwipeToLoadRecyclerView skillSwipe;
    private SkillItemAdapter mSkillItemAdapter;

    //数据源
    private List<Map<String, String>> skillItemData = new ArrayList<>();
    /**
     * 技能三级列表[getSkillList]
     *
     * @param skill_id 上级ID，顶级为0
     * @param layer    层级数，顶级为0
     */
    private String skill_id;
    private String layer;
    private Sys sys;
    private ArrayList<String> posList;
    public static ArrayList<Map<String, ArrayList<Map<String, ArrayList<String>>>>> list2 = new ArrayList<>();
    private ArrayList<Map<String, ArrayList<String>>> list;
    private ArrayList<Map<String, String>> nameMaps;
    public static ArrayList<Map<String, ArrayList<Map<String, String>>>> nameList = new ArrayList<>();
    public static ArrayList<Map<String, ArrayList<String>>> maps;
    public static ArrayList<Map<String, String>> names;
    Map<String, ArrayList<Map<String, String>>> stringMapMap = new HashMap<>();
    private ArrayList<Map<String, String>> skillSecondData;
    private int tag = 1;
    private SkillSecondAdapter skillSecondAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActionBar.setTitle("技能");

        //初始化swipe
        skillSwipe.getRecyclerView().setLayoutManager(new LinearLayoutManager(this));
        skillSwipe.setOnItemClickListener(this);
        //swiped的item监听
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
//        skill_id = "0";
//        layer = "0";
//        sys.getSkillList(skill_id, layer, this);
        sys.getSkillAllList(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            return;
        }
        switch (requestCode) {
            case SKILL:
//                nameList = data.getStringArrayListExtra("recordList");

//                String position = data.getStringExtra("position");
//                posList = data.getStringArrayListExtra("posList");
//
//                list = (ArrayList<Map<String, ArrayList<String>>>) data.getSerializableExtra("list");
//                nameMaps = (ArrayList<Map<String, String>>) data.getSerializableExtra("name");
//                int anInt = 0;
//                for (int i = 0; i < ListUtils.getSize(list2); i++) {
//                    Map map = list2.get(i);
//                    String key = null;
//                    Iterator iter = map.entrySet().iterator();
//                    while (iter.hasNext()) {
//                        Map.Entry entry = (Map.Entry) iter.next();
//                        key = (String) entry.getKey();
//                        nameList.get(i).put(key, nameMaps);
//                    }
//                    if (TextUtils.equals(key, position + "")) {
//                        map.put(key, list);
//                        anInt = 1;
//                        break;
//                    }
//                }
//                LogUtil.e(anInt + "");
//                LogUtil.e(nameList.toString());
//                if (ListUtils.getSize(list2)!=0){
//                    anInt = 1;
//                    return;
//                }
//                if (anInt != 1) {
//                    Map<String, ArrayList<Map<String, ArrayList<String>>>> stringArrayListMap = new HashMap<>();
//                    stringArrayListMap.put(position, list);
//                    list2.add(stringArrayListMap);
//
//                    stringMapMap.put(position, nameMaps);
//                    nameList.add(stringMapMap);
//                    LogUtil.e(nameList.toString());
//                }
//                mSkillItemAdapter.SetResult(Integer.parseInt(data.getStringExtra("position")));
                break;
        }
    }

    @Override
    public void onComplete(RequestParams params, String result) {
        if (params.getUri().contains("Sys/getSkillAllList")) {
            skillItemData = JSONUtils.parseDataToMapList(result);
////            if (tag == 1) {
//            if (skill_id.equals("0")) {
//                skillItemData = JSONUtils.parseDataToMapList(result);
////                if (params.getUri().contains("layer=1"))
//            } else {
//                skillSecondData = JSONUtils.parseDataToMapList(result);
////                skillSecondAdapter.notifyDataSetChanged();
//            }
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
        mSkillItemAdapter.setSelected(i);
        tag = 2;
    }

    //========================Adapter=========================
    public class SkillItemAdapter extends RecyclerView.Adapter<SkillItemAdapter.ViewHolder> {
        int selected = 10000;
        private boolean[] isSel = new boolean[ListUtils.getSize(skillItemData)];
        private int[] isFirst = new int[ListUtils.getSize(skillItemData)];
        private SkillSecondAdapter[] secondAdapters = new SkillSecondAdapter[ListUtils.getSize(skillItemData)];

        public SkillItemAdapter() {
            for (int i = 0; i < ListUtils.getSize(skillItemData); i++) {
                isSel[i] = false;
                isFirst[i] = 0;
                secondAdapters[i] = null;
            }
        }

        public void setSelected(int position) {
            isSel[position] = !isSel[position];
            if (isSel[position]) {

            } else {
                isFirst[position] = 0;
            }

            notifyDataSetChanged();
        }

        @Override
        public SkillItemAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(SkillAty.this).inflate(R.layout.listitem_skill, parent, false);
            ViewHolder viewHolder = new ViewHolder(view);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(final SkillItemAdapter.ViewHolder holder, final int position) {
            final Map<String, String> map = skillItemData.get(position);
            holder.skillLeftTv.setText(map.get(SKILL_NAME));
//            holder.lvSecond.setVisibility(View.GONE);
//            holder.linlaySkill.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Bundle bundle = new Bundle();
//
//                    bundle.putString("skill_id1", map.get("skill_id"));
//                    bundle.putString("name", map.get(SKILL_NAME));
//
////                    startActivityForResult(SkillSecondAty.class, bundle, SKILL);
//                }
//            });

//            if (isSel[position]) {
            holder.skillLeftTv.setTextSize(AutoUtils.getPercentHeightSize(34));
            skillSecondData = new ArrayList<>();
            skillSecondData = JSONUtils.parseKeyAndValueToMapList(map.get("_child"));

//            showProgressDialog();
//            sys.getSkillList(skill_id1, "1", SkillAty.this);
//            holder.lvSecond.setVisibility(View.VISIBLE);
//            if (skillSecondAdapter == null) {

            holder.lvSecond.setVisibility(View.GONE);
//            } else {
//                skillSecondAdapter.notifyDataSetChanged();
//            }
            if (isSel[position]) {
                holder.tvRight.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.btn_up, 0);
                ArrayList<Integer> numList = new ArrayList<>();
                if (!ListUtils.isEmpty(skillSecondData)) {
                    for (int i = 0; i < ListUtils.getSize(skillSecondData); i++) {
                        if (secondAdapters[position] != null)
                            if (secondAdapters[position].isSel[i]) {
                                numList.add(i);
                            }
                    }
                }
                secondAdapters[position] = new SkillSecondAdapter(skillSecondData, map.get("skill_id"));
                holder.lvSecond.setAdapter(secondAdapters[position]);
                holder.lvSecond.setVisibility(View.VISIBLE);
                isFirst[position] = 1;
                holder.vDivid.setVisibility(View.VISIBLE);
                if (!ListUtils.isEmpty(numList)) {
                    for (int i = 0; i < ListUtils.getSize(numList); i++) {
                        secondAdapters[position].isSel[numList.get(i)] = true;
                        secondAdapters[position].notifyDataSetChanged();
                    }
                }
            } else {
                holder.tvRight.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.btn_enter, 0);
                isFirst[position] = 0;
                holder.vDivid.setVisibility(View.GONE);
                holder.lvSecond.setVisibility(View.GONE);
            }

            List<String> list = new ArrayList<>();
            Map<String, Map<String, List<Map<String, String>>>> workOrder1 = WorkOrder.getInstance().getOrder1();
            if (MapUtils.isEmpty(workOrder1)) {
                holder.tvRight.setText("");
            } else {
                if (workOrder1.containsKey(map.get("skill_id"))) {
                    Map<String, List<Map<String, String>>> workOrder2 = workOrder1.get(map.get("skill_id"));
                    Iterator iterator = workOrder2.entrySet().iterator();
                    while (iterator.hasNext()) {
                        Map.Entry entry = (Map.Entry) iterator.next();
                        for (int i = 0; i < ListUtils.getSize(workOrder2.get(entry.getKey())); i++) {
                            if (ListUtils.getSize(list) >= 3) break;
                            Iterator iterator2 = workOrder2.get(entry.getKey()).get(i).entrySet().iterator();
                            while (iterator2.hasNext()) {
                                Map.Entry entry2 = (Map.Entry) iterator2.next();
                                list.add(entry2.getValue().toString());
                            }
                        }
                    }
                    holder.tvRight.setText(ListUtils.join(list));
                } else {
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
            @ViewInject(R.id.list_skill_second_list)
            LinearListView lvSecond;
            @ViewInject(R.id.list_skill_divid)
            View vDivid;

            public ViewHolder(View itemView) {
                super(itemView);
                x.view().inject(this, itemView);
                AutoUtils.auto(itemView);
            }
        }
    }

    //========================Adapter=========================
    public class SkillSecondAdapter extends BaseAdapter {
        int selected = 10000;
        private ViewHolder viewHolder;
        private List<Map<String, String>> list;
        public boolean[] isSel = null;
        String skill_id1;

        public SkillSecondAdapter(List<Map<String, String>> list, String skill_id) {
            isSel = new boolean[ListUtils.getSize(list)];
            this.list = list;
            this.skill_id1 = skill_id;
            for (int i = 0; i < ListUtils.getSize(list); i++) {
                isSel[i] = false;
            }

        }

        @Override
        public int getCount() {
            return ListUtils.getSize(list);
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(SkillAty.this).inflate(R.layout.listitem_skill, parent, false);
                viewHolder = new ViewHolder();
                x.view().inject(viewHolder, convertView);
                AutoUtils.autoSize(convertView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            final Map<String, String> map = list.get(position);

            viewHolder.skillLeftTv.setPadding(AutoUtils.getPercentWidthSize(50), 0, 0, 0);
            viewHolder.skillLeftTv.setText(map.get(SKILL_NAME));
            viewHolder.lvThreeView.setVisibility(View.GONE);
            viewHolder.linlaySkill.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    isSel[position] = !isSel[position];
                    notifyDataSetChanged();
//                    Bundle bundle = new Bundle();
//                    bundle.putString("skill_id1", skill_id1);
//                    bundle.putString("skill_id2", map.get("skill_id"));
//                    bundle.putString("name", map.get(SKILL_NAME));

//                    startActivity(SkillThirdDetailAty.class, bundle);
                }
            });
            if (isSel[position]) {
                viewHolder.tvRight.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.btn_up, 0);
                ArrayList<Map<String, String>> arrayList = JSONUtils.parseKeyAndValueToMapList(map.get("_child"));
                viewHolder.lvThreeView.setVisibility(View.VISIBLE);
                viewHolder.lvThreeView.setAdapter(new SkillThreeAdapter(arrayList, skill_id1, map.get("skill_id"), position));
                viewHolder.vDivid.setVisibility(View.VISIBLE);
            } else {
                viewHolder.tvRight.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.btn_enter, 0);
                viewHolder.lvThreeView.setVisibility(View.GONE);
            }
            if (position == ListUtils.getSize(list) - 1 && !isSel[position]) {
                viewHolder.vDivid.setVisibility(View.GONE);
            } else {
                viewHolder.vDivid.setVisibility(View.VISIBLE);
            }

            List<String> listName = new ArrayList<>();
            Map<String, List<Map<String, String>>> workOrder2 = WorkOrder.getInstance().getOrder2(skill_id1);
            if (MapUtils.isEmpty(workOrder2)) {
                viewHolder.tvRight.setText("");
            } else {
                if (workOrder2.containsKey(map.get("skill_id"))) {
                    List<Map<String, String>> workOrder3 = workOrder2.get(map.get("skill_id"));
                    for (int i = 0; i < ListUtils.getSize(workOrder3); i++) {
                        if (ListUtils.getSize(listName) >= 3) break;
                        Iterator iterator = workOrder3.get(i).entrySet().iterator();
                        while (iterator.hasNext()) {
                            Map.Entry entry = (Map.Entry) iterator.next();
                            listName.add(entry.getValue().toString());
                        }
                    }
                    viewHolder.tvRight.setText(ListUtils.join(listName));
                } else {
                    viewHolder.tvRight.setText("");
                }
            }
            return convertView;
        }

        public class ViewHolder {
            @ViewInject(R.id.skill_left_tv)
            TextView skillLeftTv;
            @ViewInject(R.id.skill_right_tv)
            TextView tvRight;
            @ViewInject(R.id.list_skill_click)
            LinearLayout linlaySkill;
            @ViewInject(R.id.list_skill_second_list)
            LinearListView lvThreeView;
            @ViewInject(R.id.list_skill_divid)
            View vDivid;


//            public ViewHolder(View itemView) {
//                super(itemView);
//                x.view().inject(this, itemView);
//                AutoUtils.auto(itemView);
//            }
        }
    }

    public class SkillThreeAdapter extends BaseAdapter {

        private ViewHolder viewHolder;
        private List<Map<String, String>> list;
        private String skill_id1;
        private String skill_id2;
        private int anInt;

        public SkillThreeAdapter(List<Map<String, String>> list, String skill_id1, String skill_id2, int anInt) {
            this.list = list;
            this.skill_id1 = skill_id1;
            this.skill_id2 = skill_id2;
            this.anInt = anInt;
        }

        public void setSelect(int position) {
            List<Map<String, String>> workOrder3 = WorkOrder.getInstance().getOrder3(skill_id1, skill_id2);
            Map<String, String> map = new HashMap<>();
            map.put(list.get(position).get("skill_id"), list.get(position).get("name"));
            if (ListUtils.isEmpty(workOrder3)) {
                map.put(list.get(position).get("skill_id"), list.get(position).get("name"));
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
//            notifyDataSetChanged();
//            if (mSkillItemAdapter != null) {
            mSkillItemAdapter.notifyDataSetChanged();
//            }

//            skillSecondAdapter.isSel[anInt] = true;
//            skillSecondAdapter.notifyDataSetChanged();
//            if (skillSecondAdapter != null) {
//                skillSecondAdapter.notifyDataSetChanged();
//            }
        }

        @Override
        public int getCount() {
            return ListUtils.getSize(list);
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(SkillAty.this).inflate(R.layout.listitem_detail_skill, parent, false);
                viewHolder = new ViewHolder();
                x.view().inject(viewHolder, convertView);
                AutoUtils.autoSize(convertView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.skillDetailTv.setText(list.get(position).get(SKILL_NAME));
            viewHolder.skillDetailTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setSelect(position);
                }
            });
//            if (position == ListUtils.getSize(list) - 1) {
//                viewHolder.vDivid.setVisibility(View.GONE);
//            } else {
//                viewHolder.vDivid.setVisibility(View.VISIBLE);
//            }
            List<Map<String, String>> workOrder3 = WorkOrder.getInstance().getOrder3(skill_id1, skill_id2);
            Map<String, String> map = new HashMap<>();
            map.put(list.get(position).get("skill_id"), list.get(position).get("name"));
            if (workOrder3 != null) {
                if (workOrder3.contains(map)) {
                    viewHolder.skillDetailTv.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.btn_choose, 0);
                } else {
                    viewHolder.skillDetailTv.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                }
            }

            return convertView;
        }

        public class ViewHolder {
            @ViewInject(R.id.skill_detail_tv)
            TextView skillDetailTv;
            @ViewInject(R.id.list_skill_divid)
            View vDivid;
//            public ViewHolder(View itemView) {
//                super(itemView);
//                x.view().inject(this, itemView);
//                AutoUtils.auto(itemView);
//            }
        }
    }
}
