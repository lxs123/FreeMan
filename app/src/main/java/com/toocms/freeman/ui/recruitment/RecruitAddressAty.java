package com.toocms.freeman.ui.recruitment;

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
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;
import java.util.Map;

import cn.zero.android.common.util.JSONUtils;
import cn.zero.android.common.util.ListUtils;
import cn.zero.android.common.view.swipetoloadlayout.OnRefreshListener;
import cn.zero.android.common.view.swipetoloadlayout.view.SwipeToLoadRecyclerView;

/**
 * 招工地址
 * Created by admin on 2017/3/24.
 */

public class RecruitAddressAty extends BaseAty implements OnRefreshListener {
    @ViewInject(R.id.recruit_address_list)
    private SwipeToLoadRecyclerView swipeToLoadRecyclerView;
    private View view;
    @ViewInject(R.id.recruit_address_empty)
    TextView tvEmpty;
    /**
     * 地址 - 已添加列表[listing]
     *
     * @param noid 用户编号
     */


    /**
     * 地址 - 删除[remove]
     *
     * @param noid    用户id
     * @param addr_id 地址id
     */


    /**
     * 地址 - 设置为默认[setDefault]
     *
     * @param noid    用户id
     * @param addr_id 地址id
     */

    /**
     * 地址 - 取消默认[cancelDefault]
     *
     * @param noid    用户id
     * @param addr_id 地址id
     */
    private String noid;
    private Addr addr;
    private ArrayList<Map<String, String>> list;

    @Override
    protected int getLayoutResId() {
        return R.layout.aty_recruit_address;
    }

    @Override
    protected void initialized() {
        addr = new Addr();
    }

    @Override
    protected void requestData() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActionBar.setTitle("招工地址");
        swipeToLoadRecyclerView.setOnRefreshListener(this);
        swipeToLoadRecyclerView.getRecyclerView().setLayoutManager(new LinearLayoutManager(this));

    }

    @Override
    protected void onResume() {
        super.onResume();
        showProgressContent();
        noid = application.getUserInfo().get("noid");
        addr.listing(noid, this);
    }


    @Override
    public void onComplete(RequestParams params, String result) {
        if (params.getUri().contains("Addr/listing")) {
            list = JSONUtils.parseDataToMapList(result);
            updateUI();
        } else if (params.getUri().contains("Addr/remove")) {
            showToast(JSONUtils.parseKeyAndValueToMap(result).get("message"));
            noid = application.getUserInfo().get("noid");
            addr.listing(noid, this);
        } else if (params.getUri().contains("Addr/cancelDefault")) {
            showToast(JSONUtils.parseKeyAndValueToMap(result).get("message"));
            addr.listing(noid, this);
        } else if (params.getUri().contains("Addr/setDefault")) {
            showToast(JSONUtils.parseKeyAndValueToMap(result).get("message"));
            addr.listing(noid, this);
        }
        super.onComplete(params, result);
        swipeToLoadRecyclerView.stopRefreshing();
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
                startActivity(AddAddressAty.class, null);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRefresh() {
        noid = application.getUserInfo().get("noid");
        addr.listing(noid, this);
    }

    private void updateUI() {
        if (view == null) {
            view = View.inflate(RecruitAddressAty.this, R.layout.header_recruit_address, null);
            ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            view.setLayoutParams(layoutParams);
        }
        swipeToLoadRecyclerView.addHeaderView(view);
        swipeToLoadRecyclerView.setAdapter(new AddressAdapter());
        if (ListUtils.isEmpty(list)) {
            tvEmpty.setVisibility(View.VISIBLE);
        } else {
            tvEmpty.setVisibility(View.GONE);
        }
    }

    private class AddressAdapter extends RecyclerView.Adapter<AddressAdapter.ViewHodler> {


        @Override
        public ViewHodler onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.listitem_recruit_address, parent, false);
            return new ViewHodler(view);
        }

        @Override
        public void onBindViewHolder(final ViewHodler holder, int position) {
            final Map<String, String> map = list.get(position);
            holder.tvName.setText(map.get("name"));
            holder.tvPhone.setText(map.get("mobile"));
            holder.tvAddress.setText(map.get("ress"));
            if (TextUtils.equals(map.get("def"), "1")) {
                holder.tvDefault.setCompoundDrawablesWithIntrinsicBounds(R.drawable.btn_addres_select, 0, 0, 0);
            } else {
                holder.tvDefault.setCompoundDrawablesWithIntrinsicBounds(R.drawable.btn_addres_unselect, 0, 0, 0);
            }
            holder.tvEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    bundle.putString("name", holder.tvName.getText().toString());
                    bundle.putString("phone", holder.tvPhone.getText().toString());
                    bundle.putString("address", holder.tvAddress.getText().toString());
                    bundle.putString("id", map.get("addr_id"));
                    bundle.putString("flag", "edit");
                    startActivity(AddAddressAty.class, bundle);
                }
            });
            holder.tvDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showDialog("提示", "是否删除该地址？", "删除", "取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            showProgressDialog();
                            addr.remove(noid, map.get("addr_id"), RecruitAddressAty.this);
                        }
                    }, null);
                }
            });
            holder.tvDefault.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (TextUtils.equals(map.get("def"), "1")) {
                        showProgressDialog();
                        addr.cancelDefault(noid, map.get("addr_id"), RecruitAddressAty.this);
                        holder.tvDefault.setCompoundDrawablesWithIntrinsicBounds(R.drawable.btn_addres_unselect, 0, 0, 0);
                    } else {
                        showProgressDialog();
                        addr.setDefault(noid, map.get("addr_id"), RecruitAddressAty.this);
                        holder.tvDefault.setCompoundDrawablesWithIntrinsicBounds(R.drawable.btn_addres_select, 0, 0, 0);
                    }

                }
            });
            if (TextUtils.equals(getIntent().getStringExtra("flag"), "new_jo")) {
                holder.linlayAddress.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent intent = new Intent();
//
//                     "province_name": - 省
//                     "city_name":     - 市
//                     "area_name":     - 区
                        intent.putExtra("province_name", map.get("province_name"));
                        intent.putExtra("city_name", map.get("city_name"));
                        intent.putExtra("area_name", map.get("area_name"));
                        intent.putExtra("ress", map.get("ress"));
                        intent.putExtra("longitude", map.get("longitude"));
                        intent.putExtra("latitude", map.get("latitude"));
                        setResult(RESULT_OK, intent);
                        finish();
                    }
                });
            }

        }

        @Override
        public int getItemCount() {
            return ListUtils.getSize(list);
        }

        public class ViewHodler extends RecyclerView.ViewHolder {
            @ViewInject(R.id.listitem_address_name)
            TextView tvName;
            @ViewInject(R.id.listitem_address_phone)
            TextView tvPhone;
            @ViewInject(R.id.listitem_address)
            TextView tvAddress;
            @ViewInject(R.id.listitem_address_default)
            TextView tvDefault;
            @ViewInject(R.id.listitem_address_edit)
            TextView tvEdit;
            @ViewInject(R.id.listitem_address_delete)
            TextView tvDelete;
            @ViewInject(R.id.list_address_click)
            LinearLayout linlayAddress;


            public ViewHodler(View itemView) {
                super(itemView);
                x.view().inject(this, itemView);
                AutoUtils.autoSize(itemView);
            }
        }
    }
}
