package com.toocms.freeman.ui.pay.wallet;

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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.toocms.frame.image.ImageLoader;
import com.toocms.freeman.R;
import com.toocms.freeman.https.Platform;
import com.toocms.freeman.ui.BaseAty;
import com.zero.autolayout.utils.AutoUtils;

import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;
import java.util.Map;

import cn.zero.android.common.util.JSONUtils;
import cn.zero.android.common.util.ListUtils;
import cn.zero.android.common.view.swipetoloadlayout.view.SwipeToLoadRecyclerView;

/**
 * 选择银行卡
 * Created by admin on 2017/3/30.
 */

public class SelBankCardAty extends BaseAty {
    @ViewInject(R.id.sel_bank_list)
    SwipeToLoadRecyclerView swipeToLoadRecyclerView;
    @ViewInject(R.id.sel_bank_empty)
    TextView tvEmpty;
    @ViewInject(R.id.sel_wx_empty)
    TextView tvWxEmpty;
    private RecyclerAdapter adapter;
    private String flag;
    int position = 1;
    /**
     * 绑定列表[listing]
     *
     * @param noid    noid
     * @param partner partner 第三方类型： WECHAT ALIPAY BANK
     */
    private String noid;
    private String partner;
    private Platform platform;
    private ArrayList<Map<String, String>> list = new ArrayList<>();

    @Override
    protected int getLayoutResId() {
        return R.layout.aty_sel_bank;
    }

    @Override
    protected void initialized() {
        platform = new Platform();
    }

    @Override
    protected void requestData() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        flag = getIntent().getStringExtra("flag");
        swipeToLoadRecyclerView.getRecyclerView().setLayoutManager(new LinearLayoutManager(this));
        if (TextUtils.equals(flag, "wx")) {
            mActionBar.setTitle("选择微信账户");
            position = 2;
            partner = "WECHAT";
        } else if (TextUtils.equals(flag, "ali")) {
            mActionBar.setTitle("选择支付宝账号");
            position = 3;
            partner = "ALIPAY";
        } else {
            mActionBar.setTitle("选择银行卡");
            position = 1;
            partner = "BANK";
        }
        tvEmpty.setVisibility(View.GONE);
        noid = application.getUserInfo().get("noid");
    }

    @Override
    protected void onResume() {
        super.onResume();
        showProgressDialog();
        platform.listing(noid, partner, this);
    }

    @Override
    public void onComplete(RequestParams params, String result) {
        if (params.getUri().contains("Platform/listing")) {
            list = JSONUtils.parseDataToMapList(result);
            if (adapter == null) {
                adapter = new RecyclerAdapter();
                swipeToLoadRecyclerView.setAdapter(adapter);
            } else {
                adapter.notifyDataSetChanged();
            }
            if (TextUtils.equals(flag, "wx")) {
                if (ListUtils.isEmpty(list)) {
                    tvWxEmpty.setVisibility(View.VISIBLE);
                } else {
                    tvWxEmpty.setVisibility(View.GONE);
                }
            } else if (TextUtils.equals(flag, "ali")) {
                if (ListUtils.isEmpty(list)) {
                    tvWxEmpty.setVisibility(View.VISIBLE);
                } else {
                    tvWxEmpty.setVisibility(View.GONE);
                }
            } else {
                if (ListUtils.isEmpty(list)) {
                    tvEmpty.setVisibility(View.VISIBLE);
                } else {
                    tvEmpty.setVisibility(View.GONE);
                }
            }

        }
        super.onComplete(params, result);
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
                Bundle bundle = new Bundle();
                if (position == 2) {
                    bundle.putString("flag", "wx");
                } else if (position == 1) {
                    bundle.putString("flag", "bank");
                } else {
                    bundle.putString("flag", "ali");
                }
                startActivity(AddBankAty.class, bundle);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {
        private String substring;
        private String card_type_name;

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.listitem_sel_bank, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            final Map<String, String> map = list.get(position);
            if (TextUtils.equals(flag, "wx")) {
                holder.linlayContent.setVisibility(View.GONE);
                holder.tvWx.setVisibility(View.VISIBLE);
                holder.imgv.setImageResource(R.drawable.icon_wet);
                holder.tvWx.setText(map.get("userid"));
            } else if (TextUtils.equals(flag, "ali")) {
                holder.linlayContent.setVisibility(View.GONE);
                holder.tvWx.setVisibility(View.VISIBLE);
                holder.imgv.setImageResource(R.drawable.icon_pay);
                holder.tvWx.setText(map.get("userid"));
            } else {
                holder.tvWx.setVisibility(View.GONE);
                holder.linlayContent.setVisibility(View.VISIBLE);
                ImageLoader imageLoader = new ImageLoader();
                if (map.containsKey("icon"))
                    imageLoader.disPlay(holder.imgv, map.get("icon"));
                card_type_name = null;
                if (map.containsKey("bank_name")) {
                    holder.tvBank.setText(map.get("bank_name"));
                    card_type_name = map.get("card_type_name");
                    String platform_id = map.get("userid");
                    if (platform_id.length() > 5)
                        substring = platform_id.substring(platform_id.length() - 4, platform_id.length());
                    holder.tvType.setText("*" + substring + card_type_name);
                }


            }
            holder.linlayBank.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    switch (flag) {
                        case "bank":
                            intent.putExtra("bank_name", map.get("bank_name"));
                            intent.putExtra("bank_type", substring + card_type_name);
                            intent.putExtra("userid", map.get("userid"));
                            break;
                        case "wx":
                            intent.putExtra("userid", map.get("userid"));
                            break;
                        case "ali":
                            intent.putExtra("userid", map.get("userid"));
                            break;
                    }
                    setResult(RESULT_OK, intent);
                    finish();
                }
            });
        }

        @Override
        public int getItemCount() {
            return ListUtils.getSize(list);
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            @ViewInject(R.id.list_sel_bank_content)
            LinearLayout linlayContent;
            @ViewInject(R.id.list_wx_content)
            TextView tvWx;
            @ViewInject(R.id.list_sel_img)
            ImageView imgv;
            @ViewInject(R.id.list_bank_name)
            TextView tvBank;
            @ViewInject(R.id.list_bank_type)
            TextView tvType;
            @ViewInject(R.id.list_sel_banklay)
            LinearLayout linlayBank;

            public ViewHolder(View itemView) {
                super(itemView);
                x.view().inject(this, itemView);
                AutoUtils.autoSize(itemView);
            }
        }
    }
}
