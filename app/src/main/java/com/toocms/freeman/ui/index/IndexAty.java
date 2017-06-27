package com.toocms.freeman.ui.index;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.toocms.frame.config.Config;
import com.toocms.frame.image.ImageLoader;
import com.toocms.frame.tool.AppManager;
import com.toocms.freeman.R;
import com.toocms.freeman.config.Constants;
import com.toocms.freeman.https.Message;
import com.toocms.freeman.https.Seminate;
import com.toocms.freeman.ui.BaseAty;
import com.toocms.freeman.ui.contract.ContDetailAty;
import com.toocms.freeman.ui.contract.MyContractAty;
import com.toocms.freeman.ui.lar.LoginAty;
import com.toocms.freeman.ui.lar.RegisterAty;
import com.toocms.freeman.ui.mine.MemberDetailAty;
import com.toocms.freeman.ui.mine.MineAty;
import com.toocms.freeman.ui.recruitment.RecruitmentAty;
import com.toocms.freeman.ui.recruitment.jobhelp.JOHelpAty;
import com.toocms.freeman.ui.recruitment.myjoborder.JODetailAty;
import com.toocms.freeman.ui.searchjob.SearchJobAty;
import com.zero.autolayout.utils.AutoUtils;

import org.xutils.common.util.LogUtil;
import org.xutils.http.RequestParams;
import org.xutils.image.ImageOptions;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.zero.android.common.permission.PermissionFail;
import cn.zero.android.common.util.JSONUtils;
import cn.zero.android.common.util.ListUtils;
import cn.zero.android.common.view.banner.ConvenientBanner;
import cn.zero.android.common.view.banner.holder.CBViewHolderCreator;
import cn.zero.android.common.view.banner.holder.Holder;
import cn.zero.android.common.view.banner.listener.OnItemClickListener;

/**
 * 首页
 * Created by admin on 2017/3/22.
 */

public class IndexAty extends BaseAty {
    @ViewInject(R.id.index_city)
    private TextView indexCityTv;
    @ViewInject(R.id.index_img)
    private ConvenientBanner imgvImg;
    private List<Map<String, String>> list = new ArrayList<>();
    @ViewInject(R.id.index_message_spot)
    private View vSpot;
    private static final String[] authBaseArr = {Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.READ_PHONE_STATE};
    /**
     * 轮播图[getSlider]
     * 跳转方式
     * - 1 招工信息详情
     * - 2 注册页
     * - 3 html网页
     * - 4 个人中心
     * - 5 客服中心
     * - 6 我的劳务合同
     * - 7 文章列表
     */
    /**
     * 获取未读消息数量[hasUnread]
     *
     * @param noid 用户编号
     */
    private Seminate seminate;
    private Message message;
    private String noid;
    private ArrayList<Map<String, String>> date;
    private int anInt = 0;

    //模拟数据源
    {
        for (int i = 0; i < 2; i++) {
            Map<String, String> map = new HashMap<>();
            map.put("slider", "..");
            list.add(map);
        }
    }

    //初始化定位
    public LocationClient mLocationClient = null;
    public BDLocationListener mListener = new MyLocationListener();
    private boolean isLocal = false;

    @Override
    protected int getLayoutResId() {
        return R.layout.aty_index;
    }

    @Override
    protected void initialized() {
        seminate = new Seminate();
        message = new Message();
    }

