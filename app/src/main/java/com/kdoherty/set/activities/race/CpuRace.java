package com.kdoherty.set.activities.race;

import android.content.Context;
import android.content.Intent;
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
import com.kdoherty.set.activities.practice.CpuPracticeOver;
import com.kdoherty.set.model.Card;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class CpuRace extends AbstractCpuActivity {

    private TextView mTimerView;

    private int target;

    final SimpleDateFormat timeFormat = new SimpleDateFormat("m:ss",
            Locale.getDefault());
    String startTimeStr = timeFormat.format(0);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState, R.layout.activity_race, R.color.WHITE);
        initTimer();
        initCpuView();
        updateScore();
        score = (TextView) findViewById(R.id.score);
        target = getIntent().getExtras().getInt(Constants.Keys.TARGET);
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

    private void initCpuView() {
        score = (TextView) findViewById(R.id.score);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(score.getLayoutParams());
        params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        params.addRule(RelativeLayout.LEFT_OF, R.id.centerPoint);
        score.setLayoutParams(params);
        score.setPadding(25, 15, 0, 15);

        RelativeLayout rLayout = (RelativeLayout) findViewById(R.id.race_layout);

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
            if (mSetCount.get() == target) {
                finishGame();
            }
        }
        return setFound;
    }

    @Override
    protected void onSetReceive(Context context, Intent intent) {
        super.onSetReceive(context, intent);
        if (mCpuScore == target) {
            finishGame();
        }
    }

    @Override
    protected void finishGame() {
        Intent gameOver = new Intent(getApplicationContext(),
                CpuPracticeOver.class);
        gameOver.putExtra(Constants.Keys.USER_SCORE, mSetCount.get());
        gameOver.putExtra(Constants.Keys.USER_WRONG, mBadSetCount);
        gameOver.putExtra(Constants.Keys.CPU_SCORE, mCpuScore);
        startActivity(gameOver);
    }
}
