package com.kdoherty.set.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.kdoherty.set.R;
import com.kdoherty.set.model.Card;
import com.kdoherty.set.model.Card.Color;
import com.kdoherty.set.model.Card.Fill;
import com.kdoherty.set.model.Card.Shape;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by kdoherty on 8/5/14.
 */
public class ImageAdapter extends BaseAdapter {

    private Context mContext;

//    private Game mGame;

    private List<Integer> mCardIds = new ArrayList<>();

    private List<Card> mCards;

    private static Map<Card, Integer> sCardImages;

    private LayoutInflater mInflater;

    // Constructor
    public ImageAdapter(Context context, List<Card> cards) {
        this.mCards = cards;
        this.mContext = context;
        this.mInflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        update();
    }

    public void update() {
        mCardIds.clear();
        for (Card card : mCards) {
            mCardIds.add(getCardImages().get(card));
        }
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mCardIds.size();
    }

    @Override
    public Object getItem(int position) {
        return mCardIds.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public View getCardView(GridView view, Integer id) {
        for (int i = 0; i < view.getChildCount(); i++) {
            if (getItem(i).equals(id)) {
                return view.getChildAt(i);
            }
        }
        throw new RuntimeException("Could not find view with id " + id);
    }

    private static class ViewHolder {
        public final ImageView card;

        public ViewHolder(ImageView card) {
            this.card = card;
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView cardIv;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.card, parent, false);
            cardIv = (ImageView) convertView.findViewById(R.id.cardImgView);
            convertView.setTag(new ViewHolder(cardIv));
        } else {
            ViewHolder viewHolder = (ViewHolder) convertView.getTag();
            cardIv = viewHolder.card;
        }
        cardIv.setImageResource(mCardIds.get(position));
        return convertView;
    }

    public static Map<Card, Integer> getCardImages() {
        if (sCardImages == null) {
            initCardImages();
        }
        return sCardImages;
    }

