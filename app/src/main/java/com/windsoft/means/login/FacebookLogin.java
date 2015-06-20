package com.windsoft.means.login;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.windsoft.means.Global;
import com.windsoft.means.activity.LoginActivity;

import java.util.Arrays;

/**
 * Created by dongkyu on 2015-06-17.
 */
public class FacebookLogin {

    private static final String TAG = "FacebookLogin";

    private CallbackManager callback;

    private AccessTokenTracker tracker;

    private Activity activity;

    private LoginButton loginButton;

    public FacebookLogin(Activity activity) {
        this.activity = activity;

        init();
    }


    public FacebookLogin(Activity activity, LoginButton loginButton) {
        this.activity = activity;
        this.loginButton = loginButton;

        init();
    }


    private void init() {
        callback = CallbackManager.Factory.create();
        tracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldToken, AccessToken newToken) {
                if (oldToken != null) {     // 로그아웃
                    Log.d(TAG, "oldToken.token = " + oldToken.getToken());
                    Log.d(TAG, "oldToken.id = " + oldToken.getUserId());
                }

                if (newToken != null) {     // 로그인
                    Log.d(TAG, "new.token = " + newToken.getToken());
                    Log.d(TAG, "new.id = " + newToken.getUserId());

                    Global.editor.putString(Global.LOGIN_ID_KEY, newToken.getUserId());
                    Global.editor.commit();

                    ((LoginActivity) activity).login(newToken.getUserId());
                }
            }
        };

        if (loginButton != null) {
            loginButton.setReadPermissions("public_profile");

            loginButton.registerCallback(callback, new FacebookCallback<LoginResult>() {
                @Override
                public void onSuccess(LoginResult loginResult) {
                    Toast.makeText(activity, "성공하였습니다!", Toast.LENGTH_SHORT).show();

//                Intent intent = new Intent(LoginActivity.this, MusicService.class);
//                intent.putExtra(Global.COMMAND_KEY, Global.sign)
                }

                @Override
                public void onCancel() {
                    Toast.makeText(activity, "취소 되었습니다.", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onError(FacebookException e) {
                    Toast.makeText(activity, "에러!", Toast.LENGTH_SHORT).show();
                }
            });
        }

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginManager.getInstance().logInWithReadPermissions(activity, Arrays.asList("public_profile"));
            }
        });
    }


    public void activityCallback(int requestCode, int resultCode, Intent data) {
        if (callback != null) {
            callback.onActivityResult(requestCode, resultCode, data);
        }
    }


    public void stopTracking() {
        if (tracker != null) {
            tracker.stopTracking();
        }
    }

}
