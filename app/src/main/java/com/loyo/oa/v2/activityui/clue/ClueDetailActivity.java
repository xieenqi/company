package com.loyo.oa.v2.activityui.clue;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.library.module.widget.loading.LoadingLayout;
import com.loyo.oa.common.utils.DateTool;
import com.loyo.oa.common.utils.PermissionTool;
import com.loyo.oa.contactpicker.ContactPickerActivity;
import com.loyo.oa.contactpicker.model.event.ContactPickedEvent;
import com.loyo.oa.contactpicker.model.result.StaffMemberCollection;
import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.clue.api.ClueService;
import com.loyo.oa.v2.activityui.clue.model.ClueDetailWrapper;
import com.loyo.oa.v2.activityui.clue.model.ClueSales;
import com.loyo.oa.v2.activityui.clue.common.ClueCommon;
import com.loyo.oa.v2.activityui.commonview.CommonHtmlUtils;
import com.loyo.oa.v2.activityui.customer.CallPhoneBackActivity;
import com.loyo.oa.v2.activityui.customer.model.CallBackCallid;
import com.loyo.oa.v2.activityui.customer.model.CustomerRegional;
import com.loyo.oa.v2.activityui.setting.EditUserMobileActivity;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.beans.BaseBeanT;
import com.loyo.oa.v2.beans.OrganizationalMember;
import com.loyo.oa.v2.common.ExtraAndResult;
import com.loyo.oa.v2.common.FinalVariables;
import com.loyo.oa.v2.common.RegularCheck;
import com.loyo.oa.v2.common.compat.Compat;
import com.loyo.oa.v2.customview.ActionSheetDialog;
import com.loyo.oa.v2.customview.CallPhonePopView;
import com.loyo.oa.v2.customview.PaymentPopView;
import com.loyo.oa.v2.customview.SelectCityView;
import com.loyo.oa.v2.customview.SweetAlertDialogView;
import com.loyo.oa.v2.network.DefaultLoyoSubscriber;
import com.loyo.oa.v2.network.LoyoErrorChecker;
import com.loyo.oa.v2.permission.BusinessOperation;
import com.loyo.oa.v2.permission.PermissionManager;
import com.loyo.oa.v2.tool.BaseLoadingActivity;
import com.loyo.oa.v2.tool.LogUtil;
import com.loyo.oa.v2.tool.SharedUtil;
import com.loyo.oa.v2.tool.Utils;
import com.loyo.oa.voip.VoIPCallActivity;

import org.greenrobot.eventbus.Subscribe;

