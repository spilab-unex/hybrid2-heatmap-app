package es.unex.heatmaphybrid;

import android.accounts.AccountManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Handler;
import android.support.v4.app.DialogFragment;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.stetho.Stetho;
import com.gc.materialdesign.views.ButtonRectangle;
import com.gc.materialdesign.views.Slider;
import com.gc.materialdesign.widgets.SnackBar;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.common.AccountPicker;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.google.maps.android.heatmaps.HeatmapTileProvider;
import com.google.maps.android.heatmaps.WeightedLatLng;

import com.uphyca.stetho_realm.RealmInspectorModulesProvider;


import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import es.unex.heatmaphybrid.datemanager.DatePickerFragment;

import es.unex.heatmaphybrid.locationmanager.LocationService;
import es.unex.heatmaphybrid.locationmanager.PermissionManager;
import es.unex.heatmaphybrid.messagemanager.NotificationHelper;

import es.unex.heatmaphybrid.model.LocationFrequency;
import es.unex.heatmaphybrid.model.RequestHeatMap;
import es.unex.heatmaphybrid.rest.IPostDataService;
import es.unex.heatmaphybrid.retrofit.APIService;
import es.unex.heatmaphybrid.retrofit.Common;
import es.unex.heatmaphybrid.retrofit.ResponseLambda;
import io.realm.Realm;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MainActivity extends AppCompatActivity {
    /**
     * Request Google Accounts.
     */
    private final int PICK_ACCOUNT_REQUEST = 0;

    /**
     * The radius value (by default 10 meters).
     */
    private int RADIUS = 100;

    // -UI ELEMENTS-
    /**
     * Slider to select the radius value.
     */
    private Slider mSlider;
    /**
     * Button to send the message.
     */
    private ButtonRectangle mButtonSend;
    /**
     * Button to pick the start date.
     */
    private ButtonRectangle mButtonStartDate;
    /**
     * Button to pick the end date.
     */
    private ButtonRectangle mButtonEndDate;
    /**
     * TextView to show the radius of action.
     */
    private TextView mTextViewDistance;
    /**
     * Google map.
     */
    public GoogleMap mGoogleMap;
    /**
     * MarkerOptions for the map.
     */
    private MarkerOptions mMarkerOptions;
    /**
     * Circle to draw arround the Marker with the radius.
     */
    private CircleOptions mCircleOptions;
    /**
     * Location used to add the marker to the map.
     */
    private Location mLocation;
    /**
     * Marker for the icon.
     */
    private Marker mMarker;

    /**
     * Tracking servie
     */
    private Intent locationIntent = null;

    /**
     * Circle for the radius of the icon.
     */
    private Circle mCircle;

    private NotificationHelper nHelper;

    private int startYear=0, startMonth, startDay, startHour, startMinute;
    private int endYear=0, endMonth, endDay, endHour, endMinute;

    /**
     * Endpoints to interact with the rest services
     */
    private IPostDataService rest;


    /**
     * Firebase service
     */
    private static final String TAG = "Firebase Token";

    private static String topic = "heatmap-hybrid";

    private static String topic_url = "/topics/heatmap-hybrid";

    private String token;

    private static APIService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);




        setContentView(R.layout.activity_main);

        // To check de Realm database in Chrome
        Realm.init(this);
        Stetho.initialize(
                Stetho.newInitializerBuilder(this)
                        .enableDumpapp(Stetho.defaultDumperPluginsProvider(this))
                        .enableWebKitInspector(RealmInspectorModulesProvider.builder(this)
                                .build()).build());

        //tileOverlay = null;
        nHelper = new NotificationHelper(this);

        if (locationIntent == null) {
            locationIntent = new Intent(this, LocationService.class);
        }
        // check location permission
        if (PermissionManager.checkPermissions(this, MainActivity.this)){
            startService(locationIntent);
        }

        if (mLocation == null) {
            mLocation = new Location(android.location.LocationManager.GPS_PROVIDER);
        }

        //rest = IPostDataService.retrofit.create(IPostDataService.class);
        //rest = IPostDataService.restAdapter.create(IPostDataService.class);





        // Start the Nimbees Client user tracking service
  /*      NimbeesClient.getPermissionManager().checkPermissions(this);

        if(NimbeesClient.getPermissionManager().getLocationPermissionState(getApplicationContext())){
            NimbeesClient.getLocationManager().startTracking(1,1);
        }
        */

        // We check here if the NimbeesClient have data from the user or not to call register.
