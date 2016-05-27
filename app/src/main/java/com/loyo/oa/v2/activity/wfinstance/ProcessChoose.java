package com.loyo.oa.v2.activity.wfinstance;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import com.loyo.oa.v2.R;
import com.loyo.oa.v2.adapter.ProcessChooseAdapter;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.beans.WfTemplate;
import com.loyo.oa.v2.tool.BaseActivity;
import java.util.ArrayList;

/**
 * 【流程选择】  页面
 * Created16/5/18 yyy
 */
public class ProcessChoose extends BaseActivity {

    private ListView lv_deptList;
    private LinearLayout img_title_left;
    private Intent intent;
    private ArrayList<WfTemplate> wfTemplateArrayList;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_department_choose);
        initUI();
    }

    private void initUI() {
        super.setTitle("选择流程");
        intent = getIntent();
        wfTemplateArrayList = (ArrayList<WfTemplate>) intent.getExtras().getSerializable("data");

        img_title_left = (LinearLayout) findViewById(R.id.img_title_left);
        img_title_left.setOnClickListener(click);
        lv_deptList = (ListView) findViewById(R.id.lv_deptList);
        final ProcessChooseAdapter adapter=new ProcessChooseAdapter(ProcessChoose.this,wfTemplateArrayList);
        lv_deptList.setAdapter(adapter);
        lv_deptList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(final AdapterView<?> parent,final View view,final int position,final long id) {
                Intent intent = new Intent();
                intent.putExtra("position",position);
                app.finishActivity(ProcessChoose.this, MainApp.ENTER_TYPE_LEFT, RESULT_OK, intent);
            }
        });
    }

    private View.OnClickListener click = new View.OnClickListener() {
        @Override
        public void onClick(final View v) {
            switch (v.getId()) {
                case R.id.img_title_left:
                    app.finishActivity(ProcessChoose.this, MainApp.ENTER_TYPE_LEFT, RESULT_CANCELED, null);
                    break;

                default:
                    break;
            }
        }
    };
}
