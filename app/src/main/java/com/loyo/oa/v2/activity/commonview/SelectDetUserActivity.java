package com.loyo.oa.v2.activity.commonview;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
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
import com.loyo.oa.v2.beans.Member;
import com.loyo.oa.v2.beans.Members;
import com.loyo.oa.v2.beans.NewUser;
import com.loyo.oa.v2.beans.User;
import com.loyo.oa.v2.beans.UserGroupData;
import com.loyo.oa.v2.beans.UserInfo;
import com.loyo.oa.v2.common.Common;
import com.loyo.oa.v2.common.ExtraAndResult;
import com.loyo.oa.v2.tool.LogUtil;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * 部门 人员选择
 * Created by yyy on 15/12/25.
 */
public class SelectDetUserActivity extends Activity {

    public MainApp app = MainApp.getMainApp();
    public ListView leftLv, rightLv;
    public LinearLayout llback;
    public RelativeLayout relAllcheck;
    public Button btnSure;
    public CheckBox checkBox;
    public View headerView;
    public Context mContext;
    public LayoutInflater mInflater;
    public SelectDetAdapter mDetAdapter;
    public SelectUserAdapter mUserAdapter;
    public Intent mIntent;
    public Bundle mBundle;

    public ArrayList<User> localCacheUserList; //本地所有员工 缓存
    public ArrayList<User> userList;
    public ArrayList<User> userAllList; //所有员工
    public ArrayList<Department> deptSource;//部门数据源｀
    public ArrayList<UserGroupData> totalSource; //全部数据源

    public boolean isAllCheck = false;
    public boolean popy; //当前列表 是否全选
    public int totalSize = 0;
    public int positions = 0;
    public int selectType; //0参与人 1负责人 2编辑参与人
    public String[] joinUserId;
    private TextView tv_selectdetuser_tv;

    private ArrayList<String> selectDeptIds;
    private ArrayList<String> selectUserIds;

    private ArrayList<NewUser> usersList;
    private ArrayList<NewUser> deptsList;
    private NewUser newUser;
    private Members members;

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

        mIntent = getIntent();
        mBundle = mIntent.getExtras();
        selectType = mBundle.getInt(ExtraAndResult.STR_SELECT_TYPE);
        totalSource = Common.getLstUserGroupData();
        deptSource = Common.getLstDepartment();
        userAllList = new ArrayList<>();
        userList = new ArrayList<>();

        usersList = new ArrayList<>();
        deptsList = new ArrayList<>();
        members = new Members();
        selectDeptIds = new ArrayList<>();
        selectUserIds = new ArrayList<>();
        localCacheUserList = new ArrayList<>();

        /*header初始化*/
        mInflater = LayoutInflater.from(this);
        headerView = mInflater.inflate(R.layout.item_header_selectdetuser, null);
        relAllcheck = (RelativeLayout) headerView.findViewById(R.id.selectdetuser_allcheck);
        checkBox = (CheckBox) headerView.findViewById(R.id.selectdetuser_checkbox);

        leftLv = (ListView) findViewById(R.id.lv_selectdetuser_left);
        rightLv = (ListView) findViewById(R.id.lv_selectdetuser_right);
        btnSure = (Button) findViewById(R.id.btn_title_right);
        llback = (LinearLayout) findViewById(R.id.ll_back);
        tv_selectdetuser_tv = (TextView) findViewById(R.id.tv_selectdetuser_tv);

        /*全部人员获取*/
        for (int i = 0; i < MainApp.lstDepartment.size(); i++) {
            for (int k = 0; k < MainApp.lstDepartment.get(i).getUsers().size(); k++) {
                localCacheUserList.add(MainApp.lstDepartment.get(i).getUsers().get(k));
            }
        }

        userAllList.addAll(RemoveSame(localCacheUserList));

        for (User user : userAllList) {
            user.setIndex(false);
        }

        if (selectType == 1) {
            btnSure.setVisibility(View.INVISIBLE);
        } else if (selectType == 2) {
            /*来自编辑页面已存在的参与人，选中设为true*/
            joinUserId = mBundle.getString(ExtraAndResult.STR_SUPER_ID).split(",");
            for (User user : userAllList) {
                for (int i = 0; i < joinUserId.length; i++) {
                    if (user.getId().equals(joinUserId[i])) {
                        user.setIndex(true);
                        totalSize++;
                    }
                }
            }
        }

