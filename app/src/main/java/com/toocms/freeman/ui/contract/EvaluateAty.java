package com.toocms.freeman.ui.contract;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.Selection;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
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
import com.toocms.freeman.config.JsonArryToList;
import com.toocms.freeman.https.Contract;
import com.toocms.freeman.ui.BaseAty;
import com.toocms.freeman.ui.view.MyGridView;
import com.toocms.freeman.ui.view.PhotoZoomAty;
import com.zero.autolayout.utils.AutoUtils;

import org.xutils.common.Callback;
import org.xutils.common.util.LogUtil;
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

import cn.zero.android.common.permission.PermissionFail;
import cn.zero.android.common.permission.PermissionSuccess;
import cn.zero.android.common.util.JSONUtils;
import cn.zero.android.common.util.ListUtils;
import cn.zero.android.common.util.MapUtils;

/**
 * Created by admin on 2017/3/29.
 */

public class EvaluateAty extends BaseAty {
    @ViewInject(R.id.evaluate_high)
    TextView tvHigh;
    @ViewInject(R.id.evaluate_medium)
    TextView tvMedium;
    @ViewInject(R.id.evaluate_low)
    TextView tvLow;
    @ViewInject(R.id.evaluate_noid)
    TextView tvNoid;
    @ViewInject(R.id.evaluate_total)
    TextView tvTotal;
    @ViewInject(R.id.evaluate_settlement)
    TextView tvSettlement;
    @ViewInject(R.id.evaluate_start_date)
    TextView tvStartDate;
    @ViewInject(R.id.evaluate_end_date)
    TextView tvEndDate;
    @ViewInject(R.id.evaluate_payment)
    TextView tvPayment;
    @ViewInject(R.id.evaluate_work)
    TextView tvWork;
    @ViewInject(R.id.evaluate_price)
    TextView tvPrice;
    @ViewInject(R.id.evaluate_other)
    EditText editOther;
    //上传图片
    @ViewInject(R.id.complaint_imgs)
    private MyGridView imgs;
    private TextView[] textViews;
    private ImgGridAdapter imgGridAdapter;
    private List<String> selectImagePath = new ArrayList<>();
    private ArrayList<String> stringArrayList = new ArrayList<>();
    private int position;
    /**
     * 获取评价带入信息[getSimpleContract]
     *
     * @param contract_noid
     * @param noid
     */
    /**
     * 发布评价[assess]
     *
     * @param contract_noid 合同
     * @param noid
     * @param level         等级， GOOD NORMAL BAD
     * @param content       评价内容 150字
     * @param photos        照片数组
     */
    private String level = "GOOD";
    private String content;
    private String contract_noid;
    private String noid;
    private Contract contract;

    @Override
    protected int getLayoutResId() {
        return R.layout.aty_evaluate;
    }

    @Override
    protected void initialized() {
        contract = new Contract();
    }

