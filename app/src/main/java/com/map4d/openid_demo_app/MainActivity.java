package com.map4d.openid_demo_app;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    SharedPreferences sharedpreferences;
    String AccessToken_Key = "AccessToken";
    TextView tvAccessToken;
    private static final int REQUEST_LAYOUT = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tvAccessToken = (TextView) findViewById(R.id.tvAccessToken);

        sharedpreferences = getSharedPreferences(AccessToken_Key,
                Context.MODE_PRIVATE);
        if (sharedpreferences.contains(AccessToken_Key)) {
            tvAccessToken.setText(sharedpreferences.getString(AccessToken_Key, ""));
        }else {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_log_out) {
            if (sharedpreferences.contains(AccessToken_Key)) {
                DeleteAccessToken(AccessToken_Key);
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivityForResult(intent, REQUEST_LAYOUT);
                finish();
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                tvAccessToken.setText("");
            }
        }

        return super.onOptionsItemSelected(item);
    }
    private void DeleteAccessToken(String accessToken){
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.clear();
        editor.apply();
    }
}
