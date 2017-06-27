package com.toocms.freeman.ui.mine.baseinformation;

import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.toocms.frame.image.ImageLoader;
import com.toocms.frame.ui.BaseFragment;
import com.toocms.freeman.R;
import com.toocms.freeman.https.User;
import com.zero.autolayout.utils.AutoUtils;

import org.xutils.http.RequestParams;
import org.xutils.image.ImageOptions;
import org.xutils.view.annotation.ViewInject;

import java.util.Map;

import cn.zero.android.common.util.JSONUtils;
import cn.zero.android.common.view.shapeimageview.CircularImageView;

import static com.toocms.freeman.R.id.head;

public class BaseInformationCompleteFgt extends BaseFragment {

    @ViewInject(head)
    private CircularImageView headCir;//头像
    @ViewInject(R.id.noid)
    private TextView noidTv;//会员号
    @ViewInject(R.id.nickname)
    private TextView nicknameTv;//昵称
    @ViewInject(R.id.invite)
    private TextView inviteTv;//邀请人编号
    @ViewInject(R.id.role_name)
    private TextView roleNameTv;//自由人/单位
    @ViewInject(R.id.name)
    private TextView nameTv;//真实姓名
    @ViewInject(R.id.telephone)
    private TextView telephonetv;//联系电话
    @ViewInject(R.id.province_name)
    private TextView provinceNameTv;//常驻地址
    @ViewInject(R.id.sex_name)
    private TextView sexnameTv;//性别
    @ViewInject(R.id.bmi)
    private TextView bmiTv;//身高体重
    @ViewInject(R.id.organization)
    private TextView organizationTv;//单位名称
    @ViewInject(R.id.ident_status)
    private TextView identStatusTv;//身份证认证
    @ViewInject(R.id.business_status)
    private TextView businessStatusTv;//营业执照照片
    @ViewInject(R.id.others_status)
    private TextView othersStatusTv;//其他证件

    //图片处理
    ImageLoader mImageLoader;
    //网络部分
    private User mUser;
    //接口数据
    private Map<String, String> mDataMap;

    @Override
    public void onResume() {
        super.onResume();
        Log.e("111",isAdded()+"333");
        showProgressDialog();
        mUser.getPerfect(application.getUserInfo().get("noid"), this);//测试
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.aty_base_info_complete;
    }

    @Override
    public void onComplete(RequestParams params, String result) {
        if (params.getUri().contains("User/getPerfect")) {
            mDataMap = JSONUtils.parseDataToMap(result);
        }
        updateUI();
        super.onComplete(params, result);
    }

    //根据接口数据更新UI
    private void updateUI() {
        //Log.e("***", mDataMap.get("head")+"测试图片url");
        mImageLoader.disPlay(headCir, mDataMap.get("head"));
        noidTv.setText(mDataMap.get("noid"));
        nicknameTv.setText(mDataMap.get("nickname"));
        inviteTv.setText(mDataMap.get("invite"));
        roleNameTv.setText(mDataMap.get("role_name"));
        if (mDataMap.get("name_show").equals("1")) {
            nameTv.setText(mDataMap.get("name"));
        } else {
            nameTv.setText("***");
        }
        telephonetv.setText(mDataMap.get("telephone"));
        provinceNameTv.setText(mDataMap.get("province_name"));
        sexnameTv.setText(mDataMap.get("sex_name"));
        bmiTv.setText(mDataMap.get("bmi"));
        organizationTv.setText(mDataMap.get("organization"));

        // TODO: 2017/4/24  对四种状态做处理
        switch (JSONUtils.parseKeyAndValueToMap(mDataMap.get("ident")).get("status")) {
            case "0":
                identStatusTv.setText("未上传");
                break;
            case "1":
                identStatusTv.setText("待认证");
                break;
            case "2":
                identStatusTv.setText("已认证");
                break;
            case "3":
                identStatusTv.setText("认证失败");
                break;
            case "4":
                identStatusTv.setText("删除");
                break;
        }

        switch (JSONUtils.parseKeyAndValueToMap(mDataMap.get("business")).get("status")) {
            case "0":
                businessStatusTv.setText("未上传");
                break;
            case "1":
                businessStatusTv.setText("待认证");
                break;
            case "2":
                businessStatusTv.setText("已认证");
                break;
            case "3":
                businessStatusTv.setText("认证失败");
                break;
            case "4":
                businessStatusTv.setText("删除");
                break;
        }

        switch (JSONUtils.parseKeyAndValueToMap(mDataMap.get("others")).get("status")) {
            case "0":
                othersStatusTv.setText("未上传");
                break;
            case "1":
                othersStatusTv.setText("待认证");
                break;
            case "2":
                othersStatusTv.setText("已认证");
                break;
            case "3":
                othersStatusTv.setText("认证失败");
                break;
            case "4":
                othersStatusTv.setText("删除");
                break;
        }

    }

    @Override
    protected void initialized() {
        mUser = new User();

        mImageLoader = new ImageLoader();
        ImageOptions options = new ImageOptions.Builder()
                // setSize方法中的参数改成和item布局中图片大小一样
                .setSize(AutoUtils.getPercentWidthSize(172), AutoUtils.getPercentWidthSize(172))
                // 加载图片和加载失败图片方法中默认大小图片修改同上（差不多的也可以）
                .setLoadingDrawableId(R.drawable.icon_head)
                .setFailureDrawableId(R.drawable.icon_head)
                .setFadeIn(true).setImageScaleType(ImageView.ScaleType.CENTER_CROP)
                .setUseMemCache(true).build();
        mImageLoader.setImageOptions(options);
    }

    @Override
    protected void requestData() {

    }
}
