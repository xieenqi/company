package com.loyo.oa.v2.activityui.dashboard.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.dashboard.DashboardDetailActivity;
import com.loyo.oa.v2.activityui.dashboard.adapter.StockListAdapter;
import com.loyo.oa.v2.activityui.dashboard.common.DashborardType;
import com.loyo.oa.v2.activityui.dashboard.presenter.HomeDashboardPresenter;
import com.loyo.oa.v2.activityui.dashboard.presenter.impl.HomeDashboardPresenterImpl;
import com.loyo.oa.v2.activityui.dashboard.viewcontrol.HomeDashBoardView;
import com.loyo.oa.v2.activityui.worksheet.WorksheetAddActivity;
import com.loyo.oa.v2.activityui.worksheet.bean.WorksheetTemplate;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.customview.CustomerListView;
import com.loyo.oa.v2.permission.BusinessOperation;
import com.loyo.oa.v2.customview.PaymentPopView;
import com.loyo.oa.v2.tool.BaseFragment;
import me.itangqi.waveloadingview.WaveLoadingView;

/**
 * 【仪表盘】
 * Created by yyy on 16/12/9.
 */

public class HomeDashboardFragment extends BaseFragment implements View.OnClickListener,HomeDashBoardView {

    private View mView;
    private RadioButton rb_customer, rb_clue;
    private LinearLayout ll_dashboard_followup, ll_dashboard_signin, ll_dashboard_record,ll_dashboard_order_number,
            ll_dashboard_order_money;
    private LinearLayout ll_case1, ll_case2, ll_case3;
    private CustomerListView lv_stocklist;
    private StockListAdapter mAdapter;

