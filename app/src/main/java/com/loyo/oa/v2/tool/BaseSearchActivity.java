package com.loyo.oa.v2.tool;

import android.content.Context;
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
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.loyo.oa.common.utils.DateTool;
import com.library.module.widget.loading.LoadingLayout;
import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.clue.ClueDetailActivity;
import com.loyo.oa.v2.activityui.clue.model.ClueListItem;
import com.loyo.oa.v2.activityui.followup.FollowAddActivity;
import com.loyo.oa.v2.beans.TaskRecord;
import com.loyo.oa.v2.beans.WfInstanceRecord;
import com.loyo.oa.v2.beans.WorkReportRecord;
import com.loyo.oa.v2.activityui.customer.CustomerDetailInfoActivity_;
import com.loyo.oa.v2.activityui.project.ProjectInfoActivity_;
import com.loyo.oa.v2.activityui.tasks.TasksInfoActivity_;
import com.loyo.oa.v2.activityui.wfinstance.WfinstanceInfoActivity_;
import com.loyo.oa.v2.activityui.work.WorkReportsInfoActivity_;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.beans.BaseBeans;
import com.loyo.oa.v2.activityui.customer.model.Customer;
import com.loyo.oa.v2.beans.PaginationX;
import com.loyo.oa.v2.beans.Project;
import com.loyo.oa.v2.beans.WorkReport;
import com.loyo.oa.v2.common.ExtraAndResult;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.common.http.HttpErrorCheck;
import com.loyo.oa.v2.activityui.tasks.fragment.TaskManagerFragment;
import com.loyo.oa.pulltorefresh.PullToRefreshBase;
import com.loyo.oa.pulltorefresh.PullToRefreshListView;
import com.loyo.oa.v2.network.LoyoErrorChecker;

import java.util.ArrayList;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * 搜索的 基类
 * 客户搜索；
 * 项目搜索；
 * 拜访搜索（新建拜访签到－选择客户－搜索）；
 * 任务搜索；
 * 工单搜索；
 * 审批搜索；
 * 等等。
 * @param <T>
 */
public class BaseSearchActivity<T extends BaseBeans> extends BaseLoadingActivity implements PullToRefreshListView.OnRefreshListener2{
    public static final int REQUEST_SEARCH = 1100;

