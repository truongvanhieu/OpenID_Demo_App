package com.map4d.openid_demo_app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.AccessTokenManager;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.map4d.openid_demo_app.API.APIClient;
import com.map4d.openid_demo_app.API_Interface.Login_interface;
import com.map4d.openid_demo_app.Model.Model_loginApi;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Arrays;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    private static final int REQUEST_SIGNUP = 0;
    private static final int RC_SIGN_IN = 101;
    private static final int RC_MAIN = 1;
    EditText _userText;
    EditText _passwordText;
    Button _loginButton;
    SignInButton signInGGButton;
    LoginButton signInFBButton;
    TextView _signupLink;
    String Grant_type = "password", Cliect_id = "smartcodes-web", Client_secret = "66dce544-1619-4fe5-bf59-27a57c399880";
    private String Username, Password;
    Model_loginApi model_loginApi;
    SharedPreferences sharedpreferences;
    boolean check = false;

    GoogleSignInClient googleSignInClient;
    CallbackManager callbackManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        sharedpreferences = getSharedPreferences("AccessToken",
                Context.MODE_PRIVATE);
        initLayout();
        //login with SmartCodes using API
        _loginButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Username = _userText.getText().toString();
                Password = _passwordText.getText().toString();
                login(Username,Password);
            }
        });

        _signupLink.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // Start the Signup activity
                Intent intent = new Intent(getApplicationContext(), SignUpActivity.class);
                startActivityForResult(intent, REQUEST_SIGNUP);
                finish();
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }
        });
        //login with google
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, gso);
        signInGGButton.setSize(SignInButton.SIZE_STANDARD);
        signInGGButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }
        });
        //login with facebook account
        callbackManager = CallbackManager.Factory.create();
        signInFBButton.setReadPermissions(Arrays.asList("Email","Public profile"));

        signInFBButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.e("facebooktoken",""+loginResult.getAccessToken());
                AccessToken accessToken = loginResult.getAccessToken();
                loadFaceBookProfile(accessToken);

            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {
                Log.e("facebooktoken", "Failed!");
            }
        });

    }

    //init layout
    private void initLayout(){
        _userText = (EditText) findViewById(R.id.input_username);
        _passwordText = (EditText) findViewById(R.id.input_password);
        _loginButton = (Button) findViewById(R.id.btn_login);
        _signupLink = (TextView) findViewById(R.id.link_signup);
        signInGGButton = findViewById(R.id.btnGoogle);
        signInFBButton = findViewById(R.id.btnFacebook);
    }

    private void signIn(){
//        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(this);
//        if (acct!=null){
//            signOut();
//        }
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
//    private void signOut() {
//        googleSignInClient.signOut()
//                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
//                    @Override
//                    public void onComplete(@NonNull Task<Void> task) {
//                        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
//                        startActivity(intent);
//                        finish();
//                        overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
//                    }
//                });
//    }
    public void login(String username , String password) {
        Log.d(TAG, "Login");
        if (!validate()) {
            onLoginFailed();
            return;
        }

        _loginButton.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Authenticating...");
        progressDialog.show();

        Log.d("text",Username+", "+Password);
        Check_login(username, password, Grant_type, Cliect_id, Client_secret);
        // TODO: Implement your own authentication logic here.
        if (!check){
            new android.os.Handler().postDelayed(
                    new Runnable() {
                        public void run() {
                            // On complete call either onLoginSuccess or onLoginFailed
                            onLoginSuccess();
                            // onLoginFailed();
                            progressDialog.dismiss();
                        }
                    }, 3000);
        }else{
            onLoginFailed();
            return;
        }

    }

    private Boolean Check_login(String username, String password, String grant_type, String client_id, String client_secret){
        check = false;
        Login_interface service = APIClient.getClient().create(Login_interface.class);
        Call<Model_loginApi> userCall = service.loginAccount(username, password, grant_type, client_id, client_secret);
        userCall.enqueue(new Callback<Model_loginApi>() {
            @Override
            public void onResponse(Call<Model_loginApi> call, Response<Model_loginApi> response) {
                //onSignupSuccess();
                if (response.isSuccessful()) {
                    check = true;
                    if (response.body()!=null){
                        Log.d("Data", ""+response.body().getAccess_token());
                        model_loginApi = response.body();
                        Log.d("accessToken", ""+model_loginApi.getAccess_token());
                        saveAccessToken(model_loginApi.getAccess_token());

                    }
                }else {
                    Toast.makeText(getApplicationContext(), "Login failed !", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<Model_loginApi> call, Throwable t) {
                Log.d("Failed: ", t.toString());
            }
        });
        return check;
    }

    private void updateUI(GoogleSignInAccount account) {
        try {

            String strData = "" + account.getDisplayName();
            //Toast.makeText(getApplicationContext(),strData,Toast.LENGTH_LONG).show();
            Log.e("Name",strData);
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
//            startActivityForResult(intent, RC_MAIN);
            finish();
            //overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);

        } catch (Exception ex) {
// lblInfo.setText(ex.getMessage().toString());
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        // [START on_start_sign_in]
        // Check for existing Google Sign In account, if the user is already signed in
        // the GoogleSignInAccount will be non-null.
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        updateUI(account);
        // [END on_start_sign_in]
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_SIGNUP) {
            if (resultCode == RESULT_OK) {

                // TODO: Implement successful signup logic here
                // By default we just finish the Activity and log them in automatically
                this.finish();
            }
        }
        if (requestCode == RC_SIGN_IN){
            Log.e(TAG, "Login with google account!");
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }

    }

//    AccessTokenTracker tokenTracker = new AccessTokenTracker() {
//        @Override
//        protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {
//            if (currentAccessToken==null){
//                Log.e("Facebook Account", "User logged out!");
//            }else{
//                loadFaceBookProfile(currentAccessToken);
//            }
//        }
//    };

    private void loadFaceBookProfile(AccessToken accessToken){
        GraphRequest request = GraphRequest.newMeRequest(accessToken, new GraphRequest.GraphJSONObjectCallback() {
            @Override
            public void onCompleted(JSONObject object, GraphResponse response) {
                try {
                    String first_name = object.getString("first_name");
                    String last_name = object.getString("last_name");
                    String email = object.getString("email");
                    String id = object.getString("id");
                    String image_url = "http://graph.facebook.com/"+id+"/picture?type=normal";

                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("first_name", first_name);
                    bundle.putString("last_name", last_name);
                    bundle.putString("email", email);
                    bundle.putString("id", id);
                    bundle.putString("image_url", image_url);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }catch (JSONException e){
                    e.printStackTrace();
                }
            }
        });

    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);

            // Signed in successfully, show authenticated UI.
            Log.e("request","Đăng nhập thành công!");
            updateUI(account);
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w("Error", "signInResult:failed code=" + e.getStatusCode());
        }
    }

    @Override
    public void onBackPressed() {
        // Disable going back to the MainActivity
        moveTaskToBack(true);
    }

    public void onLoginSuccess() {
        _loginButton.setEnabled(true);
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivityForResult(intent, RC_SIGN_IN);
        finish();
        overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
    }

    public void onLoginFailed() {
        Toast.makeText(getBaseContext(), "Login failed", Toast.LENGTH_LONG).show();

        _loginButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String user = _userText.getText().toString();
        String password = _passwordText.getText().toString();

//        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
//            _emailText.setError("enter a valid email address");
//            valid = false;
//        } else {
//            _emailText.setError(null);
//        }
        if (user.isEmpty() || user.length() < 4  || user.length() > 50) {
            _userText.setError("between 1 and 50 alphanumeric characters!");
            valid = false;
        } else {
            _userText.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 30) {
            _passwordText.setError("between 4 and 30 alphanumeric characters!");
            valid = false;
        } else {
            _passwordText.setError(null);
        }

        return valid;
    }

    private void saveAccessToken(String accessToken){
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString("AccessToken", accessToken);
        editor.apply();
    }

}

