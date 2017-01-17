package com.loyo.oa.v2.activityui.customer;

import android.content.Intent;
import android.os.Bundle;

import com.library.module.widget.loading.LoadingLayout;
import com.loyo.oa.v2.activityui.customer.model.Customer;
import com.loyo.oa.v2.beans.PaginationX;
import com.loyo.oa.v2.common.FinalVariables;
import com.loyo.oa.v2.customermanagement.api.ICustomer;
import com.loyo.oa.v2.customview.SweetAlertDialogView;
import com.loyo.oa.v2.network.DefaultLoyoSubscriber;
import com.loyo.oa.v2.network.RetrofitAdapterFactory;
import com.loyo.oa.v2.permission.BusinessOperation;
import com.loyo.oa.v2.permission.PermissionManager;
import com.loyo.oa.v2.tool.BaseSearchActivity;

import java.util.HashMap;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class CustomerSearchActivity extends BaseSearchActivity<Customer> {

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

//    @Override
    protected void openDetail(final int position) {

        boolean customerAuth = PermissionManager.getInstance().hasPermission(BusinessOperation.CUSTOMER_MANAGEMENT);
        if (!customerAuth) {

            SweetAlertDialogView sweetAlertDialogView = new SweetAlertDialogView(this);
            sweetAlertDialogView.alertMessageClick(new SweetAlertDialog.OnSweetClickListener() {
                @Override
                public void onClick(SweetAlertDialog sweetAlertDialog) {
                    sweetAlertDialog.dismiss();
                }
            }, "提示", "你无此功能权限");
            return;
        }

        Intent intent = new Intent();
        intent.setClass(mContext, CustomerDetailInfoActivity_.class);
        intent.putExtra("Id", adapter.getItem(position).getId());
        startActivity(intent);
    }


    @Override
    public void getData() {
        String url = FinalVariables.SEARCH_CUSTOMERS_SELF; //这里填写我负责的查询 等服务端接口
        HashMap<String, Object> params = new HashMap<>();

        params.put("pageIndex", paginationX.getShouldLoadPageIndex());
        params.put("pageSize", paginationX.getPageSize());
        params.put("keyWords", strSearch);
        switch (customerType) {
            /*我负责的查询*/
            case 1:
                url = FinalVariables.SEARCH_CUSTOMERS_RESPON;
                break;
            /*我参与的查询*/
            case 2:
                url = FinalVariables.SEARCH_CUSTOMERS_MEMBER;
                break;
            /*团队查询*/
            case 3:
                url = FinalVariables.SEARCH_CUSTOMERS_TEAM;
                break;
            /*公海查询*/
            case 4:
                url = FinalVariables.SEARCH_CUSTOMERS_PUBLIC;
                break;
            default:
                Toast("参数异常,请重启App");
                finish();
                break;

        }
        RetrofitAdapterFactory.getInstance()
                .build(/*TODO:*/url)
                .create(ICustomer.class)
                .getCustomers(params)
                .compose(RetrofitAdapterFactory.<PaginationX<Customer>>compatApplySchedulers())
                .subscribe(new DefaultLoyoSubscriber<PaginationX<Customer>>() {
                    @Override
                    public void onError(Throwable e) {
                        CustomerSearchActivity.this.fail(e);
                    }

                    @Override
                    public void onNext(PaginationX<Customer> customerPaginationX) {
                        CustomerSearchActivity.this.success(customerPaginationX);

                    }
                });
    }
}
