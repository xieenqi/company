package com.loyo.oa.v2.activityui.worksheet;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.order.OrderAddWorkSheetAttachmentActivity;
import com.loyo.oa.v2.activityui.order.event.OrderAddWorkSheetFinish;
import com.loyo.oa.v2.activityui.worksheet.adapter.OrderworksheetListAdapter;
import com.loyo.oa.v2.activityui.worksheet.bean.OrderWorksheetListModel;
import com.loyo.oa.v2.activityui.worksheet.event.OrderWorksheetAddFinish;
import com.loyo.oa.v2.activityui.worksheet.viewcontrol.OrderWorksheetListView;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.common.ExtraAndResult;
import com.loyo.oa.v2.common.event.AppBus;
import com.loyo.oa.v2.tool.BaseActivity;
import com.loyo.oa.v2.tool.LogUtil;
import org.greenrobot.eventbus.Subscribe;
import java.util.ArrayList;
import java.util.Collection;

/**
 * 【订单业务中】工单列表
 * Created by yyy on 16/10/21.
 */

public class OrderWorksheetListActivity extends BaseActivity implements View.OnClickListener, OrderWorksheetListView {

    private LinearLayout layout_back;
    private TextView tv_title;
    private RelativeLayout layout_add;
    private ListView listView_worksheet;
    private OrderWorksheetListModel mOworssheetList;
    private OrderworksheetListAdapter mAdapter;
    private ArrayList<OrderWorksheetListModel> reWorkSheet = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orderaddworksheet);
        initUI();
    }

    private void bindAdapter() {
        if (null == mAdapter) {
            mAdapter = new OrderworksheetListAdapter(mContext, reWorkSheet, this);
            listView_worksheet.setAdapter(mAdapter);
        } else {
            mAdapter.notifyDataSetChanged();
        }
    }

    public void initUI() {
        reWorkSheet.addAll((Collection<? extends OrderWorksheetListModel>) getIntent().getSerializableExtra(ExtraAndResult.EXTRA_NAME));

        layout_add = (RelativeLayout) findViewById(R.id.layout_add);
        layout_back = (LinearLayout) findViewById(R.id.layout_back);
        tv_title = (TextView) findViewById(R.id.tv_title);
        listView_worksheet = (ListView) findViewById(R.id.listView_worksheet);

        tv_title.setVisibility(View.VISIBLE);
        tv_title.setText("工单");

        layout_add.setOnClickListener(this);
        layout_back.setOnClickListener(this);
        bindAdapter();
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            /*新建工单*/
            case R.id.layout_add:
                Intent mIntent = new Intent(OrderWorksheetListActivity.this, OrderWorksheetAddActivity.class);
                startActivity(mIntent);
                break;

            /*返回*/
            case R.id.layout_back:
                onBackPressed();
                break;

            default:
                break;
        }
    }

    /**
     * 关闭activity
     */
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (reWorkSheet.size() != 0) {
            OrderAddWorkSheetFinish event = new OrderAddWorkSheetFinish();
            event.bundle = new Bundle();
            event.bundle.putSerializable(ExtraAndResult.EXTRA_ID, reWorkSheet);
            AppBus.getInstance().post(event);
        }
        finish();
    }

    /**
     * 新建工单回调
     */
    @Subscribe
    public void onOrderWorksheetAddFinish(OrderWorksheetAddFinish event) {
        mOworssheetList = (OrderWorksheetListModel) event.bundle.getSerializable(ExtraAndResult.EXTRA_ID);
        boolean isEdit = event.bundle.getBoolean(ExtraAndResult.WELCOM_KEY,false);
        int position = event.bundle.getInt(ExtraAndResult.APP_START);
        if(isEdit){
            reWorkSheet.set(position,mOworssheetList);
        }else{
            reWorkSheet.add(mOworssheetList);
        }
        bindAdapter();
    }

    /**
     * 删除工单
     */
    @Override
    public void deleteWorkSheet(int position) {
        reWorkSheet.remove(position);
        bindAdapter();
    }

    /**
     * 编辑工单
     */
    @Override
    public void editWorkSheet(int position) {
        Intent mIntent = new Intent(OrderWorksheetListActivity.this,OrderWorksheetAddActivity.class);
        mIntent.putExtra(ExtraAndResult.EXTRA_NAME,reWorkSheet.get(position));
        mIntent.putExtra(ExtraAndResult.APP_START,position);
        LogUtil.dee("editWorkSheet:"+position);
        startActivity(mIntent);
    }

    /**
     * 查看附件
     * */
    @Override
    public void intentAttachment(int position) {
        Bundle mBundle = new Bundle();
        mBundle.putBoolean("isOver", true);
        mBundle.putString("uuid",reWorkSheet.get(position).uuid);
        app.startActivityForResult(this, OrderAddWorkSheetAttachmentActivity.class, MainApp.ENTER_TYPE_RIGHT, ExtraAndResult.MSG_WHAT_HIDEDIALOG, mBundle);
    }

    /**
     * 返回监听
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getRepeatCount() == 0) {
            onBackPressed();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}

