package com.loyo.oa.v2.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.loopj.android.http.RequestParams;
import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activity.AttachmentRightActivity_;
import com.loyo.oa.v2.adapter.AttachmentSwipeAdapter;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.beans.Attachment;
import com.loyo.oa.v2.beans.Project;
import com.loyo.oa.v2.beans.ProjectMember;
import com.loyo.oa.v2.beans.User;
import com.loyo.oa.v2.common.Common;
import com.loyo.oa.v2.common.FinalVariables;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.common.http.ServerAPI;
import com.loyo.oa.v2.point.IAttachment;
import com.loyo.oa.v2.tool.BaseAsyncHttpResponseHandler;
import com.loyo.oa.v2.tool.BaseFragment;
import com.loyo.oa.v2.tool.ListUtil;
import com.loyo.oa.v2.tool.RCallback;
import com.loyo.oa.v2.tool.SelectPicPopupWindow;
import com.loyo.oa.v2.tool.customview.multi_image_selector.MultiImageSelectorActivity;
import com.loyo.oa.v2.tool.customview.swipelistview.SwipeListView;

import org.apache.http.Header;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * com.loyo.oa.v2.fragment
 * 描述 :项目附件
 * 作者 : ykb
 * 时间 : 15/9/8.
 */
public class AttachmentFragment extends BaseFragment implements View.OnClickListener, AttachmentSwipeAdapter.OnRightClickCallback {

    private View mView;
    private SwipeListView mListViewAttachment;
    private Project mProject;
    private ArrayList<Attachment> mAttachments = new ArrayList<>();
    private AttachmentSwipeAdapter adapter;
    private ViewGroup layout_upload;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null && getArguments().containsKey("project")) {
            mProject = (Project) getArguments().getSerializable("project");
        }
    }

    /**
     * a
     * 显示图片
     *
     * @param mListAttachment
     */
    private void bindAttachment(final ArrayList<Attachment> mListAttachment) {
        if (ListUtil.IsEmpty(mListAttachment)) {
            return;
        }
        onLoadSuccess(mAttachments.size());

        final ArrayList<Attachment> sortAttachment = Attachment.Sort(mListAttachment);
        ArrayList<User> users = Common.getUsersByProject(mProject);
        boolean hasRights = checkRights();
        app.logUtil.e("users : " + users.size());
        if (null == adapter) {
            adapter = new AttachmentSwipeAdapter(mActivity, sortAttachment, users, this, hasRights);
            mListViewAttachment.setAdapter(adapter);
        } else {
            adapter.setData(mListAttachment);
            adapter.setUsers(users);
            adapter.setHasRights(hasRights);
            adapter.notifyDataSetChanged();
        }
        adapter.setAttachmentAction(new AttachmentSwipeAdapter.AttachmentAction() {
            @Override
            public void afterDelete(Attachment attachment) {
                //附件删除后重新绑定
                mListAttachment.remove(attachment);
                adapter.setData(mListAttachment);
                adapter.notifyDataSetChanged();
            }
        });
    }

    /**
     * 检测权限
     *
     * @return
     */
    private boolean checkRights() {
        boolean hasRights = false;
        if (mProject.creator.equals(MainApp.user)) {
            hasRights = true;
        } else {
            ArrayList<ProjectMember> members = mProject.managers;
            if (null != members && !members.isEmpty()) {
                for (int i = 0; i < members.size(); i++) {
                    if (members.get(i).user.equals(MainApp.user)) {
                        hasRights = true;
                        break;
                    }
                }

            }
        }
        return hasRights;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.layout_upload:
                Intent intent = new Intent(mActivity, MultiImageSelectorActivity.class);
                // 是否显示拍摄图片
                intent.putExtra(MultiImageSelectorActivity.EXTRA_SHOW_CAMERA, true);
                // 最大可选择图片数量
                intent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_COUNT, 1);
                // 选择模式
                intent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_MODE, MultiImageSelectorActivity.MODE_SINGLE);
                startActivityForResult(intent, SelectPicPopupWindow.GET_IMG);
                break;
        }
    }

    /**
     * 获取数据
     */
    private void getData() {
        app.getRestAdapter().create(IAttachment.class).getAttachments(mProject.attachmentUUId, new RCallback<ArrayList<Attachment>>() {
            @Override
            public void success(ArrayList<Attachment> attachments, Response response) {

                if (null != attachments && !attachments.isEmpty()) {
                    mAttachments = attachments;
                    bindAttachment(mAttachments);
                }
            }

            @Override
            public void failure(RetrofitError error) {
                super.failure(error);
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (null == mView) {
            mView = inflater.inflate(R.layout.fragment_attachment, container, false);

            mListViewAttachment = (SwipeListView) mView.findViewById(R.id.listView_attachment);
            layout_upload = (ViewGroup) mView.findViewById(R.id.layout_upload);
            layout_upload.setOnClickListener(this);
            getData();
        }
        return mView;
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
            mProject.status=status;
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

    public class AsyncHandler_Upload_New_Attachment extends BaseAsyncHttpResponseHandler {
        File file;

        public void setBitmap(File imageFile) {
            file = imageFile;
        }

        @Override
        public Activity getActivity() {
            return mActivity;
        }

        @Override
        public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
            try {
                Attachment attachment = MainApp.gson.fromJson(getStr(arg2), Attachment.class);
                attachment.saveFile(file);

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

    }

    @Override
    public void onRightClick(Bundle b) {
        Intent intent = new Intent(mActivity, AttachmentRightActivity_.class);
        intent.putExtras(b);
        startActivityForResult(intent, AttachmentSwipeAdapter.REQUEST_ATTACHMENT);
    }

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
            case SelectPicPopupWindow.GET_IMG:
                List<String> mSelectPath = data.getStringArrayListExtra(MultiImageSelectorActivity.EXTRA_RESULT);
                for (String p : mSelectPath) {
                    File newFile = new File(p);
                    try {
                        if (newFile != null && newFile.length() > 0) {
                            RequestParams params = new RequestParams();
                            params.put("uuid", mProject.attachmentUUId);

                            if (newFile.exists()) {
                                params.put("attachments", newFile, "image/jpeg");
                            }

                            ArrayList<ServerAPI.ParamInfo> lstParamInfo = new ArrayList<ServerAPI.ParamInfo>();
                            ServerAPI.ParamInfo paramInfo = new ServerAPI.ParamInfo("bitmap", newFile);
                            lstParamInfo.add(paramInfo);
                            ServerAPI.request(this, ServerAPI.POST, FinalVariables.attachments, null, params, AsyncHandler_Upload_New_Attachment.class, lstParamInfo);
                        }
                    } catch (Exception e) {
                        Global.ProcException(e);
                    }
                }
                break;
        }
    }
}
