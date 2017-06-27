package com.toocms.freeman.ui.recruitment.myjoborder;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.toocms.freeman.R;
import com.toocms.freeman.https.BaiduLbs;
import com.toocms.freeman.ui.BaseAty;

import org.xutils.http.RequestParams;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.zero.android.common.util.JSONUtils;
import cn.zero.android.common.util.ListUtils;
import cn.zero.android.common.util.StringUtils;
import cn.zero.android.common.view.ClearEditText;

/**
 * Created by ElveTrees on 2015/11/24.
 * 搜索地址
 */
public class SearchingAddressAty extends BaseAty {

    @ViewInject(R.id.searching_address_cetxt_searching)
    private ClearEditText editSearch;

    @ViewInject(R.id.searching_address_lv)
    private ListView lv;

    private MyAdapter adapter;

    private List<Map<String, String>> addressList;

    private BaiduLbs baiduLbs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActionBar.hide();
        lv.setAdapter(adapter);
        editSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0) {
                    baiduLbs.placeSearch(s.toString(), "全国", SearchingAddressAty.this);
                }

            }
        });


        editSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId,
                                          KeyEvent event) {
//				showToast("点击了按钮"+actionId+"点击内容是"+v.getText().toString());
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {// 修改回车键功能

                    // 先隐藏键盘
                    ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE))
                            .hideSoftInputFromWindow(SearchingAddressAty.this
                                            .getCurrentFocus().getWindowToken(),
                                    InputMethodManager.HIDE_NOT_ALWAYS);

                    String q = editSearch.getText().toString();
                    if (!StringUtils.isEmpty(q)) {
                        showProgressDialog();
                        baiduLbs.placeSearch(q, "全国", SearchingAddressAty.this);
                    }
                    return true;
                }
                return false;
            }
        });
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if (!adapter.getItem(position).containsKey("location")) {
                    showToast("请选择一个更详情的地址，此地址没有坐标信息");
                    return;
                }
                Map<String, String> map = new HashMap<>();
                map.put("address", adapter.getItem(position).get("name"));
//                +adapter.getItem(position).get("address"));
                map.put("longitude", JSONUtils.parseKeyAndValueToMap(adapter.getItem(position).get("location")).get("lng"));
                map.put("latitude", JSONUtils.parseKeyAndValueToMap(adapter.getItem(position).get("location")).get("lat"));
//              EventBus.getDefault().post(new EventBean(map, "address"));
                Intent intent = new Intent();
                intent.putExtra("address", adapter.getItem(position).get("name"));
                intent.putExtra("detail", adapter.getItem(position).get("city")+"  "+adapter.getItem(position).get("district"));
                intent.putExtra("longitude", JSONUtils.parseKeyAndValueToMap(adapter.getItem(position).get("location")).get("lng"));
                intent.putExtra("latitude", JSONUtils.parseKeyAndValueToMap(adapter.getItem(position).get("location")).get("lat"));
                setResult(RESULT_OK, intent);
//                AppManager.getInstance().killActivity(LocationAddressAty.class);
                finish();
            }
        });


    }

    @Override
    protected int getLayoutResId() {
        return R.layout.aty_searching_address;
    }

    @Override
    protected void initialized() {
        adapter = new MyAdapter();
        baiduLbs = new BaiduLbs();
        addressList = new ArrayList<>();
    }

    @Override
    protected void requestData() {

    }

    @Event({R.id.back})
    private void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                finish();
                break;
        }
    }

//    @Override
//    public void onComplete(String requestUrl, ResponseInfo<String> responseInfo) {
//        Map<String, String> map = JSONUtils.parseKeyAndValueToMap(responseInfo.result);
//        addressList = JSONUtils.parseKeyAndValueToMapList(map.get("results"));
//        adapter.notifyDataSetChanged();
//        super.onComplete(requestUrl, responseInfo);
//    }


//    String string = "http://api.map.baidu.com/place/v2/suggestion?query=天安门&region=北京市&city_limit=true&output=json&ak=7LdceMFMTsh5O71G1Gns5zfE0bTksYRM&mcode=AE:FB:98:1D:48:2D:21:2D:1C:CB:46:11:56:1B:AF:4F:63:D4:AF:9A;com.toocms.freeman";

    @Override
    public void onComplete(RequestParams params, String result) {
//        LogUtil.e(params.toString());
//        LogUtil.e(result);
        Map<String, String> map = JSONUtils.parseKeyAndValueToMap(result);
        addressList = JSONUtils.parseKeyAndValueToMapList(map.get("result"));
        adapter.notifyDataSetChanged();
        super.onComplete(params, result);
    }

    private class MyAdapter extends BaseAdapter {
        private ViewHolder holder;

        @Override
        public int getCount() {
            return ListUtils.getSize(addressList);
        }

        @Override
        public Map<String, String> getItem(int position) {
            return addressList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Map<String, String> item = getItem(position);
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = getLayoutInflater().inflate(R.layout.listitem_searching_address, parent, false);
                x.view().inject(holder, convertView);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.tv_name.setText(item.get("name"));
            holder.tv_address.setText(item.get("city")+"   "+item.get("district"));
            return convertView;
        }
    }

    private class ViewHolder {
        @ViewInject(R.id.searching_address_tv_name)
        private TextView tv_name;
        @ViewInject(R.id.searching_address_tv_address)
        private TextView tv_address;
    }
}
