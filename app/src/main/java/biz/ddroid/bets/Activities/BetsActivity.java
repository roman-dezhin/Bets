package biz.ddroid.bets.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import biz.ddroid.bets.BetApplication;
import biz.ddroid.bets.R;
import biz.ddroid.bets.fragments.BasePredictionsFragment;
import biz.ddroid.bets.fragments.CompletedPredictionsFragment;
import biz.ddroid.bets.fragments.CreatePredictionFragment;
import biz.ddroid.bets.fragments.NewPredictionsFragment;
import biz.ddroid.bets.fragments.PendingPredictionsFragment;
import biz.ddroid.bets.listener.OnFragmentRefresh;
import biz.ddroid.bets.pojo.Match;
import biz.ddroid.bets.rest.PredictServices;
import biz.ddroid.bets.rest.ServicesClient;
import biz.ddroid.bets.utils.NetworkUtils;
import biz.ddroid.bets.utils.SharedPrefs;
import cz.msebera.android.httpclient.Header;

public class BetsActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, BasePredictionsFragment.OnFragmentInteractionListener,
        CreatePredictionFragment.OnFragmentInteractionListener, OnFragmentRefresh {

    private Adapter adapter;
    private ViewPager viewPager;
    private MenuItem menuItem;
    private final static int PREDICTIONS_STATUS_NEW = 0;
    private final static int PREDICTIONS_STATUS_PENDING = 1;
    private final static int PREDICTIONS_STATUS_COMPLETED = 2;

    private String TAG = "BetsActivity";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v(TAG, "onCreate: " + this.toString());

        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Setting ViewPager for each Tabs
        viewPager = findViewById(R.id.viewpager);
        setupViewPager(viewPager);
        // Set Tabs inside Toolbar
        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View header = navigationView.getHeaderView(0);

        ImageView header_user_photo = header.findViewById(R.id.navigation_drawer_user_account_picture_profile);
        String avatarUriString = SharedPrefs.getPref(this, SharedPrefs.AVATAR);
        Bitmap avatarBitmap = BitmapFactory.decodeFile(avatarUriString);
        if (avatarUriString.equals("") || avatarBitmap == null) {
            header_user_photo.setImageResource(R.drawable.ic_account_circle_white_64dp);
        } else {
            header_user_photo.setImageURI(Uri.parse(avatarUriString));
            avatarBitmap.recycle();
        }

        TextView header_user_name = header.findViewById(R.id.header_username);
        if (header_user_name != null) {
            header_user_name.setText(getSharedPreferences(SharedPrefs.PREFS_NAME, 0)
                    .getString(SharedPrefs.USERNAME, "Anonymous"));
        }
        TextView header_user_email = header.findViewById(R.id.header_user_email);
        if (header_user_email != null) {
            header_user_email.setText(getSharedPreferences(SharedPrefs.PREFS_NAME, 0)
                    .getString(SharedPrefs.EMAIL, "email@domain.tld"));
        }
    }

    @Override
    public void onFragmentInteraction(Match match, int matchStatus) {
        switch (matchStatus) {
            case PREDICTIONS_STATUS_NEW:
                DialogFragment newFragment = CreatePredictionFragment.newInstance(match);
                newFragment.show(getSupportFragmentManager(), "dialog");
                break;
        }
    }

    @Override
    public void onFragmentInteraction(int matchId, int team_home_prediction, int team_visitor_prediction) {
        if (!NetworkUtils.isNetworkConnected(this)) {
            Toast.makeText(this, R.string.no_internet_connections, Toast.LENGTH_SHORT).show();
            return;
        }
        JSONObject prediction = new JSONObject();
        try {
            prediction.put(PredictServices.MATCH_ID, Integer.toString(matchId));
            prediction.put(PredictServices.TEAM_HOME_SCORE, Integer.toString(team_home_prediction));
            prediction.put(PredictServices.TEAM_VISITOR_SCORE, Integer.toString(team_visitor_prediction));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        final ServicesClient servicesClient = BetApplication.getServicesClient();
        servicesClient.setToken(getSharedPreferences(SharedPrefs.PREFS_NAME, 0).getString(SharedPrefs.TOKEN, ""));
        PredictServices predictServices = new PredictServices(servicesClient);
        predictServices.create(prediction, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.v(TAG, "onFragmentInteraction: response: " + response.toString());
                Toast.makeText(BetsActivity.this, R.string.success_prediction, Toast.LENGTH_SHORT).show();
                NewPredictionsFragment newPredictionsFragment = (NewPredictionsFragment) adapter.getFragment(PREDICTIONS_STATUS_NEW);
                newPredictionsFragment.refreshMatches(servicesClient);
                PendingPredictionsFragment pendingPredictionsFragment = (PendingPredictionsFragment) adapter.getFragment(PREDICTIONS_STATUS_PENDING);
                pendingPredictionsFragment.refreshMatches(servicesClient);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers,  Throwable error, JSONObject response) {
                Log.v(TAG, "onFragmentInteraction: response: " + response.toString());
                Log.v(TAG, "onFragmentInteraction: error: " + error.toString());
            }

            @Override
            public void onFailure(int statusCode, Header[] headers,  Throwable error, JSONArray response) {
                Log.v(TAG, "onFragmentInteraction: response: " + response.toString());
                Log.v(TAG, "onFragmentInteraction: error: " + error.toString());
            }
        });
    }

    @Override
    public void onFragmentRefreshed() {
        if (menuItem != null) {
            menuItem.collapseActionView();
            menuItem.setActionView(null);
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_refresh) {
            Log.v(TAG, "action_refresh: " + viewPager.getCurrentItem());
            menuItem = item;
            menuItem.setActionView(R.layout.progressbar);
            menuItem.expandActionView();
            refreshFragmentData(viewPager.getCurrentItem());
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.matches) {
            viewPager.setCurrentItem(0);
        } else if (id == R.id.profile) {
            Intent intent = new Intent(getApplicationContext(), AccountProfileActivity.class);
            startActivity(intent);
        } else if (id == R.id.results) {
            Intent intent = new Intent(getApplicationContext(), ResultsActivity.class);
            startActivity(intent);
        } else if (id == R.id.statistics) {
            Intent intent = new Intent(getApplicationContext(), StatisticsActivity.class);
            startActivity(intent);
        } else if (id == R.id.rules) {
            Intent intent = new Intent(getApplicationContext(), RulesActivity.class);
            startActivity(intent);
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void setupViewPager(ViewPager viewPager) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        List<String> titleList = new ArrayList<>();
        titleList.add(getString(R.string.tab_matches_new));
        titleList.add(getString(R.string.tab_matches_pending));
        titleList.add(getString(R.string.tab_matches_completed));
        adapter = new Adapter(fragmentManager, titleList);
        Log.v(TAG, "setupViewPager: adapter: " + adapter.toString());
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(2);
    }

    private void refreshFragmentData(int tabId) {
        ServicesClient servicesClient = BetApplication.getServicesClient();
        servicesClient.setToken(getSharedPreferences(SharedPrefs.PREFS_NAME, 0).getString(SharedPrefs.TOKEN, ""));
        switch (tabId){
            case PREDICTIONS_STATUS_NEW:
                NewPredictionsFragment newPredictionsFragment = (NewPredictionsFragment) adapter.getFragment(PREDICTIONS_STATUS_NEW);
                newPredictionsFragment.refreshMatches(servicesClient);
                break;
            case PREDICTIONS_STATUS_PENDING:
                PendingPredictionsFragment pendingPredictionsFragment = (PendingPredictionsFragment) adapter.getFragment(PREDICTIONS_STATUS_PENDING);
                pendingPredictionsFragment.refreshMatches(servicesClient);
                break;
            case PREDICTIONS_STATUS_COMPLETED:
                CompletedPredictionsFragment completedPredictionsFragment = (CompletedPredictionsFragment) adapter.getFragment(PREDICTIONS_STATUS_COMPLETED);
                completedPredictionsFragment.refreshMatches(servicesClient);
                break;
        }
    }

    public class Adapter extends FragmentPagerAdapter {

        private List<String> mFragmentTitleList = new ArrayList<>();
        private Map<Integer, String> mFragmentTags;
        private FragmentManager mFragmentManager;
        Adapter(FragmentManager manager, List<String> titleList) {
            super(manager);
            mFragmentManager = manager;
            mFragmentTags = new HashMap<>();
            mFragmentTitleList = titleList;
        }

        @Override
        public int getCount() {
            return mFragmentTitleList.size();
        }

        @Override
        public Fragment getItem(int position) {
            switch (position){
                case PREDICTIONS_STATUS_NEW:
                    return NewPredictionsFragment.newInstance(PREDICTIONS_STATUS_NEW);
                case PREDICTIONS_STATUS_PENDING:
                    return PendingPredictionsFragment.newInstance(PREDICTIONS_STATUS_PENDING);
                case PREDICTIONS_STATUS_COMPLETED:
                    return CompletedPredictionsFragment.newInstance(PREDICTIONS_STATUS_COMPLETED);
            }
            return null;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            Object obj = super.instantiateItem(container, position);
            if (obj instanceof Fragment) {
                // record the fragment tag here.
                Fragment f = (Fragment) obj;
                String tag = f.getTag();
                mFragmentTags.put(position, tag);
            }
            return obj;
        }

        Fragment getFragment(int position) {
            String tag = mFragmentTags.get(position);
            if (tag == null)
                return null;
            return mFragmentManager.findFragmentByTag(tag);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }

    }
}
