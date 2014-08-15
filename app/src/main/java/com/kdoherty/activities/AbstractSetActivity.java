package com.kdoherty.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.kdoherty.adapters.ImageAdapter;
import com.kdoherty.model.Card;
import com.kdoherty.model.Game;
import com.kdoherty.model.Set;
import com.kdoherty.set.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kdoherty on 8/14/14.
 */
public abstract class AbstractSetActivity extends Activity {

    /** UI Reference to the grid view holding the card images */
    protected GridView mGridView;
    /** Image adapter for card images */
    protected ImageAdapter mAdapter;

    /** The Set game we are representing in this activity */
    protected Game game;
    /** The time in milliseconds of how long the user has to find sets */
    protected long mTime;
    /** The number of correct sets the user found */
    protected int mSetCount = 0;
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
        mBackgroundColor = getResources().getColor(backgroundColorId);
    }


    protected void initGridView(Game game) {
        this.game = game;
        mGridView = (GridView) findViewById(R.id.gridView);
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

    protected void posSetFound(List<Card> cards) {
        Set pos = new Set(cards);
        if (pos.isSet()) {
            Toast.makeText(this, "Set!", Toast.LENGTH_SHORT).show();
            mSetCount++;
            game.remove(mPosSet);
            if (game.getActiveCards().size() <= Game.NUM_START_CARDS - Set.SIZE) {
                if (game.isOver()) {
                    finishGame();
                    return;
                } else {
                    game.deal(Set.SIZE);
                }
            }
            mAdapter.update();
        } else {
            Toast.makeText(this, "Not a Set!", Toast.LENGTH_SHORT).show();
            mBadSetCount++;
        }
        unhighlightAll();
        mPosSet.clear();
    }

    abstract void finishGame();

    /**
     * Highlights the input View with the input color
     * @param view The view to highlight
     * @param color The color to highlight with
     */
    protected void highlight(View view, int color) {
        view.setBackgroundColor(color);
    }

    /**
     * Highlights all Views in the mHighlight field
     * with the input color
     * @param color The color to highlight with
     */
    protected void highlightAll(int color) {
        for (View view : mHighlight) {
            highlight(view, color);
        }
    }

    /**
     * Removes highlighting from an input View
     * @param view The view to remove highlighting from
     */
    protected void unhighlight(View view) {
        highlight(view, mBackgroundColor);
        mHighlight.remove(view);
    }

    /**
     * Removes highlighting from all Views in mHighlight
     */
    protected void unhighlightAll() {
        highlightAll(mBackgroundColor);
        mHighlight.clear();
    }

}
