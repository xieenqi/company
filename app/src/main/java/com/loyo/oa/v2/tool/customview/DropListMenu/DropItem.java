package com.loyo.oa.v2.tool.customview.DropListMenu;

import java.util.ArrayList;

public class DropItem {

    //正常情况下,点击菜单即消失
    public static final int NORMAL = 0;
    //组内单选
    public static final int GROUP_SINGLE = 1;
    //组内单选
    public static final int GROUP_SINGLE_DISMISS = 2;

    String Name;
    int Value;
    String mData;

    //显示类型
    int SelectType = 0;

    public DropItem(String name) {
        Name = name;
    }

    public DropItem(String name, int value) {
        Name = name;
        Value = value;
    }

    public DropItem(String name,int value, String data) {
        this(name, value);
        mData = data;
    }

    public DropItem(String name, int value, ArrayList<DropItem> subDropItem) {
        Name = name;
        Value = value;
        SubDropItem = subDropItem;
    }

    public String getmData() {
        return mData;
    }

    public void setmData(String mData) {
        this.mData = mData;
    }

    ArrayList<DropItem> SubDropItem;

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public int getValue() {
        return Value;
    }

    public void setValue(int value) {
        Value = value;
    }

    public ArrayList<DropItem> getSubDropItem() {
        return SubDropItem;
    }

    public void setSubDropItem(ArrayList<DropItem> subDropItem) {
        SubDropItem = subDropItem;
    }

    public void addSubDropItem(DropItem item) {
        if (SubDropItem == null) {
            SubDropItem = new ArrayList<>();
        }

        SubDropItem.add(item);
    }

    public int getSelectType() {
        return SelectType;
    }

    public void setSelectType(int selectType) {
        SelectType = selectType;
    }

    public boolean hasSub() {
        return getSubDropItem() != null && getSubDropItem().size() > 0;
    }

    public boolean hasSubExtend() {
        if (!hasSub()) {
            return false;
        }

        for (DropItem item : getSubDropItem()) {
            if (item.hasSub()) {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DropItem item = (DropItem) o;

        if (Value != item.Value) return false;
        return Name.equals(item.Name);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
