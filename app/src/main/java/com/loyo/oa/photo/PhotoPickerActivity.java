package com.loyo.oa.photo;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.loyo.oa.photo.entity.Photo;
import com.loyo.oa.photo.event.OnItemCheckListener;
import com.loyo.oa.photo.event.OnPhotoClickedListener;
import com.loyo.oa.photo.fragment.ImagePagerFragment;
import com.loyo.oa.photo.fragment.PhotoPickerFragment;
import com.loyo.oa.v2.R;
import com.loyo.oa.v2.customview.multi_image_selector.CropImageActivity_;

import java.util.ArrayList;
import java.util.List;

import static android.widget.Toast.LENGTH_LONG;
import static com.loyo.oa.photo.PhotoPicker.CAMERA_CAPTURE_ENABLED;
import static com.loyo.oa.photo.PhotoPicker.DEFAULT_COLUMN_NUMBER;
import static com.loyo.oa.photo.PhotoPicker.DEFAULT_MAX_COUNT;
import static com.loyo.oa.photo.PhotoPicker.EXTRA_CROP_ENABLED;
import static com.loyo.oa.photo.PhotoPicker.EXTRA_GRID_COLUMN;
import static com.loyo.oa.photo.PhotoPicker.EXTRA_MAX_COUNT;
import static com.loyo.oa.photo.PhotoPicker.EXTRA_ORIGINAL_PHOTOS;
import static com.loyo.oa.photo.PhotoPicker.EXTRA_PREVIEW_ENABLED;
import static com.loyo.oa.photo.PhotoPicker.EXTRA_SHOW_CAMERA;
import static com.loyo.oa.photo.PhotoPicker.EXTRA_SHOW_GIF;
import static com.loyo.oa.photo.PhotoPicker.EXTRA_SINGLE_MODE;
import static com.loyo.oa.photo.PhotoPicker.KEY_SELECTED_PHOTOS;

/**
 * https://github.com/donglua/PhotoPicker
 */

public class PhotoPickerActivity extends AppCompatActivity {

  private PhotoPickerFragment pickerFragment;
  private ImagePagerFragment imagePagerFragment;
  private MenuItem menuDoneItem;
  private ViewGroup albumSwitcher;
  private TextView albumSwitcherTitleView;

  private int maxCount = DEFAULT_MAX_COUNT;

  /** to prevent multiple calls to inflate menu */
  private boolean menuIsInflated = false;

  private boolean showGif     = false;
  private boolean singleMode  = false;
  private boolean cropEnabled = false;
  private boolean cameraCaptureEnabled = false;

  private int columnNumber = DEFAULT_COLUMN_NUMBER;
  private ArrayList<String> originalPhotos = null;


  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    boolean showCamera      = getIntent().getBooleanExtra(EXTRA_SHOW_CAMERA, true);
    boolean previewEnabled  = getIntent().getBooleanExtra(EXTRA_PREVIEW_ENABLED, true);
    this.showGif         = getIntent().getBooleanExtra(EXTRA_SHOW_GIF, false);
    this.singleMode      = getIntent().getBooleanExtra(EXTRA_SINGLE_MODE, false);
    this.cropEnabled     = getIntent().getBooleanExtra(EXTRA_CROP_ENABLED, false);
    this.cameraCaptureEnabled     = getIntent().getBooleanExtra(CAMERA_CAPTURE_ENABLED, false);
    if (this.cameraCaptureEnabled) {
      this.singleMode = true;
    }

    setShowGif(showGif);

    setContentView(R.layout.__picker_activity_photo_picker);
    setupToolbar();

    maxCount = getIntent().getIntExtra(EXTRA_MAX_COUNT, DEFAULT_MAX_COUNT);
    columnNumber = getIntent().getIntExtra(EXTRA_GRID_COLUMN, DEFAULT_COLUMN_NUMBER);
    originalPhotos = getIntent().getStringArrayListExtra(EXTRA_ORIGINAL_PHOTOS);

    pickerFragment = (PhotoPickerFragment) getSupportFragmentManager().findFragmentByTag("tag");
    if (pickerFragment == null) {
      if (this.cameraCaptureEnabled) {
        pickerFragment = PhotoPickerFragment.newInstance(true);
      }
      else {
        pickerFragment = PhotoPickerFragment
                .newInstance(showCamera, showGif,
                        previewEnabled, singleMode, cropEnabled,
                        columnNumber, maxCount, originalPhotos);
      }

      getSupportFragmentManager()
          .beginTransaction()
          .replace(R.id.container, pickerFragment, "tag")
          .commit();
      getSupportFragmentManager().executePendingTransactions();
    }

