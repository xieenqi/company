package com.loyo.oa.v2.activityui.attendance.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.attendance.model.AttendanceRecord;
import com.loyo.oa.v2.activityui.attendance.model.DayofAttendance;
import com.loyo.oa.v2.activityui.attendance.viewcontrol.AttendanceListView;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.tool.ViewHolder;
import java.util.ArrayList;
import java.util.Date;

/**
 * 【考勤列表】适配器
 * Created by yyy on 16/10/11.
 */

public class AttendanceListAdapter extends BaseAdapter {

    public int type;
    public Activity mActivity;
    public Context mContext;
    public ArrayList<DayofAttendance> mAttendances;
    public AttendanceListView crolView;

    public AttendanceListAdapter(ArrayList<DayofAttendance> mAttendance,AttendanceListView crolView,Context mContext,Activity mActivity,int type){
        this.mAttendances = mAttendance;
        this.crolView  = crolView;
        this.mContext  = mContext;
        this.mActivity = mActivity;
        this.type = type;
    }


    @Override
    public int getCount() {
        return mAttendances.size();
    }

    @Override
    public Object getItem(int i) {
        return mAttendances.isEmpty() ? null : mAttendances.get(i);
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
        int color = mContext.getResources().getColor(R.color.text99);

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
                    color = mContext.getResources().getColor(R.color.text99);
                } else if (recordOut.getExtraState() == 2) {
                    color = mContext.getResources().getColor(R.color.red1);
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

                tv_state.setTextColor(mContext.getResources().getColor(R.color.red1));
                tv_state.setText("迟到");
                tv_time.setText(MainApp.getMainApp().df6.format(new Date(recordIn.getCreatetime() * 1000)));
                iv_recordIn_type.setVisibility(View.VISIBLE);
                tv_state.setVisibility(View.VISIBLE);

            } else if (recordIn.getState() == AttendanceRecord.STATE_NORMAL) {

                tv_state.setTextColor(mContext.getResources().getColor(R.color.text99));
                tv_state.setText("已打卡");
                tv_time.setText(MainApp.getMainApp().df6.format(new Date(recordIn.getCreatetime() * 1000)));
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
            textColor = mContext.getResources().getColor(R.color.title_bg1);
        } else if (outTagstate == 2 || inTagstate == 2) {
            background = R.drawable.attendance_shape_leave;
            status = "请假";
            textColor = mContext.getResources().getColor(R.color.red1);
        } else if (totState == 6) {
            background = R.drawable.attendance_shape_test;
            status = "休息";
            textColor = mContext.getResources().getColor(R.color.green51);
        }

        iv_extra.setText(status);
        iv_extra.setBackgroundResource(background);
        iv_extra.setTextColor(textColor);

        /**下班卡*/
        if (null != recordOut && recordOut.getState() != 5) {
            if (recordOut.getState() == AttendanceRecord.STATE_NORMAL) {

                tv_result.setTextColor(mContext.getResources().getColor(R.color.text99));
                tv_result.setText("已打卡");
                tv_time1.setText(MainApp.getMainApp().df6.format(new Date(recordOut.getCreatetime() * 1000)));//打卡时间
                iv_recordOut_type.setVisibility(View.VISIBLE);
                tv_result.setVisibility(View.VISIBLE);


            } else if (recordOut.getState() == AttendanceRecord.STATE_LEAVE_EARLY) {

                tv_result.setTextColor(mContext.getResources().getColor(R.color.red1));
                tv_result.setText("早退");
                tv_time1.setText(MainApp.getMainApp().df6.format(new Date(recordOut.getCreatetime() * 1000)));//打卡时间
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
                crolView.previewAttendance(3, attendance, tv_overtime.getText().toString());
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
                crolView.previewAttendance(1, attendance, "");

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
                crolView.previewAttendance(2, attendance, "");
            }
        });
        return view;
    }
}
