package com.loyo.oa.audio.player;

import android.support.annotation.StringDef;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by EthanGong on 2016/12/10.
 */

public class AudioPlayUpdate {

    public final static String START = "START";
    public final static String RESUME = "RESUME";
    public final static String PAUSE = "PAUSE";
    public final static String STOP = "STOP";
    public final static String PROGRESS = "PROGRESS";
    public final static String ERROR = "ERROR";

    @StringDef({START, RESUME, PAUSE, STOP, PROGRESS,ERROR})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Type {
    }

    @Type
    public String type;
    public String progress;

    private AudioPlayUpdate(@Type String type, String progress) {
        this.type = type;
        this.progress = progress;
    }

    public static AudioPlayUpdate startState() {
        return new AudioPlayUpdate(START, "0");
    }

    public static AudioPlayUpdate resumeState() {
        return new AudioPlayUpdate(RESUME, "0");
    }

    public static AudioPlayUpdate pauseState() {
        return new AudioPlayUpdate(PAUSE, "0");
    }

    public static AudioPlayUpdate stopState() {
        return new AudioPlayUpdate(STOP, "0");
    }

    public static AudioPlayUpdate progressState(String progress) {
        return new AudioPlayUpdate(PROGRESS, progress);
    }

    public static AudioPlayUpdate errorState(){
        return new AudioPlayUpdate(ERROR, "0");
    }


}
