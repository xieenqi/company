package com.loyo.oa.photo.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.loyo.oa.photo.entity.Photo;
import com.loyo.oa.photo.entity.PhotoDirectory;
import com.loyo.oa.photo.event.OnItemCheckListener;
import com.loyo.oa.photo.event.OnPhotoClickListener;
import com.loyo.oa.photo.utils.AndroidLifecycleUtils;
import com.loyo.oa.photo.utils.MediaStoreHelper;
import com.loyo.oa.v2.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * https://github.com/donglua/PhotoPicker
 */
public class PhotoGridAdapter extends SelectableAdapter<PhotoGridAdapter.PhotoViewHolder> {

  private LayoutInflater inflater;
  private RequestManager glide;

  private OnItemCheckListener onItemCheckListener    = null;
  private OnPhotoClickListener onPhotoClickListener  = null;
  private View.OnClickListener onCameraClickListener = null;

  public final static int ITEM_TYPE_CAMERA = 100;
  public final static int ITEM_TYPE_PHOTO  = 101;
  private final static int COL_NUMBER_DEFAULT = 3;
  private final static int WIDTH_THREDHOLD = 200;

  private boolean hasCamera = true;
  private boolean previewEnable = true;

  private int imageSize;
  private int columnNumber = COL_NUMBER_DEFAULT;


  public PhotoGridAdapter(Context context, RequestManager requestManager, List<PhotoDirectory> photoDirectories) {
    this.photoDirectories = photoDirectories;
    this.glide = requestManager;
    inflater = LayoutInflater.from(context);
      setHasStableIds(true);
    setColumnNumber(context, columnNumber);

  }

  public PhotoGridAdapter(Context context, RequestManager requestManager,  List<PhotoDirectory> photoDirectories, ArrayList<String> orginalPhotos, int colNum) {
    this(context, requestManager, photoDirectories);
    setColumnNumber(context, colNum);
    selectedPhotos = new ArrayList<>();
      setHasStableIds(true);
    if (orginalPhotos != null) selectedPhotos.addAll(orginalPhotos);
  }

  private void setColumnNumber(Context context, int columnNumber) {
    this.columnNumber = columnNumber;
    WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
    DisplayMetrics metrics = new DisplayMetrics();
    wm.getDefaultDisplay().getMetrics(metrics);
    int widthPixels = metrics.widthPixels;
    imageSize = widthPixels / columnNumber;
    if (imageSize > WIDTH_THREDHOLD) {
      imageSize = WIDTH_THREDHOLD;
    }
  }

  @Override public int getItemViewType(int position) {
    return (showCamera() && position == 0) ? ITEM_TYPE_CAMERA : ITEM_TYPE_PHOTO;
  }


