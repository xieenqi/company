package com.loyo.oa.v2.activityui.other;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.commonview.SelectDetUserActivity2;
import com.loyo.oa.v2.activityui.other.adapter.ImageGridViewAdapter;
import com.loyo.oa.v2.activityui.other.presenter.BulletinAddPresenter;
import com.loyo.oa.v2.activityui.other.presenter.BulletinAddPresenterImpl;
import com.loyo.oa.v2.activityui.other.viewcontrol.BulletinAddView;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.activityui.attachment.bean.Attachment;
import com.loyo.oa.v2.beans.Bulletin;
import com.loyo.oa.v2.beans.Members;
import com.loyo.oa.v2.beans.NewUser;
import com.loyo.oa.v2.common.FinalVariables;
import com.loyo.oa.v2.customview.multi_image_selector.MultiImageSelectorActivity;
import com.loyo.oa.v2.tool.BaseActivity;
import com.loyo.oa.v2.tool.SelectPicPopupWindow;
import com.loyo.oa.v2.tool.StringUtil;
import com.loyo.oa.v2.customview.CusGridView;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.OnActivityResult;
import org.androidannotations.annotations.ViewById;
import java.util.ArrayList;
import java.util.List;

/**
 * 【发布通知】MVP重构
 * Restructure by yyy on 2016/10/9
 */

@EActivity(R.layout.activity_bulletin_add)
public class BulletinAddActivity extends BaseActivity implements BulletinAddView {

    @ViewById
    EditText edt_title;
    @ViewById
    EditText edt_content;
    @ViewById
    CusGridView gridView_photo;
    @ViewById
    ViewGroup layout_recevier;
    @ViewById
    TextView tv_recevier;

    private Context mContext;
    private String uuid = StringUtil.getUUID();
    private ImageGridViewAdapter mGridViewAdapter;
    private ArrayList<Attachment> mAttachment = new ArrayList<>();//照片附件的数据
    private ArrayList<SelectPicPopupWindow.ImageInfo> pickPhots = new ArrayList<>();
    private StringBuffer joinUserId, joinName;

    private List<String> mSelectPath;
    private ArrayList<SelectPicPopupWindow.ImageInfo> pickPhotsResult;
    private BulletinAddPresenter mBulletinAddPresenter;

    @AfterViews
    void init() {
        super.setTitle("发布通知");
        mContext = this;
        init_gridView_photo();
        mBulletinAddPresenter = new BulletinAddPresenterImpl(this, mContext);
    }

    /**
     * 添加 图片 附件
     */
    void init_gridView_photo() {
        mGridViewAdapter = new ImageGridViewAdapter(this, true, true, 0, pickPhots);
        ImageGridViewAdapter.setAdapter(gridView_photo, mGridViewAdapter);
    }

    /**
     * 通知谁看
     */
    @Click(R.id.layout_recevier)
    void receiverClick() {
        SelectDetUserActivity2.startThisForAllSelect(BulletinAddActivity.this, joinUserId == null ? null : joinUserId.toString(), true);
    }

    /**
     * 返回
     * */
    @Click(R.id.img_title_left)
    void close() {
        onBackPressed();
    }

    /**
     * 提交
     * */
    @Click(R.id.img_title_right)
    void submit() {
        mBulletinAddPresenter.verifyText(edt_title.getText().toString().trim(),
                                         edt_content.getText().toString().trim());
    }

    /**
     * 相册选择 回调
     */
    @OnActivityResult(MainApp.PICTURE)
    void onPhotoResult(final Intent data) {

        if (null != data) {
            pickPhotsResult = new ArrayList<>();
            mSelectPath = data.getStringArrayListExtra(MultiImageSelectorActivity.EXTRA_RESULT);
            for (String path : mSelectPath) {
                pickPhotsResult.add(new SelectPicPopupWindow.ImageInfo("file://" + path));
            }
            pickPhots.addAll(pickPhotsResult);
            init_gridView_photo();
        }

    }

    /**
     * 相册删除 回调
     */
    @OnActivityResult(FinalVariables.REQUEST_DEAL_ATTACHMENT)
    void onDeletePhotoResult(final Intent data) {
        if (data != null) {
            pickPhots.remove(data.getExtras().getInt("position"));
            init_gridView_photo();
        }
    }

    /**
     * 人员选择 回调
     * */
    @OnActivityResult(SelectDetUserActivity2.REQUEST_ALL_SELECT)
    void onDepartmentUserResult(final int resultCode, final Intent data) {
        if (resultCode != RESULT_OK || data == null) {
            return;
        }
        mBulletinAddPresenter.dealDepartmentResult((Members) data.getSerializableExtra("data"));
    }

    @Override   /*格式验证*/
    public void verifyError(int code) {
        switch (code) {
            case 1:
                Toast("标题不能为空");
                break;

            case 2:
                Toast("内容不能为空");
                break;

            case 3:
                Toast("通知人员不能为空");
                break;
        }
    }

    @Override   /*格式验证通过*/
    public void verifySuccess(String title,String content) {
        if (pickPhots.size() == 0) {
            mBulletinAddPresenter.requestBulletinAdd(title,content,uuid);
        } else {
            mBulletinAddPresenter.uploadAttachement(sweetAlertDialogView, pickPhots,title,content,uuid);
        }
    }

    @Override   /*提交成功*/
    public void onSuccess(Bulletin mBulletin) {
        Toast("提交成功");
        Intent intent = new Intent();
        intent.putExtra("data", mBulletin);
        setResult(RESULT_OK, intent);
        finish();
    }


    @Override   /*提交失败*/
    public void onError() {
        Toast("提交失败");
    }

    @Override   /*打开Loading*/
    public void showLoading() {
        showLoading("正在提交");
    }

    @Override   /*设置人员名字*/
    public void setReceiver(String name) {
        tv_recevier.setText(name);
    }

    @Override   /*关闭弹出框*/
    public void dissweetAlert() {
        dismissSweetAlert();
    }

}