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

import java.util.ArrayList;
import java.util.List;

public class WorksheetAddTestActivity extends AppCompatActivity implements WorksheetAddFragment.OnFragmentEventListener {

    private static final String TAG="WorkshtActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_worksheet_add_test);
        FragmentManager supportFragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = supportFragmentManager.beginTransaction();
        ArrayList<WorksheetAddModel> list=new ArrayList<>();
        WorksheetAddModel a=new WorksheetAddModel();
        a.orderId="dfdsa";
        a.title="WorksheetAddModel";
        a.content="asdf";
        a.typeId="asdf";
        a.typeName="asdf";
        list.add(a);
        WorksheetAddModel b = a.clone();
        b.title="list.add(a);";
        list.add(b);

        fragmentTransaction.add(R.id.test_fl,WorksheetAddFragment.newInstance(list),"add");
        fragmentTransaction.commit();
    }

    @Override
    public void onWorksheetSubmit(List<WorksheetAddModel> data) {
        Log.i(TAG, "onWorksheetSubmit: ");
        int size=data.size();
        for (int i = 0; i < size; i++) {
            WorksheetAddModel worksheetAddModel = data.get(i);
            Log.i(TAG, "onClick: "+worksheetAddModel.toString());
        }
    }

    @Override
    public void onWorksheetBack() {
        Log.i(TAG, "onBack: ");

    }
}
