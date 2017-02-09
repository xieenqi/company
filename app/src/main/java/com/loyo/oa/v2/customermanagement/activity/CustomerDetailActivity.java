package com.loyo.oa.v2.customermanagement.activity;

import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.customer.model.Customer;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.customermanagement.adapter.CustomerPagerAdapter;
import com.loyo.oa.v2.customermanagement.api.CustomerService;
import com.loyo.oa.v2.customermanagement.fragment.AttachmentsFragment;
import com.loyo.oa.v2.customermanagement.fragment.ContactsFragment;
import com.loyo.oa.v2.customermanagement.fragment.FollowupsFragment;
import com.loyo.oa.v2.customermanagement.fragment.OpptunitiesFragment;
import com.loyo.oa.v2.customermanagement.fragment.OrdersFragment;
import com.loyo.oa.v2.customermanagement.fragment.TasksFragment;
import com.loyo.oa.v2.customermanagement.fragment.VisitsFragment;
import com.loyo.oa.v2.customermanagement.fragment.WorkflowsFragment;
import com.loyo.oa.v2.network.DefaultLoyoSubscriber;
import com.loyo.oa.v2.tool.BaseFragmentActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CustomerDetailActivity extends BaseFragmentActivity
        implements View.OnClickListener, AppBarLayout.OnOffsetChangedListener
{

    public static final String KEY_ID = "com.loyo.CustomerDetailActivity.KEY_ID";

    CustomerPagerAdapter adapter;
    String customerId;
    Customer customer;

    @BindView(R.id.img_title_left)  View img_title_left;
    @BindView(R.id.img_title_right) View img_title_right;
    @BindView(R.id.tv_title_1)      TextView tv_title_1;

    @BindView(R.id.customer_basic_info) ViewGroup basicInfoView;
    @BindView(R.id.customer_state)      ViewGroup customerStateView;
    @BindView(R.id.customer_tag)        ViewGroup customerTagView;

    @BindView(R.id.tv_customer_name)  TextView customerNameText;
    @BindView(R.id.tv_customer_state) TextView customerStateText;
    @BindView(R.id.tv_customer_tag)   TextView customerTagText;
    @BindView(R.id.tv_drop_reason)    TextView dropReasonText;

    @BindView(R.id.state_editable) ImageView stateEditableVew;
    @BindView(R.id.tag_editable)   ImageView tagEditableVew;



    @OnClick(R.id.img_title_left) void onBack() {
        onBackPressed();
    }
    @OnClick(R.id.img_title_right) void onMore() {

    }

    @OnClick(R.id.customer_basic_info) void showInfo() {

    }
    @OnClick(R.id.customer_state) void editState() {

    }
    @OnClick(R.id.customer_tag) void editTag() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_detail);
        ButterKnife.bind(this);
        img_title_left.setOnTouchListener(Global.GetTouch());
        img_title_right.setOnTouchListener(Global.GetTouch());
        basicInfoView.setOnTouchListener(Global.GetTouch());
        customerStateView.setOnTouchListener(Global.GetTouch());
        customerTagView.setOnTouchListener(Global.GetTouch());
        tv_title_1.setText("客户详情");

        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        if (viewPager != null) {
            setupViewPager(viewPager);
        }

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        this.loadIntentData();
        this.getData(customerId);
    }

    void loadIntentData() {
        if (getIntent() != null) {
            Bundle bundle = getIntent().getExtras();
            if (bundle != null) {
                customerId = bundle.getString(KEY_ID);
            }
        }
    }

    void loadCustomer() {
        this.customerNameText.setText(customer.name);
        this.customerStateText.setText("状态：");
        this.customerTagText.setText("标签：" + customer.displayTagString());
        this.dropReasonText.setText("丢公海原因：");
    }

    public void getData(String id) {
        if (TextUtils.isEmpty(id)) {
            return;
        }
        CustomerService.getCustomerDetailById(id)
                .subscribe(new DefaultLoyoSubscriber<Customer>() {
                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                    }

                    @Override
                    public void onNext(Customer customer) {
                        CustomerDetailActivity.this.customer = customer;
                        CustomerDetailActivity.this.loadCustomer();
                    }
                });
    }

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        Log.v("tag", v.getId() + "");
    }

    private void setupViewPager(ViewPager viewPager) {
        adapter = new CustomerPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new AttachmentsFragment(), "附件 8");

        adapter.addFragment(new FollowupsFragment(), "跟进 1");
        adapter.addFragment(new ContactsFragment(), "联系人 2");
        adapter.addFragment(new VisitsFragment(), "拜访 3");

        adapter.addFragment(new OpptunitiesFragment(), "机会 4");
        adapter.addFragment(new OrdersFragment(), "订单 5");
        adapter.addFragment(new TasksFragment(), "任务 6");

        adapter.addFragment(new WorkflowsFragment(), "审批 7");

        viewPager.setAdapter(adapter);
    }

    /**
     * Called when the {@link AppBarLayout}'s layout offset has been changed. This allows
     * child views to implement custom behavior based on the offset (for instance pinning a
     * view at a certain y value).
     *
     * @param appBarLayout   the {@link AppBarLayout} which offset has changed
     * @param verticalOffset the vertical offset for the parent {@link AppBarLayout}, in px
     */
    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
        if (verticalOffset >= 0) {
            adapter.setPullToRefreshEnabled(true);
        }
        else {
            adapter.setPullToRefreshEnabled(false);
        }
    }
}
