package com.kdoherty.set.activities.race;

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

public class RaceSetUp extends Activity {

    CheckedTextView mCpuPlayerOptionCtv;
    Spinner targetSetsSpinner;
    Spinner difficultySpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_race_setup);
        targetSetsSpinner = (Spinner) findViewById(R.id.target_sets);
        difficultySpinner = (Spinner) findViewById(R.id.cpu_difficulty_spinner);
        mCpuPlayerOptionCtv = (CheckedTextView) findViewById(R.id.cpu_player_chk_box);
        mCpuPlayerOptionCtv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCpuPlayerOptionCtv.toggle();
                if (mCpuPlayerOptionCtv.isChecked()) {
                    difficultySpinner.setVisibility(View.VISIBLE);
                } else {
                    difficultySpinner.setVisibility(View.INVISIBLE);
                }
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.race_setup, menu);
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

    public void onStartClick(View v) {
        int targetSets;
        String targetSelected = String.valueOf(targetSetsSpinner.getSelectedItem());
        if (targetSelected.equalsIgnoreCase("Select a target")) {
            Toast.makeText(this, "Please select a target", Toast.LENGTH_SHORT).show();
            return;
        } else if (targetSelected.equalsIgnoreCase("Full Deck")) {
            targetSets = 27;
        } else {
            targetSets = Integer.valueOf(targetSelected.substring(0, targetSelected.indexOf(" Set")));
        }
        if (mCpuPlayerOptionCtv.isChecked()) {
            Intent cpuRace = new Intent(getApplicationContext(), CpuRace.class);
            String difficultySelected = String.valueOf(difficultySpinner.getSelectedItem());
            if (difficultySelected.equalsIgnoreCase("Select A Difficulty")) {
                Toast.makeText(this, "Please select a difficulty", Toast.LENGTH_SHORT).show();
                return;
            } else {
                cpuRace.putExtra(Constants.Keys.CPU_DIFFICULTY, ActivityUtils.getDifficulty(difficultySelected));
            }
            cpuRace.putExtra(Constants.Keys.TARGET, targetSets);
            startActivity(cpuRace);
        } else {
            Intent race = new Intent(getApplicationContext(), Race.class);
            race.putExtra(Constants.Keys.TARGET, targetSets);
            startActivity(race);
        }
    }
}
