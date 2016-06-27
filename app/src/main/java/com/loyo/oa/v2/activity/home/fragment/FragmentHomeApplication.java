package com.loyo.oa.v2.activity.home.fragment;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activity.attendance.AttendanceActivity_;
import com.loyo.oa.v2.activity.attendance.AttendanceAddActivity_;
import com.loyo.oa.v2.activity.customer.activity.CustomerAddActivity_;
import com.loyo.oa.v2.activity.customer.activity.SaleActivitiesAddActivity;
import com.loyo.oa.v2.activity.home.adapter.AdapterHomeItem;
import com.loyo.oa.v2.activity.home.bean.HomeItem;
import com.loyo.oa.v2.activity.home.bean.MoreWindowItem;
import com.loyo.oa.v2.activity.home.cusview.MoreWindowCase;
import com.loyo.oa.v2.activity.sale.ActivityAddMySale;
import com.loyo.oa.v2.activity.setting.ActivityEditUserMobile;
import com.loyo.oa.v2.activity.signin.SignInActivity;
import com.loyo.oa.v2.activity.tasks.TasksAddActivity_;
import com.loyo.oa.v2.activity.wfinstance.activity.ActivityWfInTypeSelect;
import com.loyo.oa.v2.activity.work.WorkReportAddActivity_;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.beans.AttendanceRecord;
import com.loyo.oa.v2.beans.HttpMainRedDot;
import com.loyo.oa.v2.beans.Permission;
import com.loyo.oa.v2.beans.TrackRule;
import com.loyo.oa.v2.beans.ValidateInfo;
import com.loyo.oa.v2.beans.ValidateItem;
import com.loyo.oa.v2.common.DialogHelp;
import com.loyo.oa.v2.common.FinalVariables;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.common.http.HttpErrorCheck;
import com.loyo.oa.v2.db.DBManager;
import com.loyo.oa.v2.point.IAttendance;
import com.loyo.oa.v2.point.IMain;
import com.loyo.oa.v2.service.AMapService;
import com.loyo.oa.v2.service.CheckUpdateService;
import com.loyo.oa.v2.service.InitDataService_;
import com.loyo.oa.v2.tool.BaseActivity;
import com.loyo.oa.v2.tool.BaseFragment;
import com.loyo.oa.v2.tool.Config_project;
import com.loyo.oa.v2.tool.LocationUtilGD;
import com.loyo.oa.v2.tool.LogUtil;
import com.loyo.oa.v2.tool.RCallback;
import com.loyo.oa.v2.tool.RestAdapterFactory;
import com.loyo.oa.v2.tool.Utils;
import com.loyo.oa.v2.tool.customview.AttenDancePopView;
import com.loyo.oa.v2.tool.customview.GeneralPopView;
import com.loyo.oa.v2.tool.customview.RoundImageView;
import com.loyo.oa.v2.tool.customview.pullToRefresh.PullToRefreshBase;
import com.loyo.oa.v2.tool.customview.pullToRefresh.PullToRefreshListView;
import com.nostra13.universalimageloader.core.ImageLoader;

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
 * 【首页应用】fragment
 */
public class FragmentHomeApplication extends BaseFragment implements LocationUtilGD.AfterLocation, PullToRefreshBase.OnRefreshListener2 {

    private View mView;
    private String changeEvent = "All";

    private GeneralPopView generalPopView;
    private AttendanceRecord attendanceRecords = new AttendanceRecord();
    private ArrayList<HttpMainRedDot> mItemNumbers = new ArrayList<>();
    private HashMap<String, Object> map = new HashMap<>();
    private ArrayList<HomeItem> items = new ArrayList<>();
    private ArrayList<MoreWindowItem> caseItems;
    private Set<String> companyTag;
    private AdapterHomeItem adapter;
    private Boolean inEnable;
    private Boolean outEnable;
    private boolean isJPus = false;//别名是否设置成功
    private int outKind; //0上班  1下班  2加班

    private PullToRefreshListView listView;
    private Button btn_add;
    private RoundImageView heading;
    private MoreWindowCase mMoreWindowcase;
    private ValidateInfo validateInfo = new ValidateInfo();

