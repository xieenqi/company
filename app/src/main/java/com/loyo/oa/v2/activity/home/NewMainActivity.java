package com.loyo.oa.v2.activity.home;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Looper;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activity.BulletinManagerActivity_;
import com.loyo.oa.v2.activity.attendance.AttendanceActivity_;
import com.loyo.oa.v2.activity.contact.ContactsActivity;
import com.loyo.oa.v2.activity.customer.CustomerManageActivity_;
import com.loyo.oa.v2.activity.discuss.ActivityMyDiscuss;
import com.loyo.oa.v2.activity.home.adapter.AdapterHomeItem;
import com.loyo.oa.v2.activity.home.bean.HomeItem;
import com.loyo.oa.v2.activity.home.cusview.MoreWindow;
import com.loyo.oa.v2.activity.project.ProjectManageActivity_;
import com.loyo.oa.v2.activity.sale.ActivitySaleOpportunitiesManager;
import com.loyo.oa.v2.activity.setting.SettingActivity;
import com.loyo.oa.v2.activity.signin.SignInManagerActivity_;
import com.loyo.oa.v2.activity.tasks.TasksManageActivity_;
import com.loyo.oa.v2.activity.wfinstance.WfInstanceManageActivity;
import com.loyo.oa.v2.activity.work.WorkReportsManageActivity;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.beans.HttpMainRedDot;
import com.loyo.oa.v2.beans.Permission;
import com.loyo.oa.v2.beans.TrackRule;
import com.loyo.oa.v2.common.FinalVariables;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.common.http.HttpErrorCheck;
import com.loyo.oa.v2.point.IMain;
import com.loyo.oa.v2.service.AMapService;
import com.loyo.oa.v2.service.InitDataService_;
import com.loyo.oa.v2.tool.BaseActivity;
import com.loyo.oa.v2.tool.Config_project;
import com.loyo.oa.v2.tool.LogUtil;
import com.loyo.oa.v2.tool.RCallback;
import com.loyo.oa.v2.tool.RestAdapterFactory;
import com.loyo.oa.v2.tool.Utils;
import com.loyo.oa.v2.tool.customview.HafeRoundImageView;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * 2.1新版首页
 * Created by yyy on 16/5/27.
 */
public class NewMainActivity extends BaseActivity implements View.OnClickListener {

    private ArrayList<HttpMainRedDot> mItemNumbers = new ArrayList<>();
    private ArrayList<HomeItem> items;
    private AdapterHomeItem adapter;

    private TextView newhome_name;
    private ListView listView;
    private Button btn_add;
    private HafeRoundImageView heading;
    private MoreWindow mMoreWindow;

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(final Context context, final Intent intent) {
            if (null == intent) {
                return;
            }
            String action = intent.getAction();
            if (TextUtils.equals(action, FinalVariables.ACTION_DATA_CHANGE)) {
                launch();
                testJurl();
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newmain);
        LocalBroadcastManager.getInstance(this).registerReceiver(mReceiver, new IntentFilter(FinalVariables.ACTION_DATA_CHANGE));
        updateUser();
        initData();
    }

