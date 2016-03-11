package com.loyo.oa.v2.activity.discuss;

import java.io.Serializable;

/**
 * Created by loyocloud on 16/3/10.
 */
public class DiscussDetInfo implements Serializable {
    private boolean isMine = true;
    private String time = "2016/03/10";
    private String name = "当前activity";
    private String content = "Android是一个不断进化的" +
            "平台，Android 5.0的v7版本支持包中引入了新的R" +
            "ecyclerView控件，正如官方文档所言，RecyclerV" +
            "iew是ListView的豪华增强版。它主要包含以下几处" +
            "新的特性，如ViewHolder，ItemDecorator，Layo" +
            "utManager，SmothScroller以及增加或删除item时" +
            "item动画等。官方推荐我们采用RecyclerView来取代ListView。";

    public boolean isMine() {
        return isMine;
    }

    public DiscussDetInfo setIsMine(boolean isMine) {
        this.isMine = isMine;
        return this;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