//        if (NimbeesClient.getUserManager().getUserData() == null) {
//            showGoogleAccountPicker();
//        }

       // rest = IPostDataService.restAdapter.create(IPostDataService.class);

        apiService = Common.getServer();

        /*
        mTextViewDistance = (TextView) findViewById(R.id.textViewDistance);
        mSlider = (Slider) findViewById(R.id.seekBar);
        // This Listener change the value in the distance field and draw the new circle with the given radius
        mSlider.setOnValueChangedListener(new Slider.OnValueChangedListener() {
            @Override
            public void onValueChanged(int i) {
                // Change the test in the UI
                mTextViewDistance.setText(i + " M");
                // Delete old circle and draw the new circle with the radius given
                drawCircle(i);
                // Update the global variable RADIUS with the new value
                RADIUS = i;
            }
        });
        */

        mButtonSend = (ButtonRectangle) findViewById(R.id.buttonSend);
        // This Listener call sendMessage on press button with radius and message
        mButtonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("date", "start");
                DialogFragment newFragment = new DatePickerFragment();
                newFragment.setArguments(bundle);
                newFragment.show(getSupportFragmentManager(), "datePicker");
            }
        });

        /* DANI
        ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                mGoogleMap = googleMap;
                if(NimbeesClient.getPermissionManager().getLocationPermissionState(getApplicationContext())){
                    getLocation();
                }

                mGoogleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                    @Override
                    public void onMapClick(LatLng point) {
                        mLocation = new Location(android.location.LocationManager.GPS_PROVIDER);
                        mLocation.setLatitude(point.latitude);
                        mLocation.setLongitude(point.longitude);
                        mGoogleMap.clear();
                        mCircleOptions = new CircleOptions().fillColor(0x5500ff00).strokeWidth(0l);
                        mMarkerOptions = new MarkerOptions().position(point).title("Heat map center");//.icon(icon);
                        mMarker = mGoogleMap.addMarker(mMarkerOptions);
                        drawCircle(RADIUS);
                    }
                });
            }
        });
        */

        /*
         * Firebase Service
         */
        getTokenFirebase();
        subscribeTopicFirebase();



    }

    /*
     * Called to subscribed in topic or check if the device is subscribed in topic.
     */
    private void subscribeTopicFirebase() {
        FirebaseMessaging.getInstance().subscribeToTopic(topic)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        String msg = getString(R.string.msg_subscribed);
                        if (!task.isSuccessful()) {
                            msg = getString(R.string.msg_subscribe_failed);
                        }

                        Log.d("Topic Information", msg);
                        Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    /*
     * Gets token of the device and saved in the private variable 'token'.
     */
    private void getTokenFirebase() {
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "getInstanceId failed", task.getException());
                            return;
                        }
                        token = task.getResult().getToken();
                        Log.d(TAG, token);

                    }
                });
    }

//    private BroadcastReceiver broadcastReceiver= new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//
//            Log.d("LOCATION",intent.getDoubleExtra("lat",0)+" : "+intent.getDoubleExtra("long",0));
//            mLocation.setLatitude(intent.getDoubleExtra("lat",0));
//            mLocation.setLongitude(intent.getDoubleExtra("long",0));
//            addLocation();
//
//        }
//    };

    /*Callback result of permissions check*/
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (PermissionManager.onRequestPermissionsResult(requestCode, permissions, grantResults, this, getApplicationContext())) {
            startService(locationIntent);
        }
    }


//    /**
//     * Util of Android to pick an Gmail user account stored in the device.
//     */
//    private void showGoogleAccountPicker() {
//        Intent googlePicker = AccountPicker.newChooseAccountIntent(null, null,
//                new String[]{GoogleAuthUtil.GOOGLE_ACCOUNT_TYPE}, true, null, null, null, null);
//        startActivityForResult(googlePicker, PICK_ACCOUNT_REQUEST);
//    }

    /**
     * Called when showGoogleAccountPicker finish and return the String with the username to be registered.
     */
//    @Override
//    public void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
//        if (requestCode == PICK_ACCOUNT_REQUEST && resultCode == MainActivity.RESULT_OK) {
//            nHelper.registerUser(data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME));
//            //mTextViewEmail.setText("User: " + data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME));
//        }
//    }


    /**
     * Update the variable mLocation with the location using the Nimbees Location service.
     */
    /* DANI
    private void getLocation() {
        NimbeesLocationManager.NimbeesLocationListener mNimbeesLocationListener = new NimbeesLocationManager.NimbeesLocationListener() {
            @Override
            public void onGetCurrentLocation(Location location) {
                mLocation = location;
                setUpMap();
            }

            @Override
            public void onError(NimbeesException e) {
            }
        };
        NimbeesClient.getLocationManager().obtainCurrentLocation(mNimbeesLocationListener);
    }
    */
