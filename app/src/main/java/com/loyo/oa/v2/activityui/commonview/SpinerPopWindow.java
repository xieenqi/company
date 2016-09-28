package com.loyo.oa.v2.activityui.commonview;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.loyo.oa.v2.R;


/**
 * SpinerPopWindow  spiner类似下弹窗
 *
 * @author enqi.xie
 */
public class SpinerPopWindow extends PopupWindow implements OnItemClickListener {

    private Context mContext;
    private ListView mListView;
    private View mShade;
    private BaseAdapter mAdapter;
    private IOnItemSelectListener mItemSelectListener;

    public SpinerPopWindow(Context context) {
        super(context);
        mContext = context;
        init();
    }

    public void setItemListener(IOnItemSelectListener listener) {
        mItemSelectListener = listener;
    }

    public void setAdatper(BaseAdapter adapter) {
        mAdapter = adapter;
        mListView.setAdapter(mAdapter);
    }

    public void showShade(boolean showShade) {
        mShade.setBackgroundColor(showShade ? 0x8c000000 : Color.TRANSPARENT);
    }

    private void init() {
        View view = LayoutInflater.from(mContext).inflate(R.layout.saleteam_screen_common, null);
        setContentView(view);
        setWidth(LayoutParams.WRAP_CONTENT);
        setHeight(LayoutParams.MATCH_PARENT);

        setFocusable(true);
        ColorDrawable dw = new ColorDrawable(0x00);
        setBackgroundDrawable(dw);

        mShade = view.findViewById(R.id.shade);
        mListView = (ListView) view.findViewById(R.id.saleteam_screencommon_lv);
        mListView.setOnItemClickListener(this);

        mShade.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    public <T> void refreshData(List<T> list, int selIndex) {
        if (list != null && selIndex != -1) {
            if (mAdapter != null) {
//                mAdapter.refreshData(list, selIndex);
            }
        }
    }


    /**
     * 设置list的背景
     *
     * @author enqi.xie
     */
    public void setListBaground(Drawable dd) {
        mListView.setBackgroundDrawable(dd);
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View view, int pos, long arg3) {
        dismiss();
        if (mItemSelectListener != null) {
            mItemSelectListener.onItemClick(pos);
        }
    }

    public interface IOnItemSelectListener {
        void onItemClick(int pos);
    }
}
