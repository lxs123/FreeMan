package com.toocms.freeman.ui.recruitment.myjoborder;

import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.toocms.frame.image.ImageLoader;
import com.toocms.freeman.R;
import com.toocms.freeman.config.JsonArryToList;
import com.toocms.freeman.https.Hire;
import com.toocms.freeman.ui.BaseAty;
import com.toocms.freeman.ui.index.ViewFeedbackAty;
import com.toocms.freeman.ui.recruitment.joborder.NewJobOrderAty;
import com.toocms.freeman.ui.recruitment.joborder.ReleaseJOAty;
import com.toocms.freeman.ui.view.ImagePagerActivity;
import com.toocms.freeman.ui.view.MyGridView;
import com.toocms.freeman.ui.view.MyImageDialog;
import com.zero.autolayout.utils.AutoUtils;

import org.xutils.http.RequestParams;
import org.xutils.image.ImageOptions;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cn.zero.android.common.util.JSONUtils;
import cn.zero.android.common.util.ListUtils;
import cn.zero.android.common.view.FButton;

/**
 * 招工单详情页  （JO -- job order ）
 * Created by admin on 2017/3/27.
 */

public class JODetailAty extends BaseAty {
    @ViewInject(R.id.jo_detail_noid)
    TextView tvNoid;
    @ViewInject(R.id.jo_detail_skill)
    TextView tvSkill;
    @ViewInject(R.id.jo_detail_person)
    TextView tvPerson;
    @ViewInject(R.id.jo_detail_start_date)
    TextView tvStartDate;
    @ViewInject(R.id.jo_detail_end_date)
    TextView tvEndDate;
    @ViewInject(R.id.jo_detail_start_time)
    TextView tvStartTime;
    @ViewInject(R.id.jo_detail_end_time)
    TextView tvEndTime;
    @ViewInject(R.id.jo_detail_week)
    TextView tvWeek;
    @ViewInject(R.id.jo_detail_re_time)
    TextView tvReTime;
    @ViewInject(R.id.jo_detail_price)
    TextView tvPrice;
    @ViewInject(R.id.jo_detail_total)
    TextView tvTotal;
    @ViewInject(R.id.jo_detail_payment)
    TextView tvPayment;
    @ViewInject(R.id.jo_detail_address)
    TextView tvAddress;
    @ViewInject(R.id.jo_detail_insurance)
    TextView tvInsurance;
    @ViewInject(R.id.jo_detail_lunch)
    TextView tvLunch;
    @ViewInject(R.id.jo_detail_live)
    TextView tvLive;
    @ViewInject(R.id.jo_detail_utils)
    TextView tvUtils;
    @ViewInject(R.id.jo_detail_transportation)
    TextView tvTransportation;
    @ViewInject(R.id.jo_detail_communications)
    TextView tvCommunications;
    @ViewInject(R.id.jo_detail_standard)
    TextView tvStandard;
    @ViewInject(R.id.jo_detail_other)
    TextView tvOther;
    @ViewInject(R.id.jo_detail_btn1)
    Button btn1;
    @ViewInject(R.id.jo_detail_btn2)
    Button btn2;
    @ViewInject(R.id.jo_detail_fbtn)
    FButton Fbtn;
    @ViewInject(R.id.jo_detail_imgs)
    private MyGridView imgs;
    @ViewInject(R.id.list_feedback_spot)
    ImageView imgvSpot;
    @ViewInject(R.id.jo_detail_imgs_text)
    TextView tvImgsNone;
    private final int NOT_RELEASE = 1;
    private final int NOT_FEEDBACK = 2;
    private final int HAVE_FEEDBACK = 4;
    private final int COMPLETE = 3;
    private Hire hire;

    private int state;
    private List<String> selectImagePath;
    public static boolean isRelease = false;

    /**
     * 删除招工单[remove]
     *
     * @param noid    用户编号
     * @param hire_id 招工单id
     */
    private String noid;
    private String hire_id;
    public static String hire_noid;
    private String coor_to_view;

    @Override
    protected int getLayoutResId() {
        return R.layout.aty_jo_detail;
    }

    @Override
    protected void initialized() {
        hire = new Hire();
    }

