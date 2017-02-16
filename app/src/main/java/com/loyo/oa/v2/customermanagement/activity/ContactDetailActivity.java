package com.loyo.oa.v2.customermanagement.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.loyo.oa.common.utils.PermissionTool;
import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.customer.CallPhoneBackActivity;
import com.loyo.oa.v2.activityui.customer.CustomerAddActivity;
import com.loyo.oa.v2.activityui.customer.model.CallBackCallid;
import com.loyo.oa.v2.activityui.customer.model.Contact;
import com.loyo.oa.v2.activityui.customer.model.ContactLeftExtras;
import com.loyo.oa.v2.activityui.customer.model.Customer;
import com.loyo.oa.v2.activityui.setting.EditUserMobileActivity;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.common.ExtraAndResult;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.common.RegularCheck;
import com.loyo.oa.v2.customermanagement.api.CustomerService;
import com.loyo.oa.v2.customermanagement.cell.ContactCardCell;
import com.loyo.oa.v2.customview.ActionSheetDialog;
import com.loyo.oa.v2.customview.CallPhonePopView;
import com.loyo.oa.v2.customview.ContactListExtra;
import com.loyo.oa.v2.customview.ContactViewGroup;
import com.loyo.oa.v2.customview.SweetAlertDialogView;
import com.loyo.oa.v2.network.DefaultLoyoSubscriber;
import com.loyo.oa.v2.network.LoyoErrorChecker;
import com.loyo.oa.v2.permission.CustomerAction;
import com.loyo.oa.v2.permission.PermissionManager;
import com.loyo.oa.v2.tool.BaseActivity;
import com.loyo.oa.v2.tool.LogUtil;
import com.loyo.oa.v2.tool.Utils;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * Created by EthanGong on 2017/2/13.
 */

public class ContactDetailActivity extends BaseActivity implements ContactCardCell.OnContactCellActionListener{


    public static final int  ContactDetailActivityRequestCode = 10;
    public static final int  CONTACT_DELETE = 11;
    public static final int  CONTACT_UPDATED = 12;
    private Contact contact;
    private Customer customer;
    private int contactIndex;
    private boolean canEdit;
    private boolean contactUpdated;
    private ArrayList<ContactLeftExtras> leftExtrases;
    private ArrayList<ViewGroup> phoneContainers;
    private ArrayList<ViewGroup> telContainers;
    private ArrayList<TextView> phoneTexts;
    private ArrayList<TextView> telTexts;
    String callNum;

    @BindView(R.id.img_title_left) View img_title_left;
    @BindView(R.id.img_title_right) View img_title_right;
    @BindView(R.id.tv_title_1)      TextView tv_title_1;

    @BindView(R.id.scroll_container) LinearLayout scrollContainer;
    @BindView(R.id.contact_extra_holder) LinearLayout contact_extra_holder;

    @BindView(R.id.layout_phone1) ViewGroup phoneContainer1;
    @BindView(R.id.layout_phone2) ViewGroup phoneContainer2;
    @BindView(R.id.layout_phone3) ViewGroup phoneContainer3;
    @BindView(R.id.layout_wiretel1) ViewGroup telContainer1;
    @BindView(R.id.layout_wiretel2) ViewGroup telContainer2;
    @BindView(R.id.layout_wiretel3) ViewGroup telContainer3;


    @BindView(R.id.tv_phone_val1)   TextView phoneText1;
    @BindView(R.id.tv_phone_val2)   TextView phoneText2;
    @BindView(R.id.tv_phone_val3)   TextView phoneText3;
    @BindView(R.id.tv_wiletel_val1) TextView telText1;
    @BindView(R.id.tv_wiletel_val2) TextView telText2;
    @BindView(R.id.tv_wiletel_val3) TextView telText3;

    @BindView(R.id.tv_name)TextView tv_name;
    @BindView(R.id.tv_qq)TextView tv_qq;
    @BindView(R.id.tv_birthday)TextView tv_birthday;
    @BindView(R.id.tv_wx)TextView tv_wx;
    @BindView(R.id.tv_email)TextView tv_email;
    @BindView(R.id.tv_memo)TextView tv_memo;
    @BindView(R.id.tv_depart)TextView tv_depart;
    @BindView(R.id.tv_contact_role)TextView tv_contact_role;

    @OnClick(R.id.img_title_left) void onBack() {
        if (contactUpdated) {
            Intent intent = new Intent();
            intent.putExtra("contact", contact);
            intent.putExtra("contactIndex", contactIndex);
            intent.putExtra("action", CONTACT_UPDATED);
            app.finishActivity(ContactDetailActivity.this,
                    MainApp.ENTER_TYPE_LEFT, RESULT_OK, intent);
        }
        else {
            super.onBackPressed();
        }
    }

    @Override
    public void onBackPressed() {
        onBack();
    }