/*
    *//**
     * Initial configuration of the map with the actual location of the user.
     *//*
    private void setUpMap() {
         // Crear all map elements
        mGoogleMap.clear();
        // Set map type
        mGoogleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        // If we cant find the location now, we call a Network Provider location
       // addLocation();

        }
    }*/

//    private void addLocation(){
//        if (mLocation != null) {
//            // Create a LatLng object for the current location
//            LatLng latLng = new LatLng(mLocation.getLatitude(), mLocation.getLongitude());
//            // Show the current location in Google Map
//            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
//            // Draw the first circle in the map
//            mCircleOptions = new CircleOptions().fillColor(0x5500ff00).strokeWidth(0l);
//            // Zoom in the Google Map
//            mGoogleMap.animateCamera(CameraUpdateFactory.zoomTo(15));
//
//            // Zoom in the Google Map
//            //icon = BitmapDescriptorFactory.fromResource(R.drawable.logo2);
//
//            // Creation and settings of Marker Options
//            mMarkerOptions = new MarkerOptions().position(latLng).title("You are here!");//.icon(icon);
//            // Creation and addition to the map of the Marker
//            mMarker = mGoogleMap.addMarker(mMarkerOptions);
//            // set the initial map radius and draw the circle
//            drawCircle(RADIUS);
//        }
//    }
/*
    *//**
     * Change radius and icon on map when change the radius in the bar.
     *
     * @param
     *//*
    private void drawCircle(int radius) {
        if (mCircle != null) {
            mCircle.remove();
        }
        // We defined a new circle center options with location and radius
        mCircleOptions.center(new LatLng(mLocation.getLatitude(), mLocation.getLongitude())).radius(radius); // In meters
        // We add here to the map the new circle
        mCircle = mGoogleMap.addCircle(mCircleOptions);
    }*/

//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        NimbeesClient.getPermissionManager().onRequestPermissionsResult(requestCode, permissions, grantResults);
//    }

    public void setStartDate(int year, int month, int day){
        this.startYear=year;
        this.startMonth=month;
        this.startDay=day;
    }

    public void setEndDate(int year, int month, int day){
        this.endYear=year;
        this.endMonth=month;
        this.endDay=day;
    }

    public void setStartTime(int hour, int minute){
        this.startHour=hour;
        this.startMinute=minute;
    }

    public void setEndTime(int hour, int minute){
        this.endHour=hour;
        this.endMinute=minute;
    }

