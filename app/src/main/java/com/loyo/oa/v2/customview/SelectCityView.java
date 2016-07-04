package com.loyo.oa.v2.customview;

import android.app.Dialog;
import android.content.Context;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.customer.bean.CityModel;
import com.loyo.oa.v2.activityui.customer.bean.DistrictModel;
import com.loyo.oa.v2.beans.ProvinceModel;
import com.loyo.oa.v2.tool.XmlParserSelectCity;
import com.loyo.oa.v2.customview.wheel.OnWheelChangedListener;
import com.loyo.oa.v2.customview.wheel.WheelView;
import com.loyo.oa.v2.customview.wheel.adapters.ArrayWheelAdapter;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

/**
 * 自定义 三联城市选择控件
 * Created by yyy on 16/3/8.
 */
public class SelectCityView extends Dialog implements OnWheelChangedListener {


    private Context mContext;

    private String[] provinces;//省集合

    private String[] cities;//市集合

    private String[] areas;//区集合

    private String[] cityValue;

    private int provoiceIndex = 0;

    private int cityIndex = 0;

    private int districtIndex = 0;

    private Map<String, String[]> mCitisDatasMap = new HashMap<String, String[]>();

    private Map<String, String[]> mDistrictDatasMap = new HashMap<String, String[]>();

    private Map<String, String> mZipcodeDatasMap = new HashMap<String, String>();

    /**
     * 省
     */
    private String mCurrentProviceName;
    /**
     * 市
     */
    private String mCurrentCityName;
    /**
     * 区
     */
    private String mCurrentDistrictName = "";

    /**
     * 城市代码
     */
    private String mCurrentZipCode = "";

    private WheelView mViewProvince;
    private WheelView mViewCity;
    private WheelView mViewDistrict;
    private TextView mBtnConfirm;

