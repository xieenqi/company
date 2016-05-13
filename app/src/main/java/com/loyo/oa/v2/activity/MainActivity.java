package com.loyo.oa.v2.activity;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.ConnectivityManager;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activity.attendance.AttendanceActivity_;
import com.loyo.oa.v2.activity.attendance.AttendanceAddActivity_;
import com.loyo.oa.v2.activity.contact.ContactsActivity;
import com.loyo.oa.v2.activity.customer.CustomerAddActivity_;
import com.loyo.oa.v2.activity.customer.CustomerDetailInfoActivity_;
import com.loyo.oa.v2.activity.customer.CustomerManageActivity_;
import com.loyo.oa.v2.activity.discuss.ActivityMyDiscuss;
import com.loyo.oa.v2.activity.discuss.hait.ActivityHait;
import com.loyo.oa.v2.activity.login.LoginActivity;
import com.loyo.oa.v2.activity.project.ProjectInfoActivity_;
import com.loyo.oa.v2.activity.project.ProjectManageActivity_;
import com.loyo.oa.v2.activity.setting.ActivityEditUserMobile;
import com.loyo.oa.v2.activity.setting.SettingActivity;
import com.loyo.oa.v2.activity.signin.SignInActivity;
import com.loyo.oa.v2.activity.signin.SignInManagerActivity_;
import com.loyo.oa.v2.activity.tasks.TasksAddActivity_;
import com.loyo.oa.v2.activity.tasks.TasksInfoActivity_;
import com.loyo.oa.v2.activity.tasks.TasksManageActivity_;
import com.loyo.oa.v2.activity.wfinstance.WfInstanceAddActivity_;
import com.loyo.oa.v2.activity.wfinstance.WfInstanceManageActivity;
import com.loyo.oa.v2.activity.wfinstance.WfinstanceInfoActivity_;
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
import com.loyo.oa.v2.tool.DateTool;
import com.loyo.oa.v2.tool.LocationUtilGD;
import com.loyo.oa.v2.tool.LogUtil;
import com.loyo.oa.v2.tool.RCallback;
import com.loyo.oa.v2.tool.RestAdapterFactory;
import com.loyo.oa.v2.tool.StringUtil;
import com.loyo.oa.v2.tool.Utils;
import com.loyo.oa.v2.tool.customview.AttenDancePopView;
import com.loyo.oa.v2.tool.customview.RippleView;
import com.loyo.oa.v2.tool.customview.dragSortListView.DragSortListView;
import com.loyo.oa.v2.tool.customview.popumenu.PopupMenu;
import com.loyo.oa.v2.tool.customview.popumenu.PopupMenuItem;
import com.nineoldandroids.view.ViewHelper;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.umeng.analytics.MobclickAgent;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
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

