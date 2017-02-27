package com.loyo.oa.v2.activityui.worksheet.fragment;

import android.app.Activity;
import android.content.Context;
import android.net.DhcpInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
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
import com.loyo.oa.v2.activityui.worksheet.bean.Worksheet;
import com.loyo.oa.v2.activityui.worksheet.bean.WorksheetAddModel;
import com.loyo.oa.v2.activityui.worksheet.bean.WorksheetTemplate;
import com.loyo.oa.v2.activityui.worksheet.common.WorksheetConfig;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.customview.PaymentPopView;
import com.loyo.oa.v2.customview.SweetAlertDialogView;
import com.loyo.oa.v2.tool.StringUtil;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 新建工单
 */
public class WorksheetAddFragment extends Fragment implements View.OnClickListener {
    private static final String WorksheetData = "worksheetData";
    private List<WorksheetAddModel> data;//原始数据，不能修改，保持原来的数据不变
    private List<WorksheetAddModel> copyData;//克隆的数据,可以修改，是最新的数据
    private RecyclerView recyclerView;
    private ViewGroup img_title_left;
    private ViewGroup img_title_right;
    private View rootView;
    private OnFragmentEventListener mListener;
    private SweetAlertDialogView sweetAlertDialogView;
    private Activity context;
    private HashMap<String, ArrayList<UploadTask>> taskList = new HashMap<>();//上传任务队列:key是uuid
    private WorksheetAdapter worksheetAdapter;
    private List<UploadController> controllerList=new ArrayList<>();


    public WorksheetAddFragment() {
    }

