package com.loyo.oa.v2.activity.attendance;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.adapter.CommonCategoryAdapter;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.fragment.AttendanceListFragment;
import com.loyo.oa.v2.tool.BaseFragment;
import com.loyo.oa.v2.tool.BaseFragmentActivity;
import com.loyo.oa.v2.tool.Utils;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 描述 :考勤打卡主界面
 * com.loyo.oa.v2.activity
 * 作者 : ykb
 * 时间 : 15/9/14.
 */
@EActivity(R.layout.activity_attendance)
public class AttendanceActivity extends BaseFragmentActivity {
    private String[] ATTENDANCE_FILTER_STRS = new String[]{"我的考勤", "团队考勤"};
    @ViewById ViewGroup img_title_left;

    @ViewById TextView tv_title_1;

    @ViewById ViewGroup layout_title_action;
    @ViewById ViewGroup layout_category;

    @ViewById(R.id.img_title_arrow) ImageView imageArrow;

    @ViewById(R.id.lv_attendance_category) ListView categoryListView;

    private Animation rotateAnimation;
    private CommonCategoryAdapter categoryAdapter;
    private float mRotation = 0;
    private FragmentManager fragmentManager = getSupportFragmentManager();
    private List<BaseFragment> fragments = new ArrayList<>();
    private int mIndex = -1,Identity;


    @AfterViews
    void initViews() {
        setTouchView(-1);
        tv_title_1.setText("我的考勤");
        imageArrow.setOnTouchListener(Global.GetTouch());
        img_title_left.setOnTouchListener(Global.GetTouch());
        findViewById(R.id.img_title_search_right).setVisibility(View.INVISIBLE);
        findViewById(R.id.img_title_right).setVisibility(View.INVISIBLE);

        imageArrow.setVisibility(View.VISIBLE);
        rotateAnimation = initAnimation();

        if (!Utils.hasRights()) {
            ATTENDANCE_FILTER_STRS = new String[]{"我的考勤"};
        }
        initCategoryUI();
        initChildren();

        //获得权限
        Identity = MainApp.user.role.getDataRange();
        if(Identity==3){
            imageArrow.setVisibility(View.GONE);
            layout_title_action.setEnabled(false);
        }
    }

    @Click({R.id.img_title_left, R.id.layout_title_action})
    void onClick(View v) {
        switch (v.getId()) {
            case R.id.layout_title_action:
                    changeCategoryView();
                break;
            case R.id.img_title_left:
                onBackPressed();
                break;
        }
    }

    /**
     * 初始化子片段
     */
    private void initChildren() {
        for (int i = 0; i < ATTENDANCE_FILTER_STRS.length; i++) {
            //            BaseFragment fragment = null;
            //            if (i == 0) {
            //                fragment = (BaseFragment) Fragment.instantiate(this, AttendanceFragment.class.getName());
            //            }  else {
            //                Bundle b = new Bundle();
            //                b.putInt("type", i);
            //                fragment = (BaseFragment) Fragment.instantiate(this, AttendanceListFragment.class.getName(), b);
            //            }
            //初始化 fragment 列表
            Bundle b = new Bundle();
            b.putInt("type", i + 1);
            BaseFragment fragment = (BaseFragment) Fragment.instantiate(this, AttendanceListFragment.class.getName(), b);

            fragments.add(fragment);
        }
        changeChild(0);
    }

    /**
     * 改变子片段
     *
     * @param index
     */
    private void changeChild(int index) {
        if (index != mIndex) {
            mIndex = index;
            fragmentManager.beginTransaction().replace(R.id.layout_attendance_container, fragments.get(index)).commit();
        }
    }

    /**
     * 初始一个旋转动画
     *
     * @return 旋转动画
     */
    private Animation initAnimation() {
        RotateAnimation rotateAnimation = new RotateAnimation(0f, 180f, Animation.RELATIVE_TO_SELF, 0.5f,// X轴
                Animation.RELATIVE_TO_SELF, 0.5f);// y轴

        rotateAnimation.setDuration(200);
        rotateAnimation.setFillAfter(true);             //保留在终止位置
        rotateAnimation.setFillEnabled(true);
        rotateAnimation.setInterpolator(new AccelerateDecelerateInterpolator());

        return rotateAnimation;
    }

    /**
     * 控制审批类别筛选视图的显示
     */
    private void changeCategoryView() {
        imageArrow.setRotation(mRotation);
        imageArrow.startAnimation(rotateAnimation);
        mRotation = (mRotation == 0f ? 180f : 0f);
        layout_category.setVisibility(layout_category.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
    }

    /**
     * 初始化审批类别筛选视图
     */
    private void initCategoryUI() {
        categoryAdapter = new CommonCategoryAdapter(this, Arrays.asList(ATTENDANCE_FILTER_STRS));
        categoryListView.setAdapter(categoryAdapter);
        categoryListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    changeCategoryView();
                    String content = ATTENDANCE_FILTER_STRS[position];
                    tv_title_1.setText(content);
                    changeChild(position);
            }
        });
    }
}
