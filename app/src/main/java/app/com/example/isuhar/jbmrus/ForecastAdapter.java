package app.com.example.isuhar.jbmrus;

/**
 * Created by iSuhar on 18.05.2015.
 */
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v4.widget.CursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.InputStream;
import java.util.regex.Pattern;

import app.com.example.isuhar.jbmrus.data.CatalogContract;
import app.com.example.isuhar.jbmrus.util.DiskLruImageCache;
import app.com.example.isuhar.jbmrus.util.DownloadImageTask;

/**
 * {@link ForecastAdapter} exposes a list of weather forecasts
 * from a {@link android.database.Cursor} to a {@link android.widget.ListView}.
 */
public class ForecastAdapter extends CursorAdapter {

    DiskLruImageCache myImgCache;
    private static final int DISK_CACHE_SIZE = 1024 * 1024 * 100; // 10MB
    private static final String DISK_CACHE_SUBDIR = "images";

    TextView nameText;
    TextView priceText;
    ImageView image;


    public ForecastAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }


    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_offers_forecast, parent, false);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        myImgCache = new DiskLruImageCache(context, DISK_CACHE_SUBDIR, DISK_CACHE_SIZE);

        String myNameText = cursor.getString(1);
        String myPriceText = cursor.getString(2);
        String myImgUrl = cursor.getString(3);

        nameText = (TextView) view.findViewById(R.id.list_item_name_textview);
        priceText = (TextView) view.findViewById(R.id.list_item_price_textview);
        image = (ImageView) view.findViewById(R.id.imageView_offer);

        nameText.setText(myNameText);
        priceText.setText(myPriceText);

        String[] parts = myImgUrl.split("/");
        String imageKey = parts[6];
        parts = imageKey.split(Pattern.quote("."));
        imageKey = parts[0];

        Bitmap bitmap = myImgCache.getBitmap(imageKey);

        if (bitmap == null) { // Not found in disk cache
            DownloadImageTask downImg = new DownloadImageTask(myImgCache, image);
            downImg.execute(myImgUrl);
        }
        else {
            image.setImageBitmap(bitmap);
        }
    }

}