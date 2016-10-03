package biz.ddroid.bets.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.util.Base64;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;

import biz.ddroid.bets.BetApplication;
import biz.ddroid.bets.R;
import biz.ddroid.bets.rest.FileServices;
import biz.ddroid.bets.rest.ServicesClient;
import biz.ddroid.bets.rest.UserServices;
import biz.ddroid.bets.utils.ImageUtils;
import biz.ddroid.bets.utils.SharedPrefs;
import cz.msebera.android.httpclient.Header;

public class AccountProfileActivity extends AppCompatActivity {
    private static final int REQUEST_CAMERA = 1;
    private static final int SELECT_FILE = 2;
    private ImageView avatar;
    private String TAG = "AccountProfileActivity";
    File avatarFile, avatarFileTmp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_profile);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        avatarFile = new File(getExternalFilesDir(null), "avatar.png");
        avatarFileTmp = new File(getExternalFilesDir(null), "avatar_temp.png");

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
                            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(avatarFileTmp));
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
        String avatarUriString = SharedPrefs.getAvatar(this);
        Bitmap avatarBitmap = BitmapFactory.decodeFile(avatarUriString);
        if (avatarUriString.equals("") || avatarBitmap == null) {
            avatar.setImageResource(R.drawable.ic_account_circle_black_128dp);
        } else {
            avatar.setImageURI(Uri.parse(avatarUriString));
            avatarBitmap.recycle();
        }

        TextView account_user_name = (TextView) findViewById(R.id.account_user_name);
        if (account_user_name != null) {
            account_user_name.setText(getSharedPreferences(SharedPrefs.PREFS_NAME, 0)
                    .getString(SharedPrefs.USERNAME, "Anonymous"));
        }
        TextView account_user_email = (TextView) findViewById(R.id.account_user_email);
        if (account_user_email != null) {
            account_user_email.setText(getSharedPreferences(SharedPrefs.PREFS_NAME, 0)
                    .getString(SharedPrefs.EMAIL, "email@domain.tld"));
        }

        ImageView addFriend = (ImageView) findViewById(R.id.account_add_friend);
        addFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO: add functionality for adding friends
                Toast.makeText(AccountProfileActivity.this, getString(R.string.functionality_under_developing), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        avatarFileTmp.delete();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case SELECT_FILE:
                if(resultCode == RESULT_OK && data != null){
                    Uri avatarUri = copyFile(data.getData());
                    avatar.setImageBitmap(BitmapFactory.decodeFile(avatarUri.getPath()));
                    SharedPrefs.saveAvatar(this, avatarUri.getPath());
                    sendAvatarToServer(avatarUri.getPath());
                }
                break;
            case REQUEST_CAMERA:
                if(resultCode == RESULT_OK){
                    if (data == null) {
                        Uri avatarUri = copyFile(Uri.fromFile(avatarFileTmp));
                        avatar.setImageBitmap(BitmapFactory.decodeFile(avatarUri.getPath()));
                        SharedPrefs.saveAvatar(this, avatarUri.getPath());
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
                }
        );
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
