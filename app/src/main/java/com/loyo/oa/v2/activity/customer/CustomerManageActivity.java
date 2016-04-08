package com.loyo.oa.v2.activity.customer;

import android.content.Intent;
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

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.adapter.CommonCategoryAdapter;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.beans.Customer;
import com.loyo.oa.v2.beans.Permission;
import com.loyo.oa.v2.common.ExtraAndResult;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.fragment.CustomerCommonFragment;
import com.loyo.oa.v2.tool.BaseActivity;
import com.loyo.oa.v2.tool.BaseFragment;
import com.loyo.oa.v2.tool.BaseFragmentActivity;
import com.loyo.oa.v2.tool.Utils;
import com.loyo.oa.v2.tool.ViewUtil;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 客户管理列表
 */

@EActivity(R.layout.activity_customer_manage)
public class CustomerManageActivity extends BaseFragmentActivity {

    @Click(R.id.img_title_left)
    void back() {
        onBackPressed();
    }
    @ViewById
    ViewGroup img_title_left;
    @ViewById
    ViewGroup img_title_search_right;
    @ViewById
    ViewGroup layout_title_action;
    @ViewById
    ViewGroup layout_category;
    @ViewById
    ListView lv_customer_category;
    @ViewById(R.id.img_title_arrow)
    ImageView imageArrow;

    public Permission perTeam;
    public Permission perOcean;
    public CommonCategoryAdapter mCategoryAdapter;
    public Animation rotateAnimation;
    public FragmentManager fragmentManager = getSupportFragmentManager();
    public List<BaseFragment> fragments = new ArrayList<>();//fragment 界面列表列表
    public String[] CUSTOMER_FILTER_STRS = new String[]{"我的客户", "团队客户", "公海客户"};
    public float mRotation = 0;
    public int mIndex = -1;

    @AfterViews
    void initUI() {

        setTitle("我的客户");
        setTouchView(-1);
        getWindow().getDecorView().setOnTouchListener(new ViewUtil.OnTouchListener_softInput_hide());

        //超级管理员权限判断
        if(!MainApp.user.isSuperUser()){
            try{
                perTeam = (Permission) MainApp.rootMap.get("0308"); //团队客户
                perOcean = (Permission) MainApp.rootMap.get("0309"); //公海客户
                if(!perTeam.isEnable() && !perOcean.isEnable()){
                    CUSTOMER_FILTER_STRS = new String[]{"我的客户"};
                }else if(perTeam.isEnable() && !perOcean.isEnable()){
                    CUSTOMER_FILTER_STRS = new String[]{"我的客户", "团队客户"};
                }else if(!perTeam.isEnable() && perOcean.isEnable()){
                    CUSTOMER_FILTER_STRS = new String[]{"我的客户", "公海客户"};
                }
            }catch(NullPointerException e){
                e.printStackTrace();
                Toast("团队/公海客户,code错误");
            }
        }

        img_title_left.setOnTouchListener(Global.GetTouch());
        img_title_search_right.setOnTouchListener(Global.GetTouch());
        initCategoryUI();
        rotateAnimation = getArrowAnimation();
        imageArrow.setVisibility(View.VISIBLE);
        initFragments();
        layout_category.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {

            }
        });
    }

    void initFragments() {

        int len = CUSTOMER_FILTER_STRS.length;
        int type = -1;
        for (int i = 0; i < len; i++) {
            if (len == 2) {
                if (i == 0) {
                    type = Customer.CUSTOMER_TYPE_MINE;
                } else {
                    type = Customer.CUSTOMER_TYPE_PUBLIC;
                }
            } else if (len == 3) {
                if (i == 0) {
                    type = Customer.CUSTOMER_TYPE_MINE;
                } else if (i == 1) {
                    type = Customer.CUSTOMER_TYPE_TEAM;
                } else {
                    type = Customer.CUSTOMER_TYPE_PUBLIC;
                }
            }
            Bundle bundle = new Bundle();
            bundle.putInt("type", type);
            CustomerCommonFragment fragment = (CustomerCommonFragment) Fragment.instantiate(this, CustomerCommonFragment.class.getName(), bundle);
            fragments.add(fragment);//添加fragment 界面
        }
        changeChild(0);

    }

    void initCategoryUI() {
        mCategoryAdapter = new CommonCategoryAdapter(this, Arrays.asList(CUSTOMER_FILTER_STRS));
        lv_customer_category.setAdapter(mCategoryAdapter);
        lv_customer_category.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(final AdapterView<?> parent, final View view, final int position, final long id) {
                changeCategoryView();
                String content = CUSTOMER_FILTER_STRS[position];
                setTitle(content);
                changeChild(position);
            }
        });
    }

    Animation getArrowAnimation() {
        RotateAnimation rotateAnimation = new RotateAnimation(0f, 180f, Animation.RELATIVE_TO_SELF, 0.5f,// X轴
                Animation.RELATIVE_TO_SELF, 0.5f);// y轴

        rotateAnimation.setDuration(200);
        rotateAnimation.setFillAfter(true);       //保留在终止位置
        rotateAnimation.setFillEnabled(true);
        rotateAnimation.setInterpolator(new AccelerateDecelerateInterpolator());

        return rotateAnimation;
    }

    @Click(R.id.layout_title_action)
    void onClickChangeCategory() {
        changeCategoryView();
    }

    void changeChild(final int index) {
        if (index != mIndex) {
            mIndex = index;
            fragmentManager.beginTransaction().replace(R.id.layout_customer_container, fragments.get(index)).commit();
        }
    }

    void changeCategoryView() {

        imageArrow.setRotation(mRotation);
        imageArrow.startAnimation(rotateAnimation);
        mRotation = (mRotation == 0f ? 180f : 0f);
        layout_category.setVisibility(layout_category.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
    }

    /**
     * 进入搜索
     */
    @Click(R.id.img_title_search_right)
    void jumpToSearch() {
        int type;
        if (Utils.hasRights()) {
            type = mIndex + 1;
        } else {
            if (mIndex == 0) {
                type = 1;
            } else {
                type = 3;
            }
        }
        Bundle b = new Bundle();
        b.putInt(ExtraAndResult.EXTRA_TYPE,type);
        b.putInt("from", BaseActivity.CUSTOMER_MANAGE);
        app.startActivity(this, CustomerSearchActivity.class, MainApp.ENTER_TYPE_RIGHT, false, b);
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        fragments.get(0).onActivityResult(requestCode, resultCode, data);
    }
}
