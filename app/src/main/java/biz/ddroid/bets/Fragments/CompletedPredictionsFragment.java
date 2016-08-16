package biz.ddroid.bets.fragments;

import android.content.Context;
import android.content.SharedPreferences;
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

import java.util.ArrayList;

import biz.ddroid.bets.BetApplication;
import biz.ddroid.bets.R;
import biz.ddroid.bets.adapters.CompletedPredictionsContentAdapter;
import biz.ddroid.bets.pojo.Match;
import biz.ddroid.bets.rest.PredictServices;
import biz.ddroid.bets.rest.ServicesClient;
import biz.ddroid.bets.utils.SharedPrefs;
import cz.msebera.android.httpclient.Header;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link CompletedPredictionsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link CompletedPredictionsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CompletedPredictionsFragment extends Fragment {
    private static final String ARG_BETS_STATUS = "bets_status";
    private static final String STATE_MATCHES = "state_completed_predictions";

    public static final int TAB_COMPLETED = 2;

    private ArrayList<Match> mMatches = new ArrayList<>();

    private int mBetsStatus;

    private OnFragmentInteractionListener mListener;

    private PredictServices predictServices;

    private CompletedPredictionsContentAdapter adapter;

    private String TAG = "CompletedPredictionsFragment";

    public CompletedPredictionsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param betsStatus status of bets in the fragment.
     * @return A new instance of fragment CompletedPredictionsFragment.
     */
    public static CompletedPredictionsFragment newInstance(int betsStatus) {
        CompletedPredictionsFragment fragment = new CompletedPredictionsFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_BETS_STATUS, betsStatus);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mBetsStatus = getArguments().getInt(ARG_BETS_STATUS);
        }
        ServicesClient servicesClient = BetApplication.servicesClient;
        SharedPreferences settings = getActivity().getSharedPreferences(SharedPrefs.PREFS_NAME, 0);
        servicesClient.setToken(settings.getString(SharedPrefs.TOKEN, ""));
        predictServices = new PredictServices(servicesClient);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.v(TAG, "onCreateView: ");
        RecyclerView recyclerView = (RecyclerView) inflater.inflate(
                R.layout.recycler_view, container, false);
        adapter = new CompletedPredictionsContentAdapter(mBetsStatus);
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

    private void parseMatches(byte[] responseBody) {
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

    private void onMatchSelected(Match match) {
        if (mListener != null) {
            mListener.onFragmentInteraction(match);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Match match);
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.v(TAG, "onStart");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.v(TAG, "onResume");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.v(TAG, "onPause");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.v(TAG, "onStop");
    }
}
