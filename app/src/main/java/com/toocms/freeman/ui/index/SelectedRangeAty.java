package com.toocms.freeman.ui.index;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.toocms.frame.tool.AppManager;
import com.toocms.freeman.R;
import com.toocms.freeman.https.Hire;
import com.toocms.freeman.ui.BaseAty;
import com.toocms.freeman.ui.recruitment.AreaListAty;
import com.toocms.freeman.ui.recruitment.joborder.ReleaseJOAty;
import com.toocms.freeman.ui.recruitment.myjoborder.JODetailAty;
import com.zero.autolayout.utils.AutoUtils;

import org.xutils.http.RequestParams;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import cn.zero.android.common.util.JSONUtils;
import cn.zero.android.common.view.FButton;

public class SelectedRangeAty extends BaseAty {
    private final int AREA = 0x0002;

    @ViewInject(R.id.range_area_click)
    private LinearLayout rangeAreaClickLl;
    @ViewInject(R.id.range_distance_click)
    private LinearLayout rangeDistanceClickLl;
    @ViewInject(R.id.range_area)
    TextView tvArea;
    @ViewInject(R.id.range_area_content)
    TextView tvAreaContent;
    @ViewInject(R.id.range_distance)
    TextView tvDistance;
    @ViewInject(R.id.range_distance_content)
    TextView tvDistanceContent;
    @ViewInject(R.id.range_seek)
    SeekBar seekBar;
    @ViewInject(R.id.range_seek1)
    TextView tvSeek1;
    @ViewInject(R.id.range_seek2)
    TextView tvSeek2;
    @ViewInject(R.id.range_seek3)
    TextView tvSeek3;
    @ViewInject(R.id.range_seek_cen_spot)
    View vSpot;
    @ViewInject(R.id.range_distance_layout)
    LinearLayout linlayDistance;
    @ViewInject(R.id.range_sure)
    private FButton fBtnSure;
    @ViewInject(R.id.sel_spot_right)
    private View vRight;
    private String province_id;
    private String city_id;
    private String area_id;
    private String distance = "";
    private Hire hire;
    private String ress;
    private String mAreaStr;
    private String ressStr;
    private String distanceStr;

    @Override
    protected int getLayoutResId() {
        return R.layout.aty_selected_range;
    }

    @Override
    protected void initialized() {
        hire = new Hire();
    }

    @Override
    protected void requestData() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        if (TextUtils.equals(getIntent().getStringExtra("flag"), "auto")) {
            mActionBar.setTitle("选定接受范围");
            fBtnSure.setText("发布");
        }
//        if (TextUtils.equals(getIntent().getStringExtra("flag"), "my_jo_in_searchaty")) {
//            rangeAreaClickLl.setEnabled(false);
//            onViewClicked(rangeDistanceClickLl);
//        }
        AutoUtils.autoSize(linlayDistance);

//        mActionBar.setTitle("");
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                vRight.setVisibility(View.VISIBLE);
                if (progress > 25 && progress <= 52) {
                    seekBar.setProgress(50);
                    tvSeek1.setTextColor(Color.parseColor("#FFF27CBE"));
                    tvSeek2.setTextColor(Color.parseColor("#FFF27CBE"));
                    tvSeek3.setTextColor(Color.parseColor("#999999"));
                    vSpot.setVisibility(View.VISIBLE);
                    tvDistanceContent.setText("5Km");
                    distanceStr = "5Km";
                    distance = "5000";
                } else if (progress >= 72) {
                    seekBar.setProgress(100);
                    vRight.setVisibility(View.GONE);
                    tvSeek1.setTextColor(Color.parseColor("#FFF27CBE"));
                    tvSeek2.setTextColor(Color.parseColor("#FFF27CBE"));
                    tvSeek3.setTextColor(Color.parseColor("#FFF27CBE"));
                    vSpot.setVisibility(View.VISIBLE);
                    tvDistanceContent.setText("10Km");
                    distanceStr = "10Km";
                    distance = "10000";
                } else if (progress <= 25) {
                    tvSeek1.setTextColor(Color.parseColor("#FFF27CBE"));
                    tvSeek2.setTextColor(Color.parseColor("#999999"));
                    tvSeek3.setTextColor(Color.parseColor("#999999"));
                    vSpot.setVisibility(View.GONE);
                    seekBar.setProgress(0);
                    distance = "";
                    tvDistanceContent.setText("");
                    distanceStr = "0Km";
                } else if (progress > 52 && progress < 72) {
                    seekBar.setProgress(50);
                    tvSeek1.setTextColor(Color.parseColor("#FFF27CBE"));
                    tvSeek2.setTextColor(Color.parseColor("#FFF27CBE"));
                    tvSeek3.setTextColor(Color.parseColor("#999999"));
                    vSpot.setVisibility(View.VISIBLE);
                    tvDistanceContent.setText("5Km");
                    distance = "5000";
                    distanceStr = "5Km";
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {


            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
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
                    AppManager.getInstance().killActivity(ReleaseJOAty.class);
//                    AppManager.getInstance().killActivity(NewJobOrderAty.class);
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
            case AREA:
                tvAreaContent.setText(data.getStringExtra("str"));
                ressStr = data.getStringExtra("str");
                ress = data.getStringExtra("province_name") + "," +
                        data.getStringExtra("city_name") + "," + data.getStringExtra("name");
                province_id = data.getStringExtra("province_id");
                city_id = data.getStringExtra("city_id");
                area_id = data.getStringExtra("area_id");
                mAreaStr = data.getStringExtra("str");
                break;
        }
    }

