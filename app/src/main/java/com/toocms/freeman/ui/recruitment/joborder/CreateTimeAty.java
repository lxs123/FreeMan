package com.toocms.freeman.ui.recruitment.joborder;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.toocms.freeman.R;
import com.toocms.freeman.ui.BaseAty;

import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import cn.qqtheme.framework.picker.DatePicker;

/**
 * Created by admin on 2017/3/24.
 */

public class CreateTimeAty extends BaseAty {
    @ViewInject(R.id.creat_start_t)
    TextView tvStartT;
    @ViewInject(R.id.creat_end_t)
    TextView tvEndT;
    @ViewInject(R.id.creat_end)
    TextView tvEnd;
    @ViewInject(R.id.creat_start)
    TextView tvStart;
    private int startYear;
    private int startMonth;
    private int startDay;
    private int endYear;
    private int endMonth;
    private int endDay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (TextUtils.equals(getIntent().getStringExtra("flag"), "contract")) {
            mActionBar.setTitle("生效时间分区");
            tvStart.setText("最早生效时间");
            tvEnd.setText("最晚生效时间");
        } else {
            mActionBar.setTitle("创建时间分区");
        }

    }

    @Override
    protected int getLayoutResId() {
        return R.layout.aty_create_time;
    }

    @Override
    protected void initialized() {

    }

    @Override
    protected void requestData() {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_sure, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_sure:
                Intent intent = new Intent();
                intent.putExtra("startY", startYear);
                intent.putExtra("startM", startMonth);
                intent.putExtra("startD", startDay);
                intent.putExtra("endY", endYear);
                intent.putExtra("endM", endMonth);
                intent.putExtra("endD", endDay);
                setResult(RESULT_OK, intent);
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Event({R.id.creat_start_t_click, R.id.creat_end_t_click})
    private void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.creat_start_t_click:
                onYearMonthDayPicker(1999, 2217, new DatePicker.OnYearMonthDayPickListener() {
                    @Override
                    public void onDatePicked(String year, String month, String day) {
                        startYear = Integer.parseInt(year);
                        startMonth = Integer.parseInt(month);
                        startDay = Integer.parseInt(day);
                        tvStartT.setText(year + "-" + month + "-" + day);
                        tvEndT.setText("");
                        endDay = 0;
                        endYear = 0;
                        endMonth = 0;
                    }
                });
                break;
            case R.id.creat_end_t_click:

                DatePicker datePicker = onYearMonthDayPicker(1999, 2217, true, new DatePicker.OnYearMonthDayPickListener() {
                    @Override
                    public void onDatePicked(String year, String month, String day) {
                        endYear = Integer.parseInt(year);
                        endMonth = Integer.parseInt(month);
                        endDay = Integer.parseInt(day);

                        if (startYear != 0) {
                            if (endYear > startYear) {
                                if (endMonth > startMonth) {
                                    tvEndT.setText(year + "-" + month + "-" + day);
                                    return;
                                } else if (endMonth == startMonth) {
                                    if (endDay >= endMonth) {
                                        tvEndT.setText(year + "-" + month + "-" + day);
                                    } else {
                                        showToast("最晚创建时间必须大于最早创建时间");
                                        endDay = 0;
                                        endYear = 0;
                                        endMonth = 0;
                                    }
                                }

                            } else if (endYear == startYear) {
                                if (endMonth > startMonth) {
                                    tvEndT.setText(year + "-" + month + "-" + day);
                                } else if (endMonth == startMonth) {
                                    if (endDay >= startDay) {
                                        tvEndT.setText(year + "-" + month + "-" + day);
                                    } else{
                                        showToast("最晚创建时间必须大于最早创建时间");
                                        endDay = 0;
                                        endYear = 0;
                                        endMonth = 0;
                                    }
                                }
                            } else {
                                showToast("最晚创建时间必须大于最早创建时间");
                                endDay = 0;
                                endYear = 0;
                                endMonth = 0;
                            }
                        } else
                            tvEndT.setText(year + "-" + month + "-" + day);
                    }
                });
                if (startYear != 0)
                    datePicker.setSelectedItem(startYear, startMonth, startDay);
                datePicker.show();
                break;
        }
    }
}
