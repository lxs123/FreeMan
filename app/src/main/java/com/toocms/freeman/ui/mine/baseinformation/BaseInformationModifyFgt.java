package com.toocms.freeman.ui.mine.baseinformation;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.toocms.frame.image.ImageLoader;
import com.toocms.frame.ui.BaseFragment;
import com.toocms.freeman.R;
import com.toocms.freeman.config.Constants;
import com.toocms.freeman.https.BaiduLbs;
import com.toocms.freeman.https.User;
import com.toocms.freeman.ui.recruitment.LocationAddressAty;
import com.zero.autolayout.utils.AutoUtils;

import org.xutils.http.RequestParams;
import org.xutils.image.ImageOptions;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import java.util.ArrayList;
import java.util.Map;

import cn.zero.android.common.permission.PermissionFail;
import cn.zero.android.common.permission.PermissionSuccess;
import cn.zero.android.common.util.JSONUtils;
import cn.zero.android.common.util.ListUtils;
import cn.zero.android.common.view.shapeimageview.CircularImageView;

import static android.app.Activity.RESULT_OK;


public class BaseInformationModifyFgt extends BaseFragment {

    public static final int BASE_INFORMATION_CODE = 0x1111;

    @ViewInject(R.id.header_cir)
    private CircularImageView headerCir;    //头像
    @ViewInject(R.id.noid_tv)
    private TextView noidTv;    //会员号
    @ViewInject(R.id.nickname_tv)
    private EditText nicknameTv;    //昵称
    @ViewInject(R.id.editText2)
    private EditText codeEdt;   //邀请人编号
    @ViewInject(R.id.role_id_tv)
    private TextView roleIdTv;  //自由人/单位
    @ViewInject(R.id.name_edt)
    private TextView nameEdt;    //真实姓名
    @ViewInject(R.id.name_ishide_cb)
    private CheckBox nameIshideCb;  //是否显示
    @ViewInject(R.id.mobile)
    private EditText modileEdt; //联系电话
    @ViewInject(R.id.address_tv)
    private TextView addressTv; //常驻地点
    @ViewInject(R.id.sex_rg)
    private RadioGroup sexRadioGroud;   //性别
    @ViewInject(R.id.man_rb)
    private RadioButton manRb;  //男
    @ViewInject(R.id.woman_rb)
    private RadioButton womanRb;    //女
    @ViewInject(R.id.height_edt)
    private EditText heightEdt;     //身高
    @ViewInject(R.id.weight_edt)
    private EditText weightEdt;     //体重
    @ViewInject(R.id.organization_edt)
    private EditText organizationEdt;   //单位名称
    @ViewInject(R.id.introduce_edt)
    private EditText introduceEdt;  //自我介绍

    private ImageLoader mImageLoader;
    private String mHead;
    //输入状态检测 (自由人/单位)
    private String roleIdStr = "自由人";
    //检测性别
    private String sexStr;

