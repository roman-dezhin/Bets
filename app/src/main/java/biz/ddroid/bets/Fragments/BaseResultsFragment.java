package biz.ddroid.bets.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;

import biz.ddroid.bets.BetApplication;
import biz.ddroid.bets.R;
import biz.ddroid.bets.listener.OnFragmentRefresh;
import biz.ddroid.bets.pojo.Match;
import biz.ddroid.bets.pojo.TournamentResult;
import biz.ddroid.bets.rest.PredictServices;
import biz.ddroid.bets.rest.ServicesClient;
import biz.ddroid.bets.utils.SharedPrefs;

public abstract class BaseResultsFragment extends Fragment {

    protected static final String ARG_RESULTS_STATUS = "results_status";
    protected static final String STATE_RESULTS = "state_results";
    protected static final String STATE_REQUEST_TIME = "state_request_time";

    private int mResultsStatus;
    private OnFragmentInteractionListener mListener;
    private OnFragmentRefresh mFragmentRefreshListener;
    protected ArrayList<TournamentResult> results = new ArrayList<>();
    protected PredictServices predictServices;
    protected ServicesClient servicesClient;
    protected Date requestTime;
    protected RecyclerView recyclerView;
    protected TextView requestDateTime;
    protected TextView dataInfo;
    protected boolean isResponseEmpty = true;
    protected boolean isRequestEnd = false;
    private String TAG = "BaseResultsFragment";

    public BaseResultsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mResultsStatus = getArguments().getInt(ARG_RESULTS_STATUS);
        }
        servicesClient = BetApplication.getServicesClient();
        servicesClient.setToken(getActivity().getSharedPreferences(SharedPrefs.PREFS_NAME, 0).getString(SharedPrefs.TOKEN, ""));
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(STATE_RESULTS, results);
        outState.putLong(STATE_REQUEST_TIME, requestTime.getTime());
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof BaseResultsFragment.OnFragmentInteractionListener) {
            mListener = (BaseResultsFragment.OnFragmentInteractionListener) context;
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

    public void onTourSelected(int toutId, String tourTitle) {
        if (mListener != null) {
            mListener.onFragmentInteraction(toutId, tourTitle);
        }
    }

    public void onFragmentRefreshed() {
        if (mFragmentRefreshListener != null) mFragmentRefreshListener.onFragmentRefreshed();
    }

    public int getResultsStatus() {
        return mResultsStatus;
    }

    protected void updateUI() {
        if (results.size() == 0 && (!isRequestEnd || isRequestEnd && !isResponseEmpty)) {
            dataInfo.setVisibility(View.VISIBLE);
            requestDateTime.setVisibility(View.GONE);
            recyclerView.setVisibility(View.GONE);
        } else if (results.size() == 0 && isRequestEnd && isResponseEmpty) {
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

    abstract public void refreshResults(ServicesClient servicesClient);

    abstract protected void parseResponse(byte[] responseBody);

    public interface OnFragmentInteractionListener {

        void onFragmentInteraction(int tourId, String tourTitle);
    }
}