    @OnClick(R.id.img_title_right) void onActionsheet() {
        ActionSheetDialog dialog = new ActionSheetDialog(this).builder();
        dialog.addSheetItem("编辑", ActionSheetDialog.SheetItemColor.Blue, new ActionSheetDialog.OnSheetItemClickListener() {
            @Override
            public void onClick(int which) {
                Bundle bundle = new Bundle();
                bundle.putSerializable("customer", customer);
                bundle.putSerializable("contract", contact);
                bundle.putSerializable(CustomerContractAddActivity.EXTRA_TYPE, CustomerContractAddActivity.EXTRA_TYPE_EDIT);
                Intent intent = new Intent(ContactDetailActivity.this, CustomerContractAddActivity.class);
                intent.putExtras(bundle);
                startActivityForResult(intent, CustomerAddActivity.REQUEST_CUSTOMER_NEW_CONTRACT);
            }
        });
        if (! contact.isDefault() ) {
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
                            CustomerService.deleteContact(customer.getId(), contact.getId())
                                    .subscribe(new DefaultLoyoSubscriber<Contact>(hud) {
                                        @Override
                                        public void onNext(Contact contact1) {

                                            Intent intent = new Intent();
                                            intent.putExtra("contact", contact1);
                                            intent.putExtra("contactIndex", contactIndex);
                                            intent.putExtra("action", CONTACT_DELETE);
                                            app.finishActivity(ContactDetailActivity.this,
                                                    MainApp.ENTER_TYPE_LEFT, RESULT_OK, intent);
                                        }
                                    });
                        }
                    }, "提示", "你确定要删除联系人?");
                }
            });
        }
        dialog.show();
    }

    @OnClick(R.id.layout_send_sms1) void onSMS1() {
        if (contact.telGroup.size() > 0) {
            onSMS(contact.telGroup.get(0));
        }
    }
    @OnClick(R.id.layout_send_sms2) void onSMS2() {
        if (contact.telGroup.size() > 1) {
            onSMS(contact.telGroup.get(1));
        }
    }
    @OnClick(R.id.layout_send_sms3) void onSMS3() {
        if (contact.telGroup.size() > 2) {
            onSMS(contact.telGroup.get(2));
        }
    }

    @OnClick(R.id.layout_phone_call1) void onPhoneCall1() {
        if (contact.telGroup.size() > 0) {
            onCallPhone(contact, contact.telGroup.get(0));
        }
    }
    @OnClick(R.id.layout_phone_call2) void onPhoneCall2() {
        if (contact.telGroup.size() > 1) {
            onCallPhone(contact, contact.telGroup.get(1));
        }
    }
    @OnClick(R.id.layout_phone_call3) void onPhoneCall3() {
        if (contact.telGroup.size() > 2) {
            onCallPhone(contact, contact.telGroup.get(2));
        }
    }

    @OnClick(R.id.layout_call_wiretel1) void onTelCall1() {
        if (contact.wiretelGroup.size() > 0) {
            onCallTel(contact, contact.wiretelGroup.get(0));
        }
    }
    @OnClick(R.id.layout_call_wiretel2) void onTelCall2() {
        if (contact.wiretelGroup.size() > 1) {
            onCallTel(contact, contact.wiretelGroup.get(1));
        }
    }
    @OnClick(R.id.layout_call_wiretel3) void onTelCall3() {
        if (contact.wiretelGroup.size() > 2) {
            onCallTel(contact, contact.wiretelGroup.get(2));
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_detail);
        ButterKnife.bind(this);
        phoneContainers = new ArrayList<>();
        phoneContainers.add(phoneContainer1);
        phoneContainers.add(phoneContainer2);
        phoneContainers.add(phoneContainer3);

        telContainers = new ArrayList<>();
        telContainers.add(telContainer1);
        telContainers.add(telContainer2);
        telContainers.add(telContainer3);

        phoneTexts = new ArrayList<>();
        phoneTexts.add(phoneText1);
        phoneTexts.add(phoneText2);
        phoneTexts.add(phoneText3);

        telTexts = new ArrayList<>();
        telTexts.add(telText1);
        telTexts.add(telText2);
        telTexts.add(telText3);
        img_title_left.setOnTouchListener(Global.GetTouch());
        img_title_right.setOnTouchListener(Global.GetTouch());
        tv_title_1.setText("联系人详情");
        loadIntentData();
        loadContact();
    }

    void loadIntentData() {
        if (getIntent() != null) {
            Bundle bundle = getIntent().getExtras();
            if (bundle != null) {
                customer = (Customer) bundle.getSerializable("customer");
                contact = (Contact) bundle.getSerializable("contact");
                contactIndex = bundle.getInt("contactIndex");
            }
        }
        canEdit = customer != null &&
                PermissionManager.getInstance().hasCustomerAuthority(customer.relationState,
                        customer.state, CustomerAction.CONTACT_ADD);
        img_title_right.setVisibility(canEdit?View.VISIBLE : View.INVISIBLE);
    }

    void loadContact() {
        tv_name.setText(contact.getName());
        tv_qq.setText(contact.getQq());
        tv_wx.setText(contact.getWx());
        tv_email.setText(contact.getEmail());
        tv_memo.setText(contact.getMemo());
        tv_birthday.setText(contact.getBirthStr());
        tv_depart.setText(contact.deptName);
        //联系人角色
        tv_contact_role.setText(TextUtils.isEmpty(contact.getContactRoleName())?"":contact.getContactRoleName());
        for (int i = 0; i < 3; i++) {
            if (i < contact.telGroup.size()) {
                phoneContainers.get(i).setVisibility(View.VISIBLE);
                phoneTexts.get(i).setText(contact.telGroup.get(i));
                ((TextView)findViewById(getResources().getIdentifier("tv_phone_name"+(i+1),"id",getPackageName()))).setText("手机"+(i+1));
            }
            else {
                phoneContainers.get(i).setVisibility(i == 0?View.VISIBLE:View.GONE);
            }
            //一个手机号码，就不显示序号
            if(0==i&&contact.telGroup.size()<=1){
                ((TextView)findViewById(R.id.tv_phone_name1)).setText("手机");
            }

            if (i < contact.wiretelGroup.size()) {
                telContainers.get(i).setVisibility(View.VISIBLE);
                telTexts.get(i).setText(contact.wiretelGroup.get(i));
                ((TextView)findViewById(getResources().getIdentifier("tv_wiletel_name"+(i+1),"id",getPackageName()))).setText("座机"+(i+1));

            }
            else {
                telContainers.get(i).setVisibility(i == 0?View.VISIBLE:View.GONE);
            }
            //一个座机号码，就不显示序号
            if(0==i&&contact.wiretelGroup.size()<=1){
                ((TextView)findViewById(R.id.tv_wiletel_name1)).setText("座机");
            }

        }
        getContactsFields();
    }

    private void getContactsFields() {
        CustomerService.getContactsField()
                .subscribe(new DefaultLoyoSubscriber<ArrayList<ContactLeftExtras>>(LoyoErrorChecker.SILENCE) {
                    @Override
                    public void onNext(ArrayList<ContactLeftExtras> contactLeftExtrasArrayList) {
                        leftExtrases = contactLeftExtrasArrayList;
                        loadExtra();
                    }
                });
    }

    private void loadExtra() {
        //添加动态字段
        contact_extra_holder.removeAllViews();
        contact_extra_holder.addView(new ContactListExtra(this, contact.getExtDatas(), leftExtrases, false, 14));
    }

    //用来处理打电话权限申请
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (Utils.CALL_REQUEST == requestCode) {
            PermissionTool.requestPermissionsResult(permissions, grantResults, new PermissionTool.PermissionsResultCallBack() {
                @Override
                public void success() {
                    Utils.call(ContactDetailActivity.this, callNum);
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
                    Utils.sendSms(ContactDetailActivity.this, callNum);
                }

                @Override
                public void fail() {
                    Toast("你拒绝了发短信权限，无法发送短信");
                }
            });
        }
    }

    @Override
    public void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != RESULT_OK || null == data) {
            return;
        }
        switch (requestCode) {

            case CustomerAddActivity.REQUEST_CUSTOMER_NEW_CONTRACT:
                boolean isDefault = contact.isDefault();
                contact = (Contact) data.getSerializableExtra("data");
                contact.setIsDefault(isDefault);
                this.loadContact();
                contactUpdated = true;
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
                                    app.startActivity(ContactDetailActivity.this, CallPhoneBackActivity.class,
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
        Utils.sendSms(this, phone);
    }

    @Override
    public void onCallPhone(final Contact contact, final String phone) {
        // 判断是否有绑定手机号
        if (checkMobile()) {
            callNum = phone;
            boolean checkTag = RegularCheck.isYunPhone(phone);
            final CallPhonePopView callPhonePopView = new CallPhonePopView(this, contact.getName(), checkTag);
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
                        Utils.call(ContactDetailActivity.this, formattedPhone);
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
            final CallPhonePopView callPhonePopView = new CallPhonePopView(this, contact.getName(), checkTag);
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

                    Utils.call(ContactDetailActivity.this, formattedPhone);
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
        showCommitLoading();
        CustomerService.setDefaultContact(customer.getId(), contact.getId())
                .subscribe(new DefaultLoyoSubscriber<Contact>(hud) {
                    @Override
                    public void onNext(Contact contact1) {
                    }
                });
    }

    @Override
    public void onContactSelect(Contact contact, int contactIndex) {
    }

    private boolean checkMobile() {
        if (null == MainApp.user.mobile || TextUtils.isEmpty(MainApp.user.mobile)) {
            final SweetAlertDialogView sweetAlertDialogView = new SweetAlertDialogView(this);
            sweetAlertDialogView.alertHandle(new SweetAlertDialog.OnSweetClickListener() {
                @Override
                public void onClick(SweetAlertDialog sweetAlertDialog) {
                    sweetAlertDialogView.sweetAlertDialog.dismiss();
                }
            }, new SweetAlertDialog.OnSweetClickListener() {
                @Override
                public void onClick(SweetAlertDialog sweetAlertDialog) {
                    sweetAlertDialogView.sweetAlertDialog.dismiss();
                    MainApp.getMainApp().startActivity(ContactDetailActivity.this,
                            EditUserMobileActivity.class,
                            MainApp.ENTER_TYPE_RIGHT, false, null);
                }
            }, "提示", getString(R.string.app_homeqq_message));
            return false;
        } else {
            return true;
        }
    }
}
