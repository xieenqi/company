package com.loyo.oa.v2.activityui.customer;

import android.content.Intent;
import android.os.Bundle;

import com.loyo.oa.v2.activityui.customer.model.Customer;
import com.loyo.oa.v2.common.FinalVariables;
import com.loyo.oa.v2.customview.SweetAlertDialogView;
import com.loyo.oa.v2.permission.BusinessOperation;
import com.loyo.oa.v2.permission.PermissionManager;
import com.loyo.oa.v2.point.ICustomer;
import com.loyo.oa.v2.tool.BaseSearchActivity;
import com.loyo.oa.v2.tool.RestAdapterFactory;

import java.util.HashMap;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class CustomerSearchActivity extends BaseSearchActivity<Customer> {

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
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

        params.put("pageIndex", paginationX.getPageIndex());
        params.put("pageSize", isTopAdd ? lstData.size() >= 20 ? lstData.size() : 20 : 20);
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
        RestAdapterFactory.getInstance().build(url).create(ICustomer.class).query(params, this);
    }
}
