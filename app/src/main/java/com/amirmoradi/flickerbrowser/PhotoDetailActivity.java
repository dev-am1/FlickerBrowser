package com.amirmoradi.flickerbrowser;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.squareup.picasso.Picasso;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.webkit.PermissionRequest;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

public class PhotoDetailActivity extends BaseActivity {
    private static final String TAG = "PhotoDetailActivity";
    ImageView download_img;

    boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v(TAG, "Permission is granted");
                return true;
            } else {

                Log.v(TAG, "Permission is revoked");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                return false;
            }
        } else { //permission is automatically granted on sdk<23 upon installation
            Log.v(TAG, "Permission is granted");
            return true;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_detail);
        activateToolbar(true);
        download_img = findViewById(R.id.download_bottom);
        Intent intent = getIntent();
        final Photo photo = (Photo) intent.getSerializableExtra(PHOTO_TRANSFER);
        if (photo != null) {
            TextView photoTitle = findViewById(R.id.photo_title);
            photoTitle.setText(getResources().getString(R.string.photo_title_text, photo.getTitle()));

            TextView photoTags = findViewById(R.id.photo_tags);
            photoTags.setText(getResources().getString(R.string.photo_tags_text, photo.getTags()));

            TextView photoAuthor = findViewById(R.id.photo_author);
            photoAuthor.setText(getResources().getString(R.string.photo_author_text, photo.getAuthor()));

            ImageView photoImage = findViewById(R.id.photo_image);

            Picasso.get().load(photo.getLink())
                    .error(R.drawable.brokenplaceholder)
                    .placeholder(R.drawable.placeholder)
                    .into(photoImage);

            View.OnClickListener onClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isStoragePermissionGranted()) {
                        PicassoImageDownloader.imageDownload(photo.getLink(), (String.valueOf(new Random().nextInt(20000))));
                        Toast.makeText(PhotoDetailActivity.this, "downloaded.", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }
            };
            download_img.setOnClickListener(onClickListener);
        }
    }
}

