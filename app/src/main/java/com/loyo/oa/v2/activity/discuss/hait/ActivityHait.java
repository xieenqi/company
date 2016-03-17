package com.loyo.oa.v2.activity.discuss.hait;

import android.app.Activity;
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
import com.loyo.oa.v2.activity.discuss.ActivityDiscussDet;
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

    private PullToRefreshRecycleView lv_notice;
    private LinearLayout img_title_left;
    private ImageView img_back;
    private TextView tv_back;
    private TextView tv_title1;
    private LinearLayoutManager linearLayoutManager;
    private HaitAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hait);
        initData();
        initView();
        initListener();
    }
    
    private List<HttpHaitMe> infos = new ArrayList<>();

    private void initData() {
        for (int i = 0; i < 20; i++) {
            infos.add(new HttpHaitMe());
        }
    }

    private void initView() {
        assignViews();

        tv_back.setText("我的讨论");
        tv_title1.setText("@我的");
        
        lv_notice.setMode(PullToRefreshBase.Mode.BOTH);
        
        linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        
        lv_notice.getRefreshableView().setLayoutManager(linearLayoutManager);
        
        adapter = new HaitAdapter();
        adapter.updataList(infos);
        lv_notice.getRefreshableView().setAdapter(adapter);
    }

    private void assignViews() {
        img_title_left = (LinearLayout) findViewById(R.id.img_title_left);
        img_back = (ImageView) findViewById(R.id.img_back);
        tv_back = (TextView) findViewById(R.id.tv_back);
        tv_title1 = (TextView) findViewById(R.id.tv_title_1);

        lv_notice = (PullToRefreshRecycleView) findViewById(R.id.lv_notice);
    }

    private void initListener() {
        tv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        lv_notice.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<RecyclerView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<RecyclerView> refreshView) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        lv_notice.onRefreshComplete();
                    }
                }, 2000);
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<RecyclerView> refreshView) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        lv_notice.onRefreshComplete();
                    }
                }, 2000);
            }
        });
    }

    private class HaitViewHolder extends RecyclerView.ViewHolder {

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
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View view) {
                    ActivityDiscussDet.startThisActivity((Activity) view.getContext());
                }
            });
        }
    }

    private class HaitAdapter extends RecyclerView.Adapter<HaitViewHolder> {
        private List<HttpHaitMe> datas = new ArrayList<>();
        
        public void updataList(List<HttpHaitMe> data){
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
            HttpHaitMe info = datas.get(position);
            
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

