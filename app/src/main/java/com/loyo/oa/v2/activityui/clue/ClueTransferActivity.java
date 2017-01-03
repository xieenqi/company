package com.loyo.oa.v2.activityui.clue;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.loyo.oa.hud.toast.LoyoToast;
import com.loyo.oa.photo.PhotoPicker;
import com.loyo.oa.photo.PhotoPreview;
import com.loyo.oa.upload.UploadController;
import com.loyo.oa.upload.UploadControllerCallback;
import com.loyo.oa.upload.UploadTask;
import com.loyo.oa.upload.view.ImageUploadGridView;
import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.attachment.bean.Attachment;
import com.loyo.oa.v2.activityui.clue.model.ClueSales;
import com.loyo.oa.v2.activityui.commonview.MapModifyView;
import com.loyo.oa.v2.activityui.commonview.bean.PositionResultItem;
import com.loyo.oa.v2.activityui.customer.CustomerLabelActivity_;
import com.loyo.oa.v2.activityui.customer.CustomerRepeat;
import com.loyo.oa.v2.activityui.customer.model.Contact;
import com.loyo.oa.v2.activityui.customer.model.ContactLeftExtras;
import com.loyo.oa.v2.activityui.customer.model.Customer;
import com.loyo.oa.v2.activityui.customer.model.HttpAddCustomer;
import com.loyo.oa.v2.activityui.customer.model.NewTag;
import com.loyo.oa.v2.activityui.other.adapter.ImageGridViewAdapter;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.attachment.api.AttachmentService;
import com.loyo.oa.v2.beans.AttachmentBatch;
import com.loyo.oa.v2.common.ExtraAndResult;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.customermanagement.api.CustomerService;
import com.loyo.oa.v2.db.DBManager;
import com.loyo.oa.v2.network.DefaultLoyoSubscriber;
import com.loyo.oa.v2.tool.BaseActivity;
import com.loyo.oa.v2.tool.ImageInfo;
import com.loyo.oa.v2.tool.LocationUtilGD;
import com.loyo.oa.v2.tool.LogUtil;
import com.loyo.oa.v2.tool.StringUtil;
import com.loyo.oa.v2.tool.UMengTools;
import com.loyo.oa.v2.tool.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 【线索转移客户】
 * Created by yyy on 16/8/22.
 */
public class ClueTransferActivity extends BaseActivity implements View.OnClickListener, UploadControllerCallback{

    public static final int REQUEST_CUSTOMER_LABEL = 5;
    public static final int REQUEST_CUSTOMER_NEW_CONTRACT = 6;
    public static final int REQUEST_CUSTOMER_SERACH = 7;

    private EditText edt_name;
    private EditText et_address;
    private EditText edit_address_details;
    private EditText edt_contract;
    private EditText edt_contract_tel;
    private EditText edt_contract_telnum;
    private TextView tv_search;
    private TextView tv_labels;
    private LinearLayout layout_customer_label;
    private LinearLayout layout_newContract;
    private RelativeLayout img_title_left;
    private RelativeLayout img_title_right;
    private ImageView img_refresh_address;
    ImageUploadGridView gridView;

    private String uuid = StringUtil.getUUID();
    private Bundle mBundle;
    private Intent mIntent;

    private String myAddress;
    private String customer_name;
    private String customerAddress;
    private String customerContract;
    private String customerContractTel;
    private String customerWrietele;
    private String cusotmerDetalisAddress;
    private String tagItemIds;

    private LocationUtilGD locationGd;
    private ImageGridViewAdapter imageGridViewAdapter;
    private ArrayList<ImageInfo> pickPhots = new ArrayList<>();
    private ArrayList<Contact> mContacts = new ArrayList<>();
    private ArrayList<NewTag> tags;
    private ArrayList<ContactLeftExtras> mCusList;
    private List<String> mSelectPath;
    private ArrayList<ImageInfo> pickPhotsResult;

    UploadController controller;

    private int bizType = 0x01;
    private int uploadSize;
    private int uploadNum;