    pickerFragment.getPhotoGridAdapter().setOnItemCheckListener(new OnItemCheckListener() {
      @Override public boolean onItemCheck(int position, Photo photo, final boolean isCheck, int selectedItemCount) {

        int total = selectedItemCount + (isCheck ? -1 : 1);

        if (menuDoneItem != null) {
          menuDoneItem.setEnabled(total > 0);
        }

        if (maxCount <= 1) {
          List<String> photos = pickerFragment.getPhotoGridAdapter().getSelectedPhotos();
          if (!photos.contains(photo.getPath())) {
            photos.clear();
            pickerFragment.getPhotoGridAdapter().notifyDataSetChanged();
          }
          return true;
        }

        if (total > maxCount) {
          Toast.makeText(getActivity(), getString(R.string.__picker_over_max_count_tips, maxCount),
              LENGTH_LONG).show();
          return false;
        }
        if (menuDoneItem != null) {
          menuDoneItem.setTitle(getString(R.string.__picker_done_with_count, total, maxCount));
        }
        return true;
      }

      @Override
      public boolean onSingleSelectCheck(int position, Photo photo, boolean isCheck, int selectedItemCount) {
        if (singleMode) {
          List<String> photos = pickerFragment.getPhotoGridAdapter().getSelectedPhotos();
          photos.clear();
          photos.add(photo.getPath());
          pickerFragment.getPhotoGridAdapter().notifyDataSetChanged();
          if (cropEnabled) {
            goToCrop(photo.getPath());
          }
          else {
            finishWithResult();
          }
          return true;
        }
        else {
          return false;
        }
      }
    });

  }

  private void setupToolbar() {
    final Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(mToolbar);
    // setTitle(R.string.__picker_title);


    ActionBar actionBar = getSupportActionBar();

    assert actionBar != null;
    actionBar.setDisplayHomeAsUpEnabled(true);
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      actionBar.setElevation(25);
    }

    albumSwitcher = (ViewGroup) findViewById(R.id.album_switcher);
    albumSwitcherTitleView = (TextView) findViewById(R.id.album_switcher_title_view);
    albumSwitcher.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        pickerFragment.onAlbumSelectListener(mToolbar, albumSwitcherTitleView);
      }
    });
  }


  /**
   * Overriding this method allows us to run our exit animation first, then exiting
   * the activity when it complete.
   */
  @Override public void onBackPressed() {
    if (imagePagerFragment != null && imagePagerFragment.isVisible()) {
      imagePagerFragment.runExitAnimation(new Runnable() {
        public void run() {
          if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
          }
        }
      });
    } else {
      super.onBackPressed();
    }
  }


  public void addImagePagerFragment(ImagePagerFragment imagePagerFragment) {
    this.imagePagerFragment = imagePagerFragment;
    this.imagePagerFragment.setOnPhotoClickedListener(new OnPhotoClickedListener() {
      @Override
      public void onPhotoClicked(int index) {
        if (!(PhotoPickerActivity.this).isFinishing()) {
          (PhotoPickerActivity.this).onBackPressed();
        }
      }
    });
    getSupportFragmentManager()
        .beginTransaction()
        .replace(R.id.container, this.imagePagerFragment)
        .addToBackStack(null)
        .commit();
  }

  @Override public boolean onCreateOptionsMenu(Menu menu) {
    if (!menuIsInflated) {
      getMenuInflater().inflate(R.menu.__picker_menu_picker, menu);
      menuDoneItem = menu.findItem(R.id.done);
      if (originalPhotos != null && originalPhotos.size() > 0) {
        menuDoneItem.setEnabled(true);
        menuDoneItem.setTitle(
                getString(R.string.__picker_done_with_count, originalPhotos.size(), maxCount));
      } else {
        menuDoneItem.setEnabled(false);
      }
      menuIsInflated = true;
      return true;
    }
    return false;
  }


  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    if (item.getItemId() == android.R.id.home) {
      super.onBackPressed();
      return true;
    }

    if (item.getItemId() == R.id.done) {
      finishWithResult();
      return true;
    }

    return super.onOptionsItemSelected(item);
  }

  private void finishWithResult() {
    Intent intent = new Intent();
    ArrayList<String> selectedPhotos = pickerFragment.getPhotoGridAdapter().getSelectedPhotoPaths();
    intent.putStringArrayListExtra(KEY_SELECTED_PHOTOS, selectedPhotos);
    setResult(RESULT_OK, intent);
    finish();
  }

  public PhotoPickerActivity getActivity() {
    return this;
  }

  public boolean isShowGif() {
    return showGif;
  }

  public void setShowGif(boolean showGif) {
    this.showGif = showGif;
  }




  /** Copy from MultiSelector TODO:
   * 裁剪图片
   */
  private void goToCrop(String imgPath) {

    Intent intent = new Intent();
    intent.setClass(this, CropImageActivity_.class);
    Bundle b = new Bundle();
    b.putString("imgPath", imgPath);
    intent.putExtras(b);

    this.startActivityForResult(intent, CropImageActivity_.REQUEST_CROP_IMAGE);
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (requestCode == CropImageActivity_.REQUEST_CROP_IMAGE && resultCode == RESULT_OK) {
      List<String> photos = pickerFragment.getPhotoGridAdapter().getSelectedPhotos();
      photos.clear();
      photos.add(data.getStringExtra("imgPath"));
      finishWithResult();
    }
  }
}
