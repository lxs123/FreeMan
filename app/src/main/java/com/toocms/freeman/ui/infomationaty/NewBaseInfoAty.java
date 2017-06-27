package com.toocms.freeman.ui.infomationaty;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.toocms.frame.image.ImageLoader;
import com.toocms.frame.tool.Commonly;
import com.toocms.freeman.R;
import com.toocms.freeman.config.Constants;
import com.toocms.freeman.https.BaiduLbs;
import com.toocms.freeman.https.User;
import com.toocms.freeman.ui.BaseAty;
import com.toocms.freeman.ui.index.IndexAty;
import com.toocms.freeman.ui.mine.baseinformation.BusinessLicenseCertificationAty;
import com.toocms.freeman.ui.mine.baseinformation.IDcardAuthenticationAty;
import com.toocms.freeman.ui.mine.baseinformation.OtherCertificatesAty;
import com.toocms.freeman.ui.recruitment.LocationAddressAty;
import com.uuzuche.lib_zxing.activity.CaptureActivity;
import com.uuzuche.lib_zxing.activity.CodeUtils;
import com.uuzuche.lib_zxing.activity.ZXingLibrary;
import com.zero.autolayout.utils.AutoUtils;

import org.xutils.common.Callback;
import org.xutils.common.util.LogUtil;
import org.xutils.http.RequestParams;
import org.xutils.image.ImageOptions;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

import cn.zero.android.common.permission.PermissionFail;
import cn.zero.android.common.permission.PermissionSuccess;
import cn.zero.android.common.util.JSONUtils;
import cn.zero.android.common.util.ListUtils;
import cn.zero.android.common.view.shapeimageview.CircularImageView;

import static com.toocms.freeman.ui.lar.RegisterPswAty.REQUEST_CODE;

public class NewBaseInfoAty extends BaseAty {

    public static final int BASE_INFORMATION_CODE = 0x1111;//搜索地址请求码

    //=====================全局=================================
    @ViewInject(R.id.complete_layout)
    FrameLayout completeLayout;
    @ViewInject(R.id.modify_layout)
    FrameLayout modifyLayout;
    private boolean isComplete = true;  //第一次进入时，是开始状态
    ImageLoader mImageLoader;
    //=========================================================

    //=====================接口================================
    private User mUser;
    private Map<String, String> mMUersViewMap;  //获取基本信息接口数据
    //=========================================================

    //======================完成页面相关View=====================
    @ViewInject(R.id.head)
    private CircularImageView headCir;//头像
    @ViewInject(R.id.noid)
    private TextView noidTv;//会员号
    @ViewInject(R.id.nickname)
    private TextView nicknameTv;//昵称
    @ViewInject(R.id.invite)
    private TextView inviteTv;//邀请人编号
    @ViewInject(R.id.role_name)
    private TextView roleNameTv;//自由人/单位
    @ViewInject(R.id.name)
    private TextView nameTv;//真实姓名
    @ViewInject(R.id.telephone)
    private TextView telephonetv;//联系电话
    @ViewInject(R.id.province_name)
    private TextView provinceNameTv;//常驻地址
    @ViewInject(R.id.sex_name)
    private TextView sexnameTv;//性别
    @ViewInject(R.id.bmi)
    private TextView bmiTv;//身高体重
    @ViewInject(R.id.organization)
    private TextView organizationTv;//单位名称
    @ViewInject(R.id.ident_status)
    private TextView identStatusTv;//身份证认证
    @ViewInject(R.id.business_status)
    private TextView businessStatusTv;//营业执照照片
    @ViewInject(R.id.others_status)
    private TextView othersStatusTv;//其他证件
    @ViewInject(R.id.base_info_comp_other_text)
    TextView tvOther;
    private Map<String, String> mUersViewMap;
    //=========================================================

