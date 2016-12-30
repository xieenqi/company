package com.loyo.oa.v2.activityui.attendance.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.library.module.widget.loading.LoadingLayout;
import com.loyo.oa.hud.progress.LoyoProgressHUD;
import com.loyo.oa.hud.toast.LoyoToast;
import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.attendance.AttendanceAddActivity_;
import com.loyo.oa.v2.activityui.attendance.AttendanceDetailsActivity_;
import com.loyo.oa.v2.activityui.attendance.adapter.AttendanceListAdapter;
import com.loyo.oa.v2.activityui.attendance.adapter.DataSelectAdapter;
import com.loyo.oa.v2.activityui.attendance.event.AttendanceAddEevent;
import com.loyo.oa.v2.activityui.attendance.model.AttendanceList;
import com.loyo.oa.v2.activityui.attendance.model.AttendanceRecord;
import com.loyo.oa.v2.activityui.attendance.model.DataSelect;
import com.loyo.oa.v2.activityui.attendance.model.DayofAttendance;
import com.loyo.oa.v2.activityui.attendance.model.HttpAttendanceList;
import com.loyo.oa.v2.activityui.attendance.model.ValidateInfo;
import com.loyo.oa.v2.activityui.attendance.presenter.impl.AttendanceListPresenterImpl;
import com.loyo.oa.v2.activityui.attendance.viewcontrol.AttendanceListView;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.beans.ValidateItem;
import com.loyo.oa.v2.common.ExtraAndResult;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.common.RecyclerItemClickListener;
import com.loyo.oa.v2.customview.AttenDancePopView;
import com.loyo.oa.v2.customview.CustomRecyclerView;
import com.loyo.oa.v2.tool.BaseFragment;
import com.loyo.oa.v2.tool.LocationUtilGD;
import com.loyo.oa.v2.tool.LogUtil;
import com.loyo.oa.v2.tool.UMengTools;
import com.loyo.oa.v2.tool.Utils;

import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;

import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * 【考勤列表】我的和团队
 * Restruture by yyy on 16/10/11.
 */
public class AttendanceListFragment extends BaseFragment implements View.OnClickListener, LocationUtilGD.AfterLocation, AttendanceListView {

    private Boolean inEnable;
    private Boolean outEnable;
    private Button btn_add;
    private CustomRecyclerView recyclerView;
    private ListView lv;
    private TextView tv_count_title;
    private TextView tv_later;         //迟到
    private TextView tv_leave_early;   //早退
    private TextView tv_leave_overtime;//加班
    private TextView tv_unattendance;  //未打卡
    private TextView tv_field_work;    //外勤
    private TextView data_time_tv;     //时间显示
    private AttendanceList attendanceList;
    private AttendanceRecord attendanceRecords = new AttendanceRecord();
    private HashMap<String, Object> map = new HashMap<>();
    private ArrayList<DayofAttendance> attendances = new ArrayList<DayofAttendance>();
    private ValidateInfo validateInfo = new ValidateInfo();
    private AttendanceListAdapter adapter;
    private LinearLayoutManager layoutManager;
    //    private CustomerDataManager customerDataManager;
    private DataSelectAdapter dataSelectAdapter;
    private ArrayList<DataSelect> dataSelects;
    private AttendanceListPresenterImpl mPresenter;

