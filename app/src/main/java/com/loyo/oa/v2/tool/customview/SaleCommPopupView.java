package com.loyo.oa.v2.tool.customview;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activity.sale.ActivitySaleOpportunitiesManager;
import com.loyo.oa.v2.activity.sale.FragmentMySale;
import com.loyo.oa.v2.activity.sale.adapter.AdapterSaleTeamScreenComm;
import com.loyo.oa.v2.activity.sale.FragmentTeamSale;
import com.loyo.oa.v2.activity.sale.bean.SaleTeamScreen;
import java.util.ArrayList;

/**
 * 销售机会 销售阶段,排序 公用筛选View
 * Created by yyy on 16/5/18.
 */
public class SaleCommPopupView extends PopupWindow {

    private View contentView;
    private ListView listView;
    private Context mContext;
    private Handler mHandler;
    private AdapterSaleTeamScreenComm adapter;
    private ViewGroup.LayoutParams params;
    private Message msg;
    private Bundle bundle;

    private ArrayList<SaleTeamScreen> data;
    private int page;
    private int resultTag;
    private boolean dynamIc = false;

    public SaleCommPopupView(final Activity context, Handler handler,ArrayList<SaleTeamScreen> data,int page,boolean dynamIc) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        contentView = inflater.inflate(R.layout.saleteam_screen_common, null);
        this.mContext = context;
        this.mHandler = handler;
        this.page = page;
        this.data = data;
        this.dynamIc = dynamIc;
        initView();

        this.setContentView(contentView);
        this.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        this.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        this.setBackgroundDrawable(new ColorDrawable(0000000000));
        this.setFocusable(true);
        this.setOutsideTouchable(true);
        this.update();

    }

    public void initView() {
        listView = (ListView) contentView.findViewById(R.id.saleteam_screencommon_lv);
        //如果为销售阶段pop,则动态设置高度
        if(dynamIc){
            params = listView.getLayoutParams();
            params.height = mContext.getResources().getDimensionPixelSize(R.dimen.sale_pop_height);
            listView.setLayoutParams(params);
        }
        adapter = new AdapterSaleTeamScreenComm(mContext,data);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                dataReque(position);
            }
        });
    }

    /**
     * 重置勾选标签数据
     * */
    public void dataReque(int position){
        for(int i = 0;i<data.size();i++){
            if(position == i){
                data.get(i).setIndex(true);
            }else{
                data.get(i).setIndex(false);
            }
        }
        reuseFirm(position);
    }

    /**
     * 业务区分
     * */
    public void reuseFirm(int position){
        msg = new Message();
        bundle = new Bundle();
        if(page == ActivitySaleOpportunitiesManager.SCREEN_STAGE){
            bundle.putString("data",data.get(position).getId());
            resultTag = FragmentTeamSale.SALETEAM_SCREEN_TAG2;
        }else if(page == ActivitySaleOpportunitiesManager.SCREEN_SORT){
            bundle.putString("data",position+1+"");
            resultTag = FragmentTeamSale.SALETEAM_SCREEN_TAG3;
        }
        msg.setData(bundle);
        msg.what = resultTag;
        mHandler.sendMessage(msg);
        dismiss();
    }
}
