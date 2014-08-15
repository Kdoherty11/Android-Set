package com.kdoherty.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.kdoherty.adapters.HomeGridAdapter;
import com.kdoherty.set.R;

public class HomeScreen extends Activity {

    public static int SCREEN_WIDTH;
    public static int SCREEN_HEIGHT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);

        SCREEN_WIDTH= metrics.widthPixels;
        SCREEN_HEIGHT = metrics.heightPixels;

        String [] captions = {"Multiplayer", "Single Player", "Stop Watch", "Play Computer"};
        int [] imageIds = {R.drawable.multiplayer, R.drawable.read_icon,
                R.drawable.stopwatch, R.drawable.computer_icon};
        HomeGridAdapter adapter = new HomeGridAdapter(this, captions, imageIds);
        GridView grid = (GridView) findViewById(R.id.home_grid);
        grid.setAdapter(adapter);
        grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        startIntent(Multiplayer.class);
                       break;
                    case 1:
                        startIntent(TimeScroller.class);
                        break;
                    case 2:
                        Toast.makeText(HomeScreen.this, "Stop Watch", Toast.LENGTH_SHORT).show();
                        startIntent(Race.class);
                        break;
                    case 3:
                        Toast.makeText(HomeScreen.this, "Computer", Toast.LENGTH_SHORT).show();
                        //startIntent();
                        //TODO:
                        break;
                    default:
                        throw new IllegalStateException("Should not get to default case");
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home_screen, menu);
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

    private void startIntent(Class clazz) {
        Intent intent = new Intent(getApplicationContext(), clazz);
        startActivity(intent);
    }
}
