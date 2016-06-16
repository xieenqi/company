package com.loyo.oa.v2.activity.home;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activity.BulletinManagerActivity_;
import com.loyo.oa.v2.activity.attendance.AttendanceActivity_;
import com.loyo.oa.v2.activity.attendance.AttendanceAddActivity_;
import com.loyo.oa.v2.activity.contact.ContactsActivity;
import com.loyo.oa.v2.activity.customer.activity.ActivityCustomerManager;
import com.loyo.oa.v2.activity.customer.activity.CustomerAddActivity_;
import com.loyo.oa.v2.activity.customer.activity.CustomerDetailInfoActivity_;
import com.loyo.oa.v2.activity.customer.activity.SaleActivitiesAddActivity;
import com.loyo.oa.v2.activity.discuss.ActivityMyDiscuss;
import com.loyo.oa.v2.activity.discuss.hait.ActivityHait;
import com.loyo.oa.v2.activity.home.adapter.AdapterHomeItem;
import com.loyo.oa.v2.activity.home.bean.HomeItem;
import com.loyo.oa.v2.activity.home.cusview.MoreWindow;
import com.loyo.oa.v2.activity.login.LoginActivity;
import com.loyo.oa.v2.activity.project.ProjectInfoActivity_;
import com.loyo.oa.v2.activity.project.ProjectManageActivity_;
import com.loyo.oa.v2.activity.sale.ActivityAddMySale;
import com.loyo.oa.v2.activity.sale.ActivitySaleOpportunitiesManager;
import com.loyo.oa.v2.activity.setting.ActivityEditUserMobile;
import com.loyo.oa.v2.activity.setting.SettingActivity;
import com.loyo.oa.v2.activity.signin.SignInActivity;
import com.loyo.oa.v2.activity.signin.SignInManagerActivity_;
import com.loyo.oa.v2.activity.tasks.TasksAddActivity_;
import com.loyo.oa.v2.activity.tasks.TasksInfoActivity_;
import com.loyo.oa.v2.activity.tasks.TasksManageActivity_;
import com.loyo.oa.v2.activity.wfinstance.WfInstanceManageActivity;
import com.loyo.oa.v2.activity.wfinstance.WfinstanceInfoActivity_;
import com.loyo.oa.v2.activity.wfinstance.activity.ActivityWfInTypeSelect;
import com.loyo.oa.v2.activity.work.WorkReportAddActivity_;
import com.loyo.oa.v2.activity.work.WorkReportsInfoActivity_;
import com.loyo.oa.v2.activity.work.WorkReportsManageActivity;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.beans.AttendanceRecord;
import com.loyo.oa.v2.beans.HttpMainRedDot;
import com.loyo.oa.v2.beans.Permission;
import com.loyo.oa.v2.beans.TrackRule;
import com.loyo.oa.v2.beans.ValidateInfo;
import com.loyo.oa.v2.beans.ValidateItem;
import com.loyo.oa.v2.common.ExtraAndResult;
import com.loyo.oa.v2.common.FinalVariables;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.common.http.HttpErrorCheck;
import com.loyo.oa.v2.point.IAttendance;
import com.loyo.oa.v2.point.IMain;
import com.loyo.oa.v2.service.AMapService;
import com.loyo.oa.v2.service.CheckUpdateService;
import com.loyo.oa.v2.service.InitDataService_;
import com.loyo.oa.v2.tool.BaseActivity;
import com.loyo.oa.v2.tool.Config_project;
import com.loyo.oa.v2.tool.LocationUtilGD;
import com.loyo.oa.v2.tool.LogUtil;
import com.loyo.oa.v2.tool.RCallback;
import com.loyo.oa.v2.tool.RestAdapterFactory;
import com.loyo.oa.v2.tool.StringUtil;
import com.loyo.oa.v2.tool.Utils;
import com.loyo.oa.v2.tool.customview.AttenDancePopView;
import com.loyo.oa.v2.tool.customview.RoundImageView;
import com.loyo.oa.v2.tool.customview.pullToRefresh.PullToRefreshBase;
import com.loyo.oa.v2.tool.customview.pullToRefresh.PullToRefreshListView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * 2.1新版首页
 * Created by yyy on 16/5/27.
 */
