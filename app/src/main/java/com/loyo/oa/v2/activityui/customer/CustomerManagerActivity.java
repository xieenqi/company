package com.loyo.oa.v2.activityui.customer;

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
import com.loyo.oa.v2.activityui.customer.fragment.CommCustomerFragment;
import com.loyo.oa.v2.activityui.customer.fragment.MyCustomerFragment;
import com.loyo.oa.v2.activityui.customer.fragment.TeamCustomerFragment;
import com.loyo.oa.v2.activityui.other.adapter.CommonCategoryAdapter;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.beans.Permission;
import com.loyo.oa.v2.activityui.customer.bean.Tag;
import com.loyo.oa.v2.common.ExtraAndResult;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.common.http.HttpErrorCheck;
import com.loyo.oa.v2.point.ICustomer;
import com.loyo.oa.v2.tool.BaseActivity;
import com.loyo.oa.v2.tool.BaseFragment;
import com.loyo.oa.v2.tool.BaseFragmentActivity;
import com.loyo.oa.v2.tool.Config_project;
import com.loyo.oa.v2.tool.RCallback;
import com.loyo.oa.v2.tool.RestAdapterFactory;
import com.loyo.oa.v2.tool.Utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * 【客户列表】fragment管理类
 * Created by yyy on 16/6/1.
 */
public class CustomerManagerActivity extends BaseFragmentActivity implements View.OnClickListener {

    /**
     * 筛选取消
     */
    public final static int CUSTOMER_CANCEL = 200;

    /**
     * 标签确定
     */
    public final static int CUSTOMER_TAG = 202;

    /**
     * 时间筛选
     */
    public final static int CUSTOMER_TIME = 201;

    /**
     * 部门筛选
     */
    public final static int CUSTOMER_DEPT_CREEN = 0x01;

    /**
     * 公海挑入
     */
    public final static int CUSTOMER_COMM_RUSH = 0x02;

    /**
     * 我的客户
     */
    public final static int CUSTOMER_MY = 1;

    /**
     * 团队客户
     */
    public final static int CUSTOMER_TEAM = 2;

    /**
     * 公海客户
     */
    public final static int CUSTOMER_COMM = 3;

    /**
     * 个人附近客户
     */
    public final static int NEARCUS_SELF = 1;

    /**
     * 团队附近客户
     */
    public final static int NEARCUS_TEAM = 2;


    private LinearLayout img_title_left, ll_category;
    private ImageView imageArrow;
    private ListView lv_sale;
    private TextView tv_title_1;
    private RelativeLayout layout_title_action, img_title_search_right;

    private Animation rotateAnimation;//标题动画
    private FragmentManager fragmentManager = getSupportFragmentManager();
    private int mIndex = -1;
    private float mRotation = 0;

    private Permission permission = (Permission) MainApp.rootMap.get("0404");
    private String[] SaleItemStatus = new String[]{"我的客户", "团队客户", "公海客户"};
    private List<BaseFragment> fragments = new ArrayList<>();
    private ArrayList<Tag> mTags;
    private ArrayList<Tag> mTags1;
    private ArrayList<Tag> mTags2;
    private ArrayList<Tag> mTags3;

