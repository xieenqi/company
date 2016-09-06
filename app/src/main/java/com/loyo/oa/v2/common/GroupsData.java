package com.loyo.oa.v2.common;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * Created by EthanGong on 16/8/27.
 *
 * 分组数据model
 *
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

    public void sortByRecursely() {
        Collections.sort(groups);
        Iterator<SectionData> iterator = groups.iterator();
        while (iterator.hasNext()) {
            SectionData next = iterator.next();
            next.sort();
        }
    }

    public SectionData get(int idx) {
        return groups.get(idx);
    }

    public int size() {
        return groups.size();
    }

    public void addItems(List<Groupable> items) {
        Iterator<Groupable> iterator = items.iterator();
        while (iterator.hasNext()) {
            Groupable item = iterator.next();
            addItem(item);
        }
    }

    public void addItem(Groupable item) {
        GroupKey key = item.groupBy();
        int i= 0;
        for ( ; i < groups.size(); i++) {
            SectionData section = groups.get(i);
            if (section.groupKey.equals(key)) {
                section.add(item);
                break;
            }
        }

        if (i == groups.size()) {
            SectionData newSection = new SectionData();
            newSection.groupKey = key;
            newSection.add(item);
            groups.add(newSection);
        }
    }

    public void remove(Groupable item) {
        //
    }

    public void clear() {
        groups.clear();
    }

    public Groupable get(int section, int pos) {
        if (section < 0 || section >= groups.size()) {
            return null;
        }
        SectionData sectionData = groups.get(section);
        if (pos < 0 || pos  >= sectionData.size()) {
            return null;
        }
        return sectionData.get(pos);

    }

    public class SectionData implements Comparable<SectionData> {
        public GroupKey groupKey;
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
                return 1;
            }
            else if (weight < anotherWeight) {
                return -1;
            }
            return 0;
        }

        public Groupable get(int idx) {
            return data.get(idx);
        }

        public int size() {
            return data.size();
        }

        /** 排序分组 */
        public void sort() {
            Collections.sort(data);
        }
    }
}
