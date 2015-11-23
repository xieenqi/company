package com.loyo.oa.v2.activity;

import android.app.AlertDialog;
import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.adapter.ProductsRadioListViewAdapter;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.beans.CommonTag;
import com.loyo.oa.v2.beans.Customer;
import com.loyo.oa.v2.beans.Demand;
import com.loyo.oa.v2.beans.Product;
import com.loyo.oa.v2.beans.SaleStage;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.fragment.SaleStageDialogFragment;
import com.loyo.oa.v2.point.ICustomer;
import com.loyo.oa.v2.tool.BaseActivity;
import com.loyo.oa.v2.tool.Config_project;
import com.loyo.oa.v2.tool.RCallback;
import com.loyo.oa.v2.tool.RestAdapterFactory;
import com.loyo.oa.v2.tool.ViewUtil;

import java.util.ArrayList;
import java.util.HashMap;

import retrofit.client.Response;


public class DemandsAddActivity extends BaseActivity implements View.OnClickListener {
    ViewGroup img_title_left, img_title_right;

    private EditText edt_num;
    private EditText edt_price;
    private EditText edt_actualNum;
    private EditText edt_actualPrice;
    private EditText edt_memo;
    private ViewGroup layout_products;
    private ViewGroup layout_salestages;
    private ViewGroup layout_reason;
    private TextView tv_products;
    private TextView tv_salestages;
    private TextView tv_reason;

