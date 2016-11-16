package com.loyo.oa.v2.activityui.signin;

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
import com.loyo.oa.v2.activityui.signin.bean.SigninSelectCustomer;
import com.loyo.oa.v2.beans.BaseBeanT;
import com.loyo.oa.v2.beans.Customer;
import com.loyo.oa.v2.beans.PaginationX;
import com.loyo.oa.v2.common.FinalVariables;
import com.loyo.oa.v2.common.http.HttpErrorCheck;
import com.loyo.oa.v2.point.ICustomer;
import com.loyo.oa.v2.point.ISigninNeworFollowUp;
import com.loyo.oa.v2.tool.BaseActivity;
import com.loyo.oa.v2.tool.Config_project;
import com.loyo.oa.v2.tool.LocationUtilGD;
import com.loyo.oa.v2.tool.LogUtil;
import com.loyo.oa.v2.tool.RestAdapterFactory;
import com.loyo.oa.v2.tool.UMengTools;
import com.loyo.oa.v2.tool.ViewHolder;
import com.loyo.oa.v2.customview.pullToRefresh.PullToRefreshBase;
import com.loyo.oa.v2.customview.pullToRefresh.PullToRefreshListView;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * 新建拜访 [客户选择]
 * Created by yyy on 15/12/24.
 */
public class SigninSelectCustomerSearch extends BaseActivity implements PullToRefreshListView.OnRefreshListener2, View.OnClickListener {

    protected String strSearch;
    protected EditText edt_search;
    protected View vs_nodata;
    protected PullToRefreshListView expandableListView_search;
    protected ArrayList<SigninSelectCustomer> lstData = new ArrayList<>();
    protected CommonSearchAdapter adapter;
    //    protected PaginationX paginationX = new PaginationX(20);
    public Customer customer;
    public String position;
    private DecimalFormat df = new DecimalFormat("0.0");
    public Context mContext;
    public int kalo = 0;
    public boolean isTopAdd = true;
    private int page = 1;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_public_search);
        mContext = this;
        initView();
    }

    public void initView() {
        vs_nodata = findViewById(R.id.vs_nodata);
        findViewById(R.id.img_title_left).setOnClickListener(this);
        findViewById(R.id.iv_clean).setOnClickListener(this);
        edt_search = (EditText) findViewById(R.id.edt_search);
        edt_search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(final TextView v, final int actionId, final KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    doSearch();
                }
                return false;
            }
        });

        edt_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(final CharSequence charSequence, final int i, final int i1, final int i2) {

            }

            @Override
            public void onTextChanged(final CharSequence charSequence, final int i, final int i1, final int i2) {

            }

            @Override
            public void afterTextChanged(final Editable editable) {
            }
        });
        edt_search.requestFocus();
        expandableListView_search = (PullToRefreshListView) findViewById(R.id.expandableListView_search);
        expandableListView_search.setMode(PullToRefreshBase.Mode.BOTH);
        expandableListView_search.setOnRefreshListener(this);
        adapter = new CommonSearchAdapter();
        expandableListView_search.setAdapter(adapter);
        /**列表监听器*/
        expandableListView_search.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(final AdapterView<?> adapterView, final View view, final int position, final long l) {
                returnData(position - 1);
            }
        });
        getAllData();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_title_left:
                onBackPressed();
                break;
            case R.id.iv_clean:
                edt_search.setText("");
                break;
        }
    }

    /**
     * 查询客户数据
     */
    void getAllData() {
        new LocationUtilGD(this, new LocationUtilGD.AfterLocation() {
            @Override
            public void OnLocationGDSucessed(final String address, final double longitude, final double latitude, final String radius) {
                position = String.valueOf(longitude).concat(",").concat(String.valueOf(latitude));
                String url = Config_project.API_URL_STATISTICS();
                HashMap<String, Object> params = new HashMap<>();
                kalo = 1;
                params.put("pageIndex", page);
                params.put("pageSize", isTopAdd ? lstData.size() >= 20 ? lstData.size() : 20 : 20);
                params.put("keyWords", strSearch);
//                params.put("order", "desc");
//                params.put("position", position);
                dataRequestvoid(url, params);
                LocationUtilGD.sotpLocation();
                UMengTools.sendLocationInfo(address, longitude, latitude);
            }

            @Override
            public void OnLocationGDFailed() {
                Toast("获取附近客户信息失败！");
                LocationUtilGD.sotpLocation();
            }
        });
    }

    /**
     * 请求体
     */
    void dataRequestvoid(final String url, final HashMap<String, Object> params) {
        showLoading("请稍后");
        RestAdapterFactory.getInstance().build(url).create(ISigninNeworFollowUp.class).signinSearchCutomer(params, new Callback<BaseBeanT<ArrayList<SigninSelectCustomer>>>() {
            @Override
            public void success(final BaseBeanT<ArrayList<SigninSelectCustomer>> customerPaginationX, final Response response) {
                HttpErrorCheck.checkResponse("拜访搜索选择客户:", response);
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

//                paginationX = customerPaginationX;
                ArrayList<SigninSelectCustomer> lstDataTemp = customerPaginationX.data;

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
            public void failure(final RetrofitError error) {
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
    public void onPullDownToRefresh(final PullToRefreshBase refreshView) {
        isTopAdd = true;
        page = 1;
        getAllData();
    }

    @Override
    public void onPullUpToRefresh(final PullToRefreshBase refreshView) {
        isTopAdd = false;
        page++;
        getAllData();
    }

    void showNoData() {
        vs_nodata.setVisibility(View.VISIBLE);
    }


    protected void returnData(final int position) {
        SigninSelectCustomer item = lstData.get(position);
        Intent intent = new Intent();
        intent.putExtra("id", item.id);
        intent.putExtra("name", item.name);
        intent.putExtra("address", item.position.addr);
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
        public Object getItem(final int i) {
            return lstData.get(i);
        }

        @Override
        public long getItemId(final int i) {
            return i;
        }

        @Override
        public View getView(final int i, View convertView, final ViewGroup viewGroup) {
            if (convertView == null) {
                convertView = LayoutInflater.from(mContext).inflate(R.layout.item_listview_common, null, false);
            }

            SigninSelectCustomer customer = lstData.get(i);
            TextView title = ViewHolder.get(convertView, R.id.tv_title);
            TextView content = ViewHolder.get(convertView, R.id.tv_content);
            TextView time = ViewHolder.get(convertView, R.id.tv_time);
            View ack = ViewHolder.get(convertView, R.id.view_ack);
            ViewGroup layout_discuss = ViewHolder.get(convertView, R.id.layout_discuss);
            layout_discuss.setVisibility(View.GONE);
            content.setVisibility(View.GONE);

            time.setVisibility(View.VISIBLE);
            title.setText(customer.name);

//            time.setText(customer.distance != null ? "距离: " + customer.distance : "距离: 无");
//
//            if (null != customer.distance) {
//                String distance;
//                if (customer.distance.contains("km")) {
//                    time.setText("距离:" + df.format(Double.parseDouble(customer.distance.replace("km", ""))) + "km");
//                } else if (customer.distance.contains("m")) {
//                    double disa = Float.parseFloat(customer.distance.replace("m", ""));
//                    if (disa <= 100) {
//                        distance = "<0.1km";
//                    } else {
//                        distance = df.format(disa / 1000) + "km";
//                    }
//                    time.setText("距离:" + distance);
//                }
//            } else {
//                time.setText("距离:无");
//            }

            return convertView;
        }
    }
}
