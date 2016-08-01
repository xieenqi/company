package com.loyo.oa.v2.activityui.contact;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.customer.bean.ContactsGroup;
import com.loyo.oa.v2.activityui.other.DepartmentUserSearchActivity;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.activityui.customer.bean.Department;
import com.loyo.oa.v2.common.Common;
import com.loyo.oa.v2.activityui.contact.fragment.ContactsDepartmentFragment;
import com.loyo.oa.v2.activityui.contact.fragment.ContactsInMyDeptFragment;
import com.loyo.oa.v2.tool.BaseFragmentActivity;
import com.loyo.oa.v2.tool.ViewUtil;
import com.loyo.oa.v2.customview.PagerSlidingTabStrip;
import java.util.ArrayList;

/**
 * 通讯录 联系人 页面
 * com.loyo.oa.v2.activity
 * 描述 :
 * 作者 : ykb
 * 时间 : 15/8/24.
 */
public class ContactsActivity extends BaseFragmentActivity implements View.OnClickListener {

    private ViewGroup img_title_left;
    private ViewGroup img_title_right;
    private ContactsDepartmentFragment departmentFragment; //公司全部 部门frag
    private ContactsInMyDeptFragment userFragment;         //本部门  人员frag
    private PagerSlidingTabStrip tabs;
    private ViewPager pager;
    private MyPagerAdapter adapter;
    private MainApp app = MainApp.getMainApp();
    private ArrayList<ContactsGroup> lstUserGroupData;

    private String departmentsSize;
    private int myDepartmentContactsSize;

    private String myDeptId;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_department_contacts);

        departmentFragment = new ContactsDepartmentFragment();
        userFragment = new ContactsInMyDeptFragment();
        lstUserGroupData = Common.getContactsGroups(null);
        initUI();
    }

    void initUI() {
        if (MainApp.user.depts.size() > 0) {
            myDeptId = MainApp.user.depts.get(0).getShortDept().getId();
        } else {
            myDeptId = MainApp.user.role.id;
        }

        setTouchView(-1);
        getUserAndDepartmentSize();

        ((TextView) findViewById(R.id.tv_title_1)).setText("通讯录");
        ViewUtil.OnTouchListener_view_transparency touch = ViewUtil.OnTouchListener_view_transparency.Instance();

        img_title_left = (ViewGroup) findViewById(R.id.img_title_left);
        img_title_left.setOnClickListener(this);
        img_title_left.setOnTouchListener(touch);

        img_title_right = (ViewGroup) findViewById(R.id.img_title_right);
        img_title_right.setOnClickListener(this);
        img_title_right.setOnTouchListener(touch);

        tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        tabs.setTextSize(app.spTopx(14));

        pager = (ViewPager) findViewById(R.id.pager);
        adapter = new MyPagerAdapter(getSupportFragmentManager());
        adapter.setTitles(new String[]{"本部门(" + myDepartmentContactsSize + ")", "全公司(" + departmentsSize + ")"});

        pager.setAdapter(adapter);
        int pageMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, getResources().getDisplayMetrics());
        pager.setPageMargin(pageMargin);
        tabs.setViewPager(pager);
    }

    /**
     * 获取部门数量和本部门人员数量
     */
/*    void getUserAndDepartmentSize() {
        myDepartmentContactsSize = Common.getMyUserDept().size();
        for (ContactsGroup group : lstUserGroupData) {
            if (null == group.getDepartments() && group.getDepartments().isEmpty()) {
                continue;
            }
            for (Department department : group.getDepartments()) {
                if (null != department) {
                    departmentsSize += Integer.parseInt(department.userNum);
                }
            }
        }
        departmentsSize += Common.getListUser(myDeptId).size();
    }*/


    /**
     * 获取全公司人数、本部门人数
     */
    void getUserAndDepartmentSize() {
        myDepartmentContactsSize = Common.getMyUserDept().size();
        departmentsSize          = MainApp.lstDepartment.get(0).userNum;
    }

    @Override
    public void onClick(final View v) {
        switch (v.getId()) {
            case R.id.img_title_left:
                onBackPressed();
                break;
            case R.id.img_title_right:
                Bundle b = new Bundle();
                b.putInt("type", 1);
                app.startActivity(this, DepartmentUserSearchActivity.class, MainApp.ENTER_TYPE_RIGHT, false, b);
                break;
            default:
                break;
        }
    }


    public class MyPagerAdapter extends FragmentPagerAdapter {
        private String[] titles = {"本部门(0)", "全公司(0)"};

        public void setTitles(final String[] titles) {
            this.titles = titles;
        }

        public MyPagerAdapter(final FragmentManager fm) {
            super(fm);
        }

        @Override
        public CharSequence getPageTitle(final int position) {
            return titles[position];
        }

        @Override
        public int getCount() {
            return titles.length;
        }

        @Override
        public Fragment getItem(final int position) {
            return (position == 0) ? userFragment : departmentFragment;
        }
    }
}
