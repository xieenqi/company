package com.loyo.oa.v2.activityui.customer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.customer.adapter.CustomerRepeatAdapter;
import com.loyo.oa.v2.activityui.customer.model.CustomerRepeatList;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.beans.PaginationX;
import com.loyo.oa.v2.customermanagement.api.CustomerService;
import com.loyo.oa.v2.network.DefaultSubscriber;
import com.loyo.oa.v2.tool.BaseActivity;

import java.util.HashMap;

/**
 * 客户管理，查重
 * Created by yyy on 15/12/9.
 */
public class CustomerRepeat extends BaseActivity implements CustomerRepeatAdapter.PickInOnCallBack{

    private LinearLayout ll_showonly;
    private RelativeLayout img_title_left;
    private ImageView iv_clean;
    private ListView lv_list;
    private TextView tv_customer_onlyname;
    private EditText edt_search;
    private Intent mIntent;
    private CustomerRepeatAdapter adapter;
    private boolean isOk;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_repeat);
        initUi();
    }

    /**
     * 数据初始化
     */
    void initUi() {
        mIntent = getIntent();
        String customerName = mIntent.getStringExtra("name");
        ll_showonly = (LinearLayout) findViewById(R.id.ll_showonly);
        lv_list = (ListView) findViewById(R.id.lv_list);
        tv_customer_onlyname = (TextView) findViewById(R.id.tv_customer_onlyname);
        edt_search = (EditText) findViewById(R.id.edt_search);
//      tv_serach = (TextView) findViewById(R.id.tv_serach);
        iv_clean = (ImageView) findViewById(R.id.iv_clean);
        img_title_left = (RelativeLayout) findViewById(R.id.img_title_left);
        edt_search.setText(customerName);
        serachRepate(customerName);
        edt_search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    serachRepate(edt_search.getText().toString().trim());
                }
                return false;
            }
        });
        iv_clean.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                edt_search.setText("");
            }
        });
        ll_showonly.setOnClickListener(onClickListener);
//      tv_serach.setOnClickListener(onClickListener);
        img_title_left.setOnClickListener(onClickListener);
        //自动搜索
        edt_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                serachRepate(edt_search.getText().toString());
            }
        });
        //键盘搜索
        edt_search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    serachRepate(edt_search.getText().toString());
                }
                return false;
            }
        });
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(final View view) {
            switch (view.getId()) {
                case R.id.img_title_left:
                    finish();
                    break;
                case R.id.ll_showonly:
                    Intent intent = new Intent();
                    intent.putExtra("name", edt_search.getText().toString());
                    app.finishActivity((Activity) mContext, MainApp.ENTER_TYPE_LEFT, RESULT_OK, intent);
                    break;
                default:
                    break;
            }
        }
    };


    /**
     * 查重请求
     */
    void serachRepate(final String name) {
        if (name.isEmpty()) {
            Toast("搜索内容不能为空");
            return;
        }
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("pageIndex", 1);
        map.put("pageSize", 20);
        map.put("keyWords", name);
        CustomerService.getCustomerDuplicates(map)
                .subscribe(new DefaultSubscriber<PaginationX<CustomerRepeatList>>() {
                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        finish();
                    }

                    @Override
                    public void onNext(PaginationX<CustomerRepeatList> customerRepeatListPaginationX) {
                        setViewdata(customerRepeatListPaginationX);
                    }
                });
    }

    /**
     * 若没有相同数据，则展示ll_showonly,提示该名字可以创建
     */
    void setViewdata(final PaginationX<CustomerRepeatList> customerRepeatList) {

        if (customerRepeatList.getRecords().size() != 0) {
            for (int i = 0; i < customerRepeatList.getRecords().size(); i++) {
                if (customerRepeatList.getRecords().get(i).getName().equals(edt_search.getText().toString())) {
                    isOk = true;
                    break;
                } else {
                    isOk = false;
                }
            }
        } else {
            isOk = false;
        }

        if (!isOk) {
            ll_showonly.setVisibility(View.VISIBLE);
            String info = "该客户名不重复,点击  “" + edt_search.getText().toString().toString() + "”  创建该客户";
            SpannableString searchInfo = new SpannableString(info);
            searchInfo.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.title_bg1)),
                    13, 13 + edt_search.getText().toString().length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
            tv_customer_onlyname.setText(searchInfo);
        } else {
            ll_showonly.setVisibility(View.GONE);
        }

        adapter = new CustomerRepeatAdapter(customerRepeatList, CustomerRepeat.this,this);
        lv_list.setAdapter(adapter);

    }

    @Override
    public void pickEmbl() {
        serachRepate(edt_search.getText().toString().trim());
    }
}
