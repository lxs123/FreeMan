package com.toocms.freeman.ui.searchjob;

import android.os.Bundle;
import android.util.Log;

import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.model.LatLng;
import com.toocms.freeman.R;
import com.toocms.freeman.ui.BaseAty;

import org.xutils.view.annotation.ViewInject;

public class AtyMap extends BaseAty {

    private String latStr; //纬度
    private String lonStr;  //经度

    @ViewInject(R.id.bmapView)
    private MapView mTextureMapView;

    private BaiduMap mMap;
    private LatLng point = null;
    private MapStatusUpdate mMapStatusUpdate;
    private MapStatus mStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SDKInitializer.initialize(getApplicationContext()); //地图初始化
        super.onCreate(savedInstanceState);
        mActionBar.setTitle("");

        latStr = getIntent().getStringExtra("lat");
        lonStr = getIntent().getStringExtra("lon");
        Log.e("***", latStr + "/" + lonStr);

        mMap = mTextureMapView.getMap();
        mMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
        point = new LatLng(Double.parseDouble(latStr), Double.parseDouble(lonStr));
        BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromResource(R.drawable.point);
        MarkerOptions overlayOptions = new MarkerOptions().position(point).icon(bitmapDescriptor);
        mMap.addOverlay(overlayOptions);
        mStatus = new MapStatus.Builder()
                .target(point)
                .zoom(18)
                .build();
        mMapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mStatus);
        mMap.setMapStatus(mMapStatusUpdate);

//定位图层  暂不需要
//        mMap.setMyLocationEnabled(true);
//        MyLocationData locationData = new MyLocationData.Builder()
//                .latitude(Double.parseDouble(latStr))
//                .longitude(Double.parseDouble(lonStr))
//                .build();
//
//        mMap.setMyLocationData(locationData);
//        MyLocationConfiguration configuration = new MyLocationConfiguration(MyLocationConfiguration.LocationMode.NORMAL, false, null);
//        mMap.setMyLocationConfigeration(configuration);

    }

    @Override
    protected int getLayoutResId() {
        return R.layout.aty_map;
    }

    @Override
    protected void initialized() {

    }

    @Override
    protected void requestData() {

    }

    @Override
    protected void onResume() {
        super.onResume();
        mTextureMapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mTextureMapView.onPause();
        mMap.setMyLocationEnabled(false);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mTextureMapView.onDestroy();
    }
}
