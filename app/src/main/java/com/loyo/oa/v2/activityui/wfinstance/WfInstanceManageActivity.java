package com.loyo.oa.v2.activityui.wfinstance;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.wfinstance.adapter.WinstanceCategoryAdapter;
import com.loyo.oa.v2.activityui.wfinstance.common.ApprovalAddBuilder;
import com.loyo.oa.v2.activityui.wfinstance.fragment.WfinstanceMyApproveFragment;
import com.loyo.oa.v2.activityui.wfinstance.fragment.WfinstanceMySubmitFragment;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.common.ExtraAndResult;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.tool.BaseActivity;
import com.loyo.oa.v2.tool.BaseFragment;
import com.loyo.oa.v2.tool.BaseFragmentActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 【审批流程】 页面 xnq
 */
public class WfInstanceManageActivity extends BaseFragmentActivity implements View.OnClickListener {
    private LinearLayout img_title_left, ll_category;
    private ImageView imageArrow;
    private ListView lv_sale;
    private TextView tv_title_1;
    private RelativeLayout layout_title_action, img_title_search_right;
    private String[] SaleItemStatus = new String[]{"我提交的", "我审批的"};
    private Animation rotateAnimation;//标题动画
    private float mRotation = 0;
    private int mIndex = -1;
    private FragmentManager fragmentManager = getSupportFragmentManager();
    private List<BaseFragment> fragments = new ArrayList<>();
    private int fragmentStatus = -1;
    /**
     * 流程类型回调
     */
    public final static int WFIN_FINISH_RUSH = 1;

//    public WfInstanceManageFragment fragment = new WfInstanceManageFragment();

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wfinstance_manager);
        getIntentData();
        initView();
//        initUI();
    }

    private void getIntentData() {
        fragmentStatus = getIntent().getIntExtra(ExtraAndResult.EXTRA_OBJ, -1);
    }

    private void initView() {
        setTitle(fragmentStatus == 0 ? "我提交的" : "我审批的");
        img_title_left = (LinearLayout) findViewById(R.id.img_title_left);
        img_title_left.setOnTouchListener(Global.GetTouch());
        img_title_left.setOnClickListener(this);
        imageArrow = (ImageView) findViewById(R.id.img_title_arrow);
        imageArrow.setVisibility(View.VISIBLE);
        lv_sale = (ListView) findViewById(R.id.lv_sale);
        ll_category = (LinearLayout) findViewById(R.id.ll_category);
        ll_category.setOnClickListener(this);
        tv_title_1 = (TextView) findViewById(R.id.tv_title_1);
        layout_title_action = (RelativeLayout) findViewById(R.id.layout_title_action);
        layout_title_action.setOnClickListener(this);
        img_title_search_right = (RelativeLayout) findViewById(R.id.img_title_search_right);
        img_title_search_right.setOnClickListener(this);
        img_title_search_right.setOnTouchListener(Global.GetTouch());
        rotateAnimation = initArrowAnimation();
        initTitleItem();
        initChildren();
    }

    void initUI() {
//        MainApp.permissionPage = 4;
//        getWindow().getDecorView().setOnTouchListener(new ViewUtil.OnTouchListener_softInput_hide());
//        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
//        transaction.add(R.id.main_content, fragment);
//        transaction.commit();
    }

    void initTitleItem() {
        WinstanceCategoryAdapter TitleItemAdapter = new WinstanceCategoryAdapter(this, Arrays.asList(SaleItemStatus));
        lv_sale.setAdapter(TitleItemAdapter);
        lv_sale.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                changeTitleImg();
                tv_title_1.setText(SaleItemStatus[position]);
                changeChild(position);
            }
        });

    }

    /**
     * 初始化子片段
     */
    private void initChildren() {
        for (int i = 0; i < SaleItemStatus.length; i++) {
            BaseFragment fragment = null;
            if ("我提交的".equals(SaleItemStatus[i])) {
                Bundle b = new Bundle();
                fragment = (BaseFragment) Fragment.instantiate(this, WfinstanceMySubmitFragment.class.getName(), b);
            } else if ("我审批的".equals(SaleItemStatus[i])) {
                Bundle b = new Bundle();
                fragment = (BaseFragment) Fragment.instantiate(this, WfinstanceMyApproveFragment.class.getName(), b);
            }
            fragments.add(fragment);
        }
        changeChild(fragmentStatus == 0 ? 0 : 1);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_title_left:
                onBackPressed();
                break;
            case R.id.ll_category:
                break;
            case R.id.layout_title_action:
//                if (SaleItemStatus.length != 1) {
//                }没有权限限制
                changeTitleImg();
                break;
            case R.id.img_title_search_right:
                openSearch();
                break;
        }
    }

    /**
     * 到审批流程  的搜索页面
     */
    public void openSearch() {
        Bundle mBundle = new Bundle();
        mBundle.putInt("from", BaseActivity.WFIN_MANAGE);
        app.startActivity(this, WfinstanceSearchActivity.class, MainApp.ENTER_TYPE_RIGHT, false, mBundle);
    }

    /**
     * title 状态动画
     */
    void changeTitleImg() {
        imageArrow.setRotation(mRotation);
        imageArrow.startAnimation(rotateAnimation);
        mRotation = (mRotation == 0f ? 180f : 0f);
        ll_category.setVisibility(ll_category.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
    }

    /**
     * 改变子片段动画
     *
     * @param index
     */
    private void changeChild(int index) {
        if (index != mIndex) {
            mIndex = index;
            try {
                fragmentManager.beginTransaction().replace(R.id.fl_wflnstance, fragments.get(index)).commit();
            } catch (IllegalStateException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * title动画
     *
     * @return
     */
    Animation initArrowAnimation() {
        RotateAnimation rotateAnimation = new RotateAnimation(0f, 180f, Animation.RELATIVE_TO_SELF, 0.5f,// X轴
                Animation.RELATIVE_TO_SELF, 0.5f);
        rotateAnimation.setDuration(200);
        rotateAnimation.setFillAfter(true);
        rotateAnimation.setFillEnabled(true);
        rotateAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
        return rotateAnimation;
    }

    /**
     * 到 审批流程 add 添加页面
     */
    public void addNewItem() {
        ApprovalAddBuilder.startCreate();
        Intent intent = new Intent();
        intent.setClass(this, WfInTypeSelectActivity.class);
        startActivityForResult(intent, ExtraAndResult.REQUEST_CODE);
        overridePendingTransition(R.anim.enter_righttoleft, R.anim.exit_righttoleft);
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}
