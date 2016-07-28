package com.loyo.oa.v2.activityui.commonview.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.amap.api.services.core.PoiItem;
import com.loyo.oa.v2.R;

import java.util.List;

/**
 * Created by yyy on 16/7/20.
 */
public class MapModifyViewSerachAdapter extends RecyclerView.Adapter<MapModifyViewSerachAdapter.MViewHolder> {

    private Context mContext;
    private List<PoiItem> mData;

    public MapModifyViewSerachAdapter(List<PoiItem> mData, Context mContext){
        this.mData = mData;
        this.mContext = mContext;
    }


    @Override
    public MViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_mapmodifyview,parent,false);
        MViewHolder holder = new MViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(MViewHolder holder, int position) {
        PoiItem poiItem = mData.get(position);
        holder.address.setText(poiItem.getTitle());
        holder.message.setText(poiItem.getSnippet());
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class MViewHolder extends RecyclerView.ViewHolder{

        private TextView address;
        private TextView message;

        public MViewHolder(View view){
            super(view);
            this.address = (TextView) view.findViewById(R.id.item_mapmodify_text);
            this.message = (TextView) view.findViewById(R.id.item_mapmodify_message);
        }
    }
}
