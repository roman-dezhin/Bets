package biz.ddroid.bets.fragments;

import android.os.Bundle;
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


import biz.ddroid.bets.R;
import biz.ddroid.bets.adapters.CompletedPredictionsContentAdapter;
import biz.ddroid.bets.pojo.Match;
import cz.msebera.android.httpclient.Header;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link CompletedPredictionsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link CompletedPredictionsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CompletedPredictionsFragment extends BasePredictionsFragment {
    private static final String STATE_MATCHES = "state_completed_predictions";

    private CompletedPredictionsContentAdapter adapter;

    private String TAG = "CompletedPredictionsFragment";

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
        RecyclerView recyclerView = (RecyclerView) inflater.inflate(
                R.layout.recycler_view, container, false);
        adapter = new CompletedPredictionsContentAdapter(getmPredictionsStatus());
        adapter.setListener(new CompletedPredictionsContentAdapter.Listener() {
            @Override
            public void onClick(Match match) {
                onMatchSelected(match);
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
                predictServices.completed(new AsyncHttpResponseHandler() {
                    @Override
                    public void onFinish() {
                        Log.v(TAG, "onFinish");
                    }

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
            } else {
                adapter.setMatches(mMatches);
            }
        }
        return recyclerView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(STATE_MATCHES, mMatches);
    }

    protected void parseMatches(byte[] responseBody) {
        try {
            JSONArray response = new JSONArray(new String(responseBody));
            for (int i=0; i < response.length(); i++) {
                JSONObject jsonMatch = response.getJSONObject(i);
                JSONArray friendsPredictionsArray = jsonMatch.getJSONArray("friends_predictions_and_points");
                String friendPredictionsString = "";
                for (int j = 0; j < friendsPredictionsArray.length(); j++) {
                    friendPredictionsString += "\n" + friendsPredictionsArray.getJSONObject(j).getString("user_name")
                            + ": " + friendsPredictionsArray.getJSONObject(j).getString("team_home_score")
                            + " : " + friendsPredictionsArray.getJSONObject(j).getString("team_visitor_score")
                    + "  " + friendsPredictionsArray.getJSONObject(j).getString("points") + "pts.";
                }
                Match match = new Match(
                        jsonMatch.getInt("mid"),
                        jsonMatch.getString("date"),
                        jsonMatch.getInt("tid"),
                        jsonMatch.getString("tournament_name"),
                        jsonMatch.getString("stage"),
                        jsonMatch.getString("team_home"),
                        jsonMatch.getString("team_visitor"),
                        jsonMatch.getInt("team_home_score"),
                        jsonMatch.getInt("team_visitor_score"),
                        jsonMatch.getInt("team_home_prediction"),
                        jsonMatch.getInt("team_visitor_prediction"),
                        jsonMatch.getString("team_home_icon"),
                        jsonMatch.getString("team_visitor_icon"),
                        jsonMatch.getString("city"),
                        friendPredictionsString,
                        jsonMatch.getInt("points"));
                mMatches.add(match);
            }
            adapter.setMatches(mMatches);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