@EActivity(R.layout.activity_main)
public class MainActivity extends BaseActivity implements PopupMenu.OnPopupMenuDismissListener,
        PopupMenu.OnPopupMenuItemClickListener, LocationUtilGD.AfterLocation {

    @ViewById(R.id.tv_title_1)
    TextView tv_user_name;
    @ViewById(R.id.img_title_left)
    ImageView img_user;
    @ViewById
    DragSortListView lv_main;
    @ViewById
    SwipeRefreshLayout swipe_container;
    @ViewById
    ViewGroup layout_network, layout_attendance, layout_avatar, layout_is_attendance, container;
    @ViewById
    ImageView img_home_head, img_fast_add, img_bulletinStatus;
    @ViewById
    RelativeLayout group_home_relative;
    @ViewById
    ImageButton img_contact;

    private Intent mIntentCheckUpdate;
    private ArrayList<HttpMainRedDot> mItemNumbers = new ArrayList<>();
    private MHandler mHandler;
    private boolean mInitData;
    private ClickItemAdapter adapter;
    private PopupMenu popupMenu;
    private ValidateInfo validateInfo = new ValidateInfo();
    private AttendanceRecord attendanceRecords = new AttendanceRecord();
    private HashMap<String, Object> map = new HashMap<>();
    private Boolean inEnable;
    private Boolean outEnable;
    private int outKind; //0上班  1下班  2加班
    private Set<String> companyTag;

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
    private ArrayList<ClickItem> items = new ArrayList<>();

    //显示通知公告红点
    public Handler handler = new Handler() {
        @Override
        public void dispatchMessage(final Message msg) {
            super.dispatchMessage(msg);
            switch (msg.what) {
                case ExtraAndResult.MSG_WHAT_VISIBLE:
                    img_bulletinStatus.setVisibility(View.VISIBLE);
                    break;
                case ExtraAndResult.MSG_WHAT_GONG:
                    img_bulletinStatus.setVisibility(View.GONE);
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    public void onPopupMenuDismiss() {
        img_fast_add.setImageResource(R.drawable.icon_home_add);
    }

    /**
     * 主界面弹窗  新建任务  提交报告 审批申请 添加客户 拜访签到
     * xnq
     *
     * @param position
     * @param item
     */
    @Override
    public void onPopupMenuItemClick(final int position, final PopupMenuItem item) {

        Class<?> _class = null;
        switch (position) {

            case 0:
                _class = CustomerAddActivity_.class;
                break;
            case 1:
                _class = SignInActivity.class;
                break;
            case 2:
                _class = TasksAddActivity_.class;
                break;
            case 3:
                _class = WorkReportAddActivity_.class;
                break;
            case 4:
                _class = WfInstanceAddActivity_.class;
                break;
            default:
                break;

        }

        app.startActivityForResult(this, _class, MainApp.ENTER_TYPE_RIGHT, 1, null);

    }

    @Override
    public void onPopupMenuItemLongClick(final int position, final PopupMenuItem item) {
    }

    private static class MHandler extends Handler {
        private WeakReference<MainActivity> mActivity;

        private MHandler(final MainActivity activity) {
            mActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(final Message msg) {
            super.handleMessage(msg);
            mActivity.get().swipe_container.setRefreshing(false);
        }
    }

    protected void onNetworkChanged(final boolean available) {
        if (null != layout_network) {
            layout_network.setVisibility(available ? View.GONE : View.VISIBLE);
        }
    }

    protected void registerBaseReceiver() {
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(baseReceiver, filter);
    }

    protected void unRegisterBaseReceiver() {
        unregisterReceiver(baseReceiver);
    }


    //刷新数据
    private void onRefresh() {
        if (!StringUtil.isEmpty(MainApp.getToken())) {
            requestNumber();
        } else {
            mHandler.sendEmptyMessageDelayed(0, 1000);
        }
    }

    private DragSortListView.DropListener onDrag = new DragSortListView.DropListener() {
        @Override
        public void drop(final int from, final int to) {
            if (from != to) {
                ClickItem item = adapter.getItem(from);
                adapter.remove(from);
                adapter.insert(item, to);
            }
        }
    };

    @AfterViews
    void init() {
        showLoading("加载中...");
        LogUtil.d(" 获得main现有的token：" + MainApp.getToken());
        setTouchView(-1);
        Global.SetTouchView(findViewById(R.id.img_contact), findViewById(R.id.img_bulletin),
                findViewById(R.id.img_setting),
                findViewById(R.id.img_fast_add));
        if (StringUtil.isEmpty(MainApp.getToken())) {
            app.toActivity(LoginActivity.class);
            return;
        }
        layout_network.setVisibility(Global.isConnected() ? View.GONE : View.VISIBLE);
        swipe_container.setColorSchemeColors(Color.parseColor("#4db1fe"));
        //首页刷新监听R.color.title_bg1, R.color.greenyellow, R.color.aquamarine
        swipe_container.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipe_container.setRefreshing(true);
                MainActivity.this.onRefresh();
            }
        });

        handlerEvent();
        checkUpdateService();
        updateUser();


        lv_main.setDropListener(onDrag);
        lv_main.setMaxScrollSpeed(100f);
        adapter = new ClickItemAdapter();

    }

    /**
     * 初始化UI处理器
     */
    private void handlerEvent() {
        LocalBroadcastManager.getInstance(this).registerReceiver(mReceiver, new IntentFilter(FinalVariables.ACTION_DATA_CHANGE));
        mHandler = new MHandler(this);
    }

    /**
     * 版本更新检查
     */
    @Background
    void checkUpdateService() {
        mIntentCheckUpdate = new Intent(mContext, CheckUpdateService.class);
        startService(mIntentCheckUpdate);
    }

    /**
     * 显示用户名字和部门的名字,头像，高斯模糊背景处理
     */
    void updateUser() {
        items = new ArrayList<>(Arrays.asList(new ClickItem(R.drawable.icon_home_customer, "客户管理", CustomerManageActivity_.class, "0205"),
                new ClickItem(R.drawable.icon_home_signin, "客户拜访", SignInManagerActivity_.class, "0206"),
                new ClickItem(R.drawable.icon_home_project, "项目管理", ProjectManageActivity_.class, "0201"),
                new ClickItem(R.drawable.home_task, "任务计划", TasksManageActivity_.class, "0202"),
                new ClickItem(R.drawable.icon_home_report, "工作报告", WorkReportsManageActivity.class, "0203"),
                new ClickItem(R.drawable.icon_home_wfinstance, "审批流程", WfInstanceManageActivity.class, "0204"),
                new ClickItem(R.drawable.icon_home_attendance, "考勤管理", AttendanceActivity_.class, "0211"),
                new ClickItem(R.drawable.ic_home_message, "我的讨论", ActivityMyDiscuss.class, "0")));

        if (MainApp.user == null) {
            return;
        }
        if (null == MainApp.user.avatar || MainApp.user.avatar.isEmpty() || !MainApp.user.avatar.contains("http")) {
            int defaultAcatar;
            if (MainApp.user.gender == 2) {
                defaultAcatar = R.drawable.icon_contact_avatar;
            } else {
                defaultAcatar = R.drawable.img_default_user;
            }
            img_user.setImageResource(defaultAcatar);
            Bitmap bitmap = BitmapFactory.decodeResource(mContext.getResources(), defaultAcatar);
            Bitmap blur = Utils.doBlur(bitmap, 50, false);
            img_home_head.setImageResource(android.R.color.transparent);
            container.setBackgroundDrawable(new BitmapDrawable(blur));
        } else {
            ImageLoader.getInstance().displayImage(MainApp.user.avatar, img_user);
            ImageLoader.getInstance().displayImage(MainApp.user.avatar, img_home_head, new ImageLoadingListener() {
                @Override
                public void onLoadingStarted(final String s, final View view) {

                }

                @Override
                public void onLoadingFailed(final String s, final View view, final FailReason failReason) {

                }

                @Override
                public void onLoadingComplete(final String s, final View view, final Bitmap bitmap) {
                    if (null != bitmap) {
                        Bitmap blur = Utils.doBlur(bitmap, 50, false);
                        img_home_head.setImageResource(android.R.color.transparent);
                        container.setBackgroundDrawable(new BitmapDrawable(blur));
                        testJurl();
                    }
                }

                @Override
                public void onLoadingCancelled(final String s, final View view) {

                }
            });
        }
        tv_user_name.setText(MainApp.user.getRealname());
        initPopupMenu();
    }

    /**
     * 初始化弹出菜单
     */
    private void initPopupMenu() {
        popupMenu = new PopupMenu(this);
        popupMenu.setBackGroundResId(R.drawable.icon_home_menubg);
        popupMenu.setMenuItems(getPopupMenus());
        popupMenu.setDismissListener(this);
        popupMenu.setListener(this);
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
                        new Handler().post(new Runnable() {
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

            try {
                img_contact.setVisibility(((Permission) MainApp.rootMap.get("0213")).isEnable() ? View.VISIBLE : View.GONE);
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        }

        //为了业务使用权限
        lv_main.setAdapter(adapter);
        lv_main.setDragEnabled(true);
        cancelLoading();
    }

    /**
     * 给激光推送 设置别名
     */
    public void setJpushAlias() {
        //给激光推送 设置别名
        if (null == MainApp.user) {
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
                    setJpushAlias();
                }
//                LogUtil.d(MainApp.user + " 激光的alias： " + s + "  状态" + i);
                isQQLogin();
            }
        });
        JPushInterface.setTags(this, companyTag, new TagAliasCallback() {
            @Override
            public void gotResult(int i, String s, Set<String> set) {
//                LogUtil.d(MainApp.user.companyId + " 激光的tags： " + "  状态:" + i);
            }
        });
    }


    /**
     * 获取弹出菜单条目
     *
     * @return
     */
    private ArrayList<PopupMenuItem> getPopupMenus() {
        ArrayList<PopupMenuItem> menuObjects = new ArrayList<>();

        PopupMenuItem task = new PopupMenuItem(this);
        task.setText("新建任务");
        task.setResource(R.drawable.icon_home_menu_task);

        PopupMenuItem report = new PopupMenuItem(this);
        report.setText("提交报告");
        report.setResource(R.drawable.icon_home_menu_report);

        PopupMenuItem wfinstance = new PopupMenuItem(this);
        wfinstance.setText("审批申请");
        wfinstance.setResource(R.drawable.icon_home_menu_wfinstance);

        PopupMenuItem customer = new PopupMenuItem(this);
        customer.setText("添加客户");
        customer.setResource(R.drawable.icon_home_menu_customer);

        PopupMenuItem signin = new PopupMenuItem(this);
        signin.setText("拜访签到");
        signin.setResource(R.drawable.icon_home_menu_signin);


        for (ClickItem clickItem : items) {
            if (clickItem.title.contains("任务计划")) {
                menuObjects.add(task);
            } else if (clickItem.title.contains("工作报告")) {
                menuObjects.add(report);
            } else if (clickItem.title.contains("审批流程")) {
                menuObjects.add(wfinstance);
            } else if (clickItem.title.contains("客户管理")) {
                menuObjects.add(customer);
            } else if (clickItem.title.contains("客户拜访")) {
                menuObjects.add(signin);
            }
        }

        return menuObjects;
    }

    /*****************************考勤相关操作*****************************/

    /**
     * 获取能否打卡的信息
     */
    private void getValidateInfo() {
        showLoading("加载中...");
        app.getRestAdapter().create(IAttendance.class).validateAttendance(new RCallback<ValidateInfo>() {
            @Override
            public void success(final ValidateInfo _validateInfo, final Response response) {
                HttpErrorCheck.checkResponse(response);
                if (null == _validateInfo) {
                    Toast("获取考勤信息失败");
                    return;
                }
                validateInfo = _validateInfo;
                try {
                    LogUtil.d("考勤信息:" + Utils.convertStreamToString(response.getBody().in()));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                for (ValidateItem validateItem : validateInfo.getValids()) {
                    if (validateItem.getType() == 1) {
                        inEnable = validateItem.isEnable();
                    } else if (validateItem.getType() == 2) {
                        outEnable = validateItem.isEnable();
                    }
                }
                rotateInt();
            }

            @Override
            public void failure(final RetrofitError error) {
                super.failure(error);
                HttpErrorCheck.checkError(error);
            }
        });
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

    @Click(R.id.layout_is_attendance)
    void onClickIsAttendance() {
        Toast("您今天已经打卡完毕");
        Intent intent = new Intent(MainActivity.this, AttendanceActivity_.class);
        startActivity(intent);
    }

    void startAttanceLocation() {
        showLoading("");
        ValidateItem validateItem = availableValidateItem();
        if (null == validateItem) {
            return;
        }
        int type = validateItem.getType();
        map.clear();
        map.put("inorout", type);
        new LocationUtilGD(MainActivity.this, this);
    }

    //高德定位回调
    @Override
    public void OnLocationGDSucessed(final String address, final double longitude, final double latitude, final String radius) {
        map.put("originalgps", longitude + "," + latitude);
        LogUtil.d("经纬度:" + MainApp.gson.toJson(map));
        app.getRestAdapter().create(IAttendance.class).checkAttendance(map, new RCallback<AttendanceRecord>() {
            @Override
            public void success(final AttendanceRecord attendanceRecord, final Response response) {
                cancelLoading();
                attendanceRecords = attendanceRecord;
//                try {
//                    LogUtil.d("check:" + Utils.convertStreamToString(response.getBody().in()));
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
                HttpErrorCheck.checkResponse("考勤信息：",response);
                attendanceRecord.setAddress(address);

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


    @Override
    public void OnLocationGDFailed() {
        LocationUtilGD.sotpLocation();
        cancelLoading();
        Toast("获取打卡位置失败");
    }

    /**
     * 跳转考勤界面，封装数据
     */
    public void intentValue() {
        Intent intent = new Intent(MainActivity.this, AttendanceAddActivity_.class);
        intent.putExtra("mAttendanceRecord", attendanceRecords);
        intent.putExtra("needPhoto", validateInfo.isNeedPhoto());
        intent.putExtra("isPopup", validateInfo.isPopup());
        intent.putExtra("outKind", outKind);
        intent.putExtra("serverTime", validateInfo.getServerTime());
        intent.putExtra("extraWorkStartTime", attendanceRecords.getExtraWorkStartTime());
        startActivityForResult(intent, FinalVariables.REQUEST_CHECKIN_ATTENDANCE);
    }


    /**
     * (翻转前)点击头像，获取能否打卡信息
     */
    @Click(R.id.img_title_left)
    void onClickAvatar() {
        getValidateInfo();
    }

    /**
     * (翻转后)点击打卡,准备跳转新建考勤
     */
    @Click(R.id.layout_attendance)
    void onClickAttendance() {

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
            if (validateInfo.isPopup()) {
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
     * 头像翻转
     */
    private void rotateInt() {
        //为什么参数必须为小数才可以？
        //解决翻转过后文本也被翻转
        ViewHelper.setPivotX(layout_attendance, layout_attendance.getWidth() / 2);
        ViewHelper.setPivotY(layout_attendance, layout_attendance.getHeight() / 2);
        ViewHelper.setRotationY(layout_attendance, 180f);

        layout_avatar.setPivotX(layout_avatar.getWidth() / 2);
        layout_avatar.setPivotY(layout_avatar.getHeight() / 2);

        ObjectAnimator objectAnimator = new ObjectAnimator().ofFloat(img_user, "rotationY", 0f, 180f).setDuration(300);
        objectAnimator.setInterpolator(new LinearInterpolator());
        objectAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(final ValueAnimator valueAnimator) {
                float value = (float) valueAnimator.getAnimatedValue();
                layout_avatar.setRotationY(value);

                if (Math.round(value) >= 90) {
                    img_user.setVisibility(View.INVISIBLE);
                    if (inEnable || outEnable) {
                        layout_is_attendance.setVisibility(View.INVISIBLE);
                        layout_attendance.setVisibility(View.VISIBLE);
                    } else {
                        layout_attendance.setVisibility(View.INVISIBLE);
                        layout_is_attendance.setVisibility(View.VISIBLE);
                    }
                }
                if (Math.round(value) == 180) {
                    mHandler.postDelayed(rotateRunner, 5000);
                }
            }
        });
        objectAnimator.start();
    }

    private Runnable rotateRunner = new Runnable() {
        @Override
        public void run() {
            //为什么参数必须为小数才可以？
            layout_avatar.setPivotX(layout_avatar.getWidth() / 2);
            layout_avatar.setPivotY(layout_avatar.getHeight() / 2);

            ObjectAnimator objectAnimator = new ObjectAnimator().ofFloat(layout_attendance, "rotationY", 0f, -180f).setDuration(300);
            objectAnimator.setInterpolator(new LinearInterpolator());
            objectAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(final ValueAnimator valueAnimator) {
                    float value = (float) valueAnimator.getAnimatedValue();
                    layout_avatar.setRotationY(value);
                    if (Math.round(value) <= -90) {
                        img_user.setVisibility(View.VISIBLE);
                        if (inEnable || outEnable) {
                            layout_is_attendance.setVisibility(View.INVISIBLE);
                            layout_attendance.setVisibility(View.INVISIBLE);
                        } else {
                            layout_attendance.setVisibility(View.INVISIBLE);
                            layout_is_attendance.setVisibility(View.INVISIBLE);
                        }
                    }
                }
            });
            objectAnimator.start();
        }
    };


    /**
     * 到 【通讯录】  页面
     */
    @Click(R.id.img_contact)
    void onClickContact() {
        if (null != MainApp.lstDepartment) {
            app.startActivity(this, ContactsActivity.class, MainApp.ENTER_TYPE_RIGHT, false, null);
        } else {
            Toast("请重新拉去组织架构");
        }
    }

    /**
     * 到 【公告通知】 页面
     */
    @Click(R.id.img_bulletin)
    void onClickBulletin() {
        app.startActivity(this, BulletinManagerActivity_.class, MainApp.ENTER_TYPE_RIGHT, false, null);
    }

    /**
     * 添加 【popu弹窗】窗口
     */
    @Click(R.id.img_fast_add)
    void onClickAdd() {
        popupMenu.showAt(findViewById(R.id.img_fast_add));
        img_fast_add.setImageResource(R.drawable.icon_home_menu_close);
    }

    /**
     * 到 【设置】 页面8
     */
    @Click(R.id.img_setting)
    void onClickSetting() {
        app.startActivity(this, SettingActivity.class, MainApp.ENTER_TYPE_RIGHT, false, null);
    }


    class ViewHolder {
        RippleView layout_item;
        ImageView img_item;
        TextView tv_item;
        TextView tv_extra;
        ImageView view_number;
        TextView tv_num;
    }

    public class ClickItemAdapter extends BaseAdapter {
        LayoutInflater inflter;

        public ClickItemAdapter() {
            inflter = LayoutInflater.from(mContext);
        }

        @Override
        public int getCount() {
            return items.size();
        }

        @Override
        public ClickItem getItem(final int position) {
            return items.get(position);
        }

        @Override
        public long getItemId(final int position) {
            return position;
        }

        public void remove(final int arg0) {
            items.remove(arg0);
            notifyDataSetChanged();
        }

        public void insert(final ClickItem item, final int arg0) {
            items.add(arg0, item);
            notifyDataSetChanged();
        }

        @Override
        public View getView(final int position, View convertView, final ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = inflter.inflate(R.layout.item_main, null, false);
                holder.img_item = (ImageView) convertView.findViewById(R.id.img_item);
                holder.tv_item = (TextView) convertView.findViewById(R.id.tv_item);
                holder.tv_extra = (TextView) convertView.findViewById(R.id.tv_extra);
                holder.layout_item = (RippleView) convertView.findViewById(R.id.layout_item);
                holder.view_number = (ImageView) convertView.findViewById(R.id.view_number);
                holder.tv_num = (TextView) convertView.findViewById(R.id.tv_num);
                convertView.setTag(holder);

            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            final ClickItem item = getItem(position);

            for (HttpMainRedDot num : mItemNumbers) {//首页红点
                String extra = "";
                if ((item.title.equals("工作报告") && num.bizType == 1)) {
                    extra = num.bizNum + "个待点评(含抄送)";
                    holder.view_number.setVisibility(num.viewed ? View.GONE : View.VISIBLE);
                } else if ((item.title.equals("任务计划") && num.bizType == 2)) {
                    extra = num.bizNum + "个未完成";
                    holder.view_number.setVisibility(num.viewed ? View.GONE : View.VISIBLE);
                } else if ((item.title.equals("审批流程") && num.bizType == 12)) {
                    extra = num.bizNum + "个待审批";
                    holder.view_number.setVisibility(num.viewed ? View.GONE : View.VISIBLE);
                } else if ((item.title.equals("项目管理") && num.bizType == 5)) {
                    extra = num.bizNum + "个进行中";
                    holder.view_number.setVisibility(num.viewed ? View.GONE : View.VISIBLE);
                } else if ((item.title.equals("客户管理") && num.bizType == 6)) {
                    extra = num.bizNum + "个将掉公海";
                    holder.view_number.setVisibility(num.viewed ? View.GONE : View.VISIBLE);
                } else if ((item.title.equals("客户拜访") && num.bizType == 11)) {
                    extra = num.bizNum + "个需拜访";
                    holder.view_number.setVisibility(num.viewed ? View.GONE : View.VISIBLE);
                } else if ((item.title.equals("考勤管理") && num.bizType == 4)) {
                    extra = num.bizNum + "个外勤";
                    holder.view_number.setVisibility(num.viewed ? View.GONE : View.VISIBLE);
                } else if (num.bizType == 19) {
                    if (!num.viewed) {
                        handler.sendEmptyMessage(ExtraAndResult.MSG_WHAT_VISIBLE);
                    } else {
                        handler.sendEmptyMessage(ExtraAndResult.MSG_WHAT_GONG);
                    }
                }
                if (!TextUtils.isEmpty(extra)) {
                    holder.tv_extra.setText(extra);
                }
            }

            if (item.title.equals("工作报告")) {
                holder.tv_num.setVisibility(View.VISIBLE);
                SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                String date = sDateFormat.format(new java.util.Date());
                String dayNum = date.substring(8, 10);
                holder.tv_num.setText(dayNum);
            }

            holder.layout_item.setRippleDuration(100);
            holder.layout_item.setRippleColor(R.color.title_bg1);
            holder.img_item.setImageDrawable(getResources().getDrawable(item.imageViewRes));
            holder.tv_item.setText(item.title);
            holder.layout_item.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
                @Override
                public void onComplete(RippleView rippleView) {
                    app.startActivityForResult((Activity) mContext, item.cls, MainApp.ENTER_TYPE_RIGHT, 1, null);
                }
            });

            return convertView;
        }
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

    @Override
    protected void onStart() {
        super.onStart();
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


    void launch() {
        updateUser();
        startTrack();
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1) {
            requestNumber();
        }
    }

    //获取轨迹，并设置AlarmManager
    @Background
    void startTrack() {
        if (!Utils.isServiceRunning(AMapService.class.getName())) {
        }
        TrackRule.InitTrackRule();
    }


    @Background
    void initData() {
        Intent intent = new Intent(mContext, InitDataService_.class);
        startService(intent);
        setJpushAlias();
        requestNumber();
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
                    app.startActivity(MainActivity.this, ActivityEditUserMobile.class, MainApp.ENTER_TYPE_RIGHT, false, null);
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


    public class ClickItem {
        public int imageViewRes;
        public String title;
        public String code;
        public Class<?> cls;

        public ClickItem(final int _imageViewRes, final String _title, final Class<?> _cls, final String _code) {
            imageViewRes = _imageViewRes;
            title = _title;
            cls = _cls;
            code = _code;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        intentJpushInfo();
        requestNumber();
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onResume(this);
    }

    /**
     * 激光推送要跳转 的 页面
     * buzzType 1，任务2，报告3，审批 4，项目  5，通知公告
     */
    public void intentJpushInfo() {
        if (null != MainApp.jpushData) {
            Intent intent = new Intent();
            if ("discuss".equals(MainApp.jpushData.operationType)) {
                intent.setClass(MainActivity.this, ActivityHait.class);//推送讨论
                startActivity(intent);
                MainApp.jpushData = null;
                return;
            } else if ("delete".equals(MainApp.jpushData.operationType)) {
                MainApp.jpushData = null;
                return;
            }
            switch (MainApp.jpushData.buzzType) {
                case 1:
                    intent.setClass(MainActivity.this, TasksInfoActivity_.class);
                    intent.putExtra(ExtraAndResult.EXTRA_ID, MainApp.jpushData.buzzId);
                    startActivity(intent);
                    MainApp.jpushData = null;
                    break;
                case 2:
                    intent.setClass(MainActivity.this, WorkReportsInfoActivity_.class);
                    intent.putExtra(ExtraAndResult.EXTRA_ID, MainApp.jpushData.buzzId);
                    startActivity(intent);
                    MainApp.jpushData = null;
                    break;
                case 3:
                    intent.setClass(MainActivity.this, WfinstanceInfoActivity_.class);
                    intent.putExtra(ExtraAndResult.EXTRA_ID, MainApp.jpushData.buzzId);
                    startActivity(intent);
                    MainApp.jpushData = null;
                    break;
                case 4:
                    intent.setClass(MainActivity.this, ProjectInfoActivity_.class);
                    intent.putExtra("projectId", MainApp.jpushData.buzzId);
                    startActivity(intent);
                    MainApp.jpushData = null;
                    break;
                case 5://通知公告
                    intent.setClass(MainActivity.this, BulletinManagerActivity_.class);
                    //intent.putExtra(ExtraAndResult.EXTRA_ID, MainApp.jpushData.buzzId);
                    startActivity(intent);
                    MainApp.jpushData = null;
                    break;
                case 6://客户详情
                    intent.setClass(MainActivity.this, CustomerDetailInfoActivity_.class);
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

    @Override
    protected void onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mReceiver);
        super.onDestroy();
    }

    /**
     * 获取首页红点数据
     */
    void requestNumber() {
        RestAdapterFactory.getInstance().build(Config_project.MAIN_RED_DOT).create(IMain.class).
                getNumber(new RCallback<ArrayList<HttpMainRedDot>>() {
                    @Override
                    public void success(final ArrayList<HttpMainRedDot> homeNumbers, final Response response) {
                        HttpErrorCheck.checkResponse("首页红点", response);
                        mItemNumbers = homeNumbers;
                        adapter.notifyDataSetChanged();
                        mHandler.sendEmptyMessageDelayed(0, 500);
                    }

                    @Override
                    public void failure(final RetrofitError error) {
                        HttpErrorCheck.checkError(error);
                        super.failure(error);
                        mHandler.sendEmptyMessageDelayed(0, 500);
                    }
                });
    }

    /**
     * 检查定位权限是否打开
     */
    private void permissionLocation() {
        if (PackageManager.PERMISSION_GRANTED ==
                getPackageManager().checkPermission("android.permission.ACCESS_FINE_LOCATION", "com.loyo.oa.v2")) {
//            Toast(" 用户授权 ");
        } else {
//            final GeneralPopView generalPopView = new GeneralPopView(context, true);
//            generalPopView.show();
//            generalPopView.setMessage("需要使用定位权限\n请在”设置”>“应用”>“权限”中配置权限");
//            generalPopView.setCanceledOnTouchOutside(true);
//            showGeneralDialog(true, true, "需要使用定位权限\\n请在”设置”>“应用”>“权限”中配置权限");
//            generalPopView.setSureOnclick(new View.OnClickListener() {
//                @Override
//                public void onClick(final View view) {
//                    generalPopView.dismiss();
//                    ActivityCompat.requestPermissions(MainActivity.this,
//                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
//                            1);
//                }
//            });
//            generalPopView.setCancelOnclick(new View.OnClickListener() {
//                @Override
//                public void onClick(final View view) {
//                    generalPopView.dismiss();
//                }
//            });

            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    1);
        }
    }
}
