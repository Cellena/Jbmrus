package app.com.example.isuhar.jbmrus.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.util.LruCache;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.InputStream;
import java.util.regex.Pattern;

import static android.widget.Toast.LENGTH_LONG;

/**
 * Created by iSuhar on 24.05.2015.
 */
public class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {

    DiskLruImageCache myImgCache;
    String imageKey;
    ImageView myImage;
    LruCache<String, Bitmap> myMemoryCache;
    Bitmap bitmap;
    Context mContext;

    public DownloadImageTask(DiskLruImageCache imgCache, ImageView image,
                             LruCache<String, Bitmap> mMemoryCache, Context context){
        myMemoryCache = mMemoryCache;
        myImgCache = imgCache;
        myImage = image;
        mContext = context;
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
                        if (bitmap==null) Toast.makeText(mContext, "Ошибка при загрузке",
                                LENGTH_LONG).show();;

                } catch (Exception e) {
                    Log.e("Error", e.getMessage());
                    e.printStackTrace();
                }
                if (bitmap!=null) {
                    myImgCache.put(imageKey, bitmap);
                    myMemoryCache.put(imageKey, bitmap);
                }
            }
            else {
                myMemoryCache.put(imageKey, bitmap);
            }
        }

        return bitmap;
    }

    @Override
    protected void onPostExecute(Bitmap result) {
        if (bitmap!=null) myImage.setImageBitmap(bitmap);
    }

}