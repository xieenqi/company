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

import com.library.module.widget.loading.LoadingLayout;
import com.loyo.oa.common.utils.UmengAnalytics;
import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.customer.CustomerInfoActivity_;
import com.loyo.oa.v2.activityui.customer.CustomerLabelCopyActivity;
import com.loyo.oa.v2.activityui.customer.CustomerStatusSingleSelectActivity;
import com.loyo.oa.v2.activityui.customer.LoseCommonCustomerReasonActivity;
import com.loyo.oa.v2.activityui.customer.event.MyCustomerRushEvent;
import com.loyo.oa.v2.activityui.customer.model.Contact;
import com.loyo.oa.v2.activityui.customer.model.Customer;
import com.loyo.oa.v2.activityui.customer.model.CustomerStatusModel;
import com.loyo.oa.v2.activityui.customer.model.MembersRoot;
import com.loyo.oa.v2.activityui.customer.model.NewTag;
import com.loyo.oa.v2.activityui.followup.FollowAddActivity;
import com.loyo.oa.v2.activityui.order.OrderAddActivity;
import com.loyo.oa.v2.activityui.order.OrderDetailActivity;
import com.loyo.oa.v2.activityui.signin.SignInActivity;
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
import com.loyo.oa.v2.customermanagement.fragment.CustomerBasicInfoHeader;
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
import com.loyo.oa.v2.network.LoyoErrorChecker;
import com.loyo.oa.v2.permission.BusinessOperation;
import com.loyo.oa.v2.permission.CustomerAction;
import com.loyo.oa.v2.permission.PermissionManager;
import com.loyo.oa.v2.tool.BaseFragmentActivity;
import com.loyo.oa.v2.tool.Utils;

import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.pedant.SweetAlert.SweetAlertDialog;

