package biz.ddroid.bets.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import biz.ddroid.bets.R;
import biz.ddroid.bets.utils.ImageUtils;
import biz.ddroid.bets.utils.SharedPrefs;

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
                }
                break;
            case REQUEST_CAMERA:
                if(resultCode == RESULT_OK){
                    if (data == null) {
                        Uri avatarUri = copyFile(Uri.fromFile(avatarFileTmp));
                        avatar.setImageBitmap(BitmapFactory.decodeFile(avatarUri.getPath()));
                        SharedPrefs.saveAvatar(this, avatarUri.getPath());
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
