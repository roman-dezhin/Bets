package biz.ddroid.bets.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import biz.ddroid.bets.R;
import biz.ddroid.bets.pojo.Match;

public class CreatePredictionFragment extends DialogFragment {
    private static final String ARG_MATCH = "match";

    private Match mMatch;

    private OnFragmentInteractionListener mListener;

    public CreatePredictionFragment() {
        // Required empty public constructor
    }

    public static CreatePredictionFragment newInstance(Match match) {
        CreatePredictionFragment fragment = new CreatePredictionFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_MATCH, match);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mMatch = getArguments().getParcelable(ARG_MATCH);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_create_prediction, container, false);

        TextView textView = (TextView) v.findViewById(R.id.textView2);
        textView.setText(mMatch.getTeam1() + " - " + mMatch.getTeam2());

        final NumberPicker numberPicker_home_team = (NumberPicker) v.findViewById(R.id.numberPicker_home_team);
        numberPicker_home_team.setMaxValue(10);
        numberPicker_home_team.setMinValue(0);
        numberPicker_home_team.setWrapSelectorWheel(false);

        final NumberPicker numberPicker_visitor_team = (NumberPicker) v.findViewById(R.id.numberPicker_visitor_team);
        numberPicker_visitor_team.setMaxValue(10);
        numberPicker_visitor_team.setMinValue(0);
        numberPicker_visitor_team.setWrapSelectorWheel(false);

        Button button = (Button) v.findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onPredictionButtonPressed(mMatch.getId(), numberPicker_home_team.getValue(), numberPicker_visitor_team.getValue());

            }
        });

        return v;
    }

    public void onPredictionButtonPressed(int matchId, int team_home_prediction, int team_visitor_prediction) {
        if (mListener != null) {
            mListener.onFragmentInteraction(matchId, team_home_prediction, team_visitor_prediction);
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
        void onFragmentInteraction(int matchId, int team_home_prediction, int team_visitor_prediction);
    }
}
