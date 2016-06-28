package com.loyo.oa.v2.ui.activity.sale;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.ui.activity.sale.bean.ActionCode;
import com.loyo.oa.v2.ui.activity.sale.bean.SaleDetails;
import com.loyo.oa.v2.ui.activity.sale.bean.SaleIntentionalProduct;
import com.loyo.oa.v2.ui.activity.sale.bean.SaleProductEdit;
import com.loyo.oa.v2.ui.activity.sale.bean.SaleStage;
import com.loyo.oa.v2.ui.activity.wfinstance.WfinstanceInfoActivity_;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.ui.activity.sale.bean.CommonTag;
import com.loyo.oa.v2.ui.activity.customer.bean.ContactLeftExtras;
import com.loyo.oa.v2.common.ExtraAndResult;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.common.http.HttpErrorCheck;
import com.loyo.oa.v2.point.ISale;
import com.loyo.oa.v2.tool.BaseActivity;
import com.loyo.oa.v2.tool.Config_project;
import com.loyo.oa.v2.tool.LogUtil;
import com.loyo.oa.v2.tool.RCallback;
import com.loyo.oa.v2.tool.RestAdapterFactory;
import com.loyo.oa.v2.tool.Utils;
import com.loyo.oa.v2.customview.ViewSaleDetailsExtra;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * 【机会详情】
 * Created by yyy on 16/5/19.
 */
public class SaleDetailsActivity extends BaseActivity implements View.OnClickListener {

