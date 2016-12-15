package com.loyo.oa.v2.activityui.signin.contract;

import com.loyo.oa.v2.common.BaseView;
import com.loyo.oa.v2.common.base.BasePersenter;

/**
 * Created by xeq on 16/12/15.
 */

public interface SigninContract {
    interface View extends BaseView {
        void setIsPhoto();
    }

    interface Presenter extends BasePersenter {
        void getIsPhoto();

        void isPhoto();
    }

    interface Model {
        void isPhotoSend();
    }


}