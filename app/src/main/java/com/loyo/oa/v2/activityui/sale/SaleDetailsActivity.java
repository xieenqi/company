package com.loyo.oa.v2.activityui.sale;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.library.module.widget.loading.LoadingLayout;
import com.loyo.oa.common.utils.DateTool;
import com.loyo.oa.hud.progress.LoyoProgressHUD;
import com.loyo.oa.hud.toast.LoyoToast;
import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.customer.model.ContactLeftExtras;
import com.loyo.oa.v2.activityui.order.OrderAddActivity;
import com.loyo.oa.v2.activityui.order.OrderDetailActivity;
import com.loyo.oa.v2.activityui.order.bean.OrderDetail;
import com.loyo.oa.v2.activityui.sale.api.SaleService;
import com.loyo.oa.v2.activityui.product.IntentionProductActivity;
import com.loyo.oa.v2.activityui.sale.bean.ActionCode;
import com.loyo.oa.v2.activityui.sale.bean.CommonTag;
import com.loyo.oa.v2.activityui.sale.bean.SaleDetails;
import com.loyo.oa.v2.activityui.sale.bean.SaleIntentionalProduct;
import com.loyo.oa.v2.activityui.sale.bean.SaleStage;
import com.loyo.oa.v2.activityui.sale.contract.SaleDetailContract;
import com.loyo.oa.v2.activityui.sale.presenter.SaleDetailPresenterImpl;
import com.loyo.oa.v2.activityui.wfinstance.WfinstanceInfoActivity_;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.common.ExtraAndResult;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.customview.ActionSheetDialog;
import com.loyo.oa.v2.customview.ViewSaleDetailsExtra;
import com.loyo.oa.v2.network.DefaultLoyoSubscriber;
import com.loyo.oa.v2.tool.BaseLoadingActivity;
import com.loyo.oa.v2.tool.LogUtil;
import com.loyo.oa.v2.tool.Utils;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * 【机会详情】
 * Created by yyy on 16/5/19.
 */
public class SaleDetailsActivity extends BaseLoadingActivity implements View.OnClickListener, SaleDetailContract.View {

    private LinearLayout img_title_left;
    private LinearLayout layout_losereson;
    private LinearLayout sale_wintime;
    private RelativeLayout img_title_right;
    private SaleDetails mSaleDetails;
    private String selectId = "";
    private StringBuffer productBuffer;
    private StringBuffer loseReasonBuffer;
    private String stageId;
    private String stageName;
    private String fromPath;
    private ArrayList<CommonTag> loseResons;

