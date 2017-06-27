package com.toocms.freeman.ui;

import com.toocms.frame.config.WeApplication;

import cn.jpush.android.api.JPushInterface;

/**
 * @Author LXS
 * @Data 2017/6/16 12:04
 */

public class MyApplication extends WeApplication {
    @Override
    public void onCreate() {
        super.onCreate();
        JPushInterface.setDebugMode(true);//如果时正式版就改成false
        JPushInterface.init(this);

    }
}
