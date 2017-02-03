package com.loyo.oa.v2.tool;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.library.module.widget.loading.LoadingLayout;
import com.loyo.oa.v2.R;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.beans.BaseBeans;
import com.loyo.oa.v2.beans.PaginationX;
import com.loyo.oa.v2.activityui.tasks.fragment.TaskManagerFragment;
import com.loyo.oa.pulltorefresh.PullToRefreshBase;
import com.loyo.oa.pulltorefresh.PullToRefreshListView;
import com.loyo.oa.v2.network.LoyoErrorChecker;

import rx.Subscription;

/**
 * 搜索的 基类
 * 客户搜索；
 * 项目搜索；
 * 拜访搜索（新建拜访签到－选择客户－搜索）；
 * 任务搜索；
 * 工单搜索；
 * 审批搜索；
 * 等等。
 *
 * @param <T>
 */
public abstract class BaseSearchActivity<T extends BaseBeans> extends BaseLoadingActivity implements PullToRefreshListView.OnRefreshListener2 {
    public static final int REQUEST_SEARCH = 1100;

    public String strSearch;
    public EditText edt_search;
    public ImageView iv_clean;
    public View headerView;
    public PullToRefreshListView refreshListView;
    public CommonSearchAdapter adapter;
    public PaginationX<T> paginationX = new PaginationX(20);
    public RelativeLayout headerViewBtn;
    public Subscription subscribe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
    }

    @Override
    public void setLayoutView() {
        setContentView(R.layout.activity_public_search);
    }

    @Override
    public void getPageData() {
        getData();
    }

    /**
     * 初始化
     */
    void initView() {
        ll_loading.setStatus(LoadingLayout.Success);
        findViewById(R.id.img_title_left).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        iv_clean = (ImageView) findViewById(R.id.iv_clean);
        iv_clean.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edt_search.setText("");
            }
        });

        edt_search = (EditText) findViewById(R.id.edt_search);
        edt_search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    doSearch();
                }
                return false;
            }
        });
        edt_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                //数据为空的时候，不显示数据
                if (TextUtils.isEmpty(editable.toString())) {
                    //不再处理后续网络请求的返回
                    if (null != subscribe) {
                        subscribe.unsubscribe();
                        subscribe = null;
                    }
                    paginationX.getRecords().clear();
                    adapter.notifyDataSetChanged();
                    ll_loading.setStatus(LoadingLayout.Success);
                } else {
                    doSearch();
                }
            }
        });
        edt_search.requestFocus();
        refreshListView = (PullToRefreshListView) findViewById(R.id.expandableListView_search);
        refreshListView.setMode(PullToRefreshBase.Mode.PULL_FROM_END);
        refreshListView.setOnRefreshListener(this);

        ListView listView = refreshListView.getRefreshableView();
        if (isShowHeadView()) {
            LayoutInflater mInflater = LayoutInflater.from(this);
            headerView = mInflater.inflate(R.layout.item_baseserach_null, null);
            headerViewBtn = (RelativeLayout) headerView.findViewById(R.id.item_baseserach_btn);
            listView.addHeaderView(headerView);
            //点击无的时候，直接关闭本页，返回空数据
            headerViewBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    app.finishActivity(BaseSearchActivity.this, MainApp.ENTER_TYPE_LEFT, RESULT_OK, new Intent());
                }
            });
        }

        adapter = new CommonSearchAdapter();
        listView.setAdapter(adapter);


