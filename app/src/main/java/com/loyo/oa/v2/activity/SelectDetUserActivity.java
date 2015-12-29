package com.loyo.oa.v2.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.adapter.SelectDetAdapter;
import com.loyo.oa.v2.adapter.SelectUserAdapter;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.beans.Department;
import com.loyo.oa.v2.beans.NewUser;
import com.loyo.oa.v2.beans.User;
import com.loyo.oa.v2.common.Common;
import com.loyo.oa.v2.tool.BaseActivity;
import com.loyo.oa.v2.tool.LogUtil;

import java.util.ArrayList;

/**
 * 部门 人员选择
 * Created by yyy on 15/12/25.
 */
public class SelectDetUserActivity extends Activity {

    public ListView leftLv, rightLv;
    public LinearLayout llback;
    public RelativeLayout relAllcheck;
    public Button btnSure;
    public CheckBox checkBox;
    public View headerView;

    public LayoutInflater mInflater;
    public SelectDetAdapter mDetAdapter;
    public SelectUserAdapter mUserAdapter;
    public ArrayList<User> userList;
    public ArrayList<User> userSource; //人员数据源
    public ArrayList<Department> deptSource; //部门数据源
    public Context mContext;

    public boolean isAllCheck = false;
    public boolean popy;
    public int totalSize = 0;

    public Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == 0x01)
                mUserAdapter.notifyDataSetChanged();
            btnSure.setText("确定" + "(" + totalSize + ")");
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        setContentView(R.layout.activity_selectdetuser);
        initView();
    }

    /**
     * 初始化
     */
    void initView() {

        /*header初始化*/
        userList = new ArrayList<>();
        mInflater = LayoutInflater.from(this);
        headerView = mInflater.inflate(R.layout.item_header_selectdetuser, null);
        relAllcheck = (RelativeLayout) headerView.findViewById(R.id.selectdetuser_allcheck);
        checkBox = (CheckBox) headerView.findViewById(R.id.selectdetuser_checkbox);

        leftLv = (ListView) findViewById(R.id.lv_selectdetuser_left);
        rightLv = (ListView) findViewById(R.id.lv_selectdetuser_right);
        btnSure = (Button) findViewById(R.id.btn_title_right);
        llback = (LinearLayout) findViewById(R.id.ll_back);

        /*左侧Lv初始化*/
        mDetAdapter = new SelectDetAdapter(mContext, Common.getLstDepartment());
        leftLv.setAdapter(mDetAdapter);
        lvOnClick();

        rightLv.addHeaderView(headerView);

        /*右侧Lv初始化*/
        setData(0);
        mUserAdapter = new SelectUserAdapter(mContext, userList, isAllCheck);
        rightLv.setAdapter(mUserAdapter);

        for(User user:userList){
            LogUtil.dll("hahah:"+user.isIndex());
        }
    }

    /**
     * 监听
     */
    void lvOnClick() {

        /*左侧Lv*/
        leftLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                setData(position);
                mHandler.sendEmptyMessage(0x01);
                checkBox.setChecked(popy);
            }
        });

        /*右侧Lv 加入header后 下标要－1*/
        rightLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                dealisAllSelect();
                if (popy) {

                } else {
                    userList.get(position - 1).setIndex(userList.get(position - 1).isIndex() ? false : true);
                /*选择数量统计*/
                    if (userList.get(position - 1).isIndex()) {
                        totalSize += 1;
                    } else {
                        totalSize -= 1;
                    }
                    mHandler.sendEmptyMessage(0x01);
                }

            }
        });

        /*返回*/
        llback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        /*全选*/
        relAllcheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int hasSet = 0;
                int canSet = 0;
                isAllCheck = isAllCheck ? false : true;
                checkBox.setChecked(isAllCheck);

                /*选择数量统计*/
                for (User user : userList) {
                    if (user.isIndex()) {
                        hasSet++;
                    } else if (user.isIndex() == false) {
                        canSet++;
                    }
                    user.setIndex(isAllCheck);
                }

                if (isAllCheck) {
                    totalSize += userList.size() - hasSet;
                } else {
                    totalSize -= userList.size() - canSet;
                }

                mHandler.sendEmptyMessage(0x01);

            }
        });
    }


    /**
     * 判断本次集合中 是否被全选
     */
    void dealisAllSelect() {

        for (User user : userList) {
            if (user.isIndex()) {
                popy = true;
            } else if (user.isIndex() == false) {
                popy = false;
                return;
            } else {
                popy = false;
                return;
            }
        }

    }

    /**
     * 获取改部门下 所有员工
     */
    void setData(int position) {

        String Id = Common.getLstDepartment().get(position).getId();
        userList.clear();

        userSource = Common.getListUser(Id);
        deptSource = Common.getLstDepartment(Id);

        assUserList(deptSource, userSource);
        dealisAllSelect();

    }

    /**
     * 拣取人员
     */
    void assUserList(ArrayList<Department> departments, ArrayList<User> users) {

        if (users.size() != 0) {
            userList.addAll(users);
        }

        if (departments.size() != 0) {

            for (Department department : departments) {
                String id = department.getId();
                ArrayList<Department> dept = Common.getLstDepartment(id);
                ArrayList<User> user = Common.getListUser(id);
                assUserList(dept, user);
            }

        } else {
            return;
        }
    }

    /**
     * 返回
     */
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            LogUtil.dll("finish");
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LogUtil.dll("结束生命");
    }
}
