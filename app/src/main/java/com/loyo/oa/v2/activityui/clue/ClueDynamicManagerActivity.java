package com.loyo.oa.v2.activityui.clue;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.commonview.CommonHtmlUtils;
import com.loyo.oa.v2.activityui.commonview.CommonImageView;
import com.loyo.oa.v2.activityui.commonview.CommonTextVew;
import com.loyo.oa.v2.activityui.commonview.CommonWebView;
import com.loyo.oa.v2.activityui.customer.CustomerDynamicManageActivity;
import com.loyo.oa.v2.activityui.customer.adapter.DynamicListnestingAdapter;
import com.loyo.oa.v2.activityui.customer.model.ImgAndText;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.beans.PaginationX;
import com.loyo.oa.v2.beans.SaleActivity;
import com.loyo.oa.v2.common.ExtraAndResult;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.common.http.HttpErrorCheck;
import com.loyo.oa.v2.customview.pullToRefresh.PullToRefreshBase;
import com.loyo.oa.v2.customview.pullToRefresh.PullToRefreshListView;
import com.loyo.oa.v2.point.IClue;
import com.loyo.oa.v2.tool.BaseActivity;
import com.loyo.oa.v2.tool.Config_project;
import com.loyo.oa.v2.tool.DateTool;
import com.loyo.oa.v2.tool.LogUtil;
import com.loyo.oa.v2.tool.RCallback;
import com.loyo.oa.v2.tool.RestAdapterFactory;
import com.loyo.oa.v2.tool.ViewHolder;
import com.loyo.oa.v2.tool.ViewUtil;

import java.util.ArrayList;
import java.util.HashMap;

import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * 【跟进动态】 销售线索
 */
public class ClueDynamicManagerActivity extends BaseActivity implements View.OnClickListener, PullToRefreshBase.OnRefreshListener2 {

    public static final int ACTIVITIES_ADD = 101;

