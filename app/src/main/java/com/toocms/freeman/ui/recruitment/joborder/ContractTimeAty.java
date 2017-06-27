package com.toocms.freeman.ui.recruitment.joborder;

import android.os.Bundle;
import android.view.Menu;

import com.toocms.freeman.R;
import com.toocms.freeman.ui.BaseAty;

/**
 * Created by admin on 2017/3/24.
 */

public class ContractTimeAty extends BaseAty {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActionBar.setTitle("筛选");
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.aty_contract_time;
    }

    @Override
    protected void initialized() {

    }

    @Override
    protected void requestData() {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_sure, menu);
        return super.onCreateOptionsMenu(menu);
    }
}
