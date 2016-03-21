package com.loyo.oa.v2.activity.commonview;

import java.util.List;

/**
 * Created by loyocloud on 16/3/18.
 */
public class HttpDiscussion {

    /**
     * id : 56eb6327526f151c0800017d
     * attachmentUUId : a227601e-a77c-49e5-8615-e9cc5279e026
     * content : @侯超 zzz
     * creator : {"id":"56a47b15526f153bc1f38fa3","name":"王旭娇","gender":2,"depts":[{"shortDept":{"id":"56a47a8c526f153bc1f38fa1","xpath":"5699117aebe07fb52300020d/5699117aebe07fb523000225/56a47a8c526f153bc1f38fa1","name":"测试1"},"shortPosition":{"id":"5698f5e07a101681d35da538","name":"普通员工","sequence":3},"title":"助理"}]}
     * summaryId : 56eb5d37526f151c08000007
     * createdAt : 1458266919
     * updatedAt : 1458266919
     * bizType : 1
     * mentionedUserIds : ["56d962f7526f150ed2000001"]
     */

    private String id;
    private String attachmentUUId;
    private String content;
    /**
     * id : 56a47b15526f153bc1f38fa3
     * name : 王旭娇
     * gender : 2
     * depts : [{"shortDept":{"id":"56a47a8c526f153bc1f38fa1","xpath":"5699117aebe07fb52300020d/5699117aebe07fb523000225/56a47a8c526f153bc1f38fa1","name":"测试1"},"shortPosition":{"id":"5698f5e07a101681d35da538","name":"普通员工","sequence":3},"title":"助理"}]
     */

    private CreatorEntity creator;
    private String summaryId;
    private int createdAt;
    private int updatedAt;
    private int bizType;
    private List<String> mentionedUserIds;

    public void setId(String id) {
        this.id = id;
    }

    public void setAttachmentUUId(String attachmentUUId) {
        this.attachmentUUId = attachmentUUId;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setCreator(CreatorEntity creator) {
        this.creator = creator;
    }

    public void setSummaryId(String summaryId) {
        this.summaryId = summaryId;
    }

    public void setCreatedAt(int createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(int updatedAt) {
        this.updatedAt = updatedAt;
    }

    public void setBizType(int bizType) {
        this.bizType = bizType;
    }

    public void setMentionedUserIds(List<String> mentionedUserIds) {
        this.mentionedUserIds = mentionedUserIds;
    }

    public String getId() {
        return id;
    }

    public String getAttachmentUUId() {
        return attachmentUUId;
    }

    public String getContent() {
        return content;
    }

    public CreatorEntity getCreator() {
        return creator;
    }

    public String getSummaryId() {
        return summaryId;
    }

    public int getCreatedAt() {
        return createdAt;
    }

    public int getUpdatedAt() {
        return updatedAt;
    }

    public int getBizType() {
        return bizType;
    }

    public List<String> getMentionedUserIds() {
        return mentionedUserIds;
    }

    @Override
    public String toString() {
        return "HttpDiscussion{" +
                "id='" + id + '\'' +
                ", attachmentUUId='" + attachmentUUId + '\'' +
                ", content='" + content + '\'' +
                ", creator=" + creator +
                ", summaryId='" + summaryId + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", bizType=" + bizType +
                ", mentionedUserIds=" + mentionedUserIds +
                '}';
    }

    public static class CreatorEntity {
        private String id;
        private String name;
        private int gender;
        /**
         * shortDept : {"id":"56a47a8c526f153bc1f38fa1","xpath":"5699117aebe07fb52300020d/5699117aebe07fb523000225/56a47a8c526f153bc1f38fa1","name":"测试1"}
         * shortPosition : {"id":"5698f5e07a101681d35da538","name":"普通员工","sequence":3}
         * title : 助理
         */

        private List<DeptsEntity> depts;

        public void setId(String id) {
            this.id = id;
        }

        public void setName(String name) {
            this.name = name;
        }

        public void setGender(int gender) {
            this.gender = gender;
        }

        public void setDepts(List<DeptsEntity> depts) {
            this.depts = depts;
        }

        public String getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public int getGender() {
            return gender;
        }

        public List<DeptsEntity> getDepts() {
            return depts;
        }

        public static class DeptsEntity {
            /**
             * id : 56a47a8c526f153bc1f38fa1
             * xpath : 5699117aebe07fb52300020d/5699117aebe07fb523000225/56a47a8c526f153bc1f38fa1
             * name : 测试1
             */

            private ShortDeptEntity shortDept;
            /**
             * id : 5698f5e07a101681d35da538
             * name : 普通员工
             * sequence : 3
             */

            private ShortPositionEntity shortPosition;
            private String title;

            public void setShortDept(ShortDeptEntity shortDept) {
                this.shortDept = shortDept;
            }

            public void setShortPosition(ShortPositionEntity shortPosition) {
                this.shortPosition = shortPosition;
            }

            public void setTitle(String title) {
                this.title = title;
            }

            public ShortDeptEntity getShortDept() {
                return shortDept;
            }

            public ShortPositionEntity getShortPosition() {
                return shortPosition;
            }

            public String getTitle() {
                return title;
            }

            @Override
            public String toString() {
                return "DeptsEntity{" +
                        "shortDept=" + shortDept +
                        ", shortPosition=" + shortPosition +
                        ", title='" + title + '\'' +
                        '}';
            }

            public static class ShortDeptEntity {
                private String id;
                private String xpath;
                private String name;

                public void setId(String id) {
                    this.id = id;
                }

                public void setXpath(String xpath) {
                    this.xpath = xpath;
                }

                public void setName(String name) {
                    this.name = name;
                }

                public String getId() {
                    return id;
                }

                public String getXpath() {
                    return xpath;
                }

                public String getName() {
                    return name;
                }

                @Override
                public String toString() {
                    return "ShortDeptEntity{" +
                            "id='" + id + '\'' +
                            ", xpath='" + xpath + '\'' +
                            ", name='" + name + '\'' +
                            '}';
                }
            }

            public static class ShortPositionEntity {
                private String id;
                private String name;
                private int sequence;

                public void setId(String id) {
                    this.id = id;
                }

                public void setName(String name) {
                    this.name = name;
                }

                public void setSequence(int sequence) {
                    this.sequence = sequence;
                }

                public String getId() {
                    return id;
                }

                public String getName() {
                    return name;
                }

                public int getSequence() {
                    return sequence;
                }

                @Override
                public String toString() {
                    return "ShortPositionEntity{" +
                            "id='" + id + '\'' +
                            ", name='" + name + '\'' +
                            ", sequence=" + sequence +
                            '}';
                }
            }
        }
    }
}
