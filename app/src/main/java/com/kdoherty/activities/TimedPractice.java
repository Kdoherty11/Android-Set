package com.kdoherty.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.kdoherty.adapters.ImageAdapter;
import com.kdoherty.model.Card;
import com.kdoherty.model.Game;
import com.kdoherty.model.Set;
import com.kdoherty.model.SetSolver;
import com.kdoherty.set.R;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class TimedPractice extends AbstractSetActivity {

    /** UI Reference to the timer */
    private TextView mTimerView;
    /** The time in milliseconds of how long the user has to find sets */
    private long mTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState, R.layout.activity_timed_practice, R.color.WHITE);
        initTimer();
        initGridView(new Game());
    }

    private void initTimer() {
        final SimpleDateFormat timeFormat = new SimpleDateFormat("m:ss",
                Locale.getDefault());
        mTime = getIntent().getExtras().getLong("time");
        String startTimeStr = timeFormat.format(mTime);

        mTimerView = (TextView) findViewById(R.id.timerView);
        mTimerView.setText(startTimeStr);

        new CountDownTimer(mTime, 1000) {

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
        }.start();
    }

    void finishGame() {
        Intent gameOver = new Intent(getApplicationContext(),
                GameOver.class);
        gameOver.putExtra("setCount", mSetCount);
        gameOver.putExtra("badSetCount", mBadSetCount);
        gameOver.putExtra("time", mTime);
        startActivity(gameOver);
    }

    /**
     * Responds to a click on the solverButtonView. It finds a set as per game
     * rules if there is one and highlights the cards in the set. If there is
     * not one on the table, a Toast is shown saying there are no sets
     *
     * @param view Reference to the Solver Button view
     */
    public void onSolverButtonClick(View view) {
        Set set = SetSolver.findSet(game.getActiveCards());
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

    /**
     * Responds to a click on the deal button. It deals a Set of cards from the
     * Deck and displays them
     *
     * @param view Reference to the Deal Button view
     */
    public void onDealButtonClick(View view) {
        if (game.getActiveCards().size() <= Game.NUM_START_CARDS) {
            game.deal(Set.SIZE);
        }
        mAdapter.update();
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
}
