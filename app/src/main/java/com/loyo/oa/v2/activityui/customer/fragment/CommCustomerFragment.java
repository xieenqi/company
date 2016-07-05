package com.loyo.oa.v2.activityui.customer.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.customer.CustomerDetailInfoActivity_;
import com.loyo.oa.v2.activityui.customer.CustomerManagerActivity;
import com.loyo.oa.v2.activityui.customer.adapter.CommCustomerAdapter;
import com.loyo.oa.v2.activityui.sale.bean.SaleTeamScreen;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.beans.Customer;
import com.loyo.oa.v2.beans.PaginationX;
import com.loyo.oa.v2.beans.Permission;
import com.loyo.oa.v2.activityui.customer.bean.Tag;
import com.loyo.oa.v2.common.ExtraAndResult;
import com.loyo.oa.v2.common.FinalVariables;
import com.loyo.oa.v2.common.http.HttpErrorCheck;
import com.loyo.oa.v2.point.ICustomer;
import com.loyo.oa.v2.tool.BaseFragment;
import com.loyo.oa.v2.tool.BaseMainListFragment;
import com.loyo.oa.v2.tool.LogUtil;
import com.loyo.oa.v2.tool.RCallback;
import com.loyo.oa.v2.tool.RestAdapterFactory;
import com.loyo.oa.v2.customview.SaleCommPopupView;
import com.loyo.oa.v2.customview.ScreenTagPopupView;
import com.loyo.oa.v2.customview.pullToRefresh.PullToRefreshBase;
import com.loyo.oa.v2.customview.pullToRefresh.PullToRefreshListView;
import java.util.ArrayList;
import java.util.HashMap;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * 【公海客户】列表
 * Created by yyy on 16/6/1.
 */
public class CommCustomerFragment extends BaseFragment implements PullToRefreshBase.OnRefreshListener2{

    private Intent mIntent;
    private View mView;
    private Button btn_add;
    private ViewStub emptyView;
    private ImageView tagImage1;
    private ImageView tagImage2;
    private LinearLayout screen1;
    private LinearLayout screen2;
    private Permission permission;
    private SaleTeamScreen saleTeamScreen;
    private PullToRefreshListView listView;
    private SaleCommPopupView saleCommPopupView;
    private ScreenTagPopupView screenTagPopupView;
    private WindowManager.LayoutParams windowParams;
    private CommCustomerAdapter adapter;

    private String filed = "lastActAt";
    private String order = "desc";
    private String userId = "";
    private String tagItemIds = "";
    private String departmentId = "";
    private int page = 1;
    private int tagPostion;
    private boolean isPullUp = false;

