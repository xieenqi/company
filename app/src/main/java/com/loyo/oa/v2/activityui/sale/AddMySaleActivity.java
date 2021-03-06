package com.loyo.oa.v2.activityui.sale;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.loyo.oa.common.utils.DateTool;
import com.loyo.oa.hud.progress.LoyoProgressHUD;
import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.customer.CommonTagSelectActivity;
import com.loyo.oa.v2.activityui.customer.CommonTagSelectActivity_;
import com.loyo.oa.v2.activityui.customer.CustomerSearchOrPickerActivity;
import com.loyo.oa.v2.activityui.customer.model.ContactLeftExtras;
import com.loyo.oa.v2.activityui.customer.model.Customer;
import com.loyo.oa.v2.activityui.product.IntentionProductActivity;
import com.loyo.oa.v2.activityui.sale.bean.ActionCode;
import com.loyo.oa.v2.activityui.sale.bean.CommonTag;
import com.loyo.oa.v2.activityui.sale.bean.SaleDetails;
import com.loyo.oa.v2.activityui.sale.bean.SaleIntentionalProduct;
import com.loyo.oa.v2.activityui.sale.bean.SaleStage;
import com.loyo.oa.v2.activityui.sale.contract.AddMySaleContract;
import com.loyo.oa.v2.activityui.sale.presenter.AddMySalePresenterImpl;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.common.ExtraAndResult;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.customview.ContactAddforExtraData;
import com.loyo.oa.v2.tool.BaseActivity;
import com.loyo.oa.v2.tool.LogUtil;
import com.loyo.oa.v2.tool.Utils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * 【创建销售机会】【编辑销售机会】
 * Created by xeq on 16/5/17.
 */
public class AddMySaleActivity extends BaseActivity implements AddMySaleContract.View {

    private TextView tv_title, tv_customer, tv_stage, tv_type, tv_source, tv_product, tv_estimate, tv_transport;
    private ImageView iv_submit;
    private LinearLayout ll_back, ll_customer, ll_stage, ll_estimate, ll_poduct, ll_type, ll_source, tv_custom, ll_transport;
    private EditText et_name, et_money, et_remake;
    private String customerName, customerId, stageId, chanceId, creatorId, oldStageNmae, newStageName;
    private ArrayList<SaleIntentionalProduct> intentionProductData = new ArrayList<>();//意向产品的数据
    private ArrayList<ContactLeftExtras> filedData;
    private ArrayList<ContactLeftExtras> extensionDatas = new ArrayList<>();
    private ArrayList<CommonTag> loseResons = new ArrayList<>();

