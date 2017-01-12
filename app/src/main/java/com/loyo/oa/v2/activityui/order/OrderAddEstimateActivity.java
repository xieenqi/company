package com.loyo.oa.v2.activityui.order;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.InputFilter;
import android.text.TextUtils;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.loyo.oa.common.utils.DateTool;
import com.loyo.oa.contactpicker.ContactPickerActivity;
import com.loyo.oa.contactpicker.model.event.ContactPickedEvent;
import com.loyo.oa.contactpicker.model.result.StaffMemberCollection;
import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.order.bean.EstimateAdd;
import com.loyo.oa.v2.activityui.order.common.OrderCommon;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.beans.OrganizationalMember;
import com.loyo.oa.v2.common.ExtraAndResult;
import com.loyo.oa.v2.common.FinalVariables;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.common.compat.Compat;
import com.loyo.oa.v2.customview.DateTimePickDialog;
import com.loyo.oa.v2.customview.PaymentPopView;
import com.loyo.oa.v2.tool.BaseActivity;
import com.loyo.oa.v2.tool.StringUtil;
import com.loyo.oa.v2.tool.Utils;

import org.greenrobot.eventbus.Subscribe;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

/**
 * 【新增回款】
 * Created by yyy on 16/8/3.
 */
public class OrderAddEstimateActivity extends BaseActivity implements View.OnClickListener {

    public final static String RET_IS_DATA_EDITED = "com.loyo.OrderAddEstimateActivity.RET_IS_DATA_EDITED";

    private LinearLayout ll_time;        //回款日期
    private LinearLayout ll_priecer;     //收款人
    private LinearLayout ll_kind;        //付款方式
    private LinearLayout ll_attachment;  //附件
    private LinearLayout ll_back;

    private TextView tv_time;        //回款日期
    private TextView tv_priceer;     //收款人
    private TextView tv_kind;        //付款方式
    private TextView tv_attachment;  //附件

    private EditText et_estprice; //回款金额
    private EditText et_kaiprice; //开票金额
    private EditText et_remake;   //备注

    private TextView tv_title;
    private ImageView iv_submit;

    private long estimatedTime = 0;
    private int paymentState;

    private String uuid;
    private String id;
    private int attamentSize = 0;
    private Intent mIntent;
    private Bundle mBundle;
    private OrganizationalMember newUser;
    private EstimateAdd mEstimateAdd;
    private HashMap<String, Object> map;
    private boolean isEdit = false;

