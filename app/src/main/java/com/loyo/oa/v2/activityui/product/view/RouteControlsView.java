package com.loyo.oa.v2.activityui.product.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.loyo.oa.v2.R;

/**
 * Created by yyy on 16/12/22.
 */

public class RouteControlsView extends LinearLayout{

    View mView;
    Context mContext;

    public RouteControlsView(Context context) {
        super(context);
        mContext = context;
        bindView();
    }

    void bindView(){
        mView = LayoutInflater.from(mContext).inflate(R.layout.item_route_control,null);
        this.addView(mView);
    }

}