  @Override public PhotoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    final View itemView = inflater.inflate(R.layout.__picker_item_photo, parent, false);
    final PhotoViewHolder holder = new PhotoViewHolder(itemView);
    if (viewType == ITEM_TYPE_CAMERA) {
      holder.vSelected.setVisibility(View.GONE);
      holder.ivPhoto.setScaleType(ImageView.ScaleType.CENTER);

      holder.ivPhoto.setOnClickListener(new View.OnClickListener() {
        @Override public void onClick(View view) {
          if (onCameraClickListener != null) {
            onCameraClickListener.onClick(view);
          }
        }
      });
    }
    return holder;
  }


  @Override public void onBindViewHolder(final PhotoViewHolder holder, int position) {

    if (getItemViewType(position) == ITEM_TYPE_PHOTO) {

      List<Photo> photos = getCurrentPhotos();
      final Photo photo;

      if (showCamera()) {
        photo = photos.get(position - 1);
      } else {
        photo = photos.get(position);
      }

      boolean canLoadImage = AndroidLifecycleUtils.canLoadImage(holder.ivPhoto.getContext());

      if (canLoadImage) {
        glide
                .load(new File(photo.getPath()).getAbsolutePath())
                .centerCrop()
                .dontAnimate()
                .thumbnail(0.3f)
                .override(imageSize, imageSize)
                .placeholder(R.drawable.__picker_ic_default_error)
                .error(R.drawable.__picker_ic_default_error)
                .into(holder.ivPhoto);
      }

      final boolean isChecked = isSelected(photo);

      holder.vSelected.setSelected(isChecked);
      holder.ivPhoto.setSelected(isChecked);

      holder.ivPhoto.setOnClickListener(new View.OnClickListener() {
        @Override public void onClick(View view) {
          if (onPhotoClickListener != null) {
            int pos = holder.getAdapterPosition();
            if (previewEnable) {
              onPhotoClickListener.onClick(view, pos, showCamera());
            } else {
              holder.vSelected.performClick();
            }
          }
        }
      });
      holder.vSelected.setOnClickListener(new View.OnClickListener() {
        @Override public void onClick(View view) {
          int pos = holder.getAdapterPosition();
          boolean isEnable = true;

          if (onItemCheckListener != null &&
                  onItemCheckListener.onSingleSelectCheck(pos, photo, isChecked,
                          getSelectedPhotos().size())) {
            // 单选命中
            return;
          }

          if (onItemCheckListener != null) {
            isEnable = onItemCheckListener.onItemCheck(pos, photo, isChecked,
                getSelectedPhotos().size());
          }
          if (isEnable) {
            toggleSelection(photo);
            notifyItemChanged(pos);
          }
        }
      });


    } else {
      holder.ivPhoto.setImageResource(R.drawable.__picker_camera);
    }
  }

  public void attemptSelectAtIndex(int index, Photo photo) {
    int pos = index;
    boolean isEnable = true;

    if (onItemCheckListener != null &&
            onItemCheckListener.onSingleSelectCheck(pos, photo, false,
                    getSelectedPhotos().size())) {
      // 单选命中
      return;
    }

    if (onItemCheckListener != null) {
      isEnable = onItemCheckListener.onItemCheck(pos, photo, false,
              getSelectedPhotos().size());
    }
    if (isEnable) {
      toggleSelection(photo);
      notifyItemChanged(pos);
    }
  }


  @Override public int getItemCount() {
    int photosCount =
        photoDirectories.size() == 0 ? 0 : getCurrentPhotos().size();
    if (showCamera()) {
      return photosCount + 1;
    }
    return photosCount;
  }

  @Override public long getItemId(int position) {

    if (getItemViewType(position) == ITEM_TYPE_PHOTO) {
      List<Photo> photos = getCurrentPhotos();
      final Photo photo;

      if (showCamera()) {
        photo = photos.get(position - 1);
      } else {
        photo = photos.get(position);
      }
      return photo.getPath().hashCode();
    }
    else {
      return "Camera".hashCode();
    }
  }

  public static class PhotoViewHolder extends RecyclerView.ViewHolder {
    private ImageView ivPhoto;
    private View vSelected;

    public PhotoViewHolder(View itemView) {
      super(itemView);
      ivPhoto   = (ImageView) itemView.findViewById(R.id.iv_photo);
      vSelected = itemView.findViewById(R.id.v_selected);
    }
  }


  public void setOnItemCheckListener(OnItemCheckListener onItemCheckListener) {
    this.onItemCheckListener = onItemCheckListener;
  }


  public void setOnPhotoClickListener(OnPhotoClickListener onPhotoClickListener) {
    this.onPhotoClickListener = onPhotoClickListener;
  }


  public void setOnCameraClickListener(View.OnClickListener onCameraClickListener) {
    this.onCameraClickListener = onCameraClickListener;
  }


  public ArrayList<String> getSelectedPhotoPaths() {
    ArrayList<String> selectedPhotoPaths = new ArrayList<>(getSelectedItemCount());

    for (String photo : selectedPhotos) {
      selectedPhotoPaths.add(photo);
    }

    return selectedPhotoPaths;
  }


  public void setShowCamera(boolean hasCamera) {
    this.hasCamera = hasCamera;
  }

  public void setPreviewEnable(boolean previewEnable) {
    this.previewEnable = previewEnable;
  }

  public boolean showCamera() {
    return (hasCamera && currentDirectoryIndex == MediaStoreHelper.INDEX_ALL_PHOTOS);
  }

  @Override public void onViewRecycled(PhotoViewHolder holder) {
    Glide.clear(holder.ivPhoto);
    super.onViewRecycled(holder);
  }
}
