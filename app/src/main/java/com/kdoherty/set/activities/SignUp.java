package com.kdoherty.set.activities;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.kdoherty.set.R;
import com.kdoherty.set.adapters.DbAdapter;

import java.io.UnsupportedEncodingException;

public class SignUp extends Activity {

    /** DataBase where we will store the new account */
    private DbAdapter mDb;

    /** The new account user name */
    private String mUserName;
    /** The new account password */
    private String mPassword;

    /** UI Reference to the user name */
    private EditText mUserNameView;
    /** UI Reference to the password */
    private EditText mPasswordView;
    /** UI Reference to confirm password */
    private EditText mConfirmPasswordView;

    /**
     * Initializes the UI references and opens the database
     */
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mUserNameView = (EditText) findViewById(R.id.etUserName);
        mUserNameView.setText(mUserName);

        mPasswordView = (EditText) findViewById(R.id.etPass);
        mPasswordView
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
        mConfirmPasswordView = (EditText) findViewById(R.id.etConfirmPass);

        // Show the Up button in the action bar.
        setupActionBar();
        mDb = new DbAdapter(this);
        mDb.open();
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
     * Closes the database
     */
    public void onDestroy() {
        super.onDestroy();
        mDb.close();
    }

    /**
     * Attempts to create an account.
     * If the credentials are valid and the user name
     * is not taken, the credentials are added to the database.
     */
    public void attemptSignUp() {
        // Reset errors.
        mUserNameView.setError(null);
        mPasswordView.setError(null);
        mConfirmPasswordView.setError(null);

        // Store values at the time of the login attempt.
        mUserName = mUserNameView.getText().toString();
        mPassword = mPasswordView.getText().toString();
        /* The new account password confirmed */
        String mConfirmPassword = mConfirmPasswordView.getText().toString();

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
        } else if (!mConfirmPassword.equals(mPassword)) {
            mConfirmPasswordView.setError(getString(R.string.error_passwords_must_match));
            focusView = mConfirmPasswordView;
            cancel = true;
        }

        // Check for a valid user name.
        if (TextUtils.isEmpty(mUserName)) {
            mUserNameView.setError(getString(R.string.error_field_required));
            focusView = mUserNameView;
            cancel = true;
        } else if (mUserName.length() < 4) {
            mUserNameView.setError(getString(R.string.error_invalid_email));
            focusView = mUserNameView;
            cancel = true;
        } else if (!mDb.getPassword(mUserName).equals("NOT EXIST")) {
            mUserNameView.setError(getString(R.string.user_name_exists));
            focusView = mUserNameView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            byte[] data;
            try {
                data = mPassword.getBytes("UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                throw new RuntimeException("Problem encoding password to UTF-8");
            }
            String base64 = Base64.encodeToString(data, Base64.DEFAULT);
            Log.d("kdoherty", "encoding password " + mPassword + " into " + base64 + " and inserting into DB");
            mDb.insertRow(mUserName, base64);
            Login.USERNAME = mUserName;
            Intent homeScreen = new Intent(getApplicationContext(), HomeScreen.class);
            startActivity(homeScreen);
        }
    }
}

