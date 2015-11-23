package com.loyo.oa.v2.fragment;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activity.PreviewAttendanceActivity_;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.beans.AttendanceList;
import com.loyo.oa.v2.beans.AttendanceRecord;
import com.loyo.oa.v2.beans.DayofAttendance;
import com.loyo.oa.v2.common.FinalVariables;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.point.IAttendance;
import com.loyo.oa.v2.tool.BaseFragment;
import com.loyo.oa.v2.tool.DateTool;
import com.loyo.oa.v2.tool.RCallback;
import com.loyo.oa.v2.tool.Utils;
import com.loyo.oa.v2.tool.ViewHolder;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import retrofit.client.Response;

/**
 * com.loyo.oa.v2.fragment
 * 描述 :考勤列表
 * 作者 : ykb
 * 时间 : 15/9/15.
 */
public class AttendanceListFragment extends BaseFragment implements View.OnClickListener {

    private ViewGroup imgTimeLeft;
    private ViewGroup imgTimeRight;
    private ListView lv;
    private TextView tv_time;
    private TextView tv_count_title;
    private TextView tv_later;//迟到
    private TextView tv_leave_early;//早退
    private TextView tv_unattendance;//未打卡
    private TextView tv_field_work;//外勤

    private int type;
    private AttendanceList attendanceList;
    private AttendanceListAdapter adapter;
    private int qtime;
    private Calendar cal;

