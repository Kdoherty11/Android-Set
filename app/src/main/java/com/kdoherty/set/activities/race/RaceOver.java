package com.kdoherty.set.activities.race;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class RaceOver extends Activity implements View.OnClickListener {

    private int mTarget;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_race_over);
        Bundle extras = getIntent().getExtras();
        String time = extras.getString(Constants.Keys.TIME);
        TextView timeView = (TextView) findViewById(R.id.time);
        timeView.setText("Time: " + time);

        mTarget = extras.getInt(Constants.Keys.TARGET);
        TextView target = (TextView) findViewById(R.id.target);
        target.setText("Target: " + mTarget);

        SharedPreferences prefs = getSharedPreferences(Constants.Keys.SPF_HIGHSCORE, Context.MODE_PRIVATE);
        long highScore = prefs.getLong(Constants.Keys.RACE_HIGH_SCORE + String.valueOf(mTarget), Long.MAX_VALUE);
        long elapsedTime = extras.getLong(Constants.Keys.ELAPSED_TIME_RACE);
        if (elapsedTime < highScore) {
            SharedPreferences.Editor edit = prefs.edit();
            edit.putLong(Constants.Keys.RACE_HIGH_SCORE + String.valueOf(mTarget), elapsedTime);
            edit.commit();
            ((TextView) findViewById(R.id.gameOver)).setText("HIGH SCORE");
            if (ActivityUtils.checkOnline(this, "Must be online to submit to leaderboard")) {
                SetApi.INSTANCE.insertRaceEntry(mTarget, ActivityUtils.getUsername(this), elapsedTime, new Callback<Response>() {
                    @Override
                    public void success(Response response, Response response2) {
                        Toast.makeText(RaceOver.this, "Submitted score to leaderboard!", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        Log.e(Constants.TAG, "Failure submitting to leaderboard " + error.getMessage());
                    }
                });
            }
        }

        findViewById(R.id.restart).setOnClickListener(this);
        findViewById(R.id.home).setOnClickListener(this);
        findViewById(R.id.leaderboard).setOnClickListener(this);
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

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch(id) {
            case R.id.restart:
                Intent times = new Intent(getApplicationContext(), RaceSetUp.class);
                startActivity(times);
                break;
            case R.id.home:
                Intent home = new Intent(getApplicationContext(), HomeScreen.class);
                startActivity(home);
                break;
            case R.id.leaderboard:
                if (ActivityUtils.checkOnline(this)) {
                    Intent leaderboard = new Intent(getApplicationContext(), Leaderboard.class);
                    leaderboard.putExtra(Constants.Keys.TARGET, mTarget);
                    leaderboard.putExtra(Constants.Keys.GAME_MODE, Constants.Modes.RACE);
                    startActivity(leaderboard);
                }
                break;
            default:
                throw new IllegalStateException("Should not get to default case");
        }
    }
}
