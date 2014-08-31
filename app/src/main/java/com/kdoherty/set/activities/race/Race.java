package com.kdoherty.set.activities.race;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.kdoherty.set.Constants;
import com.kdoherty.set.R;
import com.kdoherty.set.activities.AbstractSetActivity;
import com.kdoherty.set.model.Card;
import com.kdoherty.set.model.Game;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class Race extends AbstractSetActivity {

    private TextView mTimerTv;
    private TextView mScoreTv;
    private long mElapsedTime = 0l;
    private int mTarget;
    private Handler mTimeHandler;
    private final SimpleDateFormat mTimeFormat = new SimpleDateFormat("m:ss",
            Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState, R.layout.activity_race, R.color.WHITE);
        initGameView(new Game());
        mScoreTv = (TextView) findViewById(R.id.score);
        updateScore();
        initTimer();
        mTarget = getIntent().getExtras().getInt(Constants.Keys.TARGET);
        initHighScore();
    }

    @Override
    protected boolean posSetFound(List<Card> cards) {
        boolean setFound = super.posSetFound(cards);
        if (setFound) {
            updateScore();
            if (mSetCount.get() == mTarget) {
                finishGame();
            }
        }
        return setFound;
    }

    private void initTimer() {
        String startTimeStr = mTimeFormat.format(0);

        mTimerTv = (TextView) findViewById(R.id.timerView);
        mTimerTv.setText(startTimeStr);

        mTimeHandler = new Handler();
        Runnable task = new Runnable() {
            @Override
            public void run() {
                mElapsedTime += 100;
                mTimerTv.setText(mTimeFormat.format(mElapsedTime));
                mTimeHandler.postDelayed(this, 100);
            }
        };
        mTimeHandler.removeCallbacks(task);
        mTimeHandler.post(task);
    }

    private void initHighScore() {
        TextView highScoreView = (TextView) findViewById(R.id.highScore);
        SharedPreferences prefs = getSharedPreferences(Constants.Keys.SPF_HIGHSCORE, Context.MODE_PRIVATE);
        long highScore = prefs.getLong(Constants.Keys.RACE_HIGH_SCORE + String.valueOf(mTarget), 0);
        highScoreView.setText("High Score: " + mTimeFormat.format(highScore));
    }

    void updateScore() {
        mScoreTv.setText("Score: " + mSetCount);
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
        gameOver.putExtra(Constants.Keys.TIME, mTimerTv.getText().toString());
        gameOver.putExtra(Constants.Keys.TARGET, mTarget);
        gameOver.putExtra(Constants.Keys.ELAPSED_TIME_RACE, mElapsedTime);
        startActivity(gameOver);
    }

    @Override
    public void onStop() {
        super.onStop();
        mTimeHandler.removeCallbacksAndMessages(null);
        mTimeHandler = null;
        SharedPreferences prefs = getSharedPreferences(Constants.Keys.SPF_GAME_STATE, Context.MODE_PRIVATE);
        prefs.edit().putLong(Constants.Keys.TIME, mElapsedTime).commit();
    }

    @Override
    public void onRestart() {
        super.onRestart();
        SharedPreferences prefs = getSharedPreferences(Constants.Keys.SPF_GAME_STATE, Context.MODE_PRIVATE);
        long time = prefs.getLong(Constants.Keys.TIME, 0l);
        mElapsedTime = time;
        initTimer();
    }
}
