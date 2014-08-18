package com.kdoherty.set.activities.practice;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.kdoherty.set.Constants;
import com.kdoherty.set.R;
import com.kdoherty.set.activities.HomeScreen;

public class PracticeOver extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_practice_over);
        init();
    }

    /**
     * Initializes all the text views and sets their text
     */
    private void init() {
        TextView numSetsView = (TextView) findViewById(R.id.setCount);
        TextView numBadSetsView = (TextView) findViewById(R.id.badSetCount);
        TextView setsPerMinView = (TextView) findViewById(R.id.setsPerMin);
        TextView scoreView = (TextView) findViewById(R.id.score);
        TextView accuracyView = (TextView) findViewById(R.id.accuracy);

        Bundle bundle = getIntent().getExtras();
        int numSets = bundle.getInt(Constants.Keys.USER_SCORE);
        int numMisses = bundle.getInt(Constants.Keys.USER_WRONG);
        long time = bundle.getLong(Constants.Keys.TIME);

        double setsPerMin = numSets * (60000 / time);
        double score = generateScore(numSets, numMisses, time);
        double accuracy = numSets / (numSets + numMisses);

        numSetsView.setText("Sets " + (int) numSets);
        numBadSetsView.setText("Misses " + (int) numMisses);
        setsPerMinView.setText("Sets Per Minute " + setsPerMin);
        scoreView.setText("Score " + Math.round(score));
        accuracyView.setText("Accuracy " + Math.round(accuracy * 100) + "%");
    }

    protected double generateScore(int numSets, int numMisses, long time) {
        double setsPerMin = numSets * (60000 / time);
        return (numSets - (numMisses / 2)) * setsPerMin;
    }

    public void restart(View v) {
        Intent times = new Intent(getApplicationContext(), PracticeSetUp.class);
        startActivity(times);
    }

    public void home(View v) {
        Intent home = new Intent(getApplicationContext(), HomeScreen.class);
        startActivity(home);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.game_over, menu);
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
}
