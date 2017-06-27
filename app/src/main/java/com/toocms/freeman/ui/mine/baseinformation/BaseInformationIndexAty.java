package com.toocms.freeman.ui.mine.baseinformation;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.toocms.freeman.R;
import com.toocms.freeman.ui.BaseAty;
import com.toocms.freeman.ui.index.IndexAty;

import java.util.ArrayList;

import cn.zero.android.common.util.ListUtils;

/**
 * 基本信息
 */
public class BaseInformationIndexAty extends BaseAty {

    Fragment mFragment;
    public boolean isComplete = false;
    private String flag;
    BaseInformationModifyFgt mModifyFgt;
    public boolean isSaveComplete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mActionBar.setTitle("基本信息");
        flag = getIntent().getStringExtra("flag");
        if (TextUtils.equals(flag, "info")) {
            addFragment(new BaseInformationModifyFgt(), "1");
            isComplete = false;
        } else {
            addFragment(new BaseInformationCompleteFgt(), "0");
            isComplete = true;
        }

    }

    //右边toolbar小字相关
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (TextUtils.equals(flag, "info")) {
            getMenuInflater().inflate(R.menu.menu_complete, menu);
        } else {
            getMenuInflater().inflate(R.menu.menu_edit, menu);
        }

        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        if (item.getItemId() == R.id.menu_complete &&
                flag != null) {
            startActivity(IndexAty.class, null);
        }
        switch (item.getItemId()) {
            case R.id.menu_edit:
                if (isComplete) {//如果现在是完成状态
                    replace("1");
                    isComplete = false;
                    item.setTitle("完成");
                } else {
                    //每次 修改状态--》完成状态 时，调一下碎片里保存信息的接口
                    mModifyFgt = (BaseInformationModifyFgt) getSupportFragmentManager().findFragmentById(R.id.base_infomation_ll);
                    mModifyFgt.doModifyInterface();

                    new TextView(this).postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            replace("0");
                            isComplete = true;
                            item.setTitle("修改");
                        }
                    },2800);
                }
                break;
//            case R.id.menu_complete:
//                startActivity(IndexAty.class, null);
//                break;
            case 16908332:
                if (TextUtils.equals(getIntent().getStringExtra("flag"), "info")) {
                    startActivity(IndexAty.class, null);
                } else {
                    this.finish();
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    //碎片相关
    public void replace(String fragNum) {
        //===============伟：个人信息需要=====================
//        if (mFragment != null){
//            mFragment.onDestroy();
//        }
        //==================================================
        FragmentManager manager = getSupportFragmentManager();
        if (fragNum.equals(String.valueOf(0))) {
            mFragment = new BaseInformationCompleteFgt();
        } else {
            mFragment = new BaseInformationModifyFgt();
        }
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.base_infomation_ll, mFragment, fragNum);
        transaction.commit();
    }

    private void addFragment(Fragment fragment, String tag) {
        if (fragment.isAdded()) {
        } else {
            FragmentManager manager = getSupportFragmentManager();
            FragmentTransaction transaction = manager.beginTransaction();
            transaction.add(R.id.base_infomation_ll, fragment, tag);
            transaction.commit();
        }
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_base_information_index_aty;
    }

    @Override
    protected void initialized() {

    }

    @Override
    protected void requestData() {

    }

    //    监听返回键
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            if (TextUtils.equals(getIntent().getStringExtra("flag"), "info")) {
                startActivity(IndexAty.class, null);
                return true;
            } else {
                finish();
                return false;
            }

        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onBackPressed() {

        if (TextUtils.equals(getIntent().getStringExtra("flag"), "info")) {
            startActivity(IndexAty.class, null);
        } else {
        }
        super.onBackPressed();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e("***", "看看返回结果：" + requestCode);
        if (resultCode != RESULT_OK) {
            return;
        }
        switch (requestCode) {
            case com.toocms.frame.config.Constants.SELECT_IMAGE:
                ArrayList<String> list = getSelectImagePath(data);
                if (!ListUtils.isEmpty(list)) {
                    //把头像存进接口
                    application.setUserInfoItem("head", list.get(0));
                    Log.e("***", "存的地址：" + list.get(0));
                }
                break;
        }
    }
}
