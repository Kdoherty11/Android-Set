package com.kdoherty.adapters;

import android.graphics.Bitmap;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by kdoherty on 8/12/14.
 */
public class Item {

    private Bitmap mImage;
    private TextView mText;

    public Item(Bitmap mImage, TextView mText) {
        this.mImage = mImage;
        this.mText = mText;
    }

    public Bitmap getImage() {
        return mImage;
    }

    public TextView getText() {
        return mText;
    }
}
