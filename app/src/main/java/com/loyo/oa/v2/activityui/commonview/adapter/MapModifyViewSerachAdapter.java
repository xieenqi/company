package com.loyo.oa.v2.activityui.commonview.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.amap.api.services.core.PoiItem;
import com.amap.api.services.help.Tip;
import com.loyo.oa.v2.R;
import com.loyo.oa.v2.tool.LogUtil;

import java.util.List;

/**
 * 【地点微调搜索页】adapter
 * Created by yyy on 16/7/20.
 */
public class MapModifyViewSerachAdapter extends RecyclerView.Adapter<MapModifyViewSerachAdapter.MViewHolder> {

    private Context mContext;
    private List<PoiItem> poiItems;
    private List<Tip> gelItems;
    private int page; //业务区分0:poi 1:非poi

    public MapModifyViewSerachAdapter(List<PoiItem> poiItems,List<Tip> gelItems, Context mContext,int page){
        this.poiItems = poiItems;
        this.gelItems = gelItems;
        this.mContext = mContext;
        this.page = page;
        LogUtil.dee("page:"+page);
    }


    @Override
    public MViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_mapmodifyview,parent,false);
        MViewHolder holder = new MViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(MViewHolder holder, int position) {
        if(page == 0){
            PoiItem poiItem = poiItems.get(position);
            holder.address.setText(poiItem.getTitle());
            holder.message.setText(poiItem.getSnippet());
        }else{
            Tip tip = gelItems.get(position);
            holder.address.setText(tip.getName());
            holder.message.setText(tip.getDistrict());
            LogUtil.dee("name:"+tip.getName());
            LogUtil.dee("msg:"+tip.getDistrict());
        }
    }

    @Override
    public int getItemCount() {
        return poiItems.size();
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
