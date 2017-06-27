package com.toocms.freeman.ui.recruitment.joborder;

import android.content.Intent;
import android.os.Bundle;
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
import java.util.Map;

import cn.zero.android.common.util.JSONUtils;
import cn.zero.android.common.util.ListUtils;
import cn.zero.android.common.view.linearlistview.LinearListView;

/**
 * 新建工作订单--单位页
 * Created by admin on 2017/3/23.
 */

public class JobOrderUnitAty extends BaseAty {
    public final int UNIT_ID = 0X0010;
    @ViewInject(R.id.job_order_unit_list)
    private LinearListView unitListview;
    private ArrayList<Map<String, String>> unitList = new ArrayList<>();
    String[] unit = new String[]{"时间", "数量", "重量"};
    /**
     * 单位三级列表[getUnitList]
     *
     * @param skill_id 上级ID，顶级为0
     * @param layer    层级数，顶级为0
     */
    private Sys sys;
    private String flag;
    private ArrayList<Map<String, String>> list;
    private Intent intent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActionBar.setTitle("单位");
        unitListview.setOnItemClickListener(new LinearListView.OnItemClickListener() {
            @Override
            public void onItemClick(LinearListView linearListView, View view, int i, long l) {
//                if (ListUtils.isEmpty(list)) {
//                    Intent intent = new Intent();
//                    intent.putExtra("unit_id", unitList.get(i).get("unit_id"));
//                    intent.putExtra("name", unitList.get(i).get("name"));
//                    setResult(RESULT_OK, intent);
//                    finish();
//                }else {
                    Bundle bundle = new Bundle();
                    bundle.putString("name", unitList.get(i).get("name"));
                    bundle.putString("unit_id", unitList.get(i).get("unit_id"));
                    startActivityForResult(JOUWeightAty.class, bundle, UNIT_ID);
//                }



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
        showProgressContent();
        sys.getUnitList("0", "0", this);
    }

    @Override
    public void onComplete(RequestParams params, String result) {
        if (params.getUri().contains("Sys/getUnitList")) {
            if (TextUtils.equals(flag, "flag")) {
                list = JSONUtils.parseDataToMapList(result);
//                flag = "1";

            } else {
                unitList = JSONUtils.parseDataToMapList(result);
                flag = "flag";
                unitListview.setAdapter(new UnitAdapter());
            }
        }
        super.onComplete(params, result);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            return;
        }
        switch (requestCode) {
            case UNIT_ID:
                setResult(RESULT_OK, data);
                finish();
                break;

        }

    }

    private class UnitAdapter extends BaseAdapter {

        private ViewHolder viewHolder;

        @Override
        public int getCount() {
            return ListUtils.getSize(unitList);
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
                convertView = LayoutInflater.from(JobOrderUnitAty.this).inflate(R.layout.listitem_job_order_unit, parent, false);
                viewHolder = new ViewHolder();
                x.view().inject(viewHolder, convertView);
                AutoUtils.autoSize(convertView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            if (position == ListUtils.getSize(unitList) - 1) {
                viewHolder.vDivid.setVisibility(View.GONE);
            } else {
                viewHolder.vDivid.setVisibility(View.VISIBLE);
            }
            viewHolder.textView.setText(unitList.get(position).get("name"));
            sys.getUnitList(unitList.get(position).get("unit_id"), "1", JobOrderUnitAty.this);

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
