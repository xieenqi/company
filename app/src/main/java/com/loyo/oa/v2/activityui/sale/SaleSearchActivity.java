package com.loyo.oa.v2.activityui.sale;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.library.module.widget.loading.LoadingLayout;
import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.sale.api.SaleService;
import com.loyo.oa.v2.activityui.sale.bean.SaleRecord;
import com.loyo.oa.v2.beans.PaginationX;
import com.loyo.oa.v2.common.ExtraAndResult;
import com.loyo.oa.pulltorefresh.PullToRefreshBase;
import com.loyo.oa.pulltorefresh.PullToRefreshListView;
import com.loyo.oa.v2.network.DefaultLoyoSubscriber;
import com.loyo.oa.v2.network.LoyoErrorChecker;
import com.loyo.oa.v2.tool.BaseLoadingActivity;
import com.loyo.oa.v2.tool.Utils;

import java.util.HashMap;

/**
 * 【销售机会搜索】
 */

public class SaleSearchActivity extends BaseLoadingActivity implements PullToRefreshListView.OnRefreshListener2 {

    public static final int TEAM_SALE_SEARCH = 20;//团队机会搜索
    public static final int MY_SALE_SEARCH = 30;//我的机会搜索
    private String strSearch;
    private EditText edt_search;
    private ImageView iv_clean;
    private PullToRefreshListView expandableListView_search;
    private CommonSearchAdapter adapter;
    private LayoutInflater mInflater;
    private int fromPage;
    private PaginationX<SaleRecord> mPaginationX=new PaginationX<>();

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
                mIntent.putExtra("id", mPaginationX.getRecords().get(position - 1).getId());
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
        mPaginationX.setFirstPage();
        getData();
    }

    /**
     * 我的/团队搜索
     */
    private void getData() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("pageIndex", mPaginationX.getShouldLoadPageIndex());
        map.put("pageSize", mPaginationX.getPageSize());
        map.put("keyWords", strSearch);
        if (fromPage == MY_SALE_SEARCH) {
            SaleService.getSaleMyList(map).subscribe(getSaleListSubscriber());
        } else if (fromPage == TEAM_SALE_SEARCH) {
            SaleService.getSaleTeamList(map).subscribe(getSaleListSubscriber());
        }


    }

    //获取一个订阅者，来处理结果
    private DefaultLoyoSubscriber<PaginationX<SaleRecord>> getSaleListSubscriber(){
        return new DefaultLoyoSubscriber<PaginationX<SaleRecord>>() {
            @Override
            public void onError(Throwable e) {
                expandableListView_search.onRefreshComplete();
                 /* 重写父类方法，不调用super */
                @LoyoErrorChecker.CheckType
                int type = mPaginationX.isEnpty() ? LoyoErrorChecker.LOADING_LAYOUT:LoyoErrorChecker.TOAST ;
                LoyoErrorChecker.checkLoyoError(e, type, ll_loading);
            }

            @Override
            public void onNext(PaginationX<SaleRecord> saleRecordPaginationX) {
                expandableListView_search.onRefreshComplete();
                mPaginationX.loadRecords(saleRecordPaginationX);
                if(mPaginationX.isEnpty()){
                    ll_loading.setStatus(LoadingLayout.Empty);
                }else{
                    ll_loading.setStatus(LoadingLayout.Success);
                }
                adapter.setAdapter();
            }
        };
    }

    @Override
    public void onPullDownToRefresh(PullToRefreshBase refreshView) {
        mPaginationX.setFirstPage();
        getData();
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase refreshView) {
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
            return mPaginationX.getLoadedTotalRecords();
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
            SaleRecord item = mPaginationX.getRecords().get(position);
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
