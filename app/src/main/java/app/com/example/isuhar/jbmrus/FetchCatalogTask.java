package app.com.example.isuhar.jbmrus;

/**
 * Created by iSuhar on 17.05.2015.
 */
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.text.format.Time;
import android.util.Log;

import app.com.example.isuhar.jbmrus.data.CatalogContract;
import app.com.example.isuhar.jbmrus.data.CatalogContract.OffersEntry;
import app.com.example.isuhar.jbmrus.data.CatalogContract.CategoriesEntry;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Vector;

public class FetchCatalogTask extends AsyncTask<String, Void, Void> {

    private final String LOG_TAG = FetchCatalogTask.class.getSimpleName();

    private final Context mContext;

    public FetchCatalogTask(Context context) {
        mContext = context;
    }

    private boolean DEBUG = true;

    private void getCatalogDataFromJson(String forecastJsonStr)
            throws JSONException {

        final String OWM_CATEGORIES = "0";
        final String OWM_CATEGORIES_ID = "id";
        final String OWM_CATEGORIES_NAME = "name";

        final String OWM_OFFERS = "1";
        final String OWM_OFFER_ID = "id";
        final String OWM_OFFER_NAME = "name";
        final String OWM_OFFER_CATEGORY_ID = "id_category";
        final String OWM_OFFER_PRICE = "price";
        final String OWM_OFFER_IMG = "img";

        try {
            //Прием и сохранение катеорий
            JSONObject forecastJson = new JSONObject(forecastJsonStr);
            JSONArray categoriesArray = forecastJson.getJSONArray(OWM_CATEGORIES);//не принимает инт
            JSONObject cat = null;
            String categoryName = null;
            int categoryId = 0;

            Vector<ContentValues> cVVector = new Vector<ContentValues>(categoriesArray.length());

            for (int i = 0; i < categoriesArray.length(); i++){

                cat = categoriesArray.getJSONObject(i);
                categoryName = cat.getString(OWM_CATEGORIES_NAME);
                categoryId = cat.getInt(OWM_CATEGORIES_ID);

                ContentValues CategoriesValues = new ContentValues();

                CategoriesValues.put(CategoriesEntry.COLUMN_CATEGORY_NAME, categoryName);
                CategoriesValues.put(CategoriesEntry._ID, categoryId);

                cVVector.add(CategoriesValues);
            }

            int inserted = 0;
            // add to database
            if ( cVVector.size() > 0 ) {
                ContentValues[] cvArray = new ContentValues[cVVector.size()];
                cVVector.toArray(cvArray);
                inserted = mContext.getContentResolver().bulkInsert(CategoriesEntry.CONTENT_URI, cvArray);
            }
            //прием и сохранение товаров
            JSONArray offersArray = forecastJson.getJSONArray(OWM_OFFERS);//не принимает инт
            JSONObject offer = null;
            String offerName = null;
            int offerId = 0;
            int offerIdCat = 0;
            double offerPrice = 0;
            String offerImg = null;

            Vector<ContentValues> oVVector = new Vector<ContentValues>(offersArray.length());

            for (int i = 0; i < offersArray.length(); i++){

                offer = categoriesArray.getJSONObject(i);
                offerName = offer.getString(OWM_OFFER_NAME);
                offerId = offer.getInt(OWM_OFFER_ID);
                offerIdCat = offer.getInt(OWM_OFFER_CATEGORY_ID);
                offerPrice = offer.getDouble(OWM_OFFER_PRICE);
                offerImg = offer.getString(OWM_OFFER_IMG);

                ContentValues OffersValues = new ContentValues();

                OffersValues.put(OffersEntry.COLUMN_OFFER_NAME,offerName);
                OffersValues.put(OffersEntry._ID, offerId);
                OffersValues.put(OffersEntry.COLUMN_CAT_KEY, offerIdCat);
                OffersValues.put(OffersEntry.COLUMN_OFFER_PRICE, offerPrice);
                OffersValues.put(OffersEntry.COLUMN_OFFER_IMG, offerImg);

                oVVector.add(OffersValues);
            }

            inserted = 0;
            // add to database
            if ( oVVector.size() > 0 ) {
                ContentValues[] cvArray = new ContentValues[oVVector.size()];
                oVVector.toArray(cvArray);
                inserted = mContext.getContentResolver().bulkInsert(CategoriesEntry.CONTENT_URI, cvArray);
            }

            Log.d(LOG_TAG, "FetchCatalogTask Complete. " + inserted + " Inserted");

        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }
    }

    @Override
    protected Void doInBackground(String... params) {

        // If there's no zip code, there's nothing to look up.  Verify size of params.
        if (params.length == 0) {
            return null;
        }
        String locationQuery = params[0];

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        String forecastJsonStr = null;

        try {

            final String FORECAST_BASE_URL =
                    "http://jbmrus.16mb.com/api/?";
            final String QUERY_PARAM = "get";
            final String IMG_PARAM = "dpi";
            params[0] = "ALL";
            params[1] = "hdpi";

            Uri builtUri = Uri.parse(FORECAST_BASE_URL).buildUpon()
                    .appendQueryParameter(QUERY_PARAM, params[0])
                    .appendQueryParameter(IMG_PARAM, params[1])
                    .build();

            URL url = new URL(builtUri.toString());

            // Create the request and open the connection
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                // Nothing to do.
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                // Stream was empty.  No point in parsing.
                return null;
            }
            forecastJsonStr = buffer.toString();
            getCatalogDataFromJson(forecastJsonStr);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error ", e);
            // If the code didn't successfully get the weather data, there's no point in attempting
            // to parse it.
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(LOG_TAG, "Error closing stream", e);
                }
            }
        }
        return null;
    }
}