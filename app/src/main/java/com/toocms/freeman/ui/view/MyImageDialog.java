package com.toocms.freeman.ui.view;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.toocms.frame.image.ImageLoader;
import com.toocms.freeman.R;

import org.xutils.image.ImageOptions;

import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * Created by admin on 2017/4/24.
 */

public class MyImageDialog extends Dialog {
    Bitmap bms;
    ImageView iv;
    Window window;
    String imgvUrl;
    boolean tag = false;
    private View loadingview;
    private PhotoView iv1;
    private RelativeLayout relativeLayout;
    private String content;

    public MyImageDialog(Context context) {
        super(context);
    }

    public MyImageDialog(Context context, int themeResId) {
        super(context, themeResId);
    }

    public MyImageDialog(Context context, int cancelable, int x, int y, String url) {
        super(context, cancelable);
        windowDeploy(x, y);
        imgvUrl = url;
    }

    public MyImageDialog(Context context, int cancelable, int x, int y, boolean tag, String url) {
        super(context, cancelable);

        imgvUrl = url;
        this.tag = tag;
        windowDeploy(x, y);
//
    }

    public MyImageDialog(Context context, int cancelable, int x, int y, boolean tag, String url, String content) {
        super(context, cancelable);
        this.content = content;
        imgvUrl = url;
        this.tag = tag;
        windowDeploy(x, y);
//
    }


    protected void onCreate(Bundle savedInstanceState) {
        ImageLoader imageLoader = new ImageLoader();
        if (tag) {
            loadingview = LayoutInflater.from(getContext()).inflate(R.layout.aty_photo_zoom, null);
            iv1 = (PhotoView) loadingview.findViewById(R.id.photo_zoom);
            ImageOptions options = new ImageOptions.Builder()
                    .setImageScaleType(ImageView.ScaleType.FIT_CENTER)
                    .setUseMemCache(true).build();
            imageLoader.setImageOptions(options);
            imageLoader.disPlay(iv1, imgvUrl);
        } else {
            loadingview = LayoutInflater.from(getContext()).inflate(R.layout.imagedialogview, null);
            iv = (ImageView) loadingview.findViewById(R.id.imageview_head_big);
            RelativeLayout relayTv = (RelativeLayout) loadingview.findViewById(R.id.dialog_lay);
            TextView tv = (TextView) loadingview.findViewById(R.id.dialog_text);
            if (TextUtils.equals(imgvUrl, "text")) {
                relayTv.setVisibility(View.VISIBLE);
                tv.setText(content);
            } else {
                relayTv.setVisibility(View.GONE);
                imageLoader.disPlay(iv, imgvUrl);
            }

        }
        //初始化布局

//        iv.setImageBitmap(bms);


        //设置dialog的布局
        setContentView(loadingview);
        //如果需要放大或者缩小时的动画，可以直接在此出对loadingview或iv操作，在下面SHOW或者dismiss中操作
        super.onCreate(savedInstanceState);
        if (!tag) {
            iv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                }
            });
        } else {
            iv1.setOnPhotoTapListener(new PhotoViewAttacher.OnPhotoTapListener() {
                @Override
                public void onPhotoTap(View view, float v, float v1) {
                    dismiss();
                }

                @Override
                public void onOutsidePhotoTap() {
                    dismiss();
                }
            });
        }


        loadingview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    //设置窗口显示
    public void windowDeploy(int x, int y) {
        window = getWindow(); //得到对话框
//        window.setWindowAnimations(R.style.dialogWindowAnim); //设置窗口弹出动画
        if (!tag) {
            getWindow().setBackgroundDrawableResource(R.color.clr_dialog); //设置对话框背景为透明
        } else {
            getWindow().setBackgroundDrawableResource(R.color.black); //设置对话框背景为透明
        }

        WindowManager.LayoutParams wl = window.getAttributes();

        //根据x，y坐标设置窗口需要显示的位置
        wl.x = x; //x小于0左移，大于0右移
        wl.y = y; //y小于0上移，大于0下移
        wl.alpha = 1f; //设置透明度
//            wl.gravity = Gravity.BOTTOM; //设置重力
        window.setAttributes(wl);
    }

    public void show() {
        //设置触摸对话框意外的地方取消对话框
        setCanceledOnTouchOutside(true);
        super.show();
    }

    public void dismiss() {
        super.dismiss();
    }
}
