package com.loyo.oa.v2.customermanagement.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;

import com.loyo.oa.common.utils.LoyoUIThread;
import com.loyo.oa.common.utils.UmengAnalytics;
import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.customer.CustomerInfoActivity_;
import com.loyo.oa.v2.activityui.customer.CustomerLabelCopyActivity;
import com.loyo.oa.v2.activityui.customer.CustomerStatePickerActivity;
import com.loyo.oa.v2.activityui.customer.LoseCommonCustomerReasonActivity;
import com.loyo.oa.v2.activityui.customer.event.MyCustomerRushEvent;
import com.loyo.oa.v2.activityui.customer.model.Customer;
import com.loyo.oa.v2.activityui.customer.model.NewTag;
import com.loyo.oa.v2.activityui.customer.model.TagItem;
import com.loyo.oa.v2.activityui.signin.bean.SigninPictures;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.common.ExtraAndResult;
import com.loyo.oa.v2.common.FinalVariables;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.common.event.AppBus;
import com.loyo.oa.v2.customermanagement.adapter.CustomerPagerAdapter;
import com.loyo.oa.v2.customermanagement.api.CustomerService;
import com.loyo.oa.v2.customermanagement.fragment.AttachmentsFragment;
import com.loyo.oa.v2.customermanagement.fragment.ContactsFragment;
import com.loyo.oa.v2.customermanagement.fragment.CustomerChildFragment;
import com.loyo.oa.v2.customermanagement.fragment.DropCustomerDeadlineFragment;
import com.loyo.oa.v2.customermanagement.fragment.FollowupsFragment;
import com.loyo.oa.v2.customermanagement.fragment.OpportunitiesFragment;
import com.loyo.oa.v2.customermanagement.fragment.OrdersFragment;
import com.loyo.oa.v2.customermanagement.fragment.TasksFragment;
import com.loyo.oa.v2.customermanagement.fragment.VisitsFragment;
import com.loyo.oa.v2.customermanagement.fragment.WorkFlowsFragment;
import com.loyo.oa.v2.customermanagement.model.DropDeadlineModel;
import com.loyo.oa.v2.customview.ActionSheetDialog;
import com.loyo.oa.v2.network.DefaultLoyoSubscriber;
import com.loyo.oa.v2.permission.CustomerAction;
import com.loyo.oa.v2.permission.PermissionManager;
import com.loyo.oa.v2.tool.BaseFragmentActivity;
import com.loyo.oa.v2.tool.Utils;

import org.greenrobot.eventbus.Subscribe;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.pedant.SweetAlert.SweetAlertDialog;