    //======================修改页面相关View=====================
    @ViewInject(R.id.header_cir)
    private CircularImageView headerCir;    //头像
    @ViewInject(R.id.noid_tv)
    private TextView noidTvMtf;    //会员号
    @ViewInject(R.id.nickname_tv)
    private EditText nicknameTvMtf;    //昵称
    @ViewInject(R.id.editText2)
    private EditText codeEdt;   //邀请人编号
    @ViewInject(R.id.info_modify_codea_tag_content)
    private TextView tvCodeTag;
    @ViewInject(R.id.info_modify_code_content)
    private LinearLayout linlayCode;
    @ViewInject(R.id.sys_img)
    private ImageView sysImg;   //扫码按钮
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
    //百度地图相关返回值
    private BaiduLbs mBaiduLbs;
    private String mProvince_name;
    private String mCity_name;
    private String mArea_name;
    private String mlongitude;
    private String mLatitude;
    //检测性别
    private String sexStr;
    private String head;
    private MenuItem menuItem;
    //=========================================================

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //扫描初始化
        ZXingLibrary.initDisplayOpinion(this);

        completeLayout.setVisibility(View.VISIBLE);
        modifyLayout.setVisibility(View.GONE);
        showProgressDialog();
        mActionBar.setTitle("个人信息");
        if (getIntent().getStringExtra("flag") != null && getIntent().getStringExtra("flag").equals("wzw1")) {
            mUser.getPerfect(getIntent().getStringExtra("noid"), this);
        } else {
            mUser.getPerfect(application.getUserInfo().get("noid"), this);
        }
        LogUtil.e(application.getUserInfo().toString());
//        heightEdt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
//            @Override
//            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
//                if (actionId == EditorInfo.IME_ACTION_NEXT) {
//                    LogUtil.e("////////////////////////////////////191");
//                    heightEdt.setNextFocusForwardId(R.id.weight_edt);
//                    return true;
//                }
//                return false;
//            }
//        });

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (TextUtils.equals(getIntent().getStringExtra("flag"), "wzw1"))
            return super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_edit, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (TextUtils.equals(getIntent().getStringExtra("flag"), "wzw1"))
            return super.onPrepareOptionsMenu(menu);
        menuItem = menu.findItem(R.id.menu_edit);
        if (TextUtils.equals(getIntent().getStringExtra("flag"), "info")) {
            isComplete = false;
            completeLayout.setVisibility(View.GONE);
            modifyLayout.setVisibility(View.VISIBLE);
            menuItem.setTitle("完成");
            showProgressDialog();
            mUser.getPerfect(application.getUserInfo().get("noid"), this);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
//        if (TextUtils.equals(getIntent().getStringExtra("flag"), "wzw1"))
//            return super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
            case R.id.menu_edit:
                if (isComplete) {
                    isComplete = false;
                    completeLayout.setVisibility(View.GONE);
                    modifyLayout.setVisibility(View.VISIBLE);
                    item.setTitle("完成");
                    if (TextUtils.equals(mUersViewMap.get("isInvite"), "0")) {
                        linlayCode.setVisibility(View.GONE);
                        tvCodeTag.setVisibility(View.GONE);
                    } else {
                        linlayCode.setVisibility(View.VISIBLE);
                        tvCodeTag.setVisibility(View.VISIBLE);
                    }
                    showProgressDialog();
                    mUser.getPerfect(application.getUserInfo().get("noid"), this);
                } else {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (imm.isActive() && getCurrentFocus() != null) {
                        if (getCurrentFocus().getWindowToken() != null) {
                            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                        }
                    }

                    //调接口 保存修改信息
                    showProgressDialog();
                    if (!TextUtils.equals(roleIdTv.getText().toString(), "自由人")) {
                        if (Commonly.getViewText(organizationEdt).isEmpty()) {
                            removeProgressDialog();
                            showToast("请填写单位名称!!!");
                            break;
                        }
                    }
                    head = application.getUserInfo().get("head");

                    if (head.contains("http")) {
                        mImageLoader.loadFile(head, new Callback.CacheCallback<File>() {

                            private File tempFile;

                            @Override
                            public boolean onCache(RequestParams requestParams, File file) {
                                try {
                                    String name = file.getName();
                                    String replace = file.getPath().replace(name, "");
                                    tempFile = File.createTempFile(file.getName(), ".png", new File(replace));
//                                    new File()
                                    Bitmap bmp = BitmapFactory.decodeFile(file.getPath());
                                    FileOutputStream fileOutStream = null;
                                    fileOutStream = new FileOutputStream(tempFile);
                                    bmp.compress(Bitmap.CompressFormat.JPEG, 100, fileOutStream);  //把位图输出到指定的文件中
                                    fileOutStream.flush();
                                    fileOutStream.close();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                head = tempFile.getAbsolutePath();
                                mUser.savePerfect(application.getUserInfo().get("noid"),
                                        nicknameTvMtf.getText().toString(),
                                        roleIdTv.getText().toString().equals("自由人") ? "1" : "2",
                                        nameEdt.getText().toString(),
                                        modileEdt.getText().toString(),
                                        codeEdt.getText().toString(),
                                        nameIshideCb.isChecked() ? "1" : "2",
                                        mlongitude,
                                        mLatitude,
                                        mProvince_name,
                                        mCity_name,
                                        mArea_name,
                                        addressTv.getText().toString(),
                                        judgeSex(),
                                        heightEdt.getText().toString(),
                                        weightEdt.getText().toString(),
                                        organizationEdt.getText().toString(),
                                        introduceEdt.getText().toString(),
                                        head,
                                        NewBaseInfoAty.this);
                                return false;
                            }

                            @Override
                            public void onSuccess(RequestParams requestParams, File file) {

                            }

                            @Override
                            public void onError(Throwable throwable, boolean b) {

                            }

                            @Override
                            public void onCancelled(CancelledException e) {

                            }

                            @Override
                            public void onFinished() {

                            }
                        });

                    } else {
                        mUser.savePerfect(application.getUserInfo().get("noid"),
                                nicknameTvMtf.getText().toString(),
                                roleIdTv.getText().toString().equals("自由人") ? "1" : "2",
                                nameEdt.getText().toString(),
                                modileEdt.getText().toString(),
                                codeEdt.getText().toString(),
                                nameIshideCb.isChecked() ? "1" : "2",
                                mlongitude,
                                mLatitude,
                                mProvince_name,
                                mCity_name,
                                mArea_name,
                                addressTv.getText().toString(),
                                judgeSex(),
                                heightEdt.getText().toString(),
                                weightEdt.getText().toString(),
                                organizationEdt.getText().toString(),
                                introduceEdt.getText().toString(),
                                head,
                                this);
                    }
//                    item.setTitle("修改");


                }
                break;
            case 16908332:
                if (TextUtils.equals(getIntent().getStringExtra("flag"), "info")) {
                    startActivity(IndexAty.class, null);
                } else {
                    this.finish();
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Event({R.id.address_ll, R.id.header_cir, R.id.modify_ll, R.id.id_card_authentication_ll, R.id.business_license_certification_ll, R.id.other_certificates_ll, R.id.sys_img})
    private void onClick(View view) {
        switch (view.getId()) {
            case R.id.address_ll:
                requestPermissions(Constants.PERMISSIONS_ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION);
                break;
            case R.id.header_cir:
                requestPermissions(Constants.PERMISSIONS_WRITE_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE);
                break;
            case R.id.modify_ll:
                showItemsDialog("", new String[]{"自由人", "单位"}, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (i == 0) {
                            roleIdTv.setText("自由人");

                        } else {
                            roleIdTv.setText("单位");

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
            case R.id.sys_img:
                requestPermissions(Constants.PERMISSIONS_CAMERA, Manifest.permission.CAMERA);
                break;
        }
    }


    @Override
    public void onComplete(RequestParams params, String result) {
        if (params.getUri().contains("User/getPerfect")) {
            Log.e("***", result);
            mUersViewMap = JSONUtils.parseDataToMap(result);
            upDateUI();
        }
        if (params.getUri().contains("geocoder/v2")) {   //搜索地址接口
            String data = JSONUtils.parseKeyAndValueToMap(result).get("result");
            String addressComponent = JSONUtils.parseKeyAndValueToMap(data).get("addressComponent");
            Map<String, String> map = JSONUtils.parseKeyAndValueToMap(addressComponent);
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(map.get("province"));
            stringBuilder.deleteCharAt(stringBuilder.length() - 1);
            mProvince_name = stringBuilder.toString();
            StringBuilder sBuilde = new StringBuilder();
            sBuilde.append(map.get("city"));
            sBuilde.deleteCharAt(sBuilde.length() - 1);
            mCity_name = sBuilde.toString();
            mArea_name = map.get("district");
            addressTv.setText(mCity_name + "市" + mArea_name + map.get("street") + map.get("street_number"));
        }
        if (params.getUri().contains("User/savePerfect")) {
            showToast("保存信息成功");
            showProgressDialog();

            isComplete = true;
            completeLayout.setVisibility(View.VISIBLE);
            modifyLayout.setVisibility(View.GONE);
            menuItem.setTitle("修改");
            mUser.getPerfect(application.getUserInfo().get("noid"), this);
        }
        super.onComplete(params, result);
    }

    @Override
    public void onError(Map<String, String> error) {
        LogUtil.e(error.toString());
        super.onError(error);
    }

    private void upDateUI() {
        if (isComplete) {
            mImageLoader.disPlay(headCir, mUersViewMap.get("head"));
            noidTv.setText(mUersViewMap.get("noid"));
            nicknameTv.setText(mUersViewMap.get("nickname"));
            //当 会员noid与application里的noid相等时再修改
            if (TextUtils.equals(mUersViewMap.get("noid"), application.getUserInfo().get("noid"))) {
                application.setUserInfoItem("nickname", mUersViewMap.get("nickname"));
                application.setUserInfoItem("name", mUersViewMap.get("name"));
            }

            inviteTv.setText(mUersViewMap.get("invite"));
            roleNameTv.setText(mUersViewMap.get("role_name"));

            if (mUersViewMap.get("name_show").equals("1")) {
                nameTv.setText(mUersViewMap.get("name"));
            } else {
                nameTv.setText("***");
            }
            if (TextUtils.equals(getIntent().getStringExtra("flag"), "wzw1")) {
                if (!TextUtils.isEmpty(mUersViewMap.get("telephone")))
                    if (mUersViewMap.get("telephone").length() == 11)
                        telephonetv.setText(mUersViewMap.get("telephone").substring(0, 3) + "****" + mUersViewMap.get("telephone").substring(7, 11));
                    else {
                        telephonetv.setText("该手机号异常");
                    }
                else {
                    telephonetv.setText("无");
                }
            } else {
                telephonetv.setText(mUersViewMap.get("telephone"));
            }

            provinceNameTv.setText(mUersViewMap.get("province_name"));
            sexnameTv.setText(mUersViewMap.get("sex_name"));
            bmiTv.setText(mUersViewMap.get("bmi").substring(0, mUersViewMap.get("bmi").indexOf("-")) + "cm  "
                    + mUersViewMap.get("bmi").substring(mUersViewMap.get("bmi").indexOf("-") + 1, mUersViewMap.get("bmi").length()) + "kg");
            organizationTv.setText(mUersViewMap.get("organization"));
            identStatusTv.setTextColor(Color.parseColor("#656565"));
            // TODO: 2017/4/24  对四种状态做处理
            switch (JSONUtils.parseKeyAndValueToMap(mUersViewMap.get("ident")).get("status")) {
                case "0":
                    identStatusTv.setText("未上传");
                    identStatusTv.setTextColor(Color.parseColor("#F97A4E"));
                    identStatusTv.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_none_select, 0, 0, 0);
                    break;
                case "1":
                    identStatusTv.setText("已上传");
                    identStatusTv.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_sucess, 0, 0, 0);
                    break;
//                case "2":
//                    identStatusTv.setText("已认证");
//                    break;
//                case "3":
//                    identStatusTv.setText("认证失败");
//                    break;
//                case "4":
//                    identStatusTv.setText("删除");
//                    break;
            }
            businessStatusTv.setTextColor(Color.parseColor("#656565"));
            switch (JSONUtils.parseKeyAndValueToMap(mUersViewMap.get("business")).get("status")) {
                case "0":
                    businessStatusTv.setText("未上传");
                    businessStatusTv.setTextColor(Color.parseColor("#F97A4E"));
                    businessStatusTv.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_none_select, 0, 0, 0);
                    break;
                case "1":
                    businessStatusTv.setText("已上传");
                    businessStatusTv.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_sucess, 0, 0, 0);
                    break;
//                case "2":
//                    businessStatusTv.setText("已认证");
//                    break;
//                case "3":
//                    businessStatusTv.setText("认证失败");
//                    break;
//                case "4":
//                    businessStatusTv.setText("删除");
//                    break;
            }
            othersStatusTv.setTextColor(Color.parseColor("#656565"));
            switch (JSONUtils.parseKeyAndValueToMap(mUersViewMap.get("others")).get("status")) {
                case "0":
                    othersStatusTv.setText("未上传");
                    othersStatusTv.setTextColor(Color.parseColor("#F97A4E"));
                    othersStatusTv.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_none_select, 0, 0, 0);
                    break;
                case "1":
                    othersStatusTv.setText("已上传");
                    othersStatusTv.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_sucess, 0, 0, 0);
                    break;
//                case "2":
//                    othersStatusTv.setText("已认证");
//                    break;
//                case "3":
//                    othersStatusTv.setText("认证失败");
//                    break;
//                case "4":
//                    othersStatusTv.setText("删除");
//                    break;
            }
            tvOther.setText(mUersViewMap.get("introduce"));
        }

