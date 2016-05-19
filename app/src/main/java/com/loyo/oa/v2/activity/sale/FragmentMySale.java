package com.loyo.oa.v2.activity.sale;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activity.sale.adapter.AdapterMySaleList;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.common.FinalVariables;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.tool.BaseFragment;
import com.loyo.oa.v2.tool.customview.SaleCommPopupView;
import com.loyo.oa.v2.tool.customview.pullToRefresh.PullToRefreshListView;

/**
 * 【我的销售机会】
 * Created by yyy on 16/5/17.
 */
public class FragmentMySale extends BaseFragment {

    private View mView;
    private Button btn_add;
    private LinearLayout screen1;
    private LinearLayout screen2;
    private ImageView tagImage1;
    private ImageView tagImage2;

    private SaleCommPopupView saleCommPopupView;
    private WindowManager.LayoutParams params;
    private PullToRefreshListView lv_list;
    private AdapterMySaleList adapter;

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
        lv_list = (PullToRefreshListView) view.findViewById(R.id.lv_list);
        btn_add = (Button) view.findViewById(R.id.btn_add);
        screen1 = (LinearLayout) view.findViewById(R.id.salemy_screen1);
        screen2 = (LinearLayout) view.findViewById(R.id.salemy_screen2);
        tagImage1 = (ImageView) view.findViewById(R.id.salemy_screen1_iv1);
        tagImage2 = (ImageView) view.findViewById(R.id.salemy_screen1_iv2);

        btn_add.setOnTouchListener(Global.GetTouch());
        btn_add.setOnClickListener(click);
        screen1.setOnClickListener(click);
        screen2.setOnClickListener(click);

        adapter = new AdapterMySaleList(getActivity());
        lv_list.setAdapter(adapter);

        lv_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                app.startActivity(getActivity(),ActivitySaleDetails.class,MainApp.ENTER_TYPE_RIGHT,false,null);
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
