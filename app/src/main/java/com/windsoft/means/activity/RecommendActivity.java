package com.windsoft.means.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.windsoft.means.Global;
import com.windsoft.means.R;

import java.util.ArrayList;

public class RecommendActivity extends ActionBarActivity {

    private ArrayList<String> recommendSongList = null;

    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommend);

        Intent intent = getIntent();

        getDataFromIntent(intent);

        createInstance();
    }


    private void createInstance() {
        viewPager = (ViewPager) findViewById(R.id.activity_recommend_pager);
        viewPager.setAdapter(new Adapter(getSupportFragmentManager()));
        viewPager.setOffscreenPageLimit(recommendSongList.size());
    }


    public class Adapter extends FragmentPagerAdapter {

        public Adapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return RecommendFragment.newInstance(recommendSongList.get(position));
        }

        @Override
        public int getCount() {
            return recommendSongList.size();
        }
    }


    private void getDataFromIntent(Intent intent) {
        if (intent != null)
            recommendSongList = intent.getStringArrayListExtra(Global.KEY_BEST_SONG);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_appraisal, menu);
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
}
