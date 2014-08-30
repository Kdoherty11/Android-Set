package com.kdoherty.set.activities.race;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.kdoherty.set.Constants;
import com.kdoherty.set.R;
import com.kdoherty.set.activities.ActivityUtils;

public class RaceSetUp extends Activity {

    EditText targetSetsView;
    CheckedTextView cpuPlayerOpt;
    Spinner timeSpinner;
    Spinner difficultySpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_race_setup);
        targetSetsView = (EditText) findViewById(R.id.target_sets);
        difficultySpinner = (Spinner) findViewById(R.id.cpu_difficulty_spinner);
        cpuPlayerOpt = (CheckedTextView) findViewById(R.id.cpu_player_chk_box);
        cpuPlayerOpt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cpuPlayerOpt.toggle();
                if (cpuPlayerOpt.isChecked()) {
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
        int targetSets = 0;
        try {
            targetSets = Integer.valueOf(targetSetsView.getText().toString());
        } catch (NumberFormatException ex) {
            Toast.makeText(this, "Please enter a number between 1 and 25", Toast.LENGTH_SHORT).show();
            return;
        }
        if (targetSets < 1 || targetSets > 25) {
            Toast.makeText(this, "Please enter a number between 1 and 25", Toast.LENGTH_SHORT).show();
            return;
        }
        if (cpuPlayerOpt.isChecked()) {
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
