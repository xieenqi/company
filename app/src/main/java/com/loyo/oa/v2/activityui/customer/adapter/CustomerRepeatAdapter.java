package com.loyo.oa.v2.activityui.customer.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.loyo.oa.common.utils.DateTool;
import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.customer.model.Customer;
import com.loyo.oa.v2.activityui.customer.model.CustomerRepeatList;
import com.loyo.oa.v2.beans.PaginationX;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.customermanagement.api.CustomerService;
import com.loyo.oa.v2.network.DefaultSubscriber;

/**
 * 【客户查重】适配器
 *  Created by yyy on 15/12/9.
 */
public class CustomerRepeatAdapter extends BaseAdapter {


    private PaginationX<CustomerRepeatList> listCommon;
    private PickInOnCallBack pickInOnCallBack;
    private Context mContext;

    public interface PickInOnCallBack{
        void pickEmbl();
    }

    public CustomerRepeatAdapter(final PaginationX<CustomerRepeatList> listCommons, final Context context,PickInOnCallBack pickInOnCallBack) {
        listCommon = listCommons;
        mContext = context;
        this.pickInOnCallBack = pickInOnCallBack;
    }

    class viewHolder {
        TextView tv_title;    //客户名
        TextView tv_content1; //负责人
        TextView tv_content2; //创建时间
        ImageView img_public; //挑入按钮
    }


    @Override
    public int getCount() {
        return listCommon.getRecords().size();
    }

    @Override
    public Object getItem(final int i) {
        return null;
    }

    @Override
    public long getItemId(final int i) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup viewGroup) {
        viewHolder holder = null;
        final CustomerRepeatList customerRepeatList = listCommon.getRecords().get(position);
        if (convertView == null) {
            holder = new viewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_customerrepeat_list, null);
            holder.tv_title = (TextView) convertView.findViewById(R.id.tv_title);
            holder.tv_content1 = (TextView) convertView.findViewById(R.id.tv_content1);
            holder.tv_content2 = (TextView) convertView.findViewById(R.id.tv_content2);
            holder.img_public  = (ImageView) convertView.findViewById(R.id.img_public);
            convertView.setTag(holder);
        } else {
            holder = (viewHolder) convertView.getTag();
        }

//        String lastActivityAt = MainApp.getMainApp().df3.format(new Date(customerRepeatList.getCreatedAt() * 1000));
        String lastActivityAt = DateTool.getDateTimeFriendly(customerRepeatList.getCreatedAt());

        holder.tv_title.setText(customerRepeatList.getName());
        holder.tv_content2.setText("创建时间:"+lastActivityAt);

        if(null != customerRepeatList.getmOwner()){
            holder.tv_content1.setText("负责人:"+customerRepeatList.getmOwner().name);
        }else{
            holder.tv_content1.setText("负责人:--");
        }

        /*公海客户判断*/
        if(customerRepeatList.isLock()){
            holder.img_public.setVisibility(View.INVISIBLE);
        }else{
            holder.img_public.setVisibility(View.VISIBLE);
        }

        holder.img_public.setOnTouchListener(Global.GetTouch());
        holder.img_public.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {//挑入公海客户
                CustomerService.pickInCustomer(customerRepeatList.getId())
                        .subscribe(new DefaultSubscriber<Customer>() {
                            public void onNext(Customer customer) {
                                pickInOnCallBack.pickEmbl();
                            }
                        });
            }
        });

        return convertView;
    }
}
