package biz.ddroid.bets.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import java.util.ArrayList;

import biz.ddroid.bets.BetApplication;
import biz.ddroid.bets.pojo.Match;
import biz.ddroid.bets.rest.PredictServices;
import biz.ddroid.bets.rest.ServicesClient;
import biz.ddroid.bets.utils.SharedPrefs;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link BasePredictionsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public abstract class BasePredictionsFragment extends Fragment {

    protected static final String ARG_BETS_STATUS = "predictions_status";
    private int mPredictionsStatus;
    private OnFragmentInteractionListener mListener;
    protected ArrayList<Match> mMatches = new ArrayList<>();
    protected PredictServices predictServices;
    protected ServicesClient servicesClient;

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
}
