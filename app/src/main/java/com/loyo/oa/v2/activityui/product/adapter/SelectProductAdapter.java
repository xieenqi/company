package com.loyo.oa.v2.activityui.product.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.product.ProductDetailActivity;
import com.loyo.oa.v2.activityui.product.model.ProductListModel;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.tool.LogUtil;

import java.util.ArrayList;

/**
 * 选择产品适配器
 * Created by yyy on 16/12/22.
 */

public class SelectProductAdapter extends BaseAdapter{

    Context mContext;



    ArrayList<ProductListModel> models;

    public SelectProductAdapter(Context mContext,ArrayList<ProductListModel> models){
        this.mContext = mContext;
        this.models = models;
    }

    public void setModels(ArrayList<ProductListModel> models) {
        this.models = models;
        notifyDataSetChanged();
    }
    @Override
    public int getCount() {
        return models.size();
    }

    @Override
    public Object getItem(int position) {
        return models.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        final ProductListModel model = models.get(position);
        ViewHolder holder = null;
        if(null == convertView){
            holder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_selectproduct,null);
            holder.iv_details = (ImageView) convertView.findViewById(R.id.iv_details);
            holder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
            holder.tv_size = (TextView) convertView.findViewById(R.id.tv_size);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }

        holder.tv_name.setText(model.name);
        holder.tv_size.setText("库存"+model.getStock());

        holder.iv_details.setOnTouchListener(Global.GetTouch());
        holder.iv_details.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mIntent = new Intent(mContext, ProductDetailActivity.class);
                mIntent.putExtra("id",model.id);
                mContext.startActivity(mIntent);
            }
        });

        return convertView;
    }

    class ViewHolder{
        TextView tv_name;
        TextView tv_size;
        ImageView iv_details;
    }

}
