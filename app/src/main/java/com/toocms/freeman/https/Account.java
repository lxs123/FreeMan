package com.toocms.freeman.https;

import com.toocms.frame.web.ApiListener;
import com.toocms.frame.web.ApiTool;
import com.toocms.freeman.config.AppConfig;

import org.xutils.http.RequestParams;

import java.io.File;
import java.util.List;

import cn.zero.android.common.util.ListUtils;

/**
 * Created by admin on 2017/4/10.
 */

public class Account {
    String modle = getClass().getSimpleName();

    /**
     * 登录[login]
     *
     * @param username 登录名，暂时为只输入手机号
     * @param password 登录密码
     */
    public void login(String username, String password, ApiListener apiListener) {
        RequestParams params = new RequestParams(AppConfig.BASE_URL + modle + "/login");
        params.addBodyParameter("username", username);
        params.addBodyParameter("password", password);
        ApiTool apiTool = new ApiTool();
        apiTool.postApi(params, apiListener);
    }

    /**
     * 注册[register]         注册。只能在验证码检测时间内正常执行步骤
     *
     * @param mobile         手机号
     * @param password       密码
     * @param password_again 确认密码
     * @param code           邀请人编号
     * @param apiListener
     */
    public void register(String mobile, String password, String password_again, String code, ApiListener apiListener) {
        RequestParams params = new RequestParams(AppConfig.BASE_URL + modle + "/register");
        params.addBodyParameter("mobile", mobile);
        params.addBodyParameter("password", password);
        params.addBodyParameter("password_again", password_again);
        params.addBodyParameter("code", code);
        ApiTool apiTool = new ApiTool();
        apiTool.postApi(params, apiListener);
    }

    /**
     * 发送注册验证码[sendRegisterSMS]
     *
     * @param mobile 手机号
     */
    public void sendRegisterSMS(String mobile, ApiListener apiListener) {
        RequestParams params = new RequestParams(AppConfig.BASE_URL + modle + "/sendRegisterSMS");
        params.addBodyParameter("mobile", mobile);
        ApiTool apiTool = new ApiTool();
        apiTool.postApi(params, apiListener);
    }

    /**
     * 检测注册验证码[checkRegisterSMS]
     *
     * @param mobile 手机号
     * @param verify 验证码
     */
    public void checkRegisterSMS(String mobile, String verify, ApiListener apiListener) {
        RequestParams params = new RequestParams(AppConfig.BASE_URL + modle + "/checkRegisterSMS");
        params.addBodyParameter("mobile", mobile);
        params.addBodyParameter("verify", verify);
        ApiTool apiTool = new ApiTool();
        apiTool.postApi(params, apiListener);
    }

    /**
     * 检测邀请人[inviterCodeAccuracy]
     * 检查填写的邀请人号码是否存在此人（如果做输入邀请人即时检测使用）
     * 如果存在返回此人的信息
     *
     * @param code 邀请人号码
     */
    public void inviterCodeAccuracy(String code, ApiListener apiListener) {
        RequestParams params = new RequestParams(AppConfig.BASE_URL + modle + "/inviterCodeAccuracy");
        params.addBodyParameter("code", code);
        ApiTool apiTool = new ApiTool();
        apiTool.postApi(params, apiListener);
    }

    /**
     * 发送找回密码验证码[sendForgotSMS]
     *
     * @param mobile 手机号
     */
    public void sendForgotSMS(String mobile, ApiListener apiListener) {
        RequestParams params = new RequestParams(AppConfig.BASE_URL + modle + "/sendForgotSMS");
        params.addBodyParameter("mobile", mobile);
        ApiTool apiTool = new ApiTool();
        apiTool.postApi(params, apiListener);
    }

    /**
     * 检测找回密码验证码[checkForgotSMS]
     *
     * @param mobile 手机号
     * @param verify 验证码
     */
    public void checkForgotSMS(String mobile, String verify, ApiListener apiListener) {
        RequestParams params = new RequestParams(AppConfig.BASE_URL + modle + "/checkForgotSMS");
        params.addBodyParameter("mobile", mobile);
        params.addBodyParameter("verify", verify);
        ApiTool apiTool = new ApiTool();
        apiTool.postApi(params, apiListener);
    }

