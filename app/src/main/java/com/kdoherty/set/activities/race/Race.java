package com.kdoherty.set.activities.race;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.kdoherty.set.R;
import com.kdoherty.set.activities.AbstractSetActivity;
import com.kdoherty.set.model.Card;
import com.kdoherty.set.model.Game;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class Race extends AbstractSetActivity {

    private TextView mTimerView;
    private TextView score;

    public static final String TIME_KEY = "time";

    private int target;

    private Handler customHandler = new Handler();

    final SimpleDateFormat timeFormat = new SimpleDateFormat("m:ss",
            Locale.getDefault());
    String startTimeStr = timeFormat.format(0);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState, R.layout.activity_race, R.color.WHITE);
        initGameView(new Game());
        initTimer();
        target = getIntent().getExtras().getInt(RaceSetUp.TARGET_KEY);
    }

    @Override
    protected void initGameView(Game game) {
        super.initGameView(game);
        score = (TextView) findViewById(R.id.score);
        updateScore();
    }

    @Override
    protected boolean posSetFound(List<Card> cards) {
        boolean setFound = super.posSetFound(cards);
        if (setFound) {
            updateScore();
            if (mSetCount.get() == target) {
                finishGame();
            }
        }
        return setFound;
    }

    private void initTimer() {

        final SimpleDateFormat timeFormat = new SimpleDateFormat("m:ss",
                Locale.getDefault());
        String startTimeStr = timeFormat.format(0);

        mTimerView = (TextView) findViewById(R.id.timerView);
        mTimerView.setText(startTimeStr);

        final Handler handler = new Handler();
        final long startTimeMillis = System.currentTimeMillis();
        Runnable task = new Runnable() {
            @Override
            public void run() {
                long elapsedTime = System.currentTimeMillis() - startTimeMillis;
                mTimerView.setText(timeFormat.format(elapsedTime));
                handler.postDelayed(this, 1000);
            }
        };
        handler.removeCallbacks(task);
        handler.post(task);
    }

    void updateScore() {
        score.setText("Score: " + mSetCount);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.race, menu);
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
    protected void finishGame() {
        Intent gameOver = new Intent(getApplicationContext(), RaceOver.class);
        gameOver.putExtra(TIME_KEY, mTimerView.getText().toString());
        gameOver.putExtra(RaceSetUp.TARGET_KEY, target);
        startActivity(gameOver);
    }

}