    private int estimatedTime = 0;
    private boolean isEdit;
    private StringBuffer loseReasonBuff;
    private boolean isProduct = false, isType = false, isSource = false, isEstimatedAmount = false,
            isEstimatedTime = false, isMemo = false;
    private AddMySaleContract.Presenter mPresenter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_my_sale);
        mPresenter = new AddMySalePresenterImpl(this);
        init();
        getIntentData();
    }

    private void init() {
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_title.setText("创建销售机会");
        iv_submit = (ImageView) findViewById(R.id.iv_submit);
        iv_submit.setImageResource(R.drawable.right_submit1);
        et_name = (EditText) findViewById(R.id.et_name);
        et_money = (EditText) findViewById(R.id.et_money);
        et_money.addTextChangedListener(watcherMoney);
        et_money.setFilters(new InputFilter[]{Utils.decimalDigits(2)});
        et_remake = (EditText) findViewById(R.id.et_remake);
        ll_back = (LinearLayout) findViewById(R.id.ll_back);
        ll_back.setOnTouchListener(Global.GetTouch());
        ll_back.setOnClickListener(click);
        ll_customer = (LinearLayout) findViewById(R.id.ll_customer);
        ll_customer.setOnTouchListener(Global.GetTouch());
        ll_customer.setOnClickListener(click);
        ll_stage = (LinearLayout) findViewById(R.id.ll_stage);
        ll_stage.setOnTouchListener(Global.GetTouch());
        ll_stage.setOnClickListener(click);
        ll_estimate = (LinearLayout) findViewById(R.id.ll_estimate);
        ll_estimate.setOnTouchListener(Global.GetTouch());
        ll_estimate.setOnClickListener(click);
        ll_poduct = (LinearLayout) findViewById(R.id.ll_poduct);
        ll_poduct.setOnTouchListener(Global.GetTouch());
        ll_poduct.setOnClickListener(click);
        ll_type = (LinearLayout) findViewById(R.id.ll_type);
        ll_type.setOnTouchListener(Global.GetTouch());
        ll_type.setOnClickListener(click);
        ll_source = (LinearLayout) findViewById(R.id.ll_source);
        ll_source.setOnTouchListener(Global.GetTouch());
        ll_source.setOnClickListener(click);
        iv_submit.setOnClickListener(click);
        tv_customer = (TextView) findViewById(R.id.tv_customer);
        tv_stage = (TextView) findViewById(R.id.tv_stage);
        tv_type = (TextView) findViewById(R.id.tv_type);
        tv_source = (TextView) findViewById(R.id.tv_source);
        tv_product = (TextView) findViewById(R.id.tv_product);
        tv_estimate = (TextView) findViewById(R.id.tv_estimate);
        tv_custom = (LinearLayout) findViewById(R.id.tv_custom);
        ll_transport = (LinearLayout) findViewById(R.id.ll_transport);
        ll_transport.setOnTouchListener(Global.GetTouch());
        ll_transport.setOnClickListener(click);
        tv_transport = (TextView) findViewById(R.id.tv_transport);
    }

    private TextWatcher watcherMoney = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            if (!s.toString().contains(".") && s.toString().length() > 7) {
                s.delete(7, s.toString().length());
            }
        }
    };
    private View.OnClickListener click = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.ll_back:
                    onBackPressed();
                    break;
                case R.id.iv_submit:
                    inspectData();
                    break;
                case R.id.ll_customer://选择客户
