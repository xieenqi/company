package com.loyo.oa.v2.order.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;
import com.loyo.oa.common.utils.DateTool;
import com.loyo.oa.common.utils.LoyoUIThread;
import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.customer.CustomerSearchOrPickerActivity;
import com.loyo.oa.v2.activityui.customer.model.ContactLeftExtras;
import com.loyo.oa.v2.activityui.customer.model.Customer;
import com.loyo.oa.v2.activityui.order.OrderAttachmentActivity;
import com.loyo.oa.v2.activityui.order.OrderEstimateListActivity;
import com.loyo.oa.v2.activityui.order.bean.EstimateAdd;
import com.loyo.oa.v2.activityui.order.bean.OrderDetail;
import com.loyo.oa.v2.activityui.order.event.OrderAddWorkSheetFinish;
import com.loyo.oa.v2.activityui.product.IntentionProductActivity;
import com.loyo.oa.v2.activityui.sale.bean.SaleIntentionalProduct;
import com.loyo.oa.v2.activityui.worksheet.OrderWorksheetListActivity;
import com.loyo.oa.v2.activityui.worksheet.bean.OrderWorksheetListModel;
import com.loyo.oa.v2.common.ExtraAndResult;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.customermanagement.api.CustomerService;
import com.loyo.oa.v2.customview.DateTimePickDialog;
import com.loyo.oa.v2.network.DefaultLoyoSubscriber;
import com.loyo.oa.v2.order.activity.ActivityFragmentsStackManager;
import com.loyo.oa.v2.order.activity.OrderAddOrEditActivity;
import com.loyo.oa.v2.order.api.OrderService;
import com.loyo.oa.v2.order.widget.OrderCustomFieldsView;
import com.loyo.oa.v2.tool.StringUtil;
import com.loyo.oa.v2.tool.Utils;

import org.greenrobot.eventbus.Subscribe;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.pedant.SweetAlert.SweetAlertDialog;

import static com.loyo.oa.v2.R.id.et_money;
import static com.loyo.oa.v2.R.id.et_name;
import static com.loyo.oa.v2.R.id.et_remake;
import static com.loyo.oa.v2.R.id.tv_customer;
import static com.loyo.oa.v2.R.id.tv_end_time;
import static com.loyo.oa.v2.R.id.tv_estimate;
import static com.loyo.oa.v2.R.id.tv_start_time;

/**
 * Created by EthanGong on 2017/2/22.
 */

/*
    系统字段写死字段及顺序
    label  | optional | name           | xml resource id

    订单标题  必填
    对应客户  必填
    购买产品  必填
    成交金额  必填

    <必填系统或者自定义字段区域>

    添加回款  默认选填    paymentRecords   R.id.container_estimate
    添加工单  默认选填    worksheets       R.id.container_worksheet
    订单编号  默认选填    orderNum         R.id.container_number
    开始时间  默认选填    startAt          R.id.container_start_time
    结束时间  默认选填    endAt            R.id.container_end_time

    <选填系统或者自定义字段区域>

    附件     默认选填    attachment       R.id.container_attachment
    备注     默认选填    remark           R.id.container_remark

    默认选填的选项可能配置为必填，此时需要展示在展开区域，选填部门放在折叠区域
 */

public class OrderFieldsFragment extends BaseStackFragment {

    public interface ActionListener {
        void onCommit(HashMap<String, Object> map);
    }

    private WeakReference<ActionListener> listenerRef;
    private ActivityFragmentsStackManager manager;

    private static final ArrayList<String> checkFieldNames = new ArrayList<String>() {{
        add("paymentRecords");
        add("worksheets");
        add("orderNum");
        add("startAt");
        add("endAt");
        add("attachment");
        add("remark");
    }};

    View view;
    String md5;
    OrderCustomFieldsView requiredFieldsView;
    OrderCustomFieldsView optionalFieldsView;
    private HashMap<String, ViewGroup> containerMap;
    private HashMap<String, String> hintMap;
    private ArrayList<ContactLeftExtras> fieldsList;
    private ArrayList<ContactLeftExtras> needCheckSystemFields;
    private ArrayList<ContactLeftExtras> requiredCustomFields;
    private ArrayList<ContactLeftExtras> optionalCustomFields;

