package com.loyo.oa.v2.activity.wfinstance;

import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.tool.BaseActivity;

/**
 * 【部门选择】  页面
 * Created by pj on 15/12/3.xnq
 */
public class DepartmentChoose extends BaseActivity {

    ListView lv_deptList;
    RelativeLayout img_title_left;
    RelativeLayout img_title_right;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_department_choose);

        initUI();
    }

    private void initUI() {
        super.setTitle("部门选择");
        img_title_left = (RelativeLayout) findViewById(R.id.img_title_left);
        img_title_left.setOnClickListener(click);
        img_title_right = (RelativeLayout) findViewById(R.id.img_title_right);
        img_title_right.setVisibility(View.GONE);
        lv_deptList = (ListView) findViewById(R.id.lv_deptList);
        lv_deptList.setAdapter(new  DepartmentChooseAdapter());
    }

    private View.OnClickListener click = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.img_title_left:
                    app.finishActivity(DepartmentChoose.this, MainApp.ENTER_TYPE_LEFT, RESULT_CANCELED, null);
                    break;
            }
        }
    };

}
