package es.unex.heatmaphybrid.messagemanager;

import android.location.Location;

import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Toast;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.nimbees.platform.NimbeesClient;
import com.nimbees.platform.NimbeesException;
import com.nimbees.platform.callbacks.NimbeesCallback;
import com.nimbees.platform.callbacks.NimbeesRegistrationCallback;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import es.gloin.nimbees.common.beans.notifications.MessageContent;
import es.gloin.nimbees.common.beans.notifications.filters.PastLocationFilter;
import es.gloin.nimbees.common.beans.notifications.filters.UserNameListFilter;
import es.unex.heatmaphybrid.MainActivity;
import es.unex.heatmaphybrid.model.LocationFrequency;


/**
 * This class contains methods to help MainActivity
 */
public class NotificationHelper {

  /**
   * MainActivity to show elements on the UI.
   */
  MainActivity mMainActivity;

  /**
   * The Array for notifications
   */
  public ArrayAdapter<String> mNotificationArrayAdapter;

  /**
   * Default constructor
   *
   * @param act The Mainactivity instance.
   */
  public NotificationHelper(MainActivity act) {
    this.mMainActivity = act;
  }


  /**
   * Register the user in Nimbees.
   */
  public void registerUser(String username) {

    NimbeesClient.getUserManager().register(username, new NimbeesRegistrationCallback() {
      @Override
      public void onSuccess() {
        // The toast to show on the UI the message defined here
        Toast.makeText(mMainActivity, "Register OK", Toast.LENGTH_SHORT).show();
        Log.e("HEATMAP", "User registered for the reception of push notifications");
      }

      @Override
      public void onFailure(NimbeesException e) {
          Toast.makeText(mMainActivity, "Register Error", Toast.LENGTH_SHORT).show();
          Log.e("HEATMAP", "ERROR registering the user for receiving push notifications");

      }
    });
  }

  /**
   * This method send the message with the given radius and dates.
   *
   * @param radius the radius to send the message.
   * @param location the location of the user.
   * @param beginDate the begin date for the HeatMap
   * @param endDate the end date for the HeatMap
   */
  public void sendRequestLocationMessage(int radius, Location location, Date beginDate, Date endDate) {
    if (location == null) {
      return;
    }

    // The list of filters, by default we only add a past location filter with the requested locations
    List<Object> filters = new ArrayList();
    PastLocationFilter pastLocationFilter = new PastLocationFilter(location.getLatitude(), location.getLongitude(),
            (double) radius, beginDate, endDate);
    filters.add(pastLocationFilter);

    //We need to create a custom message , the steps are :
    // 1. We create a instance of message with the information.
    RequestLocationMessage requestLocation = new RequestLocationMessage(NimbeesClient.getUserManager().getUserData().getAlias(),beginDate, endDate, location.getLatitude(), location.getLongitude(), radius);

    // 2. We convert the instance to a Json using Gson ( wich produces a String Json format).
    Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").create();
    String requestLocationMsg = gson.toJson(requestLocation, RequestLocationMessage.class);

    // 3. Now we send the Json formed String to the method sendMessage.
    NimbeesClient.getNotificationManager()
        .sendNotification(requestLocationMsg, MessageContent.NotificationType.CUSTOM, filters,
            new NimbeesCallback<Integer>() {
              @Override
              public void onSuccess(Integer integer) {
                Toast.makeText(mMainActivity, "Request Location Sent! ", Toast.LENGTH_SHORT).show();
                  Log.e("HEATMAP", "Request Location Sent! " );
              }

              @Override
              public void onFailure(NimbeesException e) {
                Toast.makeText(mMainActivity, "ERROR Requesting Location", Toast.LENGTH_SHORT).show();
                  Log.e("HEATMAP", "ERROR Requesting Location" );
              }
            });
  }

  /**
   * This method send the message with the user's locations for the given dates.
   *
   */
  public static void sendLocationsMessage(List <LocationFrequency> locations, String requesterId) {

    // The list of filters, by default we only add a past location filter with the requested locations
    List<Object> filters = new ArrayList();
    List <String> listRequesters = new ArrayList<>();
    listRequesters.add(requesterId);
    UserNameListFilter usersFilter = new UserNameListFilter(listRequesters);
    filters.add(usersFilter);

    //We need to create a custom message , the steps are :
    // 1. We create a instance of message with the information.
    SendLocationsMessage locationsMessage = new SendLocationsMessage(NimbeesClient.getUserManager().getUserData().getAlias(), locations);

    // 2. We convert the instance to a Json using Gson ( wich produces a String Json format).
    String locationsMsg = new Gson().toJson(locationsMessage, SendLocationsMessage.class);

    // 3. Now we send the Json formed String to the method sendMessage.
    NimbeesClient.getNotificationManager()
            .sendNotification(locationsMsg, MessageContent.NotificationType.CUSTOM, filters,
                    new NimbeesCallback<Integer>() {
                      @Override
                      public void onSuccess(Integer integer) {
                          Log.e("HEATMAP", "Locations sent" );
                      }

                      @Override
                      public void onFailure(NimbeesException e) {
                          Log.e("HEATMAP", "ERROR sending the locations" );
                      }
                    });
  }



}
