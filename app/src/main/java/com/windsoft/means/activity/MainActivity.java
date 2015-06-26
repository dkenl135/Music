package com.windsoft.means.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;

import com.windsoft.means.Global;
import com.windsoft.means.MusicService;
import com.windsoft.means.R;
import com.windsoft.means.model.MusicModel;

import java.util.ArrayList;


public class MainActivity extends FragmentActivity {

    private final String TAG = "MainActivity";

    private ViewPager viewPager;

    private ArrayList<MusicModel> musicList = new ArrayList<>();    // 모든 음악 데이터

    private ArrayList<AppraisalFragment> fragments = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = new Intent(this, MusicService.class);
        intent.putExtra(Global.COMMAND_KEY, Global.CHECK_DB);
        startService(intent);
    }


    public String getSong(int num) {
        return musicList.get(num).getName();
    }


    public String getArtist(int num) {
        return musicList.get(num).getArtist();
    }


    private class Adapter extends FragmentStatePagerAdapter {
        public Adapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            if (position > musicList.size() - 3) {
                Log.d(TAG, "DB 다시 받아옴");
                Intent intent = new Intent(MainActivity.this, MusicService.class);
                intent.putExtra(Global.COMMAND_KEY, Global.REQ_DB);
                startService(intent);
            }
            return AppraisalFragment.newInstance(musicList.size());
        }

        @Override
        public int getCount() {
            return musicList.size();
        }

    }


    @Override
    protected void onNewIntent(Intent intent) {

        if (intent != null) {
            String command = intent.getStringExtra(Global.COMMAND_KEY);

            if (command != null) {
                if (command.equals(Global.RES_DB)) {    // DB 새로고침 응답
                    commandResDB();
                }
            }
        }

        super.onNewIntent(intent);
    }


    private void commandResDB() {
        Log.d(TAG, "commandResDB()");
        musicList = Global.manager.findList();

        viewPager = (ViewPager) findViewById(R.id.activity_main_pager);
        viewPager.setAdapter(new Adapter(getSupportFragmentManager()));
        viewPager.setOffscreenPageLimit(5);
    }
}
