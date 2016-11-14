package com.loyo.oa.v2.activityui.worksheet;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;

import com.loyo.oa.photo.PhotoPicker;
import com.loyo.oa.photo.PhotoPreview;
import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.customer.CommonTagSelectActivity;
import com.loyo.oa.v2.activityui.worksheet.bean.WorksheetOrder;
import com.loyo.oa.v2.activityui.worksheet.bean.WorksheetTemplate;
import com.loyo.oa.v2.activityui.worksheet.common.WorksheetConfig;
import com.loyo.oa.v2.activityui.worksheet.fragment.WorksheetAddStep1Fragment;
import com.loyo.oa.v2.activityui.worksheet.fragment.WorksheetAddStep2Fragment;
import com.loyo.oa.v2.common.ExtraAndResult;
import com.loyo.oa.v2.tool.BaseFragment;
import com.loyo.oa.v2.tool.BaseFragmentActivity;
import com.loyo.oa.v2.tool.ImageInfo;

import java.util.ArrayList;
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

    private ArrayList<ImageInfo> pickPhots = new ArrayList<>();

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
            case PhotoPicker.REQUEST_CODE:
                if (data != null) {
                    List<String> photos = data.getStringArrayListExtra(PhotoPicker.KEY_SELECTED_PHOTOS);
                    step2Fragment.addPhoto(photos);
                }
                break;

            /*附件删除回调*/
            case PhotoPreview.REQUEST_CODE:
                int index = data.getExtras().getInt(PhotoPreview.KEY_DELETE_INDEX);
                if (data != null && index >= 0) {
                    step2Fragment.removeAttachmentAt(index);
                }
                break;

            default: {
                Log.v("", "");
                break;
            }
        }
    }
}

