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
import android.widget.TextView;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activity.customer.CustomerDetailInfoActivity_;
import com.loyo.oa.v2.activity.ProjectInfoActivity_;
import com.loyo.oa.v2.activity.WorkReportsInfoActivity_;
import com.loyo.oa.v2.activity.tasks.TasksInfoActivity_;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.beans.BaseBeans;
import com.loyo.oa.v2.beans.Customer;
import com.loyo.oa.v2.beans.PaginationX;
import com.loyo.oa.v2.beans.Project;
import com.loyo.oa.v2.beans.Task;
import com.loyo.oa.v2.beans.WfInstance;
import com.loyo.oa.v2.beans.WorkReport;
import com.loyo.oa.v2.common.ExtraAndResult;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.common.http.HttpErrorCheck;
import com.loyo.oa.v2.fragment.TaskManagerFragment;
import com.loyo.oa.v2.tool.customview.pullToRefresh.PullToRefreshBase;
import com.loyo.oa.v2.tool.customview.pullToRefresh.PullToRefreshListView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * 搜索的 基类
 *
 * @param <T>
 */
public abstract class BaseSearchActivity<T extends BaseBeans> extends BaseActivity implements PullToRefreshListView.OnRefreshListener2, Callback {
    public static final int REQUEST_SEARCH = 1100;

