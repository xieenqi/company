package com.loyo.oa.v2.activityui.worksheet;

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
import com.loyo.oa.v2.activityui.worksheet.adapter.WorksheetCategoryAdapter;
import com.loyo.oa.v2.activityui.worksheet.common.WorksheetConfig;
import com.loyo.oa.v2.activityui.worksheet.common.WorksheetListType;
import com.loyo.oa.v2.activityui.worksheet.fragment.AssignableWorksheetFragment;
import com.loyo.oa.v2.activityui.worksheet.fragment.ResponsableWorksheetFragment;
import com.loyo.oa.v2.activityui.worksheet.fragment.SelfCreatedWorksheetFragment;
import com.loyo.oa.v2.activityui.worksheet.fragment.TeamWorksheetFragment;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.beans.Permission;
import com.loyo.oa.v2.common.ExtraAndResult;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.tool.BaseFragment;
import com.loyo.oa.v2.tool.BaseFragmentActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 【订单管理】 列表 页面
 */
public class WorksheetManageActivity extends BaseFragmentActivity implements View.OnClickListener {

    private ImageView img_title_arrow;
    private LinearLayout img_title_left, ll_category;
    private RelativeLayout layout_title_action, img_title_search_right;
    private TextView tv_title_1;
    private ListView lv_order_title;
    private Animation rotateAnimation;//标题动画
    private FragmentManager fragmentManager = getSupportFragmentManager();
    private Permission permission;

    private int mIndex = -1;
    private float mRotation = 0;
    private WorksheetListType[] SaleItemStatus = new WorksheetListType[]
            {WorksheetListType.RESPONSABLE, WorksheetListType.SELF_CREATED, WorksheetListType.ASSIGNABLE, WorksheetListType.TEAM};
    private List<BaseFragment> fragments = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_management);
        init();
        WorksheetConfig.getWorksheetTypes(true/* 没有数据就从网络获取 */);
    }

    private void init() {
        setTitle("我负责的");
        img_title_arrow = (ImageView) findViewById(R.id.img_title_arrow);
        img_title_left = (LinearLayout) findViewById(R.id.img_title_left);
        img_title_left.setOnTouchListener(Global.GetTouch());
        img_title_left.setOnClickListener(this);
        img_title_arrow = (ImageView) findViewById(R.id.img_title_arrow);
        img_title_arrow.setVisibility(View.VISIBLE);
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

        //超级管理员\权限判断
        if (!MainApp.user.isSuperUser()) {
            try {
                permission = (Permission) MainApp.rootMap.get("0330");
                if (!permission.isEnable()) {
                    SaleItemStatus = new WorksheetListType[]
                            {WorksheetListType.RESPONSABLE, WorksheetListType.SELF_CREATED, WorksheetListType.ASSIGNABLE};
                }
            } catch (NullPointerException e) {
                e.printStackTrace();
                SaleItemStatus = new WorksheetListType[]
                        {WorksheetListType.RESPONSABLE, WorksheetListType.SELF_CREATED, WorksheetListType.ASSIGNABLE};
            }
        }
        initTitleItem();
        initChildren();
        ClueCommon.getSourceData();//缓存线索来源数据
    }

    /**
     * 初始化子片段
     */
    private void initChildren() {

        BaseFragment fragment = null;

        Bundle b = new Bundle();

        fragment = (BaseFragment) Fragment.instantiate(this, ResponsableWorksheetFragment.class.getName(), b);
        fragments.add(fragment);

        b = new Bundle();
        fragment = (BaseFragment) Fragment.instantiate(this, SelfCreatedWorksheetFragment.class.getName(), b);
        fragments.add(fragment);

        b = new Bundle();
        fragment = (BaseFragment) Fragment.instantiate(this, AssignableWorksheetFragment.class.getName(), b);
        fragments.add(fragment);

        b = new Bundle();
        fragment = (BaseFragment) Fragment.instantiate(this, TeamWorksheetFragment.class.getName(), b);
        fragments.add(fragment);

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

                WorksheetListType type = WorksheetListType.SELF_CREATED;
                switch (mIndex){
                    case 0:
                        type = WorksheetListType.RESPONSABLE;
                        break;
                    case 1:
                        type = WorksheetListType.SELF_CREATED;
                        break;
                    case 2:
                        type = WorksheetListType.ASSIGNABLE;
                        break;
                    case 3:
                        type = WorksheetListType.TEAM;
                        break;
                    default:
                            break;
                }

                Bundle b = new Bundle();
                b.putSerializable(ExtraAndResult.EXTRA_TYPE, type);
                app.startActivity(this, WorksheetSearchActivity.class, MainApp.ENTER_TYPE_RIGHT, false, b);
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
        WorksheetCategoryAdapter TitleItemAdapter = new WorksheetCategoryAdapter(this, Arrays.asList(SaleItemStatus));
        lv_order_title.setAdapter(TitleItemAdapter);
        lv_order_title.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                changeTitleImg();
                tv_title_1.setText(SaleItemStatus[position].getTitle());
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
                fragmentManager.beginTransaction().replace(R.id.fl_order_container, fragments.get(index)).commitAllowingStateLoss();
            } catch (IllegalStateException e) {
                e.printStackTrace();
            }
        }
    }
}
