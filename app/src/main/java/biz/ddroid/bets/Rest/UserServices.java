package biz.ddroid.bets.Rest;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

public class UserServices {
    private ServicesClient client;

    public UserServices(ServicesClient c) {
        client = c;
    }

    public void isUserExist(String username, AsyncHttpResponseHandler responseHandler) {
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

    public void logout(AsyncHttpResponseHandler responseHandler) {
        client.post("user/logout", new JSONObject(), responseHandler);
    }
}
