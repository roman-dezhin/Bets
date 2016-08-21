package biz.ddroid.bets.fragments;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;

import java.util.ArrayList;

import biz.ddroid.bets.BetApplication;
import biz.ddroid.bets.pojo.Match;
import biz.ddroid.bets.rest.PredictServices;
import biz.ddroid.bets.rest.ServicesClient;
import biz.ddroid.bets.utils.SharedPrefs;

public abstract class BasePredictionsFragment extends Fragment {

    protected static final String ARG_BETS_STATUS = "predictions_status";

    private int mPredictionsStatus;
    private OnFragmentInteractionListener mListener;
    protected ArrayList<Match> mMatches = new ArrayList<>();
    protected PredictServices predictServices;
    protected ServicesClient servicesClient;
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

    public void onMatchSelected(Match match, int matchStatus) {
        if (mListener != null) {
            mListener.onFragmentInteraction(match, matchStatus);
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
        void onFragmentInteraction(Match match, int matchStatus);
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
