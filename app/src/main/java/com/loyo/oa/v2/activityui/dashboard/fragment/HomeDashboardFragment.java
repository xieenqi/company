package com.loyo.oa.v2.activityui.dashboard.fragment;

import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.loyo.oa.common.utils.BigNumberFormatTool;
import com.loyo.oa.common.utils.DensityUtil;
import com.loyo.oa.hud.progress.LoyoProgressHUD;
import com.loyo.oa.hud.toast.LoyoToast;
import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.dashboard.DashboardDetailActivity;
import com.loyo.oa.v2.activityui.dashboard.adapter.StockListAdapter;
import com.loyo.oa.v2.activityui.dashboard.common.DashboardType;
import com.loyo.oa.v2.activityui.dashboard.common.LoadStatus;
import com.loyo.oa.v2.activityui.dashboard.common.ScreenType;
import com.loyo.oa.v2.activityui.dashboard.model.FollowupStatistic;
import com.loyo.oa.v2.activityui.dashboard.model.HomePaymentModel;
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

import java.util.ArrayList;

import me.itangqi.waveloadingview.WaveLoadingView;

/**
 * 【仪表盘】
 * Created by yyy on 16/12/9.
 */

public class HomeDashboardFragment extends BaseFragment implements View.OnClickListener, HomeDashBoardView {

    private View mView;
    private RadioButton rb_customer, rb_clue;
    private RelativeLayout loading_view1, loading_view2, loading_view3, loading_view4;
    private LinearLayout loading_error1, loading_error2, loading_error3, loading_error4;
    private TextView tv_click_rest1, tv_click_rest2, tv_click_rest3, tv_click_rest4;
    private TextView tv_screen_title1, tv_screen_title2, tv_screen_title3, tv_screen_title4;
    private ImageView loading_load1, loading_load2, loading_load3, loading_load4;
    private LinearLayout ll_case1, ll_case2, ll_case3, ll_case4;
    private CustomerListView lv_stocklist;

    private LinearLayout ll_dashboard_followup, ll_dashboard_signin,
            ll_dashboard_record, ll_dashboard_order_number,
            ll_dashboard_order_money, ll_followup, ll_stock, ll_money, ll_pay;

    private TextView fw_totalsize, fw_count,
            visit_totalsize, visit_count,
            voice_totalsize, voice_count,
            tv_nums1, tv_nums2, tv_nums3, payment_tv_all;
    private TextView tvErrorMsg;//增量错误原因

    private TextView tv_target_count, tv_order_count,  /*  目标数量 订单数量  */
            tv_target_money, tv_order_money;  /*  目标金额 订单金额  */
    //回款统计相关控件
    private TextView tv_payment_1, tv_payment_2, tv_payment_3, tv_payment_all;
    private View view_payment_1, view_payment_2, view_payment_3;

    private StockListAdapter mAdapter;
    private HomeDashboardPresenter mPresenter;
    private AnimationDrawable loadAnim1, loadAnim2, loadAnim3, loadAnim4;

    private FollowupStatistic csclueFowUp;

