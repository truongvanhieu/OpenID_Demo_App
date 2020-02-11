package com.map4d.openid_demo_app;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.facebook.login.Login;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class MainActivity extends AppCompatActivity {
    SharedPreferences sharedpreferences;
    String AccessToken_Key = "AccessToken";
    private String personName, personGivenName, personFamilyName, personEmail = "Email", personId;
    TextView tvAccessToken, tvName, tvEmail;
    ImageView imgAvatar;
    private static final int REQUEST_LAYOUT = 101;
    private GoogleApiClient oogleApiClient;

    GoogleSignInClient googleSignInClient;
    private static final int RC_MAIN = 1;

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
            //signOutAccountGoogle(gg);
            getProfileGGAccount();
        }else {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        }

        //getdata();

    }
    private void getdata(){
        Bundle bundle = getIntent().getExtras();
        if (bundle!=null) {
            String first_name = bundle.getString("first_name");
            String last_name = bundle.getString("last_name");
            String email = bundle.getString("email");
            String id = bundle.getString("id");
            String image_url = bundle.getString("image_url");

            tvName.setText(first_name+" "+last_name);
            tvEmail.setText(email);

            RequestOptions requestOptions = new RequestOptions();
            requestOptions.dontAnimate();
            Glide.with(MainActivity.this).load(image_url).into(imgAvatar);

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == RC_MAIN) {
            if (resultCode == RESULT_OK) {

                // TODO: Implement successful signup logic here
                // By default we just finish the Activity and log them in automatically
                this.finish();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
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
            //saveAccount(personEmail);

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
                overridePendingTransition(R.anim.push_bottom_in, R.anim.push_top_out);
                tvAccessToken.setText("");
            }else {
                signOutAccountGoogle(acct);
                tvName.setText("");
                tvEmail.setText("");
                imgAvatar.setImageBitmap(null);


            }

        }

        return super.onOptionsItemSelected(item);
    }

    private void signOutAccountGoogle(GoogleSignInAccount account) {
        if (account!=null) {
            if (googleSignInClient!=null) {
                googleSignInClient.signOut()
                        .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                                startActivityForResult(intent, RC_MAIN);
                                finish();
                                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);

                                //overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                            }
                        });
            }else {
                Log.e("googleSignInClient","Null");
            }
        }else{
            Log.e("Account","Null");
        }
//        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
//                new ResultCallback<Status>() {
//                    @Override
//                    public void onResult(@NonNull Status status) {
//                        // Hide the sign out buttons, show the sign in button.
//                        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
//                        startActivityForResult(intent, RC_MAIN);
//                        overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
//                    }
//                });

    }

    private void DeleteAccessToken(String accessToken){
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.clear();
        editor.apply();
    }
}
