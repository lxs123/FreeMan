package com.toocms.freeman.ui.searchjob;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.toocms.frame.image.ImageLoader;
import com.toocms.frame.ui.BaseFragment;
import com.toocms.freeman.R;
import com.toocms.freeman.config.JsonArryToList;
import com.toocms.freeman.https.Hire;
import com.toocms.freeman.ui.index.ModifyDetailsAty;
import com.toocms.freeman.ui.index.ModifyRecruitmentInformationAty;
import com.toocms.freeman.ui.view.ImagePagerActivity;
import com.toocms.freeman.ui.view.MyGridView;
import com.toocms.freeman.ui.view.MyImageDialog;
import com.zero.autolayout.utils.AutoUtils;

import org.xutils.http.RequestParams;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;
import java.util.Map;

import cn.zero.android.common.util.JSONUtils;
import cn.zero.android.common.util.ListUtils;
import cn.zero.android.common.view.FButton;

/**
 * 已抢招工单——详情——招工信息
 */
public class RecruitmentInformationFgt extends BaseFragment {

    @ViewInject(R.id.accept_count_tv)
    private TextView acceptCountTv;     //已接单人数
    @ViewInject(R.id.hire_noid_tv)
    private TextView hireNoidTv;        //招工单号
    @ViewInject(R.id.skill_tv)
    private TextView skillTv;       //工作
    @ViewInject(R.id.staff_tv)
    private TextView staffTv;       //招人数量
    @ViewInject(R.id.contract_starttime_tv)
    private TextView contractStarttimeTv;       //合同开始日期
    @ViewInject(R.id.contract_endtime_tv)
    private TextView contractEndtimeTv;     //合同截至日期
    @ViewInject(R.id.work_starttime_tv)
    private TextView workStarttimeTv;       //每天工作开始日期
    @ViewInject(R.id.work_endtime_tv)
    private TextView workEndtimeTv;     //每天工作结束时间
    @ViewInject(R.id.work_week_tv)
    private TextView workWeekTv;        //工作时间周历
    @ViewInject(R.id.hire_endtime_tv)
    private TextView hireEndTimeTv;     //招工截至日期
    @ViewInject(R.id.subtotal_tv)
    private TextView subtotleTv;        //单价
    @ViewInject(R.id.amount_tv)
    private TextView amountTv;      //金额
    @ViewInject(R.id.settle_type_tv)
    private TextView settleTypeTv;      //结算方式
    @ViewInject(R.id.ress_tv)
    private TextView ressTv;        //工作地点
    @ViewInject(R.id.is_insurance_tv)
    private TextView isInsuranceTv;
    @ViewInject(R.id.is_dine_tv)
    private TextView isDineTv;
    @ViewInject(R.id.is_lodging_tv)
    private TextView isLodgingTv;
    @ViewInject(R.id.is_tool_tv)
    private TextView isToolTv;
    @ViewInject(R.id.is_transportation_expenses_tv)
    private TextView isTransportationExpensesTv;
    @ViewInject(R.id.is_correspondence_tv)
    private TextView isCorrespondenceTv;
    @ViewInject(R.id.audit_tv)
    private TextView auditTv;       //工作完成标准
    @ViewInject(R.id.others_text_tv)
    private TextView othersTextTv;      //备注

    @ViewInject(R.id.new_job_order_imgs)
    private MyGridView imgs;

    //需要初始化的下面的3个按钮
    @ViewInject(R.id.btn1)
    private Button btn1;
    @ViewInject(R.id.btn2)
    private Button btn2;
    @ViewInject(R.id.btn3)
    private FButton btn3;
    @ViewInject(R.id.jo_detail_imgs_text)
    private TextView tvImgsDone;
    private String mTag;
    private ImgGridAdapter mImageGridAdapter;

    private Hire mHire;
    public String labNoidStr;       //用户编号
    public String hireNoidStr;      //招工单编号
    public String capNoidStr;   //资方noid
    public String status;   //签约状态(仅在搜索--》已抢招工单中使用)
    public String coor_diff;    //招工单修改过没有(仅在搜索--》已抢招工单中使用)
    private String mPhotos;
    public String mIsCollectStr;   //收藏状态（接口获取）

