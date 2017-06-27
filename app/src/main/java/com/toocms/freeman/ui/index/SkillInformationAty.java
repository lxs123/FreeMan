package com.toocms.freeman.ui.index;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.toocms.freeman.R;
import com.toocms.freeman.ui.BaseAty;
import com.toocms.freeman.ui.view.MyGridView;
import com.zero.autolayout.utils.AutoUtils;

import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

/**
 * 新建招工单
 */

public class SkillInformationAty extends BaseAty {

    @ViewInject(R.id.new_job_order_imgs)
    private MyGridView imgs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActionBar.setTitle("技能信息");
        imgs.setAdapter(new ImgGridAdapter());
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.aty_skill_information;
    }

    @Override
    protected void initialized() {

    }

    @Override
    protected void requestData() {

    }

    private class ImgGridAdapter extends BaseAdapter {

        private ViewHodler viewHodler;

        @Override
        public int getCount() {
            return 5;
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
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                viewHodler = new ViewHodler();
                convertView = LayoutInflater.from(SkillInformationAty.this).inflate(R.layout.listitem_new_job_order, parent, false);
                x.view().inject(viewHodler, convertView);
                AutoUtils.autoSize(convertView);
                convertView.setTag(viewHodler);
            } else {
                viewHodler = (ViewHodler) convertView.getTag();
            }
            if (position == 4) {
                viewHodler.imgv.setImageResource(R.drawable.btn_add);
            }
            return convertView;
        }

        public class ViewHodler {
            @ViewInject(R.id.list_new_job_imgs)
            ImageView imgv;
        }
    }
}
