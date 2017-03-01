package com.loyo.oa.v2.order.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.loyo.oa.hud.toast.LoyoToast;
import com.loyo.oa.photo.PhotoPicker;
import com.loyo.oa.photo.PhotoPreview;
import com.loyo.oa.upload.UploadController;
import com.loyo.oa.upload.UploadControllerCallback;
import com.loyo.oa.upload.UploadTask;
import com.loyo.oa.upload.view.ImageUploadGridView;
import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.attachment.bean.Attachment;
import com.loyo.oa.v2.activityui.worksheet.bean.OrderWorksheetListModel;
import com.loyo.oa.v2.activityui.worksheet.bean.WorksheetTemplate;
import com.loyo.oa.v2.activityui.worksheet.common.WorksheetConfig;
import com.loyo.oa.v2.attachment.api.AttachmentService;
import com.loyo.oa.v2.beans.AttachmentBatch;
import com.loyo.oa.v2.beans.AttachmentForNew;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.customermanagement.fragment.AttachmentsFragment;
import com.loyo.oa.v2.customview.PaymentPopView;
import com.loyo.oa.v2.network.DefaultLoyoSubscriber;
import com.loyo.oa.v2.tool.BaseFragment;
import com.loyo.oa.v2.tool.StringUtil;
import com.loyo.oa.v2.tool.Utils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * 新建工单
 */
public class WorksheetAddFragment extends BaseStackFragment implements View.OnClickListener, UploadControllerCallback {
    private static final String WorksheetData = "worksheetData";
    private ArrayList<OrderWorksheetListModel> data;//原始数据，不能修改，保持原来的数据不变
    private ArrayList<OrderWorksheetListModel> copyData;//克隆的数据,可以修改，是最新的数据
    private RecyclerView recyclerView;
    private ViewGroup img_title_left;
    private ViewGroup img_title_right;
    private View rootView;
    private onResultCallBack worksheetResultCallBack;
    private ArrayList<UploadTask> notUploadTask = new ArrayList<>();//还没有上传的任务
    private WorksheetAdapter worksheetAdapter;
    private List<UploadController> controllerList = new ArrayList<>();
    private UploadController clickPosController;//点击的那个item的controller
    private final int bizType = 29;//业务id
    private ArrayList<AttachmentForNew> hasAttachment = new ArrayList<>();//上传成功的附件，保存一下，如果要删除的化，用得到

    public WorksheetAddFragment() {
    }

    /**
     * 创建fragment
     *
     * @param data 可以传入默认数据，不需要可以传入null
     * @return
     */

    public static WorksheetAddFragment newInstance(ArrayList<OrderWorksheetListModel> data) {
        WorksheetAddFragment fragment = new WorksheetAddFragment();
        if (null != data) {
            Bundle args = new Bundle();
            args.putSerializable(WorksheetData, data);
            fragment.setArguments(args);
        }
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            data = (ArrayList<OrderWorksheetListModel>) getArguments().getSerializable(WorksheetData);
            if (null == data) {
                data = new ArrayList<>();
            }
        } else {
            data = new ArrayList<>();
        }
        //默认就有一个
        if(data.size()<=0){
            OrderWorksheetListModel OrderWorksheetListModel = new OrderWorksheetListModel();
            OrderWorksheetListModel.uuid = StringUtil.getUUID();
            data.add(OrderWorksheetListModel);
            UploadController controller = new UploadController(mActivity, 9);
            controllerList.add(controller);//添加到队列
        }

