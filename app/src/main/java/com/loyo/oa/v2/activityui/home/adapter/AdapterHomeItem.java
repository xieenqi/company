package com.loyo.oa.v2.activityui.home.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.TestLoadingActivity;
import com.loyo.oa.v2.activityui.contact.ContactsActivity;
import com.loyo.oa.v2.activityui.home.bean.HomeItem;
import com.loyo.oa.v2.activityui.home.bean.HttpMainRedDot;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.common.ExtraAndResult;

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
    private int wfinstanceCount = 0;//不为0就到我审批的

    public AdapterHomeItem(Activity activity) {
        this.activity = activity;
        try {
            inflter = LayoutInflater.from(activity);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    /**
     * 设置item数据
     *
     * @param items
     */
    public void setItemData(ArrayList<HomeItem> items) {
        crmTi = false;
        oaTi = false;
        this.items = items;
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
        if (items != null && null != mItemNumbers) {
            for (HomeItem item : items) {
                for (HttpMainRedDot num : mItemNumbers) {
                    if ((item.title.equals("工作报告") && num.bizType == 1)) {
                        if (num.bizNum > 0) {
                            item.extra = num.bizNum + "个待点评(含抄送)";
                        }
                        item.viewed = num.viewed;
                    } else if ((item.title.equals("任务计划") && num.bizType == 2)) {
                        if (num.bizNum > 0) {
                            item.extra = num.bizNum + "个未完成";
                        }
                        item.viewed = num.viewed;
                    } else if ((item.title.equals("审批流程") && num.bizType == 12)) {
                        if (num.bizNum > 0) {
                            item.extra = num.bizNum + "个待我审批";
                        } else {
                            item.extra = " ";
                        }
                        wfinstanceCount = num.bizNum;
                        item.viewed = num.viewed;
                    } else if ((item.title.equals("项目管理") && num.bizType == 5)) {
                        if (num.bizNum > 0) {
                            item.extra = num.bizNum + "个进行中";
                        }
                        item.viewed = num.viewed;
                    }
//            else if ((item.title.equals("客户管理") && num.bizType == 6)) {//crm 不做红点
//                extra = num.bizNum + "个将掉公海";
//                holder.view_number.setVisibility(num.viewed ? View.GONE : View.VISIBLE);
//            } else if ((item.title.equals("客户拜访") && num.bizType == 11)) {
//                extra = num.bizNum + "个需拜访";
//                holder.view_number.setVisibility(num.viewed ? View.GONE : View.VISIBLE);
//            }
                    else if ((item.title.equals("考勤管理") && num.bizType == 4)) {
//                        item.extra = num.bizNum + "个外勤";
                        item.viewed = num.viewed;
                    } else if (item.title.equals("公告通知") && num.bizType == 19) { //通知公告红点
                        item.viewed = num.viewed;
                    } else if (item.title.equals("我的讨论") && num.bizType == 14) { //我的讨论红点
                        item.viewed = num.viewed;
                    }
                }
            }
        }
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

    @SuppressLint("InflateParams")
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
        holder.setContentView(item, position);
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

        public void setContentView(HomeItem item, final int position) {

            tv_extra.setText(item.extra);
            view_number.setVisibility(item.viewed ? View.GONE : View.VISIBLE);
            //列表分组
//            原来的实现有问题，重绘ui的时候，标记会失效
//            if (item.tag == 1) {
//                item_newmain_topview.setVisibility(crmTi ? View.GONE : View.VISIBLE);
//                tv_title.setText("CRM");
//                crmTi = true;
//            } else if (item.tag == 2) {
//                item_newmain_topview.setVisibility(oaTi ? View.GONE : View.VISIBLE);
//                tv_title.setText("OA");
//                oaTi = true;
//            } else if (item.tag == 0) {
//                item_newmain_topview.setVisibility(View.GONE);
//            }

            if ("销售线索".equals(item.title)) {
                item_newmain_topview.setVisibility(View.VISIBLE);
                tv_title.setText("CRM");
            } else if ("项目管理".equals(item.title)) {
                item_newmain_topview.setVisibility(View.VISIBLE);
                tv_title.setText("OA");
            } else{
                item_newmain_topview.setVisibility(View.GONE);
            }

            img_item.setImageResource(item.imageViewRes);
            tv_item.setText(item.title);

            //跳转对应业务
            item_newmain_layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (true) {
                        mIntent.setClass(activity, TestLoadingActivity.class);
                        mIntent.putExtra(ExtraAndResult.EXTRA_OBJ, wfinstanceCount);
                        activity.startActivity(mIntent);
                        activity.overridePendingTransition(R.anim.enter_righttoleft, R.anim.exit_righttoleft);
                        return;
                    }

                    if (null != MainApp.lstDepartment && MainApp.lstDepartment.size() != 0) {
                        mIntent.setClass(activity, ContactsActivity.class);
                        activity.startActivity(mIntent);
                        activity.overridePendingTransition(R.anim.enter_righttoleft, R.anim.exit_righttoleft);
                    } else if (items.get(position).title.equals("审批流程")) {
                        try {
                            mIntent.setClass(activity, Class.forName(items.get(position).cls));
                            mIntent.putExtra(ExtraAndResult.EXTRA_OBJ, wfinstanceCount);
                            activity.startActivity(mIntent);
                            activity.overridePendingTransition(R.anim.enter_righttoleft, R.anim.exit_righttoleft);
                        } catch (ClassNotFoundException e) {
                            e.printStackTrace();
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

        }
    }
}
