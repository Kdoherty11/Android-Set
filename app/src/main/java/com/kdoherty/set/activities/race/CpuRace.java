package com.kdoherty.set.activities.race;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kdoherty.set.Constants;
import com.kdoherty.set.R;
import com.kdoherty.set.activities.AbstractCpuActivity;
import com.kdoherty.set.activities.CpuOver;
import com.kdoherty.set.model.Card;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class CpuRace extends AbstractCpuActivity {

    private TextView mTimerTv;

    private int mTarget;

    private Handler mTimeHandler;
    private long mElapsedTime = 0l;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState, R.layout.activity_race, R.color.WHITE);
        initTimer();
        initCpuView();
        updateScore();
        mTarget = getIntent().getExtras().getInt(Constants.Keys.TARGET);
    }

    private void initTimer() {
        final SimpleDateFormat timeFormat = new SimpleDateFormat("m:ss",
                Locale.getDefault());
        String startTimeStr = timeFormat.format(0);

        mTimerTv = (TextView) findViewById(R.id.timerView);
        mTimerTv.setText(startTimeStr);

        mTimeHandler = new Handler();
        Runnable task = new Runnable() {
            @Override
            public void run() {
                mElapsedTime += 100;
                mTimerTv.setText(timeFormat.format(mElapsedTime));
                mTimeHandler.postDelayed(this, 100);
            }
        };
        mTimeHandler.removeCallbacks(task);
        mTimeHandler.post(task);
    }

    private void initCpuView() {
        mScoreTv = (TextView) findViewById(R.id.score);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(mScoreTv.getLayoutParams());
        params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        params.addRule(RelativeLayout.LEFT_OF, R.id.centerPoint);
        mScoreTv.setLayoutParams(params);
        mScoreTv.setPadding(25, 15, 0, 15);

        RelativeLayout rLayout = (RelativeLayout) findViewById(R.id.race_layout);

        RelativeLayout.LayoutParams cpuParams = new RelativeLayout.LayoutParams(mScoreTv.getLayoutParams());
        cpuParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        cpuParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        params.addRule(RelativeLayout.RIGHT_OF, R.id.centerPoint);
        mCpuScoreTv = new TextView(this);
        mCpuScoreTv.setLayoutParams(cpuParams);
        mCpuScoreTv.setText("Computer: " + mCpuScore);
        mCpuScoreTv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);
        mCpuScoreTv.setPadding(0, 15, 25, 15);
        rLayout.addView(mCpuScoreTv);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.cpu_race, menu);
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

    @Override
    protected void onSetReceive(Context context, Intent intent) {
        super.onSetReceive(context, intent);
        if (mCpuScore == mTarget) {
            finishGame();
        }
    }

    @Override
    protected void finishGame() {
        Intent gameOver = new Intent(getApplicationContext(),
                CpuOver.class);
        gameOver.putExtra(Constants.Keys.USER_SCORE, mSetCount.get());
        gameOver.putExtra(Constants.Keys.USER_WRONG, mBadSetCount);
        gameOver.putExtra(Constants.Keys.CPU_SCORE, mCpuScore);
        gameOver.putExtra(Constants.Keys.GAME_MODE, Constants.Modes.RACE);
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
