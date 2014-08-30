package com.kdoherty.set.activities.practice;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.kdoherty.set.Constants;
import com.kdoherty.set.R;
import com.kdoherty.set.activities.AbstractSetActivity;
import com.kdoherty.set.adapters.ImageAdapter;
import com.kdoherty.set.model.Card;
import com.kdoherty.set.model.Game;
import com.kdoherty.set.model.Set;
import com.kdoherty.set.model.SetSolver;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class Practice extends AbstractSetActivity {

    /** UI Reference to the timer */
    protected TextView mTimerView;
    /** The time in milliseconds of how long the user has to find sets */
    protected long mTime;
    /** Keeps track of the number of Sets the user has found */
    protected TextView score;

    protected CountDownTimer mTimer;

    private long millisRemaining;

    boolean solverPressed = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState, R.layout.activity_practice, R.color.WHITE);
        System.out.println("On create called");
        mTime = getIntent().getExtras().getLong(Constants.Keys.TIME);
        initTimer(mTime);
        initGameView(new Game());
        initHighScore();
    }

    @Override
    protected void finishGame() {
        Intent gameOver = new Intent(getApplicationContext(),
                PracticeOver.class);
        gameOver.putExtra(Constants.Keys.USER_SCORE, mSetCount.get());
        gameOver.putExtra(Constants.Keys.USER_WRONG, mBadSetCount);
        gameOver.putExtra(Constants.Keys.TIME, mTime);
        startActivity(gameOver);
    }

    @Override
    protected void initGameView(Game game) {
        super.initGameView(game);
        score = (TextView) findViewById(R.id.score);
        updateScore();
    }

    private void initHighScore() {
        TextView highScoreView = (TextView) findViewById(R.id.highScore);
        SharedPreferences prefs = getSharedPreferences(Constants.Keys.SPF_HIGHSCORE, Context.MODE_PRIVATE);
        int highScore = prefs.getInt(Constants.Keys.PRACTICE_HIGH_SCORE + String.valueOf(mTime), 0);
        highScoreView.setText("High Score: " + highScore);
    }

    void updateScore() {
        score.setText("Score: " + mSetCount);
    }

    @Override
    protected boolean posSetFound(Set set) {
        boolean setFound = super.posSetFound(set);
        if (setFound) {
            if (solverPressed) {
                mSetCount.decrementAndGet();
                solverPressed = false;
            } else {
                updateScore();
            }
        }
        return setFound;
    }

    private void initTimer(long time) {
        final SimpleDateFormat timeFormat = new SimpleDateFormat("m:ss",
                Locale.getDefault());
        String startTimeStr = timeFormat.format(time);

        mTimerView = (TextView) findViewById(R.id.timerView);
        mTimerView.setText(startTimeStr);

        mTimer = new CountDownTimer(time, 1000) {

            @Override
            public void onTick(long millisUntilFinished) {
                millisRemaining = millisUntilFinished;
                mTimerView.setText(timeFormat.format(millisUntilFinished));
                if (millisUntilFinished <= 6000) {
                    mTimerView.setTextColor(getResources().getColor(
                            R.color.red_cherry));
                }
            }

            @Override
            public void onFinish() {
                finishGame();
            }
        };
        mTimer.start();
    }

    /**
     * Responds to a click on the solverButtonView. It finds a set as per mGame
     * rules if there is one and highlights the cards in the set. If there is
     * not one on the table, a Toast is shown saying there are no sets
     *
     * @param view Reference to the Button view
     */
    public void onSolverButtonClick(View view) {
        Set set = SetSolver.findSet(mGame.getActiveCards());
        if (set == null) {
            Toast.makeText(this, "There are no sets!",
                    Toast.LENGTH_SHORT).show();
        } else {
            mPosSet.clear();
            unhighlightAll();
            for (Card card : set) {
                mHighlight.add(mAdapter.getCardView(mGridView, ImageAdapter
                        .getCardImages().get(card)));
            }
            highlightAll(getResources().getColor(R.color.red_cherry));
        }

        solverPressed = true;

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.timed_practice, menu);
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
    public void onPause() {
        super.onPause();
        System.out.println("On Pause called");
    }

    @Override
    public void onStop() {
        super.onStop();
        mTimer.cancel();
        SharedPreferences prefs = getSharedPreferences(Constants.Keys.SPF_GAME_STATE, Context.MODE_PRIVATE);
        prefs.edit().putLong(Constants.Keys.TIME, millisRemaining).commit();
    }

    @Override
    public void onRestart() {
        super.onRestart();
        SharedPreferences prefs = getSharedPreferences(Constants.Keys.SPF_GAME_STATE, Context.MODE_PRIVATE);
        long timeRemaining = prefs.getLong(Constants.Keys.TIME, 0l);
        initTimer(timeRemaining);
    }
}
