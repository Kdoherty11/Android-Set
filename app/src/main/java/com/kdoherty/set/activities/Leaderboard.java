package com.kdoherty.set.activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.kdoherty.set.Constants;
import com.kdoherty.set.R;
import com.kdoherty.set.adapters.LeaderboardAdapter;
import com.kdoherty.set.model.LeaderboardEntry;
import com.kdoherty.set.services.SetApi;

import java.util.List;
import java.util.concurrent.TimeUnit;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class Leaderboard extends Activity {

    private Callback<List<LeaderboardEntry>> callback;

    private Bundle mBundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);

        mBundle = getIntent().getExtras();
        final String gameMode = mBundle.getString(Constants.Keys.GAME_MODE);

        callback = new Callback<List<LeaderboardEntry>>() {
            @Override
            public void success(List<LeaderboardEntry> entries, Response response) {
                findViewById(R.id.pLeaderboard_progressBar).setVisibility(View.GONE);
                LeaderboardAdapter adapter = new LeaderboardAdapter(Leaderboard.this, 0, entries, gameMode);
                ((ListView) findViewById(R.id.practice_leaderboard)).setAdapter(adapter);
            }

            @Override
            public void failure(RetrofitError error) {
                System.out.println("failure getting leaderboard");
                System.out.println(error.getMessage());
            }
        };

        if (gameMode.equals(Constants.Modes.PRACTICE)) {
            initPractice();
        } else {
            initRace();
        }
    }

    private void initPractice() {
        RestAdapter adapter = new RestAdapter.Builder()
                .setEndpoint(SetApi.ENDPOINT)
                .build();
        SetApi api = adapter.create(SetApi.class);

        long timeMillis = getIntent().getExtras().getLong(Constants.Keys.TIME);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(timeMillis);
        api.getPracticeLeaderboard(minutes, callback);

        findViewById(R.id.pLeaderboard_progressBar).setVisibility(View.VISIBLE);
    }

    private void initRace() {
        RestAdapter adapter = new RestAdapter.Builder()
                .setEndpoint(SetApi.ENDPOINT)
                .build();
        SetApi api = adapter.create(SetApi.class);

        int target = getIntent().getExtras().getInt(Constants.Keys.TARGET);
        api.getRaceLeaderboard(target, callback);

        ((TextView) findViewById(R.id.header).findViewById(R.id.scoreHeader)).setText("Time");
        findViewById(R.id.pLeaderboard_progressBar).setVisibility(View.VISIBLE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.leaderboard, menu);
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
}
