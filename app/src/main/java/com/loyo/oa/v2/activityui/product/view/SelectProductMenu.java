package com.loyo.oa.v2.activityui.product.view;

import android.content.Context;
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

    public SelectProductMenu(Context mContext, SelectProMenuView selectProMenuView) {
        this.mContext = mContext;
        viewCrol = selectProMenuView;
        initUI();
    }

    void initUI() {
        classifySeletorView = new ClassifySeletorView(mContext);
        this.setContentView(classifySeletorView);
        this.setAnimationStyle(R.style.SelectProductViewAnim);
        this.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        this.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        this.setOutsideTouchable(true);
    }

    // show view
    public void showPopupWindow(final View parent, final ClassifySeletorView.SeletorListener listener) {
        if (!isShow) {
            isShow = true;
            if (null == data) {
                ProductService.getProductClassify().subscribe(new DefaultLoyoSubscriber<List<ClassifySeletorItem>>(LoyoErrorChecker.TOAST) {
                    @Override
                    public void onNext(List<ClassifySeletorItem> classifySeletorItems) {
                        data = classifySeletorItems;
                        classifySeletorView.setup(classifySeletorItems, listener);
                        //单选，不能放在上面，没有setup，不可以设置。
                        classifySeletorView.setSingleSelete(true);
                        SelectProductMenu.this.showAsDropDown(parent);
                        viewCrol.popWindowShowEmbl();
                    }
                });
            } else {
                this.showAsDropDown(parent);
                viewCrol.popWindowShowEmbl();
            }

        } else {
            isShow = false;
            this.dismiss();
            viewCrol.popWindowDimsEmbl();
        }
    }

}
