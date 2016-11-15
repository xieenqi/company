package com.loyo.oa.photo.event;

import com.loyo.oa.photo.entity.Photo;

/**
 * https://github.com/donglua/PhotoPicker
 */
public interface OnItemCheckListener {

  /***
   *
   * @param position 所选图片的位置
   * @param path     所选的图片
   * @param isCheck   当前状态
   * @param selectedItemCount  已选数量
   * @return enable check
   */
  boolean onItemCheck(int position, Photo photo, boolean isCheck, int selectedItemCount);

  boolean onSingleSelectCheck(int position, Photo photo, boolean isCheck, int selectedItemCount);

}
