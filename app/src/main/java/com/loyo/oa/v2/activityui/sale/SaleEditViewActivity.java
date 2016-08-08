package com.loyo.oa.v2.activityui.sale;

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

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.tool.ExitActivity;
import com.loyo.oa.v2.customview.GeneralPopView;

/**
 * 机会详情 菜单弹出框
 * 功   能:编辑 删除
 */

public class SaleEditViewActivity extends Activity implements OnClickListener {

    private Button btn_delete, btn_edit, btn_cancel, btn_extra;
    private LinearLayout layout;
    private Intent mIntent;
    private boolean isDelete = true;
    private boolean isEdit = true;
    private String isExra;


    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ExitActivity.getInstance().addActivity(this);
        setContentView(R.layout.activity_saleedit_view);
        isDelete = getIntent().getBooleanExtra("isDelete", false);
        isEdit = getIntent().getBooleanExtra("isEdit", false);
        isExra = getIntent().getStringExtra("isExra");
        getWindow().setLayout(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);

        btn_delete = (Button) this.findViewById(R.id.btn_delete);
        btn_edit = (Button) this.findViewById(R.id.btn_edit);
        btn_cancel = (Button) this.findViewById(R.id.btn_cancel);
        btn_extra = (Button) this.findViewById(R.id.btn_extra);
        layout = (LinearLayout) findViewById(R.id.pop_layout);

        // 添加选择窗口范围监听可以优先获取触点，即不再执行onTouchEvent()函数，点击其他地方时执行onTouchEvent()函数销毁Activity
        layout.setOnClickListener(new OnClickListener() {
            public void onClick(final View v) {
//                Toast("提示：点击窗口外部关闭窗口！");
            }

        });

        // 添加按钮监听
        btn_cancel.setOnClickListener(this);
        btn_edit.setOnClickListener(this);
        btn_delete.setOnClickListener(this);
        btn_extra.setOnClickListener(this);
        if (isDelete) {
            btn_delete.setVisibility(View.VISIBLE);
        } else {
            btn_delete.setVisibility(View.GONE);
        }
        if (isEdit) {
            btn_edit.setVisibility(View.VISIBLE);
        } else {
            btn_edit.setVisibility(View.GONE);
        }
        if (!TextUtils.isEmpty(isExra)) {
            btn_extra.setVisibility(View.VISIBLE);
            btn_extra.setText(isExra);
        } else {
            btn_extra.setVisibility(View.GONE);
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
                mIntent = new Intent();
                mIntent.putExtra("edit", true);
                setResult(RESULT_OK, mIntent);
                finish();
                break;

            //删除
            case R.id.btn_delete:
                GeneralPopView generalPopView = new GeneralPopView(this, true);
                generalPopView.show();
                generalPopView.setMessage("确认删除?");
                generalPopView.setCanceledOnTouchOutside(true);
//                showGeneralDialog(true, true, "确认删除?");
                //确定
                generalPopView.setSureOnclick(new OnClickListener() {
                    @Override
                    public void onClick(final View view) {
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
                        finish();
                    }
                });
                break;

            //取消
            case R.id.btn_cancel:
                finish();
                break;

            //自定义
            case R.id.btn_extra:
                mIntent = new Intent();
                mIntent.putExtra("extra", true);
                setResult(RESULT_OK, mIntent);
                finish();
                break;

            default:
                break;
        }
    }

}
