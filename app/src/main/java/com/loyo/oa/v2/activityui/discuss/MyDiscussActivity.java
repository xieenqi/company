package com.loyo.oa.v2.activityui.discuss;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.beans.PaginationX;
import com.loyo.oa.v2.common.ExtraAndResult;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.common.http.HttpErrorCheck;
import com.loyo.oa.v2.customview.pullToRefresh.PullToRefreshBase;
import com.loyo.oa.v2.customview.pullToRefresh.PullToRefreshListView;
import com.loyo.oa.v2.point.MyDiscuss;
import com.loyo.oa.v2.tool.BaseActivity;
import com.loyo.oa.v2.tool.Config_project;
import com.loyo.oa.v2.tool.DateTool;
import com.loyo.oa.v2.tool.LogUtil;
import com.loyo.oa.v2.tool.RCallback;
import com.loyo.oa.v2.tool.RestAdapterFactory;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * 【我的讨论】
 * create by libo 2016/3/9
 */
public class MyDiscussActivity extends BaseActivity implements View.OnClickListener, PullToRefreshListView.OnRefreshListener2 {
    private PullToRefreshListView lv_discuss;
    private LinearLayout layout_back;
    private TextView tv_title;
    private TextView tv_edit;
    private ArrayList<HttpDiscussItem> listData = new ArrayList<>();

