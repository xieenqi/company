package com.loyo.oa.v2.activity.signin;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
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
import android.widget.TextView;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.beans.Customer;
import com.loyo.oa.v2.beans.PaginationX;
import com.loyo.oa.v2.common.FinalVariables;
import com.loyo.oa.v2.common.http.HttpErrorCheck;
import com.loyo.oa.v2.point.ICustomer;
import com.loyo.oa.v2.tool.BaseActivity;
import com.loyo.oa.v2.tool.LocationUtil;
import com.loyo.oa.v2.tool.LogUtil;
import com.loyo.oa.v2.tool.RestAdapterFactory;
import com.loyo.oa.v2.tool.Utils;
import com.loyo.oa.v2.tool.ViewHolder;
import com.loyo.oa.v2.tool.customview.pullToRefresh.PullToRefreshBase;
import com.loyo.oa.v2.tool.customview.pullToRefresh.PullToRefreshListView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * 新建拜访 客户选择
 * Created by yyy on 15/12/24.
 */
public class SigninSelectCustomer extends BaseActivity implements PullToRefreshListView.OnRefreshListener2 {

    protected String strSearch;
    protected EditText edt_search;
    protected TextView tv_search;
    protected View vs_nodata;
    protected PullToRefreshListView expandableListView_search;
    protected ArrayList<Customer> lstData = new ArrayList<>();
    protected CommonSearchAdapter adapter;
    protected boolean isTopAdd = true;
    protected PaginationX paginationX = new PaginationX(20);
    public Customer customer;
    public String position;
    public int kalo = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_public_search);
        vs_nodata = findViewById(R.id.vs_nodata);
        getNearCustomersInfo();

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

        adapter = new CommonSearchAdapter();
        expandableListView_search.setAdapter(adapter);

        /**列表监听器*/
        expandableListView_search.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                returnData(position-1);
            }
        });
    }


    /**
     * 获取附近客户数据
     */
        private void getNearCustomersInfo() {
            new LocationUtil(this, new LocationUtil.AfterLocation() {
                @Override
                public void OnLocationSucessed(String address, double longitude, double latitude, float radius) {
                    LogUtil.dll("附近");
                    position = String.valueOf(longitude).concat(",").concat(String.valueOf(latitude));
                    String url = FinalVariables.QUERY_NEAR_CUSTOMERS_SELF;
                    HashMap<String, Object> params = new HashMap<>();
                    params.put("pageIndex", paginationX.getPageIndex());
                    params.put("pageSize", isTopAdd ? lstData.size() >= 20 ? lstData.size() : 20 : 20);
                    params.put("position", position);
                    kalo = 0;
                    dataRequestvoid(url, params);
                }
                @Override
                public void OnLocationFailed() {
                    Toast("获取附近客户信息失败！");
                }
            });
        }

    /**
     * 查询客户数据
     * */
    void getAllData(){
        LogUtil.dll("查询");
        String url = FinalVariables.SEARCH_CUSTOMERS_SELF;
        HashMap<String, Object> params = new HashMap<>();
        kalo = 1;
        params.put("pageIndex", paginationX.getPageIndex());
        params.put("pageSize", isTopAdd ? lstData.size() >= 20 ? lstData.size() : 20 : 20);
        params.put("keyWords", strSearch);
        dataRequestvoid(url, params);
    }

    /**
     * 请求体
     * */
    void dataRequestvoid(String url,HashMap<String,Object> params){
        RestAdapterFactory.getInstance().build(url).create(ICustomer.class).query(params, new Callback<PaginationX<Customer>>() {
            @Override
            public void success(PaginationX<Customer> customerPaginationX, Response response) {

                LogUtil.dll("URL:" + response.getUrl());
                try {
                    LogUtil.dll("result success:" + Utils.convertStreamToString(response.getBody().in()));
                } catch (IOException e) {
                    e.printStackTrace();
                }

                expandableListView_search.onRefreshComplete();
                InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(edt_search.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

                if (null == customerPaginationX) {
                    if (isTopAdd) {
                        showNoData();
                    } else {
                        Toast("没有更多数据!");
                    }
                    return;
                }

                paginationX = customerPaginationX;
                ArrayList<Customer> lstDataTemp = paginationX.getRecords();

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
                expandableListView_search.onRefreshComplete();
            }
        });
    }

    void doSearch() {
        strSearch = edt_search.getText().toString().trim();
        if (strSearch.length() > 0) {
            isTopAdd = true;
            getAllData();
        } else {
            onBackPressed();
        }
    }


    @Override
    public void onPullDownToRefresh(PullToRefreshBase refreshView) {
        isTopAdd = true;
        paginationX.setPageIndex(1);
        if(kalo == 0){
            getNearCustomersInfo();
        }else{
            getAllData();
        }
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase refreshView) {
        isTopAdd = false;
        paginationX.setPageIndex(paginationX.getPageIndex() + 1);
        if(kalo == 0){
            getNearCustomersInfo();
        }else{
            getAllData();
        }
    }

    void showNoData() {
        vs_nodata.setVisibility(View.VISIBLE);
    }


    protected void returnData(int position) {
        Intent intent = new Intent();
        intent.putExtra("data",lstData.get(position));
        setResult(RESULT_OK, intent);
        onBackPressed();
    }


    public void changeAdapter() {
        adapter.notifyDataSetChanged();
    }


    public class CommonSearchAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return lstData.size();
        }

        @Override
        public Object getItem(int i) {
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

            Customer customer = lstData.get(i);
            ImageView status = ViewHolder.get(convertView, R.id.img_status);
            TextView title = ViewHolder.get(convertView, R.id.tv_title);
            TextView content = ViewHolder.get(convertView, R.id.tv_content);
            TextView time = ViewHolder.get(convertView, R.id.tv_time);
            View ack = ViewHolder.get(convertView, R.id.view_ack);
            ViewGroup layout_discuss = ViewHolder.get(convertView, R.id.layout_discuss);
            status.setVisibility(View.GONE);
            layout_discuss.setVisibility(View.GONE);
            time.setVisibility(View.VISIBLE);

            time.setVisibility(View.GONE);
            title.setText(customer.name);
            content.setText("距离：" + customer.distance);

            return convertView;
        }
    }

}
