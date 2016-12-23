package com.loyo.oa.v2.activityui.product.view;

import android.content.Context;
import android.os.Handler;
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
import com.loyo.oa.v2.activityui.product.viewcontrol.SelectProMenuView;
import com.loyo.oa.v2.common.Global;

/**
 * Created by yyy on 16/12/22.
 */

public class SelectProductMenu extends PopupWindow implements View.OnClickListener{

    private Context mContext;
    private boolean isShow = false;
    private TextView tv_cancel,tv_commit;
    private ListView listview;
    private LinearLayout layout_hs;

    SelectProductMenuAdapter mAdapter;
    SelectProMenuView viewCrol;

    public SelectProductMenu(Context mContext,SelectProMenuView selectProMenuView){
        this.mContext = mContext;
        viewCrol = selectProMenuView;
        initUI();
    }

    void initUI(){
        View view = LayoutInflater.from(mContext).inflate(R.layout.view_selectproduct,null);
        this.setContentView(view);
        this.setAnimationStyle(R.style.SelectProductViewAnim);
        this.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        this.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        this.setOutsideTouchable(true);

        tv_cancel = (TextView) view.findViewById(R.id.tv_cancel);
        tv_commit = (TextView) view.findViewById(R.id.tv_commit);
        listview = (ListView)  view.findViewById(R.id.listview);
        layout_hs = (LinearLayout) view.findViewById(R.id.layout_hs);


        tv_cancel.setOnClickListener(this);
        tv_commit.setOnClickListener(this);
        tv_cancel.setOnTouchListener(Global.GetTouch());
        tv_commit.setOnTouchListener(Global.GetTouch());

        for(int i = 0;i<5;i++){
            layout_hs.addView(new RouteControlsView(mContext));
        }

        this.setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss() {
                viewCrol.popWindowDimsEmbl();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        isShow = false;
                    }
                },500);
            }
        });

        mAdapter = new SelectProductMenuAdapter(mContext);
        listview.setAdapter(mAdapter);
    }

    // show view
    public void showPopupWindow(View parent) {
        if (!isShow) {
            isShow = true;
            this.showAsDropDown(parent);
            viewCrol.popWindowShowEmbl();
        }else{
            this.dismiss();
        }
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){

            // 确定
            case R.id.tv_commit:

                break;

            // 取消
            case R.id.tv_cancel:

                break;

        }
    }
}
