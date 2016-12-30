package com.loyo.oa.hud.progress;

/**
 * Created by EthanGong on 2016/12/29.
 */

public interface CommitIndeterminate extends Indeterminate {
    void loadSuccessState();
    void loadErrorState();
}