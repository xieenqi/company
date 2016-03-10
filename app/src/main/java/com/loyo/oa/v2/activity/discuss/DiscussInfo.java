package com.loyo.oa.v2.activity.discuss;

/**
 * Created by libo on 16/3/9.
 */
public class DiscussInfo {
    private String title = "当前activity";
    private String time = "2016/03/09";
    private String content = "当前activity -- LauncherActivity_ -- package com.loyo.oa.v2.activity";

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "DiscussInfo{" +
                "title='" + title + '\'' +
                ", time='" + time + '\'' +
                ", content='" + content + '\'' +
                '}';
    }
}