    private ViewGroup img_title_left, layout_add;
    private PullToRefreshListView lv_saleActivity;
    private SaleActivitiesAdapter listAdapter;
    private DynamicListnestingAdapter nestionListAdapter;
    private ArrayList<SaleActivity> lstData_saleActivity_current = new ArrayList<>();
    private PaginationX<SaleActivity> paginationX = new PaginationX<>(20);
    private String clueId;
    private String name;
    private SaleActivity mSaleActivity;
    private boolean isChanged;
    private boolean isTopAdd = true;
    boolean isMyUser;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clue_followup);
        getIntenData();
        setTitle("跟进动态");
        initUI();
        getData();
    }

    private void getIntenData() {
        Intent intent = getIntent();
        clueId = intent.getStringExtra(ExtraAndResult.EXTRA_ID);
        name = intent.getStringExtra(ExtraAndResult.EXTRA_NAME);
        isMyUser = intent.getBooleanExtra(ExtraAndResult.EXTRA_ADD, false);
        if (TextUtils.isEmpty(clueId)) {
            onBackPressed();
            Toast("参数不全");
        }
    }

    /**
     * 获取数据
     */
    private void getData() {
        if (clueId != null) {
            HashMap<String, Object> map = new HashMap<>();
            map.put("salesId", clueId);
            map.put("pageIndex", paginationX.getPageIndex());
            map.put("pageSize", isTopAdd ? lstData_saleActivity_current.size() >= 20 ? lstData_saleActivity_current.size() : 20 : 20);
            RestAdapterFactory.getInstance().build(Config_project.API_URL_CUSTOMER()).create(IClue.class).getSaleactivity(map, new RCallback<PaginationX<SaleActivity>>() {
                @Override
                public void success(final PaginationX<SaleActivity> paginationXes, final Response response) {
                    HttpErrorCheck.checkResponse("跟进动态数据:", response);
                    lv_saleActivity.onRefreshComplete();
                    if (!PaginationX.isEmpty(paginationXes)) {
                        paginationX = paginationXes;
                        if (isTopAdd) {
                            lstData_saleActivity_current.clear();
                        }
                        lstData_saleActivity_current.addAll(paginationX.getRecords());
                        bindData();
                    }
                }

                @Override
                public void failure(final RetrofitError error) {
                    HttpErrorCheck.checkError(error);
                    lv_saleActivity.onRefreshComplete();
                    super.failure(error);
                }
            });
        }
    }

    private void initUI() {
        img_title_left = (ViewGroup) findViewById(R.id.img_title_left);
        img_title_left.setOnClickListener(this);
        img_title_left.setOnTouchListener(new ViewUtil.OnTouchListener_view_transparency());
        layout_add = (ViewGroup) findViewById(R.id.layout_add);
        if (!isMyUser) {
            layout_add.setVisibility(View.GONE);
        }
        layout_add.setOnTouchListener(Global.GetTouch());
        layout_add.setOnClickListener(this);
        lv_saleActivity = (PullToRefreshListView) findViewById(R.id.lv_saleActivity);
        lv_saleActivity.setMode(PullToRefreshBase.Mode.BOTH);
        lv_saleActivity.setOnRefreshListener(this);
    }

    @Override
    public void onClick(final View v) {
        switch (v.getId()) {

            /*返回*/
            case R.id.img_title_left:
                onBackPressed();
                break;

            /*新建线索*/
            case R.id.layout_add:
                Bundle bundle = new Bundle();
                bundle.putString(ExtraAndResult.EXTRA_ID, clueId);
                bundle.putString(ExtraAndResult.EXTRA_NAME, name);
                app.startActivityForResult(this, ClueDynamicAddActivity.class, MainApp.ENTER_TYPE_RIGHT, ACTIVITIES_ADD, bundle);
                break;

            default:
                break;
        }
    }

    @Override
    public void onActivityResult(final int requestCode, final int resultCode, final Intent data) {

        if (resultCode != RESULT_OK || data == null) {
            return;
        }
        switch (requestCode) {
            /*新建跟进动态回调*/
            case ACTIVITIES_ADD:
                getData();
                break;

            default:
                break;
        }
    }

    @Override
    public void onPullDownToRefresh(final PullToRefreshBase refreshView) {
        isTopAdd = true;
        paginationX.setPageIndex(1);
        getData();
    }

    @Override
    public void onPullUpToRefresh(final PullToRefreshBase refreshView) {
        isTopAdd = false;
        paginationX.setPageIndex(paginationX.getPageIndex() + 1);
        getData();
    }

    void bindData() {
        if (null == listAdapter) {
            listAdapter = new SaleActivitiesAdapter();
            lv_saleActivity.setAdapter(listAdapter);
            lv_saleActivity.setMode(PullToRefreshBase.Mode.BOTH);
            lv_saleActivity.setOnRefreshListener(this);
        } else {
            listAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onBackPressed() {
        if (mSaleActivity != null) {
            Intent intent = new Intent();
            intent.putExtra("data", mSaleActivity);
            app.finishActivity(this, MainApp.ENTER_TYPE_LEFT, isChanged ? RESULT_OK : RESULT_CANCELED, intent);
            return;
        }

        super.onBackPressed();
    }


    private class SaleActivitiesAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return lstData_saleActivity_current.size();
        }

        @Override
        public Object getItem(final int i) {
            return lstData_saleActivity_current.isEmpty() ? null : lstData_saleActivity_current.get(i);
        }

        @Override
        public long getItemId(final int i) {
            return i;
        }

        @Override
        public View getView(final int i, View convertView, final ViewGroup parent) {
            Holder holder;
            SaleActivity saleActivity = lstData_saleActivity_current.get(i);
            if (convertView == null) {
                holder = new Holder();
                convertView = getLayoutInflater().inflate(R.layout.item_saleactivities_group_child, null);
                holder.ll_layout_time = ViewHolder.get(convertView, R.id.ll_layout_time);
                holder.layout_audio = ViewHolder.get(convertView, R.id.layout_audio);
                holder.lv_listview = ViewHolder.get(convertView, R.id.lv_listview);
                holder.tv_create_time = ViewHolder.get(convertView, R.id.tv_create_time);
                holder.tv_content = ViewHolder.get(convertView, R.id.tv_content);
                holder.tv_contact_name = ViewHolder.get(convertView, R.id.tv_contact_name);
                holder.tv_follow_name = ViewHolder.get(convertView, R.id.tv_follow_name);
                holder.tv_time = ViewHolder.get(convertView, R.id.tv_time);
                holder.tv_audio_length = ViewHolder.get(convertView, R.id.tv_audio_length);
                holder.iv_imgTime = ViewHolder.get(convertView, R.id.iv_imgTime);
                holder.tv_calls = ViewHolder.get(convertView, R.id.iv_calls);
                holder.ll_web = ViewHolder.get(convertView, R.id.ll_web);//装在webview的容器
                convertView.setTag(holder);
            } else {
                holder = (Holder) convertView.getTag();
            }
            holder.setContent(holder.ll_web, saleActivity.getContent());
//            ListView lv_listview = ViewHolder.get(convertView, R.id.lv_listview);
//            TextView tv_create_time = ViewHolder.get(convertView, R.id.tv_create_time);
//            TextView tv_content = ViewHolder.get(convertView, R.id.tv_content);
//            TextView tv_contact_name = ViewHolder.get(convertView, R.id.tv_contact_name);
//            TextView tv_follow_name = ViewHolder.get(convertView, R.id.tv_follow_name);
//            TextView tv_time = ViewHolder.get(convertView, R.id.tv_time);
//            ImageView iv_imgTime = ViewHolder.get(convertView, R.id.iv_imgTime);
            holder.tv_create_time.setText(DateTool.getDiffTime(saleActivity.getCreateAt()));
            if (!saleActivity.getContent().contains("<p>")) {
                holder.tv_content.setVisibility(View.VISIBLE);
                holder.tv_content.setText(saleActivity.getContent());
            } else {
                holder.tv_content.setVisibility(View.GONE);
            }
            holder.tv_contact_name.setText("联系人：" + saleActivity.contactName);
            holder.tv_follow_name.setText("跟进人：" + saleActivity.creatorName + " #" + saleActivity.typeName);

            if (null != saleActivity.getAttachments() && saleActivity.getAttachments().size() != 0) {
                holder.lv_listview.setVisibility(View.VISIBLE);
                nestionListAdapter = new DynamicListnestingAdapter(saleActivity.getAttachments(), mContext);
                holder.lv_listview.setAdapter(nestionListAdapter);
                nestionListAdapter.refreshData();
            } else {
                holder.lv_listview.setVisibility(View.GONE);
            }

            if (i == lstData_saleActivity_current.size() - 1) {
                convertView.setBackgroundResource(R.drawable.item_bg_buttom);
            } else {
                convertView.setBackgroundColor(getResources().getColor(R.color.white));
            }
            return convertView;
        }

    }

    class Holder {
        LinearLayout ll_layout_time;
        LinearLayout layout_audio, ll_web;
        ListView lv_listview;
        TextView tv_create_time;
        TextView tv_content;
        TextView tv_contact_name;
        TextView tv_follow_name;
        TextView tv_time;
        TextView tv_audio_length;
        ImageView iv_imgTime;
        TextView tv_calls;
        /**
         * 设置图文混编
         */
        public void setContent(LinearLayout layout, String content) {
            layout.removeAllViews();
            for (final ImgAndText ele : CommonHtmlUtils.Instance().checkContentList(content)) {
                if (ele.type.startsWith("img")) {
                    CommonImageView img = new CommonImageView(ClueDynamicManagerActivity.this, ele.data);
                    layout.addView(img);
                } else {
                    CommonTextVew tex = new CommonTextVew(ClueDynamicManagerActivity.this, ele.data);
                    layout.addView(tex);
                }
            }
            layout.setVisibility(View.VISIBLE);
        }
    }
}