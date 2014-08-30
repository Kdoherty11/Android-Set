package com.kdoherty.set.activities.practice;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kdoherty.set.Constants;
import com.kdoherty.set.R;
import com.kdoherty.set.activities.AbstractCpuActivity;
import com.kdoherty.set.activities.CpuOver;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class CpuPractice extends AbstractCpuActivity {

    /** UI Reference to the timer */
    private TextView mTimerView;
    /** The time in milliseconds of how long the user has to find sets */
    private long mTime;
    /** Keeps track of the number of Sets the user has found */
    private TextView score;

    private long millisRemaining;

    private CountDownTimer mTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState, R.layout.activity_practice, R.color.WHITE);
        mTime = getIntent().getExtras().getLong(Constants.Keys.TIME);
        initTimer(mTime);
        initCpuView();
        updateScore();
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

    private void initCpuView() {
        View solverButton = findViewById(R.id.solver_button);
        ((RelativeLayout)solverButton.getParent()).removeView(solverButton);

        score = (TextView) findViewById(R.id.score);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(score.getLayoutParams());
        params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        params.addRule(RelativeLayout.LEFT_OF, R.id.centerPoint);
        score.setLayoutParams(params);
        score.setPadding(25, 15, 0, 15);

        RelativeLayout rLayout = (RelativeLayout) findViewById(R.id.timed_practice_layout);

        RelativeLayout.LayoutParams cpuParams = new RelativeLayout.LayoutParams(score.getLayoutParams());
        cpuParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        cpuParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        params.addRule(RelativeLayout.RIGHT_OF, R.id.centerPoint);
        mCpuScoreView = new TextView(this);
        mCpuScoreView.setLayoutParams(cpuParams);
        mCpuScoreView.setText("Computer: " + mCpuScore);
        mCpuScoreView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);
        mCpuScoreView.setPadding(0, 15, 25, 15);
        rLayout.addView(mCpuScoreView);
    }

    @Override
    protected void finishGame() {
        Intent gameOver = new Intent(getApplicationContext(),
                CpuOver.class);
        gameOver.putExtra(Constants.Keys.USER_SCORE, mSetCount.get());
        gameOver.putExtra(Constants.Keys.USER_WRONG, mBadSetCount);
        gameOver.putExtra(Constants.Keys.CPU_SCORE, mCpuScore);
        gameOver.putExtra(Constants.Keys.GAME_MODE, Constants.Modes.PRACTICE);
        startActivity(gameOver);
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
