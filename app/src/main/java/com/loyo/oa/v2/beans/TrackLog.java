package com.loyo.oa.v2.beans;

/**
 * Created by Administrator on 2014/12/18 0018.
 */
public class TrackLog {
    private String  address;// (string, optional): ,
    private String   companyId ;//(int, optional): ,
    private long   createdAt;// (&{time Time}, optional): ,
    private String   gps;// (string, optional): ,
    private String   id ;//(int64, optional): ,
    private int   stationTime ;//(int, optional): ,
    private User   user;// (&{organization User}, optional):

    public TrackLog (String _address,String _gps,long _createdAt){
        address=_address;
        gps=_gps;
        createdAt=_createdAt;
    }

    public TrackLog(){

    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCompanyId() {
        return companyId;
    }

    public void setCompanyId(String companyId) {
        this.companyId = companyId;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public String getGps() {
        return gps;
    }

    public void setGps(String gps) {
        this.gps = gps;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getStationTime() {
        return stationTime;
    }

    public void setStationTime(int stationTime) {
        this.stationTime = stationTime;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
