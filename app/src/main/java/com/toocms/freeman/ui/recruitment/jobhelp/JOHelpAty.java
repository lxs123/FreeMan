package com.toocms.freeman.ui.recruitment.jobhelp;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.toocms.freeman.R;
import com.toocms.freeman.https.Document;
import com.toocms.freeman.ui.BaseAty;
import com.zero.autolayout.utils.AutoUtils;

import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;
import java.util.Map;

import cn.zero.android.common.util.JSONUtils;
import cn.zero.android.common.util.ListUtils;
import cn.zero.android.common.view.swipetoloadlayout.view.SwipeToLoadRecyclerView;
import cn.zero.android.common.view.swipetoloadlayout.view.listener.OnItemClickListener;

/**
 * Created by admin on 2017/3/27.
 */

public class JOHelpAty extends BaseAty implements OnItemClickListener {
    @ViewInject(R.id.jo_help_list)
    private SwipeToLoadRecyclerView swipeToLoadRecyclerView;
    /**
     * 招工帮助列表[hiring]
     */

    /**
     * 帮助中心列表[acolyte]
     */
    private String flag;
    private Document document;
    private ArrayList<Map<String, String>> joHelpList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        flag = getIntent().getStringExtra("flag");
        showProgressDialog();
        if (TextUtils.equals(flag, "index")) {
            mActionBar.setTitle("帮助中心");
            document.acolyte(this);
        } else if (TextUtils.equals(flag, "search")) {
            mActionBar.setTitle("帮助信息");
            document.working(this);
        } else {
            mActionBar.setTitle("招工帮助");
            document.hiring(this);
        }
        swipeToLoadRecyclerView.setOnItemClickListener(this);
        swipeToLoadRecyclerView.getRecyclerView().setLayoutManager(new LinearLayoutManager(this));

    }

    @Override
    protected int getLayoutResId() {
        return R.layout.aty_jo_help;
    }

    @Override
    protected void initialized() {
        document = new Document();
    }

    @Override
    protected void requestData() {


    }

    @Override
    public void onComplete(RequestParams params, String result) {
        if (params.getUri().contains("Document/hiring")) {
            joHelpList = JSONUtils.parseDataToMapList(result);
        } else if (params.getUri().contains("Document/acolyte")) {
            joHelpList = JSONUtils.parseDataToMapList(result);
        } else if (params.getUri().contains("Document/working")) {
            joHelpList = JSONUtils.parseDataToMapList(result);
        }
        swipeToLoadRecyclerView.setAdapter(new HelpAdapter());
        super.onComplete(params, result);
    }

    @Override
    public void onItemClick(View view, int i) {
        if (TextUtils.equals(flag, "index")) {
            Bundle bundle = new Bundle();
            bundle.putString("flag", "index");
            bundle.putString("doc_id", joHelpList.get(i).get("doc_id"));
            startActivity(JOHelpDetailAty.class, bundle);
        } else if (TextUtils.equals(flag, "search")) {
            Bundle bundle = new Bundle();
            bundle.putString("flag", "search");
            bundle.putString("doc_id", joHelpList.get(i).get("doc_id"));
            startActivity(JOHelpDetailAty.class, bundle);
        } else {
            Bundle bundle = new Bundle();
            bundle.putString("doc_id", joHelpList.get(i).get("doc_id"));
            startActivity(JOHelpDetailAty.class, bundle);
        }

    }

    private class HelpAdapter extends RecyclerView.Adapter<HelpAdapter.ViewHodler> {


        @Override
        public ViewHodler onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.listitem_jo_help, parent, false);
            return new ViewHodler(view);
        }

        @Override
        public void onBindViewHolder(ViewHodler holder, int position) {
            Map<String, String> map = joHelpList.get(position);
            holder.tvId.setText(map.get("doc_id") + "、");
            holder.tvTitle.setText(map.get("title"));
            holder.tvContent.setText(map.get("content"));
        }

        @Override
        public int getItemCount() {
            return ListUtils.getSize(joHelpList);
        }

        public class ViewHodler extends RecyclerView.ViewHolder {
            @ViewInject(R.id.jo_help_id)
            TextView tvId;
            @ViewInject(R.id.jo_help_title)
            TextView tvTitle;
            @ViewInject(R.id.jo_help_content)
            TextView tvContent;

            public ViewHodler(View itemView) {
                super(itemView);
                x.view().inject(this, itemView);
                AutoUtils.autoSize(itemView);
            }
        }
    }
}
