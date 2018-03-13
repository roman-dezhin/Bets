package biz.ddroid.bets.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import biz.ddroid.bets.BetApplication;
import biz.ddroid.bets.R;
import biz.ddroid.bets.rest.PredictServices;
import biz.ddroid.bets.rest.ServicesClient;
import biz.ddroid.bets.utils.NetworkUtils;
import biz.ddroid.bets.utils.SharedPrefs;
import cz.msebera.android.httpclient.Header;

import static android.support.v4.graphics.ColorUtils.HSLToColor;

public class ResultsChartFragment extends DialogFragment {
    private static final String ARG_TOURNAMENT_ID = "Tournament Id";
    private static final String ARG_TOURNAMENT_TITLE = "Tournament title";
    private int mTourId;
    private String mTourTitle;
    private LineChart chart;
    private PredictServices predictServices;
    private ServicesClient servicesClient;
    private String TAG = "ResultsChartFragment";
    private ArrayList<Integer> colors;

    public ResultsChartFragment() {
        // Required empty public constructor
    }

    public static ResultsChartFragment newInstance(int param1, String param2) {
        ResultsChartFragment fragment = new ResultsChartFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_TOURNAMENT_ID, param1);
        args.putString(ARG_TOURNAMENT_TITLE, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mTourId = getArguments().getInt(ARG_TOURNAMENT_ID);
            mTourTitle = getArguments().getString(ARG_TOURNAMENT_TITLE);
        }
        servicesClient = BetApplication.getServicesClient();
        servicesClient.setToken(getActivity().getSharedPreferences(SharedPrefs.PREFS_NAME, 0).getString(SharedPrefs.TOKEN, ""));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_results_chart, container, false);
        chart = v.findViewById(R.id.chart);
        getDialog().setTitle(mTourTitle);
        getChartData();
        return v;
    }

    private void getChartData() {
        if (!NetworkUtils.isNetworkConnected(getActivity())) {
            Toast.makeText(getActivity(), R.string.no_internet_connections, Toast.LENGTH_SHORT).show();
        }
        predictServices = new PredictServices(servicesClient);
        JSONObject params = new JSONObject();
        try {
            params.put(PredictServices.CHART_TOURNAMENT_ID, mTourId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        predictServices.chart(params, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                if (responseBody != null) parseResponse(responseBody);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
            }
        });
    }

    private void parseResponse(byte[] responseBody) {
        List<ILineDataSet> dataSets = new ArrayList<>();
        try {
            JSONArray response = new JSONArray(new String(responseBody));
            initColors(response.length());
            for (int i = 0; i < response.length(); i++) {
                JSONObject jsonResult = response.getJSONObject(i);
                String label = jsonResult.getString(PredictServices.CHART_RESULT_LABEL);
                JSONArray matches = jsonResult.getJSONArray(PredictServices.CHART_RESULT_MATCHES);
                List<Entry> entries = new ArrayList<>();
                entries.add(new Entry(matches.getJSONObject(0).getInt(PredictServices.MATCH_ID),
                        matches.getJSONObject(0).getInt(PredictServices.POINTS)));
                for (int j = 1; j < matches.length(); j++) {
                    entries.add(new Entry(matches.getJSONObject(j).getInt(PredictServices.MATCH_ID),
                            matches.getJSONObject(j).getInt(PredictServices.POINTS) + entries.get(j-1).getY()));
                }
                LineDataSet dataSet = new LineDataSet(entries, label);
                dataSet.setColor(colors.get(i));
                dataSet.setValueTextColor(colors.get(i));
                dataSets.add(dataSet);
            }
            LineData lineData = new LineData(dataSets);
            chart.setData(lineData);
            chart.invalidate();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void initColors(int n) {
        colors = new ArrayList<>();
        for(int i = 0; i < 360; i += 360 / n) {
            float[] color = new float[3];
            color[0] = i*1.0f;
            color[1] = 0.98f;
            color[2] = 0.45f;
            colors.add(HSLToColor(color));
        }
    }
}