    //网络相关
    private User mUser;
    //根据百度地图接口得到相关位置信息属性
    private BaiduLbs baiduLbs;
    private String mProvince_name;
    private String mCity_name;
    private String mArea_name;
    private String mLongitude;
    private String mLatitude;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setHasOptionsMenu(true);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mUser.getPerfect(application.getUserInfo().get("noid"), this);//测试
    }

    public void doModifyInterface() {
        Log.e("***", "doModifyInterface方法被调用了");
        if (manRb.isChecked()) {
            sexStr = "1";
        } else if (womanRb.isChecked()) {
            sexStr = "2";
        } else {
            sexStr = "4";
        }

        showProgressDialog();

        mUser.savePerfect(
                application.getUserInfo().get("noid"),
                nicknameTv.getText().toString(),
                roleIdStr.equals("自由人") ? "1" : "2",
                nameEdt.getText().toString(),
                modileEdt.getText().toString(),
                codeEdt.getText().toString(),
                nameIshideCb.isChecked() ? "1" : "2",
                mLongitude,
                mLatitude,
                mProvince_name,
                mCity_name,
                mArea_name,
                addressTv.getText().toString(),
                sexStr,
                heightEdt.getText().toString(),
                weightEdt.getText().toString(),
                organizationEdt.getText().toString(),
                introduceEdt.getText().toString(),
                mHead,
                this
        );
    }

    @Override
    public void onComplete(RequestParams params, String result) {
        if (params.getUri().contains("User/getPerfect")) {
            Map<String, String> dataToMap = JSONUtils.parseDataToMap(result);
            //第一次进入 初始化获取到的基本信息内容
            mImageLoader.disPlay(headerCir, dataToMap.get("head"));
            //把头像地址存进用户信息
            noidTv.setText(dataToMap.get("noid"));
            nicknameTv.setText(dataToMap.get("nickname"));
            codeEdt.setText(dataToMap.get("invite"));
            roleIdTv.setText(dataToMap.get("role_name"));
            roleIdStr = dataToMap.get("role_name");
            nameEdt.setText(dataToMap.get("name"));
            if (dataToMap.get("name_show").equals("1")) {    //  显示姓名
                nameIshideCb.setChecked(true);
            } else {
                nameIshideCb.setChecked(false);
            }
            modileEdt.setText(dataToMap.get("telephone"));
            // TODO: 2017/4/24 常驻地点需要处理
            switch (dataToMap.get("sex_name")) {
                case "男":
                    manRb.setChecked(true);
                    break;
                case "女":
                    womanRb.setChecked(true);
                    break;
                case "未填":
                    manRb.setChecked(false);
                    womanRb.setChecked(false);
            }

            heightEdt.setText(dataToMap.get("bmi").substring(0, dataToMap.get("bmi").indexOf("-")));
            weightEdt.setText(dataToMap.get("bmi").substring(dataToMap.get("bmi").indexOf("-") + 1, dataToMap.get("bmi").length()));
            organizationEdt.setText(dataToMap.get("organization"));
            introduceEdt.setText(dataToMap.get("introduce"));
        }

        if (params.getUri().contains("geocoder/v2")) {
            String data = JSONUtils.parseKeyAndValueToMap(result).get("result");
            String addressComponent = JSONUtils.parseKeyAndValueToMap(data).get("addressComponent");
            Map<String, String> map = JSONUtils.parseKeyAndValueToMap(addressComponent);
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(map.get("province"));
            stringBuilder.deleteCharAt(stringBuilder.length() - 1);
            mProvince_name = stringBuilder.toString();
            StringBuilder sBuild = new StringBuilder();
            sBuild.append(map.get("city"));
            sBuild.deleteCharAt(sBuild.length() - 1);
            mCity_name = sBuild.toString();
            mArea_name = map.get("district");

            //tvArea.setText(map.get("city") + "  " + area_name);
            addressTv.setText(mCity_name + "市" + mArea_name + map.get("street") + map.get("street_number"));
        }

        if (params.getUri().contains("User/savePerfect")) {
            showToast("保存信息成功");

//            BaseInformationIndexAty mActivity = (BaseInformationIndexAty) getActivity();
//            mActivity.isComplete = true;
//            mActivity.isComplete = true;
//            mActivity.replace("0");
        }

        super.onComplete(params, result);
    }

    @Override
    public void onError(Map<String, String> error) {
        showToast("保存信息失败");
//        BaseInformationIndexAty mActivity = (BaseInformationIndexAty) getActivity();
//        mActivity.isComplete = false;
        super.onError(error);
    }

    @Event({R.id.address_ll})
    private void onClick(View view) {
        switch (view.getId()) {
            case R.id.address_ll:
                requestPermissions(Constants.PERMISSIONS_ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION);
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mHead = application.getUserInfo().get("head");
        if (TextUtils.isEmpty(mHead)) {
            Log.e("***", "if：" + mHead);
            headerCir.setImageResource(R.drawable.icon_head);
        } else {
            Log.e("***", "else：" + mHead);
            mImageLoader.disPlay(headerCir, mHead);
        }
    }

    @Event({R.id.modify_ll, R.id.id_card_authentication_ll, R.id.business_license_certification_ll, R.id.other_certificates_ll, R.id.header_cir})
    private void onCLick(View view) {
        switch (view.getId()) {
            case R.id.modify_ll:
                showItemsDialog("", new String[]{"自由人", "单位"}, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (i == 0) {
                            roleIdStr = "自由人";
                            roleIdTv.setText(roleIdStr);
                        } else {
                            roleIdStr = "单位";
                            roleIdTv.setText(roleIdStr);
                        }
                    }
                });
                break;
            case R.id.id_card_authentication_ll:
                //身份证认证
                startActivity(IDcardAuthenticationAty.class, null);
                break;
            case R.id.business_license_certification_ll:
                //营业执照照片
                startActivity(BusinessLicenseCertificationAty.class, null);
                break;
            case R.id.other_certificates_ll:
                //其他证件
                startActivity(OtherCertificatesAty.class, null);
                break;
            case R.id.header_cir:
                //头像相关
                requestPermissions(Constants.PERMISSIONS_WRITE_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE);
                break;
        }
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_base_information_modify_fgt;
    }

    @Override
    protected void initialized() {
        mImageLoader = new ImageLoader();
        ImageOptions options = new ImageOptions.Builder()
                // setSize方法中的参数改成和item布局中图片大小一样
                .setSize(AutoUtils.getPercentWidthSize(172), AutoUtils.getPercentWidthSize(172))
                // 加载图片和加载失败图片方法中默认大小图片修改同上（差不多的也可以）
                .setLoadingDrawableId(R.drawable.icon_head)
                .setFailureDrawableId(R.drawable.icon_head)
                .setFadeIn(true).setImageScaleType(ImageView.ScaleType.CENTER_CROP)
                .setUseMemCache(true).build();
        mImageLoader.setImageOptions(options);

        //初始化接口
        mUser = new User();
        baiduLbs = new BaiduLbs();
    }

    @Override
    protected void requestData() {

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            return;
        }
        switch (requestCode) {
            case com.toocms.frame.config.Constants.SELECT_IMAGE:
                ArrayList<String> list = getSelectImagePath(data);
                if (!ListUtils.isEmpty(list)) {
                    application.setUserInfoItem("head", list.get(0));
                    Log.e("***", "app:" + list.get(0));
                }
                break;
            case BASE_INFORMATION_CODE:
//                editDetail.setText(data.getStringExtra("address"));
                String ress = data.getStringExtra("detail");
                mLongitude = data.getStringExtra("longitude");
                mLatitude = data.getStringExtra("latitude");
                baiduLbs.decompile(mLatitude + "," + mLongitude, this);
                break;
        }
    }

    @PermissionSuccess(requestCode = Constants.PERMISSIONS_WRITE_EXTERNAL_STORAGE)
    public void requestSuccess() {
        startSelectSignImageAty();
    }

    @PermissionSuccess(requestCode = Constants.PERMISSIONS_ACCESS_FINE_LOCATION)
    public void requestAddressSuccess() {
        Intent intent = new Intent();
        intent.setClass(getActivity(), LocationAddressAty.class);
        startActivityForResult(intent, BASE_INFORMATION_CODE);
    }

    @PermissionFail(requestCode = Constants.PERMISSIONS_WRITE_EXTERNAL_STORAGE)
    public void requestFail() {
        showToast("获取权限失败");
    }

    @PermissionFail(requestCode = Constants.PERMISSIONS_ACCESS_FINE_LOCATION)
    public void requestAddressFail() {
        showToast("获取权限失败");
    }

}
