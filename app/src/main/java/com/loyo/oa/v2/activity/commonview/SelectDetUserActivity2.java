package com.loyo.oa.v2.activity.commonview;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.adapter.SelectUserDepartmentAdapter;
import com.loyo.oa.v2.adapter.SelectUsersAdapter;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.beans.Department;
import com.loyo.oa.v2.beans.Members;
import com.loyo.oa.v2.beans.SelectDepData;
import com.loyo.oa.v2.beans.SelectUserData;
import com.loyo.oa.v2.beans.User;
import com.loyo.oa.v2.common.Common;
import com.loyo.oa.v2.common.ExtraAndResult;
import com.loyo.oa.v2.tool.BaseActivity;
import com.loyo.oa.v2.tool.LogUtil;
import com.loyo.oa.v2.tool.customview.HorizontalScrollListView;

import java.util.ArrayList;
import java.util.List;

public class SelectDetUserActivity2 extends BaseActivity implements View.OnClickListener {

    public static final int TYPE_ONLY = 0x001; // 单选
    public static final int TYPE_MULTI_SELECT = 0x000; // 多选不支持全选
    public static final int TYPE_ALL_SELECT = 0x002; // 多选支持全选

    public static final int REQUEST_ONLY = 0x00100;
    public static final int REQUEST_MULTI_SELECT = 0x00101;
    public static final int REQUEST_ALL_SELECT = 0x00102;

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

    private List<SelectUserHelper.SelectUserBase> mSelectUserOrDepartment = new ArrayList<>(); // 多选时的选中列表

    private SelectUserDepartmentAdapter mSelectUserDepartmentAdapter; // 部门列表
    private SelectUsersAdapter mSelectUsersAdapter; // 用户列表
    private SelectUserHelper.SelectDataAdapter mSelectUserOrDepartmentAdapter; // 选中列表
    private boolean isOnce = true;
    private int mCurrentDepartmentIndex = 0; // 当前选中的部门
    private static boolean isSelctDepat = true;//是否要可以选择部门 默认可以选择部门
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SelectUserHelper.SelectThread.OK:
                    MainApp.selectAllUsers = SelectUserHelper.useAlllist;
                    updata();
                    if (!TextUtils.isEmpty(mJoinUserId)) {
                        toJoinUserIds(mJoinUserId);
                    }
                    cancelLoading();
                    break;
                case SelectUserHelper.SelectThread.FAILURE:
                    cancelLoading();
                    break;
            }
        }
    };

    /**
     * @param mJoinUserId
     */
    private void toJoinUserIds(String mJoinUserId) {
        String[] ids = mJoinUserId.split(",");
        lab:
        for (int i = 0; i < ids.length; i++) {
            String id = ids[i];
            for (int j = 0; j < SelectUserHelper.mSelectDatas.get(0).getUsers().size(); j++) {
                String itemId = SelectUserHelper.mSelectDatas.get(0).getUsers().get(j).getId();
                if (id.equals(itemId)) {
                    SelectUserHelper.mSelectDatas.get(0).getUsers().get(j).setCallbackSelect(true);
                    continue lab;
                }
            }
            for (int j = 0; isAllowAllSelect() && j < SelectUserHelper.mSelectDatas.size(); j++) {
                String itemId = SelectUserHelper.mSelectDatas.get(j).getId();
                if (id.equals(itemId)) {
                    SelectUserHelper.mSelectDatas.get(j).setAllSelect(true);
                    continue lab;
                }
            }
        }
    }

    private SelectUserData.OnDepChangeCallback mDepChangeCallback = new SelectUserData.OnDepChangeCallback() {

        @Override
        public void onDepAllChange(SelectDepData data) {
            /*if (!isAllowAllSelect())
                return;*/
            SelectUserHelper.addDepNoChangeItem(data);
            mSelectUserOrDepartmentAdapter.notifyDataSetChanged();
            mSelectUsersAdapter.notifyDataSetChanged();
        }

        @Override
        public void onDepChange(List<SelectDepData> datas) {
            /*if (!isAllowAllSelect())
                return;*/
            for (int i = 0; i < datas.size(); i++) {
                if (isSelctDepat) {
                    SelectUserHelper.addSelectUserChangeDep(datas.get(i));
                }
            }
            mSelectUsersAdapter.notifyDataSetChanged();
            mSelectUserOrDepartmentAdapter.notifyDataSetChanged();
        }

        @Override
        public void addSelectUserItem(SelectUserData data) {
            /*if (!isAllowAllSelect())
                return;*/
            if (SelectUserHelper.addSelectItem(data)) {
                mSelectUserOrDepartmentAdapter.notifyDataSetChanged();
            }
        }

        @Override
        public void removeSelectUserItem(SelectUserData data) {
            /*if (!isAllowAllSelect())
                return;*/
            if (SelectUserHelper.removeSelectItem(data)) {
                mSelectUserOrDepartmentAdapter.notifyDataSetChanged();
            }
        }
    };
    private int mSelectType;
    private int mCurrentSelectCount = 0;
    private String mJoinUserId;

    /**
     * 是否支持全选
     *
     * @return
     */
    private boolean isAllowAllSelect() {
        return mSelectType == TYPE_ALL_SELECT;
    }

    /**
     * 启动单选界面
     *
     * @param act
     */
    public static void startThisForOnly(Activity act, String joinUserId) {
        Intent intent = new Intent(act, SelectDetUserActivity2.class);
        intent.putExtra(ExtraAndResult.STR_SELECT_TYPE, TYPE_ONLY);
        intent.putExtra(ExtraAndResult.STR_SUPER_ID, joinUserId);
        act.startActivityForResult(intent, REQUEST_ONLY);
    }

    /**
     * 启动多选不支持全选的界面  设置是否选择呢部门
     *
     * @param act
     */
    public static void startThisForMulitSelect(Activity act, String joinUserId, boolean isSelctAllDept) {
        isSelctDepat = isSelctAllDept;
        startThisForMulitSelect(act, joinUserId);
    }

    /**
     * 启动多选不支持全选的界面
     *
     * @param act
     */
    public static void startThisForMulitSelect(Activity act, String joinUserId) {
        Intent intent = new Intent(act, SelectDetUserActivity2.class);
        intent.putExtra(ExtraAndResult.STR_SELECT_TYPE, TYPE_MULTI_SELECT);
        intent.putExtra(ExtraAndResult.STR_SUPER_ID, joinUserId);
        act.startActivityForResult(intent, REQUEST_MULTI_SELECT);
    }

    /**
     * 启动多选支持全选的界面
     *
     * @param act
     */
    public static void startThisForAllSelect(Activity act, String joinUserId, boolean isSelctAllDept) {
        isSelctDepat = isSelctAllDept;
        Intent intent = new Intent(act, SelectDetUserActivity2.class);
        intent.putExtra(ExtraAndResult.STR_SELECT_TYPE, TYPE_ALL_SELECT);
        intent.putExtra(ExtraAndResult.STR_SUPER_ID, joinUserId);
        act.startActivityForResult(intent, REQUEST_ALL_SELECT);
    }

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

        mSelectType = getIntent().getIntExtra(ExtraAndResult.STR_SELECT_TYPE, TYPE_ALL_SELECT);
        mJoinUserId = getIntent().getStringExtra(ExtraAndResult.STR_SUPER_ID);
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

        if (mSelectType == TYPE_ONLY) {
            btnTitleRight.setVisibility(View.GONE);
        } else {
            btnTitleRight.setVisibility(View.VISIBLE);
        }

