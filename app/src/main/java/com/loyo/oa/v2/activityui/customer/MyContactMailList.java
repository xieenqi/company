package com.loyo.oa.v2.activityui.customer;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.customer.adapter.MyContactInfoListAdapter;
import com.loyo.oa.v2.activityui.customer.model.MyContactInfo;
import com.loyo.oa.v2.activityui.wfinstance.ProcessSelectActivity;
import com.loyo.oa.v2.activityui.wfinstance.WfInTypeSelectActivity;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.common.ExtraAndResult;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.customview.SideBar;
import com.loyo.oa.v2.tool.BaseActivity;
import com.loyo.oa.v2.tool.ContactInfoUtil;

import java.io.Serializable;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * 【手机通讯录】列表
 * Created by yyy on 16/10/27.
 */

public class MyContactMailList extends BaseActivity implements View.OnClickListener {

    private ContactInfoUtil contactInfoUtil;
    private List<MyContactInfo> contactInfoList;

    private SideBar sideBar;
    private ListView sortListView;
    private TextView title;
    private LinearLayout img_left;
    private RelativeLayout img_right;

    private MyContactInfoListAdapter mAdapter;
    private PinyinComparator pinyinComparator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myphonemaillist);
        initUI();
    }

    /**
     * 初始化
     */
    private void initUI() {
        pinyinComparator = new PinyinComparator();
        contactInfoUtil = new ContactInfoUtil(mContext);
        contactInfoList = contactInfoUtil.getMyCallContactInfo();

        sortListView = (ListView) findViewById(R.id.expandableListView_user);
        img_left = (LinearLayout) findViewById(R.id.img_title_left);
        img_right = (RelativeLayout) findViewById(R.id.img_title_search_right);

        img_right.setOnTouchListener(Global.GetTouch());
        img_left.setOnTouchListener(Global.GetTouch());

        img_right.setOnClickListener(this);
        img_left.setOnClickListener(this);

        sideBar = (SideBar) findViewById(R.id.sidrbar);
        title = (TextView) findViewById(R.id.tv_title_1);
        title.setText("选择联系人");

        /*设置右侧触摸监听*/
        sideBar.setOnTouchingLetterChangedListener(new SideBar.OnTouchingLetterChangedListener() {
            @Override
            public void onTouchingLetterChanged(String s) {
                int position = mAdapter.getPositionForSection(s.charAt(0));
                if (position != -1) {
                    sortListView.setSelection(position);
                }
            }
        });

        Collections.sort(contactInfoList, pinyinComparator);
        mAdapter = new MyContactInfoListAdapter(mContext, contactInfoList);
        sortListView.setAdapter(mAdapter);

        /*列表点击监听*/
        sortListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Bundle mBundle = new Bundle();
                mBundle.putString(ExtraAndResult.EXTRA_NAME, contactInfoList.get(position).getName());
                mBundle.putString(ExtraAndResult.EXTRA_DATA, contactInfoList.get(position).getPhono());
                app.startActivity(MyContactMailList.this, CustomerAddActivity_.class, MainApp.ENTER_TYPE_RIGHT, true, mBundle);
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            /*回退*/
            case R.id.img_title_left:
                onBackPressed();
                break;

            /*搜索*/
            case R.id.img_title_search_right:
                if (null == contactInfoList || contactInfoList.size() == 0) {
                    Toast("你的通讯录没有联系人!");
                    return;
                }
                Intent mIntent = new Intent(MyContactMailList.this, MyContactMailSerachList.class);
                mIntent.putExtra(ExtraAndResult.EXTRA_OBJ, (Serializable) contactInfoList);
                startActivity(mIntent);
                finish();
                break;

        }
    }

    class PinyinComparator implements Comparator<MyContactInfo> {

        public int compare(MyContactInfo o1, MyContactInfo o2) {
            //这里主要是用来对ListView里面的数据根据ABCDEFG...来排序
            if (o2.getSortLetters().equals("#")) {
                return -1;
            } else if (o1.getSortLetters().equals("#")) {
                return 1;
            } else {
                return o1.getSortLetters().compareTo(o2.getSortLetters());
            }
        }
    }
}