    //用来判断按钮个数的字段
    public String coor_status;  //0为未操作，1同意招工单  2修改过招工单等待回复  3资方针对修改做出回复
    private Map<String, String> mDataMap;


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (getActivity() instanceof RecruitmentOrderAt) {
            labNoidStr = ((RecruitmentOrderAt) getActivity()).labNoidStr;
            hireNoidStr = ((RecruitmentOrderAt) getActivity()).hireNoidStr;
            capNoidStr = ((RecruitmentOrderAt) getActivity()).noid;
//            status = ((RecruitmentOrderAt) getActivity()).status;
//            coor_status = ((RecruitmentOrderAt) getActivity()).coor_status;

        } else if (getActivity() instanceof MyJobOrderDetailAty) {
            labNoidStr = ((MyJobOrderDetailAty) getActivity()).labNoidStr;  //劳方编号
            hireNoidStr = ((MyJobOrderDetailAty) getActivity()).hireNoidStr;    //招工单noid
            capNoidStr = ((MyJobOrderDetailAty) getActivity()).noid;      //对方编号
            status = ((MyJobOrderDetailAty) getActivity()).status;
            coor_diff = ((MyJobOrderDetailAty) getActivity()).coor_status;
            coor_status = ((MyJobOrderDetailAty) getActivity()).coor_status;
        } else if (getActivity() instanceof AlreadyRobJobOrderDetailAty) {
            labNoidStr = ((AlreadyRobJobOrderDetailAty) getActivity()).labNoidStr;
            hireNoidStr = ((AlreadyRobJobOrderDetailAty) getActivity()).hireNoidStr;
            capNoidStr = ((AlreadyRobJobOrderDetailAty) getActivity()).noid;
            status = ((AlreadyRobJobOrderDetailAty) getActivity()).status;
            coor_diff = ((AlreadyRobJobOrderDetailAty) getActivity()).coor_diff;
            coor_status = ((AlreadyRobJobOrderDetailAty) getActivity()).coor_status;
        }

//        Log.e("***", "coor_status:" + coor_status);

    }

    @Override
    public void onResume() {
        super.onResume();
        mTag = getTag();
//        initTreeBtn(mTag);
        showProgressDialog();
        mHire.detail(labNoidStr, hireNoidStr, this);
    }

    //根据跳转tag,初始化下面的三个按钮
    private void initTreeBtn(String tag) {
        btn3.setVisibility(View.VISIBLE);

        if (status.equals("2")) {     //招工中
            if (coor_status.equals("1")) {    //同意招工单去修改
                btn3.setVisibility(View.VISIBLE);
                btn2.setVisibility(View.GONE);
                btn1.setVisibility(View.GONE);
                btn3.setText("取消接单");
                btn3.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        showDialog("", "确认取消改招工单么？", "确定", "取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //取消接单接口
                                showProgressDialog();
                                mHire.cancelAccept(hireNoidStr, application.getUserInfo().get("noid"), RecruitmentInformationFgt.this);
                            }
                        }, null);

                    }
                });
            } else if (coor_status.equals("2")) {  //修改过招工单等待回复
                btn3.setVisibility(View.VISIBLE);
                btn2.setVisibility(View.VISIBLE);
                btn1.setVisibility(View.VISIBLE);
                btn1.setText("查看修改明细");
                btn2.setText("取消接单");
                btn3.setText("同意签约");
                btn3.setVisibility(View.GONE);
                btn1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Bundle bundle = new Bundle();
                        bundle.putString("flag", "wzw1");
                        bundle.putString("lab_noid", application.getUserInfo().get("noid"));  //劳方noid
                        bundle.putString("hire_noid", hireNoidStr); //招工单noid
                        bundle.putString("noid", capNoidStr);  //对方编号
                        startActivity(ModifyDetailsAty.class, bundle);
                    }
                });
                btn2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        showDialog("", "确定取消该招工单么？", "确定", "取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                showProgressDialog();
                                mHire.cancelAccept(hireNoidStr, application.getUserInfo().get("noid"), RecruitmentInformationFgt.this);
                            }
                        }, null);

                    }
                });
                btn3.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        showDialog("", "确定要接该招工单么？", "确定", "取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                showProgressDialog();
                                mHire.labReplyAccept(application.getUserInfo().get("noid"), hireNoidStr, capNoidStr, RecruitmentInformationFgt.this);
                            }
                        }, null);
                    }
                });
            } else if (coor_status.equals("0")) {  //未操作
                btn1.setVisibility(View.GONE);
                btn3.setText("接单");
                btn2.setText("修改");
                btn3.setVisibility(View.VISIBLE);
                btn2.setVisibility(View.VISIBLE);
                btn3.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showDialog("", "确定要接该招工单么？", "确定", "取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                showProgressDialog();
                                mHire.labReplyAccept(application.getUserInfo().get("noid"), hireNoidStr, capNoidStr, RecruitmentInformationFgt.this);
                            }
                        }, null);

                    }
                });
                btn2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Bundle bundle = new Bundle();
                        bundle.putString("flag", "wzw1");
                        bundle.putString("lab_noid", application.getUserInfo().get("noid"));  //劳方noid
                        bundle.putString("hire_noid", hireNoidStr); //招工单noid
                        bundle.putString("noid", capNoidStr);  //对方编号
                        startActivity(ModifyRecruitmentInformationAty.class, bundle);
                    }
                });
            }
        } else if (status.equals("3")) {   //已签约
            btn1.setVisibility(View.GONE);
            btn2.setVisibility(View.GONE);
            btn3.setVisibility(View.GONE);
        }


