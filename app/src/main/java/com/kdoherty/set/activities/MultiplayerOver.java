package com.kdoherty.set.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kdoherty.set.Constants;
import com.kdoherty.set.R;
import com.kdoherty.set.model.Player;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MultiplayerOver extends Activity {

    List<Player> players;
    Map<String, String> results = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multiplayer_over);
        players = getIntent().getExtras().getParcelableArrayList(Constants.Keys.PLAYERS);
        initResults();
        String resultTxt = results.get(ActivityUtils.getUsername(this));
        ((TextView) findViewById(R.id.result)).setText(resultTxt);
        initScoreboard();
    }

    private void initResults() {
        Collections.sort(players);
        String [] places = {"1st", "2nd", "3rd", "4th"};
        int placesIndex = 0;
        int offset = 0;
        int playersSize = players.size();
        for (int i = 0; i < playersSize - offset; i++) {
            Player player = players.get(i);
            int score = player.getSetCount();
            String name = player.getName();
            results.put(name, places[placesIndex]);
            while (i + 1 < players.size() && players.get(i + 1).getSetCount() == score) {
                results.put(players.get(++i).getName(), places[placesIndex]);
                offset++;
            }
            placesIndex++;
        }
    }

    private void initScoreboard() {
        LinearLayout scoreboard = (LinearLayout) findViewById(R.id.scoreboard);
        for (Player player : players) {
            TextView playerView = new TextView(this);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            playerView.setLayoutParams(params);
            String name = player.getName();
            playerView.setText(results.get(name).charAt(0) + ". " + name + " " +
                    player.getSetCount());
            playerView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
            scoreboard.addView(playerView);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.multiplayer_over, menu);
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
        Intent home = new Intent(getApplicationContext(), Multiplayer.class);
        startActivity(home);
    }

    public void home(View v) {
        Intent home = new Intent(getApplicationContext(), HomeScreen.class);
        startActivity(home);
    }
}
