package com.loyo.oa.v2.activityui.project.fragment;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.library.module.widget.loading.LoadingLayout;
import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.attachment.AttachmentRightActivity_;
import com.loyo.oa.v2.activityui.attachment.bean.Attachment;
import com.loyo.oa.v2.activityui.other.adapter.AttachmentSwipeAdapter;
import com.loyo.oa.v2.activityui.other.model.User;
import com.loyo.oa.v2.activityui.project.HttpProject;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.beans.Project;
import com.loyo.oa.v2.common.Common;
import com.loyo.oa.v2.common.DialogHelp;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.common.http.HttpErrorCheck;
import com.loyo.oa.v2.customview.swipelistview.SwipeListView;
import com.loyo.oa.v2.point.IAttachment;
import com.loyo.oa.v2.tool.BaseFragment;
import com.loyo.oa.v2.tool.Config_project;
import com.loyo.oa.v2.tool.ImageInfo;
import com.loyo.oa.v2.tool.ListUtil;
import com.loyo.oa.v2.tool.LogUtil;
import com.loyo.oa.v2.tool.RCallback;
import com.loyo.oa.v2.tool.RestAdapterFactory;
import com.loyo.oa.v2.tool.SelectPicPopupWindow;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedFile;
import retrofit.mime.TypedString;

/**
 * com.loyo.oa.v2.ui.fragment
 * 描述 :项目附件
 * 作者 : ykb
 * 时间 : 15/9/8.
 */
public class AttachmentFragment extends BaseFragment implements View.OnClickListener{

    private View mView;
    private SwipeListView mListViewAttachment;
    private HttpProject mProject;
    private ArrayList<Attachment> mAttachments = new ArrayList<>();
    private AttachmentSwipeAdapter adapter;
    private ViewGroup layout_upload;
    private int bizType = 5;
    private int uploadSize;
    private int uploadNum;
    private boolean isOver;
    private LoadingLayout ll_loading;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null && getArguments().containsKey("project")) {
            mProject = (HttpProject) getArguments().getSerializable("project");
        }
    }

    /**
     * 显示图片
     *
     * @param mListAttachment
     */
    private void bindAttachment(final ArrayList<Attachment> mListAttachment) {
        ll_loading.setStatus(LoadingLayout.Success);
        if (ListUtil.IsEmpty(mListAttachment)) {
                ll_loading.setStatus(LoadingLayout.Empty);
            return;
        }
        onLoadSuccess(mListAttachment.size());
        final ArrayList<Attachment> sortAttachment = Attachment.Sort(mListAttachment);
        ArrayList<User> users = Common.getUsersByProject(mProject);

        if (mProject.status == 2) {
            isOver = true;
        }

        if (null == adapter) {
            adapter = new AttachmentSwipeAdapter(mActivity, sortAttachment, users, mListViewAttachment, bizType, mProject.attachmentUUId, isOver);
            mListViewAttachment.setAdapter(adapter);
        } else {
            adapter.setData(mListAttachment);
            adapter.setUsers(users);
            adapter.notifyDataSetChanged();
        }

        /*适配器回调*/
        adapter.setAttachmentAction(new AttachmentSwipeAdapter.AttachmentAction() {
            @Override
            public void afterDelete(Attachment attachment) {
                onLoadSuccess(mListAttachment.size() - 1);
                mListAttachment.remove(attachment);
                adapter.setData(mListAttachment);
                adapter.notifyDataSetChanged();
            }
        });
    }