//    public void sendMessage(){
//        nHelper.sendRequestLocationMessage(RADIUS, mLocation, new Date(), new Date());
//    }

    public void getHeatMap(){
        EditText editLatitude = (EditText)findViewById(R.id.editLatitude);

        mLocation.setLatitude(Double.parseDouble(editLatitude.getText().toString()));

        EditText editLongitude = (EditText)findViewById(R.id.editLongitude);
        mLocation.setLongitude(Double.parseDouble(editLongitude.getText().toString()));

        EditText editRadius = (EditText)findViewById(R.id.editRadius);
        RADIUS = Integer.parseInt(editRadius.getText().toString());

        //LocationManager.clearLocations();

        if(endYear!=0 && startYear!=0){
            Calendar calendar = Calendar.getInstance();
            calendar.clear();
            calendar.set(Calendar.YEAR, startYear);
            calendar.set(Calendar.MONTH, startMonth);
            calendar.set(Calendar.DAY_OF_MONTH, startDay);
            calendar.set(Calendar.HOUR_OF_DAY, startHour);
            calendar.set(Calendar.MINUTE, startMinute);
            Date startDate = calendar.getTime();
            calendar.clear();
            calendar.set(Calendar.YEAR, endYear);
            calendar.set(Calendar.MONTH, endMonth);
            calendar.set(Calendar.DAY_OF_MONTH, endDay);
            calendar.set(Calendar.HOUR_OF_DAY, endHour);
            calendar.set(Calendar.MINUTE, endMinute);
            Date endDate = calendar.getTime();
            if(startDate.before(endDate)) {

                this.requestHeatMap(new RequestHeatMap(topic_url, token, startDate, endDate,mLocation.getLatitude(), mLocation.getLongitude(), RADIUS));


                final ProgressDialog progressDialog = new ProgressDialog (MainActivity.this);
                progressDialog.setTitle("HeatMap");
                progressDialog.setMessage("Getting users' locations..."); // Setting Message
                progressDialog.setCancelable(false);
                progressDialog.show();

                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {

                        //TODO FIX HERE
                        getHeatMapPositions();
                        progressDialog.dismiss();
                    }
                }, 20000);
            }
            else{
                Log.e("HEATMAP", "End date is before star date");
                SnackBar snackbar = new SnackBar(MainActivity.this, "Start date should be before end date", "ok",new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Handle user action
                    }
                });
                snackbar.show();
            }
        }
        else{
            Log.e("HEATMAP", "No dates");
            SnackBar snackbar = new SnackBar(MainActivity.this, "Please select the dates first", "ok",new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Handle user action
                }
            });
            snackbar.show();
        }
    }

    /*public void requestHeatMap(GetHeatMapMessage getHeatMapMessage){
        Call <String> call = rest.requestHeatMap(getHeatMapMessage);

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                Log.e("HEATMAP: ", "Heat Map successfully requested");
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.e("HEATMAP: ", "ERROR getting the users' locations " + t.getMessage());
            }
        });

    }*/

    public void requestHeatMap(RequestHeatMap getHeatMapMessage) {

        //Firebase 1er step (POST)
        //Log.e("Notification PRE", getHeatMapMessage.toString());
        apiService.requestHeatMap(getHeatMapMessage).enqueue(new Callback<Object>() {
            @Override
            public void onResponse(Call<Object> call, Response<Object> response) {
                if(response.isSuccessful())
                    Log.e("HEATMAP: ", "Heat Map successfully requested" + response.body());

            }

            @Override
            public void onFailure(Call<Object> call, Throwable t) {
                Log.e("HEATMAP: ", "ERROR getting the users' locations " +t.getMessage());
            }
        });


    }

