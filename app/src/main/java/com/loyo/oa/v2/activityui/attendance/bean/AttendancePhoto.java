package com.loyo.oa.v2.activityui.attendance.bean;

/**
 * Created by yyy on 16/1/14.
 */
public class AttendancePhoto {

        private String WeekDays;
        private String inAt;
        private String outAt;
        private String x;
        private String y;
        private String insideRange;
        private boolean outsideReview;
        private boolean needPhoto;


        public String getWeekDays() {
            return WeekDays;
        }

        public void setWeekDays(String weekDays) {
            WeekDays = weekDays;
        }

        public String getInAt() {
            return inAt;
        }

        public void setInAt(String inAt) {
            this.inAt = inAt;
        }

        public String getOutAt() {
            return outAt;
        }

        public void setOutAt(String outAt) {
            this.outAt = outAt;
        }

        public String getX() {
            return x;
        }

        public void setX(String x) {
            this.x = x;
        }

        public String getY() {
            return y;
        }

        public void setY(String y) {
            this.y = y;
        }

        public String getInsideRange() {
            return insideRange;
        }

        public void setInsideRange(String insideRange) {
            this.insideRange = insideRange;
        }

        public boolean isNeedPhoto() {
            return needPhoto;
        }

        public void setNeedPhoto(boolean needPhoto) {
            this.needPhoto = needPhoto;
        }

        public boolean isOutsideReview() {
            return outsideReview;
        }

        public void setOutsideReview(boolean outsideReview) {
            this.outsideReview = outsideReview;
        }


}
