package biz.ddroid.bets.fragments;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import biz.ddroid.bets.BetApplication;
import biz.ddroid.bets.adapters.NewPredictionsContentAdapter;
import biz.ddroid.bets.pojo.Match;
import biz.ddroid.bets.R;
import biz.ddroid.bets.rest.PredictServices;
import biz.ddroid.bets.rest.ServicesClient;
import biz.ddroid.bets.utils.SharedPrefs;
import cz.msebera.android.httpclient.Header;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link NewPredictionsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link NewPredictionsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NewPredictionsFragment extends BasePredictionsFragment {

    private static final String STATE_MATCHES = "state_new_predictions";

    private NewPredictionsContentAdapter adapter;

    private String TAG = "NewPredictionsFragment";

        @Override
        public void onResume() {
            super.onResume();
            Log.v(TAG, "onResume: ");
        }
        @Override
        public void onStart() {
            super.onStart();
            Log.v(TAG, "onStart: ");

        }
        @Override
        public void onPause() {
            super.onPause();
            Log.v(TAG, "onPause: ");

        }
        @Override
        public void onStop() {
            super.onStop();
            Log.v(TAG, "onStop: ");

        }
        @Override
        public void onDestroy() {
            super.onDestroy();
            Log.v(TAG, "onDestroy: ");

        }
        @Override
        public void onActivityCreated(@Nullable Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);
            Log.v(TAG, "onActivityCreated: ");

        }
        @Override
        public void onDestroyView() {
            super.onDestroyView();
            Log.v(TAG, "onDestroyView: ");

        }
        @Override
        public void onConfigurationChanged(Configuration newConfig) {
            super.onConfigurationChanged(newConfig);
            Log.v(TAG, "onConfigurationChanged: ");

        }
        @Override
        public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
            super.onViewStateRestored(savedInstanceState);
            Log.v(TAG, "onViewStateRestored: ");

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
        RecyclerView recyclerView = (RecyclerView) inflater.inflate(
                R.layout.recycler_view, container, false);
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

        if (savedInstanceState != null) {
            mMatches = savedInstanceState.getParcelableArrayList(STATE_MATCHES);
            adapter.setMatches(mMatches);
        } else {
            if (mMatches.isEmpty()) {
                refreshMatches(servicesClient);
            } else {
                adapter.setMatches(mMatches);
            }
        }
        return recyclerView;
    }

    @Override
    public void refreshMatches(ServicesClient servicesClient) {
        predictServices = new PredictServices(servicesClient);
        predictServices.newMatches(new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Log.v(TAG, new String(responseBody));
                parseMatches(responseBody);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Log.v(TAG, new String(responseBody));
            }
        });
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(STATE_MATCHES, mMatches);
    }

    protected void parseMatches(byte[] responseBody) {
        mMatches.clear();
        try {
            JSONArray response = new JSONArray(new String(responseBody));
            for (int i=0; i < response.length(); i++) {
                JSONObject jsonMatch = response.getJSONObject(i);
                Match match = new Match(
                        jsonMatch.getInt("mid"),
                        jsonMatch.getString("date"),
                        jsonMatch.getInt("tid"),
                        jsonMatch.getString("tournament_name"),
                        jsonMatch.getString("stage"),
                        jsonMatch.getString("team_home"),
                        jsonMatch.getString("team_visitor"),
                        jsonMatch.getString("team_home_icon"),
                        jsonMatch.getString("team_visitor_icon"),
                        jsonMatch.getString("city"),
                        jsonMatch.getInt("predictions_count"));
                mMatches.add(match);
            }
            adapter.setMatches(mMatches);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
