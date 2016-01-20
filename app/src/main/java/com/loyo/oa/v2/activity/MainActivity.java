package com.loyo.oa.v2.activity;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.ConnectivityManager;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activity.attendance.AttendanceActivity_;
import com.loyo.oa.v2.activity.attendance.AttendanceAddActivity_;
import com.loyo.oa.v2.activity.commonview.SettingActivity;
import com.loyo.oa.v2.activity.contact.ContactsActivity;
import com.loyo.oa.v2.activity.customer.CustomerAddActivity_;
import com.loyo.oa.v2.activity.customer.CustomerManageActivity_;
import com.loyo.oa.v2.activity.project.ProjectManageActivity_;
import com.loyo.oa.v2.activity.setting.ActivityEditUserMobile;
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
import com.loyo.oa.v2.beans.Modules;
import com.loyo.oa.v2.beans.TrackRule;
import com.loyo.oa.v2.beans.User;
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
import com.loyo.oa.v2.tool.LocationUtil;
import com.loyo.oa.v2.tool.LogUtil;
import com.loyo.oa.v2.tool.RCallback;
import com.loyo.oa.v2.tool.RestAdapterFactory;
import com.loyo.oa.v2.tool.StringUtil;
import com.loyo.oa.v2.tool.Utils;
import com.loyo.oa.v2.tool.customview.RippleView;
import com.loyo.oa.v2.tool.customview.dragSortListView.DragSortListView;
import com.loyo.oa.v2.tool.customview.popumenu.PopupMenu;
import com.loyo.oa.v2.tool.customview.popumenu.PopupMenuItem;
import com.nineoldandroids.view.ViewHelper;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;


import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;
import retrofit.RetrofitError;
import retrofit.client.Response;

