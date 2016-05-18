package com.loyo.oa.v2.activity.sale;

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
import com.loyo.oa.v2.adapter.CommonCategoryAdapter;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.tool.BaseFragment;
import com.loyo.oa.v2.tool.BaseFragmentActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 【销售机会列表页面】
 * Created by xeq on 16/5/17.
 */
public class ActivitySaleOpportunitiesManager extends BaseFragmentActivity implements View.OnClickListener {
    private LinearLayout img_title_left, ll_category;
    private ImageView img_title_arrow;
    private ListView lv_sale;
    private TextView tv_title_1;
    private RelativeLayout layout_title_action, img_title_search_right;

    private Animation rotateAnimation;//标题动画
    private String[] SaleItemStatus = new String[]{"我的机会", "团队机会"};
    private List<BaseFragment> fragments = new ArrayList<>();
    private float mRotation = 0;
    private FragmentManager fragmentManager = getSupportFragmentManager();
    private int mIndex = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sale_opportunities);
        init();
        initTitleItem();
        initChildren();
    }

    private void init() {
        setTitle("我的机会");
        img_title_left = (LinearLayout) findViewById(R.id.img_title_left);
        img_title_left.setOnTouchListener(Global.GetTouch());
        img_title_left.setOnClickListener(this);
        img_title_arrow = (ImageView) findViewById(R.id.img_title_arrow);
        img_title_arrow.setVisibility(View.VISIBLE);
        lv_sale = (ListView) findViewById(R.id.lv_sale);
        ll_category = (LinearLayout) findViewById(R.id.ll_category);
        ll_category.setOnClickListener(this);
        tv_title_1 = (TextView) findViewById(R.id.tv_title_1);
        layout_title_action = (RelativeLayout) findViewById(R.id.layout_title_action);
        layout_title_action.setOnClickListener(this);
        layout_title_action.setOnTouchListener(Global.GetTouch());
        img_title_search_right = (RelativeLayout) findViewById(R.id.img_title_search_right);
        img_title_search_right.setOnClickListener(this);
        img_title_search_right.setOnTouchListener(Global.GetTouch());
    }

    void initTitleItem() {
        CommonCategoryAdapter TitleItemAdapter = new CommonCategoryAdapter(this, Arrays.asList(SaleItemStatus));
        lv_sale.setAdapter(TitleItemAdapter);
        lv_sale.setOnItemClickListener(new AdapterView.OnItemClickListener() {
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
     * 初始化子片段
     */
    private void initChildren() {
        for (int i = 0; i < SaleItemStatus.length; i++) {
            BaseFragment fragment = null;
            if (i == 0) {
                Bundle b = new Bundle();
                b.putSerializable("user", MainApp.user);
                fragment = (BaseFragment) Fragment.instantiate(this, FragmentMySale.class.getName(), b);
            } else {
                Bundle b = new Bundle();
                b.putInt("type", i);
                fragment = (BaseFragment) Fragment.instantiate(this, FragmentTeamSale.class.getName(), b);
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
            fragmentManager.beginTransaction().replace(R.id.layout_customer_container, fragments.get(index)).commit();
        }
    }

    Animation initArrowAnimation() {
        RotateAnimation rotateAnimation = new RotateAnimation(0f, 180f, Animation.RELATIVE_TO_SELF, 0.5f,// X轴
                Animation.RELATIVE_TO_SELF, 0.5f);// y轴
        rotateAnimation.setDuration(200);
        rotateAnimation.setFillAfter(true);       //保留在终止位置
        rotateAnimation.setFillEnabled(true);
        rotateAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
        return rotateAnimation;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_title_left:
                finish();
                break;
            case R.id.ll_category:
                break;
            case R.id.layout_title_action:
                changeTitleImg();
                break;
            case R.id.img_title_search_right:
                Toast("收索机会");
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
}
