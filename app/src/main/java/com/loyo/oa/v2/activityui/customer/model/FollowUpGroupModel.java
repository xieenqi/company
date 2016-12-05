package com.loyo.oa.v2.activityui.customer.model;

import com.loyo.oa.v2.activityui.followup.model.FollowUpListModel;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by loyo_dev1 on 16/12/5.
 */

public class FollowUpGroupModel implements Serializable{

    public String date;
    public String timeStamp;
    public ArrayList<FollowUpListModel> activities;

}