//                    Bundle b = new Bundle();
//                    b.putBoolean(SelfVisibleCustomerPickerActivity.KEY_CAN_RETURN_EMPTY, false);
//                    app.startActivityForResult(AddMySaleActivity.this, SelfVisibleCustomerPickerActivity.class,
//                            MainApp.ENTER_TYPE_RIGHT, ExtraAndResult.REQUEST_CODE_CUSTOMER, b);

                    Bundle b = new Bundle();
                    b.putInt(CustomerSearchOrPickerActivity.EXTRA_TYPE,5);
                    b.putBoolean(CustomerSearchOrPickerActivity.EXTRA_HAVE_TAG,false);
                    b.putBoolean(CustomerSearchOrPickerActivity.EXTRA_LOAD_DEFAULT,true);
                    app.startActivityForResult(AddMySaleActivity.this, CustomerSearchOrPickerActivity.class,
                            MainApp.ENTER_TYPE_RIGHT, ExtraAndResult.REQUEST_CODE_CUSTOMER, b);
                    break;
                case R.id.ll_stage://选择销售阶段
                    Bundle stage = new Bundle();
                    stage.putInt(ExtraAndResult.EXTRA_TYPE, SaleStageActivity.SALE_STAGE);
                    stage.putString(ExtraAndResult.EXTRA_NAME, "销售阶段");
                    stage.putString(ExtraAndResult.EXTRA_DATA, tv_stage.getText().toString());
                    app.startActivityForResult(AddMySaleActivity.this, SaleStageActivity.class,
                            MainApp.ENTER_TYPE_RIGHT, ExtraAndResult.REQUEST_CODE_STAGE, stage);
                    break;
                case R.id.ll_estimate://选择预估成交时间
                    estimateTime();
                    break;
                case R.id.ll_poduct://选择意向产品
                    Bundle product = new Bundle();
                    product.putSerializable(ExtraAndResult.EXTRA_DATA, intentionProductData);
                    product.putBoolean(IntentionProductActivity.KEY_CAN_EDIT, true);
                    product.putBoolean(IntentionProductActivity.KEY_CAN_DELETE, true);
                    app.startActivityForResult(AddMySaleActivity.this, IntentionProductActivity.class,
                            MainApp.ENTER_TYPE_RIGHT, ExtraAndResult.REQUEST_CODE_PRODUCT, product);
                    break;
                case R.id.ll_type://选择机会类型
                    Bundle type = new Bundle();
                    type.putInt(ExtraAndResult.EXTRA_TYPE, SaleStageActivity.SALE_TYPE);
                    type.putString(ExtraAndResult.EXTRA_NAME, "机会类型");
                    type.putString(ExtraAndResult.EXTRA_DATA, tv_type.getText().toString());
                    app.startActivityForResult(AddMySaleActivity.this, SaleStageActivity.class,
                            MainApp.ENTER_TYPE_RIGHT, ExtraAndResult.REQUEST_CODE_TYPE, type);
                    break;
                case R.id.ll_source://选择机会来源
                    Bundle source = new Bundle();
                    source.putInt(ExtraAndResult.EXTRA_TYPE, SaleStageActivity.SALE_SOURCE);
                    source.putString(ExtraAndResult.EXTRA_NAME, "机会来源");
                    source.putString(ExtraAndResult.EXTRA_DATA, tv_source.getText().toString());
                    app.startActivityForResult(AddMySaleActivity.this, SaleStageActivity.class,
                            MainApp.ENTER_TYPE_RIGHT, ExtraAndResult.REQUEST_CODE_SOURCE, source);
                    break;
                case R.id.ll_transport://输单原因
                    Bundle loseBundle = new Bundle();
                    loseBundle.putSerializable("data", loseResons);
                    loseBundle.putString("title", "输单原因");
                    loseBundle.putInt("mode", CommonTagSelectActivity.SELECT_MODE_MULTIPLE);
                    loseBundle.putInt("type", CommonTagSelectActivity.SELECT_TYPE_LOSE_REASON);
                    loseBundle.putString("tagName", tv_transport.getText().toString());
                    app.startActivityForResult(AddMySaleActivity.this, CommonTagSelectActivity_.class,
                            app.ENTER_TYPE_RIGHT, CommonTagSelectActivity.REQUEST_TAGS, loseBundle);
                    break;
            }
        }
    };

    /**
     * 获取传递值【编辑机会】
     */
    private void getIntentData() {
        Intent intente = getIntent();
        //编辑销售机会
        SaleDetails mSaleDetails = (SaleDetails) intente.getSerializableExtra(ExtraAndResult.EXTRA_DATA);
        //客户里的销售机会创建
        customerName = intente.getStringExtra(ExtraAndResult.EXTRA_NAME);
        customerId = intente.getStringExtra(ExtraAndResult.EXTRA_ID);
        if (null != mSaleDetails) {
            isEdit = true;
            chanceId = mSaleDetails.id;
            creatorId = mSaleDetails.creatorId;
            tv_title.setText("编辑销售机会");
            et_name.setText(mSaleDetails.name);
            tv_customer.setText(mSaleDetails.cusName);
            customerName = mSaleDetails.cusName;
            customerId = mSaleDetails.customerId;
            tv_stage.setText(mSaleDetails.stageName);
            oldStageNmae = mSaleDetails.stageName;
            stageId = mSaleDetails.stageId;
            et_money.setText(Utils.setValueDouble(mSaleDetails.estimatedAmount) + "");
//            tv_estimate.setText(mSaleDetails.estimatedTime != 0 ? app.df4.format(new Date(Long.valueOf(mSaleDetails.estimatedTime + "") * 1000)) : "");
            tv_estimate.setText(mSaleDetails.estimatedTime != 0 ? DateTool.getDateFriendly(mSaleDetails.estimatedTime) : "");
            estimatedTime = mSaleDetails.estimatedTime;
            intentionProductData = mSaleDetails.proInfos;
            tv_product.setText(getIntentionProductName());
            tv_type.setText(mSaleDetails.chanceType);
            tv_source.setText(mSaleDetails.chanceSource);
            tv_custom.addView(new ContactAddforExtraData(mContext, null, mSaleDetails.extensionDatas, true, 0));
            filedData = mSaleDetails.extensionDatas;
            et_remake.setText(mSaleDetails.memo);
            ll_transport.setVisibility((null == mSaleDetails.getLoseReason() || mSaleDetails.getLoseReason().size() <= 0)
                    ? View.GONE : View.VISIBLE);

            if (null != mSaleDetails.loseReason) {
                loseResons = mSaleDetails.loseReason;
                loseReasonBuff = new StringBuffer();
                for (CommonTag commonTag : mSaleDetails.loseReason) {
                    loseReasonBuff.append(commonTag.getName() + "、");
                }
                tv_transport.setText(" " + loseReasonBuff.toString());
            }


        } else {
            mPresenter.getDynamic();
            mPresenter.getStage();
            if (!TextUtils.isEmpty(customerName)) {
                tv_customer.setText(customerName);
                ll_customer.setEnabled(false);
                et_name.setText(customerName);
            }

        }
    }

    public void estimateTime() {
        Calendar cal = Calendar.getInstance();
        Locale.setDefault(Locale.CHINA);//设置语言

        final DatePickerDialog mDialog = new DatePickerDialog(this,AlertDialog.THEME_HOLO_LIGHT, null,
                cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
        //手动设置按钮
        mDialog.setButton(DialogInterface.BUTTON_POSITIVE, "完成", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                DatePicker datePicker = mDialog.getDatePicker();
                int year = datePicker.getYear();
                int month = datePicker.getMonth();
                int day = datePicker.getDayOfMonth();
//                tv_estimate.setText(year + "-" + String.format("%02d", (month + 1)) + "-" + String.format("%02d", day));
//                estimatedTime = Integer.parseInt(DateTool.getDataOne(tv_estimate.getText().toString(), "yyyy.MM.dd"));
                long time=com.loyo.oa.common.utils.DateTool.getStamp(year,month,day);
                estimatedTime= (int) time;
                tv_estimate.setText(com.loyo.oa.common.utils.DateTool.getDateFriendly(time));
            }
        });

        //取消按钮，如果不需要直接不设置即可
        mDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        mDialog.show();
    }

    /**
     * 检查必填项
     */
    private void inspectData() {
        if (TextUtils.isEmpty(et_name.getText().toString())) {
            Toast("请填写机会名称");
            return;
        } else if (TextUtils.isEmpty(customerId)) {
            Toast("请选择客户");
            return;
        } else if (TextUtils.isEmpty(stageId)) {
            Toast("请选择销售阶段");
            return;
        } else if (TextUtils.isEmpty(et_money.getText().toString()) && isEstimatedAmount) {
            Toast("请输预估入销售金额");
            return;
        } else if (ll_transport.getVisibility() == View.VISIBLE && loseResons.size() == 0) {
            Toast("请选择输单原因");
            return;
        } else if (0 == estimatedTime && isEstimatedTime) {
            Toast("请选择预估成交时间");
            return;
        } else if (isProduct && null != intentionProductData && !(intentionProductData.size() > 0)) {
            Toast("请添加意向产品");
            return;
        } else if (TextUtils.isEmpty(tv_type.getText().toString()) && isType) {
            Toast("请选择机会类型");
            return;
        } else if (TextUtils.isEmpty(tv_source.getText().toString()) && isSource) {
            Toast("请选择机会来源");
            return;
        } else if (TextUtils.isEmpty(et_remake.getText().toString()) && isMemo) {
            Toast("请输入备注内容");
            return;
        } else if (null != filedData) {
            for (ContactLeftExtras ele : filedData) {
                extensionDatas.add(ele);
                if (ele.required && TextUtils.isEmpty(ele.val)) {
                    Toast("必填项没有完成");
                    return;
                }
            }
        }
        if ("赢单".equals(tv_stage.getText().toString())) {
            if (null == intentionProductData) {
                Toast("赢单必须添加意向产品");
                return;
            }
            if (!(intentionProductData.size() > 0)) {
                sweetAlertDialogView.alertMessage("提示", "赢单提交时请添加意向产品!");
                return;
            } else {

                sweetAlertDialogView.alertHandle(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        dismissSweetAlert();
                    }
                }, new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        dismissSweetAlert();
                        addSaleOpportunitty();
                    }
                }, "提示", "请确认赢单产品的金额和数量是否正确！\n" +
                        "对应客户：" + tv_customer.getText().toString() + "\n销售总金额：¥" + et_money.getText().toString());

                return;
            }
        }
        addSaleOpportunitty();
    }

    /**
     * 创建销售机会 到服务器
     */
    private void addSaleOpportunitty() {
        showCommitLoading();
        HashMap<String, Object> map = new HashMap<>();
        if (isEdit) {
            map.put("id", chanceId);
            map.put("creatorId", creatorId);
            map.put("content", TextUtils.isEmpty(newStageName) ? "" : "销售阶段由\"" + oldStageNmae + "\"变更为\"" + newStageName + "\"");
        } else {
            map.put("content", "创建销售机会");
        }
        map.put("customerName", customerName);
        map.put("customerId", customerId);
        map.put("name", et_name.getText().toString());
        map.put("stageId", stageId);
        if (!TextUtils.isEmpty(et_money.getText().toString())) {
            map.put("estimatedAmount", Float.valueOf(et_money.getText().toString()));
        }
        map.put("estimatedTime", estimatedTime);
        map.put("proInfos", intentionProductData);
        map.put("chanceSource", tv_source.getText().toString());
        map.put("chanceType", tv_type.getText().toString());
        map.put("memo", et_remake.getText().toString());
        map.put("extensionDatas", extensionDatas);
        if (ll_transport.getVisibility() == View.GONE) {
            loseResons.clear();
        }
        map.put("loseReason", loseResons);
        LogUtil.d("改变销售机会传递--》", app.gson.toJson(map));
        if (!isEdit) {
            mPresenter.creatSale(map);
        } else {
            mPresenter.editSale(map, chanceId);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (RESULT_OK == resultCode) {
            switch (requestCode) {
                case ExtraAndResult.REQUEST_CODE_CUSTOMER://选择客户
                    Customer customer = (Customer) data.getSerializableExtra("data");
                    if (null != customer) {
                        customerId = customer.getId();
                        customerName = customer.name;
                    }
                    tv_customer.setText(TextUtils.isEmpty(customerName) ? "无" : customerName);
                    if (TextUtils.isEmpty(et_name.getText().toString()))
                        et_name.setText(TextUtils.isEmpty(customerName) ? "" : customerName);
                    break;
                case ExtraAndResult.REQUEST_CODE_STAGE://选择销售阶段
                    SaleStage stage = (SaleStage) data.getSerializableExtra(ExtraAndResult.EXTRA_DATA);
                    if (null != stage) {
                        stageId = stage.id;
                        tv_stage.setText(stage.name);
                        newStageName = stage.name;
                        if ("输单".equals(stage.name)) {
                            ll_transport.setVisibility(View.VISIBLE);
                        } else {
                            ll_transport.setVisibility(View.GONE);
                        }
                    }
                    break;
                case ExtraAndResult.REQUEST_CODE_TYPE://选择机会类型
                    String saletype = data.getStringExtra(ExtraAndResult.EXTRA_DATA);
                    tv_type.setText(saletype);
                    break;
                case ExtraAndResult.REQUEST_CODE_SOURCE://选择机会来源
                    String salesource = data.getStringExtra(ExtraAndResult.EXTRA_DATA);
                    tv_source.setText(salesource);
                    break;
                case ExtraAndResult.REQUEST_CODE_PRODUCT://选择意向产品
                    ArrayList<SaleIntentionalProduct> resultData = (ArrayList<SaleIntentionalProduct>)
                            data.getSerializableExtra(ExtraAndResult.RESULT_DATA);
                    if (null != resultData) {
                        intentionProductData = resultData;
                        tv_product.setText(getIntentionProductName());
                    }
                    break;
                case CommonTagSelectActivity.REQUEST_TAGS://选择输单原因
                    loseResons = (ArrayList<CommonTag>) data.getSerializableExtra("data");
                    tv_transport.setText(getLoseReason());
                    break;
            }
        }
    }

    /**
     * 获取 意向产品的名字
     *
     * @return
     */
    private String getIntentionProductName() {
        String productName = "";
        if (null != intentionProductData) {
            for (SaleIntentionalProduct ele : intentionProductData) {
                productName += ele.name + "、";
            }
        } else {
            return "";
        }
        return productName.length() > 0 ? productName.substring(0, productName.length() - 1) : "";
    }

    /**
     * 获取输单原因 字符串
     *
     * @return
     */
    private String getLoseReason() {
        if (null == loseResons || loseResons.isEmpty()) {
            return "";
        }
        StringBuilder reasons = new StringBuilder();
        int index = 0;
        for (CommonTag reson : loseResons) {
            reasons.append(reson.getName());
            if (index < loseResons.size() - 1) {
                reasons.append(",");
            }
            index++;
        }
        return reasons.toString();
    }

    public LoyoProgressHUD getHUD() {
        return hud;
    }

    @Override
    public LoyoProgressHUD showStatusProgress() {
        showCommitLoading();
        return hud;
    }

    @Override
    public LoyoProgressHUD showProgress(String message) {
        showLoading2(message);
        return hud;
    }

    @Override
    public void hideProgress() {
        cancelLoading2();
    }

    @Override
    public void showMsg(String message) {
        Toast(message);
    }

    @Override
    public void setDynamicUI(ArrayList<ContactLeftExtras> dynamicData) {
        tv_custom.addView(new ContactAddforExtraData(mContext, null, filedData, true, 0));
    }

    @Override
    public void setHintUI(boolean... bool) {
        isProduct = bool[0];
        isType = bool[1];
        isSource = bool[2];
        isEstimatedAmount = bool[3];
        isEstimatedTime = bool[4];
        isMemo = bool[5];
        tv_product.setHint(isProduct ? "必填,请选择" : "请选择");
        tv_type.setHint(isType ? "必填,请选择" : "请选择");
        tv_source.setHint(isSource ? "必填,请选择" : "请选择");
        et_money.setHint(isEstimatedAmount ? "必填,请选择" : "请选择");
        tv_estimate.setHint(isEstimatedTime ? "必填,请选择" : "请选择");
        et_remake.setHint(isMemo ? "必填,请选择" : "请选择");

    }

    @Override
    public void setStageUI(String stage, String stageId) {
        this.stageId = stageId;
        tv_stage.setText(stage);
    }

    @Override
    public void creatSaleAction() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                app.finishActivity(AddMySaleActivity.this, MainApp.ENTER_TYPE_LEFT, ExtraAndResult.REQUEST_CODE_STAGE, new Intent());
            }
        },2000);
    }

    @Override
    public void editSaleAction() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent mIntent = new Intent();
                mIntent.putExtra(ExtraAndResult.RESULT_ID, ActionCode.SALE_DETAILS_EDIT);
                app.finishActivity(AddMySaleActivity.this, MainApp.ENTER_TYPE_LEFT, RESULT_OK, mIntent);
            }
        },2000);
    }
}