package com.loyo.oa.v2.activityui.worksheet.bean;


import java.io.Serializable;
import java.util.List;

/**
 * Created by yyy on 16/10/22.
 */

public class OrderWorksheetListModel implements Serializable {

    public String uuid;
    public String title;
    public String templateId;
    public String templateName;
    public String content;
    public int    size;
    public List<String> mSelectPath;

}
