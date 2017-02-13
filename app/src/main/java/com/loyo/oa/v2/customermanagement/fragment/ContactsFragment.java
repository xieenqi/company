package com.loyo.oa.v2.customermanagement.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.library.module.widget.loading.LoadingLayout;
import com.loyo.oa.common.utils.PermissionTool;
import com.loyo.oa.pulltorefresh.PullToRefreshBase;
import com.loyo.oa.pulltorefresh.PullToRefreshRecyclerView2;
import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.customer.CallPhoneBackActivity;
import com.loyo.oa.v2.activityui.customer.CustomerAddActivity;
import com.loyo.oa.v2.activityui.customer.CustomerContractAddActivity;
import com.loyo.oa.v2.activityui.customer.model.CallBackCallid;
import com.loyo.oa.v2.activityui.customer.model.Contact;
import com.loyo.oa.v2.activityui.customer.model.Customer;
import com.loyo.oa.v2.activityui.setting.EditUserMobileActivity;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.common.ExtraAndResult;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.common.RegularCheck;
import com.loyo.oa.v2.customermanagement.activity.ContactDetailActivity;
import com.loyo.oa.v2.customermanagement.adapter.CustomerContactsListAdapter;
import com.loyo.oa.v2.customermanagement.api.CustomerService;
import com.loyo.oa.v2.customermanagement.cell.ContactCardCell;
import com.loyo.oa.v2.customview.CallPhonePopView;
import com.loyo.oa.v2.customview.ContactViewGroup;
import com.loyo.oa.v2.customview.SweetAlertDialogView;
import com.loyo.oa.v2.network.DefaultLoyoSubscriber;
import com.loyo.oa.v2.permission.CustomerAction;
import com.loyo.oa.v2.permission.PermissionManager;
import com.loyo.oa.v2.tool.LogUtil;
import com.loyo.oa.v2.tool.Utils;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.pedant.SweetAlert.SweetAlertDialog;

import static android.app.Activity.RESULT_OK;

/**
 * Created by EthanGong on 2017/2/9.
 */

