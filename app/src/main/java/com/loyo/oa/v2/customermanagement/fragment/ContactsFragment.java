package com.loyo.oa.v2.customermanagement.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.library.module.widget.loading.LoadingLayout;
import com.loyo.oa.common.utils.PermissionTool;
import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.customer.CallPhoneBackActivity;
import com.loyo.oa.v2.activityui.customer.CustomerAddActivity;
import com.loyo.oa.v2.activityui.customer.CustomerContractAddActivity;
import com.loyo.oa.v2.activityui.customer.CustomerInfoActivity;
import com.loyo.oa.v2.activityui.customer.model.CallBackCallid;
import com.loyo.oa.v2.activityui.customer.model.Contact;
import com.loyo.oa.v2.activityui.customer.model.ContactLeftExtras;
import com.loyo.oa.v2.activityui.customer.model.Customer;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.common.ExtraAndResult;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.customermanagement.api.CustomerService;
import com.loyo.oa.v2.customview.ContactViewGroup;
import com.loyo.oa.v2.network.DefaultLoyoSubscriber;
import com.loyo.oa.v2.permission.CustomerAction;
import com.loyo.oa.v2.permission.PermissionManager;
import com.loyo.oa.v2.tool.BaseFragment;
import com.loyo.oa.v2.tool.LogUtil;
import com.loyo.oa.v2.tool.Utils;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.app.Activity.RESULT_OK;

/**
 * Created by EthanGong on 2017/2/9.
 */

public class ContactsFragment extends BaseFragment implements ContactViewGroup.OnContactProcessCallback{

    View view;

    @BindView(R.id.layout_container)
    LinearLayout layout_container;
    @BindView(R.id.layout_add)
    ViewGroup layout_add;
    @BindView(R.id.ll_loading)
    LoadingLayout ll_loading;

    String customerId;
    boolean canEdit;
    Customer customer;

    private Customer customerContact;
    private ArrayList<ContactLeftExtras> leftExtrases;

    private String contactId;
    private String contactName;
    private String callNum;
    private String myCall;
    private int callType;

    private String phoneNum;//用来存储一下电话号码，用在动态授权上

