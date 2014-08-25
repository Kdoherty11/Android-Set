package com.kdoherty.set.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;

import com.kdoherty.set.Constants;
import com.kdoherty.set.R;
import com.kdoherty.set.adapters.ImageAdapter;
import com.kdoherty.set.model.Card;
import com.kdoherty.set.model.Game;
import com.kdoherty.set.model.Set;
import com.kdoherty.set.services.CpuPlayerService;

import java.util.ArrayList;

/**
 * Created by kdoherty on 8/18/14.
 */
public abstract class AbstractCpuActivity extends AbstractSetActivity {

    protected TextView mCpuScoreView;
    protected int mCpuScore = 0;
    protected int mCpuDifficulty;
    protected BroadcastReceiver mReceiver;
    protected boolean mCanAddToSet = true;
    /** Keeps track of the number of Sets the user has found */
    protected TextView score;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCpuDifficulty = getIntent().getExtras().getInt(Constants.Keys.CPU_DIFFICULTY);
        score = (TextView) findViewById(R.id.score);
        initGameView(new Game());
        initCpuPlayer();
    }

    protected void onCreate(Bundle savedInstanceState, int layoutId, int bgColor) {
        super.onCreate(savedInstanceState, layoutId, bgColor);
        mCpuDifficulty = getIntent().getExtras().getInt(Constants.Keys.CPU_DIFFICULTY);
        score = (TextView) findViewById(R.id.score);
        initGameView(new Game());
        initCpuPlayer();
    }

    protected void initCpuPlayer() {
        mReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(final Context context, Intent intent) {
                onSetReceive(context, intent);
            }
        };

        IntentFilter filter = new IntentFilter();
        filter.addAction(Constants.Actions.BROADCAST);
        filter.addCategory(Intent.CATEGORY_DEFAULT);
        registerReceiver(mReceiver, filter);

        startCpuSetSearch();
    }

    protected void onSetReceive(Context context, Intent intent) {
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

    protected void startCpuSetSearch() {
        Intent cpuService = new Intent(this, CpuPlayerService.class);
        cpuService.setAction(Constants.Actions.CPU_PLAYER);
        cpuService.putExtra(Constants.Keys.CPU_DIFFICULTY, mCpuDifficulty);
        ArrayList<Card> cards = (ArrayList) mGame.getActiveCards();
        cpuService.putParcelableArrayListExtra(Constants.Keys.ACTIVE_CARDS, cards);
        startService(cpuService);
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

    protected void sendBroadcast() {
        Intent broadcast = new Intent();
        broadcast.setAction(Constants.Actions.BROADCAST);
        broadcast.addCategory(Intent.CATEGORY_DEFAULT);
        sendBroadcast(broadcast);
    }

    protected void updateScore() {
        score.setText("Score: " + mSetCount);
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
            updateScore();
            stopService(new Intent(this, CpuPlayerService.class));
            startCpuSetSearch();
        }
        return setFound;
    }
}