    @Event({R.id.range_area_click, R.id.range_distance_click, R.id.range_sure})
    private void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.range_area_click:
                tvArea.setCompoundDrawablesWithIntrinsicBounds(R.drawable.btn_select, 0, 0, 0);
                tvDistance.setCompoundDrawablesWithIntrinsicBounds(R.drawable.btn_unselect, 0, 0, 0);
                linlayDistance.setVisibility(View.GONE);
//                Bundle bundle = new Bundle();
//                bundle.putString("flag", "sel_area");
                startActivityForResult(AreaListAty.class, null, AREA);
                tvDistanceContent.setText("");
                distance = "";
                break;
            case R.id.range_distance_click:
                tvArea.setCompoundDrawablesWithIntrinsicBounds(R.drawable.btn_unselect, 0, 0, 0);
                tvDistance.setCompoundDrawablesWithIntrinsicBounds(R.drawable.btn_select, 0, 0, 0);
                linlayDistance.setVisibility(View.VISIBLE);
                tvAreaContent.setText("");
                province_id = "";
                city_id = "";
                area_id = "";
                break;
            case R.id.range_sure:
                if (TextUtils.equals(getIntent().getStringExtra("flag"), "auto")) {
                    String noid = application.getUserInfo().get("noid");
                    String hire_id = getIntent().getStringExtra("hire_id");

                    if (TextUtils.isEmpty(province_id) && TextUtils.isEmpty(distance)&&TextUtils.isEmpty(ressStr)) {
                        showToast("请选择范围发布");
                        return;
                    }
                    showProgressDialog();
                    hire.publishByAuto(noid, hire_id, province_id, city_id, area_id, distance, this);
                }

                if (TextUtils.equals(getIntent().getStringExtra("flag"), "sel_area")) {
//                    if (TextUtils.isEmpty(province_id) && TextUtils.isEmpty(distance)) {
//                        showToast("请选定范围");
//                        return;
//                    }
                    Intent intent = new Intent();
                    intent.putExtra("province_id", province_id);
                    intent.putExtra("city_id", city_id);
                    intent.putExtra("area_id", area_id);
                    intent.putExtra("ress", ress);
                    intent.putExtra("ressStr", ressStr);
                    intent.putExtra("distanceStr", distanceStr);
                    intent.putExtra("distance", distance);
                    setResult(RESULT_OK, intent);
                    finish();
                }
                if (TextUtils.equals(getIntent().getStringExtra("flag"), "my_jo_in_searchaty")) {
                    Intent intent = new Intent();
//                    distance = tvDistanceContent.getText().toString();
                    intent.putExtra("province_id", province_id);
                    intent.putExtra("city_id", city_id);
                    intent.putExtra("area_id", area_id);
                    intent.putExtra("ress", ress);
                    intent.putExtra("distance", distance);
                    setResult(RESULT_OK, intent);
                    finish();
                }
//
                break;
        }
    }
}
