package com.loyo.oa.v2.activity.discussDet;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.tool.BaseActivity;
import com.loyo.oa.v2.tool.customview.RoundImageView;
import com.loyo.oa.v2.tool.customview.pullToRefresh.PullToRefreshBase;
import com.loyo.oa.v2.tool.customview.pullToRefresh.PullToRefreshRecycleView;

import java.util.ArrayList;
import java.util.List;

/**
 * 【讨论详情界面】
 * create by libo 2016/03/10
 */

public class ActivityDiscussDet extends BaseActivity {

    private PullToRefreshRecycleView lvNotice;
    private EditText etDiscuss;
    private LinearLayout layoutBack;
    private ImageView imgBack;
    private TextView tvTitle;
    private TextView tvEdit;
    private ImageView ivSubmit;
    private TextView tvSend;

    private void assignViews() {
        lvNotice = (PullToRefreshRecycleView) findViewById(R.id.lv_notice);
        etDiscuss = (EditText) findViewById(R.id.et_discuss);
        tvSend = (TextView) findViewById(R.id.tv_send);

        layoutBack = (LinearLayout) findViewById(R.id.layout_back);
        imgBack = (ImageView) findViewById(R.id.img_back);
        tvTitle = (TextView) findViewById(R.id.tv_title);
        tvEdit = (TextView) findViewById(R.id.tv_edit);
        ivSubmit = (ImageView) findViewById(R.id.iv_submit);
    }

    public static void startThisActivity(Activity act) {
        Intent intent = new Intent(act, ActivityDiscussDet.class);
        act.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        setContentView(R.layout.activity_discuss_det);
        initData();
        initView();
        initListener();
    }

    private List<DiscussDetInfo> infos = new ArrayList<>();

    private void initData() {
        for (int i = 0; i < 20; i++) {
            infos.add(new DiscussDetInfo().setIsMine(i % 2 == 0));
        }
    }

    private void initView() {
        assignViews();

        tvTitle.setText("讨论");
        tvEdit.setText("查看项目");

        tvTitle.setVisibility(View.VISIBLE);
        tvEdit.setVisibility(View.VISIBLE);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        lvNotice.getRefreshableView().setLayoutManager(linearLayoutManager);

        DiscussDetAdapter adapter = new DiscussDetAdapter();
        adapter.updataList(infos);
        lvNotice.getRefreshableView().setAdapter(adapter);
    }

    private void initListener() {
        layoutBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        tvEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast("查看项目");
            }
        });

        lvNotice.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<RecyclerView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<RecyclerView> refreshView) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        lvNotice.onRefreshComplete();
                    }
                }, 2000);
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<RecyclerView> refreshView) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        lvNotice.onRefreshComplete();
                    }
                }, 2000);
            }
        });
    }

    private class DiscussDetMineViewHolder extends RecyclerView.ViewHolder {
        private TextView tvMineTime;
        private TextView tvMine;
        private TextView tvContent;
        private RoundImageView ivMineAvatar;

        public DiscussDetMineViewHolder(View itemView) {
            super(itemView);
            tvMineTime = (TextView) itemView.findViewById(R.id.tv_mine_time);
            tvMine = (TextView) itemView.findViewById(R.id.tv_mine);
            tvContent = (TextView) itemView.findViewById(R.id.tv_content);
            ivMineAvatar = (RoundImageView) itemView.findViewById(R.id.iv_mine_avatar);
        }
    }

    private class DiscussDetOtherViewHolder extends RecyclerView.ViewHolder {
        private TextView mTvOtherTime;
        private TextView mTvOtherName;
        private TextView mTvOtherContent;
        private RoundImageView mIvOtherAvatar;

        public DiscussDetOtherViewHolder(View itemView) {
            super(itemView);
            mTvOtherTime = (TextView) itemView.findViewById(R.id.tv_other_time);
            mTvOtherName = (TextView) itemView.findViewById(R.id.tv_other_name);
            mTvOtherContent = (TextView) itemView.findViewById(R.id.tv_other_content);
            mIvOtherAvatar = (RoundImageView) itemView.findViewById(R.id.iv_other_avatar);
        }
    }

    private class DiscussDetAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private List<DiscussDetInfo> datas = new ArrayList<>();

        public void updataList(List<DiscussDetInfo> data) {
            if (data == null)
                data = new ArrayList<>();
            this.datas = data;
            this.notifyDataSetChanged();
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = null;
            switch (viewType) {
                case DiscussSendMode.mine:
                    view = View.inflate(ActivityDiscussDet.this, R.layout.item_discuss_det_mine, null);
                    return new DiscussDetMineViewHolder(view);
                case DiscussSendMode.other:
                    view = View.inflate(ActivityDiscussDet.this, R.layout.item_discuss_det_other, null);
                    return new DiscussDetOtherViewHolder(view);
            }
            return null;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            if (holder == null)
                return;
            DiscussDetInfo info = datas.get(position);
            if (holder.getClass() == DiscussDetMineViewHolder.class) {
                DiscussDetMineViewHolder mineHolder = (DiscussDetMineViewHolder) holder;
                mineHolder.tvMineTime.setText(info.getTime());
                mineHolder.tvContent.setText(info.getContent());
            } else if (holder.getClass() == DiscussDetOtherViewHolder.class) {
                DiscussDetOtherViewHolder otherHolder = (DiscussDetOtherViewHolder) holder;
                otherHolder.mTvOtherName.setText(info.getName());
                otherHolder.mTvOtherContent.setText(info.getContent());
                otherHolder.mTvOtherTime.setText(info.getTime());
            }
        }

        @Override
        public int getItemCount() {
            return datas.size();
        }

        @Override
        public int getItemViewType(int position) {
            return datas.get(position).isMine()
                    ? DiscussSendMode.mine
                    : DiscussSendMode.other;
        }
    }

    private static class DiscussSendMode {
        private static final int other = 0x0001;
        private static final int mine = 0x0002;
    }
}