    private PaginationX<Customer> mPagination = new PaginationX<>(20);
    private ArrayList<Customer> mCustomers = new ArrayList<>();
    private String[] sort = {"跟进时间 倒序", "跟进时间 顺序", "创建时间 倒序", "创建时间 顺序"};
    private ArrayList<SaleTeamScreen> sortData = new ArrayList<>();
    private ArrayList<Tag> mTags;

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg){

            switch(msg.what){
                //时间筛选
                case CustomerManagerActivity.CUSTOMER_TIME:
                    tagPostion = msg.getData().getInt("data");
                    switch (tagPostion){
                        case 0:
                            filed = "lastActAt";
                            order = "desc";
                            break;
                        case 1:
                            filed = "lastActAt";
                            order = "asc";
                            break;
                        case 2:
                            filed = "createdAt";
                            order = "desc";
                            break;
                        case 3:
                            filed = "createdAt";
                            order = "asc";
                            break;
                    }
                    getData();
                    break;

                //标签筛选
                case CustomerManagerActivity.CUSTOMER_TAG:
                    tagItemIds = msg.getData().getString("tagid");
                    getData();
                    break;

                //标签取消
                case CustomerManagerActivity.CUSTOMER_CANCEL:
                    tagItemIds = msg.getData().getString("tagid");
                    getData();
                    break;

                //公海挑入
                case CustomerManagerActivity.CUSTOMER_COMM_RUSH:
                    getData();
                    break;
            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (null == mView) {
            mView = inflater.inflate(R.layout.fragment_cus, null);
            initView(mView);
        }
        return mView;
    }

    @Override
    public void onPullDownToRefresh(PullToRefreshBase refreshView) {
        page = 1;
        isPullUp = false;
        mPagination.setPageIndex(1);
        getData();
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase refreshView) {
        page++;
        isPullUp = true;
        mPagination.setPageIndex(mPagination.getPageIndex() + 1);
        getData();
    }

    public void initView(View view){
        mTags = (ArrayList<Tag>) getArguments().getSerializable("tag");
        permission = (Permission) getArguments().getSerializable("permission");

        for (int i = 0; i < sort.length; i++) {
            saleTeamScreen = new SaleTeamScreen();
            saleTeamScreen.setName(sort[i]);
            saleTeamScreen.setIndex(false);
            sortData.add(saleTeamScreen);
        }

        btn_add = (Button) view.findViewById(R.id.btn_add);
        screen1 = (LinearLayout) view.findViewById(R.id.cus_screen1);
        screen2 = (LinearLayout) view.findViewById(R.id.cus_screen2);
        tagImage1 = (ImageView) view.findViewById(R.id.cus_screen1_iv1);
        tagImage2 = (ImageView) view.findViewById(R.id.cus_screen1_iv2);
        emptyView = (ViewStub) mView.findViewById(R.id.vs_nodata);
        listView = (PullToRefreshListView) view.findViewById(R.id.lv_list);
        listView.setEmptyView(emptyView);
        listView.setMode(PullToRefreshBase.Mode.BOTH);
        listView.setOnRefreshListener(this);

        screen1.setOnClickListener(click);
        screen2.setOnClickListener(click);
        btn_add.setVisibility(View.GONE);
        showLoading("");
        getData();
    }


    /**
     * 绑定数据
     */
    private void bindData() {

        if (null == adapter) {
            adapter = new CommCustomerAdapter(getActivity(),mCustomers,permission,mHandler);
            listView.setAdapter(adapter);
        } else {
            adapter.notifyDataSetChanged();
        }

        /**
         * 列表监听 进入客户详情页面
         * */
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Intent intent = new Intent();
                intent.putExtra("Id", mCustomers.get(position - 1).getId());
                intent.putExtra(ExtraAndResult.EXTRA_TYPE, CustomerManagerActivity.CUSTOMER_COMM);
                intent.setClass(mActivity, CustomerDetailInfoActivity_.class);
                startActivityForResult(intent, BaseMainListFragment.REQUEST_REVIEW);
                mActivity.overridePendingTransition(R.anim.enter_righttoleft, R.anim.exit_righttoleft);
            }
        });
    }


    /**
     * 获取数据,默认设置倒序
     */
    private void getData() {
        HashMap<String, Object> params = new HashMap<>();
        params.put("pageIndex", page);
        params.put("pageSize", 15);
        params.put("field", filed);
        params.put("order", order);
        params.put("tagItemIds", tagItemIds);
        params.put("deptId", departmentId);
        params.put("userId", userId);
        LogUtil.d("客户查询传递参数：" + MainApp.gson.toJson(params));
        RestAdapterFactory.getInstance().build(FinalVariables.QUERY_CUSTOMERS_PUBLIC).create(ICustomer.class).query(params, new RCallback<PaginationX<Customer>>() {
                    @Override
                    public void success(PaginationX<Customer> customerPaginationX, Response response) {
                        HttpErrorCheck.checkResponse("客户列表", response);
                        if (null == customerPaginationX || PaginationX.isEmpty(customerPaginationX)) {
                            if (!isPullUp) {
                                mPagination.setPageIndex(1);
                                mPagination.setPageSize(20);
                                mCustomers.clear();
                                bindData();
                            } else {
                                Toast("没有更多数据了");
                                listView.onRefreshComplete();
                                return;
                            }
                        } else {
                            mPagination = customerPaginationX;
                            if (!isPullUp) {
                                mCustomers.clear();
                            }
                            mCustomers.addAll(customerPaginationX.getRecords());
                            bindData();
                        }

                        listView.onRefreshComplete();
                        MainApp.getMainApp().isCutomerEdit = false;
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        HttpErrorCheck.checkError(error);
                        listView.onRefreshComplete();
                    }
                }
        );
    }




    /**
     * PopupWindow关闭 恢复背景正常颜色
     */
    private void closePopupWindow(ImageView view) {
        windowParams = getActivity().getWindow().getAttributes();
        windowParams.alpha = 1f;
        getActivity().getWindow().setAttributes(windowParams);
        view.setBackgroundResource(R.drawable.arrow_down);
    }

    /**
     * PopupWindow打开，背景变暗
     */
    private void openPopWindow(ImageView view) {
        windowParams = getActivity().getWindow().getAttributes();
        windowParams.alpha = 0.9f;
        getActivity().getWindow().setAttributes(windowParams);
        view.setBackgroundResource(R.drawable.arrow_up);
    }


   private View.OnClickListener click = new View.OnClickListener(){
       @Override
       public void onClick(View v) {
           switch (v.getId()) {

               //时间
               case R.id.cus_screen1:
                   saleCommPopupView = new SaleCommPopupView(getActivity(), mHandler, sortData, CustomerManagerActivity.CUSTOMER_TIME, true);
                   saleCommPopupView.showAsDropDown(screen1);
                   openPopWindow(tagImage1);
                   saleCommPopupView.setOnDismissListener(new PopupWindow.OnDismissListener() {
                       @Override
                       public void onDismiss() {
                           closePopupWindow(tagImage1);
                       }
                   });
                   break;

               //标签
               case R.id.cus_screen2:
                   screenTagPopupView = new ScreenTagPopupView(getActivity(),mTags,mHandler);
                   screenTagPopupView.showAsDropDown(screen2);
                   openPopWindow(tagImage2);
                   screenTagPopupView.setOnDismissListener(new PopupWindow.OnDismissListener() {
                       @Override
                       public void onDismiss() {
                           closePopupWindow(tagImage2);
                       }
                   });
                   break;
           }
       }
   };


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode){
            //客户详情操作回调
            case CustomerManagerActivity.CUSTOMER_COMM_RUSH:
                getData();
                break;
        }
    }
}
