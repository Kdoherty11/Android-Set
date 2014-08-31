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
    private TextView mTimerTv;
    /** Time left in this game in milliseconds */
    private long mMillisRemaining;
    /** Reference to the countdown timer */
    private CountDownTimer mTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState, R.layout.activity_practice, R.color.WHITE);
        long time = getIntent().getExtras().getLong(Constants.Keys.TIME);
        initTimer(time);
        initCpuView();
        updateScore();
    }

    private void initTimer(long time) {
            final SimpleDateFormat timeFormat = new SimpleDateFormat("m:ss",
                    Locale.getDefault());
            String startTimeStr = timeFormat.format(time);

            mTimerTv = (TextView) findViewById(R.id.timerView);
            mTimerTv.setText(startTimeStr);

            mTimer = new CountDownTimer(time, 1000) {

                @Override
                public void onTick(long millisUntilFinished) {
                    mMillisRemaining = millisUntilFinished;
                    mTimerTv.setText(timeFormat.format(millisUntilFinished));
                    if (millisUntilFinished <= 6000) {
                        mTimerTv.setTextColor(getResources().getColor(
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

        mScoreTv = (TextView) findViewById(R.id.score);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(mScoreTv.getLayoutParams());
        params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        params.addRule(RelativeLayout.LEFT_OF, R.id.centerPoint);
        mScoreTv.setLayoutParams(params);
        mScoreTv.setPadding(25, 15, 0, 15);

        RelativeLayout rLayout = (RelativeLayout) findViewById(R.id.timed_practice_layout);

        RelativeLayout.LayoutParams cpuParams = new RelativeLayout.LayoutParams(mScoreTv.getLayoutParams());
        cpuParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        cpuParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        params.addRule(RelativeLayout.RIGHT_OF, R.id.centerPoint);
        mCpuScoreTv = new TextView(this);
        mCpuScoreTv.setLayoutParams(cpuParams);
        mCpuScoreTv.setText(getString(R.string.cpu_practice_computer_score) + mCpuScore);
        mCpuScoreTv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);
        mCpuScoreTv.setPadding(0, 15, 25, 15);
        rLayout.addView(mCpuScoreTv);
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
        prefs.edit().putLong(Constants.Keys.TIME, mMillisRemaining).commit();
    }

    @Override
    public void onRestart() {
        super.onRestart();
        SharedPreferences prefs = getSharedPreferences(Constants.Keys.SPF_GAME_STATE, Context.MODE_PRIVATE);
        long timeRemaining = prefs.getLong(Constants.Keys.TIME, 0l);
        initTimer(timeRemaining);
    }
}
