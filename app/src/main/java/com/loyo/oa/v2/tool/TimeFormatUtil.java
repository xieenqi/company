package com.loyo.oa.v2.tool;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by loyocloud on 16/3/18.
 */
public class TimeFormatUtil {

    public static String toFormat(long time) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy.MM.dd HH:mm");
        Date date = new Date(time * 1000l);
        return format.format(date);
    }

    public static String toMd_Hm(long time){
        SimpleDateFormat format = new SimpleDateFormat("MM-dd HH:mm");
        Date date = new Date(time * 1000l);
        return format.format(date);
    }

}
