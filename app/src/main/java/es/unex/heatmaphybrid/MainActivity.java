package es.unex.heatmaphybrid;

import android.accounts.AccountManager;
import android.app.ProgressDialog;
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
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.maps.android.heatmaps.HeatmapTileProvider;
import com.google.maps.android.heatmaps.WeightedLatLng;
import com.nimbees.platform.NimbeesClient;
import com.nimbees.platform.NimbeesException;
import com.uphyca.stetho_realm.RealmInspectorModulesProvider;
//import com.nimbees.platform.location.NimbeesLocationManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import es.unex.heatmaphybrid.datemanager.DatePickerFragment;
//import es.unex.heatmaphybrid.locationmanager.LocationManager;
import es.unex.heatmaphybrid.locationmanager.LocationManager;
import es.unex.heatmaphybrid.locationmanager.LocationService;
import es.unex.heatmaphybrid.locationmanager.PermissionManager;
import es.unex.heatmaphybrid.messagemanager.NotificationHelper;
import es.unex.heatmaphybrid.model.GetHeatMapMessage;
import es.unex.heatmaphybrid.model.LocationBeanRealm;
import es.unex.heatmaphybrid.model.LocationFrequency;
import es.unex.heatmaphybrid.model.RequestHeatMap;
import es.unex.heatmaphybrid.rest.IPostDataService;
import io.realm.Realm;
import io.realm.RealmResults;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

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

     /*   if (locationIntent == null) {
            locationIntent = new Intent(this, LocationService.class);
        }*/
        // check location permission
        if (PermissionManager.checkPermissions(this, MainActivity.this)){
            //startService(locationIntent);
        }

        if (mLocation == null) {
            mLocation = new Location(android.location.LocationManager.GPS_PROVIDER);
        }

        //rest = IPostDataService.retrofit.create(IPostDataService.class);
        //rest = IPostDataService.restAdapter.create(IPostDataService.class);


        // We check here if the NimbeesClient have data from the user or not to call register.
        if (NimbeesClient.getUserManager().getUserData() == null) {
            showGoogleAccountPicker();
        }


        // Start the Nimbees Client user tracking service
        NimbeesClient.getPermissionManager().checkPermissions(this);

        if(NimbeesClient.getPermissionManager().getLocationPermissionState(getApplicationContext())){
            NimbeesClient.getLocationManager().startTracking(1,1);
        }


        rest = IPostDataService.restAdapter.create(IPostDataService.class);
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


    }

    /**
     * Util of Android to pick an Gmail user account stored in the device.
     */
    private void showGoogleAccountPicker() {
        Intent googlePicker = AccountPicker.newChooseAccountIntent(null, null,
                new String[]{GoogleAuthUtil.GOOGLE_ACCOUNT_TYPE}, true, null, null, null, null);
        startActivityForResult(googlePicker, PICK_ACCOUNT_REQUEST);
    }

    /**
     * Called when showGoogleAccountPicker finish and return the String with the username to be registered.
     */
    @Override
    public void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        if (requestCode == PICK_ACCOUNT_REQUEST && resultCode == MainActivity.RESULT_OK) {
            nHelper.registerUser(data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME));
            //mTextViewEmail.setText("User: " + data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME));
        }
    }


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
        if (mLocation != null) {
            // Create a LatLng object for the current location
            LatLng latLng = new LatLng(mLocation.getLatitude(), mLocation.getLongitude());
            // Show the current location in Google Map
            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            // Draw the first circle in the map
            mCircleOptions = new CircleOptions().fillColor(0x5500ff00).strokeWidth(0l);
            // Zoom in the Google Map
            mGoogleMap.animateCamera(CameraUpdateFactory.zoomTo(15));

            // Zoom in the Google Map
            //icon = BitmapDescriptorFactory.fromResource(R.drawable.logo2);

            // Creation and settings of Marker Options
            mMarkerOptions = new MarkerOptions().position(latLng).title("You are here!");//.icon(icon);
            // Creation and addition to the map of the Marker
            mMarker = mGoogleMap.addMarker(mMarkerOptions);
            // set the initial map radius and draw the circle
            drawCircle(RADIUS);
        }
    }*/
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        NimbeesClient.getPermissionManager().onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

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

    public void sendMessage(){
        nHelper.sendRequestLocationMessage(RADIUS, mLocation, new Date(), new Date());
    }

    public void getHeatMap(){
        EditText editLatitude = (EditText)findViewById(R.id.editLatitude);

        mLocation.setLatitude(Double.parseDouble(editLatitude.getText().toString()));

        EditText editLongitude = (EditText)findViewById(R.id.editLongitude);
        mLocation.setLongitude(Double.parseDouble(editLongitude.getText().toString()));

        EditText editRadius = (EditText)findViewById(R.id.editRadius);
        RADIUS = Integer.parseInt(editRadius.getText().toString());

        LocationManager.clearLocations();

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
                this.requestHeatMap(new RequestHeatMap(NimbeesClient.getUserManager().getUserData().getAlias(), startDate, endDate,mLocation.getLatitude(), mLocation.getLongitude(), RADIUS));

                final ProgressDialog progressDialog = new ProgressDialog (MainActivity.this);
                progressDialog.setTitle("HeatMap");
                progressDialog.setMessage("Getting users' locations..."); // Setting Message
                progressDialog.setCancelable(false);
                progressDialog.show();

                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {
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

/*    public void requestHeatMap(GetHeatMapMessage getHeatMapMessage){
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

        Callback<Object> callback = new Callback<Object>() {

            @Override
            public void success(Object s, Response response) {
                Log.e("HEATMAP: ", "Heat Map successfully requested" + response.getBody() + " "+ new Gson().toJson(s));
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                Log.e("HEATMAP: ", "ERROR getting the users' locations " + retrofitError.getMessage());
            }
        };

        rest.requestHeatMap(getHeatMapMessage, callback);

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

        Callback<List<LocationFrequency>> callback = new Callback<List<LocationFrequency>>() {

            @Override
            public void success(List<LocationFrequency> list, Response response) {
                Log.e("HEATMAP: ", "Received locations for the HeatMap. Total: " + list.size() + response.getUrl());
                List<LocationFrequency> locations = list;

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

            @Override
            public void failure(RetrofitError retrofitError) {
                Log.e("HEATMAP: ", "ERROR getting the users' locations " + retrofitError.getMessage());
            }
        };

        rest.getHeatMap(NimbeesClient.getUserManager().getUserData().getAlias(), callback);
    }


}


