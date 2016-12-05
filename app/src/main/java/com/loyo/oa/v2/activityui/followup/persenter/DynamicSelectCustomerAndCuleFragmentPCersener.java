package com.loyo.oa.v2.activityui.followup.persenter;

import com.loyo.oa.v2.activityui.clue.model.ClueList;
import com.loyo.oa.v2.activityui.clue.model.ClueListItem;
import com.loyo.oa.v2.activityui.followup.viewcontrol.DynamicSelectCustomerAndCuleFragmentVControl;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.activityui.customer.model.Customer;
import com.loyo.oa.v2.beans.PaginationX;
import com.loyo.oa.v2.common.FinalVariables;
import com.loyo.oa.v2.common.http.HttpErrorCheck;
import com.loyo.oa.v2.point.IClue;
import com.loyo.oa.v2.point.ICustomer;
import com.loyo.oa.v2.tool.Config_project;
import com.loyo.oa.v2.tool.LogUtil;
import com.loyo.oa.v2.tool.RCallback;
import com.loyo.oa.v2.tool.RestAdapterFactory;

import java.util.ArrayList;
import java.util.HashMap;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * 跟进对象 选择客户 和 选择线索 公用的一个 persenter
 * Created by xeq on 16/11/10.
 */

public class DynamicSelectCustomerAndCuleFragmentPCersener implements DynamicSelectCustomerAndCuleFragmentPersener {
    public static final int SELECT_CUSTOMER = 101;
    public static final int SELECT_CULE = 102;
    private DynamicSelectCustomerAndCuleFragmentVControl vControl;
    private int type, pageCus = 1, pageClue = 1;
    private boolean isPullCus, isPullClue;
    private ArrayList<Customer> mCustomers = new ArrayList<>();
    private ArrayList<ClueListItem> mCule = new ArrayList<>();

    public DynamicSelectCustomerAndCuleFragmentPCersener(DynamicSelectCustomerAndCuleFragmentVControl vControl, int type) {
        this.type = type;
        this.vControl = vControl;
    }

    @Override
    public void getPageData(Object... pag) {
        if (type == SELECT_CUSTOMER) {
            getCustomerData();
        } else if (type == SELECT_CULE) {
            getCuleData();
        }
    }

    @Override
    public void bindPageData(Object obj) {

    }

    @Override
    public void sendPageData(Object obj) {

    }

    /**
     * 获取我的客户的数据
     */
    private void getCustomerData() {
        vControl.showProgress("");
        HashMap<String, Object> params = new HashMap<>();
        params.put("pageIndex", pageCus);
        params.put("pageSize", 15);
        LogUtil.d("我的客户查询参数：" + MainApp.gson.toJson(params));
        RestAdapterFactory.getInstance().build(FinalVariables.QUERY_CUSTOMERS_MY).create(ICustomer.class).query(params, new RCallback<PaginationX<Customer>>() {
                    @Override
                    public void success(PaginationX<Customer> result, Response response) {
                        HttpErrorCheck.checkResponse("我的客户", response);
                        if (null == result.records || result.records.size() == 0) {
                            if (isPullCus) {
                                vControl.showMsg("没有更多数据了!");
                            } else {
                                mCustomers.clear();
//                                vControl.setEmptyView();
                            }
                            vControl.getDataComplete();
                        } else {
                            if (isPullCus) {
                                mCustomers.addAll(result.records);
                            } else {
                                mCustomers.clear();
                                mCustomers = result.records;
                            }
                        }
                        vControl.getDataComplete();
                        vControl.bindCustomerData(mCustomers);
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        vControl.getDataComplete();
                        HttpErrorCheck.checkError(error);
                    }
                }
        );
    }

    /**
     * 获取我的 线索 的数据
     */
    private void getCuleData() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("pageIndex", pageClue);
        map.put("pageSize", 15);
        RestAdapterFactory.getInstance().build(Config_project.API_URL_CUSTOMER()).
                create(IClue.class).getMyCluelist(map, new Callback<ClueList>() {
            @Override
            public void success(ClueList clueList, Response response) {
                HttpErrorCheck.checkResponse("我的线索列表：", response);
                ArrayList<ClueListItem> data = clueList.data.records;
                if (null == data || data.size() == 0) {
                    if (isPullClue) {
                        vControl.showMsg("没有更多数据了!");
                    } else {
                        mCule.clear();
//                                vControl.setEmptyView();
                    }
                    vControl.getDataComplete();
                } else {
                    if (isPullClue) {
                        mCule.addAll(data);
                    } else {
                        mCule.clear();
                        mCule = data;
                    }
                }
                vControl.getDataComplete();
                vControl.bindClueData(mCule);
            }

            @Override
            public void failure(RetrofitError error) {
                vControl.getDataComplete();
                HttpErrorCheck.checkError(error);
            }
        });
    }

    @Override
    public void pullDownCus() {
        isPullCus = false;
        pageCus = 1;
        getPageData(pageCus);
    }

    @Override
    public void pullUpCus() {
        isPullCus = true;
        pageCus++;
        getPageData(pageCus);
    }

    @Override
    public void pullDownCule() {
        isPullClue = false;
        pageClue = 1;
        getPageData(pageClue);
    }

    @Override
    public void pullUpCule() {
        isPullClue = true;
        pageClue++;
        getPageData(pageClue);
    }
}
