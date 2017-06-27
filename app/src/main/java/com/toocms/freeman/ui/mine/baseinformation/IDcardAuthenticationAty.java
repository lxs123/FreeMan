package com.toocms.freeman.ui.mine.baseinformation;

import android.Manifest;
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

import cn.zero.android.common.permission.PermissionFail;
import cn.zero.android.common.permission.PermissionSuccess;
import cn.zero.android.common.util.JSONUtils;
import cn.zero.android.common.util.ListUtils;

public class IDcardAuthenticationAty extends BaseAty {
    /**
     * 上传证件 - 身份证[uploadIdCardPhotos]
     *
     * @param noid
     * @param byperson 手持身份证照片,单图
     * @param byfront  正面照片,单图
     * @param byback   反面照片,单图
     * @param valid    校验参数，登录中返回
     */

    ImageLoader mImageLoader;
    @ViewInject(R.id.hand_id_img)
    private ImageView handIdImg;
    @ViewInject(R.id.positive_id_img)
    private ImageView positivedImg;
    @ViewInject(R.id.negative_id_img)
    private ImageView negativeIdImg;

    private String handIdImgStr = "";
    private String positiveIdImgStr = "";
    private String negetiveIdImgStr = "";

    //刚刚点击的是哪个
    private int state = 0;
    private Account account;
    private User user;

    @Override
    protected int getLayoutResId() {
        return R.layout.aty_idcard_authentication;
    }

    @Override
    protected void initialized() {
        mImageLoader = new ImageLoader();
        ImageOptions options = new ImageOptions.Builder()
                // setSize方法中的参数改成和item布局中图片大小一样
                .setSize(AutoUtils.getPercentWidthSize(172), AutoUtils.getPercentWidthSize(172))
//                // 加载图片和加载失败图片方法中默认大小图片修改同上（差不多的也可以）
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
        mActionBar.setTitle("身份证认证");
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (state == 0) return;
        else if (state == 1) {
            if (handIdImgStr.length() > 0)
                mImageLoader.disPlay(handIdImg, handIdImgStr);
        } else if (state == 2) {
            if (positiveIdImgStr.length() > 0)
                mImageLoader.disPlay(positivedImg, positiveIdImgStr);
        } else if (state == 3) {
            if (negetiveIdImgStr.length() > 0)
                mImageLoader.disPlay(negativeIdImg, negetiveIdImgStr);
        } else return;
    }

    @Override
    public void onComplete(RequestParams params, String result) {
        if (params.getUri().contains("Account/uploadIdCardPhotos")) {
            showToast(JSONUtils.parseKeyAndValueToMap(result).get("message"));
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    finish();
                }
            }, 1000);
        } else if (params.getUri().contains("User/getPerfect")) {
            Map<String, String> map = JSONUtils.parseDataToMap(result);
            Map<String, String> ident = JSONUtils.parseKeyAndValueToMap(map.get("ident"));
            Map<String, String> images = JSONUtils.parseKeyAndValueToMap(ident.get("images"));
            mImageLoader.loadFile(images.get("byperson"), new Callback.CacheCallback<File>() {
                @Override
                public boolean onCache(RequestParams requestParams, File file) {
                    try {
                        String path = file.getPath();
                        String name = file.getName();
                        String replace = file.getPath().replace(name, "");
                        File tempFile = File.createTempFile(file.getName(), ".png", new File(replace));
//                                    new File()
//                    selectImagePath.set(position + 1, tempFile.getAbsolutePath());
                        handIdImgStr = tempFile.getAbsolutePath();
                        mImageLoader.disPlay(handIdImg, file.getAbsolutePath());
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
            mImageLoader.loadFile(images.get("byfront"), new Callback.CacheCallback<File>() {
                @Override
                public boolean onCache(RequestParams requestParams, File file) {
                    try {
                        String path = file.getPath();
                        String name = file.getName();
                        String replace = file.getPath().replace(name, "");
                        File tempFile = File.createTempFile(file.getName(), ".png", new File(replace));
//                                    new File()
//                    selectImagePath.set(position + 1, tempFile.getAbsolutePath());
                        positiveIdImgStr = tempFile.getAbsolutePath();
                        mImageLoader.disPlay(positivedImg, file.getAbsolutePath());
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
            mImageLoader.loadFile(images.get("byback"), new Callback.CacheCallback<File>() {
                @Override
                public boolean onCache(RequestParams requestParams, File file) {
                    try {
                        String path = file.getPath();
                        String name = file.getName();
                        String replace = file.getPath().replace(name, "");
                        File tempFile = File.createTempFile(file.getName(), ".png", new File(replace));
//                                    new File()
//                    selectImagePath.set(position + 1, tempFile.getAbsolutePath());
                        negetiveIdImgStr = tempFile.getAbsolutePath();
                        mImageLoader.disPlay(negativeIdImg, file.getAbsolutePath());
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

    @Event({R.id.hand_id_img, R.id.positive_id_img, R.id.negative_id_img, R.id.id_card_auth_sure})
    private void onClick(View view) {
        switch (view.getId()) {
            case R.id.hand_id_img:
                state = 1;
                requestPermissions(Constants.PERMISSIONS_WRITE_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE);
                break;
            case R.id.positive_id_img:
                state = 2;
                requestPermissions(Constants.PERMISSIONS_WRITE_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE);
                break;
            case R.id.negative_id_img:
                state = 3;
                requestPermissions(Constants.PERMISSIONS_WRITE_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE);
                break;
            case R.id.id_card_auth_sure:
                if (TextUtils.isEmpty(handIdImgStr)) {
                    showToast("请上传手持身份证正面照");
                    return;
                } else if (TextUtils.isEmpty(positiveIdImgStr)) {
                    showToast("请上传身份证正面照");
                    return;
                } else if (TextUtils.isEmpty(negetiveIdImgStr)) {
                    showToast("请上传身份证反面照");
                    return;
                }
                showProgressDialog();
                account.uploadIdCardPhotos(application.getUserInfo().get("noid"),
                        handIdImgStr, positiveIdImgStr, negetiveIdImgStr, application.getUserInfo().get("valid"),
                        this);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) return;
        switch (requestCode) {
            case com.toocms.frame.config.Constants.SELECT_IMAGE:
                ArrayList<String> list = getSelectImagePath(data);
                if (!ListUtils.isEmpty(list)) {
                    if (state == 1) {
                        handIdImgStr = list.get(0);
                    } else if (state == 2) {
                        positiveIdImgStr = list.get(0);
                    } else if (state == 3) {
                        negetiveIdImgStr = list.get(0);
                    }
                }
                break;
        }
    }


    @PermissionSuccess(requestCode = Constants.PERMISSIONS_WRITE_EXTERNAL_STORAGE)
    public void requestSuccess() {
        startSelectSignImageAty();
    }

    @PermissionFail(requestCode = Constants.PERMISSIONS_WRITE_EXTERNAL_STORAGE)
    public void requestFail() {
        showToast("获取权限失败");
    }


}
