package com.loyo.oa.v2.activity.sale;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activity.sale.bean.SaleDetails;
import com.loyo.oa.v2.activity.sale.bean.SaleIntentionalProduct;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.common.ExtraAndResult;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.common.http.HttpErrorCheck;
import com.loyo.oa.v2.point.ISale;
import com.loyo.oa.v2.tool.BaseActivity;
import com.loyo.oa.v2.tool.Config_project;
import com.loyo.oa.v2.tool.RCallback;
import com.loyo.oa.v2.tool.RestAdapterFactory;
import com.loyo.oa.v2.tool.customview.ViewSaleDetailsExtra;

import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * 机会详情
 * Created by yyy on 16/5/19.
 */
public class ActivitySaleDetails extends BaseActivity implements View.OnClickListener{

    private final int EDIT_POP_WINDOW = 500;
    private LinearLayout img_title_left;
    private LinearLayout layout_losereson;
    private RelativeLayout img_title_right;
    private SaleDetails mSaleDetails;
    private Intent mIntent;
    private String selectId;
    private StringBuffer productBuffer = new StringBuffer();

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
    private TextView stageName;
    private TextView product;

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
        stageName = (TextView) findViewById(R.id.text_stagename);
        product = (TextView) findViewById(R.id.text_product);
        ll_product = (LinearLayout) findViewById(R.id.ll_product);
        ll_stage = (LinearLayout) findViewById(R.id.ll_stage);
        ll_extra = (LinearLayout) findViewById(R.id.ll_extra);

        img_title_left.setOnClickListener(this);
        img_title_right.setOnClickListener(this);
        ll_product.setOnClickListener(this);
        ll_stage.setOnClickListener(this);
        ll_stage.setOnTouchListener(Global.GetTouch());
        ll_product.setOnTouchListener(Global.GetTouch());
        img_title_right.setOnTouchListener(Global.GetTouch());
        img_title_left.setOnTouchListener(Global.GetTouch());

        mIntent = getIntent();
        selectId = mIntent.getStringExtra("id");
        getData();
    }

    /**
     * 获取销售机会详情
     * */
    public void getData(){
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
                    }
                });
    }

    /**
     * 数据绑定
     * */
    public void bindData(){
        title.setText(mSaleDetails.getName());
        customer.setText(mSaleDetails.getCustomerName());
        salesAmount.setText(mSaleDetails.getSalesAmount()+"");
        estimatedAmount.setText(mSaleDetails.getEstimatedAmount()+"");
        chanceType.setText(mSaleDetails.getChanceType());
        chanceSource.setText(mSaleDetails.getChanceSource());
        memo.setText(mSaleDetails.getMemo());
        creator.setText(mSaleDetails.getCreatorName());
        creatorTime.setText(mSaleDetails.getCreatedAt()+"");
        updateTime.setText(mSaleDetails.getUpdatedAt()+"");
        winTime.setText(mSaleDetails.getWinTime()+"");
        stageName.setText(mSaleDetails.getStageName());
        if(null != mSaleDetails.getProInfos()){
            for(SaleIntentionalProduct sitpeoduct : mSaleDetails.getProInfos()){
                productBuffer.append(sitpeoduct.name+" ");
            }
        }
        product.setText(productBuffer.toString());
        ll_extra.setVisibility(View.VISIBLE);
        for(SaleDetails.SaleDetailsExtraList saleDetailsExtraList : mSaleDetails.getExtensionDatas()){
            ll_extra.addView(new ViewSaleDetailsExtra(mContext,saleDetailsExtraList));
        }

        /*当为输单阶段时，显示输单原因*/
        if(mSaleDetails.getProb() == 0){
            layout_losereson.setVisibility(View.VISIBLE);
            losereason.setText(mSaleDetails.getLostReason());
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
                Intent intent = new Intent(mContext, ActivitySaleEditView.class);
                startActivityForResult(intent,EDIT_POP_WINDOW);
                break;
            //意向产品
            case R.id.ll_product:
                Bundle product = new Bundle();
                product.putSerializable(ExtraAndResult.EXTRA_DATA, mSaleDetails.getProInfos());
                app.startActivityForResult(ActivitySaleDetails.this, ActivityIntentionProduct.class,
                        MainApp.ENTER_TYPE_RIGHT, ExtraAndResult.REQUEST_CODE_PRODUCT, product);
                break;
            //销售阶段
            case R.id.ll_stage:
                Bundle stage = new Bundle();
                stage.putInt(ExtraAndResult.EXTRA_TYPE, ActivitySaleStage.SALE_STAGE);
                stage.putString(ExtraAndResult.EXTRA_NAME, "销售阶段");
                app.startActivityForResult(ActivitySaleDetails.this, ActivitySaleStage.class,
                        MainApp.ENTER_TYPE_RIGHT, ExtraAndResult.REQUEST_CODE_STAGE, stage);
                break;

            default:
                break;

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode != RESULT_OK){
            return;
        }

        if(requestCode == EDIT_POP_WINDOW){
            if(data.getBooleanExtra("edit",false)){
                Toast("编辑回调");
            }else if(data.getBooleanExtra("delete",false)){
                Toast("删除回调");
            }
        }
    }
}
