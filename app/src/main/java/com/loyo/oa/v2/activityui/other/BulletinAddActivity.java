package com.loyo.oa.v2.activityui.other;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.loyo.oa.contactpicker.ContactPickerActivity;
import com.loyo.oa.contactpicker.model.event.ContactPickedEvent;
import com.loyo.oa.contactpicker.model.result.StaffMemberCollection;
import com.loyo.oa.photo.PhotoPicker;
import com.loyo.oa.photo.PhotoPreview;
import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.attachment.bean.Attachment;
import com.loyo.oa.v2.activityui.other.adapter.ImageGridViewAdapter;
import com.loyo.oa.v2.activityui.other.presenter.BulletinAddPresenter;
import com.loyo.oa.v2.activityui.other.presenter.Impl.BulletinAddPresenterImpl;
import com.loyo.oa.v2.activityui.other.viewcontrol.BulletinAddView;
import com.loyo.oa.v2.beans.Bulletin;
import com.loyo.oa.v2.beans.Members;
import com.loyo.oa.v2.common.FinalVariables;
import com.loyo.oa.v2.common.compat.Compat;
import com.loyo.oa.v2.customview.CusGridView;
import com.loyo.oa.v2.tool.BaseActivity;
import com.loyo.oa.v2.tool.ImageInfo;
import com.loyo.oa.v2.tool.StringUtil;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.OnActivityResult;
import org.androidannotations.annotations.ViewById;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

/**
 * 【发布通知】
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
    private ArrayList<ImageInfo> pickPhots = new ArrayList<>();

    private List<String> mSelectPath;
    private ArrayList<ImageInfo> pickPhotsResult;
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
        StaffMemberCollection collection =
                Compat.convertMembersToStaffCollection(mBulletinAddPresenter.getMembers());
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContactPickerActivity.SINGLE_SELECTION_KEY, false);
        if (collection != null) {
            bundle.putSerializable(ContactPickerActivity.STAFF_COLLECTION_KEY, collection);
        }
        Intent intent = new Intent();
        intent.setClass(BulletinAddActivity.this, ContactPickerActivity.class);
        intent.putExtras(bundle);
        startActivity(intent);
        overridePendingTransition(R.anim.enter_righttoleft, R.anim.exit_righttoleft);
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
    @OnActivityResult(PhotoPicker.REQUEST_CODE)
    void onPhotoResult(final Intent data) {
        if (data != null) {
            pickPhotsResult = new ArrayList<>();
            mSelectPath = data.getStringArrayListExtra(PhotoPicker.KEY_SELECTED_PHOTOS);
            for (String path : mSelectPath) {
                pickPhotsResult.add(new ImageInfo("file://" + path));
            }
            pickPhots.addAll(pickPhotsResult);
            init_gridView_photo();
        }

    }

    /**
     * 相册删除 回调
     */
    @OnActivityResult(PhotoPreview.REQUEST_CODE)
    void onDeletePhotoResult(final Intent data) {
        if (data != null) {
            int index = data.getExtras().getInt(PhotoPreview.KEY_DELETE_INDEX);
            if (index >= 0) {
                pickPhots.remove(index);
                init_gridView_photo();
            }
        }
    }

    /**
     * 人员选择 回调
     * */

    /**
     * 选人回调
     */
    @Subscribe
    public void onContactPicked(ContactPickedEvent event) {
        StaffMemberCollection collection = event.data;
        Members members = Compat.convertStaffCollectionToMembers(collection);
        if (members == null) {
            return;
        }
        mBulletinAddPresenter.dealDepartmentResult(members);
    }

    @OnActivityResult(FinalVariables.REQUEST_ALL_SELECT)
    void onDepartmentUserResult(final int resultCode, final Intent data) {
        if (resultCode != RESULT_OK || data == null) {
            return;
        }
        mBulletinAddPresenter.dealDepartmentResult((Members) data.getSerializableExtra("data"));
    }

    /**
     * 格式验证
     * */
    @Override
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

    /**
     * 格式验证通过
     * */
    @Override
    public void verifySuccess(String title,String content) {
        if (pickPhots.size() == 0) {
            mBulletinAddPresenter.requestBulletinAdd(title,content,uuid);
        } else {
            mBulletinAddPresenter.uploadAttachement(sweetAlertDialogView, pickPhots,title,content,uuid);
        }
    }

    /**
     * 提交成功
     * */
    @Override
    public void onSuccess(Bulletin mBulletin) {
        Toast("提交成功");
        Intent intent = new Intent();
        intent.putExtra("data", mBulletin);
        setResult(RESULT_OK, intent);
        finish();
    }

    /**
     * 提交失败
     * */
    @Override
    public void onError() {
        Toast("提交失败");
    }

    /**
     * 打开Loading
     * */
    @Override
    public void showLoading() {
        showLoading("正在提交");
    }

    /**
     * 设置人员名字
     * */
    @Override
    public void setReceiver(String name) {
        tv_recevier.setText(name);
    }

    /**
     * 关闭弹出框
     * */
    @Override
    public void dissweetAlert() {
        dismissSweetAlert();
    }

}