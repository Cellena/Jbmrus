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

        //final int OWM_CATEGORIES = 0;
        final String OWM_CATEGORIES_ID = "id";
        final String OWM_CATEGORIES_NAME = "name";

        //final String OWM_OFFERS = "1";
        final String OWM_OFFER_ID = "id";
        final String OWM_OFFER_NAME = "name";
        final String OWM_OFFER_CATEGORY_ID = "id_category";
        final String OWM_OFFER_PRICE = "price";
        final String OWM_OFFER_IMG = "img";

        try {
            //Прием и сохранение катеорий
            //JSONObject forecastJson = new JSONObject(forecastJsonStr);
            JSONArray myJSONArray = new JSONArray(forecastJsonStr);//не принимает инт
            JSONArray categoriesArray = myJSONArray.getJSONArray(0);

            JSONObject cat = null;
            String categoryName = null;
            int categoryId = 0;

            Vector<ContentValues> cVVector = new Vector<ContentValues>(categoriesArray.length());

            for (int i = 0; i < categoriesArray.length(); i++){

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
            if ( cVVector.size() > 0 ) {
                ContentValues[] cvArray = new ContentValues[cVVector.size()];
                cVVector.toArray(cvArray);
                inserted = mContext.getContentResolver().bulkInsert(CategoriesEntry.CONTENT_URI, cvArray);
            }

            //прием и сохранение товаров
            JSONArray offersArray = myJSONArray.getJSONArray(1);
            JSONObject offer = null;
            String offerName = null;
            int offerId = 0;
            int offerIdCat = 0;
            double offerPrice = 0;
            String offerImg = null;

            Vector<ContentValues> oVVector = new Vector<ContentValues>(offersArray.length());

            for (int i = 0; i < offersArray.length(); i++){

                offer = offersArray.getJSONObject(i);
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
                inserted = mContext.getContentResolver().bulkInsert(OffersEntry.CONTENT_URI, cvArray);
            }

            Log.d(LOG_TAG, "FetchCatalogTask Complete. " + inserted + " Inserted");

        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }
    }

    @Override
    protected Void doInBackground(String... params) {
        /*
        // If there's no zip code, there's nothing to look up.  Verify size of params.
        if (params.length == 0) {
            return null;
        }
        String locationQuery = params[0];

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        String forecastJsonStr = null;
        */
        try {
            /*

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
            */
            String forecastJsonStr =
                    "[[{\"id\":\"1\",\"name\":\"Кофе для турки\"},{\"id\":\"2\",\"name\":\"Кофе для кофемашины\"},{\"id\":\"3\",\"name\":\"Кофе в чалдах\"},{\"id\":\"4\",\"name\":\"Кофе в чашку\"},{\"id\":\"5\",\"name\":\"Зеленый кофе\"},{\"id\":\"6\",\"name\":\"Подарки\"},{\"id\":\"8\",\"name\":\"BLEND (Смесь)\"}],[{\"id\":\"9\",\"id_category\":\"1\",\"name\":\"100% Ямайка Блю Маунтин (100% Jamaica Blue Mountain)                \",\"price\":\"2180\",\"img\":\"http:\\/\\/jbmrus.16mb.com\\/res\\/img\\/hdpi\\/d3e2f70da8b985533c0aa9358585956b.jpeg\"},{\"id\":\"10\",\"id_category\":\"1\",\"name\":\"Кофе Ямайка Блю Маунтин, зерно, обжарка средняя (200 г)                \",\"price\":\"2180\",\"img\":\"http:\\/\\/jbmrus.16mb.com\\/res\\/img\\/hdpi\\/69a2c85f3a9a11152cef008da01bef18.jpeg\"},{\"id\":\"11\",\"id_category\":\"2\",\"name\":\"Кофе Ямайка Блю Маунтин, зерно, обжарка эспрессо (200 г)                \",\"price\":\"2180\",\"img\":\"http:\\/\\/jbmrus.16mb.com\\/res\\/img\\/hdpi\\/fec09cdaf6b02456645a1520d5872b5b.jpeg\"},{\"id\":\"12\",\"id_category\":\"5\",\"name\":\"Кофе Ямайка Блю Маунтин Gold Amber, зеленый в зернах, (300 г)                \",\"price\":\"1870\",\"img\":\"http:\\/\\/jbmrus.16mb.com\\/res\\/img\\/hdpi\\/b61e89125bc260f76fd7592bdad32e3f.jpeg\"},{\"id\":\"13\",\"id_category\":\"4\",\"name\":\"Кофе Ямайка Блю Маунтин Classic, молотый, 10*10 г, (100 г)                \",\"price\":\"1300\",\"img\":\"http:\\/\\/jbmrus.16mb.com\\/res\\/img\\/hdpi\\/bf5b825b910beb2582a39a5d28e6d1f9.jpeg\"},{\"id\":\"14\",\"id_category\":\"4\",\"name\":\"Кофе Ямайка Блю Маунтин Gold Amber, молотый, 10*10 г, (100 г)                \",\"price\":\"1430\",\"img\":\"http:\\/\\/jbmrus.16mb.com\\/res\\/img\\/hdpi\\/a8b346e73567c079583d07081d0ee034.jpeg\"},{\"id\":\"15\",\"id_category\":\"8\",\"name\":\"Кофе Ямайка Блю Маунтин Blend, молотый, 10*7 г, (70 г)                \",\"price\":\"374\",\"img\":\"http:\\/\\/jbmrus.16mb.com\\/res\\/img\\/hdpi\\/f82e95ea3a6b88b11a3d04fee690095a.jpeg\"},{\"id\":\"16\",\"id_category\":\"1\",\"name\":\"Кофе Ямайка Блю Маунтин, зерно, обжарка средняя (500 г)                \",\"price\":\"5148\",\"img\":\"http:\\/\\/jbmrus.16mb.com\\/res\\/img\\/hdpi\\/35641608797ab67b24d63c873dbe9054.jpeg\"},{\"id\":\"17\",\"id_category\":\"2\",\"name\":\"Кофе Ямайка Блю Маунтин, зерно, обжарка темная (500 г)                \",\"price\":\"5148\",\"img\":\"http:\\/\\/jbmrus.16mb.com\\/res\\/img\\/hdpi\\/24f0ecb2b0dda5d33abfb2dd9a654209.jpeg\"},{\"id\":\"18\",\"id_category\":\"1\",\"name\":\"Кофе Ямайка Блю Маунтин, зерно, обжарка средняя (1 кг)                \",\"price\":\"9270\",\"img\":\"http:\\/\\/jbmrus.16mb.com\\/res\\/img\\/hdpi\\/cb0431ea29885b3802a3cfa115e3c8b4.jpeg\"},{\"id\":\"19\",\"id_category\":\"2\",\"name\":\"Кофе Ямайка Блю Маунтин, зерно, обжарка эспрессо (1 кг)                \",\"price\":\"9270\",\"img\":\"http:\\/\\/jbmrus.16mb.com\\/res\\/img\\/hdpi\\/e57dc6d1a931a04a5e52e43861ed997d.jpeg\"},{\"id\":\"20\",\"id_category\":\"5\",\"name\":\"Кофе Ямайка Блю Маунтин Gold Amber, зеленый в зернах, (1 кг)                \",\"price\":\"6270\",\"img\":\"http:\\/\\/jbmrus.16mb.com\\/res\\/img\\/hdpi\\/7da1cfd0bcbe4d6a8818c082b67db226.jpeg\"},{\"id\":\"21\",\"id_category\":\"6\",\"name\":\"Кофе Ямайка Блю Маунтин Gold Amber, зерно, обжарка средняя, Бочонок (150 г)                \",\"price\":\"2970\",\"img\":\"http:\\/\\/jbmrus.16mb.com\\/res\\/img\\/hdpi\\/9b551bad05a5a8af7b19f877052c48da.jpeg\"},{\"id\":\"22\",\"id_category\":\"6\",\"name\":\"Кофе Ямайка Блю Маунтин, зерно, обжарка средняя, Бочонок (200 г)                \",\"price\":\"3250\",\"img\":\"http:\\/\\/jbmrus.16mb.com\\/res\\/img\\/hdpi\\/deedb9b97d4f11005e6bbbc12ac38d70.jpeg\"},{\"id\":\"23\",\"id_category\":\"6\",\"name\":\"Кофе Ямайка Блю Маунтин, зерно, обжарка средняя, Бочонок (1000 г)                \",\"price\":\"11750\",\"img\":\"http:\\/\\/jbmrus.16mb.com\\/res\\/img\\/hdpi\\/2f959d57c44e244808dcd631f0d5f8a7.jpeg\"},{\"id\":\"24\",\"id_category\":\"8\",\"name\":\"Кофе Ямайка Блю Маунтин Blend, зерно, обжарка средняя (500 г)                \",\"price\":\"3200\",\"img\":\"http:\\/\\/jbmrus.16mb.com\\/res\\/img\\/hdpi\\/a95873dcc163fb3aefe0b3378d8e00e2.jpeg\"},{\"id\":\"25\",\"id_category\":\"8\",\"name\":\"Кофе Ямайка Блю Маунтин Blend, зерно, обжарка средняя (1 кг)                \",\"price\":\"5300\",\"img\":\"http:\\/\\/jbmrus.16mb.com\\/res\\/img\\/hdpi\\/8db004ca820d4cd5f650162c45706cd9.jpeg\"},{\"id\":\"26\",\"id_category\":\"6\",\"name\":\"Подарочный набор Ямайка Блю Маунтин 2 мешочка по 200 г.                \",\"price\":\"5280\",\"img\":\"http:\\/\\/jbmrus.16mb.com\\/res\\/img\\/hdpi\\/ae85630b3b16658f15acca7b7ae42b49.jpeg\"},{\"id\":\"27\",\"id_category\":\"1\",\"name\":\"Ямайка Блю Маунтин «AMBER ESTATE» , 454 г. обжарен на о.Ямайка                \",\"price\":\"5280\",\"img\":\"http:\\/\\/jbmrus.16mb.com\\/res\\/img\\/hdpi\\/4225dbf6ef5062469dc8978f63522acc.jpeg\"},{\"id\":\"28\",\"id_category\":\"3\",\"name\":\"Кофе в чалдах Ямайка Блю Маунтин XQ, темная обжарка                \",\"price\":\"1770\",\"img\":\"http:\\/\\/jbmrus.16mb.com\\/res\\/img\\/hdpi\\/a4140ab37538b82f2d296876c0b904dd.jpeg\"},{\"id\":\"29\",\"id_category\":\"3\",\"name\":\"Кофе в чалдах Jamaica Blue Mountain VSXQ, средняя обжарка                \",\"price\":\"1770\",\"img\":\"http:\\/\\/jbmrus.16mb.com\\/res\\/img\\/hdpi\\/6e2db44ccab7040a82e0f3abf79c2989.jpeg\"},{\"id\":\"30\",\"id_category\":\"8\",\"name\":\"Office Blend смесь отборных сортов Арабики                \",\"price\":\"1450\",\"img\":\"http:\\/\\/jbmrus.16mb.com\\/res\\/img\\/hdpi\\/510ec797a18ae68f25716f0b2f2dacf8.jpeg\"},{\"id\":\"31\",\"id_category\":\"8\",\"name\":\"Espresso Blend 500 г. (Арабика+Робуста)                \",\"price\":\"600\",\"img\":\"http:\\/\\/jbmrus.16mb.com\\/res\\/img\\/hdpi\\/c0571d670a45db1f5a5a8ef8a748b995.jpeg\"},{\"id\":\"32\",\"id_category\":\"8\",\"name\":\"Espresso Blend 1 кг. (Арабика + Робуста)                \",\"price\":\"1150\",\"img\":\"http:\\/\\/jbmrus.16mb.com\\/res\\/img\\/hdpi\\/7abe45ab7ddec7d75b8371f3f8d260e6.jpeg\"}]]";
            forecastJsonStr = forecastJsonStr.toString();
            getCatalogDataFromJson(forecastJsonStr);

        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
            /*
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
        */

        }
        return null;
    }
}