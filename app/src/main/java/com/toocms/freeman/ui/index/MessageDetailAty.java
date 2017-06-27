package com.toocms.freeman.ui.index;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.toocms.freeman.R;
import com.toocms.freeman.https.Message;
import com.toocms.freeman.ui.BaseAty;
import com.toocms.freeman.ui.contract.ContDetailAty;
import com.toocms.freeman.ui.mine.MemberDetailAty;
import com.toocms.freeman.ui.recruitment.myjoborder.JODetailAty;

import org.xutils.http.RequestParams;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import java.util.Map;

import cn.zero.android.common.util.JSONUtils;
import cn.zero.android.common.util.MapUtils;

/**
 * 消息详情
 * Created by admin on 2017/3/23.
 */

public class MessageDetailAty extends BaseAty {
    @ViewInject(R.id.message_det_time)
    TextView tvTime;
    @ViewInject(R.id.message_det_tital)
    TextView tvTital;
    @ViewInject(R.id.message_det_content)
    TextView tvContent;
    /**
     * 消息详情[detail]
     *
     * @param noid       用户编号
     * @param message_id 消息id
     */
    private String noid;
    private String message_id;
    private Message message;
    private Map<String, String> value;
    private String link_type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActionBar.setTitle("消息详情");
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.aty_message_detail;
    }

    @Override
    protected void initialized() {
        message = new Message();
    }

    @Override
    protected void requestData() {
        showProgressContent();
        noid = application.getUserInfo().get("noid");
        message_id = getIntent().getStringExtra("message_id");
        message.detail(noid, message_id, this);
    }

    @Override
    public void onComplete(RequestParams params, String result) {
        if (params.getUri().contains("Message/detail")) {
            Map<String, String> map = JSONUtils.parseDataToMap(result);
            tvTital.setText(map.get("title"));
            tvContent.setText(map.get("content"));
            tvTime.setText(map.get("create_time"));
            String link_value = map.get("link_value");
            value = JSONUtils.parseKeyAndValueToMap(link_value);
            link_type = map.get("link_type");

        }
        super.onComplete(params, result);
    }

    @Event(R.id.message_det_content)
    private void onClick(View view) {
        switch (view.getId()) {
            case R.id.message_det_content:
                Bundle bundle = new Bundle();
                if (TextUtils.isEmpty(link_type) || TextUtils.equals(link_type, "0")
                        || MapUtils.isEmpty(value)) {
                    return;
                }
                switch (link_type) {
                    case "1"://跳转合同
                        bundle.putString("contract_noid", value.get("contract_noid"));
                        startActivity(ContDetailAty.class, bundle);
                        break;
                    case "2"://用户信息
                        bundle.putString("code", value.get("noid"));
                        bundle.putString("flag", "collect");
                        startActivity(MemberDetailAty.class, bundle);
                        break;
                    case "3": //招工单详情 hire_noid
                        bundle.putString("hire_noid", value.get("hire_noid"));
                        startActivity(JODetailAty.class, bundle);
                        break;
                }


                break;
        }
    }
}
