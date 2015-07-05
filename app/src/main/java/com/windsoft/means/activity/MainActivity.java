package com.windsoft.means.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.windsoft.means.Global;
import com.windsoft.means.MusicService;
import com.windsoft.means.R;
import com.windsoft.means.model.MusicModel;

import java.util.ArrayList;
import java.util.Random;


public class MainActivity extends FragmentActivity {

    private final String TAG = "MainActivity";

    private ViewPager viewPager;

    private ArrayList<MusicModel> musicList = new ArrayList<>();    // 모든 음악 데이터

    private ImageButton good;

    private ImageButton soso;

    private ImageButton bad;

    private Adapter adapter;

    private int size;

    private FloatingActionButton recommend;
    private FloatingActionButton logout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = new Intent(this, MusicService.class);
        intent.putExtra(Global.COMMAND_KEY, Global.CHECK_DB);
        startService(intent);

        init();
    }


    private void init() {
        good = (ImageButton) findViewById(R.id.activity_main_good);
        soso = (ImageButton) findViewById(R.id.activity_main_soso);
        bad = (ImageButton) findViewById(R.id.activity_main_bad);

        recommend = (FloatingActionButton) findViewById(R.id.menu_recommend);
        logout = (FloatingActionButton) findViewById(R.id.menu_logout);

        setListener();
    }


    private void setListener() {
        good.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = musicList.get(viewPager.getCurrentItem()).getName();

                Intent intent = new Intent(MainActivity.this, MusicService.class);
                intent.putExtra(Global.COMMAND_KEY, Global.APPRAISAL);
                intent.putExtra(Global.SONG_SCORE_KEY, Global.GOOD);
                intent.putExtra(Global.SONG_NAME_KEY, name);
                intent.putExtra(Global.KEY_POSITION, viewPager.getCurrentItem());
                startService(intent);
            }
        });


        soso.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = musicList.get(viewPager.getCurrentItem()).getName();
                Intent intent = new Intent(MainActivity.this, MusicService.class);
                intent.putExtra(Global.COMMAND_KEY, Global.APPRAISAL);
                intent.putExtra(Global.SONG_SCORE_KEY, Global.SOSO);
                intent.putExtra(Global.SONG_NAME_KEY, name);
                intent.putExtra(Global.KEY_POSITION, viewPager.getCurrentItem());
                startService(intent);
            }
        });


        bad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = musicList.get(viewPager.getCurrentItem()).getName();
                Intent intent = new Intent(MainActivity.this, MusicService.class);
                intent.putExtra(Global.COMMAND_KEY, Global.APPRAISAL);
                intent.putExtra(Global.SONG_SCORE_KEY, Global.BAD);
                intent.putExtra(Global.SONG_NAME_KEY, name);
                intent.putExtra(Global.KEY_POSITION, viewPager.getCurrentItem());
                startService(intent);
            }
        });



        recommend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "추천받기");
                Intent intent = new Intent(MainActivity.this, MusicService.class);
                intent.putExtra(Global.COMMAND_KEY, Global.RECOMMEND);
                startService(intent);
            }
        });


        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "로그아웃");
                Global.editor.putString(Global.LOGIN_ID_KEY, null);
                Global.editor.commit();
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
    }


    private class Adapter extends FragmentStatePagerAdapter {

        public Adapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Log.d(TAG, "size = " + musicList.size());
            Log.d(TAG, "position = " + position);

            if (position > musicList.size() - 10) {
                Log.d(TAG, "DB 다시 받아옴");
                Intent intent = new Intent(MainActivity.this, MusicService.class);
                intent.putExtra(Global.COMMAND_KEY, Global.REQ_DB);
                startService(intent);
            }
            if (position < musicList.size()) {
                Log.e(TAG, "이름 = " + musicList.get(position).getName());
                AppraisalFragment fragment = AppraisalFragment.newInstance(musicList.get(position));
                return fragment;
            }
            return null;
        }


        @Override
        public int getCount() {
            return size;
        }


        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }

    }


    @Override
    protected void onNewIntent(Intent intent) {

        if (intent != null) {
            String command = intent.getStringExtra(Global.COMMAND_KEY);

            if (command != null) {
                if (command.equals(Global.RES_DB)) {    // DB 새로고침 응답
                    commandResDB();
                } else if (command.equals(Global.APPRAISAL)) {
                    int cond = intent.getIntExtra(Global.COND, 0);
                    int position = intent.getIntExtra(Global.KEY_POSITION, 0);
                    commandAppraisal(cond, position);
                }
            }
        }

        super.onNewIntent(intent);
    }


    private void commandAppraisal(int cond, final int position) {
        Log.d(TAG,"commandAppraisal()");
        if (cond == Global.SUCCESS) {
            viewPager.setCurrentItem(position + 1, true);
            new CountDownTimer(300, 300) {
                @Override
                public void onTick(long millisUntilFinished) {

                }

                @Override
                public void onFinish() {
                    musicList.remove(position);
                    adapter.notifyDataSetChanged();
                    viewPager.setCurrentItem(position, false);
                }
            }.start();
        } else {
            Toast.makeText(getApplicationContext(), "실패했습니다.", Toast.LENGTH_SHORT).show();
        }
    }


    private void commandResDB() {
        Log.d(TAG, "commandResDB()");

        if (adapter != null) {
            Log.d(TAG, "notichange");
            adapter.notifyDataSetChanged();
        }
        musicList = Global.manager.findList();

        /**
         * 랜덤으로 섞기
         * */
        for (int i = 0; i < musicList.size(); i++) {
            Random random = new Random();
            int temp = random.nextInt(musicList.size());
            int curTemp = random.nextInt(musicList.size());

            MusicModel model = musicList.get(temp);
            musicList.set(temp, musicList.get(curTemp));
            musicList.set(curTemp, model);
        }
        size = musicList.size();

        viewPager = (ViewPager) findViewById(R.id.activity_main_pager);
        adapter = new Adapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(5);

    }
}
