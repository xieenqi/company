package com.loyo.oa.contactpicker.model;

import com.loyo.oa.indexablelist.widget.Indexable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by EthanGong on 2016/10/13.
 */

public class UserModel implements Indexable {

    private int identifier;

    private UserModel(int identifier) {
        this.identifier = identifier;
    }

    public long getKey() {
        return identifier/ 100;
    }

    @Override
    public String getIndex() {
        return "-" + getKey() + "-";
    }


    public static List<UserModel> testModels() {
        List<UserModel> result = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            result.add(new UserModel(i));
        }
        return result;
    }
}