    public static void initCardImages() {
        sCardImages = new HashMap<Card, Integer>();

        // One Empty
        sCardImages.put(new Card(Shape.DIAMOND, 1, Color.GREEN, Fill.EMPTY),
                R.drawable.oneemptygreendiamond);
        sCardImages.put(new Card(Shape.OVAL, 1, Color.GREEN, Fill.EMPTY),
                R.drawable.oneemptygreenoval);
        sCardImages.put(new Card(Shape.SQUIGGLE, 1, Color.GREEN, Fill.EMPTY),
                R.drawable.oneemptygreensquiggle);
        sCardImages.put(new Card(Shape.DIAMOND, 1, Color.PURPLE, Fill.EMPTY),
                R.drawable.oneemptypurplediamond);
        sCardImages.put(new Card(Shape.OVAL, 1, Color.PURPLE, Fill.EMPTY),
                R.drawable.oneemptypurpleoval);
        sCardImages.put(new Card(Shape.SQUIGGLE, 1, Color.PURPLE, Fill.EMPTY),
                R.drawable.oneemptypurplesquiggle);
        sCardImages.put(new Card(Shape.DIAMOND, 1, Color.RED, Fill.EMPTY),
                R.drawable.oneemptyreddiamond);
        sCardImages.put(new Card(Shape.OVAL, 1, Color.RED, Fill.EMPTY),
                R.drawable.oneemptyredoval);
        sCardImages.put(new Card(Shape.SQUIGGLE, 1, Color.RED, Fill.EMPTY),
                R.drawable.oneemptyredsquiggle);

        // One solid
        sCardImages.put(new Card(Shape.DIAMOND, 1, Color.GREEN, Fill.SOLID),
                R.drawable.onesolidgreendiamond);
        sCardImages.put(new Card(Shape.OVAL, 1, Color.GREEN, Fill.SOLID),
                R.drawable.onesolidgreenoval);
        sCardImages.put(new Card(Shape.SQUIGGLE, 1, Color.GREEN, Fill.SOLID),
                R.drawable.onesolidgreensquiggle);
        sCardImages.put(new Card(Shape.DIAMOND, 1, Color.PURPLE, Fill.SOLID),
                R.drawable.onesolidpurplediamond);
        sCardImages.put(new Card(Shape.OVAL, 1, Color.PURPLE, Fill.SOLID),
                R.drawable.onesolidpurpleoval);
        sCardImages.put(new Card(Shape.SQUIGGLE, 1, Color.PURPLE, Fill.SOLID),
                R.drawable.onesolidpurplesquiggle);
        sCardImages.put(new Card(Shape.DIAMOND, 1, Color.RED, Fill.SOLID),
                R.drawable.onesolidreddiamond);
        sCardImages.put(new Card(Shape.OVAL, 1, Color.RED, Fill.SOLID),
                R.drawable.onesolidredoval);
        sCardImages.put(new Card(Shape.SQUIGGLE, 1, Color.RED, Fill.SOLID),
                R.drawable.onesolidredsquiggle);

        // One striped
        sCardImages.put(new Card(Shape.DIAMOND, 1, Color.GREEN, Fill.STRIPED),
                R.drawable.onestripedgreendiamond);
        sCardImages.put(new Card(Shape.OVAL, 1, Color.GREEN, Fill.STRIPED),
                R.drawable.onestripedgreenoval);
        sCardImages.put(new Card(Shape.SQUIGGLE, 1, Color.GREEN, Fill.STRIPED),
                R.drawable.onestripedgreensquiggle);
        sCardImages.put(new Card(Shape.DIAMOND, 1, Color.PURPLE, Fill.STRIPED),
                R.drawable.onestripedpurplediamond);
        sCardImages.put(new Card(Shape.OVAL, 1, Color.PURPLE, Fill.STRIPED),
                R.drawable.onestripedpurpleoval);
        sCardImages.put(new Card(Shape.SQUIGGLE, 1, Color.PURPLE, Fill.STRIPED),
                R.drawable.onestripedpurplesquiggle);
        sCardImages.put(new Card(Shape.DIAMOND, 1, Color.RED, Fill.STRIPED),
                R.drawable.onestripedreddiamond);
        sCardImages.put(new Card(Shape.OVAL, 1, Color.RED, Fill.STRIPED),
                R.drawable.onestripedredoval);
        sCardImages.put(new Card(Shape.SQUIGGLE, 1, Color.RED, Fill.STRIPED),
                R.drawable.onestripedredsquiggle);

        // Two Empty
        sCardImages.put(new Card(Shape.DIAMOND, 2, Color.GREEN, Fill.EMPTY),
                R.drawable.twoemptygreendiamond);
        sCardImages.put(new Card(Shape.OVAL, 2, Color.GREEN, Fill.EMPTY),
                R.drawable.twoemptygreenoval);
        sCardImages.put(new Card(Shape.SQUIGGLE, 2, Color.GREEN, Fill.EMPTY),
                R.drawable.twoemptygreensquiggle);
        sCardImages.put(new Card(Shape.DIAMOND, 2, Color.PURPLE, Fill.EMPTY),
                R.drawable.twoemptypurplediamond);
        sCardImages.put(new Card(Shape.OVAL, 2, Color.PURPLE, Fill.EMPTY),
                R.drawable.twoemptypurpleoval);
        sCardImages.put(new Card(Shape.SQUIGGLE, 2, Color.PURPLE, Fill.EMPTY),
                R.drawable.twoemptypurplesquiggle);
        sCardImages.put(new Card(Shape.DIAMOND, 2, Color.RED, Fill.EMPTY),
                R.drawable.twoemptyreddiamond);
        sCardImages.put(new Card(Shape.OVAL, 2, Color.RED, Fill.EMPTY),
                R.drawable.twoemptyredoval);
        sCardImages.put(new Card(Shape.SQUIGGLE, 2, Color.RED, Fill.EMPTY),
                R.drawable.twoemptyredsquiggle);

        // Two solid
        sCardImages.put(new Card(Shape.DIAMOND, 2, Color.GREEN, Fill.SOLID),
                R.drawable.twosolidgreendiamond);
        sCardImages.put(new Card(Shape.OVAL, 2, Color.GREEN, Fill.SOLID),
                R.drawable.twosolidgreenoval);
        sCardImages.put(new Card(Shape.SQUIGGLE, 2, Color.GREEN, Fill.SOLID),
                R.drawable.twosolidgreensquiggle);
        sCardImages.put(new Card(Shape.DIAMOND, 2, Color.PURPLE, Fill.SOLID),
                R.drawable.twosolidpurplediamond);
        sCardImages.put(new Card(Shape.OVAL, 2, Color.PURPLE, Fill.SOLID),
                R.drawable.twosolidpurpleoval);
        sCardImages.put(new Card(Shape.SQUIGGLE, 2, Color.PURPLE, Fill.SOLID),
                R.drawable.twosolidpurplesquiggle);
        sCardImages.put(new Card(Shape.DIAMOND, 2, Color.RED, Fill.SOLID),
                R.drawable.twosolidreddiamond);
        sCardImages.put(new Card(Shape.OVAL, 2, Color.RED, Fill.SOLID),
                R.drawable.twosolidredoval);
        sCardImages.put(new Card(Shape.SQUIGGLE, 2, Color.RED, Fill.SOLID),
                R.drawable.twosolidredsquiggle);

        // Two striped
        sCardImages.put(new Card(Shape.DIAMOND, 2, Color.GREEN, Fill.STRIPED),
                R.drawable.twostripedgreendiamond);
        sCardImages.put(new Card(Shape.OVAL, 2, Color.GREEN, Fill.STRIPED),
                R.drawable.twostripedgreenoval);
        sCardImages.put(new Card(Shape.SQUIGGLE, 2, Color.GREEN, Fill.STRIPED),
                R.drawable.twostripedgreensquiggle);
        sCardImages.put(new Card(Shape.DIAMOND, 2, Color.PURPLE, Fill.STRIPED),
                R.drawable.twostripedpurplediamond);
        sCardImages.put(new Card(Shape.OVAL, 2, Color.PURPLE, Fill.STRIPED),
                R.drawable.twostripedpurpleoval);
        sCardImages.put(new Card(Shape.SQUIGGLE, 2, Color.PURPLE, Fill.STRIPED),
                R.drawable.twostripedpurplesquiggle);
        sCardImages.put(new Card(Shape.DIAMOND, 2, Color.RED, Fill.STRIPED),
                R.drawable.twostripedreddiamond);
        sCardImages.put(new Card(Shape.OVAL, 2, Color.RED, Fill.STRIPED),
                R.drawable.twostripedredoval);
        sCardImages.put(new Card(Shape.SQUIGGLE, 2, Color.RED, Fill.STRIPED),
                R.drawable.twostripedredsquiggle);

        // Three Empty
        sCardImages.put(new Card(Shape.DIAMOND, 3, Color.GREEN, Fill.EMPTY),
                R.drawable.threeemptygreendiamond);
        sCardImages.put(new Card(Shape.OVAL, 3, Color.GREEN, Fill.EMPTY),
                R.drawable.threeemptygreenoval);
        sCardImages.put(new Card(Shape.SQUIGGLE, 3, Color.GREEN, Fill.EMPTY),
                R.drawable.threeemptygreensquiggle);
        sCardImages.put(new Card(Shape.DIAMOND, 3, Color.PURPLE, Fill.EMPTY),
                R.drawable.threeemptypurplediamond);
        sCardImages.put(new Card(Shape.OVAL, 3, Color.PURPLE, Fill.EMPTY),
                R.drawable.threeemptypurpleoval);
        sCardImages.put(new Card(Shape.SQUIGGLE, 3, Color.PURPLE, Fill.EMPTY),
                R.drawable.threeemptypurplesquiggle);
        sCardImages.put(new Card(Shape.DIAMOND, 3, Color.RED, Fill.EMPTY),
                R.drawable.threeemptyreddiamond);
        sCardImages.put(new Card(Shape.OVAL, 3, Color.RED, Fill.EMPTY),
                R.drawable.threeemptyredoval);
        sCardImages.put(new Card(Shape.SQUIGGLE, 3, Color.RED, Fill.EMPTY),
                R.drawable.threeemptyredsquiggle);

        // Three solid
        sCardImages.put(new Card(Shape.DIAMOND, 3, Color.GREEN, Fill.SOLID),
                R.drawable.threesolidgreendiamond);
        sCardImages.put(new Card(Shape.OVAL, 3, Color.GREEN, Fill.SOLID),
                R.drawable.threesolidgreenoval);
        sCardImages.put(new Card(Shape.SQUIGGLE, 3, Color.GREEN, Fill.SOLID),
                R.drawable.threesolidgreensquiggle);
        sCardImages.put(new Card(Shape.DIAMOND, 3, Color.PURPLE, Fill.SOLID),
                R.drawable.threesolidpurplediamond);
        sCardImages.put(new Card(Shape.OVAL, 3, Color.PURPLE, Fill.SOLID),
                R.drawable.threesolidpurpleoval);
        sCardImages.put(new Card(Shape.SQUIGGLE, 3, Color.PURPLE, Fill.SOLID),
                R.drawable.threesolidpurplesquiggle);
        sCardImages.put(new Card(Shape.DIAMOND, 3, Color.RED, Fill.SOLID),
                R.drawable.threesolidreddiamond);
        sCardImages.put(new Card(Shape.OVAL, 3, Color.RED, Fill.SOLID),
                R.drawable.threesolidredoval);
        sCardImages.put(new Card(Shape.SQUIGGLE, 3, Color.RED, Fill.SOLID),
                R.drawable.threesolidredsquiggle);

        // Three striped
        sCardImages.put(new Card(Shape.DIAMOND, 3, Color.GREEN, Fill.STRIPED),
                R.drawable.threestripedgreendiamond);
        sCardImages.put(new Card(Shape.OVAL, 3, Color.GREEN, Fill.STRIPED),
                R.drawable.threestripedgreenoval);
        sCardImages.put(new Card(Shape.SQUIGGLE, 3, Color.GREEN, Fill.STRIPED),
                R.drawable.threestripedgreensquiggle);
        sCardImages.put(new Card(Shape.DIAMOND, 3, Color.PURPLE, Fill.STRIPED),
                R.drawable.threestripedpurplediamond);
        sCardImages.put(new Card(Shape.OVAL, 3, Color.PURPLE, Fill.STRIPED),
                R.drawable.threestripedpurpleoval);
        sCardImages.put(new Card(Shape.SQUIGGLE, 3, Color.PURPLE, Fill.STRIPED),
                R.drawable.threestripedpurplesquiggle);
        sCardImages.put(new Card(Shape.DIAMOND, 3, Color.RED, Fill.STRIPED),
                R.drawable.threestripedreddiamond);
        sCardImages.put(new Card(Shape.OVAL, 3, Color.RED, Fill.STRIPED),
                R.drawable.threestripedredoval);
        sCardImages.put(new Card(Shape.SQUIGGLE, 3, Color.RED, Fill.STRIPED),
                R.drawable.threestripedredsquiggle);
    }

    public static Card getCard(Integer id) {
        for (Map.Entry<Card, Integer> entry : getCardImages().entrySet()) {
            if (entry.getValue().equals(id)) {
                return entry.getKey();
            }
        }
        throw new RuntimeException("Could not find card corresponding to id: "
                + id);
    }

}