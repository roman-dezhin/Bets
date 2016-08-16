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
import biz.ddroid.bets.adapters.PendingPredictionsContentAdapter;
import biz.ddroid.bets.pojo.Match;
import cz.msebera.android.httpclient.Header;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link PendingPredictionsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link PendingPredictionsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PendingPredictionsFragment extends BasePredictionsFragment {
    private static final String STATE_MATCHES = "state_pending_predictions";

    private PendingPredictionsContentAdapter adapter;

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
        RecyclerView recyclerView = (RecyclerView) inflater.inflate(
                R.layout.recycler_view, container, false);
        adapter = new PendingPredictionsContentAdapter(getmPredictionsStatus());
        adapter.setListener(new PendingPredictionsContentAdapter.Listener() {
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
                predictServices.pending(new AsyncHttpResponseHandler() {
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
            for (int i = 0; i < response.length(); i++) {
                JSONObject jsonMatch = response.getJSONObject(i);
                JSONArray friendsPredictionsArray = jsonMatch.getJSONArray("friends_predictions");
                String friendPredictionsString = "";
                for (int j = 0; j < friendsPredictionsArray.length(); j++) {
                    friendPredictionsString += "\n" + friendsPredictionsArray.getJSONObject(j).getString("user_name")
                    + ": " + friendsPredictionsArray.getJSONObject(j).getString("team_home_score")
                            + " : " + friendsPredictionsArray.getJSONObject(j).getString("team_visitor_score");
                }
                Match match = new Match(
                        jsonMatch.getInt("mid"),
                        jsonMatch.getString("date"),
                        jsonMatch.getInt("tid"),
                        jsonMatch.getString("tournament_name"),
                        jsonMatch.getString("stage"),
                        jsonMatch.getString("team_home"),
                        jsonMatch.getString("team_visitor"),
                        jsonMatch.getInt("team_home_prediction"),
                        jsonMatch.getInt("team_visitor_prediction"),
                        jsonMatch.getString("team_home_icon"),
                        jsonMatch.getString("team_visitor_icon"),
                        jsonMatch.getString("city"),
                       friendPredictionsString);
                mMatches.add(match);
            }
            adapter.setMatches(mMatches);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
