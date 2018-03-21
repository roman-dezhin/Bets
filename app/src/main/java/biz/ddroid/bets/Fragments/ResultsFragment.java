package biz.ddroid.bets.fragments;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;

import biz.ddroid.bets.R;
import biz.ddroid.bets.activities.ResultsActivity;
import biz.ddroid.bets.adapters.BaseResultsRecyclerAdapter;
import biz.ddroid.bets.adapters.ResultsInWorldContentAdapter;
import biz.ddroid.bets.adapters.ResultsWithFriendsContentAdapter;
import biz.ddroid.bets.pojo.TournamentResult;
import biz.ddroid.bets.pojo.TournamentResultRow;
import biz.ddroid.bets.rest.PredictServices;
import biz.ddroid.bets.rest.ServicesClient;
import biz.ddroid.bets.utils.NetworkUtils;
import biz.ddroid.bets.utils.SharedPrefs;
import cz.msebera.android.httpclient.Header;

public class ResultsFragment extends BaseResultsFragment {

    private BaseResultsRecyclerAdapter adapter;

    private String TAG = "ResultsFragment";

    public ResultsFragment() {
        // Required empty public constructor
    }

    public static ResultsFragment newInstance(int resultsStatus) {
        ResultsFragment fragment = new ResultsFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_RESULTS_STATUS, resultsStatus);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.v(TAG, "onCreateView: ");
        View rootView = inflater.inflate(R.layout.fragment_results, container, false);
        requestDateTime = rootView.findViewById(R.id.request_datetime);
        dataInfo = rootView.findViewById(R.id.results_info);
        recyclerView = rootView.findViewById(R.id.my_recycler_view);
        if (getResultsStatus() == ResultsActivity.RESULTS_FRIENDS) {
            adapter = new ResultsWithFriendsContentAdapter();
            adapter.setListener(new BaseResultsRecyclerAdapter.ResultsChartListener() {

                @Override
                public void onClick(int tourId, String tourTitle) {
                    onTourSelected(tourId, tourTitle);
                }
            });
        } else if (getResultsStatus() == ResultsActivity.RESULTS_WORLD) {
            adapter = new ResultsInWorldContentAdapter();
        }
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        requestTime = new Date();
        if (savedInstanceState != null) {
            results = savedInstanceState.getParcelableArrayList(STATE_RESULTS);
            if (results != null && results.isEmpty()) {
                isRequestEnd = true;
                isResponseEmpty = true;
            }
            requestTime.setTime(savedInstanceState.getLong(STATE_REQUEST_TIME));
            adapter.setDataSet(results);
        } else {
            if (results.isEmpty()) {
                refreshResults(servicesClient);
            } else {
                adapter.setDataSet(results);
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
            filter.put(PredictServices.TOURNAMENT_FILTER, SharedPrefs.getPref(getContext(), SharedPrefs.TOUR_FILTER));
            filter.put(PredictServices.RESULT_FILTER, getResultsStatus());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        predictServices.results(filter, new AsyncHttpResponseHandler() {
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
        results.clear();
        try {
            JSONArray response = new JSONArray(new String(responseBody));
            for (int i = 0; i < response.length(); i++) {
                JSONObject jsonResult = response.getJSONObject(i);
                JSONArray rows = jsonResult.getJSONArray(PredictServices.RESULT_ROWS);
                ArrayList<TournamentResultRow> resultRows = new ArrayList<>();
                for (int j = 0; j < rows.length(); j++) {
                    resultRows.add(new TournamentResultRow(
                            rows.getJSONObject(j).getString(PredictServices.RESULT_USERNAME),
                            rows.getJSONObject(j).getInt(PredictServices.RESULT_POINTS),
                            rows.getJSONObject(j).getInt(PredictServices.RESULT_PREDICTIONS),
                            rows.getJSONObject(j).getInt(PredictServices.RESULT_SCORES),
                            rows.getJSONObject(j).getInt(PredictServices.RESULT_RESULTS),
                            rows.getJSONObject(j).getInt(PredictServices.RESULT_WINNER)));
                }
                TournamentResult result = new TournamentResult(
                        jsonResult.getInt(PredictServices.TOURNAMENT_ID),
                        jsonResult.getString(PredictServices.TOURNAMENT_NAME),
                        resultRows,
                        jsonResult.getInt(PredictServices.TOURNAMENT_IS_FINISHED));
                results.add(result);
                isResponseEmpty = false;
            }
            adapter.setDataSet(results);
            updateUI();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