    private String customerName, customerId;
    private ArrayList<SaleIntentionalProduct> productData;//意向产品的数据
    private ArrayList<EstimateAdd> estimateData;          //回款记录数据
    private ArrayList<OrderWorksheetListModel> reWorkSheet = new ArrayList<>();
    private int attachmentSize = 0;
    private String uuid;
    private long startAt;
    private long endAt;
    private String sessionId = StringUtil.getUUID();
    public int actionType;
    public String orderId;
    public OrderDetail orderDetail;
    public boolean capitalReturningRecordEdit = true;

    private WorksheetAddFragment addWorksheetFragment;//添加工单的页面

    @BindView(R.id.img_title_left)
    ViewGroup backButton;
    @BindView(R.id.img_title_right)
    ViewGroup rightButton;
    @BindView(R.id.tv_title_1)
    TextView titleView;

    @BindView(R.id.optional_zone)
    ViewGroup optionalZone;
    @BindView(R.id.layout_more)
    ViewGroup loadMore;
    @BindView(R.id.container_custom_field_required)
    ViewGroup requiredContainer;

    @BindView(R.id.container_estimate)
    ViewGroup paymentContainer;
    @BindView(R.id.container_worksheet)
    ViewGroup worksheetContainer;
    @BindView(R.id.container_number)
    ViewGroup numberContainer;
    @BindView(R.id.container_start_time)
    ViewGroup startTimeContainer;
    @BindView(R.id.container_end_time)
    ViewGroup endTimeContainer;

    @BindView(R.id.container_custom_field_optional)
    ViewGroup optionalContainer;
    @BindView(R.id.container_attachment)
    ViewGroup attachmentContainer;
    @BindView(R.id.container_remark)
    ViewGroup remarkContainer;

    @BindView(et_name)
    EditText nameText;              //订单标题
    @BindView(tv_customer)
    TextView customerText;      //对应客户
    @BindView(R.id.tv_product)
    TextView productText;        //购买产品
    @BindView(et_money)
    EditText dealText;             //成交金额
    @BindView(tv_estimate)
    TextView paymentText;       //添加回款
    @BindView(R.id.label_attachment)
    TextView attachmentText;//附件
    @BindView(R.id.tv_worksheet)
    TextView worksheetText;    //工单
    @BindView(R.id.et_num)
    EditText numberText;             //订单编号
    @BindView(et_remake)
    EditText remarkText;          //备注
    @BindView(tv_start_time)
    TextView startTimeText;   //开始时间
    @BindView(tv_end_time)
    TextView endTimeText;       //结束时间

    @OnClick(R.id.layout_more)
    void unfold() {
        optionalZone.setVisibility(View.VISIBLE);
        loadMore.setVisibility(View.GONE);
    }

    @OnClick(R.id.img_title_left)
    void onBack() {
        hideKeyboard();

        if (md5 != null && !md5.equals(fingerprint())) {
            sweetAlertDialogView.alertHandle(new SweetAlertDialog.OnSweetClickListener() {
                @Override
                public void onClick(SweetAlertDialog sweetAlertDialog) {
                    sweetAlertDialog.dismissWithAnimation();
                }
            }, new SweetAlertDialog.OnSweetClickListener() {
                @Override
                public void onClick(SweetAlertDialog sweetAlertDialog) {
                    getActivity().onBackPressed();
                }
            }, "提示", "退出后填写的信息将被清除，是否退出？");
        } else {
            getActivity().onBackPressed();
        }
    }

    @OnClick(R.id.img_title_right)
    void onCommit() {
        hideKeyboard();
        HashMap<String, Object> map = orderDataMap();
        if (map == null) {
            return;
        }
        if (listenerRef != null && listenerRef.get() != null) {
            listenerRef.get().onCommit(map);
        }
    }

    @OnClick(R.id.container_customer)
    void onCustomer() {
        hideKeyboard();
        Bundle mBundle = new Bundle();
        mBundle.putInt(CustomerSearchOrPickerActivity.EXTRA_TYPE, 5);
        mBundle.putBoolean(CustomerSearchOrPickerActivity.EXTRA_LOAD_DEFAULT, true);
        Intent intent = new Intent(getActivity(), CustomerSearchOrPickerActivity.class);
        intent.putExtras(mBundle);
        startActivityForResult(intent, ExtraAndResult.REQUEST_CODE_CUSTOMER);

    }

    private boolean showEmptyProduct;

