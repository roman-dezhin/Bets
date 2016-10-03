package biz.ddroid.bets.adapters;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import biz.ddroid.bets.R;
import biz.ddroid.bets.pojo.Match;
import biz.ddroid.bets.utils.NetworkConstants;

public class NewPredictionsContentAdapter extends RecyclerView.Adapter<NewPredictionsContentAdapter.ViewHolder>  {
    private ArrayList<Match> mMatches = new ArrayList<>();
    private Listener mListener;
    private int mMatchStatus;

    public NewPredictionsContentAdapter(int status) {
        this.mMatchStatus = status;
    }

    public interface Listener {
         void onClick(Match match, int matchStatus);
    }

    @Override
    public NewPredictionsContentAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        CardView cv = (CardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.card_match_new, parent, false);

        return new ViewHolder(cv);
    }

    @Override
    public void onBindViewHolder(NewPredictionsContentAdapter.ViewHolder holder, final int position) {
        final Match match = mMatches.get(position);
        CardView cardView = holder.cardView;
        ImageView iconTeam1 = (ImageView) cardView.findViewById(R.id.icon_team1);
        Picasso.with(holder.cardView.getContext())
                .load(NetworkConstants.SERVER_ADDRESS + match.getImageTeam1())
                .placeholder(R.drawable.team_icon_placeholder)
                .error(R.drawable.team_icon_placeholder)
                .into(iconTeam1);
        ImageView iconTeam2 = (ImageView) cardView.findViewById(R.id.icon_team2);
        Picasso.with(holder.cardView.getContext())
                .load(NetworkConstants.SERVER_ADDRESS + match.getImageTeam2())
                .placeholder(R.drawable.team_icon_placeholder)
                .error(R.drawable.team_icon_placeholder)
                .into(iconTeam2);

        TextView tourName = (TextView) cardView.findViewById(R.id.tour_name);
        tourName.setText(match.getTourName() + "   " + match.getStage());

        TextView dateTime = (TextView) cardView.findViewById(R.id.date_time);
        dateTime.setText(match.getDateTime());

        TextView betsCount = (TextView) cardView.findViewById(R.id.bets_count);
        betsCount.setText(String.format(cardView.getContext().getString(R.string.predictions_count_formatted), match.getBetsCount()));

        TextView nameTeam1 = (TextView) cardView.findViewById(R.id.name_team1);
        nameTeam1.setText(match.getTeam1());

        TextView nameTeam2 = (TextView) cardView.findViewById(R.id.name_team2);
        nameTeam2.setText(match.getTeam2());

        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener != null) {
                    mListener.onClick(match, mMatchStatus);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mMatches.size();
    }

    public void setListener(Listener listener) {
        this.mListener = listener;
    }

    public void setMatches(ArrayList<Match> listMatches) {
        this.mMatches = listMatches;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private CardView cardView;

        public ViewHolder(CardView v) {
            super(v);
            cardView = v;
        }
    }
}
