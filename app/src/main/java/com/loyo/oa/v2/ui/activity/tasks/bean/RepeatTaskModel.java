package com.loyo.oa.v2.ui.activity.tasks.bean;

import java.util.List;

/**
 * Created by yyy on 16/3/28.
 */
public class RepeatTaskModel {

    private String name;
    private List<RepeatTaskCaseModel> timeKind;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<RepeatTaskCaseModel> getTimeKind() {
        return timeKind;
    }

    public void setTimeKind(List<RepeatTaskCaseModel> timeKind) {
        this.timeKind = timeKind;
    }
}