public class CustomerDetailActivity extends BaseFragmentActivity
    implements DropCustomerDeadlineFragment.CustomerDeadlineActionListener,
        CustomerChildFragment.OnTotalCountChangeListener
{

    public static final String KEY_ID = "com.loyo.CustomerDetailActivity.KEY_ID";

    CustomerPagerAdapter adapter;
    String customerId;
    Customer customer;
    boolean canEdit;
    boolean viewPagerInited;
    FollowupsFragment followupsFragment;
    VisitsFragment visitsFragment;
    OrdersFragment ordersFragment;

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

    @BindView(R.id.tab_mask) ImageView tabMask;



    @OnClick(R.id.img_title_left) void onBack() {
        onBackPressed();
    }
    @OnClick(R.id.img_title_right) void onActionsheet() {
        boolean canDelete = PermissionManager.getInstance().hasCustomerAuthority(customer.relationState,
                customer.state, CustomerAction.DELETE);
        boolean canDump = PermissionManager.getInstance().hasCustomerAuthority(customer.relationState,
                customer.state, CustomerAction.DUMP);

        if (!canDelete && !canDump) {
            return;
        }

        ActionSheetDialog dialog = new ActionSheetDialog(this).builder();
        if (canDump)
            dialog.addSheetItem("投入公海", ActionSheetDialog.SheetItemColor.Blue, new ActionSheetDialog.OnSheetItemClickListener() {
                @Override
                public void onClick(int which) {
                    UmengAnalytics.umengSend(CustomerDetailActivity.this, UmengAnalytics.customerTopublic);
                    HashMap<String, Object> map = new HashMap<>();
                    map.put("key", "cus_reason_switcher");
                    showLoading2("");
                    CustomerService.getSigninUploadPhotoConfig(map).subscribe(new DefaultLoyoSubscriber<SigninPictures>(hud, true) {
                        @Override
                        public void onNext(SigninPictures signinPictures) {
                            if (signinPictures != null && signinPictures.value.equals("1")) {
                                Intent intent = new Intent(CustomerDetailActivity.this, LoseCommonCustomerReasonActivity.class);
                                intent.putExtra(ExtraAndResult.EXTRA_ID, customer.getId());
                                startActivityForResult(intent, ExtraAndResult.REQUSET_COPY_PERSONS);
                                overridePendingTransition(R.anim.enter_righttoleft, R.anim.exit_righttoleft);
                                cancelLoading2();
                            } else {
                                sweetAlertDialogView.alertHandle(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                                        dismissSweetAlert();
                                    }
                                }, new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                                        dismissSweetAlert();
                                        CustomerService.dumpCustomer(customer.getId())
                                                .subscribe(new DefaultLoyoSubscriber<Customer>(hud) {
                                                    @Override
                                                    public void onNext(Customer customer) {
                                                        MyCustomerRushEvent myCustomerRushEvent=new MyCustomerRushEvent();
                                                        myCustomerRushEvent.eventCode=MyCustomerRushEvent.EVENT_CODE_DEL;//投入公海，就是从前面的列表删除
                                                        AppBus.getInstance().post(myCustomerRushEvent);
                                                        finish();
                                                    }
                                                });
                                    }
                                }, "提示", "确定将客户 \"" + customer.name + "\" 投入公海吗?");
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            super.onError(e);
                        }
                    });

                }
            });
        if (canDelete)
            dialog.addSheetItem("删除", ActionSheetDialog.SheetItemColor.Red, new ActionSheetDialog.OnSheetItemClickListener() {
                @Override
                public void onClick(int which) {
                    sweetAlertDialogView.alertHandle(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                            dismissSweetAlert();
                        }
                    }, new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                            dismissSweetAlert();
                            showLoading2("");
                            CustomerService.deleteCustomer(customer.getId())
                                    .subscribe(new DefaultLoyoSubscriber<Customer>(hud) {
                                        @Override
                                        public void onNext(Customer customer) {
                                            MyCustomerRushEvent myCustomerRushEvent=new MyCustomerRushEvent();
                                            myCustomerRushEvent.eventCode=MyCustomerRushEvent.EVENT_CODE_DEL;
                                            AppBus.getInstance().post(myCustomerRushEvent);
                                            finish();
                                        }
                                    });
                        }
                    }, "提示", "你确定要删除客户?");
                    UmengAnalytics.umengSend(CustomerDetailActivity.this, UmengAnalytics.customerDelete);
                }
            });
        dialog.show();
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
        Intent mIntent = new Intent(this, CustomerStatePickerActivity.class);
        mIntent.putExtra("canEdit", canEdit);
        mIntent.putExtra("fromPage", 0);
        if (null != customer.statusId) {
            TagItem item = new TagItem();
            item.setName(customer.statusName);
            item.setId(customer.statusId);
            mIntent.putExtra("state", item);
        }
        mIntent.putExtra("customerId", customer.getId());
        startActivity(mIntent);
        UmengAnalytics.umengSend(this, UmengAnalytics.customerEditTag);
    }
    @OnClick(R.id.customer_tag) void editTag() {
        Intent mIntent = new Intent(this, CustomerLabelCopyActivity.class);
        mIntent.putExtra("canEdit", canEdit);
        mIntent.putExtra("fromPage", 0);
        if (null != customer.tags) {
            mIntent.putExtra("tagitems", Utils.convertTagItems(customer.tags));
        }
        mIntent.putExtra("customerId", customer.getId());
        startActivity(mIntent);
        UmengAnalytics.umengSend(this, UmengAnalytics.customerEditTag);
    }

    @OnClick(R.id.ll_warn) void onDropDeadline() {
        FragmentManager fm = getSupportFragmentManager();
        DropCustomerDeadlineFragment fragment =
                DropCustomerDeadlineFragment.newInstance(DropDeadlineModel.getDeadlineModel(customer),
                        this);
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

    void loadCustomer(boolean needInitPager) {
        canEdit = PermissionManager.getInstance().hasCustomerAuthority(
                customer.relationState,
                customer.state,
                CustomerAction.EDIT);

        this.customerNameText.setText(customer.name);
        this.customerStateText.setText("状态：" + customer.statusName);
        this.customerTagText.setText("标签：" + customer.displayTagString());
        if (customer.state == Customer.DumpedCustomer) {
            dropReasonText.setVisibility(View.VISIBLE);
            String recycleReason = customer.recycleReason;
            if (TextUtils.isEmpty(recycleReason)) {
                recycleReason = "无";
            }
            dropReasonText.setText("丢公海原因：" + recycleReason);
        }
        else {
            dropReasonText.setVisibility(View.GONE);
        }
        if (customer.hasDropRemind()) {
            warnView.setVisibility(View.VISIBLE);
            recycleRemindText.setText(customer.getFormattedDropRemind());
        }
        else {
            warnView.setVisibility(View.GONE);

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

        if (needInitPager) {
            if (viewPager != null) {
                setupViewPager(viewPager);
            }
            tabLayout.setupWithViewPager(viewPager);
            tabLayout.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
                @Override
                public void onScrollChanged() {
                    if (tabLayout.getChildCount() > 0) {
                        View view = tabLayout.getChildAt(tabLayout.getChildCount()-1);

                        int diff = (view.getRight()-(tabLayout.getWidth()  +tabLayout.getScrollX()));
                        if( diff == 0 )
                        {
                            tabMask.setVisibility(View.GONE);
                        }
                        else {
                            tabMask.setVisibility(View.VISIBLE);
                        }
                    }
                }
            });
            viewPagerInited = true;
        }
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
                        CustomerDetailActivity.this.loadCustomer(!viewPagerInited);
                    }
                });
    }

    private void setupViewPager(ViewPager viewPager) {
        adapter = new CustomerPagerAdapter(getSupportFragmentManager());

        followupsFragment = new FollowupsFragment();
        followupsFragment.setCustomer(customer);
        followupsFragment.index = 0;
        followupsFragment.setListener(this);
        adapter.addFragment(followupsFragment);

        ContactsFragment contactsFragment = new ContactsFragment();
        contactsFragment.setCustomer(customer);
        contactsFragment.index = 1;
        contactsFragment.setListener(this);
        adapter.addFragment(contactsFragment);

        visitsFragment = new VisitsFragment();
        visitsFragment.setCustomer(customer);
        visitsFragment.index = 2;
        visitsFragment.setListener(this);
        adapter.addFragment(visitsFragment);

        OpportunitiesFragment opportunitiesFragment = new OpportunitiesFragment();
        opportunitiesFragment.setCustomer(customer);
        opportunitiesFragment.index = 3;
        opportunitiesFragment.setListener(this);
        adapter.addFragment(opportunitiesFragment);

        ordersFragment = new OrdersFragment();
        ordersFragment.setCustomer(customer);
        ordersFragment.index = 4;
        ordersFragment.setListener(this);
        adapter.addFragment(ordersFragment);

        TasksFragment tasksFragment = new TasksFragment();
        tasksFragment.setCustomer(customer);
        tasksFragment.index = 5;
        tasksFragment.setListener(this);
        adapter.addFragment(tasksFragment);

        WorkFlowsFragment workFlowsFragment = new WorkFlowsFragment();
        workFlowsFragment.setCustomer(customer);
        workFlowsFragment.index = 6;
        workFlowsFragment.setListener(this);
        adapter.addFragment(workFlowsFragment);

        AttachmentsFragment attachmentsFragment = new AttachmentsFragment();
        attachmentsFragment.setCustomer(customer);
        attachmentsFragment.index = 7;
        attachmentsFragment.setListener(this);
        adapter.addFragment(attachmentsFragment);

        viewPager.setAdapter(adapter);
    }

    @Subscribe
    public void onMyCustomerPushEvent(MyCustomerRushEvent event) {
        if (! customer.getId().equals(event.session)) {
            // 不是这个客户的修改，直接退出
            return;
        }
        if(MyCustomerRushEvent.EVENT_CODE_UPDATE == event.eventCode){
            //更新客户信息
            if(MyCustomerRushEvent.EVENT_SUB_CODE_INFO==event.subCode){
                Customer updateCus=event.data;
                customer.name    =updateCus.name;
                customer.summary =updateCus.summary;
                customer.owner   = updateCus.owner;
                customer.members = updateCus.members;
                customer.tags    = updateCus.tags;
                customer.loc     = updateCus.loc;
                customer.position = updateCus.position;
                customer.extDatas = updateCus.extDatas;
                customer.regional = updateCus.regional;
                loadCustomer(false);
            }else if(MyCustomerRushEvent.EVENT_SUB_CODE_LABEL==event.subCode){
                //更新label
                customer.tags = event.data.tags;
                loadCustomer(false);
            }
            else if(MyCustomerRushEvent.EVENT_SUB_CODE_STATE == event.subCode){
                //更新label
                NewTag tag = event.data.tags.get(0);
                customer.statusName = tag.itemName;
                customer.statusId = tag.itemId;
                loadCustomer(false);
            }
        }
    }


    void selectPageAtIndex(int index) {
        tabLayout.setScrollPosition(index, 0f, true);
        viewPager.setCurrentItem(index);
    }

    /**
     * DropCustomerDeadlineFragment.CustomerDeadlineActionListener
     */
    @Override
    public void onAddFollowup() {
        selectPageAtIndex(0/* 跟进 */);
        LoyoUIThread.runAfterDelay(new Runnable() {
            @Override
            public void run() {
                if (CustomerDetailActivity.this.followupsFragment != null) {
                    followupsFragment.onAddFollowup();
                }
            }
        }, 300 /* 完成加载页面和动画时间 */);
    }

    @Override
    public void onAddVisit() {
        selectPageAtIndex(2/* 拜访 */);
        LoyoUIThread.runAfterDelay(new Runnable() {
            @Override
            public void run() {
                if (CustomerDetailActivity.this.visitsFragment != null) {
                    visitsFragment.onAddVisit();
                }
            }
        }, 300 /* 完成加载页面和动画时间 */);
    }

    @Override
    public void onAddCall() {
        selectPageAtIndex(1/* 联系人 */);
    }

    @Override
    public void onAddOrder() {
        selectPageAtIndex(4/* 订单 */);
        LoyoUIThread.runAfterDelay(new Runnable() {
            @Override
            public void run() {
                if (CustomerDetailActivity.this.ordersFragment != null) {
                    ordersFragment.onAddOrder();
                }
            }
        }, 300 /* 完成加载页面和动画时间 */);
    }

    /**
     * CustomerChildFragment.OnTotalCountChangeListener
     */
    @Override
    public void onTotalCountChange(CustomerChildFragment fragment, int index) {
        TabLayout.Tab tab = tabLayout.getTabAt(index);
        if (tab != null) {
            tab.setText(fragment.getTitle());
        }
    }
}
