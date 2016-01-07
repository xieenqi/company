package com.loyo.oa.v2.fragment;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.view.animation.LinearInterpolator;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activity.customer.CustomerAddActivity_;
import com.loyo.oa.v2.activity.customer.CustomerDetailInfoActivity_;
import com.loyo.oa.v2.activity.NearByCustomersActivity_;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.beans.Customer;
import com.loyo.oa.v2.beans.Department;
import com.loyo.oa.v2.beans.NearCount;
import com.loyo.oa.v2.beans.PaginationX;
import com.loyo.oa.v2.beans.Tag;
import com.loyo.oa.v2.beans.TagItem;
import com.loyo.oa.v2.beans.User;
import com.loyo.oa.v2.common.Common;
import com.loyo.oa.v2.common.FinalVariables;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.common.http.HttpErrorCheck;
import com.loyo.oa.v2.point.ICustomer;
import com.loyo.oa.v2.tool.BaseFragment;
import com.loyo.oa.v2.tool.BaseMainListFragment;
import com.loyo.oa.v2.tool.Config_project;
import com.loyo.oa.v2.tool.LocationUtil;
import com.loyo.oa.v2.tool.LogUtil;
import com.loyo.oa.v2.tool.RCallback;
import com.loyo.oa.v2.tool.RestAdapterFactory;
import com.loyo.oa.v2.tool.Utils;
import com.loyo.oa.v2.tool.ViewHolder;
import com.loyo.oa.v2.tool.customview.DropListMenu.DropItem;
import com.loyo.oa.v2.tool.customview.DropListMenu.DropListMenu;
import com.loyo.oa.v2.tool.customview.DropListMenu.OnDropItemSelectedListener;
import com.loyo.oa.v2.tool.customview.pullToRefresh.PullToRefreshBase;
import com.loyo.oa.v2.tool.customview.pullToRefresh.PullToRefreshListView;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * com.loyo.oa.v2.fragment
 * 描述 :客户列表通用界面
 * 作者 : ykb
 * 时间 : 15/9/21.
 */
public class CustomerCommonFragment extends BaseFragment implements View.OnClickListener, PullToRefreshBase.OnRefreshListener2, OnDropItemSelectedListener {

    private static final String[] TIMES_TAG = new String[]{"跟进时间 倒序", "跟进时间 顺序", "创建时间 倒序", "创建时间 顺序"};

    private View mView;
    private PullToRefreshListView listView;
    private Button btn_add;
    private TextView tv_near_customers;
    private ViewGroup layout_near_customers;
    private ViewStub emptyView;

    private ArrayList<Customer> mCustomers = new ArrayList<>();
    private int customer_type;//"0,我的客户", "1,团队客户", "2,公海客户"
    private CustomerCommonAdapter adapter;
    private PaginationX<Customer> mPagination = new PaginationX<>(20);
    private boolean isPullUp = false;
    private boolean isFrist = false;
    private String position;
    private NearCount nearCount;

