package com.loyo.oa.v2.activityui.discuss.hait;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
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
import com.loyo.oa.v2.activityui.discuss.DiscussDetActivity;
import com.loyo.oa.v2.activityui.discuss.HttpMyDiscussItem;
import com.loyo.oa.v2.beans.PaginationX;
import com.loyo.oa.v2.common.ExtraAndResult;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.common.http.HttpErrorCheck;
import com.loyo.oa.v2.point.MyDiscuss;
import com.loyo.oa.v2.tool.BaseActivity;
import com.loyo.oa.v2.tool.Config_project;
import com.loyo.oa.v2.tool.RCallback;
import com.loyo.oa.v2.tool.RestAdapterFactory;
import com.loyo.oa.v2.customview.RoundImageView;
import com.loyo.oa.v2.customview.pullToRefresh.PullToRefreshBase;
import com.loyo.oa.v2.customview.pullToRefresh.PullToRefreshRecycleView;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * @我的界面 create by libo 2016/03/10
 */
public class HaitActivity extends BaseActivity {

    private PullToRefreshRecycleView lv_myDiscuss;
    private LinearLayout img_title_left;
    private ImageView img_back;
    private TextView tv_back;
    private LinearLayout layout_back;
    private TextView tv_title1;
    private LinearLayoutManager linearLayoutManager;
    private HaitAdapter adapter;
    private boolean isTopAdd = true;
    private int pageIndex = 1;


    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hait);
        initView();
        initListener();
        getData();
    }

    private void initView() {
        assignViews();
        // tv_back.setText("我的讨论");
        tv_title1.setText("@我的");

        lv_myDiscuss.setMode(PullToRefreshBase.Mode.BOTH);

        linearLayoutManager = new LinearLayoutManager(this);
        lv_myDiscuss.getRefreshableView().setLayoutManager(linearLayoutManager);
        adapter = new HaitAdapter();
        lv_myDiscuss.getRefreshableView().setAdapter(adapter);
    }

    private void assignViews() {
        img_title_left = (LinearLayout) findViewById(R.id.img_title_left);
        img_back = (ImageView) findViewById(R.id.img_back);
        tv_back = (TextView) findViewById(R.id.tv_back);
        layout_back = (LinearLayout) findViewById(R.id.img_title_left);
        tv_title1 = (TextView) findViewById(R.id.tv_title_1);
        lv_myDiscuss = (PullToRefreshRecycleView) findViewById(R.id.lv_myDiscuss);
    }

    private void initListener() {
        layout_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                finish();
            }
        });
        lv_myDiscuss.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<RecyclerView>() {
            @Override
            public void onPullDownToRefresh(final PullToRefreshBase<RecyclerView> refreshView) {
                pageIndex = 1;
                isTopAdd = true;
                getData();
            }

            @Override
            public void onPullUpToRefresh(final PullToRefreshBase<RecyclerView> refreshView) {
                pageIndex++;
                isTopAdd = false;
                getData();
            }
        });
    }

    private void getData() {
        showLoading("");
        HashMap<String, Object> map = new HashMap<>();
        map.put("pageIndex", pageIndex + "");
        map.put("pageSize", "20");
        RestAdapterFactory.getInstance().build(Config_project.API_URL_EXTRA()).create(MyDiscuss.class).
                getMyDisscussList(map, new RCallback<PaginationX<HttpMyDiscussItem>>() {
                    @Override
                    public void success(final PaginationX<HttpMyDiscussItem> discuss, final Response response) {
                        HttpErrorCheck.checkResponse(" 【@我的】讨论数据： ", response);
                        if (!PaginationX.isEmpty(discuss)) {
                            // mDiscuss = discuss;
                            if (isTopAdd) {
                                adapter.cleanData();
                            }
                            adapter.updataList(discuss.getRecords());
                        } else {
                            Global.Toast(!isTopAdd ? R.string.app_list_noMoreData : R.string.app_no_newest_data);
                        }
                        lv_myDiscuss.onRefreshComplete();
                    }

                    @Override
                    public void failure(final RetrofitError error) {
                        HttpErrorCheck.checkError(error);
                        super.failure(error);
                        lv_myDiscuss.onRefreshComplete();
                    }
                });
    }


    private class HaitAdapter extends RecyclerView.Adapter<HaitViewHolder> {
        private List<HttpMyDiscussItem> datas = new ArrayList<>();

        public void updataList(List<HttpMyDiscussItem> data) {
            if (data == null) {
                data = new ArrayList<>();
            }
            this.datas.addAll(data);
            this.notifyDataSetChanged();
        }

        public void cleanData() {
            datas.clear();
        }

        @Override
        public HaitViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
            View view = View.inflate(HaitActivity.this, R.layout.item_hait_layout, null);
            return new HaitViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final HaitViewHolder holder, final int position) {
            HttpMyDiscussItem info = datas.get(position);
            holder.tv_time.setText(info.updatedAt.substring(11, 19));
            holder.tv_content.setText(info.atContent);
            holder.tv_title.setText(parseTitle(info.creator.name, info.title));
            ImageLoader.getInstance().displayImage(info.creator.avatar, holder.iv_avatar);
            holder.openItem(datas.get(position));
        }

        private SpannableStringBuilder parseTitle(final String name, final String group) {
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

    private class HaitViewHolder extends RecyclerView.ViewHolder {

        private TextView tv_time;
        private RoundImageView iv_avatar;
        private TextView tv_title;
        private TextView tv_content;

        public HaitViewHolder(final View itemView) {
            super(itemView);
            tv_time = (TextView) itemView.findViewById(R.id.tv_time);
            iv_avatar = (RoundImageView) itemView.findViewById(R.id.iv_avatar);
            tv_title = (TextView) itemView.findViewById(R.id.tv_title);
            tv_content = (TextView) itemView.findViewById(R.id.tv_content);

            itemView.setTag(this);
//            itemView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(final View view) {
//                    DiscussDetActivity.startThisActivity((Activity) view.getContext());
//                }
//            });
        }

        public void openItem(final HttpMyDiscussItem itemData) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(HaitActivity.this, DiscussDetActivity.class);
                    intent.putExtra(ExtraAndResult.EXTRA_TYPE, itemData.bizType);
                    intent.putExtra(ExtraAndResult.EXTRA_UUID, itemData.attachmentUUId);
                    intent.putExtra(ExtraAndResult.EXTRA_ID, "");//@我界面不刷新红点
                    intent.putExtra(ExtraAndResult.EXTRA_TYPE_ID, itemData.bizId);
                    startActivity(intent);
                }
            });
            itemView.setOnTouchListener(Global.GetTouch());
        }
    }
}

