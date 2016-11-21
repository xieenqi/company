package com.loyo.oa.v2.activityui.followup;

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
import com.loyo.oa.v2.activityui.customer.adapter.CustomerCategoryAdapter;
import com.loyo.oa.v2.activityui.followup.model.FollowFilter;
import com.loyo.oa.v2.activityui.followup.fragment.SelfFollowUpFragment;
import com.loyo.oa.v2.activityui.followup.fragment.TeamFollowUpFragment;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.beans.Permission;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.common.http.HttpErrorCheck;
import com.loyo.oa.v2.point.ISigninNeworFollowUp;
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
 * 【跟进列表】fragment管理类
 * Created by yyy on 16/11/10
 */
public class FollowUpManagerActivity extends BaseFragmentActivity implements View.OnClickListener {


    private LinearLayout img_title_left, ll_category;
    private ImageView imageArrow;
    private ListView lv_sale;
    private TextView tv_title_1;
    private RelativeLayout layout_title_action, img_title_search_right;

    private Animation rotateAnimation;//标题动画
    private FragmentManager fragmentManager = getSupportFragmentManager();
    private int mIndex = -1;
    private float mRotation = 0;
    public boolean publicOrTeam;

    private List<BaseFragment> fragments = new ArrayList<>();
    private String[] SaleItemStatus = new String[]{"我的跟进"};

    private Permission permission;
    private ArrayList<FollowFilter> mTags;
    private ArrayList<FollowFilter> mTags1;
    private ArrayList<FollowFilter> mTags2;
    private ArrayList<FollowFilter> mTags3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sale_opportunities);
        initView();
    }

    private void initView() {
        setTitle("我的跟进");
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

        layout_title_action.setOnTouchListener(Global.GetTouch());

        //超级管理员权全公司  没有获取到权限就不显示
        permission = MainApp.rootMap.get("0205"); //客户权限 暂时用客户权限做测试
        if ((permission != null && permission.isEnable() && permission.dataRange < 3) || MainApp.user.isSuperUser()) {
            SaleItemStatus = new String[]{"我的跟进", "团队跟进"};
            publicOrTeam = true;
        }

        rotateAnimation = initArrowAnimation();
        getStageData();
    }

    /**
     * 获取客户标签 筛选menu
     */
    public void getStageData() {
        showLoading("");
        RestAdapterFactory.getInstance().build(Config_project.API_URL_CUSTOMER()).create(ISigninNeworFollowUp.class).
                getFollupFilters(new RCallback<ArrayList<FollowFilter>>() {
                    @Override
                    public void success(ArrayList<FollowFilter> tags, Response response) {
                        HttpErrorCheck.checkResponse("跟进 赛选 ：", response);
                        mTags = tags;
                        try {
                            cloneMdata(tags);
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (ClassNotFoundException e) {
                            e.printStackTrace();
                        }
                        initTitleItem();
                        try {
                            initChildren();
                        } catch (NullPointerException e) {
                            e.printStackTrace();
                            Toast("没有获取到权限数据，请重新拉去后再试");
                            finish();
                        }
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
    private void cloneMdata(ArrayList<FollowFilter> tags) throws IOException, ClassNotFoundException {
        mTags1 = new ArrayList<>(tags.size());
        mTags2 = new ArrayList<>(tags.size());
        mTags3 = new ArrayList<>(tags.size());

        mTags1 = (ArrayList<FollowFilter>) Utils.deepCopyT(mTags);
        mTags2 = (ArrayList<FollowFilter>) Utils.deepCopyT(mTags);
        mTags3 = (ArrayList<FollowFilter>) Utils.deepCopyT(mTags);
    }

    void initTitleItem() {
        CustomerCategoryAdapter TitleItemAdapter = new CustomerCategoryAdapter(this, Arrays.asList(SaleItemStatus));
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
            if ("我的跟进".equals(SaleItemStatus[i])) {
                Bundle b = new Bundle();
                b.putSerializable("tag", mTags1);
                b.putSerializable("permission", permission);
                fragment = (BaseFragment) Fragment.instantiate(this, SelfFollowUpFragment.class.getName(), b);
            } else if ("团队跟进".equals(SaleItemStatus[i])) {
                Bundle b = new Bundle();
                b.putSerializable("tag", mTags1);
                b.putSerializable("permission", permission);
                fragment = (BaseFragment) Fragment.instantiate(this, TeamFollowUpFragment.class.getName(), b);
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
            /*返回*/
            case R.id.img_title_left:
                finish();
                break;

            case R.id.ll_category:
                break;

            /*列表切换*/
            case R.id.layout_title_action:
                if (SaleItemStatus.length != 1) {
                    changeTitleImg();
                }
                break;
            /*搜索*/
            case R.id.img_title_search_right:
                Intent mIntent = new Intent(FollowUpManagerActivity.this, FollowUpDetailsActivity.class);
                mIntent.putExtra("id", "582d8ea0608e4f1e39000007");
                startActivity(mIntent);
                break;

            default:
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

    /**
     * 重启Activity
     */
    void reStart() {
        Intent intent = getIntent();
        overridePendingTransition(0, 0);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        finish();
        overridePendingTransition(0, 0);
        startActivity(intent);
    }
}