import java.util.HashMap;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class ClueDetailActivity extends BaseLoadingActivity implements View.OnClickListener {

    /*  Navigation Bar */
    ViewGroup img_title_left /* 返回按钮 */,
            img_title_right  /* 右上菜单 */;

    /*  分区1 */
    ViewGroup ll_status      /* 线索状态 */;
    TextView section1_username    /* 姓名 */,
            section1_company_name /* 公司名称 */,
            tv_status  /* 线索状态 */;

    /*  分区2 */
    ViewGroup section2_visit      /* 跟进动态 */,
            ll_track /* 最近跟进详情 */;

    TextView
            tv_track_content   /* 最近跟进内容 */,
            tv_track_time      /* 最近跟进元信息 */;

    /*  分区3 */
    ViewGroup ll_sms                /* 手机发短信 */,
            ll_call                 /* 手机拨电话 */,
            ll_wiretel_call         /* 座机拨电话 */,
            layout_clue_region      /* 地区弹出列表 */,
            layout_clue_source      /* 线索来源弹出列表 */;

    TextView contact_mobile  /* 手机 */,
            contact_wiretel  /* 座机 */,
            clue_region      /* 地区 */,
            clue_source      /* 线索来源 */,
            clue_note        /* 备注 */;

    /*  分区4 */
    TextView responsible_name/* 负责人 */,
            creator_name     /* 创建人 */,
            create_time      /* 创建时间 */,
            update_time      /* 更新时间 */,
            tv_address, tv_visit_number;

    /* Data */
    String clueId;
    ClueDetailWrapper.ClueDetail data;
    private int clueStatus;
    private boolean isDelete = false, isAdd = false;
    private CustomerRegional regional = new CustomerRegional();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("线索详情");
        setupViews();
        getIntenData();
    }

    @Override
    public void setLayoutView() {
        setContentView(R.layout.activity_clue_detail);
    }

    @Override
    public void getPageData() {
        getClueDetail();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getPageData();
    }

    private void setupViews() {
        /* Navigation Bar */
        img_title_left = (ViewGroup) findViewById(R.id.img_title_left);
        img_title_right = (ViewGroup) findViewById(R.id.img_title_right);
        img_title_left.setOnClickListener(this);
        img_title_right.setOnClickListener(this);

        /* 分区1 */
        ll_status = (LinearLayout) findViewById(R.id.ll_status);
        ll_status.setOnClickListener(this); // 选择状态

        section1_username = (TextView) findViewById(R.id.tv_section1_username);
        section1_company_name = (TextView) findViewById(R.id.tv_section1_company_name);
        tv_status = (TextView) findViewById(R.id.tv_status);

        /* 分区2 */
        section2_visit = (ViewGroup) findViewById(R.id.ll_section2_visit);
        ll_track = (ViewGroup) findViewById(R.id.ll_track);
        section2_visit.setOnClickListener(this); // 选择来源

        tv_visit_number = (TextView) findViewById(R.id.tv_visit_number);
        tv_track_content = (TextView) findViewById(R.id.tv_track_content);
        tv_track_time = (TextView) findViewById(R.id.tv_track_time);

        /* 分区3 */
        ll_sms = (ViewGroup) findViewById(R.id.ll_sms);
        ll_sms.setOnClickListener(this);
        ll_call = (ViewGroup) findViewById(R.id.ll_call);
        ll_call.setOnClickListener(this);
        ll_wiretel_call = (ViewGroup) findViewById(R.id.ll_wiretel_call);
        ll_wiretel_call.setOnClickListener(this);
        layout_clue_region = (ViewGroup) findViewById(R.id.layout_clue_region);
        layout_clue_source = (ViewGroup) findViewById(R.id.layout_clue_source);
        layout_clue_region.setOnClickListener(this); // 选择地区
        layout_clue_source.setOnClickListener(this); // 选择来源

        contact_mobile = (TextView) findViewById(R.id.tv_contact_mobile);
        contact_wiretel = (TextView) findViewById(R.id.tv_contact_wiretel);
        clue_region = (TextView) findViewById(R.id.tv_clue_region);
        clue_source = (TextView) findViewById(R.id.tv_section3_clue_source);
        clue_note = (TextView) findViewById(R.id.tv_clue_note);

        /* 分区4 */
        responsible_name = (TextView) findViewById(R.id.tv_responsible_name);
        creator_name = (TextView) findViewById(R.id.tv_creator_name);
        create_time = (TextView) findViewById(R.id.tv_create_time);
        update_time = (TextView) findViewById(R.id.tv_update_time);
        tv_address = (TextView) findViewById(R.id.tv_address);

    }

    public void bindData() {
        ClueSales sales = data.sales;
//        if (!MainApp.user.id.equals(sales.responsorId)) {//如果不是负责人有编辑 添加的权限
//            img_title_right.setVisibility(View.GONE);
//            isAdd = false;
//        } else {
//            img_title_right.setVisibility(View.VISIBLE);
//            isAdd = true;
//        }

        isAdd = true;


        /* 分区1 */
        section1_username.setText(sales.name);
        section1_company_name.setText(sales.companyName);
        tv_status.setText("" + sales.getStatus());

        /* 分区2 */
        if (sales.saleActivityCount <= 0      /* 没有拜访记录 */
                || data.activity == null /* 当>0时，服务端也可能返空数据 */) {
            ll_track.setVisibility(View.GONE);
        } else {
            ll_track.setVisibility(View.VISIBLE);
            tv_track_content.setText(data.activity.content.contains("<p>") ?
                    CommonHtmlUtils.Instance().checkContent(data.activity.content) : data.activity.content);
//            tv_track_time.setText(app.df3.format(new Date(Long.valueOf(data.activity.createAt + "") * 1000))+ "  " + data.activity.creatorName + " # " + data.activity.typeName);
            tv_track_time.setText(DateTool.getDateTimeFriendly(data.activity.createAt) + "  " + data.activity.creatorName + " # " + data.activity.typeName);
        }
        tv_visit_number.setText("(" + sales.saleActivityCount + ")");

        /* 分区3 */
        contact_mobile.setText(sales.cellphone);
        contact_wiretel.setText(sales.tel);
        clue_region.setText(sales.getRegion());
        regional = sales.region;
        tv_address.setText(sales.address);
        clue_source.setText(sales.source);
        clue_note.setText(sales.remark);
        clueStatus = sales.status;
        /* 分区4 */
        responsible_name.setText(sales.responsorName);
        creator_name.setText(sales.creatorName);
//        create_time.setText(app.df3.format(new Date(Long.valueOf(sales.createdAt + "") * 1000)));
//        update_time.setText(app.df3.format(new Date(Long.valueOf(sales.updatedAt + "") * 1000)));

        create_time.setText(DateTool.getDateTimeFriendly(sales.createdAt));
        update_time.setText(DateTool.getDateTimeFriendly(sales.updatedAt));
    }

    private void getIntenData() {
        Intent intent = getIntent();
        clueId = intent.getStringExtra(ExtraAndResult.EXTRA_ID);
        if (TextUtils.isEmpty(clueId)) {
            onBackPressed();
            Toast("参数不全");
        }
    }

    /**
     * 获取 线索详情
     */
    public void getClueDetail() {
        if (clueId == null) {
            return;
        }
//        RestAdapterFactory.getInstance()
//                .build(Config_project.API_URL_CUSTOMER())
//                .create(IClue.class)
//                .getClueDetail(clueId, new Callback<BaseBeanT<ClueDetailWrapper.ClueDetail>>() {
//                    @Override
//                    public void success(BaseBeanT<ClueDetailWrapper.ClueDetail> detail, Response response) {
//                        HttpErrorCheck.checkResponse("线索详情：", response, ll_loading);
//                        data = detail.data;
//                        bindData();
//                    }
//
//                    @Override
//                    public void failure(RetrofitError error) {
//                        HttpErrorCheck.checkError(error, ll_loading);
//                    }
//                });

        ClueService.getClueDetail(clueId).subscribe(new DefaultLoyoSubscriber<BaseBeanT<ClueDetailWrapper.ClueDetail>>(ll_loading) {
            @Override
            public void onNext(BaseBeanT<ClueDetailWrapper.ClueDetail> detail) {
                ll_loading.setStatus(LoadingLayout.Success);
                data = detail.data;
                bindData();
            }
        });
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            /* 返回按钮 */
            case R.id.img_title_left:
                onBackPressed();
                break;
            /* 右上弹出菜单 */
            case R.id.img_title_right:
                functionButton();
                break;
            /* 选择地区*/
            case R.id.layout_clue_region:
                selectArea();
                break;
            /* 选择来源 */
            case R.id.layout_clue_source:
                selectSource();
                break;
            /* 跟进列表 */
            case R.id.ll_section2_visit:
                clueActivity();
                break;

            case R.id.ll_status:
                editClueStatus();
                break;
            case R.id.ll_sms:
                Utils.sendSms(this, data.sales.cellphone);
                break;
            case R.id.ll_call:
                if (!TextUtils.isEmpty(data.sales.cellphone)) {
                    isMobile(data.sales.cellphone, 0, data.sales.name);
                } else {
                    Toast("电话号码不能为空");
                }
                break;
            case R.id.ll_wiretel_call:
                if (!TextUtils.isEmpty(data.sales.cellphone)) {
                    isMobile(data.sales.cellphone, 1, data.sales.name);
                } else {
                    Toast("电话号码不能为空");
                }
                break;
        }
    }


    /**
     * callType 0 手机 1座机
     * 电话号码格式验证
     */
    public void isMobile(final String phone, final int callType, final String name) {
        if (null == MainApp.user.mobile || TextUtils.isEmpty(MainApp.user.mobile)) {
            final SweetAlertDialogView sweetAlertDialogView = new SweetAlertDialogView(mContext);
            sweetAlertDialogView.alertHandle(new SweetAlertDialog.OnSweetClickListener() {
                @Override
                public void onClick(SweetAlertDialog sweetAlertDialog) {
                    sweetAlertDialogView.sweetAlertDialog.dismiss();
                }
            }, new SweetAlertDialog.OnSweetClickListener() {
                @Override
                public void onClick(SweetAlertDialog sweetAlertDialog) {
                    sweetAlertDialogView.sweetAlertDialog.dismiss();
                    MainApp.getMainApp().startActivity((Activity) mContext, EditUserMobileActivity.class, MainApp.ENTER_TYPE_RIGHT, false, null);
                }
            }, "提示", mContext.getString(R.string.app_homeqq_message));
        } else {
            paymentSet(phone, callType, name);
        }
    }

    /**
     * 拨打电话弹出框
     */
    public void paymentSet(final String phone, final int callType, final String name) {
        boolean checkTag = false;
        if (callType == 0) {
            checkTag = RegularCheck.isYunPhone(phone);
        } else {
            checkTag = RegularCheck.isYunTell(phone);
        }

        final CallPhonePopView callPhonePopView = new CallPhonePopView(mContext, name, checkTag);
        callPhonePopView.show();
        callPhonePopView.setCanceledOnTouchOutside(true);
         /*商务电话-回拨*/
        callPhonePopView.businessPhone(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callReturn(phone, callType, name);
            }
        });

         /*商务电话-直拨*/
        callPhonePopView.directPhone(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callLive(phone, callType, name);
//                contactProcessCallback.onCallBack(phone.replaceAll(" +", ""), mContact.getId(), mContact.getName().trim().toString(), DirectPhone, phoneType);
                callPhonePopView.dismiss();
            }
        });
        /*普通电话*/
        callPhonePopView.commonlyPhone(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (callType == 0) {
                    if (RegularCheck.isMobilePhone(phone)) {
                        Utils.call(mContext, phone);
                    } else {
                        Toast("电话号码格式不正确或为空!");
                    }
                } else {
                    Utils.call(mContext, phone);
                }

                callPhonePopView.dismiss();
            }
        });
        callPhonePopView.cancelPhone(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callPhonePopView.dismiss();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        PermissionTool.requestPermissionsResult(permissions, grantResults, new PermissionTool.PermissionsResultCallBack() {
            @Override
            public void success() {
                Utils.call(mContext, data.sales.cellphone);
            }

            @Override
            public void fail() {
                Toast("你拒绝了打电话权限，无法拨出电话");
            }
        });
    }

    /**
     * 回拨查询
     */
    void callReturn(String phone, int callType, final String name) {
        showLoading("");
        HashMap<String, Object> map = new HashMap<>();
        map.put("salesleadId", data.sales.id);
        map.put("type", callType);
        map.put("mobile", phone);
//        RestAdapterFactory.getInstance().build(Config_project.API_URL_CUSTOMER()).create(IClue.class).getCallReturnInfo(map,
//                new RCallback<CallBackCallid>() {
//                    @Override
//                    public void success(final CallBackCallid callBackCallid, final Response response) {
//                        HttpErrorCheck.checkResponse("线索请求回拨", response);
//                        try {
//                            switch (callBackCallid.errcode) {
//                                case 0:
//                                    Bundle mBundle = new Bundle();
//                                    mBundle.putString(ExtraAndResult.WELCOM_KEY, callBackCallid.data.callLogId);
//                                    mBundle.putString(ExtraAndResult.EXTRA_NAME, name);
//                                    app.startActivity(ClueDetailActivity.this, CallPhoneBackActivity.class, MainApp.ENTER_TYPE_RIGHT, false, mBundle);
//                                    break;
//
//                                case 50000:
//                                    Toast("主叫与被叫号码不能相同!");
//                                    break;
//
//                                case 50001:
//                                    Toast("余额不足!");
//                                    break;
//
//                                case 50002:
//                                    Toast("号码格式错误!");
//                                    break;
//                            }
//                        } catch (NullPointerException e) {
//                            e.printStackTrace();
//                            Toast(e.getMessage());
//                            finish();
//                        }
//                    }
//
//                    @Override
//                    public void failure(final RetrofitError error) {
//                        super.failure(error);
//                        HttpErrorCheck.checkError(error);
//                    }
//                });

        ClueService.getCallReturnInfo(map).subscribe(new DefaultLoyoSubscriber<CallBackCallid>() {
            @Override
            public void onNext(CallBackCallid callBackCallid) {
                try {
                    switch (callBackCallid.errcode) {
                        case 0:
                            Bundle mBundle = new Bundle();
                            mBundle.putString(ExtraAndResult.WELCOM_KEY, callBackCallid.data.callLogId);
                            mBundle.putString(ExtraAndResult.EXTRA_NAME, name);
                            app.startActivity(ClueDetailActivity.this, CallPhoneBackActivity.class, MainApp.ENTER_TYPE_RIGHT, false, mBundle);
                            break;

                        case 50000:
                            Toast("主叫与被叫号码不能相同!");
                            break;

                        case 50001:
                            Toast("余额不足!");
                            break;

                        case 50002:
                            Toast("号码格式错误!");
                            break;
                    }
                } catch (NullPointerException e) {
                    e.printStackTrace();
                    Toast(e.getMessage());
                    finish();
                }
            }
        });
    }

    /**
     * 直播查询
     */
    void callLive(final String phone, int callType, final String name) {

        Bundle mBundle = new Bundle();
        mBundle.putString(VoIPCallActivity.CALLEE_PHONE_KEY, phone);
        mBundle.putString(VoIPCallActivity.CALLEE_NAME_KEY, name);
        mBundle.putString(VoIPCallActivity.CALLEE_SALE_KEY, data.sales.id);
        mBundle.putInt(VoIPCallActivity.CALLEE_USER_TYPE, callType);
        app.startActivity(ClueDetailActivity.this, VoIPCallActivity.class, MainApp.ENTER_TYPE_RIGHT, false, mBundle);
    }

    /**
     * 跳转跟进列表
     */
    private void clueActivity() {
        Intent intent = new Intent();
        intent.putExtra(ExtraAndResult.EXTRA_ID, data.sales.id);
        String name = data.sales.companyName;
        if (TextUtils.isEmpty(name)) {
            name = "";
        }
        intent.putExtra(ExtraAndResult.EXTRA_NAME, name);
        intent.putExtra(ExtraAndResult.RESULT_NAME, data.sales.responsorName);
        intent.putExtra(ExtraAndResult.EXTRA_ADD, isAdd);
        intent.setClass(this, ClueFollowUpListActivity.class);
        startActivityForResult(intent, this.RESULT_FIRST_USER);
        overridePendingTransition(R.anim.enter_righttoleft, R.anim.exit_righttoleft);
    }


    /**
     * 右上角菜单
     */
    private void functionButton() {
        if (PermissionManager.getInstance().hasPermission(BusinessOperation.CLUE_DELETING)) {
            isDelete = true;
        }

        ActionSheetDialog dialog = new ActionSheetDialog(ClueDetailActivity.this).builder();
        dialog.addSheetItem("转为客户", ActionSheetDialog.SheetItemColor.Blue, new ActionSheetDialog.OnSheetItemClickListener() {
            @Override
            public void onClick(int which) {
                if (null == data || null == data || data.sales == null) {
                    Toast("数据不全不能转为客户");
                    return;
                }
                Bundle mBundle = new Bundle();
                mBundle.putSerializable(ExtraAndResult.EXTRA_DATA, data.sales);
                app.startActivityForResult(ClueDetailActivity.this, ClueTransferActivity.class, MainApp.ENTER_TYPE_RIGHT, ExtraAndResult.REQUSET_COMMENT, mBundle);
            }
        });

        dialog.addSheetItem("转移给他人", ActionSheetDialog.SheetItemColor.Blue, new ActionSheetDialog.OnSheetItemClickListener() {
            @Override
            public void onClick(int which) {
                Bundle bundle = new Bundle();
                bundle.putBoolean(ContactPickerActivity.SINGLE_SELECTION_KEY, true);
                Intent intent = new Intent();
                intent.setClass(ClueDetailActivity.this, ContactPickerActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
                overridePendingTransition(R.anim.enter_righttoleft, R.anim.exit_righttoleft);
            }
        });

        dialog.addSheetItem("编辑", ActionSheetDialog.SheetItemColor.Blue, new ActionSheetDialog.OnSheetItemClickListener() {
            @Override
            public void onClick(int which) {
                Bundle mBundle = new Bundle();
                mBundle.putString(ExtraAndResult.EXTRA_ID, clueId);
                mBundle.putSerializable(ExtraAndResult.EXTRA_DATA, data);
                app.startActivityForResult(ClueDetailActivity.this, ClueAddActivity.class,
                        MainApp.ENTER_TYPE_RIGHT, ExtraAndResult.REQUEST_CODE_STAGE, mBundle);
            }
        });

        if (isDelete)
            dialog.addSheetItem("删除", ActionSheetDialog.SheetItemColor.Red, new ActionSheetDialog.OnSheetItemClickListener() {
                @Override
                public void onClick(int which) {

                    sweetAlertDialogView.alertHandle(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                            dismissSweetAlert();
                        }
                    }, new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                            dismissSweetAlert();
                            deleteClue();
                        }
                    }, "提示", "线索删除过后不可恢复，\n你确定要删除？");
                }
            });

        dialog.show();
    }

    /**
     * 显示地区选择Dialog  选择地区
     */
    void selectArea() {
        String[] cityValue = null;
        if (data != null && data != null && data.sales != null
                && data.sales.region != null) {
            cityValue = data.sales.region.toArray();
        }
        final SelectCityView selectCityView = new SelectCityView(this, cityValue);
        selectCityView.setCanceledOnTouchOutside(true);
        selectCityView.show();
        selectCityView.setOnclickselectCity(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                String[] cityArr = selectCityView.getResult();

                regional.province = cityArr[0];
                regional.city = cityArr[1];
                regional.county = cityArr[2];
                clue_region.setText(regional.salesleadDisplayText());

                selectCityView.dismiss();
                editAreaAndSource(1);
            }
        });
    }

    /**
     * 线索来源选择
     */
    private void selectSource() {
        String[] dataKind = app.gson.fromJson(SharedUtil.get(app, ExtraAndResult.SOURCES_DATA),
                new TypeToken<String[]>() {
                }.getType());
        if (null == dataKind) {
            Toast("数据加载中...");
            ClueCommon.getSourceData();//缓存线索来源数据
            return;
        }
        final PaymentPopView popViewKind = new PaymentPopView(this, dataKind, "线索来源");
        popViewKind.show();
        popViewKind.setCanceledOnTouchOutside(true);
        popViewKind.setCallback(new PaymentPopView.VaiueCallback() {
            @Override
            public void setValue(String value, int index) {
//                payeeMethod = index;
                clue_source.setText(value);
                editAreaAndSource(2);
            }
        });
    }

    private void editClueStatus() {
        String[] data = {"未处理", "已联系", "关闭"};
        final PaymentPopView popViewKind = new PaymentPopView(this, data, "线索状态");
        popViewKind.show();
        popViewKind.setCanceledOnTouchOutside(true);
        popViewKind.setCallback(new PaymentPopView.VaiueCallback() {
            @Override
            public void setValue(String value, int index) {
                clueStatus = index;
                tv_status.setText(value);
                editAreaAndSource(3);
            }
        });
    }

    /**
     * 编辑线索 1 地区 2 线索来源
     * 编辑线索把全部的内容传一次  12-22
     *
     * @param function
     */
    private void editAreaAndSource(final int function) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("name", section1_username.getText().toString());
        map.put("companyName", section1_company_name.getText().toString());
        map.put("cellphone", contact_mobile.getText().toString());
        map.put("tel", contact_wiretel.getText().toString());
        map.put("address", tv_address.getText().toString());
        map.put("remark", clue_note.getText().toString());
        map.put("region", regional);
        map.put("source", clue_source.getText().toString());
        map.put("status", clueStatus);
        LogUtil.d(app.gson.toJson(map));
