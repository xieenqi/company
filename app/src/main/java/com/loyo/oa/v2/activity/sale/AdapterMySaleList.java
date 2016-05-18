package com.loyo.oa.v2.activity.sale;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.loyo.oa.v2.R;

import java.util.ArrayList;

/**
 * com.loyo.oa.v2.adapter
 * 描述 :我的销售机会列表适配器
 * 作者 : xeq
 * 时间 : 16/5/17
 */
public class AdapterMySaleList extends BaseAdapter {

    public static final int TYPE_LIST_OF_CUSTOMER = 1;
    public static final int TYPE_LIST_OF_USER = TYPE_LIST_OF_CUSTOMER + 1;

    private ArrayList<String> data = new ArrayList<>();
    private Context mContext;
    private int mType;

    public AdapterMySaleList(Context context) {
        mContext = context;
        data.add("机会1");
        data.add("机会1");
        data.add("机会1");
        data.add("机会1");
        data.add("机会1");
        data.add("机会1");
        data.add("机会1");
        data.add("机会1");
        data.add("机会1");
    }

    public AdapterMySaleList(final Context context, final int type, final ArrayList<String> legWorks_) {
        data = legWorks_;
        mContext = context;
        mType = type;
    }

//    public void setLegWorks(final ArrayList<String> legWorks) {
//        this.legWorks = legWorks;
//    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(final int i) {
        return data.isEmpty() ? null : data.get(i);
    }

    @Override
    public long getItemId(final int i) {
        return i;
    }

    @Override
    public View getView(final int position, View view, final ViewGroup viewGroup) {
        HolderView holder;
        if (null == view) {
            view = LayoutInflater.from(mContext).inflate(R.layout.item_my_sale, viewGroup, false);
            holder = new HolderView();
            holder.tv_name = (TextView) view.findViewById(R.id.tv_name);
            holder.tv_number = (TextView) view.findViewById(R.id.tv_number);
            holder.tv_price = (TextView) view.findViewById(R.id.tv_price);
            view.setTag(holder);
        } else {
            holder = (HolderView) view.getTag();
        }
        holder.setContent(position);
//        final LegWork legWork = legWorks.get(i);
//        TextView tv_customer_name = ViewHolder.get(view, R.id.tv_customer_name);
//        TextView tv_user_name = ViewHolder.get(view, R.id.tv_user_name);
//        TextView tv_address = ViewHolder.get(view, R.id.tv_address);
//        TextView tv_time = ViewHolder.get(view, R.id.tv_time);
//
//        if (mType == TYPE_LIST_OF_CUSTOMER) {
//            tv_customer_name.setText("拜访人：" + legWork.creator.getName());
//            view.findViewById(R.id.layout_name).setVisibility(View.GONE);
//
//        } else if (mType == TYPE_LIST_OF_USER) {
//            view.findViewById(R.id.layout_name).setVisibility(View.VISIBLE);
//
//            if (null == legWork.customerName) {
//                tv_customer_name.setText("无指定客户");
//            } else {
//                tv_customer_name.setText(legWork.customerName);
//            }
//            tv_user_name.setText(legWork.creator.getName());
//        }
//        tv_address.setText("地址：" + legWork.address);
//        tv_time.setText(DateTool.getDiffTime(legWork.getCreatedAt() * 1000));

        return view;
    }

    class HolderView {
        TextView tv_name, tv_number, tv_price;

        public void setContent(int position) {
            tv_name.setText(data.get(position));
        }
    }
}
