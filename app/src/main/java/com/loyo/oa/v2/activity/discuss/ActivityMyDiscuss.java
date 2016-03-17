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
import com.loyo.oa.v2.activity.discuss.hait.ActivityHait;
import com.loyo.oa.v2.beans.PaginationX;
import com.loyo.oa.v2.common.ExtraAndResult;
import com.loyo.oa.v2.common.http.HttpErrorCheck;
import com.loyo.oa.v2.point.MyDiscuss;
import com.loyo.oa.v2.tool.BaseActivity;
import com.loyo.oa.v2.tool.Config_project;
import com.loyo.oa.v2.tool.RCallback;
import com.loyo.oa.v2.tool.RestAdapterFactory;
import com.loyo.oa.v2.tool.customview.pullToRefresh.PullToRefreshBase;
import com.loyo.oa.v2.tool.customview.pullToRefresh.PullToRefreshListView;
import com.loyo.oa.v2.tool.customview.pullToRefresh.PullToRefreshRecycleView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * 【我的讨论】
 * create by libo 2016/3/9
 */
public class ActivityMyDiscuss extends BaseActivity implements View.OnClickListener, PullToRefreshListView.OnRefreshListener2 {
    private PullToRefreshRecycleView lv_discuss;
    private LinearLayout layout_back;
    private TextView tv_title;
    private TextView tv_edit;
    private ImageView iv_submit;
    private LinearLayoutManager linearLayoutManager;
    protected PaginationX<HttpDiscussItem> mDiscuss = new PaginationX(20);

    private List<HttpDiscussItem> list = new ArrayList<>();
    private DiscussAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mydiscuss);
        initData();
        initView();
        initListener();
    }

    private void initView() {
        assignViews();

        /**  设置actionbar显示  **/
        tv_title.setText("我的讨论");
        tv_edit.setText("@我的");
        tv_title.setVisibility(View.VISIBLE);
        tv_edit.setVisibility(View.VISIBLE);
        linearLayoutManager = new LinearLayoutManager(this);
        lv_discuss.getRefreshableView().setLayoutManager(linearLayoutManager);
        lv_discuss.setMode(PullToRefreshBase.Mode.BOTH);

        adapter = new DiscussAdapter();
        lv_discuss.getRefreshableView().setAdapter(adapter);
    }

    private void initData() {
        showLoading("");
        HashMap<String, Object> map = new HashMap<>();
        map.put("pageIndex", "1");
        map.put("pageSize", "20");
        RestAdapterFactory.getInstance().build(Config_project.API_URL_EXTRA()).create(MyDiscuss.class).
                getDisscussList(map, new RCallback<PaginationX<HttpDiscussItem>>() {
                    @Override
                    public void success(PaginationX<HttpDiscussItem> discuss, Response response) {
                        HttpErrorCheck.checkResponse(" 我的讨论数据： ", response);
                        if (!PaginationX.isEmpty(discuss)) {
//                    ArrayList<Bulletin> lstData_bulletin_current = pagination.getRecords();
                            mDiscuss = discuss;

//                    if (isTopAdd) {
//                        bulletins.clear();
//                    }
//                    bulletins.addAll(lstData_bulletin_current);
//
//                    bindData();
                        } else {
                            //Global.Toast(!isTopAdd ? R.string.app_list_noMoreData : R.string.app_no_newest_data);
                        }
                        adapter.updataList(discuss.getRecords());
                        lv_discuss.onRefreshComplete();
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        HttpErrorCheck.checkError(error);
                        super.failure(error);
                        lv_discuss.onRefreshComplete();
                    }
                });
    }

    private void assignViews() {
        lv_discuss = (PullToRefreshRecycleView) findViewById(R.id.lv_discuss);
        layout_back = (LinearLayout) findViewById(R.id.layout_back);
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_edit = (TextView) findViewById(R.id.tv_edit);
        iv_submit = (ImageView) findViewById(R.id.iv_submit);
    }

    private void initListener() {
        layout_back.setOnClickListener(this);
        tv_edit.setOnClickListener(this);
        lv_discuss.setOnRefreshListener(this);


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.layout_back:
                finish();
                break;
            case R.id.tv_edit:
                Intent intent = new Intent(this, ActivityHait.class);
                intent.putExtra(ExtraAndResult.EXTRA_TYPE, "");
                intent.putExtra(ExtraAndResult.EXTRA_ID, "");
                startActivity(intent);
                break;
        }
    }

    @Override
    public void onPullDownToRefresh(PullToRefreshBase refreshView) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                lv_discuss.onRefreshComplete();
            }
        }, 2000);
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase refreshView) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                lv_discuss.onRefreshComplete();
            }
        }, 2000);
    }


    private class DiscussAdapter extends RecyclerView.Adapter<DiscussViewHolder> {

        private List<HttpDiscussItem> datas = new ArrayList<>();

        public void updataList(List<HttpDiscussItem> data) {
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
            HttpDiscussItem info = datas.get(position);
            holder.tv_title.setText(info.title);
            holder.tv_time.setText(info.updatedAt.substring(11, 19));
            holder.tv_content.setText(info.content);
            holder.openItem(datas.get(position));
        }

        @Override
        public int getItemCount() {
            return datas.size();
        }
    }

    private class DiscussViewHolder extends RecyclerView.ViewHolder {
        private ImageView iv_icon;
        private View view_msgpoint;
        private TextView tv_title;
        private TextView tv_time;
        private TextView tv_content;

        public DiscussViewHolder(View itemView) {
            super(itemView);
            iv_icon = (ImageView) itemView.findViewById(R.id.iv_icon);
            view_msgpoint = itemView.findViewById(R.id.view_msgpoint);
            tv_title = (TextView) itemView.findViewById(R.id.tv_title);
            tv_time = (TextView) itemView.findViewById(R.id.tv_time);
            tv_content = (TextView) itemView.findViewById(R.id.tv_content);
            itemView.setTag(this);

        }

        public void openItem(final HttpDiscussItem itemData) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(ActivityMyDiscuss.this, ActivityDiscussDet.class);
                    intent.putExtra(ExtraAndResult.EXTRA_TYPE, itemData.bizType);
                    intent.putExtra(ExtraAndResult.EXTRA_UUID, itemData.attachmentUUId);
                    intent.putExtra(ExtraAndResult.EXTRA_ID, itemData.id);
                    startActivity(intent);
                }
            });
            switch (itemData.bizType) {
                case 1:
                    iv_icon.setImageResource(R.drawable.ic_disuss_report);
                    break;
                case 2:
                    iv_icon.setImageResource(R.drawable.ic_discuss_task);
                    break;
                case 5:
                    iv_icon.setImageResource(R.drawable.ic_discuss_project);
                    break;
            }
        }
    }
}