    private HomeDashboardPresenter mPresenter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (null == mView) {
            mView = inflater.inflate(R.layout.fragment_dashboard, container, false);
        }
        initUI();
        return mView;
    }

    private void bindAdapter() {
        if (null == mAdapter) {
            mAdapter = new StockListAdapter(getActivity());
            lv_stocklist.setAdapter(mAdapter);
        } else {
            mAdapter.notifyDataSetChanged();
        }
        lv_stocklist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                DashborardType.COMMON.setTttle("增量 存量");
                DashborardType.COMMON.setPermission(BusinessOperation.AUGMENTER_STOCK);
                Bundle bundle = new Bundle();
                bundle.putSerializable("type", DashborardType.COMMON);
                app.startActivity(mActivity, DashboardDetailActivity.class, MainApp.ENTER_TYPE_RIGHT, false, bundle);
            }
        });
    }

    private void initUI() {

        mPresenter = new HomeDashboardPresenterImpl(mActivity,this);
        rb_customer = (RadioButton) mView.findViewById(R.id.rb_customer);
        rb_clue = (RadioButton) mView.findViewById(R.id.rb_clue);
        lv_stocklist = (CustomerListView) mView.findViewById(R.id.lv_stocklist);
        ll_dashboard_followup = (LinearLayout) mView.findViewById(R.id.ll_dashboard_followup);
        ll_dashboard_signin = (LinearLayout) mView.findViewById(R.id.ll_dashboard_signin);
        ll_dashboard_record = (LinearLayout) mView.findViewById(R.id.ll_dashboard_record);
        ll_dashboard_order_number= (LinearLayout) mView.findViewById(R.id.ll_dashboard_order_number);
        ll_dashboard_order_money= (LinearLayout) mView.findViewById(R.id.ll_dashboard_order_money);
        ll_case1 = (LinearLayout) mView.findViewById(R.id.ll_case1);
        ll_case2 = (LinearLayout) mView.findViewById(R.id.ll_case2);
        ll_case3 = (LinearLayout) mView.findViewById(R.id.ll_case3);

        rb_customer.setOnClickListener(this);
        rb_clue.setOnClickListener(this);
        ll_case1.setOnClickListener(this);
        ll_case2.setOnClickListener(this);
        ll_case3.setOnClickListener(this);

        ll_case1.setOnTouchListener(Global.GetTouch());
        ll_case2.setOnTouchListener(Global.GetTouch());
        ll_case3.setOnTouchListener(Global.GetTouch());

        mPresenter.initUi((WaveLoadingView) mView.findViewById(R.id.waveLoadingView1),
                (WaveLoadingView) mView.findViewById(R.id.waveLoadingView2));

        ll_dashboard_followup.setOnClickListener(this);
        ll_dashboard_signin.setOnClickListener(this);
        ll_dashboard_record.setOnClickListener(this);
        ll_dashboard_order_number.setOnClickListener(this);
        ll_dashboard_order_money.setOnClickListener(this);
        Global.SetTouchView(ll_dashboard_followup, ll_dashboard_signin,ll_dashboard_record,ll_dashboard_order_number,
                ll_dashboard_order_money);

        bindAdapter();
    }


    @Override
    public void onClick(View view) {

        String[] list = null;
        switch (view.getId()){


            /*客户跟进*/
            case R.id.rb_customer:
                Toast("1");
                break;

            /*线索跟进*/
            case R.id.rb_clue:
                Toast("2");
                break;

            /*客户线索筛选*/
            case R.id.ll_case1:
                list = new String[]{"今天", "昨天", "本周", "上周", "本月", "上月", "取消"};
                mPresenter.screenControlView(list,"选择时间");
                break;

            /*增量存量筛选*/
            case R.id.ll_case2:
                list = new String[]{"今天", "昨天", "本周", "上周", "本月", "上月", "取消"};
                mPresenter.screenControlView(list,"选择时间");
                break;

            /*数量金额筛选*/
            case R.id.ll_case3:
                list = new String[]{"本月", "上月", "本季度", "上季度", "本年", "去年", "取消"};
                mPresenter.screenControlView(list,"选择时间");
                break;

            case R.id.ll_dashboard_followup:
                Bundle bdFollowup = new Bundle();
                bdFollowup.putSerializable("type", DashborardType.CUS_FOLLOWUP);
                app.startActivity(mActivity, DashboardDetailActivity.class, MainApp.ENTER_TYPE_RIGHT, false, bdFollowup);
                break;
            case R.id.ll_dashboard_signin:
                Bundle bdSignin = new Bundle();
                bdSignin.putSerializable("type", DashborardType.CUS_SIGNIN);
                app.startActivity(mActivity, DashboardDetailActivity.class, MainApp.ENTER_TYPE_RIGHT, false, bdSignin);
                break;
            case R.id.ll_dashboard_record:
                Bundle bdRecord = new Bundle();
                bdRecord.putSerializable("type", DashborardType.CUS_CELL_RECORD);
                app.startActivity(mActivity, DashboardDetailActivity.class, MainApp.ENTER_TYPE_RIGHT, false, bdRecord);
                break;
            case R.id.ll_dashboard_order_number:
                Bundle bdOrderNumber = new Bundle();
                bdOrderNumber.putSerializable("type", DashborardType.ORDER_NUMBER);
                app.startActivity(mActivity, DashboardDetailActivity.class, MainApp.ENTER_TYPE_RIGHT, false, bdOrderNumber);
                break;
            case R.id.ll_dashboard_order_money:
                Bundle bdOrderMoney = new Bundle();
                bdOrderMoney.putSerializable("type", DashborardType.ORDER_MONEY);
                app.startActivity(mActivity, DashboardDetailActivity.class, MainApp.ENTER_TYPE_RIGHT, false, bdOrderMoney);
                break;

        }
    }

    @Override
    public void setScreenVal(String val) {
        Toast(val);
    }

    @Override
    public void showStatusProgress() {

    }

    @Override
    public void showProgress(String message) {

    }

    @Override
    public void hideProgress() {

    }

    @Override
    public void showMsg(String message) {

    }
}