//        if (!isDataBinded()) {
        showLoading("数据正在加载...");
        mDeptSource = Common.getLstDepartment();
        deptSort(); //重新排序
        SelectUserHelper.mCurrentSelectDatas.clear(); // 清空选中列表
        SelectUserHelper.SelectThread thread = new SelectUserHelper.SelectThread(newDeptSource, mHandler);
        thread.start();
//        } else {
//            updata();
//        }
    }

    private void initListener() {
        llBack.setOnClickListener(this);
        btnTitleRight.setOnClickListener(this);
        tv_toSearch.setOnClickListener(this);

        lvSelectUser.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SelectUserData data = SelectUserHelper.mCurrentSelectDatas.get(position);
                if (data.getClass() == SelectDepData.class) {
                    ((SelectDepData) data).setAllSelect(false);
                    LogUtil.dee("1");
                } else if (data.getClass() == SelectUserData.class) {
                    ((SelectUserData) data).setCallbackSelect(false);
                    mSelectUsersAdapter.notifyDataSetChanged();
                    LogUtil.dee("2");
                } else {
                    LogUtil.dee("3");
                    data.setCallbackSelect(false);
                }
            }
        });
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
            // 监听选中列表数量变化
            mSelectUserOrDepartmentAdapter.setDataChangeCallback(new SelectUserHelper.OnDataChangeCallback() {
                @Override
                public void onChange(int count) {
                    mCurrentSelectCount = count;
                    btnTitleRight.setText("确定" + (count == 0 ? "" : "(" + count + ")"));
                }
            });
        }

        if (mSelectUsersAdapter == null) {
            // 用户列表
            mSelectUsersAdapter = new SelectUsersAdapter(this);
            mSelectUsersAdapter.setAlone(!isAllowAllSelect());
            mSelectUsersAdapter.setOnUserSelectCallback(new SelectUsersAdapter.OnUserSelectCallback() {
                @Override
                public void onUserSelect(SelectUserData data) {
                    if (mSelectType == TYPE_ONLY) {
                        Intent intent = new Intent();
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("data", data.toNewUser());
                        intent.putExtras(bundle);
                        app.finishActivity(SelectDetUserActivity2.this, MainApp.ENTER_TYPE_LEFT, RESULT_OK, intent);
                    } else if (mSelectType == TYPE_MULTI_SELECT) {
                        if (data.isSelect()) {
                            if (SelectUserHelper.addSelectItem(data)) {
                                mSelectUserOrDepartmentAdapter.notifyDataSetChanged();
                            }
                        } else {
                            if (SelectUserHelper.removeSelectItem(data)) {
                                mSelectUserOrDepartmentAdapter.notifyDataSetChanged();
                            }
                        }
                    }
                }
            });
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


    /**
     * 绑定数据到用户adapter, 并定位选中项
     */
    private void bindDataToUserAdapter(int index) {
        if (SelectUserHelper.mSelectDatas.size() == 0) {
            return;
        }
        SelectDepData data = SelectUserHelper.mSelectDatas.get(mCurrentDepartmentIndex);
        mSelectUsersAdapter.setDepartment(data);
        mSelectUsersAdapter.updataList(data.getUsers());
        mUserLayoutManager.scrollToPosition(index);
    }

    private void assignViews() {
        tv_toSearch = (TextView) findViewById(R.id.tv_toSearch);
        btnTitleRight = (Button) findViewById(R.id.btn_title_right);
        llBack = (LinearLayout) findViewById(R.id.ll_back);
        lvSelectUser = (HorizontalScrollListView) findViewById(R.id.lv_selectUser);
        rvDepartments = (RecyclerView) findViewById(R.id.rv_departments);
        rvUsers = (RecyclerView) findViewById(R.id.rv_users);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_back:
                finish();
                break;
            case R.id.btn_title_right:
//                if (mCurrentSelectCount == 0) {
//                    finish();
//                    return;
//                }
                Bundle extras = new Bundle();
                Members members = new Members();
                getResultData(members, SelectUserHelper.mCurrentSelectDatas);
                extras.putSerializable("data", members);
                setResult(RESULT_OK, new Intent().putExtras(extras));
                finish();
                break;
            case R.id.tv_toSearch:
                Bundle bundle = new Bundle();
                bundle.putInt(ExtraAndResult.STR_SELECT_TYPE, mSelectType);
                app.startActivityForResult(SelectDetUserActivity2.this,
                        SelectDetUserSerach.class,
                        MainApp.ENTER_TYPE_ZOOM_IN,
                        ExtraAndResult.REQUEST_CODE, bundle);
                break;
            default:

                break;
        }
    }

    private void getResultData(final Members data, final List<SelectUserData> mCurrentSelectDatas) {
        for (int i = 0; i < mCurrentSelectDatas.size(); i++) {
            SelectUserData userData = mCurrentSelectDatas.get(i);
            if (userData.getClass() == SelectDepData.class) {
                data.depts.add(userData.toNewUser());
            } else {
                data.users.add(userData.toNewUser());
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == ExtraAndResult.REQUEST_CODE && data != null) {
            if (requestCode == ExtraAndResult.REQUEST_CODE) {
                int selectTypePage = -1;
                try {
                    selectTypePage = data.getIntExtra(ExtraAndResult.STR_SELECT_TYPE, 0);
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }

                switch (selectTypePage) {
                   /*负责人*/
                    case ExtraAndResult.TYPE_SELECT_SINGLE:
                        Intent intent = new Intent();
                        Bundle bundle = new Bundle();
                        User user = (User) data.getSerializableExtra(User.class.getName());
                        bundle.putSerializable("data", user.toShortUser());
                        intent.putExtras(bundle);
                        app.finishActivity(SelectDetUserActivity2.this, MainApp.ENTER_TYPE_LEFT, RESULT_OK, intent);
                        break;
                   /*参与人*/
                    case ExtraAndResult.TYPE_SELECT_MULTUI:
                        getSelectUser(data.getStringExtra("userId"));
                        break;
                   /*参与人编辑*/
                    case ExtraAndResult.TYPE_SELECT_EDT:
                        getSelectUser(data.getStringExtra("userId"));
                        break;
                    default:
                }
            }
        }
    }

    private void getSelectUser(String userId) {
        if (TextUtils.isEmpty(userId) && SelectUserHelper.mSelectDatas.size() > 0) {
            return;
        }
        List<SelectUserData> users = SelectUserHelper.mSelectDatas.get(0).getUsers();
        if (users != null && users.size() > 0) {
            for (int i = 0; i < users.size(); i++) {
                if (userId.equals(users.get(i).getId())) {
                    mCurrentDepartmentIndex = 0;
                    bindDataToUserAdapter(i);
                    users.get(i).setCallbackSelect(true);
                    if (!isAllowAllSelect()) {
                        SelectUserHelper.addSelectItem(users.get(i));
                        mSelectUserOrDepartmentAdapter.notifyDataSetChanged();
                    }
                    return;
                }
            }
        }
    }

}