    private View mView;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (null == mView) {
            mView = inflater.inflate(R.layout.fragment_attendance_list, container, false);
            tv_time = (TextView) mView.findViewById(R.id.tv_time);
            tv_count_title = (TextView) mView.findViewById(R.id.tv_count_title);
            tv_later = (TextView) mView.findViewById(R.id.tv_later);
            tv_leave_early = (TextView) mView.findViewById(R.id.tv_leave_early);
            tv_unattendance = (TextView) mView.findViewById(R.id.tv_un_attendance);
            tv_field_work = (TextView) mView.findViewById(R.id.tv_field_work);

            imgTimeLeft = (ViewGroup) mView.findViewById(R.id.img_time_left);
            imgTimeRight = (ViewGroup) mView.findViewById(R.id.img_time_right);


            imgTimeLeft.setOnTouchListener(Global.GetTouch());
            imgTimeRight.setOnTouchListener(Global.GetTouch());
            imgTimeLeft.setOnClickListener(this);
            imgTimeRight.setOnClickListener(this);

            lv = (ListView) mView.findViewById(R.id.listView_attendance);

            cal = Calendar.getInstance(Locale.CHINA);
            if (null != getArguments()) {
                if (getArguments().containsKey("type")) {
                    type = getArguments().getInt("type");
                    app.logUtil.e("type : " + type);
                }
            }
            initTimeStr(System.currentTimeMillis());
            qtime = type == 1 ? DateTool.getBeginAt_ofMonth() : (int) (System.currentTimeMillis() / 1000);
            getData();

        }
        return mView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    /**
     * 初始化时间显示
     *
     * @param mills
     */
    private void initTimeStr(long mills) {
        String time = "";
        switch (type) {
            case 1:
                time = app.df13.format(new Date(mills));
                break;
            case 2:
                time = app.df12.format(new Date(mills));
                break;
        }
        tv_time.setText(time);
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
        tv_later.setText(attendanceList.getLatecount() + "");
        tv_leave_early.setText(attendanceList.getEarlycount() + "");
        tv_unattendance.setText(attendanceList.getNoreccount() + "");
        tv_field_work.setText(attendanceList.getOutsidecount() + "");
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.img_time_left:
                switch (type) {
                    case 1:
                        previousMonth();
                        break;
                    case 2:
                        previousDay();
                        break;
                }
                break;

            case R.id.img_time_right:
                switch (type) {
                    case 1:
                        nextMonth();
                        break;
                    case 2:
                        nextDay();
                        break;
                }
                break;
        }
    }

    /**
     * 前一月
     */
    private void previousMonth() {
        if (cal.get(Calendar.MONTH) == cal.getActualMinimum(Calendar.MONTH)) {
            cal.set((cal.get(Calendar.YEAR) - 1), cal.getActualMaximum(Calendar.MONTH), 1);
        } else {
            cal.set(Calendar.MONTH, cal.get(Calendar.MONTH) - 1);
        }
        cal.set(Calendar.DAY_OF_MONTH, 1);
        refreshData();
    }

    /**
     * 后一月
     */
    private void nextMonth() {
        if (cal.get(Calendar.MONTH) == cal.getActualMaximum(Calendar.MONTH)) {
            cal.set((cal.get(Calendar.YEAR) + 1), cal.getActualMinimum(Calendar.MONTH), 1);
        } else {
            cal.set(Calendar.MONTH, cal.get(Calendar.MONTH) + 1);
        }
        cal.set(Calendar.DAY_OF_MONTH, 1);
        refreshData();
    }

    /**
     * 前一天
     */
    private void previousDay() {
        if (cal.get(Calendar.DAY_OF_MONTH) == cal.getActualMinimum(Calendar.DAY_OF_MONTH)) {
            if (cal.get(Calendar.MONTH) == cal.getActualMinimum(Calendar.MONTH)) {
                cal.set((cal.get(Calendar.YEAR) - 1), cal.getActualMaximum(Calendar.MONTH), cal.getActualMaximum(Calendar.DAY_OF_MONTH));
            } else {
                cal.set(Calendar.MONTH, cal.get(Calendar.MONTH) - 1);
                cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
            }
        } else {
            cal.set(Calendar.DAY_OF_MONTH, cal.get(Calendar.DAY_OF_MONTH) - 1);
        }

        refreshData();
    }

    /**
     * 后一天
     */
    private void nextDay() {
        if (cal.get(Calendar.DAY_OF_MONTH) == cal.getActualMaximum(Calendar.DAY_OF_MONTH)) {
            if (cal.get(Calendar.MONTH) == cal.getActualMaximum(Calendar.MONTH)) {
                cal.set((cal.get(Calendar.YEAR) + 1), cal.getActualMinimum(Calendar.MONTH), 1);
            } else {
                cal.set(Calendar.MONTH, cal.get(Calendar.MONTH) + 1);
                cal.set(Calendar.DAY_OF_MONTH, 1);
            }
        } else {
            cal.set(Calendar.DAY_OF_MONTH, cal.get(Calendar.DAY_OF_MONTH) + 1);
        }

        refreshData();
    }

    /**
     * 刷新数据
     */
    private void refreshData() {
        qtime = (int) (cal.getTime().getTime() / 1000);
        initTimeStr(cal.getTime().getTime());
        getData();
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
     * 获取列表
     */
    private void getData() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("qtype", type);
        map.put("qtime", qtime);
        app.getRestAdapter().create(IAttendance.class).getAttendances(map, new RCallback<AttendanceList>() {
            @Override
            public void success(AttendanceList _attendanceRecords, Response response) {
                attendanceList = _attendanceRecords;
                initStatistics();
                bindData();
            }
        });
    }

    /**
     * 查看考勤
     *
     * @param inOrOut
     * @param attendance
     */
    private void previewAttendance(int inOrOut, DayofAttendance attendance) {
        if (type == 1) {
            attendance.setUser(MainApp.user);
        }
        Intent intent = new Intent(mActivity, PreviewAttendanceActivity_.class);
        intent.putExtra("data", attendance);
        intent.putExtra("inOrOut", inOrOut);
        startActivityForResult(intent, FinalVariables.REQUEST_PREVIEW_OUT_ATTENDANCE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode != Activity.RESULT_OK || null == data) {
            return;
        }
        if (requestCode == FinalVariables.REQUEST_PREVIEW_OUT_ATTENDANCE) {
            getData();
        }
    }

    private class AttendanceListAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return attendanceList.getAttendances().size();
        }

        @Override
        public Object getItem(int i) {
            return attendanceList.getAttendances().isEmpty() ? null : attendanceList.getAttendances().get(i);
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

            final DayofAttendance attendance = (DayofAttendance) getItem(i);
            final AttendanceRecord recordIn = attendance.getIn();
            final AttendanceRecord recordOut = attendance.getOut();

            ViewGroup layout_recordIn = ViewHolder.get(view, R.id.layout_recordin);
            ViewGroup layout_recordOut = ViewHolder.get(view, R.id.layout_recordout);
            layout_recordIn.setOnTouchListener(Global.GetTouch());
            layout_recordOut.setOnTouchListener(Global.GetTouch());

            layout_recordIn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (null != recordIn) {
                        previewAttendance(1, attendance);
                    }
                }
            });

            layout_recordOut.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (null != recordOut) {
                        previewAttendance(2, attendance);
                    }
                }
            });

            TextView tv_title = ViewHolder.get(view, R.id.tv_title);
            ImageView iv_extra = ViewHolder.get(view, R.id.iv_extra);
            iv_extra.setVisibility(View.INVISIBLE);

            TextView tv_state = ViewHolder.get(view, R.id.tv_state);
            TextView tv_time = ViewHolder.get(view, R.id.tv_time);
            ImageView iv_recordIn_type = ViewHolder.get(view, R.id.iv_record_in_type);
            ImageView iv_recordOut_type = ViewHolder.get(view, R.id.iv_record_out_type);

            TextView tv_result = ViewHolder.get(view, R.id.tv_result);
            TextView tv_time1 = ViewHolder.get(view, R.id.tv_time1);

            ImageView divider = ViewHolder.get(view, R.id.devider);

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

            //上班卡
            if (null != recordIn) {
                String moring = "未打卡";
                if (recordIn.getState() == AttendanceRecord.STATE_BE_LATE) {
                    tv_state.setTextColor(getResources().getColor(R.color.red));
                    moring = "迟到";
                } else if (recordIn.getState() == AttendanceRecord.STATE_NORMAL) {
                    tv_state.setTextColor(getResources().getColor(R.color.black));
                    moring = "已打卡";
                }
                tv_state.setText(moring);
                tv_time.setText(app.df6.format(new Date(recordIn.getConfirmtime() * 1000)));

                iv_recordIn_type.setVisibility(View.VISIBLE);
                if (recordIn.getOutstate() == AttendanceRecord.OUT_STATE_FIELD_WORK) {
                    iv_recordIn_type.setImageResource(R.drawable.icon_field_work_unconfirm);
                } else if (recordIn.getOutstate() == AttendanceRecord.OUT_STATE_OFFICE_WORK) {
                    iv_recordIn_type.setImageResource(R.drawable.icon_office_work);
                } else if (recordIn.getOutstate() == AttendanceRecord.OUT_STATE_CONFIRMED_FIELD_WORK) {
                    iv_recordIn_type.setImageResource(R.drawable.icon_field_work_confirm);
                }
            } else {
                tv_state.setText("未打卡");
                tv_state.setTextColor(getResources().getColor(R.color.gray));
                iv_recordIn_type.setVisibility(View.GONE);
                tv_time.setText("--:--");
            }

            //下班卡
            if (null != recordOut) {
                String result = "未打卡";
                switch (recordOut.getState()) {
                    case AttendanceRecord.STATE_NORMAL:
                        tv_result.setTextColor(getResources().getColor(R.color.black));
                        result = "已签退";
                        break;
                    case AttendanceRecord.STATE_LEAVE_EARLY:
                        tv_result.setTextColor(getResources().getColor(R.color.red));
                        result = "早退";
                        break;
                }
                tv_result.setText(Utils.modifyTextColor(result, Color.RED, 0, result.length()));
                tv_time1.setText(app.df6.format(new Date(recordOut.getConfirmtime() * 1000)));

                iv_recordOut_type.setVisibility(View.VISIBLE);
                if (recordOut.getOutstate() == AttendanceRecord.OUT_STATE_FIELD_WORK) {
                    iv_recordOut_type.setImageResource(R.drawable.icon_field_work_unconfirm);
                } else if (recordOut.getOutstate() == AttendanceRecord.OUT_STATE_OFFICE_WORK) {
                    iv_recordOut_type.setImageResource(R.drawable.icon_office_work);
                } else if (recordOut.getOutstate() == AttendanceRecord.OUT_STATE_CONFIRMED_FIELD_WORK) {
                    iv_recordOut_type.setImageResource(R.drawable.icon_field_work_confirm);
                }
            } else {
                tv_result.setTextColor(getResources().getColor(R.color.gray));
                tv_result.setText("未打卡");
                iv_recordOut_type.setVisibility(View.GONE);
                tv_time1.setText("--:--");
            }

            if (i == getCount() - 1) {
                divider.setVisibility(View.GONE);
            } else {
                divider.setVisibility(View.VISIBLE);
            }

            return view;
        }
    }

}
