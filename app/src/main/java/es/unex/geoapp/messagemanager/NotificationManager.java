package es.unex.geoapp.messagemanager;

import android.content.Context;
import android.graphics.PointF;
import android.os.Bundle;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.nimbees.platform.NimbeesNotificationManager;
import com.nimbees.platform.beans.NimbeesLocation;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.math.RoundingMode;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import es.unex.geoapp.locationmanager.LocationManager;
import es.unex.geoapp.model.LocationFrequency;
import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by Javier on 10/10/2017.
 *  This NotificationManager lets handle a LocationMessage to do what you want with it
  */

public class NotificationManager extends NimbeesNotificationManager {

    /**
     * The context.
     */
    private Context mContext;

    public NotificationManager(Context context) {
        super(context);
        this.mContext = context;
    }

    /**
     * Receive a custom Message and we can do anything here with it
     *
     * @param idNotification the unique id of the notification
     * @param content the content of the message
     */
    @Override
    public void handleCustomMessage(
            long idNotification,
                String content,
                Map<String, String> aditionalContent) {


        //super.handleCustomMessage(idNotification, content, additionalContent);
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").create();
        LocationMessage message = gson.fromJson(content, LocationMessage.class);

        if (message.getKind() == NotificationKind.RequestLocation){
            RequestLocationMessage requestMsg = new Gson().fromJson(content, RequestLocationMessage.class);
            List <LocationFrequency> locations = LocationManager.getLocationHistory(requestMsg.getBeginDate(), requestMsg.getEndDate(),requestMsg.getLatitude(), requestMsg.getLongitude(), requestMsg.getRadius());

            List <List <LocationFrequency>> locationLists = partition(locations, 25);

            for (List<LocationFrequency> list: locationLists  ) {
                NotificationHelper.sendLocationsMessage(list, requestMsg.getSenderId());
            }


        } else{
            if (message.getKind() == NotificationKind.SendLocation){
                SendLocationsMessage locationsMsg = new Gson().fromJson(content, SendLocationsMessage.class);
                Log.e("HEATMAP", "Locations received. User: " + locationsMsg.getSenderId() + " Total: " + locationsMsg.getLocationList().size());
                LocationManager.storeLocations(locationsMsg.getLocationList());

            } else{
                Log.e("HEATMAP", "Another kind of message: " +content);
            }

        }

    }

    private static <T> List<List<T>> partition(List<T> input, int size) {
        List<List<T>> lists = new ArrayList<List<T>>();
        for (int i = 0; i < input.size(); i += size) {
            lists.add(input.subList(i, Math.min(input.size(), i + size)));
        }
        return lists;
    }
}
