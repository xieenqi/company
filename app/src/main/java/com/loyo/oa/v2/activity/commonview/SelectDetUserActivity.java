package com.loyo.oa.v2.activity.commonview;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
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
import com.loyo.oa.v2.beans.Members;
import com.loyo.oa.v2.beans.NewUser;
import com.loyo.oa.v2.beans.User;
import com.loyo.oa.v2.beans.UserGroupData;
import com.loyo.oa.v2.beans.UserInfo;
import com.loyo.oa.v2.common.Common;
import com.loyo.oa.v2.common.ExtraAndResult;
import com.loyo.oa.v2.tool.BaseActivity;
import com.loyo.oa.v2.tool.LogUtil;
import com.loyo.oa.v2.tool.customview.HorizontalScrollListView;
import com.loyo.oa.v2.tool.customview.RoundImageView;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 部门 人员选择
 * Created by yyy on 15/12/25.
 */
public class SelectDetUserActivity extends BaseActivity {

    public MainApp app = MainApp.getMainApp();
    public ListView leftLv, rightLv;
    private HorizontalScrollListView lv_selectUser;
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
    public SelectDataAdapter selectDataAdapter;
    public ArrayList<User> localCacheUserList = new ArrayList<>(); //本地所有员工 缓存
    public ArrayList<User> userList = new ArrayList<>();
    public ArrayList<User> userAllList = new ArrayList<>(); //所有员工
    public ArrayList<Department> deptSource = new ArrayList<>();//部门数据源｀
    public ArrayList<UserGroupData> totalSource = new ArrayList<>(); //全部数据源
    public ArrayList<Department> newDeptSource = new ArrayList<>();//部门新的顺序
    public ArrayList<Department> deptHead = new ArrayList<>();//一级部门
    public ArrayList<Department> deptOther = new ArrayList<>();//其他部门

    public boolean isAllCheck = false;
    public boolean popy; //当前列表 是否全选
    public int totalSize = 0;
    public int positions = 0;
    public int selectType; //0参与人 1负责人 2编辑参与人
    public String[] joinUserId;
    private TextView tv_selectdetuser_tv;

    private ArrayList<String> selectDeptIds = new ArrayList<>();
    private ArrayList<String> selectUserIds = new ArrayList<>();
    private ArrayList<NewUser> usersList = new ArrayList<>();
    private ArrayList<NewUser> deptsList = new ArrayList<>();
    private ArrayList<User> selectUserList = new ArrayList<User>();//横 list
    private NewUser newUser;
    private Members members;
    private Department companySource;
    public static ArrayList<Department> Data;//组织架构 的缓存
    public static final int selectWhat = 130;
    public static final int SELECT_DETEL_WHAT = 140;

    private Map<String, Integer> mSelectDepartCounts = new HashMap<>();

    /**
     * 用于保存部门中被选中人的数量
     *
     * @param departId
     * @param isAdd
     */
    public void pushSelectCounts(String departId, boolean isAdd) {
        if (mSelectDepartCounts.containsKey(departId)) {
            int count = mSelectDepartCounts.get(departId);
            if (!isAdd && count == 1) {
                mSelectDepartCounts.remove(departId);
            } else {
                mSelectDepartCounts.put(departId, isAdd ? ++count : --count);
            }
        } else {
            if (isAdd) {
                mSelectDepartCounts.put(departId, 1);
            } else {
                LogUtil.d(" 选择数据错误：当前hashmap不存在该departId ");
            }
        }
    }

    /**
     * 用于保存部门中被选中人指定数量
     *
     * @param departId
     * @param count
     */
    public void pushDepartAllCounts(String departId, int count) {
        mSelectDepartCounts.put(departId, count);
    }

