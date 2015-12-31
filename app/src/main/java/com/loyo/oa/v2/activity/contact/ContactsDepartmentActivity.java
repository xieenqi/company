package com.loyo.oa.v2.activity.contact;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.fragment.ContactsSubdivisionsFragment;
import com.loyo.oa.v2.tool.BaseFragmentActivity;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

/**
 * com.loyo.oa.v2.activity
 * 描述 :某个部门人员列表
 * 作者 : ykb
 * 时间 : 15/8/27.
 */
@EActivity(R.layout.activity_contacts_department)
public class ContactsDepartmentActivity extends BaseFragmentActivity {
    @ViewById ViewGroup layout_back;
    @ViewById TextView tv_title;

    @Extra String depId;
    @Extra String depName;

    @AfterViews
    void initViews() {
        setTouchView(-1);
        tv_title.setVisibility(View.VISIBLE);
        tv_title.setText(depName);
        layout_back.setOnTouchListener(Global.GetTouch());

        Bundle bundle = new Bundle();
        bundle.putString("depId", depId);
//        if (depId == MainApp.user.getDepts().get(0).getId()) {
//            getSupportFragmentManager().beginTransaction().add(R.id.container, Fragment.instantiate(this, ContactsInDepartmentFragment.class.getName(), bundle)).commit();
//        } else {
            getSupportFragmentManager().beginTransaction().add(R.id.container, Fragment.instantiate(this, ContactsSubdivisionsFragment.class.getName(), bundle)).commit();
//        }
    }

    @Click(R.id.layout_back)
    void onClick(View v) {
        app.finishActivity(this, MainApp.ENTER_TYPE_ZOOM_IN, RESULT_CANCELED, null);
    }
}
