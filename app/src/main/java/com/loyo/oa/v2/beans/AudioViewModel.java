package com.loyo.oa.v2.beans;

import android.widget.ImageView;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.application.MainApp;
import java.lang.ref.WeakReference;

/**
 * Created by loyo_dev1 on 16/9/21.
 */
public class AudioViewModel extends CustomerFollowUpModel {

    public String id;
    public String creatorName;
    public long createAt;
    public String content;
    public String typeName;
    public long remindAt;
    public String contactName;
    public String audioUrl;
    public long audioLength;

    private boolean isAnim = false;
    public WeakReference<ImageView> imageViewWeakReference;

    public AudioViewModel(CustomerFollowUpModel model) {
        this.id = model.id;
        this.creatorName = model.creatorName;
        this.createAt = model.createAt;
        this.content = model.content;
        this.typeName = model.typeName;
        this.remindAt = model.remindAt;
        this.contactName = model.contactName;
        this.audioUrl = model.audioUrl;
        this.audioLength = model.audioLength;
    }


    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    public String getCreatorName() {
        return creatorName;
    }

    public void setCreatorName(String creatorName) {
        this.creatorName = creatorName;
    }

    @Override
    public long getCreateAt() {
        return createAt;
    }

    @Override
    public void setCreateAt(long createAt) {
        this.createAt = createAt;
    }

    @Override
    public String getContent() {
        return content;
    }

    @Override
    public void setContent(String content) {
        this.content = content;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    @Override
    public long getRemindAt() {
        return remindAt;
    }

    @Override
    public void setRemindAt(long remindAt) {
        this.remindAt = remindAt;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public String getAudioUrl() {
        return audioUrl;
    }

    public void setAudioUrl(String audioUrl) {
        this.audioUrl = audioUrl;
    }

    public long getAudioLength() {
        return audioLength;
    }

    public void setAudioLength(long audioLength) {
        this.audioLength = audioLength;
    }

    public boolean getIsAnim(){
        return isAnim;
    }

    public void setIsAnim(boolean isAnim){
        this.isAnim = isAnim;
        if (imageViewWeakReference != null && imageViewWeakReference.get() != null) {
            ImageView imageView = imageViewWeakReference.get();
            if (isAnim) {
                MainApp.getMainApp().startAnim(imageView);
            }
            else {
                MainApp.getMainApp().stopAnim(imageView);
            }
        }
    }
}
