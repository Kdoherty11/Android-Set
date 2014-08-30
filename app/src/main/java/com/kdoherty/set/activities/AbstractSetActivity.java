package com.kdoherty.set.activities;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.kdoherty.set.R;
import com.kdoherty.set.adapters.ImageAdapter;
import com.kdoherty.set.model.Card;
import com.kdoherty.set.model.Game;
import com.kdoherty.set.model.Set;
import com.kdoherty.set.model.SetSolver;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by kdoherty on 8/14/14.
 */
public abstract class AbstractSetActivity extends Activity {

    /** UI Reference to the grid view holding the card images */
    protected GridView mGridView;
    /** Image adapter for card images */
    protected ImageAdapter mAdapter;
    /** The Set mGame we are representing in this activity */
    protected Game mGame;
    /** The number of correct sets the user found. Using AtomicInteger because this can be modified
     * by a received call from a Service
     */
    protected AtomicInteger mSetCount = new AtomicInteger(0);
    /** The number of sets the user called out but were not sets */
    protected int mBadSetCount = 0;
    /** Contains the cards which the user has chosen as part of their set */
    protected List<Card> mPosSet = new ArrayList<>();
    /** Contains the UI references to the Cards which the user has chosen */
    protected List<View> mHighlight = new ArrayList<>();
    /** The background color of the page */
    protected int mBackgroundColor;

    protected void onCreate(Bundle savedInstanceState, int layoutId, int backgroundColorId) {
        super.onCreate(savedInstanceState);
        setContentView(layoutId);
        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        mBackgroundColor = getResources().getColor(backgroundColorId);
        mGridView = (GridView) findViewById(R.id.card_grid);
    }

    protected void initGameView(Game game) {
        this.mGame = game;
        mAdapter = new ImageAdapter(this, game);
        mGridView.setAdapter(mAdapter);
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view,
                                    int i, long l) {
                addToPosSet(
                        ImageAdapter.getCard((Integer) mAdapter.getItem(i)),
                        view);
            }

        });
    }

    protected void addToPosSet(Card c, View view) {
        if (mPosSet.contains(c)) {
            mPosSet.remove(c);
            unhighlight(view);
            return;
        }
        if (mHighlight.size() >= Set.SIZE) {
            unhighlightAll();
        }
        mHighlight.add(view);
        mPosSet.add(c);
        highlightAll(getResources().getColor(R.color.blue));
        if (mPosSet.size() == Set.SIZE) {
            posSetFound(mPosSet);
        }
    }

    protected boolean posSetFound(List<Card> cards) {
        return posSetFound(new Set(cards));
    }

    protected boolean posSetFound(Set pos) {
        boolean isSet = true;
        if (pos.isSet()) {
            Toast.makeText(this, "Set!", Toast.LENGTH_SHORT).show();
            mSetCount.incrementAndGet();
            mGame.remove(pos);
            if (mGame.getActiveCards().size() <= Game.NUM_START_CARDS - Set.SIZE) {
                if (mGame.isOver()) {
                    finishGame();
                    return true;
                } else {
                    mGame.deal(Set.SIZE);
                }
            }
            while (SetSolver.findSet(mGame.getActiveCards()) == null) {
                mGame.deal(Set.SIZE);
            }
            mAdapter.update();
        } else {
            Toast.makeText(this, "Not a Set!", Toast.LENGTH_SHORT).show();
            mBadSetCount++;
            isSet = false;
        }
        unhighlightAll();
        mPosSet.clear();
        return isSet;
    }

    protected abstract void finishGame();

    /**
     * Highlights the input View with the input color
     * @param view The view to highlight
     * @param color The color to highlight with
     */
    protected final void highlight(View view, int color) {
        view.setBackgroundColor(color);
    }

    /**
     * Highlights all Views in the mHighlight field
     * with the input color
     * @param color The color to highlight with
     */
    protected final void highlightAll(int color) {
        for (View view : mHighlight) {
            highlight(view, color);
        }
    }

    /**
     * Removes highlighting from an input View
     * @param view The view to remove highlighting from
     */
    protected final void unhighlight(View view) {
        highlight(view, mBackgroundColor);
        mHighlight.remove(view);
    }

    /**
     * Removes highlighting from all Views in mHighlight
     */
    protected final void unhighlightAll() {
        highlightAll(mBackgroundColor);
        mHighlight.clear();
    }

}
