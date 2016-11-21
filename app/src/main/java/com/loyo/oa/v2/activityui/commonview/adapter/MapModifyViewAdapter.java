package com.loyo.oa.v2.activityui.commonview.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.amap.api.services.core.PoiItem;
import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.commonview.bean.PositionResultItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yyy on 16/7/20.
 */
public class MapModifyViewAdapter extends RecyclerView.Adapter<MapModifyViewAdapter.MViewHolder> {

    private Context mContext;
    private ArrayList<PositionResultItem> mData;
    private int selectPosition;

    public MapModifyViewAdapter(ArrayList<PositionResultItem> mData,Context mContext){
        this.mData = mData;
        this.mContext = mContext;
    }

    public void selectPositioni(int position){
        selectPosition = position;
    }

    @Override
    public MViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_mapmodifyview,parent,false);
        MViewHolder holder = new MViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(final MViewHolder holder, int position) {
        PositionResultItem positionResultItem = mData.get(position);
        try{
            holder.address.setText(positionResultItem.address);
            holder.message.setText(positionResultItem.message);
        }catch(NullPointerException e){
            e.printStackTrace();
        }

        if(selectPosition == position){
            holder.selected.setVisibility(View.VISIBLE);
        }else{
            holder.selected.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public  class MViewHolder extends RecyclerView.ViewHolder{

        private TextView address;
        private TextView message;
        private ImageView selected;

        public MViewHolder(View view){
            super(view);
            this.address = (TextView) view.findViewById(R.id.item_mapmodify_text);
            this.message = (TextView) view.findViewById(R.id.item_mapmodify_message);
            this.selected = (ImageView) view.findViewById(R.id.item_mapmodify_select);
        }
    }
}
