package com.loyo.oa.v2.tool.customview.multi_image_selector;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.tool.BaseActivity;
import com.loyo.oa.v2.tool.Utils;
import com.loyo.oa.v2.tool.customview.multi_image_selector.crop.ClipSquareImageView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

/**
 * com.loyo.oa.v2.tool.customview.multi_image_selector
 * 描述 :裁剪图片页
 * 作者 : ykb
 * 时间 : 15/8/27.
 */
@EActivity(R.layout.activity_crop_image)
public class CropImageActivity extends BaseActivity
{
    @ViewById
    ClipSquareImageView clipSquareIV;

    @ViewById
    ViewGroup layout_back;

    @ViewById
    TextView tv_edit;
    @ViewById
    TextView tv_title;

    @Extra
    String imgPath;

   @AfterViews
   void initViews(){
       setTouchView(-1);
       tv_edit.setVisibility(View.VISIBLE);
       tv_title.setVisibility(View.VISIBLE);
       tv_title.setText("设置头像");
       tv_edit.setText("确定");

       tv_edit.setOnTouchListener(Global.GetTouch());
       layout_back.setOnTouchListener(Global.GetTouch());

       clipSquareIV.setImageBitmap(BitmapFactory.decodeFile(imgPath));
//       ImageLoader.getInstance().displayImage("file://"+imgPath,clipSquareIV);

   }

    @Click({R.id.layout_back,R.id.tv_edit})
    void onClick(View v){
        switch (v.getId()){
            case R.id.layout_back:
                app.finishActivity(this, MainApp.ENTER_TYPE_TOP,RESULT_CANCELED,null);
                break;
            case R.id.tv_edit:
                Bitmap bitmap=clipSquareIV.clip();
                int index=imgPath.lastIndexOf("/");
                String name=imgPath.substring(index+1);
                imgPath=imgPath.replace(name,"temp_"+name);
                Utils.writeImage(bitmap,imgPath,100);
                Intent intent=new Intent();
                intent.putExtra("imgPath",imgPath);
                app.finishActivity(this,MainApp.ENTER_TYPE_TOP,RESULT_OK,intent);
                break;
        }
    }
}
