package biz.ddroid.bets.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import biz.ddroid.bets.R;

public class AccountProfileEditFragment extends DialogFragment {
    private static final String ARG_NAME = "name";
    private static final String ARG_MAIL = "mail";
    private OnFragmentInteractionListener mListener;
    private String userName, userEmail;

    public AccountProfileEditFragment() {
        // Required empty public constructor
    }

    public static AccountProfileEditFragment newInstance(String name, String mail) {
        AccountProfileEditFragment fragment = new AccountProfileEditFragment();
        Bundle args = new Bundle();
        args.putString(ARG_NAME, name);
        args.putString(ARG_MAIL, mail);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            userName = getArguments().getString(ARG_NAME);
            userEmail = getArguments().getString(ARG_MAIL);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_account_profile_edit, container, false);

        final EditText name = (EditText) v.findViewById(R.id.account_edit_username);
        name.setText(userName);
        final EditText mail = (EditText) v.findViewById(R.id.account_edit_email);
        mail.setText(userEmail);
        final EditText password = (EditText) v.findViewById(R.id.account_edit_password);
        Button save_button = (Button) v.findViewById(R.id.account_edit_save_button);
        save_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onButtonPressed(name.getText().toString(), mail.getText().toString(), password.getText().toString());
            }
        });
        return v;
    }

    public void onButtonPressed(String name, String email, String pass) {
        if (mListener != null) {
            mListener.onFragmentInteraction(name, email, pass);
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
        void onFragmentInteraction(String name, String email, String pass);
    }
}
