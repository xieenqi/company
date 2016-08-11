package com.loyo.oa.v2.beans;

import java.io.Serializable;
import java.util.List;

/**
 * Created by yyy on 16/8/9.
 */
public class AttachmentForNew implements Serializable{


        private String id;

        private Creator creator;

        private int bizType;

        private String UUId;

        private String originalName;

        private String name;

        private String url;

        private int size;

        private String humanizeSize;

        private String mime;

        private boolean isPublic;

        private String viewers;

        private int createdAt;

        private int updatedAt;

        public void setId(String id){
            this.id = id;
        }
        public String getId(){
            return this.id;
        }
        public void setCreator(Creator creator){
            this.creator = creator;
        }
        public Creator getCreator(){
            return this.creator;
        }
        public void setBizType(int bizType){
            this.bizType = bizType;
        }
        public int getBizType(){
            return this.bizType;
        }
        public void setUUId(String UUId){
            this.UUId = UUId;
        }
        public String getUUId(){
            return this.UUId;
        }
        public void setOriginalName(String originalName){
            this.originalName = originalName;
        }
        public String getOriginalName(){
            return this.originalName;
        }
        public void setName(String name){
            this.name = name;
        }
        public String getName(){
            return this.name;
        }
        public void setUrl(String url){
            this.url = url;
        }
        public String getUrl(){
            return this.url;
        }
        public void setSize(int size){
            this.size = size;
        }
        public int getSize(){
            return this.size;
        }
        public void setHumanizeSize(String humanizeSize){
            this.humanizeSize = humanizeSize;
        }
        public String getHumanizeSize(){
            return this.humanizeSize;
        }
        public void setMime(String mime){
            this.mime = mime;
        }
        public String getMime(){
            return this.mime;
        }
        public void setIsPublic(boolean isPublic){
            this.isPublic = isPublic;
        }
        public boolean getIsPublic(){
            return this.isPublic;
        }
        public void setViewers(String viewers){
            this.viewers = viewers;
        }
        public String getViewers(){
            return this.viewers;
        }
        public void setCreatedAt(int createdAt){
            this.createdAt = createdAt;
        }
        public int getCreatedAt(){
            return this.createdAt;
        }
        public void setUpdatedAt(int updatedAt){
            this.updatedAt = updatedAt;
        }
        public int getUpdatedAt(){
            return this.updatedAt;
        }


        public class Creator {
            private String id;

            private String name;

            private int gender;

            private String avatar;

            private List<Depts> depts ;

            public void setId(String id){
                this.id = id;
            }
            public String getId(){
                return this.id;
            }
            public void setName(String name){
                this.name = name;
            }
            public String getName(){
                return this.name;
            }
            public void setGender(int gender){
                this.gender = gender;
            }
            public int getGender(){
                return this.gender;
            }
            public void setAvatar(String avatar){
                this.avatar = avatar;
            }
            public String getAvatar(){
                return this.avatar;
            }
            public void setDepts(List<Depts> depts){
                this.depts = depts;
            }
            public List<Depts> getDepts(){
                return this.depts;
            }

        }


        public class Depts {
            private ShortDept shortDept;

            private ShortPosition shortPosition;

            private String title;

            public void setShortDept(ShortDept shortDept){
                this.shortDept = shortDept;
            }
            public ShortDept getShortDept(){
                return this.shortDept;
            }
            public void setShortPosition(ShortPosition shortPosition){
                this.shortPosition = shortPosition;
            }
            public ShortPosition getShortPosition(){
                return this.shortPosition;
            }
            public void setTitle(String title){
                this.title = title;
            }
            public String getTitle(){
                return this.title;
            }

        }

        public class ShortPosition {
            private String name;

            private int sequence;

            public void setName(String name){
                this.name = name;
            }
            public String getName(){
                return this.name;
            }
            public void setSequence(int sequence){
                this.sequence = sequence;
            }
            public int getSequence(){
                return this.sequence;
            }

        }


        public class ShortDept {
            private String id;

            private String xpath;

            private String name;

            public void setId(String id){
                this.id = id;
            }
            public String getId(){
                return this.id;
            }
            public void setXpath(String xpath){
                this.xpath = xpath;
            }
            public String getXpath(){
                return this.xpath;
            }
            public void setName(String name){
                this.name = name;
            }
            public String getName(){
                return this.name;
            }

        }

}
