package com.toocms.freeman.ui.recruitment;

import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiIndoorResult;
import com.baidu.mapapi.search.poi.PoiNearbySearchOption;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.baidu.mapapi.search.poi.PoiSortType;
import com.toocms.freeman.R;
import com.toocms.freeman.ui.BaseAty;
import com.toocms.freeman.ui.mine.baseinformation.BaseInformationModifyFgt;
import com.toocms.freeman.ui.recruitment.myjoborder.SearchingAddressAty;

import org.xutils.http.RequestParams;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.zero.android.common.util.ListUtils;
import cn.zero.android.common.view.swipetoloadlayout.view.SwipeToLoadRecyclerView;

/**
 * Created by ElveTrees on 2015/11/24.
 * 定位小区
 */
public class LocationAddressAty extends BaseAty implements BDLocationListener {

    @ViewInject(R.id.location_address_lv)
    private SwipeToLoadRecyclerView lv;

    @ViewInject(R.id.address_mapview)
    private MapView mapView;
    @ViewInject(R.id.location_imgv_yuan)
    private ImageView imgvYuan;
    @ViewInject(R.id.location_imgv_index)
    private ImageView imgvIndex; //中间定位的图片索引

    private LocationClient mLocationClient;//定位
    private BaiduMap baiduMap;
    private BDLocation bdLocation;//定位后的信息
    private PoiSearch poiSearch;//百度提供的检索类
    private List<PoiInfo> allPois;//检索后的信息
    private LatLng move_Latlng;//移动地图后的坐标

    private LatLng location_latLng;//定位成功后的坐标
    private MyAdapter adapter;
    private Animation animation;

    private String address;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SDKInitializer.initialize(getApplicationContext());
        super.onCreate(savedInstanceState);
        mActionBar.hide();
        lv.getRecyclerView().setLayoutManager(new LinearLayoutManager(this));
        showProgressDialog();
        lv.setAdapter(adapter);
        mapView.showZoomControls(false);
        baiduMap = mapView.getMap();
        baiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
        baiduMap.setMaxAndMinZoomLevel(20, 20);
        baiduMap.setOnMapTouchListener(mapViewTouch);
        baiduMap.setMyLocationEnabled(false);//设置定位图层，会显示一个绿点在中间

        initLocation();
        mLocationClient.registerLocationListener(this);
        mLocationClient.start();


        animation = AnimationUtils.loadAnimation(this, R.anim.anim_location_index);
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.aty_addr_search;
    }

    @Override
    protected void initialized() {
        adapter = new MyAdapter();

    }

    @Override
    protected void requestData() {

    }
