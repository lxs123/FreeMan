package com.toocms.freeman.ui.view;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.toocms.frame.image.ImageLoader;
import com.toocms.freeman.R;

import org.xutils.image.ImageOptions;

import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;


/**
 * Created by admin on 2017/5/25.
 */

public class ImageDetailFragment extends Fragment {
    private String mImageUrl;
    private PhotoView mImageView;
    private ProgressBar progressBar;
    private PhotoViewAttacher mAttacher;

    public static ImageDetailFragment newInstance(String imageUrl) {
        final ImageDetailFragment f = new ImageDetailFragment();

        final Bundle args = new Bundle();
        args.putString("url", imageUrl);
        f.setArguments(args);

        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mImageUrl = getArguments() != null ? getArguments().getString("url")
                : null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.image_detail_fragment,
                container, false);
        mImageView = (PhotoView) v.findViewById(R.id.image);
        mImageView.setOnPhotoTapListener(new PhotoViewAttacher.OnPhotoTapListener() {
            @Override
            public void onPhotoTap(View view, float v, float v1) {
                getActivity().finish();
                getActivity().overridePendingTransition(0, 0);
            }

            @Override
            public void onOutsidePhotoTap() {
                getActivity().finish();
                getActivity().overridePendingTransition(0, 0);
            }
        });
//        mAttacher = new PhotoViewAttacher(mImageView);

//        mAttacher.setOnPhotoTapListener(new PhotoViewAttacher.OnPhotoTapListener() {
//
//            @Override
//            public void onPhotoTap(View arg0, float arg1, float arg2) {
//                getActivity().finish();
//            }
//
//            @Override
//            public void onOutsidePhotoTap() {
//
//            }
//        });

        progressBar = (ProgressBar) v.findViewById(R.id.loading);
        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ImageLoader imageLoader = new ImageLoader();
        ImageOptions imageOptions = new ImageOptions.Builder().setImageScaleType(ImageView.ScaleType.FIT_CENTER)
                .setLoadingDrawableId(R.drawable.icon_loading)
                .setUseMemCache(true).build();
        imageLoader.setImageOptions(imageOptions);
        imageLoader.disPlay(mImageView, mImageUrl);
        progressBar.setVisibility(View.GONE);
    }
//        new SimpleImageLoadingListener() {
//                    @Override
//                    public void onLoadingStarted(String imageUri, View view) {
//                        progressBar.setVisibility(View.VISIBLE);
//                    }
//
//                    @Override
//                    public void onLoadingFailed(String imageUri, View view,
//                                                FailReason failReason) {
//                        String message = null;
//                        switch (failReason.getType()) {
//                            case IO_ERROR:
//                                message = 下载错误;
//                                break;
//                            case DECODING_ERROR:
//                                message = 图片无法显示;
//                                break;
//                            case NETWORK_DENIED:
//                                message = 网络有问题，无法下载;
//                                break;
//                            case OUT_OF_MEMORY:
//                                message = 图片太大无法显示;
//                                break;
//                            case UNKNOWN:
//                                message = 未知的错误;
//                                break;
//                        }
//                        Toast.makeText(getActivity(), message,
//                                Toast.LENGTH_SHORT).show();
//                        progressBar.setVisibility(View.GONE);
//                    }
//
//                    @Override
//                    public void onLoadingComplete(String imageUri, View view,
//                                                  Bitmap loadedImage) {
//                        progressBar.setVisibility(View.GONE);
//                        mAttacher.update();
//                    }
//

}
