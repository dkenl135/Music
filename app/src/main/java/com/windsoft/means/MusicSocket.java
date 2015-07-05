package com.windsoft.means;

import android.app.Service;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.Socket;
import com.google.gson.Gson;
import com.windsoft.means.activity.RecommendActivity;
import com.windsoft.means.activity.LoginActivity;
import com.windsoft.means.activity.MainActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by dongkyu on 2015-06-20.
 */
public class MusicSocket {

    private static final String TAG = "MusicSocket";

    private static final int SUCCESS = 1;

    private static final int NULL = 0;

    private Socket socket;

    private Service service;

    private int cond = NULL;

    public void appraisal(String name, int score, int position) {
        Log.d(TAG, "평가 요청 = " + name);
        try {
            JSONObject obj = new JSONObject();
            obj.put(Global.SONG_NAME, name);
            obj.put(Global.ID_KEY, Global.ID);
            obj.put(Global.SONG_SCORE_KEY, score);
            obj.put(Global.KEY_POSITION, position);

            socket.emit(Global.APPRAISAL, obj);
        } catch (Exception e) {
        }
    }


    public MusicSocket(Service service, Socket socket) {
        this.service = service;
        this.socket = socket;

        socketConnect();
    }


    public void socketConnect() {
        if (socket != null && socket.connected()) {
            socket.disconnect();
            socket.close();
        }

        cond = NULL;

        socket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                Log.d(TAG, "연결 응답");
                if (cond == NULL) {
                    Intent intent = new Intent(service, LoginActivity.class);
                    intent.putExtra(Global.COMMAND_KEY, Global.CONNECT_SERVER);
                    intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    service.startActivity(intent);

                    cond = SUCCESS;
                    Log.d(TAG, "Muzik 서버 연결 완료");
                }
            }
        }).on(Global.APPRAISAL, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                Log.d(TAG, "평가 응답");
                int cond = (int) args[0];
                int position = (int) args[1];

                Intent intent = new Intent(service, MainActivity.class);
                intent.putExtra(Global.COMMAND_KEY, Global.APPRAISAL);
                intent.putExtra(Global.COND, cond);
                intent.putExtra(Global.KEY_POSITION, position);
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                service.startActivity(intent);
            }
        }).on(Global.LOGIN_KEY, new Emitter.Listener() {

            @Override
            public void call(Object... args) {
                Log.d(TAG, "로그인 응답");
                if (cond == NULL) {
                    Log.d(TAG, "로그인 응답");

                    int curCond = (int) args[0];
                    String id = args[1].toString();

                    Intent intent = new Intent(service, LoginActivity.class);
                    intent.putExtra(Global.COMMAND_KEY, Global.LOGIN_KEY);
                    intent.putExtra(Global.COND, curCond);
                    intent.putExtra(Global.ID_KEY, id);
                    intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    service.startActivity(intent);

                    cond = SUCCESS;
                }
            }
        }).on(Global.RECOMMEND, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                int cond = (int) args[0];
                JSONArray array = (JSONArray) args[1];

                commandRecommend(cond, array);
            }
        });

        socket.open();
        socket.connect();
    }


    /**
     * TODO: 추천 받기
     * */
    public void recommend() {
        try {
            Log.d(TAG, "추천 요청");
            JSONObject obj = new JSONObject();
            obj.put(Global.ID_KEY, Global.ID);
            socket.emit(Global.RECOMMEND, obj);
        } catch (Exception e) {
        }
    }


    private void commandRecommend(int cond, JSONArray array) {
        Log.d(TAG, "추천 응답");
        try {
            if (cond == Global.SUCCESS) {
                Log.d(TAG, "추천 성공!");
                Log.d(TAG, "결과 값 = " + array);
                Intent intent = new Intent(service, RecommendActivity.class);

                if (array != null && array.length() != 0) {
                    ArrayList<String> musicList = new ArrayList<String>();
                    for (int i = 0; i < array.length(); i++) {
                        musicList.add(array.get(i).toString());
                        intent.putStringArrayListExtra(Global.KEY_BEST_SONG, musicList);
                        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        service.startActivity(intent);
                    }
                }
            } else if (cond == Global.ERROR) {
                Log.d(TAG, "추천 실패...");
                Toast.makeText(service.getApplicationContext(), "추천 오류 발생", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
        }
    }


    public void login(String id, ArrayList<String> musicList) {
        try {
            Log.d(TAG, "로그인 요청");
            cond = NULL;

            JSONObject obj = new JSONObject();
            obj.put(Global.USER_ID, id);

            Gson gson = new Gson();
            String json = gson.toJson(musicList, ArrayList.class);
            JSONArray array = new JSONArray(json);
            obj.put(Global.KEY_BEST_SONG, array);

            socket.emit(Global.LOGIN_KEY, obj);
            Log.d(TAG, "로그인 요청");
        } catch (Exception e) {
            Log.e(TAG, "commandLoginKey() 에러 = " + e.getMessage());
        }
    }
}
