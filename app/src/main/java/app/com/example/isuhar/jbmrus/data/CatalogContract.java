package app.com.example.isuhar.jbmrus.data;

/**
 * Created by iSuhar on 17.05.2015.
 */
import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;
import android.text.format.Time;

/**
 * Defines table and column names for the weather database.
 */
public class CatalogContract {


    public static final String CONTENT_AUTHORITY = "app.com.example.isuhar.jbmrus";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_CATEGORIES = "categories";
    public static final String PATH_OFFERS = "offers";
    public static final  String PATH_ORDER = "order";
    public static final  String PATH_CART = "cart";


    /* Inner class that defines the table contents of the location table */
    public static final class CategoriesEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_CATEGORIES).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_CATEGORIES;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_CATEGORIES;

        public static final String TABLE_NAME = "categories";

        public static final String COLUMN_CATEGORY_NAME = "category_name";

        public static Uri buildCategoriesUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }

    public static final class OffersEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_OFFERS).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_OFFERS;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_OFFERS;

        public static final String TABLE_NAME = "offers";

        public static final String COLUMN_CAT_KEY = "category_id";

        public static final String COLUMN_OFFER_NAME = "offer_name";

        public static final String COLUMN_OFFER_PRICE = "price";

        public static final String COLUMN_OFFER_IMG = "img";


        public static Uri buildOffersUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

    }
    public static final class OrderEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_ORDER).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_ORDER;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_ORDER;

        public static final String TABLE_NAME = "orders";

        public static final String COLUMN_ORDER_OFFER_ID = "id_offer";
        public static final String COLUMN_ORDER_OFFER_COUNT = "count_offer";

    }
}