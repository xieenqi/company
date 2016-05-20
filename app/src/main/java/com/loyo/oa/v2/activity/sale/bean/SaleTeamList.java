package com.loyo.oa.v2.activity.sale.bean;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * 机会团队列表bean
 * Created by yyy on 16/5/20.
 */
public class SaleTeamList implements Serializable{

    public int pageIndex;
    public int pageSize;
    public int totalRecords;
    public ArrayList<Record> records = new ArrayList<>();

    public class Record{
        public String id;
        public String name;
        public String estimatedAmount;
        public String stageNmae;
        public int prob;
        public String createName;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getCreateName() {
            return createName;
        }

        public void setCreateName(String createName) {
            this.createName = createName;
        }

        public String getStageNmae() {
            return stageNmae;
        }

        public void setStageNmae(String stageNmae) {
            this.stageNmae = stageNmae;
        }

        public String getEstimatedAmount() {
            return estimatedAmount;
        }

        public void setEstimatedAmount(String estimatedAmount) {
            this.estimatedAmount = estimatedAmount;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getProb() {
            return prob;
        }

        public void setProb(int prob) {
            this.prob = prob;
        }
    }

}