    private LinearLayout ll_product;
    private LinearLayout ll_stage;
    private LinearLayout ll_extra;
    private TextView title;
    private TextView customer;
    private TextView salesAmount;
    private TextView estimatedAmount;
    private TextView losereason;
    private TextView chanceType;
    private TextView chanceSource;
    private TextView memo;
    private TextView creator;
    private TextView creatorTime;
    private TextView updateTime;
    private TextView winTime;
    private TextView product;
    private TextView text_stagename;
    private TextView director;
    private ImageView iv_wfstatus;
    private double totalMoney;//意向产品总金额
    private boolean isTeam;//是否是团队的详情
    private SaleDetailContract.Presenter mPersenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPersenter = new SaleDetailPresenterImpl(this);
        setTitle("机会详情");
        initView();
    }

    @Override
    public void setLayoutView() {
        setContentView(R.layout.activity_saledetails);
    }

    @Override
    public void getPageData() {
        mPersenter.getPageData(selectId);
    }

    public void initView() {
        img_title_left = (LinearLayout) findViewById(R.id.img_title_left);
        layout_losereson = (LinearLayout) findViewById(R.id.layout_losereson);
        sale_wintime = (LinearLayout) findViewById(R.id.sale_wintime);
        img_title_right = (RelativeLayout) findViewById(R.id.img_title_right);
        title = (TextView) findViewById(R.id.title);
        customer = (TextView) findViewById(R.id.customer);
        salesAmount = (TextView) findViewById(R.id.salesAmount);
        estimatedAmount = (TextView) findViewById(R.id.estimatedAmount);
        losereason = (TextView) findViewById(R.id.losereason);
        chanceType = (TextView) findViewById(R.id.chancetype);
        chanceSource = (TextView) findViewById(R.id.chancesource);
        memo = (TextView) findViewById(R.id.memo);
        creator = (TextView) findViewById(R.id.creator);
        creatorTime = (TextView) findViewById(R.id.creatortime);
        updateTime = (TextView) findViewById(R.id.updatetime);
        winTime = (TextView) findViewById(R.id.wintime);
        product = (TextView) findViewById(R.id.text_product);
        director = (TextView) findViewById(R.id.director);
        text_stagename = (TextView) findViewById(R.id.text_stagename);
        ll_product = (LinearLayout) findViewById(R.id.ll_product);
        ll_stage = (LinearLayout) findViewById(R.id.ll_stage);
        ll_extra = (LinearLayout) findViewById(R.id.ll_extra);
        iv_wfstatus = (ImageView) findViewById(R.id.iv_wfstatus);
        img_title_left.setOnClickListener(this);
        img_title_right.setOnClickListener(this);
        ll_product.setOnClickListener(this);
        ll_stage.setOnClickListener(this);
        Global.SetTouchView(ll_stage, ll_product, img_title_right, img_title_left, iv_wfstatus);
        iv_wfstatus.setOnClickListener(this);
        getIntenData();
        getPageData();
    }

    private void getIntenData() {
        Intent mIntent = getIntent();
        selectId = mIntent.getStringExtra("id");
        isTeam = mIntent.getBooleanExtra(ExtraAndResult.IS_TEAM, false);
        fromPath = mIntent.getStringExtra("formPath");
        /*        if (!TextUtils.isEmpty(fromPath) && fromPath.equals("审批")) {
            //审批过来不准编辑
            iv_wfstatus.setEnabled(false);
            img_title_right.setVisibility(View.INVISIBLE);
            ll_product.setEnabled(false);
            ll_stage.setEnabled(false);
        }*/
    }


    /**
     * 编辑销售阶段
     */
    public void editStage() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("stageId", stageId);
        map.put("cId", selectId);
        if (null != loseResons) {
            map.put("loseReason", loseResons);
        }
        map.put("content", "销售阶段由\"" + mSaleDetails.getStageName() + "\"变更为\"" + stageName + "\"");
        LogUtil.d("编辑销售阶段:" + MainApp.gson.toJson(map));
        mPersenter.editSaleStage(map, selectId);
    }


    /**
     * 数据绑定
     */
    public void bindData() {
        ll_stage.setEnabled(getEditPriority());
        ll_product.setEnabled(true);

        boolean showActionsheet = getEditPriority() || canGenerateOrder();
        img_title_right.setVisibility(showActionsheet ? View.VISIBLE : View.INVISIBLE);

        //已通过的审批 任何人都不能删除
        if (mSaleDetails.wfState == 0 && mSaleDetails.prob == 100) {
            iv_wfstatus.setEnabled(false);
        }

        title.setText(mSaleDetails.getName());
        customer.setText(mSaleDetails.getCusName());
        salesAmount.setText("" + Utils.setValueDouble(mSaleDetails.estimatedAmount));
//        estimatedAmount.setText(mSaleDetails.estimatedTime != 0 ? app.df4.format(new Date(Long.valueOf(mSaleDetails.estimatedTime + "") * 1000)) : "无");
        estimatedAmount.setText(mSaleDetails.estimatedTime != 0 ? DateTool.getDateFriendly(mSaleDetails.estimatedTime) : "无");
        chanceType.setText(mSaleDetails.getChanceType());
        chanceSource.setText(mSaleDetails.getChanceSource());
        memo.setText(mSaleDetails.getMemo());
        if (null != mSaleDetails.getDirectorName() || !TextUtils.isEmpty(mSaleDetails.getDirectorName())) {
            director.setText(mSaleDetails.getDirectorName());
        } else {
            director.setText("无");
        }
        creator.setText(mSaleDetails.getCreatorName());
//        creatorTime.setText(app.df3.format(new Date(Long.valueOf(mSaleDetails.getCreatedAt() + "") * 1000)));
        creatorTime.setText(DateTool.getDateTimeFriendly(mSaleDetails.getCreatedAt()));
//        updateTime.setText(app.df3.format(new Date(Long.valueOf(mSaleDetails.getUpdatedAt() + "") * 1000)));
        updateTime.setText(DateTool.getDateTimeFriendly(mSaleDetails.getUpdatedAt()));
        text_stagename.setText(mSaleDetails.getStageName());
        productBuffer = new StringBuffer();
        if (null != mSaleDetails.getProInfos()) {
            for (SaleIntentionalProduct sitpeoduct : mSaleDetails.getProInfos()) {
                productBuffer.append(sitpeoduct.name + "、");
            }
            product.setText(productBuffer.toString().substring(0, productBuffer.toString().length() - 1));
        }
        ll_extra.setVisibility(View.VISIBLE);
        if (ll_extra.getChildCount() != 0) {
            ll_extra.removeAllViews();
        }
        if (null != mSaleDetails.extensionDatas) {
            for (ContactLeftExtras saleDetailsExtraList : mSaleDetails.extensionDatas) {
                ll_extra.addView(new ViewSaleDetailsExtra(mContext, saleDetailsExtraList));
            }
        }
        /*当为输单阶段时，显示输单原因*/
        if (mSaleDetails.getProb() == 0) {
            layout_losereson.setVisibility(View.VISIBLE);
            loseReasonBuffer = new StringBuffer();
            for (CommonTag commonTag : mSaleDetails.getLoseReason()) {
                loseReasonBuffer.append(commonTag.getName() + " ");
            }
            losereason.setText(loseReasonBuffer.toString());
        } else {
            layout_losereson.setVisibility(View.GONE);
        }
        if (100 == mSaleDetails.prob) {//销售阶段是赢单的时候
            iv_wfstatus.setVisibility(View.VISIBLE);
            switch (mSaleDetails.wfState) {
                case 1:
                    iv_wfstatus.setImageResource(R.drawable.img_task_wite);
                    break;
                case 2:
                    iv_wfstatus.setImageResource(R.drawable.img_wfinstance_status2);
                    break;
                case 3:
                    iv_wfstatus.setImageResource(R.drawable.img_wfinstance_status3);
                    if (!isTeam || MainApp.user.isSuperUser) {//非团队才有权限
                        img_title_right.setVisibility(View.VISIBLE);
//                        ll_product.setEnabled(true);//屏蔽快捷编辑赢单审批
//                        ll_stage.setEnabled(true);
                    }
                    break;
                case 4:
                    iv_wfstatus.setImageResource(R.drawable.img_wfinstance_status4);
                    winTime.setText(com.loyo.oa.common.utils.DateTool.getDateTimeFriendly(mSaleDetails.getWinTime()));
                    sale_wintime.setVisibility(View.VISIBLE);
                    break;
                case 5:
                    iv_wfstatus.setImageResource(R.drawable.img_task_status_finish);
                    winTime.setText(com.loyo.oa.common.utils.DateTool.getDateTimeFriendly(mSaleDetails.getWinTime()));
                    sale_wintime.setVisibility(View.VISIBLE);
                    break;
            }
        } else if (0 == mSaleDetails.wfState && mSaleDetails.stageName.contains("赢单")) {
            winTime.setText(com.loyo.oa.common.utils.DateTool.getDateTimeFriendly(mSaleDetails.getUpdatedAt()));
            sale_wintime.setVisibility(View.VISIBLE);
        }
        //计算产品总金额
        if (null != mSaleDetails.proInfos) {
            for (SaleIntentionalProduct ele : mSaleDetails.proInfos) {
                totalMoney += ele.totalMoney;
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.img_title_left:
                onBackPressed();
                break;
            //弹出菜单
            case R.id.img_title_right:
                functionBuuton();
                break;
            //意向产品
            case R.id.ll_product:
                Bundle product = new Bundle();
                product.putInt("data", ActionCode.SALE_FROM_DETAILS);
                product.putString("saleId", selectId);

                boolean canDelete = getDeletePriority();
                boolean canEdit = getEditPriority();

                product.putBoolean(IntentionProductActivity.KEY_CAN_DELETE, canDelete);
                product.putBoolean(IntentionProductActivity.KEY_CAN_EDIT, canEdit);

                product.putSerializable(ExtraAndResult.EXTRA_DATA, mSaleDetails.getProInfos());
                app.startActivityForResult(SaleDetailsActivity.this, IntentionProductActivity.class,
                        MainApp.ENTER_TYPE_RIGHT, ExtraAndResult.REQUEST_CODE_PRODUCT, product);
                break;
            //销售阶段`     
            case R.id.ll_stage:
                Bundle stage = new Bundle();
                stage.putInt(ExtraAndResult.EXTRA_TYPE, SaleStageActivity.SALE_STAGE);
                stage.putString(ExtraAndResult.EXTRA_NAME, "销售阶段");
                stage.putString(ExtraAndResult.CC_USER_NAME, mSaleDetails.name);
                stage.putString(ExtraAndResult.EXTRA_BOOLEAN, Utils.setValueDouble(totalMoney) + "");
                stage.putString(ExtraAndResult.EXTRA_DATA, text_stagename.getText().toString());
                stage.putBoolean(ExtraAndResult.EXTRA_STATUS, null == mSaleDetails.proInfos ? false : true);
                app.startActivityForResult(SaleDetailsActivity.this, SaleStageActivity.class,
                        MainApp.ENTER_TYPE_RIGHT, ExtraAndResult.REQUEST_CODE_STAGE, stage);
                break;
            //到审批
            case R.id.iv_wfstatus:
                Intent wfinstance = new Intent();
                wfinstance.putExtra(ExtraAndResult.EXTRA_ID, mSaleDetails.wfId);
                wfinstance.setClass(SaleDetailsActivity.this, WfinstanceInfoActivity_.class);
                startActivityForResult(wfinstance, ExtraAndResult.REQUEST_CODE);
                break;

        }
    }

    private boolean getDeletePriority() {
        boolean canDelete = (null != mSaleDetails)                 /*未异常*/
                &&
                (100 != mSaleDetails.prob                                         /*未赢单*/
                        || (mSaleDetails.wfState != 4 && mSaleDetails.wfState != 5 /*赢单未通过*/
                        && mSaleDetails.wfState != 0 /*赢单需要审核*/
                        && mSaleDetails.wfState != 1 && mSaleDetails.wfState != 2) // 待审核,审核中
                )
                &&
                (MainApp.user.id.equals(mSaleDetails.creatorId) /*自己的销售机会*/
                        || MainApp.user.isSuperUser              /*超级管理员*/
                );
        return canDelete;
    }

    private boolean getEditPriority() {
        boolean canEdit = (null != mSaleDetails)                 /*未异常*/
                &&
                (100 != mSaleDetails.prob                                         /*未赢单*/
                        || (mSaleDetails.wfState != 4 && mSaleDetails.wfState != 5/*赢单未通过*/
                        && mSaleDetails.wfState != 0 /*赢单需要审核*/
                        && mSaleDetails.wfState != 1 && mSaleDetails.wfState != 2) // 待审核,审核中
                )
                && MainApp.user.id.equals(mSaleDetails.creatorId) /*自己的销售机会*/;
        return canEdit;
    }

    private boolean canGenerateOrder() {
        boolean canEdit = (null != mSaleDetails)                 /*未异常*/
                &&
                (100 == mSaleDetails.prob                                         /*未赢单*/
                        && (mSaleDetails.wfState != 4 && mSaleDetails.wfState != 5/*赢单未通过*/
                        && mSaleDetails.wfState != 0 /*赢单需要审核*/
                        && mSaleDetails.wfState != 1 && mSaleDetails.wfState != 2) // 待审核,审核中
                )
                && MainApp.user.id.equals(mSaleDetails.creatorId) /*自己的销售机会*/;
        return canEdit;
    }

    /**
     * 右上角菜单
     */
    private void functionBuuton() {
        ActionSheetDialog dialog = new ActionSheetDialog(SaleDetailsActivity.this).builder();
        if (getEditPriority()) {
            dialog.addSheetItem("编辑", ActionSheetDialog.SheetItemColor.Blue, new ActionSheetDialog.OnSheetItemClickListener() {
                @Override
                public void onClick(int which) {
                    editSale();
                }
            });
        }
        if (canGenerateOrder()) {
            dialog.addSheetItem("生成订单", ActionSheetDialog.SheetItemColor.Blue, new ActionSheetDialog.OnSheetItemClickListener() {
                @Override
                public void onClick(int which) {
                    OrderDetail mData = new OrderDetail();
                    mData.proInfo = mSaleDetails.proInfos;
                    mData.dealMoney = (float) totalMoney;
                    mData.customerName = mSaleDetails.cusName;
                    mData.customerId = mSaleDetails.customerId;
                    Bundle mBundle = new Bundle();
                    mBundle.putInt("fromPage", OrderDetailActivity.ORDER_CREATE);
                    mBundle.putSerializable("data", mData);
                    app.startActivityForResult(SaleDetailsActivity.this, OrderAddActivity.class, MainApp.ENTER_TYPE_RIGHT, ExtraAndResult.REQUEST_CODE_STAGE, mBundle);
                }
            });
        }
        if (getEditPriority()) {
            dialog.addSheetItem("删除", ActionSheetDialog.SheetItemColor.Red, new ActionSheetDialog.OnSheetItemClickListener() {
                @Override
                public void onClick(int which) {
                    deleteSale();
                }
            });
        }
        dialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        int resultAction;

        if (resultCode != RESULT_OK) {
            return;
        }

        switch (requestCode) {

            /**编辑成功后 回调*/
            case ExtraAndResult.MSG_WHAT_DIALOG:
                resultAction = data.getIntExtra(ExtraAndResult.RESULT_ID, 0);
                if (resultAction == ActionCode.SALE_DETAILS_EDIT) {
                    mPersenter.getPageData(selectId);
                }
                break;

//            /**菜单选项*/
//            case EDIT_POP_WINDOW:
//                //编辑回调
//                if (data.getBooleanExtra("edit", false) && null != mSaleDetails) {
//                    Bundle editSale = new Bundle();
//                    editSale.putSerializable(ExtraAndResult.EXTRA_DATA, mSaleDetails);
//                    app.startActivityForResult(SaleDetailsActivity.this, AddMySaleActivity.class,
//                            MainApp.ENTER_TYPE_RIGHT, ExtraAndResult.MSG_WHAT_DIALOG, editSale);
//                }
//                //删除回调
//                else if (data.getBooleanExtra("delete", false)) {
//                    deleteSale();
//                } else if (data.getBooleanExtra("extra", false)) {
////                    deleteSale();
//                    Toast("bofgbmdgofb");
//                }
//                break;
            /**意向产品*/
            case ExtraAndResult.REQUEST_CODE_PRODUCT:
                resultAction = data.getIntExtra(ExtraAndResult.STR_SELECT_TYPE, 0);
                if (resultAction == ActionCode.SALE_DETAILS_RUSH) {
                    mPersenter.getPageData(selectId);
                }
                break;
            /**销售阶段*/
            case ExtraAndResult.REQUEST_CODE_STAGE:
                SaleStage stage = (SaleStage) data.getSerializableExtra(ExtraAndResult.EXTRA_DATA);
                loseResons = (ArrayList<CommonTag>) data.getSerializableExtra(ExtraAndResult.RESULT_NAME);
                if (null != stage) {
                    text_stagename.setText(stage.name);
                    stageId = stage.id;
                    stageName = stage.name;
                    editStage();
                }
                break;
            case ExtraAndResult.REQUEST_EDIT:
                closePageUI();
                break;

        }

    }

    /**
     * 编辑机会
     */
    private void editSale() {
        Bundle editSale = new Bundle();
        editSale.putSerializable(ExtraAndResult.EXTRA_DATA, mSaleDetails);
        app.startActivityForResult(SaleDetailsActivity.this, AddMySaleActivity.class,
                MainApp.ENTER_TYPE_RIGHT, ExtraAndResult.MSG_WHAT_DIALOG, editSale);
    }

    /**
     * 删除销售机会
     */
    public void deleteSale() {
        showLoading2("");

        SaleService.deleteSaleOpportunity(selectId).subscribe(new DefaultLoyoSubscriber<SaleDetails>(hud) {
            @Override
            public void onNext(SaleDetails saleDetails) {
                app.finishActivity(SaleDetailsActivity.this, MainApp.ENTER_TYPE_RIGHT, ExtraAndResult.REQUEST_CODE_SOURCE, new Intent());
            }
        });
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
        LoyoToast.info(this, message);
    }

    @Override
    public void closePageUI() {
        onBackPressed();
    }

    @Override
    public void bindDataUI(SaleDetails saleDetails) {
        mSaleDetails = saleDetails;
        bindData();
    }

    @Override
    public void editSaleStageSuccessUI() {
        mPersenter.getPageData(selectId);
    }

    @Override
    public LoadingLayout getLoadingUI() {
        return ll_loading;
    }
}