@EActivity(R.layout.activity_main)
public class MainActivity extends BaseActivity implements PopupMenu.OnPopupMenuDismissListener,
        PopupMenu.OnPopupMenuItemClickListener, LocationUtil.AfterLocation {

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
    ImageView img_home_head, img_fast_add;
    @ViewById
    RelativeLayout group_home_relative;

    private Intent mIntentCheckUpdate;
    private ArrayList<HttpMainRedDot> mItemNumbers = new ArrayList<>();
    private MHandler mHandler;
    private boolean mInitData;
    private boolean isSign;
    private ArrayList<ClickItem> items = new ArrayList<>();
    private ClickItemAdapter adapter;
    private PopupMenu popupMenu;
    private ValidateInfo validateInfo;
    private HashMap<String, Object> map = new HashMap<>();
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (null == intent) {
                return;
            }
            String action = intent.getAction();
            if (TextUtils.equals(action, FinalVariables.ACTION_DATA_CHANGE)) {
                launch();
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
    public void onPopupMenuItemClick(int position, PopupMenuItem item) {

        Class<?> _class = null;
        switch (position) {

            case 0:
                _class = TasksAddActivity_.class;
                break;
            case 1:
                _class = WorkReportAddActivity_.class;
                break;
            case 2:
                _class = WfInstanceAddActivity_.class;
                break;
            case 3:
                _class = CustomerAddActivity_.class;
                break;
            case 4:
                _class = SignInActivity.class;
                break;

        }

        app.startActivityForResult(this, _class, MainApp.ENTER_TYPE_RIGHT, 1, null);

    }

    @Override
    public void onPopupMenuItemLongClick(int position, PopupMenuItem item) {
    }

    private static class MHandler extends Handler {
        private WeakReference<MainActivity> mActivity;

        private MHandler(MainActivity activity) {
            mActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            mActivity.get().swipe_container.setRefreshing(false);
        }
    }

    protected void onNetworkChanged(boolean available) {
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

    /**
     * 初始化UI处理器
     */
    private void handlerEvent() {
        LocalBroadcastManager.getInstance(this).registerReceiver(mReceiver, new IntentFilter(FinalVariables.ACTION_DATA_CHANGE));
        mHandler = new MHandler(this);
    }

    private void onRefresh() {
        if (!StringUtil.isEmpty(MainApp.getToken())) {
            requestNumber();
        } else {
            mHandler.sendEmptyMessageDelayed(0, 1000);
        }
    }

    private DragSortListView.DropListener onDrag = new DragSortListView.DropListener() {
        @Override
        public void drop(int from, int to) {
            if (from != to) {
                ClickItem item = adapter.getItem(from);
                adapter.remove(from);
                adapter.insert(item, to);
            }
        }
    };

    @AfterViews
    void init() {

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
        items = new ArrayList<>(Arrays.asList(new ClickItem(R.drawable.icon_home_customer, "客户管理", CustomerManageActivity_.class),
                new ClickItem(R.drawable.icon_home_signin, "客户拜访", SignInManagerActivity_.class),
                new ClickItem(R.drawable.icon_home_project, "项目管理", ProjectManageActivity_.class),
                new ClickItem(R.drawable.home_task, "任务计划", TasksManageActivity_.class),
                new ClickItem(R.drawable.icon_home_report, "工作报告", WorkReportsManageActivity.class),
                new ClickItem(R.drawable.icon_home_wfinstance, "审批流程", WfInstanceManageActivity.class),
                new ClickItem(R.drawable.icon_home_attendance, "考勤管理", AttendanceActivity_.class)));


        swipe_container.setColorSchemeColors(R.color.title_bg1, R.color.greenyellow, R.color.aquamarine);
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
        initPopupMenu();

        lv_main.setDropListener(onDrag);
        adapter = new ClickItemAdapter();
        lv_main.setAdapter(adapter);
        lv_main.setDragEnabled(true);

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
        JPushInterface.setAlias(this, MainApp.user.id, new TagAliasCallback() {
            @Override
            public void gotResult(int i, String s, Set<String> set) {
                if (i != 0) {
                    setJpushAlias();
                }
                LogUtil.d(MainApp.user + " 激光的alias： " + s);
                isQQLogin();
            }
        });
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

        for(ClickItem clickItem : items){
            if(clickItem.title.contains("客户管理")){
                menuObjects.add(customer);
            }
            else if(clickItem.title.contains("任务计划")){
                menuObjects.add(task);
            }
            else if(clickItem.title.contains("审批流程")){
                menuObjects.add(wfinstance);
            }
            else if(clickItem.title.contains("客户拜访")){
                menuObjects.add(signin);
            }
            else if(clickItem.title.contains("工作报告")){
                menuObjects.add(report);
            }
        }

        return menuObjects;
    }

    /**
     * 定位成功后，跳转新建考勤回调
     */
    @Override
    public void OnLocationFailed() {
        cancelLoading();
        Toast("获取打卡位置失败");
    }

    @Override
    public void OnLocationSucessed(final String address, double longitude, double latitude, float radius) {
        map.put("originalgps", longitude + "," + latitude);
        app.getRestAdapter().create(IAttendance.class).checkAttendance(map, new RCallback<AttendanceRecord>() {
            @Override
            public void success(AttendanceRecord attendanceRecord, Response response) {
                cancelLoading();
                attendanceRecord.setAddress(address);
                Intent intent = new Intent(MainActivity.this, AttendanceAddActivity_.class);
                intent.putExtra("mAttendanceRecord", attendanceRecord);
                startActivityForResult(intent, FinalVariables.REQUEST_CHECKIN_ATTENDANCE);
            }

            @Override
            public void failure(RetrofitError error) {
                Toast("服务器连接失败,请检查网络" + error.getMessage());
                super.failure(error);
                cancelLoading();
            }
        });
    }

    /**
     * 获取能否打卡的信息
     */
    private void getValidateInfo() {
        app.getRestAdapter().create(IAttendance.class).validateAttendance(new RCallback<ValidateInfo>() {
            @Override
            public void success(ValidateInfo _validateInfo, Response response) {
                if (null == _validateInfo) {
                    Toast("获取考勤信息失败");
                    return;
                }

                validateInfo = _validateInfo;
                LogUtil.dll("考勤信息:" + MainApp.gson.toJson(_validateInfo));

                for (int i = 0; i < validateInfo.getValids().size(); i++) {
                    isSign = validateInfo.getValids().get(i).isEnable() ? true : false;
                }
                LogUtil.dll("是否打卡:" + isSign);
                rotateInt();
            }

            @Override
            public void failure(RetrofitError error) {
                super.failure(error);
                HttpErrorCheck.checkError(error);
                Toast("获取考勤信息失败" + error.getMessage());
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

    /**
     * 点击打卡,准备跳转新建考勤
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
        showLoading("");
        ValidateItem validateItem = availableValidateItem();
        if (null == validateItem) {
            return;
        }
        int type = validateItem.getType();

        map.clear();
        map.put("inorout", type);
        new LocationUtil(this, this);

    }

    /**
     * 点击头像，获取能否打卡信息
     */
    @Click(R.id.img_title_left)
    void onClickAvatar() {
        getValidateInfo();
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
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float value = (float) valueAnimator.getAnimatedValue();
                LogUtil.d("开始反转：" + value);
                layout_avatar.setRotationY(value);

                if (Math.round(value) >= 90) {
                    img_user.setVisibility(View.INVISIBLE);
                    if (isSign) {
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
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    float value = (float) valueAnimator.getAnimatedValue();
                    LogUtil.d("反转回来：" + value);
                    layout_avatar.setRotationY(value);
                    if (Math.round(value) <= -90) {
                        img_user.setVisibility(View.VISIBLE);
                        if (isSign) {
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
            Toast("组织架构缺损，请重新拉去");
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


    @Background
    void checkUpdateService() {
        mIntentCheckUpdate = new Intent(mContext, CheckUpdateService.class);
        startService(mIntentCheckUpdate);
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
        @Override
        public int getCount() {
            return items.size();
        }

        @Override
        public ClickItem getItem(int position) {
            return items.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        public void remove(int arg0) {
            items.remove(arg0);
            notifyDataSetChanged();
        }

        public void insert(ClickItem item, int arg0) {
            items.add(arg0, item);
            notifyDataSetChanged();
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = LayoutInflater.from(mContext).inflate(R.layout.item_main, null, false);

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
                if ((item.title.equals("工作报告") && num.bizType == 1 && !num.viewed)) {
                    extra = num.bizNum + "个待点评";
                } else if ((item.title.equals("任务计划") && num.bizType == 2)) {
                    extra = num.bizNum + "个待处理";
                } else if ((item.title.equals("审批流程") && num.bizType == 12)) {
                    extra = num.bizNum + "个待审批";
                } else if ((item.title.equals("项目管理") && num.bizType == 5)) {
                    extra = num.bizNum + "个进行中";
                } else if ((item.title.equals("客户管理") && num.bizType == 6)) {
                    extra = num.bizNum + "个将掉公海";
                } else if ((item.title.equals("客户拜访") && num.bizType == 11)) {
                    extra = num.bizNum + "个需拜访";
                } else if ((item.title.equals("考勤管理") && num.bizType == 4)) {
                    extra = num.bizNum + "个外勤";
                }
                if (!TextUtils.isEmpty(extra)) {
                    holder.tv_extra.setText(extra);
                    holder.view_number.setVisibility(num.viewed ? View.GONE : View.VISIBLE);
                } else {
                    holder.view_number.setVisibility(View.GONE);
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

    @Override
    public void onBackPressed() {
        app.logUtil.d("onBackPressed");
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.app_exit_message));
        builder.setTitle("提示");
        builder.setPositiveButton("确认", new android.content.DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
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
        builder.setNegativeButton("取消", new android.content.DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
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
    }


    void launch() {
        updateUser();
        startTrack();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1) {
            requestNumber();
        }
    }

    //获取轨迹，并设置AlarmManager
    @Background
    void startTrack() {
        if (!Utils.isServiceRunning(AMapService.class.getName())) {
            TrackRule.InitTrackRule();
        }
    }


    /**
     * 显示用户名字和部门的名字,头像，高斯模糊背景处理
     */
    void updateUser() {

        if (MainApp.user == null) {
            return;
        }

        ImageLoader.getInstance().displayImage(MainApp.user.avatar, img_user);
        ImageLoader.getInstance().displayImage(MainApp.user.avatar, img_home_head, new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String s, View view) {

            }

            @Override
            public void onLoadingFailed(String s, View view, FailReason failReason) {

            }

            @Override
            public void onLoadingComplete(String s, View view, Bitmap bitmap) {
                if (null != bitmap) {
                    Bitmap blur = Utils.doBlur(bitmap, 15, false);
                    img_home_head.setImageResource(android.R.color.transparent);
                    container.setBackground(new BitmapDrawable(blur));
                }
            }

            @Override
            public void onLoadingCancelled(String s, View view) {

            }
        });

        tv_user_name.setText(MainApp.user.getRealname());
        initBugly();
        testJurl();
    }

    @Background
    void initData() {
        Intent intent = new Intent(mContext, InitDataService_.class);
        startService(intent);
        setJpushAlias();
        requestNumber();
    }

    /**
     * 业务使用权限 判断设置
     * */
    public void testJurl(){
        if(null == MainApp.user){
            return;
        }
        for(int i = 0;i<MainApp.user.permission.suites.size();i++){
            try{
                for(Modules modules : MainApp.user.permission.suites.get(i).getModules()){
                    for(int k = 0;k<items.size();k++){
                        if(modules.getName().equals(items.get(k).title)){
                            if(modules.isEnable()){
                            }else{
                                items.remove(k);
                            }
                        }
                    }
                }
            }catch (NullPointerException e){
                e.printStackTrace();
            }
        }
    }


    /**
     * 企业QQ登录的用户绑定手机号码 权限待测试
     */
    public void isQQLogin() {
        if (app.isQQLogin && TextUtils.isEmpty(MainApp.user.mobile)) {
            ConfirmDialog("企业QQ绑定手机号码", "为了你的账号安全,请立即绑定手机号,绑定后可以使用手机号登录", new ConfirmDialogInterface() {
                @Override
                public void Confirm() {
                    app.startActivity(MainActivity.this, ActivityEditUserMobile.class, MainApp.ENTER_TYPE_RIGHT, false, null);
                }
            });
        }
        app.isQQLogin = false;
    }

    @Background
    void initBugly() {
        String info = "companyId=" + MainApp.user.company_id;
        if (MainApp.user.departmentsName != null) {
            info = info + "," + MainApp.user.departmentsName;
        }

        if (MainApp.user.getRealname() != null) {
            info = info + "," + MainApp.user.getRealname();
        }

        //CrashReport.setUserId(info);//leak 的东西

    }

    public class ClickItem {
        public int imageViewRes;
        public String title;
        public Class<?> cls;

        public ClickItem(int _imageViewRes, String _title, Class<?> _cls) {
            imageViewRes = _imageViewRes;
            title = _title;
            cls = _cls;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (User.getImageUrl() != null)
            ImageLoader.getInstance().displayImage(User.getImageUrl(), img_user);
        intentJpushInfo();
    }

    /**
     * 激光推送要跳转 的 页面
     * buzzType 1，任务2，报告3，审批
     */
    public void intentJpushInfo() {
        if (MainApp.jpushData != null) {
            Intent intent = new Intent();
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
        RestAdapterFactory.getInstance().build(Config_project.MAIN_RED_DOT).create(IMain.class).getNumber(new RCallback<ArrayList<HttpMainRedDot>>() {
            @Override
            public void success(ArrayList<HttpMainRedDot> homeNumbers, Response response) {
                HttpErrorCheck.checkResponse("首页红点", response);
                mItemNumbers = homeNumbers;
                adapter.notifyDataSetChanged();
                mHandler.sendEmptyMessageDelayed(0, 500);
            }

            @Override
            public void failure(RetrofitError error) {
                HttpErrorCheck.checkError(error);
                super.failure(error);
                mHandler.sendEmptyMessageDelayed(0, 500);
            }
        });
    }

}
