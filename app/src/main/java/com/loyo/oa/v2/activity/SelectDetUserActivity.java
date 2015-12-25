package com.loyo.oa.v2.activity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.tool.BaseActivity;

/**
 * 部门 人员选择
 * Created by yyy on 15/12/25.
 */
public class SelectDetUserActivity extends BaseActivity {

    public ListView leftLv,rightLv;
    public LinearLayout llback;
    public Button btnSure;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selectdetuser);
        initView();
    }

    /**
     * 初始化
     * */
    public void initView(){
        leftLv = (ListView)findViewById(R.id.lv_selectdetuser_left);
        rightLv = (ListView)findViewById(R.id.lv_selectdetuser_right);
        btnSure = (Button)findViewById(R.id.btn_title_right);
        llback = (LinearLayout)findViewById(R.id.ll_back);
    }
}
