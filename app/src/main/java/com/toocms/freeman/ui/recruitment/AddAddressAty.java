package com.toocms.freeman.ui.recruitment;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.toocms.freeman.R;
import com.toocms.freeman.config.Constants;
import com.toocms.freeman.https.Addr;
import com.toocms.freeman.https.BaiduLbs;
import com.toocms.freeman.ui.BaseAty;

import org.xutils.common.util.LogUtil;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import java.util.ArrayList;
import java.util.Map;

import cn.zero.android.common.permission.PermissionFail;
import cn.zero.android.common.permission.PermissionSuccess;
import cn.zero.android.common.util.JSONUtils;

/**
 * Created by admin on 2017/4/1.
 */

public class AddAddressAty extends BaseAty {
    public static final int SEARCH_ADD = 0x0010;
    @ViewInject(R.id.address_detail_name)
    EditText editName;
    @ViewInject(R.id.address_detail_phone)
    EditText editPhone;
    @ViewInject(R.id.address_detail)
    EditText editDetail;
    @ViewInject(R.id.address_detail_area)
    TextView tvArea;
    @ViewInject(R.id.address_detail_default)
    TextView tvDefault;
    boolean isDefault = false;
    /**
     * 地址 - 添加[insert]
     *
     * @param noid          用户noid
     * @param longitude     经度
     * @param latitude      纬度
     * @param name          姓名
     * @param mobile        手机号
     * @param province_name 省名称
     * @param city_name     市名称
     * @param area_name     区名称
     * @param ress          详细地址
     * @param def           是否默认，默认时为1 其他值为不默认
     */
    /**
     * 地址 - 修改[edit]
     *
     * @param noid          用户编号
     * @param addr_id       地址id
     * @param name          姓名
     * @param mobile        手机号
     * @param longitude     经度
     * @param latitude      纬度
     * @param province_name 省
     * @param city_name     市
     * @param area_name     区
     * @param ress          详细地址
     * @param def           是否默认
     */
    /**
     * 地址 - 详细信息[single]
     *
     * @param noid    用户编号
     * @param addr_id 地址id
     */
    private String addr_id;
    private String noid;
    private String longitude;
    private String latitude;
    private String name;
    private String mobile;
    private String province_name;//省
    private String city_name;  //市
    private String area_name;  // 区
    private String ress;
    private String def = null;
    private Addr addr;
    private BaiduLbs baiduLbs;
    private String flag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        flag = intent.getStringExtra("flag");
        if (TextUtils.equals(flag, "edit")) {
            mActionBar.setTitle("编辑地址");
            showProgressContent();
            addr_id = getIntent().getStringExtra("id");
            noid = application.getUserInfo().get("noid");
            addr.single(noid, addr_id, this);
        }
        noid = application.getUserInfo().get("noid");

    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected int getLayoutResId() {
        return R.layout.aty_address_detail;
    }

    @Override
    protected void initialized() {
        addr = new Addr();
        baiduLbs = new BaiduLbs();
    }

    @Override
    protected void requestData() {

    }