    @Override
    protected void requestData() {
        seminate.getSlider(this);
//        initData();
        imgvImg.setPages(new CBViewHolderCreator() {
            @Override
            public Object createHolder() {
                return new BannerView();
            }
        }, list).setPageIndicator(new int[]{R.drawable.spot_normal, R.drawable.spot_clicked})
                .setPageIndicatorAlign(ConvenientBanner.PageIndicatorAlign.CENTER_HORIZONTAL);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActionBar.hide();

        Intent intent = getIntent();
        LogUtil.e("139///////////////////////////////" + intent.getStringExtra("tag"));
        String tag = intent.getStringExtra("tag");
        if (!TextUtils.isEmpty(tag)) {
            Map<String, String> map = JSONUtils.parseKeyAndValueToMap(tag);
            Map<String, String> link_value = JSONUtils.parseKeyAndValueToMap(map.get("link_value"));
            String target_rule = map.get("target_rule");
            if (TextUtils.isEmpty(target_rule) || TextUtils.equals(target_rule, "0")) {
                return;
            }
            Bundle bundle = new Bundle();
            switch (target_rule) {
                case "1":
                    bundle.putString("contract_noid", link_value.get("contract_noid"));
                    startActivity(ContDetailAty.class, bundle);
                    break;
                case "2"://用户信息
                    bundle.putString("code", link_value.get("noid"));
                    bundle.putString("flag", "collect");
                    startActivity(MemberDetailAty.class, bundle);
                    break;
                case "3": //招工单详情 hire_noid
                    bundle.putString("hire_noid", link_value.get("hire_noid"));
                    startActivity(JODetailAty.class, bundle);
                    break;
            }
        }

        if (!Config.isLogin()) {
            startActivity(LoginAty.class, null);
            finish();
        }
        requestPermissions(Constants.PERMISSIONS_All_NAVI, authBaseArr);

        //定位
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (this.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 200);
            } else {
                initLocal();
            }
        } else {
            initLocal();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        noid = application.getUserInfo().get("noid");
        message.hasUnread(noid, this);


    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
//        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
        super.onDestroy();
    }

    @Override
    public void onError(Map<String, String> error) {
        super.onError(error);
        if (error.get("message").contains("登录失败")) {
            Config.setLoginState(false);
            startActivity(LoginAty.class, null);
            finish();
        }
//        LogUtil.e(error.toString());
    }

    private void initLocal() {
        Log.e("***", "初始化定位方法");
        mLocationClient = new LocationClient(getApplicationContext());
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        option.setCoorType("bd0911");
        option.setIsNeedAddress(true);
        option.setOpenGps(true);
        mLocationClient.setLocOption(option);
        mLocationClient.registerLocationListener(mListener);
        mLocationClient.start();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.e("***", requestCode + "权限");
        switch (requestCode) {
            case 200:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    initLocal();
                } else {
                    showToast("定位权限被拒绝，请手动开启定位权限");
                }
                break;
            default:
//                showToast("请手动添加权限，否则程序某些功能无法正常使用");
                break;
        }
    }


    public class MyLocationListener implements BDLocationListener {
        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            if (bdLocation != null) {
                Log.e("***", bdLocation.getCity() + bdLocation.getCityCode() + "定位城市");
                indexCityTv.setText(bdLocation.getCity() == null ? "定位中" : bdLocation.getCity());
                isLocal = false;
                anInt++;
                if (bdLocation.getCity() == null) {
                    //如果循环了50次，并且定位还未成功的话，提示
                    if (anInt == 50 && bdLocation.getCity() == null) {
                        Toast.makeText(IndexAty.this, "未检测到定位城市", Toast.LENGTH_SHORT).show();
                        indexCityTv.setText("天津");
                    } else {
                        isLocal = true;
                    }
                }
                if (isLocal) {
                    initLocal();
                }
            }
        }
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
                        bundle.putString("flag", "index");
                        startActivity(JOHelpAty.class, bundle);
                        break;
                }
            }
        });
    }


    @Override
    public void onComplete(RequestParams params, String result) {
        if (params.getUri().contains("Seminate/getSlider")) {

            date = JSONUtils.parseDataToMapList(result);
            list.clear();
            for (int i = 0; i < ListUtils.getSize(date); i++) {
                Map<String, String> map = new HashMap<>();
                map.put("slider", date.get(i).get("slider"));
                list.add(map);
            }
            initData();

        } else if (params.getUri().contains("Message/hasUnread")) {
            String data = JSONUtils.parseKeyAndValueToMap(result).get("data");
            if (!TextUtils.isEmpty(data)) {
                if (Integer.parseInt(data) > 0) {
                    vSpot.setVisibility(View.VISIBLE);
                } else {
                    vSpot.setVisibility(View.GONE);
                }
            }

        }
        super.onComplete(params, result);
    }


    @Event({R.id.index_message, R.id.index_recruitment, R.id.index_contract, R.id.index_search,
            R.id.index_mine, R.id.index_help, R.id.index_city})
    private void onClick(View view) {
        switch (view.getId()) {
            case R.id.index_message:
                startActivity(IndexMessageAty.class, null);
                break;
            case R.id.index_recruitment:
                startActivity(RecruitmentAty.class, null);
                break;
            case R.id.index_search:
                startActivity(SearchJobAty.class, null);
                break;
            case R.id.index_contract:
                startActivity(MyContractAty.class, null);
                break;
            case R.id.index_mine:
                startActivity(MineAty.class, null);
                break;
            case R.id.index_help:
                Bundle bundle = new Bundle();
                bundle.putString("flag", "index");
                startActivity(JOHelpAty.class, bundle);
                break;
            case R.id.index_city:
                removeProgressDialog();
//                startActivity(NavAty.class, null);
                break;
        }
    }

    @PermissionFail(requestCode = Constants.PERMISSIONS_All_NAVI)
    public void requestFail() {
        showToast("请求权限失败，请在设置中开启!!");
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
//            imageView.setImageResource(R.drawable.img_index);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            showDialog("提示", "是否退出自由人?", "确定", "取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    AppManager.getInstance().killAllActivity();
                    finish();
                }
            }, null);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
    //for receive customer msg from jpush server
//    private MessageReceiver mMessageReceiver;
//    public static final String MESSAGE_RECEIVED_ACTION = "com.example.jpushdemo.MESSAGE_RECEIVED_ACTION";
//    public static final String KEY_TITLE = "title";
//    public static final String KEY_MESSAGE = "message";
//    public static final String KEY_EXTRAS = "extras";
//
//    public void registerMessageReceiver() {
//        mMessageReceiver = new MessageReceiver();
//        IntentFilter filter = new IntentFilter();
//        filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
//        filter.addAction(MESSAGE_RECEIVED_ACTION);
//        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, filter);
//    }
//
//    public class MessageReceiver extends BroadcastReceiver {
//
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            try {
//                if (MESSAGE_RECEIVED_ACTION.equals(intent.getAction())) {
//                    String messge = intent.getStringExtra(KEY_MESSAGE);
//                    String extras = intent.getStringExtra(KEY_EXTRAS);
//                    StringBuilder showMsg = new StringBuilder();
//                    showMsg.append(KEY_MESSAGE + " : " + messge + "\n");
//                    if (!TextUtils.isEmpty(extras)) {
//                        showMsg.append(KEY_EXTRAS + " : " + extras + "\n");
//                    }
////                    setCostomMsg(showMsg.toString());
//                }
//            } catch (Exception e){
//            }
//        }
//    }

//    private void setCostomMsg(String msg){
//        if (null != msgText) {
//            msgText.setText(msg);
//            msgText.setVisibility(android.view.View.VISIBLE);
//        }
//    }
}
