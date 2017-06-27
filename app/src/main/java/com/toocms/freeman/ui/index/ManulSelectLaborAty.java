package com.toocms.freeman.ui.index;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
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
import cn.zero.android.common.view.FButton;

public class ManulSelectLaborAty extends BaseAty {
    private final int RANGE = 0x0001;
    @ViewInject(R.id.manul_min_price)
    EditText editMinPrice;
    @ViewInject(R.id.manul_max_price)
    EditText editMaxPrice;
    @ViewInject(R.id.manul_member)
    EditText editMember;
    @ViewInject(R.id.manul_skill)
    TextView tvSkill;
    @ViewInject(R.id.manul_area)
    TextView tvArea;
    @ViewInject(R.id.start_day)
    TextView tvStartDay;
    @ViewInject(R.id.end_day)
    TextView tvEndDay;
    @ViewInject(R.id.start_time)
    TextView tvStartTime;
    @ViewInject(R.id.end_time)
    TextView tvEndTime;
    @ViewInject(R.id.query_fbtn)
    private FButton queryFBtn;
    @ViewInject(R.id.new_jo_week)
    private MyGridView week;
    @ViewInject(R.id.new_jo_week_content)
    private LinearLayout linlayWeek;
    @ViewInject(R.id.manul_week_content)
    TextView tvWeek;
    @ViewInject(R.id.end_time_divid)
    View vEndDivid;
    @ViewInject(R.id.end_day_name)
    TextView tvEndName;
    @ViewInject(R.id.start_day_name)
    TextView tvStartName;
    boolean isWeek = false;
    private String[] weeks = new String[]{"周一", "周二", "周三", "周四", "周五", "周六", "周日"};
    /**
     * 搜索劳方列表[seekLabs]
     *
     * @param noid             用户编号
     * @param hire_id          招工单id
     * @param page             分页，默认为1
     * @param keywords         关键字
     * @param min_price        最小价格
     * @param max_price        最大价格
     * @param code             搜索劳方编号
     * @param distance         距离
     * @param contract_start   合同开始日期
     * @param contract_endtime 合同结束日期
     * @param work_starttime   每天工作开始时间
     * @param work_endtime     每天工作结束时间
     * @param sort             排序：
     * city_id     市id
     * area_id     区id
     * province_id    省
     * - cost: 成单量最多
     * - level: 信用最高
     * - distance: 距离最近
     * - maxprice: 单价高到底
     * - minprice: 单价低到高
     */
    private String province_id;
    private String city_id;
    //    市id
    private String area_id;
//    区id

    private String noid;
    private String hire_id;
    private String page;
    private String keywords;
    private String min_price;
    private String max_price;
    private String code;
    private String distance;
    private String contract_start;
    private String contract_endtime;
    private String work_starttime;
    private String work_endtime;
    private String sort;
    private Hire hire;
    private ArrayList<Map<String, String>> weekList;
    private WeekGridAdapter weekGridAdapter;
    private List<String> weekID = new ArrayList<>();
    private String startMonth;
    private String startDay;
    private int startD;
    private int startM;
    private int startY;
    private int endY;
    private int endM;
    private int endD;
    private String endMonth;
    private String endDay;
    private int startH;
    private String startHour;
    private int startMinute;
    private String startMinutes;
    private int endH;
    private int endMinute;
    private String endHour;
    private String endMinutes;
    private ArrayList<String> skillList;
    private List<Map<String, String>> skillItemData;
    private Sys sys;

    @Override
    protected int getLayoutResId() {
        return R.layout.aty_manul_select_labor;
    }

    @Override
    protected void initialized() {
        hire = new Hire();
        sys = new Sys();
    }

    @Override
    protected void requestData() {
        hire.getFormData(this);
        sys.getSkillList("0", "0", this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActionBar.setTitle("手动选择劳方");
        WorkOrder.getInstance().clear();
        vEndDivid.setVisibility(View.GONE);
        linlayWeek.setVisibility(View.GONE);
        tvStartName.setText("工作开始时间");
        tvEndName.setText("工作截止时间");
        week.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                weekGridAdapter.setIsWeek(position);
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
            tvSkill.setText("");
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
            if (ListUtils.isEmpty(list)) tvSkill.setText("");
            else tvSkill.setText(ListUtils.join(list));

        }
    }


