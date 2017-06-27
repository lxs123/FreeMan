package com.toocms.freeman.ui.loading;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import com.toocms.frame.tool.Toolkit;
import com.toocms.freeman.R;
import com.toocms.freeman.config.AppConfig;
import com.toocms.freeman.ui.index.IndexAty;
import com.zero.autolayout.AutoLayoutActivity;

import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

import cn.zero.android.common.util.ListUtils;
import cn.zero.android.common.util.PreferencesUtils;
import cn.zero.android.common.view.FButton;
import cn.zero.android.common.view.banner.ConvenientBanner;
import cn.zero.android.common.view.banner.holder.CBViewHolderCreator;
import cn.zero.android.common.view.banner.holder.Holder;

/**
 * 引导页
 * <p>
 * 用法：1、首先确定是几张图片，将IMAGE_COUNT字段修改成需要显示图片数量
 * 2、将图片命名好ic_guide1、ic_guide2以此类推，并替换到drawable-xhdpi中
 * 3、更新新版本时如需显示新更换的引导页，需将AppConfig中的IS_FIRST字段修改一下
 */
public class GuideAty extends AutoLayoutActivity {

    private final int IMAGE_COUNT = 3; // 引导页的数量

    @ViewInject(R.id.guide)
    private ConvenientBanner convenientBanner;
    @ViewInject(R.id.guide_experience)
    private FButton textView;

    private List<String> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.aty_guide);
        list = new ArrayList<>();
        for (int i = 0; i < IMAGE_COUNT; i++) {
            list.add("ic_guide" + (i + 1));
        }
        x.view().inject(this);
//        mActionBar.hide();
        PreferencesUtils.putBoolean(this, AppConfig.IS_FIRST, false);
        // 先将立即体验按钮隐藏
        textView.setVisibility(View.INVISIBLE);
        // 初始化引导页的viewpager控件
        // ================ 此处没有显示小圆点，一般情况下都是图片自带
        // 如果需要自己做的话就按照之前的方法添加就可以了 ================
        convenientBanner.setPages(new CBViewHolderCreator() {
            @Override
            public LocalImageHolderView createHolder() {
                return new LocalImageHolderView();
            }
        }, list).setPageIndicator(new int[]{R.drawable.spot, R.drawable.spot_white})
                .setPageIndicatorAlign(ConvenientBanner.PageIndicatorAlign.CENTER_HORIZONTAL)
                .setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                    @Override
                    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                    }

                    @Override
                    public void onPageSelected(int position) {
                        // 判断一下如果是最后一页延迟一下显示立即体验按钮
                        if (position == ListUtils.getSize(list) - 1) {
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    textView.setVisibility(View.VISIBLE);
                                }
                            }, 300);
                        } else {
                            textView.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onPageScrollStateChanged(int state) {
                    }
                }).setCanLoop(false);
    }

//    @Override
//    protected int getLayoutResId() {
//        return R.layout.aty_guide;
//    }
//
//    @Override
//    protected void initialized() {
//
//    }
//
//    @Override
//    protected void requestData() {
//    }

    @Event(R.id.guide_experience)
    private void onClick(View v) {
        startActivity(new Intent(this, IndexAty.class));
        finish();
    }

    private class LocalImageHolderView implements Holder<String> {

        private ImageView imageView;

        @Override
        public View createView(Context context) {
            imageView = new ImageView(context);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            return imageView;
        }

        @Override
        public void UpdateUI(Context context, int i, String s) {
            imageView.setImageResource(Toolkit.getBitmapRes(GuideAty.this, s));
        }
    }
}