        btnSure.setText("确定" + "(" + totalSize + ")");
        /*左侧Lv初始化*/
        mDetAdapter = new SelectDetAdapter(mContext, deptSource);
        leftLv.setAdapter(mDetAdapter);
        lvOnClick();
        rightLv.addHeaderView(headerView);
        userList.addAll(userAllList);
        /*右侧Lv初始化*/
        mUserAdapter = new SelectUserAdapter(mContext, userList, isAllCheck);
        rightLv.setAdapter(mUserAdapter);

    }

    /**
     * 去掉人员重复数据
     * */
    private  ArrayList RemoveSame(ArrayList<User> list)
    {
        for (int i = 0; i < list.size() - 1; i++)
        {
            for (int j = i + 1; j < list.size(); j++)
            {
                if (list.get(i).getId().equals(list.get(j).getId()))
                {
                    list.remove(j);
                    j--;
                }
            }
        }
        return list;
    }

    /**
     * 参与人组装
     */

    void setJoinUsers() {
        testGetJoiner();
        usersList.clear();
        deptsList.clear();

        for (Department department : deptSource) {
            for (int i = 0; i < selectDeptIds.size(); i++) {
                if (selectDeptIds.get(i).equals(department.getId())) {
                    newUser = new NewUser();
                    newUser.setId(department.getId());
                    newUser.setName(department.getName());
                    deptsList.add(newUser);
                }
            }
        }

        for (User user : userAllList) {
            for (int i = 0; i < selectUserIds.size(); i++) {
                if (selectUserIds.get(i).equals(user.getId())) {
                    newUser = new NewUser();
                    newUser.setId(user.getId());
                    newUser.setName(user.getRealname());
                    usersList.add(newUser);
                }
            }
        }

        members.depts = deptsList;
        members.users = usersList;

    }


    void lvOnClick() {

        /*左侧ListView*/
        leftLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                positions = position;
                getInfoUser(positions);
                mHandler.sendEmptyMessage(0x01);
                checkBox.setChecked(popy);

                String xPath = null;
                String classAName = null;
                String classAId = null;
                String[] xPathList;

                for (Department department : deptSource) {
                    if (department.getXpath().contains(deptSource.get(position).getXpath())
                            && !deptSource.get(position).getXpath().equals(department.getXpath())) {
                        LogUtil.dll("下级:" + department.getName());
                    }
                }
            }
        });

        /*右侧ListView 加入header后 下标要－1*/
        rightLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                /*负责人*/
                if (selectType == 1) {

                    Intent mIntent = new Intent();
                    Bundle mBundle = new Bundle();
                    mBundle.putSerializable(User.class.getName(), userList.get(position - 1));
                    mIntent.putExtras(mBundle);
                    app.finishActivity(SelectDetUserActivity.this, MainApp.ENTER_TYPE_LEFT, RESULT_OK, mIntent);

                } else {

                    userList.get(position - 1).setIndex(userList.get(position - 1).isIndex() ? false : true);
                    statisticsTotalSize(position);

                    if (popy) {
                        checkBox.setChecked(true);
                    } else if (!popy) {
                        checkBox.setChecked(false);
                    }

                    mHandler.sendEmptyMessage(0x01);

                }
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

        /*返回*/
        llback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        /*确定*/
        btnSure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setJoinUsers();
                mIntent = new Intent();
                mBundle = new Bundle();
                if (totalSize != 0) {
                    mBundle.putSerializable(ExtraAndResult.CC_USER_ID, members);
                } else {
                    mBundle.putSerializable(ExtraAndResult.CC_USER_ID, null);
                }
                mIntent.putExtras(mBundle);
                app.finishActivity(SelectDetUserActivity.this, MainApp.ENTER_TYPE_LEFT, RESULT_OK, mIntent);

            }
        });

        /*搜索*/
        tv_selectdetuser_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainApp.selectAllUsers = userAllList;
                mBundle = new Bundle();
                mBundle.putInt(ExtraAndResult.STR_SELECT_TYPE, selectType);
                app.startActivityForResult(SelectDetUserActivity.this, SelectDetUserSerach.class, MainApp.ENTER_TYPE_ZOOM_IN, ExtraAndResult.request_Code, mBundle);
            }
        });
    }

    /**
     * 获取选中的部门，人员
     */
    void testGetJoiner() {

        selectDeptIds.clear();
        selectUserIds.clear();


        for (Department department : deptSource) {
            dealisAllSelect(department.getUsers());
            if (popy) {
                department.setIsIndex(true);
            } else {
                department.setIsIndex(false);
            }

            if (department.isIndex()) {
                selectDeptIds.add(department.getId());
            } else {
                for (User user : department.getUsers()) {
                    if (user.isIndex()) {
                        selectUserIds.add(user.getId());
                    }
                }
            }
        }
    }


    /**
     * 选中总数统计
     */
    void statisticsTotalSize(int position) {
        if (userList.get(position - 1).isIndex()) {
            totalSize += 1;
        } else {
            totalSize -= 1;
        }
    }


    /**
     * 判断本次集合中 是否被全选
     */
    void dealisAllSelect(ArrayList<User> users) {

        for (User user : users) {
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
     * 获取当前部门人员
     *
     * @deprecated 根据部门Xpath获取人员
     */
    void getInfoUser(int positions) {
        userList.clear();
        for (Department department : deptSource) {
            if (department.getXpath().contains(deptSource.get(positions).getXpath())) {
                for (User user : department.getUsers()) {
                    userList.add(user);
                }
            }
        }
        dealisAllSelect(userList);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode != ExtraAndResult.request_Code) {
            return;
        }
        int selectTypePage = 999;
        String userId;
        switch (requestCode) {
           /*选人搜索回调*/
            case ExtraAndResult.request_Code:

                try {
                    selectTypePage = data.getIntExtra(ExtraAndResult.STR_SELECT_TYPE, 0);
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }

                switch (selectTypePage) {
                   /*负责人*/
                    case ExtraAndResult.TYPE_SELECT_SINGLE:

                        mIntent = new Intent();
                        mBundle = new Bundle();
                        mBundle.putSerializable(User.class.getName(), data.getSerializableExtra(User.class.getName()));
                        mIntent.putExtras(mBundle);
                        app.finishActivity(SelectDetUserActivity.this, MainApp.ENTER_TYPE_LEFT, RESULT_OK, mIntent);

                        break;
                   /*参与人*/
                    case ExtraAndResult.TYPE_SELECT_MULTUI:

                        userId = data.getStringExtra("userId");
                        for (User user : userAllList) {
                            if (user.getId().equals(userId)) {
                                user.setIndex(true);
                            }
                        }
                        totalSize += 1;
                        mHandler.sendEmptyMessage(0x01);

                        break;

                   /*参与人编辑*/
                    case ExtraAndResult.TYPE_SELECT_EDT:

                        userId = data.getStringExtra("userId");
                        for (User user : userAllList) {
                            if (user.getId().equals(userId)) {
                                user.setIndex(true);
                            }
                        }
                        totalSize += 1;
                        mHandler.sendEmptyMessage(0x01);

                        break;
                }
        }
    }
}
