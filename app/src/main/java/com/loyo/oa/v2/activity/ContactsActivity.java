package com.loyo.oa.v2.activity;

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
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.beans.ContactsGroup;
import com.loyo.oa.v2.beans.User;
import com.loyo.oa.v2.common.Common;
import com.loyo.oa.v2.fragment.ContactsDepartmentFragment;
import com.loyo.oa.v2.fragment.ContactsInDepartmentFragment;
import com.loyo.oa.v2.tool.BaseFragmentActivity;
import com.loyo.oa.v2.tool.ViewUtil;
import com.loyo.oa.v2.tool.customview.PagerSlidingTabStrip;

import java.util.ArrayList;

/**通讯录 联系人 页面
 * com.loyo.oa.v2.activity
 * 描述 :
 * 作者 : ykb
 * 时间 : 15/8/24.
 */
public class ContactsActivity extends BaseFragmentActivity implements View.OnClickListener {
    ViewGroup img_title_left;
    ViewGroup img_title_right;

    ContactsDepartmentFragment departmentFragment;
    ContactsInDepartmentFragment userFragment;

    PagerSlidingTabStrip tabs;
    ViewPager pager;
    MyPagerAdapter adapter;

    MainApp app = MainApp.getMainApp();

    private int departmentsSize;
    private int myDepartmentContactsSize;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_department_contacts);

        departmentFragment = new ContactsDepartmentFragment();
        userFragment = new ContactsInDepartmentFragment();

        initUI();
    }

    void initUI() {
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
        tabs.setTextSize(app.spTopx(18));

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
    void getUserAndDepartmentSize() {
        String depId = (null != MainApp.user.getDepts() && MainApp.user.getDepts().size() > 0) ? MainApp.user.getDepts().get(0).getShortDept().getId() : "";

        myDepartmentContactsSize = Common.getUsersByDeptId(depId, new ArrayList<User>()).size();

        ArrayList<ContactsGroup> groups = Common.getContactsGroups(null);

        if (!groups.isEmpty()) {
            for (int i = 0; i < groups.size(); i++) {
                departmentsSize += groups.get(i).getDepartments().size();
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_title_left:
                app.finishActivity(this, MainApp.ENTER_TYPE_LEFT, RESULT_CANCELED, null);
                break;
            case R.id.img_title_right:
                Bundle b = new Bundle();
                b.putInt("type", 1);
                app.startActivity(this, DepartmentUserSearchActivity.class, MainApp.ENTER_TYPE_RIGHT, false, b);
                break;
        }
    }

    public class MyPagerAdapter extends FragmentPagerAdapter {
        private String[] titles = {"本部门(0)", "全公司(0)"};

        public void setTitles(String[] titles) {
            this.titles = titles;
        }

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return titles[position];
        }

        @Override
        public int getCount() {
            return titles.length;
        }

        @Override
        public Fragment getItem(int position) {
            return (position == 0) ? userFragment : departmentFragment;
        }
    }
}