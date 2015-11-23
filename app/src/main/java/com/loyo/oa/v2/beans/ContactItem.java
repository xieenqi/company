package com.loyo.oa.v2.beans;

import java.io.Serializable;

/**
 * com.loyo.oa.v2.beans
 * 描述 :客户联系人字内容封装类
 * 作者 : ykb
 * 时间 : 15/9/24.
 */
public class ContactItem implements Serializable {
    /**不显示操作区域**/
    public static final int ITEM_TYPE_SHOW_NOTHING=1;
    /**显示操作区域**/
    public static final int ITEM_TYPE_SHOW_ALL=ITEM_TYPE_SHOW_NOTHING+1;
    /**显示操作区域的打电话按钮**/
    public static final int ITEM_TYPE_ONLY_SHOW_CALL=ITEM_TYPE_SHOW_NOTHING+2;

    /**
     * 条目标题
     */
    private String title;
    /**
     * 条目内容
     */
    private String content;
    /**
     * 条目类型
     * {@link #ITEM_TYPE_ONLY_SHOW_CALL},{@link #ITEM_TYPE_SHOW_ALL},{@link #ITEM_TYPE_SHOW_NOTHING}
     */
    private int showType;

    public ContactItem(int type,String title,String content){
        showType=type;
        this.title=title;
        this.content=content;
    }

    public int getShowType() {
        return showType;
    }

    public void setShowType(int showType) {
        this.showType = showType;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