    private Customer customer;
    private ArrayList<Product> lstData_Product = new ArrayList<Product>();
    private ArrayList<SaleStage> lstData_SaleStage = new ArrayList<SaleStage>();
    private String lProductID_select;
    private AlertDialog dialog_Product;
    private SaleStageDialogFragment saleStageDialogFragment;
    private Demand demand;
    private ArrayList<CommonTag> loseResons = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demands_add);

        if (getIntent() != null) {
            Bundle bundle = getIntent().getExtras();
            if (bundle != null) {
                customer = (Customer) bundle.getSerializable(Customer.class.getName());

                if (bundle.containsKey(Demand.class.getName())) {
                    demand = (Demand) bundle.getSerializable(Demand.class.getName());
                    loseResons = demand.getLoseReason();
                }
            }
        }

        initUI();
        initDataUI();
        getData();
    }

    /**
     * 获取数据
     */
    private void getData() {
        getProducts();
        getSaleStages();
    }

    /**
     * 获取产品
     */
    private void getProducts() {
        RestAdapterFactory.getInstance().build(Config_project.API_URL_CUSTOMER()).create(ICustomer.class).getProducts(new RCallback<ArrayList<Product>>() {
            @Override
            public void success(ArrayList<Product> products, Response response) {
                lstData_Product.addAll(products);
            }
        });
    }

    /**
     * 获取销售阶段
     */
    private void getSaleStages() {
        RestAdapterFactory.getInstance().build(Config_project.API_URL_CUSTOMER()).create(ICustomer.class).getSaleStges(new RCallback<ArrayList<SaleStage>>() {
            @Override
            public void success(ArrayList<SaleStage> saleStages, Response response) {
                lstData_SaleStage.addAll(saleStages);
                if (saleStageDialogFragment == null) {
                    saleStageDialogFragment = new SaleStageDialogFragment(lstData_SaleStage, tv_salestages,layout_reason);

                    if (demand != null && demand.getSaleStage() != null) {
                        saleStageDialogFragment.lSaleStageID_select = demand.getSaleStage();
                        tv_salestages.setText(demand.getSaleStage().getName());
                    }
                }
            }
        });
    }

    private void initDataUI() {
        if (demand == null) {
            super.setTitle("新建购买意向");
            return;
        }
        super.setTitle("更新购买意向");

        lProductID_select = demand.getProduct().getId();
        tv_products.setText(demand.getProduct().getName());

        edt_num.setText(String.valueOf(demand.getEstimatedNum()));
        edt_price.setText(String.valueOf(demand.getEstimatedPrice()));

        edt_actualNum.setText(String.valueOf(demand.getActualNum()));
        edt_actualPrice.setText(String.valueOf(demand.getActualPrice()));

        edt_memo.setText(demand.getMemo());

        //赢单
        if (demand.getSaleStage() != null && demand.getSaleStage().getProb() == 100) {
            edt_actualNum.setText(String.valueOf(demand.getActualNum()));
            edt_actualPrice.setText(String.valueOf(demand.getActualPrice()));
        } else if (demand.getSaleStage() != null && demand.getSaleStage().getProb() == 0) {
            showReason(getLoseReason());
        }
    }

    /**
     * 获取输单原因
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

    /**
     * 获取输单原因的id集合
     *
     * @return
     */
    private String[] getLoseReasonIds() {
        if (null == loseResons || loseResons.isEmpty()) {
            return null;
        }
        String []reasons = new String[loseResons.size()];
        int index = 0;
        for (CommonTag reson : loseResons) {
            reasons[index]=reson.getId();
            index++;
        }
        return reasons;
    }

    /**
     * 显示输单原因
     *
     * @param reason
     */
    private void showReason(String reason) {
        layout_reason.setVisibility(View.VISIBLE);
        tv_reason.setText(reason);
    }

    private void initUI() {
        img_title_left = (ViewGroup) findViewById(R.id.img_title_left);
        img_title_left.setOnClickListener(this);
        img_title_left.setOnTouchListener(new ViewUtil.OnTouchListener_view_transparency());

        img_title_right = (ViewGroup) findViewById(R.id.img_title_right);
        img_title_right.setOnClickListener(this);
        img_title_right.setOnTouchListener(new ViewUtil.OnTouchListener_view_transparency());

        edt_num = (EditText) findViewById(R.id.edt_num);
        edt_price = (EditText) findViewById(R.id.edt_price);
        edt_actualNum = (EditText) findViewById(R.id.edt_actualNum);
        edt_actualPrice = (EditText) findViewById(R.id.edt_actualPrice);
        edt_memo = (EditText) findViewById(R.id.edt_demand_memo);

        layout_products = (ViewGroup) findViewById(R.id.layout_products);
        layout_reason = (ViewGroup) findViewById(R.id.layout_reason);
        layout_salestages = (ViewGroup) findViewById(R.id.layout_salestages);
        tv_products = (TextView) findViewById(R.id.tv_products);
        tv_salestages = (TextView) findViewById(R.id.tv_salestages);
        tv_reason = (TextView) findViewById(R.id.tv_reason);

        layout_reason.setOnClickListener(this);
        layout_reason.setOnTouchListener(new ViewUtil.OnTouchListener_view_transparency());
        layout_products.setOnClickListener(this);
        layout_products.setOnTouchListener(new ViewUtil.OnTouchListener_view_transparency());
        layout_salestages.setOnClickListener(this);
        layout_salestages.setOnTouchListener(new ViewUtil.OnTouchListener_view_transparency());
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.img_title_left:
                app.finishActivity(this, MainApp.ENTER_TYPE_LEFT, 0, null);
                break;
            case R.id.img_title_right:
                if (TextUtils.isEmpty(lProductID_select)) {
                    Toast("请选择产品");
                    return;
                }

                if (null == saleStageDialogFragment.lSaleStageID_select) {
                    Toast("请选择销售阶段");
                    return;
                }
                if (demand == null) {
                    addDemand();

                } else {
                    updateDemad();
                }

                break;

            case R.id.layout_products:
                if (lstData_Product == null) {
                    Toast(R.string.app_init_await);
                    break;
                }
                if (dialog_Product == null) {

                    LayoutInflater inflater = getLayoutInflater();
                    View layout = inflater.inflate(R.layout.dialog_products_select, null, false);
                    AlertDialog.Builder builder = new AlertDialog.Builder(this).setView(layout);
                    dialog_Product = builder.create();
                }
                dialog_Product.show();
                ListView listView_products = (ListView) dialog_Product.findViewById(R.id.listView);

                final ProductsRadioListViewAdapter productsRadioListViewAdapter = new ProductsRadioListViewAdapter(this, lstData_Product);
                listView_products.setAdapter(productsRadioListViewAdapter);
                listView_products.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                        productsRadioListViewAdapter.notifyDataSetChanged();
                        productsRadioListViewAdapter.isSelected = id;

                        lProductID_select = lstData_Product.get((int) id).getId();
                        tv_products.setText(lstData_Product.get((int) id).getName());

                        dialog_Product.dismiss();
                    }
                });
                break;
            case R.id.layout_reason:
                Bundle loseBundle=new Bundle();
                loseBundle.putSerializable("data",loseResons);
                loseBundle.putString("title", "输单原因");
                loseBundle.putInt("mode", CommonTagSelectActivity.SELECT_MODE_MULTIPLE);
                loseBundle.putInt("type", CommonTagSelectActivity.SELECT_TYPE_LOSE_REASON);
                app.startActivityForResult(this, CommonTagSelectActivity_.class, 0, CommonTagSelectActivity.REQUEST_TAGS,loseBundle);
                break;
            case R.id.layout_salestages:
                if (lstData_SaleStage == null) {
                    Toast(R.string.app_init_await);
                    break;
                }

                saleStageDialogFragment.setStyle(DialogFragment.STYLE_NO_TITLE, 0);
                saleStageDialogFragment.show(getFragmentManager(), SaleStageDialogFragment.class.getName());
                break;
        }
    }

    /**
     * 构建上传数据
     *
     * @param type : 1,新增;2,修改
     * @return
     */
    private HashMap<String, Object> buildDatas(int type) {
        double num = -1;
        try {
            num = Double.valueOf(edt_num.getText().toString().trim());
        } catch (Exception e) {
            Global.ProcException(e);
        }

        double price = -1;
        try {
            price = Double.valueOf(edt_price.getText().toString().trim());
        } catch (Exception e) {
            Global.ProcException(e);
        }

        double actualNum = -1;

        try {
            actualNum = Double.valueOf(edt_actualNum.getText().toString().trim());
        } catch (Exception e) {
            Global.ProcException(e);
        }

        double actualPrice = -1;

        try {
            actualPrice = Double.valueOf(edt_actualPrice.getText().toString().trim());
        } catch (Exception e) {
            Global.ProcException(e);
        }

        String memo = edt_memo.getText().toString();

        HashMap<String, Object> map = new HashMap<>();

        if (!TextUtils.isEmpty(lProductID_select)) {
            map.put("productId", lProductID_select);
        }

        if (saleStageDialogFragment != null && null != saleStageDialogFragment.lSaleStageID_select) {
            map.put("saleStage", saleStageDialogFragment.lSaleStageID_select);
        }

        if (num != -1) {
            map.put("estimatedNum", num);
        }

        if (price != -1) {
            map.put("estimatedPrice", price);
        }

        if (actualNum != -1) {
            map.put("actualNum", actualNum);
        }

        if (actualPrice != -1) {
            map.put("actualPrice", actualPrice);
        }

        if (!TextUtils.isEmpty(memo)) {
            map.put("memo", memo);
        }

        String [] reasons= getLoseReasonIds();
        if(null!=reasons&&reasons.length>0) {
            map.put("loseIds",reasons );
        }

        if (type == 2) {
            map.put("wfId", demand.getWfId());
            map.put("wfState", demand.getWfState());
        }else {
            map.put("customerId", customer.getId());
        }

        return map;
    }

    /**
     * 处理服务器的返回
     *
     * @param result
     */
    private void processResult(Demand result) {
        if (demand == null) {
            Toast(getString(R.string.app_add) + getString(R.string.app_succeed));
        } else {
            Toast("修改成功");
        }
        if (result != null) {
            Intent intent = new Intent();
            intent.putExtra(Demand.class.getName(), demand);
            app.finishActivity(DemandsAddActivity.this, MainApp.ENTER_TYPE_LEFT, RESULT_OK, intent);
        }
    }

    /**
     * 新增购买意向
     */
    private void addDemand() {
        RestAdapterFactory.getInstance().build(Config_project.API_URL_CUSTOMER()).create(ICustomer.class).addDemand(buildDatas(1), new RCallback<Demand>() {
            @Override
            public void success(Demand demand, Response response) {
                processResult(demand);
            }
        });
    }

    /**
     * 更新购买意向
     */
    private void updateDemad() {
        RestAdapterFactory.getInstance().build(Config_project.API_URL_CUSTOMER()).create(ICustomer.class).updateDemand(demand.getId(), buildDatas(2), new RCallback<Demand>() {
            @Override
            public void success(Demand demand, Response response) {
                processResult(demand);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(null==data||resultCode!=RESULT_OK){
            return;
        }
        switch (requestCode){
            case CommonTagSelectActivity.REQUEST_TAGS:
                loseResons=(ArrayList<CommonTag>)data.getSerializableExtra("data");
                tv_reason.setText(getLoseReason());
                break;
        }
    }
}
