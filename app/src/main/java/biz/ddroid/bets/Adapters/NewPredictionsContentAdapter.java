package biz.ddroid.bets.adapters;

import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import biz.ddroid.bets.R;
import biz.ddroid.bets.pojo.Match;

public class NewPredictionsContentAdapter extends RecyclerView.Adapter<NewPredictionsContentAdapter.ViewHolder>  {
    private ArrayList<Match> mMatches = new ArrayList<>();
    private Listener mListener;
    private int mMatchesStatus;

    public NewPredictionsContentAdapter(int status) {
        this.mMatchesStatus = status;
    }

    public interface Listener {
         void onClick(int position);
    }

    public void setListener(Listener listener) {
        this.mListener = listener;
    }

    public void setMatches(ArrayList<Match> listMatches) {
        this.mMatches = listMatches;
        //update the adapter to reflect the new set of matches
        notifyDataSetChanged();
    }

    @Override
    public NewPredictionsContentAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        CardView cv = (CardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.card_match, parent, false);

        return new ViewHolder(cv);
    }

    @Override
    public void onBindViewHolder(NewPredictionsContentAdapter.ViewHolder holder, final int position) {
        final Match match = mMatches.get(position);
        CardView cardView = holder.cardView;
        ImageView imageView = (ImageView)cardView.findViewById(R.id.icon_team1);
        imageView.setImageDrawable(ResourcesCompat.getDrawable(cardView.getResources(), R.drawable.team1, null));

        imageView = (ImageView)cardView.findViewById(R.id.icon_team2);
        imageView.setImageDrawable(ResourcesCompat.getDrawable(cardView.getResources(), R.drawable.team2, null));

        TextView tour_name = (TextView) cardView.findViewById(R.id.tour_name);
        tour_name.setText(match.getTourName() + "   " + match.getStage());

        TextView date_time = (TextView) cardView.findViewById(R.id.date_time);
        date_time.setText(match.getDateTime());

        TextView bets_count = (TextView) cardView.findViewById(R.id.bets_count);
        bets_count.setText("Bets count: " + match.getBetsCount());

        TextView textView = (TextView)cardView.findViewById(R.id.name_team1);
        textView.setText(match.getTeam1());

        textView = (TextView)cardView.findViewById(R.id.name_team2);
        textView.setText(match.getTeam2());

        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener != null) {
                    mListener.onClick(match.getId());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mMatches.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private CardView cardView;

        public ViewHolder(CardView v) {
            super(v);
            cardView = v;
        }
    }
}