    private Handler mHandler = new Handler() {

        public void handleMessage(Message msg) {

            switch (msg.what) {

                //附件数量刷新
                case ExtraAndResult.MSG_WHAT_VISIBLE:
                    if (attamentSize != 0) {
                        tv_attachment.setText("附件（" + attamentSize + "）");
                    }
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_estimateadd);
        initUI();
    }

    public void initUI() {

        ll_time = (LinearLayout) findViewById(R.id.ll_time);
        ll_priecer = (LinearLayout) findViewById(R.id.ll_priecer);
        ll_kind = (LinearLayout) findViewById(R.id.ll_kind);
        ll_attachment = (LinearLayout) findViewById(R.id.ll_attachment);
        ll_back = (LinearLayout) findViewById(R.id.ll_back);

        tv_time = (TextView) findViewById(R.id.tv_time);
        tv_priceer = (TextView) findViewById(R.id.tv_priceer);
        tv_kind = (TextView) findViewById(R.id.tv_kind);
        tv_attachment = (TextView) findViewById(R.id.tv_attachment);
        tv_title = (TextView) findViewById(R.id.tv_title);
        iv_submit = (ImageView) findViewById(R.id.iv_submit);
        iv_submit.setImageResource(R.drawable.right_submit1);

        et_estprice = (EditText) findViewById(R.id.et_estprice);
        et_estprice.addTextChangedListener(OrderCommon.getTextWatcher());
        et_estprice.setFilters(new InputFilter[]{Utils.decimalDigits(2)});
        et_kaiprice = (EditText) findViewById(R.id.et_kaiprice);
        et_kaiprice.addTextChangedListener(OrderCommon.getTextWatcher());
        et_kaiprice.setFilters(new InputFilter[]{Utils.decimalDigits(2)});
        et_remake = (EditText) findViewById(R.id.et_remake);
        ll_back.setOnClickListener(this);
        iv_submit.setOnClickListener(this);
        ll_time.setOnClickListener(this);
        ll_priecer.setOnClickListener(this);
        ll_kind.setOnClickListener(this);
        ll_attachment.setOnClickListener(this);
        ll_back.setOnTouchListener(Global.GetTouch());
        iv_submit.setOnTouchListener(Global.GetTouch());

        mIntent = getIntent();
        if (mIntent != null) {
            mEstimateAdd = (EstimateAdd) mIntent.getSerializableExtra(ExtraAndResult.RESULT_DATA);
            if (mEstimateAdd != null) {
                attamentSize = mEstimateAdd.attachmentCount;
            }
        }

        if (attamentSize != 0) {
            tv_attachment.setText("附件(" + attamentSize + ")");
        }

        estimatedTime = DateTool.getStamp(false);
        tv_time.setText(DateTool.getDateFriendly(DateTool.getStamp(false)));

        if (mEstimateAdd != null) {
            tv_title.setText("编辑回款记录");
            isEdit = true;
            editEstimate();
        }
        else {
            tv_title.setText("新增回款记录");
        }

    }


    /**
     * 编辑 记录
     */
    private void editEstimate() {
        if (null != mEstimateAdd) {
            if (mEstimateAdd.payeeUser != null) {
                newUser = new OrganizationalMember();
                newUser.setId(mEstimateAdd.payeeUser.id);
                newUser.setName(mEstimateAdd.payeeUser.name);
                newUser.setAvatar(mEstimateAdd.payeeUser.avatar);
                tv_priceer.setText(mEstimateAdd.payeeUser.name);
            }
            id = mEstimateAdd.id;
            uuid = mEstimateAdd.attachmentUUId;
            if (mEstimateAdd.billingMoney != 0) {
                et_kaiprice.setText(mEstimateAdd.billingMoney + "");
            }

            estimatedTime = mEstimateAdd.receivedAt;
            paymentState = mEstimateAdd.payeeMethod;
            tv_time.setText(DateTool.getDateFriendly(mEstimateAdd.receivedAt));
            et_estprice.setText(mEstimateAdd.receivedMoney + "");
            et_remake.setText(mEstimateAdd.remark);

            if (mEstimateAdd.attachmentCount != 0) {
                attamentSize = mEstimateAdd.attachmentCount;
                tv_attachment.setText("附件(" + attamentSize + ")");
            }
            setPayeeMethod(mEstimateAdd.payeeMethod);
        }
    }

    private void setPayeeMethod(int payeeMethod) {
        switch (payeeMethod) {

            case 1:
                tv_kind.setText("现金");
                break;

            case 2:
                tv_kind.setText("支票");
                break;

            case 3:
                tv_kind.setText("银行转账");
                break;

            case 4:
                tv_kind.setText("其它");
                break;
        }
    }

    /**
     * 提交数据
     */
    public void finishWithDataReturn() {

        if (estimatedTime == 0) {
            Toast("请选择回款时间!");
            return;
        } else if (TextUtils.isEmpty(et_estprice.getText().toString())) {
            Toast("请选择回款金额!");
            return;
        } else if (TextUtils.isEmpty(tv_priceer.getText().toString())) {
            Toast("请选择收款人!");
            return;
        }

        mIntent = new Intent();
        mEstimateAdd = new EstimateAdd();
        mEstimateAdd.receivedAt = (int) estimatedTime;
        mEstimateAdd.receivedMoney = Integer.parseInt(et_estprice.getText().toString().trim());
        //如果开票金额没写，则默认为0
        if (TextUtils.isEmpty(et_kaiprice.getText().toString())) {
            mEstimateAdd.billingMoney = 0;
        } else {
            mEstimateAdd.billingMoney = Integer.parseInt(et_kaiprice.getText().toString().trim());
        }
        mEstimateAdd.id = id;
        mEstimateAdd.attachmentCount = attamentSize;
        if (null == uuid || TextUtils.isEmpty(uuid)) {
            mEstimateAdd.attachmentUUId = StringUtil.getUUID();
        } else {
            mEstimateAdd.attachmentUUId = uuid;
        }
        mEstimateAdd.payeeUser.id = newUser.getId();
        mEstimateAdd.payeeUser.name = newUser.getName();
        mEstimateAdd.payeeUser.avatar = newUser.getAvatar();
        mEstimateAdd.payeeMethod = paymentState;
        mEstimateAdd.remark = et_remake.getText().toString();

        Intent intent = new Intent();
        intent.putExtra("data", mEstimateAdd);
        intent.putExtra(RET_IS_DATA_EDITED, isEdit);
        app.finishActivity(this, MainApp.ENTER_TYPE_LEFT, RESULT_OK, intent);
    }


    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            //回退
            case R.id.ll_back:
                onBackPressed();
                break;

            //提交
            case R.id.iv_submit:
                finishWithDataReturn();

                break;

            //回款日期
            case R.id.ll_time:
                setDeadLine();
                break;

            //收款人
            case R.id.ll_priecer:
                Bundle bundle = new Bundle();
                bundle.putBoolean(ContactPickerActivity.SINGLE_SELECTION_KEY, true);
                bundle.putSerializable(ContactPickerActivity.REQUEST_KEY, FinalVariables.PICK_RESPONSIBLE_USER_REQUEST);
                Intent intent = new Intent();
                intent.setClass(this, ContactPickerActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
                break;

            //附件
            case R.id.ll_attachment:
                mBundle = new Bundle();
                mBundle.putInt("bizType", 26);
                mBundle.putString("uuid", uuid);
                app.startActivityForResult(this, OrderAttachmentActivity.class, MainApp.ENTER_TYPE_RIGHT, ExtraAndResult.MSG_WHAT_HIDEDIALOG, mBundle);
                break;

            //付款方式
            case R.id.ll_kind:
                paymentSet();
                break;

        }
    }


