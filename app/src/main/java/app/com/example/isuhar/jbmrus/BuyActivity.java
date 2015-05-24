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
import android.widget.TextView;
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

import app.com.example.isuhar.jbmrus.data.CatalogContract;


public class BuyActivity extends ActionBarActivity {

    private Intent mInfo;
    long checkPriceLong = 0;
    public String[] id;
    public String[] value;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_buy);

        mInfo = getIntent();

        checkPriceLong = mInfo.getLongExtra("checkPrice",checkPriceLong);
        id = mInfo.getStringArrayExtra("id");
        value = mInfo.getStringArrayExtra("value");

        TextView checkPriceText = (TextView) findViewById(R.id.textView10);

        String s = String.valueOf(checkPriceLong);
        checkPriceText.setText(s);
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

            String NameStr, PhoneStr, EmailStr, SityStr, StreetStr, PorchStr, AprtStr, HausStr;

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

            if(NameStr.length()==0 || PhoneStr.length()==0l || EmailStr.length()==0 || SityStr.length()==0
                    || StreetStr.length()==0 || HausStr.length()==0 || PorchStr.length()==0 || AprtStr.length()==0){
                Toast.makeText(this, "Пожалуйста, заполните все поля",
                        Toast.LENGTH_LONG).show();
            }else {
                Toast.makeText(this, "Спасибо за покупку!",
                        Toast.LENGTH_LONG).show();
                new MyAsyncTask(id, value).execute(NameStr, PhoneStr, EmailStr, SityStr, StreetStr, HausStr,
                        PorchStr, AprtStr);

                this.getContentResolver().delete(CatalogContract.OrderEntry.CONTENT_URI, null, null);
            }
        }
    }
}

class MyAsyncTask extends AsyncTask<String, Integer, Double> {

    private String[] id;
    private String[] value;

    public MyAsyncTask (String[] myId, String[] myValue){
        id = myId;
        value = myValue;
    }

    @Override
    protected Double doInBackground(String... params) {
        // TODO Auto-generated method stub


        postData(params);
        return null;
    }

    public boolean postData(String[] params) {

            // Create a new HttpClient and Post Header
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost("http://jbmrus.16mb.com/api/putOrders.php?");
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

                for (int i=0; i<id.length; i++){
                    nameValuePairs.add(new BasicNameValuePair(id[i], value[i]));
                }
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