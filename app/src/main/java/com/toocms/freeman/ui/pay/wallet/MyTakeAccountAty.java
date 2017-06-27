package com.toocms.freeman.ui.pay.wallet;

import android.content.DialogInterface;
import android.graphics.Color;
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

import com.toocms.frame.config.Settings;
import com.toocms.frame.image.ImageLoader;
import com.toocms.freeman.R;
import com.toocms.freeman.https.Platform;
import com.toocms.freeman.ui.BaseAty;
import com.yanzhenjie.recyclerview.swipe.Closeable;
import com.yanzhenjie.recyclerview.swipe.OnSwipeMenuItemClickListener;
import com.yanzhenjie.recyclerview.swipe.SwipeMenu;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuAdapter;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuCreator;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuItem;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuRecyclerView;
import com.zero.autolayout.utils.AutoUtils;

import org.xutils.http.RequestParams;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cn.zero.android.common.recyclerview.RecycleViewDivider;
import cn.zero.android.common.util.JSONUtils;
import cn.zero.android.common.util.ListUtils;

import static android.widget.LinearLayout.VERTICAL;


/**
 * Created by admin on 2017/3/30.
 */

public class MyTakeAccountAty extends BaseAty {
    @ViewInject(R.id.my_take_a_list)
    SwipeMenuRecyclerView myRecyclerView;
    @ViewInject(R.id.take_title_view)
    private View vLine;
    int position = 1;
    @ViewInject(R.id.take_to_card)
    TextView tvCard;
    @ViewInject(R.id.take_to_wx)
    TextView tvWx;
    @ViewInject(R.id.take_to_alipay)
    TextView tvAlipay;
    @ViewInject(R.id.sel_bank_empty)
    TextView tvEmpty;
    @ViewInject(R.id.sel_wx_empty)
    TextView tvWxEmpty;

    TextView[] textViews;
    private List<Map<String, String>> list = new ArrayList<>();
    private MenuAdapter adapter;
    private String flag;
    /**
     * 绑定列表[listing]
     *
     * @param noid    noid
     * @param partner partner 第三方类型： WECHAT ALIPAY BANK
     */
    private String noid;
    private String partner = "BANK";
    private Platform platform;
    /**
     * 解绑微信[unbindWechat]
     *
     * @param noid
     * @param platform_id 微信id
     */
    private String platform_id;

//
//    {
//        for (int i = 0; i < 3; i++) {
//            HashMap<String, String> map = new HashMap<>();
//            map.put("bank", "中国农业银行");
//            list.add(map);
//        }
//    }

