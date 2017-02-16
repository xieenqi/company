package com.loyo.oa.v2.activityui.customer;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.library.module.widget.loading.LoadingLayout;
import com.loyo.oa.common.utils.DateTool;
import com.loyo.oa.common.utils.PermissionTool;
import com.loyo.oa.common.utils.UmengAnalytics;
import com.loyo.oa.hud.progress.LoyoProgressHUD;
import com.loyo.oa.hud.toast.LoyoToast;
import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.attachment.AttachmentActivity_;
import com.loyo.oa.v2.activityui.commonview.CommonHtmlUtils;
import com.loyo.oa.v2.activityui.customer.common.CommonMethod;
import com.loyo.oa.v2.activityui.customer.event.MyCustomerRushEvent;
import com.loyo.oa.v2.activityui.customer.model.Contact;
import com.loyo.oa.v2.activityui.customer.model.Customer;
import com.loyo.oa.v2.activityui.customer.model.MembersRoot;
import com.loyo.oa.v2.activityui.customer.model.NewTag;
import com.loyo.oa.v2.activityui.customer.presenter.impl.CustomerDetailinfoPresenterimpl;
import com.loyo.oa.v2.activityui.customer.viewcontrol.CustomerDetailinfoView;
import com.loyo.oa.v2.activityui.followup.FollowAddActivity;
import com.loyo.oa.v2.activityui.signin.SignInActivity;
import com.loyo.oa.v2.activityui.signin.bean.SigninPictures;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.common.Common;
import com.loyo.oa.v2.common.ExtraAndResult;
import com.loyo.oa.v2.common.FinalVariables;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.common.event.AppBus;
import com.loyo.oa.v2.customermanagement.api.CustomerService;
import com.loyo.oa.v2.customview.ActionSheetDialog;
import com.loyo.oa.v2.network.DefaultLoyoSubscriber;
import com.loyo.oa.v2.permission.BusinessOperation;
import com.loyo.oa.v2.permission.CustomerAction;
import com.loyo.oa.v2.permission.PermissionManager;
import com.loyo.oa.v2.tool.BaseActivity;
import com.loyo.oa.v2.tool.Utils;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.HashMap;

import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * com.loyo.oa.v2.activity
 * 描述 :【客户详情】 界面
 * 作者 : ykb
 * 时间 : 15/9/24.
 * //TODO 本页已经无用，可以删除
 */
@EActivity(R.layout.activity_customer_detail_info_new)
public class CustomerDetailInfoActivity extends BaseActivity implements CustomerDetailinfoView {

    @ViewById
    ViewGroup img_title_left, img_title_right, layout_customer_info, layout_contact, layout_send_sms,
            layout_call, layout_wiretel_call, layout_sale_activity, layout_visit, layout_task, layout_attachment,
            layout_approval,
            ll_sale, ll_order, layout_4;
    @ViewById
    TextView tv_title_1, tv_customer_name, tv_address, tv_tags, tv_contact_name,
            tv_contact_tel, customer_detail_wiretel, tv_sale_number, tv_visit_times, tv_task_count, tv_attachment_count,
            tv_approval_count,
            tv_follow_content, tv_follow_crecter_type, tv_contact_Number, tv_sale_count, tv_order_count,
            tv_content41, tv_content42, tv_content43;
    @ViewById
    ImageView img_public;
    @ViewById
    LoadingLayout ll_loading;

    Customer mCustomer;
    @Extra(CustomerSearchOrPickerActivity.EXTRA_PICKER_ID)
    String id;
    //    @Extra(ExtraAndResult.EXTRA_TYPE)  弃用了
//    public int customerType;//"1,我负责的", "2,我参与的", "3,团队客户","4.公海客户" 5.游客
    public boolean isPutOcen;
    public boolean canEdit;
    //拜访，福建，销售，任务等等的条数，还是需要重新加载数据，不然需要修改的地方太多，并且，写跟进，添加订单以后，丢公害时间会变化，所以，必须去服务器拉取。。
    public boolean needToRefresh=false;
    private Contact mContact;
    private RelativeLayout layout_wirete, layout_phone;
    private LinearLayout layout_gj, layout_sign;
    private LinearLayout layout_menu;
    private LinearLayout layout_defaultname;
    private TextView default_name;

