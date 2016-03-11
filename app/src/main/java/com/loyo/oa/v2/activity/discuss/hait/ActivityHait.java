package com.loyo.oa.v2.activity.discuss.hait;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.ViewGroup;
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
 * @我的界面 create by libo 2016/03/10
 */
public class ActivityHait extends BaseActivity {

    private PullToRefreshRecycleView lvNotice;
    private LinearLayout imgTitleLeft;
    private ImageView imgBack;
    private TextView tvBack;
    private TextView tvTitle1;
    private LinearLayoutManager linearLayoutManager;
    private HaitAdapter adapter;

    private void assignViews() {
        imgTitleLeft = (LinearLayout) findViewById(R.id.img_title_left);
        imgBack = (ImageView) findViewById(R.id.img_back);
        tvBack = (TextView) findViewById(R.id.tv_back);
        tvTitle1 = (TextView) findViewById(R.id.tv_title_1);

        lvNotice = (PullToRefreshRecycleView) findViewById(R.id.lv_notice);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hait);
        initData();
        initView();
        initListener();
    }
    
    private List<HaitInfo> infos = new ArrayList<>();

    private void initData() {
        for (int i = 0; i < 20; i++) {
            infos.add(new HaitInfo());
        }
    }

    private void initView() {
        assignViews();

        tvBack.setText("我的讨论");
        tvTitle1.setText("@我的");
        
        lvNotice.setMode(PullToRefreshBase.Mode.BOTH);
        
        linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        
        lvNotice.getRefreshableView().setLayoutManager(linearLayoutManager);
        
        adapter = new HaitAdapter();
        adapter.updataList(infos);
        lvNotice.getRefreshableView().setAdapter(adapter);
    }

    private void initListener() {
        tvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
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

    private static class HaitViewHolder extends RecyclerView.ViewHolder {

        private TextView tvTime;
        private RoundImageView ivHaitAvatar;
        private TextView tvTitle;
        private TextView tvContent;

        public HaitViewHolder(View itemView) {
            super(itemView);
            tvTime = (TextView) itemView.findViewById(R.id.tv_time);
            ivHaitAvatar = (RoundImageView) itemView.findViewById(R.id.iv_hait_avatar);
            tvTitle = (TextView) itemView.findViewById(R.id.tv_title);
            tvContent = (TextView) itemView.findViewById(R.id.tv_content);

            itemView.setTag(this);
        }
    }

    private class HaitAdapter extends RecyclerView.Adapter<HaitViewHolder> {
        private List<HaitInfo> datas = new ArrayList<>();
        
        public void updataList(List<HaitInfo> data){
            if (data == null){
                data = new ArrayList<>();
            }
            this.datas = data;
            this.notifyDataSetChanged();
        }

        @Override
        public HaitViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = View.inflate(ActivityHait.this, R.layout.item_hait_layout, null);
            return new HaitViewHolder(view);
        }

        @Override
        public void onBindViewHolder(HaitViewHolder holder, int position) {
            HaitInfo info = datas.get(position);
            
            holder.tvTime.setText(info.getTime());
            holder.tvContent.setText(info.getTitle());
            holder.tvTitle.setText(parseTitle(info.getName(), info.getGroup()));
        }

        private SpannableStringBuilder parseTitle(String name, String group) {
            if (TextUtils.isEmpty(name) || TextUtils.isEmpty(group)) {
                return null;
            }
            String str = name + "在\u2005" + group + "\u2005中@你";
            SpannableStringBuilder builder = new SpannableStringBuilder(str);

            int start = name.length() + 2;
            int end = start + group.length();

            ForegroundColorSpan colorSpan = new ForegroundColorSpan(Color.parseColor("#2c9dfc"));
            builder.setSpan(colorSpan, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            return builder;
        }

        @Override
        public int getItemCount() {
            return datas.size();
        }
    }
}

