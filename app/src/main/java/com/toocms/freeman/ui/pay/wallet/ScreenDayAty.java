package com.toocms.freeman.ui.pay.wallet;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.toocms.freeman.R;
import com.toocms.freeman.ui.BaseAty;

import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import cn.qqtheme.framework.picker.DatePicker;

/**
 * Created by admin on 2017/3/29.
 */

public class ScreenDayAty extends BaseAty {
    @ViewInject(R.id.time_text1)
    private TextView tv1;
    @ViewInject(R.id.time_text2)
    private TextView tv2;

    private String startTimeStr, endTimeStr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Event({R.id.sure_fbtn})
    private void onclick(View view) {
        switch (view.getId()) {
            case R.id.sure_fbtn:
                Intent intent = new Intent();
                intent.putExtra("start_time", startTimeStr);
                intent.putExtra("end_time", endTimeStr);
                setResult(RESULT_OK, intent);
                finish();
                break;
        }
    }

    //time_text1

    @Event({R.id.ll1, R.id.ll2})
    private void onClick(View view) {
        switch (view.getId()) {
            case R.id.ll1:
                onYearMonthDayPicker(2010, 2049, new DatePicker.OnYearMonthDayPickListener() {
                    @Override
                    public void onDatePicked(String year, String month, String day) {
                        startTimeStr = year + "-" + month + "-" + day;
                        tv1.setText(startTimeStr);
                    }
                });
                break;
            case R.id.ll2:

                onYearMonthDayPicker(2010, 2049, new DatePicker.OnYearMonthDayPickListener() {
                    @Override
                    public void onDatePicked(String year, String month, String day) {
                        endTimeStr = year + "-" + month + "-" + day;
                        if (!TextUtils.isEmpty(startTimeStr)) {
                            if (Integer.parseInt(startTimeStr.replaceAll("-", "")) > Integer.parseInt(endTimeStr.replaceAll("-", ""))) {
                                showToast("结束时间不能小于开始时间");
                                endTimeStr = "";
                            } else {
                                tv2.setText(endTimeStr);
                            }
                        } else {
                            tv2.setText(endTimeStr);
                        }
                    }
                });
                break;
        }
    }


    @Override
    protected int getLayoutResId() {
        return R.layout.aty_packetsceen;
    }

    @Override
    protected void initialized() {

    }

    @Override
    protected void requestData() {

    }

}
