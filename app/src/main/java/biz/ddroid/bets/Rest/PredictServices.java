package biz.ddroid.bets.rest;


import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

public class PredictServices {
    private ServicesClient client;

    public final static String MATCH_ID = "mid";
    public final static String TOURNAMENT_ID = "tid";
    public final static String DATE = "date";
    public final static String TOURNAMENT_NAME = "tournament_name";
    public final static String STAGE = "stage";
    public final static String TEAM_HOME = "team_home";
    public final static String TEAM_VISITOR = "team_visitor";
    public final static String TEAM_HOME_SCORE = "team_home_score";
    public final static String TEAM_VISITOR_SCORE = "team_visitor_score";
    public final static String TEAM_HOME_PREDICTION = "team_home_prediction";
    public final static String TEAM_VISITOR_PREDICTION = "team_visitor_prediction";
    public final static String TEAM_HOME_ICON = "team_home_icon";
    public final static String TEAM_VISITOR_ICON = "team_visitor_icon";
    public final static String CITY = "city";
    public final static String POINTS = "points";
    public final static String FRIENDS_PREDICTIONS = "friends_predictions";
    public final static String FRIENDS_PREDICTIONS_AND_POINTS = "friends_predictions_and_points";
    public final static String USER_NAME = "user_name";
    public final static String PREDICTIONS_COUNT = "predictions_count";
    public final static String DATETIME_FORMAT = "HH:mm dd.MM.yy";
    public final static String RESULT_ROWS = "result_rows";
    public final static String RESULT_USERNAME = "username";
    public final static String RESULT_POINTS = "points";
    public final static String RESULT_PREDICTIONS = "predictions";
    public final static String RESULT_SCORES = "scores";
    public final static String RESULT_RESULTS = "results";
    public final static String RESULT_WINNER = "winner";
    public final static int USER_IS_WINNER = 1;
    public final static String TOURNAMENT_IS_FINISHED = "tournament_is_finished";
    public final static String TOURNAMENT_FILTER = "tournament_status";
    public final static String RESULT_FILTER = "result_status";
    public final static String STATISTIC_USERNAME = "username";
    public final static String STATISTIC_POINTS = "points";
    public final static String STATISTIC_PREDICTIONS = "predictions";
    public final static String STATISTIC_SCORES = "scores";
    public final static String STATISTIC_RESULTS = "results";
    public final static String STATISTIC_PERCENTS = "percents";
    public final static String STATISTIC_WINS = "wins";
    public final static String PAGE = "page";

    public PredictServices(ServicesClient c) {
        client = c;
    }

    public void retrieve(int matchId, AsyncHttpResponseHandler responseHandler) {
        client.get("predict/" + Integer.toString(matchId), null, responseHandler);
    }

    public void index(AsyncHttpResponseHandler responseHandler){
        client.get("predict", null, responseHandler);
    }

    public void create(JSONObject prediction, AsyncHttpResponseHandler responseHandler) {
        client.post("predict", prediction, responseHandler);
    }

    public void newMatches(AsyncHttpResponseHandler responseHandler){
        client.post("predict/new", new JSONObject(), responseHandler);
    }

    public void pendingMatches(AsyncHttpResponseHandler responseHandler){
        client.post("predict/pending", new JSONObject(), responseHandler);
    }

    public void completedMatches(JSONObject param, AsyncHttpResponseHandler responseHandler){
        client.post("predict/completed", param, responseHandler);
    }

    public void results(JSONObject params, AsyncHttpResponseHandler responseHandler) {
        client.post("predict/results", params, responseHandler);
    }

    public void statistic(JSONObject params, AsyncHttpResponseHandler responseHandler) {
        client.post("predict/statistic", params, responseHandler);
    }
}