public class ContactsFragment extends CustomerChildFragment
        implements ContactCardCell.OnContactCellActionListener {

    View view;
    @BindView(R.id.layout_add)
    ViewGroup layout_add;
    @BindView(R.id.ll_loading)
    LoadingLayout ll_loading;

    String customerId;
    boolean canEdit;
    String callNum;

    CustomerContactsListAdapter adapter;
    @BindView(R.id.contact_list_view) PullToRefreshRecyclerView2 listView;

    public ContactsFragment() {
        this.title = "联系人";
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
        customerId = customer.getId();
        canEdit = customer != null &&
                PermissionManager.getInstance().hasCustomerAuthority(customer.relationState,
                        customer.state, CustomerAction.CONTACT_ADD);
        this.totalCount = customer.contacts.size();
    }

    private void getData() {
        /* 拉取包含详细信息的联系人，用于联系人详情界面展示 */
        CustomerService.getCustomerContacts(customerId)
                .subscribe(new DefaultLoyoSubscriber<Customer>(ll_loading) {
                    @Override
                    public void onNext(Customer customer) {
                        ll_loading.setStatus(LoadingLayout.Success);
                        ContactsFragment.this.customer.contacts = customer.contacts;
                        adapter.loadData(customer.contacts);
                    }
                });
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(
                    R.layout.fragment_contacts, container, false);

            adapter = new CustomerContactsListAdapter(this);
            adapter.addData(customer.contacts);
            initViews(view);
            getData();
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
                getData();
            }
        });
        layout_add.setVisibility(canEdit ? View.VISIBLE : View.GONE);
        layout_add.setOnTouchListener(Global.GetTouch());

        listView.setMode(PullToRefreshBase.Mode.MANUAL_REFRESH_ONLY);
        listView.getRefreshableView().setLayoutManager(new LinearLayoutManager(getContext()));
        listView.getRefreshableView().setAdapter(adapter);
    }

    //用来处理打电话权限申请
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (Utils.CALL_REQUEST == requestCode) {
            PermissionTool.requestPermissionsResult(permissions, grantResults, new PermissionTool.PermissionsResultCallBack() {
                @Override
                public void success() {
                    Utils.call(getActivity(), callNum);
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
                    Utils.sendSms(getActivity(), callNum);
                }

                @Override
                public void fail() {
                    Toast("你拒绝了发短信权限，无法发送短信");
                }
            });
        }
    }

    @OnClick(R.id.layout_add) void addNewContact() {
        Bundle bundle = new Bundle();
        bundle.putSerializable("customer", customer);
        Intent intent = new Intent(getActivity(), CustomerContractAddActivity.class);
        intent.putExtras(bundle);
        startActivityForResult(intent, CustomerAddActivity.REQUEST_CUSTOMER_NEW_CONTRACT);
    }

    @Override
    public void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != RESULT_OK || null == data) {
            return;
        }
        switch (requestCode) {

            case CustomerAddActivity.REQUEST_CUSTOMER_NEW_CONTRACT:
            {
                Contact contact = (Contact) data.getSerializableExtra("data");
                insertContact(contact);
            }
                break;
            case ContactDetailActivity.ContactDetailActivityRequestCode:
            {
                Contact contact = (Contact) data.getSerializableExtra("contact");
                int action = data.getIntExtra("action", 0);
                int index = data.getIntExtra("contactIndex", -1);
                if (index > 0 && index < customer.contacts.size()) {
                    if (action == ContactDetailActivity.CONTACT_DELETE) {
                        customer.contacts.remove(index);
                        adapter.loadData(customer.contacts);
                    }
                    else if (action == ContactDetailActivity.CONTACT_UPDATED) {
                        customer.contacts.remove(index);
                        customer.contacts.add(contact);
                        adapter.loadData(customer.contacts);
                    }
                }

            }
                break;
            default:
                break;
        }
    }

    void businessCallCheck(String customerId, final Contact contact, int callType, String callNum) {
        showLoading2("");
        HashMap<String, Object> map = new HashMap<>();
        map.put("customerId", customerId);
        map.put("contactId", contact.getId());
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
                                    mBundle.putString(ExtraAndResult.EXTRA_NAME, contact.getName());
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
     * ContactCardCell.OnContactCellActionListener
     */

    @Override
    public void onSMS(String phone) {
        callNum = phone;
        Utils.sendSms(getActivity(), phone);
    }

    @Override
    public void onCallPhone(final Contact contact, final String phone) {
        // 判断是否有绑定手机号
        if (checkMobile()) {
            callNum = phone;
            boolean checkTag = RegularCheck.isYunPhone(phone);
            final CallPhonePopView callPhonePopView = new CallPhonePopView(getActivity(), contact.getName(), checkTag);
            callPhonePopView.show();
            callPhonePopView.setCanceledOnTouchOutside(true);
            /*商务电话-回拨*/
            final String formattedPhone = phone.replaceAll(" +", "");
            callPhonePopView.businessPhone(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    businessCallCheck(customer.getId(), contact, ContactViewGroup.CallbackPhone, formattedPhone);
                    callPhonePopView.dismiss();
                }
            });
            /*普通电话*/
            callPhonePopView.commonlyPhone(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (RegularCheck.isMobilePhone(formattedPhone)) {
                        Utils.call(getActivity(), formattedPhone);
                    } else {
                        Toast("电话号码格式不正确或为空!");
                    }
                    callPhonePopView.dismiss();
                }
            });
            callPhonePopView.cancelPhone(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    callPhonePopView.dismiss();
                }
            });
        }
    }

    @Override
    public void onCallTel(final Contact contact, final String tel) {
        // 判断是否有绑定手机号
        if (checkMobile()) {
            callNum = tel;
            boolean checkTag = RegularCheck.isYunTell(tel);
            final CallPhonePopView callPhonePopView = new CallPhonePopView(getActivity(), contact.getName(), checkTag);
            callPhonePopView.show();
            callPhonePopView.setCanceledOnTouchOutside(true);
            /*商务电话-回拨*/
            final String formattedPhone = tel.replaceAll(" +", "");
            callPhonePopView.businessPhone(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    businessCallCheck(customer.getId(), contact, ContactViewGroup.CallbackPhone, formattedPhone);
                    callPhonePopView.dismiss();
                }
            });
            /*普通电话*/
            callPhonePopView.commonlyPhone(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Utils.call(getActivity(), formattedPhone);
                    callPhonePopView.dismiss();
                }
            });
            callPhonePopView.cancelPhone(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    callPhonePopView.dismiss();
                }
            });
        }
    }

    @Override
    public void onSetDefaultContact(Contact contact, final int contactIndex) {
        if (!canEdit) {
            return;
        }
        showCommitLoading();
        CustomerService.setDefaultContact(customer.getId(), contact.getId())
                .subscribe(new DefaultLoyoSubscriber<Contact>(hud) {
                    @Override
                    public void onNext(Contact contact1) {
                        updateDefaultContactAtIndex(contactIndex);
                    }
                });
    }

    @Override
    public void onContactSelect(Contact contact, int contactIndex) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("customer", customer);
        bundle.putSerializable("contact", contact);
        bundle.putSerializable("contactIndex", contactIndex);
        Intent intent = new Intent(getActivity(), ContactDetailActivity.class);
        intent.putExtras(bundle);
        startActivityForResult(intent, ContactDetailActivity.ContactDetailActivityRequestCode);
    }

    private boolean checkMobile() {
        if (null == MainApp.user.mobile || TextUtils.isEmpty(MainApp.user.mobile)) {
            final SweetAlertDialogView sweetAlertDialogView = new SweetAlertDialogView(getActivity());
            sweetAlertDialogView.alertHandle(new SweetAlertDialog.OnSweetClickListener() {
                @Override
                public void onClick(SweetAlertDialog sweetAlertDialog) {
                    sweetAlertDialogView.sweetAlertDialog.dismiss();
                }
            }, new SweetAlertDialog.OnSweetClickListener() {
                @Override
                public void onClick(SweetAlertDialog sweetAlertDialog) {
                    sweetAlertDialogView.sweetAlertDialog.dismiss();
                    MainApp.getMainApp().startActivity(getActivity(),
                            EditUserMobileActivity.class,
                            MainApp.ENTER_TYPE_RIGHT, false, null);
                }
            }, "提示", getActivity().getString(R.string.app_homeqq_message));
            return false;
        } else {
            return true;
        }
    }

    private void updateDefaultContactAtIndex(int index) {
        ArrayList<Contact> contacts = customer.contacts;
        if (contacts != null && contacts.size() > index) {
            Contact defaultContact =  contacts.remove(index);
            defaultContact.setIsDefault(true);
            contacts.add(0, defaultContact);
            for (int i = 1; i < contacts.size(); i++) {
                contacts.get(i).setIsDefault(false);
            }
            customer.contacts = contacts;
            adapter.loadData(contacts);
        }

    }

    private void insertContact(Contact contact) {
        if (contact == null) {
            return;
        }
        ArrayList<Contact> contacts = customer.contacts;
        if (contacts == null) {
            contacts = new ArrayList<>();
        }

        if (contacts.size() > 0) {
            contacts.add(contact);
        }
        else {
            contact.setIsDefault(true);
            contacts.add(contact);
        }
        customer.contacts = contacts;
        adapter.loadData(contacts);
    }

}
