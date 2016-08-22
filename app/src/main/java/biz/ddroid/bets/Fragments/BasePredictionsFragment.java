package biz.ddroid.bets.fragments;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;

import biz.ddroid.bets.BetApplication;
import biz.ddroid.bets.R;
import biz.ddroid.bets.pojo.Match;
import biz.ddroid.bets.rest.PredictServices;
import biz.ddroid.bets.rest.ServicesClient;
import biz.ddroid.bets.utils.SharedPrefs;

public abstract class BasePredictionsFragment extends Fragment {

    protected static final String ARG_BETS_STATUS = "predictions_status";
    protected static final String STATE_MATCHES = "state_matches";
    protected static final String STATE_REQUEST_TIME = "state_request_time";

    private int mPredictionsStatus;
    private OnFragmentInteractionListener mListener;
    private OnFragmentRefreshListener mFragmentRefreshListener;
    protected ArrayList<Match> mMatches = new ArrayList<>();
    protected PredictServices predictServices;
    protected ServicesClient servicesClient;
    protected Date requestTime;
    protected RecyclerView recyclerView;
    protected TextView requestDateTime;
    protected TextView dataInfo;
    protected boolean isResponseEmpty = true;
    protected boolean isRequestEnd = false;
    private String TAG = "BasePredictionsFragment";

    public BasePredictionsFragment() {
        // Required empty public constructor
    }

    abstract protected void parseMatches(byte[] responseBody);

    abstract public void refreshMatches(ServicesClient servicesClient);

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mPredictionsStatus = getArguments().getInt(ARG_BETS_STATUS);
        }
        servicesClient = BetApplication.getServicesClient();
        servicesClient.setToken(getActivity().getSharedPreferences(SharedPrefs.PREFS_NAME, 0).getString(SharedPrefs.TOKEN, ""));
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(STATE_MATCHES, mMatches);
        outState.putLong(STATE_REQUEST_TIME, requestTime.getTime());
    }

    protected void updateUI() {
        if (mMatches.size() == 0 && (!isRequestEnd || isRequestEnd && !isResponseEmpty)) {
            dataInfo.setVisibility(View.VISIBLE);
            requestDateTime.setVisibility(View.GONE);
            recyclerView.setVisibility(View.GONE);
        } else if (mMatches.size() == 0 && isRequestEnd && isResponseEmpty) {
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

    public void onMatchSelected(Match match, int matchStatus) {
        if (mListener != null) {
            mListener.onFragmentInteraction(match, matchStatus);
        }
    }

    public void onFragmentRefreshed() {
        if (mFragmentRefreshListener != null) {
            mFragmentRefreshListener.onFragmentRefreshed();
        }
    }

    public int getPredictionsStatus() {
        return mPredictionsStatus;
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
        if (context instanceof OnFragmentRefreshListener) {
            mFragmentRefreshListener = (OnFragmentRefreshListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentRefreshListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Match match, int matchStatus);
    }

    public interface OnFragmentRefreshListener {
        void onFragmentRefreshed();
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.v(TAG, "onResume: " + this.toString());
    }
    @Override
    public void onStart() {
        super.onStart();
        Log.v(TAG, "onStart: " + this.toString());

    }
    @Override
    public void onPause() {
        super.onPause();
        Log.v(TAG, "onPause: " + this.toString());

    }
    @Override
    public void onStop() {
        super.onStop();
        Log.v(TAG, "onStop: " + this.toString());

    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.v(TAG, "onDestroy: " + this.toString());

    }
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.v(TAG, "onActivityCreated: " + this.toString());

    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.v(TAG, "onDestroyView: " + this.toString());

    }
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Log.v(TAG, "onConfigurationChanged: " + this.toString());

    }
    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        Log.v(TAG, "onViewStateRestored: " + this.toString());

    }
}
