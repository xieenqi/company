package com.loyo.oa.v2.activity.commonview;

import android.os.Bundle;
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
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.beans.Department;
import com.loyo.oa.v2.beans.User;
import com.loyo.oa.v2.common.Common;
import com.loyo.oa.v2.tool.BaseActivity;
import com.loyo.oa.v2.tool.customview.HorizontalScrollListView;
import com.loyo.oa.v2.tool.customview.dragSortListView.DragSortListView;

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

        mDeptSource = Common.getLstDepartment();

        /*全部人员获取*/
        for (int i = 0; i < MainApp.lstDepartment.size(); i++) {
            try {
                for (int k = 0; k < MainApp.lstDepartment.get(i).getUsers().size(); k++) {
                    localCacheUserList.add(MainApp.lstDepartment.get(i).getUsers().get(k));
                }
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        }

        userAllList.addAll(RemoveSame(localCacheUserList));

        deptSort(); //重新排序

        newDeptSource.get(0).getUsers().clear();
        newDeptSource.get(0).getUsers().addAll(userAllList);

    }

    /**
     * 去掉人员重复数据
     */
    private List RemoveSame(final List<User> list) {
        for (int i = 0; i < list.size() - 1; i++) {
            for (int j = i + 1; j < list.size(); j++) {
                if (list.get(i).getId().equals(list.get(j).getId())) {
                    list.remove(j);
                    j--;
                }
            }
        }
        return list;
    }

    /**
     * 根据部门业务结构，对部门列表重新排序
     */
    void deptSort() {
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

        // 部门列表
        mSelectUserDepartmentAdapter = new SelectUserDepartmentAdapter(this, newDeptSource);
        // 用户列表
        mSelectUsersAdapter = new SelectUsersAdapter(this);
        // 多选是横向列表
        mSelectUserOrDepartmentAdapter = new SelectUserHelper.SelectDataAdapter(this);

        mSelectUserOrDepartmentAdapter.updataList(mSelectUserOrDepartment);

        rvDepartments.setAdapter(mSelectUserDepartmentAdapter);
        rvUsers.setAdapter(mSelectUsersAdapter);
        lvSelectUser.setAdapter(mSelectUserOrDepartmentAdapter);

        bindingDataUserToAdapter();
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

        mSelectUserDepartmentAdapter.setOnSelectIndexChangeCallback(new SelectUserDepartmentAdapter.OnSelectIndexChangeCallback() {
            @Override
            public void onChange(int index) {
                mCurrentDepartmentIndex = index;
//                mSelectUsersAdapter.updataList(mDeptSource.get(mCurrentDepartmentIndex).getUsers());
                bindingDataUserToAdapter();
            }
        });
        mSelectUsersAdapter.setOnUserSelectCallback(new SelectUsersAdapter.OnUserSelectCallback() {
            @Override
            public void onUserSelect(Department department, User user) {
                if (mCurrentDepartmentIndex == 0) {
                    isDepartmentAllSelect(getDepartmentByUser(user), user);
                } else {
                    isDepartmentAllSelect(department, user);
                }
            }
        });
        mSelectUsersAdapter.setOnDepartmentAllSelectCallback(new SelectUsersAdapter.OnDepartmentAllSelectCallback() {
            @Override
            public boolean onSelect(boolean isSelect) {
                if (mCurrentDepartmentIndex == 0) {
                    mSelectUserOrDepartment.clear();
                    selectAllDepartmentAndUser(isSelect);
                } else {
                    toSelectAllUserByCurrentDepartment(isSelect);
                }
                return true; // 返回true以刷新列表
            }
        });
    }

    private void selectAllDepartmentAndUser(boolean isSelect) {
        for (int i = 0; i < newDeptSource.size(); i++) {
            newDeptSource.get(i).setIsIndex(isSelect);
        }
        for (int i = 0; i < userAllList.size(); i++) {
            userAllList.get(i).setIndex(isSelect);
        }
        if (isSelect) {
            addDataToUserOrDepartmentAdapter(newDeptSource.get(0), true);
        } else {
            removeDataInUserOrDepartmentAdapter(newDeptSource.get(0), true);
        }
    }

    /**
     * 获取
     *
     * @param user
     * @return
     */
    public Department getDepartmentByUser(User user) {
        String departmentId = user.getDepts().get(0).getShortDept().getId();
        Department department = null;
        for (Department d :
                newDeptSource) {
            if (d.equalsId(departmentId)){
                department = d;
                break;
            }
        }
        return department;
    }

    private void toAllUserByUserSelect1(User user) {
        if (user.isIndex()) {
            for (int i = 0; i < userAllList.size(); i++) {
                if (!userAllList.get(i).isIndex()) {

//                    if (department == null)
//                        return;
//                    isDepartmentAllSelect(department, user);
                    return;
                }
            }
            selectAllDepartmentAndUser(true);
        } else {
            boolean oldSelectStatus = newDeptSource.get(0).isIndex();
        }
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

    /**
     * 判断当前用户被选择时, 他所属的部门是否被全选
     *
     * @param user
     */
    private void isDepartmentAllSelect(Department department, User user) {
        if (!user.isIndex()) {
            boolean isSelectAll = department.isIndex();
            if (isSelectAll) {
                department.setIsIndex(false);
                removeDataInUserOrDepartmentAdapter(department, false);
                for (int i = 0; i < department.getUsers().size(); i++) {
                    addDataToUserOrDepartmentAdapter(department.getUsers().get(i), false);
                }
                removeDataInUserOrDepartmentAdapter(user, true);
                mSelectUsersAdapter.notifyDataSetChanged();
            } else {
                removeDataInUserOrDepartmentAdapter(user, true);
            }
            return;
        }
        for (User u : department.getUsers()) {
            if (!u.isIndex()) {
                if (user.isIndex()) {
                    addDataToUserOrDepartmentAdapter(user, true);
                }
                return;
            }
        }
        department.setIsIndex(true);
        addDataToUserOrDepartmentAdapter(department, true);
    }

    /**
     * 当选中不同部门, 更新用户列表
     */
    private void bindingDataUserToAdapter() {
        mSelectUsersAdapter.updataList(mCurrentDepartmentIndex == 0 ? userAllList : newDeptSource.get(mCurrentDepartmentIndex).getUsers());
        mSelectUsersAdapter.setDepartment(newDeptSource.get(mCurrentDepartmentIndex));
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
