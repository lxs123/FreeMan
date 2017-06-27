package com.toocms.freeman.ui.mine.baseinformation;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;

import com.toocms.freeman.R;
import com.toocms.freeman.ui.BaseAty;

//技能信息页
public class SkillInformationInBaseAty extends BaseAty {

    public boolean isComplete = false;//一开始是完成状态
    Fragment mFragment;
    private SkillInformationModify modify;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActionBar.setTitle("技能信息");
        addFragment(new SkillInfotmationComplete(), "0");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!TextUtils.equals(getIntent().getStringExtra("flag"), "member_detail") && !TextUtils.equals(getIntent().getStringExtra("flag"), "wzw1"))
            getMenuInflater().inflate(R.menu.menu_edit, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_edit:
                replace("1");
                item.setTitle("完成");
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    //碎片相关
    public void replace(String fragNum) {
        FragmentManager manager = getSupportFragmentManager();
        if (fragNum.equals(String.valueOf(0))) {     //0是完成页面
            mFragment = new SkillInfotmationComplete();
        } else {     //否则是修改页面
            mFragment = new SkillInformationModify();
        }

        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.base_infomation_ll, mFragment, fragNum);
        transaction.commit();
    }

    private void addFragment(Fragment fragment, String tag) {
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.add(R.id.base_infomation_ll, fragment, tag);
        transaction.commit();
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.aty_skill_information_in_base;
    }

    @Override
    protected void initialized() {

    }

    @Override
    protected void requestData() {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mFragment.onActivityResult(requestCode, resultCode, data);
    }
}
