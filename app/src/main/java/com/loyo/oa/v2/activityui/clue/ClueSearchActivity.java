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
import android.widget.ListView;
import android.widget.TextView;

import com.library.module.widget.loading.LoadingLayout;
import com.loyo.oa.pulltorefresh.PullToRefreshBase;
import com.loyo.oa.pulltorefresh.PullToRefreshListView;
import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.clue.model.ClueList;
import com.loyo.oa.v2.activityui.clue.model.ClueListItem;
import com.loyo.oa.v2.activityui.followup.DynamicAddActivity;
import com.loyo.oa.v2.common.ExtraAndResult;
import com.loyo.oa.v2.common.http.HttpErrorCheck;
import com.loyo.oa.v2.point.IClue;
import com.loyo.oa.v2.tool.BaseLoadingActivity;
import com.loyo.oa.v2.tool.Config_project;
import com.loyo.oa.v2.tool.RestAdapterFactory;

import java.util.ArrayList;
import java.util.HashMap;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * 【线索搜索】
 * <p/>
 * Create by yyy on 16/08/23
 */

public class ClueSearchActivity extends BaseLoadingActivity implements PullToRefreshListView.OnRefreshListener2, Callback<ClueList> {


    private String strSearch;
    private EditText edt_search;
    private ImageView iv_clean;
    private PullToRefreshListView expandableListView_search;
    private ArrayList<ClueListItem> listData = new ArrayList<>();
    private CommonSearchAdapter adapter;
    private Bundle mBundle;
    private LayoutInflater mInflater;

    private int fromPage;
    private int page = 1;
    private boolean isPullDown = true, isSelect, isResult;//是否加载第一页数据供选择  isResult是否设置返回值
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
        fromPage = mBundle.getInt(ExtraAndResult.EXTRA_TYPE);
        isSelect = mBundle.getBoolean("isSelect", false);
        isResult = mBundle.getBoolean("isResult", false);
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
                    mIntent.putExtra(ExtraAndResult.EXTRA_ID, listData.get(position - 1).id);
                    startActivity(mIntent);
                } else {//选择线索
                    Intent intent = new Intent();
                    intent.putExtra(ClueListItem.class.getName(), listData.get(position - 1));
                    intent.putExtra(ExtraAndResult.DYNAMIC_ADD_ACTION, ExtraAndResult.DYNAMIC_ADD_CULE);
                    intent.setClass(ClueSearchActivity.this, DynamicAddActivity.class);
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
        map.put("pageIndex", page);
        map.put("pageSize", 15);
        map.put("keyword", strSearch);
        if (fromPage == 1) {
            RestAdapterFactory.getInstance().build(Config_project.API_URL_CUSTOMER()).
                    create(IClue.class).getMyCluelist(map, this);
        } else if (fromPage == 2) {
            RestAdapterFactory.getInstance().build(Config_project.API_URL_CUSTOMER()).
                    create(IClue.class).getTeamCluelist(map, this);
        }
    }

    @Override
    public void success(ClueList clueList, Response response) {
        expandableListView_search.onRefreshComplete();
        HttpErrorCheck.checkResponse("我的线索列表：", response, ll_loading);
        try {
            if (!isPullDown) {
                if (clueList.data.records == null)
                    Toast("没有更多的数据了");
                listData.addAll(clueList.data.records);
            } else {
                if (clueList.data.records == null)
                    ll_loading.setStatus(LoadingLayout.Empty);
                listData = clueList.data.records;
            }
            adapter.setAdapter();
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
            ClueListItem clueListItem = listData.get(position);
            Holder holder = null;
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.item_teamclue, null);
                holder = new Holder();
                holder.tv_company_name = (TextView) convertView.findViewById(R.id.tv_company_name);
                holder.tv_customer = (TextView) convertView.findViewById(R.id.tv_customer);
                holder.tv_time = (TextView) convertView.findViewById(R.id.tv_time);
                holder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
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

            return convertView;
        }

        class Holder {
            TextView tv_company_name; /* 公司名称 */
            TextView tv_customer;     /* 负责人 */
            TextView tv_time;         /* 跟进时间 */
            TextView tv_name;         /* 客户名称 */
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        hideInputKeyboard(edt_search);
    }
}
