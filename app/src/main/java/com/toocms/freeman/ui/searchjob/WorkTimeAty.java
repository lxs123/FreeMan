package com.toocms.freeman.ui.searchjob;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.toocms.freeman.R;
import com.toocms.freeman.ui.BaseAty;

import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import cn.qqtheme.framework.picker.DatePicker;
import cn.qqtheme.framework.picker.TimePicker;

public class WorkTimeAty extends BaseAty {

    @ViewInject(R.id.tv1)
    private TextView tv1;
    @ViewInject(R.id.tv2)
    private TextView tv2;
    @ViewInject(R.id.tv3)
    private TextView tv3;
    @ViewInject(R.id.tv4)
    private TextView tv4;

    private String tv1Str;
    private String tv2Str;
    private String tv3Str;
    private String tv4Str;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mActionBar.setTitle("工作时间");
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.aty_work_time;
    }

    @Override
    protected void initialized() {

    }

    @Override
    protected void requestData() {

    }


    @Event({R.id.work_start_day, R.id.work_end_day, R.id.work_start_time, R.id.work_end_time})
    private void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.work_start_day:
                onYearMonthDayPicker(2017, 2217, new DatePicker.OnYearMonthDayPickListener() {
                    @Override
                    public void onDatePicked(String year, String month, String day) {
                        tv1Str = year + "-" +month + "-" + day;
                        tv1.setText(tv1Str);
                    }
                });
                break;
            case R.id.work_end_day:
                onYearMonthDayPicker(2017, 2217, new DatePicker.OnYearMonthDayPickListener() {
                    @Override
                    public void onDatePicked(String year, String month, String day) {
                        tv2Str = year + "-" +month + "-" + day;
                        tv2.setText(tv2Str);
                    }
                });
                break;
            case R.id.work_start_time:
                onTimePicker(0, 23, new TimePicker.OnTimePickListener() {
                    @Override
                    public void onTimePicked(String hour, String minute) {
                        tv3Str = hour + ":" +minute;
                        tv3.setText(tv3Str);
                    }
                });
                break;
            case R.id.work_end_time:
                onTimePicker(0, 23, new TimePicker.OnTimePickListener() {
                    @Override
                    public void onTimePicked(String hour, String minute) {
                        tv4Str = hour + ":" +minute;
                        tv4.setText(tv4Str);
                    }
                });
                break;
        }
    }

    @Override
    public void finish() {
        Intent intent = new Intent();
        intent.putExtra("tv1Str",tv1Str);
        intent.putExtra("tv2Str",tv2Str);
        intent.putExtra("tv3Str",tv3Str);
        intent.putExtra("tv4Str",tv4Str);
        setResult(9998,intent);
        super.finish();
    }

    @Override
    public void onBackPressed() {
       finish();
        super.onBackPressed();
    }
}
