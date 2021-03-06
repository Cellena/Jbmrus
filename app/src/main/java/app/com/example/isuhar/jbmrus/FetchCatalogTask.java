package app.com.example.isuhar.jbmrus;

/**
 * Created by iSuhar on 17.05.2015.
 */
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.Vector;

import app.com.example.isuhar.jbmrus.data.CatalogContract.CategoriesEntry;
import app.com.example.isuhar.jbmrus.data.CatalogContract.OffersEntry;

import static android.widget.Toast.LENGTH_LONG;

public class FetchCatalogTask extends AsyncTask<String, Void, Void> {

    private final String LOG_TAG = FetchCatalogTask.class.getSimpleName();

    private final Context mContext;
    private boolean mySwitchJson = true;
    private String myCategoryId = "";

    public FetchCatalogTask(Context context, boolean switchJson, String categoryId) {
        mContext = context;
        mySwitchJson = switchJson;
        myCategoryId = categoryId;
    }

    private boolean DEBUG = true;

    private void getCatalogDataFromJson(String forecastJsonStr)
            throws JSONException {

        if (mySwitchJson) {

            final int OWM_CATEGORIES = 0;
            final String OWM_CATEGORIES_ID = "id";
            final String OWM_CATEGORIES_NAME = "name";


            try {
                //Прием и сохранение катеорий
                //JSONObject forecastJson = new JSONObject(forecastJsonStr);
                JSONArray categoriesArray = new JSONArray(forecastJsonStr);

                JSONObject cat = null;
                String categoryName = null;
                int categoryId = 0;

                Vector<ContentValues> cVVector = new Vector<ContentValues>(categoriesArray.length());

                for (int i = 0; i < categoriesArray.length(); i++) {

                    cat = categoriesArray.getJSONObject(i);
                    categoryName = cat.getString(OWM_CATEGORIES_NAME);
                    categoryId = cat.getInt(OWM_CATEGORIES_ID);

                    ContentValues CategoriesValues = new ContentValues();

                    CategoriesValues.put(CategoriesEntry._ID, categoryId);
                    CategoriesValues.put(CategoriesEntry.COLUMN_CATEGORY_NAME, categoryName);

                    cVVector.add(CategoriesValues);
                }

                int inserted = 0;
                // add to database
                if (cVVector.size() > 0) {
                    ContentValues[] cvArray = new ContentValues[cVVector.size()];
                    cVVector.toArray(cvArray);
                    inserted = mContext.getContentResolver().bulkInsert(CategoriesEntry.CONTENT_URI, cvArray);
                }
                Log.d(LOG_TAG, "FetchCatalogTask Complete. " + inserted + " Inserted");

            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }
        }
        else {
            try {
                JSONArray offersArray = new JSONArray(forecastJsonStr);
                //прием и сохранение товаров
                int inserted = 0;
                final int OWM_OFFERS = 1;
                final String OWM_OFFER_ID = "id";
                final String OWM_OFFER_NAME = "name";
                final String OWM_OFFER_CATEGORY_ID = "id_category";
                final String OWM_OFFER_PRICE = "price";
                final String OWM_OFFER_IMG = "img";

                //JSONArray offersArray = myJSONArray.getJSONArray(OWM_OFFERS);
                JSONObject offer = null;
                String offerName = null;
                int offerId = 0;
                int offerIdCat = 0;
                double offerPrice = 0;
                String offerImg = null;

                Vector<ContentValues> oVVector = new Vector<ContentValues>(offersArray.length());

                for (int i = 0; i < offersArray.length(); i++) {

                    offer = offersArray.getJSONObject(i);
                    offerName = offer.getString(OWM_OFFER_NAME);
                    offerId = offer.getInt(OWM_OFFER_ID);
                    offerIdCat = offer.getInt(OWM_OFFER_CATEGORY_ID);
                    offerPrice = offer.getDouble(OWM_OFFER_PRICE);
                    offerImg = offer.getString(OWM_OFFER_IMG);


                    ContentValues OffersValues = new ContentValues();

                    OffersValues.put(OffersEntry.COLUMN_OFFER_NAME, offerName);
                    OffersValues.put(OffersEntry._ID, offerId);
                    OffersValues.put(OffersEntry.COLUMN_CAT_KEY, offerIdCat);
                    OffersValues.put(OffersEntry.COLUMN_OFFER_PRICE, offerPrice);
                    OffersValues.put(OffersEntry.COLUMN_OFFER_IMG, offerImg);

                    oVVector.add(OffersValues);
                }

                inserted = 0;
                // add to database
                if (oVVector.size() > 0) {
                    ContentValues[] cvArray = new ContentValues[oVVector.size()];
                    oVVector.toArray(cvArray);
                    inserted = mContext.getContentResolver().bulkInsert(OffersEntry.CONTENT_URI, cvArray);
                }

                Log.d(LOG_TAG, "FetchCatalogTask Complete. " + inserted + " Inserted");

            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }
        }
    }

    @Override
    protected Void doInBackground(String... params) {

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        String forecastJsonStr = null;
        try {

            final String FORECAST_BASE_URL =
                    "http://jbmrus.16mb.com/api/?";
            final String QUERY_PARAM = "get";
            final String IMG_PARAM = "dpi";
            final String CATEGORY_PARAM = "categoryId";
            final String dpi = "xhdpi";
            Uri builtUri;

            if (mySwitchJson) {
                final String get = "categories";

                builtUri = Uri.parse(FORECAST_BASE_URL).buildUpon()
                        .appendQueryParameter(QUERY_PARAM, get)
                        .appendQueryParameter(IMG_PARAM, dpi)
                        .build();
            }
            else {
                final String get = "offers";

                builtUri = Uri.parse(FORECAST_BASE_URL).buildUpon()
                        .appendQueryParameter(QUERY_PARAM, get)
                        .appendQueryParameter(IMG_PARAM, dpi)
                        .appendQueryParameter(CATEGORY_PARAM, myCategoryId)
                        .build();
            }

            URL url = new URL(builtUri.toString());

            // Create the request and open the connection
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();
            InputStream inputStream;

            // Read the input stream into a String
                inputStream = urlConnection.getInputStream();

                if (inputStream==null) Toast.makeText(mContext, "Load Fail",
                        LENGTH_LONG).show();;


            StringBuffer buffer = new StringBuffer();
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

            forecastJsonStr = forecastJsonStr.toString();
            getCatalogDataFromJson(forecastJsonStr);

        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
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