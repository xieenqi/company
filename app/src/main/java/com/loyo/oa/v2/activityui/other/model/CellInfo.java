package com.loyo.oa.v2.activityui.other.model;

/**
 * com.loyo.oa.v2.beans
 * 描述 :手机信息封装类
 * 作者 : ykb
 * 时间 : 15/8/6.
 */
public class CellInfo
{
    /**终端系统平台**/
    private String loyoPlatform="Android";
    /**终端类型**/
    private String loyoAgent;
    /**终端系统版本**/
    private String loyoOSVersion;
    /**设备IMEI**/
    private String loyoImei;
    /**设备硬件版本**/
    private String loyoHVersion;

    public CellInfo() {
    }

    public String getLoyoImei()
    {
        return loyoImei;
    }

    public void setLoyoImei(String loyoImei)
    {
        this.loyoImei = loyoImei;
    }

    public String getLoyoHVersion()
    {
        return loyoHVersion;
    }

    public void setLoyoHVersion(String loyoHVersion)
    {
        this.loyoHVersion = loyoHVersion;
    }
    public String getLoyoOSVersion()
    {
        return loyoOSVersion;
    }

    public void setLoyoOSVersion(String loyoOSVersion)
    {
        this.loyoOSVersion = loyoOSVersion;
    }

    public String getLoyoPlatform()
    {
        return loyoPlatform;
    }

    public void setLoyoPlatform(String loyoPlatform)
    {
        this.loyoPlatform = loyoPlatform;
    }

    public String getLoyoAgent()
    {
        return loyoAgent;
    }

    public void setLoyoAgent(String loyoAgent)
    {
        this.loyoAgent = loyoAgent;
    }

}