    public FragmentHomeApplication(RoundImageView heading) {
        this.heading = heading;
    }

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
                    startActivityForResult(new Intent(getActivity(), TasksAddActivity_.class), Activity.RESULT_FIRST_USER);
                    break;
                //申请审批
                case BaseActivity.WFIN_ADD:
                    startActivityForResult(new Intent(getActivity(), ActivityWfInTypeSelect.class), Activity.RESULT_FIRST_USER);
                    break;
                //提交报告
                case BaseActivity.WORK_ADD:
                    startActivityForResult(new Intent(getActivity(), WorkReportAddActivity_.class), Activity.RESULT_FIRST_USER);
                    break;
                //新建客户
                case BaseActivity.TASKS_ADD_CUSTOMER:
                    startActivityForResult(new Intent(getActivity(), CustomerAddActivity_.class), Activity.RESULT_FIRST_USER);
                    break;
                //考勤打卡
                case BaseActivity.ATTENT_ADD:
                    getValidateInfo();
                    break;
                //拜访签到
                case BaseActivity.SIGNIN_ADD:
                    startActivityForResult(new Intent(getActivity(), SignInActivity.class), Activity.RESULT_FIRST_USER);
                    break;
                //新建机会
                case BaseActivity.SALE_ADD:
                    startActivityForResult(new Intent(getActivity(), ActivityAddMySale.class), Activity.RESULT_FIRST_USER);
                    break;
                //写跟进
                case BaseActivity.FOLLOW_ADD:
                    startActivityForResult(new Intent(getActivity(), SaleActivitiesAddActivity.class), Activity.RESULT_FIRST_USER);
                    break;
            }
        }
    };

