package com.loyo.oa.v2.activity.commonview;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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
import com.loyo.oa.v2.beans.User;
import com.loyo.oa.v2.beans.UserGroupData;
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
    public ArrayList<User> userList;
    public ArrayList<User> userAllList; //所有员工
    public ArrayList<Department> deptSource;//部门数据源｀
    public ArrayList<UserGroupData> totalSource; //全部数据源
    public boolean isAllCheck = false;
    public boolean popy; //当前列表 是否全选
    public int totalSize = 0;
    public int positions = 0;
    public int seltSize;
    public int isSize;
    public int selectType; //0参与人 1负责人 2编辑参与人
    public String[] joinUserId;
    public StringBuffer nameApd;
    public StringBuffer idApd;
    private TextView tv_selectdetuser_tv;


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
                userAllList.add(MainApp.lstDepartment.get(i).getUsers().get(k));
            }
        }

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

    void lvOnClick() {

        /*左侧ListView*/
        leftLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                positions = position;
                getInfoUser(positions);

                mHandler.sendEmptyMessage(0x01);
                checkBox.setChecked(popy);

            }
        });

        /*右侧ListView 加入header后 下标要－1*/
        rightLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                userList.get(position - 1).setIndex(userList.get(position - 1).isIndex() ? false : true);
                statisticsTotalSize(position);
                dealisAllSelect(userList);

                if (popy) {
                    checkBox.setChecked(true);
                } else if (!popy) {
                    checkBox.setChecked(false);
                }

                mHandler.sendEmptyMessage(0x01);

                if (selectType == 1) {
                    Intent mIntent = new Intent();
                    Bundle mBundle = new Bundle();
                    mBundle.putSerializable(User.class.getName(), userList.get(position - 1));
                    mIntent.putExtras(mBundle);
                    app.finishActivity(SelectDetUserActivity.this, MainApp.ENTER_TYPE_LEFT, RESULT_OK, mIntent);
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

                dealDeptContent();
                mIntent = new Intent();
                mBundle = new Bundle();
                if(totalSize != 0){
                    mBundle.putString(ExtraAndResult.CC_USER_ID, idApd.toString());
                    mBundle.putString(ExtraAndResult.CC_USER_NAME, nameApd.toString());
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
     * 选中总数统计
     */
    void statisticsTotalSize(int position) {
        if (userList.get(position - 1).isIndex()) {
            totalSize += 1;
        } else {
            totalSize -= 1;
        }
    }


    void dealDeptContent() {

        idApd = new StringBuffer();
        nameApd = new StringBuffer();
        String xPath = null;
        userList.clear();

        for (int i = 0; i < deptSource.size(); i++) {

            isSize = 0;
            seltSize = 0;
            userList.clear();

            for (User user : userAllList) {
                xPath = user.depts.get(0).getShortDept().getXpath();
                if (xPath.contains(deptSource.get(i).getXpath())) {
                    userList.add(user);
                }
            }

            if (userList.size() != 0) {
                seltSize = userList.size();

                for (User user : userList) {
                    if (user.isIndex()) {
                        isSize++;
                    }
                }

                if (seltSize == isSize) {
                    nameApd.append(deptSource.get(i).getName() + ",");
                } else if (seltSize != isSize) {
                    for (User user : userList) {
                        if (user.isIndex()) {
                            if (!nameApd.toString().contains(user.getRealname())) {
                                idApd.append(user.getId() + ",");
                                nameApd.append(user.getRealname() + ",");
                            }
                        }
                    }
                }
            }

            LogUtil.dll("结果name:" + nameApd.toString());
            LogUtil.dll("结果id:" + idApd.toString());

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
     * 判断当前用户是否属于本部门，xpath判断
     */
    void getInfoUser(int positions) {
        String xPath = null;
        userList.clear();
        for (User user : userAllList) {
            xPath = user.depts.get(0).getShortDept().getXpath();
            if (xPath.contains(deptSource.get(positions).getXpath())) {
                userList.add(user);
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

                try{
                     selectTypePage = data.getIntExtra(ExtraAndResult.STR_SELECT_TYPE, 0);
                }catch (NullPointerException e){
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


    //************************************************************暂时弃用**********************************************************

    /**
     * 获取改部门下 所有员工
     */
    void setData(int position, ArrayList<User> userList) {

        userList.clear();
        String Id = deptSource.get(position).getId();

        assUserList(userList, Common.getLstDepartment(Id), Common.getListUser(Id));
        dealisAllSelect(userList);

    }


    /**
     * 拣取人员
     */
    void assUserList(ArrayList<User> arrayList, ArrayList<Department> departments, ArrayList<User> users) {

        if (users.size() != 0) {
            arrayList.addAll(users);
        }

        if (departments.size() != 0) {

            for (Department department : departments) {
                String id = department.getId();
                ArrayList<Department> dept = Common.getLstDepartment(id);
                ArrayList<User> user = Common.getListUser(id);
                assUserList(arrayList, dept, user);
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
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
