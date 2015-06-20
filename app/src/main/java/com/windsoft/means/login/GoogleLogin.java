package com.windsoft.means.login;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.plus.Plus;
import com.windsoft.means.Global;
import com.windsoft.means.activity.LoginActivity;

/**
 * Created by dongkyu on 2015-06-17.
 */
public class GoogleLogin {

    private static final String TAG = "GoogleLogin";

    private GoogleApiClient googleClient;

    private SignInButton loginButton;

    private Activity activity;


    public GoogleLogin(Activity activity) {
        this.activity = activity;
    }


    public GoogleLogin(Activity activity, SignInButton loginButton) {
        this.loginButton = loginButton;
        this.activity = activity;

        init();
    }


    private void init() {
        googleClient = new GoogleApiClient.Builder(activity)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(Bundle bundle) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                Log.d(TAG, "구글 연결 완료");
                                if (Plus.AccountApi.getAccountName(googleClient) != null) {
                                    String accountName = Plus.AccountApi.getAccountName(googleClient);

                                    Global.editor.putString(Global.LOGIN_ID_KEY, accountName);
                                    Global.editor.commit();

                                    ((LoginActivity) activity).login(accountName);
                                }
                            }
                        }).start();
                    }

                    @Override
                    public void onConnectionSuspended(int i) {
                        Log.e(TAG, "suspended()");
                        googleClient.connect();
                    }
                })
                .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(ConnectionResult connectionResult) {
                        try {
                            Log.d(TAG, "구글 연결 실패");
                            if (connectionResult.hasResolution()) {
                                Log.d(TAG, "뭔가한다...");
                                connectionResult.startResolutionForResult(activity, ConnectionResult.SIGN_IN_REQUIRED);
                            }
                        } catch (Exception e) {
                            googleClient.connect();
                        }
                    }
                })
                .addApi(Plus.API)
                .addScope(new Scope(Scopes.PLUS_LOGIN))
                .addScope(new Scope(Scopes.PLUS_ME))
                .addScope(new Scope(Scopes.PROFILE))
                .build();

//        getString(R.string.common_signin_button_text_long);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                googleClient.connect();
            }
        });

        if (loginButton == null) {
            googleClient.connect();
        }
    }

    public void clientReconnect() {
        if (googleClient.isConnected()) {
            googleClient.clearDefaultAccountAndReconnect();
        } else {
            googleClient.connect();
        }
    }


    public void clientDisconnect() {
        if (googleClient != null && googleClient.isConnected()) {
            googleClient.disconnect();
        }
    }


}
