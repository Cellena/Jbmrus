package app.com.example.isuhar.jbmrus;

import android.content.ContentUris;
import android.content.Intent;
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
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

import app.com.example.isuhar.jbmrus.data.CatalogContract;

import static android.widget.Toast.LENGTH_LONG;

public class CartFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    final String LOG_TAG = "myLogs";
    private static final int FORECAST_LOADER = 1;
    private CartAdapter mForecastAdapter;
    ListView listView;
    Button button;
    Menu menu;
    MenuItem bedMenuItem;
    long checkPrice = 0;

    /*
    private static final String[] FORECAST_COLUMNS = {
            CatalogContract.OffersEntry.TABLE_NAME + "." + CatalogContract.OffersEntry._ID,
            CatalogContract.OffersEntry.COLUMN_OFFER_NAME,
            CatalogContract.OffersEntry.COLUMN_OFFER_PRICE
    };
    */
    private static final String[] ORDER_COLUMNS = {
            CatalogContract.OffersEntry.TABLE_NAME + "." + CatalogContract.OffersEntry._ID,
            CatalogContract.OffersEntry.COLUMN_OFFER_NAME,
            CatalogContract.OffersEntry.COLUMN_OFFER_PRICE,
            "countOffers",
            CatalogContract.OffersEntry.COLUMN_OFFER_IMG
    };


    public CartFragment() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Add this line in order for this fragment to handle menu events.
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_cart, menu);

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



        mForecastAdapter = new CartAdapter(getActivity(), null, 0);

        View rootView = inflater.inflate(R.layout.fragment_cart, container, false);

        listView = (ListView) rootView.findViewById(R.id.listView_cart2);
        button = (Button) rootView.findViewById(R.id.button_cart);
        listView.setAdapter(mForecastAdapter);

        listView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Uri my = ContentUris.withAppendedId(CatalogContract.OrderEntry.CONTENT_URI, id);
                int rowDeleted = getActivity().getContentResolver().delete(
                        my,
                        null,
                        new String[]{String.valueOf(id)}
                );

                if (rowDeleted > 0) {
                    Toast.makeText(getActivity(), "Товар удален",
                            LENGTH_LONG).show();
                    mForecastAdapter.getCursor().requery();
                    mForecastAdapter.notifyDataSetChanged();
                }
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

        final Uri CONTENT_URI =
                CatalogContract.BASE_CONTENT_URI.buildUpon().appendPath(CatalogContract.PATH_CART).build();

        return new CursorLoader(
                getActivity(),
                CONTENT_URI,
                ORDER_COLUMNS,
                null,
                null,
                null
        );
    }

    public void onLoadFinished(final Loader<Cursor> cursorLoader, Cursor cursor) {
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (listView.getCount() == 0) {
                    Toast.makeText(getActivity(), "Пожалуйста, выберите товар",
                            LENGTH_LONG).show();
                }
                else {

                    boolean checkLast = false;
                    checkPrice = 0;
                    ArrayList<String> id = new ArrayList<String>();
                    ArrayList<String> value = new ArrayList<String>();

                    mForecastAdapter.getCursor().moveToFirst();
                    while (!checkLast && mForecastAdapter.getCursor().getCount() != 0) {

                        checkPrice += mForecastAdapter.getCursor().getInt(2) * mForecastAdapter.getCursor().getInt(3);

                        id.add(String.valueOf(mForecastAdapter.getCursor().getInt(0)));
                        value.add(String.valueOf(mForecastAdapter.getCursor().getInt(3)));

                        if (mForecastAdapter.getCursor().isLast()) checkLast = true;
                        mForecastAdapter.getCursor().moveToNext();

                    }


                    String[] idArray = new String[id.size()];
                    String[] valueArray = new String[value.size()];
                    id.toArray(idArray);
                    value.toArray(valueArray);

                    Intent intent = new Intent(getActivity(), BuyActivity.class);
                    intent.putExtra("checkPrice", checkPrice);
                    intent.putExtra("id", idArray);
                    intent.putExtra("value", valueArray);
                    startActivity(intent);
                    //checkPrice = 0;
                }
            }


        });

        mForecastAdapter.swapCursor(cursor);
    }

    public void onLoaderReset(Loader<Cursor> cursorLoader) {

        mForecastAdapter.swapCursor(null);
    }



}
