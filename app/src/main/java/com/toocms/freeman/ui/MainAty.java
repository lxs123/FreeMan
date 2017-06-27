package com.toocms.freeman.ui;

import android.os.Bundle;
import android.view.View;

import com.toocms.freeman.R;
import com.toocms.freeman.ui.index.BaseInfomationAty;
import com.toocms.freeman.ui.index.EvaluateInformationAty;
import com.toocms.freeman.ui.index.LabourDetailAty;
import com.toocms.freeman.ui.index.LabourListAty;
import com.toocms.freeman.ui.index.ModifyDetailsAty;
import com.toocms.freeman.ui.index.ModifyRecruitmentInformationAty;
import com.toocms.freeman.ui.index.SelectedRangeAty;
import com.toocms.freeman.ui.index.SkillInformationAty;
import com.toocms.freeman.ui.index.ViewFeedbackAty;
import com.toocms.freeman.ui.infomationaty.NewBaseInfoAty;
import com.toocms.freeman.ui.searchjob.ProvinceAty;

import org.xutils.view.annotation.Event;

public class MainAty extends BaseAty {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActionBar.hide();

//        findViewById(R.id.btn1).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivity(CollectAty.class,null);
//            }
//        });

    }

    @Override
    protected int getLayoutResId() {
        return R.layout.aty_main;
    }

    @Override
    protected void initialized() {

    }

    @Override
    protected void requestData() {

    }

    @Event({R.id.btn1,R.id.btn2,R.id.btn3,R.id.btn4,R.id.btn5,R.id.btn6,R.id.btn7,R.id.btn8,R.id.btn9,R.id.btn10,R.id.btn11,R.id.btn12})
    private void startAty(View view) {
        switch (view.getId()) {
            case R.id.btn1:

//                startActivity(BaiduMapAty.class, null);
startActivity(NewBaseInfoAty.class,null);

                //startActivity(BaiduMapAty.class, null);

                break;
            case R.id.btn2:
//                startActivity(AtySkillTest.class,null);
                break;
            case R.id.btn3:
                startActivity(SelectedRangeAty.class,null);
                break;
            case R.id.btn4:
                startActivity(ModifyDetailsAty.class,null);
                break;
            case R.id.btn5:
                startActivity(LabourDetailAty.class,null);
                break;
            case R.id.btn6:
                startActivity(EvaluateInformationAty.class,null);
                break;
            case R.id.btn7:
                startActivity(BaseInfomationAty.class,null);
                break;
            case R.id.btn8:
                startActivity(ModifyRecruitmentInformationAty.class,null);
                break;
            case R.id.btn9:
                startActivity(SkillInformationAty.class,null);
                break;
            case R.id.btn10:
                startActivity(LabourListAty.class,null);
                break;
            case R.id.btn11:
                startActivity(ViewFeedbackAty.class,null);
                break;
            case R.id.btn12:
                startActivity(ProvinceAty.class,null);
                break;
        }

    }

}
