package com.loyo.oa.v2.order.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.product.SelectProductActivity;
import com.loyo.oa.v2.activityui.product.api.ProductService;
import com.loyo.oa.v2.activityui.product.event.SelectProductEvent;
import com.loyo.oa.v2.activityui.product.model.ProductDetails;
import com.loyo.oa.v2.activityui.sale.bean.SaleIntentionalProduct;
import com.loyo.oa.v2.customview.CustomTextView;
import com.loyo.oa.v2.network.DefaultLoyoSubscriber;
import com.loyo.oa.v2.order.activity.ActivityFragmentsStackManager;
import com.loyo.oa.v2.order.adapter.AddProductAdapter;
import com.loyo.oa.v2.order.cell.ProductAddBaseCell;
import com.loyo.oa.v2.order.common.ProductDealValidator;
import com.loyo.oa.v2.order.model.ProductDeal;
import com.loyo.oa.v2.tool.BaseFragment;
import com.loyo.oa.v2.tool.StringUtil;
import com.loyo.oa.v2.tool.Utils;

import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.pedant.SweetAlert.SweetAlertDialog;


/**
 * Created by EthanGong on 2017/2/28.
 */

public class AddProductsFragment extends BaseFragment implements ProductAddBaseCell.ActionListener{

    public interface ProductPickerCallback {
        void onProductPicked(ArrayList<SaleIntentionalProduct> data, String dealTotal);
    }

    public ProductPickerCallback callback;

    View view;
    ActivityFragmentsStackManager manager;
    AddProductAdapter adapter;
    String uuid = StringUtil.getUUID();
    int currentPickIndex = -1;

    @BindView(R.id.img_title_left)  ViewGroup backButton;
    @BindView(R.id.img_title_right) ViewGroup rightButton;
    @BindView(R.id.tv_title_1) TextView titleView;
    @BindView(R.id.recycle_view) RecyclerView recyclerView;

    @BindView(R.id.tv_saleToal) CustomTextView totalText;
    @BindView(R.id.tv_discount) CustomTextView discountText;

    @OnClick(R.id.img_title_left) void onBackPressed() {

        ArrayList<ProductDeal> list = adapter.data;
        boolean hasUnsavedData = false;
        for (ProductDeal deal : list) {
            if (!deal.isEmpty()) {
                hasUnsavedData = true;
                break;
            }
        }
        if (hasUnsavedData) {
            sweetAlertDialogView.alertHandle(new SweetAlertDialog.OnSweetClickListener() {
                @Override
                public void onClick(SweetAlertDialog sweetAlertDialog) {
                    sweetAlertDialog.dismissWithAnimation();
                }
            }, new SweetAlertDialog.OnSweetClickListener() {
                @Override
                public void onClick(SweetAlertDialog sweetAlertDialog) {
                    sweetAlertDialog.dismiss();
                    manager.pop();
                }
            }, "提示", "数据未保存，确定要返回?");
        }
        else {
            this.manager.pop();
        }
    }

    @OnClick(R.id.img_title_right) void onCommit() {
        ArrayList<ProductDeal> list = adapter.data;
        ArrayList<SaleIntentionalProduct> commitData = new ArrayList<>();
        boolean canCommit = true;
        int i = 1;
        for (ProductDeal deal : list) {
            canCommit = ProductDealValidator.validateAndToast(deal, "产品"+i);
            if (canCommit) {
                commitData.add(deal.toSaleIntentionalProduct());
            }
            else {
                break;
            }
            i++;
        }
        if (!canCommit) {
            return;
        }
        if (commitData.size() == 0) {
            Toast("请填写购买产品");
        }
        else {
            if (callback != null) {
                callback.onProductPicked(commitData, totalText.getText().toString());
            }
            this.manager.pop();
        }
    }

    public AddProductsFragment(ActivityFragmentsStackManager manager) {
        this.manager = manager;
        adapter = new AddProductAdapter(this);
        adapter.callback = new AddProductAdapter.ListChangeCallback() {
            @Override
            public void onListChange(double totalMoney, double totalDiscount) {
                totalText.setText(Utils.setValueDouble(totalMoney));
                discountText.setText(Utils.setValueDouble(totalDiscount*100)+"%");
            }
        };
    }

    private AddProductsFragment() {
        adapter = new AddProductAdapter(this);
    }

    public void setData(ArrayList<SaleIntentionalProduct> data) {
        if (data == null) {
            return;
        }
        ArrayList<ProductDeal> list = new ArrayList<>();
        for (SaleIntentionalProduct product : data) {
            list.add(new ProductDeal(product));
        }
        adapter.setData(list);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_add_products, container, false);
            ButterKnife.bind(this, view);
            setup();
        }
        return view;
    }

    private void setup() {
        titleView.setText("新增购买产品");
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
        adapter.fireListChange();
    }

    /**
     * ProductAddCell.ActionListener
     */

    public void deleteProductAtIndex(final int index) {

        ProductDeal deal = adapter.data.get(index);
        if (deal.isEmpty()) {
            adapter.data.remove(index);
            adapter.notifyDataSetChanged();
            return;
        }
        sweetAlertDialogView.alertHandle(new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sweetAlertDialog) {
                sweetAlertDialog.dismissWithAnimation();
            }
        }, new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sweetAlertDialog) {
                sweetAlertDialog.dismiss();
                adapter.data.remove(index);
                adapter.notifyDataSetChanged();
            }
        }, "提示", "你确定要删除吗？");
    }

    public void pickProductForIndex(int index) {
        currentPickIndex = index;
        Intent mIntent = new Intent(this.getActivity(), SelectProductActivity.class);
        mIntent.putExtra(SelectProductActivity.KEY_SESSION, uuid);
        startActivity(mIntent);
    }

    public void addProduct() {
        adapter.data.add(new ProductDeal());
        adapter.notifyDataSetChanged();
    }

    @Override
    public void toast(String msg) {
        Toast(msg);
    }

    /**
     * 选择的产品 回调查询详情
     * */
    @Subscribe
    public void selectProductCallBack(SelectProductEvent event){
        if (!uuid.equals(event.session)) {
            return;
        }
        Bundle mBundle = event.bundle;
        String productId = mBundle.getString("id");
        boolean stockEnabled = mBundle.getBoolean("enable");
        showLoading2("");
        getProductDetail(productId, stockEnabled);
    }

    /**
     * 获取产品详情
     * */
    public void getProductDetail(String id, final boolean stockEnabled) {
        ProductService.getProductDetails(id)
                .subscribe(new DefaultLoyoSubscriber<ProductDetails>(hud) {
                    @Override
                    public void onNext(ProductDetails details) {
                        adapter.data.get(currentPickIndex).product = details;
                        adapter.data.get(currentPickIndex).stockEnabled = stockEnabled;
                        adapter.notifyItemChanged(currentPickIndex);
                    }
                });
    }

}
