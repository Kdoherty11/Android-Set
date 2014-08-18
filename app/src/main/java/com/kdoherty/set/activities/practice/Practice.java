package com.kdoherty.set.activities.practice;


import android.content.Intent;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState, R.layout.activity_practice, R.color.WHITE);
        mTime = getIntent().getExtras().getLong(Constants.Keys.TIME);
        initTimer();
        initGameView(new Game());
    }

    @Override
    protected void finishGame() {
        Intent gameOver = new Intent(getApplicationContext(),
                PracticeOver.class);
        gameOver.putExtra(Constants.Keys.USER_SCORE, mSetCount);
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

    void updateScore() {
        score.setText("Score: " + mSetCount);
    }

    @Override
    protected boolean posSetFound(Set set) {
        boolean setFound = super.posSetFound(set);
        if (setFound) {
            updateScore();
        }
        return setFound;
    }

    private void initTimer() {
        final SimpleDateFormat timeFormat = new SimpleDateFormat("m:ss",
                Locale.getDefault());
        String startTimeStr = timeFormat.format(mTime);

        mTimerView = (TextView) findViewById(R.id.timerView);
        mTimerView.setText(startTimeStr);

        mTimer = new CountDownTimer(mTime, 1000) {

            @Override
            public void onTick(long millisUntilFinished) {
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
    public void onStop() {
        super.onStop();
        mTimer.cancel();
    }
}
