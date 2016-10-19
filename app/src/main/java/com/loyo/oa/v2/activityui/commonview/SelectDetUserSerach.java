package com.loyo.oa.v2.activityui.commonview;


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
import android.widget.Toast;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.common.ExtraAndResult;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.db.OrganizationManager;
import com.loyo.oa.v2.db.bean.DBUser;
import com.loyo.oa.v2.tool.BaseActivity;
import com.loyo.oa.v2.tool.StringUtil;
import com.loyo.oa.v2.tool.ViewHolder;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

/**
 * 新版选人搜索
 * Created by yyy on 16/1/11.
 */
public class SelectDetUserSerach extends BaseActivity {


    private TextView tv_selectuser_search;
    private EditText edt_selectuser_search;
    private RelativeLayout img_title_left;
    private ListView lv_selectuser_serach;

    private List<DBUser> userAllList;
    private ArrayList<DBUser> resultData = new ArrayList<>();
    private String key;
    private int selectType;
    private MainApp app = MainApp.getMainApp();

    private Intent mIntent;
    private Bundle mBundle;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selectdetuserserach);
        initView();

    }

    /*初始化*/
    public void initView() {

        userAllList = OrganizationManager.shareManager().allUsers();
        selectType = getIntent().getExtras().getInt(ExtraAndResult.STR_SELECT_TYPE);
        tv_selectuser_search = (TextView) findViewById(R.id.tv_selectuser_search);
        edt_selectuser_search = (EditText) findViewById(R.id.edt_selectuser_search);
        lv_selectuser_serach = (ListView) findViewById(R.id.lv_selectuser_serach);
        img_title_left = (RelativeLayout) findViewById(R.id.img_title_left);

        /*输入框监听*/
        edt_selectuser_search.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(final CharSequence arg0, final int arg1, final int arg2, final int arg3) {

            }

            @Override
            public void beforeTextChanged(final CharSequence arg0, final int arg1, final int arg2, final int arg3) {
            }

            @Override
            public void afterTextChanged(final Editable s) {
                if (!TextUtils.isEmpty(s.toString())) {
                    doSearch();
                }
            }
        });

        /*适配器设置*/
        lv_selectuser_serach.setAdapter(adapter);
        lv_selectuser_serach.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(final AdapterView<?> parent, final View view, final int position, final long id) {

                mIntent = new Intent();
                mBundle = new Bundle();
                switch (selectType) {

                    /*参与人*/
                    case ExtraAndResult.TYPE_SELECT_MULTUI:

                        mBundle.putString("userId", resultData.get(position).id);
                        mBundle.putInt(ExtraAndResult.STR_SELECT_TYPE, ExtraAndResult.TYPE_SELECT_MULTUI);
                        mIntent.putExtras(mBundle);
                        app.finishActivity(SelectDetUserSerach.this, MainApp.ENTER_TYPE_LEFT, ExtraAndResult.REQUEST_CODE, mIntent);

                        break;

                    /*负责人*/
                    case ExtraAndResult.TYPE_SELECT_SINGLE:

                        // TODO:
                        mBundle.putSerializable(DBUser.class.getName(), resultData.get(position));
                        mBundle.putInt(ExtraAndResult.STR_SELECT_TYPE, ExtraAndResult.TYPE_SELECT_SINGLE);
                        mIntent.putExtras(mBundle);
                        app.finishActivity(SelectDetUserSerach.this, MainApp.ENTER_TYPE_LEFT, ExtraAndResult.REQUEST_CODE, mIntent);
                        break;

                    /*编辑参与人*/
                    case ExtraAndResult.TYPE_SELECT_EDT:

                        mBundle.putString("userId", resultData.get(position).id);
                        mBundle.putInt(ExtraAndResult.STR_SELECT_TYPE, ExtraAndResult.TYPE_SELECT_MULTUI);
                        mIntent.putExtras(mBundle);
                        app.finishActivity(SelectDetUserSerach.this, MainApp.ENTER_TYPE_LEFT, ExtraAndResult.REQUEST_CODE, mIntent);

                        break;
                    default:
                        break;
                }
            }
        });

        /*返回*/
        img_title_left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
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
            public void onClick(final View view) {
                doSearch();
            }
        });
    }

    /**
     * 搜索操作
     */

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

        for (DBUser u : userAllList) {
            if (u == null) {
                continue;
            } else if (u.name != null && u.name.contains(key)) {
                resultData.add(u);
                continue;
            } else if (u.fullPinyin != null && u.fullPinyin.startsWith(key)) {
                resultData.add(u);
                continue;
            } else if (u.simplePinyin != null && u.simplePinyin.startsWith(key)) {
                resultData.add(u);
                continue;
            }
            else if (u.shortDeptNames != null && u.shortDeptNames.contains(key)) {
                resultData.add(u);
                continue;
            }
        }

        if (resultData.size() == 0) {
            Toast.makeText(this, "未搜索到相关结果", Toast.LENGTH_SHORT).show();
        }

        adapter.notifyDataSetChanged();
    }


    /**
     * 适配器
     */

    BaseAdapter adapter = new BaseAdapter() {
        @Override
        public int getCount() {
            return resultData.size();
        }

        @Override
        public Object getItem(final int position) {
            return resultData.get(position);
        }

        @Override
        public long getItemId(final int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, final ViewGroup parent) {
            if (null == convertView) {
                convertView = LayoutInflater.from(SelectDetUserSerach.this).inflate(R.layout.item_contacts_child, null, false);
            }
            DBUser user = resultData.get(position);
            ImageView img = ViewHolder.get(convertView, R.id.img);
            TextView tv_content = ViewHolder.get(convertView, R.id.tv_name);
            TextView tv_position = ViewHolder.get(convertView, R.id.tv_position);

            tv_content.setText(user.name);

            tv_position.setText(user.shortDeptNames);

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
    public boolean onKeyDown(final int keyCode, final KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
