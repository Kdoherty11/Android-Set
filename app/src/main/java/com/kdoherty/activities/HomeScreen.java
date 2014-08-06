package com.kdoherty.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.kdoherty.set.R;

public class HomeScreen extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home_screen, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        return id == R.id.action_settings || super.onOptionsItemSelected(item);
    }

    /**
     * Called when the practice button is clicked
     * Creates and starts a new TimeScrollerActivity
     * @param view The Button displaying Timed Practice
     */
    public void onTimedPracticeClick(View view) {
        Intent timeScroller = new Intent(getApplicationContext(),
                TimeScroller.class);
        startActivity(timeScroller);
    }

    public void onOnlineClick(View view) {
        Intent login = new Intent(getApplicationContext(),
                Login.class);
        startActivity(login);
    }
}