    @Override
    public void onComplete(RequestParams params, String result) {
        if (params.getUri().contains("Hire/getFormData")) {
            Map<String, String> map = JSONUtils.parseDataToMap(result);
            weekList = JSONUtils.parseKeyAndValueToMapList(map.get("week"));
            weekGridAdapter = new WeekGridAdapter();
            week.setAdapter(weekGridAdapter);
//            settle = JSONUtils.parseKeyAndValueToMapList(map.get("settle"));
        } else if (params.getUri().contains("Sys/getSkillList")) {
            skillItemData = JSONUtils.parseDataToMapList(result);
        }
        super.onComplete(params, result);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            return;
        }
        switch (requestCode) {
            case RANGE:
                province_id = data.getStringExtra("province_id");
                city_id = data.getStringExtra("city_id");
                area_id = data.getStringExtra("area_id");
                distance = data.getStringExtra("distance");
                if (TextUtils.isEmpty(distance)) {
                    tvArea.setText(data.getStringExtra("ressStr"));
                } else {
                    tvArea.setText(data.getStringExtra("distanceStr"));
                }

                break;
        }
    }

    @Event({R.id.new_jo_week_click, R.id.query_fbtn, R.id.manul_sure
            , R.id.manul_skill_click, R.id.manul_area_click, R.id.start_day_click, R.id.end_day_click
            , R.id.start_time_click, R.id.end_time_click})
    private void onClick(View view) {
        min_price = editMinPrice.getText().toString();
        max_price = editMaxPrice.getText().toString();
        code = editMember.getText().toString();
        if (!TextUtils.isEmpty(startMonth))
            contract_start = startY + "-" + startMonth + "-" + startDay;
        if (!TextUtils.isEmpty(endMonth))
            contract_endtime = endY + "-" + endMonth + "-" + endDay;
        if (!TextUtils.isEmpty(startHour))
            work_starttime = startHour + ":" + startMinutes;
        if (!TextUtils.isEmpty(endMinutes))
            work_endtime = endHour + ":" + endMinutes;
        switch (view.getId()) {
            case R.id.manul_skill_click:
                startActivity(SkillAty.class, null);
                break;
            case R.id.manul_area_click:
                //选择范围
                Bundle bundleArea = new Bundle();
                bundleArea.putString("flag", "sel_area");
                startActivityForResult(SelectedRangeAty.class, bundleArea, RANGE);
                break;
            case R.id.new_jo_week_click:
                isWeek = !isWeek;
                if (isWeek) {
                    linlayWeek.setVisibility(View.VISIBLE);
                } else {
                    linlayWeek.setVisibility(View.GONE);
                }
                break;
            case R.id.manul_sure:
                StringBuffer stringBuffer = new StringBuffer();
                for (int i = 0; i < weekGridAdapter.isWeek.length; i++) {
                    if (weekGridAdapter.isWeek[i]) {
                        stringBuffer.append(weekList.get(i).get("name") + ",");
                        weekID.add(weekList.get(i).get("sid"));
                    }
                }
                if (stringBuffer.length() > 0) {
                    stringBuffer.deleteCharAt(stringBuffer.length() - 1);
//                    showToast("您选择了" + stringBuffer);
                    tvWeek.setText(stringBuffer);
                    linlayWeek.setVisibility(View.GONE);
                    isWeek = false;
                } else {
                    showToast("请选择工作周历");
                    return;
                }
                break;
            case R.id.query_fbtn:
                String maxPrice = editMaxPrice.getText().toString().trim();
                String minPrice = editMinPrice.getText().toString().trim();
                min_price = minPrice;
                max_price = maxPrice;
                if (!TextUtils.isEmpty(maxPrice) && !TextUtils.isEmpty(minPrice)) {
                    if (Integer.parseInt(maxPrice) < Integer.parseInt(minPrice)) {
                        editMaxPrice.setText(minPrice);
                        editMinPrice.setText(maxPrice);
                        min_price = maxPrice;
                        max_price = minPrice;
                    }
                }
                Bundle bundle = new Bundle();
                bundle.putString("hire_id", getIntent().getStringExtra("hire_id"));
                bundle.putString("min_price", min_price);
                bundle.putString("max_price", max_price);
                bundle.putString("code", code);
                bundle.putString("distance", distance);
                bundle.putString("contract_start", contract_start);
                bundle.putString("contract_endtime", contract_endtime);
                bundle.putString("work_starttime", work_starttime);
                bundle.putString("work_endtime", work_endtime);
                bundle.putString("province_id", province_id);
                bundle.putString("city_id", city_id);
                bundle.putString("area_id", area_id);
                bundle.putString("work_week", ListUtils.join(weekID));
                bundle.putString("skill_id", ListUtils.join(skillList));
                startActivity(LabourListAty.class, bundle);
//                finish();
                break;

            case R.id.start_day_click:
                onYearMonthDayPicker(1999, 2117, new DatePicker.OnYearMonthDayPickListener() {
                    @Override
                    public void onDatePicked(String year, String month, String day) {
                        startY = Integer.parseInt(year);
                        startM = Integer.parseInt(month);
                        startD = Integer.parseInt(day);
                        startMonth = month;
                        startDay = day;
                        tvStartDay.setText(year + "-" + month + "-" + day);
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
                        endMonth = month;
                        endDay = day;
                        if (endY > startY || endM > startM) {
                            tvEndDay.setText(year + "-" + month + "-" + day);
                            return;
                        }
                        if (endM == startM && endD > startD) {
                            tvEndDay.setText(year + "-" + month + "-" + day);
                        } else {
                            showToast("合同截止日期必须大于合同开始日期");
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
                        startMinutes = minute;
                        tvStartTime.setText(hour + ":" + minute);
                    }
                });
                break;
            case R.id.end_time_click:

                TimePicker timePicker = onTimePicker(0, 23, true, new TimePicker.OnTimePickListener() {
                    @Override
                    public void onTimePicked(String hour, String minute) {
                        endH = Integer.parseInt(hour);
                        endMinute = Integer.parseInt(minute);
                        endHour = hour;
                        endMinutes = minute;
                        if (endH > startH || endMinute > startMinute) {
                            tvEndTime.setText(hour + ":" + minute);
                        } else {
                            showToast("工作截止时间必须大于工作开始时间");
                        }
                    }
                });
                if (!TextUtils.isEmpty(startHour))
                    timePicker.setSelectedItem(startH, startMinute);
                timePicker.show();
                break;
        }
    }

    private class WeekGridAdapter extends BaseAdapter {

        private ViewHodler viewHodler;
        public Boolean[] isWeek = new Boolean[ListUtils.getSize(weekList)];

        public WeekGridAdapter() {
            for (int i = 0; i < ListUtils.getSize(weekList); i++) {
                isWeek[i] = false;
            }
        }

        @Override
        public int getCount() {
            return ListUtils.getSize(weekList);
        }

        public void setIsWeek(int position) {
            isWeek[position] = !isWeek[position];
            notifyDataSetChanged();
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
                convertView = LayoutInflater.from(ManulSelectLaborAty.this).inflate(R.layout.listitem_njo_week, parent, false);
                x.view().inject(viewHodler, convertView);
                AutoUtils.autoSize(convertView);
                convertView.setTag(viewHodler);
            } else {
                viewHodler = (ViewHodler) convertView.getTag();
            }
            if (isWeek[position]) {
                viewHodler.tvWeek.setCompoundDrawablesWithIntrinsicBounds(R.drawable.btn_opt, 0, 0, 0);
            } else {
                viewHodler.tvWeek.setCompoundDrawablesWithIntrinsicBounds(R.drawable.btn_opt_unselected, 0, 0, 0);
            }
            viewHodler.tvWeek.setText(weekList.get(position).get("name"));
            return convertView;
        }

        public class ViewHodler {
            @ViewInject(R.id.list_njo_week)
            TextView tvWeek;
        }
    }
}
