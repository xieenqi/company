package com.loyo.oa.v2.ui.customview;

import android.app.Dialog;
import android.content.Context;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import com.loyo.oa.v2.R;
import com.loyo.oa.v2.beans.RepeatTaskCaseModel;
import com.loyo.oa.v2.beans.RepeatTaskModel;
import com.loyo.oa.v2.tool.XmlParserRepeatTask;
import com.loyo.oa.v2.ui.wheel.OnWheelChangedListener;
import com.loyo.oa.v2.ui.wheel.WheelView;
import com.loyo.oa.v2.ui.wheel.adapters.ArrayWheelAdapter;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

/**
 * 自定义 任务重复日期选择控件
 * Created by yyy on 16/3/28.
 */
public class RepeatTaskView extends Dialog implements OnWheelChangedListener {


    private Context mContext;

    private String[] OneDatas; //天月周数据层

    private String[] hourDatas = new String[24]; //时

    private String[] minDatas = new String[60]; //分

    private Map<String, String[]> TwoDatasMap = new HashMap<String, String[]>();


    /**
     * 分钟
     * */
    private String mDataMin = "0分";

    /**
     * 小时
     * */
    private String mDataHour = "0时";

    /**
     * 选择类型(月 周 天)
     */
    private String mDataCaseName;

    /**
     * 时间类型(31天，7天，无)
     */
    private String mDataTimekind;

    private WheelView mViewWeek;
    private WheelView mViewDay;
    private WheelView mViewHour;
    private WheelView mViewMin;
    private TextView mBtnConfirm;
    private TextView mBtnCancel;

    public RepeatTaskView(Context context) {
        super(context);
        this.mContext = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_repeattask_main);
        setUpViews();
        setUpListener();
        setUpData();
    }

    /**
     * 数据绑定
     * */
    protected void initProvinceDatas() {
        List<RepeatTaskCaseModel> caseSize = null;
        List<RepeatTaskModel> totalLists = null;
        AssetManager asset = mContext.getAssets();
        try {
            InputStream input = asset.open("repeattask_data.xml");
            SAXParserFactory spf = SAXParserFactory.newInstance();
            SAXParser parser = spf.newSAXParser();
            XmlParserRepeatTask xmlTask = new XmlParserRepeatTask();
            parser.parse(input, xmlTask);
            input.close();
            totalLists = xmlTask.getDataList();//总的数据集

        } catch (Throwable e) {
            e.printStackTrace();
        } finally {

        }
        if (totalLists != null && !totalLists.isEmpty()) {
            mDataCaseName = totalLists.get(0).getName();
            List<RepeatTaskCaseModel> timekinds = totalLists.get(0).getTimeKind();
            if (timekinds != null && !timekinds.isEmpty()) {
                mDataTimekind = timekinds.get(0).getName();
            }
        }

        OneDatas = new String[totalLists.size()];
        for (int i = 0; i < totalLists.size(); i++) {
            OneDatas[i] = totalLists.get(i).getName();
            caseSize = totalLists.get(i).getTimeKind();
            String[] timekinds = new String[caseSize.size()];
            for (int j = 0; j < caseSize.size(); j++) {
                timekinds[j] = caseSize.get(j).getName();
                TwoDatasMap.put(totalLists.get(i).getName(), timekinds);
            }
        }

        for(int i = 0;i<24;i++){
                hourDatas[i] = i+"时";
        }
        for(int i = 0;i<60;i++){
            if(i<10 && i != 0){
                minDatas[i] = "0"+i+"分";
            }else{
                minDatas[i] = i+"分";
           }
        }
    }

    /**
     * 省市区控件绑定
     */
    private void setUpViews() {
        mViewWeek = (WheelView) findViewById(R.id.id_week);
        mViewDay = (WheelView) findViewById(R.id.id_day);
        mViewHour = (WheelView) findViewById(R.id.id_hour);
        mViewMin = (WheelView) findViewById(R.id.id_min);
        mBtnConfirm = (TextView) findViewById(R.id.btn_confirm);
        mBtnCancel = (TextView) findViewById(R.id.btn_cancel);
    }

    private void setUpListener() {
        mViewWeek.addChangingListener(this);
        mViewDay.addChangingListener(this);
        mViewHour.addChangingListener(this);
        mViewMin.addChangingListener(this);
    }

    private void setUpData() {
        initProvinceDatas();
        mViewWeek.setViewAdapter(new ArrayWheelAdapter<String>(mContext, OneDatas));
        mViewHour.setViewAdapter(new ArrayWheelAdapter<String>(mContext,hourDatas));
        mViewMin.setViewAdapter(new ArrayWheelAdapter<String>(mContext,minDatas));

        mViewWeek.setVisibleItems(7);
        mViewDay.setVisibleItems(7);
        mViewHour.setVisibleItems(7);
        mViewMin.setVisibleItems(7);
        updateCities();
    }

    @Override
    public void onChanged(WheelView wheel, int oldValue, int newValue) {
        if (wheel == mViewWeek) {
            updateCities();
        }else if(wheel == mViewDay){
            String[] timeKinds = TwoDatasMap.get(mDataCaseName);
            mDataTimekind = timeKinds[newValue];

        }else if(wheel == mViewHour){
             mDataHour = hourDatas[newValue];

        }else if(wheel == mViewMin){
             mDataMin = minDatas[newValue];
        }

    }

    /**
     * 滑动省View时,更新数据(周，天，月)
     */
    private void updateCities() {
        int weekCreen = mViewWeek.getCurrentItem();
        mDataCaseName = OneDatas[weekCreen];
        String[] timeKinds = TwoDatasMap.get(mDataCaseName);
        if (timeKinds == null) {
            timeKinds = new String[]{""};
        }
        mDataTimekind = timeKinds[0];
        mViewDay.setViewAdapter(new ArrayWheelAdapter<String>(mContext, timeKinds));
        mViewDay.setCurrentItem(0);
    }

    /**
     * 确定按钮监听
     */
    public void setConfirmOnClick(View.OnClickListener listener) {
        mBtnConfirm.setOnClickListener(listener);
    }

    public void setCancelOnClick(View.OnClickListener listener){
        mBtnCancel.setOnClickListener(listener);
    }


    public String[] getResult() {
        String[] str = new String[4];
        str[0] = mDataCaseName;
        str[1] = mDataTimekind;
        str[2] = mDataHour;
        str[3] = mDataMin;
        return str;
    }
}