        if (!isComplete) {
            mImageLoader.disPlay(headerCir, mUersViewMap.get("head"));
            //把头像地址存进用户信息
            noidTvMtf.setText(mUersViewMap.get("noid"));
            nicknameTvMtf.setText(mUersViewMap.get("nickname"));

            codeEdt.setText(mUersViewMap.get("invite"));
            roleIdTv.setText(mUersViewMap.get("role_name"));
            //    roleIdStr = mUersViewMap.get("role_name");
            nameEdt.setText(mUersViewMap.get("name"));
            if (TextUtils.equals(mUersViewMap.get("isInvite"), "0")) {
                linlayCode.setVisibility(View.GONE);
                tvCodeTag.setVisibility(View.GONE);
            } else {
                linlayCode.setVisibility(View.VISIBLE);
                tvCodeTag.setVisibility(View.VISIBLE);
            }
            addressTv.setText(mUersViewMap.get("ress"));
            mProvince_name = mUersViewMap.get("province_name");
            mCity_name = mUersViewMap.get("city_name");
            mArea_name = mUersViewMap.get("area_name");
            if (mUersViewMap.get("name_show").equals("1")) {    //  显示姓名
                nameIshideCb.setChecked(true);
            } else {
                nameIshideCb.setChecked(false);
            }
            modileEdt.setText(mUersViewMap.get("telephone"));
            // TODO: 2017/4/24 常驻地点需要处理
            switch (mUersViewMap.get("sex_name")) {
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

            heightEdt.setText(mUersViewMap.get("bmi").substring(0, mUersViewMap.get("bmi").indexOf("-")));
            weightEdt.setText(mUersViewMap.get("bmi").substring(mUersViewMap.get("bmi").indexOf("-") + 1, mUersViewMap.get("bmi").length()));
            if (TextUtils.equals(weightEdt.getText().toString(), "0")) {
                weightEdt.setText("");
            }
            heightEdt.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//                    if (s.toString().contains("0")) {
//                        int indexOf = s.toString().indexOf("0");
//                        if (indexOf == 0) {
//                            s = "";
//                            weightEdt.setText(s);
//                            weightEdt.setSelection(0);
//                        }
//
//                    }
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (s.toString().contains("0")) {
                        int indexOf = s.toString().indexOf("0");
                        if (indexOf == 0) {
                            if (s.length() > 1) {
                                s = s.toString().substring(1, s.length());
                            } else {
                                s = "";
                            }

                            heightEdt.setText(s);
                            heightEdt.setSelection(0);
                        }

                    }
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
            weightEdt.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//                    if (s.toString().contains("0")) {
//                        int indexOf = s.toString().indexOf("0");
//                        if (indexOf == 0) {
//                            s = "";
//                            weightEdt.setText(s);
//                            weightEdt.setSelection(0);
//                        }
//
//                    }
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (s.toString().contains("0")) {
                        int indexOf = s.toString().indexOf("0");
                        if (indexOf == 0) {
                            if (s.length() > 1) {
                                s = s.toString().substring(1, s.length());
                            } else {
                                s = "";
                            }

                            weightEdt.setText(s);
                            weightEdt.setSelection(0);
                        }

                    }
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
            organizationEdt.setText(mUersViewMap.get("organization"));
            introduceEdt.setText(mUersViewMap.get("introduce"));
        }

    }

