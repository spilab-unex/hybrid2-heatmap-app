package es.unex.geoapp.datemanager;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;

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

public class DatePickerFragment extends DialogFragment
        implements DatePickerDialog.OnDateSetListener {

    private FragmentActivity myContext;

    private String date;

     @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current date as the default date in the picker
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        this.date=getArguments().getString("date");
        Dialog dateDialog=new DatePickerDialog(getActivity(), this, year, month, day);
        if(date.equals("start")) {
            dateDialog.setTitle("Pick start date");
        }
        else{
            dateDialog.setTitle("Pick end date");
        }
        return dateDialog;
    }

    public void onDateSet(DatePicker view, int year, int month, int day) {
        if(date.equals("start")) {
            ((MainActivity) getActivity()).setStartDate(year, month, day);
        }
        else{
            ((MainActivity) getActivity()).setEndDate(year, month, day);
        }
        Bundle bundle = new Bundle();
        bundle.putString("date", date);
        DialogFragment newFragment = new TimePickerFragment();
        newFragment.setArguments(bundle);
        newFragment.show(myContext.getSupportFragmentManager(), "timePicker");
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof FragmentActivity){
            myContext=(FragmentActivity) context;
        }
    }

}