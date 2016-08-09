package com.loyo.oa.v2.activityui.order.fragment;


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
import com.loyo.oa.v2.activityui.order.OrderAddActivity;
import com.loyo.oa.v2.activityui.order.OrderDetailActivity;
import com.loyo.oa.v2.activityui.order.adapter.MyOrderAdapter;
import com.loyo.oa.v2.activityui.order.bean.OrderList;
import com.loyo.oa.v2.activityui.order.bean.OrderListItem;
import com.loyo.oa.v2.activityui.sale.SaleOpportunitiesManagerActivity;
import com.loyo.oa.v2.activityui.sale.bean.SaleTeamScreen;
import com.loyo.oa.v2.activityui.sale.fragment.TeamSaleFragment;
import com.loyo.oa.v2.common.ExtraAndResult;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.common.http.HttpErrorCheck;
import com.loyo.oa.v2.customview.SaleCommPopupView;
import com.loyo.oa.v2.customview.pullToRefresh.PullToRefreshBase;
import com.loyo.oa.v2.customview.pullToRefresh.PullToRefreshListView;
import com.loyo.oa.v2.point.IOrder;
import com.loyo.oa.v2.tool.BaseFragment;
import com.loyo.oa.v2.tool.Config_project;
import com.loyo.oa.v2.tool.RestAdapterFactory;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * 【我的订单】
 * Created by xeq on 16/8/1.
 */
public class MyOrderFragment extends BaseFragment implements View.OnClickListener, PullToRefreshBase.OnRefreshListener2 {

    private Button btn_add;
    private String[] status = {"全部状态", "待审核", "未通过", "进行中", "已完成", "意外终止"};
    private String[] sort = {"按照创建时间", "按照最高金额"};
    private LinearLayout salemy_screen1, salemy_screen2;
    private ImageView salemy_screen1_iv1, salemy_screen1_iv2;
    private WindowManager.LayoutParams windowParams;
    private int statusIndex, sortIndex;
    private ArrayList<SaleTeamScreen> sortData = new ArrayList<>();
    private ArrayList<SaleTeamScreen> statusData = new ArrayList<>();
    private ViewStub emptyView;
    private PullToRefreshListView lv_list;
    private MyOrderAdapter adapter;
    private int page = 1;
    private boolean isPullDown = true;
    private Intent mIntent;
    private Bundle mBundle;

    private List<OrderListItem> listData = new ArrayList<>();

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case TeamSaleFragment.SALETEAM_SCREEN_TAG2:
                    isPullDown = true;
                    statusIndex = (int) msg.getData().get("index");
                    break;

