package com.loyo.oa.v2.activity.contact;

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
import com.loyo.oa.v2.activity.DepartmentUserSearchActivity;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.beans.Department;
import com.loyo.oa.v2.common.Common;
import com.loyo.oa.v2.fragment.ContactsDepartmentFragment;
import com.loyo.oa.v2.fragment.ContactsInMyDeptFragment;
import com.loyo.oa.v2.tool.BaseFragmentActivity;
import com.loyo.oa.v2.tool.ViewUtil;
import com.loyo.oa.v2.tool.customview.PagerSlidingTabStrip;

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
    private int departmentsSize;
    private int myDepartmentContactsSize;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_department_contacts);

        departmentFragment = new ContactsDepartmentFragment();
        userFragment = new ContactsInMyDeptFragment();
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
    void getUserAndDepartmentSize() {
        myDepartmentContactsSize = Common.getMyUserDept().size();
        if (MainApp.lstDepartment != null) {//公司所有的人员数量
            for (Department element : MainApp.lstDepartment) {
                if (element.getUsers() != null) {
                    departmentsSize += element.getUsers().size();
                }
            }
        }
    }

    @Override
    public void onClick(final View v) {
        switch (v.getId()) {
            case R.id.img_title_left:
                app.finishActivity(this, MainApp.ENTER_TYPE_LEFT, RESULT_CANCELED, null);
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
