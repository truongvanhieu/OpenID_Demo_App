package com.map4d.openid_demo_app;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
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

import android.provider.Settings;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.navigation.ui.AppBarConfiguration;

import com.google.android.material.navigation.NavigationView;
import com.map4d.awesome_library.SumNumber;
import com.map4d.openid_demo_app.API.API_Smartcode;
import com.map4d.openid_demo_app.API.API_Vibus;
import com.map4d.openid_demo_app.API_Interface.API_Smartcode_Interface;
import com.map4d.openid_demo_app.API_Interface.Account_interface;
import com.map4d.openid_demo_app.Model.Model_Smartcode_Data;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import vn.map4d.map.annotations.MFBitmapDescriptorFactory;
import vn.map4d.map.annotations.MFCircle;
import vn.map4d.map.annotations.MFMarker;
import vn.map4d.map.annotations.MFMarkerOptions;
import vn.map4d.map.annotations.MFPolyline;
import vn.map4d.map.core.LatLng;
import vn.map4d.map.core.MFSupportMapFragment;
import vn.map4d.map.core.Map4D;
import vn.map4d.map.core.OnMapReadyCallback;
import vn.map4d.types.MFLocationCoordinate;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback, LocationListener {

    private static final int REQUEST_LOCATION = 200;
    private AppBarConfiguration mAppBarConfiguration;
    SharedPreferences sharedpreferences;
    String AccessToken_Key = "AccessToken", RefreshToken_key = "RefreshToken";
    String Grant_type = "password", Cliect_id = "demo", Client_secret = "66dce544-1619-4fe5-bf59-27a57c399880", refreshToken;
    private String personName, personGivenName, personFamilyName, personEmail = "Email", personId;
    TextView tvName, tvEmail, tvNav_Username, tvNav_Email;
    ImageView imgAvatar, image_Avatar;
    private static final int REQUEST_LAYOUT = 101;
    private GoogleApiClient oogleApiClient;

    GoogleSignInClient googleSignInClient;
    private static final int RC_MAIN = 1;
    private LocationManager locationManager;
    private LocationListener listener;
    Context context;
    private GoogleApiClient mGoogleApiClient;
    SumNumber sumNumber;
    private TextView tvSmartCode, tvCompoundCode, tvLatlng;
    private LinearLayout layoutSmartcode;
    private Button check, login, logout;
    private boolean status;
    private Double latitude, longitude;
    private Location location;
    private String latlng;
    Model_Smartcode_Data model_smartcode_data;
    ImageView img_Menu;
    DrawerLayout drawer;

    private Map4D map4D;
    private MFPolyline polyline;
    private MFCircle circle;
    private MFMarker marker;
    private  boolean defaultInfoWindow = true;
    class CustomInfoWindowAdapter implements Map4D.InfoWindowAdapter {

        // These are both viewgroups containing an ImageView with id "badge" and two TextViews with id
        // "title" and "snippet".
        private final View mWindow;

        CustomInfoWindowAdapter() {
            mWindow = getLayoutInflater().inflate(R.layout.custom_info_window, null);
        }

        @Override
        public View getInfoWindow(MFMarker marker) {
            if (defaultInfoWindow) {
                return null;
            }
            render(marker, mWindow);
            return mWindow;
        }

        @Override
        public View getInfoContents(MFMarker marker) {
            return null;
        }

        private void render(MFMarker marker, View view) {
            String title = marker.getTitle();
            TextView titleUi = ((TextView) view.findViewById(R.id.title));
            if (title != null) {
                // Spannable string allows us to edit the formatting of the text.
                SpannableString titleText = new SpannableString(title);
                titleText.setSpan(new ForegroundColorSpan(Color.RED), 0, titleText.length(), 0);
                titleUi.setText(titleText);
            } else {
                titleUi.setText(title);
            }

            String snippet = marker.getSnippet();
            TextView snippetUi = ((TextView) view.findViewById(R.id.snippet));
            if (snippet != null && snippet.length() > 12) {
                SpannableString snippetText = new SpannableString(snippet);
                snippetText.setSpan(new ForegroundColorSpan(Color.MAGENTA), 0, 10, 0);
                snippetText.setSpan(new ForegroundColorSpan(Color.BLUE), 12, snippet.length(), 0);
                snippetUi.setText(snippetText);
            } else {
                snippetUi.setText(snippet);
            }
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        MFSupportMapFragment mapFragment = (MFSupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map2D);
        mapFragment.getMapAsync(this);

        initLayout();

        sharedpreferences = getSharedPreferences(AccessToken_Key, Context.MODE_PRIVATE);
        GoogleSignInAccount gg = GoogleSignIn.getLastSignedInAccount(this);

        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        boolean isLoggedIn = accessToken != null && !accessToken.isExpired();

        if (sharedpreferences.contains(AccessToken_Key)) {
            Log.d("AccessToken", sharedpreferences.getString(AccessToken_Key, ""));
            logout.setVisibility(View.VISIBLE);
        }else if (gg!=null){
            //signOutAccountGoogle(gg);
            getProfileGGAccount();
            logout.setVisibility(View.VISIBLE);
        }else if(isLoggedIn){
            getFaceBookProfile();
            logout.setVisibility(View.VISIBLE);

        }else{
//            Intent intent = new Intent(this, LoginActivity.class);
//            startActivity(intent);
//            finish();
            logout.setVisibility(View.GONE);
            login.setVisibility(View.VISIBLE);
        }

//        //toolbar
//        Toolbar toolbar = findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
        //Navigation
        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        img_Menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawer.openDrawer(Gravity.LEFT);
            }
        });
        navigationView.setNavigationItemSelectedListener(this);

    }

    private void getDataSmartcode(final Double latitude, final Double longitude) {
        layoutSmartcode.setVisibility(View.GONE);
        latlng = latitude+","+longitude;
        Log.d("Location", latlng);
        API_Smartcode_Interface service = API_Smartcode.getClient().create(API_Smartcode_Interface.class);
        Call<Model_Smartcode_Data> userCall = service.getSmartcodeData(latlng);
        userCall.enqueue(new Callback<Model_Smartcode_Data>() {
            @Override
            public void onResponse(Call<Model_Smartcode_Data> call, Response<Model_Smartcode_Data> response) {
                //onSignupSuccess();
                if (response.isSuccessful()) {
                    if (response.body()!=null){
                        Log.d("DataSmartCode", ""+response.body().getCode());
                        model_smartcode_data = response.body();
                        if (model_smartcode_data!=null) {
                            tvSmartCode.setText(model_smartcode_data.getResults().getSmartCode());
                            tvCompoundCode.setText(model_smartcode_data.getResults().getCompoundCode());
                            tvLatlng.setText(latitude+","+longitude);
                            layoutSmartcode.setVisibility(View.VISIBLE);
                            addMakerToMap(latitude, longitude);
                            //Toast.makeText(getApplicationContext(), model_smartcode_data.getResults().getSmartCode(), Toast.LENGTH_SHORT).show();
                        }

                    }
                }else {
                    Log.d("DataSmartCode", "null");
                }
            }
            @Override
            public void onFailure(Call<Model_Smartcode_Data> call, Throwable t) {
                Log.d("Failed: ", t.toString());
            }
        });

    }

    private void initLayout(){
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        tvNav_Username = (TextView)headerView.findViewById(R.id.tvNav_Username);
        tvNav_Email = (TextView) headerView.findViewById(R.id.tvNav_Email);
        image_Avatar = (ImageView) headerView.findViewById(R.id.image_Avatar);
        tvSmartCode = (TextView) findViewById(R.id.tvSmartCode);
        tvCompoundCode = (TextView) findViewById(R.id.tvCompoundCode);
        tvLatlng = (TextView) findViewById(R.id.tvLatlng);
        layoutSmartcode = (LinearLayout) findViewById(R.id.layoutSmartCode);
        layoutSmartcode.setVisibility(View.GONE);
        img_Menu = (ImageView) findViewById(R.id.btn_menu);

        logout = (Button) headerView.findViewById(R.id.btnLogout);
        logout.setVisibility(View.GONE);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkAccountStatusForLogout();
            }


        });
        login = (Button) headerView.findViewById(R.id.btnLogin);
        login.setVisibility(View.GONE);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivityForResult(intent, RC_MAIN);
                finish();
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }
        });

    }
    private void checkAccountStatusForLogout() {
        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(this);
        if (sharedpreferences.contains(AccessToken_Key)) {
            signOutAccountKeycloak();
        }else if(AccessToken.getCurrentAccessToken() != null)
        {
            signOutAccountFacebook();
        }else{
            signOutAccountGoogle();
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
                signOutAccountKeycloak();
            }else if(AccessToken.getCurrentAccessToken() != null) {
                signOutAccountFacebook();
            }else{
                signOutAccountGoogle();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void signOutAccountKeycloak() {
        refreshToken = sharedpreferences.getString(RefreshToken_key,"");
        Log.e("Refresh_token", refreshToken+"");
        Log.e("Refresh_token", refreshToken+"");
        signOut_KeyCloak(Cliect_id, refreshToken);
        if (!status){
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivityForResult(intent, REQUEST_LAYOUT);
            finish();
            overridePendingTransition(R.anim.push_bottom_in, R.anim.push_top_out);
            DeleteAccessToken();
        }else{
            Log.d("Logout","Failed!!");
        }
    }

    private void signOutAccountFacebook() {
        LoginManager.getInstance().logOut();
        Intent intent = new Intent(this, LoginActivity.class);
        startActivityForResult(intent, REQUEST_LAYOUT);
        finish();
        overridePendingTransition(R.anim.push_bottom_in, R.anim.push_top_out);
        Toast.makeText(getApplicationContext(), "Đăng xuất thành công !", Toast.LENGTH_SHORT).show();
    }

    private void signOutAccountGoogle() {
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        // ...
                        Toast.makeText(getApplicationContext(), "Đăng xuất thành công !", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                        startActivityForResult(intent, RC_MAIN);
                        finish();
                        overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                    }
                });

    }

    private Boolean signOut_KeyCloak(String client_id, String refreshToken){
        status = false;
        Account_interface service = API_Vibus.getClient().create(Account_interface.class);
        Call<ResponseBody> userCall = service.logoutAccount(client_id, refreshToken);
        userCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                //onSignupSuccess();
                if (response.isSuccessful()) {
                    status = true;
                    if (response.body()!=null){
                        Log.d("Data", ""+response.body().toString());

                    }
                    Toast.makeText(getApplicationContext(), "Đăng xuất thành công !", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(getApplicationContext(), "Đăng xuất không thành công !", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d("Failed: ", t.toString());
            }
        });
        return status;
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
                        Log.d("Data_user_FB", object.toString());

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
            String idToken =acct.getIdToken();
            String serverAuthCode = acct.getServerAuthCode();
            Log.d("get_data_from_gg",
                    "DisplayName: "+personName+
                            "GivenName: "+ personGivenName+
                            "FamilyName: "+personFamilyName+
                            "Email: "+personEmail+
                            "Id: "+personId+
                            "PhotoUrl: "+personPhoto.toString()+
                            "IdToken: "+idToken+
                            "ServerAuthCode: "+ serverAuthCode);

            //set layout
            tvNav_Username.setText(personName);
            tvNav_Email.setText(personEmail);
            Glide.with(this).load(String.valueOf(personPhoto)).into(image_Avatar);
            //saveAccount(personEmail);
        }
    }

    private void DeleteAccessToken(){
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.clear();
        editor.apply();
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

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_home) {

        } else if (id == R.id.nav_introduce) {
            Intent intent =new Intent(HomeActivity.this, IntroduceActivity.class);
            startActivityForResult(intent, RC_MAIN);
            finish();
            overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
        } else if (id == R.id.nav_account) {
            if (tvNav_Email!=null){
                Intent intent =new Intent(HomeActivity.this, InfoActivity.class);
                startActivityForResult(intent, RC_MAIN);
                finish();
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }
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
        //map4D.animateCamera(MFCameraUpdateFactory.newLatLngZoom(latLng,16.0f));
        map4D.setOnMapClickListener(new Map4D.OnMapClickListener() {
                @Override
                public void onMapClick(MFLocationCoordinate mfLocationCoordinate) {
                    Double lat = mfLocationCoordinate.getLatitude();
                    Double lng = mfLocationCoordinate.getLongitude();
                    Log.e("latitude:", lat+"");
                    Log.e("longitude:", lng+"");

                    //addMakerToMap(lat, lng);
                    getDataSmartcode(lat, lng);
                }
            });

        //auto load my location
        map4D.setOnMyLocationClickListener(new Map4D.OnMyLocationClickListener() {
            @Override
            public void onMyLocationClick(final Location location) {
                addMakerToMap(location.getLatitude(), location.getLongitude());
                Toast.makeText(getApplicationContext(), location.getLatitude() + "_" + location.getLongitude(), Toast.LENGTH_SHORT).show();
            }
        });
        //addMakerToMap(16.080732, 108.230364);
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
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        listener = new LocationListener() {
            @Override
            public void onLocationChanged(final Location location) {
                latitude = location.getLatitude();
                longitude = location.getLongitude();
//                tvstreet.setText("Tên đường: "+streetName);
//                tvaddress.setText("Địa chỉ: "+addressName);
//                CountDownTimer countDownTimer = new CountDownTimer(86400000,5000) {
//                    @Override
//                    public void onTick(long l) {
//                        if (latitude!=null&&longitude!=null){
//                            Log.d("track...:","lat: "+latitude+", lon: "+longitude);
//                        }
//                    }
//                    @Override
//                    public void onFinish() {
//                    }
//                };
//                countDownTimer.start();
            }
            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {
                Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(i);
            }
        };

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates("gps", 100, 0, listener);

    }

    private void addMakerToMap(Double lat, Double lng){
        View view = createMarkerView();
        if (marker!=null){
            marker.remove();
        }
        if (lat!=null && lng!=null) {
            marker = map4D.addMarker(new MFMarkerOptions()
                    .position(new MFLocationCoordinate(lat, lng))
                    .title("Marker  test")
                    .snippet(lat+", "+lng)
                    .iconView(view)
            );
        }
    }
    View createMarkerView() {
        // Create new LinearLayout
        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT));
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setBackgroundColor(0x0FFFFFF);

//        TextView textView1 = new TextView(this);
//        textView1.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
//                LinearLayout.LayoutParams.WRAP_CONTENT));
//        textView1.setText("TextView");
//        textView1.setBackgroundColor(0xff66ff66); // hex color 0xAARRGGBB
//        textView1.setPadding(20, 0, 20, 20); // in pixels (left, top, right, bottom)
//        linearLayout.addView(textView1);

        ImageView imageView = new ImageView(this);
        imageView.setImageResource(R.drawable.ic_default_marker);
        linearLayout.addView(imageView);
        return linearLayout;
    }
}
