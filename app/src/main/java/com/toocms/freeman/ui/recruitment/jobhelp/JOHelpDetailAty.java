package com.toocms.freeman.ui.recruitment.jobhelp;

import android.os.Bundle;
import android.text.TextUtils;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.TextView;

import com.toocms.freeman.R;
import com.toocms.freeman.https.Document;
import com.toocms.freeman.ui.BaseAty;

import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ViewInject;

import java.util.Map;

import cn.zero.android.common.util.JSONUtils;

/**
 * 招工帮助详情
 * Created by admin on 2017/3/27.
 */

public class JOHelpDetailAty extends BaseAty {
    @ViewInject(R.id.jo_help_det_id)
    TextView tvId;
    @ViewInject(R.id.jo_help_det_title)
    TextView tvTitle;
    @ViewInject(R.id.jo_help_det_content)
    WebView webContent;
    /**
     * 招工帮助单条[hireSingle]
     *
     * @param doc_id 文档id
     */

    /**
     * 帮助中心单条[acolyteSingle]
     *
     * @param doc_id 文章id
     */
    private String doc_id;
    private Document document;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showProgressDialog();
        doc_id = getIntent().getStringExtra("doc_id");
        if (TextUtils.equals(getIntent().getStringExtra("flag"), "index")) {
            mActionBar.setTitle("帮助详情");
            document.acolyteSingle(doc_id, this);
        } else if (TextUtils.equals(getIntent().getStringExtra("flag"), "search")) {
            mActionBar.setTitle("搜工作帮助详情");
            document.workingSingle(doc_id, this);
        } else {
            mActionBar.setTitle("招工帮助详情");
            document.hireSingle(doc_id, this);
        }

    }

    @Override
    protected int getLayoutResId() {
        return R.layout.aty_jo_help_detail;
    }

    @Override
    protected void initialized() {
        document = new Document();
    }

    @Override
    protected void requestData() {


    }

    @Override
    public void onComplete(RequestParams params, String result) {
        if (params.getUri().contains("Document/acolyteSingle")) {
            Map<String, String> map = JSONUtils.parseDataToMap(result);
            tvId.setText(getIntent().getStringExtra("doc_id") + "、");
            tvTitle.setText(map.get("title"));
            WebSettings webSettings = webContent.getSettings();
            webSettings.setTextSize(WebSettings.TextSize.NORMAL);
            webContent.loadDataWithBaseURL(null, map.get("content"), "text/html", "utf-8", null);
        } else if (params.getUri().contains("Document/hireSingle") ||
                params.getUri().contains("Document/workingSingle")) {
            Map<String, String> map = JSONUtils.parseDataToMap(result);
            tvId.setText(getIntent().getStringExtra("doc_id") + "、");
            tvTitle.setText(map.get("title"));
            WebSettings webSettings = webContent.getSettings();
            webSettings.setTextSize(WebSettings.TextSize.NORMAL);
            webContent.loadDataWithBaseURL(null, map.get("content"), "text/html", "utf-8", null);
        }
        super.onComplete(params, result);
    }
}
