package com.toocms.freeman.ui.searchjob;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.toocms.freeman.R;
import com.toocms.freeman.https.Collect;
import com.toocms.freeman.ui.BaseAty;

import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ViewInject;

/**
 * 搜工作--》查询招工信息--》筛选--》招工信息查询结果item--》已抢招工单
 */
public class RecruitmentOrderAt extends BaseAty {

    @ViewInject(R.id.tablayout_recruitment_detail)
    TabLayout recruitmentDetailTablayout;
    Fragment fragment;

    private Collect mCollect;

    //从上个页面传进来的信息
    public String labNoidStr;   //自己的编号
    public String hireNoidStr;  //招工单编号
    public String noid; //对方的编号

    public boolean isCollect;//收藏状态
    public MenuItem mItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActionBar.setTitle("已抢招工单详情");

        if (getIntent().getStringExtra("flag").equals("wzw1")) {
            labNoidStr = getIntent().getStringExtra("lab_noid");
            hireNoidStr = getIntent().getStringExtra("hire_noid");
            noid = getIntent().getStringExtra("noid");
            Log.e("***", labNoidStr + "/" + hireNoidStr + "/" + noid);
        }

        addFragment(new RecruitmentInformationFgt(), "2");


        //初始化tablayout及相关监听
        recruitmentDetailTablayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0:     //劳动信息详情
                        replace("2");
                        break;
                    case 1:      //资方信息详情
                        replace("3");
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

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_collect, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        mItem = menu.findItem(R.id.menu_collect);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_collect:
                if (isCollect == false) {
                    showProgressDialog();
                    mCollect.insertHire(labNoidStr, hireNoidStr, this);
                    Log.e("***", "收藏：" + labNoidStr + "/" + hireNoidStr);
                    item.setTitle("取消收藏");
                } else {
                    showProgressDialog();
                    mCollect.cancelHire(labNoidStr, hireNoidStr, this);
                    item.setTitle("收藏");
                }
        }
//        if (item.getItemId() == R.id.menu_collect) {
//
//        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onComplete(RequestParams params, String result) {
        if (params.getUri().contains("Collect/insertHire")) {
            showToast("收藏成功");
            isCollect = true;
        }
        if (params.getUri().contains("Collect/cancelHire")) {
            showToast("取消收藏");
            isCollect = false;
        }
        super.onComplete(params, result);
    }

    //fragment相关
    private void replace(String fragNum) {
        FragmentManager manager = getSupportFragmentManager();
        if (fragNum.equals(String.valueOf(2))) {
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
        return R.layout.aty_recruitment_order;
    }

    @Override
    protected void initialized() {
        mCollect = new Collect();
    }

    @Override
    protected void requestData() {

    }
}
