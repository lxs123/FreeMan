package com.toocms.freeman.ui.mine.settings;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.EditText;

import com.toocms.frame.tool.Commonly;
import com.toocms.freeman.R;
import com.toocms.freeman.https.Seminate;
import com.toocms.freeman.ui.BaseAty;

import org.xutils.http.RequestParams;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import cn.zero.android.common.util.JSONUtils;

/**
 * Created by admin on 2017/4/5.
 */

public class FeedbackAty extends BaseAty {
    @ViewInject(R.id.feedback_content)
    EditText editContent;
    @ViewInject(R.id.feedback_phone)
    EditText editPhone;
    private Seminate seminate;

    @Override
    protected int getLayoutResId() {
        return R.layout.aty_feedback;
    }

    @Override
    protected void initialized() {
        seminate = new Seminate();
    }

    @Override
    protected void requestData() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onComplete(RequestParams params, String result) {
        if (params.getUri().contains("Feedback/send")) {
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

    @Event({R.id.feedback_submit})
    private void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.feedback_submit:
                if (Commonly.getViewText(editPhone).isEmpty()) {
                    showToast("请输入您的联系方式！");
                    return;
                } else if (Commonly.getViewText(editContent).isEmpty()) {
                    showToast("请输入您");
                    return;
                }
                showProgressDialog();
                seminate.send(editPhone.getText().toString(), editContent.getText().toString(), this);
                break;
        }
    }
}
