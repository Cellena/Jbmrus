package app.com.example.isuhar.jbmrus;

import android.content.ContentValues;
import android.app.Activity;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Contacts;
import android.support.v4.app.Fragment;
import android.text.format.Time;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.widget.AdapterView.OnItemClickListener;


import java.util.Vector;

import app.com.example.isuhar.jbmrus.data.CatalogContract;
import app.com.example.isuhar.jbmrus.ForecastAdapter;
import app.com.example.isuhar.jbmrus.data.CatalogProvider;

import static android.provider.Contacts.Settings;

public class OffersActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    final String LOG_TAG = "myLogs";
    private static final int FORECAST_LOADER = 1;
    private SimpleCursorAdapter mForecastAdapter;
    ListView listView;
    Menu menu;
    MenuItem bedMenuItem;


    private static final String[] FORECAST_COLUMNS = {
            CatalogContract.OffersEntry.TABLE_NAME + "." + CatalogContract.OffersEntry._ID,
            CatalogContract.OffersEntry.COLUMN_OFFER_NAME,
            CatalogContract.OffersEntry.COLUMN_OFFER_PRICE
    };

    private static final String[] ORDER_COLUMNS = {
            CatalogContract.OrderEntry.TABLE_NAME + "." + CatalogContract.OrderEntry._ID
    };


    public OffersActivityFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Add this line in order for this fragment to handle menu events.
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_offers, menu);
        this.menu = menu;
        int countOrders;
        Cursor orderCursor = getActivity().getContentResolver().query(
                CatalogContract.OrderEntry.CONTENT_URI,
                ORDER_COLUMNS,
                null,
                null,
                null
        );
        orderCursor.moveToFirst();
        countOrders = orderCursor.getCount();
        orderCursor.close();
        bedMenuItem = menu.findItem(R.id.action_count_offers);
        bedMenuItem.setTitle(Integer.toString(countOrders));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        getLoaderManager().initLoader(FORECAST_LOADER, null, this);

        String[] from = new String[] {CatalogContract.OffersEntry.COLUMN_OFFER_NAME,
                CatalogContract.OffersEntry.COLUMN_OFFER_PRICE};
        int[] to = new int[] { R.id.list_item_name_textview, R.id.list_item_price_textview };

        mForecastAdapter = new SimpleCursorAdapter(getActivity(),R.layout.list_offers_forecast,null,from,to,0);

        View rootView = inflater.inflate(R.layout.fragment_offers, container, false);
        final Vector<ContentValues> oVVector = new Vector<ContentValues>(1);

        listView = (ListView) rootView.findViewById(R.id.listView_offers);
        listView.setAdapter(mForecastAdapter);
        listView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {//������� �� ������� ������

                ContentValues OrderValues = new ContentValues();

                OrderValues.put(CatalogContract.OrderEntry.COLUMN_ORDER_OFFER_ID, id);
                OrderValues.put(CatalogContract.OrderEntry.COLUMN_ORDER_OFFER_COUNT, 1);

                getActivity().getContentResolver().insert(CatalogContract.OrderEntry.CONTENT_URI, OrderValues);
                int countOrders;
                Cursor orderCursor = getActivity().getContentResolver().query(
                        CatalogContract.OrderEntry.CONTENT_URI,
                        ORDER_COLUMNS,
                        null,
                        null,
                        null
                );
                orderCursor.moveToFirst();
                countOrders = orderCursor.getCount();
                orderCursor.close();
                bedMenuItem = menu.findItem(R.id.action_count_offers);
                bedMenuItem.setTitle(Integer.toString(countOrders));
            }
        });
        return rootView;
    }

    public void onActivityCreated(Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);
    }

    public void onStart() {
        super.onStart();
    }

    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {

        // Sort order:  Ascending, by name.
        String sortOrder = CatalogContract.OffersEntry._ID + " ASC";

        Uri Categories = CatalogContract.OffersEntry.CONTENT_URI;
        /*
        Loader asd = new CursorLoader(getActivity(),Categories,null,null,null,sortOrder);
        long _id = asd.getId();

        Uri CatalogUri = CatalogContract.CategoriesEntry.buildCategoriesUri(_id);
        */
        long id = 0;
        id = getActivity().getIntent().getLongExtra("id",id);

        String strId = String.valueOf(id);

        return new CursorLoader(
                getActivity(),
                Categories,
                FORECAST_COLUMNS,
                CatalogContract.OffersEntry.TABLE_NAME + '.' + CatalogContract.OffersEntry.COLUMN_CAT_KEY + " = ?",
                new String[]{strId},
                sortOrder
        );
    }

    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        mForecastAdapter.swapCursor(cursor);
    }

    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        mForecastAdapter.swapCursor(null);
    }

}
