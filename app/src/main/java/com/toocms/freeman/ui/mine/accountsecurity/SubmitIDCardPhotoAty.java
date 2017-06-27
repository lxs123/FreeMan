package com.toocms.freeman.ui.mine.accountsecurity;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.toocms.frame.image.ImageLoader;
import com.toocms.freeman.R;
import com.toocms.freeman.config.Constants;
import com.toocms.freeman.ui.BaseAty;

import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import java.util.ArrayList;

import cn.zero.android.common.permission.PermissionFail;
import cn.zero.android.common.permission.PermissionSuccess;
import cn.zero.android.common.util.ListUtils;

public class SubmitIDCardPhotoAty extends BaseAty {

    @ViewInject(R.id.sub_id_photo_one)
    ImageView imgvOne;
    @ViewInject(R.id.sub_id_photo_two)
    ImageView imgvTwo;
    @ViewInject(R.id.sub_id_photo_three)
    ImageView imgvThree;
    private int position = 1;
    private String one;
    private String two;
    private String three;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActionBar.setTitle("提交手持身份证照片");
        if (!TextUtils.isEmpty(getIntent().getStringExtra("one"))) {
            ImageLoader imageLoader = new ImageLoader();
            imageLoader.disPlay(imgvOne, getIntent().getStringExtra("one"));
            imageLoader.disPlay(imgvTwo, getIntent().getStringExtra("two"));
            imageLoader.disPlay(imgvThree, getIntent().getStringExtra("three"));
        }
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.aty_submit_idcard_photo;
    }

    @Override
    protected void initialized() {

    }

    @Override
    protected void requestData() {
    }

    @Event({R.id.sub_id_photo_one, R.id.sub_id_photo_two, R.id.sub_id_photo_three, R.id.sub_id_photo_submit})
    private void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.sub_id_photo_one:
                requestPermissions(Constants.PERMISSIONS_WRITE_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE);
                position = 1;
                break;
            case R.id.sub_id_photo_two:
                requestPermissions(Constants.PERMISSIONS_WRITE_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE);
                position = 2;
                break;
            case R.id.sub_id_photo_three:
                requestPermissions(Constants.PERMISSIONS_WRITE_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE);
                position = 3;
                break;
            case R.id.sub_id_photo_submit:
                if (TextUtils.isEmpty(one)) {
                    showToast("请上传手持身份证正面照");
                    return;
                } else if (TextUtils.isEmpty(two)) {
                    showToast("请上传身份证正面照");
                    return;
                } else if (TextUtils.isEmpty(one)) {
                    showToast("请上传身份证反面照");
                    return;
                }
                Intent intent = new Intent();
                intent.putExtra("one", one);
                intent.putExtra("two", two);
                intent.putExtra("three", three);
                setResult(RESULT_OK, intent);
                showProgressDialog();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        removeProgressDialog();
                        finish();
                    }
                }, 1500);

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            return;
        }
        switch (requestCode) {
            case com.toocms.frame.config.Constants.SELECT_IMAGE:
                ArrayList<String> selectImagePath = getSelectImagePath(data);
                ImageLoader imageLoader = new ImageLoader();
                if (!ListUtils.isEmpty(selectImagePath)) {
                    switch (position) {
                        case 1:
                            imageLoader.disPlay(imgvOne, selectImagePath.get(0));
                            one = selectImagePath.get(0);
                            break;
                        case 2:
                            imageLoader.disPlay(imgvTwo, selectImagePath.get(0));
                            two = selectImagePath.get(0);
                            break;
                        case 3:
                            imageLoader.disPlay(imgvThree, selectImagePath.get(0));
                            three = selectImagePath.get(0);
                            break;
                    }
                    break;
                }
        }
    }
}