//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        LogUtil.d(requestCode + "，，，，，，！！！！！！！！！！！，，，，，，，接受 、、、、、、、、@@@@@@@@@@@@@@@@@@@@@@@、、、、、、、、" + resultCode);
//        if (requestCode == 1) {
//            requestNumber();
//        }
//    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        initData();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_home_application, container,
                false);
        //注册拉去组织架构的广播
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mReceiver, new IntentFilter(FinalVariables.ACTION_DATA_CHANGE));
        //检查更新
        listView = (PullToRefreshListView) mView.findViewById(R.id.newhome_listview);
        btn_add = (Button) mView.findViewById(R.id.btn_add);
        getActivity().startService(new Intent(getActivity(), CheckUpdateService.class));
        if (null != items) {
            DialogHelp.showLoading(getActivity(), "", true);
        }
        adapter = new AdapterHomeItem(getActivity());
        listView.setAdapter(adapter);
        listView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
        listView.setOnRefreshListener(this);
        items = DBManager.Instance().getHomeItem();
        if (null != items && items.size() > 0) {
            adapter.setItemData(items);
        }
        updateUser();
        return mView;
    }

    public void initView() {
        DBManager.Instance().deleteHomeItem();
//        //此处缓存首页数据
        DBManager.Instance().putHomeItem(MainApp.gson.toJson(items));
        adapter.setItemData(items);
        adapter.setRedNumbreData(mItemNumbers);
        btn_add.setOnTouchListener(Global.GetTouch());
        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMoreWindow(v);
            }
        });
        if (null != MainApp.user && null != MainApp.user.avatar) {
            ImageLoader.getInstance().displayImage(MainApp.user.avatar, heading);
        }
    }

    /**
     * 数据初始化
     */
    public void initData() {
        getActivity().startService(new Intent(getActivity(), InitDataService_.class));
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    void launch() {
        setJpushAlias();
        requestNumber();
        startTrack();
    }

    /**
     * 组装首页Item数据
     */
    void updateUser() {

        items = new ArrayList<>(Arrays.asList(new HomeItem(R.drawable.newmain_toast, "公告通知", "com.loyo.oa.v2.activity.BulletinManagerActivity_", "0", 0),
                new HomeItem(R.drawable.newmain_discuss, "我的讨论", "com.loyo.oa.v2.activity.discuss.ActivityMyDiscuss", "0", 0),
                new HomeItem(R.drawable.newmain_list, "通讯录", "com.loyo.oa.v2.activity.contact.ContactsActivity", "0213", 0),
                new HomeItem(R.drawable.newmain_customer, "客户管理", "com.loyo.oa.v2.activity.customer.activity.ActivityCustomerManager", "0205", 1),
                new HomeItem(R.drawable.newmain_sale, "销售机会", "com.loyo.oa.v2.activity.sale.ActivitySaleOpportunitiesManager", "0215", 1),
                new HomeItem(R.drawable.newmain_sagin, "客户拜访", "com.loyo.oa.v2.activity.signin.SignInManagerActivity_", "0206", 1),
                new HomeItem(R.drawable.newmain_project, "项目管理", "com.loyo.oa.v2.activity.project.ProjectManageActivity_", "0201", 2),
                new HomeItem(R.drawable.newmain_task, "任务计划", "com.loyo.oa.v2.activity.tasks.TasksManageActivity_", "0202", 2),
                new HomeItem(R.drawable.newmain_report, "工作报告", "com.loyo.oa.v2.activity.work.WorkReportsManageActivity", "0203", 2),
                new HomeItem(R.drawable.newmain_wfin, "审批流程", "com.loyo.oa.v2.activity.wfinstance.WfInstanceManageActivity", "0204", 2),
                new HomeItem(R.drawable.newmain_attent, "考勤管理", "com.loyo.oa.v2.activity.attendance.AttendanceActivity_", "0211", 2)));


        caseItems = new ArrayList<>(Arrays.asList(new MoreWindowItem("新建任务", "0202", R.drawable.newmain_post_task),
                new MoreWindowItem("申请审批", "0204", R.drawable.newmain_post_wif),
                new MoreWindowItem("提交报告", "0203", R.drawable.newmain_post_report),
                new MoreWindowItem("新建客户", "0205", R.drawable.newmain_post_customer),
                new MoreWindowItem("写跟进", "0205", R.drawable.newmain_post_follow),
                new MoreWindowItem("新建机会", "0215", R.drawable.newmain_post_sale),
                new MoreWindowItem("考勤打卡", "0000", R.drawable.newmain_post_att),
                new MoreWindowItem("拜访签到", "0206", R.drawable.newmain_post_sign)));

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
        JPushInterface.setAlias(getActivity(), MainApp.user.id, new TagAliasCallback() {
            @Override
            public void gotResult(final int i, final String s, final Set<String> set) {
                if (i != 0) {
                    isJPus = true;
                    setJpushAlias();
                }
                isQQLogin();
            }
        });
        JPushInterface.setTags(getActivity(), companyTag, new TagAliasCallback() {
            @Override
            public void gotResult(int i, String s, Set<String> set) {

            }
        });
    }

    /**
     * 获取能否打卡的信息
     */
    private void getValidateInfo() {
        DialogHelp.showLoading(getActivity(), "加载中...", true);
        MainApp.getMainApp().getRestAdapter().create(IAttendance.class).validateAttendance(new RCallback<ValidateInfo>() {
            @Override
            public void success(final ValidateInfo _validateInfo, final Response response) {
                HttpErrorCheck.checkResponse("考勤信息:", response);
                if (null == _validateInfo) {
                    Toast.makeText(getActivity(), "获取考勤信息失败", Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(getActivity(), "您今天已经打卡完毕", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getActivity(), AttendanceActivity_.class);
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


    private void setAttendance() {
        if (null == validateInfo) {
            return;
        }

        if (!Global.isConnected()) {
            Toast.makeText(getActivity(), "没有网络连接，不能打卡", Toast.LENGTH_SHORT).show();
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
            if (validateInfo.isExtraTimeSwitch()) {
                outKind = 2;
            } else {
                outKind = 1;
            }
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
        final AttenDancePopView popView = new AttenDancePopView(getActivity());
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
     * 企业QQ登录的用户绑定手机号码
     */
    public void isQQLogin() {
        if (MainApp.getMainApp().isQQLogin && TextUtils.isEmpty(MainApp.user.mobile)) {
            showGeneralDialog(false, true, getString(R.string.app_homeqq_message));
            //确认
            generalPopView.setSureOnclick(new View.OnClickListener() {
                @Override
                public void onClick(final View view) {
                    generalPopView.dismiss();
                    MainApp.getMainApp().startActivity(getActivity(), ActivityEditUserMobile.class, MainApp.ENTER_TYPE_RIGHT, false, null);
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
        MainApp.getMainApp().isQQLogin = false;
    }

    void startAttanceLocation() {
        ValidateItem validateItem = availableValidateItem();
        if (null == validateItem) {
            return;
        }
        int type = validateItem.getType();
        map.clear();
        map.put("inorout", type);
        new LocationUtilGD(getActivity(), this);
    }

    /**
     * 获取首页红点数据
     */
    void requestNumber() {
        DialogHelp.cancelLoading();//列表出现消失dialog
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
        if (null != MainApp.user && !MainApp.user.isSuperUser()) {
            if (null == MainApp.user || null == MainApp.user.newpermission || null == MainApp.user.newpermission ||
                    0 == MainApp.user.newpermission.size()) {
                Timer timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        Looper.prepare();
                        getActivity().runOnUiThread(new Runnable() {
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

                for (int i = 0; i < caseItems.size(); i++) {
                    if (caseItems.get(i).code.equals(permission.getCode())) {
                        if (!permission.isEnable()) {
                            caseItems.remove(i);
                        }
                    }
                }

                for (int i = 0; i < caseItems.size(); i++) {
                    if (caseItems.get(i).code.equals(permission.getCode())) {
                        if (!permission.isEnable()) {
                            caseItems.remove(i);
                        }
                    }
                }
            }
        }
        initView();
    }


    /**
     * 通用提示弹出框init
     */
    public GeneralPopView showGeneralDialog(boolean isOut, boolean isKind, String message) {
        generalPopView = new GeneralPopView(getActivity(), isKind);
        generalPopView.show();
        generalPopView.setMessage(message);
        generalPopView.setCanceledOnTouchOutside(isOut);
        return generalPopView;
    }

    /**
     * 显示弹出菜单
     */
    void showMoreWindow(View view) {
        mMoreWindowcase = new MoreWindowCase(getActivity(), mHandler, caseItems);
        mMoreWindowcase.init();
        mMoreWindowcase.showMoreWindow(view);
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mReceiver);
    }

    /*
         * 点击切换活动判断当前页面
         */
    public void setChangeEvent(String value) {
        changeEvent = value;
    }

    public String getChangeEvent() {
        return changeEvent;
    }

    /*
     * 点击策划切换页面
     */
    public void gotoShiftEvent(int index) {
//        if (index == 1) {
//            if (mAllFragment == null) {
//                mAllFragment = new AllFragment();
//            }
//            switchFragment(mAllFragment);
//            setChangeEvent("All");
//        }

//        else {
//			if (mNearFragment == null) {
//				mNearFragment = new NearFragmengt();
//			}
//			switchFragment(mNearFragment);
//		}
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
     * 跳转考勤界面，封装数据
     */
    public void intentValue() {
        Intent intent = new Intent(getActivity(), AttendanceAddActivity_.class);
        intent.putExtra("mAttendanceRecord", attendanceRecords);
        intent.putExtra("needPhoto", validateInfo.isNeedPhoto());
        intent.putExtra("isPopup", validateInfo.isPopup());
        intent.putExtra("outKind", outKind);
        intent.putExtra("serverTime", validateInfo.getServerTime());
        intent.putExtra("extraWorkStartTime", attendanceRecords.getExtraWorkStartTime());
        startActivityForResult(intent, FinalVariables.REQUEST_CHECKIN_ATTENDANCE);
    }

    @Override
    public void OnLocationGDSucessed(final String address, double longitude, double latitude, String radius) {
        map.put("originalgps", longitude + "," + latitude);
        LogUtil.d("经纬度:" + MainApp.gson.toJson(map));
        DialogHelp.showLoading(getActivity(), "", true);
        MainApp.getMainApp().getRestAdapter().create(IAttendance.class).checkAttendance(map, new RCallback<AttendanceRecord>() {
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

    @Override
    public void OnLocationGDFailed() {
        LocationUtilGD.sotpLocation();
        DialogHelp.cancelLoading();
        Toast.makeText(getActivity(), "获取打卡位置失败", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPullDownToRefresh(PullToRefreshBase refreshView) {
        if (Utils.isNetworkAvailable(getActivity())) {
            initData();
        } else {
            Toast("请检查你的网络链接");
            listView.onRefreshComplete();
        }
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase refreshView) {

    }

}
