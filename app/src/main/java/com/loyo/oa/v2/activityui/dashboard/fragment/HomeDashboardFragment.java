package com.loyo.oa.v2.activityui.dashboard.fragment;

import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.loyo.oa.hud.progress.LoyoProgressHUD;
import com.loyo.oa.hud.toast.LoyoToast;
import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.dashboard.DashboardDetailActivity;
import com.loyo.oa.v2.activityui.dashboard.adapter.StockListAdapter;
import com.loyo.oa.v2.activityui.dashboard.common.DashborardType;
import com.loyo.oa.v2.activityui.dashboard.common.LoadStatus;
import com.loyo.oa.v2.activityui.dashboard.common.ScreenType;
import com.loyo.oa.v2.activityui.dashboard.model.FollowupStatistic;
import com.loyo.oa.v2.activityui.dashboard.model.MoneyStatistic;
import com.loyo.oa.v2.activityui.dashboard.model.StockStatistic;
import com.loyo.oa.v2.activityui.dashboard.presenter.HomeDashboardPresenter;
import com.loyo.oa.v2.activityui.dashboard.presenter.impl.HomeDashboardPresenterImpl;
import com.loyo.oa.v2.activityui.dashboard.viewcontrol.HomeDashBoardView;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.customview.CustomerListView;
import com.loyo.oa.v2.permission.BusinessOperation;
import com.loyo.oa.v2.permission.PermissionManager;
import com.loyo.oa.v2.tool.BaseFragment;
import com.loyo.oa.v2.tool.LogUtil;

import java.text.DecimalFormat;
import java.util.ArrayList;

import me.itangqi.waveloadingview.WaveLoadingView;

/**
 * 【仪表盘】
 * Created by yyy on 16/12/9.
 */

public class HomeDashboardFragment extends BaseFragment implements View.OnClickListener, HomeDashBoardView {

    private final int NULLNUM = 3;

    private View mView;
    private RadioButton rb_customer, rb_clue;
    private RelativeLayout loading_view1, loading_view2, loading_view3;
    private LinearLayout loading_error1, loading_error2, loading_error3;
    private TextView tv_click_rest1, tv_click_rest2, tv_click_rest3;
    private TextView tv_screen_title1,tv_screen_title2,tv_screen_title3;
    private ImageView loading_load1, loading_load2, loading_load3;
    private LinearLayout ll_case1, ll_case2, ll_case3;
    private CustomerListView lv_stocklist;

    private LinearLayout ll_dashboard_followup, ll_dashboard_signin,
            ll_dashboard_record, ll_dashboard_order_number,
            ll_dashboard_order_money, ll_followup, ll_stock,ll_money;

    private TextView fw_totalsize, fw_count,
            visit_totalsize, visit_count,
            voice_totalsize, voice_count;

    private TextView tv_target_count,tv_order_count,  /*  目标数量 订单数量  */
                     tv_target_money,tv_order_money;  /*  目标金额 订单金额  */

    private StockListAdapter mAdapter;
    private HomeDashboardPresenter mPresenter;
    private AnimationDrawable loadAnim1, loadAnim2,loadAnim3;

    private FollowupStatistic csclueFowUp;

    private int followUpType = 1;  //跟进 默认今天
    private int stockType    = 1;  //增量存量 默认今天
    private int moneyCnType  = 5;  //数量金额 默认本月
    private int followUpPage = 0;  //0:客户 1:线索


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