    /**
     * 找回密码[forgotPassword]
     *
     * @param mobile         手机号
     * @param password       新密码
     * @param password_again 确认密码
     */
    public void forgotPassword(String mobile, String password, String password_again, ApiListener apiListener) {
        RequestParams params = new RequestParams(AppConfig.BASE_URL + modle + "/forgotPassword");
        params.addBodyParameter("mobile", mobile);
        params.addBodyParameter("password", password);
        params.addBodyParameter("password_again", password_again);
        ApiTool apiTool = new ApiTool();
        apiTool.postApi(params, apiListener);
    }

    /**
     * 修改手机号1:发送验证码[sendEditAccountStepOneSMS]
     *
     * @param mobile 手机号
     * @param noid   用户编号
     */
    public void sendEditAccountStepOneSMS(String mobile, String noid, ApiListener apiListener) {
        RequestParams params = new RequestParams(AppConfig.BASE_URL + modle + "/sendEditAccountStepOneSMS");
        params.addBodyParameter("mobile", mobile);
        params.addBodyParameter("noid", noid);
        ApiTool apiTool = new ApiTool();
        apiTool.postApi(params, apiListener);
    }

    /**
     * 修改手机号1:检测验证码[checkEditAccountStepOneSMS]
     *
     * @param mobile 手机号
     * @param verify 验证码
     */
    public void checkEditAccountStepOneSMS(String mobile, String verify, ApiListener apiListener) {
        RequestParams params = new RequestParams(AppConfig.BASE_URL + modle + "/checkEditAccountStepOneSMS");
        params.addBodyParameter("mobile", mobile);
        params.addBodyParameter("verify", verify);
        ApiTool apiTool = new ApiTool();
        apiTool.postApi(params, apiListener);
    }

    /**
     * 修改手机号2:发送验证码[sendEditAccountStepTwoSMS]
     *
     * @param new_mobile 手机号
     * @param noid       用户编号
     * @param verify     旧手机号验证码
     */
    public void sendEditAccountStepTwoSMS(String new_mobile, String noid, String verify, ApiListener apiListener) {
        RequestParams params = new RequestParams(AppConfig.BASE_URL + modle + "/sendEditAccountStepTwoSMS");
        params.addBodyParameter("new_mobile", new_mobile);
        params.addBodyParameter("noid", noid);
        params.addBodyParameter("verify", verify);
        ApiTool apiTool = new ApiTool();
        apiTool.postApi(params, apiListener);
    }

    /**
     * 修改手机号[editAccount]
     *
     * @param noid       用户编号
     * @param mobile     旧手机号
     * @param verify     旧手机号验证码
     * @param new_mobile 新手机号
     * @param new_verify 新手机号验证码
     */
    public void editAccount(String noid, String mobile, String verify, String new_mobile,
                            String new_verify, ApiListener apiListener) {
        RequestParams params = new RequestParams(AppConfig.BASE_URL + modle + "/editAccount");
        params.addBodyParameter("noid", noid);
        params.addBodyParameter("mobile", mobile);
        params.addBodyParameter("verify", verify);
        params.addBodyParameter("new_mobile", new_mobile);
        params.addBodyParameter("new_verify", new_verify);
        ApiTool apiTool = new ApiTool();
        apiTool.postApi(params, apiListener);
    }


    /**
     * 设置安全密码:发送验证码[sendSetSafePasswordSMS]
     *
     * @param noid   用户编号
     * @param mobile 用户手机号
     */
    public void sendSetSafePasswordSMS(String noid, String mobile, ApiListener apiListener) {
        RequestParams params = new RequestParams(AppConfig.BASE_URL + modle + "/sendSetSafePasswordSMS");
        params.addBodyParameter("noid", noid);
        params.addBodyParameter("mobile", mobile);
        ApiTool apiTool = new ApiTool();
        apiTool.postApi(params, apiListener);
    }

