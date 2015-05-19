package app.com.example.isuhar.jbmrus;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Editable;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.BasicHttpContext;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;


public class BuyActivity extends ActionBarActivity {

    String NameStr, PhoneStr, EmailStr, SityStr, StreetStr, PorchStr, AprtStr, HausStr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buy);
        Button btnBuy = (Button)findViewById(R.id.buttonBuy);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_buy, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    public void onMyClickBuy(View v) {
        if (v.getId()==R.id.buttonBuy) {

            EditText Name = (EditText) findViewById(R.id.editText);
            EditText Phone = (EditText) findViewById(R.id.editText2);
            EditText Email = (EditText) findViewById(R.id.editText3);
            EditText Sity = (EditText) findViewById(R.id.editText4);
            EditText Street = (EditText) findViewById(R.id.editText5);
            EditText Haus = (EditText) findViewById(R.id.editText6);
            EditText Porch = (EditText) findViewById(R.id.editText7);
            EditText Aprt = (EditText) findViewById(R.id.editText8);

            NameStr = Name.getText().toString();
            PhoneStr = Phone.getText().toString();
            EmailStr = Email.getText().toString();
            SityStr = Sity.getText().toString();
            StreetStr = Street.getText().toString();
            HausStr = Haus.getText().toString();
            PorchStr = Porch.getText().toString();
            AprtStr = Aprt.getText().toString();

            new MyAsyncTask().execute(NameStr,PhoneStr,EmailStr,SityStr,StreetStr,HausStr,
                    PorchStr,AprtStr);
        }
    }
}
class MyAsyncTask extends AsyncTask<String, Integer, Double> {

    @Override
    protected Double doInBackground(String... params) {
        // TODO Auto-generated method stub
        postData(params);
        return null;
    }

    public boolean postData(String[] params) {

            // Create a new HttpClient and Post Header
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost("jbmrus.16mb.com/api/putOrders.php");

            try {
                // Add your data

                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                nameValuePairs.add(new BasicNameValuePair("name", params[0]));
                nameValuePairs.add(new BasicNameValuePair("ph_number", params[1]));
                nameValuePairs.add(new BasicNameValuePair("email", params[2]));
                nameValuePairs.add(new BasicNameValuePair("sity", params[3]));
                nameValuePairs.add(new BasicNameValuePair("street", params[4]));
                nameValuePairs.add(new BasicNameValuePair("haus", params[5]));
                nameValuePairs.add(new BasicNameValuePair("porch", params[6]));
                nameValuePairs.add(new BasicNameValuePair("aprt", params[7]));
                //nameValuePairs.add(new BasicNameValuePair(id, value));
                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs, "UTF-8"));

                // Execute HTTP Post Request
                HttpResponse response = httpclient.execute(httppost);

            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return true;
        }
}