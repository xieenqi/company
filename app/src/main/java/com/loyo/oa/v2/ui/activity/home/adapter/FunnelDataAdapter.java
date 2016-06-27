package com.loyo.oa.v2.ui.activity.home.adapter;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.ui.activity.home.bean.HttpSalechance;
import com.loyo.oa.v2.tool.Utils;

/**
 * 添加 销售漏斗 数据
 * Created by xeq on 16/6/16.
 */
public class FunnelDataAdapter extends LinearLayout {
    private Context mContext;
    private int[] colors = {R.drawable.img_funne0, R.drawable.img_funne1, R.drawable.img_funne2, R.drawable.img_funne3,
            R.drawable.img_funne4, R.drawable.img_funne5, R.drawable.img_funne6,
            R.drawable.img_funne7, R.drawable.img_funne8, R.drawable.img_funne9};
    private int[] bjColors = {R.color.progress09, R.color.progress08, R.color.progress07, R.color.progress06,
            R.color.progress05, R.color.progress04, R.color.progress03,
            R.color.progress02, R.color.progress01, R.color.progress00};

    public FunnelDataAdapter(Context context, AttributeSet attrs, int defStyleAttr, int index, HttpSalechance data, int bjIndex) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        setLayoutParams(new ViewGroup.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        setOrientation(VERTICAL);
        setPadding(0, 5, 0, 5);
        bindView(index, data, bjIndex);
    }

    public FunnelDataAdapter(Context context, int index, HttpSalechance data, int bjIndex) {
        this(context, null, 0, index, data, bjIndex);

    }

    private void bindView(int index, HttpSalechance data, int bjIndex) {
        final View view = LayoutInflater.from(mContext).inflate(R.layout.item_funnel, null, false);
        TextView tv_number = (TextView) view.findViewById(R.id.tv_number);
        TextView tv_name = (TextView) view.findViewById(R.id.tv_name);
        ImageView iv_img = (ImageView) view.findViewById(R.id.iv_img);
        iv_img.setImageResource(colors[index]);
        iv_img.setBackgroundResource(bjColors[bjIndex]);
        tv_number.setText(data.stageName + "");
        tv_name.setText(Utils.setValueDouble2(data.totalNum) + "单 ￥" + Utils.setValueDouble(data.totalMoney));
        this.addView(view);
    }
}
