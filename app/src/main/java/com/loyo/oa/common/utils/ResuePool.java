package com.loyo.oa.common.utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by EthanGong on 2016/11/21.
 */

public class ResuePool<T extends Object> {
    private List<T> pool = new ArrayList<>();
    private ReusableCreator<T> creator;

    public void clearPool()
    {
        pool.clear();
    }

    public T getReusableInstance() {
        if (pool.size() > 0 ) {
            T object = pool.get(0);
            pool.remove(0);
            return object;
        }
        else if (creator != null){
            T instance = creator.getInstance();
            return instance;
        }
        else {
            return null;
        }
    }

    public void recycleInstance(T instance) {
        if (instance != null) {
            pool.add(instance);
        }
    }

    public void setCreator(ReusableCreator<T> creator) {
        this.creator = creator;
    }

    public static abstract class ReusableCreator<T> {
        public abstract T getInstance();
    }
}
