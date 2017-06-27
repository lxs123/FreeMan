package com.toocms.freeman.ui.recruitment.joborder;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.toocms.freeman.R;
import com.toocms.freeman.https.Sys;
import com.toocms.freeman.ui.BaseAty;
import com.zero.autolayout.utils.AutoUtils;

import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cn.zero.android.common.util.JSONUtils;
import cn.zero.android.common.util.ListUtils;
import cn.zero.android.common.view.linearlistview.LinearListView;

/**
 * 新建工作订单--单位--重量
 * JOU---job order unit 缩写
 * Created by admin on 2017/3/23.
 */

public class JOUWeightAty extends BaseAty {
    @ViewInject(R.id.job_order_unit_list)
    private LinearListView unitListview;
    private ArrayList<Map<String, String>> unitList = new ArrayList<>();
    private ArrayList<Map<String, String>> uWeightList = new ArrayList<>();
    String[] unit = new String[]{"轻量级", "重量级"};
    String[] unitWeight = new String[]{"千克", "吨", "磅"};
    private Sys sys;
    private String flag = "f";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showProgressContent();
        sys.getUnitList(getIntent().getStringExtra("unit_id"), "1", this);
        mActionBar.setTitle(getIntent().getStringExtra("name"));


        unitListview.setOnItemClickListener(new LinearListView.OnItemClickListener() {
            @Override
            public void onItemClick(LinearListView linearListView, View view, int i, long l) {
//                    Bundle bundle = new Bundle();
//                    bundle.putString("flag", "flag");
//                    bundle.putString("unit_id2", unitList.get(i).get("unit_id"));
//                    bundle.putString("name2", unitList.get(i).get("name"));
//                    startActivity(JOUWeightAty.class, bundle);
                if (TextUtils.equals(flag, "flag")) {
                    Intent intent = new Intent();
                    flag = "1";
                    intent.putExtra("unit_id", uWeightList.get(i).get("unit_id"));
                    intent.putExtra("name", uWeightList.get(i).get("name"));
                    setResult(RESULT_OK, intent);
                    finish();
                    return;
                }
                showProgressContent();
                sys.getUnitList(unitList.get(i).get("unit_id"), "2", JOUWeightAty.this);
                flag = "flag";
                mActionBar.setTitle(unitList.get(i).get("name"));

            }
        });
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.aty_job_order_unit;
    }

    @Override
    protected void initialized() {
        sys = new Sys();
    }

    @Override
    protected void requestData() {

    }

    @Override
    public void onComplete(RequestParams params, String result) {
        if (params.getUri().contains("Sys/getUnitList")) {
            if (TextUtils.equals(flag, "flag")) {
                uWeightList = JSONUtils.parseDataToMapList(result);
                unitListview.setAdapter(new UnitAdapter(uWeightList));
            } else {
                unitList = JSONUtils.parseDataToMapList(result);
                if (ListUtils.isEmpty(unitList)) {
                    Intent intent = new Intent();
                    intent.putExtra("unit_id", getIntent().getStringExtra("unit_id"));
                    intent.putExtra("name", getIntent().getStringExtra("name"));
                    setResult(RESULT_OK, intent);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            finish();
                        }
                    }, 300);

                }
                unitListview.setAdapter(new UnitAdapter(unitList));

            }
        }
        super.onComplete(params, result);
    }

    private class UnitAdapter extends BaseAdapter {
        private List<Map<String, String>> uWeightList = new ArrayList<>();


        public UnitAdapter(List<Map<String, String>> uWeightList) {
            this.uWeightList = uWeightList;
        }

        private ViewHolder viewHolder;


        @Override
        public int getCount() {
            return ListUtils.getSize(uWeightList);
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
                convertView = LayoutInflater.from(JOUWeightAty.this).inflate(R.layout.listitem_job_order_unit, parent, false);
                viewHolder = new ViewHolder();
                x.view().inject(viewHolder, convertView);
                AutoUtils.autoSize(convertView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            if (position == ListUtils.getSize(uWeightList) - 1) {
                viewHolder.vDivid.setVisibility(View.GONE);
            } else {
                viewHolder.vDivid.setVisibility(View.VISIBLE);
            }
            viewHolder.textView.setText(uWeightList.get(position).get("name"));

            return convertView;
        }

        public class ViewHolder {
            @ViewInject(R.id.list_job_order_text)
            TextView textView;
            @ViewInject(R.id.list_unit_divid)
            View vDivid;
        }
    }
}
