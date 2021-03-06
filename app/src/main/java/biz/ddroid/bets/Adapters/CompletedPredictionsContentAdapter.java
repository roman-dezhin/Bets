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

public class CompletedPredictionsContentAdapter extends RecyclerView.Adapter<CompletedPredictionsContentAdapter.ViewHolder>  {
    private ArrayList<Match> mMatches = new ArrayList<>();
    private Listener mListener;
    private int mMatchesStatus;

    public CompletedPredictionsContentAdapter(int status) {
        this.mMatchesStatus = status;
    }

    public interface Listener {
         void onClick(Match match, int matchStatus);
    }

    @Override
    public CompletedPredictionsContentAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        CardView cv = (CardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.card_match_completed, parent, false);

        return new ViewHolder(cv);
    }

    @Override
    public void onBindViewHolder(CompletedPredictionsContentAdapter.ViewHolder holder, final int position) {
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

        final TextView friendsPredictions = (TextView) cardView.findViewById(R.id.friends_predictions);
        friendsPredictions.setText(match.getFriendsPredictions());

        TextView nameTeam1 = (TextView) cardView.findViewById(R.id.name_team1);
        nameTeam1.setText(match.getTeam1());

        TextView nameTeam2 = (TextView) cardView.findViewById(R.id.name_team2);
        nameTeam2.setText(match.getTeam2());

        TextView matchPrediction = (TextView) cardView.findViewById(R.id.match_prediction);
        matchPrediction.setText(match.getBetTeam1() + " : " + match.getBetTeam2());

        TextView points = (TextView) cardView.findViewById(R.id.points);
        points.setText(String.format(cardView.getContext().getString(R.string.points_formatted), match.getPoints()));

        TextView score = (TextView) cardView.findViewById(R.id.match_score);
        score.setText(match.getScoreTeam1() + " : " + match.getScoreTeam2());

        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener != null) {
                    mListener.onClick(match, mMatchesStatus);
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
