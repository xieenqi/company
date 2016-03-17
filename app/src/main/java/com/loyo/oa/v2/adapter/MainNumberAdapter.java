package com.loyo.oa.v2.adapter;

import android.app.Activity;
import android.content.Context;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.beans.ClickItem;
import com.loyo.oa.v2.beans.HomeNumber;
import com.loyo.oa.v2.tool.commonadapter.CommonAdapter;
import com.loyo.oa.v2.tool.commonadapter.ViewHolder;
import com.loyo.oa.v2.tool.customview.RippleView;
import com.readystatesoftware.viewbadger.BadgeView;

import java.util.ArrayList;
import java.util.List;

public class MainNumberAdapter extends CommonAdapter<ClickItem> {
    MainApp app;
    ArrayList<HomeNumber> mNumbers;

    public MainNumberAdapter(final Context context, final List<ClickItem> datas, final ArrayList<HomeNumber> numbers) {
        super(context, datas, R.layout.item_main);
        mNumbers = numbers;
        app = MainApp.getMainApp();
    }

    @Override
    public void convert(final ViewHolder holder, final ClickItem item) {
        for (HomeNumber num : mNumbers) {
            if (("报告".equals(item.title) && "workreport".toLowerCase().equals(num.getBiz_name()))
                    || ("任务".equals(item.title) && "task".equals(num.getBiz_name().toLowerCase()))
                    || ("审批".equals(item.title) && "approval".equals(num.getBiz_name().toLowerCase()))) {

                BadgeView badge = holder.getView(R.id.view_number);

                if (num.getNumber() > 0) {
                    badge.setText(String.valueOf(num.getNumber()));
                    badge.show();
                } else {
                    badge.hide();
                }
            }
        }

        RippleView rv = holder.getView(R.id.layout_item);
        rv.setRippleDuration(100);
        rv.setRippleColor(R.color.title_bg1);
        rv.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(final RippleView rippleView) {
                //前三项需要刷新小红点数字
                if (holder.getPosition() < 3) {
                    app.startActivityForResult((Activity) mContext, item.cls, MainApp.ENTER_TYPE_RIGHT, 1, null);
                } else {
                    app.startActivity((Activity) mContext, item.cls, MainApp.ENTER_TYPE_RIGHT, false, null);
                }
            }
        });

        holder.setImageResId(R.id.img_item, item.imageViewRes)
                .setText(R.id.tv_item, item.title);
    }

    public void setNumbers(final ArrayList<HomeNumber> numbers) {
        mNumbers = numbers;
    }
}
