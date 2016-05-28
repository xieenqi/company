package com.loyo.oa.v2.activity.home.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activity.home.bean.HomeItem;
import com.loyo.oa.v2.beans.HttpMainRedDot;
import com.loyo.oa.v2.tool.customview.RippleView;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * 2.1新版首页Adapter
 * Created by yyy on 16/5/27.
 */
public class AdapterHomeItem extends BaseAdapter{

    private LayoutInflater inflter;
    private Context mContext;
    private ArrayList<HomeItem> items;
    private ArrayList<HttpMainRedDot> mItemNumbers;
    private boolean crmTi = false;
    private boolean oaTi  = false;

    public AdapterHomeItem(Context context,ArrayList<HomeItem> items,ArrayList<HttpMainRedDot> mItemNumbers) {
        this.mContext = context;
        this.items = items;
        this.mItemNumbers = mItemNumbers;
        inflter = LayoutInflater.from(mContext);
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public HomeItem getItem(final int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(final int position) {
        return position;
    }

    public void remove(final int arg0) {
        items.remove(arg0);
        notifyDataSetChanged();
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = inflter.inflate(R.layout.item_newmain, null, false);
            holder.img_item = (ImageView) convertView.findViewById(R.id.item_newmain_img);
            holder.tv_item = (TextView) convertView.findViewById(R.id.item_newmain_name);
            holder.tv_extra = (TextView) convertView.findViewById(R.id.item_newmain_state);
            holder.view_number = (ImageView) convertView.findViewById(R.id.item_newmain_number);
            holder.tv_title = (TextView) convertView.findViewById(R.id.item_newmain_title);
            holder.item_newmain_topview = (LinearLayout) convertView.findViewById(R.id.item_newmain_topview);
            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final HomeItem item = getItem(position);

        for (HttpMainRedDot num : mItemNumbers) {//首页红点
            String extra = "";
            if ((item.title.equals("工作报告") && num.bizType == 1)) {
                extra = num.bizNum + "个待点评(含抄送)";
                holder.view_number.setVisibility(num.viewed ? View.GONE : View.VISIBLE);
            } else if ((item.title.equals("任务计划") && num.bizType == 2)) {
                extra = num.bizNum + "个未完成";
                holder.view_number.setVisibility(num.viewed ? View.GONE : View.VISIBLE);
            } else if ((item.title.equals("审批流程") && num.bizType == 12)) {
                extra = num.bizNum + "个待审批";
                holder.view_number.setVisibility(num.viewed ? View.GONE : View.VISIBLE);
            } else if ((item.title.equals("项目管理") && num.bizType == 5)) {
                extra = num.bizNum + "个进行中";
                holder.view_number.setVisibility(num.viewed ? View.GONE : View.VISIBLE);
            } else if ((item.title.equals("客户管理") && num.bizType == 6)) {
                extra = num.bizNum + "个将掉公海";
                holder.view_number.setVisibility(num.viewed ? View.GONE : View.VISIBLE);
            } else if ((item.title.equals("客户拜访") && num.bizType == 11)) {
                extra = num.bizNum + "个需拜访";
                holder.view_number.setVisibility(num.viewed ? View.GONE : View.VISIBLE);
            } else if ((item.title.equals("考勤管理") && num.bizType == 4)) {
                extra = num.bizNum + "个外勤";
                holder.view_number.setVisibility(num.viewed ? View.GONE : View.VISIBLE);
            }else if (num.bizType == 19) { //通知公告红点
                if (!num.viewed) {
                    holder.view_number.setVisibility(View.VISIBLE);
                } else {
                    holder.view_number.setVisibility(View.GONE);
                }
            }
            if (!TextUtils.isEmpty(extra)) {
                holder.tv_extra.setText(extra);
            }
        }

        //列表分组
        if(item.tag == 1){
            holder.item_newmain_topview.setVisibility(crmTi ? View.GONE : View.VISIBLE);
            holder.tv_title.setText("crm");
            crmTi = true;
        }else if(item.tag == 2){
            holder.item_newmain_topview.setVisibility(oaTi ? View.GONE : View.VISIBLE);
            holder.tv_title.setText("oa");
            oaTi = true;
        }else{
            holder.item_newmain_topview.setVisibility(View.GONE);
        }

        holder.img_item.setImageDrawable(mContext.getResources().getDrawable(item.imageViewRes));
        holder.tv_item.setText(item.title);

        return convertView;
    }

    class ViewHolder {
        LinearLayout item_newmain_topview;
        ImageView img_item;
        TextView tv_item;
        TextView tv_extra;
        ImageView view_number;
        TextView tv_title;
    }
}
