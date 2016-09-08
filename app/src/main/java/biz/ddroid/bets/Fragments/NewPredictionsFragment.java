package biz.ddroid.bets.fragments;

import android.content.Context;
import android.net.ConnectivityManager;
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
import com.loopj.android.http.PersistentCookieStore;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

import biz.ddroid.bets.BetApplication;
import biz.ddroid.bets.adapters.NewPredictionsContentAdapter;
import biz.ddroid.bets.pojo.Match;
import biz.ddroid.bets.R;
import biz.ddroid.bets.rest.PredictServices;
import biz.ddroid.bets.rest.ServicesClient;
import biz.ddroid.bets.rest.SystemServices;
import biz.ddroid.bets.utils.NetworkUtils;
import cz.msebera.android.httpclient.Header;

public class NewPredictionsFragment extends BasePredictionsFragment {

    private NewPredictionsContentAdapter adapter;

    private static final String TAG = "NewPredictionsFragment";

    public NewPredictionsFragment() {
        // Required empty public constructor
    }

    public static NewPredictionsFragment newInstance(int betsStatus) {
        NewPredictionsFragment fragment = new NewPredictionsFragment();
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
        adapter = new NewPredictionsContentAdapter(getPredictionsStatus());
        adapter.setListener(new NewPredictionsContentAdapter.Listener() {
            @Override
            public void onClick(Match match, int matchStatus) {
                onMatchSelected(match, matchStatus);
            }
        });
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        requestTime = new Date();
        if (savedInstanceState != null) {
            mMatches = savedInstanceState.getParcelableArrayList(STATE_MATCHES);
            if (mMatches != null && mMatches.isEmpty()) {
                isRequestEnd = true;
                isResponseEmpty = true;
            }
            requestTime.setTime(savedInstanceState.getLong(STATE_REQUEST_TIME));
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

    public void refreshMatches(ServicesClient servicesClient) {
        if (!NetworkUtils.isNetworkConnected(getActivity())) {
            Toast.makeText(getActivity(), R.string.no_internet_connections, Toast.LENGTH_SHORT).show();
            onFragmentRefreshed();
        }
        SystemServices systemServices = new SystemServices(servicesClient);
        systemServices.connect(new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                if (responseBody != null) Log.v(TAG, "ConnectOk:" + new String(responseBody));

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                if (responseBody != null) Log.v(TAG, "ConnectBad:" + new String(responseBody));

            }
        });
        Log.v(TAG, "Cookies: " + String.valueOf(new PersistentCookieStore(getContext()).getCookies()));
        predictServices = new PredictServices(servicesClient);
        predictServices.newMatches(new AsyncHttpResponseHandler() {
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

    protected void parseMatches(byte[] responseBody) {
        mMatches.clear();
        try {
            JSONArray response = new JSONArray(new String(responseBody));
            for (int i=0; i < response.length(); i++) {
                JSONObject jsonMatch = response.getJSONObject(i);
                Match match = new Match(
                        jsonMatch.getInt(PredictServices.MATCH_ID),
                        jsonMatch.getString(PredictServices.DATE),
                        jsonMatch.getInt(PredictServices.TOURNAMENT_ID),
                        jsonMatch.getString(PredictServices.TOURNAMENT_NAME),
                        jsonMatch.getString(PredictServices.STAGE),
                        jsonMatch.getString(PredictServices.TEAM_HOME),
                        jsonMatch.getString(PredictServices.TEAM_VISITOR),
                        jsonMatch.getString(PredictServices.TEAM_HOME_ICON),
                        jsonMatch.getString(PredictServices.TEAM_VISITOR_ICON),
                        jsonMatch.getString(PredictServices.CITY),
                        jsonMatch.getInt(PredictServices.PREDICTIONS_COUNT));
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
