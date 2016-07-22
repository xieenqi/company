package com.loyo.oa.v2.activityui.attendance.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.attendance.AttendanceAddActivity_;
import com.loyo.oa.v2.activityui.attendance.HttpAttendanceList;
import com.loyo.oa.v2.activityui.attendance.PreviewAttendanceActivity_;
import com.loyo.oa.v2.activityui.attendance.adapter.CustomerDataManager;
import com.loyo.oa.v2.activityui.attendance.adapter.DataSelectAdapter;
import com.loyo.oa.v2.activityui.attendance.bean.DataSelect;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.activityui.attendance.bean.AttendanceList;
import com.loyo.oa.v2.activityui.attendance.bean.AttendanceRecord;
import com.loyo.oa.v2.activityui.attendance.bean.DayofAttendance;
import com.loyo.oa.v2.activityui.attendance.ValidateInfo;
import com.loyo.oa.v2.beans.ValidateItem;
import com.loyo.oa.v2.common.DialogHelp;
import com.loyo.oa.v2.common.ExtraAndResult;
import com.loyo.oa.v2.common.FinalVariables;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.common.RecyclerItemClickListener;
import com.loyo.oa.v2.common.http.HttpErrorCheck;
import com.loyo.oa.v2.customview.CustomRecyclerView;
import com.loyo.oa.v2.point.IAttendance;
import com.loyo.oa.v2.tool.BaseFragment;
import com.loyo.oa.v2.tool.Config_project;
import com.loyo.oa.v2.tool.DateTool;
import com.loyo.oa.v2.tool.LocationUtilGD;
import com.loyo.oa.v2.tool.LogUtil;
import com.loyo.oa.v2.tool.RCallback;
import com.loyo.oa.v2.tool.RestAdapterFactory;
import com.loyo.oa.v2.tool.Utils;
import com.loyo.oa.v2.tool.ViewHolder;
import com.loyo.oa.v2.customview.AttenDancePopView;
import com.loyo.oa.v2.customview.GeneralPopView;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * com.loyo.oa.v2.ui.fragment
 * 描述 :考勤列表【我的 和 团队】
 * 作者 : ykb
 * 时间 : 15/9/15.
 */
public class AttendanceListFragment extends BaseFragment implements View.OnClickListener,LocationUtilGD.AfterLocation {

    private Boolean inEnable;
    private Boolean outEnable;
    private Button   btn_add;
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
    private GeneralPopView generalPopView;
    private LinearLayoutManager layoutManager;
    private CustomerDataManager customerDataManager;
    private DataSelectAdapter dataSelectAdapter;
    private ArrayList<DataSelect> dataSelects;
    private int scorllW;
    private int windowW;
    private int qtime, page = 1;
    private int type;                    //我的考勤【1】 团队考勤【2】
    private boolean isPullDowne = true;  //是否下拉刷新 默认是
    private boolean isAttAdd    = false;
    private int outKind;                 //0上班  1下班  2加班
    private long checkdateTime;
    private Calendar cal;
    private View mView;

    @Override
    public void onResume() {
        super.onResume();
        if(isAttAdd){
            getData(page);
        }
    }

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