    private ImageView iv_select_tag;
    private CustomerDetailinfoPresenterimpl mPresenter;
    private ArrayList<NewTag> mTagItems = new ArrayList<>();

    private String PhoneNum;

    @AfterViews
    void initViews() {
        tv_title_1.setText("客户详情");
        ll_loading.setStatus(LoadingLayout.Loading);
        ll_loading.setOnReloadListener(new LoadingLayout.OnReloadListener() {
            @Override
            public void onReload(View v) {
                ll_loading.setStatus(LoadingLayout.Loading);
                mPresenter.getData(id);
            }
        });
        layout_wirete = (RelativeLayout) findViewById(R.id.layout_wirete);
        layout_phone = (RelativeLayout) findViewById(R.id.layout_phone);
        layout_gj = (LinearLayout) findViewById(R.id.layout_gj);
        layout_sign = (LinearLayout) findViewById(R.id.layout_sign);
        layout_menu = (LinearLayout) findViewById(R.id.layout_menu);
        layout_defaultname = (LinearLayout) findViewById(R.id.layout_defaultname);
        iv_select_tag = (ImageView) findViewById(R.id.iv_select_tag);
        default_name = (TextView) findViewById(R.id.default_name);

        iv_select_tag.setOnTouchListener(Global.GetTouch());
        layout_sign.setOnTouchListener(Global.GetTouch());
        layout_gj.setOnTouchListener(Global.GetTouch());

        mPresenter = new CustomerDetailinfoPresenterimpl(mContext, this);

        if (PermissionManager.getInstance().hasPermission(BusinessOperation.VISIT_TIMELINE)) {
            layout_gj.setVisibility(View.VISIBLE);
        } else {
            layout_gj.setVisibility(View.GONE);
        }

        if (PermissionManager.getInstance().hasPermission(BusinessOperation.CUSTOMER_VISIT)) {
            layout_sign.setVisibility(View.VISIBLE);
        } else {
            layout_sign.setVisibility(View.GONE);
        }
        mPresenter.getData(id);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //目前修改的时间，必须客户信息，客户标签，通过eventBus更新数据变化，不重新去拉取
        //但是，拜访，订单，附件，销售等的个数，还是需要重新拉取数据来刷新，不然，改动太大。
        if(needToRefresh){
            mPresenter.getData(id);
            needToRefresh=false;
        }
    }

    /**
     * 默认联系人设置
     */
    private void initContact() {
        mContact = Utils.findDeault(mCustomer);
        if (null != mContact) {
            mPresenter.setDefaultContact(mContact.getId(), mCustomer.id);
            if (null == mContact.getTel() || TextUtils.isEmpty(mContact.getTel())) {
                tv_contact_tel.setText("--");
            } else {
                tv_contact_tel.setText(mContact.getTel());
            }

            if (null == mContact.getWiretel() || TextUtils.isEmpty(mContact.getWiretel())) {
                customer_detail_wiretel.setText("--");
            } else {
                customer_detail_wiretel.setText(mContact.getWiretel());
            }
            default_name.setText(mContact.getName());
            layout_phone.setVisibility(View.VISIBLE);
            layout_wirete.setVisibility(View.VISIBLE);
            layout_defaultname.setVisibility(View.VISIBLE);
        } else {
            layout_phone.setVisibility(View.GONE);
            layout_wirete.setVisibility(View.GONE);
            layout_defaultname.setVisibility(View.GONE);
        }
    }



