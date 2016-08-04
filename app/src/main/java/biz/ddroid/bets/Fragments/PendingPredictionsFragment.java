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
import biz.ddroid.bets.adapters.NewPredictionsContentAdapter;
import biz.ddroid.bets.adapters.PendingPredictionsContentAdapter;
import biz.ddroid.bets.pojo.Match;
import biz.ddroid.bets.rest.PredictServices;
import biz.ddroid.bets.rest.ServicesClient;
import biz.ddroid.bets.utils.SharedPrefs;
import cz.msebera.android.httpclient.Header;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link PendingPredictionsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link PendingPredictionsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PendingPredictionsFragment extends Fragment {
    private static final String ARG_BETS_STATUS = "bets_status";
    private static final String STATE_MATCHES = "state_pending_predictions";

    public static final int TAB_PENDING = 1;

    private ArrayList<Match> mMatches = new ArrayList<>();

    private int mBetsStatus;

    private OnFragmentInteractionListener mListener;

    private PredictServices predictServices;
    private ServicesClient servicesClient;

    private PendingPredictionsContentAdapter adapter;

    String TAG = "PendingPredictionsFragment";

    public PendingPredictionsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param betsStatus status of bets in the fragment.
     * @return A new instance of fragment PendingPredictionsFragment.
     */
    public static PendingPredictionsFragment newInstance(int betsStatus) {
        PendingPredictionsFragment fragment = new PendingPredictionsFragment();
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
        servicesClient = BetApplication.servicesClient;
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
        adapter = new PendingPredictionsContentAdapter(mBetsStatus);
        adapter.setListener(new PendingPredictionsContentAdapter.Listener() {
            @Override
            public void onClick(int position) {
                onMatchSelected(position);
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
                        Log.v(TAG, "finita");
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
                Match match = new Match(
                        jsonMatch.getInt("mid"),
                        jsonMatch.getString("date"),
                        jsonMatch.getInt("tid"),
                        jsonMatch.getString("tournament_name"),
                        jsonMatch.getString("stage"),
                        jsonMatch.getString("team_home"),
                        jsonMatch.getString("team_visitor"),
                        -1,
                        -1,
                        jsonMatch.getInt("team_home_prediction"),
                        jsonMatch.getInt("team_visitor_prediction"),
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

    private void onMatchSelected(int matchId) {
        if (mListener != null) {
            mListener.onFragmentInteraction(matchId);
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
        // TODO: Update argument type and name
        void onFragmentInteraction(int matchId);
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