    public Permission perTeam;
    public Permission perOcean;
    public boolean publicOrTeam;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sale_opportunities);
        initView();
    }

    /**
     * 获取客户标签 筛选menu
     */
    public void getStageData() {
        showLoading("");
        RestAdapterFactory.getInstance().build(Config_project.API_URL_CUSTOMER()).create(ICustomer.class).
                GetTags(new RCallback<ArrayList<com.loyo.oa.v2.activityui.customer.bean.Tag>>() {
                    @Override
                    public void success(ArrayList<Tag> tags, Response response) {
                        HttpErrorCheck.checkResponse("客户标签：", response);
                        mTags = tags;
                        try {
                            cloneMdata(tags);
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (ClassNotFoundException e) {
                            e.printStackTrace();
                        }
                        initTitleItem();
                        initChildren();
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        HttpErrorCheck.checkError(error);
                    }
                });
    }

    /**
     * 深克隆筛选数据
     */
    private void cloneMdata(ArrayList<Tag> tags) throws IOException, ClassNotFoundException {
        mTags1 = new ArrayList<>(tags.size());
        mTags2 = new ArrayList<>(tags.size());
        mTags3 = new ArrayList<>(tags.size());

        mTags1 = (ArrayList<com.loyo.oa.v2.activityui.customer.bean.Tag>) Utils.deepCopyT(mTags);
        mTags2 = (ArrayList<com.loyo.oa.v2.activityui.customer.bean.Tag>) Utils.deepCopyT(mTags);
        mTags3 = (ArrayList<com.loyo.oa.v2.activityui.customer.bean.Tag>) Utils.deepCopyT(mTags);
    }

    private void initView() {

        setTitle("我的客户");
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

        //超级管理员权限判断
        if (!MainApp.user.isSuperUser()) {
            try {
                perTeam = (Permission) MainApp.rootMap.get("0308"); //团队客户
                perOcean = (Permission) MainApp.rootMap.get("0309"); //公海客户
                if (!perTeam.isEnable() && !perOcean.isEnable()) {
                    SaleItemStatus = new String[]{"我的客户"};
                    imageArrow.setVisibility(View.INVISIBLE);
                } else if (perTeam.isEnable() && !perOcean.isEnable()) {
                    SaleItemStatus = new String[]{"我的客户", "团队客户"};
                    publicOrTeam = true;
                    imageArrow.setVisibility(View.VISIBLE);
                } else if (!perTeam.isEnable() && perOcean.isEnable()) {
                    SaleItemStatus = new String[]{"我的客户", "公海客户"};
                    publicOrTeam = false;
                    imageArrow.setVisibility(View.VISIBLE);
                } else {
                    imageArrow.setVisibility(View.VISIBLE);
                }
            } catch (NullPointerException e) {
                e.printStackTrace();
                Toast("团队/公海客户,code错误:0308,0309");
            }
        } else {
            imageArrow.setVisibility(View.VISIBLE);
        }

        if (SaleItemStatus.length != 1) {
            layout_title_action.setOnTouchListener(Global.GetTouch());
        }
        rotateAnimation = initArrowAnimation();
        getStageData();
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

    }

    /**
     * 初始化子片段
     */
    private void initChildren() {
        for (int i = 0; i < SaleItemStatus.length; i++) {
            BaseFragment fragment = null;
            if ("我的客户".equals(SaleItemStatus[i])) {
                Bundle b = new Bundle();
                b.putSerializable("tag", mTags1);
                b.putSerializable("permission", permission);
                fragment = (BaseFragment) Fragment.instantiate(this, MyCustomerFragment.class.getName(), b);
            } else if ("团队客户".equals(SaleItemStatus[i])) {
                Bundle b = new Bundle();
                b.putSerializable("tag", mTags2);
                b.putSerializable("permission", permission);
                fragment = (BaseFragment) Fragment.instantiate(this, TeamCustomerFragment.class.getName(), b);
            } else if ("公海客户".equals(SaleItemStatus[i])) {
                Bundle b = new Bundle();
                b.putSerializable("tag", mTags3);
                b.putSerializable("permission", permission);
                fragment = (BaseFragment) Fragment.instantiate(this, CommCustomerFragment.class.getName(), b);
            }
            fragments.add(fragment);
        }
        changeChild(0);
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
                fragmentManager.beginTransaction().replace(R.id.layout_customer_container, fragments.get(index)).commit();
            } catch (IllegalStateException e) {
                e.printStackTrace();
            }
        }
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_title_left:
                finish();
                break;
            case R.id.ll_category:
                break;
            //我的 团队 公海切换
            case R.id.layout_title_action:
                if (SaleItemStatus.length != 1) {
                    changeTitleImg();
                }
                break;
            //搜索
            case R.id.img_title_search_right:
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
                b.putInt(ExtraAndResult.EXTRA_TYPE, type);
                b.putInt("from", BaseActivity.CUSTOMER_MANAGE);
                app.startActivity(this, CustomerSearchActivity.class, MainApp.ENTER_TYPE_RIGHT, false, b);
                break;
        }
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
}
