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

import java.util.Date;

import biz.ddroid.bets.R;
import biz.ddroid.bets.adapters.CompletedPredictionsContentAdapter;
import biz.ddroid.bets.listener.EndlessRecyclerOnScrollListener;
import biz.ddroid.bets.pojo.Match;
import biz.ddroid.bets.rest.PredictServices;
import biz.ddroid.bets.rest.ServicesClient;
import biz.ddroid.bets.utils.NetworkUtils;
import cz.msebera.android.httpclient.Header;

public class CompletedPredictionsFragment extends BasePredictionsFragment {

    private CompletedPredictionsContentAdapter adapter;

    private String TAG = CompletedPredictionsFragment.class.getSimpleName();

    public CompletedPredictionsFragment() {
        // Required empty public constructor
    }

    public static CompletedPredictionsFragment newInstance(int betsStatus) {
        CompletedPredictionsFragment fragment = new CompletedPredictionsFragment();
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
        requestDateTime = (TextView) rootView.findViewById(R.id.request_datetime);
        dataInfo = (TextView) rootView.findViewById(R.id.predictions_info);
        recyclerView = (RecyclerView) rootView.findViewById(R.id.my_recycler_view);
        adapter = new CompletedPredictionsContentAdapter(getPredictionsStatus());
        adapter.setListener(new CompletedPredictionsContentAdapter.Listener() {
            @Override
            public void onClick(Match match, int matchStatus) {
                onMatchSelected(match, matchStatus);
            }
        });
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.addOnScrollListener(new EndlessRecyclerOnScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore() {
                requestDateTime.setVisibility(View.GONE);
                dataInfo.setVisibility(View.VISIBLE);
                dataInfo.setText(R.string.loading);
                mCurrentPage++;
                loadNextDataFromApi(mCurrentPage);
            }
        });

        requestTime = new Date();
        if (savedInstanceState != null) {
            mMatches = savedInstanceState.getParcelableArrayList(STATE_MATCHES);
            if (mMatches != null && mMatches.isEmpty()) {
                isRequestEnd = true;
                isResponseEmpty = true;
            }
            requestTime.setTime(savedInstanceState.getLong(STATE_REQUEST_TIME));
            mCurrentPage = savedInstanceState.getInt(STATE_CURRENT_PAGE);
            adapter.setMatches(mMatches);
        } else {
            if (mMatches.isEmpty()) {
                refreshMatches(servicesClient);
            } else {
                adapter.setMatches(mMatches);
            }
        }
        updateUI();
        return rootView;
    }

    public void loadNextDataFromApi(int pageNumber) {
        if (!NetworkUtils.isNetworkConnected(getActivity())) {
            Toast.makeText(getActivity(), R.string.no_internet_connections, Toast.LENGTH_LONG).show();
            onFragmentRefreshed();
        }
        predictServices = new PredictServices(servicesClient);
        JSONObject param = new JSONObject();
        try {
            param.put(PredictServices.PAGE, pageNumber);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        predictServices.completedMatches(param, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                if (responseBody != null) Log.v(TAG, new String(responseBody));
                requestTime = new Date();
                isRequestEnd = true;
                if (responseBody != null) parseMatches(responseBody);
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

    public void refreshMatches(ServicesClient servicesClient) {
        mMatches.clear();
        loadNextDataFromApi(0);
    }

    protected void parseMatches(byte[] responseBody) {
        try {
            JSONArray response = new JSONArray(new String(responseBody));
            for (int i = 0; i < response.length(); i++) {
                JSONObject jsonMatch = response.getJSONObject(i);
                JSONArray friendsPredictionsArray = jsonMatch.getJSONArray(PredictServices.FRIENDS_PREDICTIONS_AND_POINTS);
                String friendPredictionsString = "";
                for (int j = 0; j < friendsPredictionsArray.length(); j++) {
                    if (j > 0) friendPredictionsString += "\n";
                    friendPredictionsString += friendsPredictionsArray.getJSONObject(j).getString(PredictServices.USER_NAME)
                            + ": " + friendsPredictionsArray.getJSONObject(j).getString(PredictServices.TEAM_HOME_PREDICTION)
                            + " : " + friendsPredictionsArray.getJSONObject(j).getString(PredictServices.TEAM_VISITOR_PREDICTION)
                    + "  " + friendsPredictionsArray.getJSONObject(j).getString(PredictServices.POINTS) + " pts";
                }
                Match match = new Match(
                        jsonMatch.getInt(PredictServices.MATCH_ID),
                        jsonMatch.getString(PredictServices.DATE),
                        jsonMatch.getInt(PredictServices.TOURNAMENT_ID),
                        jsonMatch.getString(PredictServices.TOURNAMENT_NAME),
                        jsonMatch.getString(PredictServices.STAGE),
                        jsonMatch.getString(PredictServices.TEAM_HOME),
                        jsonMatch.getString(PredictServices.TEAM_VISITOR),
                        jsonMatch.getInt(PredictServices.TEAM_HOME_SCORE),
                        jsonMatch.getInt(PredictServices.TEAM_VISITOR_SCORE),
                        jsonMatch.getInt(PredictServices.TEAM_HOME_PREDICTION),
                        jsonMatch.getInt(PredictServices.TEAM_VISITOR_PREDICTION),
                        jsonMatch.getString(PredictServices.TEAM_HOME_ICON),
                        jsonMatch.getString(PredictServices.TEAM_VISITOR_ICON),
                        jsonMatch.getString(PredictServices.CITY),
                        friendPredictionsString,
                        jsonMatch.getInt(PredictServices.POINTS));
                mMatches.add(match);
                isResponseEmpty = false;
            }
            adapter.setMatches(mMatches);
            updateUI();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
