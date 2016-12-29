package com.loyo.oa.v2.activityui.sale.presenter;

import com.loyo.oa.hud.progress.LoyoProgressHUD;
import com.loyo.oa.v2.activityui.customer.model.ContactLeftExtras;
import com.loyo.oa.v2.activityui.sale.bean.SaleStage;
import com.loyo.oa.v2.activityui.sale.contract.AddMySaleContract;
import com.loyo.oa.v2.activityui.sale.model.AddMySaleModelImpl;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by xeq on 2016/11/30
 */

public class AddMySalePresenterImpl implements AddMySaleContract.Presenter {

    private AddMySaleContract.View mView;
    private AddMySaleContract.Model model;

    public AddMySalePresenterImpl(AddMySaleContract.View mView) {
        this.mView = mView;
        model = new AddMySaleModelImpl(this);
    }

    public LoyoProgressHUD getHUD() {
        return mView.getHUD();
    }

    @Override
    public void getPageData(Object... pag) {

    }

    @Override
    public void bindPageData(Object obj) {

    }

    @Override
    public void sendPageData(Object obj) {

    }


    @Override
    public void getDynamic() {
        model.getDynamicInfo();
    }

    @Override
    public void setDynamic(ArrayList<ContactLeftExtras> dynamic) {
        boolean isProduct = false, isType = false, isSource = false, isEstimatedAmount = false,
                isEstimatedTime = false, isMemo = false;
        ArrayList<ContactLeftExtras> filedData = new ArrayList<>();
        for (ContactLeftExtras ele : dynamic) {
            if (!ele.isSystem) {
                filedData.add(ele);
            }
            if ("product".equals(ele.fieldName) && ele.required)
                isProduct = true;
            if ("chance_type".equals(ele.fieldName) && ele.required)
                isType = true;
            if ("chance_source".equals(ele.fieldName) && ele.required)
                isSource = true;
            if ("estimated_amount".equals(ele.fieldName) && ele.required)
                isEstimatedAmount = true;
            if ("estimated_time".equals(ele.fieldName) && ele.required)
                isEstimatedTime = true;
            if ("memo".equals(ele.fieldName) && ele.required)
                isMemo = true;
        }
        mView.setHintUI(isProduct, isType, isSource, isEstimatedAmount, isEstimatedTime, isMemo);
        mView.setDynamicUI(filedData);
    }

    @Override
    public void getStage() {
        model.getSaleStageData();
    }

    @Override
    public void setStage(ArrayList<SaleStage> saleStage) {
        if (saleStage != null && saleStage.size() > 0) {
            mView.setStageUI(saleStage.get(0).name, saleStage.get(0).id);
        }
    }

    @Override
    public void creatSale(HashMap<String, Object> map) {
        model.creatSaleSend(map);
    }

    @Override
    public void editSale(HashMap<String, Object> map, String chanceId) {
        model.editSaleSend(map, chanceId);
    }

    @Override
    public void creatSaleSuccess() {
        mView.creatSaleAction();
    }

    @Override
    public void editSaleSuccess() {
        mView.editSaleAction();
    }
}