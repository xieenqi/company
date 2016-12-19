package com.loyo.oa.v2.activityui.order;

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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.library.module.widget.loading.LoadingLayout;
import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.order.bean.OrderList;
import com.loyo.oa.v2.activityui.order.bean.OrderListItem;
import com.loyo.oa.v2.activityui.order.common.OrderCommon;
import com.loyo.oa.v2.common.ExtraAndResult;
import com.loyo.oa.v2.common.http.HttpErrorCheck;
import com.loyo.oa.pulltorefresh.PullToRefreshBase;
import com.loyo.oa.pulltorefresh.PullToRefreshListView;
import com.loyo.oa.v2.point.IOrder;
import com.loyo.oa.v2.tool.BaseActivity;
import com.loyo.oa.v2.tool.BaseLoadingActivity;
import com.loyo.oa.v2.tool.Config_project;
import com.loyo.oa.v2.tool.DateTool;
import com.loyo.oa.v2.tool.RestAdapterFactory;
import com.loyo.oa.v2.tool.Utils;

import java.util.ArrayList;
import java.util.HashMap;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * 【订单搜索】
 */

public class OrderSearchActivity extends BaseLoadingActivity implements PullToRefreshListView.OnRefreshListener2, Callback<OrderList> {

    public static final int TEAM_SALE_SEARCH = 20;//团队搜索
    public static final int MY_SALE_SEARCH = 30;//我的搜索
    private String strSearch;
    private EditText edt_search;
    private ImageView iv_clean;
    private PullToRefreshListView expandableListView_search;
    private ArrayList<OrderListItem> listData = new ArrayList<>();
    private CommonSearchAdapter adapter;
    private LayoutInflater mInflater;
    private int fromPage;
    private int page = 1;
    private boolean isPullDown = true;


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
        doSearch();
    }

    /**
     * 初始化
     */
    void initView() {
        Bundle mBundle = getIntent().getExtras();
        fromPage = mBundle.getInt(ExtraAndResult.EXTRA_TYPE);
        mInflater = LayoutInflater.from(this);

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
        expandableListView_search = (PullToRefreshListView) findViewById(R.id.expandableListView_search);
        expandableListView_search.setMode(PullToRefreshBase.Mode.PULL_FROM_END);
        expandableListView_search.setOnRefreshListener(this);

        ListView expandableListView = expandableListView_search.getRefreshableView();
        adapter = new CommonSearchAdapter();
        expandableListView.setAdapter(adapter);

        /**列表监听器*/
        expandableListView_search.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Intent mIntent = new Intent();
                mIntent.putExtra(ExtraAndResult.IS_TEAM, fromPage == MY_SALE_SEARCH ? false : true);
                mIntent.putExtra(ExtraAndResult.EXTRA_ID, listData.get(position - 1).id);
                mIntent.setClass(OrderSearchActivity.this, OrderDetailActivity.class);
                startActivity(mIntent);
                OrderSearchActivity.this.overridePendingTransition(R.anim.enter_righttoleft, R.anim.exit_righttoleft);
                hideInputKeyboard(edt_search);
            }
        });
        showInputKeyboard(edt_search);
        ll_loading.setStatus(LoadingLayout.Success);//此处是进入页面不需要请求接口
    }

    /**
     * 搜索操作
     */
    public void doSearch() {
        strSearch = edt_search.getText().toString().trim();
        getData();
    }

    /**
     * 我的/团队搜索
     */
    private void getData() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("pageIndex", page);
        map.put("pageSize", 15);
        map.put("keyWords", strSearch);
        if (fromPage == MY_SALE_SEARCH) {
            RestAdapterFactory.getInstance().build(Config_project.API_URL_CUSTOMER()).
                    create(IOrder.class).getOrderMyList(map, this);
        } else if (fromPage == TEAM_SALE_SEARCH) {
            RestAdapterFactory.getInstance().build(Config_project.API_URL_CUSTOMER()).
                    create(IOrder.class).getOrderTeamList(map, this);
        }
    }

    @Override
    public void success(OrderList orderList, Response response) {
        expandableListView_search.onRefreshComplete();
        HttpErrorCheck.checkResponse("订单搜索列表：", response, ll_loading);
        ll_loading.setStatus(LoadingLayout.Success);
        try {
            if (!isPullDown) {
                if (orderList.records != null && orderList.records.size() == 0)
                    Toast("没有更多信息");
                listData.addAll(orderList.records);
            } else {
                listData = orderList.records;
            }
            adapter.setAdapter();
            if (listData.size() == 0)
                ll_loading.setStatus(LoadingLayout.Empty);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void failure(RetrofitError error) {
        expandableListView_search.onRefreshComplete();
        HttpErrorCheck.checkError(error, ll_loading, page == 1 ? true : false);
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

    public class CommonSearchAdapter extends BaseAdapter {


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
            OrderListItem item = listData.get(position);
            Holder holder = null;
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.item_order_my_team, null);
                holder = new Holder();
                holder.tv_title = (TextView) convertView.findViewById(R.id.tv_title);
                holder.tv_status = (TextView) convertView.findViewById(R.id.tv_status);
                holder.tv_time = (TextView) convertView.findViewById(R.id.tv_time);
                holder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
                holder.tv_money = (TextView) convertView.findViewById(R.id.tv_money);
                holder.tv_customer = (TextView) convertView.findViewById(R.id.tv_customer);
                holder.tv_product = (TextView) convertView.findViewById(R.id.tv_product);
                holder.ll_responsible = (LinearLayout) convertView.findViewById(R.id.ll_responsible);
                convertView.setTag(holder);
            } else {
                holder = (Holder) convertView.getTag();
            }
            holder.setContent(item);
            return convertView;
        }

        class Holder {
            TextView tv_title, tv_status, tv_time, tv_name, tv_money, tv_customer, tv_product;
            LinearLayout ll_responsible;

            public void setContent(OrderListItem item) {
                ll_responsible.setVisibility(fromPage == TEAM_SALE_SEARCH ? View.VISIBLE : View.GONE);
                tv_title.setText(item.title);
                OrderCommon.getOrderDetailsStatus(tv_status, item.status);
                tv_name.setText(item.directorName);
                tv_money.setText(Utils.setValueDouble(item.dealMoney));
                tv_customer.setText(item.customerName);
                tv_product.setText(item.proName);
                tv_time.setText(com.loyo.oa.common.utils.DateTool.getDateTimeFriendly(Long.valueOf(item.createdAt + "")));
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        hideInputKeyboard(edt_search);
    }
}
