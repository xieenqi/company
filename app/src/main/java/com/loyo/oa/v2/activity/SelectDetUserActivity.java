package com.loyo.oa.v2.activity;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.adapter.SelectDetAdapter;
import com.loyo.oa.v2.adapter.SelectUserAdapter;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.beans.Department;
import com.loyo.oa.v2.beans.NewUser;
import com.loyo.oa.v2.beans.User;
import com.loyo.oa.v2.common.Common;
import com.loyo.oa.v2.tool.BaseActivity;
import com.loyo.oa.v2.tool.LogUtil;

import java.util.ArrayList;

/**
 * 部门 人员选择
 * Created by yyy on 15/12/25.
 */
public class SelectDetUserActivity extends BaseActivity {

    public ListView leftLv, rightLv;
    public LinearLayout llback;
    public Button btnSure;
    public SelectDetAdapter mDetAdapter;
    public SelectUserAdapter mUserAdapter;
    public ArrayList<User> userList;
    public Context mContext;

    public Handler mHandler = new Handler(){
        public void handleMessage(Message msg){
            if(msg.what == 0x01)
            mUserAdapter.notifyDataSetChanged();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        setContentView(R.layout.activity_selectdetuser);
        initView();
    }

    /**
     * 初始化
     */
    void initView() {

        userList = new ArrayList<>();
        leftLv = (ListView) findViewById(R.id.lv_selectdetuser_left);
        rightLv = (ListView) findViewById(R.id.lv_selectdetuser_right);
        btnSure = (Button) findViewById(R.id.btn_title_right);
        llback = (LinearLayout) findViewById(R.id.ll_back);

        /*左侧Lv初始化*/
        mDetAdapter = new SelectDetAdapter(mContext, Common.getLstDepartment());
        leftLv.setAdapter(mDetAdapter);
        leftTvOnClick();

        /*右侧Lv初始化*/
        setData(0);
        mUserAdapter = new SelectUserAdapter(mContext,userList);
        rightLv.setAdapter(mUserAdapter);

    }

    /**
     * 左边Lv监听
     */
    void leftTvOnClick() {
        leftLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                setData(position);
                mHandler.sendEmptyMessage(0x01);

            }
        });
    }

    /**
     * 数据设置
     * */
    void setData(int position){
        String Id = Common.getLstDepartment().get(position).getId();
        userList.clear();
        assUserList(Common.getLstDepartment(Id), Common.getListUser(Id));
    }

    /**
     * 遍历获取所有人员数据
     * */
    void assUserList(ArrayList<Department> departments, ArrayList<User> users) {

        if (users.size() != 0) {
            userList.addAll(users);
        }

        if (departments.size() != 0) {
            for (int i = 0; i < departments.size(); i++) {
                String id = departments.get(i).getId();
                ArrayList<Department> dept = Common.getLstDepartment(id);
                ArrayList<User> user = Common.getListUser(id);
                assUserList(dept, user);
            }
        }else{
            return;
        }
    }
}
