package com.loyo.oa.v2.activityui.worksheet.common;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Created by EthanGong on 16/8/27.
 */
public class GroupsData {
    public List<SectionData> groups;
    public GroupsData(){
        groups = new ArrayList<SectionData>();
    }

    /** 排序分组 */
    public void sort() {
        Collections.sort(groups);
    }

    public class SectionData implements Comparable<SectionData> {
        GroupKey groupKey;
        List<Groupable> data;

        public SectionData(){
            data = new ArrayList<Groupable>();
        }

        public void add(Groupable item) {
            if (item == null) return;
            data.add(item);
        }

        @Override
        public int compareTo(SectionData another) {
            int weight = this.groupKey.compareWeight();
            int anotherWeight = another.groupKey.compareWeight();

            if (weight > anotherWeight) {
                return -1;
            }
            else if (weight < anotherWeight) {
                return 1;
            }
            return 0;
        }
    }
}
