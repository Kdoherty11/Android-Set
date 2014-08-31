package com.kdoherty.set.activities.practice;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckedTextView;
import android.widget.Spinner;
import android.widget.Toast;

import com.kdoherty.set.Constants;
import com.kdoherty.set.R;
import com.kdoherty.set.activities.ActivityUtils;

public class PracticeSetUp extends Activity {

    CheckedTextView mCpuPlayerOptionCtv;
    Spinner mTimeSpinner;
    Spinner mDifficultySpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_practice_set_up);
        mTimeSpinner = (Spinner) findViewById(R.id.time_spinner);
        mDifficultySpinner = (Spinner) findViewById(R.id.cpu_difficulty_spinner);
        mCpuPlayerOptionCtv = (CheckedTextView) findViewById(R.id.cpu_player_chk_box);
        mCpuPlayerOptionCtv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCpuPlayerOptionCtv.toggle();
                if (mCpuPlayerOptionCtv.isChecked()) {
                    mDifficultySpinner.setVisibility(View.VISIBLE);
                } else {
                    mDifficultySpinner.setVisibility(View.INVISIBLE);
                }
            }
        });
    }

    public void onStartClick(View v) {

        String timeSelected = String.valueOf(mTimeSpinner.getSelectedItem());
        if (timeSelected.equalsIgnoreCase("Select A Time")) {
            Toast.makeText(this, "Please select a time", Toast.LENGTH_SHORT).show();
            return;
        }
        if (mCpuPlayerOptionCtv.isChecked()) {
            Intent countdownCpu = new Intent(getApplicationContext(), CpuPractice.class);
            String difficultySelected = String.valueOf(mDifficultySpinner.getSelectedItem());
            if (difficultySelected.equalsIgnoreCase("Select A Difficulty")) {
                Toast.makeText(this, "Please select a difficulty", Toast.LENGTH_SHORT).show();
                return;
            } else {
                countdownCpu.putExtra(Constants.Keys.CPU_DIFFICULTY, ActivityUtils.getDifficulty(difficultySelected));
            }
            countdownCpu.putExtra(Constants.Keys.TIME, getMillis(timeSelected));
            startActivity(countdownCpu);
        } else {
            Intent countdownPractice = new Intent(getApplicationContext(), Practice.class);
            countdownPractice.putExtra(Constants.Keys.TIME, getMillis(timeSelected));
            startActivity(countdownPractice);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.time_scroller, menu);
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

    public long getMillis(String time) {
        if (time.endsWith(" Seconds")) {
            return Integer.valueOf(time.substring(0, time.indexOf(" Seconds"))) * 1000;
        } else {
            return Integer.valueOf(time.substring(0, time.indexOf(" Minute"))) * 60000;
        }
    }
}
