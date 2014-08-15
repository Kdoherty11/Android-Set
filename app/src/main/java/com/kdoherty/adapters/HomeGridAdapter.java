package com.kdoherty.adapters;

/**
 * Created by kdoherty on 8/12/14.
 */
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kdoherty.activities.HomeScreen;
import com.kdoherty.set.R;

public class HomeGridAdapter extends BaseAdapter{
    private Context mContext;
    private final String[] web;
    private final int[] Imageid;

    public HomeGridAdapter(Context c,String[] web,int[] Imageid ) {
        mContext = c;
        this.Imageid = Imageid;
        this.web = web;
    }
    @Override
    public int getCount() {
        return web.length;
    }
    @Override
    public Object getItem(int position) {
        return null;
    }
    @Override
    public long getItemId(int position) {
        return 0;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View grid;
        LayoutInflater inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            grid = new View(mContext);
            grid = inflater.inflate(R.layout.grid_item, null);
            TextView textView = (TextView) grid.findViewById(R.id.item_text);
            ImageView imageView = (ImageView)grid.findViewById(R.id.item_image);
            textView.setText(web[position]);
            imageView.setImageResource(Imageid[position]);
            grid.setMinimumHeight((HomeScreen.SCREEN_HEIGHT / 2) - HomeScreen.SCREEN_HEIGHT / 15);
        } else {
            grid = (View) convertView;
        }
        return grid;
    }
}