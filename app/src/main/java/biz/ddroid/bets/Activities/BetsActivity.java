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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import biz.ddroid.bets.fragments.CompletedPredictionsFragment;
import biz.ddroid.bets.fragments.CreatePredictionFragment;
import biz.ddroid.bets.fragments.NewPredictionsFragment;
import biz.ddroid.bets.R;
import biz.ddroid.bets.fragments.PendingPredictionsFragment;
import biz.ddroid.bets.pojo.Match;
import biz.ddroid.bets.utils.SharedPrefs;

public class BetsActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, NewPredictionsFragment.OnFragmentInteractionListener,
        PendingPredictionsFragment.OnFragmentInteractionListener, CompletedPredictionsFragment.OnFragmentInteractionListener,
        CreatePredictionFragment.OnFragmentInteractionListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Setting ViewPager for each Tabs
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
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
        if (header_user_name != null) header_user_name.setText(getSharedPreferences(SharedPrefs.PREFS_NAME, 0).getString(SharedPrefs.USERNAME, "Anonymous"));
        TextView header_user_email = (TextView) header.findViewById(R.id.header_user_email);
        if (header_user_email != null) header_user_email.setText(getSharedPreferences(SharedPrefs.PREFS_NAME, 0).getString(SharedPrefs.EMAIL, "email@domain.l"));
    }

    // Add Fragments to Tabs
    private void setupViewPager(ViewPager viewPager) {
        Adapter adapter = new Adapter(getSupportFragmentManager());
        adapter.addFragment(NewPredictionsFragment.newInstance(NewPredictionsFragment.TAB_NEW), getString(R.string.tab_matches_new));
        adapter.addFragment(PendingPredictionsFragment.newInstance(PendingPredictionsFragment.TAB_PENDING), getString(R.string.tab_matches_pending));
        adapter.addFragment(CompletedPredictionsFragment.newInstance(CompletedPredictionsFragment.TAB_COMPLETED), getString(R.string.tab_matches_completed));
        viewPager.setAdapter(adapter);
    }

    @Override
    public void onFragmentInteraction(Match match) {
        Toast.makeText(BetsActivity.this, Integer.toString(match.getId()), Toast.LENGTH_SHORT).show();
        DialogFragment newFragment = CreatePredictionFragment.newInstance(match);
        newFragment.show(getSupportFragmentManager(), "dialog");

    }

    @Override
    public void onFragmentInteraction(Uri uri) {

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
