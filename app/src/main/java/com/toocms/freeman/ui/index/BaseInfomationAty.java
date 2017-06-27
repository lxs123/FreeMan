package com.toocms.freeman.ui.index;

import android.os.Bundle;

import com.toocms.freeman.R;
import com.toocms.freeman.ui.BaseAty;

/**
 * 基本信息页
 */
public class BaseInfomationAty extends BaseAty {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActionBar.setTitle("基本信息");
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.aty_base_infomation;
    }

    @Override
    protected void initialized() {

    }

    @Override
    protected void requestData() {

    }
}
