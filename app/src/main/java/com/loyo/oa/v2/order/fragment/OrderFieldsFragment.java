package com.loyo.oa.v2.order.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.loyo.oa.common.utils.DateTool;
import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.customer.CustomerSearchOrPickerActivity;
import com.loyo.oa.v2.activityui.customer.model.ContactLeftExtras;
import com.loyo.oa.v2.activityui.order.OrderAttachmentActivity;
import com.loyo.oa.v2.activityui.order.OrderEstimateListActivity;
import com.loyo.oa.v2.activityui.order.bean.EstimateAdd;
import com.loyo.oa.v2.activityui.product.IntentionProductActivity;
import com.loyo.oa.v2.activityui.sale.bean.SaleIntentionalProduct;
import com.loyo.oa.v2.activityui.worksheet.OrderWorksheetListActivity;
import com.loyo.oa.v2.activityui.worksheet.bean.OrderWorksheetListModel;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.common.ExtraAndResult;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.customermanagement.api.CustomerService;
import com.loyo.oa.v2.customview.DateTimePickDialog;
import com.loyo.oa.v2.network.DefaultLoyoSubscriber;
import com.loyo.oa.v2.order.widget.OrderCustomFieldsView;
import com.loyo.oa.v2.tool.BaseFragment;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.loyo.oa.v2.R.id.et_money;
import static com.loyo.oa.v2.R.id.et_name;
import static com.loyo.oa.v2.R.id.tv_end_time;
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

public class OrderFieldsFragment extends BaseFragment {

    public interface ActionListener {
        void onCommit(Object object);
    }

    private WeakReference<ActionListener> listenerRef;

    private static final ArrayList<String> checkFieldNames = new ArrayList<String>(){{
        add("paymentRecords");
        add("worksheets");
        add("orderNum");
        add("startAt");
        add("endAt");
        add("attachment");
        add("remark");
    }};

    View view;
    OrderCustomFieldsView requiredFieldsView;
    OrderCustomFieldsView optionalFieldsView;
    private HashMap<String, ViewGroup> containerMap;
    private HashMap<String, String> hintMap;
    private ArrayList<ContactLeftExtras> fieldsList;
    private ArrayList<ContactLeftExtras> needCheckSystemFields;
    private ArrayList<ContactLeftExtras> requiredCustomFields;
    private ArrayList<ContactLeftExtras> optionalCustomFields;

    public static String orderTitle;
    private String customerName, customerId;
    private ArrayList<SaleIntentionalProduct> productData;//意向产品的数据
    private ArrayList<EstimateAdd> estimateData;          //回款记录数据
    private ArrayList<OrderWorksheetListModel> reWorkSheet = new ArrayList<>();
    private int attamentSize = 0;
    private String uuid;
    private long startAt;
    private long endAt;

    @BindView(R.id.img_title_left)  ViewGroup backButton;
    @BindView(R.id.img_title_right) ViewGroup rightButton;
    @BindView(R.id.tv_title_1)      TextView  titleView;

    @BindView(R.id.optional_zone)                   ViewGroup optionalZone;
    @BindView(R.id.layout_more)                     ViewGroup loadMore;
    @BindView(R.id.container_custom_field_required) ViewGroup requiredContainer;

    @BindView(R.id.container_estimate)   ViewGroup paymentContainer;
    @BindView(R.id.container_worksheet)  ViewGroup worksheetContainer;
    @BindView(R.id.container_number)     ViewGroup numberContainer;
    @BindView(R.id.container_start_time) ViewGroup startTimeContainer;
    @BindView(R.id.container_end_time)   ViewGroup endTimeContainer;

    @BindView(R.id.container_custom_field_optional) ViewGroup optionalContainer;
    @BindView(R.id.container_attachment) ViewGroup attachmentContainer;
    @BindView(R.id.container_remark)     ViewGroup remarkContainer;

    @BindView(et_name) EditText nameText;              //订单标题
    @BindView(R.id.tv_customer) TextView customerText;      //对应客户
    @BindView(R.id.tv_product) TextView productText;        //购买产品
    @BindView(et_money) EditText dealText;             //成交金额
    @BindView(R.id.tv_estimate) TextView paymentText;       //添加回款
    @BindView(R.id.label_attachment) TextView attachmentText;//附件
    @BindView(R.id.tv_worksheet) TextView worksheetText;    //工单
    @BindView(R.id.et_num) EditText numberText;             //订单编号
    @BindView(R.id.et_remake) EditText remakeText;          //备注
    @BindView(tv_start_time) TextView startTimeText;   //开始时间
    @BindView(tv_end_time) TextView endTimeText;       //结束时间

    @OnClick(R.id.layout_more) void unfold() {
        optionalZone.setVisibility(View.VISIBLE);
        loadMore.setVisibility(View.GONE);
    }