    @Override
    protected int getLayoutResId() {
        return R.layout.aty_my_take_account;
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
        textViews = new TextView[]{tvCard, tvWx, tvAlipay};
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        myRecyclerView.setLayoutManager(linearLayoutManager);
        myRecyclerView.setSwipeMenuCreator(swipeMenuCreator);
        myRecyclerView.addItemDecoration(new RecycleViewDivider(this, VERTICAL, AutoUtils.getPercentHeightSize(20), getResources().getColor(R.color.clr_bg)));
        // 设置菜单Item点击监听。
        myRecyclerView.setSwipeMenuItemClickListener(menuItemClickListener);

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

            tvEmpty.setVisibility(View.GONE);
            tvWxEmpty.setVisibility(View.GONE);

//            if (adapter == null) {
            adapter = new MenuAdapter();
            myRecyclerView.setAdapter(adapter);
//            } else {
//                adapter.notifyDataSetChanged();
//            }
        } else if (params.getUri().contains("Platform/unbindAlipay") ||
                params.getUri().contains("Platform/unbindWechat") ||
                params.getUri().contains("Platform/unbindBankIdent")) {
            platform.listing(noid, partner, this);
        }
        super.onComplete(params, result);
    }

    @Event({R.id.take_to_card, R.id.take_to_wx, R.id.take_to_alipay, R.id.take_bank_card})
    private void onClick(View view) {
        switch (view.getId()) {
            case R.id.take_to_card:
                position = 1;
                flag = "bank";
                partner = "BANK";
                adapter = null;
                break;
            case R.id.take_to_wx:
                flag = "wx";
                position = 2;
                partner = "WECHAT";
                adapter = null;
                break;
            case R.id.take_to_alipay:
                flag = "ali";
                position = 3;
                partner = "ALIPAY";
                adapter = null;
                break;
        }
        showProgressDialog();
        platform.listing(noid, partner, this);
        myRecyclerView.smoothCloseMenu();
//        adapter.notifyDataSetChanged();
        changeTextClr(position);
        startTranslate(vLine, (Settings.displayWidth / 3) * (position - 1));
    }

    private void changeTextClr(int position) {
        for (int i = 1; i < 4; i++) {
            if (position == i) {
                textViews[i - 1].setSelected(true);
            } else {
                textViews[i - 1].setSelected(false);
            }
        }
    }


    private OnSwipeMenuItemClickListener menuItemClickListener = new OnSwipeMenuItemClickListener() {
        /**
         * Item的菜单被点击的时候调用。
         * @param closeable       closeable. 用来关闭菜单。
         * @param adapterPosition adapterPosition. 这个菜单所在的item在Adapter中position。
         * @param menuPosition    menuPosition. 这个菜单的position。比如你为某个Item创建了2个MenuItem，那么这个position可能是是 0、1，
         * @param direction       如果是左侧菜单，值是：SwipeMenuRecyclerView#LEFT_DIRECTION，如果是右侧菜单，值是：SwipeMenuRecyclerView
         *                        #RIGHT_DIRECTION.
         */
        @Override
        public void onItemClick(final Closeable closeable, final int adapterPosition, int menuPosition, int direction) {
            if (direction == SwipeMenuRecyclerView.RIGHT_DIRECTION) {
                final StringBuilder builder = new StringBuilder();
                builder.append("解除");
                String account = null;
                if (TextUtils.equals(flag, "wx")) {
                    builder.append("微信账号");
                    account = "账号";
                } else if (TextUtils.equals(flag, "ali")) {
                    builder.append("支付宝账号");
                    account = "账号";
                } else {
                    builder.append("银行卡");
                    account = "卡";
                }
                builder.append("绑定就不能使用该");
                builder.append(account);
                builder.append("提现了，确定解绑？");
                showDialog("提示", String.valueOf(builder), "确定解绑", "取消", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        closeable.smoothCloseMenu();// 关闭被点击的菜单。
                        if (TextUtils.equals(flag, "wx")) {
                            showProgressDialog();
                            platform.unbindWechat(noid, platform_id, MyTakeAccountAty.this);
                        } else if (TextUtils.equals(flag, "ali")) {
                            showProgressDialog();
                            platform.unbindAlipay(noid, platform_id, MyTakeAccountAty.this);
                        } else {
                            showProgressDialog();
                            platform.unbindBankIdent(noid, platform_id, MyTakeAccountAty.this);
                        }
//                        list.remove(adapterPosition);
//                        adapter.notifyItemRemoved(adapterPosition);

                    }
                }, null);
            }
        }
    };


    private SwipeMenuCreator swipeMenuCreator = new SwipeMenuCreator() {
        @Override
        public void onCreateMenu(SwipeMenu swipeLeftMenu, SwipeMenu swipeRightMenu, int viewType) {
            int width = AutoUtils.getPercentWidthSize(160);
            // MATCH_PARENT 自适应高度，保持和内容一样高；也可以指定菜单具体高度，也可以用WRAP_CONTENT。
            int height = ViewGroup.LayoutParams.MATCH_PARENT;

            // 添加左侧的，如果swipeLeftMenu不添加，则左侧不会出现菜单。

            // 添加右侧的，如果不添加，则右侧不会出现菜单。
            {
                SwipeMenuItem deleteItem = new SwipeMenuItem(getBaseContext())
                        .setBackgroundDrawable(R.color.clr_main)
                        .setText("解绑") // 文字，还可以设置文字颜色，大小等。。
                        .setTextColor(Color.WHITE)
                        .setTextSize(18)
                        .setWidth(width)
                        .setHeight(height);
                swipeRightMenu.addMenuItem(deleteItem);// 添加一个按钮到右侧侧菜单。

            }
        }
    };

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
                bundle.putString("flag", flag);
                startActivity(AddBankAty.class, bundle);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private class MenuAdapter extends SwipeMenuAdapter<MenuAdapter.DefaultViewHolder> {

        private String substring;

        @Override
        public View onCreateContentView(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.listitem_my_take_a, parent, false);

            return view;
        }

        @Override
        public DefaultViewHolder onCompatCreateViewHolder(View realContentView, int viewType) {
            DefaultViewHolder viewHolder = new DefaultViewHolder(realContentView);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(DefaultViewHolder holder, int position) {
            Map<String, String> map = list.get(position);
            final StringBuilder builder = new StringBuilder();
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
            if (TextUtils.equals(flag, "wx")) {
                holder.tvBank.setVisibility(View.GONE);
                holder.tvType.setVisibility(View.GONE);
                holder.linlayBank.setVisibility(View.GONE);
                holder.tvWx.setVisibility(View.VISIBLE);
                holder.imgv.setImageResource(R.drawable.icon_wet);
                holder.tvWx.setText(map.get("userid"));
                platform_id = map.get("userid");
            } else if (TextUtils.equals(flag, "ali")) {
                holder.tvBank.setVisibility(View.GONE);
                holder.tvType.setVisibility(View.GONE);
                holder.linlayBank.setVisibility(View.GONE);
                holder.tvWx.setVisibility(View.VISIBLE);
                holder.imgv.setImageResource(R.drawable.icon_pay);
                holder.tvWx.setText(map.get("userid"));
                platform_id = map.get("userid");
            } else {
                holder.tvWx.setVisibility(View.GONE);
                holder.linlayBank.setVisibility(View.VISIBLE);
                holder.tvBank.setText("");
                holder.tvType.setText("");
                ImageLoader imageLoader = new ImageLoader();
                if (map.containsKey("icon"))
                    imageLoader.disPlay(holder.imgv, map.get("icon"));
                holder.tvBank.setText(map.get("bank_name"));
                String card_type_name = map.get("card_type_name");

                platform_id = map.get("userid");
                if (platform_id.length() > 5)
                    substring = platform_id.substring(platform_id.length() - 4, platform_id.length());
                holder.tvType.setText("*" + substring + card_type_name);
            }


        }

        @Override
        public int getItemCount() {
            return ListUtils.getSize(list);
        }

        public class DefaultViewHolder extends RecyclerView.ViewHolder {
            @ViewInject(R.id.list_sel_bank_content)
            LinearLayout linlayBank;    //整个的
            @ViewInject(R.id.list_wx_content)
            TextView tvWx;      //jia
            @ViewInject(R.id.list_sel_img)
            ImageView imgv;     //微信 支付宝图片
            @ViewInject(R.id.list_bank_name)
            TextView tvBank;        //中国建设银行
            @ViewInject(R.id.list_bank_type)
            TextView tvType;        //银行卡尾号
            @ViewInject(R.id.swipe_layout)
            LinearLayout mSwipeItemLayout;

            public DefaultViewHolder(View itemView) {
                super(itemView);
                x.view().inject(this, itemView);
                AutoUtils.autoSize(itemView);
            }

        }
    }
}
