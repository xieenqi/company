package com.loyo.oa.v2.beans;

import java.util.ArrayList;

/**
 * com.loyo.oa.v2.beans
 * 描述 :新版客户
 * 作者 : ykb
 * 时间 : 15/9/30.
 */
public class Customer extends BaseBeans {
    /**我的客户**/
    public static final int CUSTOMER_TYPE_MINE=1;
    /**团队客户**/
    public static final int CUSTOMER_TYPE_TEAM=CUSTOMER_TYPE_MINE+1;
    /**公海客户**/
    public static final int CUSTOMER_TYPE_PUBLIC=CUSTOMER_TYPE_MINE+2;
    /**我的附近客户**/
    public static final int CUSTOMER_TYPE_NEAR_MINE =CUSTOMER_TYPE_MINE+3;
    /**公司已赢单客户**/
    public static final int CUSTOMER_TYPE_NEAR_COMPANY=CUSTOMER_TYPE_MINE+4;
    /**团队附近客户**/
    public static final int CUSTOMER_TYPE_NEAR_TEAM=CUSTOMER_TYPE_MINE+5;



    public enum CustomerType{
        MY(1),//我的客户
        TEAM(2),//团队客户
        PUBLIC(3),//公海客户

        NEAR_MY(4),//附近我的客户
        NEAR_COMPANY(5);//附近公司已赢单客户

        private int mValue;
        CustomerType(int value){
            mValue=value;
        }

        public int getmValue(){
            return mValue;
        }
    }

    private String id;
    private String name;
    private Locate loc;
    private long createdAt;
    private ArrayList<ExtraData> extDatas = new ArrayList<>();
    private boolean lock;
    private long lockAt;
    private long lastActAt;
    private String companyId;
    private Member owner;
    private NewUser creator;
    private CustomerRegional regional;
    private Counter counter;
    private ArrayList<Member> members = new ArrayList<>();
    private ArrayList<NewTag> tags = new ArrayList<>();
    private ArrayList<Contact> contacts = new ArrayList<>();

    private String distance;
    private String lastAct;
    private String summary;
    private String uuid;
    private Industry industry;

    public Industry getIndustry() {
        return industry;
    }

    public void setIndustry(Industry industry) {
        this.industry = industry;
    }

    public Counter getCounter() {
        return counter;
    }

    public void setCounter(Counter counter) {
        this.counter = counter;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getLastAct() {
        return lastAct;
    }

    public void setLastAct(String lastAct) {
        this.lastAct = lastAct;
    }
    public Member getOwner() {
        return owner;
    }

    public void setOwner(Member owner) {
        this.owner = owner;
    }

    public ArrayList<Contact> getContacts() {
        return contacts;
    }

    public void setContacts(ArrayList<Contact> contacts) {
        this.contacts = contacts;
    }

    public ArrayList<NewTag> getTags() {
        return tags;
    }

    public void setTags(ArrayList<NewTag> tags) {
        this.tags = tags;
    }

    public ArrayList<Member> getMembers() {
        return members;
    }

    public void setMembers(ArrayList<Member> members) {
        this.members = members;
    }

    public CustomerRegional getRegional() {
        return regional;
    }

    public void setRegional(CustomerRegional regional) {
        this.regional = regional;
    }

    public NewUser getCreator() {
        return creator;
    }

    public void setCreator(NewUser creator) {
        this.creator = creator;
    }

    public String getCompanyId() {
        return companyId;
    }

    public void setCompanyId(String companyId) {
        this.companyId = companyId;
    }

    public long getLastActAt() {
        return lastActAt;
    }

    public void setLastActAt(long lastActAt) {
        this.lastActAt = lastActAt;
    }

    public long getLockAt() {
        return lockAt;
    }

    public void setLockAt(long lockAt) {
        this.lockAt = lockAt;
    }

    public boolean isLock() {
        return lock;
    }

    public void setLock(boolean lock) {
        this.lock = lock;
    }

    public ArrayList<ExtraData> getExtDatas() {
        return extDatas;
    }

    public void setExtDatas(ArrayList<ExtraData> extDatas) {
        this.extDatas = extDatas;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public Locate getLoc() {
        return loc;
    }

    public void setLoc(Locate loc) {
        this.loc = loc;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    String getOrderStr() {
        return null;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

}
