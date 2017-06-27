package com.toocms.freeman.ui.mine;

import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import com.toocms.frame.image.ImageLoader;
import com.toocms.freeman.R;
import com.toocms.freeman.https.Document;
import com.toocms.freeman.https.User;
import com.toocms.freeman.ui.BaseAty;
import com.toocms.freeman.ui.view.MyImageDialog;

import org.xutils.http.RequestParams;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import cn.zero.android.common.util.JSONUtils;

/**
 * Created by admin on 2017/4/24.
 */

public class MyExtensionAty extends BaseAty {
    @ViewInject(R.id.my_extension_code)
    TextView tvCode;
    @ViewInject(R.id.my_extension_img)
    ImageView imgvImg;
    @ViewInject(R.id.my_extension_content)
    WebView webContent;
    /**
     * 获取二维码名片[noidQrcode]
     *
     * @param noid 用户编号
     */
    private String noid;
    private User user;
    private Document document;
    private String imgvUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.aty_my_extension;
    }

    @Override
    protected void initialized() {
        user = new User();
        document = new Document();
    }


    @Override
    protected void requestData() {
        showProgressContent();
        noid = application.getUserInfo().get("noid");
        user.noidQrcode(noid, this);
        document.pursue(this);
    }

    @Override
    public void onComplete(RequestParams params, String result) {
        if (params.getUri().contains("User/noidQrcode")) {
            ImageLoader imageLoader = new ImageLoader();
            imgvUrl = params.toString();
            imageLoader.disPlay(imgvImg, imgvUrl);
            tvCode.setText(noid);

        } else if (params.getUri().contains("Document/pursue")) {
            String data = JSONUtils.parseKeyAndValueToMap(result).get("data");
            webContent.loadDataWithBaseURL(null, data, "text/html", "utf-8", null);

        }
        super.onComplete(params, result);
    }

    @Event({R.id.my_extension_img})
    private void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.my_extension_img:
                MyImageDialog myImageDialog = new MyImageDialog(this, R.style.Dialog_Fullscreen, 0, 0, imgvUrl);
                myImageDialog.show();
                WindowManager windowManager = getWindowManager();
                Display display = windowManager.getDefaultDisplay();
                WindowManager.LayoutParams lp = myImageDialog.getWindow().getAttributes();
                lp.width = (int) (display.getWidth()); //设置宽度
                lp.height = display.getHeight();
                myImageDialog.getWindow().setAttributes(lp);
                break;

        }
    }
}
