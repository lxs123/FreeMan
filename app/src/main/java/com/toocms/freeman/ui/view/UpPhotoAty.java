package com.toocms.freeman.ui.view;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.toocms.frame.image.ImageLoader;
import com.toocms.freeman.R;
import com.toocms.freeman.config.Constants;
import com.toocms.freeman.https.Contract;
import com.toocms.freeman.ui.BaseAty;
import com.zero.autolayout.utils.AutoUtils;

import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;

import cn.zero.android.common.permission.PermissionSuccess;
import cn.zero.android.common.util.JSONUtils;
import cn.zero.android.common.util.ListUtils;

/**
 * 上传照片
 * Created by admin on 2017/3/31.
 */

public class UpPhotoAty extends BaseAty {
    @ViewInject(R.id.up_photo)
    MyGridView myGridView;
    private ArrayList<String> selectImagePath = new ArrayList<>();
    private ArrayList<String> stringArrayList = new ArrayList<>();
    private ImgGridAdapter imgGridAdapter;
    private Contract contract;
    private String contract_noid;

    @Override
    protected int getLayoutResId() {
        return R.layout.aty_up_photo;
    }

    @Override
    protected void initialized() {
        contract = new Contract();
    }

    @Override
    protected void requestData() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        imgGridAdapter = new ImgGridAdapter();
        myGridView.setAdapter(imgGridAdapter);

        myGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == ListUtils.getSize(selectImagePath)) {
                    requestPermissions(Constants.PERMISSIONS_WRITE_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE);
                } else {
                    Bundle bundle = new Bundle();
                    bundle.putString("photo", selectImagePath.get(position));
                    startActivity(PhotoZoomAty.class, bundle);
                    overridePendingTransition(0, 0);
                }
            }
        });
    }

    int anInt = 7;

    @PermissionSuccess(requestCode = Constants.PERMISSIONS_WRITE_EXTERNAL_STORAGE)
    public void requsetSuccess() {
        if (ListUtils.getSize(selectImagePath) < 7) {
            startSelectMultiImageAty(stringArrayList, anInt - ListUtils.getSize(selectImagePath));
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            return;
        }
        switch (requestCode) {
            case com.toocms.frame.config.Constants.SELECT_IMAGE:
                for (int i = ListUtils.getSize(getSelectImagePath(data)); i > 0; i--) {
                    selectImagePath.add(getSelectImagePath(data).get(i - 1));
                }
                imgGridAdapter.notifyDataSetChanged();
                break;

        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_submit, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_submit:
                if (ListUtils.isEmpty(selectImagePath)) {
                    showToast("当前未选择需要上传的图片");
                    break;
                }
                showProgressDialog();
                contract.labUploadPhotos(application.getUserInfo().get("noid"), getIntent().getStringExtra("contract_noid"), selectImagePath
                        , this);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onComplete(RequestParams params, String result) {
        if (params.getUri().contains("Contract/labUploadPhotos")) {
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

    private class ImgGridAdapter extends BaseAdapter {

        private ViewHodler viewHodler;

        @Override
        public int getCount() {
            return ListUtils.getSize(selectImagePath) + 1;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                viewHodler = new ViewHodler();
                convertView = LayoutInflater.from(UpPhotoAty.this).inflate(R.layout.listitem_new_job_order, parent, false);
                x.view().inject(viewHodler, convertView);
                AutoUtils.autoSize(convertView);
                convertView.setTag(viewHodler);
            } else {
                viewHodler = (ViewHodler) convertView.getTag();
            }
            if (position == ListUtils.getSize(selectImagePath)) {
                viewHodler.imgv.setImageResource(R.drawable.btn_add);
                viewHodler.imgvDel.setVisibility(View.GONE);
                if (position == 6) {
                    viewHodler.imgv.setVisibility(View.GONE);
                }
            } else {
                ImageLoader imageLoader = new ImageLoader();
                imageLoader.disPlay(viewHodler.imgv, selectImagePath.get(position));
                viewHodler.imgvDel.setVisibility(View.VISIBLE);
            }
            viewHodler.imgvDel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectImagePath.remove(position);
                    notifyDataSetChanged();
                }
            });
            return convertView;
        }

        public class ViewHodler {
            @ViewInject(R.id.list_new_job_imgs)
            ImageView imgv;
            @ViewInject(R.id.list_new_job_delete)
            ImageView imgvDel;
        }
    }
}
