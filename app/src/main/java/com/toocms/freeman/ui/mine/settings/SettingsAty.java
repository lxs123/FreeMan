package com.toocms.freeman.ui.mine.settings;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.view.View;

import com.toocms.frame.config.Config;
import com.toocms.frame.web.ApiTool;
import com.toocms.freeman.R;
import com.toocms.freeman.ui.BaseAty;
import com.toocms.freeman.ui.lar.LoginAty;

import org.xutils.http.RequestParams;
import org.xutils.view.annotation.Event;

import cn.zero.android.common.util.FileManager;
import cn.zero.android.common.util.JSONUtils;

/**
 * Created by admin on 2017/4/1.
 */

public class SettingsAty extends BaseAty {

    @Override
    protected int getLayoutResId() {
        return R.layout.aty_settings;
    }

    @Override
    protected void initialized() {

    }

    @Override
    protected void requestData() {

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!Config.isLogin()) {
            startActivity(LoginAty.class, null);
            finish();
        }
    }

    private void getOverall() {
        RequestParams params = new RequestParams("http://zyr-api.toocms.com/index.php/Extra/getOverall");
        ApiTool apiTool = new ApiTool();
        apiTool.postApi(params, this);
    }

    @Event({R.id.settings_clear, R.id.settings_feedback, R.id.settings_exit, R.id.settings_about, R.id.settings_service})
    private void onClick(View view) {
        switch (view.getId()) {
            case R.id.settings_clear:
                showProgressDialog();
                showDialog("提示", "确定清理缓存", "确定", "取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                FileManager.clearCacheFiles();
                                removeProgressDialog();
                                showToast("清理完毕");
                            }
                        }, 1500);
                    }
                }, null);
                removeProgressDialog();

                break;
            case R.id.settings_feedback:
                startActivity(FeedbackAty.class, null);
                break;
            case R.id.settings_exit:
                Config.setLoginState(false);
                showDialog("提示", "确定要退出登录吗？", "确定", "取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        showProgressDialog();
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                removeProgressDialog();
                                startActivity(LoginAty.class, null);
                                finish();
                            }
                        }, 1500);
                    }
                }, null);


                break;
            case R.id.settings_about:
                startActivity(AboutAty.class, null);
                break;

            case R.id.settings_service:
                showProgressDialog();
                getOverall();
                break;
        }
    }

    @Override
    public void onComplete(RequestParams params, String result) {
        if (params.getUri().contains("Extra/getOverall")) {
            final String serviceTel = JSONUtils.parseDataToMap(result).get("service_tel");
            showDialog(null, "是否拨打" + serviceTel + "客服电话", 0, "拨打", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    intent.setData(Uri.parse("tel:" + serviceTel));
                    startActivity(intent);
                }
            }, "取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                }
            });
        }
        super.onComplete(params, result);
    }
}
