package com.kdoherty.set.activities;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.kdoherty.set.Constants;
import com.kdoherty.set.R;
import com.kdoherty.set.services.SetApi;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class SignUp extends Activity {

    /** The new account user name */
    private String mUserName;
    /** The new account password */
    private String mPassword;

    /** UI Reference to the user name */
    private EditText mUserNameEt;
    /** UI Reference to the password */
    private EditText mPasswordEt;
    /** UI Reference to confirm password */
    private EditText mConfirmPasswordEt;

    /**
     * Initializes the UI references and opens the database
     */
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mUserNameEt = (EditText) findViewById(R.id.etUserName);
        mUserNameEt.setText(mUserName);

        mPasswordEt = (EditText) findViewById(R.id.etPass);
        mPasswordEt
                .setOnEditorActionListener(new TextView.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView textView, int id,
                                                  KeyEvent keyEvent) {
                        if (id == R.id.etUserName || id == EditorInfo.IME_NULL) {
                            return true;
                        }
                        return false;
                    }
                });
        mConfirmPasswordEt = (EditText) findViewById(R.id.etConfirmPass);

        // Show the Up button in the action bar.
        setupActionBar();
    }

    /**
     * Set up the {@link android.app.ActionBar}, if the API is available.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void setupActionBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            getActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.sign_up, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // This ID represents the Home or Up button. In the case of this
                // activity, the Up button is shown. Use NavUtils to allow users
                // to navigate up one level in the application structure. For
                // more details, see the Navigation pattern on Android Design:
                //
                // http://developer.android.com/design/patterns/navigation.html#up-vs-back
                //
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Called when create account is clicked
     * Attempts to create an account
     * @param view
     */
    public void onCreateAccountClick(View view) {
        attemptSignUp();
    }

    /**
     * Attempts to create an account.
     * If the credentials are valid and the user name
     * is not taken, the credentials are added to the database.
     */
    public void attemptSignUp() {
        ActivityUtils.checkOnline(this);
        // Reset errors.
        mUserNameEt.setError(null);
        mPasswordEt.setError(null);
        mConfirmPasswordEt.setError(null);

        // Store values at the time of the login attempt.
        mUserName = mUserNameEt.getText().toString();
        mPassword = mPasswordEt.getText().toString();
        /* The new account password confirmed */
        String mConfirmPassword = mConfirmPasswordEt.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password.
        if (TextUtils.isEmpty(mPassword)) {
            mPasswordEt.setError(getString(R.string.error_field_required));
            focusView = mPasswordEt;
            cancel = true;
        } else if (mPassword.length() < 4) {
            mPasswordEt.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordEt;
            cancel = true;
        } else if (!mConfirmPassword.equals(mPassword)) {
            mConfirmPasswordEt.setError(getString(R.string.error_passwords_must_match));
            focusView = mConfirmPasswordEt;
            cancel = true;
        }

        // Check for a valid user name.
        if (TextUtils.isEmpty(mUserName)) {
            mUserNameEt.setError(getString(R.string.error_field_required));
            focusView = mUserNameEt;
            cancel = true;
        } else if (mUserName.length() < 4) {
            mUserNameEt.setError(getString(R.string.error_invalid_email));
            focusView = mUserNameEt;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            SetApi.INSTANCE.addUser(mUserName, mPassword, new Callback<Response>() {
                @Override
                public void success(Response result, Response response) {
                    String res = ActivityUtils.getResponseStr(result);
                    if (res.equalsIgnoreCase("\"Taken\"")) {
                        mUserNameEt.setError(getString(R.string.user_name_exists));
                        mUserNameEt.requestFocus();
                    } else {
                        SharedPreferences loginPreferences = getSharedPreferences(Constants.Keys.SPF_LOGIN, Context.MODE_PRIVATE);
                        loginPreferences.edit().clear().commit();
                        SharedPreferences usernamePrefs = getSharedPreferences(Constants.Keys.SPF_USERNAME, Context.MODE_PRIVATE);
                        usernamePrefs.edit().putString(Constants.Keys.USERNAME, mUserName).commit();
                        Intent homeScreen = new Intent(getApplicationContext(), HomeScreen.class);
                        startActivity(homeScreen);
                    }
                }

                @Override
                public void failure(RetrofitError error) {
                    Log.e(Constants.TAG, "Failure adding user " + error.getMessage());
                }
            });
        }
    }
}

