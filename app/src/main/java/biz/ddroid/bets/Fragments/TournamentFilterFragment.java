package biz.ddroid.bets.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;

import biz.ddroid.bets.R;
import biz.ddroid.bets.rest.PredictServices;
import biz.ddroid.bets.utils.SharedPrefs;

public class TournamentFilterFragment extends DialogFragment {
    private static final String ARG_FILTER = "filter";

    private int mFilter;

    private OnFragmentInteractionListener mListener;

    public TournamentFilterFragment() {
        // Required empty public constructor
    }

    public static TournamentFilterFragment newInstance(int filter) {
        TournamentFilterFragment fragment = new TournamentFilterFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_FILTER, filter);

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mFilter = getArguments().getInt(ARG_FILTER);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_tournament_filter, container, false);
        final RadioButton radioButtonActive = v.findViewById(R.id.radioActive);
        final RadioButton radioButtonAll = v.findViewById(R.id.radioAll);
        switch (mFilter) {
            case PredictServices.TOURNAMENT_FILTER_ACTIVE:
                radioButtonActive.setChecked(true);
                radioButtonAll.setChecked(false);
                break;
            case PredictServices.TOURNAMENT_FILTER_ALL:
                radioButtonActive.setChecked(false);
                radioButtonAll.setChecked(true);
                break;
        }
        radioButtonActive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean checked = ((RadioButton) v).isChecked();
                if (checked)
                    mFilter = PredictServices.TOURNAMENT_FILTER_ACTIVE;
            }
        });
        radioButtonAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean checked = ((RadioButton) v).isChecked();
                if (checked)
                    mFilter = PredictServices.TOURNAMENT_FILTER_ALL;
            }
        });
        Button save_button = v.findViewById(R.id.tour_filter_save_button);
        save_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onButtonPressed(mFilter);
            }
        });
        return v;
    }

    public void onButtonPressed(int filter) {
        if (mListener != null) {
            mListener.onFragmentInteraction(filter);
            dismiss();
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

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(int filter);
    }
}