//        RestAdapterFactory.getInstance().build(Config_project.API_URL_CUSTOMER()).create(IClue.class)
//                .editClue(clueId, map, new Callback<Object>() {
//                    @Override
//                    public void success(Object o, Response response) {
//                        HttpErrorCheck.checkResponse("【编辑详情】线索：", response);
//                        /* 提交成功，更新本地model */
//                        if (1 == function
//                                && data != null && data != null && data.sales != null) {
//                            data.sales.region = regional;
//                            clue_region.setText(regional.salesleadDisplayText());
//                        }
//
//                    }
//
//                    @Override
//                    public void failure(RetrofitError error) {
//                        HttpErrorCheck.checkError(error);
//                        /* 提交失败，更新UI至原来状态 */
//                        if (1 == function
//                                && data != null && data != null && data.sales != null) {
//                            clue_region.setText(data.sales.region.salesleadDisplayText());
//                        }
//
//                    }
//                });

        ClueService.editClue(clueId, map).subscribe(new DefaultLoyoSubscriber<Object>(LoyoErrorChecker.COMMIT_DIALOG) {
            @Override
            public void onError(Throwable e) {
                super.onError(e);
                 /* 提交失败，更新UI至原来状态 */
                if (1 == function
                        && data != null && data != null && data.sales != null) {
                    clue_region.setText(data.sales.region.salesleadDisplayText());
                }
            }

            @Override
            public void onNext(Object o) {
                /* 提交成功，更新本地model */
                if (1 == function
                        && data != null && data != null && data.sales != null) {
                    data.sales.region = regional;
                    clue_region.setText(regional.salesleadDisplayText());
                }

            }
        });
    }

    /**
     * 删除 线索
     */
    private void deleteClue() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("ids", clueId);
