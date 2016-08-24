package com.loyo.oa.v2.beans;

import java.io.Serializable;

/**
 * com.loyo.oa.v2.beans
 * 描述 :职位信息类
 * 作者 : ykb
 * 时间 : 15/7/28.
 */
public class Position extends BaseBeans implements Serializable
{
    private String id;
    private String name;
    private int sequence;
    private int companyId;
    private String simplePinyin;
    private String fullPinyin;

    public String getFullPinyin()
    {
        return fullPinyin;
    }

    public void setFullPinyin(String fullPinyin)
    {
        this.fullPinyin = fullPinyin;
    }

    public String getSimplePinyin()
    {
        return simplePinyin;
    }

    public void setSimplePinyin(String simplePinyin)
    {
        this.simplePinyin = simplePinyin;
    }

    public int getCompanyId()
    {
        return companyId;
    }

    public void setCompanyId(int companyId)
    {
        this.companyId = companyId;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public int getSequence()
    {
        return sequence;
    }

    public void setSequence(int sequence)
    {
        this.sequence = sequence;
    }

    public void setId(String id)
    {
        this.id=id;
    }
    @Override
    public String getId()
    {
        return id;
    }

    @Override
    public String getOrderStr()
    {
        return null;
    }
}