    /**
     * 数据初始化
     */
    private void initData() {
        if (null == mCustomer) {
            finish();
            return;
        }

        if (!PermissionManager.getInstance().hasCustomerAuthority(mCustomer.relationState,
                mCustomer.state, CustomerAction.PREVIEW)) {
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

        boolean canDelete = PermissionManager.getInstance().hasCustomerAuthority(mCustomer.relationState,
                mCustomer.state, CustomerAction.DELETE);
        boolean canDump = PermissionManager.getInstance().hasCustomerAuthority(mCustomer.relationState,
                mCustomer.state, CustomerAction.DUMP);
        boolean canPickIn = PermissionManager.getInstance().hasCustomerAuthority(mCustomer.relationState,
                mCustomer.state, CustomerAction.PICK_IN);
        boolean needPickIn = canPickIn && mCustomer.state == Customer.DumpedCustomer;
        img_title_right.setVisibility((!canDelete && !canDump) ? View.GONE : View.VISIBLE);
        img_public.setEnabled(needPickIn);
        img_public.setVisibility(needPickIn ? View.VISIBLE : View.GONE);

        boolean canVisit = PermissionManager.getInstance().hasCustomerAuthority(mCustomer.relationState,
                mCustomer.state, CustomerAction.VISIT);
        layout_sign.setVisibility(canVisit ? View.VISIBLE : View.GONE);

        boolean canFollowup = PermissionManager.getInstance().hasCustomerAuthority(mCustomer.relationState,
                mCustomer.state, CustomerAction.FOLLOWUP_ADD);
        layout_gj.setVisibility(canFollowup ? View.VISIBLE : View.GONE);
        if (!canVisit && !canFollowup) {
            layout_menu.setVisibility(View.GONE);
        }

        canEdit = PermissionManager.getInstance().hasCustomerAuthority(
                mCustomer.relationState,
                mCustomer.state,
                CustomerAction.EDIT);

        img_title_left.setOnTouchListener(Global.GetTouch());
        layout_customer_info.setOnTouchListener(Global.GetTouch());
        img_public.setOnTouchListener(Global.GetTouch());
        layout_contact.setOnTouchListener(Global.GetTouch());
        layout_send_sms.setOnTouchListener(Global.GetTouch());
        layout_call.setOnTouchListener(Global.GetTouch());
        tv_sale_number.setOnTouchListener(Global.GetTouch());
        layout_sale_activity.setOnTouchListener(Global.GetTouch());
        layout_visit.setOnTouchListener(Global.GetTouch());
        layout_task.setOnTouchListener(Global.GetTouch());
        layout_attachment.setOnTouchListener(Global.GetTouch());
        layout_approval.setOnTouchListener(Global.GetTouch());
        if (mCustomer.state != Customer.DumpedCustomer)
            CommonMethod.commonCustomerRecycleTime(mCustomer, layout_4, tv_content41);

        tv_content43.setVisibility(mCustomer.state == Customer.DumpedCustomer ? View.VISIBLE : View.GONE);
        if (mCustomer.state == Customer.DumpedCustomer)
            tv_content43.setText("丢公海原因：" + (TextUtils.isEmpty(mCustomer.recycleReason) ? "--" : mCustomer.recycleReason));

        tv_customer_name.setText(mCustomer.name);
        if (null != mCustomer.loc) {
            tv_address.setText("地址：" + mCustomer.loc.addr);
        }
        tv_tags.setText("标签：" + Utils.getTagItems(mCustomer));
        initContact();
        mTagItems.clear();
        mTagItems.addAll(mCustomer.tags);

        tv_visit_times.setText("（" + mCustomer.counter.getVisit() + "）");
        tv_sale_count.setText("（" + mCustomer.counter.getDemand() + "）");
        tv_order_count.setText("（" + mCustomer.counter.order + "）");
        tv_task_count.setText("（" + mCustomer.counter.getTask() + "）");
        tv_attachment_count.setText("（" + mCustomer.counter.getFile() + "）");
        tv_approval_count.setText("（" + mCustomer.counter.workflow + "）");

        //正式启用销售机会 弃用购买意向
        ll_sale.setVisibility(View.VISIBLE);
        ll_sale.setOnTouchListener(Global.GetTouch());

        //突出显示跟进动态
        tv_sale_number.setText("（" + mCustomer.saleActivityNum + "）");
        if (null != mCustomer.saleActivityInfo) {
            tv_follow_content.setVisibility(View.VISIBLE);
            tv_follow_crecter_type.setVisibility(View.VISIBLE);
            tv_follow_content.setText(mCustomer.saleActivityInfo.content.contains("<p>") ?
                    CommonHtmlUtils.Instance().checkContent(mCustomer.saleActivityInfo.content) : mCustomer.saleActivityInfo.content);
//            tv_follow_crecter_type.setText(app.df3.format(new Date(mCustomer.saleActivityInfo.createAt * 1000)) + " " +mCustomer.saleActivityInfo.creatorName + " #" + mCustomer.saleActivityInfo.typeName);
            tv_follow_crecter_type.setText(DateTool.getDateTimeFriendly(mCustomer.saleActivityInfo.createAt) + " " + mCustomer.saleActivityInfo.creatorName + " #" + mCustomer.saleActivityInfo.typeName);
        } else {
            tv_follow_content.setVisibility(View.GONE);
            tv_follow_crecter_type.setVisibility(View.GONE);
        }
        tv_contact_Number.setText("（" + mCustomer.contacts.size() + "）");

        //正式启用销售机会 弃用购买意向
        ll_sale.setVisibility(View.VISIBLE);
        ll_sale.setOnTouchListener(Global.GetTouch());
        ll_loading.setStatus(LoadingLayout.Success);
    }

    /**
     * 提示弹出框
     */
    private void setPopView(final boolean isKind, final String message) {

        sweetAlertDialogView.alertHandle(new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sweetAlertDialog) {
                dismissSweetAlert();
            }
        }, new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sweetAlertDialog) {
                dismissSweetAlert();
                if (isKind) {
                    mPresenter.delete(mCustomer.getId());
                } else {
                    /*此处投入公海操作*/
                    mPresenter.toPublic(mCustomer.getId());
                }
            }
        }, "提示", message);
    }


    @Click({R.id.img_title_left, R.id.img_title_right, R.id.layout_customer_info, R.id.img_public,
            R.id.layout_contact, R.id.layout_send_sms, R.id.layout_call, R.id.layout_sale_activity,
            R.id.layout_visit, R.id.layout_task, R.id.layout_attachment, R.id.layout_approval,
            R.id.layout_wiretel_call,
            R.id.ll_sale, R.id.ll_order, R.id.layout_gj, R.id.layout_sign, R.id.iv_select_tag})
    void onClick(final View view) {

        if (mCustomer == null) {
            finish();
            return;
        }

        Bundle bundle = new Bundle();
        Intent mIntent;
        Class<?> _class = null;
        int requestCode = -1;
        switch (view.getId()) {

            /*选择标签*/
            case R.id.iv_select_tag:
                mIntent = new Intent(CustomerDetailInfoActivity.this, CustomerLabelCopyActivity.class);
                mIntent.putExtra("canEdit", canEdit);
                mIntent.putExtra("fromPage", 0);
                if (null != mTagItems) {
                    mIntent.putExtra("tagitems", Utils.convertTagItems(mTagItems));
                    mIntent.putExtra("customerId", mCustomer.getId());
                }
                startActivity(mIntent);
                UmengAnalytics.umengSend(CustomerDetailInfoActivity.this, UmengAnalytics.customerEditTag);
                break;

            /*快捷拜访*/
            case R.id.layout_sign:
                mIntent = new Intent(CustomerDetailInfoActivity.this, SignInActivity.class);
                mIntent.putExtra("data", mCustomer);
                startActivity(mIntent);
                needToRefresh=true;
                UmengAnalytics.umengSend(CustomerDetailInfoActivity.this, UmengAnalytics.customerVisit);
                break;

            /*快捷跟进*/
            case R.id.layout_gj:
                mIntent = new Intent(CustomerDetailInfoActivity.this, FollowAddActivity.class);
                mIntent.putExtra(Customer.class.getName(), mCustomer);
                mIntent.putExtra("isDetail", true);
                mIntent.putExtra(ExtraAndResult.DYNAMIC_ADD_ACTION, ExtraAndResult.DYNAMIC_ADD_CUSTOMER);
                startActivity(mIntent);
                needToRefresh=true;
                UmengAnalytics.umengSend(CustomerDetailInfoActivity.this, UmengAnalytics.customerAddFollow);
                break;

            /*返回*/
            case R.id.img_title_left:
                if (isPutOcen) {
//                    AppBus.getInstance().post(new MyCustomerRushEvent());
                    finish();
                } else {
                    onBackPressed();
                }
                break;
            /*菜单*/
            case R.id.img_title_right:
                onActionsheet();
                break;

            /*客户信息*/
            case R.id.layout_customer_info:
                bundle.putSerializable("CustomerId", mCustomer.getId());
                bundle.putBoolean("canEdit", canEdit);
                _class = CustomerInfoActivity_.class;
                requestCode = FinalVariables.REQUEST_PREVIEW_CUSTOMER_INFO;
                break;

            /*挑入*/
            case R.id.img_public:
                CustomerService.pickInCustomer(id)
                        .subscribe(new DefaultLoyoSubscriber<Customer>() {
                            @Override
                            public void onNext(Customer customer) {
                                mPresenter.getData(id);
                                Toast("跳入客户成功");
                                //通知列表页面删除
                                MyCustomerRushEvent myCustomerRushEvent =new MyCustomerRushEvent();
                                myCustomerRushEvent.eventCode= MyCustomerRushEvent.EVENT_CODE_DEL;
                                AppBus.getInstance().post(myCustomerRushEvent);
                            }
                        });
                UmengAnalytics.umengSend(CustomerDetailInfoActivity.this, UmengAnalytics.frompublicPublicDetail);
                break;
            /*联系人*/
            case R.id.layout_contact:
                try {
                    bundle.putSerializable(ExtraAndResult.EXTRA_ID, mCustomer.id);
                    boolean canEdit = mCustomer != null &&
                            PermissionManager.getInstance().hasCustomerAuthority(mCustomer.relationState,
                                    mCustomer.state, CustomerAction.CONTACT_ADD);
                    bundle.putBoolean("canEdit", canEdit);
                    _class = CustomerContactManageActivity_.class;
                    requestCode = FinalVariables.REQUEST_PREVIEW_CUSTOMER_CONTACTS;
                    needToRefresh=true;
                } catch (NullPointerException e) {
                    Toast("参数不全");
                }
                break;
            /*发送短信*/
            case R.id.layout_send_sms:
                if (null != mCustomer.contacts && mCustomer.contacts.size() > 0) {
                    Utils.sendSms(this, mCustomer.contacts.get(0).getTel());
                    PhoneNum = mCustomer.contacts.get(0).getTel();
                } else {
                    Toast("没有号码");
                }
                break;
            /*拨打手机*/
            case R.id.layout_call:
                if (null == mCustomer.contacts && mCustomer.contacts.size() == 0) {
                    Toast("手机号为空");

                    return;
                }

                if (null == mContact.getTel() || TextUtils.isEmpty(mContact.getTel())) {
                    Toast("手机号为空");
                    return;
                }
                PhoneNum = mContact.getTel();
                mPresenter.isMobile(CustomerDetailInfoActivity.this, mContact.getTel(), 0, mContact.getName());
                break;

            /*拨打座机*/
            case R.id.layout_wiretel_call:
                if (null == mCustomer.contacts && mCustomer.contacts.size() == 0) {
                    Toast("座机号为空");
                    return;
                }

                if (null == mContact.getWiretel() || TextUtils.isEmpty(mContact.getWiretel())) {
                    Toast("座机号为空");
                    return;
                }
                PhoneNum = mContact.getWiretel();
                mPresenter.isMobile(CustomerDetailInfoActivity.this, mContact.getWiretel(), 1, mContact.getName());
                break;
            /*跟进动态列表*/
            case R.id.layout_sale_activity:
                bundle.putSerializable("mCustomer", mCustomer);
                _class = CustomerFollowUpListActivity.class;
                requestCode = FinalVariables.REQUEST_PREVIEW_CUSTOMER_ACTIVITIS;
                needToRefresh=true;
                UmengAnalytics.umengSend(CustomerDetailInfoActivity.this, UmengAnalytics.customerCheckFollow);
                break;
            /*拜访签到*/
            case R.id.layout_visit:
                bundle.putSerializable("mCustomer", mCustomer);
                _class = CustomerSigninListActivity.class;
                requestCode = FinalVariables.REQUEST_PREVIEW_LEGWORKS;
                needToRefresh=true;
                break;
            /*任务计划*/
            case R.id.layout_task:
                boolean canAddTask = mCustomer != null &&
                        PermissionManager.getInstance().hasCustomerAuthority(
                                mCustomer.relationState,
                                mCustomer.state,
                                CustomerAction.TASK_ADD
                        );
                bundle.putBoolean("canAdd", canAddTask);
                bundle.putSerializable("mCustomer", mCustomer);
                _class = TaskListActivity_.class;
                requestCode = FinalVariables.REQUEST_PREVIEW_CUSTOMER_TASKS;
                needToRefresh=true;
                break;
            /*文件*/
            case R.id.layout_attachment:

                boolean canAdd = mCustomer != null &&
                        PermissionManager.getInstance().hasCustomerAuthority(
                                mCustomer.relationState,
                                mCustomer.state,
                                CustomerAction.ATTACHMENT_ADD
                        );

                bundle.putBoolean("canAdd", canAdd);
                bundle.putInt("fromPage", Common.CUSTOMER_PAGE);
                bundle.putSerializable("uuid", mCustomer.uuid);
                bundle.putInt("bizType", 6);
                _class = AttachmentActivity_.class;
                requestCode = FinalVariables.REQUEST_DEAL_ATTACHMENT;
                needToRefresh=true;
                break;
            /*销售机会*/
            case R.id.ll_sale:
                boolean canAddSaleOpportunity = mCustomer != null &&
                        PermissionManager.getInstance().hasCustomerAuthority(
                                mCustomer.relationState,
                                mCustomer.state,
                                CustomerAction.SALE_OPPORTUNITY_ADD
                        );
                bundle.putBoolean("canAdd", canAddSaleOpportunity);
                bundle.putString(ExtraAndResult.EXTRA_ID, mCustomer.getId());
                bundle.putString(ExtraAndResult.EXTRA_NAME, mCustomer.name);
                _class = SaleManageActivity.class;
                requestCode = ExtraAndResult.REQUEST_CODE;
                needToRefresh=true;
                break;
            /*订单管理*/
            case R.id.ll_order: {
                boolean canAddOrder = mCustomer != null &&
                        PermissionManager.getInstance().hasCustomerAuthority(
                                mCustomer.relationState,
                                mCustomer.state,
                                CustomerAction.ORDER_ADD
                        );
                bundle.putBoolean("canAdd", canAddOrder);
                bundle.putString(ExtraAndResult.EXTRA_ID, mCustomer.getId());
                bundle.putString(ExtraAndResult.EXTRA_NAME, mCustomer.name);
                _class = CustomerOrderList.class;
                requestCode = ExtraAndResult.REQUEST_CODE;
                needToRefresh=true;
            }
            break;
            /*审批流程*/
            case R.id.layout_approval: {
                boolean canAddApproval = mCustomer != null &&
                        PermissionManager.getInstance().hasCustomerAuthority(
                                mCustomer.relationState,
                                mCustomer.state,
                                CustomerAction.APPROVAL_ADD
                        );
                bundle.putBoolean("canAdd", canAddApproval);
                bundle.putString(ExtraAndResult.EXTRA_ID, mCustomer.getId());
                bundle.putString(ExtraAndResult.EXTRA_NAME, mCustomer.name);
                _class = CustomerRelatedApprovalList.class;
                requestCode = ExtraAndResult.REQUEST_CODE;
                needToRefresh=true;
            }
            break;
        }
        if (null != _class && requestCode != -1) {
            goToChild(bundle, _class, requestCode);
        }
    }


    //用来处理打电话权限申请
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (Utils.CALL_REQUEST == requestCode) {
            PermissionTool.requestPermissionsResult(permissions, grantResults, new PermissionTool.PermissionsResultCallBack() {
                @Override
                public void success() {
                    Utils.call(CustomerDetailInfoActivity.this, PhoneNum);
                }

                @Override
                public void fail() {
                    Toast("你拒绝了打电话权限，无法拨出电话");
                }
            });
        } else if (Utils.SEND_SMS_REQUEST == requestCode) {
            PermissionTool.requestPermissionsResult(permissions, grantResults, new PermissionTool.PermissionsResultCallBack() {
                @Override
                public void success() {
                    Utils.sendSms(CustomerDetailInfoActivity.this, PhoneNum);
                }

                @Override
                public void fail() {
                    Toast("你拒绝了发短信权限，无法发送短信");
                }
            });
        }
    }

    private void onActionsheet() {
        boolean canDelete = PermissionManager.getInstance().hasCustomerAuthority(mCustomer.relationState,
                mCustomer.state, CustomerAction.DELETE);
        boolean canDump = PermissionManager.getInstance().hasCustomerAuthority(mCustomer.relationState,
                mCustomer.state, CustomerAction.DUMP);

        if (!canDelete && !canDump) {
            return;
        }

        ActionSheetDialog dialog = new ActionSheetDialog(this).builder();
        if (canDump)
            dialog.addSheetItem("投入公海", ActionSheetDialog.SheetItemColor.Blue, new ActionSheetDialog.OnSheetItemClickListener() {
                @Override
                public void onClick(int which) {
                    UmengAnalytics.umengSend(CustomerDetailInfoActivity.this, UmengAnalytics.customerTopublic);
                    HashMap<String, Object> map = new HashMap<>();
                    map.put("key", "cus_reason_switcher");
                    showLoading2("");
                    CustomerService.getSigninUploadPhotoConfig(map).subscribe(new DefaultLoyoSubscriber<SigninPictures>(hud) {
                        @Override
                        public void onNext(SigninPictures signinPictures) {
                            if (signinPictures != null && signinPictures.value.equals("1")) {
                                Intent intent = new Intent(CustomerDetailInfoActivity.this, LoseCommonCustomerReasonActivity.class);
                                intent.putExtra(ExtraAndResult.EXTRA_ID, mCustomer.getId());
                                startActivityForResult(intent, ExtraAndResult.REQUSET_COPY_PERSONS);
                                overridePendingTransition(R.anim.enter_righttoleft, R.anim.exit_righttoleft);
                            } else {
                                setPopViewEmbl(false, "确定将客户 \"" + mCustomer.name + "\" 投入公海吗?");
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
                    setPopViewEmbl(true, "你确定要删除客户?");
                    UmengAnalytics.umengSend(CustomerDetailInfoActivity.this, UmengAnalytics.customerDelete);
                }
            });
        dialog.show();
    }

    /**
     * 编辑行为确认
     */
    @Subscribe
    public void onMyCustomerPushEvent(MyCustomerRushEvent event) {
        if(MyCustomerRushEvent.EVENT_CODE_UPDATE==event.eventCode){
            //更新客户信息
            if(MyCustomerRushEvent.EVENT_SUB_CODE_INFO==event.subCode){
                Customer updateCus=event.data;
                mCustomer.name=updateCus.name;
                mCustomer.summary=updateCus.summary;
                mCustomer.owner= updateCus.owner;
                mCustomer.members= updateCus.members;
                mCustomer.tags= updateCus.tags;
                mCustomer.loc= updateCus.loc;
                mCustomer.position= updateCus.position;
                mCustomer.extDatas= updateCus.extDatas;
                mCustomer.regional= updateCus.regional;
                mCustomer.state= updateCus.state;
                mCustomer.relationState= updateCus.relationState;
                initData();
            }else if(MyCustomerRushEvent.EVENT_SUB_CODE_LABEL==event.subCode){
                if(!"note".equals(event.request+""))return;
                //更新label
                mCustomer.tags=event.data.tags;
                tv_tags.setText("标签：" + Utils.getTagItems(mCustomer));
            }
        }
    }


    /**
     * 查看子内容
     *
     * @param b
     * @param _class
     * @param requestCode
     */

    private void goToChild(final Bundle b, final Class<?> _class, final int requestCode) {
        app.startActivityForResult(this, _class, MainApp.ENTER_TYPE_RIGHT, requestCode, b);
    }

    @Override
    public boolean onKeyDown(final int keyCode, final KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
//            if (isPutOcen) {
//                AppBus.getInstance().post(new MyCustomerRushEvent());
//                finish();
//            } else if (isEdit) {
//                AppBus.getInstance().post(new EditCustomerRushEvent());
//                AppBus.getInstance().post(new MyCustomerRushEvent());
//                finish();
//            } else {
//                onBackPressed();
//            }
//            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case FinalVariables.REQUEST_PREVIEW_CUSTOMER_INFO:
                /*如果修改了负责人，不是自己，则finish该页面*/
//                try {
//                    boolean isCreator = data.getBooleanExtra("isCreator",true);
//                    if(!isCreator){
//                        MyCustomerRushEvent myCustomerRushEvent=new MyCustomerRushEvent();
//                        myCustomerRushEvent.eventCode=MyCustomerRushEvent.EVENT_CODE_DEL;//投入公海，就是从前面的列表删除
//                        AppBus.getInstance().post(myCustomerRushEvent);
//                        finish();
//                    }
////                    if (!isCreator) {
////                        AppBus.getInstance().post(new MyCustomerRushEvent());
////                        finish();
////                    }
//                } catch (NullPointerException e) {
//                    e.printStackTrace();
//                }
                break;
            //丢公海回来
            case ExtraAndResult.REQUSET_COPY_PERSONS:
                toPublicEmbl();
                break;
        }

        switch (resultCode) {
            case CustomerManagerActivity.CUSTOMER_COMM_RUSH:
                isPutOcen = true;
                break;
        }
    }

    /**
     * 投入公海成功处理
     */
    @Override
    public void toPublicEmbl() {
//        isPutOcen = true;
        MyCustomerRushEvent myCustomerRushEvent=new MyCustomerRushEvent();
        myCustomerRushEvent.eventCode=MyCustomerRushEvent.EVENT_CODE_DEL;//投入公海，就是从前面的列表删除
        AppBus.getInstance().post(myCustomerRushEvent);
//        AppBus.getInstance().post(new MyCustomerRushEvent());
        finish();
    }

    /**
     * 删除操作成处理
     */
    @Override
    public void deleteEmbl() {
        MyCustomerRushEvent myCustomerRushEvent=new MyCustomerRushEvent();
        myCustomerRushEvent.eventCode=MyCustomerRushEvent.EVENT_CODE_DEL;
        AppBus.getInstance().post(myCustomerRushEvent);
//        AppBus.getInstance().post(new MyCustomerRushEvent());
        finish();
    }

    /**
     * 获取参与人权限处理
     */
    @Override
    public void getMembersRootEmbl(MembersRoot membersRoot) {
        PermissionManager.getInstance().loadCRMConfig(membersRoot);
        initData();
    }

    /**
     * 获取详情数据成功处理
     */
    @Override
    public void getDataSuccessEmbl(Customer customer) {
        mCustomer = customer;
        mPresenter.getMembersRoot();
    }

    /**
     * 获取详情数据失败处理
     */
    @Override
    public void getDataErrorEmle() {
        finish();
    }

    /**
     * 弹出通用提示框
     */
    @Override
    public void setPopViewEmbl(boolean mespray, String message) {
        setPopView(mespray, message);
    }

    @Override
    public LoadingLayout getLoadigLayout() {
        return ll_loading;
    }

    @Override
    public LoyoProgressHUD getHUD() {
        return hud;
    }

    @Override
    public LoyoProgressHUD showStatusProgress() {
        showCommitLoading();
        return hud;
    }

    @Override
    public LoyoProgressHUD showProgress(String message) {
        showLoading2(message);
        return hud;
    }

    @Override
    public void hideProgress() {
        cancelLoading2();
    }

    @Override
    public void showMsg(String message) {
        LoyoToast.info(this, message);
    }
}
