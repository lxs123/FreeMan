package com.toocms.freeman.ui.mine.baseinformation;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.toocms.frame.image.ImageLoader;
import com.toocms.frame.ui.BaseFragment;
import com.toocms.freeman.R;
import com.toocms.freeman.config.JsonArryToList;
import com.toocms.freeman.https.User;
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

import butterknife.Unbinder;
import cn.zero.android.common.util.JSONUtils;
import cn.zero.android.common.util.ListUtils;

/**
 * 个人中心 的 技能信息 完成 页面
 */
public class SkillInfotmationComplete extends BaseFragment {

    @ViewInject(R.id.s_i_c_skill)
    TextView tvSkill;
    @ViewInject(R.id.s_i_c_person)
    TextView tvPerson;
    @ViewInject(R.id.s_i_c_start_date)
    TextView tvStartDate;
    @ViewInject(R.id.s_i_c_end_date)
    TextView tvEndDate;
    @ViewInject(R.id.textView4)
    TextView textView4;
    @ViewInject(R.id.s_i_c_start_time)
    TextView tvStartTime;
    @ViewInject(R.id.s_i_c_end_time)
    TextView tvEndTime;
    @ViewInject(R.id.s_i_c_week)
    TextView tvWeek;
    @ViewInject(R.id.s_i_c_price)
    TextView tvPrice;
    @ViewInject(R.id.s_i_c_payment)
    TextView tvPayment;
    @ViewInject(R.id.s_i_c_area)
    TextView tvArea;
    @ViewInject(R.id.s_i_c_insurance)
    TextView tvInsurance;
    @ViewInject(R.id.s_i_c_lunch)
    TextView tvLunch;
    @ViewInject(R.id.s_i_c_live)
    TextView tvLive;
    @ViewInject(R.id.s_i_c_utils)
    TextView tvUtils;
    @ViewInject(R.id.s_i_c_transportation)
    TextView tvTransportation;
    @ViewInject(R.id.s_i_c_communications)
    TextView tvCommunications;
    @ViewInject(R.id.s_i_c_other)
    TextView tvOther;
    Unbinder unbinder;
    @ViewInject(R.id.new_job_order_imgs)
    private MyGridView imgs;
    private User user;
    /**
     * 获取技能信息内容[getAttrstation]
     *
     * @param noid 用户编号
     */
    private String noid;
    private Map<String, String> map;
    private List<String> capability;
    private ImgGridAdapter imgGridAdapter;
    private String flag;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        flag = getActivity().getIntent().getStringExtra("flag");
        imgs.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Bundle bundle = new Bundle();
                bundle.putStringArrayList(ImagePagerActivity.EXTRA_IMAGE_URLS, (ArrayList<String>) capability);
                bundle.putInt(ImagePagerActivity.EXTRA_IMAGE_INDEX, position);
                startActivity(ImagePagerActivity.class, bundle);
                getActivity().overridePendingTransition(0, 0);
            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();
        showProgressDialog();
        noid = application.getUserInfo().get("noid");
        if (TextUtils.equals(flag, "member_detail")) {
            noid = getActivity().getIntent().getStringExtra("lab_noid");
        }
        if (TextUtils.equals(flag, "wzw1")) {
            noid = getActivity().getIntent().getStringExtra("noid");
        }
        user.getAttrstation(noid, this);
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.fgt_skill_infotmation_complete;
    }

    @Override
    protected void initialized() {
        user = new User();
    }

    @Override
    protected void requestData() {

    }

    @Override
    public void onComplete(RequestParams params, String result) {
        if (params.getUri().contains("User/getAttrstation")) {
            map = JSONUtils.parseDataToMap(result);
            capability = JsonArryToList.strList(map.get("capability"));
            imgGridAdapter = new ImgGridAdapter();
            imgs.setAdapter(imgGridAdapter);
            ArrayList<Map<String, String>> skill = JSONUtils.parseKeyAndValueToMapList(map.get("skill"));
            ArrayList<String> skillName = new ArrayList<>();
            for (int i = 0; i < ListUtils.getSize(skill); i++) {
                skillName.add(skill.get(i).get("name"));
            }
            tvSkill.setText(ListUtils.join(skillName));
            tvPerson.setText(map.get("staff"));
            tvStartDate.setText(map.get("work_startdate"));
            tvEndDate.setText(map.get("work_enddate"));
            tvStartTime.setText(map.get("work_starttime"));
            tvEndTime.setText(map.get("work_endtime"));
            ArrayList<Map<String, String>> weekName = JSONUtils.parseKeyAndValueToMapList(map.get("week_name"));
            ArrayList<String> week = new ArrayList<>();
            for (int i = 0; i < ListUtils.getSize(weekName); i++) {
                week.add(weekName.get(i).get("name"));
            }
            tvWeek.setText(ListUtils.join(week));
            tvPrice.setText("￥" + map.get("subtotal") + "/天");
            tvPayment.setText(map.get("settle_type_name"));
            ArrayList<Map<String, String>> territory = JSONUtils.parseKeyAndValueToMapList(map.get("territory"));
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < ListUtils.getSize(territory); i++) {
                if (!TextUtils.isEmpty(territory.get(i).get("city_name")) &&
                        !TextUtils.isEmpty(territory.get(i).get("area_name")))
                    builder.append(territory.get(i).get("province_name") + "," + territory.get(i).get("city_name")
                            + "," + territory.get(i).get("area_name"));
                else if (TextUtils.isEmpty(territory.get(i).get("city_name")))
                    builder.append(territory.get(i).get("province_name") + "全境");
                else if (TextUtils.isEmpty(territory.get(i).get("area_name")))
                    builder.append(territory.get(i).get("province_name") + "," + territory.get(i).get("city_name")
                            + "全境");
                if (i != ListUtils.getSize(territory) - 1) {
                    builder.append("\n");
                }
            }
            tvArea.setText(builder.toString());
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
            tvOther.setText(map.get("others_text"));

        }
        super.onComplete(params, result);
    }


    @Event({R.id.s_i_c_skill_click, R.id.s_i_c_week_click, R.id.s_i_c_area_click})
    private void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.s_i_c_skill_click:
                setDialogText(tvSkill.getText().toString());
                break;
            case R.id.s_i_c_week_click:
                setDialogText(tvWeek.getText().toString());
                break;
            case R.id.s_i_c_area_click:
                setDialogText(tvArea.getText().toString());
                break;
        }
    }

    private void setDialogText(String str) {
        MyImageDialog myImageDialog = new MyImageDialog(getActivity(), R.style.Dialog_Fullscreen, 0, 0, false, "text", str);
        myImageDialog.show();
        WindowManager windowManager = getActivity().getWindowManager();
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
            return ListUtils.getSize(capability);
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
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                viewHodler = new ViewHodler();
                convertView = LayoutInflater.from(getActivity()).inflate(R.layout.listitem_new_job_order, parent, false);
                x.view().inject(viewHodler, convertView);
                AutoUtils.autoSize(convertView);
                convertView.setTag(viewHodler);
            } else {
                viewHodler = (ViewHodler) convertView.getTag();
            }
            ImageLoader imageLoader = new ImageLoader();
            ImageOptions options = new ImageOptions.Builder().setLoadingDrawableId(R.drawable.icon_loading).setUseMemCache(true).build();
            imageLoader.setImageOptions(options);
            imageLoader.disPlay(viewHodler.imgv, capability.get(position));
            viewHodler.imgvDel.setVisibility(View.GONE);
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
