package com.toocms.freeman.ui.pay;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
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
 * 不同意结算
 * Created by admin on 2017/3/28.
 */

public class DisagreePayAty extends BaseAty {

    //上传图片
    @ViewInject(R.id.complaint_imgs)
    private MyGridView imgs;
    @ViewInject(R.id.complaint_title)
    TextView tvTitle;
    @ViewInject(R.id.complaint_edit)
    EditText editRefuse;
    /**
     * flag 是通过intent传来的参数
     * refuse 拒绝退补
     * re mediation 拒绝调解方案
     */
    private String flag;
    /**
     * 不同意结算请求[cancelAdequacy]
     *
     * @param contract_noid
     * @param noid
     * @param refuse        取消理由 80字
     * @param photos        上传图片数组
     */
    private String contract_noid;
    private String noid;
    private String refuse;
    private List<String> selectImagePath = new ArrayList<>();
    private ArrayList<String> stringArrayList = new ArrayList<>();
    private ImgGridAdapter imgGridAdapter;
    private int position = 7;
    private Contract contract;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        flag = getIntent().getStringExtra("flag");

        if (TextUtils.equals(flag, "refuse")) {
            mActionBar.setTitle("拒绝退补");
            tvTitle.setText("选择您拒绝退补的原因");
            editRefuse.setHint("请描述您的拒绝退补原因(1-80字)");
        } else if (TextUtils.equals(flag, "mediate")) {
            mActionBar.setTitle("拒绝调解方案");
            tvTitle.setText("选择您拒绝调解方案的原因");
            editRefuse.setHint("请描述您的拒绝调解方案原因(1-80字)");
        } else if (TextUtils.equals(flag, "settlement")) {
            mActionBar.setTitle("不同意结算");
            tvTitle.setText("选择您不同意结算的原因");
            editRefuse.setHint("请描述您的不同意结算原因(1-80字)");
        } else {
            tvTitle.setText("选择您不同意支付的原因");
            editRefuse.setHint("请描述您的不同意付款原因(1-80字)");
        }
        imgGridAdapter = new ImgGridAdapter();
        imgs.setAdapter(imgGridAdapter);
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
        if (params.getUri().contains("Contract/cancelAdequacy") ||
                params.getUri().contains("Contract/cancelDrawback") ||
                params.getUri().contains("Contract/cancelIssue")) {
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
        contract_noid = getIntent().getStringExtra("contract_noid");
        noid = application.getUserInfo().get("noid");
        switch (view.getId()) {
            case R.id.complaint_commit:
                refuse = editRefuse.getText().toString().trim();
//                if (ListUtils.getSize(selectImagePath) == 1) {
//                    showToast("请添加图片");
//                    return;
//                }
                showProgressDialog();
                if (TextUtils.equals(flag, "refuse")) {
                    if (TextUtils.isEmpty(refuse)) {
                        showToast("请描述您的拒绝退补原因");
                        removeProgressDialog();
                        return;
                    }
                    contract.cancelDrawback(contract_noid, noid, refuse, selectImagePath, this);
                } else if (TextUtils.equals(flag, "settlement")) {
                    if (TextUtils.isEmpty(refuse)) {
                        showToast("请描述您的不同意结算原因");
                        removeProgressDialog();
                        return;
                    }
                    contract.cancelAdequacy(contract_noid, noid, refuse, selectImagePath, this);
                } else if (TextUtils.equals(flag, "mediate")) {
                    if (TextUtils.isEmpty(refuse)) {
                        showToast("请描述您的拒绝调解原因");
                        removeProgressDialog();
                        return;
                    }
                    contract.cancelIssue(contract_noid, noid, getIntent().getStringExtra("issue_id"), selectImagePath, refuse, this);
                }


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
                convertView = LayoutInflater.from(DisagreePayAty.this).inflate(R.layout.listitem_new_job_order, parent, false);
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