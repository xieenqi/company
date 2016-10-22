package com.loyo.oa.v2.activityui.worksheet;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.clue.common.ClueCommon;
import com.loyo.oa.v2.activityui.customer.CommonTagSelectActivity;
import com.loyo.oa.v2.activityui.customer.CommonTagSelectActivity_;
import com.loyo.oa.v2.activityui.other.adapter.CommonCategoryAdapter;
import com.loyo.oa.v2.activityui.worksheet.bean.WorksheetOrder;
import com.loyo.oa.v2.activityui.worksheet.bean.WorksheetTemplate;
import com.loyo.oa.v2.activityui.worksheet.common.WorksheetConfig;
import com.loyo.oa.v2.activityui.worksheet.common.WorksheetListType;
import com.loyo.oa.v2.activityui.worksheet.fragment.AssignableWorksheetFragment;
import com.loyo.oa.v2.activityui.worksheet.fragment.ResponsableWorksheetFragment;
import com.loyo.oa.v2.activityui.worksheet.fragment.SelfCreatedWorksheetFragment;
import com.loyo.oa.v2.activityui.worksheet.fragment.TeamWorksheetFragment;
import com.loyo.oa.v2.activityui.worksheet.fragment.WorksheetAddStep1Fragment;
import com.loyo.oa.v2.activityui.worksheet.fragment.WorksheetAddStep2Fragment;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.beans.Permission;
import com.loyo.oa.v2.common.ExtraAndResult;
import com.loyo.oa.v2.common.FinalVariables;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.customview.multi_image_selector.MultiImageSelectorActivity;
import com.loyo.oa.v2.tool.BaseActivity;
import com.loyo.oa.v2.tool.BaseFragment;
import com.loyo.oa.v2.tool.BaseFragmentActivity;
import com.loyo.oa.v2.tool.SelectPicPopupWindow;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 新建工单
 */
public class WorksheetAddActivity extends BaseFragmentActivity implements View.OnClickListener {

    private FragmentManager fragmentManager = getSupportFragmentManager();

    WorksheetAddStep1Fragment step1Fragment;
    WorksheetAddStep2Fragment step2Fragment;

    private int mIndex = -1;
    private List<BaseFragment> fragments = new ArrayList<>();

    private ArrayList<SelectPicPopupWindow.ImageInfo> pickPhots = new ArrayList<>();

    public WorksheetTemplate selectedType;
    public WorksheetOrder selectedOrder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_worksheet_add);
        init();
        WorksheetConfig.getWorksheetTypes(true/* 没有数据就从网络获取 */);
        Bundle bundle = getIntent().getExtras();

        if (bundle != null) {
            selectedOrder = (WorksheetOrder) bundle.getSerializable(ExtraAndResult.EXTRA_OBJ);
        }
        // TODO: 建立单独的获取配置Service
        /* 获取配置数据 */
        WorksheetConfig.fetchWorksheetTypes();
    }

    private void init() {
        initChildren();
    }

    /**
     * 初始化子片段
     */
    private void initChildren() {

        Bundle b = new Bundle();
        step1Fragment = (WorksheetAddStep1Fragment) Fragment.instantiate(this, WorksheetAddStep1Fragment.class.getName(), b);
        fragments.add(step1Fragment);

        b = new Bundle();
        step2Fragment = (WorksheetAddStep2Fragment) Fragment.instantiate(this, WorksheetAddStep2Fragment.class.getName(), b);
        fragments.add(step2Fragment);

        changeChild(0);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        }
    }

    /**
     * 改变子片段动画
     *
     * @param index
     */
    private void changeChild(int index) {
        if (index != mIndex && fragments.size() > 0) {

            boolean push = true;
            if (mIndex > index) {
                push = false;
            }

            mIndex = index;
            try {
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                if (push) {
                    fragmentTransaction.setCustomAnimations(
                            R.anim.enter_righttoleft, R.anim.exit_righttoleft,
                            R.anim.enter_righttoleft, R.anim.exit_lefttoright
                    );
                } else {
                    fragmentTransaction.setCustomAnimations(
                            R.anim.enter_lefttoright, R.anim.exit_lefttoright,
                            R.anim.enter_righttoleft, R.anim.exit_lefttoright
                    );
                }
                fragmentTransaction.replace(R.id.fl_order_container, fragments.get(index));
                fragmentTransaction.commit();
            } catch (IllegalStateException e) {
                e.printStackTrace();
            }
        }
    }

    public void nextStep() {
        changeChild(1);
    }

    public void previousStep() {
        changeChild(0);
    }

    @Override
    public void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (null == data || resultCode != RESULT_OK) {
            return;
        }
        switch (requestCode) {

            /*跟进方式 回调*/
            case CommonTagSelectActivity.REQUEST_TAGS:
                break;

            /*相册选择 回调*/
            case MainApp.PICTURE:
                if (null != data) {
                    List<String> mSelectPath = data.getStringArrayListExtra(MultiImageSelectorActivity.EXTRA_RESULT);
                    for (String path : mSelectPath) {
                        pickPhots.add(new SelectPicPopupWindow.ImageInfo("file://" + path));
                    }
                    step2Fragment.loadPhotoData(pickPhots);
                }
                break;

           /*附件删除回调*/
            case FinalVariables.REQUEST_DEAL_ATTACHMENT:
                pickPhots.remove(data.getExtras().getInt("position"));
                step2Fragment.loadPhotoData(pickPhots);
                break;
        }
    }
}

