package com.kdoherty.set.activities.practice;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.kdoherty.set.Constants;
import com.kdoherty.set.R;
import com.kdoherty.set.activities.ActivityUtils;
import com.kdoherty.set.activities.HomeScreen;
import com.kdoherty.set.activities.Leaderboard;
import com.kdoherty.set.services.SetApi;

import java.util.concurrent.TimeUnit;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class PracticeOver extends Activity implements View.OnClickListener {

    private long mTime;

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
        TextView gameOverView = (TextView) findViewById(R.id.gameOver);

        Bundle bundle = getIntent().getExtras();
        int numSets = bundle.getInt(Constants.Keys.USER_SCORE);
        int numMisses = bundle.getInt(Constants.Keys.USER_WRONG);
        mTime = bundle.getLong(Constants.Keys.TIME);
        double setsPerMin = numSets * (60000 / mTime);
        double score = numSets;
        double accuracy;
        if (numSets + numMisses == 0) {
            accuracy = 0;
        } else {
            accuracy = numSets / (numSets + numMisses);
        }
        SharedPreferences prefs = getSharedPreferences(Constants.Keys.SPF_HIGHSCORE, Context.MODE_PRIVATE);
        int highScore = prefs.getInt(Constants.Keys.PRACTICE_HIGH_SCORE + String.valueOf(mTime), 0);
        if (numSets > highScore) {
            Editor edit = prefs.edit();
            edit.putInt(Constants.Keys.PRACTICE_HIGH_SCORE + String.valueOf(mTime), numSets);
            edit.commit();
            gameOverView.setText(getString(R.string.practice_over_high_score));
            if (ActivityUtils.checkOnline(this, getString(R.string.practice_over_not_online_leaderboard_submit))) {
                SetApi.INSTANCE.insertPracticeEntry(TimeUnit.MILLISECONDS.toMinutes(mTime), ActivityUtils.getUsername(this), numSets, new Callback<Response>() {
                    @Override
                    public void success(Response response, Response response2) {
                        Toast.makeText(PracticeOver.this, getString(R.string.practice_over_leaderboard_submit_success), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        Log.e(Constants.TAG, "Failure submitting to leaderboard " + error.getMessage());
                    }
                });
            }
        }
        numSetsView.setText(getString(R.string.practice_over_num_sets) + (int) numSets);
        numBadSetsView.setText(getString(R.string.practice_over_num_misses) + (int) numMisses);
        setsPerMinView.setText(getString(R.string.practice_over_sets_per_min) + setsPerMin);
        scoreView.setText(getString(R.string.practice_over_score) + Math.round(score));
        accuracyView.setText(getString(R.string.practice_over_accuracy) + Math.round(accuracy * 100) + "%");
        findViewById(R.id.restart).setOnClickListener(this);
        findViewById(R.id.home).setOnClickListener(this);
        findViewById(R.id.leaderboard).setOnClickListener(this);
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

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch(id) {
            case R.id.restart:
                Intent times = new Intent(getApplicationContext(), PracticeSetUp.class);
                startActivity(times);
                break;
            case R.id.home:
                Intent home = new Intent(getApplicationContext(), HomeScreen.class);
                startActivity(home);
                break;
            case R.id.leaderboard:
                if (ActivityUtils.checkOnline(this)) {
                    Intent leaderboard = new Intent(getApplicationContext(), Leaderboard.class);
                    leaderboard.putExtra(Constants.Keys.TIME, mTime);
                    leaderboard.putExtra(Constants.Keys.GAME_MODE, Constants.Modes.PRACTICE);
                    startActivity(leaderboard);
                }
                break;
            default:
                throw new IllegalStateException("Should not get to default case");
        }
    }
}
