package com.perigrine.Extras;

import android.app.Application;
import android.util.Log;

import com.perigrine.Interfaces.AppObserver;

import java.util.ArrayList;
import java.util.List;
import java.util.Observer;

/**
 * Created by ragamai on 9/27/17.
 */
public class BusinessCardVerification extends Application {

    public static BusinessCardVerification instance;
    private List<AppObserver> mObservers = new ArrayList<>();

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }

    public void notifyObservers(int event, Object arg) {
        for (int i = 0; i < mObservers.size(); i++) {
            Log.i("", "*******************  loop  notifyObservers( count = "+i+"event = " + event
            );
            mObservers.get(i).update(event, arg);
            // break;
        }
    }

    public synchronized void addObserver(AppObserver obs) {
        Log.i("", "addObserver(" + obs + ")");
        if (mObservers.indexOf(obs) < 0) {
            Log.i("", " inside addObserver(" + obs + ")");
            mObservers.add(obs);
        }
    }

    public synchronized void deleteObserver(AppObserver obs) {
        Log.i("", "deleteObserver(" + obs + ")");
        mObservers.remove(obs);
    }
}
