package com.kdoherty.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.kdoherty.adapters.ImageAdapter;
import com.kdoherty.model.Card;
import com.kdoherty.model.Game;
import com.kdoherty.model.Set;
import com.kdoherty.model.SetSolver;
import com.kdoherty.set.R;

import java.util.ArrayList;
import java.util.List;

public class TimedPractice extends Activity {

    /** Contains the cards which the user has chosen as part of their set */
    private List<Card> mPosSet = new ArrayList<>();
    /** Contains the UI references to the Cards which the user has chosen */
    private List<View> mHighlight = new ArrayList<>();

    /** Image adapter for card images */
    private ImageAdapter mAdapter;

    /** The background color of the page */
    private int mBackgroundColor;

    /** The time in milliseconds of how long the user has to find sets */
    private long mTime;

    /** The count down timer */
    private CountDownTimer mTimer;
    /** UI Reference to the timer */
    private TextView mTimerView;
    /** UI Reference to the grid view holding the card images */
    private GridView mGridView;

    /** The number of correct sets the user found */
    private int mSetCount = 0;

    /** The number of sets the user called out but were not sets */
    private int mBadSetCount = 0;

    private Game game = new Game();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timed_practice);
        mBackgroundColor = getResources().getColor(R.color.WHITE);
        mTime = getIntent().getExtras().getLong("time");
        initGridView();
        initTimer();
    }

    public void initGridView() {
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

    private static <T> List<T> shallowCopyList(List<T> list) {
        List<T> copy = new ArrayList<>();
        for (T t : list) {
            copy.add(t);
        }
        return copy;
    }

    /**
     * Responds to a click on the solverButtonView. It finds a set as per game
     * rules if there is one and highlights the cards in the set. If there is
     * not one on the table, a Toast is shown saying there are no sets
     *
     * @param view
     */
    public void onSolverButtonClick(View view) {
        Set set = SetSolver.findSet(shallowCopyList(game.getActiveCards()));
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
     * @param arg0
     */
    public void onDealButtonClick(View arg0) {
        if (game.getActiveCards().size() <= Game.NUM_START_CARDS) {
            //mAdapter.deal(Set.SIZE);
            game.deal(Set.SIZE);
        }
        //mGridView.setAdapter(mAdapter);
        mAdapter.update();
    }

    /**
     * Highlights the input View with the input color
     * @param view The view to highlight
     * @param color The color to highlight with
     */
    public void highlight(View view, int color) {
        view.setBackgroundColor(color);
    }

    /**
     * Highlights all Views in the mHighlight field
     * with the input color
     * @param color The color to highlight with
     */
    public void highlightAll(int color) {
        for (View view : mHighlight) {
            highlight(view, color);
        }
    }

    /**
     * Removes highlighting from an input View
     * @param view The view to remove highlighting from
     */
    public void unhighlight(View view) {
        highlight(view, mBackgroundColor);
        mHighlight.remove(view);
    }

    /**
     * Removes highlighting from all Views in mHighlight
     */
    public void unhighlightAll() {
        highlightAll(mBackgroundColor);
        mHighlight.clear();
    }

    /**
     * Converts milliseconds into a String of format
     * MM:SS where M is minutes and S is seconds
     * @param millis The milliseconds to convert to a String
     * @return
     */
    public String millisToString(long millis) {
        long seconds = millis / 1000;
        if (seconds < 60) {
            return "00:" + secondsToString(seconds);
        }
        long minutes = seconds / 60;
        long remainder = seconds % 60;
        return "0" + String.valueOf(minutes) + ":" + secondsToString(remainder);
    }

    public String secondsToString(long seconds) {
        if (seconds > 9) {
            return String.valueOf(seconds);
        } else {
            return "0" + String.valueOf(seconds);
        }
    }

    public void initTimer() {
        mTimerView = (TextView) findViewById(R.id.timerView);
        mTimer = new CountDownTimer(mTime, 1000) {

            public void onTick(long millisUntilFinished) {
                String timerText = millisToString(millisUntilFinished);
                mTimerView.setText(timerText);
                if (millisUntilFinished <= 6000) {
                    mTimerView.setTextColor(getResources().getColor(
                            R.color.red_cherry));
                }
            }

            public void onFinish() {
                finishGame();
            }
        };
        mTimer.start();
    }

    private void finishGame() {
        Intent gameOver = new Intent(getApplicationContext(),
                GameOver.class);
        gameOver.putExtra("setCount", mSetCount);
        gameOver.putExtra("badSetCount", mBadSetCount);
        gameOver.putExtra("time", mTime);
        startActivity(gameOver);
    }

    public void addToPosSet(Card c, View view) {
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
            Set pos = new Set(mPosSet);
            if (pos.isSet()) {
                mSetCount++;
                Toast.makeText(this, "Set!", Toast.LENGTH_SHORT).show();
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
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