    private double laPosition;//当前位置的经纬度
    private double loPosition;

    private boolean isSave = true;
    private boolean cusGuys = false;  //联系人权限
    private boolean cusPhone = false; //手机权限
    private boolean cusMobile = false;//座机权限
    private PositionResultItem positionResultItem;
    private ClueSales mCluesales;

    private Handler mHandler = new Handler() {
        @Override
        public void dispatchMessage(final Message msg) {
            if (msg.what == 0x01) {
                et_address.setText(myAddress);
                if(null == mCluesales.address || TextUtils.isEmpty(mCluesales.address)){
                    edit_address_details.setText(myAddress);
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cluetransfer);
        initUi();
    }

    public void getIntentData(){
        mIntent = getIntent();
        mCluesales = (ClueSales) mIntent.getSerializableExtra(ExtraAndResult.EXTRA_DATA);
        if(null == mCluesales){
            onBackPressed();
            Toast("参数为Null");
        }
    }

    public void initUi(){
        super.setTitle("线索转换客户");
        getIntentData();
        img_title_left = (RelativeLayout) findViewById(R.id.img_title_left);
        img_title_right = (RelativeLayout) findViewById(R.id.img_title_right);
        edt_name = (EditText) findViewById(R.id.edt_name);
        et_address = (EditText) findViewById(R.id.et_address);
        edit_address_details = (EditText) findViewById(R.id.edit_address_details);
        edt_contract = (EditText) findViewById(R.id.edt_contract);
        edt_contract_tel = (EditText) findViewById(R.id.edt_contract_tel);
        edt_contract_telnum = (EditText) findViewById(R.id.edt_contract_telnum);
        tv_search = (TextView) findViewById(R.id.tv_search);
        tv_labels = (TextView) findViewById(R.id.tv_labels);
        layout_customer_label = (LinearLayout) findViewById(R.id.layout_customer_label);
        layout_newContract = (LinearLayout) findViewById(R.id.layout_newContract);
        img_refresh_address = (ImageView) findViewById(R.id.img_refresh_address);
        gridView = (ImageUploadGridView) findViewById(R.id.image_upload_grid_view);

        img_refresh_address.setOnClickListener(this);
        tv_search.setOnClickListener(this);
        img_title_left.setOnClickListener(this);
        img_title_right.setOnClickListener(this);
        layout_customer_label.setOnClickListener(this);

        img_title_left.setOnTouchListener(Global.GetTouch());
        img_title_right.setOnTouchListener(Global.GetTouch());

        getTempCustomer();
        startLocation();
        requestJurisdiction();
        if (app.latitude != -1 && app.longitude != -1) {
            laPosition = app.latitude;
            loPosition = app.longitude;
        }

        edt_name.setText(mCluesales.companyName);         //公司名字
        edit_address_details.setText(mCluesales.address); //地址
        edt_contract.setText(mCluesales.name);            //联系人
        edt_contract_tel.setText(mCluesales.cellphone);   //手机号
        edt_contract_telnum.setText(mCluesales.tel);      //座机号

        controller = new UploadController(this, 9);
        controller.setObserver(this);
        controller.loadView(gridView);

    }


    /**
     * 获取新建客户权限
     * */
    public void requestJurisdiction(){
        showLoading2("");
        HashMap<String,Object> map = new HashMap<>();
        map.put("bizType",100);
        CustomerService.getAddCustomerJur(map)
                .subscribe(new DefaultLoyoSubscriber<ArrayList<ContactLeftExtras>>(hud) {
                    public void onNext(ArrayList<ContactLeftExtras> contactLeftExtrasArrayList) {
                        mCusList = contactLeftExtrasArrayList;
                        for (ContactLeftExtras customerJur : contactLeftExtrasArrayList) {
                            if (customerJur.label.contains("联系人") && customerJur.required) {
                                cusGuys = true;
                                edt_contract.setHint("请输入联系人姓名(必填)");
                            } else if (customerJur.label.contains("手机") && customerJur.required) {
                                cusPhone = true;
                                edt_contract_tel.setHint("请输入联系人手机号(必填)");
                            } else if (customerJur.label.contains("座机") && customerJur.required) {
                                cusMobile = true;
                                edt_contract_telnum.setHint("请输入联系人座机(必填)");
                            }
                        }
                    }
                });
    }


    void getTempCustomer() {
        Customer customer = DBManager.Instance().getCustomer();
        if (customer == null) return;

        edt_name.setText(customer.name);
        ArrayList<Contact> contacts = customer.contacts;
        if (contacts != null && contacts.size() > 0) {
            for (Contact c : contacts) {
                if (c.isDefault()) {
                    edt_contract.setText(c.getName());
                    edt_contract_tel.setText(c.getTel());
                } else {
                    setContract(c);
                }
            }
        }
    }

    /**
     * 获取定位
     */
    void startLocation() {

        et_address.setText(app.address);
        locationGd = new LocationUtilGD(this, new LocationUtilGD.AfterLocation() {
            @Override
            public void OnLocationGDSucessed(final String address, final double longitude, final double latitude, final String radius) {
                UMengTools.sendLocationInfo(address, longitude, latitude);
                myAddress = address;
                mHandler.sendEmptyMessage(0x01);
                LocationUtilGD.sotpLocation();
            }

            @Override
            public void OnLocationGDFailed() {
                Toast(R.string.LOCATION_FAILED);
                LocationUtilGD.sotpLocation();
            }
        });
    }

    void setContract(final Contact c) {
        if (c == null) return;
        mContacts.add(c);
        layout_newContract.setVisibility(View.VISIBLE);
        View view = LayoutInflater.from(this).inflate(R.layout.item_customer_new_contract, null, false);
        TextView tv_name = (TextView) view.findViewById(R.id.tv_name);
        TextView tv_phone = (TextView) view.findViewById(R.id.tv_phone);
        tv_name.setText(c.getName());
        tv_phone.setText(c.getTel());
        layout_newContract.addView(view);
    }

    /**
     * 新建客户请求
     * */
    public void requestCommitTask() {
        HttpAddCustomer positionData = new HttpAddCustomer();
        positionData.loc.addr = customerAddress;
        positionData.loc.loc.add(loPosition);
        positionData.loc.loc.add(laPosition);
        if (tags != null && tags.size() > 0) {
            for (NewTag tag : tags) {
                NewTag newtag = new NewTag();
                newtag.tId = tag.tId;
                newtag.itemId = tag.itemId;
                newtag.itemName = tag.itemName;
                positionData.tags.add(newtag);
            }
        }

        HttpAddCustomer locData = new HttpAddCustomer();
        locData.loc.addr = cusotmerDetalisAddress;

        HashMap<String, Object> map = new HashMap<>();
        if (controller.count() > 0) {
            map.put("attachmentCount", controller.count());
            map.put("uuid", uuid);
        }

        map.put("position", positionData.loc); //定位数据
        map.put("loc", locData.loc);           //地址详情数据
        map.put("name", customer_name);
        map.put("pname", customerContract);
        map.put("ptel", customerContractTel);
        map.put("wiretel", customerWrietele);
        map.put("tags", positionData.tags);
        map.put("salesleadId", mCluesales.id);
        LogUtil.dee("转移客户发送数据:"+MainApp.gson.toJson(map));
        CustomerService.addNewCustomer(map)
                .subscribe(new DefaultLoyoSubscriber<Customer>(hud) {
                    @Override
                    public void onNext(Customer customer) {
                        try {
                            Customer retCustomer = customer;
                            Toast("转移成功");
                            isSave = false;
                            Intent intent = new Intent();
                            intent.putExtra(Customer.class.getName(), retCustomer);
                            app.finishActivity((Activity) mContext, MainApp.ENTER_TYPE_LEFT,RESULT_OK, intent);

                        } catch (Exception e) {
                            Global.ProcException(e);
                        }
                    }
                });
    }

    @Override
    public void onClick(View v) {



        switch (v.getId()){


              /*刷新地址*/
            case R.id.img_refresh_address:

                mBundle = new Bundle();
                mBundle.putInt("page", MapModifyView.CUSTOMER_PAGE);
                app.startActivityForResult(this,MapModifyView.class, MainApp.ENTER_TYPE_RIGHT,MapModifyView.SERACH_MAP,mBundle);

                break;

            /*查重*/
            case R.id.tv_search:
                if (!edt_name.getText().toString().isEmpty()) {
                    Bundle bundle1 = new Bundle();
                    bundle1.putString("name", edt_name.getText().toString());
                    app.startActivityForResult((Activity) mContext, CustomerRepeat.class, MainApp.ENTER_TYPE_RIGHT, REQUEST_CUSTOMER_SERACH, bundle1);

                } else {
                    Toast("客户名称不能为空");
                }
                break;

            case R.id.img_title_left:
                app.finishActivity(this, MainApp.ENTER_TYPE_LEFT, RESULT_CANCELED, null);
                break;

            //提交
            case R.id.img_title_right:
                customer_name = edt_name.getText().toString().trim();
                customerAddress = et_address.getText().toString().trim();
                customerContract = edt_contract.getText().toString().trim();
                customerContractTel = edt_contract_tel.getText().toString().trim();
                customerWrietele = edt_contract_telnum.getText().toString().trim();
                cusotmerDetalisAddress = edit_address_details.getText().toString().trim();

                if (customer_name.isEmpty()) {
                    Toast("请输入客户名称!");
                    return;
                } else if (customerAddress.isEmpty()) {
                    Toast("请输入的客户地址!");
                    return;
                } else if (cusotmerDetalisAddress.isEmpty()) {
                    Toast("请输入的客户详细地址!");
                    return;
                } else if(TextUtils.isEmpty(customerContractTel) && cusPhone){
                    Toast("请输入客户手机号码!");
                    return;
                } else if(TextUtils.isEmpty(customerWrietele)    && cusMobile){
                    Toast("请输入客户座机号码!");
                    return;
                } else if(TextUtils.isEmpty(customerContract)    && cusGuys){
                    Toast("请输入联系人姓名!");
                    return;
                }

                showCommitLoading();
                if (controller.count() == 0) {
                    requestCommitTask();
                } else {
                    controller.startUpload();
                    controller.notifyCompletionIfNeeded();
                }
                break;

            case R.id.layout_customer_label:
                Bundle bundle2 = new Bundle();
                if (tags != null) {
                    bundle2.putSerializable("tags", tags);
                }
                app.startActivityForResult((Activity) mContext, CustomerLabelActivity_.class, MainApp.ENTER_TYPE_RIGHT, REQUEST_CUSTOMER_LABEL, bundle2);

                break;

            default:
                break;
        }
        }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (null == data) {
            return;
        }

        switch (requestCode) {
            case  MapModifyView.SERACH_MAP:
                positionResultItem = (PositionResultItem) data.getSerializableExtra("data");
                if(null != positionResultItem){
                    laPosition = positionResultItem.laPosition;
                    loPosition = positionResultItem.loPosition;
                    et_address.setText(positionResultItem.address);
                    edit_address_details.setText(positionResultItem.address);
                }
                break;

            case REQUEST_CUSTOMER_SERACH:

                Bundle bundle1 = data.getExtras();
                edt_name.setText(bundle1.getString("name"));

                break;

            case REQUEST_CUSTOMER_LABEL:
                Bundle bundle = data.getExtras();
                tags = (ArrayList<NewTag>) bundle.getSerializable("data");
                StringBuffer sb = null;
                StringBuffer sbItemId = null;
                StringBuffer sbTId = null;
                for (NewTag tag : tags) {
                    if (sb == null) {
                        sb = new StringBuffer();
                        sb.append(String.valueOf(tag.getItemName()));

                        sbItemId = new StringBuffer();
                        sbItemId.append(String.valueOf(tag.getItemId()));

                        sbTId = new StringBuffer();
                        sbTId.append(String.valueOf(tag.gettId()));

                    } else {
                        sb.append("、");
                        sb.append(String.valueOf(tag.getItemName()));

                        sbItemId.append(",");
                        sbItemId.append(String.valueOf(tag.getItemId()));

                        sbTId.append(",");
                        sbTId.append(String.valueOf(tag.gettId()));
                    }
                }

                if (sbItemId != null) {
                    tagItemIds = sbItemId.toString();
                    tv_labels.setText(sb.toString());
                } else
                    tv_labels.setText("");
                break;

            case REQUEST_CUSTOMER_NEW_CONTRACT:

                Contact contact = (Contact) data.getSerializableExtra("data");
                setContract(contact);

                break;


            /*相册选择 回调*/
            case PhotoPicker.REQUEST_CODE:
                /*相册选择 回调*/
                if (data != null) {
                    List<String> mSelectPath = data.getStringArrayListExtra(PhotoPicker.KEY_SELECTED_PHOTOS);
                    for (String path : mSelectPath) {
                        controller.addUploadTask("file://" + path, null, uuid);
                    }
                    controller.reloadGridView();
                }
                break;

            /*附件删除回调*/
            case PhotoPreview.REQUEST_CODE:
                if (data != null) {
                    int index = data.getExtras().getInt(PhotoPreview.KEY_DELETE_INDEX);
                    if (index >= 0) {
                        controller.removeTaskAt(index);
                        controller.reloadGridView();
                    }
                }
                break;

            default:
                break;
        }
    }


    /**
     * 上传附件信息
     */
    public void postAttaData() {
        ArrayList<UploadTask> list = controller.getTaskList();
        ArrayList<AttachmentBatch> attachment = new ArrayList<AttachmentBatch>();
        for (int i = 0; i < list.size(); i++) {
            UploadTask task = list.get(i);
            AttachmentBatch attachmentBatch = new AttachmentBatch();
            attachmentBatch.UUId = uuid;
            attachmentBatch.bizType = bizType;
            attachmentBatch.mime = Utils.getMimeType(task.getValidatePath());
            attachmentBatch.name = task.getKey();
            attachmentBatch.size = Integer.parseInt(task.size + "");
            attachment.add(attachmentBatch);
        }
        AttachmentService.setAttachementData2(attachment)
                .subscribe(new DefaultLoyoSubscriber<ArrayList<Attachment>>(hud, true) {
                    @Override
                    public void onNext(ArrayList<Attachment> news) {
                        requestCommitTask();
                    }
                });
    }

    @Override
    public void onRetryEvent(UploadController controller, UploadTask task) {
        controller.retry();
    }

    @Override
    public void onAddEvent(UploadController controller) {
        PhotoPicker.builder()
                .setPhotoCount(9-controller.count())
                .setShowCamera(true)
                .setPreviewEnabled(false)
                .start(this);
    }

    @Override
    public void onItemSelected(UploadController controller, int index) {
        ArrayList<UploadTask> taskList = controller.getTaskList();
        ArrayList<String> selectedPhotos = new ArrayList<>();

        for (int i = 0; i < taskList.size(); i++) {
            String path = taskList.get(i).getValidatePath();
            if (path.startsWith("file://"));
            {
                path = path.replace("file://", "");
            }
            selectedPhotos.add(path);
        }
        PhotoPreview.builder()
                .setPhotos(selectedPhotos)
                .setCurrentItem(index)
                .setShowDeleteButton(true)
                .start(this);
    }

    @Override
    public void onAllUploadTasksComplete(UploadController controller, ArrayList<UploadTask> taskList) {

        int count = controller.failedTaskCount();
        if (count > 0) {
            cancelCommitLoading();
            LoyoToast.info(this, count + "个附件上传失败，请重试或者删除");
            return;
        }
        if (taskList.size() > 0) {
            postAttaData();
        } else {
            requestCommitTask();
        }
    }



}
