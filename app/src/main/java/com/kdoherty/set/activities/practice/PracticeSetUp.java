package com.kdoherty.set.activities.practice;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckedTextView;
import android.widget.Spinner;
import android.widget.Toast;

import com.kdoherty.set.Constants;
import com.kdoherty.set.R;

public class PracticeSetUp extends Activity {

    CheckedTextView cpuPlayerOpt;
    Spinner timeSpinner;
    Spinner difficultySpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_practice_set_up);
        timeSpinner = (Spinner) findViewById(R.id.time_spinner);
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

    public void onStartClick(View v) {

        String timeSelected = String.valueOf(timeSpinner.getSelectedItem());
        if (timeSelected.equalsIgnoreCase("Select A Time")) {
            Toast.makeText(this, "Please select a time", Toast.LENGTH_SHORT).show();
            return;
        }
        if (cpuPlayerOpt.isChecked()) {
            Intent countdownCpu = new Intent(getApplicationContext(), CpuPractice.class);
            String difficultySelected = String.valueOf(difficultySpinner.getSelectedItem());
            if (difficultySelected.equalsIgnoreCase("Select A Difficulty")) {
                Toast.makeText(this, "Please select a difficulty", Toast.LENGTH_SHORT).show();
                return;
            } else {
                countdownCpu.putExtra(Constants.Keys.CPU_DIFFICULTY, getDifficulty(difficultySelected));
            }
            countdownCpu.putExtra(Constants.Keys.TIME, getMillis(timeSelected));
            startActivity(countdownCpu);
        } else {
            Intent countdownPractice = new Intent(getApplicationContext(), Practice.class);
            countdownPractice.putExtra(Constants.Keys.TIME, getMillis(timeSelected));
            startActivity(countdownPractice);
        }
    }

    private int getDifficulty(String option) {
        switch (option) {
            case "Easy":
                return Constants.Cpu.EASY;
            case "Medium":
                return Constants.Cpu.MEDIUM;
            case "Hard":
                return Constants.Cpu.HARD;
            case "Insane":
                return Constants.Cpu.INSANE;
            default:
                throw new IllegalStateException("Illegal option selected " + option);
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

    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
    }
}