//        Log.e("***", "哈哈哈" + tag);
//
//        if (tag.equals("0")) {
//            //是从 已抢招工单详情 跳转过来的  3个按钮
//            btn1.setVisibility(View.VISIBLE);
//            btn1.setText("查看修改明细");
//            btn2.setText("取消接单");
//            btn3.setText("同意签约");
//
//            //查看修改明细
//            btn1.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    startActivity(ModifyDetailsAty.class, null);
//                }
//            });
//        } else if (tag.equals("2")) {
//            //是从 招工单详情 跳转过来的  2个按钮
//            btn1.setVisibility(View.GONE);
//            btn2.setText("修改");
//            btn3.setText("接单");
//            btn2.setOnClickListener(new View.OnClickListener() {    //修改
//                @Override
//                public void onClick(View view) {
//                    Bundle bundle = new Bundle();
//                    bundle.putString("flag", "wzw1");    //搜工作--》查询招工信息--》确认--》在item上点修改
//                    bundle.putString("hire_noid", hireNoidStr);
//                    bundle.putString("lab_noid", labNoidStr);
//                    startActivity(ModifyRecruitmentInformationAty.class, bundle);
//                }
//            });
//            btn3.setOnClickListener(new View.OnClickListener() {    //接单
//                @Override
//                public void onClick(View view) {
//                    showProgressDialog();
//                    mHire.labReplyAccept(application.getUserInfo().get("noid"), hireNoidStr, capNoidStr, RecruitmentInformationFgt.this);
//                }
//            });
//        } else if (tag.equals("4")) {
//            //是从 我的招工单详情 跳转过来的  2个按钮
//            btn1.setVisibility(View.GONE);
//            btn2.setText("修改");
//            btn3.setText("接单");
//            btn2.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    Bundle bundle = new Bundle();
//                    bundle.putString("flag", "wzw1");    //搜工作--》我的招工单--》在item上点击修改
//                    bundle.putString("hire_noid", hireNoidStr);
//                    bundle.putString("lab_noid", labNoidStr);
//                    startActivity(ModifyRecruitmentInformationAty.class, bundle);
//                }
//            });
//            btn3.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    showProgressDialog();
//                    mHire.labReplyAccept(application.getUserInfo().get("noid"), hireNoidStr, capNoidStr, RecruitmentInformationFgt.this);
//                }
//            });
//        }
    }

    @Override
    public void onComplete(RequestParams params, String result) {
        if (params.getUri().contains("Hire/detail")) {
            Log.e("***", result);
            mDataMap = JSONUtils.parseDataToMap(result);
            String hire = mDataMap.get("hire");
            Map<String, String> hireMap = JSONUtils.parseKeyAndValueToMap(hire);
            acceptCountTv.setText(mDataMap.get("accept_count") + "人次");
            hireNoidTv.setText("招工单号：" + mDataMap.get("hire_noid"));
            ArrayList<Map<String, String>> skillList = JSONUtils.parseKeyAndValueToMapList(hireMap.get("skill_list"));
            ArrayList<String> arrayList = new ArrayList<>();
            for (int i = 0; i < ListUtils.getSize(skillList); i++) {
                arrayList.add(skillList.get(i).get("name"));
            }
            skillTv.setText(ListUtils.join(arrayList));
            staffTv.setText(mDataMap.get("staff"));
            contractStarttimeTv.setText(hireMap.get("contract_starttime"));
            contractEndtimeTv.setText(hireMap.get("contract_endtime"));
            workStarttimeTv.setText(hireMap.get("work_starttime"));
            workEndtimeTv.setText(hireMap.get("work_endtime"));
            workWeekTv.setText(ListUtils.join(JsonArryToList.strList(hireMap.get("work_week_name"))));
            hireEndTimeTv.setText(hireMap.get("hire_endtime"));
            if (!TextUtils.isEmpty(hireMap.get("unit_name"))) {
                if (hireMap.get("unit_name").contains("每")) {
                    subtotleTv.setText("￥" + hireMap.get("subtotal") + hireMap.get("unit_name").replace("每", "/"));
                } else {
                    subtotleTv.setText("￥" + hireMap.get("subtotal") + "/" + hireMap.get("unit_name"));
                }
            }

            amountTv.setText("￥" + hireMap.get("amount"));
            settleTypeTv.setText(hireMap.get("settle_type_name"));
            ressTv.setText(mDataMap.get("ress"));
            isInsuranceTv.setText(hireMap.get("is_insurance").equals("0") ? "否" : "是");
            isDineTv.setText(hireMap.get("is_dine").equals("0") ? "否" : "是");
            isLodgingTv.setText(hireMap.get("is_lodging").equals("0") ? "否" : "是");
            isToolTv.setText(hireMap.get("is_tool").equals("0") ? "否" : "是");
            isTransportationExpensesTv.setText(hireMap.get("is_transportation_expenses").equals("0") ? "否" : "是");
            isCorrespondenceTv.setText(hireMap.get("is_correspondence").equals("0") ? "否" : "是");
            auditTv.setText(hireMap.get("audit"));
            othersTextTv.setText(hireMap.get("others_text"));
            mPhotos = mDataMap.get("photos");
            if (ListUtils.isEmpty(JsonArryToList.strList(mPhotos))) {
                tvImgsDone.setVisibility(View.VISIBLE);
            } else {
                tvImgsDone.setVisibility(View.GONE);
            }
            mIsCollectStr = mDataMap.get("is_collect");
            if (getActivity() instanceof RecruitmentOrderAt) {
                ((RecruitmentOrderAt) getActivity()).isCollect = mIsCollectStr.equals("1") ? true : false;
                if (!((RecruitmentOrderAt) getActivity()).isCollect) {
                    ((RecruitmentOrderAt) getActivity()).mItem.setTitle("收藏");
                } else {
                    ((RecruitmentOrderAt) getActivity()).mItem.setTitle("取消收藏");
                }
            } else if (getActivity() instanceof MyJobOrderDetailAty) {
                ((MyJobOrderDetailAty) getActivity()).isCollect = mIsCollectStr.equals("1") ? true : false;
                if (!((MyJobOrderDetailAty) getActivity()).isCollect) {
                    ((MyJobOrderDetailAty) getActivity()).mItem.setTitle("收藏");
                } else {
                    ((MyJobOrderDetailAty) getActivity()).mItem.setTitle("取消收藏");
                }
            } else if (getActivity() instanceof AlreadyRobJobOrderDetailAty) {
                ((AlreadyRobJobOrderDetailAty) getActivity()).isCollect = mIsCollectStr.equals("1") ? true : false;
                if (!((AlreadyRobJobOrderDetailAty) getActivity()).isCollect) {
                    ((AlreadyRobJobOrderDetailAty) getActivity()).mItem1.setTitle("收藏");
                } else {
                    ((AlreadyRobJobOrderDetailAty) getActivity()).mItem1.setTitle("取消收藏");
                }
            }
//            Log.e("***",mIsCollectStr);
            coor_diff = hireMap.get("coor_diff");
            coor_status = hireMap.get("coor_status");
            status = hireMap.get("status");
            initTreeBtn(mTag);
        }
        if (params.getUri().contains("Hire/labReplyAccept")) {
            showToast(JSONUtils.parseKeyAndValueToMap(result).get("message"));
            onResume();
        }

        if (params.getUri().contains("Hire/cancelAccept")) {
            showToast(JSONUtils.parseKeyAndValueToMap(result).get("message"));
            onResume();
        }

        if (mImageGridAdapter == null) {
            mImageGridAdapter = new ImgGridAdapter();
            imgs.setAdapter(mImageGridAdapter);
        } else {
            mImageGridAdapter.notifyDataSetChanged();
        }
        super.onComplete(params, result);
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.fgt_recruitment_information;
    }

    @Override
    protected void initialized() {
        mHire = new Hire();
    }

    @Override
    protected void requestData() {

    }

    @Event({R.id.ress_tv, R.id.recruit_info_week_click, R.id.recruit_info_skill_click})
    private void onClick(View view) {
        switch (view.getId()) {
            case R.id.ress_tv:

                Log.e("***", "lat:" + mDataMap.get("latitude") + "/" + " lon:" + mDataMap.get("longitude"));

                Bundle bundle = new Bundle();
                bundle.putString("lat", mDataMap.get("latitude"));
                bundle.putString("lon", mDataMap.get("longitude"));
                startActivity(AtyMap.class, bundle);
                break;
            case R.id.recruit_info_week_click:
                setDialogText(workWeekTv.getText().toString());
                break;
            case R.id.recruit_info_skill_click:
                setDialogText(skillTv.getText().toString());
                break;
        }
    }
    private void setDialogText(String str) {
        MyImageDialog myImageDialog = new MyImageDialog(getActivity(), R.style.Dialog_Fullscreen, 0, 0, false, "text", str);
        myImageDialog.show();
        WindowManager windowManager = getActivity().getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        WindowManager.LayoutParams lp = myImageDialog.getWindow().getAttributes();
        lp.width = (int) (display.getWidth()); //设置宽度
        lp.height = display.getHeight();
        myImageDialog.getWindow().setAttributes(lp);
    }

    private class ImgGridAdapter extends BaseAdapter {

        private ViewHodler viewHodler;
        ImageLoader mImageLoader = new ImageLoader();

        @Override
        public int getCount() {
            return ListUtils.getSize(JsonArryToList.strList(mPhotos));
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                viewHodler = new ViewHodler();
                convertView = LayoutInflater.from(getActivity()).inflate(R.layout.listitem_new_job_order, parent, false);
                x.view().inject(viewHodler, convertView);
                AutoUtils.autoSize(convertView);
                convertView.setTag(viewHodler);
            } else {
                viewHodler = (ViewHodler) convertView.getTag();
            }
            viewHodler.delImgv.setVisibility(View.GONE);
            mImageLoader.disPlay(viewHodler.imgv, JsonArryToList.strList(mPhotos).get(position));
            viewHodler.imgv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Bundle bundle = new Bundle();
                    bundle.putStringArrayList(ImagePagerActivity.EXTRA_IMAGE_URLS, (ArrayList) JsonArryToList.strList(mPhotos));
                    bundle.putInt(ImagePagerActivity.EXTRA_IMAGE_INDEX, position);
                    startActivity(ImagePagerActivity.class, bundle);
                    getActivity().overridePendingTransition(0, 0);
                }
            });
            return convertView;
        }

        public class ViewHodler {
            @ViewInject(R.id.list_new_job_imgs)
            ImageView imgv;
            @ViewInject(R.id.list_new_job_delete)
            ImageView delImgv;
        }
    }

}
