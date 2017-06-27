package com.toocms.freeman.ui.mine.baseinformation;

import android.content.DialogInterface;
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
import com.toocms.freeman.https.Addr;
import com.toocms.freeman.ui.BaseAty;
import com.zero.autolayout.utils.AutoUtils;

import org.xutils.http.RequestParams;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cn.zero.android.common.util.JSONUtils;
import cn.zero.android.common.util.ListUtils;
import cn.zero.android.common.view.swipetoloadlayout.OnRefreshListener;
import cn.zero.android.common.view.swipetoloadlayout.view.SwipeToLoadRecyclerView;

public class WorkAreaAty extends BaseAty implements OnRefreshListener {

    @ViewInject(R.id.work_area_swipe)
    private SwipeToLoadRecyclerView mSwipeToLoadRecyclerView;
    @ViewInject(R.id.work_area_all)
    private TextView tvAll;
    @ViewInject(R.id.work_area_sel_content)
    private LinearLayout linlaySel;
    @ViewInject(R.id.work_area_empty)
    TextView tvEmpty;
    WorkAreaAdapter mWorkAreaAdapter;
    /**
     * 已添加列表[territoryListing]
     *
     * @param noid 用户编号
     */
    /**
     * 工作地域 - 删除[territoryRemove]
     *
     * @param noid         用户编号
     * @param territory_id 地域id
     */
    private String territory_id;
    private String noid;
    private Addr addr;
    private ArrayList<Map<String, String>> list;
    private boolean isAll = false;
    private List<Map<String, String>> selList = new ArrayList<>();
    ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActionBar.setTitle("工作地域");
        mSwipeToLoadRecyclerView.setOnRefreshListener(this);
        mSwipeToLoadRecyclerView.getRecyclerView().setLayoutManager(new LinearLayoutManager(this));
        if (TextUtils.equals(getIntent().getStringExtra("flag"), "sel_area")) {
            linlaySel.setVisibility(View.VISIBLE);
        } else
            linlaySel.setVisibility(View.GONE);
//        mSwipeToLoadRecyclerView.setOnItemClickListener(new OnItemClickListener() {
//            @Override
//            public void onItemClick(View view, int i) {
//                mWorkAreaAdapter.setSelect(i);
//            }
//        });
//        mSwipeToLoadRecyclerView.getRecyclerView().addItemDecoration(
//                new RecycleViewDivider(this, LinearLayoutManager.VERTICAL,
//                        AutoUtils.getPercentHeightSize(23), getResources().getColor(R.color.clr_bg)));

    }


    @Override
    protected void onResume() {
        super.onResume();
        showProgressContent();
        noid = application.getUserInfo().get("noid");
        addr.territoryListing(noid, this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_add:
                //点击 添加 菜单按钮 监听
                startActivity(AddWorkAreaAty.class, null);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.aty_work_area;
    }

    @Override
    protected void initialized() {
        addr = new Addr();
    }

    @Override
    protected void requestData() {

    }

    @Override
    public void onComplete(RequestParams params, String result) {
        if (params.getUri().contains("Addr/territoryListing")) {
            list = JSONUtils.parseDataToMapList(result);
            updateUI();
        } else if (params.getUri().contains("Addr/territoryRemove")) {
            showToast(JSONUtils.parseKeyAndValueToMap(result).get("message"));
            addr.territoryListing(noid, this);
        }
        super.onComplete(params, result);
        mSwipeToLoadRecyclerView.stopRefreshing();
    }

    @Override
    public void onRefresh() {
        noid = application.getUserInfo().get("noid");
        addr.territoryListing(noid, this);
    }

    @Event({R.id.work_area_all, R.id.work_area_sure})
    private void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.work_area_all:
                isAll = !isAll;
                if (isAll) {
                    tvAll.setCompoundDrawablesWithIntrinsicBounds(R.drawable.btn_select, 0, 0, 0);
                    for (int i = 0; i < ListUtils.getSize(list); i++) {
                        mWorkAreaAdapter.isSelect[i] = true;
                    }

                } else {
                    tvAll.setCompoundDrawablesWithIntrinsicBounds(R.drawable.btn_unselect, 0, 0, 0);
                    for (int i = 0; i < ListUtils.getSize(list); i++) {
                        mWorkAreaAdapter.isSelect[i] = false;
                    }

                }
                mWorkAreaAdapter.notifyDataSetChanged();

                break;
            case R.id.work_area_sure:
                for (int i = 0; i < ListUtils.getSize(list); i++) {
                    if (mWorkAreaAdapter.isSelect[i]) {
                        Map<String, String> map = list.get(i);
                        selList.add(map);
                    }
                }
                if (ListUtils.isEmpty(selList)) {
                    showToast("请选择工作地域");
                    return;
                }
                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                bundle.putSerializable("sel_area", (Serializable) selList);
                intent.putExtras(bundle);
                setResult(RESULT_OK, intent);
                finish();
                break;
        }
    }

    //更新UI
    private void updateUI() {
//        if (mWorkAreaAdapter == null) {
        mWorkAreaAdapter = new WorkAreaAdapter();
        mSwipeToLoadRecyclerView.setAdapter(mWorkAreaAdapter);
//        } else
//            mWorkAreaAdapter.notifyDataSetChanged();
        if (ListUtils.isEmpty(list)) {
            tvEmpty.setVisibility(View.VISIBLE);
        } else {
            tvEmpty.setVisibility(View.GONE);
        }
    }


    private class WorkAreaAdapter extends RecyclerView.Adapter<WorkAreaAdapter.ViewHolder> {
        Boolean[] isSelect = new Boolean[ListUtils.getSize(list)];
        private int anint = 0;

        public WorkAreaAdapter() {
            for (int i = 0; i < ListUtils.getSize(list); i++) {
                isSelect[i] = false;
            }
            if (TextUtils.equals(getIntent().getStringExtra("flag"), "sel_area")) {
                ArrayList<Map<String, String>> sel_area = (ArrayList<Map<String, String>>) getIntent().getSerializableExtra("sel_area");
                int bnInt = 1;
                if (!ListUtils.isEmpty(sel_area))
                    for (int i = 0; i < ListUtils.getSize(list); i++) {
                        for (int j = 0; j < ListUtils.getSize(sel_area); j++) {
                            if (TextUtils.equals(list.get(i).get("territory_id"), sel_area.get(j).get("territory_id"))) {
                                isSelect[i] = true;
                            }
                        }

                    }
                for (int i = 0; i < ListUtils.getSize(list); i++) {
                    if (!isSelect[i]) {
                        bnInt = 1;
                        break;
                    } else {
                        bnInt = 0;
                    }
                }

                if (bnInt == 0) {
                    tvAll.setCompoundDrawablesWithIntrinsicBounds(R.drawable.btn_select, 0, 0, 0);
                    isAll = true;
                } else {
                    tvAll.setCompoundDrawablesWithIntrinsicBounds(R.drawable.btn_unselect, 0, 0, 0);
                    isAll = false;
                }

            }
        }

        public void setSelect(int position) {
            isSelect[position] = !isSelect[position];
            for (int i = 0; i < ListUtils.getSize(list); i++) {
                if (!isSelect[i]) {
                    anint = 1;
                    break;
                } else {
                    anint = 0;
                }
            }
            if (anint == 0) {
                tvAll.setCompoundDrawablesWithIntrinsicBounds(R.drawable.btn_select, 0, 0, 0);
                isAll = true;
            } else {
                tvAll.setCompoundDrawablesWithIntrinsicBounds(R.drawable.btn_unselect, 0, 0, 0);
                isAll = false;
            }
            notifyDataSetChanged();
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.listitem_work_area, parent, false);
            ViewHolder viewHolder = new ViewHolder(view);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, final int position) {
            final Map<String, String> map = list.get(position);
            if (TextUtils.equals(getIntent().getStringExtra("flag"), "sel_area")) {
                if (isSelect[position])
                    holder.tvArea.setCompoundDrawablesWithIntrinsicBounds(R.drawable.btn_select, 0, 0, 0);
                else
                    holder.tvArea.setCompoundDrawablesWithIntrinsicBounds(R.drawable.btn_unselect, 0, 0, 0);

                holder.linlayArea.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        setSelect(position);
                    }
                });
            }
            if (!TextUtils.isEmpty(map.get("city_name")) && !TextUtils.isEmpty(map.get("area_name"))) {
                holder.tvArea.setText(map.get("province_name") + "," + map.get("city_name") + "," + map.get("area_name"));
            } else if (TextUtils.isEmpty(map.get("city_name"))) {
                holder.tvArea.setText(map.get("province_name") + "全境");
            } else if (TextUtils.isEmpty(map.get("area_name"))) {
                holder.tvArea.setText(map.get("province_name") + "," + map.get("city_name")+ "全境");
            }


            holder.tvDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showDialog("提示", "是否删除该工作地域", "删除", "取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            territory_id = map.get("territory_id");
                            addr.territoryRemove(noid, territory_id, WorkAreaAty.this);
                        }
                    }, null);
                }
            });

        }

        @Override
        public int getItemCount() {
            return ListUtils.getSize(list);
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            @ViewInject(R.id.list_area)
            TextView tvArea;
            @ViewInject(R.id.list_area_del)
            TextView tvDelete;
            @ViewInject(R.id.list_work_area_click)
            LinearLayout linlayArea;

            public ViewHolder(View itemView) {
                super(itemView);
                x.view().inject(this, itemView);
                AutoUtils.autoSize(itemView);
            }
        }
    }
}
