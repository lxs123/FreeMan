package com.toocms.freeman.ui.contract;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.Selection;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.toocms.frame.image.ImageLoader;
import com.toocms.freeman.R;
import com.toocms.freeman.config.Constants;
import com.toocms.freeman.https.Contract;
import com.toocms.freeman.ui.BaseAty;
import com.toocms.freeman.ui.view.MyGridView;
import com.toocms.freeman.ui.view.PhotoZoomAty;
import com.zero.autolayout.utils.AutoUtils;

import org.xutils.http.RequestParams;
import org.xutils.image.ImageOptions;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

import cn.zero.android.common.permission.PermissionFail;
import cn.zero.android.common.permission.PermissionSuccess;
import cn.zero.android.common.util.JSONUtils;
import cn.zero.android.common.util.ListUtils;

/**
 * 投诉
 * Created by admin on 2017/3/28.
 */

public class ComplaintAty extends BaseAty {
    @ViewInject(R.id.complaint_grid)
    private MyGridView gridView;
    //上传图片
    @ViewInject(R.id.complaint_imgs)
    private MyGridView imgs;
    @ViewInject(R.id.complaint_edit)
    EditText editRefuse;
    private GridAdapter adapter;
    private String[] relieveStr = new String[]{"质量不过关", "工作时间严重延期", "突然不想合作", "对方态度恶劣", "合同出现纠纷", "实物有破损"};
    private int position = 7;
    private List<String> selectImagePath = new ArrayList<>();
    private ArrayList<String> stringArrayList = new ArrayList<>();
    private ImgGridAdapter imgGridAdapter;
    private String contract_noid;
    private String noid;
    private String refuse;
    private Contract contract;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adapter = new GridAdapter();
        gridView.setAdapter(adapter);
        imgGridAdapter = new ImgGridAdapter();
        imgs.setAdapter(imgGridAdapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                adapter.selItem(position);
            }
        });
        if (ListUtils.isEmpty(selectImagePath)) {
            selectImagePath.add("plus");
        }
        imgs.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == ListUtils.getSize(selectImagePath) - 1 && position < 6) {
                    requestPermissions(Constants.PERMISSIONS_WRITE_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE);
                } else {
                    Bundle bundle = new Bundle();
                    bundle.putString("photo", selectImagePath.get(position + 1));
                    startActivity(PhotoZoomAty.class, bundle);
                    overridePendingTransition(0, 0);
                }
            }
        });
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.aty_complaint;
    }

    @Override
    protected void initialized() {
        contract = new Contract();
    }

    @Override
    protected void requestData() {

    }

    @Override
    public void onComplete(RequestParams params, String result) {
        if (params.getUri().contains("Contract/complain")) {
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

    @Event({R.id.complaint_commit})
    private void onClick(View view) {
        switch (view.getId()) {
            case R.id.complaint_commit:
//                if (position > 6) {
//                    showToast("请选择您的投诉原因");
//                    return;
//                }
//                else if (ListUtils.getSize(selectImagePath) == 1) {
//                    showToast("请添加图片");
//                    return;
//                }

                contract_noid = getIntent().getStringExtra("contract_noid");
                noid = application.getUserInfo().get("noid");
                refuse = editRefuse.getText().toString().trim();
                if (TextUtils.isEmpty(refuse) && position > 6) {
                    showToast("请选择您的投诉原因");
                    return;
                }
                showProgressDialog();
//                if (position < 6) {
//                    refuse = editRefuse.getText().toString().trim() + relieveStr[position];
//                }
                if (TextUtils.equals(getIntent().getStringExtra("status"), "cap"))
                    contract.complain(contract_noid, noid, refuse, selectImagePath, this);
                else
                    contract.complain(contract_noid, noid, refuse, selectImagePath, this);
                break;
        }
    }

    int anInt = 7;

    @PermissionSuccess(requestCode = Constants.PERMISSIONS_WRITE_EXTERNAL_STORAGE)
    public void requestSuccess() {
        if (ListUtils.getSize(selectImagePath) < 7) {
            startSelectMultiImageAty(stringArrayList, anInt - ListUtils.getSize(selectImagePath));
        }
    }

    @PermissionFail(requestCode = Constants.PERMISSIONS_WRITE_EXTERNAL_STORAGE)
    public void requestFail() {
        showToast("获取权限失败，请在设置中开启");
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

    private class GridAdapter extends BaseAdapter {
        private ViewHolder viewHolder;
        boolean[] boo;


        public GridAdapter() {
            boo = new boolean[]{false, false, false, false, false, false};
        }

        private void selItem(int anInt) {
            for (int i = 0; i < boo.length; i++) {
                if (anInt == i) {
                    boo[i] = true;
                    position = i;
                    editRefuse.setText(relieveStr[position]);
                    Editable text = editRefuse.getText();
                    Selection.setSelection(text, text.length());
                } else
                    boo[i] = false;
            }
            notifyDataSetChanged();

        }

        @Override
        public int getCount() {
            return 6;
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
                convertView = LayoutInflater.from(getBaseContext()).inflate(R.layout.listitem_relieve_cont, null, false);
                viewHolder = new ViewHolder();
                x.view().inject(viewHolder, convertView);
//                AutoUtils.auto(convertView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            if (boo[position]) {
                viewHolder.tvCause.setCompoundDrawablesWithIntrinsicBounds(R.drawable.btn_select, 0, 0, 0);
            } else {
                viewHolder.tvCause.setCompoundDrawablesWithIntrinsicBounds(R.drawable.btn_unselect, 0, 0, 0);
            }
            viewHolder.tvCause.setText(relieveStr[position]);
//            viewHolder.tvCause.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//
//                }
//            });
            return convertView;
        }

        public class ViewHolder {
            @ViewInject(R.id.list_relieve_cause)
            private TextView tvCause;
        }
    }

    private class ImgGridAdapter extends BaseAdapter {
        private final ImageLoader imageLoader;
        private ViewHodler viewHodler;

        public ImgGridAdapter() {
            imageLoader = new ImageLoader();
            ImageOptions options = new ImageOptions.Builder().setLoadingDrawableId(R.drawable.img_index).setUseMemCache(true).build();
            imageLoader.setImageOptions(options);
        }


        @Override
        public int getCount() {
            return ListUtils.getSize(selectImagePath);
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
                convertView = LayoutInflater.from(ComplaintAty.this).inflate(R.layout.listitem_new_job_order, parent, false);
                x.view().inject(viewHodler, convertView);
                AutoUtils.autoSize(convertView);
                convertView.setTag(viewHodler);
            } else {
                viewHodler = (ViewHodler) convertView.getTag();
            }

            if (position == ListUtils.getSize(selectImagePath) - 1 && position < 7) {
                viewHodler.imgv.setScaleType(ImageView.ScaleType.FIT_XY);
                viewHodler.imgv.setImageResource(R.drawable.btn_add);
                viewHodler.imgvDel.setVisibility(View.GONE);
                if (position == 6) {
                    viewHodler.imgv.setVisibility(View.GONE);
                }
            } else {

                imageLoader.disPlay(viewHodler.imgv, selectImagePath.get(position + 1));
                viewHodler.imgvDel.setVisibility(View.VISIBLE);
            }
            viewHodler.imgvDel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectImagePath.remove((position + 1));
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