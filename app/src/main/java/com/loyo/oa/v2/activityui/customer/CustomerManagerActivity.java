package com.loyo.oa.v2.activityui.customer;

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
import com.loyo.oa.v2.activityui.customer.fragment.CommCustomerFragment;
import com.loyo.oa.v2.activityui.customer.fragment.MyMemberFragment;
import com.loyo.oa.v2.activityui.customer.fragment.MyResponFragment;
import com.loyo.oa.v2.activityui.customer.fragment.TeamCustomerFragment;
import com.loyo.oa.v2.activityui.other.model.Tag;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.common.ExtraAndResult;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.common.http.HttpErrorCheck;
import com.loyo.oa.v2.jpush.HttpJpushNotification;
import com.loyo.oa.v2.permission.BusinessOperation;
import com.loyo.oa.v2.permission.PermissionManager;
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
public class CustomerManagerActivity extends BaseFragmentActivity implements View.OnClickListener, MyMemberFragment.MemberCallback {

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
//
//    /**
//     * 团队客户
//     */
//    public final static int CUSTOMER_TEAM = 3;
//
//    /**
//     * 公海客户
//     */
//    public final static int CUSTOMER_COMM = 4;
//
//    /**
//     * 游客
//     */
//    public final static int CUSTOMER_MMP = 5;
//
//
    /**
     * 个人附近客户
     */
    public final static int NEARCUS_SELF = 1;
//
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
    private int mIndex = -1, jumpType;
    private float mRotation = 0;

    private ArrayList<Tag> mTags;
    private ArrayList<Tag> mTags1;
    private ArrayList<Tag> mTags2;
    private ArrayList<Tag> mTags3;
    public boolean publicOrTeam;
    private List<BaseFragment> fragments = new ArrayList<>();
    private String[] SaleItemStatus = new String[]{"我负责的", "我参与的", "公海客户"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sale_opportunities);
        getIentenData();
        initView();
    }

    /**
     * 获取客户标签 筛选menu
     */
    public void getStageData() {
        showLoading("");
        RestAdapterFactory.getInstance().build(Config_project.API_URL_CUSTOMER()).create(ICustomer.class).
                GetTags(new RCallback<ArrayList<com.loyo.oa.v2.activityui.other.model.Tag>>() {
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

    private void getIentenData() {
        //主要是推送过来
        jumpType = getIntent().getIntExtra(ExtraAndResult.EXTRA_TYPE, 0);
    }

    /**
     * 深克隆筛选数据
     */
    private void cloneMdata(ArrayList<Tag> tags) throws IOException, ClassNotFoundException {
        mTags1 = new ArrayList<>(tags.size());
        mTags2 = new ArrayList<>(tags.size());
        mTags3 = new ArrayList<>(tags.size());

        mTags1 = (ArrayList<com.loyo.oa.v2.activityui.other.model.Tag>) Utils.deepCopyT(mTags);
        mTags2 = (ArrayList<com.loyo.oa.v2.activityui.other.model.Tag>) Utils.deepCopyT(mTags);
        mTags3 = (ArrayList<com.loyo.oa.v2.activityui.other.model.Tag>) Utils.deepCopyT(mTags);
    }

    private void initView() {

        setTitle("我负责的");
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

        if (PermissionManager.getInstance().teamPermission(BusinessOperation.CUSTOMER_MANAGEMENT)) {
            SaleItemStatus = new String[]{"我负责的", "我参与的", "团队客户", "公海客户"};
            publicOrTeam = true;
        }

        if (SaleItemStatus.length != 1) {
            layout_title_action.setOnTouchListener(Global.GetTouch());
        }
        rotateAnimation = initArrowAnimation();
        getStageData();
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
            if ("我负责的".equals(SaleItemStatus[i])) {
                Bundle b = new Bundle();
                b.putSerializable("tag", mTags1);
                fragment = (BaseFragment) Fragment.instantiate(this, MyResponFragment.class.getName(), b);
            } else if ("我参与的".equals(SaleItemStatus[i])) {
                Bundle b = new Bundle();
                b.putSerializable("tag", mTags1);
                fragment = (BaseFragment) Fragment.instantiate(this, MyMemberFragment.class.getName(), b);
            } else if ("团队客户".equals(SaleItemStatus[i])) {
                Bundle b = new Bundle();
                b.putSerializable("tag", mTags2);
                fragment = (BaseFragment) Fragment.instantiate(this, TeamCustomerFragment.class.getName(), b);
            } else if ("公海客户".equals(SaleItemStatus[i])) {
                Bundle b = new Bundle();
                b.putSerializable("tag", mTags3);
                fragment = (BaseFragment) Fragment.instantiate(this, CommCustomerFragment.class.getName(), b);
            }
            fragments.add(fragment);
        }
        if (jumpType == 0 || jumpType == HttpJpushNotification.JumpType.MY_RESON.getValue()) {
            changeChild(0);
        } else if (jumpType == HttpJpushNotification.JumpType.MY_MEMBER.getValue()) {
            changeChild(1);
            setTitle("我参与的");
        }
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
                int type = 0;
                switch (tv_title_1.getText().toString()) {

                    case "我负责的":
                        type = 1;
                        break;

                    case "我参与的":
                        type = 2;
                        break;

                    case "团队客户":
                        type = 3;
                        break;

                    case "公海客户":
                        type = 4;
                        break;

                }

                Bundle b = new Bundle();
                b.putInt(ExtraAndResult.EXTRA_TYPE, type);
                b.putInt("from", BaseActivity.CUSTOMER_MANAGE);
                app.startActivity(this, CustomerSearchActivity.class, MainApp.ENTER_TYPE_RIGHT, false, b);
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

    /**
     * 我参与界面新建完成,要回到我负责界面,直接重启activity
     */
    @Override
    public void comeBackHeadPage() {
        reStart();
    }
}
