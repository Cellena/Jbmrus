package app.com.example.isuhar.jbmrus.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.util.LruCache;
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
    LruCache<String, Bitmap> myMemoryCache;
    Bitmap bitmap;

    public DownloadImageTask(DiskLruImageCache imgCache, ImageView image,
                             LruCache<String, Bitmap> mMemoryCache){
        myMemoryCache = mMemoryCache;
        myImgCache = imgCache;
        myImage = image;
    }

    protected Bitmap doInBackground(String... urls) {

        String urlDisplay = urls[0];
        String[] parts = urlDisplay.split("/");
        imageKey = parts[6];
        parts = imageKey.split(Pattern.quote("."));
        imageKey = parts[0];

        bitmap =  myMemoryCache.get(imageKey);

        if (bitmap==null) {
            bitmap = myImgCache.getBitmap(imageKey);
            if (bitmap == null) { // Not found in disk cache
                try {
                    InputStream in = new java.net.URL(urlDisplay).openStream();
                    bitmap = BitmapFactory.decodeStream(in);
                } catch (Exception e) {
                    Log.e("Error", e.getMessage());
                    e.printStackTrace();
                }
                myImgCache.put(imageKey,bitmap);
                myMemoryCache.put(imageKey, bitmap);
            }
            else {
                myMemoryCache.put(imageKey, bitmap);
            }
        }

        return bitmap;
    }

    @Override
    protected void onPostExecute(Bitmap result) {
        myImage.setImageBitmap(bitmap);
    }

}