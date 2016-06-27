package com.loyo.oa.v2.ui.activity.wfinstance;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.ui.activity.other.adapter.DepartmentChooseAdapter;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.tool.BaseActivity;

/**
 * 【部门选择】  页面
 * Created15/12/3.xnq
 */
public class DepartmentChooseActivity extends BaseActivity {

    ListView lv_deptList;
    LinearLayout img_title_left;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_department_choose);
        initUI();
    }

    private void initUI() {
        super.setTitle("部门选择");
        img_title_left = (LinearLayout) findViewById(R.id.img_title_left);
        img_title_left.setOnClickListener(click);
        lv_deptList = (ListView) findViewById(R.id.lv_deptList);
        final DepartmentChooseAdapter adapter=new DepartmentChooseAdapter(DepartmentChooseActivity.this, MainApp.user.depts);
        lv_deptList.setAdapter(adapter);
        lv_deptList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(final AdapterView<?> parent,final View view,final int position,final long id) {
                Intent intent=new Intent();
                intent.putExtra(DepartmentChooseActivity.class.getName(), adapter.getData().get(position));
                app.finishActivity(DepartmentChooseActivity.this, MainApp.ENTER_TYPE_LEFT, RESULT_OK, intent);
            }
        });
    }

    private View.OnClickListener click = new View.OnClickListener() {
        @Override
        public void onClick(final View v) {
            switch (v.getId()) {
                case R.id.img_title_left:
                    app.finishActivity(DepartmentChooseActivity.this, MainApp.ENTER_TYPE_LEFT, RESULT_CANCELED, null);
                    break;

                default:
                    break;
            }
        }
    };
}
