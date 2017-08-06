package biz.ddroid.bets.adapters;

import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import biz.ddroid.bets.R;
import biz.ddroid.bets.pojo.TournamentResult;
import biz.ddroid.bets.pojo.TournamentResultRow;
import biz.ddroid.bets.rest.PredictServices;

public class ResultsWithFriendsContentAdapter extends BaseResultsRecyclerAdapter<TournamentResult, ResultsWithFriendsContentAdapter.ViewHolder>  {
    private String TAG = "RWFCA";

    @Override
    public ResultsWithFriendsContentAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        CardView cv = (CardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.card_result_with_friends, parent, false);

        return new ViewHolder(cv);
    }

    @Override
    public void onBindViewHolder(ResultsWithFriendsContentAdapter.ViewHolder holder, final int position) {
        TournamentResult result = get(position);
        holder.tourName.setText(result.getTourName());

        holder.tableLayout.removeViews(1, holder.tableLayout.getChildCount() - 1);

        for (TournamentResultRow row : result.getResults()) {
            TableRow tableRow = (TableRow) LayoutInflater.from(holder.tableLayout.getContext()).inflate(R.layout.result_table_row, null);
            TextView tv1 = (TextView) tableRow.findViewById(R.id.username);
            tv1.setText(row.getName());
            if (row.getWinner() == PredictServices.USER_IS_WINNER)
                tv1.setTextColor(ContextCompat.getColor(holder.tableLayout.getContext(), R.color.tournamentWinner));

            TextView tv2 = (TextView) tableRow.findViewById(R.id.points);
            tv2.setText(Integer.toString(row.getPoints()));
            tv2.setGravity(Gravity.CENTER_HORIZONTAL);

            TextView tv3 = (TextView) tableRow.findViewById(R.id.bets);
            tv3.setText(Integer.toString(row.getPredictions()));
            tv3.setGravity(Gravity.CENTER_HORIZONTAL);

            TextView tv4 = (TextView) tableRow.findViewById(R.id.scores);
            tv4.setText(Integer.toString(row.getScores()));
            tv4.setGravity(Gravity.CENTER_HORIZONTAL);

            TextView tv5 = (TextView) tableRow.findViewById(R.id.results);
            tv5.setText(Integer.toString(row.getResults()));
            tv5.setGravity(Gravity.CENTER_HORIZONTAL);

            holder.tableLayout.addView(tableRow);
        }
    }

    @Override
    public int getItemCount() {
        return size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tourName;
        private TableLayout tableLayout;

        public ViewHolder(CardView v) {
            super(v);
            tourName = (TextView) v.findViewById(R.id.tour_name);
            tableLayout = (TableLayout) v.findViewById(R.id.table);
        }
    }
}
