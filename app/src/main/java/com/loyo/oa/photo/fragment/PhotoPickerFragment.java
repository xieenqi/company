package com.loyo.oa.photo.fragment;

import android.app.ActivityManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.ListPopupWindow;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.AdapterView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.loyo.oa.photo.PhotoPickerActivity;
import com.loyo.oa.photo.adapter.PhotoGridAdapter;
import com.loyo.oa.photo.adapter.PopupDirectoryListAdapter;
import com.loyo.oa.photo.entity.Photo;
import com.loyo.oa.photo.entity.PhotoDirectory;
import com.loyo.oa.photo.event.OnAlbumSelectListener;
import com.loyo.oa.photo.event.OnPhotoClickListener;
import com.loyo.oa.photo.utils.AndroidLifecycleUtils;
import com.loyo.oa.photo.utils.ImageCaptureManager;
import com.loyo.oa.photo.utils.MediaStoreHelper;
import com.loyo.oa.photo.utils.PermissionsConstant;
import com.loyo.oa.photo.utils.PermissionsUtils;
import com.loyo.oa.v2.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.ACTIVITY_SERVICE;
import static com.loyo.oa.photo.PhotoPicker.DEFAULT_COLUMN_NUMBER;
import static com.loyo.oa.photo.PhotoPicker.EXTRA_PREVIEW_ENABLED;
import static com.loyo.oa.photo.PhotoPicker.EXTRA_SHOW_GIF;
import static com.loyo.oa.photo.utils.MediaStoreHelper.INDEX_ALL_PHOTOS;

/**
 * https://github.com/donglua/PhotoPicker
 */
public class PhotoPickerFragment extends Fragment implements OnAlbumSelectListener {

  private ImageCaptureManager captureManager;
  private PhotoGridAdapter photoGridAdapter;

  private PopupDirectoryListAdapter listAdapter;
  //所有photos的路径
  private List<PhotoDirectory> directories;
  //传入的已选照片
  private ArrayList<String> originalPhotos;

  private int SCROLL_THRESHOLD = 30;
  int column;
  private boolean singleMode  = false;
  private boolean cropEnabled = false;
  private View contentView;
  private RecyclerView mRecyclerView;
  private boolean isPushed;

  //目录弹出框的一次最多显示的目录数目
  public static int COUNT_MAX = 6;
  private final static String EXTRA_CAMERA = "camera";
  private final static String EXTRA_COLUMN = "column";
  private final static String EXTRA_COUNT = "count";
  private final static String EXTRA_GIF = "gif";
  private final static String EXTRA_ORIGIN = "origin";
  private final static String EXTRA_SINGLE = "single";
  private final static String EXTRA_CROP = "crop";
  private ListPopupWindow listPopupWindow;
  private RequestManager mGlideRequestManager;

  public static PhotoPickerFragment newInstance(boolean showCamera, boolean showGif,
      boolean previewEnable, boolean singleMode, boolean cropEnabled,
      int column, int maxCount, ArrayList<String> originalPhotos) {
    Bundle args = new Bundle();
    args.putBoolean(EXTRA_CAMERA, showCamera);
    args.putBoolean(EXTRA_GIF, showGif);
    args.putBoolean(EXTRA_PREVIEW_ENABLED, previewEnable);
    args.putBoolean(EXTRA_SINGLE, singleMode);
    args.putBoolean(EXTRA_CROP, cropEnabled);
    args.putInt(EXTRA_COLUMN, column);
    args.putInt(EXTRA_COUNT, maxCount);
    args.putStringArrayList(EXTRA_ORIGIN, originalPhotos);
    PhotoPickerFragment fragment = new PhotoPickerFragment();
    fragment.setArguments(args);
    return fragment;
  }

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setRetainInstance(true);

    mGlideRequestManager = Glide.with(this);

    directories = new ArrayList<>();
    originalPhotos = getArguments().getStringArrayList(EXTRA_ORIGIN);

    column = getArguments().getInt(EXTRA_COLUMN, DEFAULT_COLUMN_NUMBER);
    boolean showCamera = getArguments().getBoolean(EXTRA_CAMERA, true);
    boolean previewEnable = getArguments().getBoolean(EXTRA_PREVIEW_ENABLED, true);
    singleMode = getArguments().getBoolean(EXTRA_SINGLE, false);
    cropEnabled = getArguments().getBoolean(EXTRA_CROP, false);

