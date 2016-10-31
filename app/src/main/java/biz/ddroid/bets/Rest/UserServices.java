package biz.ddroid.bets.rest;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

public class UserServices {
    public final static String USER_DATA = "data";
    public final static String USER_DATA_PICTURE = "picture_upload";
    public final static String USER_UID = "uid";
    public final static String USER_NAME = "name";
    public final static String USER_MAIL = "mail";
    public final static String USER_AVATAR = "avatar";
    public final static String USER_TOUR_WINS = "field_tourwins";
    public final static String USER_TOUR_WINS_LANG = "und";
    public final static String USER_TOUR_WINS_VALUE = "value";
    public final static String USER_PICTURE = "picture";
    public final static String USER_PICTURE_URL = "url";
    public final static String USER_PICTURE_FID = "fid";
    public final static String USER_PREDICTIONS_COUNT = "predictions_count";
    public final static String USER_POINTS = "points";

    private ServicesClient client;

    public UserServices(ServicesClient c) {
        client = c;
    }

    public void isUserExists(String username, AsyncHttpResponseHandler responseHandler) {
        RequestParams params = new RequestParams("parameters[name]", username);
        client.get("user", params, responseHandler);
    }

    public void register(String username, String password, String email, AsyncHttpResponseHandler responseHandler) {
        JSONObject params = new JSONObject();
        try {
            params.put("name", username);
            params.put("mail", email);
            params.put("pass", password);
            params.put("status", 1);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        client.post("user/register", params, responseHandler);
    }

    public void login(String username, String password, AsyncHttpResponseHandler responseHandler) {
        JSONObject params = new JSONObject();
        try {
            params.put("name", username);
            params.put("pass", password);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        client.post("user/login", params, responseHandler);
    }

    public void retrieve(String uid, AsyncHttpResponseHandler responseHandler) {
        client.get("user/" + uid, null, responseHandler);
    }

    public void update(String uid, JSONObject params, AsyncHttpResponseHandler responseHandler) {
        client.put("user/" + uid, params, responseHandler);
    }

    public void logout(AsyncHttpResponseHandler responseHandler) {
        client.post("user/logout", new JSONObject(), responseHandler);
    }

    public void statistics(String uid, AsyncHttpResponseHandler responseHandler) {
        client.get("user/" + uid + "/statistics", new RequestParams(), responseHandler);
    }

    public void friends(String uid, AsyncHttpResponseHandler responseHandler) {
        client.get("user/" + uid + "/friends", new RequestParams(), responseHandler);
    }
}
