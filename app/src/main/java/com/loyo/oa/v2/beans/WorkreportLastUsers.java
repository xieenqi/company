package com.loyo.oa.v2.beans;

/**
 * com.loyo.oa.v2.beans
 * 描述 :
 * 作者 : ykb
 * 时间 : 15/8/14.
 */
public class WorkreportLastUsers extends BaseBeans
{
    /**审批人id**/
    private String ReviewerId;
    /**审批人名称**/
    private String  ReviewerName;
    /**抄送人id集合**/
    private String  MentionedUserIds;
    /**抄送部门id集合**/
    private String  MentionedDeptIds;
    /**抄送部门名字集合**/
    private String  MentionedDeptNames;
    /**抄送人员名称集合**/
    private String  MentionedUserNames;

    public String getReviewerId()
    {
        return ReviewerId;
    }

    public void setReviewerId(String reviewerId)
    {
        ReviewerId = reviewerId;
    }

    public String getMentionedUserNames()
    {
        return MentionedUserNames;
    }

    public void setMentionedUserNames(String mentionedNames)
    {
        MentionedUserNames = mentionedNames;
    }

    public String getMentionedDeptNames()
    {
        return MentionedDeptNames;
    }

    public void setMentionedDeptNames(String mentionedDeptNames)
    {
        MentionedDeptNames = mentionedDeptNames;
    }

    public String getMentionedDeptIds()
    {
        return MentionedDeptIds;
    }

    public void setMentionedDeptIds(String mentionedDeptIds)
    {
        MentionedDeptIds = mentionedDeptIds;
    }

    public String getMentionedUserIds()
    {
        return MentionedUserIds;
    }

    public void setMentionedUserIds(String mentionedUserIds)
    {
        MentionedUserIds = mentionedUserIds;
    }

    public String getReviewerName()
    {
        return ReviewerName;
    }

    public void setReviewerName(String reviewerName)
    {
        ReviewerName = reviewerName;
    }

    @Override
    String getOrderStr()
    {
        return null;
    }

    @Override
    public String getId()
    {
        return "";
    }
}
