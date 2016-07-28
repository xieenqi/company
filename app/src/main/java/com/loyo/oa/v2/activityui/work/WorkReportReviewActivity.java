package com.loyo.oa.v2.activityui.work;

import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.beans.WorkReport;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.common.http.HttpErrorCheck;
import com.loyo.oa.v2.point.IWorkReport;
import com.loyo.oa.v2.tool.BaseActivity;
import com.loyo.oa.v2.tool.Config_project;
import com.loyo.oa.v2.tool.LogUtil;
import com.loyo.oa.v2.tool.RCallback;
import com.loyo.oa.v2.tool.RestAdapterFactory;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

import java.util.HashMap;

import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * com.loyo.oa.v2.activity
 * 描述 :报告点评界面
 * 作者 : ykb
 * 时间 : 15/10/13.
 */
@EActivity(R.layout.activity_workreport_review)
public class WorkReportReviewActivity extends BaseActivity {

    @ViewById ViewGroup img_title_left;
    @ViewById TextView tv_title_1;
    @ViewById EditText edt_content;
    @ViewById RatingBar ratingBar_workReport;
    @Extra
    String mWorkReportId;

    private float score;

    @AfterViews
    void initViews() {
        img_title_left.setOnTouchListener(Global.GetTouch());
        tv_title_1.setText("报告点评");

        ratingBar_workReport.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                LogUtil.d("分数 ：" + v);
                score = v;
            }
        });
    }

    @Click(R.id.btn_workreport_review)
    void review() {
        String content = edt_content.getText().toString();
//工作报告点评时，点评内容改为非必填（打分仍然为必填）16.07.22
//        if(TextUtils.isEmpty(content)){
//            Toast("点评内容不能为空!");
//            return;
//        }

        if (!(score > 0)) {
            Toast("请评分!");
            return;
        }

        HashMap<String, Object> map = new HashMap<>();
        map.put("score", ratingBar_workReport.getProgress() * 20);
        map.put("comment", content);
        showLoading("");
        RestAdapterFactory.getInstance().build(Config_project.API_URL()).create(IWorkReport.class).reviewWorkReport(mWorkReportId, map, new RCallback<WorkReport>() {
            @Override
            public void success(final WorkReport workReport, final Response response) {
                HttpErrorCheck.checkResponse(response);
                setResult(RESULT_OK);
                back();
            }

            @Override
            public void failure(RetrofitError error) {
                HttpErrorCheck.checkError(error);
                super.failure(error);
            }
        });
    }

    @Click(R.id.img_title_left)
    void back() {
        onBackPressed();
    }
}