/*    public void getHeatMapPositions() {

        Call<List<LocationFrequency>> call = rest.getHeatMap(NimbeesClient.getUserManager().getUserData().getAlias());

        call.enqueue(new Callback<List<LocationFrequency>>() {
            @Override
            public void onResponse(Call<List<LocationFrequency>> call, Response<List<LocationFrequency>> response) {

                List<LocationFrequency> locations = response.body();

                List<WeightedLatLng> points= new ArrayList<WeightedLatLng>();
                for(LocationFrequency location:locations){
                    points.add(new WeightedLatLng(new LatLng(location.getLatitude(), location.getLongitude()),location.getFrequency()));
                }
                if(points.size()>0) {
                    HeatmapTileProvider mProvider = new HeatmapTileProvider.Builder()
                            .weightedData(points)
                            .build();
                    tileOverlay = MainActivity.this.mGoogleMap.addTileOverlay(new TileOverlayOptions().tileProvider(mProvider));
                    if (mCircle != null) {
                        mCircle.remove();
                    }
                }

            }

            @Override
            public void onFailure(Call<List<LocationFrequency>> call, Throwable t) {
                Log.e("HEATMAP: ", "ERROR getting the users' locations " + t.getMessage());
            }
        });

    }*/

    public void getHeatMapPositions() {
        final Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").create();

        //Firebase 4step
        Call<ResponseLambda> call = apiService.getHeatMap(token);
        final Type listType = new TypeToken<List<LocationFrequency>>() {}.getType();
        call.enqueue(new Callback<ResponseLambda>() {

            @Override
            public void onResponse(Call<ResponseLambda> call, Response<ResponseLambda> response) {
                Log.i("GET RETROFIT: ", "ok");
                if(response.isSuccessful()){

                    Log.e("HEATMAP: ", "Received locations for the HeatMap.");
                    List<LocationFrequency> locations = gson.fromJson(response.body().body,listType);

                    TableLayout inflate = (TableLayout) MainActivity.this.findViewById(R.id.tblLocations);
                    TableRow row;
                    TextView col1, col2, col3;

                    row = new TableRow(MainActivity.this);
                    col1 = new TextView(MainActivity.this);
                    col1.setText("Latitude"+ "      ");
                    row.addView(col1);

                    col2 = new TextView(MainActivity.this);
                    col2.setText("Longitude "+ "      ");
                    row.addView(col2);

                    col3 = new TextView(MainActivity.this);
                    col3.setText("Frequency "+ "      ");
                    row.addView(col3);

                    inflate.addView(row);


                    for(LocationFrequency location:locations){

                        Log.w("HEATMAP: ", "Point. Latitude " + location.getLatitude() + " Longitude: " + location.getLongitude() + "Frequency: " +location.getFrequency());

                        row = new TableRow(MainActivity.this);
                        col1 = new TextView(MainActivity.this);
                        col1.setText(location.getLatitude().toString() + "      ");
                        row.addView(col1);

                        col2 = new TextView(MainActivity.this);
                        col2.setText(location.getLongitude().toString()+ "      ");
                        row.addView(col2);

                        col3 = new TextView(MainActivity.this);
                        col3.setText(location.getFrequency().toString());
                        row.addView(col3);

                        inflate.addView(row);

                    }

                    /*
                    List<WeightedLatLng> points= new ArrayList<WeightedLatLng>();
                    for(LocationFrequency location:locations){
                        points.add(new WeightedLatLng(new LatLng(location.getLatitude(), location.getLongitude()),location.getFrequency()));
                    }
                    if(points.size()>0) {
                        HeatmapTileProvider mProvider = new HeatmapTileProvider.Builder()
                                .weightedData(points)
                                .build();

                        tileOverlay = MainActivity.this.mGoogleMap.addTileOverlay(new TileOverlayOptions().tileProvider(mProvider));
                        if (mCircle != null) {
                            mCircle.remove();
                        }

                    }
                    */

                }
            }

            @Override
            public void onFailure(Call<ResponseLambda> call, Throwable t) {
                Log.i("GET RETROFIT: ", "Fail "+t);
            }
        });
//        Call<List<LocationFrequency>> call = apiService.getHeatMap(token).enqueue(new Callback<List<LocationFrequency>>() ; {
//            @Override
//            public void onResponse(Call<List<LocationFrequency>> call, Response<List<LocationFrequency>> response) {
//
//
//                Log.i("GET RETROFIT: ", response.toString());
//                try {
//                    Log.i("GET RETROFIT: ", String.valueOf(call.clone().execute().body().size()));
//
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//
//                try {
//
//                    Log.e("HEATMAP: ", "Received locations for the HeatMap. Total: " + response.body().size() + response.toString());
//                    List<LocationFrequency> locations = call.clone().execute().body();
//
//                    TableLayout inflate = (TableLayout) MainActivity.this.findViewById(R.id.tblLocations);
//                    TableRow row;
//                    TextView col1, col2, col3;
//
//                    row = new TableRow(MainActivity.this);
//                    col1 = new TextView(MainActivity.this);
//                    col1.setText("Latitude"+ "      ");
//                    row.addView(col1);
//
//                    col2 = new TextView(MainActivity.this);
//                    col2.setText("Longitude "+ "      ");
//                    row.addView(col2);
//
//                    col3 = new TextView(MainActivity.this);
//                    col3.setText("Frequency "+ "      ");
//                    row.addView(col3);
//
//                    inflate.addView(row);
//
//
//                    for(LocationFrequency location:locations){
//
//                        Log.w("HEATMAP: ", "Point. Latitude " + location.getLatitude() + " Longitude: " + location.getLongitude() + "Frequency: " +location.getFrequency());
//
//                        row = new TableRow(MainActivity.this);
//                        col1 = new TextView(MainActivity.this);
//                        col1.setText(location.getLatitude().toString() + "      ");
//                        row.addView(col1);
//
//                        col2 = new TextView(MainActivity.this);
//                        col2.setText(location.getLongitude().toString()+ "      ");
//                        row.addView(col2);
//
//                        col3 = new TextView(MainActivity.this);
//                        col3.setText(location.getFrequency().toString());
//                        row.addView(col3);
//
//                        inflate.addView(row);
//
//                    }

                    /*
                    List<WeightedLatLng> points= new ArrayList<WeightedLatLng>();
                    for(LocationFrequency location:locations){
                        points.add(new WeightedLatLng(new LatLng(location.getLatitude(), location.getLongitude()),location.getFrequency()));
                    }
                    if(points.size()>0) {
                        HeatmapTileProvider mProvider = new HeatmapTileProvider.Builder()
                                .weightedData(points)
                                .build();

                        tileOverlay = MainActivity.this.mGoogleMap.addTileOverlay(new TileOverlayOptions().tileProvider(mProvider));
                        if (mCircle != null) {
                            mCircle.remove();
                        }

                    }
                    */
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }


//            }
//
//            @Override
//            public void onFailure(Call<List<LocationFrequency>> call, Throwable t) {
//                Log.e("HEATMAP: ", "ERROR getting the users' locations " + t.getMessage());
//            }
//        });
//    }

    }
}