    public Handler mHandler = new Handler() {
        @Override
        public void dispatchMessage(final Message msg) {
            super.dispatchMessage(msg);
            switch (msg.what) {
                case 0x01:
                    mUserAdapter.notifyDataSetChanged();
                    btnSure.setText("确定" + "(" + totalSize + ")");
                    break;
                case selectWhat://选择了的人
                    selectUserList.add((User) msg.obj);
                    selectDataAdapter.refreshData();
                    break;
                case SELECT_DETEL_WHAT:
                    for (int i = 0; i < selectUserList.size(); i++) {
                        if (((User) msg.obj).id.equals(selectUserList.get(i).id)) {
                            selectUserList.remove(i);
                        }
                    }
                    selectDataAdapter.refreshData();
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        setContentView(R.layout.activity_selectdetuser);
        Data = MainApp.lstDepartment;// 转存组织架构数据
//        for (Department element : Data) {
//            for (User eleUser : element.getUsers()) {
//                eleUser.index = false;
//            }
//        }
        setTouchView(-1);
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
        members = new Members();
        deptSort();

        /*header初始化*/
        mInflater = LayoutInflater.from(this);
        headerView = mInflater.inflate(R.layout.item_header_selectdetuser, null);
        relAllcheck = (RelativeLayout) headerView.findViewById(R.id.selectdetuser_allcheck);
        checkBox = (CheckBox) headerView.findViewById(R.id.selectdetuser_checkbox);
        leftLv = (ListView) findViewById(R.id.lv_selectdetuser_left);
        rightLv = (ListView) findViewById(R.id.lv_selectdetuser_right);

        //横着的list
        lv_selectUser = (HorizontalScrollListView) findViewById(R.id.lv_selectUser);
        selectDataAdapter = new SelectDataAdapter();
        lv_selectUser.setAdapter(selectDataAdapter);

        btnSure = (Button) findViewById(R.id.btn_title_right);
        llback = (LinearLayout) findViewById(R.id.ll_back);
        tv_selectdetuser_tv = (TextView) findViewById(R.id.tv_selectdetuser_tv);

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
        for (User user : userAllList) {
            user.setIndex(false);
        }
        if (selectType == ExtraAndResult.TYPE_SELECT_SINGLE) {
            btnSure.setVisibility(View.INVISIBLE);
            relAllcheck.setVisibility(View.GONE);
        } else if (selectType == ExtraAndResult.TYPE_SELECT_EDT) {
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
        mDetAdapter = new SelectDetAdapter(mContext, newDeptSource);
        leftLv.setAdapter(mDetAdapter);
        lvOnClick();
        rightLv.addHeaderView(headerView);
        userList.addAll(userAllList);

        /*右侧Lv初始化*/
        mUserAdapter = new SelectUserAdapter(mContext, userList, isAllCheck, mHandler);
        rightLv.setAdapter(mUserAdapter);

    }

    /**
     * 根据部门业务结构，对部门列表重新排序
     */
    void deptSort() {
        /*分别获取一级/其他级部门*/
        for (Department department : deptSource) {
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
     * 去掉人员重复数据
     */
    ArrayList RemoveSame(final ArrayList<User> list) {
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
     * 参与人组装
     */

    void setJoinUsers() {
        testGetJoiner();
        usersList.clear();
        deptsList.clear();

        for (Department department : newDeptSource) {
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
                    newUser.setAvatar(user.avatar);
                    usersList.add(newUser);
                }
            }
        }
        members.depts = deptsList;
        members.users = usersList;
    }

    /**
     * ListView监听
     */
    void lvOnClick() {

        /**横着ListView*/
        lv_selectUser.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(final AdapterView<?> adapterView, final View view, final int position, final long l) {
//                selectUserList.remove(position);
//                selectDataAdapter.refreshData();
//                mHandler.sendEmptyMessage(0x01);
//                for (User element : userAllList) {
//                    if (selectUserList.get(position).id.equals(element.id)) {
//                        element.index = false;
//                    }
//                }
//                mUserAdapter.notifyDataSetChanged();
            }
        });

        /**左侧ListView*/
        leftLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(final AdapterView<?> adapterView, final View view, final int position, final long l) {
                positions = position;
                getInfoUser(positions);
                mHandler.sendEmptyMessage(0x01);
                checkBox.setChecked(popy);

                //选择部门的状态
                mDetAdapter.setSelectedPosition(position);
                mDetAdapter.notifyDataSetChanged();
            }
        });

        /**右侧ListView 加入header后 下标要－1*/
        rightLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(final AdapterView<?> adapterView, final View view, final int position, final long l) {
                /*选负责人时*/
                if (selectType == 1) {
                    Intent mIntent = new Intent();
                    Bundle mBundle = new Bundle();
                    mBundle.putSerializable(User.class.getName(), userList.get(position - 1));
                    mIntent.putExtras(mBundle);
                    app.finishActivity(SelectDetUserActivity.this, MainApp.ENTER_TYPE_LEFT, RESULT_OK, mIntent);
                } else { /*选参与人*/
                    userList.get(position - 1).setIndex(userList.get(position - 1).isIndex() ? false : true);
                    statisticsTotalSize(position);

                    if (popy) {
                        checkBox.setChecked(true);
                    } else if (!popy) {
                        checkBox.setChecked(false);
                    }

                    mHandler.sendEmptyMessage(0x01);
                    if (userList.get(position - 1).isIndex()) {
                        Message msg = new Message();
                        msg.what = SelectDetUserActivity.selectWhat;
                        msg.obj = userList.get(position - 1);
                        mHandler.sendMessage(msg);
                    } else {
                        Message msg = new Message();
                        msg.what = SelectDetUserActivity.SELECT_DETEL_WHAT;
                        msg.obj = userList.get(position - 1);
                        mHandler.sendMessage(msg);
                    }
                }
            }
        });


        /**全选*/
        relAllcheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {

                int hasSet = 0;
                int canSet = 0;
                isAllCheck = isAllCheck ? false : true;
                checkBox.setChecked(isAllCheck);

                /*选择数量统计*/
                for (User user : userList) {
                    if (user.isIndex()) {
                        hasSet++;
                    } else if (!user.isIndex()) {
                        canSet++;
                    }
                    user.setIndex(isAllCheck);

                    if (isAllCheck) {
                        Message msg = new Message();
                        msg.what = SelectDetUserActivity.selectWhat;
                        msg.obj = user;
                        mHandler.sendMessage(msg);
                    } else {
                        Message msg = new Message();
                        msg.what = SelectDetUserActivity.SELECT_DETEL_WHAT;
                        msg.obj = user;
                        mHandler.sendMessage(msg);
                    }
                }

                if (isAllCheck) {
                    totalSize += userList.size() - hasSet;
                } else {
                    totalSize -= userList.size() - canSet;
                }

                mHandler.sendEmptyMessage(0x01);

            }
        });

        /**返回*/
        llback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                finish();
            }
        });

        /**确定*/
        btnSure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                setJoinUsers();
                mIntent = new Intent();
                mBundle = new Bundle();
                if (totalSize != 0) {
                    mBundle.putSerializable(ExtraAndResult.CC_USER_ID, members);
                } else if (totalSize == mUserAdapter.getData().size()) {
                    mBundle.putSerializable(ExtraAndResult.CC_USER_ID, null);
                }
                mIntent.putExtras(mBundle);
                app.finishActivity(SelectDetUserActivity.this, MainApp.ENTER_TYPE_LEFT, RESULT_OK, mIntent);
            }
        });

        /**搜索*/
        tv_selectdetuser_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                MainApp.selectAllUsers = userAllList;
                mBundle = new Bundle();
                mBundle.putInt(ExtraAndResult.STR_SELECT_TYPE, selectType);
                app.startActivityForResult(SelectDetUserActivity.this, SelectDetUserSerach.class, MainApp.ENTER_TYPE_ZOOM_IN, ExtraAndResult.REQUEST_CODE, mBundle);
            }
        });
    }

    /**
     * 获取选中的部门，人员
     */
    void testGetJoiner() {

        selectDeptIds.clear();
        selectUserIds.clear();

        for (Department department : newDeptSource) {
            try {
                dealisAllSelect(department.getUsers());
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
            if (popy) {
                department.setIsIndex(true);
            } else {
                department.setIsIndex(false);
            }

            if (department.isIndex()) {
                selectDeptIds.add(department.getId());
            } else {
                try {
                    for (User user : department.getUsers()) {
                        if (user.isIndex()) {
                            selectUserIds.add(user.getId());
                        }
                    }
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 选中总数统计
     */
    void statisticsTotalSize(final int position) {
        if (userList.get(position - 1).isIndex()) {
            totalSize += 1;
        } else {
            totalSize -= 1;
        }
    }


    /**
     * 判断本次集合中 是否被全选
     */
    void dealisAllSelect(final ArrayList<User> users) {

        for (User user : users) {
            if (user.isIndex()) {
                popy = true;
            } else if (!user.isIndex()) {
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
    void getInfoUser(final int positions) {
        userList.clear();
        for (Department department : newDeptSource) {
            if (department.getXpath().contains(newDeptSource.get(positions).getXpath())) {
                try {
                    for (User user : department.getUsers()) {
                        userList.add(user);
                    }
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
            }
        }
        dealisAllSelect(userList);
    }


    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode != ExtraAndResult.REQUEST_CODE || data == null) {
            return;
        }
        int selectTypePage = 999;
        switch (requestCode) {
           /*选人搜索回调*/
            case ExtraAndResult.REQUEST_CODE:

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
                        getSelectUser(data.getStringExtra("userId"));
                        break;
                   /*参与人编辑*/
                    case ExtraAndResult.TYPE_SELECT_EDT:
                        getSelectUser(data.getStringExtra("userId"));
                        break;
                    default:
                }
            default:

                break;
        }
    }

    private void getSelectUser(final String userId) {
        for (User user : userAllList) {
            if (user.getId().equals(userId)) {
                user.setIndex(true);

                Message msg = new Message();
                msg.what = SelectDetUserActivity.selectWhat;
                msg.obj = user;
                mHandler.sendMessage(msg);
            }
        }
        totalSize += 1;
        mHandler.sendEmptyMessage(0x01);
    }

    /**
     * 选择的人员的显示
     */
    public class SelectDataAdapter extends BaseAdapter {

        private List<SelectUserBase> mSelectList = new ArrayList<>();

        public void updataList(List<SelectUserBase> data) {
            if (data == null) {
                data = new ArrayList<>();
            }
            this.mSelectList = data;
            notifyDataSetChanged();
        }

        public void removeById(String id) {
            removeById(id, 0);
        }

        public void removeById(String id, int startIndex) {
            if (startIndex >= mSelectList.size())
                return;
            for (int i = startIndex; i < mSelectList.size(); i++) {
                if (mSelectList.get(i).equalsId(id)) {
                    mSelectList.remove(i--);
                }
            }
        }

        @Override
        public int getCount() {
            return selectUserList.size();
        }

        @Override
        public Object getItem(final int position) {
            return null;
        }

        @Override
        public long getItemId(final int position) {
            return 0;
        }

        /**
         * 刷新 数据
         */
        public void refreshData() {
            Set<User> userSet = new HashSet<>();
            userSet.addAll(selectUserList);
            selectUserList.clear();
            selectUserList.addAll(userSet);
            String xpath = "";
            for (int i = 0; i < selectUserList.size(); i++) {
                ArrayList<UserInfo> dept = selectUserList.get(i).depts;
                for (int j = 0; j < dept.size(); j++) {
                    if (xpath.equals(dept.get(j).getShortDept().getXpath())) {
                        selectDept(dept.get(j).getShortDept().getXpath());
                        continue;
                    }
                    xpath = dept.get(j).getShortDept().getXpath();
                }
            }
            notifyDataSetChanged();
        }

        public String selectDept(final String xpath) {
            for (Department user : Data) {
//                if (xpath.equals(user.getDepts().get(0).getShortDept().getXpath())) {
//                    return user.getDepts().get(0).getShortDept().getXpath() ;
//                }
            }
            return "";
        }

        @Override
        public View getView(final int position, View convertView, final ViewGroup parent) {
            Holder holder = null;
            if (convertView == null) {
                holder = new Holder();
                convertView = LayoutInflater.from(SelectDetUserActivity.this).inflate(R.layout.item_select_user, null);
                holder.head = (RoundImageView) convertView.findViewById(R.id.riv_head);
                holder.name = (TextView) convertView.findViewById(R.id.tv_name);
                convertView.setTag(holder);
            } else {
                holder = (Holder) convertView.getTag();
            }
            ImageLoader.getInstance().displayImage(selectUserList.get(position).getAvatar(), holder.head);
            //holder.name.setText(selectUserList.get(position).name);
            LogUtil.d("刷新的数九：" + selectUserList.size());
            return convertView;
        }

        class Holder {
            RoundImageView head;
            TextView name;

            @Override
            public boolean equals(Object o) {
                return super.equals(o);
            }
        }
    }

    public interface SelectUserBase {
        String getId();

        String getAvater();

        int getUserCount();

        boolean equalsId(String id);
    }
}
