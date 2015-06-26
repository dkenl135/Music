package com.windsoft.means;

import android.app.Service;
import android.content.Intent;
import android.util.Log;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.Socket;
import com.windsoft.means.activity.LoginActivity;

import org.json.JSONObject;

/**
 * Created by dongkyu on 2015-06-20.
 */
public class MusicSockt {

    private static final String TAG = "MusicSocket";

    private static final int SUCCESS = 1;

    private static final int NULL = 0;

    private Socket socket;

    private Service service;

    private int cond = NULL;

    public MusicSockt(Service service, Socket socket) {
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
                if (cond == NULL) {
                    Intent intent = new Intent(service, LoginActivity.class);
                    intent.putExtra(Global.COMMAND_KEY, Global.CONNECT_SERVER);
                    intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    service.startActivity(intent);

                    cond = SUCCESS;
                    Log.d(TAG, "Muzik 서버 연결 완료");
                }
            }
        });

        socket.open();
        socket.connect();
    }


    public void login(String id) {
        try {
            cond = NULL;

            JSONObject obj = new JSONObject();
            obj.put(Global.USER_ID, id);

            socket.emit(Global.LOGIN_KEY, obj);
            Log.d(TAG, "로그인 요청");

            socket.on(Global.LOGIN_KEY, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
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
            });
        } catch (Exception e) {
            Log.e(TAG, "commandLoginKey() 에러 = " + e.getMessage());
        }
    }
}
