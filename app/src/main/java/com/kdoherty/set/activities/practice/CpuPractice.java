package com.kdoherty.set.activities.practice;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kdoherty.set.Constants;
import com.kdoherty.set.R;
import com.kdoherty.set.adapters.ImageAdapter;
import com.kdoherty.set.model.Card;
import com.kdoherty.set.model.Set;
import com.kdoherty.set.services.CpuPlayerService;

import java.util.ArrayList;

public class CpuPractice extends Practice {

    private TextView mCpuScoreView;
    private int mCpuScore = 0;
    private int mCpuDifficulty;
    private BroadcastReceiver mReceiver;
    private boolean mCanAddToSet = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCpuDifficulty = getIntent().getExtras().getInt(Constants.Keys.CPU_DIFFICULTY);
        initCpuView();
        initCpuPlayer();
    }

    private void initCpuView() {
        View solverButton = findViewById(R.id.solver_button);
        ((RelativeLayout)solverButton.getParent()).removeView(solverButton);

        TextView score = (TextView) findViewById(R.id.score);
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

    private void initCpuPlayer() {
        mReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(final Context context, Intent intent) {
                final Set set = intent.getParcelableExtra(Constants.Keys.SET);
                if (set != null && mGame.containsSet(set)) {
                    mPosSet.clear();
                    unhighlightAll();
                    for (Card card : set) {
                        try {
                            mHighlight.add(mAdapter.getCardView(mGridView, ImageAdapter
                                    .getCardImages().get(card)));
                        } catch (RuntimeException e) {
                            throw new RuntimeException("Could not get id of " + card + " from " + mGame.getActiveCards());
                        }
                    }
                    highlightAll(getResources().getColor(R.color.brown));
                    mCpuScore++;
                    mCanAddToSet = false;
                    final Handler handler = new Handler(context.getMainLooper());
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (posSetFound(set)) {
                                mSetCount.decrementAndGet();
                                updateScore();
                            }
                            mCanAddToSet = true;
                        }
                    }, 2000);

                }
            }
        };

        IntentFilter filter = new IntentFilter();
        filter.addAction(Constants.Actions.BROADCAST);
        filter.addCategory(Intent.CATEGORY_DEFAULT);
        registerReceiver(mReceiver, filter);

        startCpuSetSearch();
    }

    private void startCpuSetSearch() {
        Intent cpuService = new Intent(this, CpuPlayerService.class);
        cpuService.setAction(Constants.Actions.CPU_PLAYER);
        cpuService.putExtra(Constants.Keys.CPU_DIFFICULTY, mCpuDifficulty);
        ArrayList<Card> cards = (ArrayList) mGame.getActiveCards();
        cpuService.putParcelableArrayListExtra(Constants.Keys.ACTIVE_CARDS, cards);
        startService(cpuService);
    }

    @Override
    void updateScore() {
        super.updateScore();
        if (mCpuScoreView != null) {
            mCpuScoreView.setText("Computer: " + mCpuScore);
        }
    }

    @Override
    protected void addToPosSet(Card c, View view) {
        if (mCanAddToSet) {
            super.addToPosSet(c, view);
        }
    }

    @Override
    protected boolean posSetFound(Set set) {
        boolean setFound = super.posSetFound(set);
        if (setFound) {
                stopService(new Intent(this, CpuPlayerService.class));
                startCpuSetSearch();
        }
        return setFound;
    }

    @Override
    protected void finishGame() {
        Intent gameOver = new Intent(getApplicationContext(),
                CpuPracticeOver.class);
        gameOver.putExtra(Constants.Keys.USER_SCORE, mSetCount);
        gameOver.putExtra(Constants.Keys.USER_WRONG, mBadSetCount);
        gameOver.putExtra(Constants.Keys.CPU_SCORE, mCpuScore);
        gameOver.putExtra(Constants.Keys.TIME, mTime);
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
    protected void onResume() {
        super.onResume();
        sendBroadcast();
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mReceiver);
    }

    private void sendBroadcast() {
        Intent broadcast = new Intent();
        broadcast.setAction(Constants.Actions.BROADCAST);
        broadcast.addCategory(Intent.CATEGORY_DEFAULT);
        sendBroadcast(broadcast);
    }
}