    private int followUpType = 1;  //跟进 默认今天
    private int stockType = 1;  //增量存量 默认今天
    private int moneyCnType = 5;  //数量金额 默认本月
    private int paymentType = 5;  //回款统计 默认本月
    private int followUpPage = 0;  //0:客户 1:线索
    private boolean isLoading;

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
    private void bindClAdapter(final ArrayList<StockStatistic> model) {
        mAdapter = new StockListAdapter(getActivity(), model);
        lv_stocklist.setAdapter(mAdapter);
        lv_stocklist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                DashboardType.COMMON.setTttle("增量 存量");
                DashboardType.COMMON.setPermission(BusinessOperation.AUGMENTER_STOCK);
                if (!checkPermission(DashboardType.COMMON)) {
                    return;
                }
                DashboardType.COMMON.setTttle(model.get(position).tagItemName);
                Bundle bundle = new Bundle();
                bundle.putInt("time", stockType);
                bundle.putSerializable("type", DashboardType.COMMON);
                bundle.putSerializable("tagItemId", model.get(position).tagItemId);
                app.startActivity(mActivity, DashboardDetailActivity.class, MainApp.ENTER_TYPE_RIGHT, false, bundle);
            }
        });
    }

    /**
     * 绑定跟进数据
     */
    private void bindFowUpData() {
        if (followUpPage == 0) {
            tv_nums1.setText("客户数");
            tv_nums2.setText("客户数");
            tv_nums3.setText("客户数");
            fw_totalsize.setText(csclueFowUp.getActivityTotal());
            fw_count.setText(csclueFowUp.getActivityDistinct());
            visit_totalsize.setText(csclueFowUp.getVisitTotal());
            visit_count.setText(csclueFowUp.getVisitDistinct());

            voice_totalsize.setText(csclueFowUp.getVoiceTotal());
            voice_count.setText(csclueFowUp.getVoiceDistinct());
        } else {
            tv_nums1.setText("线索数");
            tv_nums3.setText("线索数");
            fw_totalsize.setText(csclueFowUp.getClueActivityTotal());
            fw_count.setText(csclueFowUp.getClueActivityDistinct());

            voice_totalsize.setText(csclueFowUp.getClueVoiceTotal());
            voice_count.setText(csclueFowUp.getClueVoiceDistinct());
        }
    }

    /**
     * 绑定数量金额数据
     */
    private void bindMoneyCountData(MoneyStatistic mcModel) {
        mPresenter.initWave(
                (WaveLoadingView) mView.findViewById(R.id.waveLoadingView1),
                (WaveLoadingView) mView.findViewById(R.id.waveLoadingView2),
                mcModel.getNumberPercent(),
                mcModel.getMoneyPercent(),
                mcModel.getNumberDisplayTitle(),
                mcModel.getMoneyDisplayTitle());
        
        /*mcModel.test();*/
        tv_target_count.setText(mcModel.getTargetNumber());
        tv_order_count.setText(mcModel.getTotalNumber());
        tv_target_money.setText(mcModel.getTargetAmount());
        tv_order_money.setText(mcModel.getTotalAmount());
    }

    /**
     * 绑定回款统计数据
     */
    private void bindPaymentData(HomePaymentModel paymentModel) {
        Log.i("tttt", "bindPaymentData: 数据绑定");
        int maxViewHeight = 130;//view的柱状图的最大高度
        Double sum = paymentModel.backMoney + paymentModel.backMoneyTarget + paymentModel.planMoney;
        int payment = DensityUtil.dp2px(getActivity(), (float) (maxViewHeight * (paymentModel.backMoney / sum)));
        int planPayment = DensityUtil.dp2px(getActivity(), (float) (maxViewHeight * (paymentModel.planMoney / sum)));
        int targetPayment = DensityUtil.dp2px(getActivity(), (float) (maxViewHeight * (paymentModel.backMoneyTarget / sum)));
        payment_tv_all.setText("¥" + BigNumberFormatTool.format(paymentModel.notBackMoney));
        tv_payment_1.setText("¥" + BigNumberFormatTool.format(paymentModel.backMoney));
        tv_payment_2.setText("¥" + BigNumberFormatTool.format(paymentModel.planMoney));
        tv_payment_3.setText("¥" + BigNumberFormatTool.format(paymentModel.backMoneyTarget));
        ViewGroup.LayoutParams layoutParams1 = view_payment_1.getLayoutParams();
        layoutParams1.height = payment;
        view_payment_1.setLayoutParams(layoutParams1);
        ViewGroup.LayoutParams layoutParams2 = view_payment_2.getLayoutParams();
        layoutParams2.height = planPayment;
        view_payment_2.setLayoutParams(layoutParams2);
        ViewGroup.LayoutParams layoutParams3 = view_payment_3.getLayoutParams();
        layoutParams3.height = targetPayment;
        view_payment_3.setLayoutParams(layoutParams3);
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
        ll_case4 = (LinearLayout) mView.findViewById(R.id.ll_case4);
        ll_followup = (LinearLayout) mView.findViewById(R.id.ll_followup);
        ll_stock = (LinearLayout) mView.findViewById(R.id.ll_stock);
        ll_money = (LinearLayout) mView.findViewById(R.id.ll_money);
        ll_pay = (LinearLayout) mView.findViewById(R.id.ll_pay);
        tvErrorMsg = (TextView) mView.findViewById(R.id.dashboard_tv_errormsg);
        loading_view1 = (RelativeLayout) mView.findViewById(R.id.loading_view1);
        loading_view2 = (RelativeLayout) mView.findViewById(R.id.loading_view2);
        loading_view3 = (RelativeLayout) mView.findViewById(R.id.loading_view3);
        loading_view4 = (RelativeLayout) mView.findViewById(R.id.loading_view4);

        loading_error1 = (LinearLayout) mView.findViewById(R.id.loading_error1);
        loading_error2 = (LinearLayout) mView.findViewById(R.id.loading_error2);
        loading_error3 = (LinearLayout) mView.findViewById(R.id.loading_error3);
        loading_error4 = (LinearLayout) mView.findViewById(R.id.loading_error4);

        tv_click_rest1 = (TextView) mView.findViewById(R.id.tv_click_rest1);
        tv_click_rest2 = (TextView) mView.findViewById(R.id.tv_click_rest2);
        tv_click_rest3 = (TextView) mView.findViewById(R.id.tv_click_rest3);
        tv_click_rest4 = (TextView) mView.findViewById(R.id.tv_click_rest4);

        tv_screen_title1 = (TextView) mView.findViewById(R.id.tv_screen_title1);
        tv_screen_title2 = (TextView) mView.findViewById(R.id.tv_screen_title2);
        tv_screen_title3 = (TextView) mView.findViewById(R.id.tv_screen_title3);
        tv_screen_title4 = (TextView) mView.findViewById(R.id.tv_screen_title4);

        loading_load1 = (ImageView) mView.findViewById(R.id.loading_load1);
        loading_load2 = (ImageView) mView.findViewById(R.id.loading_load2);
        loading_load3 = (ImageView) mView.findViewById(R.id.loading_load3);
        loading_load4 = (ImageView) mView.findViewById(R.id.loading_load4);

        loadAnim1 = (AnimationDrawable) mView.findViewById(R.id.loading_load1).getBackground();
        loadAnim2 = (AnimationDrawable) mView.findViewById(R.id.loading_load2).getBackground();
        loadAnim3 = (AnimationDrawable) mView.findViewById(R.id.loading_load3).getBackground();
        loadAnim4 = (AnimationDrawable) mView.findViewById(R.id.loading_load4).getBackground();

        fw_totalsize = (TextView) mView.findViewById(R.id.fw_totalsize);
        visit_totalsize = (TextView) mView.findViewById(R.id.visit_totalsize);
        voice_totalsize = (TextView) mView.findViewById(R.id.voice_totalsize);

        tv_target_count = (TextView) mView.findViewById(R.id.tv_target_count);
        tv_order_count = (TextView) mView.findViewById(R.id.tv_order_count);
        tv_target_money = (TextView) mView.findViewById(R.id.tv_target_money);
        tv_order_money = (TextView) mView.findViewById(R.id.tv_order_money);
        tv_nums1 = (TextView) mView.findViewById(R.id.tv_nums1);
        tv_nums2 = (TextView) mView.findViewById(R.id.tv_nums2);
        tv_nums3 = (TextView) mView.findViewById(R.id.tv_nums3);

        voice_count = (TextView) mView.findViewById(R.id.voice_count);
        visit_count = (TextView) mView.findViewById(R.id.visit_count);
        fw_count = (TextView) mView.findViewById(R.id.fw_count);

        //回款统计
        tv_payment_1 = (TextView) mView.findViewById(R.id.payment_tv_1);
        tv_payment_2 = (TextView) mView.findViewById(R.id.payment_tv_2);
        tv_payment_3 = (TextView) mView.findViewById(R.id.payment_tv_3);
        payment_tv_all = (TextView) mView.findViewById(R.id.payment_tv_all);
        view_payment_1 = mView.findViewById(R.id.payment_view_1);
        view_payment_2 = mView.findViewById(R.id.payment_view_2);
        view_payment_3 = mView.findViewById(R.id.payment_view_3);

        rb_customer.setOnClickListener(this);
        ll_pay.setOnClickListener(this);
        rb_clue.setOnClickListener(this);
        ll_case1.setOnClickListener(this);
        ll_case2.setOnClickListener(this);
        ll_case3.setOnClickListener(this);
        ll_case4.setOnClickListener(this);
        tv_click_rest1.setOnClickListener(this);
        tv_click_rest2.setOnClickListener(this);
        tv_click_rest3.setOnClickListener(this);
        tv_click_rest4.setOnClickListener(this);
        ll_dashboard_followup.setOnClickListener(this);
        ll_dashboard_signin.setOnClickListener(this);
        ll_dashboard_record.setOnClickListener(this);
        ll_dashboard_order_number.setOnClickListener(this);
        ll_dashboard_order_money.setOnClickListener(this);

        Global.SetTouchView(ll_dashboard_followup, ll_dashboard_signin, ll_dashboard_record, ll_dashboard_order_number,
                ll_dashboard_order_money, ll_case1, ll_case2, ll_case3, ll_case4, tv_click_rest1, tv_click_rest2, tv_click_rest3, tv_click_rest4, ll_pay);
        isLoading = true;
        getPageData();
    }

    private void getPageData() {
        getFollowUpData(followUpType);
        getStockData(stockType);
        getMoneyCount(moneyCnType);
        //回款统计
        getPayment(paymentType);
    }

    /**
     * 选择此页面刷新全部数据
     */
    public void onInIt() {
        isLoading = false;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                getPageData();
            }
        }, 200);
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
                isLoading ? LoadStatus.LOAD : LoadStatus.SUCCESS);
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
                isLoading ? LoadStatus.LOAD : LoadStatus.SUCCESS);
        mPresenter.getStockData(type);
    }

    /**
     * 获取数量金额
     */
    private void getMoneyCount(int type) {
        mPresenter.setOnSucssView(
                loadAnim3,
                ll_money,
                loading_load3,
                loading_error3,
                loading_view3,
                isLoading ? LoadStatus.LOAD : LoadStatus.SUCCESS);
        mPresenter.getMoneyCountData(type);
    }

    /**
     * 获取回款统计
     */
    private void getPayment(int type) {
        mPresenter.setOnSucssView(
                loadAnim4,
                ll_pay,
                loading_load4,
                loading_error4,
                loading_view4,
                isLoading ? LoadStatus.LOAD : LoadStatus.SUCCESS);
        mPresenter.getPayment(type);
    }

    /**
     * 数据调取
     *
     * @param busType 请求业务类型(跟进 存量 金额..)
     * @param type    筛选数据类型(今天 昨天..)
     */
    private void getData(int busType, int type, String value) {
        switch (busType) {
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
            case 3://回款
                paymentType = type;
                tv_screen_title4.setText(value);
                getPayment(type);
                break;
        }
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            /** 客户跟进 */
            case R.id.rb_customer:
                followUpPage = 0;
                bindFowUpData();
                ll_dashboard_signin.setVisibility(View.VISIBLE);
                break;

            /** 线索跟进 */
            case R.id.rb_clue:
                followUpPage = 1;
                bindFowUpData();
                ll_dashboard_signin.setVisibility(View.GONE);
                break;

            /** 客户线索筛选 */
            case R.id.ll_case1:
                mPresenter.screenControlViews(ScreenType.FOLLOWUP);
                break;

            /** 增量存量筛选 */
            case R.id.ll_case2:
                mPresenter.screenControlViews(ScreenType.STOCK);
                break;

            /** 数量金额筛选 */
            case R.id.ll_case3:
                mPresenter.screenControlViews(ScreenType.MONEY);
                break;
            /** 回款统计筛选 */
            case R.id.ll_case4:
                mPresenter.screenControlViews(ScreenType.PAYMENT);
                break;
            /** 跳转跟进列表 */
            case R.id.ll_dashboard_followup:
                if (!checkPermission(DashboardType.CUS_FOLLOWUP)) {
                    return;
                }
                Bundle bdFollowup = new Bundle();
                bdFollowup.putInt("time", followUpType);
                if (followUpPage == 0) {
                    bdFollowup.putSerializable("type", DashboardType.CUS_FOLLOWUP);
                } else {
                    bdFollowup.putSerializable("type", DashboardType.SALE_FOLLOWUP);
                }
                app.startActivity(mActivity, DashboardDetailActivity.class, MainApp.ENTER_TYPE_RIGHT, false, bdFollowup);
                break;
            /** 跳转拜访 */
            case R.id.ll_dashboard_signin:
                if (!checkPermission(DashboardType.CUS_SIGNIN)) {
                    return;
                }
                Bundle bdSignin = new Bundle();
                bdSignin.putInt("time", followUpType);
                bdSignin.putSerializable("type", DashboardType.CUS_SIGNIN);
                app.startActivity(mActivity, DashboardDetailActivity.class, MainApp.ENTER_TYPE_RIGHT, false, bdSignin);
                break;
            /** 电话录音 */
            case R.id.ll_dashboard_record:
                if (!checkPermission(DashboardType.CUS_CELL_RECORD)) {
                    return;
                }
                Bundle bdRecord = new Bundle();
                bdRecord.putInt("time", followUpType);
                if (followUpPage == 0) {
                    bdRecord.putSerializable("type", DashboardType.CUS_CELL_RECORD);
                } else {
                    bdRecord.putSerializable("type", DashboardType.SALE_CELL_RECORD);
                }
                app.startActivity(mActivity, DashboardDetailActivity.class, MainApp.ENTER_TYPE_RIGHT, false, bdRecord);
                break;
            /** 目标数量 */
            case R.id.ll_dashboard_order_number:
                if (!checkPermission(DashboardType.ORDER_NUMBER)) {
                    return;
                }
                Bundle bdOrderNumber = new Bundle();
                bdOrderNumber.putInt("time", moneyCnType);
                bdOrderNumber.putSerializable("type", DashboardType.ORDER_NUMBER);
                app.startActivity(mActivity, DashboardDetailActivity.class, MainApp.ENTER_TYPE_RIGHT, false, bdOrderNumber);
                break;
            /** 目标金额 */
            case R.id.ll_dashboard_order_money:
                if (!checkPermission(DashboardType.ORDER_MONEY)) {
                    return;
                }
                Bundle bdOrderMoney = new Bundle();
                bdOrderMoney.putInt("time", moneyCnType);
                bdOrderMoney.putSerializable("type", DashboardType.ORDER_MONEY);
                app.startActivity(mActivity, DashboardDetailActivity.class, MainApp.ENTER_TYPE_RIGHT, false, bdOrderMoney);
                break;
            /** 回款统计 */
            case R.id.ll_pay:
                if (!checkPermission(DashboardType.PAYMENT)) {
                    return;
                }
                Bundle paymentBundle = new Bundle();
                paymentBundle.putInt("time", paymentType);
                paymentBundle.putSerializable("type", DashboardType.PAYMENT);
                app.startActivity(mActivity, DashboardDetailActivity.class, MainApp.ENTER_TYPE_RIGHT, false, paymentBundle);
                break;
            /** 跟进,点击重试 */
            case R.id.tv_click_rest1:
                isLoading = true;
                getFollowUpData(followUpType);
                break;

            /** 增量存量,点击重试 */
            case R.id.tv_click_rest2:
                isLoading = true;
                getStockData(stockType);
                break;

            /** 数量金额,点击重试 */
            case R.id.tv_click_rest3:
                isLoading = true;
                getMoneyCount(moneyCnType);
                break;
            /** 回款统计,点击重试 */
            case R.id.tv_click_rest4:
                isLoading = true;
                getPayment(paymentType);
                break;
        }
    }

    private boolean checkPermission(DashboardType per) {
        if (!PermissionManager.getInstance().hasPermission(per.getaPermission())) {
            Toast("无数据查看权限!");
            return false;
        }
        return true;
    }

    @Override
    public void setScreenVal(ScreenType screenType, int type, String value) {
        //Toast(value+":"+type);
        isLoading = true;
        getData(screenType.type(), type, value);
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

    // 获取存量增量,失败可能有服务器返回的提示，需要显示
    @Override
    public void stockErrorEmbl(String msg) {
        mPresenter.setOnSucssView(
                loadAnim2,
                ll_stock,
                loading_load2,
                loading_error2,
                loading_view2,
                LoadStatus.ERROR);
        tvErrorMsg.setText(msg);
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

    //获取回款统计成功
    @Override
    public void paymentConSuccessEmbl(HomePaymentModel paymentModel) {
        mPresenter.setOnSucssView(
                loadAnim4,
                ll_pay,
                loading_load4,
                loading_error4,
                loading_view4,
                LoadStatus.SUCCESS);
        bindPaymentData(paymentModel);
    }

    //获取回款统计失败
    @Override
    public void paymentConErrorEmbl() {
        mPresenter.setOnSucssView(
                loadAnim4,
                ll_pay,
                loading_load4,
                loading_error4,
                loading_view4,
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
