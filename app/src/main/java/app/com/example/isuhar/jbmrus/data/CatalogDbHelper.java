package app.com.example.isuhar.jbmrus.data;

/**
 * Created by iSuhar on 17.05.2015.
 */
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import app.com.example.isuhar.jbmrus.data.CatalogContract.CategoriesEntry;
import app.com.example.isuhar.jbmrus.data.CatalogContract.OffersEntry;


public class CatalogDbHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 2;

    static final String DATABASE_NAME = "catalog.db";

    public CatalogDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String SQL_CREATE_OFFERS_TABLE = "CREATE TABLE " + OffersEntry.TABLE_NAME + " (" +

                OffersEntry._ID + " INTEGER PRIMARY KEY," +

                OffersEntry.COLUMN_CAT_KEY + " INTEGER NOT NULL, " +
                OffersEntry.COLUMN_OFFER_NAME + " TEXT NOT NULL, " +
                OffersEntry.COLUMN_OFFER_PRICE + " REAL NOT NULL, " +
                OffersEntry.COLUMN_OFFER_IMG + " TEXT NOT NULL, " +

                " FOREIGN KEY (" + OffersEntry.COLUMN_CAT_KEY + ") REFERENCES " +
                CategoriesEntry.TABLE_NAME + " (" + CategoriesEntry._ID +
                "));";

        final String SQL_CREATE_CATEGORIES_TABLE = "CREATE TABLE " + CategoriesEntry.TABLE_NAME + " (" +
                CategoriesEntry._ID + " INTEGER PRIMARY KEY," +
                CategoriesEntry.COLUMN_CATEGORY_NAME + " TEXT NOT NULL " +
                " );";

        sqLiteDatabase.execSQL(SQL_CREATE_CATEGORIES_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_OFFERS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {

        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + CategoriesEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + OffersEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}