package com.toocms.freeman.ui.util;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.navisdk.adapter.BNCommonSettingParam;
import com.baidu.navisdk.adapter.BNOuterLogUtil;
import com.baidu.navisdk.adapter.BNOuterTTSPlayerCallback;
import com.baidu.navisdk.adapter.BNRoutePlanNode;
import com.baidu.navisdk.adapter.BNaviSettingManager;
import com.baidu.navisdk.adapter.BaiduNaviManager;
import com.toocms.freeman.R;

import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by admin on 2017/5/12.
 */

public class NavigationAty extends Activity {
    public static List<Activity> activityList = new LinkedList<Activity>();
    private static final String APP_FOLDER_NAME = "FreeMan";
    public static final String ROUTE_PLAN_NODE = "routePlanNode";
    private static final String[] authComArr = {Manifest.permission.READ_PHONE_STATE};

    private static final String[] authBaseArr = {Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_FINE_LOCATION};
    private static final int authBaseRequestCode = 1;
    private static final int authComRequestCode = 2;
    //    private Button mBdmcNaviBtn = null;d
    public boolean hasInitSuccess = false;
    private boolean hasRequestComAuth = false;
    private String mSDCardPath = null;
    @ViewInject(R.id.btn_navi)
    Button button;
    String authinfo = null;
    private static NavigationAty instance;
    private double lati;
    private double longa;
    private String addrStr;
    private String latitude;
    private String longitude;
    private String ress;
    private LocationClient mLocationClient;
    private BDLocationListener myLocationListener;
    //    @Override
//    protected int getLayoutResId() {
//        activityList.add(this);
//        return R.layout.aty_navigation;
//    }
//
//    @Override
//    protected void initialized() {
//
//    }
//
//    @Override
//    protected void requestData() {
//
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityList.add(this);
        setContentView(R.layout.aty_navigation);
        BNOuterLogUtil.setLogSwitcher(true);
        x.view().inject(this);
        mLocationClient = new LocationClient(getApplicationContext());
        if (initDirs()) {
            initNavi();
        }
        BDLocationListener myLocationListener = new MyLocationListener();
//        myLocationListener.onReceiveLocation(new BDLocation());
        mLocationClient.registerLocationListener(myLocationListener);
        longitude = getIntent().getStringExtra("longitude");
        latitude = getIntent().getStringExtra("latitude");
        ress = getIntent().getStringExtra("ress");
//        initLocation();
        if (BaiduNaviManager.isNaviInited()) {
            routeplanToNavi(BNRoutePlanNode.CoordinateType.BD09LL, latitude, longitude, ress);
        }
    }

    public Context context;

    public void str(Context context) {
        this.context = context;

        initLocation();
        myLocationListener = new MyLocationListener();
        myLocationListener.onReceiveLocation(new BDLocation());
        mLocationClient.registerLocationListener(myLocationListener);
        mLocationClient.start();
    }

    public Map<String, String> getLatlon(Context context) {
        this.context = context;
        initLocation();
        myLocationListener = new MyLocationListener();
        myLocationListener.onReceiveLocation(new BDLocation());
        mLocationClient.registerLocationListener(myLocationListener);
        mLocationClient.start();
        Map<String, String> map = new HashMap<>();
        map.put("latitude", lati+"");
        map.put("longitude", longa+"");
        map.put("ress", addrStr);
        return map;
    }

    public static final NavigationAty getInstance() {
        if (instance == null) {
            instance = new NavigationAty();
        }
        return instance;
    }


    public class MyLocationListener implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            //Receive Location
            //经纬度
            lati = location.getLatitude();
            longa = location.getLongitude();
            //打印出当前位置
            Log.i("TAG", "location.getAddrStr()=" + location.getAddrStr());
            //打印出当前城市
            Log.i("TAG", "location.getCity()=" + location.getCity());
            addrStr = location.getAddrStr();
            //返回码
            int i = location.getLocType();
            mLocationClient.stop();
        }
    }

    private void initLocation() {
        mLocationClient = new LocationClient(context);
        LocationClientOption option = new LocationClientOption();
        //就是这个方法设置为true，才能获取当前的位置信息
        option.setIsNeedAddress(true);
        option.setOpenGps(true);
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy
        );//可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        option.setCoorType("gcj02");//可选，默认gcj02，设置返回的定位结果坐标系
        //int span = 1000;
        //option.setScanSpan(span);//可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
        mLocationClient.setLocOption(option);
    }

    public void startNav(String latitude, String longitude, String ress, Context context) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.ress = ress;

    }

    private void initListener() {


        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {


            }
        });


    }

    public BNRoutePlanNode.CoordinateType mCoordinateType = null;

    public void routeplanToNavi(BNRoutePlanNode.CoordinateType coType, String latitude, String longitude, String ress) {
        mCoordinateType = coType;
        if (!hasInitSuccess) {
            Toast.makeText(context, "还未初始化!", Toast.LENGTH_SHORT).show();
        }
        // 权限申请
        if (android.os.Build.VERSION.SDK_INT >= 23) {
            // 保证导航功能完备
            if (!hasCompletePhoneAuth()) {
                if (!hasRequestComAuth) {
                    hasRequestComAuth = true;
                    Activity d = (Activity) context;
                    d.requestPermissions(authComArr, authComRequestCode);
                    return;
                } else {
                    Toast.makeText(context, "没有完备的权限!", Toast.LENGTH_SHORT).show();
                }
            }

        }
        BNRoutePlanNode sNode = null;
        BNRoutePlanNode eNode = null;
        switch (coType) {
            case GCJ02: {
                sNode = new BNRoutePlanNode(116.30142, 40.05087, "百度大厦", null, coType);
                eNode = new BNRoutePlanNode(116.39750, 39.90882, "北京天安门", null, coType);
                break;
            }
            case WGS84: {
                sNode = new BNRoutePlanNode(116.300821, 40.050969, "百度大厦", null, coType);
                eNode = new BNRoutePlanNode(116.397491, 39.908749, "北京天安门", null, coType);
                break;
            }
            case BD09_MC: {
                sNode = new BNRoutePlanNode(116.30784537597782, 40.057009624099436, "百度大厦", null, coType);
                eNode = new BNRoutePlanNode(Double.parseDouble(longitude), Double.parseDouble(latitude), "佛山区", null, coType);
                break;
            }
            case BD09LL: {
                sNode = new BNRoutePlanNode(longa, lati, addrStr, null, coType);
                eNode = new BNRoutePlanNode(Double.parseDouble(longitude), Double.parseDouble(latitude), ress, null, coType);
                break;
            }
            default:

        }
        if (sNode != null && eNode != null) {
            List<BNRoutePlanNode> list = new ArrayList<BNRoutePlanNode>();
            list.add(sNode);
            list.add(eNode);
            BaiduNaviManager.getInstance().launchNavigator((Activity) context, list, 1, true, new DemoRoutePlanListener(sNode));
        }
    }

    public boolean initDirs() {
        mSDCardPath = getSdcardDir();
        if (mSDCardPath == null) {
            return false;
        }
        File f = new File(mSDCardPath, APP_FOLDER_NAME);
        if (!f.exists()) {
            try {
                f.mkdir();
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }

    private boolean hasCompletePhoneAuth() {
        // TODO Auto-generated method stub

        PackageManager pm = context.getPackageManager();
        for (String auth : authComArr) {
            if (pm.checkPermission(auth, context.getPackageName()) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    public boolean hasBasePhoneAuth() {
        // TODO Auto-generated method stub

        PackageManager pm = context.getPackageManager();
        for (String auth : authBaseArr) {
            if (pm.checkPermission(auth, context.getPackageName()) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    public class DemoRoutePlanListener implements BaiduNaviManager.RoutePlanListener {

        private BNRoutePlanNode mBNRoutePlanNode = null;

        public DemoRoutePlanListener(BNRoutePlanNode node) {
            mBNRoutePlanNode = node;
        }

        @Override
        public void onJumpToNavigator() {
            /*
             * 设置途径点以及resetEndNode会回调该接口
             */

            for (Activity ac : activityList) {

                if (ac.getClass().getName().endsWith("BNDemoGuideActivity")) {
                    return;
                }
            }
//            (BaseAty)getBaseContext().removeProgressDialog();
            Intent intent = new Intent(context, BNDemoGuideActivity.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable(ROUTE_PLAN_NODE, (BNRoutePlanNode) mBNRoutePlanNode);
            intent.putExtras(bundle);
            context.startActivity(intent);

        }

        @Override
        public void onRoutePlanFailed() {
            // TODO Auto-generated method stub
            Toast.makeText(context, "算路失败", Toast.LENGTH_SHORT).show();
        }
    }


    public void initNavi() {

        BNOuterTTSPlayerCallback ttsCallback = null;

        // 申请权限
        if (android.os.Build.VERSION.SDK_INT >= 23) {

            if (!hasBasePhoneAuth()) {
                Activity d = (Activity) context;
                d.requestPermissions(authBaseArr, authBaseRequestCode);
                return;

            }
        }

        BaiduNaviManager.getInstance().init((Activity) context, mSDCardPath, APP_FOLDER_NAME, new BaiduNaviManager.NaviInitListener() {
            @Override
            public void onAuthResult(int status, String msg) {
                if (0 == status) {
                    authinfo = "key校验成功!";
                } else {
                    authinfo = "key校验失败, " + msg;
                }
//                runOnUiThread(new Runnable() {
//
//                    @Override
//                    public void run() {
//                        Toast.makeText(context, authinfo, Toast.LENGTH_LONG).show();
//                    }
//                });
            }

            public void initSuccess() {
//                Toast.makeText(context, "百度导航引擎初始化成功", Toast.LENGTH_SHORT).show();

            }

            public void initStart() {
//                Toast.makeText(context, "百度导航引擎初始化开始", Toast.LENGTH_SHORT).show();
                hasInitSuccess = true;
                initSetting();
            }

            public void initFailed() {
//                Toast.makeText(context, "百度导航引擎初始化失败", Toast.LENGTH_SHORT).show();
                hasInitSuccess = false;
            }

        }, null, ttsHandler, ttsPlayStateListener);

    }

    /**
     * 内部TTS播报状态回传handler
     */
    private Handler ttsHandler = new Handler() {
        public void handleMessage(Message msg) {
            int type = msg.what;
            switch (type) {
                case BaiduNaviManager.TTSPlayMsgType.PLAY_START_MSG: {
//                     showToastMsg("Handler : TTS play start");
                    break;
                }
                case BaiduNaviManager.TTSPlayMsgType.PLAY_END_MSG: {
//                     showToastMsg("Handler : TTS play end");
                    break;
                }
                default:
                    break;
            }
        }
    };
    /**
     * 内部TTS播报状态回调接口
     */
    private BaiduNaviManager.TTSPlayStateListener ttsPlayStateListener = new BaiduNaviManager.TTSPlayStateListener() {

        @Override
        public void playEnd() {
//             showToastMsg("TTSPlayStateListener : TTS play end");
        }

        @Override
        public void playStart() {
//             showToastMsg("TTSPlayStateListener : TTS play start");
        }
    };

    public void showToastMsg(final String msg) {
//        context.runOnUiThread(new Runnable() {
//
//            @Override
//            public void run() {
//                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
//            }
//        });
    }

    private String getSdcardDir() {
        if (Environment.getExternalStorageState().equalsIgnoreCase(Environment.MEDIA_MOUNTED)) {
            return Environment.getExternalStorageDirectory().toString();
        }
        return null;
    }

    private void initSetting() {
        // BNaviSettingManager.setDayNightMode(BNaviSettingManager.DayNightMode.DAY_NIGHT_MODE_DAY);
        BNaviSettingManager
                .setShowTotalRoadConditionBar(BNaviSettingManager.PreViewRoadCondition.ROAD_CONDITION_BAR_SHOW_ON);
        BNaviSettingManager.setVoiceMode(BNaviSettingManager.VoiceMode.Veteran);
        // BNaviSettingManager.setPowerSaveMode(BNaviSettingManager.PowerSaveMode.DISABLE_MODE);
        BNaviSettingManager.setRealRoadCondition(BNaviSettingManager.RealRoadCondition.NAVI_ITS_ON);
        Bundle bundle = new Bundle();
        // 必须设置APPID，否则会静音
        bundle.putString(BNCommonSettingParam.TTS_APP_ID, "9641664");
        BNaviSettingManager.setNaviSdkParam(bundle);
    }

    private BNOuterTTSPlayerCallback mTTSCallback = new BNOuterTTSPlayerCallback() {

        @Override
        public void stopTTS() {
            // TODO Auto-generated method stub
            Log.e("test_TTS", "stopTTS");
        }

        @Override
        public void resumeTTS() {
            // TODO Auto-generated method stub
            Log.e("test_TTS", "resumeTTS");
        }

        @Override
        public void releaseTTSPlayer() {
            // TODO Auto-generated method stub
            Log.e("test_TTS", "releaseTTSPlayer");
        }

        @Override
        public int playTTSText(String speech, int bPreempt) {
            // TODO Auto-generated method stub
            Log.e("test_TTS", "playTTSText" + "_" + speech + "_" + bPreempt);

            return 1;
        }

        @Override
        public void phoneHangUp() {
            // TODO Auto-generated method stub
            Log.e("test_TTS", "phoneHangUp");
        }

        @Override
        public void phoneCalling() {
            // TODO Auto-generated method stub
            Log.e("test_TTS", "phoneCalling");
        }

        @Override
        public void pauseTTS() {
            // TODO Auto-generated method stub
            Log.e("test_TTS", "pauseTTS");
        }

        @Override
        public void initTTSPlayer() {
            // TODO Auto-generated method stub
            Log.e("test_TTS", "initTTSPlayer");
        }

        @Override
        public int getTTSState() {
            // TODO Auto-generated method stub
            Log.e("test_TTS", "getTTSState");
            return 1;
        }
    };


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        // TODO Auto-generated method stub
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == authBaseRequestCode) {
            initNavi();
            for (int ret : grantResults) {
                if (ret == 0) {
                    continue;
                } else {
                    Toast.makeText(context, "缺少导航基本的权限!", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        } else if (requestCode == authComRequestCode) {
            for (int ret : grantResults) {
                if (ret == 0) {
                    continue;
                }
            }
            routeplanToNavi(mCoordinateType, latitude, longitude, ress);
        }

    }


}
