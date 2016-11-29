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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.loyo.oa.pulltorefresh.PullToRefreshBase;
import com.loyo.oa.pulltorefresh.PullToRefreshListView;
import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.worksheet.bean.WorksheetOrder;
import com.loyo.oa.v2.activityui.worksheet.bean.WorksheetOrderListWrapper;
import com.loyo.oa.v2.activityui.worksheet.common.WorksheetListType;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.common.ExtraAndResult;
import com.loyo.oa.v2.common.http.HttpErrorCheck;
import com.loyo.oa.v2.point.IWorksheet;
import com.loyo.oa.v2.tool.BaseActivity;
import com.loyo.oa.v2.tool.Config_project;
import com.loyo.oa.v2.tool.RestAdapterFactory;
import com.loyo.oa.v2.tool.Utils;

import java.util.ArrayList;
import java.util.HashMap;
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

                WorksheetOrder order = (WorksheetOrder) adapter.getItem(position-1);
                Intent intent = new Intent();
                intent.putExtra(ExtraAndResult.EXTRA_OBJ, order);
                app.finishActivity(WSOrderSelectActivity.this, MainApp.ENTER_TYPE_LEFT, 0, intent);
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

        if (listData == null) {
            listData = new ArrayList<WorksheetOrder>();
        }

        if (isPullDown) {
            listData.clear();
        }

        if (list != null) {
            listData.addAll(list);
        }

//        listData.addAll(WorksheetOrder.testData());
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
            return listData.get(position);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup viewGroup) {
            WorksheetOrder order = listData.get(position);
            Holder holder = null;
            if (convertView == null) {
                convertView = LayoutInflater.from(WSOrderSelectActivity.this).inflate(R.layout.item_worksheet_order, null);
                holder = new Holder();
                holder.tv_title = (TextView) convertView.findViewById(R.id.tv_title);
                holder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
                holder.tv_money = (TextView) convertView.findViewById(R.id.tv_money);
                holder.tv_customer = (TextView) convertView.findViewById(R.id.tv_customer);
                holder.tv_product = (TextView) convertView.findViewById(R.id.tv_product);
                convertView.setTag(holder);
            } else {
                holder = (Holder) convertView.getTag();
            }

            holder.tv_title.setText(order.title);
            holder.tv_money.setText(Utils.setValueDouble(order.dealMoney));
            holder.tv_customer.setText(order.customerName);
            holder.tv_product.setText(order.proName);
            return convertView;
        }

        class Holder {
            TextView tv_title, tv_name, tv_money, tv_customer, tv_product;
        }
    }
}
