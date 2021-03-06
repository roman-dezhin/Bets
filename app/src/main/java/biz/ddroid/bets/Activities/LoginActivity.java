package biz.ddroid.bets.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.PersistentCookieStore;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.regex.Pattern;

import biz.ddroid.bets.BetApplication;
import biz.ddroid.bets.R;
import biz.ddroid.bets.rest.ServicesClient;
import biz.ddroid.bets.rest.SystemServices;
import biz.ddroid.bets.rest.UserServices;
import biz.ddroid.bets.utils.NetworkUtils;
import biz.ddroid.bets.utils.SharedPrefs;
import cz.msebera.android.httpclient.Header;

public class LoginActivity extends AppCompatActivity {
    private ServicesClient servicesClient;
    private UserServices userServices;
    // UI references.
    private EditText mUsernameView;
    private EditText mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;

    private String TAG = "LoginActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        servicesClient = BetApplication.getServicesClient();
        servicesClient.setCookieStore(new PersistentCookieStore(getApplicationContext()));
        userServices = new UserServices(servicesClient);

        checkIfUserCredentialsIsExists();

        setContentView(R.layout.activity_login);

        // Set up the login form.
        mUsernameView = (EditText) findViewById(R.id.username);
        mEmailView = (EditText) findViewById(R.id.email);

        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
    }

    private void checkIfUserCredentialsIsExists() {
        SharedPreferences settings = getSharedPreferences(SharedPrefs.PREFS_NAME, 0);
        final String userId = settings.getString(SharedPrefs.UID, "");
        final String username = settings.getString(SharedPrefs.USERNAME, "");
        final String password = settings.getString(SharedPrefs.PASSWORD, "");
        final String token = settings.getString(SharedPrefs.TOKEN, "");
        final String email = settings.getString(SharedPrefs.EMAIL, "");
        if (!userId.isEmpty() && !username.isEmpty() && !password.isEmpty() && !token.isEmpty() && !email.isEmpty()) {
            if (!NetworkUtils.isNetworkConnected(this)) {
                Toast.makeText(this, R.string.no_internet_connections, Toast.LENGTH_SHORT).show();
                return;
            }
            servicesClient.setToken(token);
            SystemServices systemServices = new SystemServices(servicesClient);
            systemServices.connect(new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject responseBody) {
                    try {
                        if (responseBody.getJSONObject("user").get("uid").equals(userId)) {
                            Intent intent = new Intent(getApplicationContext(), BetsActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            login(username, password);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String responseBody, Throwable error) {
                }
            });
        }



    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        // Reset errors.
        mUsernameView.setError(null);
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String username = mUsernameView.getText().toString();
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(username)) {
            mUsernameView.setError(getString(R.string.error_field_required));
            focusView = mUsernameView;
            cancel = true;
        } else if (!isUsernameValid(username)) {
            mUsernameView.setError(getString(R.string.error_invalid_username));
            focusView = mUsernameView;
            cancel = true;
        }

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            isUserExist(username, email, password);
        }
    }

    private void isUserExist(final String username, final String email, final String password) {
        if (!NetworkUtils.isNetworkConnected(this)) {
            Toast.makeText(this, R.string.no_internet_connections, Toast.LENGTH_SHORT).show();
            return;
        }
        showProgress(true);

        userServices.isUserExists(username, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                Log.v(TAG, response.toString());
                if (response.length() > 0) {
                    login(username, password);
                } else {
                    register(username, email, password);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String response, Throwable error) {
                Log.v(TAG, error.getMessage());
                Log.v(TAG, response);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable error, JSONArray response) {
                Log.v(TAG, error.getMessage());
                Log.v(TAG, response.toString());
            }

            @Override
            public void onFinish() {
                showProgress(false);
            }
        });
    }

    private void register(final String username, final String email, final String password) {
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            mEmailView.requestFocus();
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            mEmailView.requestFocus();
        } else {
            if (!NetworkUtils.isNetworkConnected(this)) {
                Toast.makeText(this, R.string.no_internet_connections, Toast.LENGTH_SHORT).show();
                return;
            }
            showProgress(true);
            userServices.register(username, password, email, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    Log.v(TAG, response.toString());
                    login(username, password);
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable error, JSONObject response) {
                    Log.v(TAG, error.getMessage());
                    Log.v(TAG, response.toString());
                    String str = "";
                    try {
                        str = new String(error.getMessage().getBytes("ISO-8859-1"), "UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    mEmailView.setError(str);
                    mEmailView.requestFocus();
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable error, JSONArray response) {
                    Log.v(TAG, error.getMessage());
                    Log.v(TAG, response.toString());
                }

                @Override
                public void onFinish() {
                    showProgress(false);
                }
            });
        }
    }

    private void login(final String username, final String password) {
        if (!NetworkUtils.isNetworkConnected(this)) {
            Toast.makeText(this, R.string.no_internet_connections, Toast.LENGTH_SHORT).show();
            return;
        }
        showProgress(true);
        userServices.login(username, password, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.v(TAG, response.toString());
                String session_name = "";
                String session_id = "";
                String email = "";
                String token = "";
                String uid = "";
                try {
                    email = response.getJSONObject("user").getString("mail");
                    session_name = response.getString("session_name");
                    session_id = response.getString("sessid");
                    token = response.getString("token");
                    uid = response.getJSONObject("user").getString("uid");
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.d(TAG, "json response: ", e);
                }
                Log.v(TAG, "Session_name: " + session_name);
                Log.v(TAG, "Session_id: " + session_id);
                Log.v(TAG, "Email: " + email);
                Log.v(TAG, "token: " + token);
                Log.v(TAG, "uid: " + uid);


                if (!token.isEmpty()) {
                    SharedPreferences settings = getSharedPreferences(SharedPrefs.PREFS_NAME, 0);
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putString(SharedPrefs.USERNAME, username);
                    editor.putString(SharedPrefs.EMAIL, email);
                    editor.putString(SharedPrefs.PASSWORD, password);
                    editor.putString(SharedPrefs.TOKEN, token);
                    editor.putString(SharedPrefs.UID, uid);
                    editor.commit();

                    Intent intent = new Intent(getApplicationContext(), BetsActivity.class);
                    startActivity(intent);
                    finish();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable error, JSONObject response) {
                Log.v(TAG, error.getMessage());
                Log.v(TAG, response.toString());
                Log.v(TAG, "login: " + username + " pas: " + password);
                mPasswordView.setError(response.toString());
                mPasswordView.requestFocus();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable error, JSONArray response) {
                Log.v(TAG, error.getMessage());
                Log.v(TAG, response.toString());
                Log.v(TAG, "login: " + username + " pas: " + password);
                mPasswordView.setError(getString(R.string.error_incorrect_password));
                mPasswordView.requestFocus();
            }

            @Override
            public void onFinish() {
                showProgress(false);
            }
        });
    }

    private boolean isUsernameValid(String username) {
        return Pattern.matches("[\\w]+", username);
    }

    private boolean isEmailValid(String email) {
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        return password.length() > 4;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

}

