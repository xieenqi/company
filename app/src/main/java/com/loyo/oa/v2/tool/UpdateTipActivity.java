package com.loyo.oa.v2.tool;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.service.CheckUpdateService;


public class UpdateTipActivity extends Activity {

    CheckUpdateService.UpdateInfo mUpdateInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_tip);

        mUpdateInfo = (CheckUpdateService.UpdateInfo) getIntent().getSerializableExtra("data");
        showNoticeDialog();
    }

    private void showNoticeDialog() {
        setFinishOnTouchOutside(false);
        setTitle("软件版本更新");
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        TextView message = (TextView) findViewById(R.id.message);
        message.setText(mUpdateInfo.appDescription.replace("\\n", "\n"));

        TextView textView = (TextView) findViewById(R.id.download);

        builder.setPositiveButton("", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        textView.setText("下载");
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startUpdateService(CheckUpdateService.PARAM_START_DOWNLOAD);
                finish();
            }
        });

        Button buttonCannel = (Button) findViewById(R.id.cancel);
        buttonCannel.setVisibility(View.VISIBLE);
        buttonCannel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startUpdateService(CheckUpdateService.PARAM_STOP_SELF);
                finish();
            }
        });

    }

    private void startUpdateService(int request) {
        Intent intent = new Intent(this, CheckUpdateService.class);
        intent.putExtra("data", request);
        startService(intent);
    }

}
