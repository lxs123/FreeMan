package com.toocms.freeman.ui;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.toocms.frame.ui.BaseActivity;
import com.toocms.freeman.R;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import cn.qqtheme.framework.picker.DatePicker;
import cn.qqtheme.framework.picker.TimePicker;
import cn.zero.android.common.util.StatusBarUtil;
import cn.zero.android.common.util.StringUtils;

/**
 * Created by admin on 2017/2/27.
 */

public abstract class BaseAty extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        changeStatusBar();
        mActionBar.setBackgroundColor(getResources().getColor(R.color.clr_main));
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }


    // 更改状态栏颜色
    private void changeStatusBar() {
        if (Build.MANUFACTURER.equals("Xiaomi") || Build.MANUFACTURER.equals("Meizu") || Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            StatusBarUtil.setColor(this, getResources().getColor(R.color.clr_main), 0);
        else
            StatusBarUtil.setColor(this, getResources().getColor(R.color.clr_main), 0);
        if (Build.MANUFACTURER.equals("Xiaomi"))
            setMiuiStatusBarDarkMode(true);
        if (Build.MANUFACTURER.equals("Meizu"))
            setMeizuStatusBarDarkIcon(true);
    }

    private boolean setMeizuStatusBarDarkIcon(boolean darkmode) {
        Class<? extends Window> clazz = getWindow().getClass();
        try {
            int darkModeFlag = 0;
            Class<?> layoutParams = Class.forName("android.view.MiuiWindowManager$LayoutParams");
            Field field = layoutParams.getField("EXTRA_FLAG_STATUS_BAR_DARK_MODE");
            darkModeFlag = field.getInt(layoutParams);
            Method extraFlagField = clazz.getMethod("setExtraFlags", int.class, int.class);
            extraFlagField.invoke(getWindow(), darkmode ? darkModeFlag : 0, darkModeFlag);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private boolean setMiuiStatusBarDarkMode(boolean dark) {
        try {
            WindowManager.LayoutParams lp = getWindow().getAttributes();
            Field darkFlag = WindowManager.LayoutParams.class.getDeclaredField("MEIZU_FLAG_DARK_STATUS_BAR_ICON");
            Field meizuFlags = WindowManager.LayoutParams.class.getDeclaredField("meizuFlags");
            darkFlag.setAccessible(true);
            meizuFlags.setAccessible(true);
            int bit = darkFlag.getInt(null);
            int value = meizuFlags.getInt(lp);
            if (dark) value |= bit;
            else value &= ~bit;
            meizuFlags.setInt(lp, value);
            getWindow().setAttributes(lp);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public void showDialog(String title, String msg, int resId, String posBtnStr,
                           DialogInterface.OnClickListener posListener, String negBtnStr, DialogInterface.OnClickListener negListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        if (!StringUtils.isEmpty(title)) {
            builder.setTitle(title);
        }
        if (resId != 0) {
            builder.setIcon(resId);
        }
        builder.setMessage(msg);
        builder.setPositiveButton(posBtnStr, posListener);
        builder.setNegativeButton(negBtnStr, negListener);
        builder.create().show();
    }

    public void showDialog(String title, String msg, int resId, String btnStr, DialogInterface.OnClickListener listener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        if (!StringUtils.isEmpty(title)) {
            builder.setTitle(title);
        }
        if (resId != 0) {
            builder.setIcon(resId);
        }
        builder.setMessage(msg);
        builder.setPositiveButton(btnStr, listener);
        builder.create().show();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (isShouldHideKeyboard(v, ev)) {
                hideKeyboard(v.getWindowToken());
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    /**
     * 根据EditText所在坐标和用户点击的坐标相对比，来判断是否隐藏键盘，因为当用户点击EditText时则不能隐藏
     *
     * @param v
     * @param event
     * @return
     */
    private boolean isShouldHideKeyboard(View v, MotionEvent event) {
        if (v != null && (v instanceof EditText)) {
            int[] l = {0, 0};
            v.getLocationInWindow(l);
            int left = l[0],
                    top = l[1],
                    bottom = top + v.getHeight(),
                    right = left + v.getWidth();
            if (event.getX() > left && event.getX() < right
                    && event.getY() > top && event.getY() < bottom) {
                // 点击EditText的事件，忽略它。
                return false;
            } else {
                return true;
            }
        }
        // 如果焦点不是EditText则忽略，这个发生在视图刚绘制完，第一个焦点不在EditText上，和用户用轨迹球选择其他的焦点
        return false;
    }

    /**
     * 获取InputMethodManager，隐藏软键盘
     *
     * @param token
     */
    private void hideKeyboard(IBinder token) {
        if (token != null) {
            InputMethodManager im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            im.hideSoftInputFromWindow(token, InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    /**
     * x轴移动
     *
     * @param mview
     * @param endX
     */
    public void startTranslate(final View mview, int endX) {
        float startx = mview.getX();
        ValueAnimator animator = ValueAnimator.ofFloat(startx, endX);
        animator.setTarget(mview);
        animator.setDuration(200);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mview.setTranslationX((Float) animation.getAnimatedValue());
            }
        });
        animator.start();
    }

    /**
     * 时间弹窗
     * 返回值为 TimePicker 类
     *
     * @param timeStart 时间开始时间
     * @param timeEnd   时间结束时间
     * @param listener
     * @return
     */
    public TimePicker onTimePicker(int timeStart, int timeEnd, TimePicker.OnTimePickListener listener) {
        TimePicker picker = new TimePicker(this, TimePicker.HOUR_24);
        picker.setGravity(Gravity.CENTER);
        picker.setRangeStart(timeStart, 0);//09:00 时间开始时间
        picker.setRangeEnd(timeEnd, 0);//18:00 时间结束时间
        picker.setTopLineVisible(false);
        picker.setLineVisible(false);
        picker.setOnTimePickListener(listener);
        picker.show();
        return picker;
    }

    public TimePicker onTimePicker(int timeStart, int timeEnd,int setHour,int setMin, TimePicker.OnTimePickListener listener) {
        TimePicker picker = new TimePicker(this, TimePicker.HOUR_24);
        picker.setGravity(Gravity.CENTER);
        picker.setRangeStart(timeStart, 0);//09:00 时间开始时间
        picker.setRangeEnd(timeEnd, 0);//18:00 时间结束时间
        picker.setTopLineVisible(false);
        picker.setLineVisible(false);
        picker.setOnTimePickListener(listener);
        picker.setSelectedItem(setHour,setMin);
        picker.show();
        return picker;
    }

    public TimePicker onTimePicker(int timeStart, int timeEnd, int setHour,int setMin,boolean isDefault, TimePicker.OnTimePickListener listener) {
        TimePicker picker = new TimePicker(this, TimePicker.HOUR_24);
        picker.setGravity(Gravity.CENTER);
        picker.setRangeStart(timeStart, 0);//09:00 时间开始时间
        picker.setRangeEnd(timeEnd, 59);//18:00 时间结束时间
        picker.setTopLineVisible(false);
        picker.setLineVisible(false);
        picker.setOnTimePickListener(listener);
        if (!isDefault)
            picker.setSelectedItem(setHour,setMin);
            picker.show();
        return picker;
    }
    public TimePicker onTimePicker(int timeStart, int timeEnd, boolean isDefault, TimePicker.OnTimePickListener listener) {
        TimePicker picker = new TimePicker(this, TimePicker.HOUR_24);
        picker.setGravity(Gravity.CENTER);
        picker.setRangeStart(timeStart, 0);//09:00 时间开始时间
        picker.setRangeEnd(timeEnd, 59);//18:00 时间结束时间
        picker.setTopLineVisible(false);
        picker.setLineVisible(false);
        picker.setOnTimePickListener(listener);
        if (!isDefault)
            picker.show();
        return picker;
    }

    /**
     * 年月日选择弹窗
     *
     * @param startYear                开始的年（月日默认1.1，不需传参）
     * @param endYear                  截止年 （月日默认12.31）
     * @param isDefault                如果传入isDefault 为false，代表不需要对选择器进行下一步操作（）例如设置默认值
     *                                 为true时，必须写show（）方法
     * @param yearMonthDayPickListener
     */
    public DatePicker onYearMonthDayPicker(int startYear, int endYear, boolean isDefault, DatePicker.OnYearMonthDayPickListener
            yearMonthDayPickListener) {
        final DatePicker picker = new DatePicker(this);
        picker.setGravity(Gravity.CENTER);
        picker.setTopPadding(10);
        picker.setTopLineVisible(false);
        picker.setLineVisible(false);
        picker.setRangeStart(startYear, 1, 1);
        picker.setRangeEnd(endYear, 12, 31);
//      picker.setSelectedItem(2050, 10, 14); 默认
        picker.setOnDatePickListener(yearMonthDayPickListener);
        picker.setOnWheelListener(new DatePicker.OnWheelListener() {
            @Override
            public void onYearWheeled(int index, String year) {
//                picker.setTitleText(year + "-" + picker.getSelectedMonth() + "-" + picker.getSelectedDay());
            }

            @Override
            public void onMonthWheeled(int index, String month) {
//                picker.setTitleText(picker.getSelectedYear() + "-" + month + "-" + picker.getSelectedDay());
            }

            @Override
            public void onDayWheeled(int index, String day) {
//                picker.setTitleText(picker.getSelectedYear() + "-" + picker.getSelectedMonth() + "-" + day);
            }
        });
//        如果传入isDefault 为false，代表不需要对选择器进行下一步操作（）例如设置默认值
        if (!isDefault) {
            picker.show();
        }
//
        return picker;
    }

    /**
     * 年月日选择弹窗(无返回值)
     *
     * @param startYear                开始的年（月日默认1.1，不需传参）
     * @param endYear                  截止年 （月日默认12.31）
     * @param yearMonthDayPickListener
     */
    public void onYearMonthDayPicker(int startYear, int endYear, DatePicker.OnYearMonthDayPickListener
            yearMonthDayPickListener) {
        final DatePicker picker = new DatePicker(this);
        picker.setGravity(Gravity.CENTER);
        picker.setTopPadding(10);
        picker.setTopLineVisible(false);
        picker.setLineVisible(false);
        picker.setRangeStart(startYear, 1, 1);
        picker.setRangeEnd(endYear, 12, 31);
//      picker.setSelectedItem(2050, 10, 14); 默认
        picker.setOnDatePickListener(yearMonthDayPickListener);
        picker.setOnWheelListener(new DatePicker.OnWheelListener() {
            @Override
            public void onYearWheeled(int index, String year) {
//                picker.setTitleText(year + "-" + picker.getSelectedMonth() + "-" + picker.getSelectedDay());
            }

            @Override
            public void onMonthWheeled(int index, String month) {
//                picker.setTitleText(picker.getSelectedYear() + "-" + month + "-" + picker.getSelectedDay());
            }

            @Override
            public void onDayWheeled(int index, String day) {
//                picker.setTitleText(picker.getSelectedYear() + "-" + picker.getSelectedMonth() + "-" + day);
            }
        });
        picker.show();
//        return picker;
    }

    public void onYearMonthDayPicker(int startYear, int endYear, int setYear, int setMonth, int setDay,DatePicker.OnYearMonthDayPickListener
            yearMonthDayPickListener) {
        final DatePicker picker = new DatePicker(this);
        picker.setGravity(Gravity.CENTER);
        picker.setTopPadding(10);
        picker.setTopLineVisible(false);
        picker.setLineVisible(false);
        picker.setRangeStart(startYear, 1, 1);
        picker.setRangeEnd(endYear, 12, 31);
//      picker.setSelectedItem(2050, 10, 14); 默认
        picker.setOnDatePickListener(yearMonthDayPickListener);
        picker.setOnWheelListener(new DatePicker.OnWheelListener() {
            @Override
            public void onYearWheeled(int index, String year) {
//                picker.setTitleText(year + "-" + picker.getSelectedMonth() + "-" + picker.getSelectedDay());
            }

            @Override
            public void onMonthWheeled(int index, String month) {
//                picker.setTitleText(picker.getSelectedYear() + "-" + month + "-" + picker.getSelectedDay());
            }

            @Override
            public void onDayWheeled(int index, String day) {
//                picker.setTitleText(picker.getSelectedYear() + "-" + picker.getSelectedMonth() + "-" + day);
            }
        });
        picker.setSelectedItem(setYear,setMonth,setDay);
        picker.show();
//        return picker;
    }
}