    public static WorksheetAddFragment newInstance(ArrayList<WorksheetAddModel> data) {
        WorksheetAddFragment fragment = new WorksheetAddFragment();
        Bundle args = new Bundle();
        args.putSerializable(WorksheetData, data);
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            data = (List<WorksheetAddModel>) getArguments().getSerializable(WorksheetData);
            if(null==data){
                data=new ArrayList<>();
            }
        }else{
            data=new ArrayList<>();
        }
        //复制一个列表，注意，是复制对象，不能在原来的数据上面做修改
        copyData=new ArrayList<>();
        for (WorksheetAddModel item :data) {
            copyData.add(item.clone());
        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (null == rootView) {
            rootView = inflater.inflate(R.layout.fragment_worksheet_add, container, false);
            recyclerView = (RecyclerView) rootView.findViewById(R.id.recycle_view);
            img_title_left = (ViewGroup) rootView.findViewById(R.id.img_title_left);
            img_title_right = (ViewGroup) rootView.findViewById(R.id.img_title_right);
            Global.SetTouchView(img_title_left, img_title_right);
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            worksheetAdapter = new WorksheetAdapter();
            recyclerView.setAdapter(worksheetAdapter);
            img_title_left.setOnClickListener(this);
            img_title_right.setOnClickListener(this);
        }
        return rootView;
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_title_left:
                //返回
                boolean isEdit=false;
                //和原来的实体对比，判断是不是修改过
                if(data.size()==copyData.size()){
                    int size=data.size();
                    for (int i = 0; i < size; i++) {
                        if(!data.get(i).equals(copyData.get(i))){
                            //修改过
                            isEdit=true;
                            break;
                        }
                    }
                }else{
                    isEdit=true;
                }
                Toast.makeText(context,"放弃编辑:"+isEdit,Toast.LENGTH_LONG).show();
//                mListener.onBack();
                break;
            case R.id.img_title_right:
                int size=copyData.size();
                for (int i = 0; i < size; i++) {
                    WorksheetAddModel worksheetAddModel = copyData.get(i);
                    Log.i("ttttt", "onClick: "+worksheetAddModel.toString());
                }
               //替换成编辑以后的实体
//                mListener.onSubmit(data);
                break;

        }
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
        final PaymentPopView popViewKind = new PaymentPopView(context, list, "选择工单类型");
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
                WorksheetAddModel worksheetAddModel = copyData.get(position);
                updateModelByField(worksheetAddModel,"typeId",template.id);
                updateModelByField(worksheetAddModel,"typeName",template.name);
            }
        });
    }


    /**
     * 适配器
     */
    class WorksheetAdapter extends RecyclerView.Adapter {
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if (1 == viewType) {
                View view = LayoutInflater.from(context).inflate(R.layout.item_list_worksheet_add_footer, parent, false);
                return new FooterHolder(view);
            } else {
                View view = LayoutInflater.from(context).inflate(R.layout.item_list_worksheet_add, parent, false);
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
                        WorksheetAddModel worksheetAddModel = new WorksheetAddModel();
                        worksheetAddModel.uuid = StringUtil.getUUID();
                        copyData.add(worksheetAddModel);
                        UploadController controller=new UploadController(context,9);
                        controllerList.add(controller);//添加到队列
                        //通知插入了一个
                        notifyItemInserted(getItemCount()-1);
                        //等动画完成，刷新整个列表
                        footerHolder.flAdd.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                notifyDataSetChanged();
                            }
                        },300);
                    }
                });
            } else {
                final WorksheetHolder holder = (WorksheetHolder) baseHolder;
                WorksheetAddModel worksheet = copyData.get(position);
//                holder.tv_order.setText(worksheet.orderName);
                holder.tv_type.setText(worksheet.typeName);
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
                        copyData.remove(position);
                        notifyItemRemoved(position);
                        //等动画完成，刷新整个列表
                        holder.tvDelete.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                notifyDataSetChanged();
                            }
                        },300);
                    }
                });
                UploadController uploadController = controllerList.get(position);
                uploadController.setObserver(new ControllerObserver());
                uploadController.loadView(holder.gridView);

                holder.tvNumTitle.setText("工单" + (position + 1));
                WorksheetAddModel worksheetAddModel = copyData.get(position);
                holder.et_title.addTextChangedListener(new FastSaveTextWatcher(worksheetAddModel,"title"));
                holder.et_content.addTextChangedListener(new FastSaveTextWatcher(worksheetAddModel,"content"));
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

    //上传的回调
    class  ControllerObserver implements UploadControllerCallback{
        @Override
        public void onRetryEvent(UploadController controller, UploadTask task) {
            controller.retry();
        }

        @Override
        public void onAddEvent(UploadController controller) {
            PhotoPicker.builder()
                    .setPhotoCount(9 - controller.count())
                    .setShowCamera(true)
                    .setPreviewEnabled(false)
                    .start(context);
        }

        @Override
        public void onItemSelected(UploadController controller, int index) {
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
                    .start(context);
        }

        @Override
        public void onAllUploadTasksComplete(UploadController controller, ArrayList<UploadTask> taskList) {
            int count = controller.failedTaskCount();
            if (count > 0) {
                return;
            }
            if (taskList.size() > 0) {
            } else {
            }
        }
    }
    /**
     * 反射更新实体的值
     *
     * @param worksheetAddModel
     * @param fieldName
     * @param value
     */
    private void updateModelByField(WorksheetAddModel worksheetAddModel, String fieldName, String value) {
        Field field = getField(worksheetAddModel, fieldName);
        try {
            field.set(worksheetAddModel, value);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取模型中需要更新的字段
     * @param worksheetAddModel
     * @param fieldName
     * @return
     */
    private Field getField(WorksheetAddModel worksheetAddModel, String fieldName) {
        Field field = null;
        Field[] fs = worksheetAddModel.getClass().getDeclaredFields();
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
        private WorksheetAddModel worksheetAddModel;

        private FastSaveTextWatcher(WorksheetAddModel worksheetAddModel, String fieldName) {
           this.worksheetAddModel=worksheetAddModel;
            field=getField(worksheetAddModel,fieldName);
        }

        @Override
        public void afterTextChanged(Editable s) {
            String value = s.toString();
            try {
                field.set(worksheetAddModel, value);
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

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

//        if (context instanceof OnFragmentEventListener) {
//            mListener = (OnFragmentEventListener) context;
//        } else {
//            throw new RuntimeException(context.toString() + " must implement OnFragmentEventListener");
//        }
        this.context = getActivity();
        sweetAlertDialogView = new SweetAlertDialogView(context);
    }
    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
    /**
     * 获取最后的数据
     *
     * @return
     */
    public List<WorksheetAddModel> getData() {
        return data;
    }

    /**
     * 需要在activity中实现的接口
     */
    public interface OnFragmentEventListener {
        void onSubmit(List<WorksheetAddModel> data);

        void onBack();
    }
}
