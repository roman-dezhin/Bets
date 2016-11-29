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
import biz.ddroid.bets.listener.OnFragmentRefresh;
import biz.ddroid.bets.pojo.Match;
import biz.ddroid.bets.rest.PredictServices;
import biz.ddroid.bets.rest.ServicesClient;
import biz.ddroid.bets.utils.SharedPrefs;

public abstract class BasePredictionsFragment extends Fragment {

    protected static final String ARG_BETS_STATUS = "predictions_status";
    protected static final String STATE_MATCHES = "state_matches";
    protected static final String STATE_REQUEST_TIME = "state_request_time";
    protected static final String STATE_CURRENT_PAGE = "state_current_page";

    private int mPredictionsStatus;
    private OnFragmentInteractionListener mListener;
    private OnFragmentRefresh mFragmentRefreshListener;
    protected ArrayList<Match> mMatches = new ArrayList<>();
    protected PredictServices predictServices;
    protected ServicesClient servicesClient;
    protected Date requestTime;
    protected RecyclerView recyclerView;
    protected TextView requestDateTime;
    protected TextView dataInfo;
    protected boolean isResponseEmpty = true;
    protected boolean isRequestEnd = false;
    protected int mCurrentPage = 0;
    private String TAG = BasePredictionsFragment.class.getSimpleName();

    public BasePredictionsFragment() {
        // Required empty public constructor
    }

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
        outState.putInt(STATE_CURRENT_PAGE, mCurrentPage);
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
        if (context instanceof OnFragmentRefresh) {
            mFragmentRefreshListener = (OnFragmentRefresh) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentRefreshListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
        mFragmentRefreshListener = null;
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

    abstract public void refreshMatches(ServicesClient servicesClient);

    abstract protected void parseMatches(byte[] responseBody);

    public interface OnFragmentInteractionListener {

        void onFragmentInteraction(Match match, int matchStatus);
    }
}