                case TeamSaleFragment.SALETEAM_SCREEN_TAG3:
                    isPullDown = true;
                    sortIndex = (int) msg.getData().get("index");
                    break;
            }
            getData();
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View mView = null;
        if (null == mView) {
            mView = inflater.inflate(R.layout.fragment_my_order, null);
            initView(mView);
        }
        return mView;
    }

    private void initView(View view) {
        setFilterData();
        btn_add = (Button) view.findViewById(R.id.btn_add);
        btn_add.setOnTouchListener(Global.GetTouch());
        btn_add.setOnClickListener(this);
        salemy_screen1 = (LinearLayout) view.findViewById(R.id.salemy_screen1);
        salemy_screen2 = (LinearLayout) view.findViewById(R.id.salemy_screen2);
        salemy_screen1.setOnClickListener(this);
        salemy_screen2.setOnClickListener(this);
        salemy_screen1_iv1 = (ImageView) view.findViewById(R.id.salemy_screen1_iv1);
        salemy_screen1_iv2 = (ImageView) view.findViewById(R.id.salemy_screen1_iv2);
        emptyView = (ViewStub) view.findViewById(R.id.vs_nodata);
        lv_list = (PullToRefreshListView) view.findViewById(R.id.lv_list);
        lv_list.setMode(PullToRefreshBase.Mode.BOTH);
        lv_list.setOnRefreshListener(this);
        lv_list.setEmptyView(emptyView);
        lv_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent mIntent = new Intent();
//              mIntent.putExtra(ExtraAndResult.IS_TEAM, false);
                mIntent.putExtra(ExtraAndResult.EXTRA_ID, adapter.getItemData(position - 1).id);
                mIntent.setClass(getActivity(), OrderDetailActivity.class);
                startActivityForResult(mIntent, getActivity().RESULT_FIRST_USER);
                getActivity().overridePendingTransition(R.anim.enter_righttoleft, R.anim.exit_righttoleft);
            }
        });
        adapter = new MyOrderAdapter(app);
        lv_list.setAdapter(adapter);
        getData();
    }

    private void setFilterData() {
        for (int i = 0; i < sort.length; i++) {
            SaleTeamScreen saleTeamScreen = new SaleTeamScreen();
            saleTeamScreen.setName(sort[i]);
            sortData.add(saleTeamScreen);
        }
        for (int i = 0; i < status.length; i++) {
            SaleTeamScreen saleTeamScreen = new SaleTeamScreen();
            saleTeamScreen.setName(status[i]);
            statusData.add(saleTeamScreen);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            //新建
            case R.id.btn_add:
                mBundle = new Bundle();
                mBundle.putInt("fromPage",OrderDetailActivity.ORDER_ADD);
                startActivityForResult(new Intent(getActivity(), OrderAddActivity.class), getActivity().RESULT_FIRST_USER);
                getActivity().overridePendingTransition(R.anim.enter_righttoleft, R.anim.exit_righttoleft);
                break;

            //状态选择
            case R.id.salemy_screen1:
                SaleCommPopupView saleCommPopupView = new SaleCommPopupView(getActivity(), mHandler, statusData,
                        SaleOpportunitiesManagerActivity.SCREEN_STAGE, true, statusIndex);
                saleCommPopupView.showAsDropDown(salemy_screen1);
                openPopWindow(salemy_screen1_iv1);
                saleCommPopupView.setOnDismissListener(new PopupWindow.OnDismissListener() {
                    @Override
                    public void onDismiss() {
                        closePopupWindow(salemy_screen1_iv1);
                    }
                });
                break;

            //排序
            case R.id.salemy_screen2:
                saleCommPopupView = new SaleCommPopupView(getActivity(), mHandler, sortData,
                        SaleOpportunitiesManagerActivity.SCREEN_SORT, false, sortIndex);
                saleCommPopupView.showAsDropDown(salemy_screen2);
                openPopWindow(salemy_screen1_iv2);
                saleCommPopupView.setOnDismissListener(new PopupWindow.OnDismissListener() {
                    @Override
                    public void onDismiss() {
                        closePopupWindow(salemy_screen1_iv2);
                    }
                });
                break;
        }
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

    private void getData() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("pageIndex", page);
        map.put("pageSize", 15);
        map.put("status", statusIndex);
        map.put("filed", sortIndex == 1 ? "dealMoney" : "createdAt");
        RestAdapterFactory.getInstance().build(Config_project.API_URL_CUSTOMER()).
                create(IOrder.class).getOrderMyList(map, new Callback<OrderList>() {
            @Override
            public void success(OrderList orderlist, Response response) {
                lv_list.onRefreshComplete();
                HttpErrorCheck.checkResponse("我的订单列表：", response);
                if (!isPullDown) {
                    listData.addAll(orderlist.records);
                } else {
                    listData = orderlist.records;
                }
                adapter.setData(listData);
            }

            @Override
            public void failure(RetrofitError error) {
                lv_list.onRefreshComplete();
                HttpErrorCheck.checkError(error);
            }
        });
    }

    @Override
    public void onPullDownToRefresh(PullToRefreshBase refreshView) {
        isPullDown = true;
        page = 1;
        getData();
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase refreshView) {
        isPullDown = false;
        page++;
        getData();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (resultCode){

            //新建订单回调
            case ExtraAndResult.REQUEST_CODE:
                isPullDown = true;
                getData();
                break;

        }
    }
}
