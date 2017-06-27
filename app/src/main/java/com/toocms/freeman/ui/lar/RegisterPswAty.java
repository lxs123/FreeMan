package com.toocms.freeman.ui.lar;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.toocms.frame.config.Config;
import com.toocms.freeman.R;
import com.toocms.freeman.config.Constants;
import com.toocms.freeman.https.Account;
import com.toocms.freeman.ui.BaseAty;
import com.toocms.freeman.ui.index.IndexAty;
import com.toocms.freeman.ui.infomationaty.NewBaseInfoAty;
import com.uuzuche.lib_zxing.activity.CaptureActivity;
import com.uuzuche.lib_zxing.activity.CodeUtils;
import com.uuzuche.lib_zxing.activity.ZXingLibrary;

import org.xutils.common.util.LogUtil;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.zero.android.common.permission.PermissionSuccess;
import cn.zero.android.common.util.JSONUtils;
import cn.zero.android.common.view.KeyboardLayout;

/**
 * Created by admin on 2017/4/10.
 */

public class RegisterPswAty extends BaseAty {
    @ViewInject(R.id.register_logo)
    ImageView imgvLogo;
    @ViewInject(R.id.register_psw)
    EditText editPsw;
    @ViewInject(R.id.register_repsd)
    EditText editRepsd;
    @ViewInject(R.id.register_inviter)
    EditText editInviter;
    @ViewInject(R.id.login_keyboard)
    KeyboardLayout loginKeyboard;
    /**
     * 注册[register]         注册。只能在验证码检测时间内正常执行步骤
     *
     * @param mobile         手机号
     * @param password       密码
     * @param password_again 确认密码
     * @param code           邀请人编号
     * @param apiListener
     */
    /**
     * 检测邀请人[inviterCodeAccuracy]
     * 检查填写的邀请人号码是否存在此人（如果做输入邀请人即时检测使用）
     * 如果存在返回此人的信息
     *
     * @param code 邀请人号码
     */
    private String mobile;
    private String password;
    private String password_again;
    private String code;
    private Account account;
    private boolean isFirstClick;
    /**
     * 扫描跳转Activity RequestCode
     */
    public static final int REQUEST_CODE = 111;

    @Override
    protected int getLayoutResId() {
        return R.layout.aty_register_psd;
    }

    @Override
    protected void initialized() {
        account = new Account();
    }

    @Override
    protected void requestData() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ZXingLibrary.initDisplayOpinion(this);
        editInviter.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                code = editInviter.getText().toString();
                if (code.length() == 11 || code.length() == 9) {
                    account.inviterCodeAccuracy(code, RegisterPswAty.this);
                }
            }
        });
    }

    @Event({R.id.register_zxing, R.id.login})
    private void onViewClicked(View view) {
        mobile = getIntent().getStringExtra("mobile");
        password = editPsw.getText().toString().trim();
        password_again = editRepsd.getText().toString().trim();
        code = editInviter.getText().toString();
        switch (view.getId()) {
            case R.id.register_zxing:

                requestPermissions(Constants.PERMISSIONS_CAMERA, Manifest.permission.CAMERA);

                break;
            case R.id.login:
                if (TextUtils.isEmpty(mobile)) {
                    LogUtil.e("注册手机号为空");
                } else if (TextUtils.isEmpty(password)) {
                    showToast("请输入密码");
                    return;
                } else if (TextUtils.isEmpty(password_again)) {
                    showToast("请确认输入密码");
                    return;
                } else if (!TextUtils.equals(password, password_again)) {
                    showToast("两次密码输入不一致！！！");
                    return;
                } else if ((code.length() < 9 && code.length() > 0) || (code.length() > 9 && code.length() < 11)) {
                    if (isFirstClick) {
                        showToast("提示：如果不确定邀请人编号可以不添加！");
                        isFirstClick = false;
                    } else {
                        showToast("提示：邀请人编号为9位会员编号/11位手机号");
                        isFirstClick = true;
                    }


                    return;
                }
                showProgressDialog();
                account.register(mobile, password, password_again, code, this);
                break;
        }
    }

    @PermissionSuccess(requestCode = Constants.PERMISSIONS_CAMERA)
    public void requestSuccess() {
        Intent intent = new Intent(RegisterPswAty.this, CaptureActivity.class);
        startActivityForResult(intent, REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            return;
        }
        /**
         * 处理二维码扫描结果
         */
        if (requestCode == REQUEST_CODE) {
            //处理扫描结果（在界面上显示）
            if (null != data) {
                Bundle bundle = data.getExtras();
                if (bundle == null) {
                    return;
                }
                if (bundle.getInt(CodeUtils.RESULT_TYPE) == CodeUtils.RESULT_SUCCESS) {
                    String result = bundle.getString(CodeUtils.RESULT_STRING);
//                    Toast.makeText(this, "解析结果:" + result, Toast.LENGTH_LONG).show();
                    if (result.contains("http://zyr-api.toocms.com?")) {
                        int indexOf = result.lastIndexOf("=");
                        if (indexOf < result.length()) {
                            String substring = result.substring(indexOf + 1, result.length());
                            LogUtil.e(substring);
                            editInviter.setText(substring);
                            code = substring;
                        }

                    }
//                    LogUtil.e(result);
                } else if (bundle.getInt(CodeUtils.RESULT_TYPE) == CodeUtils.RESULT_FAILED) {
                    showToast("扫码取消！");
                }
            }
        }
    }

    @Override
    public void onComplete(RequestParams params, String result) {
        if (params.getUri().contains("Account/inviterCodeAccuracy")) {
            if (TextUtils.equals(JSONUtils.parseKeyAndValueToMap(result).get("flag"), "success")) {
                showToast(JSONUtils.parseKeyAndValueToMap(result).get("message"));
            }
        } else if (params.getUri().contains("Account/register")) {
            account.login(mobile, password, this);

        } else if (params.getUri().contains("Account/login")) {
            Map<String, String> map = JSONUtils.parseDataToMap(result);
            application.setUserInfo(map);
            Config.setLoginState(true);
//            showDialog("恭喜您！注册成功", "完善信息后才能发布信息、找工作，是否去完善信息？", "暂不完善", "完善信息", new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialog, int which) {
//                    Bundle bundle = new Bundle();
//                    bundle.putString("flag", "info");
//                    startActivity(NewBaseInfoAty.class, bundle);
//                    finish();
//                }
//            }, new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialog, int which) {
//                    startActivity(IndexAty.class, null);
//                    finish();
//                }
//            });
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("恭喜您！注册成功");
            builder.setMessage("完善信息后才能发布信息、找工作，是否去完善信息？");
            builder.setNegativeButton("暂不完善", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    startActivity(IndexAty.class, null);
                    finish();
                }
            });
            builder.setPositiveButton("完善信息", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Bundle bundle = new Bundle();
                    bundle.putString("flag", "info");
                    startActivity(NewBaseInfoAty.class, bundle);
                    finish();
                }
            });
            builder.setCancelable(false);
            builder.create().show();

        }
        super.onComplete(params, result);
    }

    @Override
    public void onError(Map<String, String> error) {
        if (editInviter.getText().toString().length() < 11) {
            Pattern p = Pattern.compile("^((13[0-9])|(15[^4,\\D])|(18[0,5-9])|177)\\d{6}$");
            Matcher m = p.matcher(editInviter.getText().toString());
            if (m.matches()) {
                return;
            } else {
                super.onError(error);
            }
        } else {
            super.onError(error);
        }

    }
}
