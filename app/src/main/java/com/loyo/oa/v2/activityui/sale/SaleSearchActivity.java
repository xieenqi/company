package com.loyo.oa.v2.activityui.sale;

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
import com.loyo.oa.v2.activityui.sale.bean.SaleList;
import com.loyo.oa.v2.activityui.sale.bean.SaleRecord;
import com.loyo.oa.v2.common.ExtraAndResult;
import com.loyo.oa.v2.common.http.HttpErrorCheck;
import com.loyo.oa.pulltorefresh.PullToRefreshBase;
import com.loyo.oa.pulltorefresh.PullToRefreshListView;
import com.loyo.oa.v2.point.ISale;
import com.loyo.oa.v2.tool.BaseActivity;
import com.loyo.oa.v2.tool.BaseLoadingActivity;
import com.loyo.oa.v2.tool.Config_project;
import com.loyo.oa.v2.tool.RestAdapterFactory;
import com.loyo.oa.v2.tool.Utils;

import java.util.ArrayList;
import java.util.HashMap;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * 【销售机会搜索】
 */

public class SaleSearchActivity extends BaseLoadingActivity implements PullToRefreshListView.OnRefreshListener2, Callback<SaleList> {

    public static final int TEAM_SALE_SEARCH = 20;//团队机会搜索
    public static final int MY_SALE_SEARCH = 30;//我的机会搜索
    private String strSearch;
    private EditText edt_search;
    private ImageView iv_clean;
    private PullToRefreshListView expandableListView_search;
    private ArrayList<SaleRecord> listData = new ArrayList<>();
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
                mIntent.putExtra("id", listData.get(position - 1).getId());
                mIntent.setClass(SaleSearchActivity.this, SaleDetailsActivity.class);
                startActivity(mIntent);
                SaleSearchActivity.this.overridePendingTransition(R.anim.enter_righttoleft, R.anim.exit_righttoleft);
                hideInputKeyboard(edt_search);
            }
        });

        showInputKeyboard(edt_search);
        ll_loading.setStatus(LoadingLayout.Success);
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
                    create(ISale.class).getSaleMyList(map, this);
        } else if (fromPage == TEAM_SALE_SEARCH) {
            RestAdapterFactory.getInstance().build(Config_project.API_URL_CUSTOMER()).
                    create(ISale.class).getSaleTeamList(map, this);
        }
    }

    @Override
    public void success(SaleList saleList, Response response) {
        expandableListView_search.onRefreshComplete();
        HttpErrorCheck.checkResponse("机会搜索列表：", response);
        try {
            if (!isPullDown) {
                if (saleList.records != null && saleList.records.size() == 0)
                    Toast("没有更多信息");
                listData.addAll(saleList.records);
            } else {
                listData = saleList.records;
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
        HttpErrorCheck.checkError(error, ll_loading);
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
            SaleRecord item = listData.get(position);
            Holder holder = null;
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.item_saleteamlist, null);
                holder = new Holder();
                holder.creator = (TextView) convertView.findViewById(R.id.sale_teamlist_creator);
                holder.state = (TextView) convertView.findViewById(R.id.sale_teamlist_state);
                holder.guess = (TextView) convertView.findViewById(R.id.sale_teamlist_guess);
                holder.money = (TextView) convertView.findViewById(R.id.sale_teamlist_money);
                holder.title = (TextView) convertView.findViewById(R.id.sale_teamlist_title);
                holder.ll_creator = (LinearLayout) convertView.findViewById(R.id.ll_creator);
                convertView.setTag(holder);
            } else {
                holder = (Holder) convertView.getTag();
            }
            holder.setContent(item);
            return convertView;
        }

        class Holder {
            TextView creator, state, guess, money, title;
            LinearLayout ll_creator;

            public void setContent(SaleRecord item) {
                title.setText(item.getName());
                money.setText(Utils.setValueDouble(item.getEstimatedAmount()) + "");
                String stageName = "初步接洽";
                if (!item.getStageNmae().isEmpty()) {
                    stageName = item.getStageNmae();
                }
                state.setText(stageName + "(" + item.getProb() + "%)");
                creator.setText(item.getCreateName());
                ll_creator.setVisibility(fromPage == MY_SALE_SEARCH ? View.GONE : View.VISIBLE);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        hideInputKeyboard(edt_search);
    }
}
