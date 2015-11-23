package com.loyo.oa.v2.beans;

import java.io.Serializable;

/**
 * com.loyo.oa.v2.beans
 * 描述 :
 * 作者 : ykb
 * 时间 : 15/8/21.
 */
public class LocateData implements Serializable
{
    /**
     *
     */
    private static final long serialVersionUID = -7851813638509527257L;
    /**
     * 纬度
     */
    private double lat;
    /**
     * 纬度
     */
    private double lng;
    /**
     * 记录时间
     */
    private long recordTime;
    /**
     * 地址
     */
    private String address;
    /**
     * 精度
     */
    private double accuracy;
    /**
     * 提供模式
     */
    private String provider;
    /**
     * 记录日期
     */
    private String recordDate;

    public String getRecordDate()
    {
        return recordDate;
    }

    public void setRecordDate(String recordDate)
    {
        this.recordDate = recordDate;
    }

    public String getProvider()
    {
        return provider;
    }

    public void setProvider(String provider)
    {
        this.provider = provider;
    }

    public double getAccuracy()
    {
        return accuracy;
    }

    public void setAccuracy(double accuracy)
    {
        this.accuracy = accuracy;
    }

    public String getAddress()
    {
        return address;
    }

    public void setAddress(String address)
    {
        this.address = address;
    }

    public LocateData()
    {

    }

    public LocateData(long rTime, double ilat, double ilng)
    {
        recordTime = rTime;
        lat = ilat;
        lng = ilng;
    }

    public double getLat()
    {
        return lat;
    }

    public void setLat(double lat)
    {
        this.lat = lat;
    }

    public double getLng()
    {
        return lng;
    }

    public void setLng(double lng)
    {
        this.lng = lng;
    }

    public long getRecordTime()
    {
        return recordTime;
    }

    public void setRecordTime(long recordTime)
    {
        this.recordTime = recordTime;
    }

}