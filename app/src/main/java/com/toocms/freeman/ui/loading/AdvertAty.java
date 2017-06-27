package com.toocms.freeman.ui.loading;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.toocms.frame.image.ImageLoader;
import com.toocms.freeman.R;
import com.toocms.freeman.config.AppConfig;
import com.toocms.freeman.ui.index.IndexAty;
import com.zero.autolayout.AutoLayoutActivity;

import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import cn.zero.android.common.util.PreferencesUtils;

/**
 * 广告页
 *
 * @date 2017/5/18 14:25
 */
public class AdvertAty extends AutoLayoutActivity {

    private final long MILLIS_IN_FUTURE = 3500; // 总倒计时秒数（这里如果设置整3秒的话会从2开始倒计时，所以设置成3秒5）
    private final long COUNT_DOWN_INTERVAL = 1000; // 倒计时间隔

    @ViewInject(R.id.advert_image)
    private ImageView imageView;
    @ViewInject(R.id.advert_skip)
    private TextView textView;

    private ImageLoader imageLoader;
    private Timer timer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        mActionBar.hide();
        // 获取广告图片地址显示出来，之后开始倒计时
//        showAdvert();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        imageLoader = new ImageLoader();
        timer = new Timer();
        setContentView(R.layout.aty_advert);
        x.view().inject(this);
        timer.start();

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        timer.cancel();
    }

//    @Override
//    protected int getLayoutResId() {
//        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
//        return R.layout.aty_advert;
//    }
//
//    @Override
//    protected void initialized() {
//        // 设置全屏
//
//
//    }
//
//    @Override
//    protected void requestData() {
//    }

    @Event({R.id.advert_skip, R.id.advert_image})
    private void onClick(View v) {
        switch (v.getId()) {
            case R.id.advert_skip:
                startNextPage();
                break;
            case R.id.advert_image:
                // ======================= 跳广告页面 =====================

                break;
        }
    }

//    @Override
//    public void onComplete(RequestParams params, String result) {
//        Map<String, String> map = JSONUtils.parseDataToMap(result);
//        imageLoader.disPlay(imageView, map.get("url"));
//        // 开始倒计时
//        timer.start();
//        super.onComplete(params, result);
//    }

//    private void showAdvert() {
//        RequestParams params = new RequestParams("http://ttt.toocms.com/Index/ad");
//        ApiTool apiTool = new ApiTool();
//        apiTool.getApi(params, this);
//    }

    // 启动下一页，根据获取是否第一次启动来判断将启动的页面
    private void startNextPage() {
        // 先停止计时器再启动页面，防止启动两个界面
        timer.cancel();
        if (PreferencesUtils.getBoolean(this, AppConfig.IS_FIRST, true)) {
            startActivity(new Intent(this, GuideAty.class));
        } else {
            startActivity(new Intent(this, IndexAty.class));
        }
        finish();
    }

    private class Timer extends CountDownTimer {

        public Timer() {
            super(MILLIS_IN_FUTURE, COUNT_DOWN_INTERVAL);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            textView.setText("跳过 " + millisUntilFinished / 1000);
        }

        @Override
        public void onFinish() {
            startNextPage();
        }
    }
}
