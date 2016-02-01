package com.loyo.oa.v2.beans;

import java.io.Serializable;

/**
 * Created by loyo_dev1 on 16/2/1.
 */
public class PostBizExtData implements Serializable {

   private int attachmentCount;

    public int getAttachmentCount() {
        return attachmentCount;
    }

    public void setAttachmentCount(int attachmentCount) {
        this.attachmentCount = attachmentCount;
    }
}
