package biz.ddroid.bets.Adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Map;

import biz.ddroid.bets.R;
import biz.ddroid.bets.vos.Match;

public class MatchesContentAdapter extends RecyclerView.Adapter<MatchesContentAdapter.ViewHolder>  {
    private Map<Integer, Match> mMatches;

    @Override
    public MatchesContentAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        CardView cv = (CardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.card_match, parent, false);

        return new ViewHolder(cv);
    }

    @Override
    public void onBindViewHolder(MatchesContentAdapter.ViewHolder holder, int position) {
        CardView cardView = holder.cardView;
        ImageView imageView = (ImageView)cardView.findViewById(R.id.icon_team1);
        imageView.setImageDrawable(ResourcesCompat.getDrawable(cardView.getResources(), R.drawable.team1, null));

        imageView = (ImageView)cardView.findViewById(R.id.icon_team2);
        imageView.setImageDrawable(ResourcesCompat.getDrawable(cardView.getResources(), R.drawable.team2, null));

        TextView textView = (TextView)cardView.findViewById(R.id.name_team1);
        textView.setText("AC Milan");
        textView = (TextView)cardView.findViewById(R.id.name_team2);
        textView.setText("FC Barcelona");
    }

    @Override
    public int getItemCount() {
        return 10;//mMatches.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private CardView cardView;

        public ViewHolder(CardView v) {
            super(v);
            cardView = v;
        }
    }
}