    @OnClick(R.id.container_product)
    void onProduct() {
        hideKeyboard();
        if (actionType == OrderAddOrEditActivity.ORDER_EDIT) {
            Bundle mBundle = new Bundle();
            mBundle.putSerializable(ExtraAndResult.EXTRA_DATA, productData);
            mBundle.putBoolean(IntentionProductActivity.KEY_CAN_EDIT, true);
            mBundle.putBoolean(IntentionProductActivity.KEY_CAN_DELETE, true);
            Intent intent = new Intent(getActivity(), IntentionProductActivity.class);
            intent.putExtras(mBundle);
            startActivityForResult(intent, ExtraAndResult.REQUEST_CODE_PRODUCT);
        } else {
            AddProductsFragment addProductsFragment = new AddProductsFragment(this.manager, showEmptyProduct);
            addProductsFragment.setData(productData);
            addProductsFragment.callback = new AddProductsFragment.ProductPickerCallback() {
                @Override
                public void onProductPicked(ArrayList<SaleIntentionalProduct> data, String dealTotal, boolean initEmpty) {
                    showEmptyProduct = initEmpty;
                    productData = data;
                    productText.setText(getIntentionProductName());
                    if (TextUtils.isEmpty(dealText.getText().toString())) {//成交金额  返显产品的销售总价
                        dealText.setText("0".equals(dealTotal) ? "" : dealTotal);
                    }
                }

                @Override
                public void onBack(boolean initEmpty) {
                    showEmptyProduct = initEmpty;
                }
            };
            this.manager.push(addProductsFragment, "add_products");
        }
    }

    private boolean showEmptyPayment;

    @OnClick(R.id.container_estimate)
    void onPaymentRecord() {
        hideKeyboard();
        if (actionType == OrderAddOrEditActivity.ORDER_EDIT) {
            Bundle mBundle = new Bundle();
            if (!TextUtils.isEmpty(dealText.getText().toString())) {
                mBundle.putString("price", dealText.getText().toString());
            }
            if (null != estimateData) {
                mBundle.putSerializable("data", estimateData);
            }
            mBundle.putString("orderId", orderDetail != null && orderDetail.id != null ? orderDetail.id : orderId);
            mBundle.putBoolean(ExtraAndResult.EXTRA_ADD, true);
            Intent intent = new Intent(getActivity(), OrderEstimateListActivity.class);
            intent.putExtras(mBundle);
            startActivityForResult(intent, ExtraAndResult.REQUEST_CODE_SOURCE);
        } else {

            String dealMoney = dealText.getText().toString();
            try {
                Float.parseFloat(dealMoney);
            } catch (Exception e) {
                dealMoney = "0";
            }

            AddCapitalReturnFragment addCapitalReturnFragment = new AddCapitalReturnFragment(this.manager, showEmptyPayment);
            addCapitalReturnFragment.setData(estimateData);
            addCapitalReturnFragment.dealMoney = dealMoney;
            addCapitalReturnFragment.callback = new AddCapitalReturnFragment.AddCapitalReturnCallback() {
                @Override
                public void onAddCapitalReturn(ArrayList<EstimateAdd> data, boolean initEmpty) {
                    showEmptyPayment = initEmpty;
                    estimateData = data;
                    paymentText.setText(getEstimateName());
                }

                @Override
                public void onBack(boolean initEmpty) {
                    showEmptyPayment = initEmpty;
                }
            };
            this.manager.push(addCapitalReturnFragment, "add_capital_return");
        }
    }

    private boolean showWorksheet;

    @OnClick(R.id.container_worksheet)
    void onWorksheet() {
        hideKeyboard();
        if (actionType == OrderAddOrEditActivity.ORDER_EDIT) {
            //这里通过EventBus来接受数据
            Intent mIntent = new Intent(getActivity(), OrderWorksheetListActivity.class);
            mIntent.putExtra(OrderWorksheetListActivity.KEY_LIST, reWorkSheet);
            mIntent.putExtra(OrderWorksheetListActivity.KEY_SESSION, sessionId);
            startActivity(mIntent);
        } else {
            //因为addWorksheetFragment，保存了一些状态，所以为空才创建。
            if (null == addWorksheetFragment) {
                addWorksheetFragment = WorksheetAddFragment.newInstance(reWorkSheet);
            }
            addWorksheetFragment.setWorksheetResultCallBack(new WorksheetAddFragment.onResultCallBack() {
                @Override
                public void onWorksheetSubmit(ArrayList<OrderWorksheetListModel> data) {
                    worksheetFinish(data);
                    manager.pop();
                }

                @Override
                public void onWorksheetBack() {
                    manager.pop();
                }
            });
            this.manager.push(addWorksheetFragment, "add_worksheet");
        }
    }


