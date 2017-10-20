package es.unex.heatmaphybrid;

import android.app.Application;

import com.nimbees.platform.NimbeesClient;
import com.nimbees.platform.NimbeesException;

/**
 * Created by Javier on 03/10/2017.
 *
 * NimbeesClient need to be initialized before any other class, thats why we extend Application here.
 *
 * Lets fly to the bees!
 */

public class app extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        try {
            NimbeesClient.init(this);
        } catch (NimbeesException e) {
            e.printStackTrace();
        }
    }
}