    private DropListMenu mDropMenu;
    private ArrayList<DropItem> source = new ArrayList<>();
    private String filed = "";
    private String order = "";
    private String tagItemIds = "";
    private String departmentId = "";
    private String userId = "";
    private int page = 1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (null != getArguments()) {
            if (getArguments().containsKey("position")) {
                position = getArguments().getString("position");
            }
            if (getArguments().containsKey("type")) {
                customer_type = getArguments().getInt("type");
            }
            if (getArguments().containsKey("data")) {
                mCustomers = (ArrayList) getArguments().getSerializable("data");
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (null == mView) {
            mView = inflater.inflate(R.layout.fragment_customer_common, container, false);

            mDropMenu = (DropListMenu) mView.findViewById(R.id.droplist_menu);

            emptyView = (ViewStub) mView.findViewById(R.id.vs_nodata);

            listView = (PullToRefreshListView) mView.findViewById(R.id.listView_customers);
            listView.setEmptyView(emptyView);
            btn_add = (Button) mView.findViewById(R.id.btn_add);
            tv_near_customers = (TextView) mView.findViewById(R.id.tv_near_customers);
            layout_near_customers = (ViewGroup) mView.findViewById(R.id.layout_near_customers);

            //layout_near_customers.setOnTouchListener(Global.GetTouch());
            layout_near_customers.setOnClickListener(this);

            btn_add.setOnTouchListener(Global.GetTouch());
            btn_add.setOnClickListener(this);

            switch (customer_type) {
                case Customer.CUSTOMER_TYPE_MINE:
                case Customer.CUSTOMER_TYPE_TEAM:
                case Customer.CUSTOMER_TYPE_PUBLIC:
                    listView.setMode(PullToRefreshBase.Mode.BOTH);
                    listView.setOnRefreshListener(this);
                    mDropMenu.setVisibility(View.VISIBLE);
                    break;

                case Customer.CUSTOMER_TYPE_NEAR_MINE:
                case Customer.CUSTOMER_TYPE_NEAR_TEAM:
                case Customer.CUSTOMER_TYPE_NEAR_COMPANY:
                    listView.setMode(PullToRefreshBase.Mode.DISABLED);
                    break;
            }
        }
        order = "desc";
        filed = "lastActAt";

        initMenu();
        return mView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (customer_type != Customer.CUSTOMER_TYPE_MINE) {
            btn_add.setVisibility(View.GONE);
        }
        getData();
    }

    @Override
    public void onResume() {
        super.onResume();
        //客户信息有跟新 刷新列表
        if (MainApp.getMainApp().isCutomerEdit) {
            getData();
        }

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            //附近的客户
            case R.id.layout_near_customers:
                Bundle bundle = new Bundle();
                bundle.putString("position", position);
                bundle.putSerializable("nearCount", nearCount);
                bundle.putInt("type", customer_type);//团队与个人
                app.startActivity(mActivity, NearByCustomersActivity_.class, MainApp.ENTER_TYPE_RIGHT, false, bundle);
                break;
            case R.id.btn_add:
                Intent intent = new Intent();
                intent.setClass(mActivity, CustomerAddActivity_.class);
                startActivityForResult(intent, BaseMainListFragment.REQUEST_CREATE);
                break;
        }
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

    /**
     * 初始化时间筛选menu
     */
    void initMenu() {
        source.clear();
        isFrist = true;
        if (customer_type == Customer.CUSTOMER_TYPE_TEAM) {
            initOrganizationMenu();
        }
        //初始化时间筛选menu
        DropItem time = new DropItem("时间");
        time.setSelectType(DropItem.GROUP_SINGLE);
        for (int i = 0; i < TIMES_TAG.length; i++) {
            time.addSubDropItem(new DropItem(TIMES_TAG[i], i));
        }
        source.add(time);
        initTagsMenu();
    }

    /**
     * 初始化客户标签筛选menu
     */
    private void initTagsMenu() {
        RestAdapterFactory.getInstance().build(Config_project.API_URL_CUSTOMER()).create(ICustomer.class).GetTags(new RCallback<ArrayList<Tag>>() {
            @Override
            public void success(ArrayList<Tag> tags, Response response) {
                HttpErrorCheck.checkResponse(response);
                if (null == tags) {
                    return;
                }

                DropItem dropItemTag = new DropItem("标签");
                dropItemTag.setSelectType(DropItem.GROUP_SINGLE);
                int index = 0;
                for (Tag tag : tags) {
                    ArrayList<DropItem> dropItems = new ArrayList<>();
                    DropItem parentItem = new DropItem(tag.getName(), index++);
                    ArrayList<TagItem> items = tag.getItems();
                    int cIndex = 0;
                    for (TagItem item : items) {
                        DropItem dropItem = new DropItem(item.getName(), cIndex++, item.getId());
                        dropItems.add(dropItem);
                    }
                    parentItem.setSubDropItem(dropItems);
                    dropItemTag.addSubDropItem(parentItem);
                }
                source.add(dropItemTag);
                mDropMenu.setmMenuItems(source);
                mDropMenu.setMenuSelectedListener(CustomerCommonFragment.this);
            }
        });
    }

    /**
     * 初始化部门筛选menu
     */
    private void initOrganizationMenu() {
        ArrayList<Department> departments = Common.getLstDepartment();
        DropItem dropItemTag = new DropItem("部门");
        dropItemTag.setSelectType(DropItem.GROUP_SINGLE);
        for (int i = 0; i < departments.size(); i++) {
            ArrayList<User> users = Common.getUsersByDeptId(departments.get(i).getId(), new ArrayList<User>());
            ArrayList<DropItem> dropItems = new ArrayList<>();
            DropItem parentItem = new DropItem(departments.get(i).getName(), i, departments.get(i).getId());
            if (!users.isEmpty()) {
                for (int j = 0; j < users.size(); j++) {
                    DropItem dropItem = new DropItem(users.get(j).getRealname(), j, users.get(j).id);
                    dropItems.add(dropItem);
                }
            }
            parentItem.setSubDropItem(dropItems);
            dropItemTag.addSubDropItem(parentItem);
        }
        source.add(dropItemTag);
    }

    @Override
    public void onCancelAll(int ColumnIndex) {
        page = 1;
        switch (customer_type) {
            case Customer.CUSTOMER_TYPE_MINE:
            case Customer.CUSTOMER_TYPE_PUBLIC:
                if (ColumnIndex == 1) {
                    tagItemIds = "";
                }
                break;
            case Customer.CUSTOMER_TYPE_TEAM:
                //部门
                if (ColumnIndex == 0) {
                    departmentId = "";
                    userId = "";
                }
                //客户标签
                else if (ColumnIndex == 2) {
                    tagItemIds = "";
                }
                break;
        }
        getData();
    }

    /**
     * 【筛选 选择监听】
     *
     * @param listview
     * @param ColumnIndex
     * @param items
     */
    @Override
    public void onSelected(View listview, int ColumnIndex, SparseArray<DropItem> items) {
        page = 1;
        if (items != null && items.size() > 0) {
            switch (customer_type) {
                /**我的客户*/
                case Customer.CUSTOMER_TYPE_MINE:
                    //时间
                    if (ColumnIndex == 0) {
                        switch (items.get(items.keyAt(0)).getValue()) {
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
                    }
                    //客户标签
                    else if (ColumnIndex == 1) {
                        for (int i = 0; i < items.size(); i++) {
                            tagItemIds += items.get(items.keyAt(i)).getmData();
                            if (i != items.size() - 1) {
                                tagItemIds += ",";
                            }
                        }
                    }
                    break;


                /**公海客户*/
                case Customer.CUSTOMER_TYPE_PUBLIC:
                    //时间
                    if (ColumnIndex == 0) {
                        switch (items.get(items.keyAt(0)).getValue()) {
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
                    }
                    //客户标签
                    else if (ColumnIndex == 1) {
                        for (int i = 0; i < items.size(); i++) {
                            tagItemIds += items.get(items.keyAt(i)).getmData();
                            if (i != items.size() - 1) {
                                tagItemIds += ",";
                            }
                        }
                    }
                    break;

                /**团队客户*/
                case Customer.CUSTOMER_TYPE_TEAM:
                    //部门
                    if (ColumnIndex == 0) {
                        if (items.get(items.keyAt(0)).getValue() == 0) {
                            departmentId = items.get(items.keyAt(0)).getmData();
                        } else {
                            userId = items.get(items.keyAt(0)).getmData();
                        }
                    }
                    //时间
                    else if (ColumnIndex == 1) {
                        switch (items.get(items.keyAt(0)).getValue()) {
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
                    }
                    //客户标签
                    else {
                        for (int i = 0; i < items.size(); i++) {
                            tagItemIds += items.get(items.keyAt(i)).getmData();
                            if (i != items.size() - 1) {
                                tagItemIds += ",";
                            }
                        }
                    }
                    break;
            }
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < items.size(); i++) {
                sb.append(items.get(items.keyAt(i)).getName()).append(" ").append(items.get(items.keyAt(i)).getValue()).append(",");
            }
            getData();
            Toast("选择的内容AAA：" + sb.toString());
        }
    }

    /**
     * 获取客户数据集
     *
     * @return
     */
    public ArrayList<Customer> getmCustomers() {
        return mCustomers;
    }

    /**
     * http获取附近客户信息
     */
    private void getNearCustomersInfo() {
        new LocationUtil(mActivity, new LocationUtil.AfterLocation() {
            @Override
            public void OnLocationSucessed(String address, double longitude, double latitude, float radius) {
                String url = "";
                switch (customer_type) {
                    //团队客户
                    case Customer.CUSTOMER_TYPE_TEAM:
                        url = FinalVariables.QUERY_NEAR_CUSTOMERS_COUNT_TEAM;
                        break;
                    //个人客户
                    case Customer.CUSTOMER_TYPE_MINE:
                        url = FinalVariables.QUERY_NEAR_CUSTOMERS_COUNT_SELF;
                        break;
                }

                if (TextUtils.isEmpty(url)) {
                    return;
                }

                position = String.valueOf(longitude).concat(",").concat(String.valueOf(latitude));

                RestAdapterFactory.getInstance().build(url).create(ICustomer.class).queryNearCount(position, new RCallback<NearCount>() {
                    @Override
                    public void success(NearCount _nearCount, Response response) {
                        nearCount = _nearCount;
                        if (null != nearCount) {
                            tv_near_customers.setText("发现" + nearCount.total + "个附近客户");
                            showNearCustomersView();
                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        super.failure(error);
                        HttpErrorCheck.checkError(error);
                    }

                });
            }

            @Override
            public void OnLocationFailed() {
                Toast("获取附近客户信息失败！");
            }
        });
    }

    /**
     * 显示附近客户
     */
    private void showNearCustomersView() {
        layout_near_customers.setVisibility(View.VISIBLE);
        int oX = app.diptoPx(240);
        int nX = 0;
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(layout_near_customers, "translationX", oX, nX);
        objectAnimator.setInterpolator(new LinearInterpolator());
        objectAnimator.setDuration(500);
        objectAnimator.setTarget(layout_near_customers);
        objectAnimator.start();
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
        String url = "";
        switch (customer_type) {
            case Customer.CUSTOMER_TYPE_MINE:
                url = FinalVariables.QUERY_CUSTOMERS_SELF;
                break;
            case Customer.CUSTOMER_TYPE_PUBLIC:
                url = FinalVariables.QUERY_CUSTOMERS_PUBLIC;
                break;
            case Customer.CUSTOMER_TYPE_TEAM:
                url = FinalVariables.QUERY_CUSTOMERS_TEAM;
                break;
            case Customer.CUSTOMER_TYPE_NEAR_MINE:
                params.put("position", position);
                url = FinalVariables.QUERY_NEAR_CUSTOMERS_SELF;
                break;
            case Customer.CUSTOMER_TYPE_NEAR_TEAM:
                params.put("position", position);
                url = FinalVariables.QUERY_NEAR_CUSTOMERS_TEAM;
                break;
            case Customer.CUSTOMER_TYPE_NEAR_COMPANY:
                params.put("position", position);
                url = FinalVariables.QUERY_CUSTOMERS_COMPANY;
                break;
        }

        if (TextUtils.isEmpty(url)) {
            return;
        }

        RestAdapterFactory.getInstance().build(url).create(ICustomer.class).query(params, new RCallback<PaginationX<Customer>>() {
                    @Override
                    public void success(PaginationX<Customer> customerPaginationX, Response response) {
                        HttpErrorCheck.checkResponse(response);
                        if (null == customerPaginationX || PaginationX.isEmpty(customerPaginationX)) {
                            if (!isPullUp) {
                                mPagination.setPageIndex(1);
                                mPagination.setPageSize(20);
                                mCustomers.clear();
                                bindData();
                                Toast("没有数据");
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
                        if (!isPullUp && (customer_type == Customer.CUSTOMER_TYPE_MINE || customer_type == Customer.CUSTOMER_TYPE_TEAM)) {
                            getNearCustomersInfo();
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
        tagItemIds = "";
    }


    /**
     * 绑定数据
     */

    private void bindData() {
        if (null == adapter) {
            adapter = new CustomerCommonAdapter();
            listView.setAdapter(adapter);
        } else {
            adapter.notifyDataSetChanged();
        }

        /**
         * 列表监听 进入客户详情页面
         * */
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                LogUtil.d(" 客户管理每一个item： " + MainApp.gson.toJson(mCustomers.get((int) l)));
                Intent intent = new Intent();
                intent.putExtra("Id", mCustomers.get((int) l).getId());

                intent.setClass(mActivity, CustomerDetailInfoActivity_.class);
                startActivityForResult(intent, BaseMainListFragment.REQUEST_REVIEW);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        /*详情中有"投入公海"和"公海挑入","删除"操作，返回该页面时，则刷新当前客户列表，没有则不刷新*/
        if (requestCode == BaseMainListFragment.REQUEST_REVIEW && resultCode == Activity.RESULT_OK) {
            getData();
            LogUtil.dll("投入公海，公海挑入，刷新");
        }

        if (resultCode != Activity.RESULT_OK || data == null || data.getExtras() == null || data.getExtras().size() == 0) {
            return;
        }
        Customer customer = (Customer) data.getSerializableExtra(Customer.class.getName());

        if (customer == null) {
            return;
        }
        switch (requestCode) {
            case BaseMainListFragment.REQUEST_CREATE:
                mCustomers.add(0, customer);
                break;
            case BaseMainListFragment.REQUEST_REVIEW:
                for (int i = 0; i < mCustomers.size(); i++) {
                    if (TextUtils.equals(mCustomers.get(i).getId(), customer.getId())) {
                        mCustomers.set(i, customer);
                        break;
                    }
                }
                break;
        }

        bindData();
    }

    private class CustomerCommonAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mCustomers.size();
        }

        @Override
        public Object getItem(int i) {
            return mCustomers.isEmpty() ? null : mCustomers.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View convertView, ViewGroup parent) {

            if (convertView == null) {
                convertView = LayoutInflater.from(mActivity).inflate(R.layout.item_common_customer, null, false);
            }

            TextView tv_title = ViewHolder.get(convertView, R.id.tv_title);
            TextView tv_content1 = ViewHolder.get(convertView, R.id.tv_content1);
            TextView tv_content2 = ViewHolder.get(convertView, R.id.tv_content2);
            TextView tv_content3 = ViewHolder.get(convertView, R.id.tv_content3);
            TextView tv_content4 = ViewHolder.get(convertView, R.id.tv_distance);

            ImageView img1 = ViewHolder.get(convertView, R.id.img_1);
            ImageView img2 = ViewHolder.get(convertView, R.id.img_2);
            ImageView img3 = ViewHolder.get(convertView, R.id.img_3);
            ImageView img_public = ViewHolder.get(convertView, R.id.img_public);
            ImageView img_go_where = ViewHolder.get(convertView, R.id.img_go_where);


            ViewGroup layout1 = ViewHolder.get(convertView, R.id.layout_1);
            ViewGroup layout2 = ViewHolder.get(convertView, R.id.layout_2);
            ViewGroup layout3 = ViewHolder.get(convertView, R.id.layout_3);
            ViewGroup layout_go_where = ViewHolder.get(convertView, R.id.layout_go_where);

            final Customer customer = mCustomers.get(i);

            tv_title.setText(customer.name);

            String tagItems = Utils.getTagItems(customer);
            String lastActivityAt = app.df3.format(new Date(customer.lastActAt * 1000));
            //我的客户
            if (customer_type == Customer.CUSTOMER_TYPE_MINE) {
                img_public.setVisibility(View.GONE);
                layout_go_where.setVisibility(View.GONE);
                layout3.setVisibility(View.GONE);

                layout2.setVisibility(View.VISIBLE);
                img1.setImageResource(R.drawable.icon_customer_tag);
                img2.setImageResource(R.drawable.icon_customer_follow_time);

                tv_content1.setText("标签：" + tagItems);
                tv_content2.setText("跟进时间：" + lastActivityAt);

            }
            //团队
            else if (customer_type == Customer.CUSTOMER_TYPE_TEAM) {
                img_public.setVisibility(View.GONE);
                layout_go_where.setVisibility(View.GONE);
                layout2.setVisibility(View.VISIBLE);
                layout3.setVisibility(View.VISIBLE);

                img1.setImageResource(R.drawable.icon_customer_tag);
                img2.setImageResource(R.drawable.icon_follow_up_creator);
                img3.setImageResource(R.drawable.icon_customer_follow_time);

                String responser = null == customer.owner || null == customer.owner ? "" : customer.owner.name;
                tv_content1.setText("标签：" + tagItems);
                tv_content2.setText("负责人：" + responser);
                tv_content3.setText("跟进时间：" + lastActivityAt);
            }
            //公海
            else if (customer_type == Customer.CUSTOMER_TYPE_PUBLIC) {
                img_public.setVisibility(View.VISIBLE);
                layout_go_where.setVisibility(View.GONE);
                layout2.setVisibility(View.VISIBLE);
                layout3.setVisibility(View.GONE);

                img1.setImageResource(R.drawable.icon_customer_tag);
                img2.setImageResource(R.drawable.icon_customer_follow_time);

                tv_content1.setText("标签：" + tagItems);
                tv_content2.setText("跟进时间：" + lastActivityAt);

            }
            //附近-个人
            else if (customer_type == Customer.CUSTOMER_TYPE_NEAR_MINE) {
                layout_go_where.setVisibility(View.VISIBLE);
                img_public.setVisibility(View.GONE);
                layout2.setVisibility(View.GONE);
                layout3.setVisibility(View.GONE);

                img1.setImageResource(R.drawable.icon_customer_tag);

                tv_content1.setText("标签：" + tagItems);
                tv_content4.setText("距离：" + customer.distance);
            }
            //附近-团队
            else if (customer_type == Customer.CUSTOMER_TYPE_NEAR_TEAM) {
                layout_go_where.setVisibility(View.VISIBLE);
                img_public.setVisibility(View.GONE);
                layout2.setVisibility(View.GONE);
                layout3.setVisibility(View.GONE);

                img1.setImageResource(R.drawable.icon_customer_tag);

                tv_content1.setText("标签：" + tagItems);
                tv_content4.setText("距离：" + customer.distance);
            }

            //附近-公司已赢单
            if (customer_type == Customer.CUSTOMER_TYPE_NEAR_COMPANY) {
                layout_go_where.setVisibility(View.VISIBLE);
                img_public.setVisibility(View.GONE);
                layout2.setVisibility(View.GONE);
                layout3.setVisibility(View.GONE);

                img1.setImageResource(R.drawable.img_sign_list_position);
                img2.setImageResource(R.drawable.icon_customer_demands_plan);

                tv_content1.setText("地址：" + customer.loc.addr);
                //                tv_content2.setText("购买产品：");
                tv_content4.setText("距离：" + customer.distance);
            }

            img_public.setOnTouchListener(Global.GetTouch());
            img_public.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    RestAdapterFactory.getInstance().build(Config_project.API_URL_CUSTOMER()).create(ICustomer.class).pickedIn(customer.getId(), new RCallback<Customer>() {
                        @Override
                        public void success(Customer customer, Response response) {
                            getData();
                        }
                    });
                }
            });

            img_go_where.setOnTouchListener(Global.GetTouch());
            img_go_where.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Utils.goWhere(mActivity, customer.loc.loc[1], customer.loc.loc[0]);
                }
            });

            if (i == mCustomers.size() - 1) {
                convertView.setBackgroundResource(R.drawable.item_bg_buttom);
            } else {
                convertView.setBackgroundColor(getResources().getColor(R.color.white));
            }
            return convertView;
        }
    }
}
