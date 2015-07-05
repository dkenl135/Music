package com.windsoft.means;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.windsoft.means.activity.MainActivity;

import net.htmlparser.jericho.Element;
import net.htmlparser.jericho.HTMLElementName;
import net.htmlparser.jericho.Source;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MusicService extends Service {

    private final String TAG = "MusicService";

    private MusicSocket socket;

    private boolean getSong = false;

    public MusicService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (intent != null) {
            String command = intent.getStringExtra(Global.COMMAND_KEY);

            if (command != null) {
                if (command.equals(Global.CHECK_DB)) {      // DB 체크
                    commandCheckDB();
                } else if (command.equals(Global.REQ_DB)) {     // DB 새로고침 요청
                    int i = Global.pref.getInt(Global.SONG_INDEX, 0);
                    int j = Global.pref.getInt(Global.PAGE_INDEX, 1);
                    commandReqDB(i, j);
                    if (++i == Global.SONG_LIST.length) i = 0;
                    if (++j == 6) i = 1;
                    Global.editor.putInt(Global.SONG_INDEX, i);
                    Global.editor.putInt(Global.PAGE_INDEX, j);
                    Global.editor.commit();
                } else if (command.equals(Global.LOGIN_KEY)) {
                    commandLoginKey(intent);
                } else if (command.equals(Global.CONNECT_SERVER)) {
                    commandConnectServer();
                } else if (command.equals(Global.APPRAISAL)) {
                    int score = intent.getIntExtra(Global.SONG_SCORE_KEY, 0);
                    String name = intent.getStringExtra(Global.SONG_NAME_KEY);
                    int position = intent.getIntExtra(Global.KEY_POSITION, 0);
                    commandAppraisal(name, score, position);
                } else if (command.equals(Global.RECOMMEND)) {
                    commandRecommend();
                }
            }
        }

        return super.onStartCommand(intent, flags, startId);
    }



    /**
     * TODO: 추천받기 버튼
     * */
    private void commandRecommend() {
        socket.recommend();
    }



    /**
     * TODO: 곡 평가
     *
     * */
    private void commandAppraisal(String name, int score, int position) {
        Log.d(TAG, "commandAppraisal");
        socket.appraisal(name, score, position);
    }


    /*
    * TODO: 서버 연결
    * */
    private void commandConnectServer() {
        try {
            Log.d(TAG, "commandConnectServer()");
            Log.d(TAG, Global.SERVER_URL);

            Socket curSocket = IO.socket(Global.SERVER_URL);;

            socket = new MusicSocket(this, curSocket);
        } catch (Exception e) {
            Log.e(TAG, "commandConnectServer() 에러 = " + e.getMessage());
        }
    }


    /*
    * TODO: 로그인 or 회원가입 요청 서버 전송
    * @param: intent = userId값
    * */
    private void commandLoginKey(final Intent intent) {
        Log.d(TAG, "commandLoginKey()");
        String id = intent.getStringExtra(Global.LOGIN_ID_KEY);
        ArrayList<String> musicList = (ArrayList<String>) intent.getSerializableExtra(Global.KEY_BEST_SONG);

        socket.login(id, musicList);
    }


    /*
    * TODO: 장르별 노래 데이터 받아오기
    * */
    private void commandReqDB(final int i, final int j) {
        Log.d(TAG, "commandReqDB()");
        getSong = false;

                    /*
                    * TODO:Genie 사이트에서 장르별 노래 데이터 파싱
                    * @param: Global.SONG_LIST[i] = 장르 데이터, j = page
                    * */
        getSongChart(Global.SONG_LIST[i] + j);

        Log.d(TAG, "Intent ResDB");
    }


    private void getSongChart(final String urlStr) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    ArrayList<String> songs = new ArrayList<>();

                    Log.d(TAG, "url = " + urlStr);
                    URL url = new URL(urlStr);
                    InputStream is = url.openStream();
                    Source source = new Source(new InputStreamReader(is, "utf-8"));
                    source.fullSequentialParse();

                    List<Element> list = source.getAllElements(HTMLElementName.INPUT);

                    for (Element input : list) {
                        String title = input.getAttributeValue("title");

                        if (title != null) {
                            title = title.replace("'", "\"");
                            songs.add(title);
                        }
                    }

                    ArrayList<String> artists = new ArrayList<>();

                    Log.d(TAG, "url = " + urlStr);
                    url = new URL(urlStr);
                    is = url.openStream();
                    source = new Source(new InputStreamReader(is, "utf-8"));
                    source.fullSequentialParse();

                    list = source.getAllElements(HTMLElementName.SPAN);

                    for (Element span : list) {
                        String curClass = span.getAttributeValue("class");

                        if (curClass != null && curClass.equals("meta")) {
                            List<Element> aList = span.getAllElements(HTMLElementName.A);

                            for (Element a : aList) {
                                String artist = (a.getContent().toString()).replaceAll("'", "\"");
                                artists.add(artist);
                                break;
                            }
                        }
                    }

                    Log.d(TAG, "노래 사이즈 = " + songs.size());
                    Log.d(TAG, "아티스트 사이즈 = " + artists.size());

                    for (int i = 0; i < songs.size(); i++) {
                        String temp = songs.get(i);
                        /**
                         * 중복 제거
                         * */
                        for (int j = 1; j < songs.size() - 1; j++) {
                            if (temp.equals(songs.get(j))) {
                                songs.remove(j);
                            }
                        }

                        if (Global.manager.find(songs.get(i)) == null) {
                            Global.manager.insert(songs.get(i), artists.get(i));
                        }
                    }

                    getSong = true;

                    is.close();
                    source.clearCache();
                } catch (Exception e) {
                    Log.e(TAG, "getSongChart() 에러 = " + e.getMessage());
                }


                Intent intent = new Intent(MusicService.this, MainActivity.class);
                intent.putExtra(Global.COMMAND_KEY, Global.RES_DB);
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        }).start();
    }


    private void commandCheckDB() {
        Log.d(TAG, "commandCheckDB()");

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Global.manager = new DBManager(getApplicationContext(), Global.DB_NAME, null, 1);

                    // 없다면
                    if (!isCheckDB()) {
                        Log.d(TAG, "DB 없음");
                        Intent intent = new Intent(MusicService.this, MusicService.class);
                        intent.putExtra(Global.COMMAND_KEY, Global.REQ_DB);
                        startService(intent);
                    } else {
                        Log.d(TAG, "DB 있음");
                        Intent intent = new Intent(MusicService.this, MainActivity.class);
                        intent.putExtra(Global.COMMAND_KEY, Global.RES_DB);
                        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                } catch (Exception e) {
                    Log.e(TAG, "commandCheckDB() 에러 = " + e.getMessage());
                }

            }
        }).start();
    }

    private boolean isCheckDB() {
        Log.d(TAG, "패키지네임 = " + getPackageName());
        String filePath = "/data/data/" + getPackageName() + "/databases/" + Global.DB_NAME;

        File file = new File(filePath);
        if (file.exists())
            return true;
        return false;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
