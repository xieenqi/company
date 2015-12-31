package com.loyo.oa.v2.fragment;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activity.attendance.AttendanceAddActivity_;
import com.loyo.oa.v2.beans.AttendanceRecord;
import com.loyo.oa.v2.beans.ValidateInfo;
import com.loyo.oa.v2.beans.ValidateItem;
import com.loyo.oa.v2.common.FinalVariables;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.point.IAttendance;
import com.loyo.oa.v2.tool.BaseFragment;
import com.loyo.oa.v2.tool.DateTool;
import com.loyo.oa.v2.tool.LocationUtil;
import com.loyo.oa.v2.tool.RCallback;
import com.loyo.oa.v2.tool.ViewHolder;
import com.loyo.oa.v2.tool.customview.CustomProgressDialog;

import java.util.Date;
import java.util.HashMap;

import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * com.loyo.oa.v2.fragment
 * 描述 :考勤打卡界面
 * 作者 : ykb
 * 时间 : 15/9/14.
 */

public class AttendanceFragment extends BaseFragment implements LocationUtil.AfterLocation {
    private ListView lv_attendance_type;
    private TextView tv_time;
    private TextView tv_weekday;
    private ValidateInfo validateInfo;
    private MAdapter adapter;
    private CustomProgressDialog progressDialog;
    private HashMap<String, Object> map = new HashMap<>();
    private View mView;

    private BroadcastReceiver mBroad = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (null == intent || TextUtils.isEmpty(intent.getAction())) {
                return;
            }
            if (TextUtils.equals(Intent.ACTION_TIME_TICK, intent.getAction())) {
                changeTime();
            }
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if (null == mView) {
            mView = inflater.inflate(R.layout.fragment_attendance, container, false);
            lv_attendance_type = (ListView) mView.findViewById(R.id.lv_attendance_type);
            tv_time = (TextView) mView.findViewById(R.id.tv_time);
            tv_weekday = (TextView) mView.findViewById(R.id.tv_week_day);
        }
        return mView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        changeTime();
        getData();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity.registerReceiver(mBroad, new IntentFilter(Intent.ACTION_TIME_TICK));
    }

    @Override
    public void onDestroyView() {
        mActivity.unregisterReceiver(mBroad);
        super.onDestroyView();
    }

    /**
     * 改变时间
     */
    private void changeTime() {
        long mills = System.currentTimeMillis();
        String weekDay = DateTool.getWeekStr(mills);
        String date = app.df10.format(new Date(mills));
        tv_time.setText(date);
        tv_weekday.setText(weekDay);
    }

    /**
     * 初始化数据
     */
    private void initData() {
        if (null == validateInfo) {
            return;
        }
        if (null == adapter) {
            adapter = new MAdapter();
            lv_attendance_type.setAdapter(adapter);
        } else {
            adapter.notifyDataSetChanged();
        }
    }

    /**
     * 获取能否打卡的信息
     */
    private void getData() {
        app.getRestAdapter().create(IAttendance.class).validateAttendance(new RCallback<ValidateInfo>() {
            @Override
            public void success(ValidateInfo _validateInfo, Response response) {
                validateInfo = _validateInfo;
                initData();
            }

            @Override
            public void failure(RetrofitError error) {
                Toast("获取考勤打卡内容失败");
                super.failure(error);
            }
        });
    }

    /**
     * 新增考勤
     *
     * @param inOrOut
     */
    private void checkAttendance(int inOrOut) {
        map.clear();
        map.put("inorout", inOrOut);
        progressDialog = new CustomProgressDialog(mActivity);
        progressDialog.setTitle("加载中,请稍后...");
        progressDialog.show();
        new LocationUtil(mActivity, this);
    }

    @Override
    public void OnLocationFailed() {
        Toast("获取打卡位置失败");
        progressDialog.dismiss();
    }

    @Override
    public void OnLocationSucessed(final String address, double longitude, double latitude, float radius) {
        map.put("originalgps", longitude + "," + latitude);
        app.getRestAdapter().create(IAttendance.class).checkAttendance(map, new RCallback<AttendanceRecord>() {
            @Override
            public void success(AttendanceRecord attendanceRecord, Response response) {
                progressDialog.dismiss();
                attendanceRecord.setAddress(address);
                Intent intent = new Intent(mActivity, AttendanceAddActivity_.class);
                intent.putExtra("mAttendanceRecord", attendanceRecord);
                startActivityForResult(intent, FinalVariables.REQUEST_CHECKIN_ATTENDANCE);
            }

            @Override
            public void failure(RetrofitError error) {
                Toast("服务器连接失败,请检查网络");
                super.failure(error);
            }
        });
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_OK || null == data) {
            return;
        }
        switch (requestCode) {
            case FinalVariables.REQUEST_CHECKIN_ATTENDANCE:
                getData();
                break;
        }
    }
    private class MAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return validateInfo.getValids().size();
        }

        @Override
        public Object getItem(int i) {
            return validateInfo.getValids().isEmpty() ? null : validateInfo.getValids().get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            if (null == view) {
                view = LayoutInflater.from(mActivity).inflate(R.layout.item_attendance_listview, viewGroup, false);
            }
            final ValidateItem item = (ValidateItem) getItem(i);
            final int type = item.getType();


            ImageView status = ViewHolder.get(view, R.id.iv_attendance_status);
            TextView result = ViewHolder.get(view, R.id.tv_result);
            TextView title = ViewHolder.get(view, R.id.tv_name);
            TextView content = ViewHolder.get(view, R.id.tv_time);
            Button attendance = ViewHolder.get(view, R.id.tv_attendance);
            ImageView divider = ViewHolder.get(view, R.id.devider);

            if (i == validateInfo.getValids().size() - 1) {
                divider.setVisibility(View.GONE);
            } else {
                divider.setVisibility(View.VISIBLE);
            }

            attendance.setOnTouchListener(Global.GetTouch());
            attendance.setEnabled(item.isEnable());
            attendance.setBackgroundResource(item.isEnable() ? R.drawable.round_bg_shpe : R.drawable.round_bg_shpe2);

            if (item.ischecked()) {
                result.setVisibility(View.VISIBLE);
                result.setText(item.getReason());
                if (item.getState() == AttendanceRecord.STATE_NORMAL) {
                    result.setTextColor(getResources().getColor(R.color.gray));
                } else {
                    result.setTextColor(getResources().getColor(R.color.red));
                }
                attendance.setVisibility(View.GONE);
            } else {
                result.setVisibility(View.GONE);
                attendance.setVisibility(View.VISIBLE);
                if (item.isEnable()) {
                    attendance.setText(item.ischecked() ? "未打卡" : "打卡");
                } else {
                    attendance.setVisibility(item.ischecked() ? View.GONE : View.VISIBLE);
                }
            }

            if (type == ValidateItem.ATTENDANCE_STATE_IN) {
                status.setImageResource(item.isEnable() ? R.drawable.icon_checkin_normal : R.drawable.icon_checkin_invalid);
            } else {
                status.setImageResource(item.isEnable() ? R.drawable.icon_checkout_normal : R.drawable.icon_checkout_invalid);
            }

            title.setText(type == ValidateItem.ATTENDANCE_STATE_IN ? "上班" : "下班");
            long time = type == ValidateItem.ATTENDANCE_STATE_IN ? validateInfo.getSetting().getCheckInTime() : validateInfo.getSetting().getCheckOutTime();
            content.setText(app.df6.format(new Date(time)));

            attendance.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    checkAttendance(type);
                }
            });
            return view;
        }
    }
}
