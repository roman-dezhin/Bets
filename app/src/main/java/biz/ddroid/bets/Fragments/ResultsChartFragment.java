package biz.ddroid.bets.fragments;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import biz.ddroid.bets.BetApplication;
import biz.ddroid.bets.R;
import biz.ddroid.bets.rest.PredictServices;
import biz.ddroid.bets.rest.ServicesClient;
import biz.ddroid.bets.utils.NetworkUtils;
import biz.ddroid.bets.utils.SharedPrefs;
import cz.msebera.android.httpclient.Header;

import static android.support.v4.graphics.ColorUtils.HSLToColor;

public class ResultsChartFragment extends DialogFragment implements OnChartValueSelectedListener {
    private static final String ARG_TOURNAMENT_ID = "Tournament Id";
    private static final String ARG_TOURNAMENT_TITLE = "Tournament title";
    private int mTourId;
    private String mTourTitle;
    private LineChart chart;
    private PredictServices predictServices;
    private ServicesClient servicesClient;
    private String TAG = "ResultsChartFragment";
    private ArrayList<Integer> colors;
    private Map<Float, String> matchDateMap = new HashMap<>();
    private Map<String, ArrayList<Match>> chartData = new HashMap<>();
    private Set<Integer> matchIdsSet = new HashSet<>();
    private ArrayList<String> labels = new ArrayList<>();
    private ArrayList<Integer> matchIdsArrayList = new ArrayList<>();
    private TextView matchTitleTextView;
    private TextView scoreTextView;
    private TextView predictionTextView;

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
        matchTitleTextView = v.findViewById(R.id.chart_match_title);
        scoreTextView = v.findViewById(R.id.chart_score);
        predictionTextView = v.findViewById(R.id.chart_prediction);
        getDialog().setTitle(mTourTitle);
        getChartData();
        return v;
    }

    @Override
    public void onStart()
    {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null)
        {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            dialog.getWindow().setLayout(width, height);
        }
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
        try {
            JSONArray response = new JSONArray(new String(responseBody));
            for (int i = 0; i < response.length(); i++) {
                JSONObject jsonResult = response.getJSONObject(i);
                String label = jsonResult.getString(PredictServices.CHART_RESULT_LABEL);
                JSONArray matches = jsonResult.getJSONArray(PredictServices.CHART_RESULT_MATCHES);
                ArrayList<Match> matchArrayList = new ArrayList<>();
                for (int j = 0; j < matches.length(); j++) {
                    matchArrayList.add(new Match(matches.getJSONObject(j).getInt(PredictServices.MATCH_ID),
                            matches.getJSONObject(j).getString(PredictServices.DATE),
                            matches.getJSONObject(j).getString(PredictServices.TEAM_HOME),
                            matches.getJSONObject(j).getString(PredictServices.TEAM_VISITOR),
                            matches.getJSONObject(j).getInt(PredictServices.TEAM_HOME_SCORE),
                            matches.getJSONObject(j).getInt(PredictServices.TEAM_VISITOR_SCORE),
                            matches.getJSONObject(j).getInt(PredictServices.TEAM_HOME_PREDICTION),
                            matches.getJSONObject(j).getInt(PredictServices.TEAM_VISITOR_PREDICTION),
                            matches.getJSONObject(j).getInt(PredictServices.POINTS)));
                    matchIdsSet.add(matches.getJSONObject(j).getInt(PredictServices.MATCH_ID));
                }
                chartData.put(label, matchArrayList);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        matchIdsArrayList.addAll(matchIdsSet);
        Collections.sort(matchIdsArrayList);
        initChart();
    }

    private void initChart() {
        List<ILineDataSet> dataSets = new ArrayList<>();
        initColors(chartData.size());
        int i = 0;
        for (Map.Entry<String, ArrayList<Match>> mapEntry : chartData.entrySet()) {
            labels.add(mapEntry.getKey());
            List<Entry> entries = new ArrayList<>();
            entries.add(new Entry(matchIdsArrayList.indexOf(mapEntry.getValue().get(0).getId()),
                    mapEntry.getValue().get(0).getPoints()));
            matchDateMap.put(entries.get(0).getX(), mapEntry.getValue().get(0).getDate());
            for (int j = 1; j < mapEntry.getValue().size(); j++) {
                entries.add(new Entry(matchIdsArrayList.indexOf(mapEntry.getValue().get(j).getId()),
                        mapEntry.getValue().get(j).getPoints() + entries.get(j - 1).getY()));
                matchDateMap.put(entries.get(j).getX(), mapEntry.getValue().get(j).getDate());
            }
            LineDataSet dataSet = new LineDataSet(entries, labels.get(i));
            dataSet.setColor(colors.get(i));
            dataSet.setValueTextColor(colors.get(i));
            dataSet.setHighlightEnabled(true);
            dataSets.add(dataSet);
            i++;
        }
        LineData lineData = new LineData(dataSets);
        chart.setData(lineData);
        chart.setHighlightPerTapEnabled(true);
        chart.setOnChartValueSelectedListener(this);
        chartStyling();
    }

    private void initColors(int n) {
        colors = new ArrayList<>();
        if (n == 0) {
            return;
        }
        for(int i = 0; i < 360; i += 360 / n) {
            float[] color = new float[3];
            color[0] = i*1.0f;
            color[1] = 0.98f;
            color[2] = 0.45f;
            colors.add(HSLToColor(color));
        }
    }

    private void chartStyling() {
        Description description = new Description();
        description.setText("");
        chart.setDescription(description);

        IAxisValueFormatter formatter = new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                String date = matchDateMap.get(value);
                if (date != null)
                    return date.substring(6, 11);
                else
                    return "";
            }
        };
        chart.getXAxis().setValueFormatter(formatter);
        chart.getXAxis().setGranularity(1f);

        chart.setVisibleXRangeMaximum(30);
        chart.setVisibleYRangeMaximum(100, YAxis.AxisDependency.RIGHT);
        chart.moveViewTo(chart.getXChartMax(), chart.getYChartMax(), YAxis.AxisDependency.RIGHT);
    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {
        ArrayList<Match> matches = chartData.get(labels.get(h.getDataSetIndex()));
        int mid = matchIdsArrayList.get((int) h.getX());
        for (Match match : matches) {
            if (match.getId() == mid) {
                String title = match.getNameHome() + " - " + match.getNameVisitor();
                matchTitleTextView.setText(title);
                String score = getString(R.string.chart_score_formatted, match.getScoreHome(), match.getScoreVisitor());
                scoreTextView.setText(score);
                String prediction = getString(R.string.chart_prediction_formatted, match.getPredictionHome(), match.getPredictionVisitor());
                predictionTextView.setText(prediction);
            }
        }
    }

    @Override
    public void onNothingSelected() {

    }

    private class Match {
        private int id;
        private String date;
        private String nameHome;
        private String nameVisitor;
        private int scoreHome;
        private int scoreVisitor;
        private int predictionHome;
        private int predictionVisitor;
        private int points;

        public Match(int id, String date, String nameHome, String nameVisitor, int scoreHome, int scoreVisitor, int predictionHome, int predictionVisitor, int points) {
            this.id = id;
            this.date = date;
            this.nameHome = nameHome;
            this.nameVisitor = nameVisitor;
            this.scoreHome = scoreHome;
            this.scoreVisitor = scoreVisitor;
            this.predictionHome = predictionHome;
            this.predictionVisitor = predictionVisitor;
            this.points = points;
        }

        int getId() {
            return id;
        }

        String getDate() {
            return date;
        }

        String getNameHome() {
            return nameHome;
        }

        String getNameVisitor() {
            return nameVisitor;
        }

        int getScoreHome() {
            return scoreHome;
        }

        int getScoreVisitor() {
            return scoreVisitor;
        }

        int getPredictionHome() {
            return predictionHome;
        }

        int getPredictionVisitor() {
            return predictionVisitor;
        }

        int getPoints() {
            return points;
        }
    }
}