    private DiscussAdapter adapter;
    private boolean isTopAdd = false;
    private int pageIndex = 1;
    private boolean isfirst = true;
    Handler handler = new Handler() {
        @Override
        public void dispatchMessage(Message msg) {
            super.dispatchMessage(msg);
            adapter.updataList(listData);
        }
    };

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mydiscuss);
        initView();
        initListener();
        getData();
    }

    private void initView() {
        assignViews();

        /**  设置actionbar显示  **/
        tv_title.setText("我的讨论");
        tv_edit.setText("@我的");
        tv_title.setVisibility(View.VISIBLE);
        tv_edit.setVisibility(View.VISIBLE);
        lv_discuss.setMode(PullToRefreshBase.Mode.BOTH);
        lv_discuss.setOnRefreshListener(this);
        adapter = new DiscussAdapter();
        lv_discuss.getRefreshableView().setAdapter(adapter);
    }

    private void assignViews() {
        lv_discuss = (PullToRefreshListView) findViewById(R.id.lv_discuss);
        layout_back = (LinearLayout) findViewById(R.id.layout_back);
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_edit = (TextView) findViewById(R.id.tv_edit);
    }

    private void initListener() {
        layout_back.setOnClickListener(this);
        tv_edit.setOnClickListener(this);
        lv_discuss.setOnRefreshListener(this);
    }

    private void getData() {
        if (isfirst)
            showLoading("");
        HashMap<String, Object> map = new HashMap<>();
        map.put("pageIndex", pageIndex + "");
        map.put("pageSize", "10");
        RestAdapterFactory.getInstance().build(Config_project.API_URL_EXTRA()).create(MyDiscuss.class).
                getDisscussList(map, new RCallback<PaginationX<HttpDiscussItem>>() {
                    @Override
                    public void success(final PaginationX<HttpDiscussItem> discuss, final Response response) {
                        HttpErrorCheck.checkResponse(" 我的讨论数据： ", response);
                        if (!PaginationX.isEmpty(discuss)) {
                            if (isTopAdd) {
                                listData = null;
                                listData = discuss.getRecords();
                            } else {
                                listData.addAll(discuss.getRecords());
                            }

                            Message msg = new Message();
                            msg.obj = listData;
                            handler.sendEmptyMessage(1);
                        } else {
                            Global.Toast(!isTopAdd ? R.string.app_list_noMoreData : R.string.app_no_newest_data);
                        }
                        lv_discuss.onRefreshComplete();
                    }

                    @Override
                    public void failure(final RetrofitError error) {
                        HttpErrorCheck.checkError(error);
                        super.failure(error);
                        lv_discuss.onRefreshComplete();
                    }
                });
        isfirst = false;
    }


    @Override
    public void onClick(final View v) {
        switch (v.getId()) {
            case R.id.layout_back:
                onBackPressed();
                break;
            case R.id.tv_edit:
                app.startActivity(this, HaitMyActivity.class, MainApp.ENTER_TYPE_RIGHT, false, null);
                break;
        }
    }

    @Override
    public void onPullDownToRefresh(final PullToRefreshBase refreshView) {
        pageIndex = 1;
        isTopAdd = true;
        getData();

    }

    @Override
    public void onPullUpToRefresh(final PullToRefreshBase refreshView) {
        pageIndex++;
        isTopAdd = false;
        getData();
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case ExtraAndResult.REQUEST_CODE:
                    pageIndex = 1;
                    isTopAdd = true;
                    getData();
                    LogUtil.d("数组刷新了红点数据");
                    break;
            }
        }
    }

    private class DiscussAdapter extends BaseAdapter {
        private List<HttpDiscussItem> datas = new ArrayList<>();
        private LayoutInflater inflater;

        public DiscussAdapter() {
            inflater = LayoutInflater.from(MyDiscussActivity.this);
        }

        public void updataList(List<HttpDiscussItem> data) {
            if (data == null) {
                data = new ArrayList<>();
            }
            datas.clear();
            datas.addAll(data);
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return null == datas ? 0 : datas.size();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            DiscussViewHolder holder = null;
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.item_mydiscuss_layout, null);
                holder = new DiscussViewHolder(convertView);
            } else {
                holder = (DiscussViewHolder) convertView.getTag();
            }
            HttpDiscussItem info = datas.get(position);
            holder.tv_title.setText(info.title);
            holder.tv_time.setText(info.newUpdatedAt != 0 ? DateTool.getDiffTime(info.newUpdatedAt) : info.updatedAt.substring(11, 19));
            holder.tv_content.setText(info.creator.name + ":" + info.content);
            holder.openItem(datas.get(position));
            return convertView;
        }
    }

    private class DiscussViewHolder {
        private ImageView iv_icon;
        private ImageView v_msgPoint;
        private TextView tv_title;
        private TextView tv_time;
        private TextView tv_content;
        private TextView tv_dateTime;
        View itemView;

        public DiscussViewHolder(final View itemView) {
            iv_icon = (ImageView) itemView.findViewById(R.id.iv_icon);
            v_msgPoint = (ImageView) itemView.findViewById(R.id.v_msgPoint);
            tv_title = (TextView) itemView.findViewById(R.id.tv_title);
            tv_time = (TextView) itemView.findViewById(R.id.tv_time);
            tv_content = (TextView) itemView.findViewById(R.id.tv_content);
            tv_dateTime = (TextView) itemView.findViewById(R.id.tv_dateTime);
            itemView.setTag(this);
            this.itemView = itemView;
        }

        public void openItem(final HttpDiscussItem itemData) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View view) {
                    Intent intent = new Intent(MyDiscussActivity.this, DiscussDetialActivity.class);
                    intent.putExtra(ExtraAndResult.EXTRA_TYPE, itemData.bizType);
                    intent.putExtra(ExtraAndResult.EXTRA_UUID, itemData.attachmentUUId);
                    intent.putExtra(ExtraAndResult.EXTRA_TYPE_ID, itemData.bizId);
                    intent.putExtra(ExtraAndResult.EXTRA_ID, itemData.summaryId);
                    startActivityForResult(intent, ExtraAndResult.REQUEST_CODE);
                    overridePendingTransition(R.anim.enter_righttoleft, R.anim.exit_righttoleft);
                }
            });
            switch (itemData.bizType) {
                case 1:
                    iv_icon.setImageResource(R.drawable.ic_disuss_report);
                    tv_dateTime.setVisibility(View.VISIBLE);
                    tv_dateTime.setText(app.df11.format(new Date(System.currentTimeMillis())));
                    break;
                case 2:
                    iv_icon.setImageResource(R.drawable.ic_discuss_task);
                    tv_dateTime.setVisibility(View.GONE);
                    break;
                case 5:
                    iv_icon.setImageResource(R.drawable.ic_discuss_project);
                    tv_dateTime.setVisibility(View.GONE);
                    break;
                default:

                    break;

            }
            v_msgPoint.setVisibility(itemData.viewed ? View.INVISIBLE : View.VISIBLE);
        }
    }
}
