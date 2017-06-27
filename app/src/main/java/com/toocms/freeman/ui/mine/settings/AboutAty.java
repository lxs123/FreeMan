package com.toocms.freeman.ui.mine.settings;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.toocms.frame.image.ImageLoader;
import com.toocms.freeman.R;
import com.toocms.freeman.https.Document;
import com.toocms.freeman.ui.BaseAty;

import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ViewInject;

import java.util.Map;

import cn.zero.android.common.util.JSONUtils;

/**
 * 关于我们
 *
 * @author
 * @date 2017/4/24 11:01
 */
public class AboutAty extends BaseAty {

    @ViewInject(R.id.about_content)
    private TextView textView;
    @ViewInject(R.id.about_logo)
    ImageView imgvLogo;
    private Document document;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActionBar.setTitle("关于我们");
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.aty_about;
    }

    @Override
    protected void initialized() {
        document = new Document();
    }

    @Override
    protected void requestData() {
        showProgressContent();
        document.aboutus(this);
    }

    @Override
    public void onComplete(RequestParams params, String result) {
        // =========================== 指定接口返回的关于我们内容的字段 =========================
        // Html.fromHtml()
        Map<String, String> map = JSONUtils.parseDataToMap(result);
        ImageLoader loader = new ImageLoader();
        loader.disPlay(imgvLogo, map.get("logo"));
        textView.setText(map.get("content"));
        super.onComplete(params, result);
    }
}
