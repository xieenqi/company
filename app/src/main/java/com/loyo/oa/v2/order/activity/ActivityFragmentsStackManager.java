package com.loyo.oa.v2.order.activity;

import android.support.v4.app.Fragment;

/**
 * Created by EthanGong on 2017/2/28.
 */

public interface ActivityFragmentsStackManager {
    void push(Fragment fragment, String tag);
    void push(Fragment fragment);
    void pop();
}