//        /**列表监听器*/
//        refreshListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
//                Intent mIntent;
//                switch (befromPage) {
//                    //新建审批--
//                    case WFIN_ADD:
//                        mIntent = new Intent();
//                        mIntent.putExtra("data", paginationX.getRecords().get(position - 2));
//                        app.finishActivity(BaseSearchActivity.this, MainApp.ENTER_TYPE_LEFT, RESULT_OK, mIntent);
//                        break;
//                    //新建任务 所属项目－－
//                    case TASKS_ADD:
//                        mIntent = new Intent();
//                        mIntent.putExtra("data", paginationX.getRecords().get(position - 2));
//                        app.finishActivity(BaseSearchActivity.this, MainApp.ENTER_TYPE_LEFT, RESULT_OK, mIntent);
//                        break;
//                    //新建任务 关联客户
//                    case TASKS_ADD_CUSTOMER:
//                        returnData(position - 2);
//                        break;
//                    //新建拜访
//                    case SIGNIN_ADD:
//                        returnData(position - 2);
//                        break;
//                    //新建报告--
//                    case WORK_ADD:
//                        mIntent = new Intent();
//                        mIntent.putExtra("data", paginationX.getRecords().get(position - 2));
//                        app.finishActivity(BaseSearchActivity.this, MainApp.ENTER_TYPE_LEFT, RESULT_OK, mIntent);
//                        break;
//                    //客户管理--
//                    case CUSTOMER_MANAGE:
//                        mIntent = new Intent(getApplicationContext(), CustomerDetailInfoActivity_.class);
//                        mIntent.putExtra("Id", paginationX.getRecords().get(position - 2).getId());
//                        startActivity(mIntent);
//                        break;
//                    //任务管理--
//                    case TASKS_MANAGE:
//                        mIntent = new Intent(getApplicationContext(), TasksInfoActivity_.class);
//                        mIntent.putExtra(ExtraAndResult.EXTRA_ID, paginationX.getRecords().get(position - 2).getId());
//                        startActivity(mIntent);
//                        break;
//                    //工作报告管理--
//                    case WORK_MANAGE:
//                        mIntent = new Intent(getApplicationContext(), WorkReportsInfoActivity_.class);
//                        mIntent.putExtra(ExtraAndResult.EXTRA_ID, paginationX.getRecords().get(position - 2).getId());
//                        startActivity(mIntent);
//                        break;
//                    //项目管理--
//                    case PEOJECT_MANAGE:
//                        mIntent = new Intent(getApplicationContext(), ProjectInfoActivity_.class);
//                        mIntent.putExtra("projectId", paginationX.getRecords().get(position - 2).getId());
//                        startActivity(mIntent);
//                        break;
//                    //审批管理--
//                    case WFIN_MANAGE:
//                        mIntent = new Intent(getApplicationContext(), WfinstanceInfoActivity_.class);
//                        mIntent.putExtra(ExtraAndResult.EXTRA_ID, paginationX.getRecords().get(position - 2).getId());
//                        startActivity(mIntent);
//                        break;
//                    //线索管理--
//                    case CLUE_MANAGE:
//                        mIntent = new Intent(getApplicationContext(), ClueDetailActivity.class);
//                        mIntent.putExtra(ExtraAndResult.EXTRA_ID, paginationX.getRecords().get(position - 2).getId());
//                        startActivity(mIntent);
//                        break;
//                    // 跟进对象 客户 到新建跟进动态
//                    case DYNAMIC_MANAGE:
//                        mIntent = new Intent(getApplicationContext(), FollowAddActivity.class);
//                        mIntent.putExtra(ExtraAndResult.DYNAMIC_ADD_ACTION, ExtraAndResult.DYNAMIC_ADD_CUSTOMER);
//                        mIntent.putExtra(Customer.class.getName(), (Customer) (paginationX.getRecords().get(position - 2)));
//                        startActivity(mIntent);
//                        break;
//                }
//                hideInputKeyboard(edt_search);
//            }
//        });
        refreshListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (isShowHeadView()) {
                    onListItemClick(view, position - 2);
                } else {
                    onListItemClick(view, position - 1);
                }
            }
        });
    }
