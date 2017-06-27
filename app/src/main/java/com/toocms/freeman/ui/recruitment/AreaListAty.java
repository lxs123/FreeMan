package com.toocms.freeman.ui.recruitment;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.toocms.freeman.R;
import com.toocms.freeman.https.Addr;
import com.toocms.freeman.ui.BaseAty;
import com.zero.autolayout.utils.AutoUtils;

import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import cn.zero.android.common.util.JSONUtils;
import cn.zero.android.common.util.ListUtils;

import static cn.zero.android.common.util.JSONUtils.parseDataToMapList;

/**
 * 省市区
 * Created by admin on 2017/4/14.
 */

public class AreaListAty extends BaseAty {
    @ViewInject(R.id.city_list)
    ListView linLv;
    private String province_name;
    private String city_name;

    /**
     * 获取地区库省列表[provinceList]
     *
     * @param opend 如果为1，返回后台开通省。默认返回所有
     */

    /**
     * 获取地址库市列表[cityList]
     *
     * @param opend       如果为1，获取后台开通市。默认返回所有
     * @param province_id 省id
     */
    private String province_id;
    /**
     * 获取地址库区列表[areaList]
     *
     * @param opend   如果为1，获取后台开通的区。默认返回所有
     * @param city_id 市id
     */
    private String city_id;
    private String opend;
    private Addr addr;
    private ArrayList<Map<String, String>> list;
    private MyAdapter myAdapter;
    int type = 0;//0 省，1 市 ，2 区
    private String flag = "province";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    protected void onResume() {
        super.onResume();
        opend = "2";
        showProgressContent();
        switch (type) {
            case 0:
                addr.provinceList(opend, this);
                break;

        }


    }

    @Override
    protected int getLayoutResId() {
        return R.layout.aty_city_list;
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
        super.onComplete(params, result);
        list = new ArrayList<>();
        myAdapter = new MyAdapter();

        Map<String, String> map = new HashMap<>();
        map.put("region_id", "");
        map.put("name", "全境");
        list.add(map);

        if (params.getUri().contains("Region/provinceList")) {
            list.addAll(JSONUtils.parseDataToMapList(result));
            myAdapter.notifyDataSetChanged();
        } else if (params.getUri().contains("Region/cityList")) {
            mActionBar.setTitle("市");
            type = 1;
            list.addAll(parseDataToMapList(result));
            myAdapter.notifyDataSetChanged();
        } else if (params.getUri().contains("Region/areaList")) {
            type = 2;
            mActionBar.setTitle("区/县");
            list.addAll(parseDataToMapList(result));
            myAdapter.notifyDataSetChanged();
        }

        linLv.setAdapter(myAdapter);
    }

    private class MyAdapter extends BaseAdapter {

        private ViewHolder viewHolder;


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
                convertView = getLayoutInflater().from(AreaListAty.this).inflate(R.layout.listitem_province_or_city, parent, false);
                viewHolder = new ViewHolder();
                x.view().inject(viewHolder, convertView);
                AutoUtils.autoSize(convertView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            final Map<String, String> map = list.get(position);
            viewHolder.tvName.setText(map.get("name"));
            viewHolder.tvName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (TextUtils.equals(flag, "province")) {
                        province_id = map.get("region_id");
                        province_name = map.get("name");

                        if (position == 0) {
                            Intent intent = new Intent();
                            intent.putExtra("province_name", "全国境内");
                            intent.putExtra("city_name", "");
                            intent.putExtra("name", "");
                            intent.putExtra("str", "全国境内");
                            setResult(RESULT_OK, intent);
                            finish();
                        }

                        addr.cityList(opend, province_id, AreaListAty.this);
                        flag = "city";
                    } else if (TextUtils.equals(flag, "city")) {
                        city_id = map.get("region_id");
                        city_name = map.get("name");

                        if (position == 0) {
                            Intent intent = new Intent();
                            intent.putExtra("province_id", province_id);
                            intent.putExtra("province_name", province_name);
                            intent.putExtra("city_id", "");
                            intent.putExtra("city_name", "");
                            intent.putExtra("name", "");
                            intent.putExtra("str", province_name + "全境");
                            setResult(RESULT_OK, intent);
                            finish();
                        }
                        addr.areaList(opend, city_id, AreaListAty.this);
                        flag = "area";
                    } else if (TextUtils.equals(flag, "area")) {
                        if (position == 0) {
                            Intent intent = new Intent();
                            intent.putExtra("province_id", province_id);
                            intent.putExtra("province_name", province_name);
                            intent.putExtra("city_id", city_id);
                            intent.putExtra("city_name", city_name);
                            intent.putExtra("name", "");
                            intent.putExtra("str", province_name + "," + city_name + "全境");
                            setResult(RESULT_OK, intent);
                            finish();
                        }
                    }


                    if (type == 2) {
                        Intent intent = new Intent();
                        intent.putExtra("province_name", province_name);
                        intent.putExtra("city_name", city_name);
                        intent.putExtra("name", map.get("name"));
                        intent.putExtra("province_id", province_id);
                        intent.putExtra("city_id", city_id);
                        intent.putExtra("area_id", map.get("region_id"));
                        intent.putExtra("str", province_name + "," + city_name + map.get("name"));
                        setResult(RESULT_OK, intent);
                        finish();
                    }

                }
            });
            if (position == ListUtils.getSize(list) - 1) {
                viewHolder.vDivid.setVisibility(View.GONE);
            } else {
                viewHolder.vDivid.setVisibility(View.VISIBLE);
            }
            return convertView;
        }
    }

    private class ViewHolder {
        @ViewInject(R.id.province_name)
        TextView tvName;
        @ViewInject(R.id.list_divid)
        View vDivid;
    }
}
