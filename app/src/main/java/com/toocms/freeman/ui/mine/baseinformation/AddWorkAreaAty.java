package com.toocms.freeman.ui.mine.baseinformation;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.toocms.freeman.R;
import com.toocms.freeman.https.Addr;
import com.toocms.freeman.ui.BaseAty;

import org.xutils.http.RequestParams;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import cn.zero.android.common.util.JSONUtils;

public class AddWorkAreaAty extends BaseAty {
    public static final int PROVINCE_ID = 0X0011;
    public static final int CITY_ID = 0X0101;
    public static final int AREA_ID = 0X0110;
    @ViewInject(R.id.add_work_province)
    TextView tvProvince;
    @ViewInject(R.id.add_work_city)
    TextView tvCity;
    @ViewInject(R.id.add_work_area)
    TextView tvArea;
    /**
     * 工作地域 - 添加[territoryInsert]
     *
     * @param noid        用户编号
     * @param province_id 省id
     * @param city_id     市id
     * @param area_id     区id
     */
    private String noid;
    private String province_id;
    private String city_id;
    private String area_id;
    private Addr addr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActionBar.setTitle("添加工作地域");
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.aty_add_work_area;
    }

    @Override
    protected void initialized() {
        addr = new Addr();
    }

    @Override
    protected void requestData() {

    }

    @Event({R.id.add_work_province_click, R.id.add_work_city_click, R.id.add_work_area_click, R.id.add_work_sure})
    private void onViewClicked(View view) {
        Bundle bundle = new Bundle();
        bundle.putString("province_id", province_id);
        bundle.putString("city_id", city_id);
        switch (view.getId()) {
            case R.id.add_work_province_click:
                bundle.putString("flag", "province");
                startActivityForResult(CityListAty.class, bundle, PROVINCE_ID);
                break;
            case R.id.add_work_city_click:
                if (TextUtils.isEmpty(tvProvince.getText().toString())) {
                    showToast("请选择省");
                    return;
                }
                bundle.putString("flag", "city");
                startActivityForResult(CityListAty.class, bundle, CITY_ID);
                break;
            case R.id.add_work_area_click:
                if (TextUtils.isEmpty(tvProvince.getText().toString())) {
                    showToast("请选择省份");
                    return;
                } else if (TextUtils.isEmpty(tvCity.getText().toString())) {
                    showToast("请选择城市");
                    return;
                }
                bundle.putString("flag", "area");
                startActivityForResult(CityListAty.class, bundle, AREA_ID);
                break;
            case R.id.add_work_sure:
                if (TextUtils.isEmpty(tvProvince.getText().toString())) {
                    showToast("请选择省份");
                    return;
                } else if (TextUtils.isEmpty(tvCity.getText().toString())) {
//                    showToast("请选择城市");
//                    return;
                    city_id = "0";
                    area_id = "0";
                } else if (TextUtils.isEmpty(area_id)) {
//                    showToast("请选择区/县");
//                    return;
                    area_id = "0";
                }
                showProgressDialog();
                noid = application.getUserInfo().get("noid");
                addr.territoryInsert(noid, province_id, city_id, area_id, this);
                break;
        }
    }

    @Override
    public void onComplete(RequestParams params, String result) {
        if (params.getUri().contains("Addr/territoryInsert")) {
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            return;
        }
        if (TextUtils.isEmpty(data.getStringExtra("name"))) {
            return;
        }
        switch (requestCode) {
            case PROVINCE_ID:
                province_id = data.getStringExtra("region_id");
                tvProvince.setText(data.getStringExtra("name"));
                tvArea.setText("");
                tvCity.setText("");
                break;
            case CITY_ID:
                city_id = data.getStringExtra("region_id");
                tvCity.setText(data.getStringExtra("name"));
                tvArea.setText("");
                break;
            case AREA_ID:
                area_id = data.getStringExtra("region_id");
                tvArea.setText(data.getStringExtra("name"));
                break;
        }
    }
}