public class NewMainActivity extends BaseActivity implements View.OnClickListener, LocationUtilGD.AfterLocation, PullToRefreshBase.OnRefreshListener2 {

    private AttendanceRecord attendanceRecords = new AttendanceRecord();
    private ArrayList<HttpMainRedDot> mItemNumbers = new ArrayList<>();
    private HashMap<String, Object> map = new HashMap<>();
    private ArrayList<HomeItem> items;
    private Set<String> companyTag;
    private AdapterHomeItem adapter;
    private Boolean inEnable;
    private Boolean outEnable;
    private boolean isJPus = false;//别名是否设置成功
    private boolean mInitData;
    private int outKind; //0上班  1下班  2加班

    private TextView newhome_name;
    private PullToRefreshListView listView;
    private Button btn_add;
    private RoundImageView heading;
    private MoreWindow mMoreWindow;
    private Intent mIntentCheckUpdate;
    private ValidateInfo validateInfo = new ValidateInfo();

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(final Context context, final Intent intent) {
            if (null == intent) {
                return;
            }
            String action = intent.getAction();
            if (TextUtils.equals(action, FinalVariables.ACTION_DATA_CHANGE)) {
                if (null != listView) {
                    listView.onRefreshComplete();
                }
                launch();
            }
        }
    };

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                //新建任务
                case BaseActivity.TASKS_ADD:
                    app.startActivityForResult(NewMainActivity.this, TasksAddActivity_.class, MainApp.ENTER_TYPE_RIGHT, 1, null);
                    break;
                //申请审批
                case BaseActivity.WFIN_ADD:
                    app.startActivityForResult(NewMainActivity.this, ActivityWfInTypeSelect.class, MainApp.ENTER_TYPE_RIGHT, 1, null);
                    //app.startActivityForResult(NewMainActivity.this, WfInstanceAddActivity_.class, MainApp.ENTER_TYPE_RIGHT, 1, null);
                    break;
                //提交报告
                case BaseActivity.WORK_ADD:
                    app.startActivityForResult(NewMainActivity.this, WorkReportAddActivity_.class, MainApp.ENTER_TYPE_RIGHT, 1, null);
                    break;
                //新建客户
                case BaseActivity.TASKS_ADD_CUSTOMER:
                    app.startActivityForResult(NewMainActivity.this, CustomerAddActivity_.class, MainApp.ENTER_TYPE_RIGHT, 1, null);
                    break;
                //考勤打卡
                case BaseActivity.ATTENT_ADD:
                    getValidateInfo();
                    break;
                //拜访签到
                case BaseActivity.SIGNIN_ADD:
                    app.startActivityForResult(NewMainActivity.this, SignInActivity.class, MainApp.ENTER_TYPE_RIGHT, 1, null);
                    break;
                //新建机会
                case BaseActivity.SALE_ADD:
                    app.startActivityForResult(NewMainActivity.this, ActivityAddMySale.class, MainApp.ENTER_TYPE_RIGHT, 1, null);
                    break;
                //写跟进
                case BaseActivity.FOLLOW_ADD:
                    app.startActivityForResult(NewMainActivity.this, SaleActivitiesAddActivity.class, MainApp.ENTER_TYPE_RIGHT, 1, null);
                    break;
            }
        }
    };


    @Override
    protected void onStart() {
        super.onStart();
        LogUtil.d(" 获得newMain现有的token：" + MainApp.getToken());
        //判断登陆是否失效
        if (MainApp.user == null || TextUtils.isEmpty(MainApp.user.id)) {
            if (StringUtil.isEmpty(MainApp.getToken())) {
                Toast.makeText(this, "您的登陆已经失效,请重新登陆!", Toast.LENGTH_SHORT).show();
                app.startActivity(this, LoginActivity.class, MainApp.ENTER_TYPE_LEFT, true, null);
                return;
            }
        }
        //初始化 用户数据
        if (!mInitData) {
            initData();
            mInitData = true;
        }
        permissionLocation();
    }


    @Override
    protected void onResume() {
        super.onResume();
        intentJpushInfo();
        if (null != MainApp.user) {
            requestNumber();
        }
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newmain);
        startService(rushTokenIntent);
        heading = (RoundImageView) findViewById(R.id.newhome_heading_img);
        heading.setOnClickListener(this);
        //注册拉去组织架构的广播
        LocalBroadcastManager.getInstance(this).registerReceiver(mReceiver, new IntentFilter(FinalVariables.ACTION_DATA_CHANGE));
        //检查更新
        mIntentCheckUpdate = new Intent(mContext, CheckUpdateService.class);
        listView = (PullToRefreshListView) findViewById(R.id.newhome_listview);
        btn_add = (Button) findViewById(R.id.btn_add);
        newhome_name = (TextView) findViewById(R.id.newhome_name);
        startService(mIntentCheckUpdate);
        showLoading("");
        initData();
    }

    @Override
    protected void onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mReceiver);
        super.onDestroy();
    }

    void launch() {
        setJpushAlias();
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
        updateUser();
        //超级管理员判断
        if (null != MainApp.user && !MainApp.user.isSuperUser()) {
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
        startService(new Intent(mContext,InitDataService_.class));
    }

    /**
     * 初始化
     */
    public void initView() {

        newhome_name.setText(MainApp.user.getRealname());
        ImageLoader.getInstance().displayImage(MainApp.user.avatar, heading);
        adapter = new AdapterHomeItem(this, items, mItemNumbers);
        btn_add.setOnClickListener(this);
        btn_add.setOnTouchListener(Global.GetTouch());
        listView.setAdapter(adapter);

        listView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
        listView.setOnRefreshListener(this);
    }

    /**
     * 组装首页Item数据
     */
    void updateUser() {
        items = new ArrayList<>(Arrays.asList(new HomeItem(R.drawable.newmain_toast, "公告通知", BulletinManagerActivity_.class, "0213", 0),
                new HomeItem(R.drawable.newmain_discuss, "我的讨论", ActivityMyDiscuss.class, "0", 0),
                new HomeItem(R.drawable.newmain_list, "通讯录", ContactsActivity.class, "0", 0),
                //new HomeItem(R.drawable.newmain_customer, "客户管理", CustomerManageActivity_.class, "0205", 1),
                new HomeItem(R.drawable.newmain_customer, "客户管理", ActivityCustomerManager.class, "0205", 1),
                new HomeItem(R.drawable.newmain_sale, "销售机会", ActivitySaleOpportunitiesManager.class, "0215", 1),
                new HomeItem(R.drawable.newmain_sagin, "客户拜访", SignInManagerActivity_.class, "0206", 1),
                new HomeItem(R.drawable.newmain_project, "项目管理", ProjectManageActivity_.class, "0201", 2),
                new HomeItem(R.drawable.newmain_task, "任务计划", TasksManageActivity_.class, "0202", 2),
                new HomeItem(R.drawable.newmain_report, "工作报告", WorkReportsManageActivity.class, "0203", 2),
                new HomeItem(R.drawable.newmain_wfin, "审批流程", WfInstanceManageActivity.class, "0204", 2),
                new HomeItem(R.drawable.newmain_attent, "考勤管理", AttendanceActivity_.class, "0211", 2)));
    }

    /**
     * 显示弹出菜单
     */
    void showMoreWindow(View view) {
        if (null == mMoreWindow) {
            mMoreWindow = new MoreWindow(this, mHandler);
            mMoreWindow.init();
        }
        mMoreWindow.showMoreWindow(view);
    }

    /**
     * 获取首页红点数据
     */
    void requestNumber() {
        cancelLoading();//列表出现消失dialog
        RestAdapterFactory.getInstance().build(Config_project.MAIN_RED_DOT).create(IMain.class).
                getNumber(new RCallback<ArrayList<HttpMainRedDot>>() {
                    @Override
                    public void success(final ArrayList<HttpMainRedDot> homeNumbers, final Response response) {
                        HttpErrorCheck.checkResponse("a首页红点", response);

                        mItemNumbers = removalRedNumber(homeNumbers);
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
     * 首页红点 数据去重重复的
     *
     * @param oldData
     * @return
     */
    private ArrayList<HttpMainRedDot> removalRedNumber(ArrayList<HttpMainRedDot> oldData) {
        HashSet<Integer> set = new HashSet<>();
        ArrayList<HttpMainRedDot> newList = new ArrayList();
        for (HttpMainRedDot element : oldData) {
            if (set.add(element.bizType))
                newList.add(element);
        }
        return newList;
    }


    /**
     * 获取可用的考勤
     *
     * @return
     */
    private ValidateItem availableValidateItem() {
        ValidateItem validateItem = null;
        for (int i = 0; i < validateInfo.getValids().size(); i++) {
            validateItem = validateInfo.getValids().get(i);
            if (validateItem.isEnable() && !validateItem.ischecked()) {
                break;
            }
        }
        return validateItem;
    }

    //高德定位回调
    @Override
    public void OnLocationGDSucessed(final String address, final double longitude, final double latitude, final String radius) {
        map.put("originalgps", longitude + "," + latitude);
        LogUtil.d("经纬度:" + MainApp.gson.toJson(map));
        showLoading("");
        app.getRestAdapter().create(IAttendance.class).checkAttendance(map, new RCallback<AttendanceRecord>() {
            @Override
            public void success(final AttendanceRecord attendanceRecord, final Response response) {
                attendanceRecords = attendanceRecord;
                HttpErrorCheck.checkResponse("考勤信息：", response);
                attendanceRecord.setAddress(TextUtils.isEmpty(address) ? "没有获取到有效地址" : address);
                if (attendanceRecord.getState() == 3) {
                    attanceWorry();
                } else {
                    intentValue();
                }
            }

            @Override
            public void failure(final RetrofitError error) {
                super.failure(error);
                HttpErrorCheck.checkError(error);
            }
        });
        LocationUtilGD.sotpLocation();
    }

    /**
     * 定位失败
     */
    @Override
    public void OnLocationGDFailed() {
        LocationUtilGD.sotpLocation();
        cancelLoading();
        Toast("获取打卡位置失败");
    }


    void startAttanceLocation() {
        ValidateItem validateItem = availableValidateItem();
        if (null == validateItem) {
            return;
        }
        int type = validateItem.getType();
        map.clear();
        map.put("inorout", type);
        new LocationUtilGD(NewMainActivity.this, this);
    }

    /**
     * 跳转考勤界面，封装数据
     */
    public void intentValue() {
        Intent intent = new Intent(NewMainActivity.this, AttendanceAddActivity_.class);
        intent.putExtra("mAttendanceRecord", attendanceRecords);
        intent.putExtra("needPhoto", validateInfo.isNeedPhoto());
        intent.putExtra("isPopup", validateInfo.isPopup());
        intent.putExtra("outKind", outKind);
        intent.putExtra("serverTime", validateInfo.getServerTime());
        intent.putExtra("extraWorkStartTime", attendanceRecords.getExtraWorkStartTime());
        startActivityForResult(intent, FinalVariables.REQUEST_CHECKIN_ATTENDANCE);
    }


    /**
     * 早退提示
     */
    public void attanceWorry() {
        showGeneralDialog(false, true, getString(R.string.app_attanceworry_message));
        //确认
        generalPopView.setSureOnclick(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                generalPopView.dismiss();
                intentValue();
            }
        });
        //取消
        generalPopView.setCancelOnclick(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                generalPopView.dismiss();
            }
        });
    }


    /**
     * 判断上班下班
     */
    public void dealInOutWork() {
        /*上班*/
        if (inEnable) {
            outKind = 0;
            startAttanceLocation();
            /*下班*/
        } else if (outEnable) {
            outKind = 1;
            startAttanceLocation();
        }
    }

    /**
     * 加班提示框
     */
    public void popOutToast() {
        final AttenDancePopView popView = new AttenDancePopView(this);
        popView.show();
        popView.setCanceledOnTouchOutside(true);

        /*正常下班*/
        popView.generalOutBtn(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                outKind = 1;
                startAttanceLocation();
                popView.dismiss();
            }
        });

       /*完成加班*/
        popView.finishOutBtn(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                outKind = 2;
                startAttanceLocation();
                popView.dismiss();
            }
        });

        /*取消*/
        popView.cancels(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                popView.dismiss();
            }
        });
    }

    private void setAttendance() {
        if (null == validateInfo) {
            return;
        }

        if (!Global.isConnected()) {
            Toast("没有网络连接，不能打卡");
            return;
        }
        /*工作日*/
        if (validateInfo.isWorkDay()) {
            /*加班*/
            if (validateInfo.isPopup() && LocationUtilGD.permissionLocation()) {
                popOutToast();
                /*不加班*/
            } else {
                dealInOutWork();
            }
            /*非工作日，下班状态*/
        } else if (!validateInfo.isWorkDay() && outEnable) {
            outKind = 2;
            startAttanceLocation();
            /*非工作日，上班状态*/
        } else if (!validateInfo.isWorkDay() && inEnable) {
            outKind = 0;
            startAttanceLocation();
        }
    }


    /**
     * 获取能否打卡的信息
     */
    private void getValidateInfo() {
        showLoading("加载中...");
        app.getRestAdapter().create(IAttendance.class).validateAttendance(new RCallback<ValidateInfo>() {
            @Override
            public void success(final ValidateInfo _validateInfo, final Response response) {
                HttpErrorCheck.checkResponse("考勤信息:", response);
                if (null == _validateInfo) {
                    Toast("获取考勤信息失败");
                    return;
                }
                validateInfo = _validateInfo;
                for (ValidateItem validateItem : validateInfo.getValids()) {
                    if (validateItem.getType() == 1) {
                        inEnable = validateItem.isEnable();
                    } else if (validateItem.getType() == 2) {
                        outEnable = validateItem.isEnable();
                    }
                }

                if (inEnable || outEnable) {
                    setAttendance();
                }
                //已打卡完毕 跳转考勤列表
                else {
                    Toast("您今天已经打卡完毕");
                    Intent intent = new Intent(NewMainActivity.this, AttendanceActivity_.class);
                    startActivity(intent);
                }
            }

            @Override
            public void failure(final RetrofitError error) {
                super.failure(error);
                HttpErrorCheck.checkError(error);
            }
        });
    }

    /**
     * 退出应用
     */
    @Override
    public void onBackPressed() {
        showGeneralDialog(true, true, getString(R.string.app_exit_message));
        //确定
        generalPopView.setSureOnclick(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                generalPopView.dismiss();
                //android 5.0以后不能隐式启动或关闭服务
                if (mIntentCheckUpdate != null) {
                    try {
                        stopService(mIntentCheckUpdate);
                    } catch (Exception ex) {
                        Global.ProcException(ex);
                    }
                }

                android.os.Process.killProcess(android.os.Process.myPid());
            }
        });

        //取消
        generalPopView.setCancelOnclick(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                generalPopView.dismiss();
            }
        });
    }

    /**
     * 企业QQ登录的用户绑定手机号码
     */
    public void isQQLogin() {
        if (app.isQQLogin && TextUtils.isEmpty(MainApp.user.mobile)) {
            showGeneralDialog(false, true, getString(R.string.app_homeqq_message));
            //确认
            generalPopView.setSureOnclick(new View.OnClickListener() {
                @Override
                public void onClick(final View view) {
                    generalPopView.dismiss();
                    app.startActivity(NewMainActivity.this, ActivityEditUserMobile.class, MainApp.ENTER_TYPE_RIGHT, false, null);
                }
            });
            //取消
            generalPopView.setCancelOnclick(new View.OnClickListener() {
                @Override
                public void onClick(final View view) {
                    generalPopView.dismiss();
                }
            });
        }
        app.isQQLogin = false;
    }

    /**
     * 给激光推送 设置别名
     */
    public void setJpushAlias() {
        if (null == MainApp.user || isJPus) {
            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    LogUtil.d(" Jpush user kongkong 空空");
                    setJpushAlias();
                }
            }, 5000);
            return;
        }
        if (null == companyTag) {
            companyTag = new HashSet<String>();
            companyTag.add(MainApp.user.companyId);
        }
        JPushInterface.setAlias(this, MainApp.user.id, new TagAliasCallback() {
            @Override
            public void gotResult(final int i, final String s, final Set<String> set) {
                if (i != 0) {
                    isJPus = true;
                    setJpushAlias();
                }
                isQQLogin();
            }
        });
        JPushInterface.setTags(this, companyTag, new TagAliasCallback() {
            @Override
            public void gotResult(int i, String s, Set<String> set) {
            }
        });
    }

    /**
     * 激光推送要跳转 的 页面
     * buzzType 1，任务2，报告3，审批 4，项目  5，通知公告
     */
    public void intentJpushInfo() {
        if (null != MainApp.jpushData) {
            Intent intent = new Intent();
            if ("discuss".equals(MainApp.jpushData.operationType)) {
                intent.setClass(NewMainActivity.this, ActivityHait.class);//推送讨论
                startActivity(intent);
                MainApp.jpushData = null;
                return;
            } else if ("delete".equals(MainApp.jpushData.operationType)) {
                MainApp.jpushData = null;
                return;
            }
            switch (MainApp.jpushData.buzzType) {
                case 1:
                    intent.setClass(NewMainActivity.this, TasksInfoActivity_.class);
                    intent.putExtra(ExtraAndResult.EXTRA_ID, MainApp.jpushData.buzzId);
                    startActivity(intent);
                    MainApp.jpushData = null;
                    break;
                case 2:
                    intent.setClass(NewMainActivity.this, WorkReportsInfoActivity_.class);
                    intent.putExtra(ExtraAndResult.EXTRA_ID, MainApp.jpushData.buzzId);
                    startActivity(intent);
                    MainApp.jpushData = null;
                    break;
                case 3:
                    intent.setClass(NewMainActivity.this, WfinstanceInfoActivity_.class);
                    intent.putExtra(ExtraAndResult.EXTRA_ID, MainApp.jpushData.buzzId);
                    startActivity(intent);
                    MainApp.jpushData = null;
                    break;
                case 4:
                    intent.setClass(NewMainActivity.this, ProjectInfoActivity_.class);
                    intent.putExtra("projectId", MainApp.jpushData.buzzId);
                    startActivity(intent);
                    MainApp.jpushData = null;
                    break;
                case 5://通知公告
                    intent.setClass(NewMainActivity.this, BulletinManagerActivity_.class);
                    startActivity(intent);
                    MainApp.jpushData = null;
                    break;
                case 6://客户详情
                    intent.setClass(NewMainActivity.this, CustomerDetailInfoActivity_.class);
                    intent.putExtra("Id", MainApp.jpushData.buzzId);
                    intent.putExtra(ExtraAndResult.EXTRA_TYPE, 1);//默认我的客户
                    startActivity(intent);
                    MainApp.jpushData = null;
                    break;
                default:
                    break;
            }
        }
    }


    /**
     * 检查定位权限是否打开
     */
    private void permissionLocation() {
        if (PackageManager.PERMISSION_GRANTED ==
                getPackageManager().checkPermission("android.permission.ACCESS_FINE_LOCATION", "com.loyo.oa.v2")) {
        } else {
            ActivityCompat.requestPermissions(NewMainActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    1);
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //设置
            case R.id.newhome_heading_img:
                app.startActivity(this, SettingActivity.class, MainApp.ENTER_TYPE_RIGHT, false, null);
                break;
            //更多
            case R.id.btn_add:
                showMoreWindow(v);
                break;
        }
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
    public void onPullDownToRefresh(PullToRefreshBase refreshView) {
        initData();
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase refreshView) {

    }
}
