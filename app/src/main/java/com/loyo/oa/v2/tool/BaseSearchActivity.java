package com.loyo.oa.v2.tool;

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
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.library.module.widget.loading.LoadingLayout;
import com.loyo.oa.v2.R;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.beans.PaginationX;
import com.loyo.oa.v2.activityui.tasks.fragment.TaskManagerFragment;
import com.loyo.oa.pulltorefresh.PullToRefreshBase;
import com.loyo.oa.pulltorefresh.PullToRefreshListView;
import com.loyo.oa.v2.network.LoyoErrorChecker;

import java.io.Serializable;

import rx.Subscription;

/**
 * 搜索的 基类
 * 客户搜索；
 * 项目搜索；
 * 拜访搜索（新建拜访签到－选择客户－搜索）；
 * 任务搜索；
 * 工单搜索；
 * 审批搜索；
 * 等等。
 *
 * @param <T>
 */
public abstract class BaseSearchActivity<T extends Serializable> extends BaseLoadingActivity implements PullToRefreshListView.OnRefreshListener2 {


    public static final String EXTRA_JUMP_NEW_PAGE = "jumpNewPage";//是否是跳转页面
    public static final String EXTRA_JUMP_PAGE_CLASS = "class";//跳转的目标页面
    public static final String EXTRA_CAN_BE_EMPTY = "canBeEmpty";//选择的时候 ，是否可以返回"无"
    public static final String EXTRA_LOAD_DEFAULT = "loadData";// 进来就加载默认数据


    public boolean jumpNewPage = false;
    public Class<?> cls;
    public boolean canBeEmpty = false;
    public boolean loadDefaultData = false;