    @OnClick(R.id.container_start_time)
    void onStartTime() {
        hideKeyboard();
        DateTimePickDialog dateTimePickDialog = new DateTimePickDialog(getActivity(), null);
        dateTimePickDialog.dateTimePicKDialog(new DateTimePickDialog.OnDateTimeChangedListener() {
            @Override
            public void onDateTimeChanged(int year, int month, int day, int hour, int min) {
                long time = DateTool.getStamp(year, month, day, hour, min, 0);
                startAt = time;
                startTimeText.setText(DateTool.getDateTimeFriendly(startAt));
            }

            @Override
            public void onCancel() {

            }
        }, true, "取消");
    }

    @OnClick(R.id.container_end_time)
    void onEndTime() {
        hideKeyboard();
        DateTimePickDialog dateTimePickDialog = new DateTimePickDialog(getActivity(), null);
        dateTimePickDialog.dateTimePicKDialog(new DateTimePickDialog.OnDateTimeChangedListener() {
            @Override
            public void onDateTimeChanged(int year, int month, int day, int hour, int min) {
                long time = DateTool.getStamp(year, month, day, hour, min, 0);
                endAt = time;
                endTimeText.setText(DateTool.getDateTimeFriendly(endAt));
            }

            @Override
            public void onCancel() {

            }
        }, true, "取消");
    }

    @OnClick(R.id.container_attachment)
    void onAttachment() {
        hideKeyboard();
        Bundle mBundle = new Bundle();
        mBundle.putInt("bizType", 25);
        mBundle.putString("uuid", uuid);
        Intent intent = new Intent(getActivity(), OrderAttachmentActivity.class);
        intent.putExtras(mBundle);
        startActivityForResult(intent, ExtraAndResult.MSG_WHAT_HIDEDIALOG);
    }


    public OrderFieldsFragment(ActionListener listener) {
        this.listenerRef = new WeakReference<>(listener);
    }

    public OrderFieldsFragment(ActionListener listener, ActivityFragmentsStackManager manager) {
        this.manager = manager;
        this.listenerRef = new WeakReference<>(listener);
    }

