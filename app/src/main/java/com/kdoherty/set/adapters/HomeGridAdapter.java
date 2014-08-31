package com.kdoherty.set.adapters;

/**
 * Created by kdoherty on 8/12/14.
 */

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.kdoherty.set.R;
import com.kdoherty.set.activities.HomeScreen;

public class HomeGridAdapter extends BaseAdapter {
    private final Context mContext;
    private final String[] mCaptions;
    private final int[] mImageIds;

    public HomeGridAdapter(Context context, String[] captions, int[] imageIds) {
        this.mContext = context;
        this.mCaptions = captions;
        this.mImageIds = imageIds;
    }

    @Override
    public int getCount() {
        return mCaptions.length;
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
            TextView captionTv = (TextView) grid.findViewById(R.id.item_text);
            ImageView imageView = (ImageView) grid.findViewById(R.id.item_image);
            captionTv.setText(mCaptions[position]);
            imageView.setImageResource(mImageIds[position]);
            grid.setMinimumHeight((HomeScreen.SCREEN_HEIGHT / 2) - HomeScreen.SCREEN_HEIGHT / 15);
        } else {
            grid = (View) convertView;
        }
        return grid;
    }
}