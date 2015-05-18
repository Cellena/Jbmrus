package app.com.example.isuhar.jbmrus.data;

/**
 * Created by iSuhar on 17.05.2015.
 */
import android.annotation.TargetApi;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

import java.sql.SQLException;

public class CatalogProvider extends ContentProvider {

    // The URI Matcher used by this content provider.
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private CatalogDbHelper mOpenHelper;


    static final int OFFERS = 100;
    static final int OFFERS_BY_CATEGORIES = 101;
    static final int CATEGORIES = 102;

    private static final SQLiteQueryBuilder sOffersByCategoriesQueryBuilder;

    static {
        sOffersByCategoriesQueryBuilder = new SQLiteQueryBuilder();

        sOffersByCategoriesQueryBuilder.setTables(
                CatalogContract.OffersEntry.TABLE_NAME + " INNER JOIN " +
                        CatalogContract.CategoriesEntry.TABLE_NAME +
                        " ON " + CatalogContract.OffersEntry.TABLE_NAME +
                        "." + CatalogContract.OffersEntry.COLUMN_CAT_KEY +
                        " = " + CatalogContract.CategoriesEntry.TABLE_NAME +
                        "." + CatalogContract.CategoriesEntry._ID);
    }

    static UriMatcher buildUriMatcher() {

        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = CatalogContract.CONTENT_AUTHORITY;

        matcher.addURI(authority, CatalogContract.PATH_OFFERS, OFFERS);
        matcher.addURI(authority, CatalogContract.PATH_OFFERS + "/*", OFFERS_BY_CATEGORIES);

        matcher.addURI(authority, CatalogContract.PATH_CATEGORIES, CATEGORIES);

        return matcher;
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new CatalogDbHelper(getContext());
        return true;
    }

    @Override
    public String getType(Uri uri) {

        final int match = sUriMatcher.match(uri);

        switch (match) {

            case OFFERS_BY_CATEGORIES:
                return CatalogContract.OffersEntry.CONTENT_TYPE;
            case OFFERS:
                return CatalogContract.OffersEntry.CONTENT_TYPE;
            case CATEGORIES:
                return CatalogContract.CategoriesEntry.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    private Cursor getOffersByCategories(Uri uri, String[] projection, String sortOrder,
                                         String[] selectionArgs) {

        String selection = CatalogContract.OffersEntry.TABLE_NAME + "."
                + CatalogContract.OffersEntry.COLUMN_CAT_KEY + " = ? ";

        return mOpenHelper.getReadableDatabase().query(
                CatalogContract.OffersEntry.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );
    }
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {

        Cursor retCursor;
        switch (sUriMatcher.match(uri)) {

            case OFFERS_BY_CATEGORIES: {
                retCursor = getOffersByCategories(uri, projection, sortOrder, selectionArgs);
                break;
            }
            case OFFERS: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        CatalogContract.OffersEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            case CATEGORIES: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        CatalogContract.CategoriesEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch (match) {
            case OFFERS: {
                long _id = db.insert(CatalogContract.OffersEntry.TABLE_NAME, null, values);
                if (_id > 0)
                    returnUri = CatalogContract.OffersEntry.buildOffersUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case CATEGORIES: {
                long _id = db.insert(CatalogContract.CategoriesEntry.TABLE_NAME, null, values);
                if (_id > 0) {
                    returnUri = CatalogContract.CategoriesEntry.buildCategoriesUri(_id);
                } else throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Student: Start by getting a writable database
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsDeleted;

        if (null == selection) {
            selection = "1";
        }

        switch (match) {
            case OFFERS:
                rowsDeleted = db.delete(CatalogContract.OffersEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case CATEGORIES:
                rowsDeleted = db.delete(CatalogContract.CategoriesEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        // Student: return the actual rows deleted
        return rowsDeleted;
    }

    @Override
    public int update(
            Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        // Student: Start by getting a writable database
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsUpdated;

        switch (match) {
            case OFFERS:
                rowsUpdated = db.update(CatalogContract.OffersEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            case CATEGORIES:
                rowsUpdated = db.update(CatalogContract.CategoriesEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        // Student: return the actual rows deleted
        return rowsUpdated;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int returnCount = 0;
        switch (match) {
            case OFFERS:
                db.beginTransaction();

                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(CatalogContract.OffersEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            case CATEGORIES:
                db.beginTransaction();

                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(CatalogContract.CategoriesEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;

            default:
                return super.bulkInsert(uri, values);
        }
    }

    @Override
    @TargetApi(11)
    public void shutdown() {
        mOpenHelper.close();
        super.shutdown();
    }
}