    @Override
    protected void requestData() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActionBar.setTitle("招工单详情");
        state = getIntent().getIntExtra("state", 1);
        hire_noid = getIntent().getStringExtra("hire_noid");
        noid = application.getUserInfo().get("noid");
        hire_id = getIntent().getStringExtra("hire_id");
        isRelease = false;
        coor_to_view = getIntent().getStringExtra("coor_to_view");
    }

    @Override
    protected void onResume() {
        super.onResume();

        showProgressDialog();

        if (TextUtils.isEmpty(hire_noid) && !isRelease) {
            initBtn();
            hire.single(noid, hire_id, this);
        } else {
            hire.detail(noid, hire_noid, this);
        }
    }

    private void initBtn() {
        if (state == NOT_FEEDBACK || state == HAVE_FEEDBACK) {
            btn1.setVisibility(View.VISIBLE);
            btn1.setText("查看反馈");
            btn2.setText("复制");
            Fbtn.setText("修改");

            if (TextUtils.equals(coor_to_view, "0")) {
                imgvSpot.setVisibility(View.GONE);
            } else {
                imgvSpot.setVisibility(View.VISIBLE);
            }

        } else if (state == COMPLETE) {
            btn1.setVisibility(View.GONE);
            btn2.setText("查看反馈");
            Fbtn.setText("复制");
        } else {
            btn1.setText("复制");
            btn2.setText("修改");
            Fbtn.setText("发布");
        }
    }


    @Override
    public void onComplete(RequestParams params, String result) {
        if (params.getUri().contains("Hire/detail")) {
            final Map<String, String> map = JSONUtils.parseDataToMap(result);
            tvNoid.setTextColor(getResources().getColor(R.color.clr_default_text));
            tvNoid.setText(map.get("hire_noid"));
            selectImagePath = JsonArryToList.strList(map.get("photos"));
            imgs.setAdapter(new ImgGridAdapter());
            if (ListUtils.isEmpty(selectImagePath)) {
                tvImgsNone.setVisibility(View.VISIBLE);
            } else {
                tvImgsNone.setVisibility(View.GONE);
            }
            Map<String, String> mapHire = JSONUtils.parseKeyAndValueToMap(map.get("hire"));
            ArrayList<String> skillName = new ArrayList<>();
            ArrayList<Map<String, String>> skill = JSONUtils.parseKeyAndValueToMapList(mapHire.get("skill_list"));
            for (int i = 0; i < ListUtils.getSize(skill); i++) {
                skillName.add(skill.get(i).get("name"));
            }
            tvSkill.setText(ListUtils.join(skillName));
            tvOther.setText(mapHire.get("others_text"));
            tvStandard.setText(mapHire.get("audit"));
            tvPerson.setText(map.get("staff"));
            tvPayment.setText(mapHire.get("settle_type_name"));
            if (mapHire.get("unit_name").contains("每")) {
                tvPrice.setText("￥" + mapHire.get("subtotal") + mapHire.get("unit_name").replace("每", "/"));
            } else {
                tvPrice.setText("￥" + mapHire.get("subtotal") + "/" + mapHire.get("unit_name"));
            }

            tvTotal.setText("￥" + mapHire.get("amount"));
            tvStartDate.setText(mapHire.get("contract_starttime"));
            tvEndDate.setText(mapHire.get("contract_endtime"));
            tvStartTime.setText(mapHire.get("work_starttime"));
            tvEndTime.setText(mapHire.get("work_endtime"));
            tvReTime.setText(mapHire.get("hire_endtime"));
//            ArrayList<Map<String, String>> week_name = JSONUtils.parseKeyAndValueToMapList(mapHire.get("week"));
//            StringBuffer stringBuffer = new StringBuffer();
//            for (int j = 0; j < ListUtils.getSize(week_name); j++) {
//                stringBuffer.append(week_name.get(j).get("name") + ",");
//
//            }
//            if (stringBuffer.length() > 0) {
//                stringBuffer.deleteCharAt(stringBuffer.length() - 1);
////                    showToast("您选择了" + stringBuffer);
//                tvWeek.setText(stringBuffer);
//            }
            tvWeek.setText(ListUtils.join(JsonArryToList.strList(mapHire.get("work_week_name"))));
            tvAddress.setText(map.get("ress"));
            if (TextUtils.equals(mapHire.get("is_correspondence"), 0 + "")) {
                tvCommunications.setText("否");
            } else tvCommunications.setText("是");
            if (TextUtils.equals(mapHire.get("is_dine"), "0")) tvLunch.setText("否");
            else tvLunch.setText("是");
            if (TextUtils.equals(mapHire.get("is_lodging"), "0")) tvLive.setText("否");
            else tvLive.setText("是");
            if (TextUtils.equals(mapHire.get("is_tool"), "0")) tvUtils.setText("否");
            else tvUtils.setText("是");
            if (TextUtils.equals(mapHire.get("is_transportation_expenses"), "0"))
                tvTransportation.setText("否");
            else tvTransportation.setText("是");
            if (TextUtils.equals(mapHire.get("is_insurance"), "0")) tvInsurance.setText("否");
            else tvInsurance.setText("是");
            state = Integer.parseInt(mapHire.get("status"));
            coor_to_view = map.get("coor_to_view");
            initBtn();
        } else if (params.getUri().contains("Hire/remove")) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    finish();
                }
            }, 1500);
        } else if (params.getUri().contains("Hire/single")) {
            final Map<String, String> map = JSONUtils.parseDataToMap(result);
            tvNoid.setText("暂无招工单号");
            tvNoid.setTextColor(Color.parseColor("#fb4a4a"));
            selectImagePath = JsonArryToList.strList(map.get("photos"));
            if (ListUtils.isEmpty(selectImagePath)) {
                tvImgsNone.setVisibility(View.VISIBLE);
            } else {
                tvImgsNone.setVisibility(View.GONE);
            }
            imgs.setAdapter(new ImgGridAdapter());
            ArrayList<String> skillName = new ArrayList<>();
            ArrayList<Map<String, String>> skill = JSONUtils.parseKeyAndValueToMapList(map.get("skill"));
            for (int i = 0; i < ListUtils.getSize(skill); i++) {
                skillName.add(skill.get(i).get("name"));
            }
            tvSkill.setText(ListUtils.join(skillName));
            tvOther.setText(map.get("others_text"));
            tvStandard.setText(map.get("audit"));
            tvPerson.setText(map.get("staff"));
            tvPayment.setText(map.get("settle_type_name"));
            ArrayList<Map<String, String>> unitMap = JSONUtils.parseKeyAndValueToMapList(map.get("unit"));
            if (unitMap.get(0).get("name").contains("每")) {
                tvPrice.setText("￥" + map.get("subtotal") + unitMap.get(0).get("name").replace("每", "/"));
            } else {
                tvPrice.setText("￥" + map.get("subtotal") + "/" + unitMap.get(0).get("name"));
            }

            tvTotal.setText("￥" + map.get("amount"));
            tvStartDate.setText(map.get("contract_starttime"));
            tvEndDate.setText(map.get("contract_endtime"));
            tvStartTime.setText(map.get("work_starttime"));
            tvEndTime.setText(map.get("work_endtime"));
            tvReTime.setText(map.get("hire_endtime"));
            ArrayList<Map<String, String>> week_name = JSONUtils.parseKeyAndValueToMapList(map.get("week"));
            StringBuffer stringBuffer = new StringBuffer();
            for (int j = 0; j < ListUtils.getSize(week_name); j++) {
                stringBuffer.append(week_name.get(j).get("name") + ",");

            }
            if (stringBuffer.length() > 0) {
                stringBuffer.deleteCharAt(stringBuffer.length() - 1);
//                    showToast("您选择了" + stringBuffer);
                tvWeek.setText(stringBuffer);
            }
//            tvWeek.setText(ListUtils.join(JsonArryToList.strList(map.get("work_week_name"))));
            tvAddress.setText(map.get("ress"));
            if (TextUtils.equals(map.get("is_correspondence"), 0 + "")) {
                tvCommunications.setText("否");
            } else tvCommunications.setText("是");
            if (TextUtils.equals(map.get("is_dine"), "0")) tvLunch.setText("否");
            else tvLunch.setText("是");
            if (TextUtils.equals(map.get("is_lodging"), "0")) tvLive.setText("否");
            else tvLive.setText("是");
            if (TextUtils.equals(map.get("is_tool"), "0")) tvUtils.setText("否");
            else tvUtils.setText("是");
            if (TextUtils.equals(map.get("is_transportation_expenses"), "0"))
                tvTransportation.setText("否");
            else tvTransportation.setText("是");
            if (TextUtils.equals(map.get("is_insurance"), "0")) tvInsurance.setText("否");
            else tvInsurance.setText("是");
        }
        super.onComplete(params, result);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        state = getIntent().getIntExtra("state", 0);
        if (state == NOT_RELEASE)
            getMenuInflater().inflate(R.menu.menu_delete, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_delete:
                showDialog(null, "确定要删除此招工信息?", "确定", "取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        noid = application.getUserInfo().get("noid");
                        hire_id = getIntent().getStringExtra("hire_id");
                        hire.remove(noid, hire_id, JODetailAty.this);
                    }
                }, null);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Event({R.id.jo_detail_btn1, R.id.jo_detail_btn2, R.id.jo_detail_fbtn, R.id.jo_detail_skill_click
            , R.id.jo_detail_week_click})
    private void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.jo_detail_btn1:
                if (state == NOT_RELEASE) {
                    Bundle bundle = new Bundle();
                    bundle.putString("hire_id", hire_id);
                    bundle.putString("flag", "copy");
                    startActivity(NewJobOrderAty.class, bundle);
                } else if (state == NOT_FEEDBACK || state == HAVE_FEEDBACK) {
                    imgvSpot.setVisibility(View.GONE);
                    Bundle bundle = new Bundle();
                    bundle.putString("hire_noid", getIntent().getStringExtra("hire_noid"));
                    startActivity(ViewFeedbackAty.class, bundle);

                }
                break;
            case R.id.jo_detail_btn2:
                if (state == NOT_RELEASE) {
                    Bundle bundle = new Bundle();
                    bundle.putString("hire_id", hire_id);
                    bundle.putString("flag", "edit");
                    startActivity(NewJobOrderAty.class, bundle);
                } else if (state == NOT_FEEDBACK || state == HAVE_FEEDBACK) {
                    Bundle bundle = new Bundle();
                    bundle.putString("hire_id", hire_id);
                    bundle.putString("flag", "copy");
                    startActivity(NewJobOrderAty.class, bundle);
                } else if (state == COMPLETE) {
                    Bundle bundle = new Bundle();
                    bundle.putString("hire_noid", getIntent().getStringExtra("hire_noid"));
                    startActivity(ViewFeedbackAty.class, bundle);
                }
                break;
            case R.id.jo_detail_fbtn:
                if (state == NOT_RELEASE) {
                    Bundle bundle = new Bundle();
                    bundle.putString("hire_id", hire_id);
                    startActivity(ReleaseJOAty.class, bundle);
                } else if (state == NOT_FEEDBACK || state == HAVE_FEEDBACK) {
                    Bundle bundle = new Bundle();
                    bundle.putString("hire_id", hire_id);
                    bundle.putString("flag", "edit");
                    startActivity(NewJobOrderAty.class, bundle);
                } else if (state == COMPLETE) {
                    Bundle bundle = new Bundle();
                    bundle.putString("hire_id", hire_id);
                    bundle.putString("flag", "copy");
                    startActivity(NewJobOrderAty.class, bundle);
                }
                break;
            case R.id.jo_detail_skill_click:
                setDialogText(tvSkill.getText().toString());
                break;
            case R.id.jo_detail_week_click:
                setDialogText(tvWeek.getText().toString());
                break;
        }
    }

    private void setDialogText(String str) {
        MyImageDialog myImageDialog = new MyImageDialog(this, R.style.Dialog_Fullscreen, 0, 0, false, "text", str);
        myImageDialog.show();
        WindowManager windowManager = this.getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        WindowManager.LayoutParams lp = myImageDialog.getWindow().getAttributes();
        lp.width = (int) (display.getWidth()); //设置宽度
        lp.height = display.getHeight();
        myImageDialog.getWindow().setAttributes(lp);
    }

    private class ImgGridAdapter extends BaseAdapter {

        private ViewHodler viewHodler;

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
                convertView = LayoutInflater.from(JODetailAty.this).inflate(R.layout.listitem_new_job_order, parent, false);
                x.view().inject(viewHodler, convertView);
                AutoUtils.autoSize(convertView);
                convertView.setTag(viewHodler);
            } else {
                viewHodler = (ViewHodler) convertView.getTag();
            }
            viewHodler.imgvDel.setVisibility(View.GONE);
            ImageLoader imageLoader = new ImageLoader();
            ImageOptions options = new ImageOptions.Builder().setLoadingDrawableId(R.drawable.icon_loading).build();
            imageLoader.setImageOptions(options);
            imageLoader.disPlay(viewHodler.imgv, selectImagePath.get(position));
            viewHodler.imgv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    bundle.putStringArrayList(ImagePagerActivity.EXTRA_IMAGE_URLS, (ArrayList<String>) selectImagePath);
                    bundle.putInt(ImagePagerActivity.EXTRA_IMAGE_INDEX, position);
                    startActivity(ImagePagerActivity.class, bundle);
                    overridePendingTransition(0, 0);
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