    public OrderFieldsFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_order_fields, container, false);
            ButterKnife.bind(this, view);

            containerMap = new HashMap<String, ViewGroup>() {{
                put("paymentRecords", paymentContainer);
                put("worksheets", worksheetContainer);
                put("orderNum", numberContainer);
                put("startAt", startTimeContainer);
                put("endAt", endTimeContainer);
                put("attachment", attachmentContainer);
                put("remark", remarkContainer);
            }};
            hintMap = new HashMap<String, String>() {{
                put("paymentRecords", "必填");
                put("worksheets", "必填");
                put("orderNum", "必填");
                put("startAt", "必填");
                put("endAt", "必填");
                put("attachment", "必填");
                put("remark", "备注（必填）");
            }};

            if (actionType == OrderAddOrEditActivity.ORDER_EDIT) {
                titleView.setText("编辑订单");
            } else if (actionType == OrderAddOrEditActivity.ORDER_COPY) {
                titleView.setText("复制订单");
            } else {
                titleView.setText("新建订单");
            }
            backButton.setOnTouchListener(Global.GetTouch());
            rightButton.setOnTouchListener(Global.GetTouch());
            getFields();
        }
        return view;
    }

    /**
     * 获取新建订单动态字段
     */
    private void getFields() {
        showLoading2("");
        HashMap<String, Object> map = new HashMap<>();
        map.put("bizType", 104);
        CustomerService.getAddCustomerJur(map)
                .subscribe(new DefaultLoyoSubscriber<ArrayList<ContactLeftExtras>>(hud) {

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        if (getActivity() != null) {
                            getActivity().finish();
                        }
                    }

                    @Override
                    public void onNext(ArrayList<ContactLeftExtras> contactLeftExtrasArrayList) {
                        setFieldsList(contactLeftExtrasArrayList);
                        updateUIByCheckingSystemField();
                        buildCustomFieldsViews();
                        getDetailIfNeeded();
                        bindDataIfNeeded();
                    }
                });
    }

    private void getDetailIfNeeded() {
        if (TextUtils.isEmpty(orderId)) {
            return;
        }
        if (actionType == OrderAddOrEditActivity.ORDER_EDIT
                || actionType == OrderAddOrEditActivity.ORDER_COPY) {
            LoyoUIThread.runAfterDelay(new Runnable() {
                @Override
                public void run() {
                    getOrderDetail(orderId);
                }
            }, 100);
        }
    }

    private void bindDataIfNeeded() {
        md5 = fingerprint();
        if (orderDetail == null) {
            return;
        }
        /* 机会转订单，从客户详情新建订单，客户等信息可以直接展示 */
        bindImportOrderData();
    }

    /**
     * 获取订单详情
     * 编辑订单，复制订单需要重新拉取订单详情
     */
    private void getOrderDetail(String id) {
        showLoading2("");
        HashMap<String, Object> map = new HashMap<>();
        if (actionType == OrderAddOrEditActivity.ORDER_EDIT) {
            map.put("fetchList", true);
        } else if (actionType == OrderAddOrEditActivity.ORDER_COPY) {
            map.put("isCopy", true);
        }
        OrderService.getSaleDetails(id, map)
                .subscribe(new DefaultLoyoSubscriber<OrderDetail>(hud) {
                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        if (getActivity() != null) {
                            getActivity().finish();
                        }
                    }

                    @Override
                    public void onNext(OrderDetail detail) {
                        orderDetail = detail;
                        if (actionType == OrderAddOrEditActivity.ORDER_COPY && orderId != null) {
                            resetCopyDataAndBind();
                        } else if (actionType == OrderAddOrEditActivity.ORDER_EDIT) {
                            bindEditData();
                        }
                    }
                });
    }

    public void bindEditData() {

        if (orderDetail.reWorkSheet == null) {
            orderDetail.reWorkSheet = new ArrayList<>();
        }

        uuid = orderDetail.attachmentUUId;
        customerId = orderDetail.customerId;
        customerName = orderDetail.customerName;
        productData = orderDetail.proInfo;
        estimateData = orderDetail.paymentRecords;
        reWorkSheet = orderDetail.reWorkSheet;

        nameText.setText(orderDetail.title);
        customerText.setText(orderDetail.customerName);
        productText.setText(getIntentionProductName());
        paymentText.setText(getEstimateName());
        worksheetText.setText(getWorksheetDisplayValue());
        dealText.setText(orderDetail.dealMoney + "");
        numberText.setText(orderDetail.orderNum);
        remarkText.setText(orderDetail.remark);
        if (orderDetail.startAt > 0) {
            startAt = orderDetail.startAt;
            startTimeText.setText(DateTool.getDateTimeFriendly(orderDetail.startAt));
        }
        if (orderDetail.endAt > 0) {
            endAt = orderDetail.endAt;
            endTimeText.setText(DateTool.getDateTimeFriendly(orderDetail.endAt));
        }
        requiredFieldsView.bindData(orderDetail.extensionDatas);
        optionalFieldsView.bindData(orderDetail.extensionDatas);
        unfold();

        md5 = fingerprint();
    }

    private void resetCopyDataAndBind() {
        uuid = StringUtil.getUUID();
        if (orderDetail.proInfo == null) {
            orderDetail.proInfo = new ArrayList<>();
        }


        if (orderDetail.paymentRecords == null) {
            orderDetail.paymentRecords = new ArrayList<>();
        }

        for (EstimateAdd capitalReturning : orderDetail.paymentRecords) {
            capitalReturning.attachmentCount = 0;
            capitalReturning.attachmentUUId = StringUtil.getUUID();
            capitalReturning.status = 0;
            capitalReturning.id = null;
            capitalReturning.orderId = null;
        }

        if (orderDetail.reWorkSheet == null) {
            orderDetail.reWorkSheet = new ArrayList<>();
        }

        for (OrderWorksheetListModel worksheetListModel : orderDetail.reWorkSheet) {
            worksheetListModel.uuid = StringUtil.getUUID();
        }

        orderDetail.attachmentUUId = uuid;
        customerId = orderDetail.customerId;
        customerName = orderDetail.customerName;
        productData = orderDetail.proInfo;
        estimateData = orderDetail.paymentRecords;
        reWorkSheet = orderDetail.reWorkSheet;

        nameText.setText(orderDetail.title);
        customerText.setText(orderDetail.customerName);
        productText.setText(getIntentionProductName());
        paymentText.setText(getEstimateName());
        worksheetText.setText(getWorksheetDisplayValue());
        dealText.setText(orderDetail.dealMoney + "");
        numberText.setText(orderDetail.orderNum);
        remarkText.setText(orderDetail.remark);

        if (orderDetail.startAt > 0) {
            startAt = orderDetail.startAt;
            startTimeText.setText(DateTool.getDateTimeFriendly(orderDetail.startAt));
        }
        if (orderDetail.endAt > 0) {
            endAt = orderDetail.endAt;
            endTimeText.setText(DateTool.getDateTimeFriendly(orderDetail.endAt));
        }
        requiredFieldsView.bindData(orderDetail.extensionDatas);
        optionalFieldsView.bindData(orderDetail.extensionDatas);

        unfold();
        md5 = fingerprint();
    }

    private void bindImportOrderData() {

        if (actionType != OrderAddOrEditActivity.ORDER_ADD) {
            unfold();
        }
        customerId = orderDetail.customerId;
        customerName = orderDetail.customerName;
        customerText.setText(orderDetail.customerName);

        if (orderDetail.proInfo != null) {
            productData = orderDetail.proInfo;
            productText.setText(getIntentionProductName());
        }
        if (orderDetail.dealMoney != 0) {
            dealText.setText(orderDetail.dealMoney + "");
        }
    }

    private void setFieldsList(ArrayList<ContactLeftExtras> fieldsList) {
        this.fieldsList = fieldsList;
        this.needCheckSystemFields = new ArrayList<>();
        this.requiredCustomFields = new ArrayList<>();
        this.optionalCustomFields = new ArrayList<>();
        for (ContactLeftExtras field : fieldsList) {
            if (field.isSystem) {
                if (checkFieldNames.contains(field.name)) {
                    needCheckSystemFields.add(field);
                }

            } else {
                if (field.required) {
                    requiredCustomFields.add(field);
                } else {
                    optionalCustomFields.add(field);
                }
            }
        }
    }

    private void updateUIByCheckingSystemField() {
        for (ContactLeftExtras field : needCheckSystemFields) {
            if (field.required) {
                ViewGroup viewGroup = containerMap.get(field.name);
                if (viewGroup == null) {
                    continue;
                }
                ((ViewGroup) viewGroup.getParent()).removeView(viewGroup);
                requiredContainer.addView(viewGroup);
                TextView valueView = (TextView) viewGroup.findViewWithTag("value");
                if (valueView instanceof TextView) {
                    valueView.setHint(hintMap.get(field.name));
                }
            }
        }
    }

    private void buildCustomFieldsViews() {
        requiredFieldsView = new OrderCustomFieldsView(getActivity(),
                requiredCustomFields, true, 0);
        optionalFieldsView = new OrderCustomFieldsView(getActivity(),
                optionalCustomFields, true, 0);
        requiredContainer.addView(requiredFieldsView);
        optionalContainer.addView(optionalFieldsView);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != -1 && null == data) {
            return;
        }

        switch (requestCode) {

            //选择客户
            case ExtraAndResult.REQUEST_CODE_CUSTOMER:
                Customer customer = (Customer) data.getSerializableExtra("data");
                if (null != customer) {
                    customerId = customer.getId();
                    customerName = customer.name;
                }
                customerText.setText(TextUtils.isEmpty(customerName) ? "无" : customerName);
                break;

            //选择购买产品
            case ExtraAndResult.REQUEST_CODE_PRODUCT:
                productData = (ArrayList<SaleIntentionalProduct>) data.getSerializableExtra(ExtraAndResult.RESULT_DATA);
                productText.setText(getIntentionProductName());
                if (TextUtils.isEmpty(dealText.getText().toString())) {//成交金额  返显产品的销售总价
                    String priceInfo = data.getStringExtra("salePrice");
                    String price = priceInfo.substring(1, priceInfo.length());
                    dealText.setText("0".equals(price) ? "" : price);
                }
                break;

            //选择回款
            case ExtraAndResult.REQUEST_CODE_SOURCE:
                if (data == null) {
                    return;
                }
                ArrayList<EstimateAdd> deals = (ArrayList<EstimateAdd>) data.getSerializableExtra("data");
                if (deals != null) {
                    estimateData = deals;
                    paymentText.setText(getEstimateName());
                }
                break;

            //附件回调
            case ExtraAndResult.MSG_WHAT_HIDEDIALOG:
                uuid = data.getStringExtra("uuid");
                attachmentSize = data.getIntExtra("size", 0);
                attachmentText.setText("附件（" + attachmentSize + "）");
                break;

        }
    }

    /**
     * 添加工单数据回调
     */
    @Subscribe
    public void onOrderAddWorkSheetFinish(OrderAddWorkSheetFinish event) {
        if (!sessionId.equals(event.session)) {
            return;
        }
        worksheetFinish((Collection<? extends OrderWorksheetListModel>)
                event.bundle.getSerializable(OrderWorksheetListActivity.KEY_LIST));
    }

    /**
     * 更新工单的数据
     *
     * @param worksheetList
     */
    private void worksheetFinish(Collection<? extends OrderWorksheetListModel> worksheetList) {
        reWorkSheet.clear();
        reWorkSheet.addAll(worksheetList);
        StringBuffer sBuffer = new StringBuffer();
        for (OrderWorksheetListModel orderWorksheetListModel : reWorkSheet) {
            if (reWorkSheet.size() > 1) {
                sBuffer.append(orderWorksheetListModel.title + ",");
            } else {
                sBuffer.append(orderWorksheetListModel.title);
            }
        }
        worksheetText.setText(sBuffer.toString());
    }

    /**
     * 获取 意向产品的名字
     *
     * @return
     */
    private String getIntentionProductName() {
        String productName = "";
        if (null != productData) {
            for (SaleIntentionalProduct ele : productData) {
                productName += ele.name + "、";

            }
        } else {
            return "";
        }
        return productName.length() > 0 ? productName.substring(0, productName.length() - 1) : "";
    }

    /**
     * 获取 回款记录的成交金额
     */
    private String getEstimateName() {
        String estimateName = "";
        if (null != estimateData && estimateData.size() > 0) {
            estimateName = "￥";
            for (EstimateAdd est : estimateData) {
                estimateName += est.receivedMoney + "、";
            }
        }
        return estimateName.length() > 0 ? estimateName.substring(0, estimateName.length() - 1) : "";
    }

    private String getWorksheetDisplayValue() {
        StringBuffer sBuffer = new StringBuffer();
        for (OrderWorksheetListModel orderWorksheetListModel : reWorkSheet) {
            if (reWorkSheet.size() > 1) {
                sBuffer.append(orderWorksheetListModel.title + ",");
            } else {
                sBuffer.append(orderWorksheetListModel.title);
            }
        }
        return sBuffer.toString();
    }

    private boolean checkRequiredSystemFields() {

        boolean result = true;
        for (ContactLeftExtras field : needCheckSystemFields) {
            if (!field.required) {
                continue;
            }

            switch (field.name) {
                case "paymentRecords": {
                    if (estimateData == null || estimateData.size() <= 0) {
                        Toast("请创建回款!");
                        result = false;
                    }
                    break;
                }
                case "worksheets": {
                    if (reWorkSheet == null || reWorkSheet.size() <= 0) {
                        Toast("请创建工单!");
                        result = false;
                    }
                    break;
                }
                case "orderNum": {
                    if (TextUtils.isEmpty(numberText.getText())) {
                        Toast("请填写订单编号!");
                        result = false;
                    }
                    break;
                }
                case "startAt": {
                    if (startAt <= 0) {
                        Toast("请选择开始时间!");
                        result = false;
                    }
                    break;
                }
                case "endAt": {
                    if (endAt <= 0) {
                        Toast("请选择结束时间!");
                        result = false;
                    }
                    break;
                }
                case "attachment": {
                    if (attachmentSize == 0) {
                        Toast("请上传附件!");
                        result = false;
                    }
                    break;
                }
                case "remark": {
                    if (TextUtils.isEmpty(remarkText.getText())) {
                        Toast("请填写备注!");
                        result = false;
                    }
                    break;
                }
                default:
                    break;
            }
            if (!result) {
                break;
            }

        }

        return result;
    }

    /**
     * 提交订单
     */
    public HashMap<String, Object> orderDataMap() {

        if (TextUtils.isEmpty(nameText.getText().toString().trim())) {
            Toast("请填写订单标题!");
            return null;
        } else if (TextUtils.isEmpty(customerId)) {
            Toast("请选择对应客户!");
            return null;
        } else if (TextUtils.isEmpty(productText.getText().toString())) {
            Toast("请选择购买产品!");
            return null;
        } else if (TextUtils.isEmpty(dealText.getText().toString())) {
            Toast("请选择成交金额!");
            return null;
        }
        if (!checkRequiredSystemFields()) {
            return null;
        }

        try {
            float dealMoney = Float.parseFloat(dealText.getText().toString());
        } catch (Exception e) {
            Toast("请检查成交金额格式!");
            return null;
        }

        ArrayList<ContactLeftExtras> fieldData = new ArrayList<>();
        for (ContactLeftExtras extra : requiredFieldsView.getExtras()) {
            if (!extra.isSystem && extra.required && TextUtils.isEmpty(extra.val)) {
                Toast(extra.label + "为必填项！");
                return null;
            }
        }

        for (ContactLeftExtras extra : requiredFieldsView.getExtras()) {
            if (!extra.isSystem && !TextUtils.isEmpty(extra.val)) {
                fieldData.add(extra);
            }
        }
        for (ContactLeftExtras extra : optionalFieldsView.getExtras()) {
            if (!extra.isSystem && !TextUtils.isEmpty(extra.val)) {
                fieldData.add(extra);
            }
        }
        HashMap<String, Object> map = new HashMap<>();
        if (actionType == OrderAddOrEditActivity.ORDER_EDIT) {
            map.put("id", orderId);
        }
        map.put("attachmentCount", attachmentSize);
        map.put("customerId", customerId);
        map.put("customerName", customerName);
        map.put("title", nameText.getText().toString().trim());
        if (null == uuid || TextUtils.isEmpty(uuid)) {
            map.put("attachmentUUId", StringUtil.getUUID());
        } else {
            map.put("attachmentUUId", uuid);
        }
        map.put("dealMoney", Float.parseFloat(dealText.getText().toString()));
        map.put("orderNum", numberText.getText().toString());
        map.put("remark", remarkText.getText().toString());

        /* 产品 */
        map.put("proInfo", productData);

        /* 回款 */
        map.put("paymentRecords", estimateData);

        /* 自定义字段 */
        map.put("extensionDatas", fieldData);
        /* 工单 */
        map.put("reWorkSheet", reWorkSheet);
        if (startAt > 0) {
            map.put("startAt", startAt);
        }
        if (endAt > 0) {
            map.put("endAt", endAt);
        }
        return map;
    }

    private String fingerprint() {
        ArrayList<ContactLeftExtras> fieldData = new ArrayList<>();

        for (ContactLeftExtras extra : requiredFieldsView.getExtras()) {
            if (!extra.isSystem && !TextUtils.isEmpty(extra.val)) {
                fieldData.add(extra);
            }
        }
        for (ContactLeftExtras extra : optionalFieldsView.getExtras()) {
            if (!extra.isSystem && !TextUtils.isEmpty(extra.val)) {
                fieldData.add(extra);
            }
        }
        HashMap<String, Object> map = new HashMap<>();
        if (actionType == OrderAddOrEditActivity.ORDER_EDIT) {
            map.put("id", orderId);
        }
        map.put("attachmentCount", attachmentSize);
        map.put("customerId", customerId);
        map.put("customerName", customerName);
        map.put("title", nameText.getText().toString().trim());
        if (null == uuid || TextUtils.isEmpty(uuid)) {
            map.put("attachmentUUId", StringUtil.getUUID());
        } else {
            map.put("attachmentUUId", uuid);
        }
        try {
            map.put("dealMoney", Float.parseFloat(dealText.getText().toString()));
        } catch (Exception e) {
        }
        map.put("orderNum", numberText.getText().toString());
        map.put("remark", remarkText.getText().toString());

        /* 产品 */
        map.put("proInfo", productData);

        /* 回款 */
        map.put("paymentRecords", estimateData);

        /* 自定义字段 */
        map.put("extensionDatas", fieldData);
        /* 工单 */
        map.put("reWorkSheet", reWorkSheet);
        if (startAt > 0) {
            map.put("startAt", startAt);
        }
        if (endAt > 0) {
            map.put("endAt", endAt);
        }
        Gson gson = new Gson();
        String jsonStr = gson.toJson(map);
        Log.v("fingerprint", jsonStr);

        return Utils.md5(jsonStr);
    }
}