        //复制一个列表，注意，是复制对象，不能在原来的数据上面做修改
        copyData = new ArrayList<>();
        for (OrderWorksheetListModel item : data) {
            copyData.add(item.clone());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //
        if (null == rootView) {
            rootView = inflater.inflate(R.layout.fragment_worksheet_add, container, false);
            recyclerView = (RecyclerView) rootView.findViewById(R.id.recycle_view);
            img_title_left = (ViewGroup) rootView.findViewById(R.id.img_title_left);
            img_title_right = (ViewGroup) rootView.findViewById(R.id.img_title_right);
            Global.SetTouchView(img_title_left, img_title_right);
            recyclerView.setLayoutManager(new LinearLayoutManager(mActivity));
            worksheetAdapter = new WorksheetAdapter();
            recyclerView.setAdapter(worksheetAdapter);
            img_title_left.setOnClickListener(this);
            img_title_right.setOnClickListener(this);
        } else {
            //因为重新走来生命周期，数据是克隆来的，观察者模式失效，所以要重新通知数据改变
            worksheetAdapter.notifyDataSetChanged();
        }
        return rootView;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_title_left:
                //返回
                back();
                break;
            case R.id.img_title_right:
                //验证数据没有通过
                if (!verify()) return;
                //替换成编辑以后的实体
                UploadController uploaders = new UploadController(mActivity, Integer.MAX_VALUE);
                for (UploadController uploader : controllerList) {
                    uploaders.addAllTask(uploader.getTaskList());
                }
                getNotUploadTask();//上传之前，先获取没有上传的，方便等下上传成功后绑定附件
                //如果有没有上传的附件
                if (notUploadTask.size() > 0) {
                    showLoading2("");
                    uploaders.setObserver(this);
                    uploaders.startUpload();
                    uploaders.notifyCompletionIfNeeded();
                } else {
                    if (null != worksheetResultCallBack)
                        worksheetResultCallBack.onWorksheetSubmit(copyData);
                }
                break;

        }
    }

    /**
     * 提供给外部调用，主要是拦截物理back按键，然后调用本方法返回
     */
    public void back() {
        boolean isEdit = false;
        //和原来的实体对比，判断是不是修改过
        if (data.size() == copyData.size()) {
            //对原来的数据进行了编辑
            int size = copyData.size();
            for (int i = 0; i < size; i++) {
                if (!data.get(i).equals(copyData.get(i))) {
                    //修改过
                    isEdit = true;
                    break;
                }
            }
        } else {
            //增加或者删除了工单
            isEdit = true;
        }
        //创建新的数据，并且填入的有值
        if (!isEdit && data.size() == 0) {
            int size = copyData.size();
            for (int i = 0; i < size; i++) {
                if (!copyData.get(i).isEmpty()) {
                    //修改过
                    isEdit = true;
                    break;
                }
            }
        }
        //判断是不是有未上传的附件
        if (!isEdit) {
            int size = controllerList.size();
            for (int i = 0; i < size && !isEdit; i++) {
                UploadController uploadController = controllerList.get(i);
                ArrayList<UploadTask> taskList = uploadController.getTaskList();
                for (UploadTask uptask : taskList) {
                    if (uptask.getStatus() != UploadTask.UPLOADED) {
                        isEdit = true;
                        break;
                    }
                }
            }
        }
        if (isEdit) {
            sweetAlertDialogView.alertHandle(new SweetAlertDialog.OnSweetClickListener() {
                @Override
                public void onClick(SweetAlertDialog sweetAlertDialog) {
                    sweetAlertDialog.dismiss();
                }
            }, new SweetAlertDialog.OnSweetClickListener() {
                @Override
                public void onClick(SweetAlertDialog sweetAlertDialog) {
                    sweetAlertDialog.dismiss();
                    //移除没有保存的附件
                    int size = controllerList.size();
                    for (int i = 0; i < size; i++) {
                        UploadController uploadController = controllerList.get(i);
                        ArrayList<UploadTask> taskList = uploadController.getTaskList();
                        //在循环中不能删除item，记录下来，统一删除
                        ArrayList<UploadTask> delTask = new ArrayList<UploadTask>();
                        for (UploadTask uptask : taskList) {
                            if (uptask.getStatus() != UploadTask.UPLOADED) {
                                delTask.add(uptask);
                            }
                        }
                        for (UploadTask uptask : delTask) {
                            taskList.remove(uptask);
                        }
                        uploadController.reloadGridView();
                    }
                    if (null != worksheetResultCallBack) worksheetResultCallBack.onWorksheetBack();
                }
            }, "提示", "是否放弃编辑？确定后信息将不会保存。");
        } else {
            if (null != worksheetResultCallBack) worksheetResultCallBack.onWorksheetBack();
        }
    }

    //验证数据的有效性
    private boolean verify() {
        int size = copyData.size();
        for (int i = 0; i < size; i++) {
            String verify = copyData.get(i).verify();
            if (null != verify) {
                verify = "“工单" + (i + 1) + "”" + verify;
                Toast.makeText(mActivity, verify, Toast.LENGTH_LONG).show();
                return false;
            }
        }
        for (int i = 0; i < size; i++) {
            for (int j = i + 1; j < size; j++) {
                if (TextUtils.equals(copyData.get(i).title, copyData.get(j).title)) {
                    Toast.makeText(mActivity, "工单" + (i + 1) + "和" + (j + 1) + "标题重复", Toast.LENGTH_LONG).show();
                    return false;
                }
            }
        }
        return true;
    }

    private void chooseWorksheetType(final WorksheetAdapter.WorksheetHolder holder, final int position) {
        final ArrayList<WorksheetTemplate> types = WorksheetConfig.getWorksheetTypes(true);
        if (types == null || types.size() == 0) {
            sweetAlertDialogView.alertIcon("无可选工单类型", null);
            return;
        }
        String[] list = new String[types.size()];
        for (int i = 0; i < types.size(); i++) {
            list[i] = types.get(i).name;
        }
        final PaymentPopView popViewKind = new PaymentPopView(mActivity, list, "选择工单类型");
        popViewKind.show();
        popViewKind.setCanceledOnTouchOutside(true);
        popViewKind.setCallback(new PaymentPopView.VaiueCallback() {
            @Override
            public void setValue(String value, int index) {
                WorksheetTemplate template = types.get(index - 1);
                if (template.hasItems == false) {
                    sweetAlertDialogView.alertIcon(null, "该工单类型未配置模版,请选择其他类型!");
                    return;
                }
                holder.tv_type.setText(template.name);
                OrderWorksheetListModel OrderWorksheetListModel = copyData.get(position);
                updateModelByField(OrderWorksheetListModel, "templateId", template.id);
                updateModelByField(OrderWorksheetListModel, "templateName", template.name);
            }
        });
    }

    @Override
    public boolean onBackPressed() {
        back();
        return true;
    }


    /**
     * 适配器
     */
    class WorksheetAdapter extends RecyclerView.Adapter {
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if (1 == viewType) {
                View view = LayoutInflater.from(mActivity).inflate(R.layout.item_list_worksheet_add_footer, parent, false);
                return new FooterHolder(view);
            } else {
                View view = LayoutInflater.from(mActivity).inflate(R.layout.item_list_worksheet_add, parent, false);
                return new WorksheetHolder(view);
            }

        }

        @Override
        public void onBindViewHolder(final RecyclerView.ViewHolder baseHolder, final int position) {
            if (baseHolder instanceof FooterHolder) {
                //底部按钮
                final FooterHolder footerHolder = (FooterHolder) baseHolder;
                footerHolder.tvAdd.setText("添加第" + getItemCount() + "个工单");
                footerHolder.flAdd.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        OrderWorksheetListModel OrderWorksheetListModel = new OrderWorksheetListModel();
                        OrderWorksheetListModel.uuid = StringUtil.getUUID();
                        copyData.add(OrderWorksheetListModel);
                        UploadController controller = new UploadController(mActivity, 9);
                        controllerList.add(controller);//添加到队列
                        //通知插入了一个
                        notifyItemInserted(getItemCount() - 1);
                        //等动画完成，刷新整个列表
                        footerHolder.flAdd.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                notifyDataSetChanged();
                            }
                        }, 300);
                    }
                });
            } else {
                final WorksheetHolder holder = (WorksheetHolder) baseHolder;
                OrderWorksheetListModel worksheet = copyData.get(position);
//                holder.tv_order.setText(worksheet.orderName);
                holder.tv_type.setText(worksheet.templateName);
                holder.et_title.setText(worksheet.title);
                holder.et_content.setText(worksheet.content);
                //选择工单类型
                holder.ll_type.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        chooseWorksheetType(holder, position);
                    }
                });
                //删除
                holder.tvDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (copyData.get(position).isEmpty() && controllerList.get(position).getTaskList().size() <= 0) {
                            //没有填入数据，并且没有添加图片，直接删除
                            copyData.remove(position);
                            notifyItemRemoved(position);
                            controllerList.remove(position);//移除选择的附件
                            //等动画完成，刷新整个列表
                            holder.tvDelete.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    notifyDataSetChanged();
                                }
                            }, 300);
                        } else {
                            sweetAlertDialogView.alertHandle(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sweetAlertDialog) {
                                    sweetAlertDialog.dismiss();
                                }
                            }, new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sweetAlertDialog) {
                                    sweetAlertDialog.dismiss();
                                    //没有填入数据，并且没有添加图片，直接删除
                                    copyData.remove(position);
                                    notifyItemRemoved(position);
                                    controllerList.remove(position);//移除选择的附件
                                    //等动画完成，刷新整个列表
                                    holder.tvDelete.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            notifyDataSetChanged();
                                        }
                                    }, 300);
                                }
                            }, "提示", "确定删除“工单" + (position + 1) + "”？");
                        }
                    }
                });

                UploadController uploadController = null;
                if (controllerList.size() <= position) {
                    //如果不存在上传组建，就新建一个
                    uploadController = new UploadController(mActivity, 9);
                    controllerList.add(uploadController);//添加到队列
                } else {
                    uploadController = controllerList.get(position);
                }
                uploadController.setObserver(WorksheetAddFragment.this);
                holder.gridView.setTag(position + "");//使用tag保存一下绑定数据的位置
                uploadController.loadView(holder.gridView);
                holder.tvNumTitle.setText("工单" + (position + 1));
                //设置监听器，绑定ui和模型,如此蛋疼的写，是避免一个editview上面绑定多个watcher
                FastSaveTextWatcher watcher = (FastSaveTextWatcher) holder.et_title.getTag();
                if (null != watcher) holder.et_title.removeTextChangedListener(watcher);
                holder.et_title.setTag(new FastSaveTextWatcher(worksheet, "title"));
                holder.et_title.addTextChangedListener((TextWatcher) holder.et_title.getTag());
                watcher = (FastSaveTextWatcher) holder.et_content.getTag();
                if (null != watcher) holder.et_content.removeTextChangedListener(watcher);
                holder.et_content.setTag(new FastSaveTextWatcher(worksheet, "content"));
                holder.et_content.addTextChangedListener((TextWatcher) holder.et_content.getTag());
            }
        }

        @Override
        public int getItemCount() {
            //因为有一个底部按钮，所以＋1
            return copyData.size() + 1;
        }

        @Override
        public int getItemViewType(int position) {
            int type = 0;//默认的类型，也就是工单列表
            //最后一个item是添加按钮
            if (position == copyData.size()) {
                type = 1;
            }
            return type;
        }

        class WorksheetHolder extends RecyclerView.ViewHolder {
            //            public LinearLayout ll_order;
//            public TextView tv_order;
            public LinearLayout ll_type;
            public TextView tv_type;
            public EditText et_title;
            public EditText et_content;
            public ImageUploadGridView gridView;
            private RelativeLayout rlNum;
            private TextView tvNumTitle;
            private TextView tvDelete;

            public WorksheetHolder(View itemView) {
                super(itemView);
                rlNum = (RelativeLayout) itemView.findViewById(R.id.rl_num);
                tvNumTitle = (TextView) itemView.findViewById(R.id.tv_num_title);
                tvDelete = (TextView) itemView.findViewById(R.id.tv_delete);
//                ll_order= (LinearLayout) itemView.findViewById(R.id.ll_order);
//                tv_order= (TextView) itemView.findViewById(R.id.tv_order);
                ll_type = (LinearLayout) itemView.findViewById(R.id.ll_type);
                tv_type = (TextView) itemView.findViewById(R.id.tv_type);
                et_title = (EditText) itemView.findViewById(R.id.et_title);
                et_content = (EditText) itemView.findViewById(R.id.edt_content);
                gridView = (ImageUploadGridView) itemView.findViewById(R.id.image_upload_grid_view);
            }

        }

        //底部添加按钮
        class FooterHolder extends RecyclerView.ViewHolder {
            public FrameLayout flAdd;
            public TextView tvAdd;

            public FooterHolder(View itemView) {
                super(itemView);
                flAdd = (FrameLayout) itemView.findViewById(R.id.fl_add);
                tvAdd = (TextView) itemView.findViewById(R.id.tv_add);
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            /*相册选择 回调*/
            case PhotoPicker.REQUEST_CODE:
                if (data != null) {
                    String s = (String) clickPosController.getGridView().getTag();
                    int position = Integer.parseInt(s);
                    OrderWorksheetListModel OrderWorksheetListModel = copyData.get(position);
                    List<String> mSelectPath = data.getStringArrayListExtra(PhotoPicker.KEY_SELECTED_PHOTOS);
                    for (String path : mSelectPath) {
                        clickPosController.addUploadTask("file://" + path, null, OrderWorksheetListModel.uuid);
                    }
                    clickPosController.reloadGridView();
                }
                break;
            /*附件删除回调*/
            case PhotoPreview.REQUEST_CODE:
                if (data != null) {
                    final int index = data.getExtras().getInt(PhotoPreview.KEY_DELETE_INDEX);
                    if (index >= 0) {
                        UploadTask rmTask = clickPosController.getTaskList().get(index);
                        HashMap<String, Object> map = new HashMap<String, Object>();
                        map.put("bizType", bizType);
                        map.put("uuid", rmTask.UUID);
                        String attachmentId = null;
                        //找到要删的附件的attachmentId
                        for (AttachmentForNew atta : hasAttachment) {
                            if (TextUtils.equals(rmTask.UUID, atta.getUUId()) && atta.getName().endsWith(rmTask.name)) {//按照名字和uuid来判断是不是一张图片
                                attachmentId = atta.getId();
                                break;
                            }
                        }
                        //发送删除的请求
                        if (!TextUtils.isEmpty(attachmentId)) {
                            showLoading2("");
                            AttachmentService.remove(attachmentId, map)
                                    .subscribe(new DefaultLoyoSubscriber<Object>(hud) {
                                        @Override
                                        public void onNext(Object object) {
                                            //从本地ui删除
                                            clickPosController.removeTaskAt(index);
                                            clickPosController.reloadGridView();
                                        }
                                    });
                        }else{
                            //说明没有上传，直接在本地删除
                            clickPosController.removeTaskAt(index);
                            clickPosController.reloadGridView();
                        }

                    }
                }
                break;
        }
    }


    @Override
    public void onRetryEvent(UploadController controller, UploadTask task) {
        controller.retry();
    }

    @Override
    public void onAddEvent(UploadController controller) {
        clickPosController = controller;
        PhotoPicker.builder()
                .setPhotoCount(9 - controller.count())
                .setShowCamera(true)
                .setPreviewEnabled(false)
                .start(mActivity, WorksheetAddFragment.this);
    }

    @Override
    public void onItemSelected(UploadController controller, int index) {
        clickPosController = controller;

        ArrayList<UploadTask> taskList = controller.getTaskList();
        ArrayList<String> selectedPhotos = new ArrayList<>();

        for (int i = 0; i < taskList.size(); i++) {
            String path = taskList.get(i).getValidatePath();
            if (path.startsWith("file://")) ;
            {
                path = path.replace("file://", "");
            }
            selectedPhotos.add(path);
        }
        PhotoPreview.builder()
                .setPhotos(selectedPhotos)
                .setCurrentItem(index)
                .setShowDeleteButton(true)
                .start(mActivity, WorksheetAddFragment.this);
        controller.reloadGridView();
    }

    @Override
    public void onAllUploadTasksComplete(UploadController controller, ArrayList<UploadTask> taskList) {
        int count = controller.failedTaskCount();
        cancelLoading2();
        if (count > 0) {
            LoyoToast.info(mActivity, count + "个附件上传失败，请重试或者删除");
            return;
        }
        //绑定附件信息
        postAttaData();

    }


    /**
     * 获取还没有上传的任务
     */
    private void getNotUploadTask() {
        for (int j = 0; j < controllerList.size(); j++) {
            UploadController controller = controllerList.get(j);
            ArrayList<UploadTask> list = controller.getTaskList();
            for (int i = 0; i < list.size(); i++) {
                UploadTask task = list.get(i);
                if (UploadTask.UPLOADED == task.getStatus()) {
                    continue;
                }
                notUploadTask.add(task);
            }
        }
    }

    /**
     * 构造绑定附件的信息
     */
    private ArrayList<AttachmentBatch> buildAttachment() {
        ArrayList<AttachmentBatch> attachment = new ArrayList<>();
        for (UploadTask task : notUploadTask) {
            AttachmentBatch attachmentBatch = new AttachmentBatch();
            attachmentBatch.UUId = task.UUID;
            attachmentBatch.bizType = bizType;//工单附件的业务id
            attachmentBatch.mime = Utils.getMimeType(task.getValidatePath());
            attachmentBatch.name = task.getKey();
            attachmentBatch.size = Integer.parseInt(task.size + "");
            attachment.add(attachmentBatch);
        }
        return attachment;
    }

    /**
     * 上传附件信息
     */
    public void postAttaData() {
        AttachmentService.setAttachementData(buildAttachment())
                .subscribe(new DefaultLoyoSubscriber<ArrayList<AttachmentForNew>>(hud, true/*dismissOnlyWhenError*/) {
                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        e.printStackTrace();
                    }

                    @Override
                    public void onNext(ArrayList<AttachmentForNew> news) {
                        hasAttachment.addAll(news);
                        //绑定完了，就清空原来的列表，避免重复绑定
                        notUploadTask.clear();
                        if (null != worksheetResultCallBack) {
                            worksheetResultCallBack.onWorksheetSubmit(copyData);
                        }
                    }
                });
    }

    /**
     * 反射更新实体的值
     *
     * @param OrderWorksheetListModel
     * @param fieldName
     * @param value
     */
    private void updateModelByField(OrderWorksheetListModel OrderWorksheetListModel, String fieldName, String value) {
        Field field = getField(OrderWorksheetListModel, fieldName);
        try {
            field.set(OrderWorksheetListModel, value);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取模型中需要更新的字段
     *
     * @param OrderWorksheetListModel
     * @param fieldName
     * @return
     */
    private Field getField(OrderWorksheetListModel OrderWorksheetListModel, String fieldName) {
        Field field = null;
        Field[] fs = OrderWorksheetListModel.getClass().getDeclaredFields();
        for (int i = 0; i < fs.length; i++) {
            Field f = fs[i];
            f.setAccessible(true); //设置属性是可以访问的
            //是要访问的属性
            if (fieldName.equals(f.getName())) {
                field = f;
                break;
            }
        }
        if (field == null) {
            throw new UnsupportedOperationException("unknow field");
        } else {
            return field;
        }
    }

    /**
     * 在textview中修改值，利用反射，快速修改实体的值
     */
    private class FastSaveTextWatcher implements TextWatcher {
        private Field field;
        private OrderWorksheetListModel OrderWorksheetListModel;

        private FastSaveTextWatcher(OrderWorksheetListModel OrderWorksheetListModel, String fieldName) {
            this.OrderWorksheetListModel = OrderWorksheetListModel;
            field = getField(OrderWorksheetListModel, fieldName);
        }

        @Override
        public void afterTextChanged(Editable s) {
            String value = s.toString();
            try {
                field.set(OrderWorksheetListModel, value);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }
    }

    public void setWorksheetResultCallBack(onResultCallBack worksheetResultCallBack) {
        this.worksheetResultCallBack = worksheetResultCallBack;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        worksheetResultCallBack = null;
    }

    /**
     * 获取最后的数据
     *
     * @return
     */
    public ArrayList<OrderWorksheetListModel> getData() {
        return data;
    }

    /**
     * 需要在activity中实现的接口
     */
    public interface onResultCallBack {
        void onWorksheetSubmit(ArrayList<OrderWorksheetListModel> data);

        void onWorksheetBack();
    }
}
