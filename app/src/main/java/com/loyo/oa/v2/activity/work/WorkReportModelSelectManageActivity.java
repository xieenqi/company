package com.loyo.oa.v2.activity.work;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.loyo.oa.v2.R;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.beans.WorkReportTpl;
import com.loyo.oa.v2.common.http.ServerAPI;
import com.loyo.oa.v2.point.IWorkReport;
import com.loyo.oa.v2.tool.BaseAsyncHttpResponseHandler;
import com.loyo.oa.v2.tool.RCallback;
import com.loyo.oa.v2.tool.StringUtil;
import com.loyo.oa.v2.tool.ViewUtil;
import com.loyo.oa.v2.tool.customview.GeneralPopView;
import com.loyo.oa.v2.tool.customview.pullToRefresh.PullToRefreshListView;
import com.loyo.oa.v2.tool.customview.pullToRefresh.Slide.SlideView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.apache.http.Header;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;

import retrofit.client.Response;

@EActivity(R.layout.activity_workreport_model_select)
public class WorkReportModelSelectManageActivity extends Activity implements SlideView.OnSlideListener {

    @ViewById
    ViewGroup img_title_left;
    @ViewById
    PullToRefreshListView listView;

    MainApp app;
    Context mContext;
    ArrayList<WorkReportTpl> lstData = new ArrayList<>();

    SparseArray<SlideView> lstData_SlideView = new SparseArray();
    SlideView mLastSlideViewWithStatusOn;
    Boolean isBlockOnItemClick = false;

    @AfterViews
    void initUI() {
        app = (MainApp) getApplicationContext();
        mContext = this;

        ((TextView) findViewById(R.id.tv_title_1)).setText("选择模版");
        img_title_left.setOnTouchListener(ViewUtil.OnTouchListener_view_transparency.Instance());
        listView.setPullToRefreshEnabled(false);

        getData();
    }

    void getData() {
        ArrayList<ServerAPI.ParamInfo> lstParamInfo = new ArrayList<>(Arrays.asList(
                new ServerAPI.ParamInfo("isTopAdd", false),
                new ServerAPI.ParamInfo("isBottomAdd", false))
        );

        ServerAPI.request(this, ServerAPI.GET, "/workreports/templates", null, AsyncHandler_get.class, lstParamInfo);
    }

    @Click(R.id.img_title_left)
    void onClose() {
        onBackPressed();
    }

    public class AsyncHandler_get extends BaseAsyncHttpResponseHandler {

        @Override
        public Activity getActivity() {
            return (Activity) mContext;
        }

        @Override
        public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {

            if (arg2 != null && arg2.length > 0 && !StringUtil.isEmpty(getStr(arg2))) {
                Type type = new TypeToken<ArrayList<WorkReportTpl>>() {
                }.getType();
                ArrayList<WorkReportTpl> lstData_temp = MainApp.gson.fromJson(getStr(arg2), type);

                if (lstData_temp == null || lstData_temp.size() == 0) {
                    return;
                }

                lstData = lstData_temp;
                bindAdapter();
            }
        }
    }

    void bindAdapter() {
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                WorkReportTpl tpl = lstData.get((int) l);
                app.getRestAdapter().create(IWorkReport.class).getTpl(tpl.getId(), new RCallback<WorkReportTpl>() {
                    @Override
                    public void success(WorkReportTpl workReportTpl, Response response) {
                        if (isBlockOnItemClick) {
                            isBlockOnItemClick = false;
                            return;
                        }

                        Intent intent = new Intent();
                        intent.putExtra("data", workReportTpl);
                        app.finishActivity((Activity) mContext, MainApp.ENTER_TYPE_LEFT, RESULT_OK, intent);
                    }
                });

            }
        });
    }

    BaseAdapter adapter = new BaseAdapter() {
        @Override
        public int getCount() {
            return lstData.size();
        }

        @Override
        public Object getItem(int i) {
            return lstData_SlideView.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup viewGroup) {

            SlideView slideView = (SlideView) convertView;

            Item_info item_info;
            if (slideView == null) {

                convertView = LayoutInflater.from(mContext).inflate(R.layout.item_listview_workreport_tpl, null);
                item_info = new Item_info();
                slideView = new SlideView(mContext);
                slideView.setContentView(convertView);

                item_info.tv = (TextView) convertView.findViewById(R.id.tv);
                item_info.layout_holder = (ViewGroup) slideView.findViewById(R.id.layout_holder);

                slideView.setOnSlideListener(WorkReportModelSelectManageActivity.this);
                slideView.setTag(item_info);
                item_info.layout_holder.setOnClickListener(new OnClickListener_layout_holder(position));
            } else {
                item_info = (Item_info) convertView.getTag();
            }

            slideView.shrink();
            lstData_SlideView.put(position, slideView);

            WorkReportTpl item = lstData.get(position);

            item_info.tv.setText(item.getTitle());

            return slideView;
        }
    };

    @Override
    public void onSlide(View view, int status) {
        if (mLastSlideViewWithStatusOn != null && mLastSlideViewWithStatusOn != view) {
            mLastSlideViewWithStatusOn.shrink();
        }

        if (status == SLIDE_STATUS_ON) {
            mLastSlideViewWithStatusOn = (SlideView) view;
            isBlockOnItemClick = true;
        }
    }

    class Item_info {
        TextView tv;
        ViewGroup layout_holder;
    }

    class OnClickListener_layout_holder implements View.OnClickListener {
        int position;

        public OnClickListener_layout_holder(int position) {
            this.position = position;
        }

        @Override
        public void onClick(View v) {

            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            builder.setTitle("确认");
            builder.setPositiveButton(mContext.getString(R.string.dialog_submit), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    WorkReportTpl tpl = lstData.get(position);
                    app.getRestAdapter().create(IWorkReport.class).deleteTpl(tpl.getId(), new RCallback<WorkReportTpl>() {
                        @Override
                        public void success(WorkReportTpl workReportTpl, Response response) {
                            lstData.remove(position);
                            bindAdapter();
                        }
                    });

                    dialog.dismiss();
                }
            });
            builder.setNegativeButton(mContext.getString(R.string.dialog_cancel), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            builder.setMessage("是否删除该模版?");
            builder.show();
        }
    }
}
