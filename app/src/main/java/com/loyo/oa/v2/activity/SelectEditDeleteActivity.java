package com.loyo.oa.v2.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.tool.BaseActivity;
import com.loyo.oa.v2.tool.ExitActivity;

/**
 * 任务详情编辑[ 选择【项目 的结束 编辑 删除】弹窗 ]
 * */
public class SelectEditDeleteActivity extends BaseActivity implements OnClickListener {

    private Button btn_delete, btn_edit, btn_cancel, btn_extra;
    private LinearLayout layout;
    private Intent intent;
    private Intent mIntent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ExitActivity.getInstance().addActivity(this);
        setContentView(R.layout.dialog_edit_delete);
        getWindow().setLayout(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);

        intent = getIntent();
        btn_delete = (Button) this.findViewById(R.id.btn_delete);
        btn_edit = (Button) this.findViewById(R.id.btn_edit);
        btn_cancel = (Button) this.findViewById(R.id.btn_cancel);
        btn_extra = (Button) this.findViewById(R.id.btn_extra);
        layout = (LinearLayout) findViewById(R.id.pop_layout);

        // 添加选择窗口范围监听可以优先获取触点，即不再执行onTouchEvent()函数，点击其他地方时执行onTouchEvent()函数销毁Activity
        layout.setOnClickListener(new OnClickListener() {

            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "提示：点击窗口外部关闭窗口！",
                        Toast.LENGTH_SHORT).show();
            }

        });

        // 添加按钮监听
        btn_cancel.setOnClickListener(this);
        btn_edit.setOnClickListener(this);
        btn_delete.setOnClickListener(this);

        if (intent != null && intent.getExtras() != null) {
            if (!intent.getBooleanExtra("edit", false)) {
                btn_edit.setVisibility(View.GONE);
            }else{
                String editText=intent.getStringExtra("editText");
                btn_edit.setText(TextUtils.isEmpty(editText)?"编 辑":editText);
            }

            if (!intent.getBooleanExtra("delete", false)) {
                btn_delete.setVisibility(View.GONE);
            }

            if (intent.hasExtra("extra")) {
                btn_extra.setText(intent.getStringExtra("extra"));
                btn_extra.setVisibility(View.VISIBLE);
                btn_extra.setOnClickListener(this);
            }
        }
    }

    @Override
    protected void onDestroy() {
        ExitActivity.getInstance().removeActivity(this);
        super.onDestroy();
    }

    // 实现onTouchEvent触屏函数但点击屏幕时销毁本Activity
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        finish();
        return true;
    }

    public void onClick(View v) {
        switch (v.getId()) {

            //编辑
            case R.id.btn_edit:
                mIntent = new Intent();
                intent.putExtra("edit", true);
                setResult(RESULT_OK, intent);
                finish();
                break;

            //删除
            case R.id.btn_delete:
                super.ConfirmDialog("提示", "确认删除?", new ConfirmDialogInterface() {
                    @Override
                    public void Confirm() {
                        mIntent = new Intent();
                        mIntent.putExtra("delete", true);
                        setResult(RESULT_OK, mIntent);
                        finish();
                    }
                });
                break;

            case R.id.btn_extra:
                mIntent = new Intent();
                mIntent.putExtra("extra", true);
                setResult(RESULT_OK, mIntent);
                finish();
                break;

            //取消
            case R.id.btn_cancel:
                finish();
                break;
        }
    }

}
