package com.toocms.freeman.ui.mine.baseinformation;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.toocms.frame.image.ImageLoader;
import com.toocms.freeman.R;
import com.toocms.freeman.config.Constants;
import com.toocms.freeman.config.JsonArryToList;
import com.toocms.freeman.https.Account;
import com.toocms.freeman.https.User;
import com.toocms.freeman.ui.BaseAty;
import com.toocms.freeman.ui.view.MyGridView;
import com.zero.autolayout.utils.AutoUtils;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.image.ImageOptions;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cn.zero.android.common.permission.PermissionSuccess;
import cn.zero.android.common.util.JSONUtils;
import cn.zero.android.common.util.ListUtils;

public class OtherCertificatesAty extends BaseAty {

    @ViewInject(R.id.my_grid_view)
    MyGridView myGridView;
    //用来存图片的集合
    List<String> mPicList = new ArrayList<>();
    ImageLoader mImageLoader;

    MyGridAdapter mAdapter;
    //保存点击图片的位置
    private int position;
    private Account account;
    private User user;


    @Override
    protected int getLayoutResId() {
        return R.layout.aty_other_certificates;
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
        mActionBar.setTitle("其他证件");

        myGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                position = i;
                requestPermissions(Constants.PERMISSIONS_WRITE_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mAdapter == null) {
            mAdapter = new MyGridAdapter();
            myGridView.setAdapter(mAdapter);
        }
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onComplete(RequestParams params, String result) {
        if (params.getUri().contains("Account/uploadOthersPhotos")) {
            showToast(JSONUtils.parseKeyAndValueToMap(result).get("message"));
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    finish();
                }
            }, 1000);
        } else if (params.getUri().contains("User/getPerfect")) {
            Map<String, String> map = JSONUtils.parseDataToMap(result);
            Map<String, String> ident = JSONUtils.parseKeyAndValueToMap(map.get("others"));
            mPicList = JsonArryToList.strList(ident.get("images"));
            mAdapter.notifyDataSetChanged();
        }
        super.onComplete(params, result);
    }

    @Event(R.id.other_sure)
    private void onClick(View view) {
        switch (view.getId()) {
            case R.id.other_sure:
                if (ListUtils.isEmpty(mPicList)) {
                    showToast("请上传其他证件");
                    return;
                }
                showProgressDialog();
                account.uploadOthersPhotos(application.getUserInfo().get("noid"), mPicList, application.getUserInfo().get("valid"), this);
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
                    if (position < ListUtils.getSize(mPicList)) {
                        mPicList.remove(position);
                        mPicList.add(position, list.get((0)));
                    } else {
                        mPicList.add(position, list.get(0));
                    }
                }
                break;
        }
    }

    @PermissionSuccess(requestCode = Constants.PERMISSIONS_WRITE_EXTERNAL_STORAGE)
    public void requestSuccess() {
        startSelectSignImageAty();
    }

    public class MyGridAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return ListUtils.getSize(mPicList) + 1;
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(final int i, View view, ViewGroup viewGroup) {
            ViewHolder mViewHolder;
            if (view == null) {
                view = LayoutInflater.from(OtherCertificatesAty.this).inflate(R.layout.listitem_pic, viewGroup, false);
                mViewHolder = new ViewHolder(view);
                view.setTag(mViewHolder);
            } else {
                mViewHolder = (ViewHolder) view.getTag();
            }

            if (i >= 2) mViewHolder.blankView.setVisibility(View.VISIBLE);

            if (i < ListUtils.getSize(mPicList)) {
                // TODO: 2017/3/30 处理 mViewHolder.onClickImg
                if (mPicList.get(i).contains("http")) {
                    mImageLoader.loadFile(mPicList.get(i), new Callback.CacheCallback<File>() {
                        @Override
                        public boolean onCache(RequestParams requestParams, File file) {
                            try {
                                String path = file.getPath();
                                String name = file.getName();
                                String replace = file.getPath().replace(name, "");
                                File tempFile = File.createTempFile(file.getName(), ".png", new File(replace));
//                                    new File()


                                Bitmap bmp = BitmapFactory.decodeFile(file.getPath());
                                FileOutputStream fileOutStream = null;
                                fileOutStream = new FileOutputStream(tempFile);
                                bmp.compress(Bitmap.CompressFormat.JPEG, 100, fileOutStream);  //把位图输出到指定的文件中
                                fileOutStream.flush();
                                fileOutStream.close();
                                mPicList.set(i, tempFile.getAbsolutePath());
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
                mImageLoader.disPlay(mViewHolder.onClickImg, mPicList.get(i));
            } else if (i == ListUtils.getSize(mPicList)) {
                mViewHolder.onClickImg.setImageResource(R.drawable.img_click);
            }
            return view;
        }

        public class ViewHolder {
            @ViewInject(R.id.onclick_img)
            ImageView onClickImg;
            @ViewInject(R.id.blank_view)
            View blankView;

            public ViewHolder(View itemView) {
                x.view().inject(this, itemView);
                AutoUtils.auto(itemView);
            }
        }
    }

}