    private int windowW;
    private int qtime, page = 1;
    private int type;                    //我的考勤【1】 团队考勤【2】
    private boolean isPullDowne = true;  //是否下拉刷新 默认是
    private boolean isAttAdd = false;
    private int outKind;                 //0上班  1下班  2加班
    //    private long checkdateTime;
    private Calendar cal;
    private View mView;
    private LoadingLayout ll_loading;

//    @Override
//    public void onResume() {
//        super.onResume();
//        if (isAttAdd) {
//            LogUtil.dee("onResume");
//            getData(page);
//        }
//    }
//
//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (null == mView) {
            mView = inflater.inflate(R.layout.fragment_attendance_list, container, false);
            initUI();
        }
        btn_add.setVisibility(1 == type ? View.VISIBLE : View.GONE);
        return mView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    private void initUI() {
        ll_loading = (LoadingLayout) mView.findViewById(R.id.ll_loading);
        ll_loading.setStatus(LoadingLayout.Loading);
        ll_loading.setOnReloadListener(new LoadingLayout.OnReloadListener() {
            @Override
            public void onReload(View v) {
                ll_loading.setStatus(LoadingLayout.Loading);
                page = 1;
                getData(page);
            }
        });
        mPresenter = new AttendanceListPresenterImpl(mActivity, this);
        recyclerView = (CustomRecyclerView) mView.findViewById(R.id.recy_data_select);
        tv_count_title = (TextView) mView.findViewById(R.id.tv_count_title);
        tv_later = (TextView) mView.findViewById(R.id.tv_later);
        tv_leave_early = (TextView) mView.findViewById(R.id.tv_leave_early);
        tv_unattendance = (TextView) mView.findViewById(R.id.tv_un_attendance);
        tv_field_work = (TextView) mView.findViewById(R.id.tv_field_work);
        tv_leave_overtime = (TextView) mView.findViewById(R.id.tv_leave_overtime);
        data_time_tv = (TextView) mView.findViewById(R.id.data_time_tv);
        lv = (ListView) mView.findViewById(R.id.listView_attendance);
        btn_add = (Button) mView.findViewById(R.id.btn_add);
        btn_add.setOnTouchListener(Global.GetTouch());
        btn_add.setOnClickListener(this);
        cal = Calendar.getInstance(Locale.CHINA);
        if (null != getArguments()) {
            if (getArguments().containsKey("type")) {
                type = getArguments().getInt("type");
            }
        }
        DataSelectInit();
        initTimeStr(System.currentTimeMillis());
        getData(page);
        lv.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
                    // 判断是否滚动到底部
                    if (view.getLastVisiblePosition() == view.getCount() - 1) {
                        //加载更多功能的代码
                        // Toast("到底部啦");
                        if (type == 2) {
                            loadMore();
                        }
                    }
                    if (view.getLastVisiblePosition() == 0) {
                        page = 1;
                        isPullDowne = true;
                    }
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });
        Utils.btnHideForListView(lv, btn_add);
    }

    /**
     * 时间选择控件初始化
     */
    public void DataSelectInit() {

        int year = Integer.parseInt(com.loyo.oa.common.utils.DateTool.getYear());

        if (type == 2) {
            dataSelects = com.loyo.oa.common.utils.DateTool.getYearMonthDay(2015, year);
            Collections.reverse(dataSelects);
            dataSelects.remove(dataSelects.size() - 1);
            windowW = Utils.getWindowHW(getActivity()).getDefaultDisplay().getWidth();
            data_time_tv.setText(dataSelects.get(0).yearMonDay);
            layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, true);//true 反向显示 false 正常显示
            recyclerView.setLayoutManager(layoutManager);
            dataSelectAdapter = new DataSelectAdapter(getActivity(), dataSelects, windowW, 2, 0);
            recyclerView.setAdapter(dataSelectAdapter);
            qtime = Integer.parseInt(dataSelects.get(0).mapOftime);
        } else {
            dataSelects = com.loyo.oa.common.utils.DateTool.getYearMonth(2015, year);
            Collections.reverse(dataSelects);
            windowW = Utils.getWindowHW(getActivity()).getDefaultDisplay().getWidth();
            data_time_tv.setText(dataSelects.get(0).yearMonDay);
            layoutManager = new LinearLayoutManager(getActivity(), 1, true);//true 反向显示 false 正常显示
            layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
            recyclerView.setLayoutManager(layoutManager);
            dataSelectAdapter = new DataSelectAdapter(getActivity(), dataSelects, windowW, 1, 0);
            recyclerView.setAdapter(dataSelectAdapter);
            qtime = Integer.parseInt(dataSelects.get(0).mapOftime);
        }


        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(), recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                dataSelectAdapter.selectPosition(position);
                dataSelectAdapter.notifyDataSetChanged();
                data_time_tv.setText(dataSelects.get(position).yearMonDay);
                qtime = Integer.parseInt(dataSelects.get(position).mapOftime);
                ll_loading.setStatus(LoadingLayout.Loading);
                getData(page);
            }

            @Override
            public void onLongItemClick(View view, int position) {

            }
        }));
    }

    /**
     * 初始化时间显示
     *
     * @param mills
     */
    private void initTimeStr(long mills) {
        String time = "";
//        checkdateTime = mills;
        switch (type) {
            case 1:
//                time = app.df13.format(new Date(mills));
                time= com.loyo.oa.common.utils.DateTool.getYearMonth(mills/1000);
                break;
            case 2:
//                time = app.df12.format(new Date(mills));
                time= com.loyo.oa.common.utils.DateTool.getDateFriendly(mills/1000);
                break;
        }
        // 顶部显示的 当前时间
        // tv_time.setText(time);
    }

    /**
     * 初始化统计数据
     */
    private void initStatistics() {
        switch (type) {
            case 1:
                tv_count_title.setText("本月总计:");
                break;
            case 2:
                tv_count_title.setText("本日总计:");
                break;
        }
        tv_later.setText(attendanceList.getLateCount() + "");
        tv_leave_early.setText(attendanceList.getEarlyCount() + "");
        tv_unattendance.setText(attendanceList.getNoreCcount() + "");
        tv_field_work.setText(attendanceList.getOutsidecount() + "");
        tv_leave_overtime.setText(attendanceList.getExtraCount() + "");

    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            /*打卡按钮*/
            case R.id.btn_add:
                getValidateInfo();
                break;

            default:
                break;
        }
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
        intent.putExtra("lateMin", attendanceRecords.getLateMin());
        intent.putExtra("earlyMin", attendanceRecords.getEarlyMin());

        startActivity(intent);
        getActivity().overridePendingTransition(R.anim.enter_righttoleft, R.anim.exit_righttoleft);

    }


    /**
     * 早退提示
     */
    public void attanceWorry() {

        sweetAlertDialogView.alertHandle(new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sweetAlertDialog) {
                cancelDialog();
            }
        }, new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sweetAlertDialog) {
                cancelDialog();
                intentValue();
            }
        }, "提示", getString(R.string.app_attanceworry_message));

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

    void startAttanceLocation() {
        ValidateItem validateItem = availableValidateItem();
        if (null == validateItem) {
            return;
        }
        int type = validateItem.getType();
        map.clear();
        map.put("inorout", type);
        new LocationUtilGD(app, this);
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
        final AttenDancePopView popView = new AttenDancePopView(mActivity);
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
            LogUtil.dee("return validateInfo = null");
            return;
        }

        if (!Global.isConnected()) {
            Toast("获取位置失败，请检查网络或GPS是否正常");
            return;
        }
        /*工作日*/
        if (validateInfo.isWorkDay()) {

            if (validateInfo.isPopup() && LocationUtilGD.permissionLocation()) { /*加班*/
                popOutToast();

            } else { /*非加班*/
                dealInOutWork();
            }

        } else if (!validateInfo.isWorkDay() && outEnable) { /*非工作日，下班状态*/
            outKind = 2;
            startAttanceLocation();

        } else if (!validateInfo.isWorkDay() && inEnable) { /*非工作日，上班状态*/
            outKind = 0;
            startAttanceLocation();
        }
    }

    /**
     * 获取能否打卡的信息
     */
    private void getValidateInfo() {
        isAttAdd = true;
        mPresenter.getValidateInfo();
    }

    /**
     * 加载跟多
     */
    public void loadMore() {
        isPullDowne = false;
        page++;
        qtime = (int) (cal.getTime().getTime() / 1000);
        initTimeStr(cal.getTime().getTime());
        getData(page);
    }

    /**
     * 绑定数据
     */
    private void bindData(HttpAttendanceList result) {
        attendances.clear();
        attendances.addAll(result.records.getAttendances());
        if (null == adapter) {
            adapter = new AttendanceListAdapter(attendances, this, getActivity(), mActivity, type);
            lv.setAdapter(adapter);
        } else {
            adapter.notifyDataSetChanged();
        }

        lv.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return false;
            }
        });
        ll_loading.setStatus(LoadingLayout.Success);
        if (attendances.size() == 0)
            ll_loading.setStatus(LoadingLayout.Empty);
    }

    /**
     * 获取列表
     */
    private void getData(final int page) {
        isAttAdd = false;
//        showLoading("请稍后");
        mPresenter.getListData(type, qtime, page);
    }

    /**
     * 位置信息回调
     */
    @Override
    public void OnLocationGDSucessed(final String address, double longitude, double latitude, String radius) {
        UMengTools.sendLocationInfo(address, longitude, latitude);
        map.put("originalgps", longitude + "," + latitude);
        showLoading2("");
        mPresenter.checkAttendance(map, address);
        LocationUtilGD.sotpLocation();
    }

    @Override
    public void OnLocationGDFailed() {
        LocationUtilGD.sotpLocation();
        cancelLoading2();
        Toast("获取位置失败，请检查网络或GPS是否正常");
    }

    /**
     * 获取列表数据成功处理
     */
    @Override
    public void getListDataEmbl(HttpAttendanceList result) {
        if (type == 1) {
            attendanceList = result.records;
            initStatistics();

        } else {
            mPresenter.getTeamData(qtime);
        }
        bindData(result);

    }

    /**
     * 获取团队数据成功处理 统计数据
     */
    @Override
    public void getTeamDataEmbl(AttendanceList mAttendanceList) {
        attendanceList = mAttendanceList;
        initStatistics();
    }

    /**
     * 弹出信息提示
     */
    @Override
    public void messageToat(String message) {

    }

    /**
     * 获取打开信息回调处理
     */
    @Override
    public void getValidateInfoEmbl(ValidateInfo mValidateInfo) {
        if (null == mValidateInfo) {
            Toast("获取考勤信息失败");
            return;
        }
        validateInfo = mValidateInfo;
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
        }
    }

    /**
     * 跳转考勤详情操作
     */
    @Override
    public void previewAttendance(int inOrOut, DayofAttendance attendance, String overTime) {
        if (type == 1) {
            attendance.setUser(MainApp.user);
        }
        isAttAdd = true;
        Intent intent = new Intent(mActivity, AttendanceDetailsActivity_.class);
        intent.putExtra(ExtraAndResult.EXTRA_ID, inOrOut == 1 ? attendance.getIn().getId() : attendance.getOut().getId());
        intent.putExtra("inOrOut", inOrOut);
        intent.putExtra("overTime", overTime);
        startActivity(intent);
    }

    /**
     * 考勤信息检查成功处理
     */
    @Override
    public void checkAttendanceEmbl(AttendanceRecord attendanceRecord, String address) {
        if (null == attendanceRecord) {
            Toast("没有获取到考勤信息");
            return;
        }
        attendanceRecords = attendanceRecord;
        attendanceRecord.setAddress(TextUtils.isEmpty(address) ? "没有获取到有效地址" : address);
        if (attendanceRecord.getState() == 3) {
            attanceWorry();
        } else {
            intentValue();
        }
    }

    @Override
    public LoadingLayout getLoading() {
        return ll_loading;
    }


    /**
     * 添加考勤回调
     */
    @Subscribe
    public void onAddAttendance(AttendanceAddEevent event) {
        getData(1);
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
        cancelCommitLoading();
    }

    @Override
    public void showMsg(String message) {
        LoyoToast.info(this.getActivity(), message);
    }
}
