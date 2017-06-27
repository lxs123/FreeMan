package com.toocms.freeman.ui.searchjob;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.toocms.freeman.R;
import com.toocms.freeman.ui.BaseAty;
import com.toocms.freeman.ui.view.MyGridView;
import com.zero.autolayout.utils.AutoUtils;

import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

import cn.zero.android.common.util.ListUtils;

public class WorkweekAty extends BaseAty {

    @ViewInject(R.id.my_grid_view)
    MyGridView myGridView;
    //上个页面传来的信息
    private String[] strings;
    private String[] idString;
    //要给上个页面接口传递的数据
    private String intWeekId;
    private String weekname;
    private List<String> weekIdList = new ArrayList<>();
    private List<String> weekNameList = new ArrayList<>();
    private int mWidth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActionBar.setTitle("工作周历");

        Display defaultDisplay = getWindowManager().getDefaultDisplay();
        mWidth = defaultDisplay.getWidth();

        strings = getIntent().getStringArrayExtra("weekarray");
        idString = getIntent().getStringArrayExtra("weekidarray");
        myGridView.setAdapter(new WeekAdapter());
    }

    @Event({R.id.week_fbtn})
    private void onClick(View view) {
        switch (view.getId()) {
            case R.id.week_fbtn:
                //把所存周历集合内容转化成数组
                if (ListUtils.getSize(weekIdList) != 0) {
                    intWeekId = new String();
                    weekname = new String();
                    intWeekId = ListUtils.join(weekIdList);
                    weekname = ListUtils.join(weekNameList);
                } else {
                    intWeekId = new String();
                    weekname = new String();
                }

                Intent intent = new Intent();
                intent.putExtra("intWeekId", intWeekId);
                intent.putExtra("weekname", weekname);
                Log.e("***", intWeekId + "/" + weekname);
                setResult(2048, intent);
                finish();
                break;
        }
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.aty_workweek;
    }

    @Override
    protected void initialized() {

    }

    @Override
    protected void requestData() {

    }

    public class WeekAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return strings.length;
        }

        @Override
        public String getItem(int i) {
            return strings[i];
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(final int i, View view, ViewGroup viewGroup) {

            ViewHolder viewHolder;
            if (view == null) {
                view = LayoutInflater.from(WorkweekAty.this).inflate(R.layout.listitem_njo_week, viewGroup, false);
                viewHolder = new ViewHolder(view);
                view.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) view.getTag();
            }

            viewHolder.listNojWeekCb.setVisibility(View.VISIBLE);
            viewHolder.listNojWeekTv.setVisibility(View.GONE);
            viewHolder.listNojWeekCb.setLayoutParams(new LinearLayout.LayoutParams((mWidth - 40) / 4, ViewGroup.LayoutParams.WRAP_CONTENT));

            viewHolder.listNojWeekCb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if (b) {
                        weekIdList.add(idString[i]);
                        weekNameList.add(strings[i]);
                    } else {
                        for (int j = 0; j < ListUtils.getSize(weekIdList); j++) {
                            if (idString[i].equals(weekIdList.get(j))) {
                                weekIdList.remove(j);
                                weekNameList.remove(j);
                            }
                        }
                    }
                }
            });

            viewHolder.listNojWeekCb.setText(getItem(i));
            return view;
        }

        private class ViewHolder {
            @ViewInject(R.id.list_njo_week1)
            CheckBox listNojWeekCb;
            @ViewInject(R.id.list_njo_week)
            TextView listNojWeekTv;

            public ViewHolder(View itemView) {
                x.view().inject(this, itemView);
                AutoUtils.auto(itemView);
            }
        }
    }
}
