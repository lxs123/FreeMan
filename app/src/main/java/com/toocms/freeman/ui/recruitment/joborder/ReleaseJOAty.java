package com.toocms.freeman.ui.recruitment.joborder;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import com.toocms.frame.tool.AppManager;
import com.toocms.freeman.R;
import com.toocms.freeman.https.Hire;
import com.toocms.freeman.ui.BaseAty;
import com.toocms.freeman.ui.index.CollectAty;
import com.toocms.freeman.ui.index.ManulSelectLaborAty;
import com.toocms.freeman.ui.index.SelectedRangeAty;
import com.toocms.freeman.ui.recruitment.myjoborder.JODetailAty;

import org.xutils.http.RequestParams;
import org.xutils.view.annotation.Event;

import cn.zero.android.common.util.JSONUtils;

/**
 * 发布招工单页
 * JO  job order缩写
 * Created by admin on 2017/3/23.
 */

public class ReleaseJOAty extends BaseAty {
    private final int RANGE = 0X0001;
    /**
     * 自动发布[publishByAuto]
     *
     * @param noid        用户编号
     * @param hire_id     招工单id
     * @param province_id 省id
     * @param city_id     市id
     * @param area_id     区id
     * @param distance    距离数字，单位为米
     */
    private String noid;
    private String hire_id;
    private String province_id;
    private String city_id;
    private String area_id;
    private String distance;
    private Hire hire;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActionBar.setTitle("发布招工单");

    }


    @Override
    protected int getLayoutResId() {
        return R.layout.aty_release_jo;
    }

    @Override
    protected void initialized() {
        hire = new Hire();
    }

    @Override
    protected void requestData() {
    }

    @Override
    public void onComplete(RequestParams params, String result) {
        if (params.getUri().contains("Hire/publishByAuto")) {
            showToast(JSONUtils.parseKeyAndValueToMap(result).get("message"));
            JODetailAty.isRelease = true;
            JODetailAty.hire_noid = JSONUtils.parseKeyAndValueToMap(result).get("data");
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    AppManager.getInstance().killActivity(NewJobOrderAty.class);
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
        switch (requestCode) {
            case RANGE:

                break;
        }
    }

    //按钮监听
    @Event({R.id.manul_select_labor_tv, R.id.collect_tv, R.id.release_jo_auto_sel})
    private void onClick(View view) {
        final Bundle bundle = new Bundle();
        bundle.putString("hire_id", getIntent().getStringExtra("hire_id"));
        switch (view.getId()) {
            case R.id.manul_select_labor_tv:
                startActivity(ManulSelectLaborAty.class, bundle);
//                finish();
                break;
            case R.id.collect_tv:

                startActivity(CollectAty.class, bundle);
//                finish();
                break;
            case R.id.release_jo_auto_sel:
                showDialog("", "是否选定接受范围？", "选定范围", "直接发布", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        bundle.putString("flag", "auto");
                        startActivity(SelectedRangeAty.class, bundle);
//                        finish();
                    }
                }, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        showProgressDialog();
                        noid = application.getUserInfo().get("noid");
                        hire_id = getIntent().getStringExtra("hire_id");
                        hire.publishByAuto(noid, hire_id, province_id, city_id, area_id, distance, ReleaseJOAty.this);
                    }
                });
                break;
        }
    }
}
