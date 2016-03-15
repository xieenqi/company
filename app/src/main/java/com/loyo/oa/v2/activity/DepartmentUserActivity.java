package com.loyo.oa.v2.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import com.loyo.oa.v2.R;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.beans.User;
import com.loyo.oa.v2.common.Common;
import com.loyo.oa.v2.fragment.DepartmentFragment;
import com.loyo.oa.v2.fragment.UserFragment;
import com.loyo.oa.v2.tool.StringUtil;
import com.loyo.oa.v2.tool.ViewUtil;
import com.loyo.oa.v2.tool.customview.PagerSlidingTabStrip;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * 参与人 负责人 点评人 抄送人 选择界面
 * */

public class DepartmentUserActivity extends FragmentActivity implements View.OnClickListener {

    public static final int request_Code = 100;
    public static final String MSG_TYPE_MULTI = "com.loyo.oa.v2.multi";
    public static final String STR_SHOW_TYPE = "show_type";
    public static final String STR_SELECT_TYPE = "select_type";
    public static final String STR_SUPER_ID = "super_id";
    public static final String STR_SUPER_NAME = "super_name";
    public static final String CC_DEPARTMENT_ID = "chaosong_Department_id";
    public static final String CC_USER_ID = "chaosong_User_id";
    public static final String CC_DEPARTMENT_NAME = "chaosong_Department_name";
    public static final String CC_USER_NAME = "chaosong_User_name";
    private ViewGroup img_title_left;
    private Button btn_title_right;
    private DepartmentFragment departmentFragment; //部门fragment
    private UserFragment userFragment;             //员工fragment
    public int show_type;   //页面类型（0=部门+人员,1=仅人员）
    public int select_type; //选择类型 (0=多选，1=单选)
    public String superDeptId;
    public static final int TYPE_SELECT_SINGLE = 1;
    public static final int TYPE_SELECT_MULTUI = 0;
    public static final int TYPE_SHOW_USER = 1;
    public static final int TYPE_SHOW_DEPT_USER = 0;

    private PagerSlidingTabStrip tabs;
    private ViewPager pager;
    private MyPagerAdapter adapter;
    private MultiUserReceiver receiver = new MultiUserReceiver();
    private HashMap<String, String> ccUserMap = new HashMap<>();
    private HashMap<String, String> ccDeptMap = new HashMap<>();

    MainApp app = MainApp.getMainApp();

    @Override
    protected void onSaveInstanceState(final Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("token", MainApp.getToken());
        outState.putSerializable("user", MainApp.user);
    }

    @Override
    protected void onRestoreInstanceState(final Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        if (StringUtil.isEmpty(MainApp.getToken())) {
            MainApp.setToken(savedInstanceState.getString("token"));
        }

        if (MainApp.user == null && savedInstanceState.containsKey("user")) {
            MainApp.user = (User) savedInstanceState.getSerializable("user");
        }
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_department_user);
        if (getIntent() != null) {
            Bundle bundle = getIntent().getExtras();
            if (bundle != null) {
                show_type = bundle.getInt(STR_SHOW_TYPE, 0);
                select_type = bundle.getInt(STR_SELECT_TYPE, 0);
                superDeptId = bundle.getString(STR_SUPER_ID, "");
            }
        }

        departmentFragment = new DepartmentFragment(superDeptId, select_type, show_type);
        userFragment = new UserFragment(superDeptId);

        LocalBroadcastManager.getInstance(this).
                registerReceiver(receiver, new IntentFilter(MSG_TYPE_MULTI));

