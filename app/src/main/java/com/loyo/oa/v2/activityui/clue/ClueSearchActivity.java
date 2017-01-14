package com.loyo.oa.v2.activityui.clue;

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
import com.loyo.oa.pulltorefresh.PullToRefreshBase;
import com.loyo.oa.pulltorefresh.PullToRefreshListView;
import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.clue.api.ClueService;
import com.loyo.oa.v2.activityui.clue.common.ClueType;
import com.loyo.oa.v2.activityui.clue.model.ClueList;
import com.loyo.oa.v2.activityui.clue.model.ClueListItem;
import com.loyo.oa.v2.activityui.followup.FollowAddActivity;
import com.loyo.oa.v2.beans.PaginationX;
import com.loyo.oa.v2.common.ExtraAndResult;
import com.loyo.oa.v2.network.DefaultLoyoSubscriber;
import com.loyo.oa.v2.network.LoyoErrorChecker;
import com.loyo.oa.v2.tool.BaseLoadingActivity;

import java.util.ArrayList;
import java.util.HashMap;


/**
 * 【线索搜索】
 * <p/>
 * Create by yyy on 16/08/23
 */

public class ClueSearchActivity extends BaseLoadingActivity implements PullToRefreshListView.OnRefreshListener2 {

    public final static String KEY_SEARCH_TYPE = "com.loyo.clue.SEARCH_TYPE";

    private String strSearch;
    private EditText edt_search;
    private ImageView iv_clean;
    private PullToRefreshListView expandableListView_search;
    private CommonSearchAdapter adapter;
    private Bundle mBundle;
    private LayoutInflater mInflater;
    private PaginationX<ClueListItem> mPaginationX=new PaginationX<>();

    private ClueType type;
    private boolean  isSelect, isResult, responsibleVisiblity = false;//是否加载第一页数据供选择  isResult是否设置返回值  负者人项默认不显示
    private Intent mIntent;


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
        if (isSelect) {
            doSearch();
        } else {
            ll_loading.setStatus(LoadingLayout.Success);
        }
    }

    /**
     * 初始化
     */
    void initView() {
        mBundle = getIntent().getExtras();
        type = (ClueType) mBundle.getSerializable(ClueSearchActivity.KEY_SEARCH_TYPE);
        isSelect = mBundle.getBoolean("isSelect", false);
        isResult = mBundle.getBoolean("isResult", false);
        responsibleVisiblity = mBundle.getBoolean("responsibleVisiblity", false);
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
                if (!isSelect) {//查看详情
                    mIntent = new Intent(getApplicationContext(), ClueDetailActivity.class);
                    mIntent.putExtra(ExtraAndResult.EXTRA_ID, mPaginationX.getRecords().get(position - 1).id);
                    startActivity(mIntent);
                } else {//选择线索
                    Intent intent = new Intent();
                    intent.putExtra(ClueListItem.class.getName(), mPaginationX.getRecords().get(position - 1));
                    intent.putExtra(ExtraAndResult.DYNAMIC_ADD_ACTION, ExtraAndResult.DYNAMIC_ADD_CULE);
                    intent.setClass(ClueSearchActivity.this, FollowAddActivity.class);
                    if (isResult) {
                        setResult(RESULT_OK, intent);
                        onBackPressed();
                    } else {
                        startActivity(intent);
                        overridePendingTransition(R.anim.enter_righttoleft, R.anim.exit_righttoleft);
                        finish();
                    }
                }
                hideInputKeyboard(edt_search);
            }
        });
        getPageData();
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
        map.put("pageIndex", mPaginationX.getShouldLoadPageIndex());
        map.put("pageSize", mPaginationX.getPageSize());
        map.put("keyword", strSearch);

        //新的网络模块
        if (type == ClueType.MY_CLUE) {
            ClueService.getMyClueList(map).subscribe(getDefaultLoyoSubscriber());
        } else if (type == ClueType.TEAM_CLUE) {
            ClueService.getTeamClueList(map).subscribe(getDefaultLoyoSubscriber());
        }
    }

    //订阅者，处理网络请求事件
    private DefaultLoyoSubscriber<ClueList> getDefaultLoyoSubscriber() {
        return new DefaultLoyoSubscriber<ClueList>() {
            @Override
            public void onError(Throwable e) {
                 /* 重写父类方法，不调用super */
                @LoyoErrorChecker.CheckType
                int type = mPaginationX.isEnpty()  ?LoyoErrorChecker.LOADING_LAYOUT : LoyoErrorChecker.TOAST;
                LoyoErrorChecker.checkLoyoError(e, type, ll_loading);
                expandableListView_search.onRefreshComplete();
            }
            @Override
            public void onNext(PaginationX<ClueListItem> clueListItemPaginationX) {
                expandableListView_search.onRefreshComplete();
                ll_loading.setStatus(LoadingLayout.Success);
                mPaginationX.loadRecords(clueListItemPaginationX);
                if(mPaginationX.isEnpty()){
                    ll_loading.setStatus(LoadingLayout.Empty);
                }
                adapter.setAdapter();
                if(mPaginationX.isNeedToBackTop()){
                    expandableListView_search.getRefreshableView().setSelection(0);
                }
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
            return mPaginationX.getTotalRecords();
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
            ClueListItem clueListItem = mPaginationX.getRecords().get(position);
            Holder holder = null;
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.item_teamclue, null);
                holder = new Holder();
                holder.tv_company_name = (TextView) convertView.findViewById(R.id.tv_company_name);
                holder.tv_customer = (TextView) convertView.findViewById(R.id.tv_customer);
                holder.tv_time = (TextView) convertView.findViewById(R.id.tv_time);
                holder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
                holder.ll_responsible = (LinearLayout) convertView.findViewById(R.id.ll_responsible);
                convertView.setTag(holder);
            } else {
                holder = (Holder) convertView.getTag();
            }

            holder.tv_name.setText(clueListItem.name);
            holder.tv_company_name.setText(clueListItem.companyName);
            holder.tv_customer.setText(clueListItem.name);
            if (clueListItem.lastActAt != 0) {
//                holder.tv_time.setText(DateTool.timet(clueListItem.lastActAt + "", "yyyy-MM-dd HH:mm"));
                holder.tv_time.setText(com.loyo.oa.common.utils.DateTool.getDateTimeFriendly(clueListItem.lastActAt));
            } else {
                holder.tv_time.setText("--");
            }
            holder.ll_responsible.setVisibility(responsibleVisiblity ? View.VISIBLE : View.GONE);
            return convertView;
        }

        class Holder {
            TextView tv_company_name; /* 公司名称 */
            TextView tv_customer;     /* 负责人 */
            TextView tv_time;         /* 跟进时间 */
            TextView tv_name;         /* 客户名称 */
            LinearLayout ll_responsible;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        hideInputKeyboard(edt_search);
    }
}
