package app.com.example.isuhar.jbmrus;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.support.v4.widget.CursorAdapter;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import app.com.example.isuhar.jbmrus.util.DiskLruImageCache;
import app.com.example.isuhar.jbmrus.util.DownloadImageTask;

/**
 * {@link ForecastAdapter} exposes a list of weather forecasts
 * from a {@link android.database.Cursor} to a {@link android.widget.ListView}.
 */
public class CartAdapter extends CursorAdapter {

    DiskLruImageCache myImgCache;
    private static final int DISK_CACHE_SIZE = 1024 * 1024 * 100; // 10MB
    private static final String DISK_CACHE_SUBDIR = "images";

    private LruCache<String, Bitmap> mMemoryCache;
    final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
    final int cacheSize = maxMemory / 8;

    TextView nameText;
    TextView priceText;
    TextView countText;
    ImageView image;

    public CartAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);

        mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                // The cache size will be measured in kilobytes rather than
                // number of items.
                return bitmap.getByteCount() / 1024;
            }
        };
        myImgCache = new DiskLruImageCache(context, DISK_CACHE_SUBDIR, DISK_CACHE_SIZE);
    }


    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item_cart, parent, false);

        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        String myNameText = cursor.getString(1);
        String myPriceText = cursor.getString(2);
        String myImgUrl = cursor.getString(4);
        String myCountText = cursor.getString(3);

        nameText = (TextView) view.findViewById(R.id.list_item_name_textview2);
        priceText = (TextView) view.findViewById(R.id.list_item_price_textview2);
        countText = (TextView) view.findViewById(R.id.list_item_count_textview);
        image = (ImageView) view.findViewById(R.id.imageView_offer_cart);

        nameText.setText(myNameText);
        priceText.setText(myPriceText);
        countText.setText(myCountText);

        DownloadImageTask ImageTask = new DownloadImageTask(myImgCache, image, mMemoryCache, context);
        ImageTask.execute(myImgUrl);
    }
}