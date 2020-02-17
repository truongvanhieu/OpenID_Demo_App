package com.map4d.openid_demo_app;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import android.os.CountDownTimer;
import android.provider.Settings;
import android.renderscript.Sampler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;
import com.map4d.awesome_library.MyView;
import com.map4d.awesome_library.SumNumber;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import vn.map4d.map4dsdk.maps.Map4D;
import vn.map4d.map4dsdk.maps.OnMapReadyCallback;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback, LocationListener {

    private AppBarConfiguration mAppBarConfiguration;
    SharedPreferences sharedpreferences;
    String AccessToken_Key = "AccessToken";
    private String personName, personGivenName, personFamilyName, personEmail = "Email", personId;
    TextView tvAccessToken, tvName, tvEmail, tvNav_Username, tvNav_Email;
    ImageView imgAvatar, image_Avatar;
    private static final int REQUEST_LAYOUT = 101;
    private GoogleApiClient oogleApiClient;

    GoogleSignInClient googleSignInClient;
    private static final int RC_MAIN = 1;
    private Map4D map4D;
    LocationListener listener;
    LocationManager locationManager;
    Context context;
    private GoogleApiClient mGoogleApiClient;
    SumNumber sumNumber;
    private EditText num1, num2;
    private TextView sum;
    private Button check, login_logout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        initLayout();

        sharedpreferences = getSharedPreferences(AccessToken_Key, Context.MODE_PRIVATE);
        GoogleSignInAccount gg = GoogleSignIn.getLastSignedInAccount(this);

        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        boolean isLoggedIn = accessToken != null && !accessToken.isExpired();

        if (sharedpreferences.contains(AccessToken_Key)) {
            tvAccessToken.setText(sharedpreferences.getString(AccessToken_Key, ""));
            login_logout.setVisibility(View.VISIBLE);
        }else if (gg!=null){
            //signOutAccountGoogle(gg);
            getProfileGGAccount();
            login_logout.setVisibility(View.VISIBLE);
        }else if(isLoggedIn){
            getFaceBookProfile();
            login_logout.setVisibility(View.VISIBLE);

        }else{
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
            login_logout.setVisibility(View.GONE);
        }

        //toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //Navigation
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(HomeActivity.this);

    }

    private void initLayout(){
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        tvNav_Username = (TextView)headerView.findViewById(R.id.tvNav_Username);
        tvNav_Email = (TextView) headerView.findViewById(R.id.tvNav_Email);
        image_Avatar = (ImageView) headerView.findViewById(R.id.image_Avatar);
        num1 = (EditText) findViewById(R.id.num1);
        num2 = (EditText) findViewById(R.id.num2);
        sum = (TextView) findViewById(R.id.tvsum);
        check = (Button) findViewById(R.id.check);
        check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                testSum();
            }
        });
        login_logout = (Button) headerView.findViewById(R.id.btnLogin_logout);
        login_logout.setVisibility(View.GONE);
        login_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkAccountStatus();
            }


        });

    }
    private void checkAccountStatus() {
        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(this);
        if (sharedpreferences.contains(AccessToken_Key)) {
            DeleteAccessToken(AccessToken_Key);
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivityForResult(intent, REQUEST_LAYOUT);
            finish();
            overridePendingTransition(R.anim.push_bottom_in, R.anim.push_top_out);
        }else if(AccessToken.getCurrentAccessToken() != null)
        {
            LoginManager.getInstance().logOut();
            Intent intent = new Intent(this, LoginActivity.class);
            startActivityForResult(intent, REQUEST_LAYOUT);
            finish();
            overridePendingTransition(R.anim.push_bottom_in, R.anim.push_top_out);
        }else{
            signOutAccountGoogle(acct);
        }
    }
    private void testSum(){
        String n1 = num1.getText().toString();
        String n2 = num2.getText().toString();
        if (!n1.isEmpty() && !n2.isEmpty()){
            double kq = SumNumber.getInstanceSum(Double.valueOf(n1),Double.valueOf(n2));
            sum.setText("Kết quả là = "+kq);
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
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
            }else if(AccessToken.getCurrentAccessToken() != null)
            {
                LoginManager.getInstance().logOut();
                Intent intent = new Intent(this, LoginActivity.class);
                startActivityForResult(intent, REQUEST_LAYOUT);
                finish();
                overridePendingTransition(R.anim.push_bottom_in, R.anim.push_top_out);
            }else{
                signOutAccountGoogle(acct);
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        mGoogleApiClient.connect();
        super.onStart();
    }

    //get facebook profile
    private void getFaceBookProfile(){
        GraphRequest request = GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
            @Override
            public void onCompleted(JSONObject object, GraphResponse response) {

                if (response.getError() == null) {
                    Log.e("ERR", "no error");
                    try {
                        String id = object.getString("id");
                        String email = object.getString("email");
                        String first_name = object.getString("first_name");
                        String last_name = object.getString("last_name");
                        String image_url = "https://graph.facebook.com/"+id+"/picture?type=normal";

                        tvNav_Email.setText(email);
                        tvNav_Username.setText(first_name+" "+last_name);
                        Glide.with(HomeActivity.this).load(image_url).into(image_Avatar);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                } else {
                    Log.e("ERR", "error");
                }
            }
        });

        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,first_name,last_name,email");
        request.setParameters(parameters);
        request.executeAsync();

    }

    //gte google profile
    private void getProfileGGAccount(){
        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(this);
        if (acct != null) {
            personName = acct.getDisplayName();
            personGivenName = acct.getGivenName();
            personFamilyName = acct.getFamilyName();
            personEmail = acct.getEmail();
            personId = acct.getId();
            Uri personPhoto = acct.getPhotoUrl();

            tvNav_Username.setText(personName);
            tvNav_Email.setText(personEmail);
            Glide.with(this).load(String.valueOf(personPhoto)).into(image_Avatar);
            //saveAccount(personEmail);

        }
    }

    private boolean isSignedIn() {
        return GoogleSignIn.getLastSignedInAccount(HomeActivity.this) != null;
    }

    private void signOutAccountGoogle(GoogleSignInAccount account) {
//        if (account!=null) {
//            if (isSignedIn()) {
//                HomeActivity.this.googleSignInClient.signOut()
//                        .addOnCompleteListener(this, new OnCompleteListener<Void>() {
//                            @Override
//                            public void onComplete(@NonNull Task<Void> task) {
//                                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
//                                startActivityForResult(intent, RC_MAIN);
//                                finish();
//                                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
//
//                                //overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
//                            }
//                        });
//            }else {
//                Log.e("googleSignInClient","Null");
//            }
//        }else{
//            Log.e("Account","Null");
//        }
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        // ...
                        Toast.makeText(getApplicationContext(),"Logged Out",Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                        startActivityForResult(intent, RC_MAIN);
                        finish();
                        overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                    }
                });

    }

    private void DeleteAccessToken(String accessToken){
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.clear();
        editor.apply();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_home) {

        } else if (id == R.id.nav_show_Map) {
//            Intent viewMap3D =new Intent(OnMapActivity.this, Mode3dActivity.class);
//            startActivity(viewMap3D);
        } else if (id == R.id.nav_account) {

        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    @Override
    public void onMapReady(final Map4D map4D) {
        this.map4D = map4D;

        configure_button();
        //auto load my location
        map4D.setOnMyLocationClickListener(new Map4D.OnMyLocationClickListener() {
            @Override
            public void onMyLocationClick(final Location location) {
                Toast.makeText(getApplicationContext(), location.getLatitude()+"_"+location.getLongitude(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    void configure_button() {
        // first check for permissions
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.INTERNET},10);
            }
            return;
        }
        map4D.setMyLocationEnabled(true);
//        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
//        listener = new LocationListener() {
//            @Override
//            public void onLocationChanged(final Location location) {
////                latitude = location.getLatitude();
////                longitude = location.getLongitude();
////                tvstreet.setText("Tên đường: "+streetName);
////                tvaddress.setText("Địa chỉ: "+addressName);
////                CountDownTimer countDownTimer = new CountDownTimer(86400000,5000) {
////                    @Override
////                    public void onTick(long l) {
////                        if (latitude!=null&&longitude!=null){
////                            Log.d("track...:","lat: "+latitude+", lon: "+longitude);
////                        }
////                    }
////                    @Override
////                    public void onFinish() {
////                    }
////                };
////                countDownTimer.start();
//            }
//            @Override
//            public void onStatusChanged(String s, int i, Bundle bundle) {
//
//            }
//
//            @Override
//            public void onProviderEnabled(String s) {
//
//            }
//
//            @Override
//            public void onProviderDisabled(String s) {
//                Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
//                startActivity(i);
//            }
//        };
//
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            // TODO: Consider calling
//            //    ActivityCompat#requestPermissions
//            // here to request the missing permissions, and then overriding
//            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//            //                                          int[] grantResults)
//            // to handle the case where the user grants the permission. See the documentation
//            // for ActivityCompat#requestPermissions for more details.
//            return;
//        }
//        locationManager.requestLocationUpdates("gps", 100, 0, listener);

    }
}
