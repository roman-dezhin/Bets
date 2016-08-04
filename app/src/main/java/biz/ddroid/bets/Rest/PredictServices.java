package biz.ddroid.bets.rest;


import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONObject;

public class PredictServices {
    private ServicesClient client;

    public PredictServices(ServicesClient c) {
        client = c;
    }

    public void retrieve(int matchId, AsyncHttpResponseHandler responseHandler) {
        client.get("predict/" + Integer.toString(matchId), null, responseHandler);
    }

    public void index(AsyncHttpResponseHandler responseHandler){
        client.get("predict", null, responseHandler);
    }

    public void newMatches(AsyncHttpResponseHandler responseHandler){
        client.post("predict/new", new JSONObject(), responseHandler);
    }

    public void pending(AsyncHttpResponseHandler responseHandler){
        client.post("predict/pending", new JSONObject(), responseHandler);
    }

    public void completed(AsyncHttpResponseHandler responseHandler){
        client.post("predict/completed", new JSONObject(), responseHandler);
    }
}
