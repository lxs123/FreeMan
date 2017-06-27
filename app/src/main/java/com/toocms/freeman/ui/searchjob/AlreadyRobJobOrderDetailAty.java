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
 * 招工单详情（已抢找工单）
 */
public class AlreadyRobJobOrderDetailAty extends BaseAty {

    @ViewInject(R.id.tablayout_recruitment_detail)
    TabLayout recruitmentDetailTablayout;
    Fragment fragment;

    private Collect mCollect;


    public String status;   //签约状态(仅在搜索--》已抢招工单中使用)
    public String coor_diff;    //招工单修改过没有(仅在搜索--》已抢招工单中使用)
    public String labNoidStr;   //自己的编号
    public String hireNoidStr;  //招工单编号
    public String noid; //对方的编号
    public String coor_status;

    public boolean isCollect;   //收藏状态
    public MenuItem mItem1;
    public Hire mHire;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActionBar.setTitle("招工单详情");

        if (getIntent().getStringExtra("flag").equals("wzw1")) {
            labNoidStr = getIntent().getStringExtra("lab_noid");
            hireNoidStr = getIntent().getStringExtra("hire_noid");
            noid = getIntent().getStringExtra("noid");
            status = getIntent().getStringExtra("status");
            coor_diff = getIntent().getStringExtra("coor_diff");
            coor_status = getIntent().getStringExtra("coor_status");
            Log.e("***", getClass().getSimpleName()+":"+coor_status);
        }

        mHire.detail(application.getUserInfo().get("noid"),hireNoidStr,this);

        addFragment(new RecruitmentInformationFgt(), "0");

        //初始化tablayout及相关监听
        recruitmentDetailTablayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0:     //劳动信息详情
                        replace("0");
                        break;
                    case 1:      //资方信息详情
                        replace("1");
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

        if (getIntent().getStringExtra("item")!=null && getIntent().getStringExtra("item").equals("1")){
            recruitmentDetailTablayout.getTabAt(1).select();
        }
        else {
            Log.e("***","哈哈哈");
        }

    }

    @Event({R.id.my_jo_back})
    private void onClick(View view) {
        switch (view.getId()) {
            case R.id.my_jo_back:
                finish();
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_collect, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        mItem1 = menu.findItem(R.id.menu_collect);
        if (mItem1 != null){
            if (!isCollect) {
                mItem1.setTitle("收藏");
            } else {
                mItem1.setTitle("取消收藏");
            }
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

    //fragment相关
    private void replace(String fragNum) {
        FragmentManager manager = getSupportFragmentManager();
        if (fragNum.equals(String.valueOf(0))) {
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
    public void onComplete(RequestParams params, String result) {
        if (params.getUri().contains("Collect/insertHire")) {
            showToast("收藏成功");
            isCollect = true;
            mItem1.setTitle("取消收藏");
        }
        if (params.getUri().contains("Collect/cancelHire")) {
            showToast("取消收藏");
            isCollect = false;
            mItem1.setTitle("收藏");
        }
        if (params.getUri().contains("Hire/detail")){
            isCollect = JSONUtils.parseDataToMap(result).get("is_collect").equals("1");
            if (mItem1 != null){
                if (!isCollect) {
                    mItem1.setTitle("收藏");
                } else {
                    mItem1.setTitle("取消收藏");
                }
            }
//            if (!isCollect) {
//                mItem1.setTitle("收藏");
//            } else {
//                mItem1.setTitle("取消收藏");
//            }
        }
        super.onComplete(params, result);
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.aty_already_rob_job_order_detail;
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
