package com.loyo.oa.v2.activityui.customer;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.customer.adapter.MyContactInfoSerachAdapter;
import com.loyo.oa.v2.activityui.customer.model.MyContactInfo;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.common.ExtraAndResult;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.tool.BaseActivity;
import com.loyo.oa.v2.tool.LogUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 【手机通讯录】搜索
 * Created by yyy on 16/10/28.
 */

public class MyContactMailSerachList extends BaseActivity implements View.OnClickListener{

    private RelativeLayout img_title_left;
    private EditText edt_search;
    private ImageView iv_clean;
    private View vs_nodata;
    private ListView expandableListView_search;
    private MyContactInfoSerachAdapter mAdapter;
    private List<MyContactInfo> contactInfoList;
    private List<MyContactInfo> serachResultName;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myphonemaillist_serach);
        initUI();
    }

    /**
     * 初始化
     * */
    private void initUI(){
        contactInfoList = (List<MyContactInfo>) getIntent().getSerializableExtra(ExtraAndResult.EXTRA_OBJ);
        serachResultName = new ArrayList<>();
        LogUtil.dee("contactInfoList:"+ MainApp.gson.toJson(contactInfoList));

        vs_nodata = findViewById(R.id.vs_nodata);
        img_title_left = (RelativeLayout)findViewById(R.id.img_title_left);
        edt_search = (EditText) findViewById(R.id.edt_search);
        iv_clean = (ImageView) findViewById(R.id.iv_clean);
        expandableListView_search = (ListView) findViewById(R.id.expandableListView_search);

        img_title_left.setOnClickListener(this);
        iv_clean.setOnClickListener(this);

        img_title_left.setOnTouchListener(Global.GetTouch());
        iv_clean.setOnTouchListener(Global.GetTouch());

        edt_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                doSearch(editable);
            }
        });

        /*列表点击监听*/
        expandableListView_search.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Bundle mBundle = new Bundle();
                mBundle.putString(ExtraAndResult.EXTRA_NAME, serachResultName.get(position).getName());
                mBundle.putString(ExtraAndResult.EXTRA_DATA, serachResultName.get(position).getPhono());
                app.startActivity(MyContactMailSerachList.this, CustomerAddActivity_.class, MainApp.ENTER_TYPE_RIGHT, true, mBundle);
            }
        });
    }

    /**
     * 搜索操作
     * */
    private void doSearch(Editable editable){
        serachResultName.clear();
        for(MyContactInfo myContactInfo : contactInfoList){
            if(myContactInfo.getName().contains(editable.toString())){
                serachResultName.add(myContactInfo);
            }
        }
        bindAdapter();
    }

    /**
     * 数据绑定
     * */
    private void bindAdapter(){
        if(null == mAdapter){
            mAdapter = new MyContactInfoSerachAdapter(mContext,serachResultName);
            expandableListView_search.setAdapter(mAdapter);
        }else{
            mAdapter.notifyDataSetChanged();
        }
        if(serachResultName.size() == 0){
            vs_nodata.setVisibility(View.VISIBLE);
        }else{
            vs_nodata.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){

            /*回退*/
            case R.id.img_title_left:
                onBackPressed();
                break;

            /*清除*/
            case R.id.iv_clean:
                serachResultName.clear();
                bindAdapter();
                break;

        }
    }
}
