package com.loyo.oa.v2.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.adapter.SelectDetAdapter;
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

    public ListView leftLv,rightLv;
    public LinearLayout llback;
    public Button btnSure;
    public SelectDetAdapter mAdapter;
    public ArrayList<User> userList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selectdetuser);
        initView();
    }

    /**
     * 初始化
     * */
    void initView(){
        userList = new ArrayList<>();
        leftLv = (ListView)findViewById(R.id.lv_selectdetuser_left);
        rightLv = (ListView)findViewById(R.id.lv_selectdetuser_right);
        btnSure = (Button)findViewById(R.id.btn_title_right);
        llback = (LinearLayout)findViewById(R.id.ll_back);

        mAdapter = new SelectDetAdapter(this, Common.getLstDepartment());
        leftLv.setAdapter(mAdapter);
        leftTvOnClick();
    }

    /**
     * 左边Lv监听
     * */
    void leftTvOnClick(){
        leftLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                String Id = Common.getLstDepartment().get(position).getId();
                LogUtil.dll("部门大小:"+Common.getLstDepartment(Id).size());
                LogUtil.dll("人员大小:" + Common.getListUser(Id).size());
                assUserList(Common.getLstDepartment(Id), Common.getListUser(Id), Id);

            }
        });
    }


    void assUserList(ArrayList<Department> departments,ArrayList<User> users,String Id){

        if(users.size() != 0){
            userList.addAll(users);
        }

        if(departments.size() != 0){

        }

        for (int i = 0;i<userList.size();i++){
            LogUtil.dll("name:"+userList.get(i).getRealname());
            LogUtil.dll("id:"+userList.get(i).getId());
            LogUtil.dll("img:"+userList.get(i).getAvatar());
        }

    }
}
