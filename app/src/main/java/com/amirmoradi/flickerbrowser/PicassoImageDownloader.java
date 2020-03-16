package com.amirmoradi.flickerbrowser;

import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Comparator;

import static androidx.constraintlayout.widget.Constraints.TAG;

/**
 * Created by Dev_am1 on 12/4/2019
 */
class PicassoImageDownloader {

    //save image
    public static boolean imageDownload(String url, String path) {
        Picasso.get().load(url)
                .into(getTarget(path));
        return getTarget(path) != null;

    }


    //target to save
    static Target getTarget(final String path) {

        Target target = new Target() {

            @Override
            public void onBitmapLoaded(final Bitmap bitmap, Picasso.LoadedFrom from) {
                new Thread(new Runnable() {

                    @Override
                    public void run() {

                        Log.d(TAG, "run: creating new file");

                        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), path + ".png");
                        file.mkdir();
                        if (!file.exists() && Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                            file.mkdir();

                        } else {
                            file.delete();
                        }

                        try {
                            if (!file.exists() && Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                Files.createFile(file.toPath());
                            }
                            FileOutputStream ostream = new FileOutputStream(file);
                            bitmap.compress(Bitmap.CompressFormat.PNG, 100, ostream);
                            ostream.flush();
                            ostream.close();
                        } catch (IOException e) {
                            Log.e("IOException", e.getMessage());
                            e.getStackTrace();


                        }
                    }
                }).start();

            }

            @Override
            public void onBitmapFailed(Exception e, Drawable errorDrawable) {

            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {

            }
        };
        return target;
    }
}