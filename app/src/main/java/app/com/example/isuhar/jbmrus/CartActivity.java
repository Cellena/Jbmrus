package app.com.example.isuhar.jbmrus;

import android.app.Activity;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.content.Intent;
import android.widget.Button;
import android.widget.EditText;


public class CartActivity extends ActionBarActivity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        Button btnSubmit = (Button)findViewById(R.id.button_cart);
    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_cart, menu);
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


        public void onMyClick(View v) {
            if (v.getId()==R.id.button_cart) {
                Intent intent = new Intent(this, BuyActivity.class);
                startActivity(intent);
            }
            if (v.getId()==R.id.buttonBuy) {
                /*
                EditText Phone = (EditText) findViewById(R.id.editText2);
                EditText Name = (EditText) findViewById(R.id.fam);
                Phone.setText(Name.getText());
                */
            }
        }
}
