package com.loyo.oa.v2.beans;

import java.io.Serializable;

/**
 * Created by loyo_dev1 on 16/1/14.
 */
public class BizExtData implements Serializable{

    private int discussCount;

    private int attachmentCount;


    public int getDiscussCount() {
        return discussCount;
    }

    public void setDiscussCount(int discussCount) {
        this.discussCount = discussCount;
    }

    public int getAttachmentCount() {
        return attachmentCount;
    }

    public void setAttachmentCount(int attachmentCount) {
        this.attachmentCount = attachmentCount;
    }


}
