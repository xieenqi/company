package com.loyo.oa.v2.activity.sale;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activity.sale.bean.SaleDetails;
import com.loyo.oa.v2.common.http.HttpErrorCheck;
import com.loyo.oa.v2.point.ISale;
import com.loyo.oa.v2.tool.BaseActivity;
import com.loyo.oa.v2.tool.Config_project;
import com.loyo.oa.v2.tool.LogUtil;
import com.loyo.oa.v2.tool.RCallback;
import com.loyo.oa.v2.tool.RestAdapterFactory;
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

        img_title_left.setOnClickListener(this);
        img_title_right.setOnClickListener(this);

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

            case R.id.img_title_right:
                Intent intent = new Intent(mContext, ActivitySaleEditView.class);
                startActivityForResult(intent,EDIT_POP_WINDOW);
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
