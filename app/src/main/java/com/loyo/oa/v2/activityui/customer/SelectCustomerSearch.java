package com.loyo.oa.v2.activityui.customer;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.TextView;

import com.library.module.widget.loading.LoadingLayout;
import com.loyo.oa.pulltorefresh.PullToRefreshBase;
import com.loyo.oa.pulltorefresh.PullToRefreshListView;
import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.customer.model.Customer;
import com.loyo.oa.v2.activityui.signin.api.SignInService;
import com.loyo.oa.v2.activityui.signin.bean.SigninSelectCustomer;
import com.loyo.oa.v2.beans.BaseBeanT;
import com.loyo.oa.v2.beans.PaginationX;
import com.loyo.oa.v2.network.DefaultLoyoSubscriber;
import com.loyo.oa.v2.tool.BaseLoadingActivity;

import java.util.ArrayList;
import java.util.HashMap;


/**
 * 新建拜访 [搜索客户选择]
 * Created by yyy on 15/12/24.
 */
public class SelectCustomerSearch extends BaseLoadingActivity implements PullToRefreshListView.OnRefreshListener2, View.OnClickListener {

    protected String strSearch;
    protected EditText edt_search;
    protected PullToRefreshListView expandableListView_search;
    protected ArrayList<SigninSelectCustomer> lstData = new ArrayList<>();
    protected CommonSearchAdapter adapter;
    //    private Customer customer;
//    private String position;
    private Context mContext;
    private boolean isTopAdd = true, isSignin = false;
    private int page = 1;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        initView();
    }

    @Override
    public void setLayoutView() {
        setContentView(R.layout.activity_public_search);
    }

    @Override
    public void getPageData() {
        ll_loading.setStatus(LoadingLayout.Loading);
        doSearch();
    }

    public void initView() {
        isSignin = getIntent().getBooleanExtra("isSignin", false);//是否初始化第一页
        findViewById(R.id.img_title_left).setOnClickListener(this);
        findViewById(R.id.iv_clean).setOnClickListener(this);
        edt_search = (EditText) findViewById(R.id.edt_search);
        edt_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(final CharSequence charSequence, final int i, final int i1, final int i2) {

            }

            @Override
            public void onTextChanged(final CharSequence charSequence, final int i, final int i1, final int i2) {

            }

            @Override
            public void afterTextChanged(final Editable editable) {
                doSearch();
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
        if (!isSignin) {
            dataRequestvoid();
        } else {
            ll_loading.setStatus(LoadingLayout.Success);
        }
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
     * 请求体
     */
    void dataRequestvoid() {
        HashMap<String, Object> params = new HashMap<>();
        params.put("pageIndex", page);
        params.put("pageSize", 20);
        params.put("keyWords", edt_search.getText().toString().trim());
//        RestAdapterFactory.getInstance().build(Config_project.API_URL_STATISTICS()).create(ISigninOrFollowUp.class).
//                signinSearchCutomer(params, new Callback<BaseBeanT<PaginationX<SigninSelectCustomer>>>() {
//                    @Override
//                    public void success(final BaseBeanT<PaginationX<SigninSelectCustomer>> customerPaginationX, final Response response) {
//                        HttpErrorCheck.checkResponse("拜访搜索选择客户:", response, ll_loading);
//                        expandableListView_search.onRefreshComplete();
////                        InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
////                        imm.hideSoftInputFromWindow(edt_search.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
//                        if (null == customerPaginationX) {
//                            if (isTopAdd) {
//                                showNoData();
//                            } else {
//                                Toast("没有更多数据!");
//                            }
//                            return;
//                        }
//                        ArrayList<SigninSelectCustomer> lstDataTemp = customerPaginationX.data.getRecords();
//                        if (lstDataTemp == null || lstDataTemp.size() == 0) {
//                            if (isTopAdd) {
//                                showNoData();
//                            } else {
//                                Toast("没有更多数据!");
//                            }
//                            return;
//                        } else {
//                            if (isTopAdd) {
//                                lstData.clear();
//                            }
//                            lstData.addAll(lstDataTemp);
//                        }
//                        changeAdapter();
//                    }
//
//                    @Override
//                    public void failure(final RetrofitError error) {
//                        HttpErrorCheck.checkError(error, ll_loading);
//                        expandableListView_search.onRefreshComplete();
//                    }
//                });

        SignInService.signInSearchCutomer(params).subscribe(new DefaultLoyoSubscriber<BaseBeanT<PaginationX<SigninSelectCustomer>>>(ll_loading) {
            @Override
            public void onNext(BaseBeanT<PaginationX<SigninSelectCustomer>> customerPaginationX) {
                ll_loading.setStatus(LoadingLayout.Success);
                expandableListView_search.onRefreshComplete();
                if (null == customerPaginationX) {
                    if (isTopAdd) {
                        showNoData();
                    } else {
                        Toast("没有更多数据!");
                    }
                    return;
                }
                ArrayList<SigninSelectCustomer> lstDataTemp = customerPaginationX.data.getRecords();
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
                changeAdapter();
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                expandableListView_search.onRefreshComplete();
            }
        });
    }

    void doSearch() {
//        page = 1;
//        isTopAdd = true;
//        dataRequestvoid();
        onPullDownToRefresh(expandableListView_search);
    }


    @Override
    public void onPullDownToRefresh(final PullToRefreshBase refreshView) {
        isTopAdd = true;
        page = 1;
        dataRequestvoid();
    }

    @Override
    public void onPullUpToRefresh(final PullToRefreshBase refreshView) {
        isTopAdd = false;
        page++;
        dataRequestvoid();
    }

    void showNoData() {
        ll_loading.setStatus(LoadingLayout.Empty);
    }


    protected void returnData(final int position) {
        Intent intent = new Intent();
        if (!isSignin) {//机会 订单 等选客户
            Customer cus = new Customer();
            cus.id = lstData.get(position).id;
            cus.name = lstData.get(position).name;
            cus.contacts = lstData.get(position).contacts;
            intent.putExtra("data", cus);
        } else {//此处拜访选客户
            SigninSelectCustomer item = lstData.get(position);
            intent.putExtra("id", item.id);
            intent.putExtra("name", item.name);
            intent.putExtra("loc", item.position);
            intent.putExtra("contact", item.contacts);
        }
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
        public View getView(int position, View convertView, ViewGroup parent) {
            Holder holder;
            if (convertView == null) {
                holder = new Holder();
                convertView = LayoutInflater.from(SelectCustomerSearch.this).inflate(R.layout.item_signin_select_customer, null);
                holder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
                holder.tv_distance = (TextView) convertView.findViewById(R.id.tv_distance);
                holder.tv_location = (TextView) convertView.findViewById(R.id.tv_location);
                convertView.setTag(holder);
            } else {
                holder = (Holder) convertView.getTag();
            }
            holder.setContent(lstData.get(position));
            return convertView;
        }

        class Holder {
            TextView tv_name, tv_distance, tv_location;

            public void setContent(SigninSelectCustomer item) {
                tv_name.setText(item.name);
                if (item.position != null) {
                    tv_location.setText(item.position.addr);
                }
//                tv_distance.setText(item.distance + "米");
            }
        }
    }
}
