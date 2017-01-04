package com.loyo.oa.v2.activityui.contact;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.contact.fragment.ContactsDepartmentFragment;
import com.loyo.oa.v2.activityui.contact.fragment.ContactsInMyDeptFragment;
import com.loyo.oa.v2.activityui.other.DepartmentUserSearchActivity;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.customview.PagerSlidingTabStrip;
import com.loyo.oa.v2.db.OrganizationManager;
import com.loyo.oa.v2.db.bean.DBDepartment;
import com.loyo.oa.v2.service.OrganizationService;
import com.loyo.oa.v2.tool.BaseFragmentActivity;
import com.loyo.oa.v2.tool.ViewUtil;

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
    private ViewGroup loading_view;// dialog_view
    private ImageView loading_indicator;
    private TextView loading_tip;
    private ContactsDepartmentFragment departmentFragment; //公司全部 部门frag
    private ContactsInMyDeptFragment userFragment;         //本部门  人员frag
    private PagerSlidingTabStrip tabs;
    private ViewPager pager;
    private MyPagerAdapter adapter;
    private MainApp app = MainApp.getMainApp();

    private String departmentsSize;
    private int myDepartmentContactsSize;

    private String myDeptId;

    /* Broadcasr */
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(final Context context, final Intent intent) {
            //Bundle b = intent.getExtras();
            if ( "com.loyo.oa.v2.ORGANIZATION_UPDATED".equals( intent.getAction() )){
                getUserAndDepartmentSize();
                adapter = new MyPagerAdapter(getSupportFragmentManager());
                adapter.setTitles(new String[]{"本部门(" + myDepartmentContactsSize + ")", "全公司(" + departmentsSize + ")"});

                pager.setAdapter(adapter);
                int pageMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, getResources().getDisplayMetrics());
                pager.setPageMargin(pageMargin);
                tabs.setViewPager(pager);
                tabs.setVisibility(View.VISIBLE);
                loading_view.setVisibility(View.GONE);
                loading_indicator.clearAnimation();
            }
        }
    };

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        registerBroadcastReceiver();
        setContentView(R.layout.activity_department_contacts);

        departmentFragment = new ContactsDepartmentFragment();
        userFragment = new ContactsInMyDeptFragment();
        initUI();
    }

    void initUI() {
        if(null == MainApp.user){
            Toast("正在拉去数据,请稍后..");
            finish();
            return;
        }
        if (MainApp.user.depts.size() > 0) {
            myDeptId = MainApp.user.depts.get(0).getShortDept().getId();
        } else {
            myDeptId = MainApp.user.role.id;
        }

        setTouchView(-1);
        //getUserAndDepartmentSize();

        ((TextView) findViewById(R.id.tv_title_1)).setText("通讯录");
        ViewUtil.OnTouchListener_view_transparency touch = ViewUtil.OnTouchListener_view_transparency.Instance();

        img_title_left = (ViewGroup) findViewById(R.id.img_title_left);
        img_title_left.setOnClickListener(this);
        img_title_left.setOnTouchListener(touch);

        img_title_right = (ViewGroup) findViewById(R.id.img_title_right);
        img_title_right.setOnClickListener(this);
        img_title_right.setOnTouchListener(touch);

        loading_view = (ViewGroup) findViewById(R.id.dialog_view);

        loading_indicator = (ImageView) loading_view.findViewById(R.id.img);
        loading_tip = (TextView) loading_view.findViewById(R.id.tipTextView);// 提示文字


        tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        tabs.setTextSize(app.spTopx(14));

        pager = (ViewPager) findViewById(R.id.pager);

        if (OrganizationManager.isOrganizationCached() == false
                && OrganizationService.isFetchingOrganziationData()) {
            // DialogHelp.showLoading(this, "加载通讯录中...", true);
            // start animation
            tabs.setVisibility(View.GONE);
            // 加载动画
            Animation hyperspaceJumpAnimation = AnimationUtils.loadAnimation(
                    this, R.anim.load_animayion);
            // 使用ImageView显示动画
            loading_indicator.startAnimation(hyperspaceJumpAnimation);
            loading_tip.setText("获取通讯录中...");

        }
        else {

            loading_view.setVisibility(View.GONE);

            getUserAndDepartmentSize();
            adapter = new MyPagerAdapter(getSupportFragmentManager());
            adapter.setTitles(new String[]{"本部门(" + myDepartmentContactsSize + ")", "全公司(" + departmentsSize + ")"});

            pager.setAdapter(adapter);
            int pageMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, getResources().getDisplayMetrics());
            pager.setPageMargin(pageMargin);
            tabs.setViewPager(pager);
        }
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        unregisterBroadcastReceiver();
    }

    /**
     * 获取全公司人数、本部门人数
     */
    void getUserAndDepartmentSize() {
        try{
            // myDepartmentContactsSize = OrganizationManager.shareManager().getCurrentUserSameDeptsUsers().size();
            myDepartmentContactsSize = OrganizationManager.shareManager().getCurrentUserSameDeptsUsers2().size();
            DBDepartment company = OrganizationManager.shareManager().getsComany();
            departmentsSize          = String.valueOf((company!= null?company.allUsers().size() : 0));
        }catch (Exception e){
            Toast("数据拉取中，请等待");
            finish();
            e.printStackTrace();
        }
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

    public void registerBroadcastReceiver(){
        IntentFilter filter = new IntentFilter("com.loyo.oa.v2.USER_EDITED");
        filter.addAction("com.loyo.oa.v2.ORGANIZATION_UPDATED");
        registerReceiver(mReceiver, filter);
    }

    public void unregisterBroadcastReceiver() {
        unregisterReceiver(mReceiver);
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
