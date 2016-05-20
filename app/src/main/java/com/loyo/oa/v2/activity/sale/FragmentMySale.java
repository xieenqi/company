package com.loyo.oa.v2.activity.sale;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activity.sale.adapter.AdapterMySaleList;
import com.loyo.oa.v2.activity.sale.bean.SaleMyList;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.common.FinalVariables;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.common.http.HttpErrorCheck;
import com.loyo.oa.v2.point.ISale;
import com.loyo.oa.v2.tool.BaseFragment;
import com.loyo.oa.v2.tool.Config_project;
import com.loyo.oa.v2.tool.RCallback;
import com.loyo.oa.v2.tool.RestAdapterFactory;
import com.loyo.oa.v2.tool.customview.SaleCommPopupView;
import com.loyo.oa.v2.tool.customview.pullToRefresh.PullToRefreshBase;
import com.loyo.oa.v2.tool.customview.pullToRefresh.PullToRefreshListView;

import java.util.ArrayList;
import java.util.HashMap;

import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * 【我的销售机会】
 * Created by yyy on 16/5/17.
 */
public class FragmentMySale extends BaseFragment implements PullToRefreshBase.OnRefreshListener2{

    private View mView;
    private Button btn_add;
    private LinearLayout screen1;
    private LinearLayout screen2;
    private ViewStub emptyView;
    private ImageView tagImage1;
    private ImageView tagImage2;
    private SaleCommPopupView saleCommPopupView;
    private WindowManager.LayoutParams params;
    private PullToRefreshListView listView;
    private AdapterMySaleList adapter;
    private SaleMyList mSaleMyList;
    private int requestPage = 1;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg){

            switch (msg.what){

                case FragmentTeamSale.SALETEAM_SCREEN_TAG2:
                    Toast(msg.getData().getString("data"));
                    break;

                case FragmentTeamSale.SALETEAM_SCREEN_TAG3:
                    Toast(msg.getData().getString("data"));
                    break;

            }

        }
    };

    @Override
    public void onResume() {
        super.onResume();
        getData();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (null == mView) {
            mView = inflater.inflate(R.layout.fragment_my_sale, null);
            initView(mView);
        }
        return mView;
    }

    private void initView(View view) {
        listView = (PullToRefreshListView) view.findViewById(R.id.lv_list);
        btn_add = (Button) view.findViewById(R.id.btn_add);
        screen1 = (LinearLayout) view.findViewById(R.id.salemy_screen1);
        screen2 = (LinearLayout) view.findViewById(R.id.salemy_screen2);
        emptyView = (ViewStub) view.findViewById(R.id.vs_nodata);
        tagImage1 = (ImageView) view.findViewById(R.id.salemy_screen1_iv1);
        tagImage2 = (ImageView) view.findViewById(R.id.salemy_screen1_iv2);

        btn_add.setOnTouchListener(Global.GetTouch());
        btn_add.setOnClickListener(click);
        screen1.setOnClickListener(click);
        screen2.setOnClickListener(click);
        listView.setMode(PullToRefreshBase.Mode.BOTH);
        listView.setOnRefreshListener(this);
        listView.setEmptyView(emptyView);


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                app.startActivity(getActivity(), ActivitySaleDetails.class, MainApp.ENTER_TYPE_RIGHT, false, null);
            }
        });
    }

    /**
     * 绑定数据
     * */
    public void bindData(){
        if(null == adapter){
            adapter = new AdapterMySaleList(getActivity(),mSaleMyList.records);
            listView.setAdapter(adapter);
        }else{
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onPullDownToRefresh(PullToRefreshBase refreshView) {
        requestPage = 1;
        getData();
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase refreshView) {
        requestPage++;
        getData();
    }

    /**
     * 获取 我的机会列表
     * */
    public void getData(){
        HashMap<String,Object> params = new HashMap<>();
        params.put("pageIndex",requestPage);
        params.put("pageSize",1);
        params.put("stageId","");
        params.put("type","");

        RestAdapterFactory.getInstance().build(Config_project.API_URL_CUSTOMER()).create(ISale.class).getSaleMyList(params, new RCallback<SaleMyList>() {
            @Override
            public void success(SaleMyList saleMyLists, Response response) {
                HttpErrorCheck.checkResponse("客户列表", response);
                if(null == saleMyLists.records || saleMyLists.records.size() == 0){
                    Toast("没有更多数据了!");
                    listView.onRefreshComplete();
                    return;
                }else{
                    mSaleMyList = saleMyLists;
                }
                bindData();
                listView.onRefreshComplete();
            }

            @Override
            public void failure(RetrofitError error) {
                HttpErrorCheck.checkError(error);
                listView.onRefreshComplete();
            }
        });
    }

    /**
     * PopupWindow关闭 恢复背景正常颜色
     * */
    private void closePopupWindow(ImageView view)
    {
        params = getActivity().getWindow().getAttributes();
        params.alpha = 1f;
        getActivity().getWindow().setAttributes(params);
        view.setBackgroundResource(R.drawable.arrow_down);
    }

    /**
     * PopupWindow打开，背景变暗
     * */
    private void openPopWindow(ImageView view){
        params = getActivity().getWindow().getAttributes();
        params.alpha=0.95f;
        getActivity().getWindow().setAttributes(params);
        view.setBackgroundResource(R.drawable.arrow_up);
    }


    private View.OnClickListener click = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {

                //新建机会
                case R.id.btn_add:
                    MainApp.getMainApp().startActivityForResult(mActivity, ActivityAddMySale.class,
                            MainApp.ENTER_TYPE_RIGHT, FinalVariables.REQUEST_CREATE_LEGWORK, null);
                    break;


                //销售阶段
                case R.id.salemy_screen1:
                    saleCommPopupView = new SaleCommPopupView(getActivity(),mHandler,1);
                    saleCommPopupView.showAsDropDown(screen1);
                    openPopWindow(tagImage1);
                    saleCommPopupView.setOnDismissListener(new PopupWindow.OnDismissListener() {
                        @Override
                        public void onDismiss() {
                            closePopupWindow(tagImage1);
                        }
                    });
                    break;

                //排序
                case R.id.salemy_screen2:
                    saleCommPopupView = new SaleCommPopupView(getActivity(),mHandler,2);
                    saleCommPopupView.showAsDropDown(screen2);
                    openPopWindow(tagImage2);
                    saleCommPopupView.setOnDismissListener(new PopupWindow.OnDismissListener() {
                        @Override
                        public void onDismiss() {
                            closePopupWindow(tagImage2);
                        }
                    });

                    break;

                default:
                    break;

            }
        }
    };
}
