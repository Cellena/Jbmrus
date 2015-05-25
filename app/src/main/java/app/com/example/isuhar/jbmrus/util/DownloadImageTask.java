package app.com.example.isuhar.jbmrus.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;


import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.regex.Pattern;

/**
 * Created by iSuhar on 24.05.2015.
 */
public class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {

    DiskLruImageCache myImgCache;
    String imageKey;
    ImageView myImage;

    public DownloadImageTask(DiskLruImageCache imgCache, ImageView image){

        myImgCache = imgCache;
        myImage = image;
    }

    protected Bitmap doInBackground(String... urls) {

        String urlDisplay = urls[0];
        String[] parts = urlDisplay.split("/");
        imageKey = parts[6];
        parts = imageKey.split(Pattern.quote("."));
        imageKey = parts[0];

        Bitmap mIcon = null;
        try {
            InputStream in = new java.net.URL(urlDisplay).openStream();
            mIcon = BitmapFactory.decodeStream(in);
        } catch (Exception e) {
            Log.e("Error", e.getMessage());
            e.printStackTrace();
        }

        return mIcon;
    }

    protected void onPostExecute(Bitmap result) {
        myImgCache.put(imageKey, result);
        myImage.setImageBitmap(result);
    }
}