package com.loyo.oa.v2.activityui.customer;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.j256.ormlite.stmt.query.In;
import com.library.module.widget.loading.LoadingLayout;
import com.loyo.oa.common.utils.DateTool;
import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.customer.model.Customer;
import com.loyo.oa.v2.activityui.project.OtherModuleSelectSelectProjectActivity;
import com.loyo.oa.v2.activityui.project.api.ProjectService;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.beans.PaginationX;
import com.loyo.oa.v2.beans.Project;
import com.loyo.oa.v2.common.ExtraAndResult;
import com.loyo.oa.v2.common.FinalVariables;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.customermanagement.api.ICustomer;
import com.loyo.oa.v2.network.DefaultLoyoSubscriber;
import com.loyo.oa.v2.network.RetrofitAdapterFactory;
import com.loyo.oa.v2.tool.BaseSearchActivity;
import com.loyo.oa.v2.tool.Utils;

import java.util.HashMap;

/**
 * com.loyo.oa.v2.activity
 * 描述 : 选择项目，这个主要是提供给其他模块调用，选择项目，eg，任务，审批的关联项目调用
 * 作者 : ykb
 * 时间 : 15/10/14.
 */
public class OtherModuleSelectSelectCustomerActivity extends BaseSearchActivity<Customer> {

    private int type = 0;
    private boolean jumpNewPage=false;
    private Class<?> cls;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        if (null != getIntent()) {
            type = getIntent().getIntExtra(ExtraAndResult.EXTRA_TYPE, 0);
            jumpNewPage = getIntent().getBooleanExtra("jumpNewPage", false);
            cls= (Class<?>) getIntent().getSerializableExtra("class");
        }
        super.onCreate(savedInstanceState);
        getPageData();
    }

    @Override
    public void onListItemClick(View view, int position) {
        if(jumpNewPage){
            Intent mIntent=new Intent();
            mIntent.putExtra(ExtraAndResult.DYNAMIC_ADD_ACTION, ExtraAndResult.DYNAMIC_ADD_CUSTOMER);
            mIntent.putExtra(Customer.class.getName(), paginationX.getRecords().get(position));
            mIntent.setClass(this,cls);
            startActivity(mIntent);
            overridePendingTransition(R.anim.enter_righttoleft, R.anim.exit_righttoleft);
        }else{
            Intent intent=new Intent();
            intent.putExtra("data",paginationX.getRecords().get(position));
            app.finishActivity(OtherModuleSelectSelectCustomerActivity.this, MainApp.ENTER_TYPE_LEFT, RESULT_OK, intent);
        }
    }

    @Override
    public void bindData(CommonSearchAdapter.SearchViewHolder viewHolder, Customer data) {
        viewHolder.time.setText("跟进时间：" + DateTool.getDateTimeFriendly(data.lastActAt));
        viewHolder.title.setText(data.name);
        viewHolder.content.setText("标签:" + Utils.getTagItems(data));
    }


    @Override
    public void getData() {
        String url = FinalVariables.SEARCH_CUSTOMERS_SELF; //这里填写我负责的查询 等服务端接口
        HashMap<String, Object> params = new HashMap<>();

        params.put("pageIndex", paginationX.getShouldLoadPageIndex());
        params.put("pageSize", paginationX.getPageSize());
        params.put("keyWords", strSearch);
        switch (type) {
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
                        OtherModuleSelectSelectCustomerActivity.this.fail(e);
                    }

                    @Override
                    public void onNext(PaginationX<Customer> customerPaginationX) {
                        OtherModuleSelectSelectCustomerActivity.this.success(customerPaginationX);

                    }
                });
    }
}