    //    监听返回键
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            if (TextUtils.equals(getIntent().getStringExtra("flag"), "info")) {
                startActivity(IndexAty.class, null);
                return true;
            } else {
                finish();
                return false;
            }

        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) return;
        switch (requestCode) {
            case BASE_INFORMATION_CODE:
                String ress = data.getStringExtra("detail");
                mlongitude = data.getStringExtra("longitude");
                mLatitude = data.getStringExtra("latitude");
                mBaiduLbs.decompile(mLatitude + "," + mlongitude, this);
                break;
            case com.toocms.frame.config.Constants.SELECT_IMAGE:
                ArrayList<String> list = getSelectImagePath(data);
                if (!ListUtils.isEmpty(list)) {
                    application.setUserInfoItem("head", list.get(0));
                }
                mImageLoader.disPlay(headerCir, application.getUserInfo().get("head"));
                break;
            case REQUEST_CODE:
                if (data != null) {
                    Bundle bundle = data.getExtras();
                    if (bundle == null) return;
                    if (bundle.getInt(CodeUtils.RESULT_TYPE) == CodeUtils.RESULT_SUCCESS) {
                        String result = bundle.getString(CodeUtils.RESULT_STRING);
                        if (result.contains("http://zyr-api.toocms.com?")) {
                            int indexOf = result.lastIndexOf("=");
                            if (indexOf < result.length()) {
                                String substring = result.substring(indexOf + 1, result.length());
                                Log.e("***", substring);
                                codeEdt.setText(substring);

                            }
                        } else if (bundle.getInt(CodeUtils.RESULT_TYPE) == CodeUtils.RESULT_FAILED) {
                            showToast("取消扫码！");
                        }
                    }
                }
                break;
        }
    }

    @PermissionSuccess(requestCode = Constants.PERMISSIONS_ACCESS_FINE_LOCATION)
    public void requestAddressSuccess() {
        Intent intent = new Intent();
        intent.setClass(NewBaseInfoAty.this, LocationAddressAty.class);
        startActivityForResult(intent, BASE_INFORMATION_CODE);
    }


    @PermissionSuccess(requestCode = Constants.PERMISSIONS_CAMERA)
    public void requestSysSuccess() {
        Intent intent = new Intent(NewBaseInfoAty.this, CaptureActivity.class);
        startActivityForResult(intent, REQUEST_CODE);
    }

    @PermissionSuccess(requestCode = Constants.PERMISSIONS_WRITE_EXTERNAL_STORAGE)
    public void requestHeadCirSuccess() {
        startSelectSignImageAty();
    }

    @PermissionFail(requestCode = Constants.PERMISSIONS_WRITE_EXTERNAL_STORAGE)
    public void requestHeadCirFail() {
        showToast("获取权限失败");
    }

    @PermissionFail(requestCode = Constants.PERMISSIONS_ACCESS_FINE_LOCATION)
    public void requestAddressFail() {
        showToast("获取权限失败");
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.aty_new_base_info;
    }

    @Override
    protected void initialized() {
        mUser = new User();
        mBaiduLbs = new BaiduLbs();
        //图片处理
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
    }

    @Override
    protected void requestData() {

    }

    private String judgeSex() {
        if (manRb.isChecked()) {
            sexStr = "1";
        } else if (womanRb.isChecked()) {
            sexStr = "2";
        } else {
            sexStr = "4";
        }
        return sexStr;
    }
}
