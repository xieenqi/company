package com.loyo.oa.upload.explosion;

/**
 * Created by EthanGong on 2016/11/16.
 */

public interface ExplosionListener {
    void beforeExplosion();
    void inExplosion(float value);
    void afterExplosion();
}