    photoGridAdapter = new PhotoGridAdapter(getActivity(), mGlideRequestManager, directories, originalPhotos, column);
    photoGridAdapter.setShowCamera(showCamera);
    photoGridAdapter.setPreviewEnable(previewEnable);

    Bundle mediaStoreArgs = new Bundle();

    boolean showGif = getArguments().getBoolean(EXTRA_GIF);
    mediaStoreArgs.putBoolean(EXTRA_SHOW_GIF, showGif);
    MediaStoreHelper.getPhotoDirs(getActivity(), mediaStoreArgs,
        new MediaStoreHelper.PhotosResultCallback() {
          @Override public void onResultCallback(List<PhotoDirectory> dirs) {
            directories.clear();
            directories.addAll(dirs);
            photoGridAdapter.notifyDataSetChanged();
            listAdapter.notifyDataSetChanged();
            adjustHeight();
          }
        });

    captureManager = new ImageCaptureManager(getActivity());
  }


  @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {

    if (contentView != null) {
      return contentView;
    }

    final View rootView = inflater.inflate(R.layout.__picker_fragment_photo_picker, container, false);

    listAdapter  = new PopupDirectoryListAdapter(mGlideRequestManager, directories);

    RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.rv_photos);
    StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(column, OrientationHelper.VERTICAL);
    layoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS);
    recyclerView.setLayoutManager(layoutManager);
    recyclerView.setAdapter(photoGridAdapter);
    recyclerView.setItemAnimator(new DefaultItemAnimator());
    mRecyclerView = recyclerView;

    //final Button btSwitchDirectory = (Button) rootView.findViewById(R.id.button);

    listPopupWindow = new ListPopupWindow(getActivity());
    listPopupWindow.setWidth(ListPopupWindow.MATCH_PARENT);

    listPopupWindow.setAdapter(listAdapter);
    listPopupWindow.setModal(true);
    listPopupWindow.setDropDownGravity(Gravity.BOTTOM);
    listPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
      @Override
      public void onDismiss() {
        popFrom();
      }
    });



    photoGridAdapter.setOnPhotoClickListener(new OnPhotoClickListener() {
      @Override public void onClick(View v, int position, boolean showCamera) {
        final int index = showCamera ? position - 1 : position;

        List<String> photos = photoGridAdapter.getCurrentPhotoPaths();

        int[] screenLocation = new int[2];
        v.getLocationOnScreen(screenLocation);
        ImagePagerFragment imagePagerFragment =
            ImagePagerFragment.newInstance(photos, index, screenLocation, v.getWidth(),
                v.getHeight());

        ((PhotoPickerActivity) getActivity()).addImagePagerFragment(imagePagerFragment);
      }
    });

    photoGridAdapter.setOnCameraClickListener(new OnClickListener() {
      @Override public void onClick(View view) {
        if (!PermissionsUtils.checkCameraPermission(PhotoPickerFragment.this)) return;
        if (!PermissionsUtils.checkWriteStoragePermission(PhotoPickerFragment.this)) return;
        openCamera();
      }
    });

    recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
      @Override public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);

        if (Math.abs(dy) > SCROLL_THRESHOLD && isInLowMemory()) {
          mGlideRequestManager.pauseRequests();
        } else {
          resumeRequestsIfNotDestroyed();
        }
      }
      @Override public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
        if (newState == RecyclerView.SCROLL_STATE_IDLE) {
          resumeRequestsIfNotDestroyed();
        }
      }
    });

    contentView = rootView;

    return rootView;
  }

  private boolean isInLowMemory() {

    if (!AndroidLifecycleUtils.canLoadImage(this)) {
      return true;
    }
    final ActivityManager activityManager = (ActivityManager) this.getActivity().getSystemService(ACTIVITY_SERVICE);
    ActivityManager.MemoryInfo info = new ActivityManager.MemoryInfo();
    activityManager.getMemoryInfo(info);
    return info.lowMemory;
  }

  private void openCamera() {
    try {
      Intent intent = captureManager.dispatchTakePictureIntent();
      startActivityForResult(intent, ImageCaptureManager.REQUEST_TAKE_PHOTO);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @Override public void onActivityResult(int requestCode, int resultCode, Intent data) {
    if (requestCode == ImageCaptureManager.REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {

      if (captureManager == null) {
        FragmentActivity activity = getActivity();
        captureManager = new ImageCaptureManager(activity);
      }

      captureManager.galleryAddPic();
      if (directories.size() > 0) {
        String path = captureManager.getCurrentPhotoPath();
        PhotoDirectory directory = directories.get(INDEX_ALL_PHOTOS);
        Photo photo = new Photo(path.hashCode(), path);
        directory.getPhotos().add(INDEX_ALL_PHOTOS, photo);
        directory.setCoverPath(path);
        photoGridAdapter.notifyDataSetChanged();
        photoGridAdapter.attemptSelectAtIndex(INDEX_ALL_PHOTOS + 1, photo);
      }
    }
  }

  @Override
  public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
    if (grantResults.length > 0
            && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
      switch (requestCode) {
        case PermissionsConstant.REQUEST_CAMERA:
        case PermissionsConstant.REQUEST_EXTERNAL_WRITE:
          if (PermissionsUtils.checkWriteStoragePermission(this) &&
                  PermissionsUtils.checkCameraPermission(this)) {
            openCamera();
          }
          break;
      }
    }
  }

  public PhotoGridAdapter getPhotoGridAdapter() {
    return photoGridAdapter;
  }


  @Override public void onSaveInstanceState(Bundle outState) {
    captureManager.onSaveInstanceState(outState);
    super.onSaveInstanceState(outState);
  }


  @Override public void onViewStateRestored(Bundle savedInstanceState) {
    captureManager.onRestoreInstanceState(savedInstanceState);
    super.onViewStateRestored(savedInstanceState);
  }

  public ArrayList<String> getSelectedPhotoPaths() {
    return photoGridAdapter.getSelectedPhotoPaths();
  }

  public void adjustHeight() {
    if (listAdapter == null) return;
    int count = listAdapter.getCount();
    count = count < COUNT_MAX ? count : COUNT_MAX;
    if (listPopupWindow != null) {
      listPopupWindow.setHeight(count * getResources().getDimensionPixelOffset(R.dimen.__picker_item_directory_height));
    }
  }

  @Override public void onDestroy() {
    super.onDestroy();

    if (directories == null) {
      return;
    }

    for (PhotoDirectory directory : directories) {
      directory.getPhotoPaths().clear();
      directory.getPhotos().clear();
      directory.setPhotos(null);
    }
    directories.clear();
    directories = null;
  }

  private void resumeRequestsIfNotDestroyed() {
    if (!AndroidLifecycleUtils.canLoadImage(this)) {
      return;
    }

    mGlideRequestManager.resumeRequests();
  }

  @Override
  public void onAlbumSelectListener(final View anchor,  final TextView textView) {
    listPopupWindow.setAnchorView(anchor);
    listPopupWindow.setOnItemClickListener(new AdapterView.OnItemClickListener() {
      @Override public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        popFrom();
        listPopupWindow.dismiss();
        PhotoDirectory directory = directories.get(position);

        textView.setText(directory.getName());

        photoGridAdapter.setCurrentDirectoryIndex(position);
        photoGridAdapter.notifyDataSetChanged();
      }
    });

    if (listPopupWindow.isShowing()) {
      popFrom();
      listPopupWindow.dismiss();
    } else if (!getActivity().isFinishing()) {
      adjustHeight();
      listPopupWindow.show();
      pushTo();
    }
  }

  public void pushTo() {
    if ( isPushed ) {
      return;
    }
    isPushed = true;
    final ScaleAnimation animation =new ScaleAnimation(1.0f, 0.95f, 1.0f, 0.95f,
            Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
    animation.setInterpolator(new AccelerateDecelerateInterpolator());
    animation.setDuration(300);
    animation.setFillAfter(true);
    mRecyclerView.startAnimation(animation);
  }

  public void popFrom() {
    if ( !isPushed ) {
      return;
    }
    isPushed = false;
    final ScaleAnimation animation =new ScaleAnimation(0.95f, 1.0f, 0.95f, 1.0f,
            Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
    animation.setInterpolator(new AccelerateDecelerateInterpolator());
    animation.setDuration(300);
    animation.setFillAfter(true);
    mRecyclerView.startAnimation(animation);
  }
}