public class CustomerDetailActivity extends BaseFragmentActivity
        implements DropCustomerDeadlineFragment.CustomerDeadlineActionListener,
        CustomerChildFragment.OnTotalCountChangeListener, CustomerBasicInfoHeader.CustomerBasicInfoHeaderListener{

    public static final String KEY_ID = "com.loyo.CustomerDetailActivity.KEY_ID";
    public static final int EXTRA_CUSTOMER_EDIT_STATUS = 1;//标签改变

    CustomerPagerAdapter adapter;
    String customerId;
    Customer customer;
    boolean canEdit;
    boolean viewPagerInited;
    FollowupsFragment followupsFragment;
    VisitsFragment visitsFragment;
    OrdersFragment ordersFragment;
    CustomerBasicInfoHeader headerFragment;

    @BindView(R.id.viewpager)
    ViewPager viewPager;
    @BindView(R.id.tabs)
    TabLayout tabLayout;

    @BindView(R.id.img_title_left)
    View img_title_left;
    @BindView(R.id.img_title_right)
    View img_title_right;
    @BindView(R.id.tv_title_1)
    TextView tv_title_1;

    @BindView(R.id.ll_loading)
    LoadingLayout ll_loading;

    @BindView(R.id.basic_info_container) ViewGroup basicInfoContainer;

    @BindView(R.id.tab_mask)
    ImageView tabMask;
    @BindView(R.id.tab_mask_left)
    ImageView tabMaskLeft;
    @BindView(R.id.customer_pick)
    ViewGroup customerPick;


    @OnClick(R.id.img_title_left)
    void onBack() {
        onBackPressed();
    }

    @OnClick(R.id.img_title_right)
    void onActionsheet() {
        if (customer == null) {
            return;
        }
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
                                                        MyCustomerRushEvent myCustomerRushEvent = new MyCustomerRushEvent();
                                                        myCustomerRushEvent.eventCode = MyCustomerRushEvent.EVENT_CODE_DEL;//投入公海，就是从前面的列表删除
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
                                            MyCustomerRushEvent myCustomerRushEvent = new MyCustomerRushEvent();
                                            myCustomerRushEvent.eventCode = MyCustomerRushEvent.EVENT_CODE_DEL;
                                            AppBus.getInstance().post(myCustomerRushEvent);
                                            onBackPressed();
                                        }
                                    });
                        }
                    }, "提示", "你确定要删除客户?");
                    UmengAnalytics.umengSend(CustomerDetailActivity.this, UmengAnalytics.customerDelete);
                }
            });
        dialog.show();
    }

    @OnClick(R.id.customer_pick)
    void onPickCustomer() {
        final String id = customer.getId();
        CustomerService.pickInCustomer(id)
                .subscribe(new DefaultLoyoSubscriber<Customer>() {
                    @Override
                    public void onNext(Customer customer) {
                        ll_loading.setStatus(LoadingLayout.Loading);
                        getData(id);
                        Toast("挑入客户成功");
                        //通知列表页面删除
                        MyCustomerRushEvent myCustomerRushEvent = new MyCustomerRushEvent();
                        myCustomerRushEvent.eventCode = MyCustomerRushEvent.EVENT_CODE_DEL;
                        AppBus.getInstance().post(myCustomerRushEvent);
                    }
                });
        UmengAnalytics.umengSend(CustomerDetailActivity.this, UmengAnalytics.frompublicPublicDetail);
    }

    /**
     * 加载页面和数据
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_detail);
        ButterKnife.bind(this);
        headerFragment = new CustomerBasicInfoHeader();
        headerFragment.setListenerRef(this);
        FragmentManager fm = getSupportFragmentManager();
        fm.beginTransaction()
                .add(R.id.basic_info_container, headerFragment)
                .commit();

        img_title_left.setOnTouchListener(Global.GetTouch());
        img_title_right.setOnTouchListener(Global.GetTouch());

        tv_title_1.setText("客户详情");
        ll_loading.setStatus(LoadingLayout.Loading);
        ll_loading.setOnReloadListener(new LoadingLayout.OnReloadListener() {
            @Override
            public void onReload(View v) {
                ll_loading.setStatus(LoadingLayout.Loading);
                getData(customerId);
            }
        });

        this.loadIntentData();
        CustomerService.getMembersRoot()
                .subscribe(new DefaultLoyoSubscriber<MembersRoot>(LoyoErrorChecker.SILENCE) {
                    @Override
                    public void onNext(MembersRoot membersRoot) {
                        PermissionManager.getInstance().loadCRMConfig(membersRoot);
                    }
                });
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
        //判断有没有查看权限
        if (!PermissionManager.getInstance().hasCustomerAuthority(customer.relationState,
                customer.state, CustomerAction.PREVIEW)) {
            //在列表页面，删除刚才点击那一条
            MyCustomerRushEvent myCustomerRushEvent = new MyCustomerRushEvent();
            myCustomerRushEvent.eventCode = MyCustomerRushEvent.EVENT_CODE_DEL;
            AppBus.getInstance().post(myCustomerRushEvent);
            sweetAlertDialogView.alertMessageClick(new SweetAlertDialog.OnSweetClickListener() {
                @Override
                public void onClick(SweetAlertDialog sweetAlertDialog) {
                    dismissSweetAlert();
                    finish();
                }
            }, "提示", "你已没有此客户查看权限");
            return;
        }

        canEdit = PermissionManager.getInstance().hasCustomerAuthority(
                customer.relationState,
                customer.state,
                CustomerAction.EDIT);

        boolean canDelete = PermissionManager.getInstance().hasCustomerAuthority(customer.relationState,
                customer.state, CustomerAction.DELETE);
        boolean canDump = PermissionManager.getInstance().hasCustomerAuthority(customer.relationState,
                customer.state, CustomerAction.DUMP);
        boolean canPickIn = PermissionManager.getInstance().hasCustomerAuthority(customer.relationState,
                customer.state, CustomerAction.PICK_IN);
        boolean needPickIn = canPickIn && customer.state == Customer.DumpedCustomer;

        if (!canDelete && !canDump) {
            img_title_right.setVisibility(View.INVISIBLE);
        } else {
            img_title_right.setVisibility(View.VISIBLE);
        }

        customerPick.setVisibility(needPickIn ? View.VISIBLE : View.GONE);

        customerId = customer.getId();

        headerFragment.setCustomer(customer);
        headerFragment.loadCustomer();

        if (!viewPagerInited) {
            if (viewPager != null) {
                setupViewPager(viewPager);
            }
            tabLayout.setupWithViewPager(viewPager);
            tabLayout.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
                @Override
                public void onScrollChanged() {
                    if (tabLayout.getChildCount() > 0) {
                        View view = tabLayout.getChildAt(tabLayout.getChildCount() - 1);

                        int diff = (view.getRight() - (tabLayout.getWidth() + tabLayout.getScrollX()));
                        if (diff == 0) {
                            tabMask.setVisibility(View.GONE);
                        } else {
                            tabMask.setVisibility(View.VISIBLE);
                        }
                    }
                }
            });
            viewPagerInited = true;
        } else {
            for (CustomerChildFragment fragment : adapter.mFragments) {
                fragment.reloadWithCustomer(customer);
            }
        }
    }

    public void getData(String id) {
        if (TextUtils.isEmpty(id)) {
            return;
        }
        CustomerService.getCustomerDetailById(id)
                .subscribe(new DefaultLoyoSubscriber<Customer>(ll_loading) {
                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                    }

                    @Override
                    public void onNext(Customer customer) {
                        ll_loading.setStatus(LoadingLayout.Success);
                        CustomerDetailActivity.this.customer = customer;
                        CustomerDetailActivity.this.loadCustomer();
                        //更新列表丢公海提醒数据
                        MyCustomerRushEvent myCustomerRushEvent = new MyCustomerRushEvent(CustomerDetailActivity.this.customer);
                        myCustomerRushEvent.eventCode           = MyCustomerRushEvent.EVENT_CODE_UPDATE;
                        myCustomerRushEvent.subCode             = MyCustomerRushEvent.EVENT_SUB_CODE_RECYCLER;
                        myCustomerRushEvent.session             = customer.getId();
                        AppBus.getInstance().post(myCustomerRushEvent);
                    }
                });
    }

    public void refreshDropRemind() {
        if (customer == null || TextUtils.isEmpty(customer.getId())) {
            return;
        }
        ll_loading.setStatus(LoadingLayout.Loading);
        getData(customer.getId());
//        CustomerService.getCustomerDropRemindById(customer.getId())
//                .subscribe(new DefaultLoyoSubscriber<DropRemind>(hud) {
//                    @Override
//                    public void onNext(DropRemind dropRemind) {
//                        CustomerDetailActivity.this.customer.activityRemind = dropRemind.activityRemind;
//                        CustomerDetailActivity.this.customer.orderRemind = dropRemind.orderRemind;
//                        CustomerDetailActivity.this.customer.activityRecycleRemind = dropRemind.activityRecycleRemind;
//                        CustomerDetailActivity.this.customer.orderRecycleRemind = dropRemind.orderRecycleRemind;
//                        //更新列表丢公海提醒数据
//                        MyCustomerRushEvent myCustomerRushEvent = new MyCustomerRushEvent(CustomerDetailActivity.this.customer);
//                        myCustomerRushEvent.eventCode           = MyCustomerRushEvent.EVENT_CODE_UPDATE;
//                        myCustomerRushEvent.subCode             = MyCustomerRushEvent.EVENT_SUB_CODE_RECYCLER;
//                        myCustomerRushEvent.session             = customer.getId();
//                        AppBus.getInstance().post(myCustomerRushEvent);
//                        CustomerDetailActivity.this.loadCustomer(!viewPagerInited);
//                    }
//                });
    }

    private void setupViewPager(ViewPager viewPager) {
        adapter = new CustomerPagerAdapter(getSupportFragmentManager());
        followupsFragment =
                (FollowupsFragment)adapter.addFragment(new FollowupsFragment(customer, 0, this, "跟进"));
        adapter.addFragment(new ContactsFragment(customer, 1, this, "联系人"));
        visitsFragment =
                (VisitsFragment)adapter.addFragment(new VisitsFragment(customer, 2, this, "拜访"));
        adapter.addFragment(new OpportunitiesFragment(customer, 3, this, "机会"));
        ordersFragment =
                (OrdersFragment)adapter.addFragment(new OrdersFragment(customer, 4, this, "订单"));
        adapter.addFragment(new TasksFragment(customer, 5, this, "任务"));
        adapter.addFragment(new WorkFlowsFragment(customer, 6, this, "审批"));
        adapter.addFragment(new AttachmentsFragment(customer, 7, this, "附件"));
        viewPager.setAdapter(adapter);
    }

    /**
     * Event & ActivityResult
     */

    @Subscribe
    public void onMyCustomerPushEvent(MyCustomerRushEvent event) {
        if (!customer.getId().equals(event.session)) {
            // 不是这个客户的修改，直接退出
            return;
        }
        if (MyCustomerRushEvent.EVENT_CODE_UPDATE == event.eventCode) {
            //更新客户信息
            if (MyCustomerRushEvent.EVENT_SUB_CODE_INFO == event.subCode) {
                Customer updateCus     = event.data;
                customer.name          = updateCus.name;
                customer.summary       = updateCus.summary;
                customer.owner         = updateCus.owner;
                customer.members       = updateCus.members;
                customer.tags          = updateCus.tags;
                customer.loc           = updateCus.loc;
                customer.position      = updateCus.position;
                customer.extDatas      = updateCus.extDatas;
                customer.regional      = updateCus.regional;
                customer.statusId      = updateCus.statusId;
                customer.statusName    = updateCus.statusName;
                customer.state         = updateCus.state;
                customer.relationState = updateCus.relationState;
                loadCustomer();
            } else if (MyCustomerRushEvent.EVENT_SUB_CODE_LABEL == event.subCode) {
                if (!"note".equals(event.request + "")) return;
                //更新label 
                customer.tags = event.data.tags;
                loadCustomer();
            } else if (MyCustomerRushEvent.EVENT_SUB_CODE_LTC == event.subCode) {
                Customer updateCus = event.data;
                //更新label
                customer.statusName = updateCus.statusName;
                customer.statusId   = updateCus.statusId;
                customer.tags       = updateCus.tags;
                customer.contacts   = updateCus.contacts;
                ((CustomerChildFragment)adapter.getItem(1)).reloadWithCustomer(customer);//更新联系人
                loadCustomer();
                refreshDropRemind();

            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (RESULT_OK != resultCode) return;
        switch (requestCode) {
            case ExtraAndResult.REQUSET_COPY_PERSONS:
                onBackPressed();
                break;
            //修改状态
            case EXTRA_CUSTOMER_EDIT_STATUS:
                //TODO 这里有没有直接修改客户状态的接口呀，这样写，太难受了。

                CustomerStatusModel.CustomerStatusItemModel customerStatusItemModel = (CustomerStatusModel.CustomerStatusItemModel) data.getSerializableExtra("data");
                customer.statusId = customerStatusItemModel.id;
                customer.statusName = customerStatusItemModel.name;
                String tid = data.getStringExtra("tid");
                showLoading2("");
                CustomerService.setCusLabel(customer.id, convertNewTags(tid))
                        .subscribe(new DefaultLoyoSubscriber<Contact>(hud) {
                            @Override
                            public void onNext(Contact contact) {
                                refreshDropRemind();
                            }
                        });
                loadCustomer();
                //更新列表数据
                MyCustomerRushEvent myCustomerRushEvent = new MyCustomerRushEvent(customer);
                myCustomerRushEvent.eventCode = MyCustomerRushEvent.EVENT_CODE_UPDATE;
                myCustomerRushEvent.subCode = MyCustomerRushEvent.EVENT_SUB_CODE_STATE;
                myCustomerRushEvent.session = customer.getId();
                AppBus.getInstance().post(myCustomerRushEvent);
                break;
        }
    }

    private ArrayList<NewTag> convertNewTags(String tid) {
        ArrayList<NewTag> tags = new ArrayList<>();
        NewTag tag = new NewTag();
        tag.setItemId(customer.statusId);
        tag.setItemName(customer.statusName);
        tag.settId(tid);
        tags.add(tag);
        return tags;
    }

    /**
     * 基本信息头部事件回调
     * CustomerBasicInfoHeader.CustomerBasicInfoHeaderListener
     */

    @Override
    public void onShowInfo() {
        if (customer == null) {
            return;
        }
        Bundle bundle = new Bundle();
        bundle.putSerializable("CustomerId", customer.getId());
        bundle.putBoolean("canEdit", canEdit);
        Class _class = CustomerInfoActivity_.class;
        app.startActivityForResult(this, _class, MainApp.ENTER_TYPE_RIGHT,
                FinalVariables.REQUEST_PREVIEW_CUSTOMER_INFO, bundle);
    }

    @Override
    public void onEditState() {
        Intent mIntent = new Intent(this, CustomerStatusSingleSelectActivity.class);
        mIntent.putExtra(CustomerStatusSingleSelectActivity.EXTRA_CURRENT, customer.statusId);
        startActivityForResult(mIntent, EXTRA_CUSTOMER_EDIT_STATUS);
        UmengAnalytics.umengSend(this, UmengAnalytics.customerEditTag);
    }

    @Override
    public void onEditTag() {
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

    @Override
    public void onDropDeadline() {
        FragmentManager fm = getSupportFragmentManager();
        DropCustomerDeadlineFragment fragment =
                DropCustomerDeadlineFragment.newInstance(DropDeadlineModel.getDeadlineModel(customer),
                        this);
        fragment.show(fm, "drop_deadline");
    }

    /**
     * 丢公海详细界面事件回调
     * DropCustomerDeadlineFragment.CustomerDeadlineActionListener
     */

    @Override
    public void onAddFollowup() {
        //selectPageAtIndex(0/* 跟进 */);
        if (CustomerDetailActivity.this.followupsFragment != null) {
            boolean canAdd = customer != null && customer.state == Customer.NormalCustomer &&
                    PermissionManager.getInstance().hasCustomerAuthority(customer.relationState,
                            customer.state, CustomerAction.FOLLOWUP_ADD);
            if (!canAdd) {
                sweetAlertDialogView.alertIcon("提示", "你没有写跟进权限");
            } else {
                Bundle bundle = new Bundle();
                bundle.putSerializable(Customer.class.getName(), customer);
                bundle.putInt(ExtraAndResult.DYNAMIC_ADD_ACTION, ExtraAndResult.DYNAMIC_ADD_CUSTOMER);
                bundle.putBoolean("isDetail", true);
                app.startActivityForResult(CustomerDetailActivity.this, FollowAddActivity.class,
                        MainApp.ENTER_TYPE_RIGHT, FollowupsFragment.ACTIVITIES_ADD, bundle);
                UmengAnalytics.umengSend(CustomerDetailActivity.this, UmengAnalytics.customerCheckFollowAddFollow);
            }
        }
    }

    @Override
    public void onAddVisit() {
        //selectPageAtIndex(2/* 拜访 */);
        if (CustomerDetailActivity.this.visitsFragment != null) {
            boolean
                    canAdd = customer != null &&
                    PermissionManager.getInstance().hasCustomerAuthority(customer.relationState,
                            customer.state, CustomerAction.VISIT);
            if (!canAdd) {
                sweetAlertDialogView.alertIcon("提示", "你没有拜访权限");
            } else if (!PermissionManager.getInstance().hasPermission(BusinessOperation.CUSTOMER_VISIT)) {
                sweetAlertDialogView.alertIcon(null, "此功能权限已关闭\n请联系管理员开启后再试!");
            } else {
                Bundle b = new Bundle();
                b.putSerializable("data", customer);
                app.startActivityForResult(CustomerDetailActivity.this,
                        SignInActivity.class, MainApp.ENTER_TYPE_RIGHT,
                        FinalVariables.REQUEST_CREATE_LEGWORK, b);
            }
        }
    }

    @Override
    public void onAddCall() {
        selectPageAtIndex(1/* 联系人 */);
    }

    @Override
    public void onAddOrder() {
        //selectPageAtIndex(4/* 订单 */);
        if (CustomerDetailActivity.this.ordersFragment != null) {
            boolean canAdd = customer != null &&
                    PermissionManager.getInstance().hasCustomerAuthority(
                            customer.relationState,
                            customer.state,
                            CustomerAction.ORDER_ADD);
            if (!canAdd) {
                sweetAlertDialogView.alertIcon("提示", "你没有添加客户相关订单权限");
            } else if (!PermissionManager.getInstance()
                    .hasPermission(BusinessOperation.ORDER_MANAGEMENT)) {
                sweetAlertDialogView.alertIcon(null, "此功能权限已关闭\n请联系管理员开启后再试!");
            } else {
                if (customerId != null) {
                    Bundle bundle;
                    bundle = new Bundle();
                    bundle.putString(ExtraAndResult.EXTRA_NAME, customer.name);
                    bundle.putString(ExtraAndResult.EXTRA_ID, customerId);
                    bundle.putInt("fromPage", OrderDetailActivity.ORDER_ADD);
                    app.startActivityForResult(CustomerDetailActivity.this, OrderAddActivity.class,
                            MainApp.ENTER_TYPE_RIGHT, 200, bundle);
                }
            }
        }
    }

    private void selectPageAtIndex(int index) {
        tabLayout.setScrollPosition(index, 0f, true);
        viewPager.setCurrentItem(index);
    }

    /**
     * 子页面数据更新回调
     * CustomerChildFragment.OnTotalCountChangeListener
     */
    
    @Override
    public void onTotalCountChange(CustomerChildFragment fragment, int index) {
        TabLayout.Tab tab = tabLayout.getTabAt(index);
        if (tab != null) {
            tab.setText(fragment.getTitle());
        }
    }

    @Override
    public void onDropRemindRefresh(CustomerChildFragment fragment) {
        refreshDropRemind();
    }
}
