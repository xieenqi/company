package com.loyo.oa.v2.activityui.customer;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.customer.adapter.MyContactInfoListAdapter;
import com.loyo.oa.v2.activityui.customer.event.ContactMaillistRushEvent;
import com.loyo.oa.v2.activityui.customer.model.MyContactInfo;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.common.ExtraAndResult;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.common.event.AppBus;
import com.loyo.oa.v2.customview.SideBar;
import com.loyo.oa.v2.customview.SweetAlertDialogView;
import com.loyo.oa.v2.tool.BaseActivity;
import com.loyo.oa.v2.tool.ContactInfoUtil;
import com.loyo.oa.v2.tool.Utils;
import java.io.Serializable;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import cn.pedant.SweetAlert.SweetAlertDialog;

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

    private int pageForm = 0;//1:来自联系人 2:来自新建客户
    private boolean isEdit;  //新建联系人已被编辑

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

        if (PackageManager.PERMISSION_GRANTED ==
                getPackageManager().checkPermission("android.permission.READ_CONTACTS", "com.loyo.oa.v2")
                && PackageManager.PERMISSION_GRANTED ==
                getPackageManager().checkPermission("android.permission.WRITE_CONTACTS", "com.loyo.oa.v2")) {
        } else {
            final SweetAlertDialogView sDialog = new SweetAlertDialogView(this);
            sDialog.alertHandle(new SweetAlertDialog.OnSweetClickListener() {
                @Override
                public void onClick(SweetAlertDialog sweetAlertDialog) {
                    sDialog.sweetAlertDialog.dismiss();
                    finish();
                }
            }, new SweetAlertDialog.OnSweetClickListener() {
                @Override
                public void onClick(SweetAlertDialog sweetAlertDialog) {
                    sDialog.sweetAlertDialog.dismiss();
                    Utils.doSeting(MyContactMailList.this);
                    finish();
                }
            },"提示","需要使用通讯录读写权限\n请在”设置”>“应用”>“权限”中配置权限");
            return;
        }

        pageForm = getIntent().getIntExtra(ExtraAndResult.EXTRA_NAME,0);
        isEdit   = getIntent().getBooleanExtra(ExtraAndResult.EXTRA_OBJ,false);

        if(pageForm == 0){
            Toast("参数不完整");
            finish();
        }

        if(isEdit){
            final SweetAlertDialogView sDialog = new SweetAlertDialogView(this);
            sDialog.alertHandle(new SweetAlertDialog.OnSweetClickListener() {
                @Override
                public void onClick(SweetAlertDialog sweetAlertDialog) {
                    sDialog.sweetAlertDialog.dismiss();
                    finish();
                }
            }, new SweetAlertDialog.OnSweetClickListener() {
                @Override
                public void onClick(SweetAlertDialog sweetAlertDialog) {
                    sDialog.sweetAlertDialog.dismiss();
                }
            },"提示","您确定要覆盖已编辑的联系人信息吗?");
        }

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

                /*来自联系人*/
                if(pageForm == 1){
                    ContactMaillistRushEvent event = new ContactMaillistRushEvent();
                    event.bundle = new Bundle();
                    event.bundle.putString(ExtraAndResult.EXTRA_NAME, contactInfoList.get(position).getName());
                    event.bundle.putString(ExtraAndResult.EXTRA_DATA, contactInfoList.get(position).getPhono());
                    AppBus.getInstance().post(event);
                    finish();
                }
                /*来自新建客户*/
                else if(pageForm == 2){
                    Bundle mBundle = new Bundle();
                    mBundle.putString(ExtraAndResult.EXTRA_NAME, contactInfoList.get(position).getName());
                    mBundle.putString(ExtraAndResult.EXTRA_DATA, contactInfoList.get(position).getPhono());
                    app.startActivity(MyContactMailList.this, CustomerAddActivity_.class, MainApp.ENTER_TYPE_RIGHT, true, mBundle);
                }

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