    private void initUI(){
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
                        if (type == 2 && null == DialogHelp.loadingDialog ) {
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

        recyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                scorllW += dx;
                LogUtil.dee("scorllW:"+scorllW);
                if(windowW == scorllW){
                    Toast("滑动到了 屏幕宽度");
                }
            }
        });
    }

    /**
     * 时间选择控件初始化
     * */
    public void DataSelectInit(){

        int year = Integer.parseInt(DateTool.getNowTime("yyyy"));

        if(type == 2){
            dataSelects = DateTool.getYearAllofDay(2015,year);
            Collections.reverse(dataSelects);
            dataSelects.remove(dataSelects.size() - 1);
            windowW = Utils.getWindowHW(getActivity()).getDefaultDisplay().getWidth();
            data_time_tv.setText(dataSelects.get(0).yearMonDay);
            layoutManager = new LinearLayoutManager(getActivity(),LinearLayoutManager.HORIZONTAL,true);//true 反向显示 false 正常显示
            //customerDataManager = new CustomerDataManager(1, StaggeredGridLayoutManager.HORIZONTAL);
            //customerDataManager.setSpeedRatio(0.5);
            recyclerView.setLayoutManager(layoutManager);
            dataSelectAdapter = new DataSelectAdapter(getActivity(),dataSelects,windowW,2,0);
            recyclerView.setAdapter(dataSelectAdapter);
            qtime = Integer.parseInt(dataSelects.get(0).mapOftime);
        }else{
            dataSelects = DateTool.getYearAllofMonth(2015,year);
            Collections.reverse(dataSelects);
            windowW = Utils.getWindowHW(getActivity()).getDefaultDisplay().getWidth();
            data_time_tv.setText(dataSelects.get(0).yearMonDay);
            layoutManager = new LinearLayoutManager(getActivity(),1,true);//true 反向显示 false 正常显示
            layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
            recyclerView.setLayoutManager(layoutManager);
            dataSelectAdapter = new DataSelectAdapter(getActivity(),dataSelects,windowW,1,0);
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
        checkdateTime = mills;
        switch (type) {
            case 1:
                time = app.df13.format(new Date(mills));
                break;
            case 2:
                time = app.df12.format(new Date(mills));
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
            case R.id.btn_add:
                getValidateInfo();
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
        startActivityForResult(intent, FinalVariables.REQUEST_CHECKIN_ATTENDANCE);
        getActivity().overridePendingTransition(R.anim.enter_righttoleft, R.anim.exit_righttoleft);

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
        new LocationUtilGD(getActivity(), this);
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
        isAttAdd = true;
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
     * 加载跟多 xnq
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
    private void bindData() {

        if (null == adapter) {
            adapter = new AttendanceListAdapter();
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
    }

    /**
     * 获取团队集合数据
     */
    private void getTeamData() {
        RestAdapterFactory.getInstance().build(Config_project.API_URL()).create(IAttendance.class).
                getTeamCount(getDateTime((long) qtime), new RCallback<AttendanceList>() {
                    @Override
                    public void success(AttendanceList attendanceLists, Response response) {
                        HttpErrorCheck.checkResponse(type + " 团队Count：", response);
                        attendanceList = attendanceLists;
                        initStatistics();
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        HttpErrorCheck.checkError(error);
                        super.failure(error);
                    }
                });
    }

    private int getDateTime(long qtime) {
        return Integer.valueOf(app.df4.format(new Date((qtime * 1000))).replace(".", ""));
    }

    /**
     * 获取列表
     */
    private void getData(final int page) {
        LogUtil.dee("调用中");
        isAttAdd = false;
        showLoading("");
        HashMap<String, Object> map = new HashMap<>();
        map.put("qtype", type);
        map.put("qtime", qtime);
        map.put("pageIndex", page);
        map.put("pageSize", 20);
        app.getRestAdapter().create(IAttendance.class).getAttendances(map, new RCallback<HttpAttendanceList>() {
            @Override
            public void success(HttpAttendanceList result, Response response) {
                HttpErrorCheck.checkResponse(type + " 考勤列表的数据：", response);
                if (type == 1) {
                    attendanceList = result.records;
                    initStatistics();
                } else {
                    getTeamData();
                }

                if (isPullDowne || page == 1) {
                    attendances = result.records.getAttendances();
                } else {
                    attendances.addAll(result.records.getAttendances());
                }
                bindData();
                if (page != 1) {
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void failure(RetrofitError error) {
                super.failure(error);
                HttpErrorCheck.checkErrorForAttendance(error);
            }
        });
    }

    /**
     * 查看考勤xq
     *
     * @param inOrOut
     * @param attendance
     */
    private void previewAttendance(int inOrOut, DayofAttendance attendance, String overTime) {
        if (type == 1) {
            attendance.setUser(MainApp.user);
        }
        Intent intent = new Intent(mActivity, PreviewAttendanceActivity_.class);
        intent.putExtra(ExtraAndResult.EXTRA_ID, inOrOut == 1 ? attendance.getIn().getId() : attendance.getOut().getId());
        intent.putExtra("inOrOut", inOrOut);
        intent.putExtra("overTime", overTime);
        startActivityForResult(intent, FinalVariables.REQUEST_PREVIEW_OUT_ATTENDANCE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_OK) {
            return;
        }

        if (requestCode == FinalVariables.REQUEST_PREVIEW_OUT_ATTENDANCE) {
            page = 1;
            getData(page);
        }
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

    /**
     * 打卡列表适配器
     */

    private class AttendanceListAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return attendances.size();
        }

        @Override
        public Object getItem(int i) {
            return attendances.isEmpty() ? null : attendances.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {

            if (null == view) {
                view = LayoutInflater.from(mActivity).inflate(R.layout.item_attendance_info, viewGroup, false);
            }

            int inTagstate = 0;
            int outTagstate = 0;
            int totState = 0;


            final DayofAttendance attendance = (DayofAttendance) getItem(i);
            final AttendanceRecord recordIn = attendance.getIn();
            final AttendanceRecord recordOut = attendance.getOut();

            ViewGroup layout_overtime = ViewHolder.get(view, R.id.layout_overtime);//加班
            ViewGroup layout_recordIn = ViewHolder.get(view, R.id.layout_recordin);//上班
            ViewGroup layout_recordOut = ViewHolder.get(view, R.id.layout_recordout);//下班
            layout_recordIn.setOnTouchListener(Global.GetTouch());
            layout_recordOut.setOnTouchListener(Global.GetTouch());

            final TextView tv_overtime = ViewHolder.get(view, R.id.overtime);//加班时间显示
            TextView tv_title = ViewHolder.get(view, R.id.tv_title);
            TextView tv_result = ViewHolder.get(view, R.id.tv_result);
            TextView tv_time1 = ViewHolder.get(view, R.id.tv_time1);
            TextView tv_state = ViewHolder.get(view, R.id.tv_state);
            TextView tv_time = ViewHolder.get(view, R.id.tv_time);

            TextView iv_extra = ViewHolder.get(view, R.id.iv_extra);
            ImageView iv_recordIn_type = ViewHolder.get(view, R.id.iv_record_in_type);
            ImageView iv_recordOut_type = ViewHolder.get(view, R.id.iv_record_out_type);
//            ImageView divider = ViewHolder.get(view, R.id.devider);

            String overTimes = "--";
            int color = getActivity().getResources().getColor(R.color.text99);

            //加班时间
            if (recordOut != null) {
                totState = recordOut.getState();
                outTagstate = recordOut.getTagstate();

                if (recordOut.getState() == 5) {
                    int extraTime = recordOut.getExtraTime();
                    if (extraTime > 60) {
                        overTimes = extraTime / 60 + "小时" + extraTime % 60 + "分";
                    } else if (extraTime != 0) {
                        overTimes = extraTime + "分钟";
                    }

                    if (recordOut.getExtraState() == 1) {
                        color = getActivity().getResources().getColor(R.color.text99);
                    } else if (recordOut.getExtraState() == 2) {
                        color = getActivity().getResources().getColor(R.color.red);
                    }
                }
            }

            tv_overtime.setTextColor(color);
            tv_overtime.setText(overTimes);

            //标题
            if (type == 1) {
                tv_title.setText(attendance.getDatename());
            } else {
                if (null != attendance.getUser()) {
                    tv_title.setText(attendance.getUser().getRealname());
                } else {
                    tv_title.setText("");
                }
            }

            /**上班卡*/
            if (null != recordIn) {
                inTagstate = recordIn.getTagstate();
                if (recordIn.getState() == AttendanceRecord.STATE_BE_LATE) {

                    tv_state.setTextColor(getResources().getColor(R.color.red1));
                    tv_state.setText("迟到");
                    tv_time.setText(app.df6.format(new Date(recordIn.getCreatetime() * 1000)));
                    iv_recordIn_type.setVisibility(View.VISIBLE);
                    tv_state.setVisibility(View.VISIBLE);

                } else if (recordIn.getState() == AttendanceRecord.STATE_NORMAL) {

                    tv_state.setTextColor(getResources().getColor(R.color.text99));
                    tv_state.setText("已打卡");
                    tv_time.setText(app.df6.format(new Date(recordIn.getCreatetime() * 1000)));
                    iv_recordIn_type.setVisibility(View.VISIBLE);
                    tv_state.setVisibility(View.VISIBLE);

                } else if (recordIn.getState() == 4 || recordIn.getState() == 6) {

                    tv_state.setVisibility(View.GONE);
                    iv_recordIn_type.setVisibility(View.INVISIBLE);
                    tv_time.setText("--");

                }

                /*外勤未确认*/
                if (recordIn.getOutstate() == AttendanceRecord.OUT_STATE_FIELD_WORK) {
                    iv_recordIn_type.setImageResource(R.drawable.icon_field_work_confirm);
                }
                /*外勤已确认*/
                else if (recordIn.getOutstate() == AttendanceRecord.OUT_STATE_CONFIRMED_FIELD_WORK) {
                    iv_recordIn_type.setImageResource(R.drawable.icon_field_work_unconfirm);
                }
                 /*内勤*/
                else if (recordIn.getOutstate() == AttendanceRecord.OUT_STATE_OFFICE_WORK) {
                    iv_recordIn_type.setVisibility(View.INVISIBLE);
                }
            } else {
                tv_state.setVisibility(View.GONE);
                iv_recordIn_type.setVisibility(View.INVISIBLE);
                tv_time.setText("--");
            }

            /**
             * 请假 出差判断
             * */

            int textColor = 0;
            int background = 0;
            String status = "";

            if (outTagstate == 1 || inTagstate == 1) {
                background = R.drawable.attendance_shape_travel;
                status = "出差";
                textColor = getResources().getColor(R.color.title_bg1);
            } else if (outTagstate == 2 || inTagstate == 2) {
                background = R.drawable.attendance_shape_leave;
                status = "请假";
                textColor = getResources().getColor(R.color.red1);
            } else if (totState == 6) {
                background = R.drawable.attendance_shape_test;
                status = "休息";
                textColor = getResources().getColor(R.color.green51);
            }

            iv_extra.setText(status);
            iv_extra.setBackgroundResource(background);
            iv_extra.setTextColor(textColor);

            /**下班卡*/
            if (null != recordOut && recordOut.getState() != 5) {
                if (recordOut.getState() == AttendanceRecord.STATE_NORMAL) {

                    tv_result.setTextColor(getResources().getColor(R.color.text99));
                    tv_result.setText("已打卡");
                    tv_time1.setText(app.df6.format(new Date(recordOut.getCreatetime() * 1000)));//打卡时间
                    iv_recordOut_type.setVisibility(View.VISIBLE);
                    tv_result.setVisibility(View.VISIBLE);


                } else if (recordOut.getState() == AttendanceRecord.STATE_LEAVE_EARLY) {

                    tv_result.setTextColor(getResources().getColor(R.color.red1));
                    tv_result.setText("早退");
                    tv_time1.setText(app.df6.format(new Date(recordOut.getCreatetime() * 1000)));//打卡时间
                    iv_recordOut_type.setVisibility(View.VISIBLE);
                    tv_result.setVisibility(View.VISIBLE);

                } else if (recordOut.getState() == 4 || recordOut.getState() == 6) {

                    tv_result.setVisibility(View.GONE);
                    iv_recordOut_type.setVisibility(View.INVISIBLE);
                    tv_time1.setText("--");
                }

                /*未确认的外勤*/
                if (recordOut.getOutstate() == AttendanceRecord.OUT_STATE_FIELD_WORK) {
                    iv_recordOut_type.setImageResource(R.drawable.icon_field_work_confirm);
                }
                /*已确认的外勤*/
                else if (recordOut.getOutstate() == AttendanceRecord.OUT_STATE_CONFIRMED_FIELD_WORK) {
                    iv_recordOut_type.setImageResource(R.drawable.icon_field_work_unconfirm);
                }
                /*内勤*/
                else if (recordOut.getOutstate() == AttendanceRecord.OUT_STATE_OFFICE_WORK) {
                    iv_recordOut_type.setVisibility(View.INVISIBLE);
                }

            } else {
                tv_result.setVisibility(View.GONE);
                iv_recordOut_type.setVisibility(View.INVISIBLE);
                tv_time1.setText("--");
            }

//            if (i == getCount() - 1) {
//                divider.setVisibility(View.INVISIBLE);
//            } else {
//                divider.setVisibility(View.VISIBLE);
//            }

            /**
             * 按键监听
             * */

            /*加班*/
            layout_overtime.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (null == recordOut) {
                        return;
                    }

                    if (recordOut.getState() != 5) {
                        return;
                    }
                    previewAttendance(3, attendance, tv_overtime.getText().toString());
                }
            });

            /*上班*/
            layout_recordIn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (null == recordIn) {
                        return;
                    }

                    if (recordIn.getState() == 4 || recordIn.getState() == 6 || recordIn.getState() == 0) {
                        return;
                    }
                    previewAttendance(1, attendance, "");

                }
            });

            /*下班*/
            layout_recordOut.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (null == recordOut) {
                        return;
                    }

                    if (recordOut.getState() == 5 || recordOut.getState() == 6 || recordOut.getState() == 4 || recordOut.getState() == 0) {
                        return;
                    }
                    previewAttendance(2, attendance, "");

                }
            });
            return view;
        }
    }
}