//        RestAdapterFactory.getInstance().build(Config_project.API_URL_CUSTOMER()).create(IClue.class)
//                .deleteClue(map, new Callback<Object>() {
//                    @Override
//                    public void success(Object o, Response response) {
//                        HttpErrorCheck.checkResponse("【删除详情】线索：", response);
//                        app.finishActivity(ClueDetailActivity.this, MainApp.ENTER_TYPE_LEFT, ExtraAndResult.REQUEST_CODE, new Intent());
//                    }
//
//                    @Override
//                    public void failure(RetrofitError error) {
//                        HttpErrorCheck.checkError(error);
//                    }
//                });

        ClueService.deleteClue(map).subscribe(new DefaultLoyoSubscriber<Object>() {
            @Override
            public void onNext(Object o) {
                app.finishActivity(ClueDetailActivity.this, MainApp.ENTER_TYPE_LEFT, ExtraAndResult.REQUEST_CODE, new Intent());
            }
        });
    }

    /**
     * 转移 线索
     */
    private void transferClue(String responsorId) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("ids", clueId);
        map.put("responsorId", responsorId);
//        RestAdapterFactory.getInstance().build(Config_project.API_URL_CUSTOMER()).create(IClue.class)
//                .transferClue(map, new Callback<Object>() {
//                    @Override
//                    public void success(Object o, Response response) {
//                        HttpErrorCheck.checkResponse("【转 移】线索：", response);
//                        app.finishActivity(ClueDetailActivity.this, MainApp.ENTER_TYPE_LEFT, ExtraAndResult.REQUEST_CODE, new Intent());
//                    }
//
//                    @Override
//                    public void failure(RetrofitError error) {
//                        HttpErrorCheck.checkError(error);
//                    }
//                });

        ClueService.transferClue(map).subscribe(new DefaultLoyoSubscriber<Object>() {
            @Override
            public void onNext(Object o) {
                app.finishActivity(ClueDetailActivity.this, MainApp.ENTER_TYPE_LEFT, ExtraAndResult.REQUEST_CODE, new Intent());
            }
        });
    }

    /**
     * 选人回调
     */
    @Subscribe
    public void onContactPicked(ContactPickedEvent event) {
        StaffMemberCollection collection = event.data;
        final OrganizationalMember user = Compat.convertStaffCollectionToNewUser(collection);
        if (user == null) {
            return;
        }
        sweetAlertDialogView.alertHandle(new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sweetAlertDialog) {
                dismissSweetAlert();
            }
        }, new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sweetAlertDialog) {
                dismissSweetAlert();
                transferClue(user.getId());
            }
        }, "提示", "转移后，线索的数据和管理权限\n将归属新的负责人。\n你确定要转移？");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            return;
        }

        switch (requestCode) {
            case FinalVariables.REQUEST_ONLY:
                final OrganizationalMember u = (OrganizationalMember) data.getSerializableExtra("data");

                sweetAlertDialogView.alertHandle(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        dismissSweetAlert();
                    }
                }, new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        dismissSweetAlert();
                        transferClue(u.getId());
                    }
                }, "提示", "转移后，线索的数据和管理权限\n将归属新的负责人。\n你确定要转移？");

                break;

            /*转移客户*/
            case ExtraAndResult.REQUSET_COMMENT:
                app.finishActivity(ClueDetailActivity.this, MainApp.ENTER_TYPE_LEFT, ExtraAndResult.REQUEST_CODE, new Intent());
                break;

        }
    }
}
