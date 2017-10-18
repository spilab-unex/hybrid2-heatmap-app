package es.unex.geoapp.datemanager;



import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.TimePicker;

import com.gc.materialdesign.widgets.SnackBar;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.maps.android.heatmaps.HeatmapTileProvider;
import com.google.maps.android.heatmaps.WeightedLatLng;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import es.unex.geoapp.MainActivity;
import es.unex.geoapp.locationmanager.LocationManager;
import es.unex.geoapp.model.LocationFrequency;


/**
 * Created by jgaralo on 11/10/17.
 */

public class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {

    private String date;

    private FragmentActivity myContext;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current time as the default values for the picker
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        this.date=getArguments().getString("date");

        Dialog timeDialog= new TimePickerDialog(getActivity(), this, hour, minute,
                DateFormat.is24HourFormat(getActivity()));
        if(date.equals("start")) {
            timeDialog.setTitle("Pick start date");
        }
        else{
            timeDialog.setTitle("Pick end date");
        }
        return timeDialog;
    }

    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        if(date.equals("start")) {
            ((MainActivity) getActivity()).setStartTime(hourOfDay, minute);
            Bundle bundle = new Bundle();
            bundle.putString("date", "end");
            DialogFragment newFragment = new DatePickerFragment();
            newFragment.setArguments(bundle);
            newFragment.show(myContext.getSupportFragmentManager(), "datePicker");
        }
        else{
            ((MainActivity) getActivity()).setEndTime(hourOfDay, minute);
            ((MainActivity) getActivity()).getHeatMap();
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof FragmentActivity){
            myContext=(FragmentActivity) context;
        }
    }
}
