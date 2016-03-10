package com.loyo.oa.v2.activity.discuss;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activity.discussDet.ActivityDiscussDet;
import com.loyo.oa.v2.activity.hait.ActivityHait;
import com.loyo.oa.v2.tool.BaseActivity;
import com.loyo.oa.v2.tool.customview.pullToRefresh.PullToRefreshBase;
import com.loyo.oa.v2.tool.customview.pullToRefresh.PullToRefreshListView;
import com.loyo.oa.v2.tool.customview.pullToRefresh.PullToRefreshRecycleView;

import java.util.ArrayList;
import java.util.List;

/**
 * 【我的讨论】
 * create by libo 2016/3/9
 */
public class ActivityMyDiscuss extends BaseActivity implements View.OnClickListener, PullToRefreshListView.OnRefreshListener2 {
    private PullToRefreshRecycleView lvNotice;
    private LinearLayout layoutBack;
    private ImageView imgBack;
    private TextView tvTitle;
    private TextView tvEdit;
    private ImageView ivSubmit;
    private LinearLayoutManager linearLayoutManager;

    private List<DiscussInfo> list = new ArrayList<>();
    private DiscussAdapter adapter;

    private void assignViews() {
        lvNotice = (PullToRefreshRecycleView) findViewById(R.id.lv_notice);

        layoutBack = (LinearLayout) findViewById(R.id.layout_back);
        imgBack = (ImageView) findViewById(R.id.img_back);
        tvTitle = (TextView) findViewById(R.id.tv_title);
        tvEdit = (TextView) findViewById(R.id.tv_edit);
        ivSubmit = (ImageView) findViewById(R.id.iv_submit);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mydiscuss);
        initData();
        initView();
        initListener();
    }

    private void initData() {
        for (int i = 0; i < 20; i++) {
            list.add(new DiscussInfo());
        }
    }

    private void initView() {
        assignViews();

        /**  设置actionbar显示  **/
        tvTitle.setText("我的讨论");
        tvEdit.setText("@我的");
        tvTitle.setVisibility(View.VISIBLE);
        tvEdit.setVisibility(View.VISIBLE);

        linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        lvNotice.getRefreshableView().setLayoutManager(linearLayoutManager);
        lvNotice.setMode(PullToRefreshBase.Mode.BOTH);

        adapter = new DiscussAdapter();
        adapter.updataList(list);
        lvNotice.getRefreshableView().setAdapter(adapter);
    }

    private void initListener() {
        layoutBack.setOnClickListener(this);
        tvEdit.setOnClickListener(this);

        lvNotice.setOnRefreshListener(this);


    }

    @Override
    public void onClick(View v) {
        if (v == layoutBack) {
            finish();
        } else if (v == tvEdit) {
//            Toast("@我的");
            Intent intent = new Intent(this, ActivityHait.class);
            startActivity(intent);
        }
    }

    @Override
    public void onPullDownToRefresh(PullToRefreshBase refreshView) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                lvNotice.onRefreshComplete();
            }
        }, 2000);
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase refreshView) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                lvNotice.onRefreshComplete();
            }
        }, 2000);
    }

    private class DiscussViewHolder extends RecyclerView.ViewHolder {
        private ImageView ivIcon;
        private View viewMsgpoint;
        private TextView tvTitle;
        private TextView tvTime;
        private TextView tvContent;

        public DiscussViewHolder(View itemView) {
            super(itemView);
            ivIcon = (ImageView) itemView.findViewById(R.id.iv_icon);
            viewMsgpoint = itemView.findViewById(R.id.view_msgpoint);
            tvTitle = (TextView) itemView.findViewById(R.id.tv_title);
            tvTime = (TextView) itemView.findViewById(R.id.tv_time);
            tvContent = (TextView) itemView.findViewById(R.id.tv_content);
            itemView.setTag(this);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ActivityDiscussDet.startThisActivity(ActivityMyDiscuss.this);
                }
            });
        }
    }

    private class DiscussAdapter extends RecyclerView.Adapter<DiscussViewHolder> {

        private List<DiscussInfo> datas = new ArrayList<>();

        public void updataList(List<DiscussInfo> data) {
            if (data == null) {
                data = new ArrayList<>();
            }
            this.datas = data;
            this.notifyDataSetChanged();
        }

        @Override
        public DiscussViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = View.inflate(ActivityMyDiscuss.this, R.layout.item_mydiscuss_layout, null);
            return new DiscussViewHolder(view);
        }

        @Override
        public void onBindViewHolder(DiscussViewHolder holder, int position) {
            DiscussInfo info = datas.get(position);
            holder.tvTitle.setText(info.getTitle());
            holder.tvTime.setText(info.getTime());
            holder.tvContent.setText(info.getContent());
        }

        @Override
        public int getItemCount() {
            return datas.size();
        }
    }
}
