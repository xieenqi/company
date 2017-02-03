package com.loyo.oa.v2.activityui.customer;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.library.module.widget.loading.LoadingLayout;
import com.loyo.oa.common.utils.DateTool;
import com.loyo.oa.v2.activityui.customer.model.Customer;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.beans.PaginationX;
import com.loyo.oa.v2.common.ExtraAndResult;
import com.loyo.oa.v2.common.FinalVariables;
import com.loyo.oa.v2.customermanagement.api.ICustomer;
import com.loyo.oa.v2.network.DefaultLoyoSubscriber;
import com.loyo.oa.v2.network.LoyoErrorChecker;
import com.loyo.oa.v2.network.RetrofitAdapterFactory;
import com.loyo.oa.v2.tool.BaseSearchActivity;
import com.loyo.oa.v2.tool.Utils;

import java.util.HashMap;

/**
 * com.loyo.oa.v2.activity
 * 描述 : 搜索或者选择项目，eg，客户管理里面搜索，任务，审批的关联项目选择客户调用
 * 如果传递jumpNewPage＝true，需要传入一个class，然后跳到这个页面,type是客户类型
 * 作者 : jie
 */
public class CustomerSearchOrPickerActivity extends BaseSearchActivity<Customer> {
    //可传入参数定义
    public static final String EXTRA_JUMP_NEW_PAGE = "jumpNewPage";
    public static final String EXTRA_JUMP_PAGE_CLASS = "class";
    public static final String EXTRA_CAN_BE_EMPTY = "canBeEmpty";
    public static final String EXTRA_TYPE = "type";
    public static final String EXTRA_HAVE_TAG = "haveTag";
    public static final String EXTRA_PICKER_ID = "Id";

    private int type = 0;
    private boolean jumpNewPage = false;
    private Class<?> cls;
    private boolean canBeEmpty = false;
    private boolean haveTag = true;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        if (null != getIntent()) {
            type = getIntent().getIntExtra(EXTRA_TYPE, 0);
            canBeEmpty = getIntent().getBooleanExtra(EXTRA_CAN_BE_EMPTY, false);
            jumpNewPage = getIntent().getBooleanExtra(EXTRA_JUMP_NEW_PAGE, false);
            cls = (Class<?>) getIntent().getSerializableExtra(EXTRA_JUMP_PAGE_CLASS);
            haveTag = getIntent().getBooleanExtra(EXTRA_HAVE_TAG, true);

        }
        super.onCreate(savedInstanceState);
    }

    @Override
    public boolean isShowHeadView() {
        return canBeEmpty;
    }

    @Override
    public void onListItemClick(View view, int position) {
        if (jumpNewPage) {
            Bundle b = new Bundle();
            b.putInt(ExtraAndResult.DYNAMIC_ADD_ACTION, ExtraAndResult.DYNAMIC_ADD_CUSTOMER);
            b.putSerializable(Customer.class.getName(), paginationX.getRecords().get(position));
            b.putString(EXTRA_PICKER_ID, paginationX.getRecords().get(position).getId());
            MainApp.getMainApp().startActivity(this, cls, MainApp.ENTER_TYPE_RIGHT, false, b);
        } else {
            Intent intent = new Intent();
            intent.putExtra("data", paginationX.getRecords().get(position));
            app.finishActivity(CustomerSearchOrPickerActivity.this, MainApp.ENTER_TYPE_LEFT, RESULT_OK, intent);
        }
    }

    @Override
    public void bindData(CommonSearchAdapter.SearchViewHolder viewHolder, Customer data) {
        viewHolder.time.setText("跟进时间：" + DateTool.getDateTimeFriendly(data.lastActAt));
        viewHolder.title.setText(data.name);
        viewHolder.content.setText("标签:" + Utils.getTagItems(data));
        viewHolder.content.setVisibility(haveTag?View.VISIBLE:View.GONE);
    }


    @Override
    public void getData() {
        String url; //这里填写我负责的查询 等服务端接口
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
            /*我的客户数集(我参与的 和 我负责的)*/
            case 5:
                url = FinalVariables.QUERY_CUSTOMERS_MY;
                break;
            default:
                //如果没有获取到type 参数，就抛出异常
                throw new UnsupportedOperationException("type类型为空或者不支持！");
        }
        subscribe = RetrofitAdapterFactory.getInstance()
                .build(/*TODO:*/url)
                .create(ICustomer.class)
                .getCustomers(params)
                .compose(RetrofitAdapterFactory.<PaginationX<Customer>>compatApplySchedulers())
                .subscribe(new DefaultLoyoSubscriber<PaginationX<Customer>>(LoyoErrorChecker.SILENCE) {
                    @Override
                    public void onError(Throwable e) {
                        CustomerSearchOrPickerActivity.this.fail(e);
                    }

                    @Override
                    public void onNext(PaginationX<Customer> customerPaginationX) {
                        CustomerSearchOrPickerActivity.this.success(customerPaginationX);

                    }
                });
    }
}
