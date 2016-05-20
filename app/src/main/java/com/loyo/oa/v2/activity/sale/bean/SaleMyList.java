package com.loyo.oa.v2.activity.sale.bean;

import java.util.ArrayList;

/**
 * 我的销售机会列表bean
 * Created by yyy on 16/5/20.
 */
public class SaleMyList {

    public int pageIndex;
    public int pageSize;
    public int totalRecords;
    public ArrayList<Record> records = new ArrayList<>();

    public class Record{

        private String id;
        private String name;
        private String stageNmae;
        private int estimatedAmount;
        private int prob;


        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public int getProb() {
            return prob;
        }

        public void setProb(int prob) {
            this.prob = prob;
        }

        public int getEstimatedAmount() {
            return estimatedAmount;
        }

        public void setEstimatedAmount(int estimatedAmount) {
            this.estimatedAmount = estimatedAmount;
        }

        public String getStageNmae() {
            return stageNmae;
        }

        public void setStageNmae(String stageNmae) {
            this.stageNmae = stageNmae;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}