    public void setCustomer(Customer customer) {
        this.customer = customer;
        customerId = customer.getId();
        canEdit = customer != null &&
                PermissionManager.getInstance().hasCustomerAuthority(customer.relationState,
                        customer.state, CustomerAction.CONTACT_ADD);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(
                    R.layout.fragment_contacts, container, false);
            initViews(view);
        }
        return view;
    }

    void initViews(View view) {
        ButterKnife.bind(this, view);

        ll_loading.setStatus(LoadingLayout.Loading);
        ll_loading.setOnReloadListener(new LoadingLayout.OnReloadListener() {
            @Override
            public void onReload(View v) {
                ll_loading.setStatus(LoadingLayout.Loading);
                getContactsFields();
            }
        });
        layout_add.setVisibility(canEdit ? View.VISIBLE : View.GONE);
        layout_add.setOnTouchListener(Global.GetTouch());
        getContactsFields();
    }

    /**
     * 获取最新 左侧动态字段
     */
    private void getContactsFields() {
        CustomerService.getContactsField()
                .subscribe(new DefaultLoyoSubscriber<ArrayList<ContactLeftExtras>>(ll_loading) {
                    @Override
                    public void onNext(ArrayList<ContactLeftExtras> contactLeftExtrasArrayList) {
                        leftExtrases = contactLeftExtrasArrayList;
                        getData();
                    }
                });
    }

    /**
     * 获取客户联系人列表
     */
    private void getData() {
        CustomerService.getCustomerContacts(customerId)
                .subscribe(new DefaultLoyoSubscriber<Customer>(ll_loading) {
                    @Override
                    public void onNext(Customer customer) {
                        customerContact = customer;
                        initData();
                    }
                });
    }


    /**
     * 初始化数据
     */
    private void initData() {
        if (null == customerContact.contacts || customerContact.contacts.size() == 0) {
            ll_loading.setStatus(LoadingLayout.Empty);
            return;
        }
        ll_loading.setStatus(LoadingLayout.Success);


        layout_container.removeAllViews();
        ArrayList<Contact> contactsCopy = new ArrayList<>();
        contactsCopy.clear();

        /*默认数据放在最前*/
        for (Contact mContact : customerContact.contacts) {
            if (mContact.isDefault()) {
                contactsCopy.add(mContact);
                break;
            }
        }
        /*非默认联系人排后*/
        for (Contact mContact : customerContact.contacts) {
            if (!mContact.isDefault()) {
                contactsCopy.add(mContact);
            }
        }

        for (int i = 0; i < contactsCopy.size(); i++) {
            Contact contact = contactsCopy.get(i);
            ContactViewGroup contactViewGroup = new ContactViewGroup(getActivity(),
                    customerContact, leftExtrases, contact, this);
            contactViewGroup.bindView(i + 1, layout_container, canEdit);
        }
    }


    @Override
    public void setPhone(String phone) {
        //用来保存刚才用户拨打的电话
        this.phoneNum = phone;
    }

    //用来处理打电话权限申请
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (Utils.CALL_REQUEST == requestCode) {
            PermissionTool.requestPermissionsResult(permissions, grantResults, new PermissionTool.PermissionsResultCallBack() {
                @Override
                public void success() {
                    Utils.call(getActivity(), phoneNum);
                }

                @Override
                public void fail() {
                    Toast("你拒绝了打电话权限，无法拨出电话");
                }
            });
        }else if(Utils.SEND_SMS_REQUEST==requestCode){
            PermissionTool.requestPermissionsResult(permissions, grantResults, new PermissionTool.PermissionsResultCallBack() {
                @Override
                public void success() {
                    Utils.sendSms(getActivity(), phoneNum);
                }

                @Override
                public void fail() {
                    Toast("你拒绝了发短信权限，无法发送短信");
                }
            });
        }
    }

    @OnClick(R.id.layout_add)
    void addNewContact() {
        Bundle bundle = new Bundle();
        bundle.putSerializable("customer", customerContact);

        Intent intent = new Intent(getActivity(), CustomerContractAddActivity.class);
        startActivityForResult(intent,
                CustomerAddActivity.REQUEST_CUSTOMER_NEW_CONTRACT);
    }

    @Override
    public void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != RESULT_OK || null == data) {
            return;
        }
        switch (requestCode) {

            case CustomerAddActivity.REQUEST_CUSTOMER_NEW_CONTRACT:
                Contact contact = (Contact) data.getSerializableExtra("data");
                customerContact.contacts.add(contact);
                LogUtil.dee("contacts:"+MainApp.gson.toJson(customerContact.contacts));
                initData();
                break;

            case CustomerInfoActivity.REQUEST_CUSTOMER_UPDATE_CONTRACT:
                Contact contactUpdated = (Contact) data.getSerializableExtra("data");
                if (contactUpdated == null) {
                    return;
                }

                for (int i = 0; i < customerContact.contacts.size(); i++) {
                    if (TextUtils.equals(contactUpdated.getId(), customerContact.contacts.get(i).getId())) {
                        contactUpdated.setIsDefault(customerContact.contacts.get(i).isDefault());
                        customerContact.contacts.set(i, contactUpdated);
                        break;
                    }
                }
                initData();
                break;

            default:
                break;
        }
    }


    void requestClientInfo() {
        showLoading2("");
        HashMap<String, Object> map = new HashMap<>();
        map.put("customerId", customerContact.getId());
        map.put("contactId", contactId);
        map.put("type", callType);
        map.put("mobile", callNum);
        LogUtil.dee("请求回拨发送数据："+MainApp.gson.toJson(map));
        CustomerService.requestCallBack(map)
                .subscribe(new DefaultLoyoSubscriber<CallBackCallid>(hud) {
                    @Override
                    public void onNext(CallBackCallid callBackCallid) {
                        try{
                            switch (callBackCallid.errcode){
                                case 0:
                                    Bundle mBundle = new Bundle();
                                    mBundle.putString(ExtraAndResult.WELCOM_KEY, callBackCallid.data.callLogId);
                                    mBundle.putString(ExtraAndResult.EXTRA_NAME, contactName);
                                    app.startActivity(getActivity(), CallPhoneBackActivity.class,
                                            MainApp.ENTER_TYPE_RIGHT, false, mBundle);
                                    break;

                                case 50000:
                                    Toast("主叫与被叫号码不能相同!");
                                    break;

                                case 50001:
                                    Toast("余额不足!");
                                    break;

                                case 50002:
                                    Toast("号码格式错误!");
                                    break;

                                default:
                                    Toast(callBackCallid.errmsg);
                                    break;
                            }
                        } catch (NullPointerException e) {
                            e.printStackTrace();
                            Toast(e.getMessage());
                        }
                    }
                });
    }

    /**
     * 拨打商务电话回调
     */
    @Override
    public void onCallBack(String callNum, String contactId, String contactName, @ContactViewGroup.CallPhoneType int callType, int phoneType) {
        this.callNum = callNum;
        this.contactId = contactId;
        this.contactName = contactName;
        this.callType = phoneType;
        myCall = MainApp.user.mobile;
        LogUtil.dee("我的号码:" + myCall);
        LogUtil.dee("被叫号码:" + callNum);
        LogUtil.dee("contactId:" + contactId);
        LogUtil.dee("contactName:" + contactName);
//
        if (callType == ContactViewGroup.CallbackPhone) {
            requestClientInfo();
        }
    }

    @Override
    public void onPhoneError() {
        Toast("电话号码格式不正确或为空!");
    }


    /**
     * 删除联系人的回调 xnq
     *
     * @param contact
     */
    @Override
    public void onDel(final Contact contact) {
        CustomerService.deleteContact(customerContact.getId(), contact.getId())
                .subscribe(new DefaultLoyoSubscriber<Contact>() {
                    @Override
                    public void onNext(Contact contact1) {

                        for (int i = 0; i < customerContact.contacts.size(); i++) {
                            Contact newContact = customerContact.contacts.get(i);
                            if (newContact.equals(contact)) {
                                customerContact.contacts.remove(i);
                                initData();
                                break;
                            }
                        }
                        refresh();
                    }
                });
    }

    private void refresh() {
        onCreate(null);
    }

    /**
     * 设置默认联系人的回调 xnq
     *
     * @param contact
     */
    @Override
    public void onSetDefault(final Contact contact) {
        CustomerService.setDefaultContact(customerContact.getId(), contact.getId())
                .subscribe(new DefaultLoyoSubscriber<Contact>() {
                    @Override
                    public void onNext(Contact contact1) {
                        for (int i = 0; i < customerContact.contacts.size(); i++) {
                            Contact newContact = customerContact.contacts.get(i);
                            if (newContact.isDefault()) {
                                newContact.setIsDefault(false);
                                break;
                            }
                        }
                        for (int i = 0; i < customerContact.contacts.size(); i++) {
                            Contact newContact = customerContact.contacts.get(i);
                            if (newContact.equals(contact)) {
                                newContact.setIsDefault(true);
                                customerContact.contacts.set(i, newContact);
                                break;
                            }
                        }
                        initData();
                    }
                });
    }

}
