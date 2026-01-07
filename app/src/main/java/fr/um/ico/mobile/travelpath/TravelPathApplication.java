package fr.um.ico.mobile.travelpath;

import android.app.Application;
import fr.um.ico.mobile.travelpath.di.AppContainer;

public class TravelPathApplication extends Application {

    // Instance unique du container
    public AppContainer appContainer;

    @Override
    public void onCreate() {
        super.onCreate();
        appContainer = new AppContainer(this);
    }
}