    @Override
    protected void requestData() {
        showProgressDialog();
        contract_noid = getIntent().getStringExtra("contract_noid");
        noid = application.getUserInfo().get("noid");
        contract.getSimpleContract(contract_noid, noid, this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (TextUtils.equals(getIntent().getStringExtra("flag"), "edit")) {
            mActionBar.setTitle("修改评价");
        }
        textViews = new TextView[]{tvHigh, tvMedium, tvLow};


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
    public boolean onCreateOptionsMenu(Menu menu) {
        if (TextUtils.equals(getIntent().getStringExtra("flag"), "edit")) {
            getMenuInflater().inflate(R.menu.menu_edit, menu);
        } else {
            getMenuInflater().inflate(R.menu.menu_publish, menu);
        }

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_edit:
            case R.id.menu_publish:
                content = editOther.getText().toString().trim();
                if (TextUtils.isEmpty(content)) {
                    showToast("请输入评价内容");
                    break;
                }
                ImageLoader imageLoader = new ImageLoader();
                for (int i = 1; i < ListUtils.getSize(selectImagePath); i++) {
                    if (selectImagePath.get(i).contains("http")) {
                        final int finalI = i;
                        imageLoader.loadFile(selectImagePath.get(i + 1), new Callback.CacheCallback<File>() {
                            @Override
                            public boolean onCache(RequestParams requestParams, File file) {
                                if (finalI < ListUtils.getSize(selectImagePath) - 1) {
                                    try {
                                        String name = file.getName();
                                        String replace = file.getPath().replace(name, "");
                                        File tempFile = File.createTempFile(file.getName(), ".png", new File(replace));
                                        LogUtil.e(tempFile.getAbsolutePath());
                                        Bitmap bmp = BitmapFactory.decodeFile(file.getPath());
                                        FileOutputStream fileOutStream = null;
                                        fileOutStream = new FileOutputStream(tempFile);
                                        bmp.compress(Bitmap.CompressFormat.PNG, 100, fileOutStream);  //把位图输出到指定的文件中
                                        fileOutStream.flush();
                                        fileOutStream.close();
                                        selectImagePath.set(finalI + 1, tempFile.getAbsolutePath());
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
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
                            public void onCancelled(Callback.CancelledException e) {

                            }

                            @Override
                            public void onFinished() {

                            }
                        });
                    }
                }
                showProgressDialog();
                contract.assess(contract_noid, noid, level, content, selectImagePath, this);
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onComplete(RequestParams params, String result) {
        if (params.getUri().contains("Contract/getSimpleContract")) {
            final Map<String, String> map = JSONUtils.parseDataToMap(result);
            tvNoid.setText(contract_noid);
            tvTotal.setText("￥" + map.get("amount"));
            tvSettlement.setText("￥" + map.get("settle_amount"));
            tvStartDate.setText(map.get("contract_starttime"));
            tvEndDate.setText(map.get("contract_endtime"));
            tvPayment.setText(map.get("settle_type_name"));
            tvWork.setText(map.get("skill_name"));
            tvPrice.setText(map.get("subtotal"));
            if (ListUtils.isEmpty(selectImagePath)) {
                selectImagePath.add("plus");
            }
            tvHigh.setSelected(true);
            if (imgGridAdapter == null) {
                imgGridAdapter = new ImgGridAdapter();
                imgs.setAdapter(imgGridAdapter);
            }
            final Map<String, String> origin_data = JSONUtils.parseKeyAndValueToMap(map.get("origin_data"));

            if (!MapUtils.isEmpty(origin_data)) {
                if (TextUtils.equals(origin_data.get("level"), "GOOD")) {
                    tvHigh.setSelected(true);
                    tvMedium.setSelected(false);
                    tvLow.setSelected(false);

                } else if (TextUtils.equals(origin_data.get("level"), "NORMAL")) {
                    tvMedium.setSelected(true);
                    tvHigh.setSelected(false);
                    tvLow.setSelected(false);
                } else {
                    tvLow.setSelected(true);
                    tvMedium.setSelected(false);
                    tvHigh.setSelected(false);
                }
                editOther.setText(origin_data.get("content"));
                Editable text = editOther.getText();
                Selection.setSelection(text, text.length());
                selectImagePath.addAll(JsonArryToList.strList(origin_data.get("photos")));
                imgGridAdapter.notifyDataSetChanged();
            }


//            selectImagePath.addAll(JsonArryToList.strList(origin_data.get("photos")));


        } else if (params.getUri().contains("Contract/assess")) {
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

    @Event({R.id.evaluate_high, R.id.evaluate_medium, R.id.evaluate_low})
    private void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.evaluate_high:
                position = 0;
                level = "GOOD";
                break;
            case R.id.evaluate_medium:
                position = 1;
                level = "NORMAL";
                break;
            case R.id.evaluate_low:
                position = 2;
                level = "BAD";
                break;
        }
        setEvaluate(position);
    }

    private void setEvaluate(int position) {
        for (int i = 0; i < 3; i++) {
            if (position == i) {
                textViews[position].setSelected(true);
            } else {
                textViews[i].setSelected(false);
            }
        }
    }


    private class ImgGridAdapter extends BaseAdapter {

        private final ImageLoader imageLoader;
        private ViewHodler viewHodler;

        public ImgGridAdapter() {
            imageLoader = new ImageLoader();
            ImageOptions options = new ImageOptions.Builder().setLoadingDrawableId(R.drawable.img_index).setUseMemCache(true)
                    .setImageScaleType(ImageView.ScaleType.FIT_XY).build();
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
                convertView = LayoutInflater.from(EvaluateAty.this).inflate(R.layout.listitem_new_job_order, parent, false);
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
                if (selectImagePath.get(position + 1).contains("http")) {
                    imageLoader.loadFile(selectImagePath.get(position + 1), new Callback.CacheCallback<File>() {
                        @Override
                        public boolean onCache(RequestParams requestParams, File file) {
                            if (position < ListUtils.getSize(selectImagePath) - 1) {
                                try {
                                    String path = file.getPath();
                                    String name = file.getName();
                                    String replace = file.getPath().replace(name, "");
                                    File tempFile = File.createTempFile(file.getName(), ".png", new File(replace));
//                                    new File()
                                    LogUtil.e(tempFile.getAbsolutePath());
                                    Bitmap bmp = BitmapFactory.decodeFile(file.getPath());
                                    FileOutputStream fileOutStream = null;
                                    fileOutStream = new FileOutputStream(tempFile);
                                    bmp.compress(Bitmap.CompressFormat.PNG, 100, fileOutStream);  //把位图输出到指定的文件中
                                    fileOutStream.flush();
                                    fileOutStream.close();
                                    selectImagePath.set(position + 1, tempFile.getAbsolutePath());
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
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
                        public void onCancelled(Callback.CancelledException e) {

                        }

                        @Override
                        public void onFinished() {

                        }
                    });
                }
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
