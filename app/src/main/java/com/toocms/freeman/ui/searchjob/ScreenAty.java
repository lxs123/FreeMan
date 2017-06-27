package com.toocms.freeman.ui.searchjob;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.toocms.freeman.R;
import com.toocms.freeman.https.Hire;
import com.toocms.freeman.https.Sys;
import com.toocms.freeman.ui.BaseAty;
import com.toocms.freeman.ui.index.SelectedRangeAty;
import com.toocms.freeman.ui.index.SkillAty;
import com.toocms.freeman.ui.util.WorkOrder;
import com.toocms.freeman.ui.view.MyGridView;
import com.zero.autolayout.utils.AutoUtils;

import org.xutils.http.RequestParams;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import cn.qqtheme.framework.picker.DatePicker;
import cn.qqtheme.framework.picker.TimePicker;
import cn.zero.android.common.util.JSONUtils;
import cn.zero.android.common.util.ListUtils;
import cn.zero.android.common.util.MapUtils;

public class ScreenAty extends BaseAty {
    private final int SEL_RANGE = 0x0001;
    @ViewInject(R.id.start_day)
    TextView tvStartDay;
    @ViewInject(R.id.end_day)
    TextView tvEndDay;
    @ViewInject(R.id.start_time)
    TextView tvStartTime;
    @ViewInject(R.id.end_time)
    TextView tvEndTime;
    @ViewInject(R.id.screen_min_price)
    EditText editMinPrice;
    @ViewInject(R.id.screen_max_price)
    EditText editMaxPrice;
    @ViewInject(R.id.screen_account_id)
    EditText editAccountId;
    //    招工单号
    @ViewInject(R.id.screen_recr_id)
    EditText editRecrId;
    @ViewInject(R.id.new_jo_week)
    private MyGridView week;
    @ViewInject(R.id.new_jo_week_content)
    private LinearLayout linlayWeek;
    @ViewInject(R.id.screen_week)
    private TextView tvWeek;
    @ViewInject(R.id.tvWork)
    private TextView tvWork;
    @ViewInject(R.id.select_range_tv)
    private TextView tvRange;
    @ViewInject(R.id.screen_keywords)
    EditText etxtKeyword;

    //存工作相关
    private List<String> skillList;
    private ArrayList<Map<String, String>> skillItemData;


    private int startY;
    private int startM;
    private int startD;
    private int endY;
    private int endM;
    private int endD;
    private int startH;
    private int startMinute;
    private int endH;
    private int endMinute;
    private String startHour;
    private WeekGridAdapter adapter;

    //接口相关
    private Hire mHire;
    private Sys mSys;

    //要给下个页面接口传递的数据
    private String intWeekId;
    //    private String intSkillId;
    private String distanceStr;
    private String provinceIdStr;
    private String cityIdStr;
    private String areaIdStr;
    private String contractStartTimeStr;
    private String contractEndTimeStr;
    private String workStartTimeStr;
    private String workEndTimeStr;

    boolean isWeek = false;
    private String[] weeks;
    //从接口初始化的周历时间
    private ArrayList<Map<String, String>> mWeekList;
    //保存周历时间id
    private List<String> weekIdList = new ArrayList<>();
    private int mWidth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActionBar.setTitle("筛选");
        WorkOrder.getInstance().clear();

        editAccountId.requestFocus();
        editRecrId.requestFocus();
        editMaxPrice.requestFocus();
        editMinPrice.requestFocus();

        //初始化表单数据 从hire接口得到工作周历
        mHire.getFormData(this);
        //初始化工作接口
        mSys.getSkillList("0", "0", this);

        mWidth = getWindowManager().getDefaultDisplay().getWidth();