        initUI();
    }

    void initUI() {
        ((TextView) findViewById(R.id.tv_title_1)).setText("选择");

        ViewUtil.OnTouchListener_view_transparency touch = ViewUtil.OnTouchListener_view_transparency.Instance();

        img_title_left = (ViewGroup) findViewById(R.id.img_title_left);
        img_title_left.setOnClickListener(this);
        img_title_left.setOnTouchListener(touch);

        btn_title_right = (Button) findViewById(R.id.btn_title_right);
        btn_title_right.setOnClickListener(this);

        //选单一人员时显示搜索框,奇葩需求
        ViewGroup vgSearch = (ViewGroup) findViewById(R.id.img_title_search_right);

        if (select_type == TYPE_SELECT_SINGLE) {
            //单一人员选择时，不显示确定按钮
            findViewById(R.id.img_title_right).setVisibility(View.GONE);

            vgSearch.setOnClickListener(this);
            vgSearch.setOnTouchListener(touch);
        } else {
            vgSearch.setVisibility(View.GONE);
        }

        tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        tabs.setTextSize(app.spTopx(18));

        pager = (ViewPager) findViewById(R.id.pager);
        adapter = new MyPagerAdapter(getSupportFragmentManager());
        pager.setAdapter(adapter);

        int pageMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, getResources().getDisplayMetrics());
        pager.setPageMargin(pageMargin);

        tabs.setViewPager(pager);
    }

    @Override
    public void onClick(final View v) {
        switch (v.getId()) {
            case R.id.img_title_left:
                app.finishActivity(this, MainApp.ENTER_TYPE_LEFT, RESULT_CANCELED, null);
                break;
            case R.id.btn_title_right:
                setResultOK();
                break;
            case R.id.img_title_search_right:
                app.startActivityForResult(this, DepartmentUserSearchActivity.class, MainApp.ENTER_TYPE_RIGHT,
                        DepartmentActivity.RESULT_ON_ACTIVITY_RETURN, null);
                break;

            default:

                break;
        }
    }

    void setResultOK() {
        Intent intent = getMultiUser();
        app.finishActivity(this, MainApp.ENTER_TYPE_LEFT, RESULT_OK, intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
        SelectNumbers = 0;
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK
                && DepartmentActivity.RESULT_ON_ACTIVITY_RETURN == requestCode) {

            if (select_type == TYPE_SELECT_SINGLE) {
                app.finishActivity(this, MainApp.ENTER_TYPE_LEFT, RESULT_OK, data);
            } else if (select_type == TYPE_SELECT_MULTUI) {
                setResultOK();
            }
        }
    }

    Intent getMultiUser() {
        Intent intent = new Intent();
        StringBuffer cc_department_id = null;
        StringBuffer cc_department_name = null;
        StringBuffer cc_user_id = null;
        StringBuffer cc_user_name = null;

        Iterator iterDept = ccDeptMap.entrySet().iterator();
        while (iterDept.hasNext()) {
            Map.Entry entry = (Map.Entry) iterDept.next();

            if (cc_department_id == null) {
                cc_department_id = new StringBuffer();
                cc_department_name = new StringBuffer();

                cc_department_id.append(entry.getKey());
                cc_department_name.append(entry.getValue());
            } else {
                cc_department_id.append(String.format(",%s", entry.getKey()));
                cc_department_name.append(String.format(",%s", entry.getValue()));
            }
        }

        Iterator iterUser = ccUserMap.entrySet().iterator();
        while (iterUser.hasNext()) {
            Map.Entry entry = (Map.Entry) iterUser.next();

            if (cc_user_id == null) {
                cc_user_id = new StringBuffer();
                cc_user_name = new StringBuffer();

                cc_user_id.append(entry.getKey());
                cc_user_name.append(entry.getValue());
            } else {
                cc_user_id.append(String.format(",%s", entry.getKey()));
                cc_user_name.append(String.format(",%s", entry.getValue()));
            }
        }

        if (cc_department_id != null) {
            intent.putExtra(CC_DEPARTMENT_ID, cc_department_id.toString());
        }
        if (cc_department_name != null) {
            intent.putExtra(CC_DEPARTMENT_NAME, cc_department_name.toString());
        }
        if (cc_user_id != null) {
            intent.putExtra(CC_USER_ID, cc_user_id.toString());
        }
        if (cc_user_name != null) {
            intent.putExtra(CC_USER_NAME, cc_user_name.toString());
        }

        return intent;
    }

    public class MyPagerAdapter extends FragmentPagerAdapter {

        private final String[] TITLES = {"部门", "员工"};

        public MyPagerAdapter(final FragmentManager fm) {
            super(fm);
        }

        @Override
        public CharSequence getPageTitle(final int position) {
            return TITLES[position];
        }

        @Override
        public int getCount() {
            return TITLES.length;
        }

        @Override
        public Fragment getItem(final int position) {
            return (position == 0) ? departmentFragment : userFragment;
        }

    }

    class MultiUserReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(final Context context, final Intent intent) {

            boolean clean = intent.getBooleanExtra("clean", false);

            if (!clean) {
                //删除标识
                boolean type = intent.getBooleanExtra("type", true);

                String ccUserIds = intent.getStringExtra(DepartmentUserActivity.CC_USER_ID);
                String ccUserName = intent.getStringExtra(DepartmentUserActivity.CC_USER_NAME);
                String ccDeptIds = intent.getStringExtra(DepartmentUserActivity.CC_DEPARTMENT_ID);
                String ccDeptName = intent.getStringExtra(DepartmentUserActivity.CC_DEPARTMENT_NAME);

                if (null != ccUserIds) {
                    if (type && !ccUserMap.containsKey(ccUserIds)) {
                        ccUserMap.put(ccUserIds, ccUserName);
                        SelectNumbers++;
                    } else if (ccUserMap.containsKey(ccUserIds)) {
                        ccUserMap.remove(ccUserIds);
                        SelectNumbers--;
                    }
                }

                if (null != ccDeptIds) {
                    ArrayList<User> temuser = Common.getDepartment(ccDeptIds).getUsers();
                    if (type && !ccDeptMap.containsKey(ccDeptIds)) {
                        ccDeptMap.put(ccDeptIds, ccDeptName);
                        SelectNumbers += Common.getDepartmentUsersCount(ccDeptIds);
                    } else if (ccDeptMap.containsKey(ccDeptIds)) {
                        ccDeptMap.remove(ccDeptIds);
                        SelectNumbers -= Common.getDepartmentUsersCount(ccDeptIds);
                    }
                    if (temuser != null && !temuser.isEmpty()) {
                        for (int i = 0; i < temuser.size(); i++) {
                            String id = String.valueOf(temuser.get(i).id);
                            if (ccUserMap.containsKey(id)) {
                                ccUserMap.remove(id);
                            }
                        }
                    }
                }

                btn_title_right.setText(String.format("确定(%d)", SelectNumbers));
            } else {
                ccUserMap.clear();
                ccDeptMap.clear();
                btn_title_right.setText("确定");
            }
        }
    }

    public static int SelectNumbers = 0;

    //新增用户=true,删除用户=false;
    public static void sendMultiSelectUsers(final Context context, final String ccUserId, final String ccUserName, final String ccDeptId, final String ccDeptName, final boolean type) {
//        if (ccUserId > 0) {
//            SelectNumbers += type ? 1 : -1;
//        }
//
//        if (ccDeptId > 0) {
//            SelectNumbers += type ? Common.getDepartmentUsersCount(ccDeptId) : (0 - Common.getDepartmentUsersCount(ccDeptId));
//        }

        Intent intent = new Intent();
        intent.setAction(MSG_TYPE_MULTI);

        if (!TextUtils.isEmpty(ccUserId)) {
            intent.putExtra(DepartmentUserActivity.CC_USER_ID, String.valueOf(ccUserId));
        }

        if (ccUserName != null && !ccUserName.isEmpty()) {
            intent.putExtra(DepartmentUserActivity.CC_USER_NAME, ccUserName);
        }

        if (!TextUtils.isEmpty(ccDeptId)) {
            intent.putExtra(DepartmentUserActivity.CC_DEPARTMENT_ID, String.valueOf(ccDeptId));
        }

        if (ccDeptName != null && !ccDeptName.isEmpty()) {
            intent.putExtra(DepartmentUserActivity.CC_DEPARTMENT_NAME, ccDeptName);
        }

        intent.putExtra("type", type);
        LocalBroadcastManager.getInstance(context).sendBroadcastSync(intent);
    }

    public static void sendCleanUsers(final Context context) {
        SelectNumbers = 0;

        Intent intent = new Intent();
        intent.putExtra("clean", true);
        LocalBroadcastManager.getInstance(context).sendBroadcastSync(intent);
    }

}
