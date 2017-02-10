package com.loyo.oa.v2.activityui.signin;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.library.module.widget.loading.LoadingLayout;
import com.loyo.oa.common.utils.DateTool;
import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.customer.CustomerSearchOrPickerActivity;
import com.loyo.oa.v2.activityui.customer.model.Customer;
import com.loyo.oa.v2.activityui.signin.api.SignInService;
import com.loyo.oa.v2.activityui.signin.bean.SigninSelectCustomer;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.beans.BaseBeanT;
import com.loyo.oa.v2.beans.PaginationX;
import com.loyo.oa.v2.common.ExtraAndResult;
import com.loyo.oa.v2.network.DefaultLoyoSubscriber;
import com.loyo.oa.v2.tool.BaseSearchActivity;
import com.loyo.oa.v2.tool.Utils;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 说明，这个类和CustomerSearchOrPickerActivity类不冲突，返回数据模型不一样，主要是用在拜访签到等的客户选择。
 * Created by jie on 17/2/7.
 */

public class SigninSelectCustomerSearchActivity extends BaseSearchActivity<SigninSelectCustomer> {
    public static final String EXTRA_PICKER_ID = "Id";

    @Override
    public void getData() {
        HashMap<String, Object> params = new HashMap<>();
        params.put("pageIndex", paginationX.getShouldLoadPageIndex());
        params.put("pageSize", paginationX.getPageSize());
        params.put("keyWords", edt_search.getText().toString().trim());
        SignInService.signInSearchCutomer(params).subscribe(new DefaultLoyoSubscriber<BaseBeanT<PaginationX<SigninSelectCustomer>>>(ll_loading) {
            @Override
            public void onNext(BaseBeanT<PaginationX<SigninSelectCustomer>> customerPaginationX) {
                success(customerPaginationX.data);
            }

            @Override
            public void onError(Throwable e) {
                fail(e);
            }
        });
    }

    @Override
    public void onListItemClick(View view, int position) {
        if (jumpNewPage) {
            Bundle b = new Bundle();
            b.putInt(ExtraAndResult.DYNAMIC_ADD_ACTION, ExtraAndResult.DYNAMIC_ADD_CUSTOMER);
            b.putSerializable(Customer.class.getName(), paginationX.getRecords().get(position));
            b.putString(EXTRA_PICKER_ID, paginationX.getRecords().get(position).id);
            MainApp.getMainApp().startActivity(this, cls, MainApp.ENTER_TYPE_RIGHT, false, b);
        } else {
            Intent intent = new Intent();
            intent.putExtra("data", paginationX.getRecords().get(position));
            app.finishActivity(SigninSelectCustomerSearchActivity.this, MainApp.ENTER_TYPE_LEFT, RESULT_OK, intent);
        }
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup, SigninSelectCustomer data) {
        Holder holder;
        if (convertView == null) {
            holder = new Holder();
            convertView = LayoutInflater.from(SigninSelectCustomerSearchActivity.this).inflate(R.layout.item_signin_select_customer, null);
            holder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
            holder.tv_distance = (TextView) convertView.findViewById(R.id.tv_distance);
            holder.tv_location = (TextView) convertView.findViewById(R.id.tv_location);
            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }
        holder.setContent(data);
        return convertView;
    }
    class Holder {
        TextView tv_name, tv_distance, tv_location;
        public void setContent(SigninSelectCustomer item) {
            tv_name.setText(item.name);
            if (item.position != null) {
                tv_location.setText(item.position.addr);
            }
        }
    }

}