//    /**
//     * 检测权限
//     *
//     * @return
//     */
//    private boolean checkRights() {
//        boolean hasRights = false;
//        if (mProject.creator.equals(MainApp.user)) {
//            hasRights = true;
//        } else {
//            ArrayList<HttpProject.ProjectManaer> members = mProject.managers;
//            if (null != members && !members.isEmpty()) {
//                for (int i = 0; i < members.size(); i++) {
//                    if (members.get(i).user.equals(MainApp.user)) {
//                        hasRights = true;
//                        break;
//                    }
//                }
//            }
//        }
//        if (mProject.status == 2) {
//            return false;
//        }
//        return hasRights;
//    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.layout_upload:
                Intent intent = new Intent(getActivity(), SelectPicPopupWindow.class);
                intent.putExtra("localpic", true);
                intent.putExtra("addpg", false);
                startActivityForResult(intent, MainApp.GET_IMG);
                break;
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (null == mView) {
            mView = inflater.inflate(R.layout.fragment_attachment, container, false);
            ll_loading = (LoadingLayout) mView.findViewById(R.id.ll_loading);
            ll_loading.setStatus(LoadingLayout.Loading);
            ll_loading.setOnReloadListener(new LoadingLayout.OnReloadListener() {
                @Override
                public void onReload(View v) {
                    ll_loading.setStatus(LoadingLayout.Loading);
                    getData();
                }
            });
            mListViewAttachment = (SwipeListView) mView.findViewById(R.id.listView_attachment);
            layout_upload = (ViewGroup) mView.findViewById(R.id.layout_upload);
            layout_upload.setOnClickListener(this);
            getData();
        }
        return mView;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!mProject.isProjectRelevant()) {
            layout_upload.setVisibility(View.GONE);
        }
    }

    /**
     * 获取数据
     */
    private void getData() {
        RestAdapterFactory.getInstance().build(Config_project.API_URL_ATTACHMENT()).create(IAttachment.class).
                getAttachments(mProject.attachmentUUId, new RCallback<ArrayList<Attachment>>() {
                    @Override
                    public void success(ArrayList<Attachment> attachments, Response response) {
                        LogUtil.dll(" 项目的附件获取数据： " + MainApp.gson.toJson(attachments));
                        if (null != attachments && !attachments.isEmpty()) {
                            mAttachments = attachments;
                            bindAttachment(mAttachments);
                        }else {
                            ll_loading.setStatus(LoadingLayout.Success);
                            ll_loading.setStatus(LoadingLayout.Empty);
                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        HttpErrorCheck.checkError(error, ll_loading);
                        super.failure(error);
                    }
                });
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (mProject != null && mProject.status == Project.STATUS_FINISHED) {
            layout_upload.setVisibility(View.GONE);
        } else {
            layout_upload.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onProjectChange(int status) {
        if (null != mProject) {
            mProject.status = status;
        }
        if (layout_upload == null) {
            return;
        }
        if (status == Project.STATUS_FINISHED) {
            layout_upload.setVisibility(View.GONE);
        } else {
            layout_upload.setVisibility(View.VISIBLE);
        }
    }


    /**
     * 批量上传附件
     */
    private void newUploadAttachement(File file) {
        if (uploadSize == 0) {
            DialogHelp.showLoading(getActivity(), "正在上传", true);
        }
        uploadSize++;
        TypedFile typedFile = new TypedFile("image/*", file);
        TypedString typedUuid = new TypedString(mProject.attachmentUUId);
        RestAdapterFactory.getInstance().build(Config_project.API_URL_ATTACHMENT()).create(IAttachment.class).newUpload(typedUuid, bizType, typedFile,
                new RCallback<Attachment>() {
                    @Override
                    public void success(final Attachment attachments, final Response response) {
                        HttpErrorCheck.checkResponse(response);
                        try {
                            Attachment attachment = attachments;
                            if (mAttachments != null) {
                                mAttachments.add(0, attachment);
                            } else {
                                mAttachments = new ArrayList<>(Arrays.asList(attachment));
                            }
                            bindAttachment(mAttachments);
                        } catch (Exception e) {
                            Global.ProcException(e);
                        }
                    }

                    @Override
                    public void failure(final RetrofitError error) {
                        super.failure(error);
                        HttpErrorCheck.checkError(error);
                    }
                });
    }


//    @Override
//    public void onRightClick(Bundle b) {
//        Intent intent = new Intent(mActivity, AttachmentRightActivity_.class);
//        intent.putExtras(b);
//        startActivityForResult(intent, AttachmentSwipeAdapter.REQUEST_ATTACHMENT);
//    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_OK) {
            return;
        }

        switch (requestCode) {
            case AttachmentSwipeAdapter.REQUEST_ATTACHMENT:
                Attachment attachment = (Attachment) data.getSerializableExtra("data");
                if (null != attachment) {
                    for (int i = 0; i < mAttachments.size(); i++) {
                        Attachment tempAttachment = mAttachments.get(i);
                        if (TextUtils.equals(attachment.getId(), tempAttachment.getId())) {
                            mAttachments.set(i, attachment);
                            adapter.setData(mAttachments);
                            adapter.notifyDataSetChanged();
                            break;
                        }
                    }
                }
                break;

            /**
             * 附件上传回调
             * */
            case MainApp.GET_IMG:
                try {
                    ArrayList<ImageInfo> pickPhots = (ArrayList<ImageInfo>) data.getSerializableExtra("data");
                    if (pickPhots == null) {
                        return;
                    }
                    uploadSize = 0;
                    uploadNum = pickPhots.size();
                    for (ImageInfo item : pickPhots) {
                        Uri uri = Uri.parse(item.path);
                        File newFile = Global.scal(getActivity(), uri);
                        if (newFile != null && newFile.length() > 0) {
                            if (newFile.exists()) {
                                newUploadAttachement(newFile);
                            }
                        }
                    }
                } catch (Exception ex) {
                    Global.ProcException(ex);
                }

                break;
        }
    }
}
