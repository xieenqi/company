package com.loyo.oa.v2.activityui.customer.presenter.impl;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.clue.ClueDetailActivity;
import com.loyo.oa.v2.activityui.customer.model.Contact;
import com.loyo.oa.v2.activityui.customer.model.Member;
import com.loyo.oa.v2.activityui.customer.model.MembersRoot;
import com.loyo.oa.v2.activityui.customer.presenter.CustomerDetailInfoPresenter;
import com.loyo.oa.v2.activityui.customer.viewcontrol.CustomerDetailinfoView;
import com.loyo.oa.v2.activityui.setting.EditUserMobileActivity;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.beans.Customer;
import com.loyo.oa.v2.beans.Permission;
import com.loyo.oa.v2.common.RegularCheck;
import com.loyo.oa.v2.common.http.HttpErrorCheck;
import com.loyo.oa.v2.customview.ActionSheetDialog;
import com.loyo.oa.v2.customview.CallPhonePopView;
import com.loyo.oa.v2.customview.SweetAlertDialogView;
import com.loyo.oa.v2.point.ICustomer;
import com.loyo.oa.v2.tool.Config_project;
import com.loyo.oa.v2.tool.RCallback;
import com.loyo.oa.v2.tool.RestAdapterFactory;
import com.loyo.oa.v2.tool.Utils;
import com.loyo.oa.voip.VoIPCallActivity;

import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit.RetrofitError;
import retrofit.client.Response;

import static com.loyo.oa.v2.common.Global.Toast;

/**
 * Created by yyy on 16/11/7.
 */

public class CustomerDetailinfoPresenterimpl implements CustomerDetailInfoPresenter {

    private Context mContext;
    private CustomerDetailinfoView crolView;
    private String contactId, customerId;

    public CustomerDetailinfoPresenterimpl(Context mContext, CustomerDetailinfoView crolView) {
        this.mContext = mContext;
        this.crolView = crolView;
    }

    public void setDefaultContact(String contactId, String customerId) {
        this.contactId = contactId;
        this.customerId = customerId;
    }

    /**
     * 丢入公海操作
     */
    @Override
    public void toPublic(String id) {
        RestAdapterFactory.getInstance().build(Config_project.API_URL_CUSTOMER()).create(ICustomer.class).toPublic(id, new RCallback<Customer>() {
            @Override
            public void success(final Customer newCustomer, final Response response) {
                crolView.toPublicEmbl();
            }

            @Override
            public void failure(final RetrofitError error) {
                HttpErrorCheck.checkError(error);
            }
        });
    }

    /**
     * 删除客户操作
     */
    @Override
    public void delete(String id) {
        RestAdapterFactory.getInstance().build(Config_project.API_URL_CUSTOMER()).create(ICustomer.class).delete(id, new RCallback<Customer>() {
            @Override
            public void success(final Customer newCustomer, final Response response) {
                crolView.deleteEmbl();
            }

            @Override
            public void failure(final RetrofitError error) {
                HttpErrorCheck.checkError(error);
                crolView.showMsg("删除客户失败");
            }
        });
    }

    /**
     * 获取参与人权限
     */
    @Override
    public void getMembersRoot() {
        crolView.showProgress("");
        RestAdapterFactory.getInstance().build(Config_project.API_URL_CUSTOMER()).create(ICustomer.class).
                getMembersRoot(new RCallback<MembersRoot>() {
                    @Override
                    public void success(MembersRoot membersRoot, Response response) {
                        HttpErrorCheck.checkResponse("参与人权限", response);
                        crolView.getMembersRootEmbl(membersRoot);
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        super.failure(error);
                        HttpErrorCheck.checkError(error);
                    }
                });
    }

    /**
     * 获取客户详情数据
     */
    @Override
    public void getData(String id) {
        RestAdapterFactory.getInstance().build(Config_project.API_URL_CUSTOMER()).create(ICustomer.class).getCustomerById(id, new RCallback<Customer>() {
            @Override
            public void success(final Customer customer, final Response response) {
                HttpErrorCheck.checkResponse("客户详情-->", response);
                if (customer == null) {
                    crolView.showMsg("获取数据失败");
                    return;
                }
                crolView.getDataSuccessEmbl(customer);
            }

            @Override
            public void failure(final RetrofitError error) {
                HttpErrorCheck.checkError(error);
                crolView.getDataErrorEmle();
            }
        });
    }

