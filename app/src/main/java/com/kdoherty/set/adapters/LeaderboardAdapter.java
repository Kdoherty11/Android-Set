package com.kdoherty.set.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.kdoherty.set.Constants;
import com.kdoherty.set.R;
import com.kdoherty.set.model.LeaderboardEntry;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

/**
 * Created by kdoherty on 8/29/14.
 */
public class LeaderboardAdapter extends ArrayAdapter<LeaderboardEntry> {

    private Context context;
    private List<LeaderboardEntry> entries;
    private String gameMode;
    private LayoutInflater mInflater;

    public LeaderboardAdapter(Context context, int resource, List<LeaderboardEntry> entries, String gameMode) {
        super(context, resource, entries);
        this.context = context;
        this.entries = entries;
        this.gameMode = gameMode;
        this.mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    private static class ViewHolder {

        public final TextView positionTv;
        public final TextView nameTv;
        public final TextView scoreTv;


        public ViewHolder(TextView positionTv, TextView nameTv, TextView scoreTv) {
            this.positionTv = positionTv;
            this.nameTv = nameTv;
            this.scoreTv = scoreTv;
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        TextView positionTv;
        TextView nameTv;
        TextView scoreTv;

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.leaderboard_entry, parent, false);
            positionTv = (TextView) convertView.findViewById(R.id.position);
            nameTv = (TextView) convertView.findViewById(R.id.name);
            scoreTv = (TextView) convertView.findViewById(R.id.score);
            convertView.setTag(new ViewHolder(positionTv, nameTv, scoreTv));
        } else {
            ViewHolder viewHolder = (ViewHolder) convertView.getTag();
            positionTv = viewHolder.positionTv;
            nameTv = viewHolder.nameTv;
            scoreTv = viewHolder.scoreTv;
        }

        LeaderboardEntry entry = entries.get(position);

        long score = entry.getScore();
        String scoreString;
        if (gameMode.equals(Constants.Modes.PRACTICE)) {
            scoreString = String.valueOf(score);
        } else {
            final SimpleDateFormat timeFormat = new SimpleDateFormat("m:ss",
                    Locale.getDefault());
            scoreString = timeFormat.format(score);
        }

        positionTv.setText(String.valueOf(position + 1) + ".");
        nameTv.setText(entry.getName());
        scoreTv.setText(scoreString);

        return convertView;
    }
}
