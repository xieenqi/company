package com.loyo.oa.v2.activityui.worksheet;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.attachment.bean.Attachment;
import com.loyo.oa.v2.activityui.worksheet.adapter.WorkSheetListNestingAdapter;
import com.loyo.oa.v2.customview.ActionSheetDialog;
import com.loyo.oa.v2.tool.BaseActivity;

import java.util.ArrayList;

/**
 * 【 工单详情 】  页面
 * Created by yyy on 16/8/30.
 */
public class WorksheetInfoActivity extends BaseActivity implements View.OnClickListener {


    private WorkSheetListNestingAdapter mAdapter;

    private String[] testUrl = {"http://uimg.ukuaiqi.com/2b34ed16-adea-49a2-93ae-4f1109138d4f/IMG_20160830_104826-22035654.jpg",
                                "http://uimg.ukuaiqi.com/9357a35b-4b17-48dd-a892-1804275f2f39/IMG_20160708_101239244523072.jpg",
                                "http://uimg.ukuaiqi.com/43931540-8b14-4552-af78-6e0c6c425218/IMG_20160830_104826-659978405.jpg"};
    private ArrayList<Attachment> mData = new ArrayList<Attachment>();
    private Attachment mAttachment;

    /** UI */
    private ListView lv_listview;
    private LinearLayout img_title_left;
    private RelativeLayout img_title_right;

    private TextView tv_title_1, /*标题*/
            tv_Assignment_name,  /*分派人*/
            tv_boom,             /*触发方式*/
            tv_related_order,    /*关联订单*/
            tv_responsible_name; /*负责人*/


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_worksheet_info);
        initView();
    }

    private void initView() {

        lv_listview = (ListView) findViewById(R.id.lv_listview);
        img_title_left = (LinearLayout) findViewById(R.id.img_title_left);
        img_title_left.setOnClickListener(this);
        tv_title_1 = (TextView) findViewById(R.id.tv_title_1);
        tv_Assignment_name = (TextView) findViewById(R.id.tv_Assignment_name);
        tv_boom = (TextView) findViewById(R.id.tv_boom);
        tv_related_order = (TextView) findViewById(R.id.tv_related_order);
        tv_responsible_name = (TextView) findViewById(R.id.tv_responsible_name);
        tv_title_1.setText("工单信息");

        bindData();
    }

    private void bindData(){

        tv_Assignment_name.setText("马云");
                tv_boom.setText("自动");
                tv_related_order.setText("009381");
                tv_responsible_name.setText("张小龙");

        for(int i = 0;i<testUrl.length;i++){
            mAttachment = new Attachment();
            mAttachment.setUrl(testUrl[i]);
            mAttachment.setSize("2031");
            mAttachment.setOriginalName("test.jpg");
            mData.add(mAttachment);
        }

        mAdapter = new WorkSheetListNestingAdapter(mData,this);
        lv_listview.setAdapter(mAdapter);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_title_right:
                functionButton();
                break;
            case R.id.img_title_left:
                onBackPressed();
                break;
        }
    }

    /**
     * 右上角菜单
     */
    private void functionButton() {
        ActionSheetDialog dialog = new ActionSheetDialog(WorksheetInfoActivity.this).builder();
        dialog.addSheetItem("意外终止", ActionSheetDialog.SheetItemColor.Blue, new ActionSheetDialog.OnSheetItemClickListener() {
            @Override
            public void onClick(int which) {
//                Bundle mBundle = new Bundle();
//                mBundle.putSerializable(ExtraAndResult.EXTRA_DATA, data.data.sales);
//                app.startActivityForResult(ClueDetailActivity.this, ClueTransferActivity.class, MainApp.ENTER_TYPE_RIGHT, ExtraAndResult.REQUSET_COMMENT, mBundle);
            }
        });
        dialog.show();
    }

}
