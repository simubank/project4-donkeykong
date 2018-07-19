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

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.AutocompletePredictionBuffer;
import com.google.android.gms.location.places.AutocompletePredictionBufferResponse;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.PlaceBufferResponse;
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
import com.levelup.td.tdlevelupquest.Utils.APICallback;
import com.levelup.td.tdlevelupquest.Utils.NetworkHelper;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static java.nio.file.Files.newBufferedReader;

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
    private String apiResult;
    private GoogleApiClient mGoogleApiClient;

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

        // TODO: Start using the Places API.z
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainScreenActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    1);
        }
        NetworkHelper.getInstance().botAPIGetRequest("https://dev.botsfinancial.com/api/accounts/873a24c9-1852-432a-8185-fb6e94d52ad1_b59fad0d-24ea-464c-a4cb-c2c1ee9702d9/transactions",
                getApplicationContext(), new APICallback() {
                    @Override
                    public void onResponse(boolean success, JSONObject message) {
                        apiResult = message.toString();
                        Log.d("wwe", apiResult);
                    }
                });
        LaunchActivity.initDrawer(MainScreenActivity.this,this);
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
        BufferedReader reader;
        HashMap<Integer,String> placeTypeMap = new HashMap<>();

//        try{
//            reader = newBufferedReader(new FileReader("/keyValueForGooglePlaces.txt"));
//            String line = reader.readLine();
//            while(line != null){
//                placeTypeMap.put()
//            }
//        }
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(data, this);

                List<Integer> placeTypesList = place.getPlaceTypes();

                String toastMsg = String.format("Place Name: %s", placeTypesList);
                Log.d("wwe", "Place Category Index:"+place.getPlaceTypes());
                Log.d("wwe", "Place ID:"+place.getId());
                Log.d("wwe", "Place Price Level:"+place.getPriceLevel());
                Log.d("wwe", "Place Location:"+place.getLatLng());
                Log.d("wwe", "Place Rating:"+place.getRating());


                Double NELat = place.getLatLng().latitude + 1;
                Double NELong = place.getLatLng().longitude + 1;
                Double SWLat = place.getLatLng().latitude - 1;
                Double SWLong = place.getLatLng().longitude - 1;
                LatLng NELatlng = new LatLng(NELat, NELong);
                LatLng SWLatlng = new LatLng(SWLat, SWLong);
                LatLngBounds newbounds = new LatLngBounds(SWLatlng,NELatlng);
                Task<AutocompletePredictionBufferResponse> results =
                        mGeoDataClient.getAutocompletePredictions("restaurants", newbounds,
                                null);
                results.addOnCompleteListener(new OnCompleteListener<AutocompletePredictionBufferResponse>() {
                    @Override
                    public void onComplete(@NonNull Task<AutocompletePredictionBufferResponse> task) {
                        AutocompletePredictionBufferResponse autocompletePredictions = task.getResult();
                        Iterator<AutocompletePrediction> iterator = autocompletePredictions.iterator();
                        while(iterator.hasNext()){
                            AutocompletePrediction prediction = iterator.next();
                            Log.d("wwe", "Query completed. Received " + prediction.getFullText(null)+ " predictions.");
                            String placeID = prediction.getPlaceId();
                            placeIDCreate(placeID);
                        }
                        autocompletePredictions.release();
                    }
                });

                Toast.makeText(this, toastMsg, Toast.LENGTH_LONG).show();
            }
        }
    }

    public void placeIDCreate(String placeID){
        Task<PlaceBufferResponse> placeResult = mGeoDataClient.getPlaceById(placeID);
        placeResult.addOnCompleteListener(new OnCompleteListener<PlaceBufferResponse>() {
            @Override
            public void onComplete(@NonNull Task<PlaceBufferResponse> task) {
                PlaceBufferResponse places = task.getResult();
                final Place place = places.get(0);
                Log.d("wwe", "Place Category Index:"+place.getPlaceTypes());
                Log.d("wwe", "Place ID:"+place.getId());
                Log.d("wwe", "Place Price Level:"+place.getPriceLevel());
                Log.d("wwe", "Place Location:"+place.getLatLng());
                Log.d("wwe", "Place Rating:"+place.getRating());
            }
        });
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

}