    /**
     * 判断是否为参与人
     */
    @Override
    public boolean isMenber(Customer mCustomer) {
        if (null != mCustomer) {
            for (Member element : mCustomer.members) {
                if (MainApp.user.id.equals(element.getUser().getId())) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 显示编辑客户弹出框
     */
    @Override
    public void showEditPopu(Activity mActivity) {
        boolean isDelte = false, isPublic = false;
         /*超级管理员\web控制权限判断*/
        if (!MainApp.user.isSuperUser()) {
            Permission perDelete = MainApp.rootMap.get("0405");
            Permission perOcean = MainApp.rootMap.get("0403");
            if (perDelete != null && perDelete.enable)
                isDelte = true;
            if (perOcean != null && perOcean.enable)
                isPublic = true;
        }

        ActionSheetDialog dialog = new ActionSheetDialog(mActivity).builder();
        if (isDelte || MainApp.user.isSuperUser())
            dialog.addSheetItem("删除", ActionSheetDialog.SheetItemColor.Red, new ActionSheetDialog.OnSheetItemClickListener() {
                @Override
                public void onClick(int which) {
                    crolView.setPopViewEmbl(true, "你确定要删除客户?");
                }
            });
        if (isPublic || MainApp.user.isSuperUser())
            dialog.addSheetItem("投入公海", ActionSheetDialog.SheetItemColor.Blue, new ActionSheetDialog.OnSheetItemClickListener() {
                @Override
                public void onClick(int which) {
                    crolView.setPopViewEmbl(true, "投入公海，相当于放弃此客户所有数据和管理权限，您确定要投入公海?");
                }
            });
        dialog.show();
    }

    /**
     * 拨打电话弹出框
     */
    @Override
    public void paymentSet(final Activity mActivity, final String phone, final int callType, final String name) {
        boolean checkTag = false;
        if (callType == 0) {
            checkTag = RegularCheck.isYunPhone(phone);
        } else {
            checkTag = RegularCheck.isYunTell(phone);
        }

        final CallPhonePopView callPhonePopView = new CallPhonePopView(mContext, name, checkTag);
        callPhonePopView.show();
        callPhonePopView.setCanceledOnTouchOutside(true);
        /*商务电话*/
        callPhonePopView.businessPhone(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle mBundle = new Bundle();
                mBundle.putString(VoIPCallActivity.CALLEE_PHONE_KEY, phone.replaceAll(" +", ""));
                mBundle.putString(VoIPCallActivity.CALLEE_NAME_KEY, name.trim().toString());
                MainApp.getMainApp().startActivity(mActivity, VoIPCallActivity.class, MainApp.ENTER_TYPE_RIGHT, false, mBundle);
                callPhonePopView.dismiss();
            }
        });
         /*商务电话-直拨*/
        callPhonePopView.directPhone(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle mBundle = new Bundle();
                mBundle.putString(VoIPCallActivity.CALLEE_PHONE_KEY, phone);
                mBundle.putString(VoIPCallActivity.CALLEE_NAME_KEY, name);
                mBundle.putString(VoIPCallActivity.CALLEE_USER_KEY, contactId);
                mBundle.putString(VoIPCallActivity.CALLEE_CUSTOMER_KEY, customerId);
                MainApp.getMainApp().startActivity(mActivity, VoIPCallActivity.class, MainApp.ENTER_TYPE_RIGHT, false, mBundle);
                callPhonePopView.dismiss();
            }
        });
        /*普通电话*/
        callPhonePopView.commonlyPhone(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (callType == 0) {
                    if (RegularCheck.isMobilePhone(phone)) {
                        Utils.call(mContext, phone);
                    } else {
                        Toast("电话号码格式不正确或为空!");
                    }
                } else {
                    Utils.call(mContext, phone);
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

    /**
     * 电话号码格式验证
     */
    @Override
    public void isMobile(final Activity mActivity, final String phone, final int callType, final String name) {
        if (null == MainApp.user.mobile || TextUtils.isEmpty(MainApp.user.mobile)) {
            final SweetAlertDialogView sweetAlertDialogView = new SweetAlertDialogView(mContext);
            sweetAlertDialogView.alertHandle(new SweetAlertDialog.OnSweetClickListener() {
                @Override
                public void onClick(SweetAlertDialog sweetAlertDialog) {
                    sweetAlertDialogView.sweetAlertDialog.dismiss();
                }
            }, new SweetAlertDialog.OnSweetClickListener() {
                @Override
                public void onClick(SweetAlertDialog sweetAlertDialog) {
                    sweetAlertDialogView.sweetAlertDialog.dismiss();
                    MainApp.getMainApp().startActivity((Activity) mContext, EditUserMobileActivity.class, MainApp.ENTER_TYPE_RIGHT, false, null);
                }
            }, "提示", mContext.getString(R.string.app_homeqq_message));
        } else {
            paymentSet(mActivity, phone, callType, name);
        }
    }

}
