package com.loyo.oa.v2.customermanagement.activity;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.customer.CustomerInfoActivity_;
import com.loyo.oa.v2.activityui.customer.model.Customer;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.common.FinalVariables;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.customermanagement.adapter.CustomerPagerAdapter;
import com.loyo.oa.v2.customermanagement.api.CustomerService;
import com.loyo.oa.v2.customermanagement.fragment.AttachmentsFragment;
import com.loyo.oa.v2.customermanagement.fragment.ContactsFragment;
import com.loyo.oa.v2.customermanagement.fragment.DropCustomerDeadlineFragment;
import com.loyo.oa.v2.customermanagement.fragment.FollowupsFragment;
import com.loyo.oa.v2.customermanagement.fragment.OpportunitiesFragment;
import com.loyo.oa.v2.customermanagement.fragment.OrdersFragment;
import com.loyo.oa.v2.customermanagement.fragment.TasksFragment;
import com.loyo.oa.v2.customermanagement.fragment.VisitsFragment;
import com.loyo.oa.v2.customermanagement.fragment.WorkFlowsFragment;
import com.loyo.oa.v2.customermanagement.model.DropDeadlineModel;
import com.loyo.oa.v2.network.DefaultLoyoSubscriber;
import com.loyo.oa.v2.permission.CustomerAction;
import com.loyo.oa.v2.permission.PermissionManager;
import com.loyo.oa.v2.tool.BaseFragmentActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CustomerDetailActivity extends BaseFragmentActivity
        implements View.OnClickListener
{

    public static final String KEY_ID = "com.loyo.CustomerDetailActivity.KEY_ID";

    CustomerPagerAdapter adapter;
    String customerId;
    Customer customer;
    boolean canEdit;

    @BindView(R.id.viewpager) ViewPager viewPager;
    @BindView(R.id.tabs)      TabLayout tabLayout;
    @BindView(R.id.ll_warn)   ViewGroup warnView;

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
    @BindView(R.id.tv_recyleRemind) TextView recycleRemindText;



    @OnClick(R.id.img_title_left) void onBack() {
        onBackPressed();
    }
    @OnClick(R.id.img_title_right) void onMore() {

    }

    @OnClick(R.id.customer_basic_info) void showInfo() {
        Bundle bundle = new Bundle();
        bundle.putSerializable("CustomerId", customer.getId());
        bundle.putBoolean("canEdit", canEdit);
        Class _class = CustomerInfoActivity_.class;
        app.startActivityForResult(this, _class, MainApp.ENTER_TYPE_RIGHT,
                FinalVariables.REQUEST_PREVIEW_CUSTOMER_INFO, bundle);
    }
    @OnClick(R.id.customer_state) void editState() {

    }
    @OnClick(R.id.customer_tag) void editTag() {
    }

    @OnClick(R.id.ll_warn) void onDropDeadline() {
        FragmentManager fm = getSupportFragmentManager();
        DropCustomerDeadlineFragment fragment =
                DropCustomerDeadlineFragment.newInstance(DropDeadlineModel.getDeadlineModel(customer));
        fragment.show(fm, "drop_deadline");
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
        warnView.setOnTouchListener(Global.GetTouch());
        tv_title_1.setText("客户详情");

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
        canEdit = PermissionManager.getInstance().hasCustomerAuthority(
                customer.relationState,
                customer.state,
                CustomerAction.EDIT);

        this.customerNameText.setText(customer.name);
        this.customerStateText.setText("状态：" + customer.statusName);
        this.customerTagText.setText("标签：" + customer.displayTagString());
        if (customer.state == Customer.DumpedCustomer) {
            dropReasonText.setVisibility(View.VISIBLE);
            String recyleReason = customer.recycleReason;
            if (TextUtils.isEmpty(recyleReason)) {
                recyleReason = "无";
            }
            dropReasonText.setText("丢公海原因：" + recyleReason);
        }
        else {
            dropReasonText.setVisibility(View.GONE);
        }
        if (TextUtils.isEmpty(customer.recycleRemind)) {
            warnView.setVisibility(View.GONE);
        }
        else {
            warnView.setVisibility(View.VISIBLE);
            recycleRemindText.setText(customer.recycleRemind);
        }
        if (canEdit) {
            stateEditableVew.setVisibility(View.VISIBLE);
            tagEditableVew.setVisibility(View.VISIBLE);
            customerStateView.setClickable(true);
            customerTagView.setClickable(true);
        }
        else {
            stateEditableVew.setVisibility(View.GONE);
            tagEditableVew.setVisibility(View.GONE);
            customerStateView.setClickable(false);
            customerTagView.setClickable(false);
        }

        if (viewPager != null) {
            setupViewPager(viewPager);
        }
        tabLayout.setupWithViewPager(viewPager);
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


        FollowupsFragment followupsFragment = new FollowupsFragment();
        followupsFragment.setCustomer(customer);
        adapter.addFragment(followupsFragment);

        ContactsFragment contactsFragment = new ContactsFragment();
        contactsFragment.setCustomer(customer);
        adapter.addFragment(contactsFragment);

        VisitsFragment visitsFragment = new VisitsFragment();
        visitsFragment.setCustomer(customer);
        adapter.addFragment(visitsFragment);

        OpportunitiesFragment opportunitiesFragment = new OpportunitiesFragment();
        opportunitiesFragment.setCustomer(customer);
        adapter.addFragment(opportunitiesFragment);

        OrdersFragment ordersFragment = new OrdersFragment();
        ordersFragment.setCustomer(customer);
        adapter.addFragment(ordersFragment);

        TasksFragment tasksFragment = new TasksFragment();
        tasksFragment.setCustomer(customer);
        adapter.addFragment(tasksFragment);

        WorkFlowsFragment workFlowsFragment = new WorkFlowsFragment();
        workFlowsFragment.setCustomer(customer);
        adapter.addFragment(workFlowsFragment);

        AttachmentsFragment attachmentsFragment = new AttachmentsFragment();
        attachmentsFragment.setCustomer(customer);
        adapter.addFragment(attachmentsFragment);

        viewPager.setAdapter(adapter);
    }
}