//    R.id.location_imgv_loc, R.id.location_address_tv_searching

    @Event({R.id.back, R.id.location_imgv_yuan, R.id.location_address_tv_searching})
    private void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.location_address_tv_searching:
                startActivityForResult(SearchingAddressAty.class, null, AddAddressAty.SEARCH_ADD);
                break;
            case R.id.location_imgv_yuan:
                mLocationClient.start();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            return;
        }
        switch (requestCode) {
            case AddAddressAty.SEARCH_ADD:
                setResult(RESULT_OK, data);
                finish();
                break;
            case BaseInformationModifyFgt.BASE_INFORMATION_CODE:
                setResult(RESULT_OK, data);
                finish();
                break;
        }
    }

    /**
     * 初始化定位
     **/
    private void initLocation() {
        mLocationClient = new LocationClient(getApplicationContext());
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy
        );//可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        option.setCoorType("bd09ll");//可选，默认gcj02，设置返回的定位结果坐标系
        int span = 1000;
        option.setScanSpan(span);//可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
        option.setIsNeedAddress(true);//可选，设置是否需要地址信息，默认不需要
        option.setOpenGps(true);//可选，默认false,设置是否使用gps
        option.setLocationNotify(true);//可选，默认false，设置是否当gps有效时按照1S1次频率输出GPS结果
        option.setIsNeedLocationDescribe(true);//可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
        option.setIsNeedLocationPoiList(true);//可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
        option.setIgnoreKillProcess(false);//可选，默认false，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认杀死
        option.SetIgnoreCacheException(false);//可选，默认false，设置是否收集CRASH信息，默认收集
        option.setEnableSimulateGps(true);//可选，默认false，设置是否需要过滤gps仿真结果，默认需要
        mLocationClient.setLocOption(option);
    }


    float displacement; //位移距离
    float startx;
    float starty;
    /**
     * 地图触摸事件
     **/
    BaiduMap.OnMapTouchListener mapViewTouch = new BaiduMap.OnMapTouchListener() {
        @Override
        public void onTouch(MotionEvent motionEvent) {

            switch (motionEvent.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    startx = motionEvent.getX();
                    starty = motionEvent.getY();

                    imgvIndex.startAnimation(animation);
                    break;
                case MotionEvent.ACTION_MOVE:
                    break;
                case MotionEvent.ACTION_UP:
//                    imgvIndex.getAnimation().cancel();
                    imgvIndex.clearAnimation();
                    float endx, endy;
                    endx = motionEvent.getX();
                    endy = motionEvent.getY();

                    float x, y;
                    x = Math.abs(startx - endx);
                    y = Math.abs(starty - endy);
                    displacement = (float) Math.sqrt(x * x + y * y);

                    if (displacement > 250) {
                        move_Latlng = baiduMap.getProjection().fromScreenLocation(getCenterViewPoint());
                        MyLocationData myLocationData = new MyLocationData.Builder()
                                .latitude(move_Latlng.latitudeE6).longitude(move_Latlng.longitudeE6)
                                .build();
                        baiduMap.setMyLocationData(myLocationData);
                        MapStatusUpdate statusUpdate = MapStatusUpdateFactory.newLatLng(move_Latlng);
                        baiduMap.animateMapStatus(statusUpdate);
//                    imgv_location_middle.setTranslationY(3);
                        getReverseGeoCodeByLatlng(move_Latlng);
                    }

                    break;
            }
        }
    };


    @Override
    public void onComplete(RequestParams params, String result) {
        super.onComplete(params, result);
    }

    @Override
    public void onReceiveLocation(BDLocation bdLocation) {
        if (bdLocation.getAddrStr() != null) {
            this.bdLocation = bdLocation;
            double latitude = bdLocation.getLatitude();
            double longitude = bdLocation.getLongitude();
            baiduMap.setMyLocationEnabled(true);//设置显示定位图层

//          显示定位的位置
            MyLocationData myLocationData = new MyLocationData.Builder()
                    .latitude(latitude).longitude(longitude)
                    .build();
            baiduMap.setMyLocationData(myLocationData);
            location_latLng = new LatLng(latitude, longitude);//定位后的坐标
//            addAddressMark(location_latLng, true);
            MapStatusUpdate statusUpdate = MapStatusUpdateFactory.newLatLng(location_latLng);
            baiduMap.animateMapStatus(statusUpdate);
            //检索周边信息
            String address = bdLocation.getAddrStr();
            if (address != null) {
                searchNearInfo(location_latLng, address.substring(0, address.length() / 2));
            }
            mLocationClient.stop();//停止定位
            removeProgressDialog();
        }
    }

    /**
     * 检索周边
     **/
    private void searchNearInfo(LatLng latLng, String keyword) {
        poiSearch = PoiSearch.newInstance();
        PoiNearbySearchOption option = new PoiNearbySearchOption();
        option.location(latLng);
        option.keyword(keyword);//搜索关键字
        option.radius(5000);//5000M内
        option.sortType(PoiSortType.distance_from_near_to_far);//类型排序
        option.pageCapacity(12);//每页12条信息
        poiSearch.searchNearby(option);
        poiSearch.setOnGetPoiSearchResultListener(new OnGetPoiSearchResultListener() {
            @Override
            public void onGetPoiResult(PoiResult poiResult) {
                allPois = poiResult.getAllPoi();

                //将目前定位的地址添加到list中
                PoiInfo info = new PoiInfo();
                if (allPois != null) {
//                    if (bdLocation != null) {
//                        info.name = bdLocation.getAddress().street;
//                        info.address = bdLocation.getAddrStr();
//                        allPois.add(0, info);
//                    }
//                    initSelects();//初始化全部没选中
                    //显示List数据
                    adapter.notifyDataSetChanged();
                }
                poiSearch.destroy();
            }

            @Override
            public void onGetPoiDetailResult(PoiDetailResult poiDetailResult) {

            }

            @Override
            public void onGetPoiIndoorResult(PoiIndoorResult poiIndoorResult) {

            }
        });
    }


    /**
     * 获取中间的View的位置
     **/
    private Point getCenterViewPoint() {
        int location[] = new int[2];
        imgvYuan.getLocationInWindow(location);
        return new Point(location[0] + imgvYuan.getWidth() / 2, location[1] + imgvYuan.getHeight() / 2);
    }

    /**
     * 根据坐标检索获取坐标地址的信息
     **/
    private void getReverseGeoCodeByLatlng(LatLng latLng) {
        GeoCoder geoCoder = GeoCoder.newInstance();
        ReverseGeoCodeOption option = new ReverseGeoCodeOption();
        option.location(latLng);
        geoCoder.reverseGeoCode(option);
        geoCoder.setOnGetGeoCodeResultListener(new OnGetGeoCoderResultListener() {
            @Override
            public void onGetGeoCodeResult(GeoCodeResult geoCodeResult) {

            }

            @Override
            public void onGetReverseGeoCodeResult(ReverseGeoCodeResult reverseGeoCodeResult) {
                choosePosition = -10;
                PoiInfo poiInfo = new PoiInfo();
                poiInfo.name = reverseGeoCodeResult.getAddressDetail().street;
                poiInfo.address = reverseGeoCodeResult.getAddress();
                allPois = reverseGeoCodeResult.getPoiList();
//                allPois.add(0, poiInfo);
                adapter.notifyDataSetChanged();
            }
        });
    }


    private int choosePosition = -10;
    private PoiInfo _item;

    private class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {

        public PoiInfo getItem(int position) {
            return allPois.get(position);
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = getLayoutInflater().inflate(R.layout.listitem_location_address, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, final int position) {
            final PoiInfo item = getItem(position);
//            LogUtil.e("莪玩热舞rewrew------"+item.uid);
//            LogUtil.e("莪玩热舞rewrew------"+item.city);
            holder.tv_name.setText(item.name);
            holder.tv_address.setText(item.address);
            holder.imgv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isFastClick()) {
                        return;
                    }
                    _item = item;
                    handler.sendEmptyMessageDelayed(0, 200);
                    choosePosition = position;
                    notifyDataSetChanged();
                }
            });

        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public int getItemCount() {
            return ListUtils.getSize(allPois);
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            @ViewInject(R.id.searching_address_tv_name)
            private TextView tv_name;
            @ViewInject(R.id.searching_address_tv_address)
            private TextView tv_address;
            @ViewInject(R.id.location_imgv)
            private LinearLayout imgv;

            public ViewHolder(View view) {
                super(view);
                x.view().inject(this, view);
            }
        }
    }


    android.os.Handler handler = new android.os.Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Map<String, String> map = new HashMap<>();
            map.put("address", _item.name);
            map.put("longitude", _item.location.longitude + "");
            map.put("latitude", _item.location.latitude + "");
//            EventBus.getDefault().post(new EventBean(map,"address"));
            Intent intent = new Intent();
            intent.putExtra("address", _item.name);
            intent.putExtra("detail", _item.address);
            intent.putExtra("longitude", _item.location.longitude + "");
            intent.putExtra("latitude", _item.location.latitude + "");
            setResult(RESULT_OK, intent);
            finish();
        }
    };


    private long lastClickTime;

    public synchronized boolean isFastClick() {
        long time = System.currentTimeMillis();
        if (time - lastClickTime < 500) {
            return true;
        }
        lastClickTime = time;
        return false;
    }
}