    private final int EDIT_POP_WINDOW = 500;
    private LinearLayout img_title_left;
    private LinearLayout layout_losereson;
    private RelativeLayout img_title_right;
    private SaleDetails mSaleDetails;
    private Intent mIntent;
    private String selectId = "";
    private StringBuffer productBuffer;
    private StringBuffer loseReasonBuffer;
    private String stageId;
    private String stageName;
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
    private boolean isDelete = true;
    private double totalMoney;//意向产品总金额

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saledetails);
        setTitle("机会详情");
        initView();
    }

    public void initView() {
        mContext = this;

        img_title_left = (LinearLayout) findViewById(R.id.img_title_left);
        layout_losereson = (LinearLayout) findViewById(R.id.layout_losereson);
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
        ll_stage.setOnTouchListener(Global.GetTouch());
        ll_product.setOnTouchListener(Global.GetTouch());
        img_title_right.setOnTouchListener(Global.GetTouch());
        img_title_left.setOnTouchListener(Global.GetTouch());
        iv_wfstatus.setOnTouchListener(Global.GetTouch());
        iv_wfstatus.setOnClickListener(this);
        getIntenData();
        getData();
    }

    /**
     * 获取销售机会详情
     */
    public void getData() {
        showLoading("");
        RestAdapterFactory.getInstance().build(Config_project.API_URL_CUSTOMER())
                .create(ISale.class)
                .getSaleDetails(selectId, new RCallback<SaleDetails>() {
                    @Override
                    public void success(SaleDetails saleDetails, Response response) {
                        HttpErrorCheck.checkResponse("机会详情", response);
                        mSaleDetails = saleDetails;
                        bindData();
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        HttpErrorCheck.checkError(error);
                        finish();
                    }
                });
    }

    private void getIntenData() {
        mIntent = getIntent();
        selectId = mIntent.getStringExtra("id");
        String fromPath = mIntent.getStringExtra("formPath");
        if (!TextUtils.isEmpty(fromPath) && fromPath.equals("审批")) {
            //审批过来不准编辑
            iv_wfstatus.setEnabled(false);
            img_title_right.setVisibility(View.INVISIBLE);
            ll_product.setEnabled(false);
            ll_stage.setEnabled(false);
        }
    }

    /**
     * 删除销售机会
     */
    public void deleteSale() {
        showLoading("");
        RestAdapterFactory.getInstance().build(Config_project.API_URL_CUSTOMER())
                .create(ISale.class)
                .deleteSaleOpportunity(selectId, new RCallback<SaleDetails>() {
                    @Override
                    public void success(SaleDetails saleDetails, Response response) {
                        HttpErrorCheck.checkResponse("删除", response);
                        app.finishActivity(SaleDetailsActivity.this, MainApp.ENTER_TYPE_RIGHT, ExtraAndResult.REQUEST_CODE_SOURCE, new Intent());
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        HttpErrorCheck.checkError(error);
                    }
                });
    }

    /**
     * 编辑销售阶段
     */
    public void editStage() {
        showLoading("");
        HashMap<String, Object> map = new HashMap<>();
        map.put("stageId", stageId);
        map.put("cId", selectId);
        if (null != loseResons) {
            map.put("loseReason", loseResons);
        }
        map.put("content", "销售阶段由\"" + mSaleDetails.getStageName() + "\"变更为\"" + stageName + "\"");
        LogUtil.d("编辑销售阶段:" + MainApp.gson.toJson(map));
        RestAdapterFactory.getInstance().build(Config_project.API_URL_CUSTOMER())
                .create(ISale.class)
                .editSaleStage(map, selectId, new RCallback<SaleProductEdit>() {
                    @Override
                    public void success(SaleProductEdit saleProductEdit, Response response) {
                        HttpErrorCheck.checkResponse("编辑销售阶段", response);
                        getData();
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        HttpErrorCheck.checkError(error);
                    }
                });
    }


    /**
     * 数据绑定
     */
    public void bindData() {
        //机会 是否 是创建者
        if (MainApp.user.id.equals(mSaleDetails.creatorId) && mSaleDetails.prob != 100) {
            img_title_right.setVisibility(View.VISIBLE);
            ll_stage.setEnabled(true);
            ll_product.setEnabled(true);
        } else {
            img_title_right.setVisibility(View.INVISIBLE);
            ll_stage.setEnabled(false);
            ll_product.setEnabled(false);
        }
        title.setText(mSaleDetails.getName());
        customer.setText(mSaleDetails.getCusName());
        salesAmount.setText("" + Utils.setValueDouble(mSaleDetails.estimatedAmount));
        estimatedAmount.setText(app.df4.format(new Date(Long.valueOf(mSaleDetails.estimatedTime + "") * 1000)));
        chanceType.setText(mSaleDetails.getChanceType());
        chanceSource.setText(mSaleDetails.getChanceSource());
        memo.setText(mSaleDetails.getMemo());
        if (null != mSaleDetails.getDirectorName() || !TextUtils.isEmpty(mSaleDetails.getDirectorName())) {
            director.setText(mSaleDetails.getDirectorName());
        } else {
            director.setText("无");
        }
        creator.setText(mSaleDetails.getCreatorName());
        creatorTime.setText(app.df3.format(new Date(Long.valueOf(mSaleDetails.getCreatedAt() + "") * 1000)));
        updateTime.setText(app.df3.format(new Date(Long.valueOf(mSaleDetails.getUpdatedAt() + "") * 1000)));
        winTime.setText(mSaleDetails.getWinTime() + "");
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
        if (0 != mSaleDetails.wfState) {//销售阶段是赢单的时候
            img_title_right.setVisibility(View.INVISIBLE);
            ll_product.setEnabled(false);
            ll_stage.setEnabled(false);
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
                    img_title_right.setVisibility(View.VISIBLE);
                    ll_product.setEnabled(true);
                    ll_stage.setEnabled(true);
                    isDelete = false;
                    break;
                case 4:
                    iv_wfstatus.setImageResource(R.drawable.img_wfinstance_status4);
                    break;
                case 5:
                    iv_wfstatus.setImageResource(R.drawable.img_task_status_finish);
                    break;
            }

        }
        //计算产品总金额
        if (null != mSaleDetails.proInfos) {
            for (SaleIntentionalProduct ele : mSaleDetails.proInfos) {
                totalMoney += ele.totalMoney * ele.quantity;
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.img_title_left:
                finish();
                break;
            //弹出菜单
            case R.id.img_title_right:
                Intent intent = new Intent(mContext, SaleEditViewActivity.class);
                intent.putExtra("isDelete", isDelete);
                startActivityForResult(intent, EDIT_POP_WINDOW);
                break;
            //意向产品
            case R.id.ll_product:
                Bundle product = new Bundle();
                product.putInt("data", ActionCode.SALE_FROM_DETAILS);
                product.putString("saleId", selectId);
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
                    LogUtil.dee("编辑成功回调");
                    getData();
                }
                break;

            /**菜单选项*/
            case EDIT_POP_WINDOW:
                //编辑回调
                if (data.getBooleanExtra("edit", false) && null != mSaleDetails) {
                    Bundle editSale = new Bundle();
                    editSale.putSerializable(ExtraAndResult.EXTRA_DATA, mSaleDetails);
                    app.startActivityForResult(SaleDetailsActivity.this, AddMySaleActivity.class,
                            MainApp.ENTER_TYPE_RIGHT, ExtraAndResult.MSG_WHAT_DIALOG, editSale);
                }
                //删除回调
                else if (data.getBooleanExtra("delete", false)) {
                    deleteSale();
                }
                break;
            /**意向产品*/
            case ExtraAndResult.REQUEST_CODE_PRODUCT:
                resultAction = data.getIntExtra(ExtraAndResult.STR_SELECT_TYPE, 0);
                if (resultAction == ActionCode.SALE_DETAILS_RUSH) {
                    getData();
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
                finish();
                break;

        }

    }

}
