package app.com.example.isuhar.jbmrus;

import android.content.ContentValues;
import android.app.Activity;
import android.content.Intent;
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


import app.com.example.isuhar.jbmrus.data.CatalogContract;
import app.com.example.isuhar.jbmrus.ForecastAdapter;
import app.com.example.isuhar.jbmrus.data.CatalogProvider;

import static android.provider.Contacts.Settings;

public class ForecastFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    final String LOG_TAG = "myLogs";
    private static final int FORECAST_LOADER = 0;
    private SimpleCursorAdapter mForecastAdapter;
    private SimpleCursorAdapter mForecastOffersAdapter;
    ListView listView;
    private static final String[] ORDER_COLUMNS = {
            CatalogContract.OrderEntry.TABLE_NAME + "." + CatalogContract.OrderEntry._ID
    };
    Menu menu;
    MenuItem bedMenuItem;

    private static final String[] FORECAST_COLUMNS = {
            CatalogContract.CategoriesEntry.TABLE_NAME + "." + CatalogContract.CategoriesEntry._ID,
            CatalogContract.CategoriesEntry.COLUMN_CATEGORY_NAME
    };

    static final int COL_CATEGORY_ID = 0;
    static final int COL_CATEGORY_NAME = 1;

    public ForecastFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Add this line in order for this fragment to handle menu events.
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.forecastfragment, menu);
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

        String[] from = new String[] {CatalogContract.CategoriesEntry.COLUMN_CATEGORY_NAME};
        int[] to = new int[] { R.id.list_item_textview_category };

        mForecastAdapter = new SimpleCursorAdapter(getActivity(),R.layout.list_item_forecast,null,from,to,0);

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        listView = (ListView) rootView.findViewById(R.id.listview_forecast);
        listView.setAdapter(mForecastAdapter);
        listView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {//нажатие на элемент списка
                Intent intent = new Intent(getActivity(), OffersActivity.class);
                intent.putExtra("id", id);
                startActivity(intent);
            }
        });
        return rootView;
    }

    public void onActivityCreated(Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);
    }

    private void updateCategories() {
        int count = getActivity().getContentResolver().delete(CatalogContract.CategoriesEntry.CONTENT_URI, null, null);
        count += getActivity().getContentResolver().delete(CatalogContract.OffersEntry.CONTENT_URI, null, null);
        FetchCatalogTask catalogTask = new FetchCatalogTask(getActivity());
        catalogTask.execute();//передаем параметры запроса
    }

    public void onStart() {
        super.onStart();
        if (getActivity().getContentResolver().query(
                CatalogContract.CategoriesEntry.CONTENT_URI,
                FORECAST_COLUMNS, null, null, null, null).getCount() == 0) updateCategories();
    }

    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {

        // Sort order:  Ascending, by name.
        String sortOrder = CatalogContract.CategoriesEntry._ID + " ASC";

        Uri Categories = CatalogContract.CategoriesEntry.CONTENT_URI;
        /*
        Loader asd = new CursorLoader(getActivity(),Categories,null,null,null,sortOrder);
        long _id = asd.getId();

        Uri CatalogUri = CatalogContract.CategoriesEntry.buildCategoriesUri(_id);
        */

        return new CursorLoader(
                getActivity(),
                Categories,
                FORECAST_COLUMNS,
                null,
                null,
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