    public void paymentSet() {
        String[] data = {"现金", "支票", "银行转账", "其它"};
        final PaymentPopView popView = new PaymentPopView(this, data, "付款方式");
        popView.show();
        popView.setCanceledOnTouchOutside(true);
        popView.setCallback(new PaymentPopView.VaiueCallback() {
            @Override
            public void setValue(String value, int index) {
                paymentState = index;
                tv_kind.setText(value);
            }
        });
    }

    /**
     * 回款日期选择框
     */
    void setDeadLine() {
        DateTimePickDialog dateTimePickDialog = new DateTimePickDialog(this, null, true);
        dateTimePickDialog.dateTimePicKDialog(new DateTimePickDialog.OnDateTimeChangedListener() {
            @Override
            public void onDateTimeChanged(final int year, final int month, final int day, final int hour, final int min) {
                long time= DateTool.getStamp(year, month, day, hour, min, 0);
                String str= DateTool.getDateFriendly(time);
                tv_time.setText(str);
                estimatedTime=time;
            }

            @Override
            public void onCancel() {

            }

        }, true, "取消");
    }

    /**
     * 时间选择
     */
    public void estimateTime() {
        Calendar cal = Calendar.getInstance();
        Locale.setDefault(Locale.CHINA);//设置语言
        final DatePickerDialog mDialog = new DatePickerDialog(this, AlertDialog.THEME_HOLO_LIGHT, null,
                cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));

        //手动设置按钮
        mDialog.setButton(DialogInterface.BUTTON_POSITIVE, "完成", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                DatePicker datePicker = mDialog.getDatePicker();
                int year = datePicker.getYear();
                int month = datePicker.getMonth();
                int day = datePicker.getDayOfMonth();
                estimatedTime= DateTool.getStamp(year,month,day);
                tv_time.setText(DateTool.getDateFriendly(estimatedTime));

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != RESULT_OK) {
            return;
        }

        switch (requestCode) {

            //用户单选, 负责人
            case FinalVariables.REQUEST_ONLY:
                OrganizationalMember u = (OrganizationalMember) data.getSerializableExtra("data");
                newUser = u;
                tv_priceer.setText(newUser.getName());
                break;

            //附件回调
            case ExtraAndResult.MSG_WHAT_HIDEDIALOG:
                uuid = data.getStringExtra("uuid");
                attamentSize = data.getIntExtra("size", 0);
                mHandler.sendEmptyMessage(ExtraAndResult.MSG_WHAT_VISIBLE);
                break;

        }
    }

    /**
     * 选人回调
     */
    @Subscribe
    public void onContactPicked(ContactPickedEvent event) {

        if (FinalVariables.PICK_RESPONSIBLE_USER_REQUEST.equals(event.request)) {
            StaffMemberCollection collection = event.data;
            OrganizationalMember user = Compat.convertStaffCollectionToNewUser(collection);
            if (user == null) {
                return;
            }
            newUser = user;
            tv_priceer.setText(newUser.getName());
        }
    }
}
