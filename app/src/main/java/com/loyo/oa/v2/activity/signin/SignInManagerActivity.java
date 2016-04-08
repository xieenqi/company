package com.loyo.oa.v2.activity.signin;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
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
import android.widget.Toast;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.adapter.CommonCategoryAdapter;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.beans.Permission;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.fragment.SignInOfTeamFragment;
import com.loyo.oa.v2.fragment.SignInOfUserFragment;
import com.loyo.oa.v2.tool.BaseFragment;
import com.loyo.oa.v2.tool.Utils;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 【客户拜访】界面  activity xnq
 */
@EActivity(R.layout.activity_sign_list_my)
public class SignInManagerActivity extends FragmentActivity {

    private String[] LEGWORK_FILTER_STRS = new String[]{"我的拜访", "团队拜访"};
    @ViewById
    ViewGroup img_title_left;
    @ViewById
    TextView tv_title_1;
    @ViewById
    ViewGroup layout_title_action;
    @ViewById
    ViewGroup layout_category;
    @ViewById(R.id.img_title_arrow)
    ImageView imageArrow;
    @ViewById(R.id.lv_signin_category)
    ListView categoryListView;

    private Permission permission;
    private Animation rotateAnimation;
    private CommonCategoryAdapter categoryAdapter;
    private float mRotation = 0;
    private FragmentManager fragmentManager = getSupportFragmentManager();
    private List<BaseFragment> fragments = new ArrayList<>();
    private int mIndex = -1;

    @AfterViews
    void initViews() {
        tv_title_1.setText("我的拜访");
        layout_title_action.setOnTouchListener(Global.GetTouch());
        img_title_left.setOnTouchListener(Global.GetTouch());
        findViewById(R.id.img_title_search_right).setVisibility(View.INVISIBLE);
        findViewById(R.id.img_title_right).setVisibility(View.INVISIBLE);

        imageArrow.setVisibility(View.VISIBLE);
        rotateAnimation = initAnimation();

        if(!MainApp.user.isSuperUser()){
            try{
                permission = (Permission) MainApp.rootMap.get("0310");
                if(!permission.isEnable()){
                    LEGWORK_FILTER_STRS = new String[]{"我的拜访"};
                }
            }catch (NullPointerException e){
                e.printStackTrace();
                Toast.makeText(this,"团队拜访权限，code错误",Toast.LENGTH_SHORT).show();
            }
        }

        initCategoryUI();
        initChildren();
    }

    @Click({R.id.img_title_left, R.id.layout_title_action})
    void onClick(final View v) {
        switch (v.getId()) {
            case R.id.layout_title_action:
                changeCategoryView();
                break;
            case R.id.img_title_left:
                onBackPressed();
                break;
            default:

                break;
        }
    }

    /**
     * 初始化子片段
     */
    private void initChildren() {
        for (int i = 0; i < LEGWORK_FILTER_STRS.length; i++) {
            BaseFragment fragment = null;
            if (i == 0) {
                Bundle b = new Bundle();
                b.putSerializable("user", MainApp.user);
                fragment = (BaseFragment) Fragment.instantiate(this, SignInOfUserFragment.class.getName(), b);
            } else {
                Bundle b = new Bundle();
                b.putInt("type", i);
                fragment = (BaseFragment) Fragment.instantiate(this, SignInOfTeamFragment.class.getName(), b);
            }
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
            fragmentManager.beginTransaction().replace(R.id.layout_list_container, fragments.get(index)).commit();
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
        categoryAdapter = new CommonCategoryAdapter(this, Arrays.asList(LEGWORK_FILTER_STRS));
        categoryListView.setAdapter(categoryAdapter);
        categoryListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(final AdapterView<?> parent, final View view, final int position, final long id) {
                changeCategoryView();
                String content = LEGWORK_FILTER_STRS[position];
                tv_title_1.setText(content);
                changeChild(position);
            }
        });
    }
}
