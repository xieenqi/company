package com.loyo.oa.v2.activity.commonview;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.adapter.SelectUserDepartmentAdapter;
import com.loyo.oa.v2.adapter.SelectUsersAdapter;
import com.loyo.oa.v2.beans.Department;
import com.loyo.oa.v2.beans.SelectDepData;
import com.loyo.oa.v2.beans.SelectUserData;
import com.loyo.oa.v2.beans.User;
import com.loyo.oa.v2.common.Common;
import com.loyo.oa.v2.tool.BaseActivity;
import com.loyo.oa.v2.tool.customview.HorizontalScrollListView;

import java.util.ArrayList;
import java.util.List;

public class SelectDetUserActivity2 extends BaseActivity implements View.OnClickListener {
    private LinearLayout llBack;
    private Button btnTitleRight;
    private HorizontalScrollListView lvSelectUser;
    private TextView tv_toSearch;
    private RecyclerView rvDepartments;
    private RecyclerView rvUsers;
    private int screenHeight;
    private int screenWidth;

    private LinearLayoutManager mDepartmentLayoutManager;
    private LinearLayoutManager mUserLayoutManager;

    private List<Department> mDeptSource = new ArrayList<>(); // 部门和用户集合
    private List<Department> newDeptSource = new ArrayList<>();//部门新的顺序
    private List<Department> deptHead = new ArrayList<>();//一级部门
    private List<Department> deptOther = new ArrayList<>();//其他部门

    private List<User> localCacheUserList = new ArrayList<>(); // 用于缓存所有用户, 有重复
    private List<User> userAllList = new ArrayList<>(); // 用于缓存所有用户, 无重复

    private List<SelectUserHelper.SelectUserBase> mSelectUserOrDepartment = new ArrayList<>(); // 多选时的选中列表
    private List<User> mSelectUsers = new ArrayList<>(); // 当不能全选时, 只能添加用户
    private User mSelectUser; // 单选

