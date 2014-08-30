package com.kdoherty.set.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.util.Base64;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.TextView;

import com.google.common.base.Strings;
import com.kdoherty.set.Constants;
import com.kdoherty.set.R;
import com.kdoherty.set.adapters.DbAdapter;

import java.io.UnsupportedEncodingException;

public class Login extends Activity {

    /** DataBase Adapter. This is where user credentials and information is stored */
    private DbAdapter mDb;
    /**
     * The default email to populate the email field with.
     */
    public static final String EXTRA_EMAIL = "com.example.android.authenticatordemo.extra.EMAIL";

    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserLoginTask mAuthTask = null;

    /** Value for user name at the time of the login attempt. */
    private String mUserName;
    /** Value for password at the time of the login attempt. */
    private String mPassword;

    /** UI Reference for user name */
    private EditText mUserNameView;
    /** UI Reference for password */
    private EditText mPasswordView;
    /** UI Reference for the login form */
    private View mLoginFormView;
    /** UI Reference for the login status */
    private View mLoginStatusView;
    /** UI Reference for the login status message */
    private TextView mLoginStatusMessageView;
    private CheckedTextView rememberMe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);

        // Set up the login form.
        mUserName = getIntent().getStringExtra(EXTRA_EMAIL);
        mUserNameView = (EditText) findViewById(R.id.etUserName);
        mUserNameView.setText(mUserName);

        mPasswordView = (EditText) findViewById(R.id.etPass);
        mPasswordView
                .setOnEditorActionListener(new TextView.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView textView, int id,
                                                  KeyEvent keyEvent) {
                        if (id == R.id.btnLogin || id == EditorInfo.IME_NULL) {
                            attemptLogin();
                            return true;
                        }
                        return false;
                    }
                });

        rememberMe = (CheckedTextView) findViewById(R.id.remember_me);
        rememberMe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rememberMe.toggle();
            }
        });

        SharedPreferences loginPreferences = getSharedPreferences(Constants.Keys.SPF_LOGIN,
                Context.MODE_PRIVATE);

        String rememberedUsername = loginPreferences.getString(Constants.Keys.USERNAME, "");
        String rememberedPassword = loginPreferences.getString(Constants.Keys.PASSWORD, "");
        mUserNameView.setText(rememberedUsername);
        mPasswordView.setText(rememberedPassword);
        if (!Strings.isNullOrEmpty(rememberedUsername) && !Strings.isNullOrEmpty(rememberedPassword)) {
            rememberMe.setChecked(true);
        }

        mLoginFormView = findViewById(R.id.login_form);
        mLoginStatusView = findViewById(R.id.login_status);
        mLoginStatusMessageView = (TextView) findViewById(R.id.login_status_message);

        TextView txtSignUp = (TextView) findViewById(R.id.txtSignUp);
        txtSignUp.setMovementMethod(LinkMovementMethod.getInstance());

        mDb = new DbAdapter(this);
        mDb.open();
    }

    /**
     * Called when sign in is clicked
     * Attempts to login
     * @param view
     */
    public void onSignInClick(View view) {
        attemptLogin();
    }

    /**
     * Called when sign up is clicked
     * Brings up the SignUpActivity
     * @param view
     */
    public void onSignUpClick(View view) {
        Intent signUpIntent = new Intent(getApplicationContext(),
                SignUp.class);
        startActivity(signUpIntent);
    }


    /**
     * Closes the DataBase
     */
    protected void onDestroy() {
        super.onDestroy();
        mDb.close();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.login, menu);
        return true;
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    public void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mUserNameView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        mUserName = mUserNameView.getText().toString();
        mPassword = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password.
        if (TextUtils.isEmpty(mPassword)) {
            mPasswordView.setError(getString(R.string.error_field_required));
            focusView = mPasswordView;
            cancel = true;
        } else if (mPassword.length() < 4) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(mUserName)) {
            mUserNameView.setError(getString(R.string.error_field_required));
            focusView = mUserNameView;
            cancel = true;
        } else if (mUserName.length() < 4) {
            mUserNameView.setError(getString(R.string.error_invalid_email));
            focusView = mUserNameView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            mLoginStatusMessageView.setText(R.string.login_progress_signing_in);
            showProgress(true);
            mAuthTask = new UserLoginTask();
            mAuthTask.execute((Void) null);
        }
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
            int shortAnimTime = getResources().getInteger(
                    android.R.integer.config_shortAnimTime);

            mLoginStatusView.setVisibility(View.VISIBLE);
            mLoginStatusView.animate().setDuration(shortAnimTime)
                    .alpha(show ? 1 : 0)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            mLoginStatusView.setVisibility(show ? View.VISIBLE
                                    : View.GONE);
                        }
                    });

            mLoginFormView.setVisibility(View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime)
                    .alpha(show ? 0 : 1)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            mLoginFormView.setVisibility(show ? View.GONE
                                    : View.VISIBLE);
                        }
                    });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mLoginStatusView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    /**
     * Represents an asynchronous login task used to authenticate the user.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        /**
         * Checks if the the entered credentials are valid
         * @param params
         * @return Are the credentials valid?
         */
        protected Boolean doInBackground(Void... params) {
            String password = mDb.getPassword(mUserName);
            if (mDb.getPassword(mUserName).equals("NOT EXIST")) {
                return false;
            }

            byte[] data = Base64.decode(password, Base64.DEFAULT);
            String decoded = "";
            try {
                decoded = new String(data, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                throw new RuntimeException("Problem decoding password");
            }
            //return mPassword.equals(decoded) || mPassword.equals(password);
            return mPassword.equals(decoded);
        }

        /**
         * If the credentials are valid we will bring the user to the homeScreen
         * otherwise we will tell the user that the password was incorrect
         * @param success Are the credentials valid
         */
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            showProgress(false);
            if (success) {
                if (rememberMe.isChecked()) {
                    SharedPreferences loginPreferences = getSharedPreferences(Constants.Keys.SPF_LOGIN, Context.MODE_PRIVATE);
                    loginPreferences.edit().putString(Constants.Keys.USERNAME, mUserName).putString(Constants.Keys.PASSWORD, mPassword).commit();
                } else {
                    SharedPreferences loginPreferences = getSharedPreferences(Constants.Keys.SPF_LOGIN, Context.MODE_PRIVATE);
                    loginPreferences.edit().clear().commit();
                }
                SharedPreferences usernamePrefs = getSharedPreferences(Constants.Keys.SPF_USERNAME, Context.MODE_PRIVATE);
                usernamePrefs.edit().putString(Constants.Keys.USERNAME, mUserName).commit();
                Intent homeScreen = new Intent(getApplicationContext(),
                        HomeScreen.class);
                startActivity(homeScreen);
            } else {
                mPasswordView
                        .setError(getString(R.string.error_incorrect_password));
                mPasswordView.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
