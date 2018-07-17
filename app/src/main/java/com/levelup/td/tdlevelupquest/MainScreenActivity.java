package com.levelup.td.tdlevelupquest;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.AutocompletePredictionBufferResponse;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceLikelihood;
import com.google.android.gms.location.places.PlaceLikelihoodBufferResponse;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.PlaceDetectionClient;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.RuntimeExecutionException;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;

import java.util.Timer;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Created by Mikeb on 7/10/2018.
 */

public class MainScreenActivity extends AppCompatActivity {
    private GeoDataClient mGeoDataClient;
    private PlaceDetectionClient mPlaceDetectionClient;
    private LatLngBounds mBounds;
    private LatLng mLatLgn;
    private AutocompleteFilter mPlaceFilter;
    private GoogleMap mgoogle;
    private MapFragment mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_screen);
        Button btn = findViewById(R.id.pickerButton);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkThisOut();
            }
        });
        Log.d("wow", "onCreate: wwwwww");
        // Construct a GeoDataClient.
        mGeoDataClient = Places.getGeoDataClient(this, null);

        // Construct a PlaceDetectionClient.
        mPlaceDetectionClient = Places.getPlaceDetectionClient(this, null);

        // TODO: Start using the Places API.
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainScreenActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    1);
        }
    }

    private void checkThisOut(){
        int PLACE_PICKER_REQUEST = 1;
        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
        try{
            startActivityForResult(builder.build(this), PLACE_PICKER_REQUEST);
        }catch (Exception e) {
            Log.d("error", e.getMessage());
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(data, this);
                String toastMsg = String.format("Place Name: %s", place.getName());
                Log.d("wwe", "Place Category Index:"+place.getPlaceTypes());
                Log.d("wwe", "Place ID:"+place.getId());
                Log.d("wwe", "Place Price Level:"+place.getPriceLevel());
                Log.d("wwe", "Place Location:"+place.getLatLng());
                Log.d("wwe", "Place Rating:"+place.getRating());

                Double NELat = place.getLatLng().latitude + 10;
                Double NELong = place.getLatLng().longitude + 10;
                Double SWLat = place.getLatLng().latitude - 10;
                Double SWLong = place.getLatLng().longitude - 10;
                LatLng NELatlng = new LatLng(NELat, NELong);
                LatLng SWLatlng = new LatLng(SWLat, SWLong);
                LatLngBounds newbounds = new LatLngBounds(SWLatlng,NELatlng);

                getPrediction(newbounds);


//                try {
//                    AutocompletePredictionBufferResponse autocompletePredictions = results.getResult();
//
//                    Log.d("wwe", "Query completed. Received " + autocompletePredictions.getCount()
//                            + " predictions.");
//
//                } catch (RuntimeExecutionException e) {
//                    // If the query did not complete successfully return null
//                    Log.e("wwe", "Error getting autocomplete prediction API call", e);
//
//                }

                Toast.makeText(this, toastMsg, Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1: {

                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(MainScreenActivity.this, "Permission denied to read your Fine location", Toast.LENGTH_SHORT).show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }
    public void getPrediction(final LatLngBounds newbounds){
        new AsyncTask<Void,Void,Task<AutocompletePredictionBufferResponse>>(){
            @Override
            protected Task<AutocompletePredictionBufferResponse> doInBackground(Void... voids) {
                Task<AutocompletePredictionBufferResponse> results =
                        mGeoDataClient.getAutocompletePredictions("restaurants", newbounds,
                                null);
                return results;
            }

            @Override
            protected void onPostExecute(Task<AutocompletePredictionBufferResponse> s) {
                super.onPostExecute(s);
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Log.d("wwe", "Query completed. Received " + s.getResult().getCount()+ " predictions.");
            }
        }.execute();
    }

}
