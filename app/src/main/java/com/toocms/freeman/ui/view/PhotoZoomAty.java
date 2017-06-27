package com.toocms.freeman.ui.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.toocms.frame.image.ImageLoader;
import com.toocms.freeman.R;
import com.zero.autolayout.AutoLayoutActivity;

import org.xutils.image.ImageOptions;

import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * Created by admin on 2017/4/24.
 */

public class PhotoZoomAty extends AutoLayoutActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aty_photo_zoom);
        PhotoView imageView = (PhotoView) findViewById(R.id.photo_zoom);
        ImageLoader imageLoader = new ImageLoader();
        ImageOptions imageOptions = new ImageOptions.Builder().setLoadingDrawableId(R.drawable.icon_loading)
                .setImageScaleType(ImageView.ScaleType.FIT_CENTER).build();
        imageLoader.setImageOptions(imageOptions);
        imageLoader.disPlay(imageView, getIntent().getStringExtra("photo"));
        RelativeLayout layout = (RelativeLayout) findViewById(R.id.photo);
        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(0, 0);
            }
        });
        imageView.setOnPhotoTapListener(new PhotoViewAttacher.OnPhotoTapListener() {
            @Override
            public void onPhotoTap(View view, float v, float v1) {
                finish();
                overridePendingTransition(0, 0);
            }

            @Override
            public void onOutsidePhotoTap() {
                finish();
                overridePendingTransition(0, 0);
            }
        });
    }
}
