package com.loyo.oa.v2.ui.activity.home.adapter;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.beans.HttpMainRedDot;
import com.loyo.oa.v2.ui.activity.contact.ContactsActivity;
import com.loyo.oa.v2.ui.activity.home.bean.HomeItem;

import java.util.ArrayList;

/**
 * 2.1新版首页Adapter
 * Created by yyy on 16/5/27.
 */
public class AdapterHomeItem extends BaseAdapter {

    private LayoutInflater inflter;
    private Activity activity;
    private ArrayList<HomeItem> items;
    private ArrayList<HttpMainRedDot> mItemNumbers;
    private boolean crmTi = false;
    private boolean oaTi = false;
    private Intent mIntent = new Intent();

    public AdapterHomeItem(Activity activity) {
        this.activity = activity;
        try {
            inflter = LayoutInflater.from(activity);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

//    public AdapterHomeItem(Context context, ArrayList<HomeItem> items, ArrayList<HttpMainRedDot> mItemNumbers) {
//        this.mContext = context;
//        this.items = items;
//        this.mItemNumbers = mItemNumbers;
//        try {
//            inflter = LayoutInflater.from(context);
//        } catch (NullPointerException e) {
//            e.printStackTrace();
//        }
//    }

    /**
     * 设置item数据
     *
     * @param items
     */
    public void setItemData(ArrayList<HomeItem> items) {
        crmTi = false;
        oaTi = false;
        this.items = items;
        notifyDataSetChanged();
    }

    /**
     * 设置 红点数据
     *
     * @param mItemNumbers
     */
    public void setRedNumbreData(ArrayList<HttpMainRedDot> mItemNumbers) {
        this.mItemNumbers = mItemNumbers;
        crmTi = false;
        oaTi = false;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return null == items ? 0 : items.size();
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
        final ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = inflter.inflate(R.layout.item_newmain, null, false);
            holder.img_item = (ImageView) convertView.findViewById(R.id.item_newmain_img);
            holder.tv_item = (TextView) convertView.findViewById(R.id.item_newmain_name);
            holder.tv_extra = (TextView) convertView.findViewById(R.id.item_newmain_state);
            holder.view_number = (ImageView) convertView.findViewById(R.id.item_newmain_number);
            holder.tv_title = (TextView) convertView.findViewById(R.id.item_newmain_title);
            holder.item_newmain_topview = (LinearLayout) convertView.findViewById(R.id.item_newmain_topview);
            holder.item_newmain_layout = (LinearLayout) convertView.findViewById(R.id.item_newmain_layout);
            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final HomeItem item = items.get(position);
        if (null != mItemNumbers) {//首页红点 有数据才加载
            for (HttpMainRedDot num : mItemNumbers) {
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
                }
//            else if ((item.title.equals("客户管理") && num.bizType == 6)) {//crm 不做红点
//                extra = num.bizNum + "个将掉公海";
//                holder.view_number.setVisibility(num.viewed ? View.GONE : View.VISIBLE);
//            } else if ((item.title.equals("客户拜访") && num.bizType == 11)) {
//                extra = num.bizNum + "个需拜访";
//                holder.view_number.setVisibility(num.viewed ? View.GONE : View.VISIBLE);
//            }
                else if ((item.title.equals("考勤管理") && num.bizType == 4)) {
                    extra = num.bizNum + "个外勤";
                    holder.view_number.setVisibility(num.viewed ? View.GONE : View.VISIBLE);
                } else if (item.title.equals("公告通知") && num.bizType == 19) { //通知公告红点
                    holder.view_number.setVisibility(num.viewed ? View.GONE : View.VISIBLE);
                } else if (item.title.equals("我的讨论") && num.bizType == 14) { //我的讨论红点
                    holder.view_number.setVisibility(num.viewed ? View.GONE : View.VISIBLE);
                }
                if (!TextUtils.isEmpty(extra)) {
                    holder.tv_extra.setText(extra);
                }
            }
        }

        //列表分组
        if (item.tag == 1) {
            holder.item_newmain_topview.setVisibility(crmTi ? View.GONE : View.VISIBLE);
            holder.tv_title.setText("CRM");
            crmTi = true;
        } else if (item.tag == 2) {
            holder.item_newmain_topview.setVisibility(oaTi ? View.GONE : View.VISIBLE);
            holder.tv_title.setText("OA");
            oaTi = true;
        } else if (item.tag == 0) {
            holder.item_newmain_topview.setVisibility(View.GONE);
        }
        holder.img_item.setImageResource(item.imageViewRes);
        holder.tv_item.setText(item.title);

        //跳转对应业务
        holder.item_newmain_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (items.get(position).title.equals("通讯录")) {
                    if (null != MainApp.lstDepartment) {
                        mIntent.setClass(activity, ContactsActivity.class);
                        activity.startActivity(mIntent);
                    } else {
                        Toast.makeText(activity, "请重新拉去组织架构", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    try {
                        mIntent.setClass(activity, Class.forName(items.get(position).cls));
                        activity.startActivity(mIntent);
                        activity.overridePendingTransition(R.anim.enter_righttoleft, R.anim.exit_righttoleft);
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }

                }
            }
        });

        return convertView;
    }

    class ViewHolder {
        LinearLayout item_newmain_layout;
        LinearLayout item_newmain_topview;
        ImageView img_item;
        TextView tv_item;
        TextView tv_extra;
        ImageView view_number;
        TextView tv_title;
    }
}