    @Override
    protected void onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mReceiver);
        super.onDestroy();
    }

    void launch() {
        requestNumber();
        startTrack();
    }


    //获取轨迹，并设置AlarmManager
    void startTrack() {
        if (!Utils.isServiceRunning(AMapService.class.getName())) {
        }
        TrackRule.InitTrackRule();
    }

    /**
     * 首页业务显示\隐藏权限 判断设置
     */
    public void testJurl() {
        //超级管理员判断
        if (!MainApp.user.isSuperUser()) {
            if (null == MainApp.user || null == MainApp.user.newpermission || null == MainApp.user.newpermission ||
                    0 == MainApp.user.newpermission.size()) {
                Timer timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        Looper.prepare();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                testJurl();
                            }
                        });
                        Looper.loop();
                    }
                }, 5000);
                return;
            }

            ArrayList<Permission> suitesNew = new ArrayList<>();
            suitesNew.clear();
            suitesNew.addAll(MainApp.user.newpermission);

            for (Permission permission : suitesNew) {
                LogUtil.d(permission.getName() + ":" + permission.getCode() + "-" + permission.isEnable());
                for (int i = 0; i < items.size(); i++) {
                    if (items.get(i).code.equals(permission.getCode())) {
                        if (!permission.isEnable()) {
                            items.remove(i);
                        }
                    }
                }
            }
        }
        initView();
    }


    /**
     * 数据初始化
     */
    public void initData() {
        Intent intent = new Intent(mContext, InitDataService_.class);
        startService(intent);
    }

    /**
     * 初始化
     */
    public void initView() {
        heading = (HafeRoundImageView) findViewById(R.id.newhome_heading_img);
        listView = (ListView) findViewById(R.id.newhome_listview);
        btn_add = (Button) findViewById(R.id.btn_add);
        newhome_name = (TextView) findViewById(R.id.newhome_name);

        newhome_name.setText(MainApp.user.getRealname());
        ImageLoader.getInstance().displayImage(MainApp.user.avatar, heading);
        adapter = new AdapterHomeItem(this, items, mItemNumbers);
        listView.setAdapter(adapter);

        btn_add.setOnClickListener(this);
        heading.setOnClickListener(this);
        heading.setOnTouchListener(Global.GetTouch());
        btn_add.setOnTouchListener(Global.GetTouch());

        //跳转对应功能
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(items.get(position).title.equals("通讯录")){
                    if (null != MainApp.lstDepartment) {
                        app.startActivity((Activity) mContext, ContactsActivity.class, MainApp.ENTER_TYPE_RIGHT, false, null);
                    } else {
                        Toast("请重新拉去组织架构");
                    }
                }else{
                    app.startActivityForResult((Activity) mContext, items.get(position).cls, MainApp.ENTER_TYPE_RIGHT, 1, null);
                }
            }
        });
    }

    /**
     * 组装首页Item数据
     */
    void updateUser() {

        items = new ArrayList<>(Arrays.asList(new HomeItem(R.drawable.newmain_customer, "公告通知", BulletinManagerActivity_.class, "0213", 0),
                new HomeItem(R.drawable.newmain_sale, "我的讨论", ActivityMyDiscuss.class, "0", 0),
                new HomeItem(R.drawable.newmain_sagin, "通讯录", ContactsActivity.class, "0", 0),
                new HomeItem(R.drawable.newmain_project, "客户管理", CustomerManageActivity_.class, "0205", 1),
                new HomeItem(R.drawable.newmain_task, "销售机会", ActivitySaleOpportunitiesManager.class, "0206", 1),
                new HomeItem(R.drawable.newmain_report, "客户拜访", SignInManagerActivity_.class, "0203", 1),
                new HomeItem(R.drawable.newmain_wfin, "项目管理", ProjectManageActivity_.class, "0201", 2),
                new HomeItem(R.drawable.newmain_attent, "任务计划", TasksManageActivity_.class, "0202", 2),
                new HomeItem(R.drawable.newmain_discuss, "工作报告", WorkReportsManageActivity.class, "0203", 2),
                new HomeItem(R.drawable.newmain_list, "审批流程", WfInstanceManageActivity.class, "0204", 2),
                new HomeItem(R.drawable.newmain_toast, "考勤管理", AttendanceActivity_.class, "0211", 2)));

    }

    /**
     * 显示弹出菜单
     * */
    void showMoreWindow(View view){
        if(null == mMoreWindow){
                mMoreWindow = new MoreWindow(this);
                mMoreWindow.init();
        }
        mMoreWindow.showMoreWindow(view,100);
    }


    /**
     * 获取首页红点数据
     */
    void requestNumber() {
        showLoading("");
        RestAdapterFactory.getInstance().build(Config_project.MAIN_RED_DOT).create(IMain.class).
                getNumber(new RCallback<ArrayList<HttpMainRedDot>>() {
                    @Override
                    public void success(final ArrayList<HttpMainRedDot> homeNumbers, final Response response) {
                        HttpErrorCheck.checkResponse("首页红点", response);
                        mItemNumbers = homeNumbers;
                        testJurl();
                    }

                    @Override
                    public void failure(final RetrofitError error) {
                        HttpErrorCheck.checkError(error);
                        super.failure(error);
                    }
                });
    }

    /**
     * slidingMenu设置(备用)
     */
    public void slidingMenuInit() {
        // configure the SlidingMenu
        SlidingMenu menu = new SlidingMenu(this);
        menu.setMode(SlidingMenu.LEFT);
        // 设置触摸屏幕的模式
        menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
        menu.setShadowWidthRes(R.dimen.dimen_30);
        //menu.setShadowDrawable(R.drawable.shadow);

        // 设置滑动菜单视图的宽度
        menu.setBehindOffsetRes(R.dimen.dimen_150);
        // 设置渐入渐出效果的值
        menu.setFadeDegree(0.35f);
        menu.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);
        //为侧滑菜单设置布局
        menu.setMenu(R.layout.slidingmenu_left);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //通讯录
            case R.id.newhome_heading_img:
                app.startActivity(this, SettingActivity.class, MainApp.ENTER_TYPE_RIGHT, false, null);
                break;
            //更多
            case R.id.btn_add:
                showMoreWindow(v);
                break;
        }
    }
}
