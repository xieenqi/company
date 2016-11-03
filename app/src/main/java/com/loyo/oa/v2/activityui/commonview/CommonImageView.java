package com.loyo.oa.v2.activityui.commonview;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.other.PreviewImagefromHttp;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.common.ExtraAndResult;
import com.loyo.oa.v2.common.FinalVariables;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * Created by xeq on 16/11/3.
 */

public class CommonImageView extends LinearLayout {
    public CommonImageView(final Context context, final String ele) {
        super(context);
        this.removeAllViews();
        ImageView img = (ImageView) LayoutInflater.from(context).inflate(R.layout.item_imagview, null);
        ImageLoader.getInstance().displayImage(ele, img);
        img.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putSerializable(ExtraAndResult.EXTRA_OBJ, ele.replace("_thumb", ""));
                MainApp.getMainApp().startActivityForResult((Activity) context, PreviewImagefromHttp.class,
                        MainApp.ENTER_TYPE_RIGHT, FinalVariables.REQUEST_DEAL_ATTACHMENT, bundle);
            }
        });
        this.addView(img);
        this.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams pl = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        this.setLayoutParams(pl);
    }

    public CommonImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CommonImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
}
