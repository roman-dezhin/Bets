package biz.ddroid.bets.rest;


import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONObject;

public class FileServices {
    public final static String FILE = "file";
    public final static String FILE_NAME = "filename";
    public final static String FILE_PATH = "filepath";
    public final static String PATH_TO_FILE = "public://avatar/";
    public final static String FILE_ID = "fid";

    private ServicesClient client;

    public FileServices(ServicesClient c) {
        client = c;
    }

    public void create(JSONObject file, AsyncHttpResponseHandler responseHandler) {
        client.post("file", file, responseHandler);
    }

    public void delete(String fid, AsyncHttpResponseHandler responseHandler) {
        client.delete("file/" + fid, responseHandler);
    }
}
