package com.loyo.oa.v2.activityui.worksheet;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.clue.ClueDetailActivity;
import com.loyo.oa.v2.activityui.clue.bean.ClueListItem;
import com.loyo.oa.v2.activityui.worksheet.adapter.BaseGroupsDataAdapter;
import com.loyo.oa.v2.activityui.worksheet.adapter.ResponsableWorksheetsAdapter;
import com.loyo.oa.v2.activityui.worksheet.adapter.WorksheetListAdapter;
import com.loyo.oa.v2.activityui.worksheet.bean.Worksheet;
import com.loyo.oa.v2.activityui.worksheet.bean.WorksheetEvent;
import com.loyo.oa.v2.activityui.worksheet.bean.WorksheetEventListWrapper;
import com.loyo.oa.v2.activityui.worksheet.bean.WorksheetListWrapper;
import com.loyo.oa.v2.activityui.worksheet.bean.WorksheetOrder;
import com.loyo.oa.v2.activityui.worksheet.bean.WorksheetOrderListWrapper;
import com.loyo.oa.v2.activityui.worksheet.common.GroupsData;
import com.loyo.oa.v2.activityui.worksheet.common.WorksheetListType;
import com.loyo.oa.v2.common.ExtraAndResult;
import com.loyo.oa.v2.common.http.HttpErrorCheck;
import com.loyo.oa.v2.customview.pullToRefresh.PullToRefreshBase;
import com.loyo.oa.v2.customview.pullToRefresh.PullToRefreshExpandableListView;
import com.loyo.oa.v2.customview.pullToRefresh.PullToRefreshListView;
import com.loyo.oa.v2.point.IWorksheet;
import com.loyo.oa.v2.tool.BaseActivity;
import com.loyo.oa.v2.tool.Config_project;
import com.loyo.oa.v2.tool.DateTool;
import com.loyo.oa.v2.tool.RestAdapterFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * 【订单选择】
 */

public class WSOrderSelectActivity extends BaseActivity implements PullToRefreshBase.OnRefreshListener2 {

    private EditText edt_search;
    private ImageView iv_clean;
    private PullToRefreshListView pullToRefreshListView;
    private ViewStub emptyView;

    private WorksheetListType searchType;
    private int page = 1;
    private boolean isPullDown = true;
    private Bundle mBundle;
    private String strSearch;
    protected ArrayList<WorksheetOrder> listData;

    private OrderSearchAdapter adapter;

    private LayoutInflater mInflater;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_public_search); // activity_public_search
        initView();
        doSearch();

    }

    /**
     * 初始化
     */
    void initView() {
        mBundle = getIntent().getExtras();
        searchType = (WorksheetListType) mBundle.getSerializable(ExtraAndResult.EXTRA_TYPE);
        mInflater = LayoutInflater.from(this);
        emptyView = (ViewStub) findViewById(R.id.vs_nodata);

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
                doSearch();
            }
        });
        edt_search.requestFocus();

        pullToRefreshListView = (PullToRefreshListView)findViewById(R.id.expandableListView_search);
        pullToRefreshListView.setMode(PullToRefreshBase.Mode.PULL_FROM_END);
        pullToRefreshListView.setOnRefreshListener(this);
        pullToRefreshListView.setEmptyView(emptyView);

        ListView innerListView = pullToRefreshListView.getRefreshableView();
        adapter = new OrderSearchAdapter();

        innerListView.setAdapter(adapter);
        innerListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                hideInputKeyboard(edt_search);
            }
        });
    }

    /**
     * 搜索操作
     */
    public void doSearch() {
        strSearch = edt_search.getText().toString().trim();
        getData();
    }

    protected  void getData() {

        HashMap<String, Object> map = new HashMap<>();
        map.put("pageIndex", page);
        map.put("pageSize", 15);
        if (strSearch != null) {
            map.put("keyword", strSearch);
        }

        RestAdapterFactory.getInstance().build(Config_project.API_URL_CUSTOMER()).
                create(IWorksheet.class).getWorksheetOrdersList(map, new Callback<WorksheetOrderListWrapper>() {
            @Override
            public void success(WorksheetOrderListWrapper listWrapper, Response response) {
                pullToRefreshListView.onRefreshComplete();
                HttpErrorCheck.checkResponse("我的工单列表：", response);
                loadData(listWrapper.data.records);
                pullToRefreshListView.setEmptyView(emptyView);
            }

            @Override
            public void failure(RetrofitError error) {
                pullToRefreshListView.onRefreshComplete();
                HttpErrorCheck.checkError(error);
            }
        });
    }

    private void loadData(List<WorksheetOrder> list) {
    }

    @Override
    public void onPullDownToRefresh(PullToRefreshBase refreshView) {
        isPullDown = true;
        page = 1;
        getData();
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase refreshView) {
        isPullDown = false;
        page++;
        getData();
    }

    protected void changeAdapter() {
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        hideInputKeyboard(edt_search);
    }


    public class OrderSearchAdapter extends BaseAdapter {


        public void setAdapter() {
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return listData == null ? 0 : listData.size();
        }

        @Override
        public Object getItem(int position) {
            return 0;
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup viewGroup) {
//            ClueListItem clueListItem = listData.get(position);
//            Holder holder = null;
//            if (convertView == null) {
//                convertView = LayoutInflater.from(WSOrderSelectActivity.this).inflate(R.layout.item_teamclue, null);
//                holder = new Holder();
//                holder.tv_company_name = (TextView) convertView.findViewById(R.id.tv_company_name);
//                holder.tv_customer = (TextView) convertView.findViewById(R.id.tv_customer);
//                holder.tv_time = (TextView) convertView.findViewById(R.id.tv_time);
//                holder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
//                convertView.setTag(holder);
//            } else {
//                holder = (Holder) convertView.getTag();
//            }
//
//            holder.tv_name.setText(clueListItem.name);
//            holder.tv_company_name.setText(clueListItem.companyName);
//            holder.tv_customer.setText(clueListItem.name);
//            if (clueListItem.lastActAt != 0) {
//                holder.tv_time.setText(DateTool.timet(clueListItem.lastActAt + "", "yyyy-MM-dd HH:mm"));
//            } else {
//                holder.tv_time.setText("--");
//            }

            return convertView;
        }

        class Holder {
            TextView tv_company_name; /* 公司名称 */
            TextView tv_customer;     /* 负责人 */
            TextView tv_time;         /* 跟进时间 */
            TextView tv_name;         /* 客户名称 */
        }
    }
}
