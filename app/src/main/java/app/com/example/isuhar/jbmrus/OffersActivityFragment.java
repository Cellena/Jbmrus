package app.com.example.isuhar.jbmrus;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

import app.com.example.isuhar.jbmrus.data.CatalogContract;

public class OffersActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    final String LOG_TAG = "myLogs";
    private static final int FORECAST_LOADER = 1;
    private ForecastAdapter mForecastAdapter;
    ListView listView;
    Menu menu;
    MenuItem bedMenuItem;
    long id = 1;


    private static final String[] FORECAST_COLUMNS = {
            CatalogContract.OffersEntry.TABLE_NAME + "." + CatalogContract.OffersEntry._ID,
            CatalogContract.OffersEntry.COLUMN_OFFER_NAME,
            CatalogContract.OffersEntry.COLUMN_OFFER_PRICE,
            CatalogContract.OffersEntry.COLUMN_OFFER_IMG
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

        id = getActivity().getIntent().getLongExtra("id", id);
        Bundle arguments = getArguments();
        if (arguments != null) {
            id = arguments.getLong("id");
        }
        getLoaderManager().initLoader(FORECAST_LOADER, null, this);

        mForecastAdapter = new ForecastAdapter(getActivity(), null, 0);

        View rootView = inflater.inflate(R.layout.fragment_offers, container, false);

        listView = (ListView) rootView.findViewById(R.id.listView_offers);
        listView.setAdapter(mForecastAdapter);

        listView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                ContentValues OrderValues = new ContentValues();

                OrderValues.put(CatalogContract.OrderEntry.COLUMN_ORDER_OFFER_ID, id);

                Uri inserted = getActivity().getContentResolver().insert(CatalogContract.OrderEntry.CONTENT_URI, OrderValues);
                if (inserted!=null) {
                    Toast.makeText(getActivity(), "Added!",
                        Toast.LENGTH_LONG).show();
                }
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

    private void updateOffers() {
        FetchCatalogTask catalogTask = new FetchCatalogTask(getActivity(), false, String.valueOf(id));
        catalogTask.execute();//передаем параметры запроса
    }

    public void onStart() {
        super.onStart();
        id = getActivity().getIntent().getLongExtra("id", id);
        if (getActivity().getContentResolver().query(
                CatalogContract.OffersEntry.CONTENT_URI,
                FORECAST_COLUMNS,
                CatalogContract.OffersEntry.TABLE_NAME + '.' + CatalogContract.OffersEntry.COLUMN_CAT_KEY + " = ?",
                new String[]{String.valueOf(id)},
                null
        ).getCount()==0) updateOffers();

    }

    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {

        String sortOrder = CatalogContract.OffersEntry._ID + " ASC";
        id = getActivity().getIntent().getLongExtra("id", id);
        Bundle arguments = getArguments();
        if (arguments != null) {
            id = arguments.getLong("id");
        }

        Uri Categories = CatalogContract.OffersEntry.CONTENT_URI;


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
