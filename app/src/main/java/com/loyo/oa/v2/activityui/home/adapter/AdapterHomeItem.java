package com.loyo.oa.v2.activityui.home.adapter;

import android.annotation.SuppressLint;
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
import com.loyo.oa.v2.activityui.contact.ContactsActivity;
import com.loyo.oa.v2.activityui.home.bean.HomeItem;
import com.loyo.oa.v2.activityui.home.bean.HttpMainRedDot;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.common.ExtraAndResult;
import com.loyo.oa.v2.tool.LogUtil;

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
//        notifyDataSetChanged();
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
//        notifyDataSetChanged();
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
        holder.setContentView(item, position, mItemNumbers);
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

        public void setContentView(HomeItem item, final int position, ArrayList<HttpMainRedDot> mItemNumbers) {

            if (null != mItemNumbers) {//首页红点 有数据才加载
                for (HttpMainRedDot num : mItemNumbers) {
                    String extra = "";
                    if ((item.title.equals("工作报告") && num.bizType == 1)) {
                        if (num.bizNum > 0) {
                            extra = num.bizNum + "个待点评(含抄送)";
                        }
                        view_number.setVisibility(num.viewed ? View.GONE : View.VISIBLE);
                    } else if ((item.title.equals("任务计划") && num.bizType == 2)) {
                        if (num.bizNum > 0) {
                            extra = num.bizNum + "个未完成";
                        }
                        view_number.setVisibility(num.viewed ? View.GONE : View.VISIBLE);
                    } else if ((item.title.equals("审批流程") && num.bizType == 12)) {
                        if (num.bizNum > 0) {
                            extra = num.bizNum + "个待我审批";
                        } else {
                            extra = " ";
                        }
                        wfinstanceCount = num.bizNum;
                        view_number.setVisibility(num.viewed ? View.GONE : View.VISIBLE);
                    } else if ((item.title.equals("项目管理") && num.bizType == 5)) {
                        if (num.bizNum > 0) {
                            extra = num.bizNum + "个进行中";
                        }
                        view_number.setVisibility(num.viewed ? View.GONE : View.VISIBLE);
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
                        view_number.setVisibility(num.viewed ? View.GONE : View.VISIBLE);
                    } else if (item.title.equals("公告通知") && num.bizType == 19) { //通知公告红点
                        view_number.setVisibility(num.viewed ? View.GONE : View.VISIBLE);
                    } else if (item.title.equals("我的讨论") && num.bizType == 14) { //我的讨论红点
                        view_number.setVisibility(num.viewed ? View.GONE : View.VISIBLE);
                    } else {
                        view_number.setVisibility(View.GONE);
                    }
//                    tv_extra.setText(!TextUtils.isEmpty(extra)?extra:"");
                    if (!TextUtils.isEmpty(extra)) {
                        tv_extra.setText(extra);
                    }
//                    else {
//                        tv_extra.setText("");
//                    }
                    LogUtil.d(position + "加载》》》》》》》》》》》》》》》》》》》》》》》》》》》》" + extra);
                }
            }

            //列表分组
            if (item.tag == 1) {
                item_newmain_topview.setVisibility(crmTi ? View.GONE : View.VISIBLE);
                tv_title.setText("CRM");
                crmTi = true;
            } else if (item.tag == 2) {
                item_newmain_topview.setVisibility(oaTi ? View.GONE : View.VISIBLE);
                tv_title.setText("OA");
                oaTi = true;
            } else if (item.tag == 0) {
                item_newmain_topview.setVisibility(View.GONE);
            }
            img_item.setImageResource(item.imageViewRes);
            tv_item.setText(item.title);

            //跳转对应业务
            item_newmain_layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (items.get(position).title.equals("通讯录")) {
                        if (null != MainApp.lstDepartment && MainApp.lstDepartment.size() != 0) {
                            mIntent.setClass(activity, ContactsActivity.class);
                            activity.startActivity(mIntent);
                            activity.overridePendingTransition(R.anim.enter_righttoleft, R.anim.exit_righttoleft);
                        } else {
                            Toast.makeText(activity, "数据获取中请等待，或手动更新数据", Toast.LENGTH_SHORT).show();
                        }
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
