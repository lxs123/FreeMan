package com.toocms.freeman.ui.mine;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.toocms.frame.config.Config;
import com.toocms.frame.image.ImageLoader;
import com.toocms.freeman.R;
import com.toocms.freeman.https.User;
import com.toocms.freeman.ui.BaseAty;
import com.toocms.freeman.ui.infomationaty.NewBaseInfoAty;
import com.toocms.freeman.ui.lar.LoginAty;
import com.toocms.freeman.ui.mine.accountsecurity.AccountSecurityAty;
import com.toocms.freeman.ui.mine.baseinformation.SkillInformationInBaseAty;
import com.toocms.freeman.ui.mine.baseinformation.WorkAreaAty;
import com.toocms.freeman.ui.mine.settings.SettingsAty;
import com.toocms.freeman.ui.pay.wallet.MyWalletAty;
import com.toocms.freeman.ui.recruitment.RecruitAddressAty;

import org.xutils.http.RequestParams;
import org.xutils.image.ImageOptions;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import cn.zero.android.common.util.JSONUtils;
import cn.zero.android.common.view.shapeimageview.CircularImageView;

public class MineAty extends BaseAty {
    @ViewInject(R.id.mine_head)
    CircularImageView imgvHead;
    @ViewInject(R.id.mine_nickname)
    TextView tvNickname;
    /**
     * 技能信息认证状态[isAttestation]
     *
     * @param noid 用户编号
     */
    private String noid;
    private User user;
    @ViewInject(R.id.mine_info_state)
    private ImageView imgvInfo;
    @ViewInject(R.id.mine_skill_state)
    private ImageView imgvSkill;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActionBar.setTitle("我的");
        if (!Config.isLogin()) {
            startActivity(LoginAty.class, null);
            finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        noid = application.getUserInfo().get("noid");
        ImageLoader imageLoader = new ImageLoader();
        ImageOptions options = new ImageOptions.Builder().setLoadingDrawableId(R.drawable.icon_head)
                .setFailureDrawableId(R.drawable.icon_head).build();
        imageLoader.setImageOptions(options);
        imageLoader.disPlay(imgvHead, application.getUserInfo().get("head"));
        tvNickname.setText(application.getUserInfo().get("nickname"));
        user.isAttestation(noid, this);
        user.isPerfect(noid, this);
//        LogUtil.e(application.getUserInfo().get("name"));
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.aty_mine;
    }

    @Override
    protected void initialized() {
        user = new User();
    }

    @Override
    protected void requestData() {

    }

    @Override
    public void onComplete(RequestParams params, String result) {
        if (params.getUri().contains("User/isAttestation")) {
            String data = JSONUtils.parseKeyAndValueToMap(result).get("data");
            // 已认证为1  未认证为0  审核中为2
            if (TextUtils.equals(data, "0")) {

                imgvSkill.setImageResource(R.drawable.btn_unthrough);

            } else if (TextUtils.equals(data, "1")) {
                imgvSkill.setImageResource(R.drawable.btn_through);
            } else if (TextUtils.equals(data, "2")) {
                imgvSkill.setImageResource(R.drawable.btn_check);
            } else if (TextUtils.equals(data, "4")) {
                imgvSkill.setImageResource(R.drawable.btn_shenqing);
            }
        } else if (params.getUri().contains("User/isPerfect")) {
            String data = JSONUtils.parseKeyAndValueToMap(result).get("data");
            // 已认证为1  未认证为0  审核中为2
            if (TextUtils.equals(data, "0")) {
                imgvInfo.setImageResource(R.drawable.btn_unthrough);
            } else if (TextUtils.equals(data, "1")) {
                imgvInfo.setImageResource(R.drawable.btn_through);
            } else if (TextUtils.equals(data, "2")) {
                imgvInfo.setImageResource(R.drawable.btn_check);
            } else if (TextUtils.equals(data, "4")) {
                imgvInfo.setImageResource(R.drawable.btn_shenqing);
            }
        }
        super.onComplete(params, result);
    }

    @Event({R.id.base_infomation_ll, R.id.skill_information_ll, R.id.mine_wallet,
            R.id.mine_settings, R.id.mine_address, R.id.account_security_ll,
            R.id.mine_address_area, R.id.mine_collect, R.id.mine_extension,
            R.id.mine_evaluate})
    private void onClick(View view) {
        switch (view.getId()) {
            case R.id.base_infomation_ll:
                //基本信息
                startActivity(NewBaseInfoAty.class, null);
                break;
            //技能信息
            case R.id.skill_information_ll:
                startActivity(SkillInformationInBaseAty.class, null);
                break;
            case R.id.mine_wallet:
                startActivity(MyWalletAty.class, null);
                break;
            case R.id.mine_settings:
                startActivity(SettingsAty.class, null);
                break;
            case R.id.mine_address:
                startActivity(RecruitAddressAty.class, null);
                break;
//            账户安全
            case R.id.account_security_ll:
                startActivity(AccountSecurityAty.class, null);
                break;
            case R.id.mine_address_area:
                startActivity(WorkAreaAty.class, null);
                break;
            case R.id.mine_collect:
                startActivity(MyCollectAty.class, null);
                break;
            case R.id.mine_extension:
                startActivity(MyExtensionAty.class, null);
                break;
            case R.id.mine_evaluate:        //评价
                startActivity(MyEvaluateAty.class, null);
                break;
        }
    }
}
