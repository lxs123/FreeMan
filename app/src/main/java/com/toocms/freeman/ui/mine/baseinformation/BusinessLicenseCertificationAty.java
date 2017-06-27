package com.toocms.freeman.ui.mine.baseinformation;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.toocms.frame.image.ImageLoader;
import com.toocms.freeman.R;
import com.toocms.freeman.config.Constants;
import com.toocms.freeman.https.Account;
import com.toocms.freeman.https.User;
import com.toocms.freeman.ui.BaseAty;
import com.zero.autolayout.utils.AutoUtils;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.image.ImageOptions;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

import cn.zero.android.common.permission.PermissionSuccess;
import cn.zero.android.common.util.JSONUtils;
import cn.zero.android.common.util.ListUtils;

public class BusinessLicenseCertificationAty extends BaseAty {

    @ViewInject(R.id.business_img)
    private ImageView businessImg;
    private String businessStr;
    ImageLoader mImageLoader;
    private Account account;
    private User user;

    @Override
    protected int getLayoutResId() {
        return R.layout.aty_business_license_certification;
    }

    @Override
    protected void initialized() {
        mImageLoader = new ImageLoader();
        ImageOptions options = new ImageOptions.Builder()
                // setSize方法中的参数改成和item布局中图片大小一样
                .setSize(AutoUtils.getPercentWidthSize(172), AutoUtils.getPercentWidthSize(172))
                // 加载图片和加载失败图片方法中默认大小图片修改同上（差不多的也可以）
//                .setLoadingDrawableId(R.drawable.ic_default_172_172)
//                .setFailureDrawableId(R.drawable.ic_default_172_172)
                .setFadeIn(true).setImageScaleType(ImageView.ScaleType.CENTER_CROP)
                .setUseMemCache(true).build();
        mImageLoader.setImageOptions(options);
        account = new Account();
        user = new User();
    }

    @Override
    protected void requestData() {
        user.getPerfect(application.getUserInfo().get("noid"), this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActionBar.setTitle("营业执照照片");
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (businessStr != null && businessStr.length() > 0) {
            mImageLoader.disPlay(businessImg, businessStr);
        } else return;
    }

    @Event({R.id.business_img, R.id.business_sure})
    private void onClick(View view) {
        switch (view.getId()) {
            case R.id.business_img:
                requestPermissions(Constants.PERMISSIONS_WRITE_EXTERNAL_STORAGE, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
                break;
            case R.id.business_sure:
                if (TextUtils.isEmpty(businessStr)) {
                    showToast("请上传营业执照照片");
                    return;
                }
                showProgressDialog();
                account.uploadBusinessPhotos(application.getUserInfo().get("noid"), businessStr, application.getUserInfo().get("valid"), this);
                break;
        }
    }

    @Override
    public void onComplete(RequestParams params, String result) {
        if (params.getUri().contains("Account/uploadBusinessPhotos")) {
            showToast(JSONUtils.parseKeyAndValueToMap(result).get("message"));
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    finish();
                }
            }, 1000);
        } else if (params.getUri().contains("User/getPerfect")) {
            Map<String, String> map = JSONUtils.parseDataToMap(result);
            Map<String, String> ident = JSONUtils.parseKeyAndValueToMap(map.get("business"));
            mImageLoader.loadFile(ident.get("images"), new Callback.CacheCallback<File>() {
                @Override
                public boolean onCache(RequestParams requestParams, File file) {
                    try {
                        String path = file.getPath();
                        String name = file.getName();
                        String replace = file.getPath().replace(name, "");
                        File tempFile = File.createTempFile(file.getName(), ".png", new File(replace));
//                                    new File()
//                    selectImagePath.set(position + 1, tempFile.getAbsolutePath());
                        businessStr = tempFile.getAbsolutePath();
                        mImageLoader.disPlay(businessImg, file.getAbsolutePath());
                        Bitmap bmp = BitmapFactory.decodeFile(file.getPath());
                        FileOutputStream fileOutStream = null;
                        fileOutStream = new FileOutputStream(tempFile);
                        bmp.compress(Bitmap.CompressFormat.JPEG, 100, fileOutStream);  //把位图输出到指定的文件中
                        fileOutStream.flush();
                        fileOutStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

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
        }
        super.onComplete(params, result);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) return;
        switch (requestCode) {
            case com.toocms.frame.config.Constants.SELECT_IMAGE:
                ArrayList<String> list = getSelectImagePath(data);
                if (!ListUtils.isEmpty(list)) {
                    businessStr = list.get(0);
                }
                break;
        }
    }

    @PermissionSuccess(requestCode = Constants.PERMISSIONS_WRITE_EXTERNAL_STORAGE)
    public void requestSuccess() {
        startSelectSignImageAty();
    }
}
