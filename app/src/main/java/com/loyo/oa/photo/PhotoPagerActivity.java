package com.loyo.oa.photo;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.loyo.oa.photo.fragment.ImagePagerFragment;
import com.loyo.oa.v2.R;

import java.util.List;

import static com.loyo.oa.photo.PhotoPreview.KEY_DELETE_INDEX;


/**
 * https://github.com/donglua/PhotoPicker
 */
public class PhotoPagerActivity extends AppCompatActivity {

  private ImagePagerFragment pagerFragment;
  private ImageView backBtn;
  private ImageView deleteBtn;
  private TextView titleView;

  private ActionBar actionBar;
  private boolean showDelete;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.__picker_activity_photo_pager);

    backBtn = (ImageView) findViewById(R.id.img_back);
    deleteBtn = (ImageView) findViewById(R.id.img_delete);
    setupCallbacks();
    titleView = (TextView)findViewById(R.id.title_view);

    int currentItem = getIntent().getIntExtra(PhotoPreview.EXTRA_CURRENT_ITEM, 0);
    List<String> paths = getIntent().getStringArrayListExtra(PhotoPreview.EXTRA_PHOTOS);
    showDelete = getIntent().getBooleanExtra(PhotoPreview.EXTRA_SHOW_DELETE, true);
    deleteBtn.setVisibility(showDelete?View.VISIBLE:View.GONE);

    if (pagerFragment == null) {
      pagerFragment =
          (ImagePagerFragment) getSupportFragmentManager().findFragmentById(R.id.photoPagerFragment);
    }
    pagerFragment.setPhotos(paths, currentItem);

    pagerFragment.getViewPager().addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
      @Override public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        updateTitle();
      }
    });
    pagerFragment.setOnPhotoClickedListener(null);
  }


  @Override public boolean onCreateOptionsMenu(Menu menu) {
    return true;
  }


  @Override public void onBackPressed() {

    Intent intent = new Intent();
    intent.putExtra(PhotoPicker.KEY_SELECTED_PHOTOS, pagerFragment.getPaths());
    intent.putExtra(KEY_DELETE_INDEX,-1);
    setResult(RESULT_OK, intent);
    finish();

    super.onBackPressed();
  }


  @Override
  public boolean onOptionsItemSelected(MenuItem item) {

    if (item.getItemId() == android.R.id.home) {
      onBackPressed();
      return true;
    }

    if (item.getItemId() == R.id.delete) {
      return true;
    }

    return super.onOptionsItemSelected(item);
  }

  private void setupCallbacks() {
    backBtn.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        onBackPressed();
      }
    });

    deleteBtn.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        final int index = pagerFragment.getCurrentItem();

        final String deletedPath =  pagerFragment.getPaths().get(index);
        // show confirm dialog
        new AlertDialog.Builder(PhotoPagerActivity.this)
                .setTitle(R.string.__picker_confirm_to_delete)
                .setPositiveButton(R.string.__picker_yes, new DialogInterface.OnClickListener() {
                  @Override public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                    Intent intent = new Intent();
                    intent.putExtra(KEY_DELETE_INDEX,index);
                    setResult(RESULT_OK, intent);
                    finish();
                  }
                })
                .setNegativeButton(R.string.__picker_cancel, new DialogInterface.OnClickListener() {
                  @Override public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                  }
                })
                .show();
      }
    });
  }

  public void updateTitle() {
    if (titleView != null) titleView.setText(
        getString(R.string.__picker_image_index, pagerFragment.getViewPager().getCurrentItem() + 1,
            pagerFragment.getPaths().size()));
  }
}
