package com.loyo.oa.audio.player;

import android.support.annotation.StringDef;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by EthanGong on 2016/12/10.
 */

public class AudioPlayUpdate {

    public final static String PLAY = "";
    public final static String RESUME = "";
    public final static String PAUSE = "";
    public final static String STOP = "";
    public final static String PROGRESS = "";

    @StringDef({PLAY, RESUME, PAUSE, STOP, PROGRESS})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Type {
    }

    @Type
    public String type;
    public float progress;

    private AudioPlayUpdate(@Type String type, float progress) {
        this.type = type;
        this.progress = progress;
    }

    public static AudioPlayUpdate playState() {
        return new AudioPlayUpdate(PLAY, 0);
    }

    public static AudioPlayUpdate resumeState() {
        return new AudioPlayUpdate(RESUME, 0);
    }

    public static AudioPlayUpdate pauseState() {
        return new AudioPlayUpdate(PAUSE, 0);
    }

    public static AudioPlayUpdate stopState() {
        return new AudioPlayUpdate(STOP, 0);
    }

    public static AudioPlayUpdate progressState(float progress) {
        return new AudioPlayUpdate(PROGRESS, progress);
    }


}
