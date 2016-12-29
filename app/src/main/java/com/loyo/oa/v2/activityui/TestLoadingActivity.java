package com.loyo.oa.v2.activityui;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.loyo.oa.common.utils.LoyoUIThread;
import com.loyo.oa.hud.progress.LoyoProgressHUD;
import com.loyo.oa.v2.R;

public class TestLoadingActivity extends Activity implements View.OnClickListener {

    private Button actionButton;
    private LoyoProgressHUD hud;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_loading);
        actionButton = (Button) findViewById(R.id.action);
        actionButton.setOnClickListener(this);
    }

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        if (R.id.action == v.getId()) {
//            DialogHelp.showLoading(this, "");
//            LoyoUIThread.runAfterDelay(new Runnable() {
//                @Override
//                public void run() {
//                    DialogHelp.setLoadingMsg("this is message");
//                    LoyoUIThread.runAfterDelay(new Runnable() {
//                        @Override
//                        public void run() {
//                            DialogHelp.setLoadingMsg("产品列表、详情页\n" +
//                                    "1、【WEB】产品列表分类及名称可以搜索，可批量设置分类；\n" +
//                                    "2、【WEB】增加产品详情页面，从产品列表点击进入；\n" +
//                                    "3、【WEB、APP】新建订单（选择产品后）可查看产品的所有相关信息。");
//                        }
//                    }, 3000);
//                }
//            }, 3000);
            hud = LoyoProgressHUD.commitHUD(this);
            hud.show();
            LoyoUIThread.runAfterDelay(new Runnable() {
                @Override
                public void run() {
                    hud.dismissWithError();
                }
            }, 2000);
        }
    }
}