    /**
     * 绑定增量存量Adapter
     */
    private void bindClAdapter(final ArrayList<StockStatistic> stockListModel) {
        if (null == mAdapter) {
            mAdapter = new StockListAdapter(getActivity(), stockListModel);
            lv_stocklist.setAdapter(mAdapter);
        } else {
            mAdapter.notifyDataSetChanged();
        }
        lv_stocklist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                DashborardType.COMMON.setTttle("增量 存量");
                DashborardType.COMMON.setPermission(BusinessOperation.AUGMENTER_STOCK);
                if (!checkPermission(DashborardType.COMMON)) {
                    return;
                }
                DashborardType.COMMON.setTttle(stockListModel.get(position).tagItemName);
                Bundle bundle = new Bundle();
                bundle.putSerializable("type", DashborardType.COMMON);
                bundle.putSerializable("tagItemId",stockListModel.get(position).tagItemId);
                app.startActivity(mActivity, DashboardDetailActivity.class, MainApp.ENTER_TYPE_RIGHT, false, bundle);
            }
        });
    }

    /**
     * 绑定跟进数据
     */
    private void bindFowUpData() {
        if (followUpPage == 0) {
            fw_totalsize.setText(csclueFowUp.saleActivity.activity.totalCount + "");
            fw_count.setText(csclueFowUp.saleActivity.activity.distinctCount + "");
            visit_totalsize.setText(csclueFowUp.saleActivity.visit.totalCount + "");
            visit_count.setText(csclueFowUp.saleActivity.visit.distinctCount + "");
            voice_totalsize.setText(csclueFowUp.saleActivity.voice.totalCount + "");
            voice_count.setText(csclueFowUp.saleActivity.voice.distinctCount + "");
        } else {
            fw_totalsize.setText(csclueFowUp.salesLead.activity.totalCount + "");
            fw_count.setText(csclueFowUp.salesLead.activity.distinctCount + "");
            visit_totalsize.setText(csclueFowUp.salesLead.voice.totalCount + "");
            visit_count.setText(csclueFowUp.salesLead.voice.distinctCount + "");
        }
    }

    /**
     * 绑定数量金额数据
     * */
    private void bindMoneyCountData(MoneyStatistic mcModel){

        DecimalFormat df = new DecimalFormat("0.00");

        long targetAmount = mcModel.targetAmount; /*  目标数量  */
        long totalAmount  = mcModel.totalAmount;  /*  实际数量  */

        long targetMoney = mcModel.targetNumber;  /*  目标金额  */
        long totalMoney  = mcModel.totalNumber;   /*  实际金额  */

        int mvNumValues = 100; /* 数量涨幅值 */
        int mvMonValues = 100; /* 金额涨幅值 */

        String mvNumShow = "100%"; /* 数量涨幅百分比 */
        String mvMonShow = "100%"; /* 金额涨幅百分比 */

        if(targetAmount != 0 && targetAmount > totalAmount){
            mvNumValues =  (int) Math.floor((double)totalAmount/targetAmount); //取整
            mvNumShow = df.format(((double)totalAmount/targetAmount * 100))+"%";
        }

        if(targetMoney != 0 && targetMoney > totalMoney){
            mvMonValues =  (int) Math.floor((double)totalMoney/targetMoney); //取整
            mvMonShow = df.format(((double)totalMoney/targetMoney * 100))+"%";
        }

        LogUtil.dee("数量百分比值:"+ mvNumValues);
        LogUtil.dee("数量百分比(两位小数):"+ mvNumShow);

        LogUtil.dee("金额百分比值:"+ mvMonValues);
        LogUtil.dee("金额百分比(两位小数):"+ mvMonShow);

        mPresenter.initWave((WaveLoadingView) mView.findViewById(R.id.waveLoadingView1),
                            (WaveLoadingView) mView.findViewById(R.id.waveLoadingView2),
                             mvNumValues,mvMonValues,mvNumShow,mvMonShow);
        tv_target_count.setText(targetAmount+"");
        tv_order_count.setText(totalAmount+"");
        tv_target_money.setText("¥ " + targetMoney);
        tv_order_money.setText("¥ " + totalMoney);
    }

    private void initUI() {

        mPresenter = new HomeDashboardPresenterImpl(mActivity, this);
        rb_customer = (RadioButton) mView.findViewById(R.id.rb_customer);
        rb_clue = (RadioButton) mView.findViewById(R.id.rb_clue);
        lv_stocklist = (CustomerListView) mView.findViewById(R.id.lv_stocklist);
        ll_dashboard_followup = (LinearLayout) mView.findViewById(R.id.ll_dashboard_followup);
        ll_dashboard_signin = (LinearLayout) mView.findViewById(R.id.ll_dashboard_signin);
        ll_dashboard_record = (LinearLayout) mView.findViewById(R.id.ll_dashboard_record);
        ll_dashboard_order_number = (LinearLayout) mView.findViewById(R.id.ll_dashboard_order_number);
        ll_dashboard_order_money = (LinearLayout) mView.findViewById(R.id.ll_dashboard_order_money);
        ll_case1 = (LinearLayout) mView.findViewById(R.id.ll_case1);
        ll_case2 = (LinearLayout) mView.findViewById(R.id.ll_case2);
        ll_case3 = (LinearLayout) mView.findViewById(R.id.ll_case3);
        ll_followup = (LinearLayout) mView.findViewById(R.id.ll_followup);
        ll_stock = (LinearLayout) mView.findViewById(R.id.ll_stock);
        ll_money = (LinearLayout) mView.findViewById(R.id.ll_money);

        loading_view1 = (RelativeLayout) mView.findViewById(R.id.loading_view1);
        loading_view2 = (RelativeLayout) mView.findViewById(R.id.loading_view2);
        loading_view3 = (RelativeLayout) mView.findViewById(R.id.loading_view3);

        loading_error1 = (LinearLayout) mView.findViewById(R.id.loading_error1);
        loading_error2 = (LinearLayout) mView.findViewById(R.id.loading_error2);
        loading_error3 = (LinearLayout) mView.findViewById(R.id.loading_error3);

        tv_click_rest1 = (TextView) mView.findViewById(R.id.tv_click_rest1);
        tv_click_rest2 = (TextView) mView.findViewById(R.id.tv_click_rest2);
        tv_click_rest3 = (TextView) mView.findViewById(R.id.tv_click_rest3);

        tv_screen_title1 = (TextView) mView.findViewById(R.id.tv_screen_title1);
        tv_screen_title2 = (TextView) mView.findViewById(R.id.tv_screen_title2);
        tv_screen_title3 = (TextView) mView.findViewById(R.id.tv_screen_title3);

        loading_load1 = (ImageView) mView.findViewById(R.id.loading_load1);
        loading_load2 = (ImageView) mView.findViewById(R.id.loading_load2);
        loading_load3 = (ImageView) mView.findViewById(R.id.loading_load3);

        loadAnim1 = (AnimationDrawable) mView.findViewById(R.id.loading_load1).getBackground();
        loadAnim2 = (AnimationDrawable) mView.findViewById(R.id.loading_load2).getBackground();
        loadAnim3 = (AnimationDrawable) mView.findViewById(R.id.loading_load3).getBackground();

        fw_totalsize = (TextView) mView.findViewById(R.id.fw_totalsize);
        visit_totalsize = (TextView) mView.findViewById(R.id.visit_totalsize);
        voice_totalsize = (TextView) mView.findViewById(R.id.voice_totalsize);

        tv_target_count = (TextView) mView.findViewById(R.id.tv_target_count);
        tv_order_count = (TextView) mView.findViewById(R.id.tv_order_count);
        tv_target_money = (TextView) mView.findViewById(R.id.tv_target_money);
        tv_order_money = (TextView) mView.findViewById(R.id.tv_order_money);

        voice_count = (TextView) mView.findViewById(R.id.voice_count);
        visit_count = (TextView) mView.findViewById(R.id.visit_count);
        fw_count = (TextView) mView.findViewById(R.id.fw_count);

        rb_customer.setOnClickListener(this);
        rb_clue.setOnClickListener(this);
        ll_case1.setOnClickListener(this);
        ll_case2.setOnClickListener(this);
        ll_case3.setOnClickListener(this);
        tv_click_rest1.setOnClickListener(this);
        tv_click_rest2.setOnClickListener(this);
        tv_click_rest3.setOnClickListener(this);
        ll_dashboard_followup.setOnClickListener(this);
        ll_dashboard_signin.setOnClickListener(this);
        ll_dashboard_record.setOnClickListener(this);
        ll_dashboard_order_number.setOnClickListener(this);
        ll_dashboard_order_money.setOnClickListener(this);

        Global.SetTouchView(ll_dashboard_followup, ll_dashboard_signin, ll_dashboard_record, ll_dashboard_order_number,
                ll_dashboard_order_money, ll_case1, ll_case2, ll_case3, tv_click_rest1);
        getFollowUpData(followUpType);
        getStockData(stockType);
        getMoneyCount(moneyCnType);
    }

    /**
     * 获取跟进数据
     */
    private void getFollowUpData(int type) {
        mPresenter.setOnSucssView(
                loadAnim1,
                ll_followup,
                loading_load1,
                loading_error1,
                loading_view1,
                LoadStatus.LOAD);
        mPresenter.getFollowUpData(type);
    }

    /**
     * 获取存量增量
     */
    private void getStockData(int type) {
        mPresenter.setOnSucssView(
                loadAnim2,
                ll_stock,
                loading_load2,
                loading_error2,
                loading_view2,
                LoadStatus.LOAD);
        mPresenter.getStockData(type);
    }

    /**
     * 获取数量金额
     * */
    private void getMoneyCount(int type){
        mPresenter.setOnSucssView(
                loadAnim3,
                ll_money,
                loading_load3,
                loading_error3,
                loading_view3,
                LoadStatus.LOAD);
        mPresenter.getMoneyCountData(type);
    }

    /**
     * 数据调取
     * @param  busType  请求业务类型(跟进 存量 金额..)
     * @param  type     筛选数据类型(今天 昨天..)
     * */
    private void getData(int busType,int type,String value){
        switch (busType){
            case 0:
                followUpType = type;
                tv_screen_title1.setText(value);
                getFollowUpData(type);
                break;

            case 1:
                stockType = type;
                tv_screen_title2.setText(value);
                getStockData(type);
                break;

            case 2:
                moneyCnType = type;
                tv_screen_title3.setText(value);
                getMoneyCount(type);
                break;
        }
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            /*客户跟进*/
            case R.id.rb_customer:
                followUpPage = 0;
                bindFowUpData();
                ll_dashboard_signin.setVisibility(View.VISIBLE);
                break;

            /*线索跟进*/
            case R.id.rb_clue:
                followUpPage = 1;
                bindFowUpData();
                ll_dashboard_signin.setVisibility(View.GONE);
                break;

            /*客户线索筛选*/
            case R.id.ll_case1:
                mPresenter.screenControlViews(ScreenType.FOLLOWUP);
                break;

            /*增量存量筛选*/
            case R.id.ll_case2:
                mPresenter.screenControlViews(ScreenType.STOCK);
                break;

            /*数量金额筛选*/
            case R.id.ll_case3:
                mPresenter.screenControlViews(ScreenType.MONEY);
                break;

            case R.id.ll_dashboard_followup:
                if (!checkPermission(DashborardType.CUS_FOLLOWUP)) {
                    return;
                }
                Bundle bdFollowup = new Bundle();
                bdFollowup.putSerializable("type", DashborardType.CUS_FOLLOWUP);
                app.startActivity(mActivity, DashboardDetailActivity.class, MainApp.ENTER_TYPE_RIGHT, false, bdFollowup);
                break;
            case R.id.ll_dashboard_signin:
                if (!checkPermission(DashborardType.CUS_SIGNIN)) {
                    return;
                }
                Bundle bdSignin = new Bundle();
                bdSignin.putSerializable("type", DashborardType.CUS_SIGNIN);
                app.startActivity(mActivity, DashboardDetailActivity.class, MainApp.ENTER_TYPE_RIGHT, false, bdSignin);
                break;
            case R.id.ll_dashboard_record:
                if (!checkPermission(DashborardType.CUS_CELL_RECORD)) {
                    return;
                }
                Bundle bdRecord = new Bundle();
                bdRecord.putSerializable("type", DashborardType.CUS_CELL_RECORD);
                app.startActivity(mActivity, DashboardDetailActivity.class, MainApp.ENTER_TYPE_RIGHT, false, bdRecord);
                break;
            case R.id.ll_dashboard_order_number:
                if (!checkPermission(DashborardType.ORDER_NUMBER)) {
                    return;
                }
                Bundle bdOrderNumber = new Bundle();
                bdOrderNumber.putSerializable("type", DashborardType.ORDER_NUMBER);
                app.startActivity(mActivity, DashboardDetailActivity.class, MainApp.ENTER_TYPE_RIGHT, false, bdOrderNumber);
                break;
            case R.id.ll_dashboard_order_money:
                if (!checkPermission(DashborardType.ORDER_MONEY)) {
                    return;
                }
                Bundle bdOrderMoney = new Bundle();
                bdOrderMoney.putSerializable("type", DashborardType.ORDER_MONEY);
                app.startActivity(mActivity, DashboardDetailActivity.class, MainApp.ENTER_TYPE_RIGHT, false, bdOrderMoney);
                break;

            /*跟进,点击重试*/
            case R.id.tv_click_rest1:
                getFollowUpData(followUpType);
                break;

             /*增量存量,点击重试*/
            case R.id.tv_click_rest2:
                getStockData(stockType);
                break;

            /*数量金额,点击重试*/
            case R.id.tv_click_rest3:
                getMoneyCount(moneyCnType);
                break;
        }
    }

    private boolean checkPermission(DashborardType per) {
        if (!PermissionManager.getInstance().hasPermission(per.getaPermission())) {
            Toast("无数据查看权限!");
            return false;
        }
        return true;
    }

    @Override
    public void setScreenVal(ScreenType screenType,int type,String value) {
        Toast(value+":"+type);
        getData(screenType.type(),type,value);
    }

    // 获取跟进成功
    @Override
    public void followUpSuccessEmbl(FollowupStatistic csclueFowUp) {
        mPresenter.setOnSucssView(
                loadAnim1,
                ll_followup,
                loading_load1,
                loading_error1,
                loading_view1,
                LoadStatus.SUCCESS);
        this.csclueFowUp = csclueFowUp;
        bindFowUpData();
    }

    // 获取跟进失败
    @Override
    public void followUpErrorEmbl() {
        mPresenter.setOnSucssView(
                loadAnim1,
                ll_followup,
                loading_load1,
                loading_error1,
                loading_view1,
                LoadStatus.ERROR);
    }

    // 获取存量增量成功
    @Override
    public void stockSuccessEmbl(ArrayList<StockStatistic> stockListModel) {
        mPresenter.setOnSucssView(
                loadAnim2,
                ll_stock,
                loading_load2,
                loading_error2,
                loading_view2,
                LoadStatus.SUCCESS);
        bindClAdapter(stockListModel);
    }

    // 获取存量增量失败
    @Override
    public void stockErrorEmbl() {
        mPresenter.setOnSucssView(
                loadAnim2,
                ll_stock,
                loading_load2,
                loading_error2,
                loading_view2,
                LoadStatus.ERROR);
    }

    // 获取数量金额成功
    @Override
    public void moneyConSuccessEmbl(MoneyStatistic moneyCountModel) {
        mPresenter.setOnSucssView(
                loadAnim3,
                ll_money,
                loading_load3,
                loading_error3,
                loading_view3,
                LoadStatus.SUCCESS);
        bindMoneyCountData(moneyCountModel);
    }

    // 获取数量金额失败
    @Override
    public void moneyConErrorEmbl() {
        mPresenter.setOnSucssView(
                loadAnim3,
                ll_money,
                loading_load3,
                loading_error3,
                loading_view3,
                LoadStatus.ERROR);
    }

    @Override
    public LoyoProgressHUD showStatusProgress() {
        showCommitLoading();
        return hud;
    }

    @Override
    public LoyoProgressHUD showProgress(String message) {
        showLoading2(message);
        return hud;
    }

    @Override
    public void hideProgress() {
        cancelLoading2();
    }

    @Override
    public void showMsg(String message) {
        LoyoToast.info(this.getActivity(), message);
    }
}