//    /**
//     * 根据业务 展示"无"Item
//     */
//    public void switchPage(int befromPage) {
//        switch (befromPage) {
//            case WFIN_ADD:
//                headerViewBtn.setVisibility(View.VISIBLE);
//                break;
//
//            case TASKS_ADD:
//                headerViewBtn.setVisibility(View.VISIBLE);
//                break;
//
//            case TASKS_ADD_CUSTOMER:
//                headerViewBtn.setVisibility(View.VISIBLE);
//                break;
//
//            case WORK_ADD:
//                headerViewBtn.setVisibility(View.VISIBLE);
//                break;
//        }
//    }


    /**
     * 搜索操作
     */
    public void doSearch() {
        strSearch = edt_search.getText().toString().trim();
        paginationX.setFirstPage();
        ll_loading.setStatus(LoadingLayout.Loading);
        getPageData();
    }


    @Override
    public void onPullDownToRefresh(PullToRefreshBase refreshView) {
        paginationX.setFirstPage();
        getPageData();

    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase refreshView) {
        getPageData();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != RESULT_OK) {
            return;
        }

        switch (requestCode) {
            case TaskManagerFragment.REQUEST_REVIEW:
                T t = (T) data.getSerializableExtra("review");
                if (t != null) {
                    returnBack = t;
                }
                break;
        }
    }

    T returnBack;

    @Override
    public void onBackPressed() {
        if (returnBack == null) {
            super.onBackPressed();
        } else {
            Intent intent = new Intent();
            intent.putExtra("review", returnBack);
            app.finishActivity(this, MainApp.ENTER_TYPE_LEFT, RESULT_OK, intent);
        }
    }


    /**
     * 请求成功的时候
     */
    public void success(PaginationX<T> data) {
        refreshListView.onRefreshComplete();
        paginationX.loadRecords(data);
        if (paginationX.isEnpty() && !isShowHeadView()) {
            ll_loading.setStatus(LoadingLayout.Empty);
        } else {
            ll_loading.setStatus(LoadingLayout.Success);
        }
        changeAdapter();
    }

    ;

    /**
     * 请求失败的时候
     *
     * @param e
     */
    public void fail(Throwable e) {
        refreshListView.onRefreshComplete();
        @LoyoErrorChecker.CheckType
        int type = paginationX.isEnpty() ? LoyoErrorChecker.LOADING_LAYOUT : LoyoErrorChecker.TOAST;
        LoyoErrorChecker.checkLoyoError(e, type, ll_loading);
    }

    protected void changeAdapter() {
        adapter.notifyDataSetChanged();
    }

    /**
     * 加载数据的方法，子类通过复写分方法，异步加载数据；
     * 加载以后，成功就调用＃success（）；
     * 失败就调用fail（）；
     * 注意，需要把网络请求的实例赋值给subscribe，否则无法取消请求
     */
    public abstract void getData();

    /**
     * 当item被点击的时候，子类复写，来决定怎么处理
     * 默认处理是把数据添加到intent里面，关闭当前页面，带回去
     */
    public void onListItemClick(View view, int position) {
        Intent intent = new Intent();
        intent.putExtra("data", adapter.getItem(position));
        setResult(RESULT_OK, intent);
        onBackPressed();
    }

    /**
     * 是否显示头部无的按钮,默认不显示，需要显示，直接复写本方法
     *
     * @return
     */
    public boolean isShowHeadView() {
        return false;
    }

    /**
     * 绑定数据
     *
     * @param viewHolder 试图
     * @param data       数据
     */
    public abstract void bindData(CommonSearchAdapter.SearchViewHolder viewHolder, T data);

    public class CommonSearchAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return paginationX.getRecords().size();
        }

        @Override
        public T getItem(int i) {
            return paginationX.getRecords().get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View convertView, ViewGroup viewGroup) {
            SearchViewHolder viewHolder = null;
            if (convertView == null) {
                convertView = LayoutInflater.from(mContext).inflate(R.layout.item_listview_common, null, false);
            } else {
                viewHolder = (SearchViewHolder) convertView.getTag();
            }
            if (null == viewHolder) {
                viewHolder = new SearchViewHolder();
                viewHolder.title = ViewHolder.get(convertView, R.id.tv_title);
                viewHolder.content = ViewHolder.get(convertView, R.id.tv_content);
                viewHolder.time = ViewHolder.get(convertView, R.id.tv_time);
                viewHolder.ack = ViewHolder.get(convertView, R.id.view_ack);
                viewHolder.layout_discuss = ViewHolder.get(convertView, R.id.layout_discuss);
                convertView.setTag(viewHolder);
            }
            T item = getItem(i);
            bindData(viewHolder, item);
            return convertView;

//            Object o=null;
//            TextView title = ViewHolder.get(convertView, R.id.tv_title);
//            TextView content = ViewHolder.get(convertView, R.id.tv_content);
//            TextView time = ViewHolder.get(convertView, R.id.tv_time);
//            View ack = ViewHolder.get(convertView, R.id.view_ack);
//            ViewGroup layout_discuss = ViewHolder.get(convertView, R.id.layout_discuss);
//            layout_discuss.setVisibility(View.GONE);
//            time.setVisibility(View.VISIBLE);
//            //审批
//            if (o instanceof WfInstanceRecord) {
//                WfInstanceRecord wfInstance = (WfInstanceRecord) o;
//                if (wfInstance.title != null) {
//                    title.setText(wfInstance.title);
//                }
//                time.setText("提交时间: " + DateTool.getDateTimeFriendly(wfInstance.createdAt));
//                if (null != wfInstance.nextExecutorName) {
//                    content.setText(String.format("申请人 %s", wfInstance.nextExecutorName));
//                }
//
//            }
//            //任务
//            else if (o instanceof TaskRecord) {
//                TaskRecord task = (TaskRecord) o;
//                try {
//                    if (task.planendAt == 0) {
//                        time.setText("任务截止时间: 无");
//                    } else {
//                        time.setText("任务截止时间: " + DateTool.getDateTimeFriendly(task.planendAt));
//                    }
//                } catch (Exception e) {
//                    Global.ProcException(e);
//                }
//                if (null != task.responsibleName) {
//                    content.setText("负责人: " + task.responsibleName);
//                }
//                if (!TextUtils.isEmpty(task.title)) {
//                    title.setText(task.title);
//                }
//            }
//            //报告
//            else if (o instanceof WorkReportRecord) {
//                final WorkReportRecord workReport = (WorkReportRecord) o;
//                if (null != workReport.reviewerName) {
//                    content.setText("点评: " + workReport.reviewerName);
//                }
//                StringBuilder reportTitle = new StringBuilder(workReport.title);
//                String reportType = "";
//                switch (workReport.type) {
//                    case WorkReport.DAY:
//                        reportType = " 日报";
//                        break;
//                    case WorkReport.WEEK:
//                        reportType = " 周报";
//                        break;
//                    case WorkReport.MONTH:
//                        reportType = " 月报";
//                        break;
//                }
//                reportTitle.append(reportType);
//                if (workReport.isDelayed) {
//                    reportTitle.append(" (补签)");
//                }
//                title.setText(reportTitle);
//                String end = "提交时间: " + DateTool.getDateTimeFriendly(workReport.createdAt);
//                time.setText(end);
//
//            }
//            //项目
//            else if (o instanceof Project) {
//                Project project = (Project) o;
//                try {
//                    time.setText("提交时间: " + DateTool.getDateTimeFriendly(project.getCreatedAt() / 1000));
//                } catch (Exception e) {
//                    Global.ProcException(e);
//                }
//
//                content.setText(project.content);
//                ack.setVisibility(View.GONE);
//                title.setText(project.title);
//            }
//            //客户
//            else if (o instanceof Customer) {
//
//                customer = (Customer) o;
//                time.setText("跟进时间：" + com.loyo.oa.common.utils.DateTool.getDateTimeFriendly(customer.lastActAt));
//                title.setText(customer.name);
//                content.setText("标签:" + Utils.getTagItems(customer));
//
//            } else if (o instanceof ClueListItem) {
//
//                ClueListItem clueListItem = (ClueListItem) o;
//                if (clueListItem.lastActAt == 0) {
//                    time.setText("--");
//                } else {
//                    time.setText("跟进时间：" + com.loyo.oa.common.utils.DateTool.getDateFriendly(clueListItem.lastActAt));
//                }
//                title.setText(clueListItem.name);
//                content.setText("公司名称" + clueListItem.companyName);
//
//            }
//            return convertView;
        }

        public class SearchViewHolder {
            public TextView title;
            public TextView content;
            public TextView time;
            public View ack;
            public ViewGroup layout_discuss;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        hideInputKeyboard(edt_search);
    }
}