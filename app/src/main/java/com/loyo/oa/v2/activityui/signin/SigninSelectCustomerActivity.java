package com.loyo.oa.v2.activityui.signin;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

import com.library.module.widget.loading.LoadingLayout;
import com.loyo.oa.hud.progress.LoyoProgressHUD;
import com.loyo.oa.hud.toast.LoyoToast;
import com.loyo.oa.pulltorefresh.PullToRefreshBase;
import com.loyo.oa.pulltorefresh.PullToRefreshListView;
import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.customer.CustomerAddActivity_;
import com.loyo.oa.v2.activityui.customer.event.MyCustomerRushEvent;
import com.loyo.oa.v2.activityui.customer.model.Customer;
import com.loyo.oa.v2.activityui.customer.model.Locate;
import com.loyo.oa.v2.activityui.signin.adapter.SigninSelectCustomerAdapter;
import com.loyo.oa.v2.activityui.signin.bean.SigninSelectCustomer;
import com.loyo.oa.v2.activityui.signin.event.SigninCustomerRushEvent;
import com.loyo.oa.v2.activityui.signin.persenter.SigninSelectCustomerPControl;
import com.loyo.oa.v2.activityui.signin.viewcontrol.SigninSelectCustomerVControl;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.beans.Location;
import com.loyo.oa.v2.common.event.AppBus;
import com.loyo.oa.v2.tool.BaseLoadingActivity;

import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

/**
 * 拜访签到【选择客户】页面
 * Created by xeq on 16/11/15.
 */

public class SigninSelectCustomerActivity extends BaseLoadingActivity implements View.OnClickListener,
        PullToRefreshBase.OnRefreshListener2, SigninSelectCustomerVControl {
    private static final int SEARCH_CUSTOMER = 100;
    private static final int CREAT_CUSTOMER = 200;
    private PullToRefreshListView lv_list;
    private SigninSelectCustomerPControl pControl;
    private SigninSelectCustomerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pControl = new SigninSelectCustomerPControl(this);
        initView();
        AppBus.getInstance().register(this);
    }

    @Override
    protected void onDestroy() {
        AppBus.getInstance().unregister(this);
        super.onDestroy();
    }

    @Override
    public void setLayoutView() {
        setContentView(R.layout.activity_signin_select_customer);
    }

    @Override
    public void getPageData() {
        getLocationData();
    }

    private void initView() {
        findViewById(R.id.ll_back).setOnClickListener(this);
        findViewById(R.id.ll_search).setOnClickListener(this);
        findViewById(R.id.ll_add_customer).setOnClickListener(this);
        ((TextView) findViewById(R.id.tv_title)).setText("选择客户");
        lv_list = (PullToRefreshListView) findViewById(R.id.lv_list);
        lv_list.setMode(PullToRefreshBase.Mode.BOTH);
        lv_list.setOnRefreshListener(this);
        adapter = new SigninSelectCustomerAdapter(app);
        lv_list.setAdapter(adapter);
        lv_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SigninSelectCustomer item = adapter.getItemData(position - 1);

                SigninSelectCustomer signinSelectCustomer = new SigninSelectCustomer();
                signinSelectCustomer.contacts=item.contacts;
                signinSelectCustomer.name=item.name;
                signinSelectCustomer.id=item.id;
                if (adapter.isLocationOk(item)) {
                    signinSelectCustomer.position=item.position;//有定位信息才传
                }
                SigninCustomerRushEvent signinCustomerRushEvent = new SigninCustomerRushEvent(signinSelectCustomer);
                AppBus.getInstance().post(signinCustomerRushEvent);
                onBackPressed();

            }
        });
        getPageData();
    }

    private void getLocationData() {
        Intent intent = getIntent();
        double lon = intent.getDoubleExtra("lon", -1);
        double lat = intent.getDoubleExtra("lat", -1);
        if (lat == -1 || lon == -1) {
            Toast("没有有效的定位信息");
            onBackPressed();
        }
        pControl.oneGetNearCustomer(lon, lat);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_back:
                onBackPressed();
                break;
            /*搜索客户*/
            case R.id.ll_search:
                Bundle b = new Bundle();
                app.startActivityForResult(this, SigninSelectCustomerSearchActivity.class, MainApp.ENTER_TYPE_RIGHT, SEARCH_CUSTOMER, b);
                break;
            /*创建一个新客户*/
            case R.id.ll_add_customer:
                Intent intent = new Intent(SigninSelectCustomerActivity.this, CustomerAddActivity_.class);
                startActivity(intent);
                overridePendingTransition(R.anim.enter_righttoleft, R.anim.exit_righttoleft);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            return;
        }
        switch (requestCode) {
            case SEARCH_CUSTOMER:
                //因为搜索页面统一提供回调，所以再回调处理。
                SigninSelectCustomer customer = (SigninSelectCustomer) data.getSerializableExtra("data");
                SigninCustomerRushEvent signinCustomerRushEvent = new SigninCustomerRushEvent(customer);
                AppBus.getInstance().post(signinCustomerRushEvent);
                onBackPressed();
                break;
            case CREAT_CUSTOMER:
                break;
        }
    }

    /**
     * 通过EventBus处理新建客户
     */
    @Subscribe
    public void onMyCustomerListRushEvent(MyCustomerRushEvent event) {
        //如果是添加的客户，收到以后，转换成客户signinCustomerRushEvent，然后再发送出去
        if (MyCustomerRushEvent.EVENT_CODE_ADD == event.eventCode) {
            SigninCustomerRushEvent signinCustomerRushEvent = new SigninCustomerRushEvent(getSigninSelectCustomer(event.data));
            AppBus.getInstance().post(signinCustomerRushEvent);
            finish();
        }
    }

    /**
     * 把一个customer转换成SigninSelectCustomer类型
     * @param customer
     * @return
     */
    private SigninSelectCustomer getSigninSelectCustomer(Customer customer) {
        SigninSelectCustomer signinSelectCustomer = new SigninSelectCustomer();

        List<Double> loc = new ArrayList<>();
        Locate oldeLoc = customer.position;
        if(oldeLoc!=null&&oldeLoc.loc.length==2){
            loc.add(oldeLoc.loc[0]);
            loc.add(oldeLoc.loc[1]);
            Location location = new Location(loc, oldeLoc.addr);
            signinSelectCustomer.position=location;
        }
        signinSelectCustomer.contacts=customer.contacts;
        signinSelectCustomer.name=customer.name;
        signinSelectCustomer.id=customer.id;
        return signinSelectCustomer;
    }


    @Override
    public void onPullDownToRefresh(PullToRefreshBase refreshView) {
        pControl.pullDown();
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase refreshView) {
        pControl.pullUp();
    }

    @Override
    public LoyoProgressHUD showStatusProgress() {
        showCommitLoading();
        return hud;
    }

    @Override
    public LoyoProgressHUD showProgress(String message) {
        showLoading2("");
        return hud;
    }

    @Override
    public void hideProgress() {
        cancelLoading2();
    }

    @Override
    public void showMsg(String message) {
        LoyoToast.info(this, message);
    }

    @Override
    public void getDataComplete() {
        lv_list.onRefreshComplete();
    }

    @Override
    public void bindData(ArrayList<SigninSelectCustomer> data) {
        adapter.setData(data);
    }

    @Override
    public LoadingLayout getLoadingLayout() {
        return ll_loading;
    }
}
