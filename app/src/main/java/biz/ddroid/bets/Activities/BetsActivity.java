package biz.ddroid.bets.activities;

import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import biz.ddroid.bets.BetApplication;
import biz.ddroid.bets.fragments.BasePredictionsFragment;
import biz.ddroid.bets.fragments.CompletedPredictionsFragment;
import biz.ddroid.bets.fragments.CreatePredictionFragment;
import biz.ddroid.bets.fragments.NewPredictionsFragment;
import biz.ddroid.bets.R;
import biz.ddroid.bets.fragments.PendingPredictionsFragment;
import biz.ddroid.bets.pojo.Match;
import biz.ddroid.bets.rest.PredictServices;
import biz.ddroid.bets.rest.ServicesClient;
import biz.ddroid.bets.utils.SharedPrefs;
import cz.msebera.android.httpclient.Header;

public class BetsActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, BasePredictionsFragment.OnFragmentInteractionListener,
        CreatePredictionFragment.OnFragmentInteractionListener {

    private Adapter adapter;
    private ViewPager viewPager;
    private final static int PREDICTIONS_STATUS_NEW = 0;
    private final static int PREDICTIONS_STATUS_PENDING = 1;
    private final static int PREDICTIONS_STATUS_COMPLETED = 2;

    private String TAG = "BetsActivity";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Setting ViewPager for each Tabs
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);
        // Set Tabs inside Toolbar
        TabLayout tabs = (TabLayout) findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View header = navigationView.getHeaderView(0);
        TextView header_user_name = (TextView) header.findViewById(R.id.header_username);
        if (header_user_name != null) {
            header_user_name.setText(getSharedPreferences(SharedPrefs.PREFS_NAME, 0)
                    .getString(SharedPrefs.USERNAME, "Anonymous"));
        }
        TextView header_user_email = (TextView) header.findViewById(R.id.header_user_email);
        if (header_user_email != null) {
            header_user_email.setText(getSharedPreferences(SharedPrefs.PREFS_NAME, 0)
                    .getString(SharedPrefs.EMAIL, "email@domain.l"));
        }
    }

    private void setupViewPager(ViewPager viewPager) {
        adapter = new Adapter(getSupportFragmentManager());
        adapter.addFragment(NewPredictionsFragment.newInstance(PREDICTIONS_STATUS_NEW), getString(R.string.tab_matches_new));
        adapter.addFragment(PendingPredictionsFragment.newInstance(PREDICTIONS_STATUS_PENDING), getString(R.string.tab_matches_pending));
        adapter.addFragment(CompletedPredictionsFragment.newInstance(PREDICTIONS_STATUS_COMPLETED), getString(R.string.tab_matches_completed));
        viewPager.setAdapter(adapter);
    }

    @Override
    public void onFragmentInteraction(Match match, int matchStatus) {
        Toast.makeText(BetsActivity.this, Integer.toString(match.getId()), Toast.LENGTH_SHORT).show();
        switch (matchStatus) {
            case PREDICTIONS_STATUS_NEW:
                DialogFragment newFragment = CreatePredictionFragment.newInstance(match);
                newFragment.show(getSupportFragmentManager(), "dialog");
                break;
        }
    }

    @Override
    public void onFragmentInteraction(int matchId, int team_home_prediction, int team_visitor_prediction) {
        JSONObject prediction = new JSONObject();
        try {
            prediction.put("mid", Integer.toString(matchId));
            prediction.put("team_home_score", Integer.toString(team_home_prediction));
            prediction.put("team_visitor_score", Integer.toString(team_visitor_prediction));
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
                Toast.makeText(BetsActivity.this, "Success predict", Toast.LENGTH_SHORT).show();
                NewPredictionsFragment newPredictionsFragment = (NewPredictionsFragment) adapter.getItem(0);
                newPredictionsFragment.refreshMatches(servicesClient);
                PendingPredictionsFragment pendingPredictionsFragment = (PendingPredictionsFragment) adapter.getItem(1);
                pendingPredictionsFragment.refreshMatches(servicesClient);
                //adapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers,  Throwable error, JSONObject response) {
                Log.v(TAG, "onFragmentInteraction: response: " + response.toString());
                Log.v(TAG, "onFragmentInteraction: error: " + error.toString());
                Toast.makeText(BetsActivity.this, response.toString(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers,  Throwable error, JSONArray response) {
                Log.v(TAG, "onFragmentInteraction: response: " + response.toString());
                Log.v(TAG, "onFragmentInteraction: error: " + error.toString());
                try {
                    Toast.makeText(BetsActivity.this, response.getString(0), Toast.LENGTH_SHORT).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    static class Adapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public Adapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        /*@Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }*/

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
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

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
