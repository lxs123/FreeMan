package com.toocms.freeman.ui.searchjob;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.toocms.frame.image.ImageLoader;
import com.toocms.freeman.R;
import com.toocms.freeman.https.Seminate;
import com.toocms.freeman.https.User;
import com.toocms.freeman.ui.BaseAty;
import com.toocms.freeman.ui.index.WebViewAty;
import com.toocms.freeman.ui.lar.RegisterAty;
import com.toocms.freeman.ui.recruitment.jobhelp.JOHelpAty;
import com.zero.autolayout.utils.AutoUtils;

import org.xutils.http.RequestParams;
import org.xutils.image.ImageOptions;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.zero.android.common.util.JSONUtils;
import cn.zero.android.common.util.ListUtils;
import cn.zero.android.common.view.banner.ConvenientBanner;
import cn.zero.android.common.view.banner.holder.CBViewHolderCreator;
import cn.zero.android.common.view.banner.holder.Holder;
import cn.zero.android.common.view.banner.listener.OnItemClickListener;

public class SearchJobAty extends BaseAty {

    @ViewInject(R.id.index_img)
    private ConvenientBanner imgvImg;
    private List<Map<String, String>> list = new ArrayList<>();
    private Seminate seminate;
    private ArrayList<Map<String, String>> date;
    User mUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActionBar.setTitle("搜工作");
//        initData();
    }

    private void initData() {
        imgvImg.setPages(new CBViewHolderCreator() {
            @Override
            public Object createHolder() {
                return new BannerView();
            }
        }, list).setPageIndicator(new int[]{R.drawable.spot_normal, R.drawable.spot_clicked})
                .setPageIndicatorAlign(ConvenientBanner.PageIndicatorAlign.CENTER_HORIZONTAL);
        imgvImg.startTurning(3000).setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(int i) {
                String link_type = date.get(i).get("link_type");
                int anInt = Integer.parseInt(link_type);
                Bundle bundle = new Bundle();
                bundle.putString("link_value", date.get(i).get("link_value"));
                /**
                 * 跳转方式
                 - 1 招工信息详情
                 - 2 注册页
                 - 3 html网页
                 - 4 个人中心
                 - 5 客服中心
                 - 6 我的劳务合同
                 - 7 文章列表
                 */
                switch (anInt) {
                    case 1:
                        break;
                    case 2:
                        startActivity(RegisterAty.class, bundle);
                        break;
                    case 3:
                        startActivity(WebViewAty.class, bundle);
                        break;
                    case 4:
                        break;
                    case 5:
                        break;
                    case 6:
                        break;
                    case 7:
                        break;
                }
            }
        });
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.aty_search_job;
    }

    @Override
    protected void initialized() {
        seminate = new Seminate();
        mUser = new User();
    }

    @Override
    protected void requestData() {
        seminate.getSlider(this);
    }

    @Override
    public void onComplete(RequestParams params, String result) {
        if (params.getUri().contains("Seminate/getSlider")) {

            date = JSONUtils.parseDataToMapList(result);
            for (int i = 0; i < ListUtils.getSize(date); i++) {
                Map<String, String> map = new HashMap<>();
                map.put("slider", date.get(i).get("slider"));
                list.add(map);
            }
            initData();

        }

        if (params.getUri().contains("getPerfect")) {
            if (!(JSONUtils.parseDataToMap(result).get("attestation").equals("1") && JSONUtils.parseDataToMap(result).get("perfect").equals("1"))) {
                showDialog("", "请完善个人信息", 0, "确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        return;
                    }
                });
            } else {
                startActivity(ScreenAty.class, null);
            }
        }
        super.onComplete(params, result);
    }

    @Event({R.id.query_recruitment_information_tv, R.id.my_job_order_tv, R.id.already_rob_tv, R.id.help_information_tv})
    private void onClick(View view) {
        switch (view.getId()) {
            case R.id.query_recruitment_information_tv:
                mUser.getPerfect(application.getUserInfo().get("noid"), this);

                break;
            case R.id.my_job_order_tv:
                //我的招工单
                startActivity(MyJobOrderInSearchAty.class, null);
                break;
            case R.id.already_rob_tv:
                //已抢招工单
                startActivity(AlreadyRobJobOrderAty.class, null);
                break;
            case R.id.help_information_tv:
                Bundle bundle = new Bundle();
                bundle.putString("flag", "search");
                startActivity(JOHelpAty.class, bundle);
                break;
        }
    }

    private class BannerView implements Holder<Map<String, String>> {

        private ImageView imageView;
        private ImageLoader imageLoader;

        public BannerView() {
            imageLoader = new ImageLoader();
            ImageOptions options = new ImageOptions.Builder()
                    .setSize(AutoUtils.getPercentWidthSize(750), AutoUtils.getPercentWidthSize(374))
                    .setLoadingDrawableId(R.drawable.img_index)
                    .setFailureDrawableId(R.drawable.img_index)
                    .setFadeIn(true).setImageScaleType(ImageView.ScaleType.FIT_XY)
                    .setUseMemCache(true).build();
            imageLoader.setImageOptions(options);
        }

        @Override
        public View createView(Context context) {
            imageView = new ImageView(context);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            return imageView;
        }

        @Override
        public void UpdateUI(Context context, int i, Map<String, String> stringStringMap) {
            imageLoader.disPlay(imageView, stringStringMap.get("slider"));
//            imageView.setImageResource(R.drawable.img_index);
////            imageView.setImageResource(R.drawable.img_index);
        }
    }

}
