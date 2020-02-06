package com.map4d.openid_demo_app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class MainActivity extends AppCompatActivity {
    SharedPreferences sharedpreferences;
    String AccessToken_Key = "AccessToken";
    private String personName, personGivenName, personFamilyName, personEmail = "Email", personId;
    TextView tvAccessToken, tvName, tvEmail;
    ImageView imgAvatar;
    private static final int REQUEST_LAYOUT = 101;
    GoogleSignInClient googleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tvAccessToken = (TextView) findViewById(R.id.tvAccessToken);
        tvName = (TextView) findViewById(R.id.tvName);
        tvEmail = (TextView) findViewById(R.id.tvEmail);
        imgAvatar = (ImageView) findViewById(R.id.imgAvatar);

        sharedpreferences = getSharedPreferences(AccessToken_Key, Context.MODE_PRIVATE);
        GoogleSignInAccount gg = GoogleSignIn.getLastSignedInAccount(this);
        if (sharedpreferences.contains(AccessToken_Key)) {
            tvAccessToken.setText(sharedpreferences.getString(AccessToken_Key, ""));
        }else if (gg!=null){
            tvEmail.setText(gg.getEmail());
            tvName.setText(gg.getDisplayName());
        }else {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }
        getProfileGGAccount();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    private void getProfileGGAccount(){
        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(this);
        if (acct != null) {
            personName = acct.getDisplayName();
            personGivenName = acct.getGivenName();
            personFamilyName = acct.getFamilyName();
            personEmail = acct.getEmail();
            personId = acct.getId();
            Uri personPhoto = acct.getPhotoUrl();

            tvName.setText(personName);
            tvEmail.setText(personEmail);
            Glide.with(this).load(String.valueOf(personPhoto)).into(imgAvatar);
            saveAccount(personEmail);

        }
    }
    private void saveAccount(String email){
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString("Email", email);
        editor.apply();
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(this);
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_log_out) {
            if (sharedpreferences.contains(AccessToken_Key)) {
                DeleteAccessToken(AccessToken_Key);
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivityForResult(intent, REQUEST_LAYOUT);
                finish();
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                tvAccessToken.setText("");
            }else if (acct!=null){
                signOut();

            }

        }

        return super.onOptionsItemSelected(item);
    }

    private void signOut() {
        googleSignInClient.signOut()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                        startActivity(intent);

                        //overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                    }
                });
    }

    private void DeleteAccessToken(String accessToken){
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.clear();
        editor.apply();
    }
}