    public String strSearch;
    public EditText edt_search;
    public ImageView iv_clean;
    public View headerView;
    public PullToRefreshListView refreshListView;
    public BaseAdapter adapter;
    public PaginationX<T> paginationX = new PaginationX(20);
    public RelativeLayout headerViewBtn;
    public Subscription subscriber;
    public ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getIntentData();
        initView();
    }

    private void getIntentData(){
        Intent intent=getIntent();
        if(null!=intent){
            canBeEmpty = getIntent().getBooleanExtra(EXTRA_CAN_BE_EMPTY, false);
            jumpNewPage = getIntent().getBooleanExtra(EXTRA_JUMP_NEW_PAGE, false);
            loadDefaultData = getIntent().getBooleanExtra(EXTRA_LOAD_DEFAULT, false);
            cls = (Class<?>) getIntent().getSerializableExtra(EXTRA_JUMP_PAGE_CLASS);
        }
    }

    @Override
    public void setLayoutView() {
        setContentView(R.layout.activity_public_search);
    }

    @Override
    public void getPageData() {
        getData();
    }

    /**
     * 初始化
     */
    void initView() {
        ll_loading.setStatus(LoadingLayout.Success);
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
                //数据为空的时候，不显示数据
                if (TextUtils.isEmpty(editable.toString())) {
                    //不再处理后续网络请求的返回
                    if (null != subscriber) {
                        subscriber.unsubscribe();
                        subscriber = null;
                    }
                    paginationX.getRecords().clear();
                    adapter.notifyDataSetChanged();
                    ll_loading.setStatus(LoadingLayout.Success);
                    refreshListView.setMode(PullToRefreshBase.Mode.MANUAL_REFRESH_ONLY);
                } else {
                    refreshListView.setMode(PullToRefreshBase.Mode.PULL_FROM_END);
                    doSearch();
                }
            }
        });
        edt_search.requestFocus();
        refreshListView = (PullToRefreshListView) findViewById(R.id.expandableListView_search);
        refreshListView.setOnRefreshListener(this);
        refreshListView.setMode(PullToRefreshBase.Mode.MANUAL_REFRESH_ONLY);
        listView = refreshListView.getRefreshableView();
        if (canBeEmpty) {
            LayoutInflater mInflater = LayoutInflater.from(this);
            headerView = mInflater.inflate(R.layout.item_baseserach_null, null);
            headerViewBtn = (RelativeLayout) headerView.findViewById(R.id.item_baseserach_btn);
            listView.addHeaderView(headerView);
            //点击无的时候，直接关闭本页，返回空数据
            headerViewBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    app.finishActivity(BaseSearchActivity.this, MainApp.ENTER_TYPE_LEFT, RESULT_OK, new Intent());
                }
            });
        }

        adapter=setAdapter();
        listView.setAdapter(adapter);
        //如果可以为空，说明是选择器，就加载默认的数据
        if(loadDefaultData){
            paginationX.setFirstPage();
            getPageData();
        }

        refreshListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (canBeEmpty) {
                    onListItemClick(view, position - 2);
                } else {
                    onListItemClick(view, position - 1);
                }
            }
        });
    }
    /**
     * 如果item的条目不一样，可以覆盖这个方法，自定义adapter
     */
    public BaseAdapter setAdapter(){
        adapter = new CommonSearchAdapter();
        return adapter;
    }


    /**
     * 搜索操作
     */
    public void doSearch() {
        strSearch = edt_search.getText().toString().trim();
        paginationX.setFirstPage();
        ll_loading.setStatus(LoadingLayout.Loading);
        getPageData();
    }


    @Override
    public void onPullDownToRefresh(PullToRefreshBase refreshView) {
        paginationX.setFirstPage();
        getPageData();

    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase refreshView) {
        getPageData();

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


    /**
     * 请求成功的时候
     */
    public void success(PaginationX<T> data) {
        refreshListView.onRefreshComplete();
        paginationX.loadRecords(data);
        if (paginationX.isEnpty() && !canBeEmpty) {
            ll_loading.setStatus(LoadingLayout.Empty);
        } else {
            ll_loading.setStatus(LoadingLayout.Success);
        }
        changeAdapter();
    }

    ;

    /**
     * 请求失败的时候
     *
     * @param e
     */
    public void fail(Throwable e) {
        refreshListView.onRefreshComplete();
        @LoyoErrorChecker.CheckType
        int type = paginationX.isEnpty() ? LoyoErrorChecker.LOADING_LAYOUT : LoyoErrorChecker.TOAST;
        LoyoErrorChecker.checkLoyoError(e, type, ll_loading);
    }

    protected void changeAdapter() {
        adapter.notifyDataSetChanged();
    }

    /**
     * 加载数据的方法，子类通过复写分方法，异步加载数据；
     * 加载以后，成功就调用＃success（）；
     * 失败就调用fail（）；
     * 注意，需要把网络请求的实例赋值给subscribe，否则无法取消请求
     */
    public abstract void getData();

    /**
     * 当item被点击的时候，子类复写，来决定怎么处理
     * 默认处理是把数据添加到intent里面，关闭当前页面，带回去
     */
    public void onListItemClick(View view, int position) {
        Intent intent = new Intent();
        intent.putExtra("data", (T)adapter.getItem(position));
        setResult(RESULT_OK, intent);
        onBackPressed();
    }

    /**
     * 绑定数据
     *
     * @param viewHolder 试图
     * @param data       数据
     */
    public void bindData(CommonSearchAdapter.SearchViewHolder viewHolder, T data){}

    public class CommonSearchAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return paginationX.getRecords().size();
        }

        @Override
        public T getItem(int i) {
            return paginationX.getRecords().get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View convertView, ViewGroup viewGroup) {
            SearchViewHolder viewHolder = null;
            if (convertView == null) {
                convertView = LayoutInflater.from(mContext).inflate(R.layout.item_listview_common, null, false);
            } else {
                viewHolder = (SearchViewHolder) convertView.getTag();
            }
            if (null == viewHolder) {
                viewHolder = new SearchViewHolder();
                viewHolder.title = ViewHolder.get(convertView, R.id.tv_title);
                viewHolder.content = ViewHolder.get(convertView, R.id.tv_content);
                viewHolder.time = ViewHolder.get(convertView, R.id.tv_time);
                viewHolder.ack = ViewHolder.get(convertView, R.id.view_ack);
                viewHolder.layout_discuss = ViewHolder.get(convertView, R.id.layout_discuss);
                convertView.setTag(viewHolder);
            }
            T item = getItem(i);
            bindData(viewHolder, item);
            return convertView;
        }

        public class SearchViewHolder {
            public TextView title;
            public TextView content;
            public TextView time;
            public View ack;
            public ViewGroup layout_discuss;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        hideInputKeyboard(edt_search);
    }
}