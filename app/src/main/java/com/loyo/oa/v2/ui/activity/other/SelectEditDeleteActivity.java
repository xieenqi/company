package com.loyo.oa.v2.ui.activity.other;

import android.app.Activity;
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
import com.loyo.oa.v2.tool.ExitActivity;
import com.loyo.oa.v2.tool.LogUtil;
import com.loyo.oa.v2.ui.customview.GeneralPopView;

/**
 * 【业务弹窗】
 *  任务 项目 审批 报告详情 弹窗菜单
 */
public class SelectEditDeleteActivity extends Activity implements OnClickListener {

    private Button btn_delete, btn_edit, btn_cancel, btn_extra;
    private LinearLayout layout;
    private Intent intent;
    private Intent mIntent;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
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

            public void onClick(final View v) {
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
            } else {
                String editText = intent.getStringExtra("editText");
                btn_edit.setText(TextUtils.isEmpty(editText) ? "编 辑" : editText);
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
    public boolean onTouchEvent(final MotionEvent event) {
        finish();
        return true;
    }

    public void onClick(final View v) {
        switch (v.getId()) {
            //编辑
            case R.id.btn_edit:
                LogUtil.dll("点击编辑");
                mIntent = new Intent();
                intent.putExtra("edit", true);
                setResult(RESULT_OK, intent);
                finish();
                break;

            //删除
            case R.id.btn_delete:
                final GeneralPopView generalPopView = new GeneralPopView(this, true);
                generalPopView.show();
                generalPopView.setMessage("确认删除?");
                generalPopView.setCanceledOnTouchOutside(true);
                //确定
                generalPopView.setSureOnclick(new OnClickListener() {
                    @Override
                    public void onClick(final View view) {
                        generalPopView.dismiss();
                        mIntent = new Intent();
                        mIntent.putExtra("delete", true);
                        setResult(RESULT_OK, mIntent);
                        finish();
                    }
                });
                //取消
                generalPopView.setCancelOnclick(new OnClickListener() {
                    @Override
                    public void onClick(final View view) {
                        generalPopView.dismiss();
                    }
                });
                break;

            //复制
            case R.id.btn_extra:
                mIntent = new Intent();
                mIntent.putExtra("extra", true);
                setResult(RESULT_OK, mIntent);
                finish();
                break;

            //取消
            case R.id.btn_cancel:
                LogUtil.dll("点击btn_cancel");
                finish();
                break;

            default:
                break;
        }
    }

}
