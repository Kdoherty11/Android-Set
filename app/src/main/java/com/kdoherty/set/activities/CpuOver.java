package com.kdoherty.set.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.kdoherty.set.Constants;
import com.kdoherty.set.R;
import com.kdoherty.set.activities.practice.PracticeSetUp;
import com.kdoherty.set.activities.race.RaceSetUp;

public class CpuOver extends Activity {

    private String mGameMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_practice_cpu_over);
        Bundle bundle = getIntent().getExtras();
        int userScore = bundle.getInt(Constants.Keys.USER_SCORE);
        int compScore = bundle.getInt(Constants.Keys.CPU_SCORE);
        mGameMode = bundle.getString(Constants.Keys.GAME_MODE);
        String resultTxt;
        if (userScore > compScore) {
            resultTxt = "YOU WIN!";
        } else if (compScore > userScore) {
            resultTxt = "YOU LOSE!";
        } else {
            resultTxt = "TIE!";
        }
        ((TextView) findViewById(R.id.result)).setText(resultTxt);
        ((TextView) findViewById(R.id.user_score)).setText("Your Score " + userScore);
        ((TextView) findViewById(R.id.cpu_score)).setText("Computer Score " + compScore);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.countdown_practice_cpu_over, menu);
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

    public void restart(View v) {

        Intent times;
        if (mGameMode.equals(Constants.Modes.PRACTICE)) {
            times = new Intent(getApplicationContext(), PracticeSetUp.class);
        } else {
            times = new Intent(getApplicationContext(), RaceSetUp.class);
        }
        startActivity(times);
    }

    public void home(View v) {
        Intent home = new Intent(getApplicationContext(), HomeScreen.class);
        startActivity(home);
    }

}