    protected String strSearch;
    protected EditText edt_search;
    protected TextView tv_search;
    protected View vs_nodata;
    protected PullToRefreshListView expandableListView_search;
    protected ArrayList<T> lstData = new ArrayList<>();
    protected CommonSearchAdapter adapter;
    protected boolean isTopAdd = true;
    protected PaginationX paginationX = new PaginationX(20);
    protected int befromPage;
    public Customer customer;
    public Bundle mBundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_public_search);
        vs_nodata = findViewById(R.id.vs_nodata);
        mBundle = getIntent().getExtras();

        befromPage = mBundle.getInt("from");
        if (befromPage == SIGNIN_ADD || befromPage == TASKS_ADD || befromPage == TASKS_ADD_CUSTOMER ||
                befromPage == WFIN_ADD || befromPage == WORK_ADD) {
            getData();
        }

        findViewById(R.id.img_title_left).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        tv_search = (TextView) findViewById(R.id.tv_search);
        tv_search.setText("取消");

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
                if (edt_search.length() == 0) {
                    tv_search.setText("取消");
                } else {
                    tv_search.setText("搜索");
                }
            }
        });
        edt_search.requestFocus();

        findViewById(R.id.tv_search).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doSearch();
            }
        });

        expandableListView_search = (PullToRefreshListView) findViewById(R.id.expandableListView_search);
        expandableListView_search.setMode(PullToRefreshBase.Mode.PULL_FROM_END);
        expandableListView_search.setOnRefreshListener(this);

        ListView expandableListView = expandableListView_search.getRefreshableView();
        adapter = new CommonSearchAdapter();
        expandableListView_search.setAdapter(adapter);


        /**列表监听器*/
        expandableListView_search.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                Intent mIntent;

                switch (befromPage) {
                    //新建审批
                    case WFIN_ADD:
                        mIntent = new Intent();
                        mIntent.putExtra("data", lstData.get(position - 1));
                        app.finishActivity(BaseSearchActivity.this, MainApp.ENTER_TYPE_TOP, RESULT_OK, mIntent);
                        break;
                    //新建任务 所属项目
                    case TASKS_ADD:
                        mIntent = new Intent();
                        mIntent.putExtra("data", lstData.get(position - 1));
                        app.finishActivity(BaseSearchActivity.this, MainApp.ENTER_TYPE_TOP, RESULT_OK, mIntent);
                        break;
                    //新建任务 关联客户
                    case TASKS_ADD_CUSTOMER:
                        returnData(position - 1);
                        break;
                    //新建拜访
                    case SIGNIN_ADD:
                        returnData(position - 1);
                        break;
                    //新建报告
                    case WORK_ADD:
                        mIntent = new Intent();
                        mIntent.putExtra("data", lstData.get(position - 1));
                        app.finishActivity(BaseSearchActivity.this, MainApp.ENTER_TYPE_TOP, RESULT_OK, mIntent);
                        break;
                    //客户管理
                    case CUSTOMER_MANAGE:
                        mIntent = new Intent(getApplicationContext(), CustomerDetailInfoActivity_.class);
                        mIntent.putExtra("Id", lstData.get(position - 1).getId());
                        startActivity(mIntent);
                        break;
                    //任务管理
                    case TASKS_MANAGE:
                        mIntent = new Intent(getApplicationContext(), TasksInfoActivity_.class);
                        mIntent.putExtra(ExtraAndResult.EXTRA_ID, lstData.get(position - 1).getId());
                        startActivity(mIntent);
                        break;
                    //工作报告管理
                    case WORK_MANAGE:
                        mIntent = new Intent(getApplicationContext(), WorkReportsInfoActivity_.class);
                        mIntent.putExtra(ExtraAndResult.EXTRA_ID, lstData.get(position - 1).getId());
                        startActivity(mIntent);
                        break;
                    //项目管理
                    case PEOJECT_MANAGE:
                        mIntent = new Intent(getApplicationContext(), ProjectInfoActivity_.class);
                        mIntent.putExtra("projectId", lstData.get(position - 1).getId());
                        startActivity(mIntent);
                        break;
                    //审批管理
                    case WFIN_MANAGE:
                        mIntent = new Intent(getApplicationContext(), ProjectInfoActivity_.class);
                        mIntent.putExtra("projectId", lstData.get(position - 1).getId());
                        startActivity(mIntent);
                        break;

                }
            }
        });
    }


    void doSearch() {
        strSearch = edt_search.getText().toString().trim();
        if (strSearch.length() > 0) {
            isTopAdd = true;
            getData();
        } else {
            onBackPressed();
        }
    }

    @Override
    public void onPullDownToRefresh(PullToRefreshBase refreshView) {
        isTopAdd = true;
        paginationX.setPageIndex(1);
        getData();
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase refreshView) {
        isTopAdd = false;
        paginationX.setPageIndex(paginationX.getPageIndex() + 1);
        getData();
    }

    void showNoData() {
        vs_nodata.setVisibility(View.VISIBLE);
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


    @Override
    public void success(Object o, Response response) {

        LogUtil.dll("URL:" + response.getUrl());
        try {
            LogUtil.dll("搜索result success:" + Utils.convertStreamToString(response.getBody().in()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        expandableListView_search.onRefreshComplete();
        InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(edt_search.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

        if (null == o) {
            if (isTopAdd) {
                showNoData();
            } else {
                Toast("没有更多数据!");
            }
            return;
        }

        paginationX = (PaginationX) o;
        ArrayList<T> lstDataTemp = paginationX.getRecords();

        if (lstDataTemp == null || lstDataTemp.size() == 0) {
            if (isTopAdd) {
                showNoData();
            } else {
                Toast("没有更多数据!");
            }
            return;
        } else {
            if (isTopAdd) {
                lstData.clear();
            }
            lstData.addAll(lstDataTemp);
        }
        vs_nodata.setVisibility(View.GONE);
        changeAdapter();

    }


    @Override
    public void failure(RetrofitError error) {
        HttpErrorCheck.checkError(error);
    }

    protected void changeAdapter() {
        adapter.notifyDataSetChanged();
    }

    protected abstract void openDetail(int position);

    public abstract void getData();

    public class CommonSearchAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return lstData.size();
        }

        @Override
        public BaseBeans getItem(int i) {
            return lstData.get(i);
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

            ImageView status = ViewHolder.get(convertView, R.id.img_status);
            TextView title = ViewHolder.get(convertView, R.id.tv_title);
            TextView content = ViewHolder.get(convertView, R.id.tv_content);
            TextView time = ViewHolder.get(convertView, R.id.tv_time);
            View ack = ViewHolder.get(convertView, R.id.view_ack);
            ViewGroup layout_discuss = ViewHolder.get(convertView, R.id.layout_discuss);
            status.setVisibility(View.GONE);
            layout_discuss.setVisibility(View.GONE);
            time.setVisibility(View.VISIBLE);
            //审批
            if (o instanceof WfInstance) {
                WfInstance wfInstance = (WfInstance) o;
                if (wfInstance.title != null) {
                    title.setText(wfInstance.title);
                }
                time.setText("提交时间: " + app.df3.format(new Date(wfInstance.createdAt * 1000)));
                if (wfInstance.creator != null) {
                    content.setText(String.format("申请人 %s", wfInstance.creator.getRealname()));
                }
                //                ack.setVisibility(wfInstance.isAck() ? View.GONE : View.VISIBLE);

                switch (wfInstance.status) {
                    case WfInstance.STATUS_NEW:
                        status.setImageResource(R.drawable.img_wfinstance_list_status1);
                        break;
                    case WfInstance.STATUS_PROCESSING:
                        status.setImageResource(R.drawable.img_wfinstance_list_status2);
                        break;
                    case WfInstance.STATUS_ABORT:
                        status.setImageResource(R.drawable.img_wfinstance_list_status3);
                        break;
                    case WfInstance.STATUS_APPROVED:
                        status.setImageResource(R.drawable.img_wfinstance_list_status4);
                        break;
                    case WfInstance.STATUS_FINISHED:
                        status.setImageResource(R.drawable.img_wfinstance_list_status5);
                        break;
                }
            }
            //任务
            else if (o instanceof Task) {
                Task task = (Task) o;
                if (task.getStatus() == Task.STATUS_PROCESSING) {
                    status.setImageResource(R.drawable.task_status_1);
                } else if (task.getStatus() == Task.STATUS_REVIEWING) {
                    status.setImageResource(R.drawable.task_status_2);
                } else if (task.getStatus() == Task.STATUS_FINISHED) {
                    status.setImageResource(R.drawable.task_status_3);
                }

                try {
                    //                time.setText("任务截止时间: " + DateTool.formateServerDate(task.getCreatedAt(), app.df3));
                    time.setText("任务截止时间: " + app.df3.format(new Date(task.getCreatedAt())));
                } catch (Exception e) {
                    Global.ProcException(e);
                }
                //                ack.setVisibility(task.isAck() ? View.GONE : View.VISIBLE);
                if (null != task.getResponsiblePerson() && !TextUtils.isEmpty(task.getResponsiblePerson().getRealname())) {
                    content.setText("负责: " + task.getResponsiblePerson().getRealname());
                }
                if (!TextUtils.isEmpty(task.getTitle())) {
                    title.setText(task.getTitle());
                }

            }
            //报告
            else if (o instanceof WorkReport) {
                final WorkReport workReport = (WorkReport) o;
                if (null != workReport.getReviewer() && null != workReport.getReviewer().getUser() && !TextUtils.isEmpty(workReport.getReviewer().getUser().getName())) {
                    content.setText("点评: " + workReport.getReviewer().getUser().getName());
                }
                StringBuilder reportTitle = new StringBuilder(workReport.getCreator().name + "提交 ");
                String reportDate = "";
                String reportType = "";
                switch (workReport.getType()) {
                    case WorkReport.DAY:
                        reportType = " 日报";
                        reportDate = app.df4.format(new Date(workReport.getBeginAt() * 1000));
                        break;
                    case WorkReport.WEEK:
                        reportType = " 周报";
                        reportDate = app.df4.format(new Date(workReport.getBeginAt() * 1000)) + "-" + app.df4.format(new Date(workReport.getEndAt() * 1000));
                        break;
                    case WorkReport.MONTH:
                        reportType = " 月报";
                        reportDate = DateTool.toDateStr(workReport.getBeginAt() * 1000, "yyyy.MM");
                        ;
                        break;
                }
                reportTitle.append(reportDate + reportType);
                if (workReport.isDelayed()) {
                    reportTitle.append(" (补签)");
                }

                title.setText(reportTitle);

                String end = "提交时间: " + app.df3.format(new Date(workReport.getCreatedAt() * 1000));
                time.setText(end);
                //                ack.setVisibility(workReport.isAck() ? View.GONE : View.VISIBLE);
                status.setImageResource(workReport.isReviewed() ? R.drawable.img_workreport_list_status2 : R.drawable.img_workreport_list_status1);

            }
            //项目
            else if (o instanceof Project) {
                Project project = (Project) o;
                if (project.status == 1) {
                    status.setImageResource(R.drawable.task_status_1);
                } else {
                    status.setImageResource(R.drawable.img_project_complete);
                }

                try {
                    time.setText("提交时间: " + app.df9.format(new Date(project.getCreatedAt())));
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
                time.setText("跟进时间：" + app.df3.format(new Date(customer.lastActAt * 1000)));
                title.setText(customer.name);
                content.setText("标签"+Utils.getTagItems(customer));

              /*  if (!TextUtils.isEmpty(customer.distance)) {
                    content.setText("距离：" + customer.distance);
                } else {
                    content.setVisibility(View.GONE);
                }*/
            }

            return convertView;
        }
    }
}