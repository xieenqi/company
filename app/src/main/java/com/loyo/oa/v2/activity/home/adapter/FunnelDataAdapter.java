package com.loyo.oa.v2.activity.home.adapter;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activity.home.bean.HttpSalechance;
import com.loyo.oa.v2.tool.Utils;

/**
 * 添加 销售漏斗 数据
 * Created by xeq on 16/6/16.
 */
public class FunnelDataAdapter extends LinearLayout {
    private Context mContext;
    private int[] colors = {R.drawable.img_funnel0, R.drawable.img_funnel1, R.drawable.img_funnel2, R.drawable.img_funnel3,
            R.drawable.img_funnel4, R.drawable.img_funnel5, R.drawable.img_funnel6,
            R.drawable.img_funnel7, R.drawable.img_funnel8, R.drawable.img_funnel9,
    };
//    private int index;

    public FunnelDataAdapter(Context context, AttributeSet attrs, int defStyleAttr, int index, HttpSalechance data) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        setLayoutParams(new ViewGroup.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        setOrientation(VERTICAL);
        setPadding(0, 5, 0, 5);
        bindView(index, data);
    }

    public FunnelDataAdapter(Context context, int index, HttpSalechance data) {
        this(context, null, 0, index, data);
//        this.index = index;

    }

    private void bindView(int index, HttpSalechance data) {
        final View view = LayoutInflater.from(mContext).inflate(R.layout.item_funnel, null, false);
        TextView tv_number = (TextView) view.findViewById(R.id.tv_number);
        TextView tv_name = (TextView) view.findViewById(R.id.tv_name);
        ImageView iv_img = (ImageView) view.findViewById(R.id.iv_img);
        iv_img.setImageResource(colors[index]);
        tv_number.setText(data.stageName + "");
        tv_name.setText(Utils.setValueDouble2(data.totalNum) + "单 ￥" + Utils.setValueDouble(data.totalMoney));
        this.addView(view);
    }
}
