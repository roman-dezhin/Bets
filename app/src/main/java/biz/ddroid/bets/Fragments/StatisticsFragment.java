package biz.ddroid.bets.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.List;

import biz.ddroid.bets.R;
import biz.ddroid.bets.pojo.Statistic;
import biz.ddroid.bets.rest.PredictServices;
import biz.ddroid.bets.rest.ServicesClient;
import biz.ddroid.bets.utils.NetworkUtils;
import cz.msebera.android.httpclient.Header;

public class StatisticsFragment extends BaseStatisticsFragment {

    private String TAG = "StatisticsFragment";

    public StatisticsFragment() {
        // Required empty public constructor
    }

    public static StatisticsFragment newInstance(int statistics_status) {
        StatisticsFragment fragment = new StatisticsFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_STATISTICS_STATUS, statistics_status);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.v(TAG, "onCreateView: ");
        View rootView = inflater.inflate(R.layout.fragment_statistics, container, false);
        requestDateTime = (TextView) rootView.findViewById(R.id.request_datetime);
        dataInfo = (TextView) rootView.findViewById(R.id.info);
        tableLayout = (TableLayout) rootView.findViewById(R.id.statistics_table);

        requestTime = new Date();
        if (savedInstanceState != null) {
            statistics = savedInstanceState.getParcelableArrayList(STATE_STATISTICS);
            if (statistics != null && statistics.isEmpty()) {
                isRequestEnd = true;
                isResponseEmpty = true;
            }
            requestTime.setTime(savedInstanceState.getLong(STATE_REQUEST_TIME));
            drawTableRows(statistics);
        } else {
            if (statistics.isEmpty()) {
                refreshResults(servicesClient);
            } else {
                drawTableRows(statistics);
            }
        }
        updateUI();
        return rootView;
    }

    public void refreshResults(ServicesClient servicesClient) {
        if (!NetworkUtils.isNetworkConnected(getActivity())) {
            Toast.makeText(getActivity(), R.string.no_internet_connections, Toast.LENGTH_SHORT).show();
            onFragmentRefreshed();
        }
        predictServices = new PredictServices(servicesClient);
        JSONObject filter = new JSONObject();
        try {
            filter.put(PredictServices.TOURNAMENT_FILTER, 0);
            filter.put(PredictServices.RESULT_FILTER, getStatisticsStatus());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        predictServices.statistic(filter, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                if (responseBody != null) Log.v(TAG, new String(responseBody));
                requestTime = new Date();
                isRequestEnd = true;
                if (responseBody != null) parseResponse(responseBody);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                if (responseBody != null) Log.v(TAG, new String(responseBody));
            }

            @Override
            public void onFinish() {
                onFragmentRefreshed();
            }
        });
    }

    protected void parseResponse(byte[] responseBody) {
        statistics.clear();
        try {
            JSONArray response = new JSONArray(new String(responseBody));
            for (int i = 0; i < response.length(); i++) {
                JSONObject jsonResult = response.getJSONObject(i);
                Statistic statistic = new Statistic(
                        jsonResult.getString(PredictServices.STATISTIC_USERNAME),
                        jsonResult.getInt(PredictServices.STATISTIC_POINTS),
                        jsonResult.getInt(PredictServices.STATISTIC_PREDICTIONS),
                        jsonResult.getInt(PredictServices.STATISTIC_SCORES),
                        jsonResult.getInt(PredictServices.STATISTIC_RESULTS),
                        jsonResult.getInt(PredictServices.STATISTIC_PERCENTS),
                        jsonResult.getInt(PredictServices.STATISTIC_WINS));
                statistics.add(statistic);
                isResponseEmpty = false;
            }
            drawTableRows(statistics);
            updateUI();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void drawTableRows(List<Statistic> statistics) {
        //TODO: найти причину повторения tableLayout и появления "чужих" TableRow в tableLayout
        tableLayout.removeViews(1, tableLayout.getChildCount() - 1);

        for (Statistic statistic : statistics) {
            TableRow tableRow = (TableRow) LayoutInflater.from(tableLayout.getContext()).inflate(R.layout.statistic_table_row, null);
            TextView tv1 = (TextView) tableRow.findViewById(R.id.username);
            tv1.setText(statistic.getName());

            TextView tv2 = (TextView) tableRow.findViewById(R.id.points);
            tv2.setText(Integer.toString(statistic.getPoints()));
            tv2.setGravity(Gravity.CENTER_HORIZONTAL);

            TextView tv3 = (TextView) tableRow.findViewById(R.id.bets);
            tv3.setText(Integer.toString(statistic.getPredictions()));
            tv3.setGravity(Gravity.CENTER_HORIZONTAL);

            TextView tv4 = (TextView) tableRow.findViewById(R.id.scores);
            tv4.setText(Integer.toString(statistic.getScores()));
            tv4.setGravity(Gravity.CENTER_HORIZONTAL);

            TextView tv5 = (TextView) tableRow.findViewById(R.id.results);
            tv5.setText(Integer.toString(statistic.getResults()));
            tv5.setGravity(Gravity.CENTER_HORIZONTAL);

            TextView tv6 = (TextView) tableRow.findViewById(R.id.percents);
            tv6.setText(Integer.toString(statistic.getPercent()));
            tv6.setGravity(Gravity.CENTER_HORIZONTAL);

            TextView tv7 = (TextView) tableRow.findViewById(R.id.wins);
            tv7.setText(Integer.toString(statistic.getWins()));
            tv7.setGravity(Gravity.CENTER_HORIZONTAL);

            tableLayout.addView(tableRow);
        }
    }
}
