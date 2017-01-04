package com.loyo.oa.v2.activityui.product.view;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.library.module.widget.loading.LoadingLayout;
import com.loyo.oa.common.utils.DensityUtil;
import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.product.adapter.SelectProductMenuAdapter;
import com.loyo.oa.v2.activityui.product.api.ProductService;
import com.loyo.oa.v2.activityui.product.viewcontrol.SelectProMenuView;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.customview.classify_seletor.ClassifySeletorItem;
import com.loyo.oa.v2.customview.classify_seletor.ClassifySeletorView;
import com.loyo.oa.v2.customview.classify_seletor.ItemAdapter;
import com.loyo.oa.v2.network.DefaultLoyoSubscriber;
import com.loyo.oa.v2.network.LoyoErrorChecker;

import java.util.List;

/**
 * Created by yyy on 16/12/22.
 */

public class SelectProductMenu extends PopupWindow  {

    private Context mContext;
    private boolean isShow = false;
    private TextView tv_cancel, tv_commit;
    private ListView listview;
    private LinearLayout layout_hs;

    SelectProductMenuAdapter mAdapter;
    SelectProMenuView viewCrol;


    private List<ClassifySeletorItem> data;
    private ClassifySeletorView classifySeletorView;
    private LoadingLayout ll_layout;
    private ClassifySeletorView.SeletorListener listener;
    public SelectProductMenu(Context mContext, SelectProMenuView selectProMenuView) {
        this.mContext = mContext;
        viewCrol = selectProMenuView;
        initUI();
    }

    void initUI() {
        //加载的ui
        classifySeletorView = new ClassifySeletorView(mContext);
        ll_layout=new LoadingLayout(mContext);
        ll_layout.setBackgroundColor(Color.WHITE);
        ll_layout.addView(classifySeletorView);
        ll_layout.init();
        //添加下边距,避免文字贴边
        ll_layout.setPadding(ll_layout.getLeft(),ll_layout.getPaddingTop()+ DensityUtil.dp2px(mContext,30),ll_layout.getPaddingRight(),ll_layout.getPaddingBottom()+ DensityUtil.dp2px(mContext,30));
        ll_layout.setStatus(LoadingLayout.Success);
        ll_layout.setOnReloadListener(new LoadingLayout.OnReloadListener() {
            @Override
            public void onReload(View v) {
                loadData();
            }
        });
        //设置popwin
        this.setContentView(ll_layout);
        this.setAnimationStyle(R.style.SelectProductViewAnim);
        this.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        this.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        this.setOutsideTouchable(true);
    }

    //加载数据
    private void loadData(){
        ll_layout.setStatus(LoadingLayout.Loading);
        ProductService.getProductClassify().subscribe(new DefaultLoyoSubscriber<List<ClassifySeletorItem>>(ll_layout) {
            @Override
            public void onNext(List<ClassifySeletorItem> classifySeletorItems) {
                ll_layout.setStatus(LoadingLayout.Success);
                data = classifySeletorItems;
                classifySeletorView.setup(classifySeletorItems, listener);
                //单选，不能放在上面，没有setup，不可以设置。
                classifySeletorView.setSingleSelete(true);
                //把边距减回来，避免太大空隙
                ll_layout.setPadding(ll_layout.getLeft(),ll_layout.getPaddingTop()- DensityUtil.dp2px(mContext,30),ll_layout.getPaddingRight(),ll_layout.getPaddingBottom()- DensityUtil.dp2px(mContext,30));

            }
        });
    }
    // show view
    public void showPopupWindow(final View parent, final ClassifySeletorView.SeletorListener listener) {
        if (!isShow) {
            isShow = true;
            if (null == data) {
                this.listener=listener;
                loadData();
            }
            SelectProductMenu.this.showAsDropDown(parent);
            viewCrol.popWindowShowEmbl();
        } else {
            isShow = false;
            this.dismiss();
            viewCrol.popWindowDimsEmbl();
        }
    }

}
