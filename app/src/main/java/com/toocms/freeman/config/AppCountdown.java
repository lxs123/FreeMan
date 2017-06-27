package com.toocms.freeman.config;

import android.graphics.Color;
import android.os.CountDownTimer;
import android.widget.TextView;

import org.xutils.x;

import cn.zero.android.common.util.PreferencesUtils;

/**
 * Created by admin on 2017/4/10.
 */

public class AppCountdown {
    private static final int INTERVAL_TIME = 60;
    private TextView textView;
    private Timer timer;
    private long surplustime;//剩余时长

    public AppCountdown() {
        surplustime = PreferencesUtils.getLong(x.app(), "STOP_TIME") - System.currentTimeMillis();
        timer = new Timer(surplustime > 0 ? surplustime : INTERVAL_TIME * 1000);
    }

    public void saveStopTime() {
        PreferencesUtils.putLong(x.app(), "STOP_TIME", System.currentTimeMillis() + surplustime);
    }

    public void play(TextView textView) {
        this.textView = textView;
        if (surplustime > 0) {
            textView.setEnabled(false);
            textView.setTextColor(Color.parseColor("#ff5000"));
            timer.start();
        } else {
            textView.setEnabled(true);
            textView.setTextColor(Color.parseColor("#467CD4"));
            textView.setText("发送验证码");
        }
    }

    public void start() {
        timer = new Timer(INTERVAL_TIME * 1000);
        timer.start();
    }

    public void stop() {
        saveStopTime();
        timer.cancel();
    }

    private class Timer extends CountDownTimer {

        public Timer(long millisInFuture) {
            super(millisInFuture, 1000);
        }


        @Override
        public void onTick(long millisUntilFinished) {
            surplustime = millisUntilFinished;
            textView.setTextColor(Color.parseColor("#656565"));
            textView.setText("重新获取(" + millisUntilFinished / 1000 + "s)");
        }

        @Override
        public void onFinish() {
            textView.setEnabled(true);
            textView.setText("重新获取");
            textView.setTextColor(Color.parseColor("#467CD4"));
        }
    }
}
