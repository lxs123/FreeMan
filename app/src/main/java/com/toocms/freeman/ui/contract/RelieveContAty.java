package com.toocms.freeman.ui.contract;

import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.Selection;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;

import com.toocms.freeman.R;
import com.toocms.freeman.https.Contract;
import com.toocms.freeman.ui.BaseAty;

import org.xutils.http.RequestParams;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cn.zero.android.common.util.JSONUtils;

/**
 * 解除合同
 * Created by admin on 2017/3/28.
 */

public class RelieveContAty extends BaseAty {
    @ViewInject(R.id.relieve_grid)
    private GridView gridView;
    @ViewInject(R.id.relieve_reason)
    private EditText editReason;
    private List<Map<String, String>> list = new ArrayList<>();
    private GridAdapter adapter;
    private String[] relieveStr = new String[]{"质量不过关", "工作时间严重延期", "突然不想合作", "对方态度恶劣", "合同出现纠纷", "实物有破损"};
    private int position = 7;
    private Contract contract;
    /**
     * 资方 - 发起解除[capProgressStay]
     *
     * @param contract_noid 合同单号
     * @param noid          用户id
     * @param refuse        解除原因 80字
     */
    private String contract_noid;
    private String noid;
    private String refuse;

//    {
//        for (int i = 0; i < 6; i++) {
//            Map<String, String> map = new HashMap<>();
//            map.put("isSelect", "0");
//            list.add(map);
//        }
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adapter = new GridAdapter();
        gridView.setAdapter(adapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                adapter.selItem(position);
            }
        });
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.aty_relieve_cont;
    }

    @Override
    protected void initialized() {
        contract = new Contract();
    }

    @Override
    protected void requestData() {

    }

    @Override
    public void onComplete(RequestParams params, String result) {
        if (params.getUri().contains("Contract/capProgressStay") || params.getUri().contains("Contract/capStay")) {
            showToast(JSONUtils.parseKeyAndValueToMap(result).get("message"));
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    finish();
                }
            }, 1500);
        } else if (params.getUri().contains("Contract/labStay") || params.getUri().contains("Contract/labProgressStay")) {
            showToast(JSONUtils.parseKeyAndValueToMap(result).get("message"));
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    finish();
                }
            }, 1500);
        }
        super.onComplete(params, result);
    }

    @Event({R.id.relieve_fbtn_commit})
    private void onClick(View view) {
        switch (view.getId()) {
            case R.id.relieve_fbtn_commit:
                refuse = editReason.getText().toString().trim();
                if (TextUtils.isEmpty(refuse) || position > 6) {
                    showToast("请选择您的解除合同原因");
                    return;
                }
                showProgressDialog();
                contract_noid = getIntent().getStringExtra("contract_noid");
                noid = application.getUserInfo().get("noid");

                if (TextUtils.equals(getIntent().getStringExtra("flag"), "progress")) {
                    if (TextUtils.equals(getIntent().getStringExtra("status"), "cap"))
                        contract.capProgressStay(contract_noid, noid, refuse, this);
                    else
                        contract.labProgressStay(contract_noid, noid, refuse, this);
                } else {
                    if (TextUtils.equals(getIntent().getStringExtra("status"), "cap"))
                        contract.capStay(contract_noid, noid, refuse, this);
                    else
                        contract.labStay(contract_noid, noid, refuse, this);
                }

//                else if (Commonly.getViewText(editReason).isEmpty()){
//                    showToast("请描述您的");
//                }
                break;
        }

    }

    private class GridAdapter extends BaseAdapter {
        private GridAdapter.ViewHolder viewHolder;
        boolean[] boo;


        public GridAdapter() {
            boo = new boolean[]{false, false, false, false, false, false};
        }

        private void selItem(int anInt) {
//            if (boo[anInt]) {
            for (int i = 0; i < boo.length; i++) {
                if (anInt == i) {
                    boo[i] = true;
                    position = i;
                    editReason.setText(relieveStr[position]);
                    Editable editReasonText = editReason.getText();
                    Selection.setSelection(editReasonText, editReasonText.length());
                } else
                    boo[i] = false;
            }
//            } else {
//            for (int i = 0; i < boo.length; i++) {
//                if (anInt == i) {
//                    boo[anInt] = true;
//                } else {
//                    boo[i] = false;
//                }
//            }

//            }
            notifyDataSetChanged();

        }

        @Override
        public int getCount() {
            return 6;
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
                convertView = LayoutInflater.from(getBaseContext()).inflate(R.layout.listitem_relieve_cont, null, false);
                viewHolder = new GridAdapter.ViewHolder();
                x.view().inject(viewHolder, convertView);
//                AutoUtils.auto(convertView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (GridAdapter.ViewHolder) convertView.getTag();
            }
//            String isSelect = list.get(position).get("isSelect");
            viewHolder.tvCause.setText(relieveStr[position]);
            if (boo[position]) {
                viewHolder.tvCause.setCompoundDrawablesWithIntrinsicBounds(R.drawable.btn_select, 0, 0, 0);
            } else {
                viewHolder.tvCause.setCompoundDrawablesWithIntrinsicBounds(R.drawable.btn_unselect, 0, 0, 0);
            }
            return convertView;
        }

        public class ViewHolder {
            @ViewInject(R.id.list_relieve_cause)
            private TextView tvCause;
        }
    }
}