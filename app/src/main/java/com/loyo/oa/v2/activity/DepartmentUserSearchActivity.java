package com.loyo.oa.v2.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.loyo.oa.v2.tool.ViewHolder;
import com.loyo.oa.v2.tool.customview.pullToRefresh.PullToRefreshListView;
import com.loyo.oa.v2.R;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.beans.User;
import com.loyo.oa.v2.beans.UserGroupData;
import com.loyo.oa.v2.common.Common;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.tool.StringUtil;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;

public class DepartmentUserSearchActivity extends Activity {

    EditText edt_search;
    PullToRefreshListView listView;
    ArrayList<User> data = new ArrayList<>();
    ArrayList<User> resultData = new ArrayList<>();

    ViewGroup img_title_left;

    private int type = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_search);
        Intent intent = getIntent();
        if (null != intent) {
            type = intent.hasExtra("type") ? intent.getIntExtra("type", -1) : -1;
        }

        for (UserGroupData d : Common.getLstUserGroupData()) {
            data.addAll(d.getLstUser());
        }

        edt_search = (EditText) findViewById(R.id.edt_search);
        edt_search.addTextChangedListener(new TextWatcher() {

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

        listView = (PullToRefreshListView) findViewById(R.id.listView_customer);
        listView.setAdapter(adapter);
        listView.setPullToRefreshEnabled(false);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                User user = resultData.get((int) id);
                if (type == 1) {
                    Bundle b = new Bundle();
                    b.putSerializable("user", user);
                    MainApp.getMainApp().startActivity(DepartmentUserSearchActivity.this, ContactInfoActivity_.class, MainApp.ENTER_TYPE_RIGHT, false, b);
                } else {
                    Intent intent = new Intent();
                    intent.putExtra(User.class.getName(), user);
                    MainApp.getMainApp().finishActivity(DepartmentUserSearchActivity.this, MainApp.ENTER_TYPE_LEFT, RESULT_OK, intent);
                }
            }

        });

        img_title_left = (ViewGroup) findViewById(R.id.img_title_left);
        img_title_left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //关闭键盘
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm.isActive()) {
                    imm.hideSoftInputFromWindow(edt_search.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                }
                onBackPressed();
            }
        });

        findViewById(R.id.tv_search).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doSearch();
            }
        });
    }

    String key;

    void doSearch() {
        key = edt_search.getText().toString().trim();
        if (StringUtil.isEmpty(key)) {
            Global.Toast("请输入查询姓名!");
            return;
        }

        resultData.clear();
        if (data == null || data.size() == 0) {
            return;
        }

        for (User u : data) {
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
                convertView = LayoutInflater.from(DepartmentUserSearchActivity.this).inflate(R.layout.item_contacts_child, null, false);
            }
            User user = resultData.get(position);
            ImageView img = ViewHolder.get(convertView, R.id.img);
            TextView tv_content = ViewHolder.get(convertView, R.id.tv_name);
            TextView tv_position = ViewHolder.get(convertView, R.id.tv_position);

            tv_content.setText(user.getRealname());
            String departmentName = user.departmentsName;
            if (null != user && null != user.shortPosition && !TextUtils.isEmpty(user.shortPosition.getName())) {
                tv_position.setText(departmentName + " | " + user.shortPosition.getName());
            }
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
}
