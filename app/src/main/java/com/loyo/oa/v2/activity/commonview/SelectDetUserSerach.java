package com.loyo.oa.v2.activity.commonview;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.loyo.oa.v2.R;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.beans.User;
import com.loyo.oa.v2.common.ExtraAndResult;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.tool.LogUtil;
import com.loyo.oa.v2.tool.StringUtil;
import com.loyo.oa.v2.tool.ViewHolder;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;

/**
 * 新版选人搜索
 * Created by yyy on 16/1/11.
 */
public class SelectDetUserSerach extends Activity{


    private TextView tv_selectuser_search;
    private EditText edt_selectuser_search;
    private RelativeLayout img_title_left;
    private ListView lv_selectuser_serach;

    private ArrayList<User> userAllList;
    private ArrayList<User> resultData = new ArrayList<>();
    private String key;
    private int selectType;
    private MainApp app = MainApp.getMainApp();

    private Intent mIntent;
    private Bundle mBundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selectdetuserserach);
        initView();

    }

    /*初始化*/
    public void initView(){

         userAllList = MainApp.selectAllUsers;
         selectType = getIntent().getExtras().getInt(ExtraAndResult.STR_SELECT_TYPE);
         tv_selectuser_search = (TextView) findViewById(R.id.tv_selectuser_search);
         edt_selectuser_search = (EditText) findViewById(R.id.edt_selectuser_search);
         lv_selectuser_serach = (ListView) findViewById(R.id.lv_selectuser_serach);
         img_title_left = (RelativeLayout) findViewById(R.id.img_title_left);

        /*输入框监听*/
        edt_selectuser_search.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {

            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                doSearch();
            }
        });

        /*适配器设置*/
        lv_selectuser_serach.setAdapter(adapter);
        lv_selectuser_serach.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                 mIntent = new Intent();
                 mBundle = new Bundle();
                switch(selectType){

                    /*参与人*/
                    case ExtraAndResult.TYPE_SELECT_MULTUI:

                        mBundle.putString("userId",resultData.get(position).getId());
                        mBundle.putInt(ExtraAndResult.STR_SELECT_TYPE, ExtraAndResult.TYPE_SELECT_MULTUI);
                        mIntent.putExtras(mBundle);
                        app.finishActivity(SelectDetUserSerach.this, MainApp.ENTER_TYPE_LEFT, ExtraAndResult.request_Code, mIntent);

                        break;

                    /*负责人*/
                    case ExtraAndResult.TYPE_SELECT_SINGLE:

                        mBundle.putSerializable(User.class.getName(), resultData.get(position));
                        mBundle.putInt(ExtraAndResult.STR_SELECT_TYPE, ExtraAndResult.TYPE_SELECT_SINGLE);
                        mIntent.putExtras(mBundle);
                        app.finishActivity(SelectDetUserSerach.this, MainApp.ENTER_TYPE_LEFT, ExtraAndResult.request_Code, mIntent);
                        break;

                    /*编辑参与人*/
                    case ExtraAndResult.TYPE_SELECT_EDT:

                        mBundle.putString("userId",resultData.get(position).getId());
                        mBundle.putInt(ExtraAndResult.STR_SELECT_TYPE, ExtraAndResult.TYPE_SELECT_MULTUI);
                        mIntent.putExtras(mBundle);
                        app.finishActivity(SelectDetUserSerach.this, MainApp.ENTER_TYPE_LEFT, ExtraAndResult.request_Code, mIntent);

                        break;

                }
            }
        });

        /*返回*/
        img_title_left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //关闭键盘
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm.isActive()) {
                    imm.hideSoftInputFromWindow(edt_selectuser_search.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                }
                onBackPressed();
            }
        });

        /*搜索操作*/
        tv_selectuser_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LogUtil.dll("点击搜索");
                doSearch();
            }
        });
    }

    /**
     * 搜索操作
     * */

    void doSearch() {
        key = edt_selectuser_search.getText().toString().trim();
        if (StringUtil.isEmpty(key)) {
            Global.Toast("请输入查询姓名!");
            return;
        }

        resultData.clear();
        if (userAllList == null || userAllList.size() == 0) {
            return;
        }

        for (User u : userAllList) {
            if (u == null) {
                continue;
            } else if (u.getRealname() != null && u.getRealname().contains(key)) {
                resultData.add(u);
                continue;
            } else if (u.fullPinyin != null && u.fullPinyin.contains(key)) {
                resultData.add(u);
                continue;
            } else if (u.simplePinyin != null && u.simplePinyin.contains(key)) {
                resultData.add(u);
                continue;
            }
        }

        if (resultData.size() > 0) {
            adapter.notifyDataSetChanged();
        }
    }


    /**
     * 适配器
     * */

    BaseAdapter adapter = new BaseAdapter() {
        @Override
        public int getCount() {
            return resultData.size();
        }

        @Override
        public Object getItem(int position) {
            return resultData.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (null == convertView) {
                convertView = LayoutInflater.from(SelectDetUserSerach.this).inflate(R.layout.item_contacts_child, null, false);
            }
            User user = resultData.get(position);
            ImageView img = ViewHolder.get(convertView, R.id.img);
            TextView tv_content = ViewHolder.get(convertView, R.id.tv_name);
            TextView tv_position = ViewHolder.get(convertView, R.id.tv_position);

            tv_content.setText(user.getRealname());
            String deptName,workName;

            try{
                deptName = user.depts.get(0).getShortDept().getName();
            }catch(NullPointerException e){
                e.printStackTrace();
                deptName = "无";
            }

            try{
                workName = user.role.name;
            }catch(NullPointerException e){
                e.printStackTrace();
                workName = "无";
            }

            tv_position.setText(deptName+"  "+workName);

            if (!TextUtils.isEmpty(user.avatar)) {
                ImageLoader.getInstance().displayImage(user.avatar, img);
            }

            if (position == resultData.size() - 1) {
                ViewHolder.get(convertView, R.id.devider).setVisibility(View.GONE);
            } else {
                ViewHolder.get(convertView, R.id.devider).setVisibility(View.VISIBLE);
            }
            return convertView;
        }
    };

    /**
     * 返回
     */
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
