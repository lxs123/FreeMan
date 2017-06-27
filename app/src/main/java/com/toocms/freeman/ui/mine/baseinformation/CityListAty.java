package com.toocms.freeman.ui.mine.baseinformation;

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
import java.util.Map;

import cn.zero.android.common.util.JSONUtils;
import cn.zero.android.common.util.ListUtils;

/**
 * Created by admin on 2017/4/14.
 */

public class CityListAty extends BaseAty {
    @ViewInject(R.id.city_list)
    ListView linLv;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        province_id = intent.getStringExtra("province_id");
        city_id = intent.getStringExtra("city_id");
        String flag = intent.getStringExtra("flag");
        if (TextUtils.equals(flag, "province")) {
            mActionBar.setTitle("省");
            type = 0;
        } else if (TextUtils.equals(flag, "city")) {
            mActionBar.setTitle("市");
            type = 1;
        } else if (TextUtils.equals(flag, "area")) {
            type = 2;
            mActionBar.setTitle("区/县");
        }
        myAdapter = new MyAdapter();
        linLv.setAdapter(myAdapter);

    }

    @Override
    protected void onResume() {
        super.onResume();
        switch (type) {
            case 0:
                opend = "2";
                addr.provinceList(opend, this);
                break;
            case 1:
                opend = "2";
                addr.cityList(opend, province_id, this);
                break;
            case 2:
                opend = "2";
                addr.areaList(opend, city_id, this);
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
        if (params.getUri().contains("Region/provinceList")) {
            list = JSONUtils.parseDataToMapList(result);
            myAdapter.notifyDataSetChanged();
        } else if (params.getUri().contains("Region/cityList")) {
            list = JSONUtils.parseDataToMapList(result);
            myAdapter.notifyDataSetChanged();
        } else if (params.getUri().contains("Region/areaList")) {
            list = JSONUtils.parseDataToMapList(result);
            myAdapter.notifyDataSetChanged();
        }
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
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = getLayoutInflater().from(CityListAty.this).inflate(R.layout.listitem_province_or_city, parent, false);
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
                    Intent intent = new Intent();
                    intent.putExtra("name", map.get("name"));
                    intent.putExtra("region_id", map.get("region_id"));
                    setResult(RESULT_OK, intent);
                    finish();
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
