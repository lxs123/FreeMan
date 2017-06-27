package com.toocms.freeman.ui.contract.editcontract.look;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;

import com.toocms.freeman.R;
import com.toocms.freeman.ui.BaseAty;

import org.xutils.view.annotation.ViewInject;

/**
 * Created by admin on 2017/3/28.
 */

public class LookContractInfo extends BaseAty {
    @ViewInject(R.id.edit_contract_tab)
    TabLayout tabLayout;
    private Fragment fragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tabLayout.addTab(tabLayout.newTab().setText("原合同信息"));
//        if (TextUtils.equals(getIntent().getStringExtra("tag"), "cap")) {
            tabLayout.addTab(tabLayout.newTab().setText("对方修改"));
            tabLayout.addTab(tabLayout.newTab().setText("我方修改"));
//        } else {
//            tabLayout.addTab(tabLayout.newTab().setText("我的修改"));
//            tabLayout.addTab(tabLayout.newTab().setText("资方修改"));
//        }

        tabLayout.setTabMode(TabLayout.MODE_FIXED);
        fragment = new LookOldContractFgt();
        addFragment(fragment, "1");
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == 0) {
                    replace("1");
                } else if (tab.getPosition() == 1) {
                    replace("2");
//                    if (TextUtils.equals(getIntent().getStringExtra("tag"), "cap")) {
//
//                    }else {
//                        replace("3");
//                    }

                } else {
                    replace("3");
//                    if (TextUtils.equals(getIntent().getStringExtra("tag"), "cap")) {
//
//                    }else {
//                        replace("2");
//                    }
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
    protected int getLayoutResId() {
        return R.layout.aty_look_con_info;
    }

    @Override
    protected void initialized() {

    }

    @Override
    protected void requestData() {

    }


    /**
     * 添加Fragment
     *
     * @param fragment
     * @param tag
     */
    private void addFragment(Fragment fragment, String tag) {
        if (fragment.isAdded()) {//不是这么做的   这样做并没效果
        } else {
            android.support.v4.app.FragmentManager manager = getSupportFragmentManager();
            android.support.v4.app.FragmentTransaction transaction = manager.beginTransaction();
            transaction.add(R.id.look_con_info, fragment, tag);
            transaction.commit();
        }
    }

    // 删除fragment
    private void removeFragment(String tag) {
        android.support.v4.app.FragmentManager manager = getSupportFragmentManager();
        Fragment fragment = manager.findFragmentByTag(tag);
        android.support.v4.app.FragmentTransaction transaction = manager.beginTransaction();
        transaction.remove(fragment);
        transaction.commit();
    }

    /**
     * 替换
     *
     * @param fragNum
     */
    private void replace(String fragNum) {
        android.support.v4.app.FragmentManager manager = getSupportFragmentManager();
        if (fragNum.equals(String.valueOf(1))) {
            fragment = new LookOldContractFgt();
        } else if (fragNum.equals(String.valueOf(2))) {
            fragment = new LookWorkerEditFgt();
        } else {
            fragment = new LookMyEditFgt();
        }
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.look_con_info, fragment);
        transaction.commit();
    }


}
