package biz.ddroid.bets.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.util.Date;

import biz.ddroid.bets.R;
import biz.ddroid.bets.adapters.PendingPredictionsContentAdapter;
import biz.ddroid.bets.pojo.Match;
import biz.ddroid.bets.rest.PredictServices;
import biz.ddroid.bets.rest.ServicesClient;
import cz.msebera.android.httpclient.Header;

public class PendingPredictionsFragment extends BasePredictionsFragment {

    private static final String STATE_MATCHES = "state_matches";
    private static final String STATE_REQUEST_TIME = "state_request_time";

    private PendingPredictionsContentAdapter adapter;
    private RecyclerView recyclerView;
    private TextView requestDateTime;
    private TextView dataInfo;
    private Date requestTime;
    private boolean isResponseEmpty = true;
    private boolean isRequestEnd = false;

    private String TAG = "PendingPredictionsFragment";

    public PendingPredictionsFragment() {
        // Required empty public constructor
    }

    public static PendingPredictionsFragment newInstance(int betsStatus) {
        PendingPredictionsFragment fragment = new PendingPredictionsFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_BETS_STATUS, betsStatus);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.v(TAG, "onCreateView: ");
        View rootView = inflater.inflate(R.layout.fragment_predictions, container, false);
        rootView.setTag(TAG);
        requestDateTime = (TextView) rootView.findViewById(R.id.request_datetime);
        dataInfo = (TextView) rootView.findViewById(R.id.new_predictions_info);
        recyclerView = (RecyclerView) rootView.findViewById(R.id.my_recycler_view);
        adapter = new PendingPredictionsContentAdapter(getPredictionsStatus());
        adapter.setListener(new PendingPredictionsContentAdapter.Listener() {
            @Override
            public void onClick(Match match, int matchStatus) {
                onMatchSelected(match, matchStatus);
            }
        });
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        if (savedInstanceState != null) {
            mMatches = savedInstanceState.getParcelableArrayList(STATE_MATCHES);
            if (mMatches.isEmpty()) {
                isRequestEnd = true;
                isResponseEmpty = true;
            }
            requestTime = new Date();
            requestTime.setTime(savedInstanceState.getLong(STATE_REQUEST_TIME));
            adapter.setMatches(mMatches);
        } else {
            requestTime = new Date();
            if (mMatches.isEmpty()) {
                refreshMatches(servicesClient);
            } else {
                adapter.setMatches(mMatches);
            }
        }
        updateUI();
        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(STATE_MATCHES, mMatches);
        outState.putLong(STATE_REQUEST_TIME, requestTime.getTime());
    }

    @Override
    public void refreshMatches(ServicesClient servicesClient) {
        predictServices = new PredictServices(servicesClient);
        predictServices.pending(new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Log.v(TAG, new String(responseBody));
                requestTime = new Date();
                isRequestEnd = true;
                parseMatches(responseBody);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Log.v(TAG, new String(responseBody));
            }
        });
    }

    protected void parseMatches(byte[] responseBody) {
        mMatches.clear();
        try {
            JSONArray response = new JSONArray(new String(responseBody));
            for (int i = 0; i < response.length(); i++) {
                JSONObject jsonMatch = response.getJSONObject(i);
                JSONArray friendsPredictionsArray = jsonMatch.getJSONArray(PredictServices.FRIENDS_PREDICTIONS);
                String friendPredictionsString = "";
                for (int j = 0; j < friendsPredictionsArray.length(); j++) {
                    friendPredictionsString += "\n" + friendsPredictionsArray.getJSONObject(j).getString(PredictServices.USER_NAME)
                    + ": " + friendsPredictionsArray.getJSONObject(j).getString(PredictServices.TEAM_HOME_PREDICTION)
                            + " : " + friendsPredictionsArray.getJSONObject(j).getString(PredictServices.TEAM_VISITOR_PREDICTION);
                }
                Match match = new Match(
                        jsonMatch.getInt(PredictServices.MATCH_ID),
                        jsonMatch.getString(PredictServices.DATE),
                        jsonMatch.getInt(PredictServices.TOURNAMENT_ID),
                        jsonMatch.getString(PredictServices.TOURNAMENT_NAME),
                        jsonMatch.getString(PredictServices.STAGE),
                        jsonMatch.getString(PredictServices.TEAM_HOME),
                        jsonMatch.getString(PredictServices.TEAM_VISITOR),
                        jsonMatch.getInt(PredictServices.TEAM_HOME_PREDICTION),
                        jsonMatch.getInt(PredictServices.TEAM_VISITOR_PREDICTION),
                        jsonMatch.getString(PredictServices.TEAM_HOME_ICON),
                        jsonMatch.getString(PredictServices.TEAM_VISITOR_ICON),
                        jsonMatch.getString(PredictServices.CITY),
                       friendPredictionsString);
                mMatches.add(match);
                isResponseEmpty = false;
            }
            adapter.setMatches(mMatches);
            updateUI();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void updateUI() {
        if (adapter.getItemCount() == 0 && (!isRequestEnd || isRequestEnd && !isResponseEmpty)) {
            dataInfo.setVisibility(View.VISIBLE);
            requestDateTime.setVisibility(View.GONE);
            recyclerView.setVisibility(View.GONE);
        } else if (adapter.getItemCount() == 0 && isRequestEnd && isResponseEmpty) {
            dataInfo.setVisibility(View.VISIBLE);
            dataInfo.setText(R.string.no_data);
            requestDateTime.setVisibility(View.GONE);
            recyclerView.setVisibility(View.GONE);
        }
        else {
            dataInfo.setVisibility(View.GONE);
            requestDateTime.setVisibility(View.VISIBLE);
            requestDateTime.setText(DateFormat.getTimeInstance().format(requestTime));
            recyclerView.setVisibility(View.VISIBLE);
        }
    }
}