    protected String strSearch;
    protected EditText edt_search;
    private ImageView iv_clean;
    protected View headerView;
    protected PullToRefreshListView expandableListView_search;
    protected CommonSearchAdapter adapter;
    protected PaginationX<T> paginationX = new PaginationX(20);
    public Customer customer;
    public RelativeLayout headerViewBtn;
    public Bundle mBundle;//子类在用
    protected int customerType;
    protected int befromPage;


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
        if (befromPage == SIGNIN_ADD || befromPage == TASKS_ADD || befromPage == TASKS_ADD_CUSTOMER ||
                befromPage == WFIN_ADD || befromPage == WORK_ADD || befromPage == DYNAMIC_MANAGE) {
            getData();
        } else {
            ll_loading.setStatus(LoadingLayout.Success);
        }
    }

    /**
     * 初始化
     */
    void initView() {
        mBundle = getIntent().getExtras();
        LayoutInflater mInflater = LayoutInflater.from(this);
        headerView = mInflater.inflate(R.layout.item_baseserach_null, null);
        headerViewBtn = (RelativeLayout) headerView.findViewById(R.id.item_baseserach_btn);

        LogUtil.dee("customerType:" + customerType);
        customerType = mBundle.getInt(ExtraAndResult.EXTRA_TYPE);
        befromPage = mBundle.getInt("from");
        switchPage(befromPage);
        paginationX.setFirstPage();
        getPageData();

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
//        tv_search = (TextView) findViewById(R.id.tv_search);
//        tv_search.setText("取消");

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
                doSearch();
            }
        });
        edt_search.requestFocus();
        expandableListView_search = (PullToRefreshListView) findViewById(R.id.expandableListView_search);
        expandableListView_search.setMode(PullToRefreshBase.Mode.PULL_FROM_END);
        expandableListView_search.setOnRefreshListener(this);

        ListView expandableListView = expandableListView_search.getRefreshableView();
        adapter = new CommonSearchAdapter();
        expandableListView.setAdapter(adapter);
        expandableListView.addHeaderView(headerView);

        /**列表监听器*/
        expandableListView_search.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Intent mIntent;
                switch (befromPage) {
                    //新建审批
                    case WFIN_ADD:
                        mIntent = new Intent();
                        mIntent.putExtra("data", paginationX.getRecords().get(position - 2));
                        app.finishActivity(BaseSearchActivity.this, MainApp.ENTER_TYPE_LEFT, RESULT_OK, mIntent);
                        break;
                    //新建任务 所属项目
                    case TASKS_ADD:
                        mIntent = new Intent();
                        mIntent.putExtra("data", paginationX.getRecords().get(position - 2));
                        app.finishActivity(BaseSearchActivity.this, MainApp.ENTER_TYPE_LEFT, RESULT_OK, mIntent);
                        break;
                    //新建任务 关联客户
                    case TASKS_ADD_CUSTOMER:
                        returnData(position - 2);
                        break;
                    //新建拜访
                    case SIGNIN_ADD:
                        returnData(position - 2);
                        break;
                    //新建报告
                    case WORK_ADD:
                        mIntent = new Intent();
                        mIntent.putExtra("data", paginationX.getRecords().get(position - 2));
                        app.finishActivity(BaseSearchActivity.this, MainApp.ENTER_TYPE_LEFT, RESULT_OK, mIntent);
                        break;
                    //客户管理
                    case CUSTOMER_MANAGE:
                        mIntent = new Intent(getApplicationContext(), CustomerDetailInfoActivity_.class);
                        mIntent.putExtra("Id", paginationX.getRecords().get(position - 2).getId());
                        startActivity(mIntent);
                        break;
                    //任务管理
                    case TASKS_MANAGE:
                        mIntent = new Intent(getApplicationContext(), TasksInfoActivity_.class);
                        mIntent.putExtra(ExtraAndResult.EXTRA_ID, paginationX.getRecords().get(position - 2).getId());
                        startActivity(mIntent);
                        break;
                    //工作报告管理
                    case WORK_MANAGE:
                        mIntent = new Intent(getApplicationContext(), WorkReportsInfoActivity_.class);
                        mIntent.putExtra(ExtraAndResult.EXTRA_ID, paginationX.getRecords().get(position - 2).getId());
                        startActivity(mIntent);
                        break;
                    //项目管理
                    case PEOJECT_MANAGE:
                        mIntent = new Intent(getApplicationContext(), ProjectInfoActivity_.class);
                        mIntent.putExtra("projectId", paginationX.getRecords().get(position - 2).getId());
                        startActivity(mIntent);
                        break;
                    //审批管理
                    case WFIN_MANAGE:
                        mIntent = new Intent(getApplicationContext(), WfinstanceInfoActivity_.class);
                        mIntent.putExtra(ExtraAndResult.EXTRA_ID, paginationX.getRecords().get(position - 2).getId());
                        startActivity(mIntent);
                        break;
                    //线索管理
                    case CLUE_MANAGE:
                        mIntent = new Intent(getApplicationContext(), ClueDetailActivity.class);
                        mIntent.putExtra(ExtraAndResult.EXTRA_ID, paginationX.getRecords().get(position - 2).getId());
                        startActivity(mIntent);
                        break;
                    // 跟进对象 客户 到新建跟进动态
                    case DYNAMIC_MANAGE:
                        mIntent = new Intent(getApplicationContext(), FollowAddActivity.class);
                        mIntent.putExtra(ExtraAndResult.DYNAMIC_ADD_ACTION, ExtraAndResult.DYNAMIC_ADD_CUSTOMER);
                        mIntent.putExtra(Customer.class.getName(), (Customer) (paginationX.getRecords().get(position - 2)));
                        startActivity(mIntent);
                        break;
                }
                hideInputKeyboard(edt_search);
            }
        });

        /**
         * 返回"无"
         * */
        headerViewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                app.finishActivity(BaseSearchActivity.this, MainApp.ENTER_TYPE_LEFT, RESULT_OK, new Intent());
            }
        });
    }

    /**
     * 搜索操作
     */
    public void doSearch() {
        strSearch = edt_search.getText().toString().trim();
        paginationX.setFirstPage();
        getData();
    }

    /**
     * 根据业务 展示"无"Item
     */
    public void switchPage(int befromPage) {
        switch (befromPage) {
            case WFIN_ADD:
                headerViewBtn.setVisibility(View.VISIBLE);
                break;

            case TASKS_ADD:
                headerViewBtn.setVisibility(View.VISIBLE);
                break;

            case TASKS_ADD_CUSTOMER:
                headerViewBtn.setVisibility(View.VISIBLE);
                break;

            case WORK_ADD:
                headerViewBtn.setVisibility(View.VISIBLE);
                break;
        }
    }

    @Override
    public void onPullDownToRefresh(PullToRefreshBase refreshView) {
        paginationX.setFirstPage();
        getData();
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase refreshView) {
        getData();
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


    protected void returnData(int position) {
        Intent intent = new Intent();
        intent.putExtra("data", adapter.getItem(position));
        setResult(RESULT_OK, intent);
        onBackPressed();
    }

    /**
     * 请求成功的时候
     */
    public void success(PaginationX<T> data){
        expandableListView_search.onRefreshComplete();
        paginationX.loadRecords(data);
        if(paginationX.isEnpty()){
            ll_loading.setStatus(LoadingLayout.Empty);
        }else{
            ll_loading.setStatus(LoadingLayout.Success);
        }
        changeAdapter();
    };

    /**
     *  请求失败的时候
     * @param e
     */
    public void fail(Throwable e){
        expandableListView_search.onRefreshComplete();
        @LoyoErrorChecker.CheckType
        int type = paginationX.isEnpty() ? LoyoErrorChecker.LOADING_LAYOUT : LoyoErrorChecker.TOAST;
        LoyoErrorChecker.checkLoyoError(e, type, ll_loading);
    }


    protected void changeAdapter() {
        adapter.notifyDataSetChanged();
    }

    public void getData() {
    }

    public class CommonSearchAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return paginationX.getRecords().size();
        }

        @Override
        public BaseBeans getItem(int i) {
            return paginationX.getRecords().get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View convertView, ViewGroup viewGroup) {
            if (convertView == null) {
                convertView = LayoutInflater.from(mContext).inflate(R.layout.item_listview_common, null, false);
            }
            Object o = getItem(i);
            TextView title = ViewHolder.get(convertView, R.id.tv_title);
            TextView content = ViewHolder.get(convertView, R.id.tv_content);
            TextView time = ViewHolder.get(convertView, R.id.tv_time);
            View ack = ViewHolder.get(convertView, R.id.view_ack);
            ViewGroup layout_discuss = ViewHolder.get(convertView, R.id.layout_discuss);
            layout_discuss.setVisibility(View.GONE);
            time.setVisibility(View.VISIBLE);
            //审批
            if (o instanceof WfInstanceRecord) {
                WfInstanceRecord wfInstance = (WfInstanceRecord) o;
                if (wfInstance.title != null) {
                    title.setText(wfInstance.title);
                }
//                time.setText("提交时间: " + app.df3.format(new Date(wfInstance.createdAt * 1000)));
                time.setText("提交时间: " + DateTool.getDateTimeFriendly(wfInstance.createdAt));
                if (null != wfInstance.nextExecutorName) {
                    content.setText(String.format("申请人 %s", wfInstance.nextExecutorName));
                }
                //ack.setVisibility(wfInstance.isAck() ? View.GONE : View.VISIBLE);

            }
            //任务
            else if (o instanceof TaskRecord) {
                TaskRecord task = (TaskRecord) o;
                try {
                    if (task.planendAt == 0) {
                        time.setText("任务截止时间: 无");
                    } else {
//                        time.setText("任务截止时间: " + MainApp.getMainApp().df3.format(new Date(task.planendAt * 1000)) + "");
                        time.setText("任务截止时间: " + DateTool.getDateTimeFriendly(task.planendAt));
                    }
                } catch (Exception e) {
                    Global.ProcException(e);
                }
                //ack.setVisibility(task.isAck() ? View.GONE : View.VISIBLE);
                if (null != task.responsibleName) {
                    content.setText("负责人: " + task.responsibleName);
                }
                if (!TextUtils.isEmpty(task.title)) {
                    title.setText(task.title);
                }

            }
            //报告
            else if (o instanceof WorkReportRecord) {
                final WorkReportRecord workReport = (WorkReportRecord) o;
                if (null != workReport.reviewerName) {
                    content.setText("点评: " + workReport.reviewerName);
                }
                StringBuilder reportTitle = new StringBuilder(workReport.title);
                String reportType = "";
                switch (workReport.type) {
                    case WorkReport.DAY:
                        reportType = " 日报";
                        break;
                    case WorkReport.WEEK:
                        reportType = " 周报";
                        break;
                    case WorkReport.MONTH:
                        reportType = " 月报";
                        break;
                }
                reportTitle.append(reportType);
                if (workReport.isDelayed) {
                    reportTitle.append(" (补签)");
                }
                title.setText(reportTitle);
//                String end = "提交时间: " + app.df3.format(new Date(workReport.createdAt * 1000));
                String end = "提交时间: " + DateTool.getDateTimeFriendly(workReport.createdAt);
                time.setText(end);
                //ack.setVisibility(workReport.isAck() ? View.GONE : View.VISIBLE);

            }
            //项目
            else if (o instanceof Project) {
                Project project = (Project) o;
                try {
//                    time.setText("提交时间: " + app.df9.format(new Date(project.getCreatedAt())));
                    time.setText("提交时间: " + DateTool.getDateTimeFriendly(project.getCreatedAt() / 1000));
                } catch (Exception e) {
                    Global.ProcException(e);
                }

                content.setText(project.content);
                ack.setVisibility(View.GONE);
                title.setText(project.title);
            }
            //客户
            else if (o instanceof Customer) {

                customer = (Customer) o;
//                time.setText("跟进时间：" + app.df3.format(new Date(customer.lastActAt * 1000)));
                time.setText("跟进时间：" + com.loyo.oa.common.utils.DateTool.getDateTimeFriendly(customer.lastActAt));
                title.setText(customer.name);
                content.setText("标签:" + Utils.getTagItems(customer));

                /*if (!TextUtils.isEmpty(customer.distance)) {
                    content.setText("距离：" + customer.distance);
                } else {
                    content.setVisibility(View.GONE);
                }*/
            } else if (o instanceof ClueListItem) {

                ClueListItem clueListItem = (ClueListItem) o;
                if (clueListItem.lastActAt == 0) {
                    time.setText("--");
                } else {
//                    time.setText("跟进时间：" + DateTool.timet(clueListItem.lastActAt + "", "yyyy-MM-dd"));
                    time.setText("跟进时间：" + com.loyo.oa.common.utils.DateTool.getDateFriendly(clueListItem.lastActAt));
                }
                title.setText(clueListItem.name);
                content.setText("公司名称" + clueListItem.companyName);

            }
            return convertView;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        hideInputKeyboard(edt_search);
    }
}