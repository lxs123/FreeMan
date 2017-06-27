package com.toocms.freeman.ui.lar;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.toocms.frame.config.Config;
import com.toocms.frame.tool.AppManager;
import com.toocms.frame.tool.Commonly;
import com.toocms.freeman.R;
import com.toocms.freeman.https.Account;
import com.toocms.freeman.ui.BaseAty;
import com.toocms.freeman.ui.index.IndexAty;
import com.zero.autolayout.utils.AutoUtils;

import org.xutils.http.RequestParams;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import java.util.Map;
import java.util.Set;

import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;
import cn.zero.android.common.util.JSONUtils;
import cn.zero.android.common.view.KeyboardLayout;

/**
 * Created by admin on 2017/4/10.
 */

public class LoginAty extends BaseAty implements KeyboardLayout.OnSoftKeyboardListener {
    /**
     * 登录[login]
     *
     * @param username 登录名，暂时为只输入手机号
     * @param password 登录密码
     */
    String username;
    String password;
    @ViewInject(R.id.login_logo)
    ImageView imgvLogo;
    @ViewInject(R.id.login_account)
    EditText editAccount;
    @ViewInject(R.id.login_psd)
    EditText editPsd;
    @ViewInject(R.id.login_forget)
    TextView tvForget;
    @ViewInject(R.id.login_keyboard)
    KeyboardLayout loginKeyboard;
    private Account account;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(false);
        mActionBar.setTitle("登录");
        mActionBar.setTitleMarginStart(AutoUtils.getPercentHeightSize(357));
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);//去除返回图标
        loginKeyboard.setOnSoftKeyboardListener(this);
        if (TextUtils.equals(getIntent().getStringExtra("flag"), "forget")) {
            editAccount.setText(getIntent().getStringExtra("phone"));
        }
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.aty_login;
    }

    @Override
    protected void initialized() {
        account = new Account();
    }

    @Override
    protected void requestData() {

    }

    @Event({R.id.login_forget, R.id.login})
    private void onClicked(View view) {
        username = editAccount.getText().toString().trim();
        password = editPsd.getText().toString().trim();
        switch (view.getId()) {
            case R.id.login_forget:
                if (TextUtils.isEmpty(username)) {
                    showToast("请输入手机号");
                    return;
                }
                Bundle bundle = new Bundle();
                bundle.putString("phone", username);
                startActivity(ForgetPswAty.class, bundle);
                break;
            case R.id.login:
                if (TextUtils.isEmpty(username)) {
                    showToast("请输入用户手机号");
                    return;
                } else if (Commonly.getViewText(editAccount).length() < 11) {
                    showToast("请输入正确的手机号码");
                    return;
                } else if (TextUtils.isEmpty(password)) {
                    showToast("请输入用户密码");
                    return;
                } else if (Commonly.getViewText(editPsd).length() < 6) {
                    showToast("请检查密码格式是否正确");
                }
                showProgressDialog();
                account.login(username, password, this);
                break;
        }
    }

    @Override
    public void onShown() {
        imgvLogo.setVisibility(View.GONE);
    }

    @Override
    public void onHidden() {
        imgvLogo.setVisibility(View.VISIBLE);
    }

    @Override
    public void onComplete(RequestParams params, String result) {
        if (params.getUri().contains("Account/login")) {
//            showToast(JSONUtils.parseKeyAndValueToMap(result).get("message"));
            Map<String, String> map = JSONUtils.parseDataToMap(result);
            application.setUserInfo(map);
            JPushInterface.setAlias(this, //上下文对象
                    map.get("noid")
                    , //别名
                    new TagAliasCallback() {//回调接口,i=0表示成功,其它设置失败
                        @Override
                        public void gotResult(int i, String s, Set<String> set) {
                            Log.d("alias", "set alias result is" + i);
                        }
                    });
            Config.setLoginState(true);
            startActivity(IndexAty.class, null);
        }
        super.onComplete(params, result);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_register, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_register:
                startActivity(RegisterAty.class, null);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            showDialog("提示", "是否退出自由人?", "确定", "取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    AppManager.getInstance().killAllActivity();
                    finish();
                }
            }, null);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