    @OnClick(R.id.img_title_left) void onBackPressed() {
        getActivity().onBackPressed();
    }

    @OnClick(R.id.img_title_right) void onCommit() {
        if (listenerRef != null && listenerRef.get() != null) {
            listenerRef.get().onCommit(null);
        }
    }

    @OnClick(R.id.container_customer) void onCustomer() {
        Bundle b = new Bundle();
        b.putInt(CustomerSearchOrPickerActivity.EXTRA_TYPE,5);
        b.putBoolean(CustomerSearchOrPickerActivity.EXTRA_LOAD_DEFAULT,true);
        app.startActivityForResult(getActivity(), CustomerSearchOrPickerActivity.class,
                MainApp.ENTER_TYPE_RIGHT, ExtraAndResult.REQUEST_CODE_CUSTOMER, b);

    }

    @OnClick(R.id.container_product) void onProduct() {
        Bundle mBundle = new Bundle();
        mBundle.putSerializable(ExtraAndResult.EXTRA_DATA, productData);
        mBundle.putBoolean(IntentionProductActivity.KEY_CAN_EDIT, true);
        mBundle.putBoolean(IntentionProductActivity.KEY_CAN_DELETE, true);
        app.startActivityForResult(getActivity(), IntentionProductActivity.class,
                MainApp.ENTER_TYPE_RIGHT, ExtraAndResult.REQUEST_CODE_PRODUCT, mBundle);
    }

    @OnClick(R.id.container_estimate) void onPaymentRecord() {
        Bundle mBundle = new Bundle();
        if (!TextUtils.isEmpty(dealText.getText().toString())) {
            mBundle.putString("price", dealText.getText().toString());
        }
        if (null != estimateData) {
            mBundle.putSerializable("data", estimateData);
        }
//        mBundle.putString("orderId", mOrderDetail!=null&&mOrderDetail.id!=null?mOrderDetail.id:orderId);
//        mBundle.putBoolean(ExtraAndResult.EXTRA_ADD, capitalReturningRecordEdit);
        app.startActivityForResult(getActivity(), OrderEstimateListActivity.class,
                MainApp.ENTER_TYPE_RIGHT, ExtraAndResult.REQUEST_CODE_SOURCE, mBundle);
    }

    @OnClick(R.id.container_worksheet) void onWorksheet() {
        Intent mIntent = new Intent(getActivity(), OrderWorksheetListActivity.class);
        if (!TextUtils.isEmpty(nameText.getText().toString())) {
            orderTitle = nameText.getText().toString();
        } else {
            orderTitle = "";
        }
        mIntent.putExtra(ExtraAndResult.EXTRA_NAME, reWorkSheet);
        startActivity(mIntent);
    }

    @OnClick(R.id.container_start_time) void onStartTime() {
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

    @OnClick(R.id.container_end_time) void onEndTime() {
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

    @OnClick(R.id.container_attachment) void onAttachment() {
        Bundle mBundle = new Bundle();
        mBundle.putInt("bizType", 25);
        mBundle.putString("uuid", uuid);
        app.startActivityForResult(getActivity(), OrderAttachmentActivity.class,
                MainApp.ENTER_TYPE_RIGHT, ExtraAndResult.MSG_WHAT_HIDEDIALOG, mBundle);
    }

    public OrderFieldsFragment(ActionListener listener) {
        this.listenerRef = new WeakReference<>(listener);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_order_fields, container, false);
            ButterKnife.bind(this, view);

            containerMap = new HashMap<String, ViewGroup>(){{
                put("paymentRecords", paymentContainer);
                put("worksheets", worksheetContainer);
                put("orderNum", numberContainer);
                put("startAt", startTimeContainer);
                put("endAt", endTimeContainer);
                put("attachment", attachmentContainer);
                put("remark", remarkContainer);
            }};
            hintMap = new HashMap<String, String>(){{
                put("paymentRecords", "必填");
                put("worksheets", "必填");
                put("orderNum", "必填");
                put("startAt", "必填");
                put("endAt", "必填");
                put("attachment", "必填");
                put("remark", "备注（必填）");
            }};

            titleView.setText("新建订单");
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
                    public void onNext(ArrayList<ContactLeftExtras> contactLeftExtrasArrayList) {
                        setFieldsList(contactLeftExtrasArrayList);
                        updateUIByCheckingSystemField();
                        buildCustomFieldsViews();
                    }
                });
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
            }
            else {
                if (field.required) {
                    requiredCustomFields.add(field);
                }
                else {
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
                ((ViewGroup)viewGroup.getParent()).removeView(viewGroup);
                requiredContainer.addView(viewGroup);
                TextView valueView = ( TextView)viewGroup.findViewWithTag("value");
                if (valueView instanceof  TextView) {
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
}
