package biz.ddroid.bets.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.util.Base64;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;

import biz.ddroid.bets.BetApplication;
import biz.ddroid.bets.R;
import biz.ddroid.bets.adapters.FriendsArrayAdapter;
import biz.ddroid.bets.pojo.Friend;
import biz.ddroid.bets.rest.FileServices;
import biz.ddroid.bets.rest.ServicesClient;
import biz.ddroid.bets.rest.UserServices;
import biz.ddroid.bets.utils.ImageUtils;
import biz.ddroid.bets.utils.SharedPrefs;
import cz.msebera.android.httpclient.Header;

public class AccountProfileActivity extends AppCompatActivity {
    private static final int REQUEST_CAMERA = 1;
    private static final int SELECT_FILE = 2;
    private static final String FRIENDS_LIST = "friends_list";
    private static final String PREDICTION_COUNT = "prediction_count";
    private static final String POINTS = "points";
    private static final String TOUR_WINS = "tour_wins";
    private static final String FRIENDS_COUNT = "friends_count";
    private ImageView avatar;
    private String TAG = "AccountProfileActivity";
    private File avatarFile, avatarFileForCamera;
    private final String AVATAR_FILE_NAME = "avatar.png";
    private TextView accountUserPredictionCount;
    private TextView accountUserPoints;
    private TextView accountUserTourWins;
    private TextView accountUserFriendsCount;
    private ListView friendsList;
    private ArrayList<Friend> friends;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_profile);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        avatarFile = new File(getExternalFilesDir(null), AVATAR_FILE_NAME);
        avatarFileForCamera = new File(getExternalFilesDir(null), "avatar_temp.png");

        avatar = (ImageView) findViewById(R.id.account_image);
        avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final CharSequence[] items = {getString(R.string.avatar_dialog_from_camera), getString(R.string.avatar_dialog_from_gallery), getString(R.string.avatar_dialog_cancel)};
                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(AccountProfileActivity.this);
                builder.setTitle(R.string.avatar_dialog_title);
                builder.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int item) {
                        if (items[item].equals(getString(R.string.avatar_dialog_from_camera))) {
                            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(avatarFileForCamera));
                            if (intent.resolveActivity(getPackageManager()) != null) {
                                startActivityForResult(intent, REQUEST_CAMERA);
                            }
                        } else if (items[item].equals(getString(R.string.avatar_dialog_from_gallery))) {
                            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            if (intent.resolveActivity(getPackageManager()) != null) {
                                startActivityForResult(intent, SELECT_FILE);
                            }
                        } else if (items[item].equals(getString(R.string.avatar_dialog_cancel))) {
                            dialog.dismiss();
                        }
                    }
                });
                builder.show();
            }
        });
        String avatarUriString = SharedPrefs.getPref(this, SharedPrefs.AVATAR);
        Bitmap avatarBitmap = BitmapFactory.decodeFile(avatarUriString);
        if (avatarUriString.equals("") || avatarBitmap == null) {
            avatar.setImageResource(R.drawable.ic_account_circle_black_128dp);
        } else {
            avatar.setImageURI(Uri.parse(avatarUriString));
            avatarBitmap.recycle();
        }

        TextView accountUserName = (TextView) findViewById(R.id.account_user_name);
        if (accountUserName != null) {
            accountUserName.setText(getSharedPreferences(SharedPrefs.PREFS_NAME, 0)
                    .getString(SharedPrefs.USERNAME, "Anonymous"));
        }
        TextView accountUserEmail = (TextView) findViewById(R.id.account_user_email);
        if (accountUserEmail != null) {
            accountUserEmail.setText(getSharedPreferences(SharedPrefs.PREFS_NAME, 0)
                    .getString(SharedPrefs.EMAIL, "email@domain.tld"));
        }

        accountUserPredictionCount = (TextView) findViewById(R.id.account_user_predictions_count);
        accountUserPoints = (TextView) findViewById(R.id.account_user_points);
        accountUserTourWins = (TextView) findViewById(R.id.account_user_tours_wins);
        accountUserFriendsCount = (TextView) findViewById(R.id.account_user_friends_count);

        ImageView addFriend = (ImageView) findViewById(R.id.account_add_friend);
        addFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO: add functionality for adding friends
                Toast.makeText(AccountProfileActivity.this, getString(R.string.functionality_under_developing), Toast.LENGTH_SHORT).show();
            }
        });

        friendsList = (ListView) findViewById(R.id.friends_list);

        if (savedInstanceState != null) {
            friends = savedInstanceState.getParcelableArrayList(FRIENDS_LIST);
            FriendsArrayAdapter arrayAdapter = new FriendsArrayAdapter(AccountProfileActivity.this, friends);
            friendsList.setAdapter(arrayAdapter);
            accountUserPredictionCount.setText(savedInstanceState.getString(PREDICTION_COUNT));
            accountUserPoints.setText(savedInstanceState.getString(POINTS));
            accountUserTourWins.setText(savedInstanceState.getString(TOUR_WINS));
            accountUserFriendsCount.setText(savedInstanceState.getString(FRIENDS_COUNT));
        }
        else getUserData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        avatarFileForCamera.delete();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case SELECT_FILE:
                if(resultCode == RESULT_OK && data != null){
                    Uri avatarUri = copyFile(data.getData());
                    avatar.setImageBitmap(BitmapFactory.decodeFile(avatarUri.getPath()));
                    SharedPrefs.setPref(this, SharedPrefs.AVATAR, avatarUri.getPath());
                    sendAvatarToServer(avatarUri.getPath());
                }
                break;
            case REQUEST_CAMERA:
                if(resultCode == RESULT_OK){
                    if (data == null) {
                        Uri avatarUri = copyFile(Uri.fromFile(avatarFileForCamera));
                        avatar.setImageBitmap(BitmapFactory.decodeFile(avatarUri.getPath()));
                        SharedPrefs.setPref(this, SharedPrefs.AVATAR, avatarUri.getPath());
                        sendAvatarToServer(avatarUri.getPath());
                    }
                }
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == android.R.id.home) {
            NavUtils.navigateUpFromSameTask(this);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(FRIENDS_LIST, friends);
        outState.putString(PREDICTION_COUNT, accountUserPredictionCount.getText().toString());
        outState.putString(POINTS, accountUserPoints.getText().toString());
        outState.putString(TOUR_WINS, accountUserTourWins.getText().toString());
        outState.putString(FRIENDS_COUNT, accountUserFriendsCount.getText().toString());
    }

    private void getUserData() {
        final ServicesClient servicesClient = BetApplication.getServicesClient();
        servicesClient.setToken(getSharedPreferences(SharedPrefs.PREFS_NAME, 0).getString(SharedPrefs.TOKEN, ""));
        UserServices userServices = new UserServices(servicesClient);
        final String uid = getSharedPreferences(SharedPrefs.PREFS_NAME, 0).getString(SharedPrefs.UID, "");
        if (uid.equals("")) {
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
            finish();
        }
        userServices.retrieve(uid, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                try {
                    if (response.get(UserServices.USER_UID).equals(uid)) {
                        updateUserData(response);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }

            @Override
            public void onFinish() {
                super.onFinish();
                Log.v(TAG, "userServices.retrieve: onFinish");
            }
        });

        userServices.statistics(uid, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                try {
                    accountUserPredictionCount.setText(String.format(getString(R.string.account_predictions_count_formatted), response.getString(UserServices.USER_PREDICTIONS_COUNT)));
                    accountUserPoints.setText(String.format(getString(R.string.account_points_formatted),response.getString(UserServices.USER_POINTS)));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }

            @Override
            public void onFinish() {
                super.onFinish();
                Log.v(TAG, "userServices.statistics: onFinish");
            }
        });

        userServices.friends(uid, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                super.onSuccess(statusCode, headers, response);
                accountUserFriendsCount.setText(String.format(getString(R.string.account_friends_count_formatted), response.length()));
                friends = parseFriendsResponse(response);
                FriendsArrayAdapter arrayAdapter = new FriendsArrayAdapter(AccountProfileActivity.this, friends);
                friendsList.setAdapter(arrayAdapter);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }

            @Override
            public void onFinish() {
                super.onFinish();
                Log.v(TAG, "userServices.friends: onFinish");
            }
        });
    }

    private ArrayList<Friend> parseFriendsResponse(JSONArray response) {
        ArrayList<Friend> friends = new ArrayList<>();
        for (int i = 0; i < response.length(); i++) {
            try {
                JSONObject ob = response.getJSONObject(i);
                friends.add(new Friend(ob.getInt(UserServices.USER_UID), ob.getString(UserServices.USER_NAME), ob.getString(UserServices.USER_AVATAR)));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return friends;
    }

    private void updateUserData(JSONObject user) {
        try {
            SharedPrefs.setPref(this, SharedPrefs.USERNAME, user.getString(UserServices.USER_NAME));
            SharedPrefs.setPref(this, SharedPrefs.EMAIL, user.getString(UserServices.USER_MAIL));
            if (!user.getJSONObject(UserServices.USER_PICTURE).getString(UserServices.USER_PICTURE_FID)
                    .equals(SharedPrefs.getPref(this, SharedPrefs.AVATAR_FILE_ID))) {
                SharedPrefs.setPref(this, SharedPrefs.AVATAR_FILE_ID, user.getJSONObject(UserServices.USER_PICTURE).getString(UserServices.USER_PICTURE_FID));
                Picasso.with(this).load(user.getJSONObject(UserServices.USER_PICTURE).getString(UserServices.USER_PICTURE_URL))
                        .into(new Target() {
                            @Override
                            public void onBitmapLoaded(final Bitmap bitmap, Picasso.LoadedFrom from) {
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        File file = new File(getExternalFilesDir(null), AVATAR_FILE_NAME);
                                        try {
                                            file.createNewFile();
                                            FileOutputStream ostream = new FileOutputStream(file);
                                            bitmap.compress(Bitmap.CompressFormat.PNG,100,ostream);
                                            ostream.close();
                                            avatar.setImageBitmap(bitmap);
                                        }
                                        catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }).start();
                            }

                            @Override
                            public void onBitmapFailed(Drawable errorDrawable) {

                            }

                            @Override
                            public void onPrepareLoad(Drawable placeHolderDrawable) {

                            }
                        });
            }
            accountUserTourWins.setText(String.format(getString(R.string.account_tours_wins_formatted),
                    user.getJSONObject(UserServices.USER_TOUR_WINS)
                    .getJSONArray(UserServices.USER_TOUR_WINS_LANG).getJSONObject(0)
                    .getString(UserServices.USER_TOUR_WINS_VALUE)));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void sendAvatarToServer(String fileUri) {
        final ServicesClient servicesClient = BetApplication.getServicesClient();
        servicesClient.setToken(getSharedPreferences(SharedPrefs.PREFS_NAME, 0).getString(SharedPrefs.TOKEN, ""));
        FileServices fileServices = new FileServices(servicesClient);
        String uid = getSharedPreferences(SharedPrefs.PREFS_NAME, 0).getString(SharedPrefs.UID, "");

        JSONObject params = new JSONObject();
        Date date = new Date();
        String filename = uid + "-" + date.getTime() + ".png";
        ByteArrayOutputStream byteArrayOS = new ByteArrayOutputStream();
        BitmapFactory.decodeFile(fileUri).compress(Bitmap.CompressFormat.PNG, 100, byteArrayOS);
        String file = Base64.encodeToString(byteArrayOS.toByteArray(), Base64.DEFAULT);
        try {
            params.put(FileServices.FILE_NAME, filename);
            params.put(FileServices.FILE_PATH, FileServices.PATH_TO_FILE + filename);
            params.put(FileServices.FILE, file);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        fileServices.create(params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                Log.v(TAG, "sendAvatarToServer()->fileServices.create()->onSuccess()");
                try {
                    String fid = response.getString(FileServices.FILE_ID);
                    updateUserAvatar(fid);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                Log.v(TAG, "sendAvatarToServer()->fileServices.create()->onFailure()");
            }

            @Override
            public void onFinish() {
                super.onFinish();
                Log.v(TAG, "fileServices.creates: onFinish");
            }
        });
    }

    private void updateUserAvatar(String fid) {
        final ServicesClient servicesClient = BetApplication.getServicesClient();
        servicesClient.setToken(getSharedPreferences(SharedPrefs.PREFS_NAME, 0).getString(SharedPrefs.TOKEN, ""));
        final FileServices fileServices = new FileServices(servicesClient);
        UserServices userServices = new UserServices(servicesClient);
        String uid = getSharedPreferences(SharedPrefs.PREFS_NAME, 0).getString(SharedPrefs.UID, "");


        JSONObject picture = new JSONObject();
        try {
            picture.put(UserServices.USER_DATA_PICTURE, fid);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JSONObject data = new JSONObject();
        try {
            data.put(UserServices.USER_DATA, picture);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        userServices.update(uid, data, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                Log.v(TAG, "updateUserAvatar()->userServices.update()->onSuccess()");
                try {
                    fileServices.delete(getSharedPreferences(SharedPrefs.PREFS_NAME, 0).getString(SharedPrefs.AVATAR_FILE_ID, ""), new AsyncHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

                        }
                    });
                    String fid = response.getJSONObject(UserServices.USER_DATA_PICTURE).getString(FileServices.FILE_ID);
                    SharedPreferences settings = getSharedPreferences(SharedPrefs.PREFS_NAME, 0);
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putString(SharedPrefs.AVATAR_FILE_ID, fid);
                    editor.commit();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                Log.v(TAG, "updateUserAvatar()->userServices.update()->onFailure()");
            }

            @Override
            public void onFinish() {
                super.onFinish();
                Log.v(TAG, "userServices.update: onFinish");
            }
        });
    }

    private Uri copyFile(Uri uri) {
        OutputStream os = null;
        try {
            avatarFile.createNewFile();
            os = new FileOutputStream(avatarFile);
            String filePath;
            if (uri != null && "content".equals(uri.getScheme())) {
                Cursor cursor = this.getContentResolver().query(uri, new String[] { android.provider.MediaStore.Images.ImageColumns.DATA }, null, null, null);
                cursor.moveToFirst();
                filePath = cursor.getString(0);
                cursor.close();
            } else {
                filePath = uri.getPath();
            }
            ImageUtils.getCroppedCircularBitmap(
                    ImageUtils.rotateBitmap(filePath, ImageUtils.lessResolution(
                            filePath,
                            (int) getResources().getDimension(R.dimen.account_profile_activity_account_image_width),
                            (int) getResources().getDimension(R.dimen.account_profile_activity_account_image_height))
                    )).compress(Bitmap.CompressFormat.PNG, 100, os);
            os.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (os != null) {
                    os.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return Uri.fromFile(avatarFile);
    }
}