    /**
     * 设置安全密码:检测验证码[checkSetSafePasswordSMS]
     *
     * @param mobile 手机号
     * @param verify 验证码
     */
    public void checkSetSafePasswordSMS(String mobile, String verify, ApiListener apiListener) {
        RequestParams params = new RequestParams(AppConfig.BASE_URL + modle + "/checkSetSafePasswordSMS");
        params.addBodyParameter("mobile", mobile);
        params.addBodyParameter("verify", verify);
        ApiTool apiTool = new ApiTool();
        apiTool.postApi(params, apiListener);
    }

    /**
     * 设置安全密码[setSafePassword]
     *
     * @param noid           用户编号
     * @param verify         验证码
     * @param password       设置密码
     * @param password_again 重复密码
     */
    public void setSafePassword(String noid, String verify, String password, String password_again, ApiListener apiListener) {
        RequestParams params = new RequestParams(AppConfig.BASE_URL + modle + "/setSafePassword");
        params.addBodyParameter("noid", noid);
        params.addBodyParameter("verify", verify);
        params.addBodyParameter("password", password);
        params.addBodyParameter("password_again", password_again);
        ApiTool apiTool = new ApiTool();
        apiTool.postApi(params, apiListener);
    }

    /**
     * 申请删除帐号[delete]
     *
     * @param noid               用户编号
     * @param valid              校验参数，用户信息中valid参数
     * @param protected_password 安全密码
     * @param byperson           手持身份证，单图
     * @param byfront            身份证正面，单图
     * @param byback             身份证背面，单图
     */
    public void delete(String noid, String valid, String protected_password, String byperson,
                       String byfront, String byback, ApiListener apiListener) {
        RequestParams params = new RequestParams(AppConfig.BASE_URL + modle + "/delete");
        params.addBodyParameter("noid", noid);
        params.addBodyParameter("valid", valid);
        params.addBodyParameter("protected_password", protected_password);
        params.addBodyParameter("byperson", new File(byperson));
        params.addBodyParameter("byfront", new File(byfront));
        params.addBodyParameter("byback", new File(byback));
        ApiTool apiTool = new ApiTool();
        apiTool.postApi(params, apiListener);
    }

    /**
     * 上传证件 - 身份证[uploadIdCardPhotos]
     *
     * @param noid
     * @param byperson 手持身份证照片,单图
     * @param byfront  正面照片,单图
     * @param byback   反面照片,单图
     * @param valid    校验参数，登录中返回
     */
    public void uploadIdCardPhotos(String noid, String byperson, String byfront, String byback, String valid, ApiListener apiListener) {
        RequestParams params = new RequestParams(AppConfig.BASE_URL + modle + "/uploadIdCardPhotos");
        params.addBodyParameter("noid", noid);
        params.addBodyParameter("byperson", new File(byperson));
        params.addBodyParameter("byfront", new File(byfront));
        params.addBodyParameter("byback", new File(byback));
        params.addBodyParameter("valid", valid);
        ApiTool apiTool = new ApiTool();
        apiTool.postApi(params, apiListener);
    }

    /**
     * 上传证件 - 营业执照[uploadBusinessPhotos]
     *
     * @param noid   用户编号
     * @param papers 营业执照单图
     * @param valid  加密保护参数
     */
    public void uploadBusinessPhotos(String noid, String papers, String valid, ApiListener apiListener) {
        RequestParams params = new RequestParams(AppConfig.BASE_URL + modle + "/uploadBusinessPhotos");
        params.addBodyParameter("noid", noid);
        params.addBodyParameter("papers", new File(papers));
        params.addBodyParameter("valid", valid);
        ApiTool apiTool = new ApiTool();
        apiTool.postApi(params, apiListener);
    }

    /**
     * 上传证件 - 其他证件[uploadOthersPhotos]
     *
     * @param noid
     * @param papers
     * @param valid
     */
    public void uploadOthersPhotos(String noid, List<String> papers, String valid, ApiListener apiListener) {
        RequestParams params = new RequestParams(AppConfig.BASE_URL + modle + "/uploadOthersPhotos");
        params.addBodyParameter("noid", noid);
        for (int i = 0; i < ListUtils.getSize(papers); i++) {
            params.addBodyParameter("papers[" + i + "]", new File(papers.get(i)));
        }
        params.addBodyParameter("valid", valid);
        ApiTool apiTool = new ApiTool();
        apiTool.postApi(params, apiListener);
    }
}
