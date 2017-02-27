package com.loyo.oa.v2.order.activity;

import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.worksheet.bean.WorksheetAddModel;
import com.loyo.oa.v2.activityui.worksheet.fragment.WorksheetAddFragment;

import java.util.List;

public class WorksheetAddTestActivity extends AppCompatActivity implements WorksheetAddFragment.OnFragmentEventListener {

    private static final String TAG="WorkshtActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_worksheet_add_test);
        FragmentManager supportFragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = supportFragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.test_fl,WorksheetAddFragment.newInstance(null),"add");
        fragmentTransaction.commit();
    }

    @Override
    public void onSubmit(List<WorksheetAddModel> data) {
        Log.i(TAG, "onSubmit: ");
        int size=data.size();
        for (int i = 0; i < size; i++) {
            WorksheetAddModel worksheetAddModel = data.get(i);
            Log.i(TAG, "onClick: "+worksheetAddModel.toString());
        }
    }

    @Override
    public void onBack() {
        Log.i(TAG, "onBack: ");

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i(TAG, "onActivityResult: !!!");
    }
}
