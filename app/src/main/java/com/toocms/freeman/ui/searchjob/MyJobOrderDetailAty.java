package com.toocms.freeman.ui.searchjob;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.toocms.freeman.R;
import com.toocms.freeman.https.Collect;
import com.toocms.freeman.https.Hire;
import com.toocms.freeman.ui.BaseAty;

import org.xutils.http.RequestParams;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import cn.zero.android.common.util.JSONUtils;

/**
 * 我的招工单详情
 */

public class MyJobOrderDetailAty extends BaseAty {

    @ViewInject(R.id.tablayout_recruitment_detail)
    TabLayout recruitmentDetailTablayout;
    Fragment fragment;

    private Collect mCollect;

    //从上个页面传进来的信息
    public String labNoidStr;   //自己的编号
    public String hireNoidStr;  //招工单编号
    public String noid; //对方的编号

    //用来判断按钮个数的字段
    public String coor_status;  //0为未操作，1同意招工单  2修改过招工单等待回复  3资方针对修改做出回复
    public String status;

    public boolean isCollect;//收藏状态
    public MenuItem mItem;
    private Hire mHire;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActionBar.setTitle("我的招工单详情");

        if (getIntent().getStringExtra("flag").equals("wzw1")) {
            labNoidStr = getIntent().getStringExtra("lab_noid");
            hireNoidStr = getIntent().getStringExtra("hire_noid");
            noid = getIntent().getStringExtra("noid");
            coor_status = getIntent().getStringExtra("coor_status");
            status = getIntent().getStringExtra("status");
            Log.e("***", labNoidStr + "/" + hireNoidStr + "/" + noid + "/" + status);
        }

        showProgressDialog();
        mHire.detail(application.getUserInfo().get("noid"), hireNoidStr, this);

        addFragment(new RecruitmentInformationFgt(), "4");

        //初始化tablayout及相关监听
        recruitmentDetailTablayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0:     //招工信息详情
                        replace("4");
                        break;
                    case 1:      //资方信息详情
                        replace("5");
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        if (getIntent().getStringExtra("item") != null && getIntent().getStringExtra("item").equals("1")){
            recruitmentDetailTablayout.getTabAt(1).select();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_collect, menu);
        mItem = menu.findItem(R.id.menu_collect);
        if (!isCollect ) {
            mItem.setTitle("收藏");
        } else {
            mItem.setTitle("取消收藏");
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        Log.e("***","menu开始准备");
        if (!isCollect ) {
            mItem.setTitle("收藏");
        } else {
            mItem.setTitle("取消收藏");
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_collect) {
            if (isCollect == false) {
                showProgressDialog();
                mCollect.insertHire(labNoidStr, hireNoidStr, this);
                Log.e("***", "收藏：" + labNoidStr + "/" + hireNoidStr);
            } else {
                showProgressDialog();
                mCollect.cancelHire(labNoidStr, hireNoidStr, this);
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onComplete(RequestParams params, String result) {
        if (params.getUri().contains("Collect/insertHire")) {
            showToast(JSONUtils.parseKeyAndValueToMap(result).get("message"));
            isCollect = true;
            mItem.setTitle("取消收藏");
        }
        if (params.getUri().contains("Collect/cancelHire")) {
            showToast(JSONUtils.parseKeyAndValueToMap(result).get("message"));
            isCollect = false;
            mItem.setTitle("收藏");
        }
        if (params.getUri().contains("Hire/detail")) {
            Log.e("***","detail执行了");
            isCollect = JSONUtils.parseDataToMap(result).get("is_collect").equals("1");
            if (mItem!=null){
                if (!isCollect ) {
                    mItem.setTitle("收藏");
                } else {
                    mItem.setTitle("取消收藏");
                }
            }

        }
        super.onComplete(params, result);
    }

    @Event({R.id.my_jo_back})
    private void onClick(View view) {
        switch (view.getId()) {
            case R.id.my_jo_back:
                finish();
                break;
        }
    }

    //fragment相关
    private void replace(String fragNum) {
        FragmentManager manager = getSupportFragmentManager();
        if (fragNum.equals(String.valueOf(4))) {
            fragment = new RecruitmentInformationFgt();
        } else {
            fragment = new ManagementFgt();
        }
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.look_con_info, fragment, fragNum);
        transaction.commit();
    }

    private void addFragment(Fragment fragment, String tag) {
        if (fragment.isAdded()) {
        } else {
            FragmentManager manager = getSupportFragmentManager();
            FragmentTransaction transaction = manager.beginTransaction();
            transaction.add(R.id.look_con_info, fragment, tag);
            transaction.commit();
        }
    }


    @Override
    protected int getLayoutResId() {
        return R.layout.activity_my_job_order_detail_aty;
    }

    @Override
    protected void initialized() {
        mCollect = new Collect();
        mHire = new Hire();
    }

    @Override
    protected void requestData() {

    }

}
