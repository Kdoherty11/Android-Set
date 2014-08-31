package com.kdoherty.set.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.common.base.Strings;
import com.kdoherty.set.Constants;
import com.kdoherty.set.R;
import com.kdoherty.set.services.SetApi;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class Login extends Activity {

    /** Value for user name at the time of the login attempt. */
    private String mUserName;
    /** Value for password at the time of the login attempt. */
    private String mPassword;

    /** UI Reference for user name */
    private EditText mUserNameEt;
    /** UI Reference for password */
    private EditText mPasswordEt;
    /** UI Reference for the login form */
    private View mLoginFormView;
    /** UI Reference for the login status */
    private View mLoginStatusView;
    /** UI Reference for the login status message */
    private TextView mLoginStatusTv;
    private CheckedTextView mRememberMeCtv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);

        mUserNameEt = (EditText) findViewById(R.id.etUserName);
        mUserNameEt.setText(mUserName);

        mPasswordEt = (EditText) findViewById(R.id.etPass);
        mPasswordEt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
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

        mRememberMeCtv = (CheckedTextView) findViewById(R.id.remember_me);
        mRememberMeCtv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mRememberMeCtv.toggle();
            }
        });

        SharedPreferences loginPreferences = getSharedPreferences(Constants.Keys.SPF_LOGIN,
                Context.MODE_PRIVATE);

        String rememberedUsername = loginPreferences.getString(Constants.Keys.USERNAME, "");
        String rememberedPassword = loginPreferences.getString(Constants.Keys.PASSWORD, "");
        mUserNameEt.setText(rememberedUsername);
        mPasswordEt.setText(rememberedPassword);
        if (!Strings.isNullOrEmpty(rememberedUsername) && !Strings.isNullOrEmpty(rememberedPassword)) {
            mRememberMeCtv.setChecked(true);
        }

        mLoginFormView = findViewById(R.id.login_form);
        mLoginStatusView = findViewById(R.id.login_status);
        mLoginStatusTv = (TextView) findViewById(R.id.login_status_message);

        TextView txtSignUp = (TextView) findViewById(R.id.txtSignUp);
        txtSignUp.setMovementMethod(LinkMovementMethod.getInstance());
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
        if (!ActivityUtils.checkOnline(this)) {
            return;
        }
        // Reset errors.
        mUserNameEt.setError(null);
        mPasswordEt.setError(null);

        // Store values at the time of the login attempt.
        mUserName = mUserNameEt.getText().toString();
        mPassword = mPasswordEt.getText().toString();

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
        }

        // Check for a valid email address.
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
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            mLoginStatusTv.setText(R.string.login_progress_signing_in);
            showProgress(true);
            SetApi.INSTANCE.authUser(mUserName, mPassword, new Callback<Boolean>() {
                @Override
                public void success(Boolean result, Response response) {
                    showProgress(false);
                    if (result) {
                        if (mRememberMeCtv.isChecked()) {
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
                        mPasswordEt.setError(getString(R.string.error_incorrect_password));
                        mPasswordEt.requestFocus();
                    }
                }

                @Override
                public void failure(RetrofitError error) {
                    showProgress(false);
                    Toast.makeText(Login.this, "Please try again later", Toast.LENGTH_LONG).show();
                }
            });
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
