package com.loyo.oa.v2.activityui.clue;

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
import com.loyo.oa.v2.activityui.clue.common.ClueCommon;
import com.loyo.oa.v2.activityui.clue.common.ClueType;
import com.loyo.oa.v2.activityui.clue.fragment.MyClueFragment;
import com.loyo.oa.v2.activityui.clue.fragment.TeamClueFragment;
import com.loyo.oa.v2.activityui.other.adapter.CommonCategoryAdapter;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.permission.BusinessOperation;
import com.loyo.oa.v2.permission.PermissionManager;
import com.loyo.oa.v2.tool.BaseFragment;
import com.loyo.oa.v2.tool.BaseFragmentActivity;
import com.loyo.oa.v2.tool.LogUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 【我的线索】 列表 页面
 * Created by yyy on 16/8/19.
 */
public class ClueManagerActivity extends BaseFragmentActivity implements View.OnClickListener {

    private ImageView img_title_arrow;
    private LinearLayout img_title_left, ll_category;
    private RelativeLayout layout_title_action, img_title_search_right;
    private TextView tv_title_1;
    private ListView lv_order_title;
    private Animation rotateAnimation;//标题动画
    private FragmentManager fragmentManager = getSupportFragmentManager();

    private int mIndex = -1;
    private float mRotation = 0;
    private String[] SaleItemStatus = new String[]{"我的线索"};
    private List<BaseFragment> fragments = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_management);
        init();
    }

    private void init() {
        setTitle("我的线索");
        img_title_arrow = (ImageView) findViewById(R.id.img_title_arrow);
        img_title_left = (LinearLayout) findViewById(R.id.img_title_left);
        img_title_left.setOnTouchListener(Global.GetTouch());
        img_title_left.setOnClickListener(this);
        img_title_arrow = (ImageView) findViewById(R.id.img_title_arrow);
        img_title_arrow.setVisibility(View.INVISIBLE);
        lv_order_title = (ListView) findViewById(R.id.lv_order_title);
        ll_category = (LinearLayout) findViewById(R.id.ll_category);
        ll_category.setOnClickListener(this);
        tv_title_1 = (TextView) findViewById(R.id.tv_title_1);
        layout_title_action = (RelativeLayout) findViewById(R.id.layout_title_action);
        layout_title_action.setOnClickListener(this);
        layout_title_action.setOnTouchListener(Global.GetTouch());
        img_title_search_right = (RelativeLayout) findViewById(R.id.img_title_search_right);
        img_title_search_right.setOnClickListener(this);
        img_title_search_right.setOnTouchListener(Global.GetTouch());

        if (PermissionManager.getInstance().teamPermission(BusinessOperation.CLUE_MANAGEMENT)) {
            SaleItemStatus = new String[]{"我的线索", "团队线索"};
            img_title_arrow.setVisibility(View.VISIBLE);
            layout_title_action.setEnabled(true);
        } else {
            img_title_arrow.setVisibility(View.GONE);
            layout_title_action.setEnabled(false);
        }
        initTitleItem();
        initChildren();
        ClueCommon.getSourceData();//缓存线索来源数据
    }

    /**
     * 初始化子片段
     */
    private void initChildren() {
        for (int i = 0; i < SaleItemStatus.length; i++) {
            BaseFragment fragment = null;
            if (i == 0) {
                Bundle b = new Bundle();
                fragment = (BaseFragment) Fragment.instantiate(this, MyClueFragment.class.getName(), b);
            } else {
                Bundle b = new Bundle();
                fragment = (BaseFragment) Fragment.instantiate(this, TeamClueFragment.class.getName(), b);
            }
            fragments.add(fragment);
        }
        changeChild(0);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            /*返回*/
            case R.id.img_title_left:
                onBackPressed();
                break;

            case R.id.ll_category:
                break;

            /*切换按钮*/
            case R.id.layout_title_action:
                changeTitleImg();
                break;

            /*搜索*/
            case R.id.img_title_search_right:
                ClueType type;
                if (mIndex == 0) {
                    type = ClueType.MY_CLUE;
                } else {
                    type = ClueType.TEAM_CLUE;
                }
                LogUtil.dee("type:" + type);
                Bundle b = new Bundle();
                b.putSerializable(ClueSearchActivity.KEY_SEARCH_TYPE, type);
                app.startActivity(this, ClueSearchActivity.class, MainApp.ENTER_TYPE_RIGHT, false, b);
                break;
        }
    }

    /**
     * title 状态动画
     */
    void changeTitleImg() {
        img_title_arrow.setRotation(mRotation);
        img_title_arrow.startAnimation(rotateAnimation);
        mRotation = (mRotation == 0f ? 180f : 0f);
        ll_category.setVisibility(ll_category.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
    }

    Animation initArrowAnimation() {
        RotateAnimation rotateAnimation = new RotateAnimation(0f, 180f, Animation.RELATIVE_TO_SELF, 0.5f,// X轴
                Animation.RELATIVE_TO_SELF, 0.5f);
        rotateAnimation.setDuration(200);
        rotateAnimation.setFillAfter(true);
        rotateAnimation.setFillEnabled(true);
        rotateAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
        return rotateAnimation;
    }

    void initTitleItem() {
        CommonCategoryAdapter TitleItemAdapter = new CommonCategoryAdapter(this, Arrays.asList(SaleItemStatus));
        lv_order_title.setAdapter(TitleItemAdapter);
        lv_order_title.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                changeTitleImg();
                tv_title_1.setText(SaleItemStatus[position]);
                changeChild(position);
            }
        });
        rotateAnimation = initArrowAnimation();
    }

    /**
     * 改变子片段动画
     *
     * @param index
     */
    private void changeChild(int index) {
        if (index != mIndex && fragments.size() > 0) {
            mIndex = index;
            try {
                fragmentManager.beginTransaction().replace(R.id.fl_order_container, fragments.get(index)).commit();
            } catch (IllegalStateException e) {
                e.printStackTrace();
            }
        }
    }
}
