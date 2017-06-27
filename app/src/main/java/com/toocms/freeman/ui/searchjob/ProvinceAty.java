package com.toocms.freeman.ui.searchjob;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.toocms.freeman.R;
import com.toocms.freeman.ui.BaseAty;
import com.zero.autolayout.utils.AutoUtils;

import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

import cn.zero.android.common.view.linearlistview.LinearListView;

public class ProvinceAty extends BaseAty {

    @ViewInject(R.id.province_LLV)
    private LinearListView provinceLlv;

    ProvinceOrCityAdapter mAdapter;
    //数据源
    List<String> dataList = new ArrayList<>();

    //模拟数据
    {
        for (int i = 0; i < 10; i++) {
            dataList.add("天津"+i);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActionBar.setTitle("城市");

        updateUI();
    }

    private void updateUI() {

        if (mAdapter == null){
            mAdapter = new ProvinceOrCityAdapter();
            provinceLlv.setAdapter(mAdapter);
        }

        mAdapter.notifyDataSetChanged();
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.aty_province;
    }

    @Override
    protected void initialized() {

    }

    @Override
    protected void requestData() {

    }

    private class ProvinceOrCityAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return 10;
        }

        @Override
        public Object getItem(int position) {
            return dataList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ProvinceOrCityAdapter.ViewHolder viewHolder;
            if (convertView == null) {
                convertView = LayoutInflater.from(ProvinceAty.this).inflate(R.layout.listitem_province_or_city, parent, false);
                viewHolder = new ViewHolder(convertView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.provinceName.setText(dataList.get(position));

            return convertView;
        }

        public class ViewHolder {
            @ViewInject(R.id.province_name)
            TextView provinceName;

            public ViewHolder(View itemView) {
                x.view().inject(this, itemView);
                AutoUtils.auto(itemView);
            }
        }
    }
}
