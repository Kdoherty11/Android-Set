package com.kdoherty.set.activities.race;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.kdoherty.set.R;
import com.kdoherty.set.activities.HomeScreen;

public class RaceOver extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_race_over);
        Bundle extras = getIntent().getExtras();
        String time = extras.getString(Race.TIME_KEY);
        TextView timeView = (TextView) findViewById(R.id.time);
        timeView.setText("Time: " + time);

        int targetNum = extras.getInt(RaceSetUp.TARGET_KEY);
        TextView target = (TextView) findViewById(R.id.target);
        target.setText("Target: " + targetNum);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.game_over_race, menu);
        return true;
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

    public void restart(View v) {
        Intent times = new Intent(getApplicationContext(), RaceSetUp.class);
        startActivity(times);
    }

    public void home(View v) {
        Intent home = new Intent(getApplicationContext(), HomeScreen.class);
        startActivity(home);
    }
}