    @Event({R.id.address_area, R.id.address_detail_default, R.id.address_detail_save})
    private void onViewClicked(View view) {
        name = editName.getText().toString().trim();
        mobile = editPhone.getText().toString().trim();
        ress = editDetail.getText().toString().trim();
        switch (view.getId()) {
            case R.id.address_area:
                requestPermissions(Constants.PERMISSIONS_ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION);
                break;
            case R.id.address_detail_default:
                isDefault = !isDefault;
                if (isDefault) {
                    tvDefault.setCompoundDrawablesWithIntrinsicBounds(R.drawable.btn_select, 0, 0, 0);
                    def = "1";
                } else {
                    tvDefault.setCompoundDrawablesWithIntrinsicBounds(R.drawable.btn_unselect, 0, 0, 0);
                    def = "0";
                }
                break;
            case R.id.address_detail_save:
                if (TextUtils.isEmpty(noid)) {
                    LogUtil.e("请检查application");
                    return;
                } else if (TextUtils.isEmpty(name)) {
                    showToast("请输入联系人姓名");
                    return;
                } else if (TextUtils.isEmpty(mobile)) {
                    showToast("请输入联系人手机号");
                    return;
                } else if (TextUtils.isEmpty(longitude) || TextUtils.isEmpty(latitude) || TextUtils.isEmpty(province_name)) {
                    showToast("请选择所在地区");
                    return;
                }
                showProgressDialog();
                if (TextUtils.equals(flag, "edit")) {
                    addr.edit(noid, addr_id, name, mobile, longitude, latitude, province_name, city_name, area_name, ress, def, this);
                } else {
//                    LogUtil.d(noid + "," + longitude + "," + latitude + "," + name + "," + mobile + "," + province_name + "," + city_name + "," + area_name + "," + ress + "," + def);
                    addr.insert(noid, longitude, latitude, name, mobile, province_name, city_name, area_name, ress, def, this);
                    break;
                }
        }
    }

    @PermissionSuccess(requestCode = Constants.PERMISSIONS_ACCESS_FINE_LOCATION)
    public void requestSuccess() {
        startActivityForResult(LocationAddressAty.class, null, SEARCH_ADD);
    }

    @PermissionFail(requestCode = Constants.PERMISSIONS_ACCESS_FINE_LOCATION)
    public void requestFail() {
        showToast("获取权限失败");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            return;
        }
        switch (requestCode) {
            case SEARCH_ADD:
//                editDetail.setText(data.getStringExtra("address"));
                ress = data.getStringExtra("detail");
                longitude = data.getStringExtra("longitude");
                latitude = data.getStringExtra("latitude");
                baiduLbs.decompile(latitude + "," + longitude, this);
                break;
        }
    }

    @Override
    public void onComplete(RequestParams params, String result) {
        if (params.getUri().contains("geocoder/v2")) {
            String data = JSONUtils.parseKeyAndValueToMap(result).get("result");
            String addressComponent = JSONUtils.parseKeyAndValueToMap(data).get("addressComponent");
            Map<String, String> map = JSONUtils.parseKeyAndValueToMap(addressComponent);
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(map.get("province"));
            stringBuilder.deleteCharAt(stringBuilder.length() - 1);
            province_name = stringBuilder.toString();
            StringBuilder sBuild = new StringBuilder();
            sBuild.append(map.get("city"));
            sBuild.deleteCharAt(sBuild.length() - 1);
            city_name = sBuild.toString();
            area_name = map.get("district");
            tvArea.setText(map.get("city") + "  " + area_name);
            editDetail.setText(city_name + "市" + area_name + map.get("street") + map.get("street_number"));
        } else if (params.getUri().contains("Addr/insert")) {
            showToast(JSONUtils.parseKeyAndValueToMap(result).get("message"));
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    finish();
                }
            }, 1500);
        } else if (params.getUri().contains("Addr/single")) {
            ArrayList<Map<String, String>> mapList = JSONUtils.parseDataToMapList(result);
            Map<String, String> map = mapList.get(0);
            editName.setText(map.get("name"));
            editPhone.setText(map.get("mobile"));
            tvArea.setText(map.get("city_name") + map.get("area_name"));
            editDetail.setText(map.get("city_name") + map.get("area_name") + map.get("ress"));
            longitude = map.get("longitude");
            latitude = map.get("latitude");
            province_name = map.get("province_name");
            city_name = map.get("city_name");
            area_name = map.get("area_name");
            if (TextUtils.equals(map.get("def"), "1")) {
                tvDefault.setCompoundDrawablesWithIntrinsicBounds(R.drawable.btn_select, 0, 0, 0);
                isDefault = true;
            } else {
                tvDefault.setCompoundDrawablesWithIntrinsicBounds(R.drawable.btn_unselect, 0, 0, 0);
                isDefault = false;
            }
        } else if (params.getUri().contains("Addr/edit")) {
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
}