    public SelectCityView(Context context,String[] cityValue) {
        super(context);
        this.mContext = context;
        this.cityValue = cityValue;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_selectcity_main);
        setUpViews();
        setUpListener();
        setUpData();
    }

    protected void initProvinceDatas() {
        List<ProvinceModel> provinceList = null;
        AssetManager asset = mContext.getAssets();
        try {
            InputStream input = asset.open("province_data.xml");
            SAXParserFactory spf = SAXParserFactory.newInstance();
            SAXParser parser = spf.newSAXParser();
            XmlParserSelectCity handler = new XmlParserSelectCity();
            parser.parse(input, handler);
            input.close();
            provinceList = handler.getDataList();
            if (provinceList != null && !provinceList.isEmpty()) {
                mCurrentProviceName = provinceList.get(0).getName();
                List<CityModel> cityList = provinceList.get(0).getCityList();
                if (cityList != null && !cityList.isEmpty()) {
                    mCurrentCityName = cityList.get(0).getName();
                    List<DistrictModel> districtList = cityList.get(0).getDistrictList();
                    mCurrentDistrictName = districtList.get(0).getName();
                    mCurrentZipCode = districtList.get(0).getZipcode();
                }
            }
            provinces = new String[provinceList.size()];
            for (int i = 0; i < provinceList.size(); i++) {
                provinces[i] = provinceList.get(i).getName();
                List<CityModel> cityList = provinceList.get(i).getCityList();
                String[] cityNames = new String[cityList.size()];
                for (int j = 0; j < cityList.size(); j++) {
                    cityNames[j] = cityList.get(j).getName();
                    List<DistrictModel> districtList = cityList.get(j).getDistrictList();
                    String[] distrinctNameArray = new String[districtList.size()];
                    DistrictModel[] distrinctArray = new DistrictModel[districtList.size()];
                    for (int k = 0; k < districtList.size(); k++) {
                        DistrictModel districtModel = new DistrictModel(districtList.get(k).getName(), districtList.get(k).getZipcode());
                        mZipcodeDatasMap.put(districtList.get(k).getName(), districtList.get(k).getZipcode());
                        distrinctArray[k] = districtModel;
                        distrinctNameArray[k] = districtModel.getName();
                    }
                    mDistrictDatasMap.put(cityNames[j], distrinctNameArray);
                }
                mCitisDatasMap.put(provinceList.get(i).getName(), cityNames);
            }
        } catch (Throwable e) {
            e.printStackTrace();
        } finally {

        }
    }

    /**
     * 省市区控件绑定
     */
    private void setUpViews() {
        mViewProvince = (WheelView) findViewById(R.id.id_province);
        mViewCity = (WheelView) findViewById(R.id.id_city);
        mViewDistrict = (WheelView) findViewById(R.id.id_district);
        mBtnConfirm = (TextView) findViewById(R.id.btn_confirm);
    }

    private void setUpListener() {
        mViewProvince.addChangingListener(this);
        mViewCity.addChangingListener(this);
        mViewDistrict.addChangingListener(this);
    }

    @Override
    public void onChanged(WheelView wheel, int oldValue, int newValue) {
        if (wheel == mViewProvince) {
            updateCities();
        } else if (wheel == mViewCity) {
            updateAreas();
        } else if (wheel == mViewDistrict) {
            mCurrentDistrictName = mDistrictDatasMap.get(mCurrentCityName)[newValue];
            mCurrentZipCode = mZipcodeDatasMap.get(mCurrentDistrictName);
        }
    }


    /**
     * 绑定数据省市区
     * */
    private void setUpData() {
        initProvinceDatas();
        mViewProvince.setVisibleItems(7);
        mViewCity.setVisibleItems(7);
        mViewDistrict.setVisibleItems(7);

        if(null != cityValue && cityValue.length == 3) {
            setPersetData();
        }else{
            //省
            updateProvince();
            //市
            updateCities();
            //区
            updateAreas();
        }
    }

    /**
     * 把已选中的城市，反显在wheel上
     * */
    private void setPersetData(){
        //省
        for(int i = 0;i<provinces.length;i++){
            if(cityValue[0].equals(provinces[i])){
                provoiceIndex = i;
                break;
            }
        }

        mViewProvince.setViewAdapter(new ArrayWheelAdapter<String>(mContext, provinces));
        mViewProvince.setCurrentItem(provoiceIndex);

        //市
        mCurrentProviceName = provinces[provoiceIndex];
        cities = mCitisDatasMap.get(mCurrentProviceName);
        if (cities == null) {
            cities = new String[]{""};
        }
        for(int i = 0;i<cities.length;i++){
            if(cityValue[1].equals(cities[i])){
                cityIndex = i;
                break;
            }
        }

        mViewCity.setViewAdapter(new ArrayWheelAdapter<String>(mContext, cities));
        mViewCity.setCurrentItem(cityIndex);//控制显示第几条数据

        //区
        mCurrentCityName = mCitisDatasMap.get(mCurrentProviceName)[cityIndex];
        areas = mDistrictDatasMap.get(mCurrentCityName);
        if (areas == null) {
            areas = new String[]{""};
        }

        for(int i = 0;i<areas.length;i++){
            if(cityValue[2].equals(areas[i])){
                districtIndex = i;
                break;
            }
        }
        mViewDistrict.setViewAdapter(new ArrayWheelAdapter<String>(mContext, areas));
        mViewDistrict.setCurrentItem(districtIndex);
    }


    /**
     * 初始化省数据
     * */
    private void updateProvince(){
        mViewProvince.setViewAdapter(new ArrayWheelAdapter<String>(mContext, provinces));
        mViewProvince.setCurrentItem(0);
    }

    /**
     * 滑动省View时，更新城市/区数据
     */
    private void updateCities() {
        int pCurrent = mViewProvince.getCurrentItem();
        mCurrentProviceName = provinces[pCurrent];
        cities = mCitisDatasMap.get(mCurrentProviceName);
        if (cities == null) {
            cities = new String[]{""};
        }
        mViewCity.setViewAdapter(new ArrayWheelAdapter<String>(mContext, cities));
        mViewCity.setCurrentItem(0);//控制显示第几条数据
        updateAreas();
    }

    /**
     * 滑动区View时，更新区数据
     */
    private void updateAreas() {
        int pCurrent = mViewCity.getCurrentItem();
        mCurrentCityName = mCitisDatasMap.get(mCurrentProviceName)[pCurrent];
        areas = mDistrictDatasMap.get(mCurrentCityName);
        if (areas == null) {
            areas = new String[]{""};
        }
        mViewDistrict.setViewAdapter(new ArrayWheelAdapter<String>(mContext, areas));
        mViewDistrict.setCurrentItem(0);//控制显示第几条数据

        mCurrentDistrictName = mDistrictDatasMap.get(mCurrentCityName)[0];
        mCurrentZipCode = mZipcodeDatasMap.get(mCurrentDistrictName);
    }



    /**
     * 确定按钮监听
     */
    public void setOnclickselectCity(View.OnClickListener listener) {
        mBtnConfirm.setOnClickListener(listener);
    }

    public String[] getResult() {
        String[] str = new String[3];
        str[0] = mCurrentProviceName;
        str[1] = mCurrentCityName;
        str[2] = mCurrentDistrictName;
        return str;
    }
}
