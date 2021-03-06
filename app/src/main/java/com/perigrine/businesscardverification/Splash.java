package com.perigrine.businesscardverification;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.perigrine.Helper.APICalls;
import com.perigrine.Helper.Common;
import com.perigrine.preferences.AppPreferences;

public class Splash extends AppCompatActivity {
    AppPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        preferences = new AppPreferences(Splash.this);
        if (!Common.isFirstTimeLaunch(this)) {
            setContentView(R.layout.create_layout);
            Thread timerThread = new Thread() {
                public void run() {
                    try {
                        sleep(1000);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                setContentView(R.layout.splash);
                                try {
                                    sleep(3000);
                                    Common.setLaunchType(Splash.this);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }finally {
                                    Intent intent = new Intent(Splash.this, LoginActivity.class);
                                    startActivity(intent);
                                }
                            }
                        });

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } finally {

                    }
                }
            };
            timerThread.start();
        } else {
            setContentView(R.layout.splash);
            Thread timerThread = new Thread() {
                public void run() {
                    try {
                        sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } finally {
                        checkToken();
                    }
                }
            };
            timerThread.start();
        }


    }

    private void checkToken() {
        if(preferences.getToken().equals("")){
            Intent intent = new Intent(Splash.this, LoginActivity.class);
            startActivity(intent);
        }else{
            Intent intent = new Intent(Splash.this, HomeVistorsList.class);
            startActivity(intent);
        }
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        finish();
    }
}