        linlayWeek.setVisibility(View.GONE);
        week.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                adapter.setIsWeeks(position);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        List<String> list = new ArrayList<>();
        skillList = new ArrayList<>();
        Map<String, Map<String, List<Map<String, String>>>> workOrder1 = WorkOrder.getInstance().getOrder1();
        if (MapUtils.isEmpty(workOrder1)) {
            tvWork.setText("");
        } else {
            if (ListUtils.isEmpty(skillItemData)) return;
            for (int j = 0; j < ListUtils.getSize(skillItemData); j++) {
                Map<String, String> map = skillItemData.get(j);
                if (workOrder1.containsKey(map.get("skill_id"))) {
                    Map<String, List<Map<String, String>>> workOrder2 = workOrder1.get(map.get("skill_id"));
                    Iterator iterator = workOrder2.entrySet().iterator();
                    while (iterator.hasNext()) {
                        Map.Entry entry = (Map.Entry) iterator.next();
                        for (int i = 0; i < ListUtils.getSize(workOrder2.get(entry.getKey())); i++) {
//                            if (ListUtils.getSize(list) >= 3) break;
                            Iterator iterator2 = workOrder2.get(entry.getKey()).get(i).entrySet().iterator();
                            while (iterator2.hasNext()) {
                                Map.Entry entry2 = (Map.Entry) iterator2.next();
                                list.add(entry2.getValue().toString());
                                skillList.add(entry2.getKey().toString());
                            }
                        }
                    }
                }
            }
            if (ListUtils.isEmpty(list)) tvWork.setText("");
            else tvWork.setText(ListUtils.join(list));
        }

//        //把skill集合中的内容交给数组
//        if (ListUtils.getSize(skillList) != 0) {
//            intSkillId = new String();
//            intSkillId = ListUtils.join(skillList);
//        } else {
//            intSkillId = new String();
//        }

    }


    @Override
    public void onComplete(RequestParams params, String result) {
        if (params.getUri().contains("Hire/getFormData")) {
            String weekStr = JSONUtils.parseDataToMap(result).get("week");
            Log.e("***", weekStr);
            mWeekList = JSONUtils.parseKeyAndValueToMapList(weekStr);
            weeks = new String[ListUtils.getSize(mWeekList)];
            for (int i = 0; i < ListUtils.getSize(mWeekList); i++) {
                weeks[i] = mWeekList.get(i).get("name");
            }
            upDateDataUI();
        }
        if (params.getUri().contains("Sys/getSkillList")) {
            skillItemData = JSONUtils.parseDataToMapList(result);
        }

        super.onComplete(params, result);
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.aty_screen;
    }

    @Override
    protected void initialized() {
        mHire = new Hire();
        mSys = new Sys();
    }

    @Override
    protected void requestData() {

    }

    private void upDateDataUI() {
        if (adapter == null) {
            adapter = new WeekGridAdapter();
            week.setAdapter(adapter);
        } else
            adapter.notifyDataSetChanged();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            return;
        }
        switch (requestCode) {
            case SEL_RANGE:
                provinceIdStr = data.getStringExtra("province_id");
                cityIdStr = data.getStringExtra("city_id");
                areaIdStr = data.getStringExtra("area_id");
                distanceStr = data.getStringExtra("distance");
                if (TextUtils.isEmpty(provinceIdStr)) {
                    tvRange.setText(distanceStr);
                } else if (TextUtils.isEmpty(distanceStr)) {
                    tvRange.setText(data.getStringExtra("ress"));
                }
                break;
        }
    }

    //按键监听
    @Event({R.id.screen_min_price, R.id.screen_max_price, R.id.screen_account_id, R.id.screen_recr_id, R.id.sure_fbtn, R.id.new_jo_week_click, R.id.select_range_tv_click, R.id.select_job_tv
            , R.id.start_day_click, R.id.end_day_click, R.id.start_time_click, R.id.end_time_click
            , R.id.screen_week_sure, R.id.screen_clear})
    private void onClick(View view) {

        switch (view.getId()) {
//


            case R.id.sure_fbtn:
                //确定按钮
                Bundle bundle = new Bundle();
                bundle.putString("work_week", intWeekId);
                bundle.putString("min_price", editMinPrice.getText().toString());
                bundle.putString("max_price", editMaxPrice.getText().toString());
                bundle.putString("cap_noid", editAccountId.getText().toString());
                bundle.putString("skill_id", ListUtils.join(skillList));
                // TODO: 2017/4/20 距离 省 市 区
                bundle.putString("distance", distanceStr);
                bundle.putString("province_id", provinceIdStr);
                bundle.putString("city_id", cityIdStr);
                bundle.putString("area_id", areaIdStr);
                bundle.putString("contract_starttime", contractStartTimeStr);
                bundle.putString("contract_endtime", contractEndTimeStr);
                bundle.putString("work_starttime", workStartTimeStr);
                bundle.putString("work_endtime", workEndTimeStr);
                bundle.putString("hire_noid", editRecrId.getText().toString());
                bundle.putString("keyword", etxtKeyword.getText().toString());
                startActivity(RecruitmentQueryResultAty.class, bundle);
                break;
//            清空
            case R.id.screen_clear:
                tvWeek.setText("");
                for (int i = 0; i < weeks.length; i++) {
                    adapter.isWeeks[i] = false;
                }
                adapter.notifyDataSetChanged();
                tvRange.setText("");
                tvWork.setText("");
                editAccountId.setText("");
                editRecrId.setText("");
                editMaxPrice.setText("");
                editMinPrice.setText("");
                tvStartTime.setText("");
                tvStartDay.setText("");
                tvEndTime.setText("");
                tvEndDay.setText("");
                WorkOrder.getInstance().clear();
                intWeekId = "";
                contractStartTimeStr = "";
                contractEndTimeStr = "";
                workStartTimeStr = "";
                workEndTimeStr = "";
                distanceStr = "";
                skillList = new ArrayList<>();
                provinceIdStr = "";
                cityIdStr = "";
                areaIdStr = "";
                etxtKeyword.setText("");

                break;
            case R.id.new_jo_week_click:
                //工作周历
                isWeek = !isWeek;
                if (isWeek) {
                    linlayWeek.setVisibility(View.VISIBLE);
                } else {
                    linlayWeek.setVisibility(View.GONE);
                }
                break;
            case R.id.screen_week_sure:
                StringBuilder builder = new StringBuilder();
                for (int i = 0; i < weeks.length; i++) {
                    if (adapter.isWeeks[i]) {
                        builder.append(weeks[i] + ",");
                    }
                }
                if (builder.length() != 0) {
                    builder.deleteCharAt(builder.length() - 1);
                }
                tvWeek.setText(builder);
                linlayWeek.setVisibility(View.GONE);
                isWeek = false;
                //把所存周历集合内容转化成数组
                if (ListUtils.getSize(weekIdList) != 0) {
                    intWeekId = new String();
                    intWeekId = ListUtils.join(weekIdList);
                } else {
                    intWeekId = new String();
                }
                break;
            case R.id.select_range_tv_click:
                Bundle bundleSel = new Bundle();
                bundleSel.putString("flag", "sel_area");
                //选定范围
                startActivityForResult(SelectedRangeAty.class, bundleSel, SEL_RANGE);
                break;
            case R.id.select_job_tv:
                //工作按钮
                startActivity(SkillAty.class, null);
                break;
            case R.id.start_day_click:
                onYearMonthDayPicker(1999, 2117, new DatePicker.OnYearMonthDayPickListener() {
                    @Override
                    public void onDatePicked(String year, String month, String day) {
                        startY = Integer.parseInt(year);
                        startM = Integer.parseInt(month);
                        startD = Integer.parseInt(day);
                        tvStartDay.setText(year + "-" + month + "-" + day);
                        contractStartTimeStr = year + "-" + month + "-" + day;
                    }
                });
                break;
            case R.id.end_day_click:

                DatePicker datePicker = onYearMonthDayPicker(2000, 2117, true, new DatePicker.OnYearMonthDayPickListener() {
                    @Override
                    public void onDatePicked(String year, String month, String day) {
                        endY = Integer.parseInt(year);
                        endM = Integer.parseInt(month);
                        endD = Integer.parseInt(day);
                        if (startY != 0) {
                            if (endY > startY) {
                                tvEndDay.setText(year + "-" + month + "-" + day);
                                contractEndTimeStr = year + "-" + month + "-" + day;
                            } else if (endY == startY) {
                                if (endM > startM) {
                                    tvEndDay.setText(year + "-" + month + "-" + day);
                                    contractEndTimeStr = year + "-" + month + "-" + day;
                                } else if (endM == startM && endD >= startD) {
                                    tvEndDay.setText(year + "-" + month + "-" + day);
                                    contractEndTimeStr = year + "-" + month + "-" + day;
                                } else {
                                    showToast("合同截止日期必须大于合同开始日期");
                                }
//                                tvEndDay.setText(year + "-" + month + "-" + day);
//                                contractEndTimeStr = year + "-" + month + "-" + day;
                            } else {
                                showToast("合同截止日期必须大于合同开始日期");
                            }
                        } else {
                            tvEndDay.setText(year + "-" + month + "-" + day);
                            contractEndTimeStr = year + "-" + month + "-" + day;
                        }

                    }
                });
                if (startY != 0)
                    datePicker.setSelectedItem(startY, startM, startD);
                datePicker.show();
                break;
            case R.id.start_time_click:
                onTimePicker(0, 23, new TimePicker.OnTimePickListener() {


                    @Override
                    public void onTimePicked(String hour, String minute) {
                        startH = Integer.parseInt(hour);
                        startHour = hour;
                        startMinute = Integer.parseInt(minute);
                        tvStartTime.setText(hour + ":" + minute);
                        workStartTimeStr = hour + ":" + minute;
                    }
                });
                break;
            case R.id.end_time_click:

                TimePicker timePicker = onTimePicker(0, 23, true, new TimePicker.OnTimePickListener() {
                    @Override
                    public void onTimePicked(String hour, String minute) {
                        endH = Integer.parseInt(hour);
                        endMinute = Integer.parseInt(minute);
                        if (!TextUtils.isEmpty(startHour)) {
                            if (endH > startH) {
                                tvEndTime.setText(hour + ":" + minute);
                                workEndTimeStr = hour + ":" + minute;
                            } else if (endH == startH) {
                                if (endMinute > startMinute) {
                                    tvEndTime.setText(hour + ":" + minute);
                                    workEndTimeStr = hour + ":" + minute;
                                } else {
                                    showToast("工作截止时间必须大于工作开始时间");
                                }
                            } else {
                                showToast("工作截止时间必须大于工作开始时间");
                            }
                        } else {
                            tvEndTime.setText(hour + ":" + minute);
                            workEndTimeStr = hour + ":" + minute;
                        }

                    }
                });
                timePicker.setSelectedItem(startH, startMinute);
                timePicker.show();
                break;
        }
    }


    private class WeekGridAdapter extends BaseAdapter {
        public Boolean[] isWeeks = new Boolean[weeks.length];
        private ViewHolder viewHolder;

        {
            for (int i = 0; i < weeks.length; i++) {
                isWeeks[i] = false;
            }
        }

        public void setIsWeeks(int position) {
            isWeeks[position] = !isWeeks[position];
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return weeks.length;
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
                viewHolder = new ViewHolder();
                convertView = LayoutInflater.from(ScreenAty.this).inflate(R.layout.listitem_njo_week, parent, false);

//                convertView.setLayoutParams(new AbsListView.LayoutParams(mWidth / 4, 100));
                Log.e("***", mWidth / 4 + "/" + parent.getHeight());

                x.view().inject(viewHolder, convertView);
                AutoUtils.autoSize(convertView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            if (isWeeks[position]) {
                viewHolder.tvWeek.setCompoundDrawablesWithIntrinsicBounds(R.drawable.btn_opt, 0, 0, 0);
                //圆点变红了，把他的sid存到集合里
                if (!weekIdList.contains(mWeekList.get(position).get("sid"))) {
                    weekIdList.add(mWeekList.get(position).get("sid"));
                }
            } else {
                viewHolder.tvWeek.setCompoundDrawablesWithIntrinsicBounds(R.drawable.btn_opt_unselected, 0, 0, 0);
                //圆点变没了，把他从集合里弄出来
                if (weekIdList.contains(mWeekList.get(position).get("sid"))) {
                    weekIdList.remove(weekIdList.indexOf(mWeekList.get(position).get("sid")));
                }
            }

            viewHolder.tvWeek.setText(weeks[position]);
            return convertView;
        }

        public class ViewHolder {
            @ViewInject(R.id.list_njo_week)
            TextView tvWeek;
        }
    }
}
