package biz.ddroid.bets.activities;

import android.support.design.widget.TabLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import biz.ddroid.bets.BetApplication;
import biz.ddroid.bets.R;
import biz.ddroid.bets.fragments.BaseResultsFragment;
import biz.ddroid.bets.fragments.ResultsChartFragment;
import biz.ddroid.bets.fragments.ResultsFragment;
import biz.ddroid.bets.listener.OnFragmentRefresh;
import biz.ddroid.bets.rest.ServicesClient;
import biz.ddroid.bets.utils.SharedPrefs;

public class ResultsActivity extends AppCompatActivity implements OnFragmentRefresh, BaseResultsFragment.OnFragmentInteractionListener {

    private Adapter adapter;
    private ViewPager viewPager;
    private MenuItem menuItem;
    public final static int RESULTS_FRIENDS = 0;
    public final static int RESULTS_WORLD = 1;

    private String TAG = "ResultsActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Setting ViewPager for each Tabs
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);
        // Set Tabs inside Toolbar
        TabLayout tabs = (TabLayout) findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);
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

        if (id == android.R.id.home) {
            NavUtils.navigateUpFromSameTask(this);
            return true;
        }

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

    @Override
    public void onFragmentRefreshed() {
        if (menuItem != null) {
            menuItem.collapseActionView();
            menuItem.setActionView(null);
        }
    }

    private void setupViewPager(ViewPager viewPager) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        List<String> titleList = new ArrayList<>();
        titleList.add(getString(R.string.tab_results_friends));
        titleList.add(getString(R.string.tab_results_world));
        adapter = new Adapter(fragmentManager, titleList);
        Log.v(TAG, "setupViewPager: adapter: " + adapter.toString());
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(1);
    }

    private void refreshFragmentData(int tabId) {
        ServicesClient servicesClient = BetApplication.getServicesClient();
        servicesClient.setToken(getSharedPreferences(SharedPrefs.PREFS_NAME, 0).getString(SharedPrefs.TOKEN, ""));
        ResultsFragment resultsFragment = (ResultsFragment) adapter.getFragment(tabId);
        resultsFragment.refreshResults(servicesClient);
    }

    @Override
    public void onFragmentInteraction(int tourId, String tourTitle) {
        DialogFragment newFragment = ResultsChartFragment.newInstance(tourId, tourTitle);
        newFragment.setStyle(DialogFragment.STYLE_NORMAL, R.style.ChartDialog);
        newFragment.show(getSupportFragmentManager(), "dialog");
    }

    public class Adapter extends FragmentPagerAdapter {

        private List<String> mFragmentTitleList = new ArrayList<>();
        private Map<Integer, String> mFragmentTags;
        private FragmentManager mFragmentManager;
        public Adapter(FragmentManager manager, List<String> titleList) {
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
                case RESULTS_FRIENDS:
                    return ResultsFragment.newInstance(RESULTS_FRIENDS);
                case RESULTS_WORLD:
                    return ResultsFragment.newInstance(RESULTS_WORLD);
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

        public Fragment getFragment(int position) {
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