    private SelectUserDepartmentAdapter mSelectUserDepartmentAdapter;
    private SelectUsersAdapter mSelectUsersAdapter;
    private int mCurrentDepartmentIndex = 0; // 当前选中的部门
    private SelectUserHelper.SelectDataAdapter mSelectUserOrDepartmentAdapter;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SelectUserHelper.SelectThread.OK:
                    cancelLoading();
                    updata();
                    break;
                case SelectUserHelper.SelectThread.FAILURE:
                    cancelLoading();
                    break;
            }
        }
    };

    private SelectUserData.OnDepChangeCallback mDepChangeCallback = new SelectUserData.OnDepChangeCallback() {

        @Override
        public void onDepAllChange(SelectDepData data) {
            SelectUserHelper.addDepNoChangeItem(data);
            mSelectUserOrDepartmentAdapter.notifyDataSetChanged();
            mSelectUsersAdapter.notifyDataSetChanged();
        }

        @Override
        public void onDepChange(List<SelectDepData> datas) {
            boolean ischange = false;
            for (int i = 0; i < datas.size(); i++) {
                if (SelectUserHelper.addSelectUserChangeDep(datas.get(i))) {
                    ischange = true;
                }
            }
            mSelectUsersAdapter.notifyDataSetChanged();
            if (ischange) {
                mSelectUserOrDepartmentAdapter.notifyDataSetChanged();
            }
        }

        @Override
        public void addSelectUserItem(SelectUserData data) {
            if (SelectUserHelper.addSelectItem(data)) {
                mSelectUserOrDepartmentAdapter.notifyDataSetChanged();
            }
        }

        @Override
        public void removeSelectUserItem(SelectUserData data) {
            if (SelectUserHelper.removeSelectItem(data)) {
                mSelectUserOrDepartmentAdapter.notifyDataSetChanged();
            }
        }
    };
    private boolean isOnce = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_det_user2);
        initData();
        initView();
        initListener();
    }

    private void initData() {
        // 获取屏幕高度\宽度
        screenHeight = this.getWindowManager().getDefaultDisplay().getHeight();
        screenWidth = this.getWindowManager().getDefaultDisplay().getWidth();
    }

    /**
     * 判断数据是否生成
     *
     * @return
     */
    public boolean isDataBinded() {
        return SelectUserHelper.mSelectDatas == null && SelectUserHelper.mSelectDatas.size() > 0;
    }

    /**
     * 根据部门业务结构，对部门列表重新排序
     */
    private void deptSort() {
        /*分别获取一级/其他级部门*/
        for (Department department : mDeptSource) {
            if (department.getXpath().split("/").length == 2) {
                deptHead.add(department);
            } else if (!department.getXpath().contains("/")) {
                deptHead.add(department);
            } else {
                deptOther.add(department);
            }
        }

        /*根据Xpath,把部门按照一级/二级顺序排序,排除掉公司数据*/
        for (Department dept1 : deptHead) {
            newDeptSource.add(dept1);
            for (Department dept2 : deptOther) {
                if (dept2.getXpath().contains(dept1.getXpath()) && dept1.getXpath().indexOf("/") != -1) {
                    newDeptSource.add(dept2);
                }
            }
        }

        Department companySource = null;

        /*把公司数据，移动到首位*/
        for (int i = 0; i < newDeptSource.size(); i++) {
            if (newDeptSource.get(i).getXpath().indexOf("/") == -1) {
                companySource = newDeptSource.get(i);
                newDeptSource.remove(i);
                break;
            }
        }
        newDeptSource.add(0, companySource);
    }

    private void initView() {
        assignViews();

        // 设置部门与用户的layout的宽度比为1：2
        ViewGroup.LayoutParams lp_department = rvDepartments.getLayoutParams();
        lp_department.width = screenWidth / 3;
        rvDepartments.setLayoutParams(lp_department);

        mDepartmentLayoutManager = new LinearLayoutManager(this);
        mDepartmentLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        mUserLayoutManager = new LinearLayoutManager(this);
        mUserLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        rvDepartments.setLayoutManager(mDepartmentLayoutManager);
        rvUsers.setLayoutManager(mUserLayoutManager);

        if (!isDataBinded()) {
            showLoading("数据正在加载...");
            mDeptSource = Common.getLstDepartment();
            deptSort(); //重新排序
            SelectUserHelper.SelectThread thread = new SelectUserHelper.SelectThread(newDeptSource, mHandler);
            thread.start();
        } else {
            updata();
        }
    }

    /**
     * 刷新界面数据
     */
    public void updata() {
        if (isOnce) {
            isOnce = false;
            // 界面第一次进入时为部门数据添加选中监听
            for (int i = 0; i < SelectUserHelper.mSelectDatas.size(); i++) {
                SelectUserHelper.mSelectDatas.get(i).setmDepChangeCallback(mDepChangeCallback);
            }
        }
        if (mSelectUserDepartmentAdapter == null) {
            // 部门列表
            mSelectUserDepartmentAdapter = new SelectUserDepartmentAdapter(this);
            rvDepartments.setAdapter(mSelectUserDepartmentAdapter);
            mSelectUserDepartmentAdapter.setOnSelectIndexChangeCallback(new SelectUserDepartmentAdapter.OnSelectIndexChangeCallback() {
                @Override
                public void onChange(int index) {
                    mCurrentDepartmentIndex = index;
                    bindDataToUserAdapter();
                }
            });
        }

        if (mSelectUserOrDepartmentAdapter == null) {
            // 多选是横向列表
            mSelectUserOrDepartmentAdapter = new SelectUserHelper.SelectDataAdapter(this);
            lvSelectUser.setAdapter(mSelectUserOrDepartmentAdapter);
        }

        if (mSelectUsersAdapter == null) {
            // 用户列表
            mSelectUsersAdapter = new SelectUsersAdapter(this);
            rvUsers.setAdapter(mSelectUsersAdapter);
        }

        mSelectUserOrDepartmentAdapter.updataList(SelectUserHelper.mCurrentSelectDatas);
        mSelectUserDepartmentAdapter.updataList(SelectUserHelper.mSelectDatas);
        bindDataToUserAdapter();
    }

    /**
     * 绑定数据到用户adapter
     */
    private void bindDataToUserAdapter() {
        if (SelectUserHelper.mSelectDatas.size() == 0) {
            return;
        }
        mSelectUsersAdapter.setDepartment(SelectUserHelper
                .mSelectDatas
                .get(mCurrentDepartmentIndex));
        mSelectUsersAdapter.updataList(SelectUserHelper
                .mSelectDatas
                .get(mCurrentDepartmentIndex)
                .getUsers());
    }

    private void assignViews() {
        tv_toSearch = (TextView) findViewById(R.id.tv_toSearch);
        btnTitleRight = (Button) findViewById(R.id.btn_title_right);
        llBack = (LinearLayout) findViewById(R.id.ll_back);
        lvSelectUser = (HorizontalScrollListView) findViewById(R.id.lv_selectUser);
        rvDepartments = (RecyclerView) findViewById(R.id.rv_departments);
        rvUsers = (RecyclerView) findViewById(R.id.rv_users);
    }

    private void initListener() {
        llBack.setOnClickListener(this);
        btnTitleRight.setOnClickListener(this);
        tv_toSearch.setOnClickListener(this);

//        mSelectUsersAdapter.setOnUserSelectCallback(new SelectUsersAdapter.OnUserSelectCallback() {
//            @Override
//            public void onUserSelect(Department department, User user) {
//                if (mCurrentDepartmentIndex == 0) {
//                    isDepartmentAllSelect(getDepartmentByUser(user), user);
//                } else {
//                    isDepartmentAllSelect(department, user);
//                }
//            }
//        });
//        mSelectUsersAdapter.setOnDepartmentAllSelectCallback(new SelectUsersAdapter.OnDepartmentAllSelectCallback() {
//            @Override
//            public boolean onSelect(boolean isSelect) {
//                if (mCurrentDepartmentIndex == 0) {
//                    mSelectUserOrDepartment.clear();
//                    selectAllDepartmentAndUser(isSelect);
//                } else {
//                    toSelectAllUserByCurrentDepartment(isSelect);
//                }
//                return true; // 返回true以刷新列表
//            }
//        });
    }


    /**
     * 选择后，选横向列表添加数据
     */
    private void addDataToUserOrDepartmentAdapter(SelectUserHelper.SelectUserBase userBase, boolean notify) {
        if (userBase.isDepart()) {
            String departmentId = userBase.getDepartId();
            for (int i = mSelectUserOrDepartment.size() - 1; i >= 0; i--) {
                SelectUserHelper.SelectUserBase base = mSelectUserOrDepartment.get(i);
                if (base instanceof User) {
                    User user = (User) base;
                    if (user.isExistDepartment(departmentId)) {
                        mSelectUserOrDepartment.remove(i);
                    }
                } else if (base instanceof Department) {
                    Department department = (Department) base;
                    if (department.equalsId(departmentId)) {
                        return;
                    }
                }
            }
            if (!mSelectUserOrDepartment.contains(userBase)) {
                mSelectUserOrDepartment.add(0, userBase);
            }
        } else {
            if (!mSelectUserOrDepartment.contains(userBase)) {
                mSelectUserOrDepartment.add(0, userBase);
            }
        }
        if (notify)
            mSelectUserOrDepartmentAdapter.notifyDataSetChanged();
    }

    /**
     * 移除选择的用户或部门
     *
     * @param base
     * @param notify
     */
    private void removeDataInUserOrDepartmentAdapter(SelectUserHelper.SelectUserBase base, boolean notify) {
        if (base.isDepart()) {
            mSelectUserOrDepartment.remove(base);
        } else {
            mSelectUserOrDepartment.remove(base);
        }
        if (notify)
            mSelectUserOrDepartmentAdapter.notifyDataSetChanged();
    }

    /**
     * 当用户选择全选或取消全选时更改用户状态
     *
     * @param isSelect
     */
    private void toSelectAllUserByCurrentDepartment(boolean isSelect) {
        boolean oldIndex = newDeptSource.get(mCurrentDepartmentIndex).isIndex();
        newDeptSource.get(mCurrentDepartmentIndex).setIsIndex(isSelect);
        List<User> users = newDeptSource.get(mCurrentDepartmentIndex).getUsers();
        for (int i = 0; i < users.size(); i++) {
            User user = users.get(i);
            user.setIndex(isSelect);
        }
        if (isSelect) {
            addDataToUserOrDepartmentAdapter(newDeptSource.get(mCurrentDepartmentIndex), true);
        } else {
            removeDataInUserOrDepartmentAdapter(newDeptSource.get(mCurrentDepartmentIndex), true);
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_back:
                finish();
                break;
            case R.id.btn_title_right:

                break;
            case R.id.tv_toSearch:

                break;
            default:

                break;
        }
    }
}
